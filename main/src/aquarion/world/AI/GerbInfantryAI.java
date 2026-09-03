package aquarion.world.AI;

import aquarion.content.AquaSounds;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Path;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Log;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.ai.ControlPathfinder;
import mindustry.ai.Pathfinder;
import mindustry.ai.types.CommandAI;
import mindustry.ai.types.GroundAI;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.world.Tile;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.*;

public class GerbInfantryAI extends GroundAI {
    float stuckTime = 0f;
    float stuckX = -999f, stuckY = -999f;

    float decisionTimer = Mathf.random(120f);
    float recentDamage = 0f;
    /** Per-unit personality (higher = bolder, lower = skittish). */
    final float personality = Mathf.random(0.2f, 0.8f);
    /** Frames since the unit last took a hit; drives "under fire" reactions. */
    float stimulusTimer = 0f;
    float lastHealth = 0f;

    float tacticTime = 0f;
    static final float decisionInterval = 60f;
    static final float stuckRange = tilesize * 1.5f;
    /** Only rally onto allies within this range - don't chase a unit across the map. */
    static final float regroupRange = tilesize * 45f;
    /** How close units must be to share one squad decision. */
    static final float squadRadius = tilesize * 32f;

    enum Tactic { ADVANCE, RETREAT, REGROUP, FLANK, HOLD, COWER, COVER }
    Tactic tactic = Tactic.ADVANCE;
    Tactic lastTactic = null;

    float flankAngle = 0f;
    /** This unit's personal approach lane, re-rolled at the start of each route. */
    float routeLat = 0f;
    /** Tactic the current lane/flank was rolled for. */
    Tactic rolledFor = null;

    float soundTimer = 0f;
    static final float soundCooldown = 120f;
    private static final boolean[] pathNotFound = {false};
    /** Last tile the pathfinder handed us, so we keep moving if the path is mid-computation. */
    final Vec2 lastPathDest = new Vec2();
    /** Clamped request point (never path halfway across the map). */
    final Vec2 pathReq = new Vec2();
    float pathTimer = Mathf.random(10f);
    static final float pathInterval = 10f;
    boolean pathPending = false;
    boolean pathSide = false;
    boolean movedThisFrame = false;
    float debugTimer = 0f;
    int insuranceFired = 0;

    @Override
    public void updateMovement() {
        if (unit == null || !unit.isValid()) return;

        //never drown: if we ended up in deep water, march straight back out
        if (inDeepWater()) {
            fleeWater();
            faceMovement();
            return;
        }

        Building core = unit.closestEnemyCore();
        float stuckThreshold = Math.max(1f, stuckRange * 2f / unit.type.speed);

        updateRecentDamage();
        tacticTime += Time.delta;
        soundTimer += Time.delta;

        //individual decisions: every unit reacts to its own surroundings, staggered so they don't act in sync
        decisionTimer += Time.delta;
        if (decisionTimer >= decisionInterval + Mathf.random(-15f, 15f)) {
            decideTactic(core);
            decisionTimer = 0f;
        }

        //announce own call changes
        if (tactic != lastTactic) {
            playTacticSound(tactic);
            lastTactic = tactic;
        }

        //Speaking
        maybeGroupChatter();

        switch (tactic) {
            case ADVANCE -> doAdvance(core);
            case RETREAT -> doRetreat();
            case REGROUP -> doRegroup();
            case FLANK -> doFlank(core);
            case HOLD -> doHold();
            case COWER -> doCower();
            case COVER -> doCover();
        }

        handleStuck(stuckThreshold);
        boolean engaged = engageNearbyEnemies();
        //floor: never sit still - if nothing moved us and we aren't fighting, march toward the enemy
        if (!movedThisFrame && !engaged) {
            try{
                pathfind(Pathfinder.fieldCore, true, stuckTime > 20f);
                movedThisFrame = true;
                insuranceFired++;
            }catch(Throwable ignored){ /*missing core field - just stay put this frame*/ }
        }
        //pure pathfinding - the GroundAI move calls handle all wall/darkness avoidance
        if (engaged) {
            faceTarget();
        } else {
            faceMovement();
        }
        if (engaged) movedThisFrame = true;

        //temporary stuck-diagnostic: only logs when a unit idles for a while
        debugTimer += Time.delta;
        if (debugTimer >= 90f) {
            debugTimer = 0f;
            if (unit.vel().len() < 0.6f) {
                Log.info("[AI-hold] id=@ tac=@ move=@ engaged=@ core=@ water=@ lastPath=@,@ | @,@ ins=@",
                    unit.id, tactic, movedThisFrame, engaged,
                    core == null ? -1 : (int)unit.dst(core), inDeepWater(),
                    (int)lastPathDest.x, (int)lastPathDest.y,
                    (int)unit.x, (int)unit.y, insuranceFired);
            }
        }
        movedThisFrame = false;
    }

