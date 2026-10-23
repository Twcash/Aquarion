package aquarion.world.units;

import mindustry.gen.Unit;

/** Runtime hooks shared by generated player units, custom trains, and legacy saved units. */
public class AquaPlayerUnitHooks{
    public static void updatePlayerUnit(Unit unit){
        var damageState = AquaPlayerUnits.damageState(unit.type);
        if(damageState != null) damageState.update(unit);
    }

    public static void drawPlayerUnit(Unit unit){
    }

    public static void createdPlayerUnit(Unit unit){
    }

    public static void diedPlayerUnit(Unit unit){
    }
}
