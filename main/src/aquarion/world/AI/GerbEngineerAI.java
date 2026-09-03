package aquarion.world.AI;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.ai.types.GroundAI;
import mindustry.ctype.ContentType;
import mindustry.entities.units.AIController;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.SectorPreset;
import mindustry.core.World;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;

import static aquarion.content.blocks.UnitBlocks.concussorPad;
import static aquarion.content.blocks.UnitBlocks.infantryPad;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

/**
 * Gerb worker AI. Engineers group up in a crew and march far away from the player's
 * core, then lay down the outpost blueprint for the current sector difficulty.
 */
public class GerbEngineerAI extends GroundAI {
    public static final float minOutpostDist = 60f;
    public static final float maxOutpostDist = 160f;
    public static final int crewSize = 6;
    public static final float gatherRange = 320f * tilesize;
    public static final float gatherTimeout = 15 * 60;
    public static final float plotTimeout = 120 * 60;
    public static final float planTimeout = 10 * 60;

    private final Seq<Unit> crew = new Seq<>();
    private static final boolean[] pathNotFound = {false};
    /** Last tile the pathfinder handed us, so we keep moving if the path is mid-computation. */
    final Vec2 lastPathDest = new Vec2();
    /** Clamped request point (never path halfway across the map). */
    final Vec2 pathReq = new Vec2();
    float pathTimer = Mathf.random(10f);
    static final float pathInterval = 10f;
    boolean pathPending = false;
    boolean pathSide = false;
    private final Vec2 site = new Vec2();
    private final Vec2 plot = new Vec2();
    private float gatherTimer = 0f;
    private float plotTimer = 0f;
    private float planTimer = 0f;
    private boolean ready = false;
    private boolean expanded = false;
    private int slot = 0;

    public static Seq<Block> blueprint(int difficulty){
        Seq<Block> b = new Seq<>();
        //every tier founds the outpost with an infantry landing pad
        b.add(infantryPad);
        b.add(concussorPad);
        //TODO add the higher-tier buildings behind difficulty gates:
        //if(difficulty >= 3) b.add(...);
        //if(difficulty >= 6) b.add(...);
        //if(difficulty >= 9) b.add(...);
        return b;
    }

    /** Difficulty of the sector the player is currently on, 1 if there is no sector. */
    public static int currentDifficulty(){
        var sector = state.rules.sector;
        if(sector == null || sector.info == null || sector.info.lastPresetName == null) return 1;

        SectorPreset preset = (SectorPreset)Vars.content.getByName(ContentType.sector, sector.info.lastPresetName);
        return preset == null ? 1 : Math.max(1, Math.round(preset.difficulty));
    }

    @Override
    public void updateMovement(){
        faceMovement();

        if(unit == null || !unit.isValid()) return;

        //engineers are non-combat; never aim at anything
        for(var mount : unit.mounts) mount.target = null;

        if(!ready){
            gatherTimer += Time.delta;
            if(!gather()){
                if(gatherTimer < gatherTimeout) return;
            }
            slot = slotIndex();
            chooseSite();
            ready = true;
        }

        build();
    }

    boolean gather(){
        crew.clear();
        crew.add(unit);
        for(Unit u : Groups.unit){
            if(u == unit || u.dead() || !u.isValid()) continue;
            if(u.team != unit.team) continue;
            if(!(u.controller() instanceof GerbEngineerAI)) continue;
            if(u.within(unit, gatherRange)) crew.add(u);
        }

        if(crew.size < crewSize){
            //drift toward the crew's centre so the squad actually collects
            Vec2 centroid = Tmp.v1.set(0f, 0f);
            for(Unit u : crew) centroid.add(u.x(), u.y());
            centroid.scl(1f / crew.size);
            pathMoveTo(centroid, tilesize * 16f);
            if(unit.vel().len() > 0.5f){
                unit.lookAt(unit.vel().angle());
            }
            return false;
        }
        return true;
    }

