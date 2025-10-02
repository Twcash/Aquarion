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
import static mindustry.Vars.player;

//I don't smoke weed.
@Annotations.EntityComponent
abstract class AquaLegsComp implements Posc, Rotc, Unitc {
    private static final Vec2 straightVec = new Vec2();

    @Annotations.Import
    float x, y, rotation, speedMultiplier;
    @Annotations.Import
    UnitType type;
    @Annotations.Import
    Team team;
    @Annotations.Import
    boolean disarmed;

    transient Leg[] legs = {};
    transient float totalLength;
    transient float moveSpace;
    transient float baseRotation;
    transient Floor lastDeepFloor;
    transient Vec2 curMoveOffset = new Vec2();

    @Annotations.Replace
    @Override
    public EntityCollisions.SolidPred solidity(){
        return ignoreSolids() ? null : type.allowLegStep ? EntityCollisions::legsSolid : EntityCollisions::solid;
    }

    @Override
    @Annotations.Replace
    public Floor drownFloor(){
        return lastDeepFloor;
    }

    @Override
    public void add(){
        resetLegs();
    }

    @Override
    public void unloaded(){
        resetLegs(1f);
    }

    @Annotations.MethodPriority(-1)
    @Override
    public void destroy(){
        if(!isAdded() || Vars.headless) return;

        float legExplodeRad = type.legRegion.height  / 4f / 1.45f;

        //create effects for legs being destroyed
        for(int i = 0; i < legs.length; i++){
            Leg l = legs[i];

            Vec2 base = legOffset(Tmp.v1, i).add(x, y);

            Tmp.v2.set(l.base).sub(l.joint).inv().setLength(type.legExtension);

            for(Vec2 vec : new Vec2[]{base, l.joint, l.base}){
                Damage.dynamicExplosion(vec.x, vec.y, 0f, 0f, 0f, legExplodeRad, Vars.state.rules.damageExplosions, false, team, type.deathExplosionEffect);
            }

            Fx.legDestroy.at(base.x, base.y, 0f, new LegDestroyData(base.cpy(), l.joint, type.legRegion));
            Fx.legDestroy.at(l.joint.x, l.joint.y, 0f, new LegDestroyData(l.joint.cpy().add(Tmp.v2), l.base, type.legBaseRegion));

        }
    }

    public void resetLegs(){
        resetLegs(type.legLength);
    }

    public void resetLegs(float legLength) {
        if (type instanceof AquaLegUnitType unit) {

            this.legs = new Leg[unit.legSequence.length];

            baseRotation = rotation;

            for (int i = 0; i < legs.length; i++) {
                Leg l = new Leg();

                float dstRot = legAngle(i);
                Vec2 baseOffset = legOffset(Tmp.v5, i).add(x, y);

                l.joint.trns(dstRot, unit.legSequence[i].baseLength).add(baseOffset);
                l.base.trns(dstRot, unit.legSequence[i].baseLength+unit.legSequence[i].legLength).add(baseOffset);

                legs[i] = l;
            }
            totalLength = Mathf.random(100f);
        }
    }

