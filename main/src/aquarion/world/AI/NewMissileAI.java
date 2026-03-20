package aquarion.world.AI;

import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.units.AIController;
import mindustry.gen.Groups;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;

public class NewMissileAI extends AIController {
    @Override
    public void updateMovement(){
        unloadPayloads();
       target = findTarget(unit.x, unit.y, unit.type.range, unit.type.targetAir, unit.type.targetGround);
        if(target != null){
            moveTo(target, 0);
            unit.lookAt(target);
        }
    }
}