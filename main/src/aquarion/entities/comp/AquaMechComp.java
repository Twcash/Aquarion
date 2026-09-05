package aquarion.entities.comp;

import aquarion.annotations.Annotations.*;
import arc.math.Angles;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.gen.Mechc;
import mindustry.gen.Unitc;
import mindustry.type.UnitType;

/**
 * Custom mech behavior merged on top of the vanilla MechComp to make large mechs read as humanoid bipeds.
 * The hip pivots with the body when turning in place and steps into turns decisively while walking,
 * instead of planting the feet stiffly like the vanilla mech base does.
 */
@EntityComponent
abstract class AquaMechComp implements Unitc, Mechc {
    @Import
    float rotation, baseRotation;
    @Import
    UnitType type;

    /** Degrees per tick the hip pivots toward the body rotation while standing still and turning. */
    protected float idleTurnSpeed = 3f;
    /** Multiplier on the type's rotateSpeed for how decisively the hip steps into a turn while walking. */
    protected float turnSpeedMultiplier = 2f;

    @Override
    public void update() {
        if (!moving()) {
            baseRotation = Angles.moveToward(baseRotation, rotation, idleTurnSpeed * Time.delta);
        }
    }

    @Override
    @Replace(1)
    public void rotateMove(Vec2 vec) {
        moveAt(Tmp.v2.trns(baseRotation, vec.len()));

        if (!vec.isZero()) {
            baseRotation = Angles.moveToward(baseRotation, vec.angle(), type.rotateSpeed * turnSpeedMultiplier * Math.max(Time.delta, 1));
        }
    }
}
