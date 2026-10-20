package aquarion.world.units;

import aquarion.gen.Amphibiousc;
import aquarion.units.type.AquaUnitType;
import arc.Events;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.IntMap;
import arc.struct.IntSeq;
import arc.struct.IntSet;
import arc.struct.IntIntMap;
import arc.struct.ObjectMap;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.gen.Unit;
import mindustry.game.EventType;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.StaticWall;

import java.util.Comparator;
import java.util.PriorityQueue;

import static mindustry.Vars.*;

/** A bounded A* over swimming steps and valid hop landing sites, used by both unit controllers. */
public class AmphibiousPathfinder {
    //All units share a frame budget; issuing a group command cannot run dozens of full searches at once.
    //Use the available time budget: the old edge cap often stopped searches long
    //before the deadline, leaving units idle for many frames on ordinary detours.
    private static final int frameEdges = 2048, searchSlice = 64;
    private static final long frameNanos = 2_000_000L;
    private static final Queue<AmphibiousPathfinder> pending = new Queue<>();
    private static final ObjectMap<AquaUnitType, IntSeq> offsetCache = new ObjectMap<>();
    private static final ObjectMap<AquaUnitType, Float> offsetDistances = new ObjectMap<>();
    private static final ObjectMap<AquaUnitType, Seq<SharedRoute>> sharedRoutes = new ObjectMap<>();
    private static int terrainRevision;
    static {
        Events.run(EventType.Trigger.afterGameUpdate, AmphibiousPathfinder::update);
        Events.on(EventType.ResetEvent.class, event -> reset());
        Events.on(EventType.WorldLoadEvent.class, event -> reset());
        Events.on(EventType.TileChangeEvent.class, event -> {
            terrainRevision++;
            sharedRoutes.clear();
        });
    }

    private final Seq<Node> route = new Seq<>();
    private final Vec2 goal = new Vec2(Float.NaN, Float.NaN);
    private final Vec2 movement = new Vec2();
    private final IntMap<Node> nodes = new IntMap<>();
    private final IntIntMap landCache = new IntIntMap();
    private final PriorityQueue<Visit> open = new PriorityQueue<>(Comparator.comparingDouble(n -> n.score));
    private float repath;
    private int waypoint;
    private boolean searching, queued;
    private Unit searchUnit;
    private AquaUnitType searchType;
    private Node start, best, current;
    private IntSeq offsets;
    private int nextEdge, expanded;
    private float searchX, searchY, searchStop, bestDistance, fastest, startX, startY;
    private float progressX = Float.NaN, progressY, stuckTime;
    private AmphibiousPathfinder waitingFor;
    private int routeTerrain = -1;

    private static class Node {
        int x, y;
        float cost, score;
        boolean hop, closed;
        Node parent;
        Node(int x, int y){ this.x = x; this.y = y; }
    }

    private static class Visit {
        final Node node;
        final float cost, score;
        Visit(Node node){ this.node = node; cost = node.cost; score = node.score; }
    }

    private static class SharedRoute {
        float targetX, targetY, stopRange, created;
        Seq<Node> points;
    }

    /** Advances queued searches fairly, once per game frame, rather than once per commanded unit. */
    public static void update(){
        long deadline = System.nanoTime() + frameNanos;
        int remaining = frameEdges;
        while(!pending.isEmpty() && remaining > 0 && System.nanoTime() < deadline){
            AmphibiousPathfinder path = pending.removeFirst();
            path.queued = false;
            if(!path.searching) continue;
            if(path.routeTerrain != terrainRevision){
                path.landCache.clear();
                path.routeTerrain = terrainRevision;
            }
            if(path.searchUnit.dead || path.searchUnit.type != path.searchType){ path.cancelSearch(); continue; }
            int used = path.advanceSearch(Math.min(searchSlice, remaining), deadline);
            remaining -= used;
            if(path.searching) path.enqueue();
            if(used == 0) break;
        }
    }

