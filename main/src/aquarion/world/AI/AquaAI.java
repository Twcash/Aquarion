package aquarion.world.AI;

import arc.func.Boolf;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.ai.Pathfinder;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.Weapon;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.*;

/**
 * Shared foundation for all Aquarion AI controllers.
 *
 * <p>Provides a large toolbox of small, composable helpers for targeting, engagement,
 * movement, pathfinding, stuck handling, terrain checks, boid steering, squad awareness,
 * tactical scans and damage tracking. Subclasses typically only need to override
 * {@link #updateMovement()} (and optionally {@link #updateTargeting()}) and wire the
 * helpers together.</p>
 *
 * <p>Anything returning {@link arc.math.geom.Vec2} returns a reusable temp vector
 * ({@code Tmp.v1/v2}), so copy the result if it must survive the current frame.</p>
 */
public class AquaAI extends AIController {

    // -- shared timing slots (independent from AIController's internal 4-slot timer) --
    protected static final int tTarget = 0, tScan = 1, tPath = 2, tStuck = 3, tWander = 4, tSquad = 5, tMisc = 6;
    protected final Interval aiTimer = new Interval(16);

    // -- pathfinding state used by pathMoveTo/drift/clampPathTarget --
    protected final Vec2 lastPathDest = new Vec2();
    protected final Vec2 pathReq = new Vec2();
    protected float pathTimer = Mathf.random(10f);
    protected static final float pathInterval = 10f;
    protected boolean pathPending = false;
    protected boolean pathUnreachable = false;
    protected boolean pathSide = false;
    protected boolean movedThisFrame = false;

    // -- stuck detection state --
    protected float stuckTime = 0f;
    protected float stuckX = -999f, stuckY = -999f;
    protected static final float defaultStuckRange = tilesize * 1.5f;

    // -- damage tracking state (feeds recentDamage/justHit) --
    protected float recentDamage = 0f;
    protected float lastHealth = 0f;
    protected float stimulusTimer = 0f;

    //================================================================================
    // Lifecycle
    //================================================================================

    @Override
    public void updateUnit(){
        if(unit == null || !unit.isValid()) return;
        super.updateUnit();
    }

    //================================================================================
    // Validity / safety guards
    //================================================================================

    /** Whether this AI has a live, usable unit. */
    public boolean valid(){
        return unit != null && unit.isValid();
    }

    /** Whether the unit is alive and controllable. */
    public boolean alive(){
        return unit != null && !unit.dead();
    }

    /** Whether {@code t} is a legal target for this unit right now. */
    public boolean validTarget(@Nullable Teamc t){
        return t != null && !invalid(t);
    }

    /** Whether {@code t} is a legal target within the given range. */
    public boolean validTarget(@Nullable Teamc t, float range){
        return t != null && !Units.invalidateTarget(t, unit.team, unit.x, unit.y, range);
    }

    /** Whether the unit can even attack anything. */
    public boolean canAttack(){
        return unit.type.canAttack && unit.hasWeapons();
    }

    //================================================================================
    // Targeting
    //================================================================================

    /** Nearest hostile unit within {@code range}, or null. */
    public @Nullable Unit nearestEnemy(float range){
        return Units.closestEnemy(unit.team, unit.x, unit.y, range, u -> true);
    }

    /** Nearest hostile unit within {@code range} matching {@code filter}, or null. */
    public @Nullable Unit nearestEnemy(float range, Boolf<Unit> filter){
        return Units.closestEnemy(unit.team, unit.x, unit.y, range, filter);
    }

    /** Nearest hostile unit on the whole map, or null. */
    public @Nullable Unit nearestEnemyAnywhere(){
        return Units.closestEnemy(unit.team, unit.x, unit.y, Float.MAX_VALUE, u -> true);
    }

    /** Nearest friendly unit within {@code range} (excluding self), or null. */
    public @Nullable Unit nearestAlly(float range){
        return Units.closest(unit.team, unit.x, unit.y, range, u -> u != unit);
    }

    /** Nearest friendly unit within {@code range} matching {@code filter}, or null. */
    public @Nullable Unit nearestAlly(float range, Boolf<Unit> filter){
        return Units.closest(unit.team, unit.x, unit.y, range, filter);
    }

    /** Nearest enemy building within {@code range}, or null. */
    public @Nullable Building nearestEnemyBuilding(float range){
        return Units.findEnemyTile(unit.team, unit.x, unit.y, range, b -> true);
    }

    /** Nearest enemy building within {@code range} matching {@code filter}, or null. */
    public @Nullable Building nearestEnemyBuilding(float range, Boolf<Building> filter){
        return Units.findEnemyTile(unit.team, unit.x, unit.y, range, filter);
    }

