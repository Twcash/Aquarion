package aquarion.entities.comp;


import mindustry.game.Team;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Teamc;
import mindustry.gen.Unitc;
import ent.anno.Annotations.*;


@SuppressWarnings({"unused", "UnnecessaryReturnStatement"})
@EntityComponent
abstract class DerelictComp implements Unitc, Healthc, Hitboxc, Teamc {
    @Import
    Team team;
    @Import boolean dead;
    @Import float health = 100f; // Default health
    private boolean isDead = false; // Track whether the unit is dead

    @Replace
      @Override
    public void update() {
        // Your regular update logic here
        if (health <= 0 && !isDead) {
            // You can decide when and how to kill the unit
            // For now, we just prevent the unit from dying
        }
    }

    // Override the kill() method to prevent death
    @Replace
    @Override
    public void kill() {
        // Suppress the kill behavior
        if (shouldDie()) {
            isDead = true; // Only set to true if you want the unit to die
            // Add custom logic here if you want to manage death differently
        }
    }

    private boolean shouldDie() {
        // Example condition: allow death only if a custom condition is met
        return health <= 0; // Unit dies if health is 0 or lower
    }

    public float health() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }
}