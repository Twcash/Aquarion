package aquarion.world.units;

import aquarion.world.blocks.distribution.CargoStation.CargoStationBuild;
import aquarion.world.blocks.distribution.RailPoint.RailPointBuild;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Payloadc;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;

/** Transfers real supplies through building acceptance rules; only power is free. */
public class RailPayloadSupport{
    public static boolean outOfAmmo(Unit unit){
        for(Unit car : RailConsist.members(unit)) if(car instanceof Payloadc carrier){
            for(Payload payload : carrier.payloads().copy()) if(payload instanceof BuildPayload target){
                feed(unit, target.build);
                if(target.build instanceof Turret.TurretBuild turret
                    && !(target.build.block instanceof mindustry.world.blocks.defense.turrets.PowerTurret)
                    && !turret.hasAmmo()) return true;
            }
        }
        return false;
    }
    private static boolean accepts(Building build, Item item){
        return item != null && (build.block instanceof Turret || build.block.consumesItem(item)) && build.acceptItem(build, item);
    }
    private static boolean accepts(Building build, Liquid liquid){
        return liquid != null && (build.block instanceof Turret || build.block.consumesLiquid(liquid)) && build.acceptLiquid(build, liquid);
    }
    private static float room(Building build, Liquid liquid){
        return build.liquids == null ? 0f : Math.max(0f, build.block.liquidCapacity - build.liquids.get(liquid));
    }
    public static void feed(Unit holder, Building build){
        if(Vars.net.client()) return;
        for(Unit car : RailConsist.members(holder)){
            if(car.stack.amount > 0 && accepts(build, car.stack.item)){
                while(car.stack.amount > 0 && accepts(build, car.stack.item)){
                    build.handleItem(build, car.stack.item);
                    car.stack.amount--;
                }
                if(car.stack.amount == 0) car.clearItem();
            }
            if(car instanceof LiquidRailUnit tank && tank.liquidAmount > 0f && accepts(build, tank.cargoLiquid)){
                float moved = Math.min(tank.liquidAmount, room(build, tank.cargoLiquid));
                if(moved > 0f){ build.handleLiquid(build, tank.cargoLiquid, moved); tank.liquidAmount -= moved; }
                if(tank.liquidAmount <= 0f) tank.cargoLiquid = null;
            }
            if(car instanceof Payloadc carrier){
                for(int i = 0; i < carrier.payloads().size; i++){
                    Payload payload = carrier.payloads().get(i);
                    if(payload instanceof BuildPayload target && target.build == build) continue;
                    if(build.acceptPayload(build, payload)){
                        build.handlePayload(build, payload);
                        carrier.payloads().remove(i--);
                    }
                }
            }
        }
    }
    public static boolean needsDock(Unit unit, CargoStationBuild dock){
        if(!dock.pickup() || dock.team != unit.team || !RailPathfinder.active(dock)) return false;
        for(Unit car : RailConsist.members(unit)) if(car instanceof Payloadc carrier){
            for(Payload payload : carrier.payloads()) if(payload instanceof BuildPayload target && target.build.block instanceof Turret){
                Building build = target.build;
                if(dock.liquidCargo()){
                    for(Liquid liquid : Vars.content.liquids()) if(dock.liquids.get(liquid) > 0.001f && accepts(build, liquid) && room(build, liquid) > 0.001f) return true;
                }else{
                    for(Item item : Vars.content.items()) if(dock.items.get(item) > 0 && accepts(build, item)) return true;
                    if(dock.storedPayload != null && build.acceptPayload(build, dock.storedPayload)) return true;
                }
            }
        }
        return false;
    }
    public static CargoStationBuild choose(Unit unit, RailPointBuild origin){
        if(!RailPathfinder.active(origin)) return null;
        for(Unit car : RailConsist.members(unit)) if(car instanceof Payloadc carrier){
            // Snapshot: feeding payload consumers may remove another car's payload ammunition.
            for(Payload payload : carrier.payloads().copy()) if(payload instanceof BuildPayload target) feed(unit, target.build);
        }
        CargoStationBuild best = null;
        float cost = Float.POSITIVE_INFINITY;
        for(var building : Groups.build) if(building instanceof CargoStationBuild dock && needsDock(unit, dock)){
            var route = RailPathfinder.find(origin, dock.x, dock.y);
            if(route != null && route.nodes.peek() == dock && (best == null || route.cost < cost || (route.cost == cost && dock.pos() < best.pos()))){ best = dock; cost = route.cost; }
        }
        return best;
    }
    public static int transfer(Unit unit, CargoStationBuild dock){
        if(Vars.net.client() || !needsDock(unit, dock)) return 0;
        int transferred = 0;
        for(Unit car : RailConsist.members(unit)) if(car instanceof Payloadc carrier){
            for(Payload payload : carrier.payloads()) if(payload instanceof BuildPayload target && target.build.block instanceof Turret){
                Building build = target.build;
                if(dock.liquidCargo()){
                    for(Liquid liquid : Vars.content.liquids()) if(accepts(build, liquid)){
                        float moved = Math.min(dock.liquids.get(liquid), room(build, liquid));
                        if(moved > 0f){ build.handleLiquid(build, liquid, moved); dock.liquids.remove(liquid, moved); transferred++; }
                    }
                }else{
                    for(Item item : Vars.content.items()) while(dock.items.get(item) > 0 && accepts(build, item)){
                        build.handleItem(build, item); dock.items.remove(item, 1); transferred++;
                    }
                    if(dock.storedPayload != null && build.acceptPayload(build, dock.storedPayload)){
                        build.handlePayload(build, dock.storedPayload); dock.takePayload(); transferred++;
                    }
                }
            }
        }
        return transferred;
    }
}
