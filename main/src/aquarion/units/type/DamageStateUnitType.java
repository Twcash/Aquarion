package aquarion.units.type;

import aquarion.world.units.UnitDamageState;
import mindustry.type.UnitType;

/** Shared optional damage effects without changing a unit's movement or drawing. */
public class DamageStateUnitType extends UnitType{
    /** Null uses the global player-unit default, which is disabled by default. */
    public UnitDamageState damageState;

    public DamageStateUnitType(String name){
        super(name);
    }
}
