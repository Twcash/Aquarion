package aquarion.world.AI;

import aquarion.content.AquaSounds;
import arc.math.Mathf;
import arc.math.geom.Path;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
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

import static mindustry.Vars.*;

public class GerbInfantryAI extends AIController {
    float stuckTime = 0f;
    float stuckX = -999f, stuckY = -999f;

    float decisionTimer = 0f;
    float recentDamage = 0f;

    float tacticTime = 0f;
    static final float decisionInterval = 60f;
    static final float stuckRange = tilesize * 1.5f;

    enum Tactic { ADVANCE, RETREAT, REGROUP, FLANK, HOLD, COWER }
    Tactic tactic = Tactic.ADVANCE;
    Tactic lastTactic = null;

    float flankAngle = 0f;

    float soundTimer = 0f;
    static final float soundCooldown = 120f; // ~2 seconds at 60fps
    private static final boolean[] pathNotFound = {false};

    @Override
    public void updateMovement() {
        if (unit == null || !unit.isValid()) return;

        Building core = unit.closestEnemyCore();
        float stuckThreshold = Math.max(1f, stuckRange * 2f / unit.type.speed);

        updateRecentDamage();
        decisionTimer += Time.delta;
        tacticTime += Time.delta;
        soundTimer += Time.delta;

        if (decisionTimer >= decisionInterval) {
            decideTactic(core);
            decisionTimer = 0f;
        }

        // Play sound only when tactic changes
        if (tactic != lastTactic) {
            playTacticSound(tactic);
            lastTactic = tactic;
        }

        // Speaking
        maybeGroupChatter();

        // --Tactics
        //TODO Possibly more variety/variance on existing ones...
        switch (tactic) {
            case ADVANCE -> doAdvance(core);
            case RETREAT -> doRetreat();
            case REGROUP -> doRegroup();
            case FLANK -> doFlank(core);
            case HOLD -> doHold();
            case COWER -> doCower();
        }

        handleStuck(stuckThreshold);
        engageNearbyEnemies();
        faceTarget();
        faceMovement();
    }

    // -- Main tactical behaviors ---

    void doAdvance(Building core) {
        if (core == null || !core.isValid()) {
            tactic = Tactic.REGROUP;
            return;
        }

        if (unit.within(core, unit.range())) {
            engage(core);
        } else {
            if (stuckTime > 60f) {
                Building alt = Vars.indexer.findEnemyTile(unit.team, unit.x, unit.y, 200f, b -> b.team != unit.team);
                if (alt != null) moveTo(alt, 10f);
            } else {
                pathfind(Pathfinder.fieldCore, true, stuckTime > 20f);
            }
        }
    }


    void doRetreat() {
        Unit nearest = Units.closestEnemy(unit.team, unit.x, unit.y, 120f, u -> true);
        if (nearest != null) {
            Tmp.v1.set(unit).sub(nearest).nor().scl(unit.type.speed);
            pathMoveTo(Tmp.v1, unit.type.hitSize*2f, false);

            faceMovement();
        } else {
            tactic = Tactic.ADVANCE;
        }
    }

    void doRegroup() {
        Unit ally = Units.closest(unit.team, unit.x, unit.y, u -> u != unit);
        if (ally != null) {
            Tmp.v1.set(ally).sub(unit).nor().scl(unit.type.speed * 0.8f);
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

        if (tacticTime < 1f) flankAngle = Mathf.random(-60f, 60f);
        Vec2 flankPos = Tmp.v1.trns(unit.angleTo(target) + flankAngle, 60f).add(target);

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
        }
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
        int allies = Groups.unit.count(u -> u.team == unit.team && u.within(unit, 100f));
        int enemies = Groups.unit.count(u -> u.team != unit.team && u.within(unit, 120f));

        float retreatChance = 0f, regroupChance = 0f, flankChance = 0f, holdChance = 0f, cowerChance = 0.05f;

        if (hp < 0.4f) retreatChance += 0.5f;
        if (recentDamage > 30f) retreatChance += 0.3f;
        if (enemies > allies * 2) retreatChance += 0.3f;

        if (allies >= 3 && enemies > 1) regroupChance += 0.4f;
        if (allies >= 4 && enemies <= allies) flankChance += 0.3f;
        if (core != null && unit.within(core, unit.range() / 1.3f)) holdChance += 0.4f;

        if (hp < 0.5f) cowerChance += 0.2f;
        if (recentDamage > 20f) cowerChance += 0.15f;
        if (Groups.unit.contains(u -> u.team == unit.team && u.maxHealth > unit.maxHealth * 1.5f && u.within(unit, 80f)))
            cowerChance += 0.2f;

        float roll = Mathf.random();
        if (roll < retreatChance) tactic = Tactic.RETREAT;
        else if (roll < retreatChance + regroupChance) tactic = Tactic.REGROUP;
        else if (roll < retreatChance + regroupChance + flankChance) tactic = Tactic.FLANK;
        else if (roll < retreatChance + regroupChance + flankChance + holdChance) tactic = Tactic.HOLD;
        else if (roll < retreatChance + regroupChance + flankChance + holdChance + cowerChance) tactic = Tactic.COWER;
        else tactic = Tactic.ADVANCE;

        tacticTime = 0f;
    }


    void handleStuck(float stuckThreshold) {
        if (unit.within(stuckX, stuckY, stuckRange)) {
            stuckTime += Time.delta;

            if (stuckTime > stuckThreshold * 2f) {
                tactic = Tactic.REGROUP;
                stuckTime = 0f;
            }

        } else {
            stuckX = unit.x;
            stuckY = unit.y;
            stuckTime = 0f;
        }
    }

    void updateRecentDamage() {
        float dmg = unit.maxHealth - unit.health;
        if (dmg > recentDamage) recentDamage = dmg;
        recentDamage *= 0.98f;
    }

    void engageNearbyEnemies() {
        Unit enemy = Units.closestEnemy(unit.team, unit.x, unit.y, unit.range(), u -> true);
        Building block = Vars.indexer.findEnemyTile(unit.team, unit.x, unit.y, unit.range(), b -> b.block != null);

        if (enemy != null && enemy.isValid() && unit.within(enemy, unit.range())) {
            engage(enemy);
        } else if (block != null && block.isValid() && unit.within(block, unit.range())) {
            engage(block);
        }
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
        for (var mount : unit.mounts) {
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

    // --- Sound helpers ---

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

        Tmp.v4.set(target);
        Tmp.v5.set(target);
        pathNotFound[0] = false;

        boolean found = controlPath.getPathPosition(
                unit,
                Tmp.v4,
                target,
                Tmp.v5,
                pathNotFound
        );

        if(pathNotFound[0]){
            Tmp.v1.set(target)
                    .sub(unit)
                    .nor()
                    .scl(10f)
                    .add(unit);

            moveTo(Tmp.v1, 0f);
        }
        moveTo(
                Tmp.v5,
                0f,
                4f,
                false,
                null,
                false
        );

        unit.lookAt(Tmp.v5);
    }

}