    // -- Main tactical behaviors ---

    void doAdvance(Building core) {
        if (core == null || !core.isValid()) {
            //no enemy core to assault: hunt the nearest enemy presence instead
            Building alt = Vars.indexer.findEnemyTile(unit.team, unit.x, unit.y, Float.MAX_VALUE, b -> b.block != null && b.team != unit.team);
            Unit far = Units.closestEnemy(unit.team, unit.x, unit.y, Float.MAX_VALUE, u -> true);
            if (alt != null) {
                pathMoveTo(Tmp.v1.set(alt.x, alt.y), unit.range() * 0.6f, false);
                return;
            }
            if (far != null) {
                pathMoveTo(Tmp.v1.set(far.x, far.y), unit.range() * 0.6f, false);
                return;
            }
            //nothing hostile anywhere - hold; doHold keeps pushing if anything shows up
            tactic = Tactic.HOLD;
            return;
        }

        //walls of turrets ahead - most of the squad swings around, the bold ones probe
        if (heavyDefenseAhead() && personality < 0.65f) {
            tactic = Tactic.FLANK;
            return;
        }

        //enemy structures nearby are valid targets too, not just the core
        Building structure = Vars.indexer.findEnemyTile(unit.team, unit.x, unit.y, tilesize * 30f, b -> b.block != null && b.team != unit.team);
        if (structure != null && unit.within(structure, unit.range())) {
            engage(structure);
            return;
        }

        Unit enemy = Units.closestEnemy(unit.team, unit.x, unit.y, unit.range(), u -> true);
        if (enemy != null && unit.within(enemy, unit.range())) {
            engage(enemy);
            return;
        }

        if (unit.within(core, unit.range())) {
            engage(core);
            return;
        }

        if (stuckTime > 60f) {
            //instead of ramming into whoever is blocking us, sidestep first
            Tmp.v1.trns(unit.rotation + 90f * (unit.id % 2 == 0 ? 1 : -1), unit.type.speed * 1.5f).add(unit);
            pathMoveTo(Tmp.v1, 4f, false);
            return;
        }

        //mass advance via the shared, precomputed flow field - no per-unit A* requests to overload
        movedThisFrame = true;
        pathfind(Pathfinder.fieldCore, true, stuckTime > 20f);
    }


    void doRetreat() {
        Unit nearest = Units.closestEnemy(unit.team, unit.x, unit.y, 120f, u -> true);
        if (nearest != null) {
            //fall back a short step so tougher allies can push ahead; don't run off, don't wade into water
            Vec2 dir = Tmp.v2.set(unit).sub(nearest);
            if (isWater(unit.x + dir.x, unit.y + dir.y)) {
                dir.rotate(90f * (unit.id % 2 == 0 ? 1 : -1));
            }

            Vec2 target = Tmp.v1.set(unit).add(dir.nor().scl(unit.type.hitSize * 6f));
            pathMoveTo(target, unit.type.hitSize * 2f, false);
            faceMovement();
        } else {
            tactic = Tactic.ADVANCE;
        }
    }