    /** Nearest enemy building flagged with {@code flag} within {@code range}, or null. */
    public @Nullable Building nearestEnemyBuilding(BlockFlag flag, float range){
        return Units.findEnemyTile(unit.team, unit.x, unit.y, range, b -> b.block.flags.contains(flag));
    }

    /** Nearest friendly building flagged with {@code flag} within {@code range}, or null. */
    public @Nullable Building nearestAllyBuilding(BlockFlag flag, float range){
        return Units.findAllyTile(unit.team, unit.x, unit.y, range, b -> b.block.flags.contains(flag));
    }

    /** Nearest enemy turret within {@code range}, or null. */
    public @Nullable Building nearestEnemyTurret(float range){
        return Units.findEnemyTile(unit.team, unit.x, unit.y, range, b -> b.block instanceof Turret);
    }

    /** Nearest enemy core, or null. */
    public @Nullable Building nearestEnemyCore(){
        return unit.closestEnemyCore();
    }

    /** Nearest friendly core, or null. */
    public @Nullable Building nearestCore(){
        return unit.closestCore();
    }

    /** Nearest enemy (unit or building) within {@code range}, preferring units. */
    public @Nullable Teamc closestEnemyTarget(float range){
        return Units.closestTarget(unit.team, unit.x, unit.y, range);
    }

    /** Whether any enemy unit or building sits within {@code range}. */
    public boolean anyEnemyNear(float range){
        return nearestEnemy(range) != null || nearestEnemyBuilding(range) != null;
    }

    /** Whether any enemy turret sits within {@code range}. */
    public boolean anyTurretNear(float range){
        return nearestEnemyTurret(range) != null;
    }

    //================================================================================
    // Engagement / weapons
    //================================================================================

    /** Whether {@code t} is an airborne target (flying unit vs. ground unit/building). */
    public static boolean isAir(@Nullable Teamc t){
        return t instanceof Unit u && u.isFlying();
    }

    /** Whether any controllable mount can hit airborne targets. */
    public boolean canHitAir(){
        for(var mount : unit.mounts){
            Weapon w = mount.weapon;
            if(w.controllable && !w.noAttack && w.bullet != null && w.bullet.collidesAir) return true;
        }
        return false;
    }

    /** Whether any controllable mount can hit grounded targets. */
    public boolean canHitGround(){
        for(var mount : unit.mounts){
            Weapon w = mount.weapon;
            if(w.controllable && !w.noAttack && w.bullet != null && w.bullet.collidesGround) return true;
        }
        return false;
    }

    /** Longest range among controllable mounts, or {@code unit.range()} if none. */
    public float weaponRange(){
        float best = unit.range();
        for(var mount : unit.mounts){
            if(mount.weapon.controllable) best = Math.max(best, mount.weapon.range());
        }
        return best;
    }

    /** Whether the target is inside the range of a mount that can actually hit it. */
    public boolean inWeaponRange(Teamc t){
        if(t == null) return false;
        boolean air = isAir(t);
        for(var mount : unit.mounts){
            Weapon w = mount.weapon;
            if(!w.controllable || w.noAttack || w.bullet == null) continue;
            if(air ? !w.bullet.collidesAir : !w.bullet.collidesGround) continue;
            if(unit.within(t, w.range())) return true;
        }
        return false;
    }

    /** Points every controllable mount that can hit {@code t} at it. Does not set {@link #target}. */
    public void engage(Teamc t){
        engage(t, false);
    }

    /** Like {@link #engage(Teamc)} but also forces the unit to face the target. */
    public void engage(Teamc t, boolean forceLook){
        if(t == null || !(t instanceof Healthc h && h.isValid())) return;
        boolean air = isAir(t);
        for(var mount : unit.mounts){
            Weapon w = mount.weapon;
            if(!w.controllable || w.noAttack || w.bullet == null) continue;
            if(air ? w.bullet.collidesAir : w.bullet.collidesGround){
                mount.target = t;
            }
        }
        if(forceLook || unit.type.faceTarget) unit.lookAt(t);
    }

    /** Sets this unit's main {@link #target} and aims all viable mounts at it. */
    public void acquire(Teamc t){
        if(t == null || !(t instanceof Healthc h && h.isValid())) return;
        target = t;
        engage(t);
    }

    /** Clears the main target. */
    public void clearTarget(){
        target = null;
    }

    /** Stops every mount from aiming or firing. */
    public void holdFire(){
        for(var mount : unit.mounts){
            mount.target = null;
            mount.shoot = false;
            mount.rotate = false;
        }
    }

