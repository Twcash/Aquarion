package aquarion.world.AI;

import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.ai.types.CommandAI;
import mindustry.entities.Units;
import mindustry.gen.Teamc;

//WHYYYYYYYYYYYYYYYYYYYY
public class MenuCommandAI extends CommandAI {

    @Override
    public void updateUnit() {
        if (unit == null || !unit.isValid()) return;
        if (attackTarget != null && Units.invalidateTarget(attackTarget, unit.team, unit.x, unit.y)) {
            attackTarget = null;
            targetPos = null;
        }

        updateTargeting();

        Teamc dst = attackTarget != null ? attackTarget : target;
        if (dst == null || Units.invalidateTarget(dst, unit.team, unit.x, unit.y)) return;

        float engageRange = Math.max(1f, unit.range() - 10f);

        if (unit.within(dst, engageRange)) {
            unit.lookAt(dst);
            return;
        }

        Vec2 move = Tmp.v1.set(dst).sub(unit).setLength(unit.speed());

        unit.movePref(move);
        unit.lookAt(dst);
    }
}