    private static void reset(){
        terrainRevision++;
        while(!pending.isEmpty()){
            AmphibiousPathfinder path = pending.removeFirst();
            path.queued = false;
            path.cancelSearch();
            path.route.clear();
        }
        offsetCache.clear();
        offsetDistances.clear();
        sharedRoutes.clear();
    }

    private void enqueue(){
        if(!queued){ pending.addLast(this); queued = true; }
    }

    private void cancelSearch(){
        searching = false;
        waitingFor = null;
        searchUnit = null;
        current = start = best = null;
        nodes.clear();
        landCache.clear();
        open.clear();
    }

    private static float tileRadius(AquaUnitType type){
        //Match Hitboxc.hitboxTile: ground collision boxes are capped below one tile.
        return Math.min(type.hitSize * 0.66f, 7.8f) / 2f;
    }

    /** Use the engine's actual tile-collision footprint, including at map edges. */
    public static boolean canLand(AquaUnitType type, float x, float y){
        float radius = tileRadius(type);
        int minX = Mathf.floor((x - radius + tilesize / 2f) / tilesize);
        int maxX = Mathf.floor((x + radius + tilesize / 2f) / tilesize);
        int minY = Mathf.floor((y - radius + tilesize / 2f) / tilesize);
        int maxY = Mathf.floor((y + radius + tilesize / 2f) / tilesize);
        for(int tx = minX; tx <= maxX; tx++){
            for(int ty = minY; ty <= maxY; ty++){
                Tile tile = world.tile(tx, ty);
                if(tile == null || tile.solid() || !(tile.floor().hasSurface() || tile.floor().isLiquid)) return false;
            }
        }
        return true;
    }

    /** Weapon range alone is not a reason to stop on the far side of a wall. */
    public static boolean clearShot(Unit unit, mindustry.gen.Teamc target){
        int samples = Math.max(1, Mathf.ceil(unit.dst(target) / (tilesize / 2f)));
        for(int i = 1; i <= samples; i++){
            float fraction = i / (float)samples;
            Tile tile = world.tileWorld(Mathf.lerp(unit.x, target.x(), fraction),
                Mathf.lerp(unit.y, target.y(), fraction));
            if(tile == null || (tile.solid() && tile.build != target)) return false;
        }
        return true;
    }

    /** Checks the whole swept footprint; hops cross natural walls, never solid buildings. */
    public static boolean canHop(AquaUnitType type, float x, float y, float targetX, float targetY){
        float distance = Mathf.dst(x, y, targetX, targetY);
        if(distance > type.hopDistance + 0.01f || distance < type.hopMinDistance ||
            !canLand(type, targetX, targetY)) return false;
        return canTraverse(type, x, y, targetX, targetY);
    }

    /** Checks a small swept movement too, so in-flight validation does not rescan the entire remaining hop. */
    public static boolean canTraverse(AquaUnitType type, float x, float y, float targetX, float targetY){
        float distance = Mathf.dst(x, y, targetX, targetY);
        int samples = Math.max(1, Mathf.ceil(distance / (tilesize / 2f)));
        float radius = tileRadius(type);
        for(int i = 0; i <= samples; i++){
            float fraction = i / (float)samples;
            float cx = Mathf.lerp(x, targetX, fraction), cy = Mathf.lerp(y, targetY, fraction);
            int minX = Mathf.floor((cx - radius + tilesize / 2f) / tilesize);
            int maxX = Mathf.floor((cx + radius + tilesize / 2f) / tilesize);
            int minY = Mathf.floor((cy - radius + tilesize / 2f) / tilesize);
            int maxY = Mathf.floor((cy + radius + tilesize / 2f) / tilesize);
            for(int tx = minX; tx <= maxX; tx++){
                for(int ty = minY; ty <= maxY; ty++){
                    Tile tile = world.tile(tx, ty);
                    if(tile == null) return false;
                    if(tile.solid() && !(type.hopOverEnvironment && tile.build == null && tile.block() instanceof StaticWall)) return false;
                }
            }
        }
        return true;
    }