    /** Engages the nearest enemy in range; returns whether anything was engaged. */
    public boolean engageNearest(float range){
        Teamc t = closestEnemyTarget(range);
        if(t != null){
            target = t;
            engage(t);
            return true;
        }
        return false;
    }

    /** Engages the nearest enemy unit in range; returns whether one was engaged. */
    public boolean engageNearestUnit(float range){
        Unit u = nearestEnemy(range);
        if(u != null){
            target = u;
            engage(u);
            return true;
        }
        return false;
    }

    /** Engages the nearest enemy building in range; returns whether one was engaged. */
    public boolean engageNearestBuilding(float range){
        Building b = nearestEnemyBuilding(range);
        if(b != null){
            target = b;
            engage(b);
            return true;
        }
        return false;
    }

    //================================================================================
    // Movement
    //================================================================================

    /** Move directly to {@code t}, stopping {@code radius} away. */
    public void moveToTarget(Teamc t, float radius){
        moveTo(t, radius);
    }

    /** Move directly to {@code t}, stopping {@code radius} away, with manual smoothing. */
    public void moveToTarget(Teamc t, float radius, float smooth){
        moveTo(t, radius, smooth);
    }

    /** Keep an exact {@code distance} from {@code t} (retreats when too close). */
    public void keepDistance(Position t, float distance){
        moveTo(t, distance, 100f, true, null);
    }

    /** Move toward {@code t} and keep firing while doing so. */
    public boolean advanceOn(Teamc t, float radius){
        if(t == null || invalid(t)) return false;
        target = t;
        engage(t);
        moveTo(t, radius);
        return true;
    }

    /** Orbit {@code t} at {@code radius} while firing, using the built-in circle attack. */
    public void orbit(Teamc t, float radius){
        target = t;
        engage(t);
        if(unit.hasWeapons()){
            circleAttack(radius);
        }else{
            circle(t, radius);
        }
    }

    /** Strafe around {@code t}: circle the target while keeping it in weapon range. */
    public void strafe(Teamc t, float radius){
        target = t;
        engage(t);
        float ang = unit.angleTo(t);
        float diff = Angles.angleDist(ang, unit.rotation);
        if(diff > 70f && unit.within(t, radius)){
            circle(t, radius);
        }else{
            moveTo(t, radius);
        }
    }

    /**
     * Wall-safe strafing: weaves along a {@code radius} ring around {@code t}
     * while firing, pathing to points on the ring so the unit never rams a wall.
     * Staggered per unit so squads don't strafe in perfect sync.
     */
    public void strafeRing(Teamc t, float radius){
        if(t == null || invalid(t)) return;
        target = t;
        engage(t);
        //weave around the target's side; the phase keeps each unit off-sync
        float phase = (unit.id * 137.508f) % 360f;
        float ang = unit.angleTo(t) + 90f * side() + Mathf.sin(Time.time * 0.15f + phase) * 35f;
        Tmp.v1.trns(ang, radius).add(t);
        pathMoveTo(Tmp.v1, Math.max(radius * 0.25f, tilesize * 2f));
    }

    /** Run away from {@code threat} to a point {@code dist} away, pathing to avoid walls. */
    public void fleeFrom(Teamc threat, float dist){
        if(threat == null) return;
        Tmp.v1.set(unit).sub(threat).nor().scl(dist).add(unit);
        pathMoveTo(Tmp.v1, 0f);
    }

    /** Retreat behind {@code protector} relative to {@code threat}, pathing to avoid walls. */
    public void hideBehind(Teamc threat, Teamc protector, float dist){
        if(protector == null) return;
        if(threat != null){
            Tmp.v1.set(threat).sub(protector).nor().scl(-dist).add(protector);
        }else{
            Tmp.v1.set(protector).add(Tmp.v2.rnd(dist));
        }
        pathMoveTo(Tmp.v1, unit.type.hitSize);
    }

    /** March toward the nearest enemy spawn point. */
    public void moveToSpawn(float radius){
        Tile spawner = getClosestSpawner();
        if(spawner != null){
            moveTo(spawner, radius);
        }
    }

    /** Stops the unit dead (zeroes move pref). */
    public void halt(){
        unit.movePref(Tmp.v1.setZero());
    }

    //================================================================================
    // Pathfinding
    //================================================================================

    /**
     * Pathfinds to {@code target} using the flow-field pathfinder instead of beelining.
     * Works for ground units; flying units just use a direct {@code moveTo}.
     */
    public void pathMoveTo(Vec2 target, float arriveDist){
        pathMoveTo(target, arriveDist, false);
    }

