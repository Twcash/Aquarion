package aquarion.units;

import aquarion.world.AI.FishAIDangerMap;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class FishUnitType extends UnitType {
    public FishUnitType(String name) {
        super(name);
    }
    @Override
    public void killed(Unit unit) {
        super.killed(unit);
        FishAIDangerMap.addDeath(unit.x, unit.y);
    }
}