    public void move(Unit unit, float targetX, float targetY, float stopRange){
        if(!(unit instanceof Amphibiousc frog) || !(unit.type instanceof AquaUnitType type) || frog.hopping()) return;
        if(unit.within(targetX, targetY, stopRange)) return;
        repath -= Time.delta;
        if(routeTerrain != terrainRevision){
            landCache.clear();
            routeTerrain = terrainRevision;
            if(!searching) repath = 0f;
        }
        if(!goal.within(targetX, targetY, tilesize)){
            cancelSearch();
            route.clear();
            waypoint = 0;
            goal.set(targetX, targetY);
            routeTerrain = terrainRevision;
            repath = 0f;
        }
        //Resting units do not need to run pathfinding or repeatedly validate a hop they cannot start yet.
        if(!unit.floorOn().isLiquid && frog.hopCooldownRemaining() > 0f) return;

        if(!searching){
            if(Float.isNaN(progressX) || unit.dst2(progressX, progressY) > 1f){
                progressX = unit.x;
                progressY = unit.y;
                stuckTime = 0f;
            }else if((stuckTime += Time.delta) >= type.hopStuckTime){
                route.clear();
                waypoint = 0;
                repath = 0f;
                stuckTime = 0f;
            }
        }
        float arrival = Math.max(1.5f, type.hopMinDistance);
        while(waypoint < route.size && unit.within(route.get(waypoint).x * tilesize, route.get(waypoint).y * tilesize, arrival)) waypoint++;
        while(waypoint < route.size && unit.within(route.get(waypoint).x * tilesize, route.get(waypoint).y * tilesize,
            unit.physicSize() * 2f + 2f) && !HopLanding.clear(unit, route.get(waypoint).x * tilesize, route.get(waypoint).y * tilesize)) waypoint++;
        if(waypoint < route.size){
            Node next = route.get(waypoint);
            float nx = next.x * tilesize, ny = next.y * tilesize;
            if(canLand(type, nx, ny)){
                if(next.hop || !unit.floorOn().isLiquid){
                    if(frog.requestHop(nx, ny) || frog.hopCooldownRemaining() > 0f) return;
                }else if(swimToward(unit, type, nx, ny)) return;
            }
            //The route was blocked or the unit was pushed away from a reachable waypoint.
            route.clear();
            waypoint = 0;
            repath = 0f;
        }
        if(adoptSharedRoute(unit, type, targetX, targetY, stopRange)) return;
        if(waitingFor != null){
            if(waitingFor.searching) return;
            waitingFor = null;
            repath = 0f;
        }
        if(searching || repath > 0f) return;

        //Open terrain needs no graph search: swim or take one validated hop toward the destination.
        if(unit.floorOn().isLiquid && swimToward(unit, type, targetX, targetY)) return;
        float distance = unit.dst(targetX, targetY);
        movement.set(targetX - unit.x, targetY - unit.y).setLength(Math.min(distance, type.hopDistance));
        if(frog.requestHop(unit.x + movement.x, unit.y + movement.y)) return;

        beginSearch(unit, type, targetX, targetY, Math.max(stopRange, tilesize));
        repath = type.hopRepathInterval;
    }

    private boolean swimToward(Unit unit, AquaUnitType type, float x, float y){
        movement.set(x - unit.x, y - unit.y);
        float distance = movement.len();
        if(distance < 0.001f) return true;
        movement.setLength(Math.min(distance, tilesize / 2f));
        float nx = unit.x + movement.x, ny = unit.y + movement.y;
        if(!world.floorWorld(nx, ny).isLiquid || !canLand(type, nx, ny) ||
            !canLand(type, (unit.x + nx) / 2f, (unit.y + ny) / 2f)) return false;
        movement.set(x - unit.x, y - unit.y).limit(unit.speed());
        unit.moveAt(movement);
        unit.lookAt(x, y);
        return true;
    }