    /** Like {@link #pathMoveTo(Vec2, float)} but allows sidestep drifting when stuck. */
    public void pathMoveTo(Vec2 target, float arriveDist, boolean allowUnstick){
        if(target == null || !valid()) return;
        if(unit.isFlying()){
            moveTo(target, arriveDist);
            return;
        }
        if(unit.within(target, arriveDist)) return;

        clampPathTarget(target, pathReq);
        pathTimer += Time.delta;

        if(pathTimer >= pathInterval || lastPathDest.isZero() || unit.within(lastPathDest, tilesize * 1.5f)){
            pathTimer = 0f;
            pathPending = false;
            var result = controlPath.getPathPosition(unit, pathReq);
            if(result.move && result.dest != null){
                lastPathDest.set(result.dest);
                pathUnreachable = false;
            }else if(result.unreachable){
                lastPathDest.setZero();
                pathUnreachable = true;
                //back off before re-asking so the unit skirts around instead of ramming the obstacle
                pathTimer = -60f;
            }else{
                //path still computing - back off before re-asking
                lastPathDest.setZero();
                pathPending = true;
                pathTimer = -30f;
            }
        }

        if(!lastPathDest.isZero() && !pathPending){
            movedThisFrame = true;
            movePrefTo(lastPathDest);
        }else{
            drift();
        }

        if(allowUnstick && stuckTime > 20f){
            movedThisFrame = true;
            pathfind(Pathfinder.fieldCore, true, true);
        }
    }

    /** Copies a path target clamped to at most {@code tilesize * 90} away. */
    public void clampPathTarget(Vec2 target, Vec2 out){
        float maxD = tilesize * 90f;
        if(unit.within(target, maxD)){
            out.set(target);
        }else{
            out.trns(unit.angleTo(target), maxD).add(unit);
        }
    }

    /** Moves at full speed toward {@code target} without arrival deceleration. */
    public void movePrefTo(Vec2 target){
        if(target == null) return;
        unit.movePref(Tmp.v1.set(target).sub(unit).setLength(unit.speed()));
    }

    /**
     * Keeps the unit pushing toward its goal while the pathfinder catches up.
     * When the target is unreachable, skids sideways along the obstacle instead
     * of bulldozing into it. Movement stays at full speed so units don't crawl.
     */
    public void drift(){
        float ang = unit.angleTo(pathReq.x, pathReq.y);
        float offset;
        if(pathUnreachable){
            //skid consistently to one side (per unit) so the unit actually rounds the obstacle
            offset = 90f * (unit.id % 2 == 0 ? 1 : -1);
        }else{
            pathSide = !pathSide;
            offset = 20f * (pathSide ? 1 : -1);
        }
        movedThisFrame = true;
        unit.movePref(Tmp.v1.trns(ang + offset, unit.speed()));
    }

    /** Whether the pathfinder has handed us a usable step yet. */
    public boolean isPathPending(){
        return pathPending;
    }

    /** Pathfind toward the enemy core field. */
    public void pathfindToCore(){
        pathfind(Pathfinder.fieldCore, true);
    }

    /** Pathfind toward the enemy core field with full options. */
    public void pathfindToCore(boolean leader, boolean avoidance){
        pathfind(Pathfinder.fieldCore, leader, avoidance);
    }

    /** Pathfind toward an arbitrary flow field. */
    public void pathfindTo(int field){
        pathfind(field, true);
    }

    /** Pathfind toward an arbitrary flow field with options. */
    public void pathfindTo(int field, boolean leader, boolean avoidance){
        pathfind(field, leader, avoidance);
    }

    //================================================================================
    // Stuck handling
    //================================================================================

    /** Tracks net progress and unsticks the unit when it has been idle too long. */
    public void updateStuck(){
        float threshold = Math.max(1f, defaultStuckRange * 2f / unit.type.speed);
        if(unit.within(stuckX, stuckY, defaultStuckRange)){
            stuckTime += Time.delta;
            if(stuckTime > threshold * 4f){
                unstick();
                stuckX = unit.x;
                stuckY = unit.y;
                stuckTime = 0f;
            }
        }else{
            stuckX = unit.x;
            stuckY = unit.y;
            stuckTime = 0f;
        }
    }

    /** Whether the unit has been stuck longer than it should have taken to clear the range. */
    public boolean isStuck(){
        float threshold = Math.max(1f, defaultStuckRange * 2f / unit.type.speed);
        return stuckTime > threshold;
    }

    /** Resets the stuck tracker to the current position. */
    public void resetStuck(){
        stuckTime = 0f;
        stuckX = unit.x;
        stuckY = unit.y;
    }

