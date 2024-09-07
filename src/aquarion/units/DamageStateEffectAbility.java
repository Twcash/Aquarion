package aquarion.units;

import arc.graphics.Color;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;

public class DamageStateEffectAbility extends Ability {
    public float minHealth = 0.30f;
    public float interval = 3f;
    public float x, y, rotation;
    public boolean rotateEffect = false;
    public float effectParam = 3f;
    public boolean teamColor = false;
    public boolean parentizeEffects = false;
    public Color color = Color.white;
    public Effect effect = Fx.missileTrail;

    protected float counter;

    public DamageStateEffectAbility(float x, float y, Color color, Effect effect, float interval){
        this.x = x;
        this.y = y;
        this.color = color;
        this.effect = effect;
        this.interval = interval;
        display = false;
    }

    public DamageStateEffectAbility(){
        display = false;
    }

    @Override
    public void update(Unit unit){
        if(Vars.headless) return;  // No effect in headless mode.

        counter += Time.delta;

        // Check if the unit's health is below the threshold and the interval has passed
        if(unit.health < (unit.maxHealth * minHealth) && counter >= interval && !unit.inFogTo(Vars.player.team())){
            Tmp.v1.trns(unit.rotation - 90f, x, y);
            counter %= interval;  // Reset the counter
            // Apply the effect
            effect.at(
                    Tmp.v1.x + unit.x, Tmp.v1.y + unit.y,
                    rotateEffect ? (unit.rotation + rotation) : (effectParam + rotation),
                    teamColor ? unit.team.color : color,
                    parentizeEffects ? unit : null
            );
        }
    }
    }