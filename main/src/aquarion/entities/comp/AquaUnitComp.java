package aquarion.entities.comp;

import aquarion.annotations.Annotations.EntityComponent;
import aquarion.annotations.Annotations.*;
import aquarion.world.units.CustUnitDeathFx;
import aquarion.world.units.UnitHooks;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import aquarion.world.units.AquaPlayerUnits;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;

/** Shared behavior for Aquarion core and buildable units, under player or AI control. */
@EntityComponent
public abstract class AquaUnitComp implements Unitc{
    // Keep the pre-death-effects save layout. These fields still synchronize in multiplayer.
    @NoSerialize public float deathTimer;
    @NoSerialize public boolean deathSinking;
    @NoSerialize public float deathDamageTaken, deathDamageLimit;
    private transient float healthBeforeDyingDamage;
    private transient boolean wasDyingBeforeDamage;

    @Override public void afterRead(){
        // Saves contain the unit's normal health/velocity, never a partially completed visual sequence.
        deathTimer = deathDamageTaken = deathDamageLimit = 0f;
        deathSinking = false;
        healthBeforeDyingDamage = 0f;
        wasDyingBeforeDamage = false;
    }

    @Insert(value = "rawDamage(float)", after = false)
    public void beforeDyingDamage(){
        wasDyingBeforeDamage = dyingPlayerUnit();
        healthBeforeDyingDamage = health();
    }

    @Insert("rawDamage(float)")
    public void afterDyingDamage(){
        if(!wasDyingBeforeDamage || !dyingPlayerUnit()) return;
        deathDamageTaken += Math.max(healthBeforeDyingDamage - health(), 0f);
        if(deathDamageExceeded() && !Vars.net.client()) mindustry.gen.Call.unitDestroy(id());
    }

    public boolean deathDamageExceeded(){
        return deathDamageLimit > 0f && deathDamageTaken > deathDamageLimit;
    }

    /** Target searches use dead()/isValid(); the internal flag still drives the death sequence. */
    public boolean dyingPlayerUnit(){
        return aquaPlayerUnit() && ((Unit)self()).dead && isAdded();
    }

    @Replace(1)
    @Override public boolean dead(){
        return ((Unit)self()).dead && !dyingPlayerUnit();
    }

    @Replace(1)
    @Override public boolean isValid(){
        return isAdded() && (!((Unit)self()).dead || dyingPlayerUnit());
    }

    @Wrap(value = "killed()", block = Unitc.class)
    public boolean beginPlayerDeath(){
        if(!aquaPlayerUnit()) return true;
        deathTimer = 0f;
        deathDamageTaken = 0f;
        deathDamageLimit = maxHealth() * 0.5f;
        deathSinking = !type().flying && self() instanceof mindustry.gen.WaterMovec && floorOn().isLiquid;
        if(type().flying || CustUnitDeathFx.duration(self()) == 0f) return true;
        ((Unit)self()).health = Math.min(((Unit)self()).health, 0f);
        ((Unit)self()).dead = true;
        deathTimer = 0f;
        return false;
    }

    @Wrap(value = "update()", block = Unitc.class)
    public boolean updateLivingPlayer(){
        // Keep all other components (including weapons) updating, but replace vanilla ground destruction.
        return !aquaPlayerUnit() || !((Unit)self()).dead || (type().flying && !deathSinking) || CustUnitDeathFx.duration(self()) == 0f;
    }

    @Wrap(value = "destroy()", block = Unitc.class)
    public boolean finishPlayerDeath(){
        if(!aquaPlayerUnit() || !((Unit)self()).dead || deathDamageExceeded()) return true;
        if(type().flying && !deathSinking) return true;
        return deathTimer >= CustUnitDeathFx.duration(self());
    }

    @Insert(value = "update()", after = false)
    public void beginAircraftSinking(){
        // Enter water before vanilla broadcasts final removal on touchdown.
        if(!dyingPlayerUnit() || !type().flying || deathSinking || deathDamageExceeded()
            || health() <= -maxHealth() * type().wreckHealthMultiplier
            || elevation() > type().fallSpeed * Time.delta + 0.001f || !floorOn().isLiquid) return;
        deathSinking = true;
        deathTimer = 0f;
        ((Unit)self()).elevation = 0f;
        ((Unit)self()).lastDrownFloor = floorOn();
        aquarion.world.graphics.AquaFx.waterSplash.at(x(), y(), Mathf.clamp(hitSize() / 16f, 0.5f, 3f), floorOn().mapColor);
    }

    public boolean aquaPlayerUnit(){
        return AquaPlayerUnits.contains(type());
    }

    @Override public void update(){
        if(aquaPlayerUnit()) UnitHooks.updatePlayerUnit(self());
        if(!aquaPlayerUnit() || !((Unit)self()).dead) return;
        deathTimer += Time.delta;
        CustUnitDeathFx.update(self(), deathTimer);
        if(type().flying && !deathSinking) return;
        float duration = CustUnitDeathFx.duration(self());
        if(duration <= 0f) return;
        if(!CustUnitDeathFx.sinking(self()) && deathTimer >= 25f && deathTimer - Time.delta < 25f){
            mindustry.content.Fx.unitLand.at(x(), y(), hitSize() / 8f, arc.graphics.Color.gray);
            mindustry.entities.Effect.shake(1.5f, 8f, self());
        }
        if(deathSinking){
            // Velocity movement stays with the native movement component; only water drag slows the wreck.
            ((Unit)self()).drag = 0.008f;
        }else{
            ((Unit)self()).vel.scl(Mathf.pow(0.85f, Time.delta));
        }
        ((Unit)self()).isShooting = true;
        for(var mount : ((Unit)self()).mounts){
            mount.shoot = true;
            mount.rotate = true;
        }
        if(CustUnitDeathFx.sinking(self())){
            ((Unit)self()).drownTime = Mathf.clamp(deathTimer / duration);
            ((Unit)self()).lastDrownFloor = floorOn();
        }
        if(deathTimer >= duration && !Vars.net.client()) mindustry.gen.Call.unitDestroy(id());
    }

}