    /** Sidesteps perpendicular to the unit's facing to break free of walls or crowds. */
    public void unstick(){
        float ang = unit.rotation + 90f * (unit.id % 2 == 0 ? 1 : -1);
        //full-speed sideways shove - a moveTo here would decelerate to a crawl and never escape
        unit.movePref(Tmp.v1.trns(ang, unit.speed() * 2f));
    }

    //================================================================================
    // Terrain / environment
    //================================================================================

    /** Tile at world coordinates, or null if out of bounds. */
    public @Nullable Tile tileAt(float x, float y){
        return world.tile(World.toTile(x), World.toTile(y));
    }

    /** Tile the unit is standing on, or null. */
    public @Nullable Tile unitTile(){
        return unit.tileOn();
    }

    /** Whether the given world point is liquid floor. */
    public boolean isWater(float x, float y){
        Tile t = tileAt(x, y);
        return t != null && t.floor().isLiquid;
    }

    /** Whether the given world point is deep floor. */
    public boolean isDeepWater(float x, float y){
        Tile t = tileAt(x, y);
        return t != null && t.floor().isDeep();
    }

    /** Whether the unit currently stands in liquid. */
    public boolean inWater(){
        return isWater(unit.x, unit.y);
    }

    /** Whether the unit currently stands in deep liquid. */
    public boolean inDeepWater(){
        Tile t = unit.tileOn();
        return t != null && t.floor().isDeep();
    }

    /** Whether the unit stands on a solid tile. */
    public boolean onSolid(){
        return unit.onSolid();
    }

    /** Whether a tile is empty ground a foot unit can stand on. */
    public boolean walkable(@Nullable Tile t){
        return t != null && !t.solid() && !t.floor().isLiquid && !t.floor().isDeep();
    }

    /** Whether the unit can physically pass a tile. */
    public boolean passable(float x, float y){
        Tile t = tileAt(x, y);
        return t != null && unit.canPass(t.x, t.y);
    }

    /** Nearest walkable, empty tile within {@code radius}, or null. */
    public @Nullable Tile nearestClearTile(float radius){
        int r = (int)(radius / tilesize);
        int cx = unit.tileX(), cy = unit.tileY();
        Tile best = null;
        float bestD = Float.MAX_VALUE;
        for(int dx = -r; dx <= r; dx++){
            for(int dy = -r; dy <= r; dy++){
                Tile t = world.tile(cx + dx, cy + dy);
                if(t == null || !walkable(t) || t.build != null) continue;
                float d = dx * dx + dy * dy;
                if(d < bestD){
                    bestD = d;
                    best = t;
                }
            }
        }
        return best;
    }

    /** March out of deep water toward the nearest clear tile. */
    public void fleeWater(){
        Tile best = nearestClearTile(tilesize * 4f);
        if(best != null){
            pathMoveTo(Tmp.v1.set(best.worldx(), best.worldy()), tilesize);
        }else{
            Tmp.v1.set(unit.vel).scl(-2f).add(unit);
            pathMoveTo(Tmp.v1, 0f);
        }
    }

    /** Whether the unit can boost and is currently airborne. */
    public boolean boosting(){
        return unit.type.canBoost && unit.elevation > 0.001f && !unit.onSolid();
    }

    /** Descend to the ground if boostable and airborne. */
    public void descend(){
        if(unit.type.canBoost && unit.elevation > 0.001f && !unit.onSolid()){
            unit.elevation = Mathf.approachDelta(unit.elevation, 0f, unit.type.descentSpeed);
        }
    }

    /** Ascend to full elevation if boostable. */
    public void ascend(){
        unit.elevation = Mathf.approachDelta(unit.elevation, 1f, unit.type.riseSpeed);
    }

    //================================================================================
    // Boid steering
    //================================================================================

    /**
     * Soft push-away vector from the nearest map edges. Strength grows the closer
     * the unit gets to a border.
     */
    public Vec2 boundaryPush(float margin, float force){
        float bx = 0f, by = 0f;
        if(unit.x < margin){
            bx += (margin - unit.x) / margin;
        }else if(unit.x > world.unitWidth() - margin){
            bx -= (unit.x - (world.unitWidth() - margin)) / margin;
        }
        if(unit.y < margin){
            by += (margin - unit.y) / margin;
        }else if(unit.y > world.unitHeight() - margin){
            by -= (unit.y - (world.unitHeight() - margin)) / margin;
        }
        return Tmp.v1.set(bx * force, by * force);
    }

