package aquarion.entities.comp;

import aquarion.annotations.Annotations;
import aquarion.annotations.Annotations.*;
import aquarion.world.AI.GerbRole;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Legsc;
import mindustry.gen.Unitc;

/**
 * Per-unit tactical state for Gerb units. Lives on the entity itself (generated
 * {@code GerbUnit}) instead of inside any single AI controller, so the state
 * survives when the brain swaps controllers.
 */
@EntityComponent
abstract class GerbComp implements Unitc, Legsc {
    /** Per-unit personality, 0 (timid) to 1 (bold); rolled once per spawn. */
    public transient float personality = 0f;
    /** Current tactical role; the brain picks the controller to match. */
    public transient GerbRole role = GerbRole.ASSAULT;
    /** Flank direction, re-rolled each time the unit enters a flank. */
    public transient float flankAngle = 0f;
    /** Anti-flap: minimum ticks between controller swaps. */
    public transient float swapCooldown = 0f;
    /** Damage tracking, updated every tick by {@link #update()}. */
    public transient float lastHealth = 0f;
    public transient float stimulusTimer = 0f;
    public transient float recentDamage = 0f;

    @Import float health, maxHealth;

    @Override
    public void update(){
        if(personality <= 0f) personality = Mathf.random(0.2f, 0.8f);

        //remember when the unit takes a hit so AIs can react to "under fire"
        if(health < lastHealth) stimulusTimer = 120f;
        lastHealth = health;
        if(stimulusTimer > 0f) stimulusTimer -= Time.delta;
        recentDamage = Mathf.lerpDelta(recentDamage, 0f, 0.05f);

        if(swapCooldown > 0f) swapCooldown -= Time.delta;
    }

    /** Whether the unit took a hit within the last ~2 seconds. */
    public boolean justHit(){
        return stimulusTimer > 0f;
    }

    /** Health fraction 0..1. */
    public float healthRatio(){
        return health / maxHealth;
    }

    /** Whether health is below the given fraction. */
    public boolean lowHealth(float threshold){
        return healthRatio() < threshold;
    }
}