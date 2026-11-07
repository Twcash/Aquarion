package aquarion.entities.comp;

import aquarion.gen.AquaUnitc;

import aquarion.annotations.Annotations.*;
import aquarion.gen.BurrowUnit;
import aquarion.units.type.BurrowUnitType;
import aquarion.world.units.AmphibiousPathfinder;
import aquarion.world.units.BurrowTrajectory;
import aquarion.world.units.BurrowCombat;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.EntityCollisions;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.type.UnitType;

import static mindustry.Vars.*;

@EntityComponent
abstract class BurrowComp implements AquaUnitc, ElevationMovec{
    @Import UnitType type;
    @Import float x, y, rotation, health, maxHealth, hitTime, armor, armorOverride, healthMultiplier;
    @Import Vec2 vel;

    int burrowOwnerId = -1, burrowSegmentIndex;
    boolean burrowActive, burrowSlamDone;
    float burrowTime, burrowRest, burrowStartX, burrowStartY, burrowEndX, burrowEndY;
    float burrowAdvance;
    int burrowBuriedSegments;
    transient Seq<BurrowUnit> burrowBodies = new Seq<>();
    transient float burrowInputX, burrowInputY, burrowDustTime, burrowOrphanTime;
    transient boolean burrowBodiesReady, burrowWasExposed;
    transient BurrowTrajectory burrowTrajectory;
    transient Teamc burrowAttackTarget;

    public BurrowUnit burrowHead(){
        return burrowOwnerId < 0 ? self() : Groups.unit.getByID(burrowOwnerId) instanceof BurrowUnit owner ? owner : null;
    }

    public BurrowTrajectory burrowPath(){
        BurrowUnit head = burrowHead();
        if(head == null) return null;
        BurrowUnitType cfg = (BurrowUnitType)head.type;
        if(head.burrowTrajectory == null || !head.burrowTrajectory.matches(head.burrowStartX, head.burrowStartY,
            head.burrowEndX, head.burrowEndY, cfg.breachHeight)){
            head.burrowTrajectory = new BurrowTrajectory(head.burrowStartX, head.burrowStartY,
                head.burrowEndX, head.burrowEndY, cfg.breachHeight);
        }
        return head.burrowTrajectory;
    }

    public float burrowArcDistance(){
        BurrowUnit head = burrowHead();
        if(head == null) return 0f;
        BurrowUnitType cfg = (BurrowUnitType)head.type;
        return burrowPath().distanceAt(Math.max(0f, head.burrowTime - cfg.surfaceTime) / (cfg.riseTime + cfg.diveTime))
            + head.burrowAdvance - burrowSegmentIndex * cfg.segmentSpacing;
    }

    public void advanceBurialPush(){
        if(burrowOwnerId >= 0 || !burrowActive || !(type instanceof BurrowUnitType cfg)) return;
        float length = burrowPath().length;
        while(burrowBuriedSegments <= cfg.bodySegments && burrowArcDistance() - burrowBuriedSegments * cfg.segmentSpacing >= length){
            burrowBuriedSegments++;
            //Move every part along the same path, preserving the distance between parts.
            burrowAdvance += cfg.burialPushDistance;
        }
    }

    public float burrowExposure(){
        BurrowUnit head = burrowHead();
        if(head == null || !head.burrowActive) return 0f;
        BurrowUnitType cfg = (BurrowUnitType)head.type;
        if(burrowSegmentIndex == 0 && head.burrowTime <= cfg.surfaceTime) return Mathf.clamp(head.burrowTime / cfg.surfaceTime);
        float distance = burrowArcDistance(), length = burrowPath().length;
        if(distance <= 0f || distance >= length) return 0f;
        float edge = cfg.segmentSpacing / 2f;
        return Math.min(burrowSegmentIndex == 0 ? 1f : Mathf.clamp(distance / edge), Mathf.clamp((length - distance) / edge));
    }

    public float burrowHeight(){
        BurrowUnit head = burrowHead();
        return head == null || !head.burrowActive ? 0f : burrowPath().heightAt(burrowArcDistance());
    }

    public float burrowDrawRotation(){
        BurrowUnit head = burrowHead();
        return head == null || !head.burrowActive ? rotation :
            Angles.angle(head.burrowStartX, head.burrowStartY, head.burrowEndX, head.burrowEndY);
    }

    @Replace(1) @Override
    public EntityCollisions.SolidPred solidity(){ return null; }
    @Replace(1) @Override
    public boolean ignoreSolids(){ return true; }
    @Replace(1) @Override
    public int collisionLayer(){ return -1; }
    @Replace(1) @Override
    public boolean isFlying(){ return false; }