    void chooseSite(){
        Building core = unit.closestCore();
        if(core == null){
            site.set(unit.x, unit.y);
            return;
        }

        Vec2 anchor = Tmp.v1.set(0f, 0f);
        for(Unit u : crew) anchor.add(u.x(), u.y());
        anchor.scl(1f / crew.size);

        float min = minOutpostDist * tilesize, max = maxOutpostDist * tilesize;
        float cx = core.x, cy = core.y;
        int range = (int)(max / tilesize);

        Tile best = null;
        float bestDist = Float.MAX_VALUE;
        Seq<Tile> candidates = new Seq<>();

        //scan the ring around the player's core; pick the spot closest to the crew
        for(int ty = World.toTile(cy) - range; ty <= World.toTile(cy) + range; ty += 3){
            for(int tx = World.toTile(cx) - range; tx <= World.toTile(cx) + range; tx += 3){
                Tile t = world.tile(tx, ty);
                if(t == null) continue;

                //not near the player, but also not absurdly far away
                float distCore = Mathf.dst(t.worldx(), t.worldy(), cx, cy);
                if(distCore < min || distCore > max) continue;

                //nearest open, walkable ground that can take the pad
                if(t.solid() || t.floor().isLiquid || t.floor().isDeep()) continue;
                if(!Build.validPlace(infantryPad, unit.team, tx, ty, 0)) continue;

                //make sure it sits on real, reachable land - not a tiny island or a wall pocket
                if(!walkable(t)
                    && !walkable(world.tile(tx + 1, ty)) && !walkable(world.tile(tx - 1, ty))
                    && !walkable(world.tile(tx, ty + 1)) && !walkable(world.tile(tx, ty - 1))) continue;

                float dist = Mathf.dst(t.worldx(), t.worldy(), anchor.x, anchor.y);
                if(dist < bestDist){
                    bestDist = dist;
                    candidates.clear();
                    candidates.add(t);
                }else if(dist <= bestDist + 2f * tilesize){
                    //keep a couple of near-equal options so a full patch isn't re-picked forever
                    candidates.add(t);
                }
            }
        }

        if(!candidates.isEmpty()){
            best = candidates.random();
            site.set(best.worldx(), best.worldy());
            return;
        }

        //fallback: straight away from the player's core, clamped to the map
        float dir = unit.angleTo(core) + 180f;
        site.set(core.x + Angles.trnsx(dir, max), core.y + Angles.trnsy(dir, max));
    }

    /** Builds this engineer's slice of the outpost blueprint. */
    void build(){
        Seq<Block> blueprint = blueprint(currentDifficulty());
        if(blueprint.isEmpty()) return;

        //keep expanding the outpost - work through the blueprint and start over
        Block block = blueprint.get(slot % blueprint.size);
        BuildPlan plan = unit.buildPlan();

        if(plan == null){
            setupPlot(block);

            if(!plot.isZero() && doneAt(plot, block)){
                nextPlot();
                return;
            }

            //couldn't reach or place at this site (walls, water, coins) - roll a new one
            plotTimer += Time.delta;
            if(plotTimer > plotTimeout){
                nextPlot();
                chooseSite();
                return;
            }

            pathMoveTo(plot, unit.type.buildRange * 0.7f);
            faceMovement();
            if(unit.within(plot, unit.type.buildRange * 0.7f)){
                Tile t = closestPlaceable(plot, block, 3);
                if(t != null){
                    unit.updateBuilding = true;
                    unit.addBuild(new BuildPlan(t.x, t.y, 0, block));
                    planTimer = 0f;
                }
            }
            return;
        }

        //construction stuck (no resources, planning collision...) - give up and move on
        planTimer += Time.delta;
        if(planTimer > planTimeout){
            unit.clearBuilding();
            nextPlot();
            return;
        }

        BuildPlan active = plan;
        Tile tile = active.tile();

        if(tile != null){
            //walk over and face the direction of travel
            if(!unit.within(tile.worldx(), tile.worldy(), unit.type.buildRange)){
                pathMoveTo(Tmp.v1.set(tile.worldx(), tile.worldy()), unit.type.buildRange * 0.6f);
            }else{
                //in range: stand and face the building being worked on
                unit.lookAt(tile.worldx(), tile.worldy());
            }
        }

        if(done(active, block) || failed(active, block)){
            unit.clearBuilding();
            nextPlot();
        }
    }

    /** Claims the next plot in the layout if none is active. */
    void setupPlot(Block block){
        if(plot.isZero()){
            //preferred spot for this slot's ring position
            Vec2 pref = Tmp.v1.trns(slot * 137.508f, (block.size + 4f) * tilesize * (1f + slot / 8f)).add(site);
            Tile t = closestPlaceable(pref, block, 6);
            if(t == null){
                //no room in that direction - use any free patch near the outpost
                t = anywhereNear(block);
            }
            if(t != null){
                plot.set(t.worldx(), t.worldy());
                plotTimer = 0f;
            }else if(!expanded){
                //outpost is full: push it further from the core once; the timeout moves us on after that
                expandSite();
                expanded = true;
            }
        }
    }

    /** Searches a square ring around the outpost anchor for any open, walkable spot. */
    Tile anywhereNear(Block block){
        int sx = World.toTile(site.x), sy = World.toTile(site.y);
        Tile best = null;
        float bestDist = Float.MAX_VALUE;

        for(int ty = sy - 18; ty <= sy + 18; ty++){
            for(int tx = sx - 18; tx <= sx + 18; tx++){
                Tile t = world.tile(tx, ty);
                if(t == null) continue;
                if(!Build.validPlace(block, unit.team, tx, ty, 0)) continue;
                if(!walkable(t)) continue;

                float d = Mathf.dst2(t.worldx(), t.worldy(), site.x, site.y);
                if(d < bestDist){
                    bestDist = d;
                    best = t;
                }
            }
        }
        return best;
    }

    /** Moves the whole outpost anchor further away from the player's core. */
    void expandSite(){
        Building core = unit.closestCore();
        if(core == null) return;
        float dir = Angles.angle(site.x - core.x, site.y - core.y);
        site.add(Angles.trnsx(dir, 24f * tilesize), Angles.trnsy(dir, 24f * tilesize));
    }

