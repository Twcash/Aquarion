package aquarion.entities.comp;

import aquarion.gen.AquaUnitc;

import aquarion.annotations.Annotations.*;
import aquarion.units.type.AquaUnitType;
import aquarion.world.units.AmphibiousPathfinder;
import aquarion.world.units.HopLanding;
import aquarion.world.AI.AmphibiousAI;
import aquarion.world.AI.AmphibiousCommandAI;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.EntityCollisions;
import mindustry.async.PhysicsProcess;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.gen.*;
import mindustry.type.UnitType;

import static mindustry.Vars.*;

/** Naval movement in liquid, with serialized/synchronized discrete hops on dry ground. */
@EntityComponent
abstract class AmphibiousComp implements AquaUnitc, WaterMovec {
    @Import UnitType type;
    @Import float x, y, rotation, speedMultiplier, elevation;
    @Import boolean wasFlying;
    @Import Vec2 vel;

    boolean hopping;
    float hopTime, hopDuration, hopStartX, hopStartY, hopEndX, hopEndY;
    float hopSafeX, hopSafeY, hopCooldownRemaining;
    transient float hopAimX, hopAimY;
    transient float harpoonHold;
    transient boolean hopHasSafePosition;
    transient float hopRecoveryDelay;

    /** Repair overlap from crowd pushes, construction, or an invalidated emergency landing. */
    public boolean recoverHopFooting(){
        if(!(type instanceof AquaUnitType cfg) || hopping || dead()) return false;
        if(AmphibiousPathfinder.canLand(cfg, x, y)){
            hopSafeX = x;
            hopSafeY = y;
            hopHasSafePosition = true;
            hopRecoveryDelay = 0f;
            return true;
        }
        if(hopRecoveryDelay > 0f) return false;
        hopRecoveryDelay = cfg.hopRepathInterval;
        float safeX = hopSafeX, safeY = hopSafeY;
        boolean found = hopHasSafePosition && within(safeX, safeY, cfg.hopDistance) &&
            AmphibiousPathfinder.canLand(cfg, safeX, safeY);
        if(!found){
            float nearest = Float.POSITIVE_INFINITY;
            int tx = tileX(), ty = tileY();
            int radius = Math.max(2, Mathf.ceil(cfg.hopDistance / tilesize));
            for(int ring = 1; ring <= radius; ring++){
                for(int dx = -ring; dx <= ring; dx++){
                    for(int dy = -ring; dy <= ring; dy++){
                        if(Math.abs(dx) != ring && Math.abs(dy) != ring) continue;
                        float candidateX = (tx + dx) * tilesize, candidateY = (ty + dy) * tilesize;
                        float distance = Mathf.dst2(x, y, candidateX, candidateY);
                        if(distance < nearest && AmphibiousPathfinder.canLand(cfg, candidateX, candidateY)){
                            nearest = distance;
                            safeX = candidateX;
                            safeY = candidateY;
                            found = true;
                        }
                    }
                }
                if(found) break;
            }
        }
        if(!found) return false;
        //This is depenetration, not a normal hop: never apply landing damage here.
        x = hopSafeX = safeX;
        y = hopSafeY = safeY;
        hopHasSafePosition = true;
        hopRecoveryDelay = hopCooldownRemaining = harpoonHold = 0f;
        elevation = 0f;
        wasFlying = false;
        vel.setZero();
        return true;
    }

    /** Tethers slide grounded units safely; an in-progress hop finishes before pulling. */
    public boolean pullHarpoon(float targetX, float targetY, float speed, float stopDistance){
        if(dead() || !(type instanceof AquaUnitType cfg)) return false;
        if(hopping){
            harpoonHold = 2f;
            return true;
        }
        float distance = Mathf.dst(x, y, targetX, targetY);
        float step = Math.min(speed * Time.delta, Math.max(0f, distance - stopDistance));
        if(step <= 0f){
            harpoonHold = 2f;
            return true;
        }
        Vec2 delta = Tmp.v4.set(targetX - x, targetY - y).setLength(step);
        if(AmphibiousPathfinder.canLand(cfg, x + delta.x, y + delta.y) &&
            AmphibiousPathfinder.canTraverse(cfg, x, y, x + delta.x, y + delta.y)){
            float previousX = x, previousY = y;
            move(delta.x, delta.y);
            if(Mathf.dst(previousX, previousY, x, y) > step * 0.1f){
                harpoonHold = 2f;
                vel.setZero();
                return true;
            }
        }
        //A blocked tether must not continually disable the AI's escape hops.
        harpoonHold = 0f;
        vel.setZero();
        return false;
    }

