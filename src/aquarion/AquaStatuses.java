package aquarion;

import aquarion.world.graphics.AquaFx;
import arc.graphics.Color;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.type.StatusEffect;

import static mindustry.content.StatusEffects.*;

public class AquaStatuses {
    public static StatusEffect ionized, concussed, corroding;
    public static void load(){
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
    }
}
