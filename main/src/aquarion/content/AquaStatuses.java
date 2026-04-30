package aquarion.content;

import aquarion.world.graphics.AquaFx;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.units.StatusEntry;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;

import static mindustry.content.StatusEffects.*;

public class AquaStatuses {
    public static StatusEffect ionized, flung, concussed, corroding, cold, weighted, rearmed, retrofit, armored;
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
        flung = new AquaStatusEffect("flung"){{
            dragMultiplier = 0.1f;
            color = Pal.health;
            effect = AquaFx.trailSmoke1;
            speedMultiplier = 0;
            fling = true;
            reloadMultiplier = 0.25f;
        }};
        weighted = new StatusEffect("weighted"){{
            dragMultiplier = 1.3f;
            show = true;
            color = Pal.accent;
            outline = true;
            speedMultiplier = 0.8f;
            permanent = true;
            healthMultiplier = 1.1f;
            effect = Fx.none;
        }};
        rearmed = new StatusEffect("rearmed"){{
            reloadMultiplier = 1.2f;
            damageMultiplier = 1.1f;
            show = true;
            color = Pal.accent;
            permanent = true;
            outline = true;
            effect = Fx.none;
            healthMultiplier = 0.9f;
        }};
        retrofit = new StatusEffect("retrofit"){{
            healthMultiplier = 1.5f;
            color = Pal.accent;
            speedMultiplier = 1.3f;
            show = true;
            outline = true;
            permanent = true;
            dragMultiplier = 0.9f;
            effect = Fx.none;
        }};
        armored = new StatusEffect("armored"){{
            speedMultiplier = 0.7f;
            color = Pal.accent;
            healthMultiplier = 1.8f;
            permanent = true;
            outline = true;
            dragMultiplier = 1.1f;
            show = true;
            reloadMultiplier = 0.8f;
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
    public static class AquaStatusEffect extends StatusEffect{
        public boolean fling = false;
        public AquaStatusEffect(String name) {
            super(name);
        }
        @Override
        public boolean showUnlock(){
            return false;
        }

        /** Runs every tick on the affected unit while time is greater than 0. */
        public void update(Unit unit, StatusEntry entry){
            super.update(unit, entry);
            if(fling){
                float prog = entry.time;
                unit.rotation += 0.01f;
                unit.elevation = Interp.pow2In.apply(Mathf.clamp(4*(prog+(-Mathf.pow(prog, 2)))));
            };
        }
    }
}
