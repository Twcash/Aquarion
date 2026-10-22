package aquarion.world.units;

import aquarion.world.blocks.distribution.RailPoint;
import aquarion.world.blocks.distribution.RailPoint.RailPointBuild;
import arc.math.Angles;
import arc.func.Boolf;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.gen.Groups;

import java.util.HashMap;
import java.util.PriorityQueue;

import static mindustry.Vars.world;

/** Routes only across explicitly linked, unobstructed rails. */
public class RailPathfinder{
    private static Object trainWorld;
    private static int trainChanges;
    private static long trainRevision;
    private static final HashMap<RailPointBuild, Seq<Edge>> trainGraph = new HashMap<>();

    private static HashMap<RailPointBuild, Seq<Edge>> trainGraph(){
        boolean fresh = trainWorld == world && trainChanges == world.tileChanges && trainRevision == RailPoint.graphRevision;
        if(fresh){
            for(Seq<Edge> sections : trainGraph.values()){
                for(Edge section : sections){
                    if(!section.valid()){ fresh = false; break; }
                }
                if(!fresh) break;
            }
        }
        if(fresh) return trainGraph;
        trainGraph.clear();
        trainWorld = world;
        trainChanges = world.tileChanges;
        trainRevision = RailPoint.graphRevision;
        for(Building building : Groups.build){
            if(!(building instanceof RailPointBuild source) || !active(source)) continue;
            for(int i = 0; i < source.links.size; i++){
                Building target = world.build(source.links.get(i));
                if(!(target instanceof RailPointBuild next) || !active(next)) continue;
                Edge section = edge(source, next);
                if(section != null) trainGraph.computeIfAbsent(source, key -> new Seq<>()).add(section);
            }
        }
        return trainGraph;
    }
    public static boolean active(RailPointBuild node){
        return node != null && world != null && world.build(node.pos()) == node;
    }

    public static Edge edge(RailPointBuild from, RailPointBuild to){
        if(!active(from) || !active(to) || !from.hasLink(to) || !from.linkValid(to)) return null;
        float[] controls = new float[4];
        if(!((RailPoint)from.block).curve(from.x, from.y, from.rotation, to.x, to.y, to.rotation, controls)) return null;
        return new Edge(from, to, controls);
    }

    public static Route find(RailPointBuild start, float targetX, float targetY){
        return find(start, targetX, targetY, edge -> true);
    }

    public static Route find(RailPointBuild start, float targetX, float targetY, Boolf<Edge> allowed){
        return search(start, targetX, targetY, allowed, node -> true, false);
    }

    public static Route parking(RailPointBuild start, Boolf<RailPointBuild> suitable, Boolf<Edge> allowed){
        return search(start, 0f, 0f, allowed, suitable, true);
    }

    /** Finds a reachable firing position, including positions between rail nodes. */
    public static Route pursuit(RailPointBuild start, Boolf<Vec2> inRange){
        if(!active(start)) return null;
        var graph = trainGraph();
        HashMap<RailPointBuild, Visit> visits = new HashMap<>();
        PriorityQueue<Visit> queue = new PriorityQueue<>((a, b) -> Float.compare(a.cost, b.cost));
        Visit first = new Visit(start, null, 0f);
        visits.put(start, first);
        queue.add(first);
        Vec2 sample = new Vec2();
        while(!queue.isEmpty()){
            Visit visit = queue.remove();
            if(visits.get(visit.node) != visit) continue;
            Visit goal = inRange.get(sample.set(visit.node.x, visit.node.y)) ? visit : null;
            Seq<Edge> adjacent = graph.get(visit.node);
            if(goal == null && adjacent != null) for(Edge section : adjacent){
                if(section.to.team != start.team) continue;
                int steps = Math.max(1, (int)Math.ceil(section.length / 4f));
                for(int i = 1; i <= steps; i++){
                    section.sample(section.length * i / steps, sample);
                    if(inRange.get(sample)){
                        goal = new Visit(section.to, visit, visit.cost + section.length);
                        break;
                    }
                }
                if(goal != null) break;
                float cost = visit.cost + section.length;
                Visit previous = visits.get(section.to);
                if(previous == null || cost < previous.cost){
                    Visit next = new Visit(section.to, visit, cost);
                    visits.put(section.to, next);
                    queue.add(next);
                }
            }
            if(goal != null){
                Seq<RailPointBuild> nodes = new Seq<>();
                for(Visit current = goal; current != null; current = current.previous) nodes.add(current.node);
                nodes.reverse();
                return new Route(nodes, goal.cost);
            }
        }
        return null;
    }