    @Replace(1)
    @Override
    public EntityCollisions.SolidPred solidity(){
        return hopping || ignoreSolids() ? null : EntityCollisions::solid;
    }

    @Replace(1)
    @Override
    public boolean onSolid(){
        return EntityCollisions.solid(tileX(), tileY());
    }

    @Replace(1)
    @Override
    public float floorSpeedMultiplier(){
        if(!(type instanceof AquaUnitType cfg)) return speedMultiplier;
        float moveSpeed = floorOn().isLiquid ? cfg.swimSpeed : cfg.hopSpeed;
        return moveSpeed / Math.max(type.speed, 0.001f) * speedMultiplier;
    }

    @Replace(1)
    @Override
    public void moveAt(Vec2 vector, float acceleration){
        hopAimX = vector.x;
        hopAimY = vector.y;
        if(harpoonHold > 0f || hopping || !floorOn().isLiquid){
            vel.setZero();
            return;
        }
        Vec2 change = Tmp.v4.set(vector).sub(vel).limit(acceleration * vector.len() * Time.delta);
        vel.add(change);
    }

    public float hopArc(){
        return hopping ? Math.max(0f, Mathf.sin(Mathf.clamp(hopTime / Math.max(hopDuration, 0.001f)) * Mathf.PI)) : 0f;
    }

    public float hopVisualHeight(){
        return type instanceof AquaUnitType cfg ? hopArc() * cfg.hopHeight : 0f;
    }

    @Replace(1)
    @Override
    public boolean isFlying(){
        return hopping && type instanceof AquaUnitType cfg && hopArc() >= cfg.hopAirTargetThreshold;
    }

    @Replace(1)
    @Override
    public boolean isGrounded(){
        return !isFlying();
    }

    @Replace(1)
    @Override
    public int collisionLayer(){
        //Ground crowds must not shove a hopping unit off its validated trajectory.
        //Weapon targeting still uses isFlying(), so only the apex is air-targetable.
        return hopping ? PhysicsProcess.layerFlying :
            type.allowLegStep && type.legPhysicsLayer ? PhysicsProcess.layerLegs : PhysicsProcess.layerGround;
    }

    /** AI requests an exact landing; player and logic movement use the same validation. */
    public boolean requestHop(float targetX, float targetY){
        if(harpoonHold > 0f || hopping || hopCooldownRemaining > 0f || dead() || !(type instanceof AquaUnitType cfg)) return false;
        if(!cfg.hopFromWater && floorOn().isLiquid) return false;
        if(!AmphibiousPathfinder.canHop(cfg, x, y, targetX, targetY)) return false;
        if(!HopLanding.find(self(), x, y, targetX, targetY, Tmp.v3)){
            hopCooldownRemaining = 6f;
            return false;
        }
        targetX = Tmp.v3.x;
        targetY = Tmp.v3.y;
        hopping = true;
        hopTime = 0f;
        hopStartX = hopSafeX = x;
        hopStartY = hopSafeY = y;
        hopHasSafePosition = AmphibiousPathfinder.canLand(cfg, x, y);
        hopEndX = targetX;
        hopEndY = targetY;
        HopLanding.reserve(self());
        hopDuration = Math.max(1f, Mathf.dst(x, y, targetX, targetY) / (cfg.hopSpeed * Math.max(speedMultiplier, 0.01f)));
        rotation = angleTo(targetX, targetY);
        vel.setZero();
        if(!headless && !inFogTo(player.team())){
            cfg.hopTakeoffEffect.at(x, y, hitSize() / 8f, floorOn().mapColor);
        }
        return true;
    }

