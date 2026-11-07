package aquarion.units.type;

import aquarion.gen.BuggyUnit;
import aquarion.gen.Buggyc;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import mindustry.gen.Unit;

/** A wheeled chassis with bicycle steering and independently drawn front wheels. */
public class BuggyUnitType extends AquaUnitType{
    /** Effective steering wheelbase; larger values produce wider corners. */
    public float wheelBase = 10.5f;
    public float maxSteering = 26f, steeringSpeed = 3f, steeringResponse = 0.7f;
    public float driveAcceleration = 0.055f, brakeAcceleration = 0.16f;
    public float lateralGrip = 0.3f, coastDeceleration = 0.07f;
    public float reverseSpeed = 0.55f;
    public float wallStuckTime = 18f, wallRecoveryTime = 36f, wallRecoverySpeed = 0.4f;
    /** Crash damage = closing speed squared times this value, before armor. */
    public float collisionDamage = 3f, collisionSelfDamageMultiplier = 1f;
    public float collisionMinSpeed = 1.2f, collisionDamageCooldown = 20f;
    /** Hysteresis prevents direction switching while turning across the side of the chassis. */
    public float reverseEnterAngle = 120f, reverseExitAngle = 75f;
    public float wheelX = 3.35f, frontWheelY = 2.2f, rearWheelY = -3.1f;
    public float wheelFrameDistance = 0.65f;
    public int wheelFrames = 7;
    public TextureRegion[] wheelRegions;

    public BuggyUnitType(String name){
        super(name);
        constructor = BuggyUnit::create;
        flying = false;
        omniMovement = true;
        faceTarget = false;
        rotateMoveFirst = false;
        rotateSpeed = 0f;
        drag = 0f;
        accel = driveAcceleration;
    }

    @Override
    public void init(){
        if(wheelBase <= 0f || maxSteering <= 0f || maxSteering >= 85f || steeringSpeed <= 0f ||
            driveAcceleration <= 0f || brakeAcceleration <= 0f || lateralGrip < 0f ||
            coastDeceleration < 0f || reverseSpeed <= 0f || reverseSpeed > 1f ||
            wallStuckTime <= 0f || wallRecoveryTime <= 0f || wallRecoverySpeed <= 0f || wallRecoverySpeed > 1f ||
            collisionDamage < 0f || collisionSelfDamageMultiplier < 0f || collisionMinSpeed < 0f || collisionDamageCooldown < 0f ||
            reverseExitAngle < 0f || reverseEnterAngle <= reverseExitAngle || reverseEnterAngle > 180f ||
            wheelFrames < 1 || wheelFrameDistance <= 0f){
            throw new IllegalArgumentException("Invalid buggy movement settings for " + name);
        }
        super.init();
    }

    @Override
    public void load(){
        super.load();
        wheelRegions = new TextureRegion[wheelFrames];
        for(int i = 0; i < wheelFrames; i++) wheelRegions[i] = Core.atlas.find(name + "-wheel" + (i + 1));
        shadowRegion = region;
    }

    public int wheelFrame(float travel){
        return Math.floorMod(Mathf.floor(travel / wheelFrameDistance), wheelFrames);
    }

    @Override
    public void drawOutline(Unit unit){
        if(unit instanceof Buggyc buggy && wheelRegions != null){
            applyColor(unit);
            TextureRegion wheel = wheelRegions[wheelFrame(buggy.wheelTravel())];
            for(int side : Mathf.signs){
                drawWheel(unit, wheel, wheelX * side, rearWheelY, 0f);
                drawWheel(unit, wheel, wheelX * side, frontWheelY, buggy.steeringAngle());
            }
            Draw.reset();
        }
        super.drawOutline(unit);
    }

    private void drawWheel(Unit unit, TextureRegion wheel, float x, float y, float steer){
        float angle = unit.rotation - 90f;
        Draw.rect(wheel, unit.x + Angles.trnsx(angle, x, y), unit.y + Angles.trnsy(angle, x, y),
            unit.rotation - 90f + steer);
    }
}
