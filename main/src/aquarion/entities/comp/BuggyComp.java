package aquarion.entities.comp;

import aquarion.annotations.Annotations.*;
import aquarion.units.type.BuggyUnitType;
import aquarion.world.units.BuggyCollision;
import aquarion.world.units.AmphibiousPathfinder;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.gen.*;
import mindustry.type.UnitType;

import static mindustry.Vars.*;

/** Steering, signed wheel travel, and momentum live on the unit through controller changes. */
@EntityComponent
abstract class BuggyComp implements Unitc, ElevationMovec{
    @Import UnitType type;
    @Import float x, y, rotation;
    @Import Vec2 vel;

    float steeringAngle, wheelTravel, driveSpeed;
    boolean reversing;
    transient float driveInputX, driveInputY;
    transient float wheelLastX, wheelLastY;
    transient boolean wheelPositionKnown;
    transient float collisionCooldownRemaining;
    transient float wallStallTime, wallRecoveryRemaining, wallRecoveryDirection, wallRecoverySteering;

    @Replace(1)
    @Override
    public void move(float cx, float cy){
        if(!net.client() && !dead()) BuggyCollision.check(self(), cx, cy);
        var solid = solidity();
        if(solid != null) collisions.move(self(), cx, cy, solid);
        else{
            x += cx;
            y += cy;
        }
    }

    @Replace(1)
    @Override
    public void moveAt(Vec2 vector, float acceleration){
        driveInputX = vector.x;
        driveInputY = vector.y;
    }

    @Replace(1)
    @Override
    public void rotateMove(Vec2 vector){
        moveAt(vector, type.accel);
    }

    @Replace(1)
    @Override
    public void approach(Vec2 vector){
        moveAt(vector, type.accel);
    }

    @Replace(1)
    @Override
    public void lookAt(float angle){
        //Body rotation comes exclusively from rolling wheels, including during combat.
    }

    @MethodPriority(1)
    @Override
    public void update(){
        if(!(type instanceof BuggyUnitType cfg)) return;
        float forwardX = Mathf.cosDeg(rotation), forwardY = Mathf.sinDeg(rotation);
        float traveled = wheelPositionKnown ? Mathf.dst(x, y, wheelLastX, wheelLastY) : 0f;
        if(wheelPositionKnown){
            float dx = x - wheelLastX, dy = y - wheelLastY;
            //Ignore teleports; animate from real, signed travel rather than requested speed.
            if(Mathf.dst2(wheelLastX, wheelLastY, x, y) < hitSize() * hitSize() * 16f){
                wheelTravel += dx * forwardX + dy * forwardY;
            }
        }
        wheelLastX = x;
        wheelLastY = y;
        wheelPositionKnown = true;
        if(net.client() && !isLocal()) return;
        collisionCooldownRemaining = Math.max(0f, collisionCooldownRemaining - Time.delta);
        if(dead()){
            driveInputX = driveInputY = 0f;
            return;
        }

        float inputLength = Mathf.len(driveInputX, driveInputY);
        boolean accelerating = inputLength > 0.001f && speed() > 0f;
        float desiredSteering = 0f, targetSpeed = 0f;
        float forwardSpeed = vel.x * forwardX + vel.y * forwardY;
        if(accelerating){
            float requestedAngle = Angles.angle(driveInputX, driveInputY);
            float angle = Angles.angleDist(rotation, requestedAngle);
            if(reversing ? angle < cfg.reverseExitAngle : angle > cfg.reverseEnterAngle){
                reversing = !reversing;
                wallRecoveryRemaining = 0f;
            }
            float heading = requestedAngle + (reversing ? 180f : 0f);
            float error = Mathf.mod(heading - rotation + 180f, 360f) - 180f;
            desiredSteering = Mathf.clamp(error * cfg.steeringResponse * (reversing ? -1f : 1f),
                -cfg.maxSteering, cfg.maxSteering);
            targetSpeed = Math.min(inputLength, speed()) * (reversing ? -cfg.reverseSpeed : 1f);
        }
        if(!accelerating){
            wallStallTime = wallRecoveryRemaining = 0f;
        }else{
            float direction = reversing ? -1f : 1f;
            float probe = Math.max(4f, Math.min(hitSize() * 0.66f, 7.8f));
            boolean blocked = !AmphibiousPathfinder.canLand(cfg,
                x + forwardX * probe * direction, y + forwardY * probe * direction);
            if(wallRecoveryRemaining <= 0f){
                wallStallTime = blocked && traveled < 0.02f * Time.delta ? wallStallTime + Time.delta : 0f;
                if(wallStallTime >= cfg.wallStuckTime){
                    wallStallTime = 0f;
                    wallRecoveryRemaining = cfg.wallRecoveryTime;
                    wallRecoveryDirection = -direction;
                    wallRecoverySteering = Math.abs(desiredSteering) > 1f ? -desiredSteering :
                        (id() % 2 == 0 ? cfg.maxSteering : -cfg.maxSteering);
                }
            }
            if(wallRecoveryRemaining > 0f){
                //Steering needs rolling speed. Back away and countersteer instead
                //of endlessly applying throttle into the same wall with zero yaw.
                targetSpeed = speed() * cfg.wallRecoverySpeed * wallRecoveryDirection;
                desiredSteering = wallRecoverySteering;
                wallRecoveryRemaining = Math.max(0f, wallRecoveryRemaining - Time.delta);
            }
        }
        steeringAngle = Mathf.approach(steeringAngle, desiredSteering, cfg.steeringSpeed * Time.delta);
        //Bicycle steering: no turning on the spot, and steering reverses naturally when backing up.
        float yaw = forwardSpeed / cfg.wheelBase * (float)Math.tan(steeringAngle * Mathf.degRad) * Mathf.radDeg;
        rotation = Mathf.mod(rotation + yaw * Time.delta, 360f);
        forwardX = Mathf.cosDeg(rotation);
        forwardY = Mathf.sinDeg(rotation);
        forwardSpeed = vel.x * forwardX + vel.y * forwardY;
        float lateralSpeed = -vel.x * forwardY + vel.y * forwardX;
        float acceleration = !accelerating ? cfg.coastDeceleration :
            forwardSpeed * targetSpeed < 0f ? cfg.brakeAcceleration : cfg.driveAcceleration;
        forwardSpeed = Mathf.approach(forwardSpeed, targetSpeed, acceleration * Time.delta);
        //Keep sideways momentum through the corner; tire grip gradually brings it back in line.
        lateralSpeed *= (float)Math.pow(Math.max(0f, 1f - cfg.lateralGrip), Time.delta);
        vel.set(forwardX * forwardSpeed - forwardY * lateralSpeed,
            forwardY * forwardSpeed + forwardX * lateralSpeed);
        driveSpeed = forwardSpeed;
        driveInputX = driveInputY = 0f;
    }
}
