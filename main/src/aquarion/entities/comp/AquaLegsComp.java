package aquarion.entities.comp;
import aquarion.annotations.Annotations;
import aquarion.gen.*;
import aquarion.units.AquaLegUnitType;
import aquarion.world.entities.AquaLeg;
import aquarion.world.entities.AquaLegConfig;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Structs;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.InverseKinematics;
import mindustry.type.UnitType;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.headless;

//I don't smoke weed.
@Annotations.EntityComponent
abstract class AquaLegsComp implements Posc, Rotc, Hitboxc, Unitc {

    @Annotations.Import float x, y, rotation, speedMultiplier;
    @Annotations.Import UnitType type;
    @Annotations.Import Team team;

    transient AquaLeg[] legs = {};
    transient float[] totalLengths = {};
    transient float moveSpace;
    transient float baseRotation;
    transient Floor lastDeepFloor;
    transient Vec2 curMoveOffset = new Vec2();

    @Annotations.Replace
    @Override
    public EntityCollisions.SolidPred solidity() {
        return ignoreSolids() ? null : type.allowLegStep ? EntityCollisions::legsSolid : EntityCollisions::solid;
    }

    @Override
    @Annotations.Replace
    public Floor drownFloor() {
        return lastDeepFloor;
    }

    @Override
    public void add() {
        resetLegs();
    }

    @Override
    public void unloaded() {
        resetLegs(1f);
    }

    public void resetLegs() {
        resetLegs(type.legLength);
    }

    public void resetLegs(float legLength) {
        AquaLegUnitType aType = type instanceof AquaLegUnitType e ? e : null;
        if (aType == null) return;

        AquaLegConfig[] legSeq = aType.legSequence;
        legs = new AquaLeg[legSeq.length];
        totalLengths = new float[legSeq.length];

        for (int i = 0; i < legSeq.length; i++) {
            AquaLegConfig config = legSeq[i];
            AquaLeg leg = new AquaLeg(config.jointConfigs.length);

            for (int j = 0; j < config.jointConfigs.length; j++) {
                leg.joints[j].set(config.defaultPositions[j].x + x, config.defaultPositions[j].y + y);
                config.jointPositions[j].set(leg.joints[j]);
            }

            // foot/base initially at last joint
            leg.base.set(leg.joints[leg.joints.length - 1]);
            legs[i] = leg;
            totalLengths[i] = Mathf.random(100f);
        }

        if (type.lockLegBase) baseRotation = rotation;
    }

    @Override
    public void update() {
        AquaLegUnitType aType = type instanceof AquaLegUnitType e ? e : null;
        if (aType == null) return;
        AquaLegConfig[] legSeq = aType.legSequence;

        // Update base rotation
        if (Mathf.dst(deltaX(), deltaY()) > 0.001f) {
            baseRotation = Angles.moveToward(baseRotation, Mathf.angle(deltaX(), deltaY()), type.rotateSpeed);
        }
        if (type.lockLegBase) baseRotation = rotation;

        float legLength = type.legLength;

        // Ensure legs exist
        if (legs.length != legSeq.length) resetLegs();

        float moveSpeed = type.legSpeed;
        int div = Math.max(legs.length / type.legGroupSize, 2);
        moveSpace = legLength / 1.6f / (div / 2f) * type.legMoveSpace;

        // Advance leg cycles
        for (int i = 0; i < legs.length; i++) {
            totalLengths[i] += type.legContinuousMove ? type.speed * speedMultiplier * Time.delta
                    : Mathf.dst(deltaX(), deltaY());
        }

        float trns = moveSpace * 0.85f * type.legForwardScl;
        boolean moving = moving();
        Vec2 moveOffset = !moving ? Tmp.v4.setZero() : Tmp.v4.trns(Angles.angle(deltaX(), deltaY()), trns);
        moveOffset = curMoveOffset.lerpDelta(moveOffset, 0.1f);

        lastDeepFloor = null;
        int deeps = 0;

        for (int i = 0; i < legs.length; i++) {
            AquaLeg leg = legs[i];
            AquaLegConfig config = legSeq[i];

            Vec2 baseOffset = legOffset(Tmp.v5, i).add(x, y);
            Vec2 footTarget = Tmp.v1.trns(defaultLegAngle(i), legLength * type.legLengthScl).add(baseOffset).add(moveOffset);

            float stageF = (totalLengths[i] + i * type.legPairOffset) / moveSpace;
            int stage = (int) stageF;
            int group = stage % div;
            boolean move = i % div == group;
            leg.moving = move;
            leg.group = group;

            // Multi-joint IK
            Vec2 prev = new Vec2(baseOffset);
            for (int j = 0; j < leg.joints.length; j++) {
                AquaLegConfig.JointConfig jc = config.jointConfigs[j];

                // Single-joint compatibility
                Vec2 target = footTarget.cpy();
                if (leg.joints.length > 1) {
                    Vec2 dir = footTarget.cpy().sub(prev);
                    float len = Mathf.clamp(dir.len(), jc.minLen, jc.maxLen);
                    dir.setLength(len);

                    float angle = dir.angle();
                    float rotDiff = angleDiff(jc.baseRot, angle);
                    if (Math.abs(rotDiff) > jc.rotationLimit) {
                        angle = jc.baseRot + Mathf.clamp(rotDiff, -jc.rotationLimit, jc.rotationLimit);
                        dir.trns(angle, len);
                    }
                    target = prev.cpy().add(dir);
                }

                float lerpFactor = Math.max(0.1f, move ? moveSpeed * Time.delta : moveSpeed / 4f);
                leg.joints[j].lerpDelta(target, lerpFactor);
                config.jointPositions[j].set(leg.joints[j]);
                prev.set(leg.joints[j]);
            }

            leg.base.lerpDelta(footTarget, Math.max(0.1f, move ? moveSpeed * Time.delta : moveSpeed / 4f));

            // Deep floor detection
            Floor floor = Vars.world.floorWorld(leg.base.x, leg.base.y);
            if (floor.isDeep()) {
                deeps++;
                lastDeepFloor = floor;
            }
        }

        if (deeps != legs.length || !floorOn().isDeep()) lastDeepFloor = null;
    }

    Vec2 legOffset(Vec2 out, int index) {
        AquaLegUnitType aType = type instanceof AquaLegUnitType e ? e : null;
        if (aType == null) return out.trns(0f, 0f);
        out.trns(defaultLegAngle(index), aType.legBaseOffset);
        return out;
    }

    float legAngle(int index) {
        return defaultLegAngle(index);
    }

    float defaultLegAngle(int index) {
        return baseRotation + 360f / legs.length * index;
    }

    float angleDiff(float a, float b) {
        float diff = (b - a + 180f) % 360f - 180f;
        return diff < -180f ? diff + 360f : diff;
    }
}

