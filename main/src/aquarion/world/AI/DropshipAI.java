package aquarion.world.AI;

import mindustry.Vars;
import mindustry.ai.types.FlyingAI;
import mindustry.gen.Teamc;
import mindustry.world.meta.BlockFlag;

public class DropshipAI extends FlyingAI {
    //No straying off attacking random bs
    @Override
    public Teamc findTarget(float x, float y, float range, boolean air, boolean ground){
        var core = targetFlag(x, y, BlockFlag.core, true);
        if(core == null){
            super.findTarget(x,y,range,air,ground);
        } else return core;
        return null;
    }
    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground){
        var core = targetFlag(x, y, BlockFlag.core, true);
        if(core == null){
            super.findTarget(x,y,range,air,ground);
        } else return core;
        return null;
    }
    @Override
    public void updateMovement(){

        if(target != null && unit.hasWeapons()){
            if(unit.type.circleTarget){
                circleAttack(unit.type.circleTargetRadius);
            }else{
                moveTo(target, unit.type.range * 0.8f);
                unit.lookAt(target);
            }
        }
    }
}