    @Replace(1) @Override
    public void moveAt(Vec2 vector, float acceleration){
        burrowInputX = vector.x;
        burrowInputY = vector.y;
        if(burrowOwnerId >= 0 || burrowActive){ vel.setZero(); return; }
        float dx = vector.x - vel.x, dy = vector.y - vel.y;
        float length = Mathf.len(dx, dy), step = acceleration * vector.len() * Time.delta;
        if(length > step && length > 0f){ dx *= step / length; dy *= step / length; }
        vel.add(dx, dy);
    }

    @Replace(1) @Override
    public void damage(float amount){
        if(burrowExposure() <= 0.1f || net.client()) return;
        if(burrowOwnerId >= 0){
            BurrowUnit head = burrowHead();
            if(head != null){ hitTime = 1f; head.damageBurrowBody(amount * ((BurrowUnitType)head.type).bodyDamageMultiplier, false, 1f, true); }
        }else rawDamage(Damage.applyArmor(amount, armorOverride >= 0f ? armorOverride : armor) / healthMultiplier / state.rules.unitHealth(team()));
    }

    @Replace(1) @Override
    public void damagePierce(float amount, boolean withEffect){
        if(burrowExposure() <= 0.1f || net.client()) return;
        if(burrowOwnerId >= 0){
            BurrowUnit head = burrowHead();
            if(head != null) head.damageBurrowBody(amount * ((BurrowUnitType)head.type).bodyDamageMultiplier, true, 1f, withEffect);
        }else damageBurrowBody(amount, true, 1f, withEffect);
    }

    @Replace(1) @Override
    public void damageArmorMult(float amount, float armorMult, boolean withEffect){
        if(burrowExposure() <= 0.1f || net.client()) return;
        BurrowUnit head = burrowHead();
        if(head != null) head.damageBurrowBody(amount * (burrowOwnerId < 0 ? 1f : ((BurrowUnitType)head.type).bodyDamageMultiplier), false, armorMult, withEffect);
    }

    /** Body hits remain valid while the head has already dived underground. */
    public void damageBurrowBody(float amount, boolean pierce, float armorMult, boolean withEffect){
        float previous = hitTime;
        float applied = pierce ? amount : Damage.applyArmor(amount, (armorOverride >= 0f ? armorOverride : armor) * armorMult);
        rawDamage(applied / healthMultiplier / state.rules.unitHealth(team()));
        if(!withEffect) hitTime = previous;
    }

    public void ensureBurrowBodies(){
        if(net.client() || burrowOwnerId >= 0 || !(type instanceof BurrowUnitType cfg) || cfg.bodyType == null || burrowBodiesReady) return;
        burrowBodies.clear();
        for(int i = 0; i < cfg.bodySegments; i++) burrowBodies.add((BurrowUnit)null);
        //Reattach saved bodies before creating any replacements. IDs are preserved by save IO.
        for(Unit other : Groups.unit){
            if(other instanceof BurrowUnit body && body.burrowOwnerId == id() && body.burrowSegmentIndex > 0 && body.burrowSegmentIndex <= cfg.bodySegments){
                burrowBodies.set(body.burrowSegmentIndex - 1, body);
            }
        }
        for(int i = 0; i < cfg.bodySegments; i++){
            if(burrowBodies.get(i) != null) continue;
            BurrowUnit body = (BurrowUnit)cfg.bodyType.create(team());
            body.burrowOwnerId = id();
            body.burrowSegmentIndex = i + 1;
            body.rotation = rotation;
            body.set(x - Mathf.cosDeg(rotation) * cfg.segmentSpacing * (i + 1), y - Mathf.sinDeg(rotation) * cfg.segmentSpacing * (i + 1));
            body.add();
            burrowBodies.set(i, body);
        }
        burrowBodiesReady = true;
    }

    public boolean startBurrowBreach(float targetX, float targetY){
        if(burrowOwnerId >= 0 || burrowActive || dead() || !(type instanceof BurrowUnitType cfg) ||
            !AmphibiousPathfinder.canLand(cfg, x, y)) return false;
        float angle = angleTo(targetX, targetY);
        float distance = Math.min(cfg.breachDistance, dst(targetX, targetY));
        float endX = x, endY = y;
        boolean found = false;
        for(float step = distance; step >= 8f; step -= 4f){
            float nx = x + Mathf.cosDeg(angle) * step, ny = y + Mathf.sinDeg(angle) * step;
            if(AmphibiousPathfinder.canLand(cfg, nx, ny)){ endX = nx; endY = ny; found = true; break; }
        }
        if(!found) return false;
        burrowActive = true;
        burrowSlamDone = false;
        burrowTime = burrowRest = 0f;
        burrowAdvance = 0f;
        burrowBuriedSegments = 0;
        burrowStartX = x;
        burrowStartY = y;
        burrowEndX = endX;
        burrowEndY = endY;
        rotation = angle;
        vel.setZero();
        return true;
    }

