package aquarion.world.units;

import aquarion.units.type.LiquidRailUnitType;
import aquarion.world.AI.RailCommandAI;
import aquarion.world.blocks.distribution.CargoStation.CargoStationBuild;
import aquarion.world.blocks.distribution.RailPoint.RailPointBuild;
import mindustry.Vars;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.Liquid;

public class RailLiquidCargo{
    public static float amount(Unit unit){
        float total = 0f;
        for(Unit car : RailConsist.members(unit)) if(car instanceof LiquidRailUnit tank) total += tank.liquidAmount;
        return total;
    }
    public static float amount(Unit unit, Liquid liquid){
        float total = 0f;
        for(Unit car : RailConsist.members(unit)) if(car instanceof LiquidRailUnit tank && tank.cargoLiquid == liquid) total += tank.liquidAmount;
        return total;
    }
    public static Liquid liquid(Unit unit){
        for(Unit car : RailConsist.members(unit)) if(car instanceof LiquidRailUnit tank && tank.liquidAmount > 0.001f) return tank.cargoLiquid;
        return null;
    }
    public static float cargoAmount(Unit unit){
        float total = 0f;
        for(Unit car : RailConsist.members(unit)) if(car instanceof LiquidRailUnit tank){
            total += tank.liquidAmount - RailPayloadSupport.reservedInCar(unit, tank, tank.cargoLiquid);
        }
        return total;
    }
    public static Liquid cargoLiquid(Unit unit){
        for(Unit car : RailConsist.members(unit)) if(car instanceof LiquidRailUnit tank
            && tank.liquidAmount > RailPayloadSupport.reservedInCar(unit, tank, tank.cargoLiquid) + 0.001f) return tank.cargoLiquid;
        return null;
    }
    public static boolean valid(Unit unit, CargoStationBuild station){
        boolean hasTank = false;
        for(Unit car : RailConsist.members(unit)) if(car instanceof LiquidRailUnit) hasTank = true;
        return hasTank && station.liquidCargo() && (cargoAmount(unit) <= 0.001f ? station.pickup() && pickup(unit, station) != null
            : station.acceptsLiquidCargo(cargoLiquid(unit)));
    }
    public static float demand(Unit unit, CargoStationBuild dock, Liquid liquid){
        float room = 0f;
        for(var building : Groups.build){
            if(!(building instanceof CargoStationBuild depot) || depot.team != unit.team || !depot.acceptsLiquidCargo(liquid)) continue;
            var route = RailPathfinder.find(dock, depot.x, depot.y);
            if(route != null && route.nodes.peek() == depot) room += depot.liquidRoom();
        }
        for(Unit other : Groups.unit){
            if(other.dead || other.team != unit.team || RailConsist.same(unit, other) || RailConsist.leader(other) != other
                || amount(other, liquid) <= 0f || !(other.controller() instanceof RailCommandAI ai)) continue;
            var point = ai.trafficNode();
            if(point == null) continue;
            var route = RailPathfinder.find(dock, point.x, point.y);
            if(route != null && route.nodes.peek() == point) room -= amount(other, liquid) - RailPayloadSupport.reserved(other, liquid);
        }
        return Math.max(0f, room);
    }
    public static Liquid pickup(Unit unit, CargoStationBuild dock){
        for(Liquid liquid : Vars.content.liquids()) if(dock.liquids.get(liquid) > 0.001f && demand(unit, dock, liquid) > 0.001f){
            for(Unit car : RailConsist.members(unit)) if(car instanceof LiquidRailUnit tank && !RailPayloadSupport.reserveCar(unit, car)
                && car.type instanceof LiquidRailUnitType type && tank.liquidAmount < type.liquidCapacity - 0.001f
                && (tank.liquidAmount <= 0.001f || tank.cargoLiquid == liquid)) return liquid;
        }
        return null;
    }
    public static CargoStationBuild choose(Unit unit, RailPointBuild origin){
        if(!RailPathfinder.active(origin)) return null;
        CargoStationBuild best = null;
        float cost = Float.POSITIVE_INFINITY, stock = Float.POSITIVE_INFINITY;
        boolean loading = cargoAmount(unit) <= 0.001f;
        for(var building : Groups.build){
            if(!(building instanceof CargoStationBuild station) || station.team != unit.team || !RailPathfinder.active(station) || !valid(unit, station)) continue;
            var route = RailPathfinder.find(origin, station.x, station.y);
            if(route == null || route.nodes.peek() != station) continue;
            float inventory = station.liquids.sum((liquid, amount) -> amount);
            if(best == null || (!loading && inventory < stock) || ((loading || inventory == stock) && (route.cost < cost || (route.cost == cost && station.pos() < best.pos())))){
                best = station; cost = route.cost; stock = inventory;
            }
        }
        return best;
    }
    public static float transfer(Unit unit, CargoStationBuild station){
        if(station.team != unit.team || !RailPathfinder.active(station) || !valid(unit, station)) return 0f;
        Liquid liquid = station.pickup() ? pickup(unit, station) : cargoLiquid(unit);
        float remaining = station.pickup() ? Math.min(station.liquids.get(liquid), demand(unit, station, liquid))
            : Math.min(station.liquidRoom(), Math.max(0f, amount(unit, liquid) - RailPayloadSupport.reserved(unit, liquid)));
        float moved = 0f;
        for(Unit car : RailConsist.members(unit)){
            if(!(car instanceof LiquidRailUnit tank) || !(car.type instanceof LiquidRailUnitType type)) continue;
            if(station.pickup() && RailPayloadSupport.reserveCar(unit, car)) continue;
            if(station.pickup() ? tank.liquidAmount > 0f && tank.cargoLiquid != liquid : tank.cargoLiquid != liquid) continue;
            float reserve = RailPayloadSupport.reservedInCar(unit, tank, liquid);
            float change = Math.min(remaining, station.pickup() ? type.liquidCapacity - tank.liquidAmount : tank.liquidAmount - reserve);
            if(change <= 0f) continue;
            tank.liquidAmount += station.pickup() ? change : -change;
            tank.cargoLiquid = tank.liquidAmount <= 0.001f ? null : liquid;
            if(station.pickup()) station.liquids.remove(liquid, change); else station.liquids.add(liquid, change);
            moved += change; remaining -= change;
        }
        return moved;
    }
}