    private static Route search(RailPointBuild start, float targetX, float targetY, Boolf<Edge> allowed,
                                Boolf<RailPointBuild> suitable, boolean parking){
        if(!active(start)) return null;
        // Links are the graph. Avoid scanning a square of map tiles at every visited node.
        HashMap<RailPointBuild, Seq<RailPointBuild>> neighbors = new HashMap<>();
        for(Building building : Groups.build){
            if(!(building instanceof RailPointBuild source) || source.team != start.team || !active(source)) continue;
            for(int i = 0; i < source.links.size; i++){
                Building target = world.build(source.links.get(i));
                if(!(target instanceof RailPointBuild next) || next.team != start.team || !active(next)) continue;
                neighbors.computeIfAbsent(source, key -> new Seq<>()).addUnique(next);
                neighbors.computeIfAbsent(next, key -> new Seq<>()).addUnique(source);
            }
        }
        HashMap<RailPointBuild, Visit> visits = new HashMap<>();
        PriorityQueue<Visit> queue = new PriorityQueue<>((a, b) -> {
            int order = Float.compare(a.cost, b.cost);
            return order == 0 ? Integer.compare(a.node.pos(), b.node.pos()) : order;
        });
        Visit first = new Visit(start, null, 0f);
        visits.put(start, first);
        queue.add(first);
        Visit best = null;
        float closest = Float.POSITIVE_INFINITY;
        while(!queue.isEmpty()){
            Visit visit = queue.remove();
            if(visits.get(visit.node) != visit) continue;
            float proximity = parking ? visit.cost : visit.node.dst2(targetX, targetY);
            if(suitable.get(visit.node) && (best == null || proximity < closest || (proximity == closest && (visit.cost < best.cost
                || (visit.cost == best.cost && visit.node.pos() < best.node.pos()))))){
                closest = proximity;
                best = visit;
                // Dijkstra pops the cheapest refuge first; an exact destination cannot
                // be improved by exploring the rest of the network either.
                if(parking || proximity < 0.0001f) break;
            }
            Seq<RailPointBuild> adjacent = neighbors.get(visit.node);
            if(adjacent == null) continue;
            for(RailPointBuild next : adjacent){
                Edge edge = edge(visit.node, next);
                if(edge == null || !allowed.get(edge)) continue;
                float cost = visit.cost + edge.length;
                Visit previous = visits.get(next);
                if(previous == null || cost < previous.cost){
                    Visit reached = new Visit(next, visit, cost);
                    visits.put(next, reached);
                    queue.add(reached);
                }
            }
        }
        if(best == null) return null;
        Seq<RailPointBuild> nodes = new Seq<>();
        for(Visit visit = best; visit != null; visit = visit.previous) nodes.add(visit.node);
        nodes.reverse();
        return new Route(nodes, best.cost);
    }

    /** A train may revisit a junction from another incoming section, but cannot turn back through its body. */
    public static Route findDirected(RailPointBuild start, RailPointBuild goal, float heading, Boolf<Edge> allowed){
        return directedSearch(start, goal, heading, 0f, allowed, 0f, section -> false);
    }

    public static Route clearance(RailPointBuild start, float heading, float length, Boolf<Edge> allowed){
        return directedSearch(start, null, heading, length, allowed, 0f, section -> false);
    }

    public static Route findDirected(RailPointBuild start, RailPointBuild goal, float heading, Boolf<Edge> allowed,
                                     float bodyLength, Boolf<Edge> occupied){
        return directedSearch(start, goal, heading, 0f, allowed, bodyLength, occupied);
    }

    private record TrackState(RailPointBuild previous, RailPointBuild node, boolean bodyCleared){}

    private static class TrainVisit{
        final TrackState state;
        final TrainVisit previous;
        final float cost, heading;
        TrainVisit(TrackState state, TrainVisit previous, float cost, float heading){
            this.state = state; this.previous = previous; this.cost = cost; this.heading = heading;
        }
    }

    private static Route directedSearch(RailPointBuild start, RailPointBuild goal, float heading, float length, Boolf<Edge> allowed, float bodyLength, Boolf<Edge> occupied){
        if(!active(start) || (goal != null && !active(goal))) return null;
        HashMap<RailPointBuild, Seq<Edge>> neighbors = trainGraph();
        HashMap<TrackState, TrainVisit> visits = new HashMap<>();
        PriorityQueue<TrainVisit> queue = new PriorityQueue<>((a, b) -> {
            int order = Float.compare(a.cost, b.cost);
            if(order == 0) order = Integer.compare(a.state.node.pos(), b.state.node.pos());
            if(order == 0) order = Integer.compare(a.state.previous == null ? -1 : a.state.previous.pos(), b.state.previous == null ? -1 : b.state.previous.pos());
            return order;
        });
        TrackState initial = new TrackState(null, start, bodyLength <= 0f);
        TrainVisit first = new TrainVisit(initial, null, 0f, heading);
        visits.put(initial, first);
        queue.add(first);
        Vec2 sample = new Vec2();
        while(!queue.isEmpty()){
            TrainVisit current = queue.remove();
            if(visits.get(current.state) != current) continue;
            if(goal == current.state.node || (goal == null && current.cost >= length)){
                Seq<RailPointBuild> nodes = new Seq<>();
                for(TrainVisit visit = current; visit != null; visit = visit.previous) nodes.add(visit.state.node);
                nodes.reverse();
                return new Route(nodes, current.cost);
            }
            Seq<Edge> adjacent = neighbors.get(current.state.node);
            if(adjacent == null) continue;
            for(Edge section : adjacent){
                RailPointBuild next = section.to;
                if(next == current.state.previous || next.team != start.team || !allowed.get(section)
                    || (!current.state.bodyCleared && occupied.get(section))) continue;
                float departure = section.sample(0f, sample);
                if(Float.isFinite(current.heading) && Angles.angleDist(current.heading, departure) > 90f) continue;
                float cost = current.cost + section.length;
                TrackState state = new TrackState(current.state.node, next, cost >= bodyLength);
                TrainVisit old = visits.get(state);
                if(old != null && old.cost <= cost) continue;
                TrainVisit reached = new TrainVisit(state, current, cost, section.sample(section.length, sample));
                visits.put(state, reached);
                queue.add(reached);
            }
        }
        return null;
    }

