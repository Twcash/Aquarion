package aquarion.world.blocks.units;

import mindustry.type.UnitType;
import mindustry.world.meta.Env;

public class AquaUnitType extends UnitType {
    public AquaUnitType(String name) {
        super(name);
        envDisabled = Env.space;
    }
}
