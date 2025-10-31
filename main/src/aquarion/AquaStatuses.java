package aquarion;

import aquarion.world.graphics.AquaFx;
import arc.graphics.Color;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.type.StatusEffect;

import static mindustry.content.StatusEffects.*;

public class AquaStatuses {
    public static StatusEffect ionized, concussed, corroding, cold;
    public static void load(){
        cold = new StatusEffect("cold"){{
            speedMultiplier = 0.8f;
            damageMultiplier = 0.8f;
            reloadMultiplier = 0.9f;
            applyColor = Color.valueOf("a8b1ff");
            outline = false;
            buildSpeedMultiplier = 0.8f;
            effect = AquaFx.cold;
            allDatabaseTabs = true;
        }};
        ionized = new StatusEffect("ionized"){{
            color = Color.valueOf("ffab84");
            damage = 0.167f;
            effect = AquaFx.ionizing;
            transitionDamage = 8f;

            init(() -> {
                opposite(wet, freezing);
                affinity(melting, (unit, result, time) -> {
                    unit.damagePierce(transitionDamage);
                    AquaFx.ionizing.at(unit.x + Mathf.range(unit.bounds() / 2f), unit.y + Mathf.range(unit.bounds() / 2f));
                    result.set(burning, Math.min(time + result.time, 300f));
                });
            });
        }};
        concussed = new StatusEffect("concussed"){{
            color = Color.valueOf("5e533f");
            effect = Fx.none;
            speedMultiplier = 0.6f;
            healthMultiplier = 0.8f;
            reloadMultiplier = 0.6f;
        }};
        corroding = new StatusEffect("corroding"){{
            color = Color.valueOf("ffab84");
            damage = 0.05f;
            effect = Fx.none;
            transitionDamage = 8f;
            speedMultiplier = 0.9f;
            healthMultiplier = 0.8f;
            damageMultiplier = 1.1f;
            reloadMultiplier = 0.7f;
        }};
    }
}
