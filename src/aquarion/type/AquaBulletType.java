package aquarion.type;

import mindustry.entities.bullet.BulletType;

public class AquaBulletType extends BulletType {
    public boolean kinetic = false;
    public boolean heat = false;
    public boolean energy = false;
    public boolean concussion = false;

    public AquaBulletType(float speed, float damage) {
        this.damage = damage;
        this.speed = speed;
    }

    // Method to calculate effective damage based on active damage types
    public float calculateEffectiveDamage(GerbUnitType unitType) {
        float damageMultiplier = 1.0f;

        // Adjust multiplier based on each resistance type
        if (kinetic) {
            damageMultiplier *= (1 - unitType.kineticResistance);
        }
        if (heat) {
            damageMultiplier *= (1 - unitType.heatResistance);
        }
        if (energy) {
            damageMultiplier *= (1 - unitType.energyResistance);
        }
        if (concussion) {
            damageMultiplier *= (1 - unitType.concussionResistance);
        }

        // Apply the calculated multiplier to the base damage
        return damage * damageMultiplier;
    }
}