    /** Soft push-away vector from natural walls and own-team buildings near the unit. */
    public Vec2 obstaclePush(float radius, float force){
        float sx = 0f, sy = 0f;
        int r = (int)(radius / tilesize) + 1;
        int ox = unit.tileX(), oy = unit.tileY();
        for(int dx = -r; dx <= r; dx++){
            for(int dy = -r; dy <= r; dy++){
                Tile t = world.tile(ox + dx, oy + dy);
                if(t == null) continue;
                boolean solid = (t.build != null && t.build.team == unit.team) || (t.build == null && t.solid());
                if(!solid) continue;
                float cx = (t.x + 0.5f) * tilesize, cy = (t.y + 0.5f) * tilesize;
                float d = unit.dst(cx, cy);
                if(d < 0.001f || d >= radius) continue;
                float w = (1f - d / radius) * force;
                sx += (unit.x - cx) / d * w;
                sy += (unit.y - cy) / d * w;
            }
        }
        return Tmp.v1.set(sx, sy);
    }

    /** Separation (push away) from nearby allies. */
    public Vec2 separation(float radius, float force){
        Vec2 sum = Tmp.v1.setZero();
        Units.nearby(unit.team, unit.x, unit.y, radius, other -> {
            if(other == unit || other.dead() || !other.isValid()) return;
            float d = unit.dst(other);
            if(d < 0.001f || d >= radius + other.hitSize / 2f) return;
            float w = (1f - d / (radius + other.hitSize / 2f)) * force;
            sum.add((unit.x - other.x) / d * w, (unit.y - other.y) / d * w);
        });
        return sum;
    }

    /** Alignment (match heading) with nearby allies. */
    public Vec2 alignment(float radius, float force){
        Vec2 sum = Tmp.v1.setZero();
        int[] count = {0};
        Units.nearby(unit.team, unit.x, unit.y, radius, other -> {
            if(other == unit || other.dead() || !other.isValid()) return;
            sum.add(other.vel);
            count[0]++;
        });
        return count[0] > 0 ? sum.scl(force / count[0]) : sum.setZero();
    }

    /** Cohesion (pull toward) the centre of nearby allies. */
    public Vec2 cohesion(float radius, float force){
        Vec2 sum = Tmp.v1.setZero();
        int[] count = {0};
        Units.nearby(unit.team, unit.x, unit.y, radius, other -> {
            if(other == unit || other.dead() || !other.isValid()) return;
            sum.add(other);
            count[0]++;
        });
        if(count[0] == 0) return sum.setZero();
        return sum.scl(1f / count[0]).sub(unit).scl(force);
    }

    /** Force vector pointing at {@code target} with the given magnitude. */
    public Vec2 seek(Vec2 target, float force){
        return Tmp.v1.set(target).sub(unit).setLength(force);
    }

    /** Force vector pointing away from {@code threat} with the given magnitude. */
    public Vec2 flee(Vec2 threat, float force){
        return Tmp.v1.set(unit).sub(threat).setLength(force);
    }

    /** A wander destination point from the unit at {@code angle} and {@code radius}. */
    public Vec2 wanderTarget(float angle, float radius){
        return Tmp.v1.trns(angle, radius).add(unit);
    }

    /** A new random wander angle (degrees). */
    public float randomWanderAngle(){
        return Mathf.random(360f);
    }

    //================================================================================
    // Squad / awareness
    //================================================================================

    /** Count of friendly, alive units within {@code range}. */
    public int nearbyAllies(float range){
        return Groups.unit.count(u -> u.team == unit.team && !u.dead() && u.within(unit, range));
    }

    /** Count of hostile, alive units within {@code range}. */
    public int nearbyEnemies(float range){
        return Groups.unit.count(u -> u.team != unit.team && !u.dead() && u.within(unit, range));
    }

    /** Number of friendly units on the whole map. */
    public int totalAllies(){
        return Groups.unit.count(u -> u.team == unit.team && !u.dead());
    }

    /** Number of hostile units on the whole map. */
    public int totalEnemies(){
        return Groups.unit.count(u -> u.team != unit.team && !u.dead());
    }

    /** Whether this unit is outnumbered within {@code range} by more than {@code margin}. */
    public boolean outnumbered(float range, int margin){
        return nearbyEnemies(range) > nearbyAllies(range) + margin;
    }

    /** A nearby ally with more max health than this unit, or null. */
    public @Nullable Unit findStrongerAlly(float range){
        return Units.closest(unit.team, unit.x, unit.y, range, u -> u != unit && u.isValid() && u.maxHealth > unit.maxHealth * 1.25f);
    }

    /** A nearby ally with less max health than this unit, or null. */
    public @Nullable Unit findWeakerAlly(float range){
        return Units.closest(unit.team, unit.x, unit.y, range, u -> u != unit && u.isValid() && u.maxHealth < unit.maxHealth);
    }

