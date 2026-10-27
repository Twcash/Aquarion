package aquarion.world.units;

import aquarion.world.AI.RailCommandAI;
import aquarion.world.blocks.distribution.CargoStation;
import aquarion.world.blocks.distribution.CargoStation.CargoStationBuild;
import aquarion.world.blocks.distribution.RailPoint.RailPointBuild;
import mindustry.gen.Groups;
import mindustry.gen.Payloadc;
import mindustry.gen.Unit;
import mindustry.world.blocks.payloads.Payload;

/** Payloads use vanilla payload-unit storage, including building state and save/network support. */
public class RailPayloadCargo{
    public static Payload carried(Unit unit){
        for(Unit car : RailConsist.members(unit)) if(car instanceof Payloadc carrier && carrier.hasPayload()) return carrier.payloads().first();
        return null;
    }

    public static boolean hasRoom(Unit unit, Payload payload){
        if(!CargoStation.smallPayload(payload)) return false;


        for(Unit car : RailConsist.members(unit)) if(car instanceof Payloadc carrier && !carrier.hasPayload() && carrier.canPickupPayload(payload)) return true;
        return false;
    }

    public static int demand(Unit unit, CargoStationBuild dock, Payload payload){
        int room = 0;
        for(var building : Groups.build){
            if(!(building instanceof CargoStationBuild depot) || depot.team != unit.team || !depot.acceptsPayloadCargo(payload)) continue;
            var route = RailPathfinder.find(dock, depot.x, depot.y);
            if(route != null && route.nodes.peek() == depot) room++;
        }
        for(Unit other : Groups.unit){
            if(other.dead || other.team != unit.team || RailConsist.same(unit, other) || RailConsist.leader(other) != other
                || !(other.controller() instanceof RailCommandAI ai)) continue;
            Payload cargo = carried(other);
            if(cargo == null || !cargo.contentEquals(payload) || ai.trafficNode() == null) continue;
            var route = RailPathfinder.find(dock, ai.trafficNode().x, ai.trafficNode().y);
            if(route != null && route.nodes.peek() == ai.trafficNode()){
                for(Unit car : RailConsist.members(other)) if(car instanceof Payloadc carrier) room -= carrier.payloads().size;
            }
        }
        return Math.max(0, room);
    }

    public static boolean valid(Unit unit, CargoStationBuild station){

        Payload cargo = carried(unit);
        if(cargo != null) return station.acceptsPayloadCargo(cargo);
        return station.pickup() && !station.liquidCargo() && station.storedPayload != null
            && hasRoom(unit, station.storedPayload) && demand(unit, station, station.storedPayload) > 0;
    }

    public static CargoStationBuild choose(Unit unit, RailPointBuild origin){
        if(!RailPathfinder.active(origin)) return null;
        CargoStationBuild best = null;
        float cost = Float.POSITIVE_INFINITY;
        for(var building : Groups.build){
            if(!(building instanceof CargoStationBuild station) || station.team != unit.team || !RailPathfinder.active(station) || !valid(unit, station)) continue;
            var route = RailPathfinder.find(origin, station.x, station.y);
            if(route == null || route.nodes.peek() != station) continue;
            if(best == null || route.cost < cost || (route.cost == cost && station.pos() < best.pos())){ best = station; cost = route.cost; }
        }
        return best;
    }

    public static int transfer(Unit unit, CargoStationBuild station){
        return transfer(unit, station, false);
    }

    public static int transfer(Unit unit, CargoStationBuild station, boolean commanded){
        if(station.team != unit.team || !RailPathfinder.active(station)) return 0;
        boolean explicitPickup = commanded && station.pickup() && station.storedPayload != null && hasRoom(unit, station.storedPayload);
        boolean explicitUnload = commanded && !station.pickup() && !station.liquidCargo();
        if(!explicitPickup && !explicitUnload && !valid(unit, station)) return 0;
        if(station.pickup()){
            Payload payload = station.storedPayload;
            for(Unit car : RailConsist.members(unit)) if(car instanceof Payloadc carrier && !carrier.hasPayload() && carrier.canPickupPayload(payload)){
                carrier.addPayload(station.takePayload());
                return 1;
            }
        }else{
            for(Unit car : RailConsist.members(unit)) if(car instanceof Payloadc carrier && carrier.hasPayload()){
                Payload payload = carrier.payloads().first();
                if(explicitUnload){
                    if(!station.receiveCommandedPayload(payload)) continue;
                }else{
                    if(!station.acceptsPayloadCargo(payload)) continue;
                    station.handlePayload(null, payload);
                }
                if(station.storedPayload == payload){ carrier.payloads().remove(0); return 1; }
            }
        }
        return 0;
    }
}