    @MethodPriority(1)
    @Override
    public void update(){
        if(!(type instanceof AquaUnitType cfg)) return;
        if(net.client() && !isLocal()){
            //Remote positions come from interpolation; only advance the visual arc locally.
            if(hopping) hopTime = Math.min(hopDuration, hopTime + Time.delta);
            elevation = hopArc();
            wasFlying = isFlying();
            return;
        }
        hopCooldownRemaining = Math.max(0f, hopCooldownRemaining - Time.delta);
        harpoonHold = Math.max(0f, harpoonHold - Time.delta);
        hopRecoveryDelay = Math.max(0f, hopRecoveryDelay - Time.delta);
        if(dead()){
            HopLanding.release(self());
            hopping = false;
            elevation = 0f;
            wasFlying = false;
            return;
        }
        if(hopping){
            HopLanding.reserve(self());
            vel.setZero();
            hopTime = Math.min(hopDuration, hopTime + Time.delta);
            float progress = Mathf.clamp(hopTime / Math.max(hopDuration, 0.001f));
            float nextX = Mathf.lerp(hopStartX, hopEndX, progress);
            float nextY = Mathf.lerp(hopStartY, hopEndY, progress);
            if(progress >= 1f && !HopLanding.clear(self(), hopEndX, hopEndY)){
                //An ally entered the reserved spot after takeoff. Do not land inside it.
                HopLanding.release(self());
                x = hopSafeX;
                y = hopSafeY;
                hopping = false;
                hopCooldownRemaining = 6f;
                elevation = 0f;
                wasFlying = false;
                hopAimX = hopAimY = 0f;
                recoverHopFooting();
                return;
            }
            //If construction blocks the remaining trajectory, return to the last clear footing.
            if(!AmphibiousPathfinder.canLand(cfg, hopEndX, hopEndY) ||
                !AmphibiousPathfinder.canTraverse(cfg, x, y, nextX, nextY)){
                x = hopSafeX;
                y = hopSafeY;
                hopping = false;
                HopLanding.release(self());
                hopCooldownRemaining = cfg.hopCooldown;
            }else{
                x = nextX;
                y = nextY;
                if(AmphibiousPathfinder.canLand(cfg, x, y) && HopLanding.clear(self(), x, y)){
                    hopSafeX = x;
                    hopSafeY = y;
                }
                if(progress >= 1f){
                    if(!AmphibiousPathfinder.canLand(cfg, x, y)){
                        x = hopSafeX;
                        y = hopSafeY;
                    }
                    hopping = false;
                    HopLanding.release(self());
                    hopCooldownRemaining = cfg.hopCooldown;
                    //Touchdown damage is ground-only and server-authoritative.
                    if(!net.client() && cfg.hopCrushDamage > 0f && cfg.hopCrushRadius > 0f){
                        Damage.damage(team(), x, y, cfg.hopCrushRadius,
                            cfg.hopCrushDamage * damageMultiplier() * state.rules.unitDamage(team()), false, true);
                    }
                    if(!headless && !inFogTo(player.team())){
                        if(floorOn().isLiquid) floorOn().walkEffect.at(x, y, hitSize() / 8f, floorOn().mapColor);
                        else cfg.hopLandEffect.at(x, y, hitSize() / 8f, floorOn().mapColor);
                        type.stepSound.at(x, y, type.stepSoundPitch + Mathf.range(type.stepSoundPitchRange), type.stepSoundVolume);
                        if(cfg.hopLandShake > 0f) Effect.shake(cfg.hopLandShake, cfg.hopLandShake, x, y);
                    }
                }
            }
        }else if(!(controller() instanceof AmphibiousAI) && !(controller() instanceof AmphibiousCommandAI) &&
            hopCooldownRemaining <= 0f && (Math.abs(hopAimX) + Math.abs(hopAimY)) > 0.001f){
            //Routed AI starts exact hops itself; don't override a swimming waypoint
            //with an unsolicited full-distance hop when it approaches a shoreline.
            Vec2 direction = Tmp.v4.set(hopAimX, hopAimY).nor();
            boolean water = floorOn().isLiquid;
            float probe = Math.max(tilesize, hitSize() / 2f);
            float aheadX = x + direction.x * probe, aheadY = y + direction.y * probe;
            boolean shore = !world.floorWorld(aheadX, aheadY).isLiquid || !AmphibiousPathfinder.canLand(cfg, aheadX, aheadY);
            if(!water || (cfg.hopFromWater && shore)){
                float dx = direction.x, dy = direction.y;
                for(float distance = cfg.hopDistance; distance >= cfg.hopMinDistance; distance -= tilesize / 2f){
                    if(requestHop(x + dx * distance, y + dy * distance)) break;
                }
            }
        }
        //A solid starting footprint prevents every future hop, so repair it even
        //when the unit has no command or is still waiting for its next hop.
        if(!hopping) recoverHopFooting();
        if(!hopping && !floorOn().isLiquid) vel.setZero();
        elevation = hopArc();
        //The landing effect belongs to the actual touchdown, not the end of the apex targeting window.
        wasFlying = isFlying();
        hopAimX = hopAimY = 0f;
    }

    @MethodPriority(-1)
    @Override
    public void remove(){
        HopLanding.release(self());
    }
}
