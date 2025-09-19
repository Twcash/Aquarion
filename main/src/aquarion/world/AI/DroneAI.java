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
    public boolean initiatied = false;

    @Override
    public void updateMovement() {
        if (initiatied && shooter != null) {

            float time = unit instanceof TimedKillc t ? t.time() : 1000000f;
            if (time >= unit.type.homingDelay) {
                unit.lookAt(shooter.aimX(), shooter.aimY());
            }

            float maxRange = shooter.type.range;
            float allowedRange = maxRange * 0.9f;

            Teamc target = findDroneTarget(shooter.x, shooter.y, maxRange, true, true);

            Vec2 targetPos = Tmp.v1;

            if (target != null) {
                targetPos.set(target.x(), target.y());
                unit.lookAt(targetPos);
            } else {
                targetPos.set(shooter.x, shooter.y);
            }

            float dstFromShooter = Mathf.dst(shooter.aimX, shooter.aimY, unit.x, unit.y);
            if (target == null || dstFromShooter > shooter.range() * 0.9f) {
                moveTo(Tmp.v2.set(shooter.x, shooter.y), shooter.hitSize * 2f);
            } else {
                moveTo(targetPos, unit.range() * 0.9f);
            }

            float accelTime = unit.type.missileAccelTime;
            float speed = unit.speed();
            float actualSpeed = (accelTime <= 0f) ? speed : Mathf.pow(Math.min(time / accelTime, 1f), 2f) * speed;

            unit.moveAt(vec.trns(unit.rotation, actualSpeed));
        }
    }
    public Teamc findDroneTarget(float x, float y, float range, boolean air, boolean ground) {
        return Units.closestTarget(shooter.team, x, y, range, u ->
                !u.dead() && u.team != shooter.team && shooter.within(u, range) && (ground || air)
        );
    }
}