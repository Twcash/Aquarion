package aquarion.entities.comp;

import aquarion.annotations.Annotations;
import aquarion.annotations.Annotations.*;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.async.PhysicsProcess;
import mindustry.content.UnitTypes;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.ElevationMovec;
import mindustry.gen.Unitc;
import mindustry.graphics.Layer;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.world.Tile;

@Annotations.EntityComponent
abstract class JetUnitComp implements Unitc {
    @Import
    float x, y, hitSize, rotation;
    @Import
    Vec2 vel;
    @Import
    UnitType type;

    protected float thrust = 1.5f;//Additive to speed.
    protected float turnStrength = 0.3f;
    protected float maxSpeedMul = 3f;
    protected float minSpeedMul = 0.35f;
    protected float bankAmount = 25f;//For visuals.

    protected float bank = 0f;
    //OmniMovement = false; with extra steps.
    @Replace
    public void movePref(Vec2 movement) {
        if (!movement.isZero()) {
            float targetAngle = movement.angle();

            float speedNorm = Mathf.clamp(vel.len() / (type.speed * maxSpeedMul));

            rotation = Angles.moveToward(
                    rotation,
                    targetAngle,
                    type.rotateSpeed * turnStrength * speedNorm * Time.delta
            );

            float angDiff = Angles.angleDist(rotation, targetAngle);
            bank = Mathf.approach(
                    bank,
                    Mathf.clamp(angDiff, 0f, bankAmount),
                    Time.delta * 2f
            );
        } else {
            bank = Mathf.approach(bank, 0f, Time.delta);
        }

        Tmp.v1.trns(rotation, thrust * type.speed);
        vel.add(Tmp.v1.scl(Time.delta));

        if (vel.len() > 0.01f) {
            float velAngle = vel.angle();
            float newAngle = Angles.moveToward(
                    velAngle,
                    rotation,
                    type.rotateSpeed * 0.45f * Time.delta
            );
            vel.setAngle(newAngle);
        }

        float max = type.speed * maxSpeedMul;
        float min = type.speed * minSpeedMul;

        float len = vel.len();
        if (len > max) vel.setLength(max);
        if (len < min) vel.setLength(min);
    }
}