    private static IntSeq offsets(AquaUnitType type){
        IntSeq offsets = offsetCache.get(type);
        if(offsets != null && offsetDistances.get(type) == type.hopDistance) return offsets;
        offsets = new IntSeq();
        IntSet seen = new IntSet();
        for(int direction = 0; direction < 16; direction++){
            for(float distance = tilesize; distance <= type.hopDistance + 0.01f; distance += tilesize){
                int dx = Math.round(Mathf.cosDeg(direction * 22.5f) * distance / tilesize);
                int dy = Math.round(Mathf.sinDeg(direction * 22.5f) * distance / tilesize);
                if((dx != 0 || dy != 0) && Mathf.dst(dx, dy) * tilesize <= type.hopDistance && seen.add(Point2.pack(dx, dy))){
                    offsets.add(dx, dy);
                }
            }
        }
        offsetCache.put(type, offsets);
        offsetDistances.put(type, type.hopDistance);
        return offsets;
    }

    private void beginSearch(Unit unit, AquaUnitType type, float targetX, float targetY, float stopRange){
        cancelSearch();
        route.clear();
        waypoint = 0;
        //Nearby members of the same command group wait for one useful route instead of duplicating it.
        for(AmphibiousPathfinder other : pending){
            if(other.searching && other.searchType == type &&
                other.searchUnit.within(unit, tilesize * 2f) &&
                Mathf.within(other.searchX, other.searchY, targetX, targetY, tilesize) &&
                Math.abs(other.searchStop - stopRange) < tilesize){
                waitingFor = other;
                return;
            }
        }
        searching = true;
        searchUnit = unit;
        searchType = type;
        searchX = targetX;
        searchY = targetY;
        searchStop = stopRange;
        startX = unit.x;
        startY = unit.y;
        expanded = nextEdge = 0;
        start = new Node(unit.tileX(), unit.tileY());
        nodes.put(Point2.pack(start.x, start.y), start);
        open.add(new Visit(start));
        best = start;
        bestDistance = Mathf.dst(startX, startY, targetX, targetY);
        //A hop includes its rest period; using raw airborne speed makes the search
        //explore large areas that could never beat an already promising route.
        float sustainedHopSpeed = type.hopDistance / (type.hopDistance / type.hopSpeed + type.hopCooldown);
        fastest = Math.max(type.swimSpeed, sustainedHopSpeed);
        offsets = offsets(type);
        enqueue();
    }

    private boolean landable(int x, int y){
        int key = Point2.pack(x, y);
        int cached = landCache.get(key, 0);
        if(cached == 0){
            cached = canLand(searchType, x * tilesize, y * tilesize) ? 1 : 2;
            landCache.put(key, cached);
        }
        return cached == 1;
    }

