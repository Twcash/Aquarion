package aquarion.units.type;

import aquarion.world.entities.parts.AquaPart;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.meta.Env;

public class AquaUnitType extends UnitType {
    public AquaUnitType(String name) {
        super(name);
        this.envDisabled = Env.none;
    }
    @Override
    public void draw(Unit unit){
        if(parts.size > 0) AquaPart.aquaParams.set(unit);
        super.draw(unit);

    }
}
