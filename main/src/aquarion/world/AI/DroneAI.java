package aquarion.world.AI;

import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.entities.units.AIController;
import mindustry.gen.Unit;

public class DroneAI extends AIController {
    public @Nullable Unit shooter;

    @Override
    public void updateMovement() {
        faceMovement();
        unloadPayloads();
        if(shooter !=null){
            Tmp.v1.set(shooter.aimX, shooter.aimY);
            moveTo(Tmp.v1, unit.range() *0.8f);
        }
        updateTargeting();
        if(target!=null){
            faceTarget();
        }
    }
}