    void doRegroup() {
        //only rally onto allies that are actually close - don't chase a unit across the map
        Unit ally = Units.closest(unit.team, unit.x, unit.y, u -> u != unit && u.within(unit, regroupRange));
        if (ally != null) {
            //regroup to a personal spot around the ally instead of stacking inside it
            Tmp.v1.set(ally).add(Tmp.v2.trns((unit.id * 137.508f) % 360f, unit.type.hitSize * 2f));
            pathMoveTo(Tmp.v1, unit.type.hitSize*2f, false);
            faceMovement();
        } else {
            tactic = Tactic.ADVANCE;
        }
    }

    void doFlank(Building core) {
        Building target = (core != null && core.isValid()) ? core :
                Vars.indexer.findEnemyTile(unit.team, unit.x, unit.y, 140f, b -> b.block != null && b.team != unit.team);

        if (target == null || !target.isValid()) {
            tactic = Tactic.ADVANCE;
            return;
        }

        //pick a side whenever the squad's call changes so the squad splits instead of piling up
        if (rolledFor != tactic) {
            flankAngle = (unit.id % 2 == 0 ? -1f : 1f) * Mathf.random(35f, 70f);
            rolledFor = tactic;
        }

        float dist = Mathf.dst(unit.x, unit.y, target.x, target.y);
        float outDist = Math.min(dist * 0.8f, tilesize * 15f);
        Vec2 flankPos = Tmp.v1.trns(unit.angleTo(target) + flankAngle, outDist).add(target);

        //if that swing lands in a lake, mirror it to the other side
        if (isWater(flankPos.x, flankPos.y)) {
            flankPos.set(target).add(Tmp.v2.trns(unit.angleTo(target) - flankAngle, outDist));
        }

        pathMoveTo(flankPos, 10f, false);
        faceMovement();
        if (unit.within(target, unit.range())) {
            engage(target);
        }
    }

    void doHold() {
        Unit enemy = Units.closestEnemy(unit.team, unit.x, unit.y, unit.range(), u -> true);
        if (enemy != null) {
            unit.lookAt(enemy);
            engage(enemy);
            //under fire: break contact aim with a short sidestep
            if (stimulusTimer > 0f && unit.vel().len() < 2f) {
                Tmp.v1.set(enemy).add(Tmp.v2.trns(unit.angleTo(enemy) + 90f * (unit.id % 2 == 0 ? 1 : -1), unit.range() * 0.5f));
                pathMoveTo(Tmp.v1, unit.range() * 0.3f, false);
            }
        } else {
            Building block = Vars.indexer.findEnemyTile(unit.team, unit.x, unit.y, unit.range(), b -> b.block != null && b.team != unit.team);
            if (block != null && block.isValid()) {
                unit.lookAt(block);
                engage(block);
            } else {
                //holding but nothing in range: push toward the enemy core via the shared flow field
                movedThisFrame = true;
                pathfind(Pathfinder.fieldCore, true, stuckTime > 20f);
            }
        }
    }

