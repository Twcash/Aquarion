package aquarion.world.AI;

import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.ai.types.CommandAI;
import mindustry.entities.Units;
import mindustry.gen.Teamc;
import mindustry.world.Tile;

import static mindustry.Vars.world;

public class MenuCommandAI extends CommandAI {

    @Override
    public void updateUnit() {
        if (unit == null || !unit.isValid()) return;

        //commanded target died or left the map - clear it so the commander can assign a new one
        if (attackTarget != null && Units.invalidateTarget(attackTarget, unit.team, unit.x, unit.y)) {
            attackTarget = null;
            targetPos = null;
        }

        //acquire/aim weapons - this also fills in 'target' and drives the weapon mounts
        updateTargeting();

        //explicit command wins over the auto-acquired target
        Teamc dst = attackTarget != null ? attackTarget : target;
        if (dst == null || Units.invalidateTarget(dst, unit.team, unit.x, unit.y)) return;

        float engageRange = Math.max(1f, unit.range() - 10f);

        //in range - stop and let the weapons finish the job
        if (unit.within(dst, engageRange)) {
            unit.lookAt(dst);
            return;
        }

        //out of range - close the distance directly. The game's ControlPathfinder never runs
        //while on the menu (its thread is gated on state.isPlaying()), so CommandAI's ground
        //pathing stalls there; drive a simple attack-move instead.
        Vec2 move = Tmp.v1.set(dst).sub(unit).setLength(unit.speed());
        if (unit.isGrounded()) steerAround(move);

        unit.movePref(move);
        unit.lookAt(dst);
    }

    /** Slides the movement vector around walls/water instead of ramming into them. */
    private void steerAround(Vec2 move) {
        float spd = unit.speed();
        if (spd <= 0.001f) return;

        float look = Math.max(1f, unit.hitSize) * 0.7f;

        //head straight if the tile ahead is clear
        if (canStep(unit.x + move.x / spd * look, unit.y + move.y / spd * look)) return;

        //blocked ahead - slide along whichever flank is open
        Vec2 alt = Tmp.v2.set(move).rotate(90f);
        boolean left = canStep(unit.x + alt.x / spd * look, unit.y + alt.y / spd * look);
        boolean right = canStep(unit.x - alt.x / spd * look, unit.y - alt.y / spd * look);

        if (left || right) {
            //both flanks open - pick left arbitrarily
            move.set(alt).rotate(left ? 0f : 180f);
        } else {
            //fully boxed in - stand and let the commander re-aim once the path clears
            move.setZero();
        }
    }

    private boolean canStep(float x, float y) {
        Tile tile = world.tileWorld(x, y);
        return tile != null && canCross(tile);
    }

    private boolean canCross(Tile tile) {
        if (unit.isFlying()) return true;
        return unit.type.naval ? tile.floor().isLiquid : !tile.solid() && !tile.floor().isDeep();
    }
}