package aquarion.world.units;

import arc.math.Mathf;
import mindustry.ai.Pathfinder;
import mindustry.ai.types.GroundAI;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.Turret;

import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

public class AquaGroundAI extends GroundAI {
    @Override
    public void updateMovement() {
        Building core = unit.closestEnemyCore();
        Building turret = Units.findEnemyTile(unit.team, unit.x, unit.y, unit.range(),
                b -> b.block instanceof Turret);
        if (core != null && unit.within(core, unit.range() / 1.3f + core.block.size * tilesize / 2f)) {
            target = core;
            for (var mount : unit.mounts) {
                if (mount.weapon.controllable && mount.weapon.bullet.collidesGround) {
                    mount.target = core;
                }
            }
        }
        boolean move = true;
        if (turret != null && unit.within(turret, unit.range() * 0.8f)) {
            move = false;
        }
        int nearbyAllies = Units.count(unit.x, unit.y, 120, u -> u != unit && u.within(unit, 60f));
        if (nearbyAllies > 3) {
            unit.vel.scl(0.7f); // Reduce speed
        }
        if ((core == null || !unit.within(core, unit.type.range * 0.5f))) {
            if (state.rules.waves && unit.team == state.rules.defaultTeam) {
                Tile spawner = getClosestSpawner();
                if (spawner != null && unit.within(spawner, state.rules.dropZoneRadius + 120f)) move = false;
                if (spawner == null && core == null) move = false;
            }
            if (core == null && (!state.rules.waves || getClosestSpawner() == null)) {
                move = false;
            }
            if (move) pathfind(Pathfinder.fieldCore);
        }
        if (unit.type.canBoost && unit.elevation > 0.001f && !unit.onSolid()) {
            unit.elevation = Mathf.approachDelta(unit.elevation, 0f, unit.type.riseSpeed);
        }
        faceTarget();
    }
}