    /** Takes cover behind the nearest solid wall on the far side from the threat. */
    void doCover() {
        Unit threat = Units.closestEnemy(unit.team, unit.x, unit.y, 200f, u -> true);
        float away = threat == null ? unit.rotation + 180f : unit.angleTo(threat) + 180f;

        Tile best = null;
        float bestD = Float.MAX_VALUE;
        int cx = unit.tileX(), cy = unit.tileY();

        for (int dx = -6; dx <= 6; dx++) {
            for (int dy = -6; dy <= 6; dy++) {
                Tile t = world.tile(cx + dx, cy + dy);
                if (t == null || !t.solid()) continue;
                //only walls that sit between us and the threat
                float ang = unit.angleTo(t.worldx(), t.worldy());
                if (Math.abs(Angles.angleDist(ang, away)) > 100f) continue;
                float d = dx * dx + dy * dy;
                if (d < bestD) {
                    bestD = d;
                    best = t;
                }
            }
        }

        if (best != null) {
            //hide just behind the wall, opposite the threat
            if (threat != null) {
                float behind = Angles.angle(best.worldx() - threat.x, best.worldy() - threat.y);
                Tmp.v1.set(best.worldx(), best.worldy())
                    .add(Angles.trnsx(behind, tilesize * 1.5f), Angles.trnsy(behind, tilesize * 1.5f));
            } else {
                Tmp.v1.set(best.worldx(), best.worldy() - tilesize * 2f);
            }
            pathMoveTo(Tmp.v1, tilesize, false);
            if (threat != null && unit.within(threat, unit.range())) {
                engage(threat);
            }
        } else {
            //nothing to hide behind - just pull back a step
            doRetreat();
        }
    }