    /**
     * The lowest-id unit of the given AI class within {@code radius} - the implicit
     * squad leader. Returns this unit if no other qualifies.
     */
    public Unit squadLeader(Class<? extends AIController> type, float radius){
        Unit lead = unit;
        for(Unit u : Groups.unit){
            if(u.team != unit.team || u.dead() || !u.isValid()) continue;
            if(!type.isInstance(u.controller())) continue;
            if(u.within(unit, radius) && u.id() < lead.id()) lead = u;
        }
        return lead;
    }

    //================================================================================
    // Tactical scans
    //================================================================================

    /** Whether at least {@code threshold} enemy turrets sit between this unit and the enemy core. */
    public boolean heavyDefenseAhead(int threshold){
        Building core = unit.closestEnemyCore();
        if(core == null) return false;
        float dist = unit.dst(core);
        if(dist < unit.range() * 1.5f) return false;

        int threats = 0;
        for(Building b : indexer.getEnemy(unit.team, BlockFlag.turret)){
            if(b == null || b.health <= 0f) continue;
            if(!b.within(unit, dist)) continue;
            if(Math.abs(Angles.angleDist(unit.angleTo(b.x, b.y), unit.angleTo(core.x, core.y))) < 60f){
                threats++;
            }
        }
        return threats >= threshold;
    }

    /** Whether a solid wall sits between this unit and the nearest threat. */
    public boolean hasCoverNearby(float threatRange){
        Unit threat = nearestEnemy(threatRange);
        float away = threat == null ? unit.rotation + 180f : unit.angleTo(threat) + 180f;
        int cx = unit.tileX(), cy = unit.tileY();
        for(int dx = -4; dx <= 4; dx++){
            for(int dy = -4; dy <= 4; dy++){
                Tile t = world.tile(cx + dx, cy + dy);
                if(t == null || !t.solid()) continue;
                float ang = unit.angleTo(t.worldx(), t.worldy());
                if(Math.abs(Angles.angleDist(ang, away)) < 105f) return true;
            }
        }
        return false;
    }

    /** Nearest solid wall on the far side of the threat, or null. */
    public @Nullable Tile findCover(float threatRange, float searchRadius){
        Unit threat = nearestEnemy(threatRange);
        float away = threat == null ? unit.rotation + 180f : unit.angleTo(threat) + 180f;
        Tile best = null;
        float bestD = Float.MAX_VALUE;
        int cx = unit.tileX(), cy = unit.tileY();
        int r = (int)(searchRadius / tilesize);
        for(int dx = -r; dx <= r; dx++){
            for(int dy = -r; dy <= r; dy++){
                Tile t = world.tile(cx + dx, cy + dy);
                if(t == null || !t.solid()) continue;
                float ang = unit.angleTo(t.worldx(), t.worldy());
                if(Math.abs(Angles.angleDist(ang, away)) > 100f) continue;
                float d = dx * dx + dy * dy;
                if(d < bestD){
                    bestD = d;
                    best = t;
                }
            }
        }
        return best;
    }

    /** Move to hide just behind the given cover wall, opposite the threat. */
    public void takeCover(Tile cover, float threatRange){
        if(cover == null) return;
        Unit threat = nearestEnemy(threatRange);
        if(threat != null){
            float behind = Angles.angle(cover.worldx() - threat.x, cover.worldy() - threat.y);
            Tmp.v1.set(cover.worldx(), cover.worldy())
                .add(Angles.trnsx(behind, tilesize * 1.5f), Angles.trnsy(behind, tilesize * 1.5f));
        }else{
            Tmp.v1.set(cover.worldx(), cover.worldy() - tilesize * 2f);
        }
        pathMoveTo(Tmp.v1, tilesize);
        if(threat != null && unit.within(threat, unit.range())){
            engage(threat);
        }
    }

    //================================================================================
    // Damage / status tracking
    //================================================================================

    /** Call once per update so {@link #recentDamage} and {@link #justHit()} stay fresh. */
    public void updateDamageTracking(){
        float dmg = unit.maxHealth - unit.health;
        if(dmg > recentDamage) recentDamage = dmg;
        recentDamage *= 0.98f;
        if(unit.health < lastHealth){
            stimulusTimer = 120f;
        }
        lastHealth = unit.health;
        if(stimulusTimer > 0f) stimulusTimer -= Time.delta;
    }

    /** Whether the unit has taken a hit within roughly the last two seconds. */
    public boolean justHit(){
        return stimulusTimer > 0f;
    }

    /** Health fraction 0..1. */
    public float healthRatio(){
        return unit.healthf();
    }

    /** Whether health is below the given fraction. */
    public boolean lowHealth(float threshold){
        return unit.healthf() < threshold;
    }