    private static class Visit{
        final RailPointBuild node;
        final Visit previous;
        final float cost;

        Visit(RailPointBuild node, Visit previous, float cost){
            this.node = node;
            this.previous = previous;
            this.cost = cost;
        }
    }

    public static class Route{
        public final Seq<RailPointBuild> nodes;
        public final float cost;

        Route(Seq<RailPointBuild> nodes, float cost){
            this.nodes = nodes;
            this.cost = cost;
        }
    }

    /** Distance lookup on the same cubic drawn by RailPoint; movement uses arc length. */
    public static class Edge{
        public final RailPointBuild from, to;
        public final float length;
        private final float[] x, y, distances;
        private final int fromRotation, toRotation;
        private Object checkedWorld;
        private int checkedChanges;
        private long checkedRevision;
        private boolean checkedValid;

        Edge(RailPointBuild from, RailPointBuild to, float[] controls){
            this.from = from;
            this.to = to;
            fromRotation = from.rotation;
            toRotation = to.rotation;
            float polygon = Mathf.dst(from.x, from.y, controls[0], controls[1])
                + Mathf.dst(controls[0], controls[1], controls[2], controls[3])
                + Mathf.dst(controls[2], controls[3], to.x, to.y);
            int steps = Math.max(24, Mathf.ceil(polygon));
            x = new float[steps + 1];
            y = new float[steps + 1];
            distances = new float[steps + 1];
            for(int i = 0; i <= steps; i++){
                float t = i / (float)steps, s = 1f - t;
                x[i] = s * s * s * from.x + 3f * s * s * t * controls[0] + 3f * s * t * t * controls[2] + t * t * t * to.x;
                y[i] = s * s * s * from.y + 3f * s * s * t * controls[1] + 3f * s * t * t * controls[3] + t * t * t * to.y;
                if(i > 0) distances[i] = distances[i - 1] + Mathf.dst(x[i - 1], y[i - 1], x[i], y[i]);
            }
            length = distances[steps];
            checkedWorld = world;
            checkedChanges = world.tileChanges;
            checkedRevision = RailPoint.graphRevision;
            checkedValid = true;
        }

        public boolean valid(){
            if((from.rotation & 1) != (fromRotation & 1) || (to.rotation & 1) != (toRotation & 1)
                || !active(from) || !active(to) || !from.hasLink(to)) return false;
            // Track geometry and wall intersections only change when the world or links do.
            // Do not rebuild every curve and scan every crossed tile every simulation tick.
            if(checkedWorld != world || checkedChanges != world.tileChanges || checkedRevision != RailPoint.graphRevision){
                checkedWorld = world;
                checkedChanges = world.tileChanges;
                checkedRevision = RailPoint.graphRevision;
                checkedValid = from.linkValid(to);
            }
            return checkedValid;
        }

        public float sample(float distance, Vec2 out){
            distance = Mathf.clamp(distance, 0f, length);
            int lo = 1, hi = distances.length - 1;
            while(lo < hi){
                int mid = (lo + hi) >>> 1;
                if(distances[mid] < distance) lo = mid + 1;
                else hi = mid;
            }
            float span = distances[lo] - distances[lo - 1];
            float t = span == 0f ? 0f : (distance - distances[lo - 1]) / span;
            out.set(Mathf.lerp(x[lo - 1], x[lo], t), Mathf.lerp(y[lo - 1], y[lo], t));
            return Angles.angle(x[lo - 1], y[lo - 1], x[lo], y[lo]);
        }

        public float project(float px, float py){
            float best = Float.POSITIVE_INFINITY, distance = 0f;
            for(int i = 1; i < x.length; i++){
                float dx = x[i] - x[i - 1], dy = y[i] - y[i - 1];
                float length2 = dx * dx + dy * dy;
                float t = length2 == 0f ? 0f : Mathf.clamp(((px - x[i - 1]) * dx + (py - y[i - 1]) * dy) / length2);
                float offset = Mathf.dst2(px, py, x[i - 1] + t * dx, y[i - 1] + t * dy);
                if(offset < best){
                    best = offset;
                    distance = Mathf.lerp(distances[i - 1], distances[i], t);
                }
            }
            return distance;
        }
    }
}



