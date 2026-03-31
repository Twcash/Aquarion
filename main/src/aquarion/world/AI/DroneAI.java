package aquarion.world.AI;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Teamc;
import mindustry.gen.TimedKillc;
import mindustry.gen.Unit;

public class DroneAI extends AIController {
    public @Nullable Unit shooter;

    @Override
    public void updateMovement() {
        unloadPayloads();
        if(shooter != null && !shooter.dead){
            Tmp.v1.set(shooter.aimX, shooter.aimY);
            if(Tmp.v1.dst(unit.x, unit.y) > shooter.type.range * 0.8f){
                moveTo(shooter, unit.type.range * 0.8f);
                unit.lookAt(shooter);
            } else {
                moveTo(Tmp.v1, unit.type.range * 0.8f);
                unit.lookAt(shooter.aimX, shooter.aimY);
            }
        }
    }

}