    /** Whether a solid wall sits between this unit and the nearest threat. */
    boolean hasCoverNearby() {
        Unit threat = Units.closestEnemy(unit.team, unit.x, unit.y, 160f, u -> true);
        float away = threat == null ? unit.rotation + 180f : unit.angleTo(threat) + 180f;
        int cx = unit.tileX(), cy = unit.tileY();

        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                Tile t = world.tile(cx + dx, cy + dy);
                if (t == null || !t.solid()) continue;
                float ang = unit.angleTo(t.worldx(), t.worldy());
                if (Math.abs(Angles.angleDist(ang, away)) < 105f) return true;
            }
        }
        return false;
    }

    void doCower() {
        Unit threat = Units.closestEnemy(unit.team, unit.x, unit.y, 160f, u -> true);
        Unit protector = findStrongerAlly(threat);

        if (protector == null || !protector.isValid()) {
            tactic = Tactic.REGROUP;
            return;
        }

        // Move to a position behind the protector relative to the enemy direction
        if (threat != null) {
            Tmp.v1.set(threat).sub(protector).nor().scl(-10f).add(protector);
        } else {
            Tmp.v1.set(protector).add(Tmp.v2.rnd(5f));
        }

        pathMoveTo(Tmp.v1, 2, false);
        faceMovement();
        // Peek and fire from cover
        engageNearbyEnemies();
    }

    void decideTactic(Building core) {
        float hp = unit.healthf();
        int allies = Groups.unit.count(u -> u.team == unit.team && !u.dead() && u.within(unit, 100f));
        int enemies = Groups.unit.count(u -> u.team != unit.team && !u.dead() && u.within(unit, 120f));
        int closeEnemies = Groups.unit.count(u -> u.team != unit.team && !u.dead() && u.within(unit, 40f));
        boolean hit = stimulusTimer > 0f;
        boolean turretWall = heavyDefenseAhead();
        float bold = personality, timid = 1f - personality;

        float coverChance = 0f, retreatChance = 0f, flankChance = 0f, holdChance = 0f, cowerChance = 0f;

        //stimulus: catching fire and hurt, with a wall nearby -> duck behind it
        if (hit && hp < 0.8f && hasCoverNearby()) {
            coverChance += 0.5f + timid * 0.35f;
            if (hp < 0.45f) coverChance += 0.25f;
        }

        //stimulus: badly hurt -> fall back and let tougher allies take point
        if (hit && hp < 0.38f) {
            retreatChance += 0.5f + timid * 0.4f;
        }
        if (closeEnemies > allies + 1 && hp < 0.6f) {
            retreatChance += 0.25f;
        }

        //map: a wall of turrets ahead -> swing around it
        if (turretWall) {
            flankChance += 0.45f + bold * 0.3f;
            if (hp < 0.6f) flankChance += 0.2f;
        }

        //stimulus: enemy in range -> fight from this position
        boolean inContact = (core != null && unit.within(core, unit.range() * 1.15f)) || closeEnemies > 0;
        if (inContact) {
            holdChance += 0.55f;
            if (bold > 0.6f) holdChance += 0.3f;
            else if (timid > 0.6f && hit) retreatChance += 0.25f;
            if (enemies <= allies && hit) holdChance += 0.2f;
        }

        //stimulus: scared and protected -> cower behind the heavy
        Unit threat = Units.closestEnemy(unit.team, unit.x, unit.y, 160f, u -> true);
        if (hp < 0.65f && findStrongerAlly(threat) != null) {
            cowerChance += 0.25f + timid * 0.15f;
        }

        float roll = Mathf.random();
        if (roll < coverChance) tactic = Tactic.COVER;
        else if (roll < coverChance + retreatChance) tactic = Tactic.RETREAT;
        else if (roll < coverChance + retreatChance + flankChance) tactic = Tactic.FLANK;
        else if (roll < coverChance + retreatChance + flankChance + holdChance) tactic = Tactic.HOLD;
        else if (roll < coverChance + retreatChance + flankChance + holdChance + cowerChance) tactic = Tactic.COWER;
        else tactic = Tactic.ADVANCE;

        tacticTime = 0f;
    }


    void handleStuck(float stuckThreshold) {
        //track net progress, not raw movement - shuffling against a wall still counts as stuck
        if (unit.within(stuckX, stuckY, stuckRange)) {
            stuckTime += Time.delta;

            if (stuckTime > stuckThreshold * 4f) {
                unstick(stuckThreshold);
                stuckX = unit.x;
                stuckY = unit.y;
                stuckTime = 0f;
            }

        } else {
            stuckX = unit.x;
            stuckY = unit.y;
            stuckTime = 0f;
        }
    }

    /**
     * Doesn't try to force the same unreachable objective: switches to a reachable target,
     * or steps back. Movement always goes through the pathfinder.
     */
    void unstick(float stuckThreshold) {
        //abandon the unreachable objective for a nearby one that's actually reachable
        Unit far = Units.closestEnemy(unit.team, unit.x, unit.y, Float.MAX_VALUE, u -> true);
        Building structure = Vars.indexer.findEnemyTile(unit.team, unit.x, unit.y, Float.MAX_VALUE, b -> b.block != null && b.team != unit.team);

        if (far != null) {
            pathMoveTo(Tmp.v1.set(far.x, far.y), unit.range() * 0.6f, false);
        } else if (structure != null) {
            pathMoveTo(Tmp.v1.set(structure.x, structure.y), unit.range() * 0.6f, false);
        } else {
            //nothing to reach for - step back so we unwedge
            Tmp.v1.trns(unit.rotation + 180f, unit.type.speed * 2f).add(unit);
            pathMoveTo(Tmp.v1, 2f, false);
        }
    }

    void updateRecentDamage() {
        float dmg = unit.maxHealth - unit.health;
        if (dmg > recentDamage) recentDamage = dmg;
        recentDamage *= 0.98f;

        //stimulus: remember that we just took a hit (stays hot for ~2 seconds)
        if (unit.health < lastHealth) {
            stimulusTimer = 120f;
        }
        lastHealth = unit.health;
        if (stimulusTimer > 0f) stimulusTimer -= Time.delta;
    }

    /**
     * A unit should never stand still. If nothing else moved us this frame,
     * orbit the nearest target (staying inside firing range), or shuffle forward.
     */
    void keepBusy() {
        if (unit.vel().len() > 1f) return; //already on the move

        Unit enemy = Units.closestEnemy(unit.team, unit.x, unit.y, unit.range() * 1.4f, u -> true);
        Building block = Vars.indexer.findEnemyTile(unit.team, unit.x, unit.y, unit.range() * 1.4f, b -> b.block != null && b.team != unit.team);

        if (enemy != null) {
            //strafe an orbit around the enemy so we keep firing while staying busy
            Tmp.v1.set(enemy).add(Tmp.v2.trns(unit.angleTo(enemy) + (unit.id % 2 == 0 ? 90f : -90f), unit.range() * 0.6f));
            engage(enemy);
        } else if (block != null) {
            Tmp.v1.set(block).add(Tmp.v2.trns(unit.angleTo(block) + (unit.id % 2 == 0 ? 90f : -90f), unit.range() * 0.6f));
            engage(block);
        } else {
            //nothing to fight and nothing moved us - actually go hunt instead of spinning in place
            rallyAtNearestEnemy();
        }

        pathMoveTo(Tmp.v1, 0f, false);
    }

    /** Points this unit at the nearest real enemy objective, or holds position if none exists. */
    void rallyAtNearestEnemy() {
        Building core = unit.closestEnemyCore();
        Building structure = Vars.indexer.findEnemyTile(unit.team, unit.x, unit.y, Float.MAX_VALUE, b -> b.block != null && b.team != unit.team);
        Unit distant = Units.closestEnemy(unit.team, unit.x, unit.y, Float.MAX_VALUE, u -> true);

        if (core != null) {
            Tmp.v1.set(core);
        } else if (structure != null) {
            Tmp.v1.set(structure);
        } else if (distant != null) {
            Tmp.v1.set(distant);
        } else {
            //no enemies anywhere: don't wander, just hold this spot
            Tmp.v1.set(unit.x, unit.y);
        }

        if (isWater(Tmp.v1.x, Tmp.v1.y)) {
            Tmp.v1.trns(unit.angleTo(Tmp.v1) + 90f, tilesize * 2f).add(unit);
        }
    }

    /** The unit that decides for this squad: the nearest lowest-id engineer nearby. */
    Unit squadLeader() {
        Unit lead = unit;
        for (Unit u : Groups.unit) {
            if (u.team != unit.team || u.dead() || !u.isValid()) continue;
            if (!(u.controller() instanceof GerbInfantryAI)) continue;
            if (u.within(unit, squadRadius) && u.id() < lead.id()) lead = u;
        }
        return lead;
    }

    /** Whether there is a thick wall of enemy turrets between the unit and the enemy core. */
    boolean heavyDefenseAhead() {
        Building core = unit.closestEnemyCore();
        if (core == null) return false;

        float dist = unit.dst(core);
        if (dist < unit.range() * 1.5f) return false; //already at the fight

        int threats = 0;
        for (Building b : Vars.indexer.getEnemy(unit.team, BlockFlag.turret)) {
            if (b == null || b.health <= 0f) continue;
            if (!b.within(unit, dist)) continue; //only ones between us and the core
            if (Math.abs(Angles.angleDist(unit.angleTo(b.x, b.y), unit.angleTo(core.x, core.y))) < 60f) {
                threats++;
            }
        }
        return threats >= 4;
    }

    boolean inDeepWater() {
        Tile t = unit.tileOn();
        return t != null && t.floor().isDeep() && !unit.type.naval;
    }

    void fleeWater() {
        Tile best = null;
        float bestDist = Float.MAX_VALUE;
        int cx = unit.tileX(), cy = unit.tileY();

        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                Tile t = world.tile(cx + dx, cy + dy);
                if (t == null || t.floor().isDeep() || t.solid()) continue;
                float d = dx * dx + dy * dy;
                if (d < bestDist) {
                    bestDist = d;
                    best = t;
                }
            }
        }

        if (best != null) {
            Tmp.v1.set(best.worldx(), best.worldy());
            pathMoveTo(Tmp.v1, 2f, false);
        } else {
            //no land nearby: turn around and head back
            Tmp.v1.set(unit.vel).scl(-2f).add(unit);
            pathMoveTo(Tmp.v1, 0f, false);
        }
    }

    boolean isWater(float x, float y) {
        Tile t = world.tile(World.toTile(x), World.toTile(y));
        return t != null && t.floor().isLiquid;
    }

    boolean engageNearbyEnemies() {
        Unit enemy = Units.closestEnemy(unit.team, unit.x, unit.y, unit.range(), u -> true);
        Building block = Vars.indexer.findEnemyTile(unit.team, unit.x, unit.y, unit.range(), b -> b.block != null);

        if (enemy != null && enemy.isValid() && unit.within(enemy, unit.range())) {
            engage(enemy);
            return true;
        } else if (block != null && block.isValid() && unit.within(block, unit.range())) {
            engage(block);
            return true;
        }
        return false;
    }

    void engage(Unit u) {
        if (u == null || !u.isValid()) return;
        for (var mount : unit.mounts) {
            if(u.type.flying){
                if (mount.weapon.controllable && mount.weapon.bullet != null && mount.weapon.bullet.collidesAir) {
                    if(unit.type.faceTarget) unit.lookAt(u);
                    mount.target = u;
                }
            } else {
                if (mount.weapon.controllable && mount.weapon.bullet != null && mount.weapon.bullet.collidesGround) {
                    if(unit.type.faceTarget) unit.lookAt(u);
                    mount.target = u;
                }
            }
        }
    }

    void engage(Building b) {
        if (b == null || !b.isValid()) return;
        for (           mindustry.entities.units.WeaponMount mount : unit.mounts) {
            if (mount.weapon.controllable && mount.weapon.bullet != null && mount.weapon.bullet.collidesGround) {
                if(unit.type.faceTarget) unit.lookAt(b);
                mount.target = b;
            }
        }
    }

    Unit findStrongerAlly(Unit threat) {
        return Groups.unit.find(u -> u.team == unit.team
                && u != unit
                && u.maxHealth > unit.maxHealth * 1.25f
                && u.within(unit, 100f)
                && (!u.dead() && u.isValid())
        );
    }

    void playTacticSound(Tactic t) {
        if (soundTimer < soundCooldown) return;
        soundTimer = 0f;

        switch (t) {
            case ADVANCE -> AquaSounds.advance.at(unit.x, unit.y,1-Mathf.random(0,.3f), .4f-Mathf.random(0,.3f));
            case RETREAT -> AquaSounds.retreat.at(unit.x, unit.y,1-Mathf.random(0,.3f), 0.4f-Mathf.random(0,.3f));
            case REGROUP -> AquaSounds.rally.at(unit.x, unit.y,1-Mathf.random(0,.3f), 0.4f-Mathf.random(0,.3f));
            case FLANK -> AquaSounds.hold.at(unit.x, unit.y,1-Mathf.random(0,.3f), 0.4f-Mathf.random(0,.3f));
            case HOLD -> AquaSounds.hold.at(unit.x, unit.y,1-Mathf.random(0,.3f), .4f-Mathf.random(0,.3f));
            case COWER -> AquaSounds.retreat.at(unit.x, unit.y, 1-Mathf.random(0,.3f),0.4f-Mathf.random(0,.3f));
            case COVER -> AquaSounds.hold.at(unit.x, unit.y,1-Mathf.random(0,.3f), .4f-Mathf.random(0,.3f));
        }
    }

    void maybeGroupChatter() {
        if (soundTimer < soundCooldown) return;
        int nearbyAllies = Groups.unit.count(u -> u.team == unit.team && u.within(unit, 120f));
        if (nearbyAllies >= 2 && Mathf.chance(0.01f)) {
            playTacticSound(tactic);
        }
    }
    void pathMoveTo(Vec2 target, float arriveDist, boolean allowUnstick){
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
            movedThisFrame = true;
            moveTo(lastPathDest, 0f);
        }else{
            //no step yet: drift diagonally toward the goal instead of freezing
            drift();
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
        movedThisFrame = true;
        Tmp.v1.trns(ang + 20f * (pathSide ? 1 : -1), unit.type.speed * 2f).add(unit);
        moveTo(Tmp.v1, 0f);
    }

}