    @MethodPriority(1) @Override
    public void update(){
        if(!(type instanceof BurrowUnitType cfg)) return;
        BurrowUnit head = burrowHead();
        if(burrowOwnerId >= 0){
            vel.setZero();
            if(head == null || head.dead){
                if(!net.client() && (burrowOrphanTime += Time.delta) > 30f) remove();
                return;
            }
            burrowOrphanTime = 0f;
            team(head.team);
            health = head.health;
            maxHealth = head.maxHealth;
            cfg = (BurrowUnitType)head.type;
        }else{
            ensureBurrowBodies();
            if(net.client() && !isLocal()){
                if(burrowActive){ burrowTime += Time.delta; advanceBurialPush(); }
            }else if(!dead()){
                if(burrowActive){
                    vel.setZero();
                    burrowTime += Time.delta;
                    advanceBurialPush();
                    burrowPath().groundAt(burrowArcDistance(), Tmp.v4);
                    x = Mathf.clamp(Tmp.v4.x, 0f, (world.width() - 1) * tilesize);
                    y = Mathf.clamp(Tmp.v4.y, 0f, (world.height() - 1) * tilesize);
                    rotation = Angles.angle(burrowStartX, burrowStartY, burrowEndX, burrowEndY);
                    if(!burrowSlamDone && burrowTime >= cfg.headDuration()){
                        burrowSlamDone = true;
                        if(!net.client() && cfg.slamDamage > 0f) Damage.damage(team(), burrowEndX, burrowEndY, cfg.slamRadius,
                            cfg.slamDamage * damageMultiplier() * state.rules.unitDamage(team()), false, true);
                        if(!headless){
                            cfg.slamEffect.at(burrowEndX, burrowEndY, hitSize() / 8f, floorOn().mapColor);
                            if(cfg.slamShake > 0f) Effect.shake(cfg.slamShake, cfg.slamShake, burrowEndX, burrowEndY);
                            Sounds.mechStepHeavy.at(burrowEndX, burrowEndY);
                        }
                    }
                    if(burrowTime >= cfg.cycleDuration(burrowPath().length)) burrowActive = false;
                }else{
                    burrowRest += Time.delta;
                    if(burrowRest >= cfg.burrowInterval){
                        var target = burrowAttackTarget;
                        if(!BurrowCombat.valid(self(), target) || !within(target, cfg.breachRange + BurrowCombat.targetRadius(target))){
                            target = Units.closestTarget(team(), x, y, cfg.breachRange, u -> u.isGrounded(), b -> true);
                        }
                        if(target != null){
                            //Reposition instead of attempting a zero-distance pounce while sitting on the target.
                            if(dst(target) >= cfg.minPounceDistance) startBurrowBreach(target.x(), target.y());
                        }
                        else if(Mathf.len(burrowInputX, burrowInputY) > 0.001f){
                            float angle = Angles.angle(burrowInputX, burrowInputY);
                            startBurrowBreach(x + Mathf.cosDeg(angle) * cfg.breachDistance, y + Mathf.sinDeg(angle) * cfg.breachDistance);
                        }
                    }
                }
                Unit previous = self();
                for(BurrowUnit body : burrowBodies){
                    if(body == null || !body.isAdded()){ burrowBodiesReady = false; continue; }
                    if(burrowActive){
                        //Every part passes through the same entry, sine arc, and exit.
                        //Distance along that path keeps the visible chain connected.
                        burrowPath().groundAt(body.burrowArcDistance(), Tmp.v4);
                        body.set(Tmp.v4);
                        body.rotation = rotation;
                    }else{
                        float angle = Angles.angle(body.x, body.y, previous.x, previous.y);
                        body.rotation = angle;
                        body.set(previous.x - Mathf.cosDeg(angle) * cfg.segmentSpacing, previous.y - Mathf.sinDeg(angle) * cfg.segmentSpacing);
                    }
                    previous = body;
                }
            }
        }
        boolean exposed = burrowExposure() > 0f;
        if(!headless && exposed != burrowWasExposed){
            (exposed ? cfg.emergeEffect : cfg.diveEffect).at(x, y, hitSize() / 8f, floorOn().mapColor);
        }
        burrowWasExposed = exposed;
        if(!headless && !exposed && vel.len2() > 0.01f && (burrowDustTime += Time.delta) >= 10f){
            cfg.emergeEffect.at(x, y, hitSize() / 16f, floorOn().mapColor);
            burrowDustTime = 0f;
        }
        burrowInputX = burrowInputY = 0f;
        burrowAttackTarget = null;
    }

    @Override
    public void remove(){
        if(burrowOwnerId < 0){
            for(BurrowUnit body : burrowBodies) if(body != null && body.isAdded()) body.remove();
            burrowBodies.clear();
        }
    }
}
