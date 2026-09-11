package aquarion.world.AI;

import mindustry.ai.types.FlyingAI;
import mindustry.entities.Units;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;

public class FlyingDefunctAI extends FlyingAI {
    private static final float mapRange = 1000000f;

    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground){
        Unit result = Units.closestEnemy(unit.team, x, y, mapRange, u -> u.checkTarget(air, ground));
        if(result != null) return result;

        return super.findMainTarget(x, y, range, air, ground);
    }
}