    /** Whether health is at or above the given fraction. */
    public boolean healthy(float threshold){
        return unit.healthf() >= threshold;
    }

    //================================================================================
    // Building helpers (for worker/builder AIs)
    //================================================================================

    /** Whether the unit currently has a build plan active. */
    public boolean isBuilding(){
        return unit.isBuilding();
    }

    /** Clears the unit's current build plan. */
    public void clearBuilding(){
        unit.clearBuilding();
    }

    /** Adds a build plan for the block at tile coordinates. */
    public void buildAt(int x, int y, Block block){
        unit.addBuild(new BuildPlan(x, y, 0, block));
    }

    /** Adds a deconstruct plan for the tile. */
    public void deconstructAt(int x, int y){
        unit.addBuild(new BuildPlan(x, y));
    }

    /** Whether a block can be placed by this unit's team at tile coordinates. */
    public boolean validPlace(Block block, int x, int y){
        return Build.validPlace(block, unit.team, x, y, 0);
    }

    //================================================================================
    // Checkpoints / routes
    //================================================================================

    /** Index of the nearest point in a checkpoint route, so a unit joins at the closest one. */
    public static int nearestCheckpoint(Unit unit, Seq<Tile> points){
        int nearest = 0;
        float minDst = Float.MAX_VALUE;
        for(int i = 0; i < points.size; i++){
            Tile tile = points.get(i);
            float dst = unit.dst2(tile.worldx(), tile.worldy());
            if(dst < minDst){
                minDst = dst;
                nearest = i;
            }
        }
        return nearest;
    }

    /** Advances the checkpoint index, wrapping around, and returns the new checkpoint. */
    public static Tile nextCheckpoint(Seq<Tile> points, int[] index){
        int i = (index[0] + 1) % Math.max(points.size, 1);
        index[0] = i;
        return points.size == 0 ? null : points.get(i);
    }

    //================================================================================
    // Generic search / decision helpers
    //================================================================================

    /** Nearest element of a list to a point, or null. */
    public static <T extends Position> @Nullable T nearest(Position from, Seq<T> list){
        T best = null;
        float bestD = Float.MAX_VALUE;
        for(T t : list){
            if(t == null) continue;
            float d = t.dst2(from);
            if(d < bestD){
                bestD = d;
                best = t;
            }
        }
        return best;
    }

    /**
     * Rolls an index into {@code weights} proportional to each weight
     * (e.g. {@code rollIndex(0.5f, 0.2f, 0.3f)}). Returns -1 if all weights are zero.
     */
    public static int rollIndex(float... weights){
        float total = 0f;
        for(float w : weights) total += w;
        if(total <= 0f) return -1;
        float roll = Mathf.random(total);
        for(int i = 0; i < weights.length; i++){
            roll -= weights[i];
            if(roll <= 0f) return i;
        }
        return weights.length - 1;
    }

    //================================================================================
    // Small math / misc
    //================================================================================

    /** Deterministic ±1 side for this unit, useful for splitting squads into left/right. */
    public int side(){
        return unit.id % 2 == 0 ? 1 : -1;
    }

    /** Random chance test. */
    public boolean chance(float probability){
        return Mathf.chance(probability);
    }

    /** Random value in [min, max]. */
    public float rand(float min, float max){
        return Mathf.random(min, max);
    }

    /** Random value in [-amount, amount]. */
    public float randRange(float amount){
        return Mathf.range(amount);
    }

    /** Angle from this unit to a point. */
    public float angleTo(float x, float y){
        return unit.angleTo(x, y);
    }

    /** Angle from this unit to a target. */
    public float angleTo(Position t){
        return unit.angleTo(t);
    }

    /** Distance from this unit to a point. */
    public float dst(float x, float y){
        return unit.dst(x, y);
    }

    /** Distance from this unit to a target. */
    public float dst(Position t){
        return unit.dst(t);
    }

    /** Squared distance from this unit to a point. */
    public float dst2(float x, float y){
        return unit.dst2(x, y);
    }

    /** Whether this unit is within {@code dist} of a point. */
    public boolean within(float x, float y, float dist){
        return unit.within(x, y, dist);
    }

    /** Whether this unit is within {@code dist} of a target. */
    public boolean within(Position t, float dist){
        return unit.within(t, dist);
    }

    /** Whether the unit is currently moving. */
    public boolean moving(){
        return unit.moving();
    }

    /** The unit's current movement speed. */
    public float speed(){
        return unit.speed();
    }

    /** Rotate the unit toward a point. */
    public void face(float x, float y){
        unit.lookAt(x, y);
    }

    /** Rotate the unit toward a target. */
    public void face(Position t){
        unit.lookAt(t);
    }

    }