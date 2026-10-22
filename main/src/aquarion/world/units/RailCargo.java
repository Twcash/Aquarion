package aquarion.world.units;

import aquarion.world.blocks.distribution.CargoStation.CargoStationBuild;
import aquarion.world.AI.RailCommandAI;
import aquarion.world.blocks.distribution.RailPoint.RailPointBuild;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.Vars;

/** Cargo uses the unit's standard saved item stack and the station's saved item module. */
public class RailCargo{
    public static CargoStationBuild choose(Unit unit, RailPointBuild origin){
        CargoStationBuild supplies = RailPayloadSupport.choose(unit, origin);
        if(supplies != null) return supplies;
        if(RailPayloadCargo.carried(unit) != null){
            CargoStationBuild payload = RailPayloadCargo.choose(unit, origin);
            if(payload != null) return payload;
        }
        if(RailConsist.cargoAmount(unit) > 0){
            CargoStationBuild items = choose(unit, origin, false, RailConsist.cargoItem(unit));
            if(items != null) return items;
        }
        if(RailLiquidCargo.cargoAmount(unit) > 0f){
            CargoStationBuild liquids = RailLiquidCargo.choose(unit, origin);
            if(liquids != null) return liquids;
        }
        if(RailConsist.cargoAmount(unit) > 0 || RailLiquidCargo.cargoAmount(unit) > 0f) return null;
        CargoStationBuild payload = RailPayloadCargo.choose(unit, origin);
        if(payload != null) return payload;
        CargoStationBuild liquid = RailLiquidCargo.choose(unit, origin);
        if(liquid != null) return liquid;
        return RailConsist.capacity(unit) > 0 ? choose(unit, origin, true, null) : null;
    }
    private static CargoStationBuild choose(Unit unit, RailPointBuild origin, boolean pickup, Item cargo){
        if(!RailPathfinder.active(origin)) return null;
        CargoStationBuild best = null;
        float bestCost = Float.POSITIVE_INFINITY;
        for(Building building : Groups.build){
            if(!(building instanceof CargoStationBuild station) || station.team != unit.team
                || station.liquidCargo() || station.pickup() != pickup || !RailPathfinder.active(station)
                || (pickup ? station.items.total() == 0 : !station.acceptsCargo(cargo)
                    || RailConsist.amount(unit, cargo) <= RailPayloadSupport.reserved(unit, cargo))) continue;
            RailPathfinder.Route route = RailPathfinder.find(origin, station.x, station.y);
            // find() also returns partial routes: cargo requires an actual reachable station.
            if(route == null || route.nodes.peek() != station) continue;
            if(pickup && pickupItem(unit, station) == null) continue;
            int stock = station.items.total(), bestStock = best == null ? Integer.MAX_VALUE : best.items.total();
            if(best == null || (!pickup && stock < bestStock)
                || ((pickup || stock == bestStock) && (route.cost < bestCost
                || (route.cost == bestCost && station.pos() < best.pos())))){
                best = station;
                bestCost = route.cost;
            }
        }
        return best;
    }

    /** Never fill a train with an item for which the network has no requesting depot. */
    public static Item pickupItem(Unit unit, CargoStationBuild dock){
        for(Item item : Vars.content.items()){
            if(dock.items.get(item) > 0 && availableDemand(unit, dock, item) > 0) return item;
        }
        return null;
    }

    /** Free receiving capacity, less the matching cargo already aboard other trains on this network. */
    public static int availableDemand(Unit unit, CargoStationBuild dock, Item item){
        long room = 0L;
        for(Building building : Groups.build){
            if(!(building instanceof CargoStationBuild depot) || depot.team != unit.team || !depot.acceptsCargo(item)) continue;
            RailPathfinder.Route path = RailPathfinder.find(dock, depot.x, depot.y);
            if(path != null && path.nodes.peek() == depot) room += depot.room();
        }
        if(room == 0L) return 0;
        for(Unit other : Groups.unit){
            if(other.dead || other.team != unit.team || !(other.controller() instanceof RailCommandAI ai)
                || RailConsist.same(unit, other) || RailConsist.leader(other) != other || RailConsist.amount(other, item) == 0) continue;
            RailPointBuild point = ai.trafficNode();
            if(point == null) continue;
            RailPathfinder.Route path = RailPathfinder.find(dock, point.x, point.y);
            if(path != null && path.nodes.peek() == point) room -= RailConsist.amount(other, item) - RailPayloadSupport.reserved(other, item);
        }
        return (int)Math.max(0L, Math.min(Integer.MAX_VALUE, room));
    }

    public static boolean valid(Unit unit, CargoStationBuild station){
        if(RailPayloadSupport.needsDock(unit, station)) return true;
        if(station.liquidCargo()) return RailLiquidCargo.valid(unit, station);
        if(station.payloadMode || (station.pickup() && station.storedPayload != null && RailPayloadCargo.valid(unit, station))) return RailPayloadCargo.valid(unit, station);
        if(RailConsist.capacity(unit) == 0 && RailConsist.cargoAmount(unit) == 0) return false;
        return !station.liquidCargo() && station.pickup() == (RailConsist.cargoAmount(unit) == 0)
            && (station.pickup() ? station.items.total() > 0 : station.acceptsCargo(RailConsist.cargoItem(unit))
                && RailConsist.amount(unit, RailConsist.cargoItem(unit)) > RailPayloadSupport.reserved(unit, RailConsist.cargoItem(unit)));
    }

    public static int transfer(Unit unit, CargoStationBuild station){
        if(RailPayloadSupport.needsDock(unit, station)) return RailPayloadSupport.transfer(unit, station);
        if(station.liquidCargo()) return (int)Math.ceil(RailLiquidCargo.transfer(unit, station));
        if(station.payloadMode || (station.pickup() && station.storedPayload != null && RailPayloadCargo.valid(unit, station))) return RailPayloadCargo.transfer(unit, station);
        if(station.liquidCargo()) return 0;
        if(station.team != unit.team || !RailPathfinder.active(station)) return 0;
        if(station.pickup()){
            if(RailConsist.cargoAmount(unit) != 0) return 0;
            Item item = pickupItem(unit, station);
            if(item == null) return 0;
            int amount = Math.min(Math.min(RailConsist.capacity(unit), station.items.get(item)), availableDemand(unit, station, item));
            if(amount <= 0) return 0;
            station.items.remove(item, amount);
            RailConsist.add(unit, item, amount);
            return amount;
        }
        if(!station.acceptsCargo(RailConsist.cargoItem(unit))) return 0;
        int amount = Math.min(station.room(), RailConsist.amount(unit, RailConsist.cargoItem(unit)) - RailPayloadSupport.reserved(unit, RailConsist.cargoItem(unit)));
        if(amount <= 0) return 0;
        station.items.add(RailConsist.cargoItem(unit), amount);
        RailConsist.remove(unit, amount);
        return amount;
    }

    public static int transfer(Unit unit, CargoStationBuild station, boolean commanded){
        if(commanded && station.pickup() && station.storedPayload != null && RailPayloadCargo.hasRoom(unit, station.storedPayload)){
            return RailPayloadCargo.transfer(unit, station, true);
        }
        return transfer(unit, station);
    }
}



