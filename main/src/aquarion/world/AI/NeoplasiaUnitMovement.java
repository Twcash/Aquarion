package aquarion.world.AI;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import static mindustry.Vars.world;

/** Keep search destinations inside the playable area instead of fighting an off-map wander vector. */
final class NeoplasiaUnitMovement{
    static Vec2 clampDestination(Vec2 destination, float margin){
        float mx = Math.min(margin, world.unitWidth() / 2f);
        float my = Math.min(margin, world.unitHeight() / 2f);
        return destination.set(Mathf.clamp(destination.x, mx, world.unitWidth() - mx),
            Mathf.clamp(destination.y, my, world.unitHeight() - my));
    }
}