    @Override
    public void update() {
        if (type instanceof AquaLegUnitType unit) {

            baseRotation = rotation;

            //set up initial leg positions
            if (legs.length != unit.legSequence.length) {
                resetLegs();
            }

            float moveSpeed = type.legSpeed;
            int div = Math.max(legs.length / type.legGroupSize, 2);
            //TODO should move legs even when still, based on speed. also, to prevent "slipping", make sure legs move when they are too far from their destination
            totalLength += type.legContinuousMove ? type.speed * speedMultiplier * Time.delta : Mathf.dst(deltaX(), deltaY());

            float trns = moveSpace * 0.85f * type.legForwardScl;

            //rotation + offset vector
            boolean moving = moving();
            Vec2 moveOffset = !moving ? Tmp.v4.setZero() : Tmp.v4.trns(Angles.angle(deltaX(), deltaY()), trns);
            //make it smooth, not jumpy
            moveOffset = curMoveOffset.lerpDelta(moveOffset, 0.1f);

            lastDeepFloor = null;
            int deeps = 0;

            for (int i = 0; i < legs.length; i++) {
                float dstRot = legAngle(i);
                Vec2 baseOffset = legOffset(Tmp.v5, i).add(x, y);
                Leg l = legs[i];
                float totalLength = unit.legSequence[i].baseLength+unit.legSequence[i].legLength;
                AquaLegConfig conf = unit.legSequence[i];
                moveSpace = totalLength / 1.6f / (div / 2f) * type.legMoveSpace;

                //TODO is limiting twice necessary?
                l.joint.sub(baseOffset).clampLength(conf.legMinLen * conf.legLength, conf.legMaxLen * conf.legLength).add(baseOffset);
                l.base.sub(baseOffset).clampLength(conf.baseMinLen * conf.baseLength, conf.baseMaxLen * conf.baseLength).add(baseOffset);

                float stageF = (totalLength + i * type.legPairOffset) / moveSpace;
                int stage = (int) stageF;
                int group = stage % div;
                boolean move = i % div == group;
                boolean side = unit.legSequence[i].baseX > 0;
                //Shoulf "back legs" have reversed direction when legAngle handles direction now?
                boolean backLeg = unit.legSequence[i].baseY > 0;
                if (backLeg && type.flipBackLegs) side = !side;
                //if (type.flipLegSide) side = !side;

                l.moving = move;
                l.stage = moving ? stageF % 1f : Mathf.lerpDelta(l.stage, 0f, 0.1f);

                Floor floor = Vars.world.floorWorld(l.base.x, l.base.y);
                if (floor.isDeep()) {
                    deeps++;
                    lastDeepFloor = floor;
                }

                if (l.group != group) {

                    //create effect when transitioning to a group it can't move in
                    if (!move && (moving || !type.legContinuousMove) && i % div == l.group) {
                        if (!headless && !inFogTo(player.team())) {
                            if (floor.isLiquid) {
                                floor.walkEffect.at(l.base.x, l.base.y, type.rippleScale, floor.mapColor);
                                floor.walkSound.at(x, y, 1f, floor.walkSoundVolume);
                            } else {
                                Fx.unitLandSmall.at(l.base.x, l.base.y, type.rippleScale, floor.mapColor);
                            }

                            //shake when legs contact ground
                            if (type.stepShake > 0) {
                                Effect.shake(type.stepShake, type.stepShake, l.base);
                            }
                        }

                        if (type.legSplashDamage > 0 && !disarmed) {
                            Damage.damage(team, l.base.x, l.base.y, type.legSplashRange, type.legSplashDamage * Vars.state.rules.unitDamage(team), false, true);

                            var tile = Vars.world.tileWorld(l.base.x, l.base.y);
                            if (tile != null && tile.block().unitMoveBreakable) {
                                ConstructBlock.deconstructFinish(tile, tile.block(), self());
                            }
                        }
                    }

                    l.group = group;
                }

                //leg destination
                Vec2 legDest = Tmp.v1.trns(dstRot, conf.baseLength * type.legLengthScl).add(baseOffset).add(moveOffset);
                //join destination
                Vec2 jointDest = Tmp.v2;
                InverseKinematics.solve(conf.baseLength, conf.legLength, Tmp.v6.set(l.base).sub(baseOffset), false, jointDest);
                jointDest.add(baseOffset);
                Tmp.v6.set(baseOffset).lerp(l.base, 0.5f);

                if (move) {
                    float moveFract = stageF % 1f;

                    l.base.lerpDelta(legDest, moveFract);
                    l.joint.lerpDelta(jointDest, moveFract / 2f);
                }

                l.joint.lerpDelta(jointDest, moveSpeed / 4f);

                //limit again after updating
                l.joint.sub(baseOffset).clampLength(conf.legMinLen * conf.legLength, conf.legMaxLen * conf.legLength).add(baseOffset);
                l.base.sub(baseOffset).clampLength(conf.baseMinLen * conf.baseLength, conf.baseMaxLen * conf.baseLength).add(baseOffset);
            }

            //when at least 1 leg is touching land, it can't drown
            if (deeps != legs.length || !floorOn().isDeep()) {
                lastDeepFloor = null;
            }
        }
    }

    Vec2 legOffset(Vec2 out, int index){
        if(type instanceof AquaLegUnitType unit) {
            out.set(new Vec2(unit.legSequence[index].baseX,unit.legSequence[index].baseY));
            out.rotate(rotation -90);
        }
        return out;
    }


    float legAngle(int index) {
        if (type instanceof AquaLegUnitType unit) {
            return defaultLegAngle(index);
        }
        return 0;
    }
    float defaultLegAngle(int index){
        if(type instanceof AquaLegUnitType unit) {
            return rotation + unit.legSequence[index].baseRot;
        }
        return  0;
    }
}