    /** Advances to the next building in the blueprint and clears the current plot. */
    void nextPlot(){
        slot++;
        plot.setZero();
        planTimer = 0f;
        expanded = false;
    }

    /** Nearest tile within a small radius of the plot that can actually take the block. */
    Tile closestPlaceable(Vec2 center, Block block, int radius){
        int cx = World.toTile(center.x), cy = World.toTile(center.y);
        Tile best = null;
        float bestDist = Float.MAX_VALUE;

        for(int ty = cy - radius; ty <= cy + radius; ty++){
            for(int tx = cx - radius; tx <= cx + radius; tx++){
                Tile t = world.tile(tx, ty);
                if(t == null) continue;
                if(!Build.validPlace(block, unit.team, tx, ty, 0)) continue;

                float d = Mathf.dst2(t.worldx(), t.worldy(), center.x, center.y);
                if(d < bestDist){
                    bestDist = d;
                    best = t;
                }
            }
        }
        return best;
    }

    /** Whether a ground unit can stand on this tile. */
    boolean walkable(Tile t){
        return t != null && !t.solid() && !t.floor().isLiquid && !t.floor().isDeep();
    }

    /** Whether the target block is fully built on this plot. */
    boolean doneAt(Vec2 plot, Block block){
        Tile tile = world.tile(World.toTile(plot.x), World.toTile(plot.y));
        if(tile == null) return false;
        Building b = tile.build;
        return (b == null && tile.block() == block && tile.team() == unit.team)
            || (b != null && b.block == block && b.team == unit.team && !(b instanceof ConstructBlock.ConstructBuild));
    }

    /** Whether the active plan's building is complete. */
    boolean done(BuildPlan plan, Block block){
        Tile tile = plan.tile();
        if(tile == null) return false;
        Building b = tile.build;
        return (b == null && tile.block() == block && tile.team() == unit.team)
            || (b != null && b.block == block && b.team == unit.team && !(b instanceof ConstructBlock.ConstructBuild));
    }

    /** Whether the active plan is unrecoverable and should be dropped. */
    boolean failed(BuildPlan plan, Block block){
        Tile tile = plan.tile();
        if(tile == null) return true;

        Building b = tile.build;
        if(b instanceof ConstructBlock.ConstructBuild cb){
            return cb.current != block || cb.team != unit.team;
        }
        if(b != null){
            return b.block != block || b.team != unit.team;
        }
        //nothing standing: done if ours stands, otherwise the spot was taken
        return tile.block() != Blocks.air && !(tile.block() == block && tile.team() == unit.team);
    }

    /** Stable index within the crew, so each engineer builds its own part of the outpost. */
    int slotIndex(){
        int n = 0;
        for(Unit u : crew){
            if(u.id() < unit.id) n++;
        }
        return n % Math.max(blueprint(currentDifficulty()).size, 1);
    }

    void moveTo(float x, float y, float radius){
        Tmp.v2.set(x, y);
        pathMoveTo(Tmp.v2, radius);
    }

    /** Pathfinds to a destination instead of beelining, and faces the direction of travel. */
    void pathMoveTo(Vec2 target, float arriveDist){
        if(target == null || unit == null || !unit.isValid()) return;
        if(unit.isFlying()) return;

        if(unit.within(target, arriveDist)) return;

        //clamp the request so units never path the whole map in one go, and pace it out
        clampPathTarget(target, pathReq);
        pathTimer += Time.delta;

        if(pathTimer >= pathInterval || lastPathDest.isZero() || unit.within(lastPathDest, tilesize * 1.5f)){
            pathTimer = 0f;
            pathPending = false;
            var result = Vars.controlPath.getPathPosition(unit, pathReq);
            if(result.move && result.dest != null){
                lastPathDest.set(result.dest);
            }else if(result.unreachable){
                lastPathDest.setZero();
            }else{
                //path still computing - don't wait on a reached step; back off before re-asking
                lastPathDest.setZero();
                pathPending = true;
                pathTimer = -30f;
            }
        }

        if(!lastPathDest.isZero() && !pathPending){
            moveTo(lastPathDest, 0f);
        }else{
            //no step yet: drift diagonally toward the goal instead of freezing
            drift();
        }

        if(unit.vel().len() > 0.5f){
            unit.lookAt(unit.vel().angle());
        }
    }

    /** Copies a path target that is at most {@code maxPathDist} away, dropping far endpoints to a nearer waypoint. */
    void clampPathTarget(Vec2 target, Vec2 out){
        float maxD = tilesize * 90f;
        if(unit.within(target, maxD)){
            out.set(target);
        }else{
            out.trns(unit.angleTo(target), maxD).add(unit);
        }
    }

    /** Keeps the unit pushing forward-toward its goal while the pathfinder catches up. */
    void drift(){
        pathSide = !pathSide;
        float ang = unit.angleTo(pathReq.x, pathReq.y);
        Tmp.v1.trns(ang + 20f * (pathSide ? 1 : -1), unit.type.speed * 2f).add(unit);
        moveTo(Tmp.v1, 0f);
    }
}