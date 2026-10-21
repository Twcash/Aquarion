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
    public static final float reserveFraction = 0.25f;

    /** Stable across direction changes: reserve one in four cars of each storage kind, rounded up. */
    public static boolean reserveCar(Unit unit, Unit candidate){
        boolean liquid = candidate instanceof LiquidRailUnit;
        if(!liquid && candidate.type.itemCapacity <= 0) return false;
        boolean armed = false;
        int count = 0, before = 0;
        for(Unit car : RailConsist.members(unit)){
            if(car instanceof Payloadc carrier) for(Payload payload : carrier.payloads()){
                if(payload instanceof BuildPayload target && target.build.block instanceof Turret) armed = true;
            }
            if(liquid ? car instanceof LiquidRailUnit : !(car instanceof LiquidRailUnit) && car.type.itemCapacity > 0){
                count++;
                if(car.id < candidate.id) before++;
            }
        }
        return armed && before < (int)Math.ceil(count * reserveFraction);
    }

    public static int reservedInCar(Unit unit, Unit car, Item item){
        return item != null && car.stack.item == item && uses(unit, item) && reserveCar(unit, car) ? car.stack.amount : 0;
    }
    public static float reservedInCar(Unit unit, LiquidRailUnit car, Liquid liquid){
        return liquid != null && car.cargoLiquid == liquid && uses(unit, liquid) && reserveCar(unit, car) ? car.liquidAmount : 0f;
    }

    private static boolean uses(Unit unit, Item item){
        for(Unit car : RailConsist.members(unit)) if(car instanceof Payloadc carrier){
            for(Payload payload : carrier.payloads()) if(payload instanceof BuildPayload target && target.build.block instanceof Turret){
                if(target.build.block.consumesItem(item)
                    || (target.build.block instanceof mindustry.world.blocks.defense.turrets.ItemTurret turret && turret.ammoTypes.containsKey(item))) return true;
            }
        }
        return false;
    }
    private static boolean uses(Unit unit, Liquid liquid){
        for(Unit car : RailConsist.members(unit)) if(car instanceof Payloadc carrier){
            for(Payload payload : carrier.payloads()) if(payload instanceof BuildPayload target && target.build.block instanceof Turret){
                if(target.build.block.consumesLiquid(liquid)
                    || (target.build.block instanceof mindustry.world.blocks.defense.turrets.LiquidTurret turret && turret.ammoTypes.containsKey(liquid))
                    || (target.build.block instanceof mindustry.world.blocks.defense.turrets.ContinuousLiquidTurret turret && turret.ammoTypes.containsKey(liquid))) return true;
            }
        }
        return false;
    }
    private static boolean reserveSpace(Unit unit, CargoStationBuild dock){
        for(Unit car : RailConsist.members(unit)){
            if(!reserveCar(unit, car)) continue;
            if(dock.liquidCargo() && car instanceof LiquidRailUnit tank && car.type instanceof aquarion.units.type.LiquidRailUnitType type
                && tank.liquidAmount < type.liquidCapacity - 0.001f){
                for(Liquid liquid : Vars.content.liquids()) if(dock.liquids.get(liquid) > 0.001f && uses(unit, liquid)
                    && (tank.liquidAmount <= 0.001f || tank.cargoLiquid == liquid)) return true;
            }else if(!dock.liquidCargo() && car.stack.amount < car.type.itemCapacity){
                for(Item item : Vars.content.items()) if(dock.items.get(item) > 0 && uses(unit, item)
                    && (car.stack.amount == 0 || car.stack.item == item)) return true;
            }
        }
        return false;
    }
    private static int reserve(Unit unit, CargoStationBuild dock){
        int changed = 0;
        for(Unit car : RailConsist.members(unit)){
            if(!reserveCar(unit, car)) continue;
            if(dock.liquidCargo() && car instanceof LiquidRailUnit tank && car.type instanceof aquarion.units.type.LiquidRailUnitType type){
                for(Liquid liquid : Vars.content.liquids()){
                    if(!uses(unit, liquid) || (tank.liquidAmount > 0.001f && tank.cargoLiquid != liquid)) continue;
                    float amount = Math.min(dock.liquids.get(liquid), Math.max(0f, type.liquidCapacity - tank.liquidAmount));
                    if(amount > 0.001f){ tank.cargoLiquid = liquid; tank.liquidAmount += amount; dock.liquids.remove(liquid, amount); changed++; }
                }
            }else if(!dock.liquidCargo() && car.type.itemCapacity > 0){
                for(Item item : Vars.content.items()){
                    if(!uses(unit, item) || (car.stack.amount > 0 && car.stack.item != item)) continue;
                    int amount = Math.min(dock.items.get(item), Math.max(0, car.type.itemCapacity - car.stack.amount));
                    if(amount > 0){ car.addItem(item, amount); dock.items.remove(item, amount); changed += amount; }
                }
            }
        }
        return changed;
    }

    public static int reserved(Unit unit, Item item){
        if(item == null || !uses(unit, item)) return 0;
        int total = 0;
        for(Unit car : RailConsist.members(unit)) if(car.stack.item == item){
            total += reservedInCar(unit, car, item);
        }
        return total;
    }
    public static float reserved(Unit unit, Liquid liquid){
        if(liquid == null || !uses(unit, liquid)) return 0f;
        float total = 0f;
        for(Unit car : RailConsist.members(unit)) if(car instanceof LiquidRailUnit tank && tank.cargoLiquid == liquid
            && car.type instanceof aquarion.units.type.LiquidRailUnitType type){
            total += reservedInCar(unit, tank, liquid);
        }
        return total;
    }

    public static boolean engaged(Unit unit){
        for(Unit car : RailConsist.members(unit)) if(car instanceof Payloadc carrier){
            for(Payload payload : carrier.payloads()) if(payload instanceof BuildPayload target && target.build instanceof Turret.TurretBuild turret){
                if(turret.hasAmmo() && turret.target instanceof mindustry.gen.Teamc enemy && enemy.team() != unit.team
                    && (!(turret.target instanceof mindustry.gen.Healthc health) || !health.dead())
                    && target.build.within(turret.target, turret.range()) && !target.build.within(turret.target, turret.minRange())) return true;
            }
        }
        return false;
    }

    public static boolean validEnemy(Unit unit, mindustry.gen.Teamc enemy){
        return enemy != null && enemy.team() != unit.team
            && (!(enemy instanceof mindustry.gen.Healthc health) || !health.dead());
    }

    private static boolean canTarget(Turret block, mindustry.gen.Teamc enemy){
        return enemy instanceof Unit other ? other.checkTarget(block.targetAir, block.targetGround) : block.targetGround;
    }

    public static boolean canEngage(Unit unit, mindustry.gen.Teamc enemy){
        if(!validEnemy(unit, enemy)) return false;
        for(Unit car : RailConsist.members(unit)) if(car instanceof Payloadc carrier){
            for(Payload payload : carrier.payloads()) if(payload instanceof BuildPayload target && target.build instanceof Turret.TurretBuild turret
                && target.build.block instanceof Turret block && turret.hasAmmo() && canTarget(block, enemy)
                && car.within(enemy, turret.range()) && !car.within(enemy, turret.minRange())){
                turret.target = enemy;
                return true;
            }
        }
        return false;
    }

    public static float pursuitRange(Unit unit){
        float range = 0f;
        for(Unit car : RailConsist.members(unit)) if(car instanceof Payloadc carrier){
            for(Payload payload : carrier.payloads()) if(payload instanceof BuildPayload target && target.build instanceof Turret.TurretBuild turret
                && turret.hasAmmo()) range = Math.max(range, Math.max(160f, turret.trackingRange() * 2f));
        }
        return range;
    }

    public static mindustry.gen.Teamc pursuitTarget(Unit unit){
        float range = pursuitRange(unit);
        if(range <= 0f) return null;
        mindustry.gen.Teamc best = null;
        float closest = Float.POSITIVE_INFINITY;
        for(Unit car : RailConsist.members(unit)) if(car instanceof Payloadc carrier){
            for(Payload payload : carrier.payloads()) if(payload instanceof BuildPayload target && target.build instanceof Turret.TurretBuild turret
                && target.build.block instanceof Turret block && turret.hasAmmo()){
                if(turret.target instanceof mindustry.gen.Teamc enemy && validEnemy(unit, enemy) && canTarget(block, enemy) && car.within(enemy, range)) return enemy;
                for(Unit enemy : Groups.unit){
                    if(!validEnemy(unit, enemy) || !canTarget(block, enemy) || !car.within(enemy, range)) continue;
                    float distance = car.dst2(enemy);
                    if(distance < closest){ best = enemy; closest = distance; }
                }
                for(Building enemy : Groups.build){
                    if(!validEnemy(unit, enemy) || !canTarget(block, enemy) || !car.within(enemy, range)) continue;
                    float distance = car.dst2(enemy);
                    if(distance < closest){ best = enemy; closest = distance; }
                }
            }
        }
        return best;
    }
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

    public static boolean outOfSupplies(Unit unit){
        if(outOfAmmo(unit)) return true;
        for(Unit car : RailConsist.members(unit)) if(reserveCar(unit, car)){
            if(car instanceof LiquidRailUnit tank){
                if(tank.liquidAmount <= 0.001f) for(Liquid liquid : Vars.content.liquids()) if(uses(unit, liquid)) return true;
            }else if(car.stack.amount == 0){
                for(Item item : Vars.content.items()) if(uses(unit, item)) return true;
            }
        }
        return false;
    }

    public static void passDock(Unit unit, RailPointBuild point){
        if(point instanceof CargoStationBuild dock && dock.pickup()) transfer(unit, dock);
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
        return reserveSpace(unit, dock);
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
        return transferred + reserve(unit, dock);
    }
}
