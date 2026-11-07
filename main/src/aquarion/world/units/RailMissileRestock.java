package aquarion.world.units;

import aquarion.world.blocks.defense.MissileBlock;
import aquarion.world.blocks.distribution.CargoStation.CargoStationBuild;
import mindustry.gen.Payloadc;
import mindustry.gen.Unit;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;
import java.util.WeakHashMap;

public class RailMissileRestock{
    //Had to look this up. A regular hashmap would just keep crashing.
    //Still don't *completely* understand this, but I hope I don't have t use this again.
    private static final WeakHashMap<Unit, Block> legacyLoads = new WeakHashMap<>();
    //Check previous cargo and see if what missile there was.
    public static void remember(Unit car, Block block){
        Block load = block instanceof MissileBlock ? block : null;
        if(car instanceof PayloadRailUnit burden) burden.missileLoad = load;
        else if(load == null) legacyLoads.remove(car);
        else legacyLoads.put(car, load);
    }

    private static Block missing(Unit car){
        if(!(car instanceof Payloadc carrier) || carrier.hasPayload()) return null;
        return car instanceof PayloadRailUnit burden ? burden.missileLoad : legacyLoads.get(car);
    }

    public static boolean needed(Unit train){
        for(Unit car : RailConsist.members(train)) if(missing(car) != null) return true;
        return false;
    }

    public static boolean needsDock(Unit train, CargoStationBuild dock){
        if(!dock.pickup() || dock.liquidCargo() || dock.team != train.team
            || !(dock.storedPayload instanceof BuildPayload payload) || payload.isDead()) return false;
        for(Unit car : RailConsist.members(train)){
            if(missing(car) == payload.block() && car instanceof Payloadc carrier && carrier.canPickupPayload(payload)) return true;
        }
        return false;
    }

    public static int transfer(Unit train, CargoStationBuild dock){
        if(!needsDock(train, dock)) return 0;
        for(Unit car : RailConsist.members(train)){
            if(missing(car) == ((BuildPayload)dock.storedPayload).block() && car instanceof Payloadc carrier
                && carrier.canPickupPayload(dock.storedPayload)){
                carrier.addPayload(dock.takePayload());
                return 1;
            }
        }
        return 0;
    }
}