    private int advanceSearch(int budget, long deadline){
        int used = 0;
        if(offsets.isEmpty()){ finishSearch(); return used; }
        while(searching && used < budget && System.nanoTime() < deadline){
            if(current == null){
                if(open.isEmpty()){ finishSearch(); break; }
                //Yield without discarding the frontier. Concave obstacles may require
                //moving away from the goal before any closer landing is reachable.
                if(expanded >= searchType.hopSearchBudget){ expanded = 0; break; }
                Visit visit = open.poll();
                if(visit.node.closed || visit.cost != visit.node.cost){ used++; continue; }
                current = visit.node;
                current.closed = true;
                expanded++;
                nextEdge = 0;
                float remaining = Mathf.dst(current == start ? startX : current.x * tilesize,
                    current == start ? startY : current.y * tilesize, searchX, searchY);
                if(remaining < bestDistance){ best = current; bestDistance = remaining; }
                if(remaining <= searchStop){ finishSearch(); break; }
            }
            int dx = offsets.get(nextEdge++), dy = offsets.get(nextEdge++);
            used++;
            int nx = current.x + dx, ny = current.y + dy;
            //The first edge starts at the real position. Rounding to a tile center can
            //produce a maximum-length hop that the actual unit can never reach.
            float cx = current == start ? startX : current.x * tilesize;
            float cy = current == start ? startY : current.y * tilesize;
            float wx = nx * tilesize, wy = ny * tilesize;
            Node node = nodes.get(Point2.pack(nx, ny));
            if(node == null || !node.closed){
                float distance = Mathf.dst(cx, cy, wx, wy);
                boolean swim = Math.abs(dx) <= 1 && Math.abs(dy) <= 1 &&
                    world.floorWorld(cx, cy).isLiquid && world.floorWorld(wx, wy).isLiquid;
                float cost = current.cost + (swim ? distance / searchType.swimSpeed : distance / searchType.hopSpeed + searchType.hopCooldown);
                if((node == null || cost < node.cost) && landable(nx, ny) &&
                    (current != start || HopLanding.clear(searchUnit, wx, wy)) &&
                    (swim ? canLand(searchType, (cx + wx) / 2f, (cy + wy) / 2f) :
                        (searchType.hopFromWater || !world.floorWorld(cx, cy).isLiquid) && canHop(searchType, cx, cy, wx, wy))){
                    if(node == null){ node = new Node(nx, ny); nodes.put(Point2.pack(nx, ny), node); }
                    node.parent = current;
                    node.hop = !swim;
                    node.cost = cost;
                    node.score = cost + Mathf.dst(wx, wy, searchX, searchY) / fastest;
                    open.add(new Visit(node));
                }
            }
            if(nextEdge >= offsets.size) current = null;
        }
        return used;
    }

    private void finishSearch(){
        route.clear();
        waypoint = 0;
        //A partial route is useful on large maps; extend it at the next bounded search.
        for(Node node = best; node != start; node = node.parent) route.add(node);
        route.reverse();
        if(route.any()){
            Seq<SharedRoute> shared = sharedRoutes.get(searchType, Seq::new);
            if(shared.size >= 16) shared.remove(0);
            SharedRoute result = new SharedRoute();
            result.targetX = searchX;
            result.targetY = searchY;
            result.stopRange = searchStop;
            result.created = Time.time;
            result.points = route.copy();
            shared.add(result);
        }
        cancelSearch();
    }

    private boolean adoptSharedRoute(Unit unit, AquaUnitType type, float targetX, float targetY, float stopRange){
        Seq<SharedRoute> shared = sharedRoutes.get(type);
        if(shared == null || (!type.hopFromWater && unit.floorOn().isLiquid)) return false;
        for(int i = shared.size - 1; i >= 0; i--){
            SharedRoute candidate = shared.get(i);
            if(Time.time - candidate.created > 120f){ shared.remove(i); continue; }
            if(!Mathf.within(candidate.targetX, candidate.targetY, targetX, targetY, tilesize) ||
                Math.abs(candidate.stopRange - Math.max(stopRange, tilesize)) >= tilesize) continue;
            int first = 0;
            float nearest = Float.POSITIVE_INFINITY;
            for(int p = 0; p < candidate.points.size; p++){
                Node node = candidate.points.get(p);
                float distance = unit.dst2(node.x * tilesize, node.y * tilesize);
                if(distance < nearest){ nearest = distance; first = p; }
            }
            float arrival = Math.max(1.5f, type.hopMinDistance);
            if(nearest <= arrival * arrival) first++;
            //Do not re-adopt an exhausted partial route or hop backward to an old waypoint.
            for(int p = candidate.points.size - 1; p >= first; p--){
                Node node = candidate.points.get(p);
                float nx = node.x * tilesize, ny = node.y * tilesize;
                if(!unit.within(nx, ny, type.hopDistance) || !canHop(type, unit.x, unit.y, nx, ny) ||
                    !HopLanding.clear(unit, nx, ny)) continue;
                cancelSearch();
                route.clear();
                for(int n = p; n < candidate.points.size; n++) route.add(candidate.points.get(n));
                waypoint = 0;
                return true;
            }
        }
        return false;
    }
}
