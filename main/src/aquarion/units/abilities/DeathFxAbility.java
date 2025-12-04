package aquarion.units.abilities;

import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
//This is only really useful for legged units.
public class DeathFxAbility extends Ability {
    public Effect effect = Fx.none;
    public DeathFxAbility(Effect effect){{
        this.effect = effect;
    }}
    @Override
    public void death(Unit unit){
        if(!Vars.net.client()) {
        effect.at(unit.x, unit.y, unit.rotation);
        }
    }
}
