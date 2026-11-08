package aquarion.world.AI;

import mindustry.entities.units.AIController;
import mindustry.gen.TimedKillc;

public class NewMissileAI extends AIController {
    @Override
    public boolean shouldShoot(){
        return !(unit instanceof TimedKillc missile) || missile.time() >= unit.type.homingDelay;
    }

    @Override
    public void updateMovement(){
        unloadPayloads();
        target = findTarget(unit.x, unit.y, unit.type.range, unit.type.targetAir, unit.type.targetGround);
        if(target != null){
            moveTo(target, 0);
            unit.lookAt(target);
        }else{
            unit.moveAt(vec.trns(unit.rotation, unit.speed()));
        }
    }
}
