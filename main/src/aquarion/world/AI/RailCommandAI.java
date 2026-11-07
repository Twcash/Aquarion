package aquarion.world.AI;

import aquarion.world.blocks.distribution.RailPoint;
import aquarion.world.blocks.distribution.RailPoint.RailPointBuild;
import aquarion.world.units.RailPathfinder;
import aquarion.world.units.RailPathfinder.Edge;
import aquarion.world.units.RailPathfinder.Route;
import aquarion.world.units.RailTraffic;
import aquarion.world.units.RailCargo;
import aquarion.world.units.RailConsist;
import aquarion.world.blocks.distribution.CargoStation.CargoStationBuild;
import aquarion.world.blocks.units.RailWorkshop.RailWorkshopBuild;
import arc.math.geom.Position;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.ai.types.CommandAI;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;

import static mindustry.Vars.*;

/** Test train controller: no ground pathfinding, steering shortcuts or formation offsets. */
public class RailCommandAI extends CommandAI{
    private final Seq<RailPointBuild> route = new Seq<>();
    private final Vec2 requested = new Vec2(Float.NaN, Float.NaN);
    private final Vec2 position = new Vec2();
    private RailPointBuild node;
    private Edge edge;
    private float distance, retry;
    private boolean attached;
    private boolean sectionCommitted;
    private RailPointBuild waitingFor, parking, yieldOrigin;
    private Unit yieldOwner;
    private RailPointBuild passingJunction;
    private float blockedTime;
    private float moveSpeed, reverseDelay, lastDirection = Float.NaN;
    private boolean reverseFacing;
    private Unit linkedTo;
    private CargoStationBuild cargoTarget;
    private float cargoRetry;
    private Vec2 patrolResume;
    private float patrolSupplyRetry;
    private Teamc pursuitEnemy;
    private Vec2 pursuitResume;
    private RailPointBuild pursuitGoal;
    private float pursuitRetry;
    private Teamc unreachableEnemy;
    private float unreachableRetry;
    private final Vec2 pursuitLastPosition = new Vec2();
    private float pursuitStall;
    private boolean parked;
    private boolean clearingReverse;
    private float reverseRemaining, clearanceRetry, clearanceLeadIn;
    private final Vec2 reverseOrder = new Vec2();
    private RailWorkshopBuild couplingStation;
    private float couplingTravel, couplingLimit;
    private final Seq<TrainPose> trainTrail = new Seq<>();
    private float trailDistance, followDistance;

    private static class TrainPose{
        float x, y, rotation, at;
        Edge edge;
        RailPointBuild node;
        TrainPose(Unit unit, float at, Edge edge, RailPointBuild node){
            x = unit.x; y = unit.y; rotation = unit.rotation; this.at = at; this.edge = edge; this.node = node;
        }
    }

    public RailPoint supportingRail(){
        RailPointBuild support = edge == null ? node : edge.from;
        return RailPathfinder.active(support) ? (RailPoint)support.block : null;
    }

    public boolean stageCoupling(RailWorkshopBuild station, float limit, RailPointBuild destination){
        if(couplingStation != station){
            couplingStation = station;
            if(!attached) attach();
            couplingTravel = 0f;
            if(edge != null){
                Route from = RailPathfinder.find(station, edge.from.x, edge.from.y);
                Route to = RailPathfinder.find(station, edge.to.x, edge.to.y);
                float a = from != null && from.nodes.peek() == edge.from ? from.cost + distance : Float.POSITIVE_INFINITY;
                float b = to != null && to.nodes.peek() == edge.to ? to.cost + edge.length - distance : Float.POSITIVE_INFINITY;
                couplingTravel = Math.min(a, b);
                if(!Float.isFinite(couplingTravel)) couplingTravel = 0f;
                if(edge.to == station) couplingTravel = -(edge.length - distance);
            }else if(node != null){
                Route path = RailPathfinder.find(station, node.x, node.y);
                if(path != null && path.nodes.peek() == node) couplingTravel = path.cost;
            }
        }
        couplingLimit = limit;
        if(couplingTravel >= couplingLimit - 0.01f) return true;
        if(destination != null && (targetPos == null || !targetPos.within(destination, 0.01f))){
            commandPosition(new Vec2(destination.x, destination.y));
        }
        return false;
    }

    public boolean couplingElsewhere(RailWorkshopBuild station){
        return clearingReverse || (couplingStation != null && couplingStation != station && RailPathfinder.active(couplingStation) && couplingStation.holding(unit));
    }

    public float couplingProgress(){ return couplingTravel; }

    public void releaseCoupling(){
        couplingStation = null;
        couplingTravel = couplingLimit = 0f;
    }

    public boolean onStation(RailPointBuild station){
        return (edge == null && node == station) || (edge != null && edge.to == station);
    }

    public void adoptConsistOrder(RailCommandAI previous){
        sectionCommitted = false;
        alignConsistDirection();
        targetPos = previous.targetPos == null ? null : previous.targetPos.cpy();
        attackTarget = previous.attackTarget;
        stances.set(previous.stances);
        requested.set(Float.NaN, Float.NaN);
        if(yieldOwner == null && previous.yieldOwner != null){
            yieldOwner = previous.yieldOwner;
            parking = previous.parking;
            yieldOrigin = previous.yieldOrigin;
            passingJunction = previous.passingJunction;
            parked = false;
            previous.yieldOwner = null;
            previous.parking = previous.yieldOrigin = null;
        }
        cargoTarget = previous.cargoTarget;
        cargoRetry = previous.cargoRetry;
        patrolResume = previous.patrolResume == null ? null : previous.patrolResume.cpy();
        patrolSupplyRetry = previous.patrolSupplyRetry;
        previous.patrolResume = null;
        pursuitEnemy = previous.pursuitEnemy;
        pursuitResume = previous.pursuitResume == null ? null : previous.pursuitResume.cpy();
        pursuitGoal = previous.pursuitGoal;
        pursuitRetry = previous.pursuitRetry;
        unreachableEnemy = previous.unreachableEnemy;
        unreachableRetry = previous.unreachableRetry;
        pursuitLastPosition.set(unit.x, unit.y);
        pursuitStall = 0f;
        previous.pursuitEnemy = null;
        previous.pursuitResume = null;
        commandQueue.clear();
        commandQueue.addAll(previous.commandQueue);
        previous.commandQueue.clear();
        moveSpeed = 0f;
        reverseDelay = 15f;
        previous.route.clear();
    }

    public void seedConsist(Unit rear){
        Seq<Unit> cars = RailConsist.members(unit);
        Seq<TrainPose> combined = new Seq<>();
        float length = 0f;
        for(int i = cars.size - 2; i >= 0; i--){
            if(!(cars.get(i).controller() instanceof RailCommandAI front)) continue;
            front.seedPair(cars.get(i + 1));
            if(cars.get(i + 1).controller() instanceof RailCommandAI back) back.followDistance = length;
            for(TrainPose pose : front.trainTrail){ pose.at += length; combined.add(pose); }
            length += front.trailDistance;
        }
        trainTrail.clear(); trainTrail.addAll(combined); trailDistance = length;
    }

    private void seedPair(Unit rear){
        trainTrail.clear();
        trailDistance = 0f;
        if(!attached) attach();
        RailCommandAI rearAI = rear.controller() instanceof RailCommandAI ai ? ai : null;
        if(rearAI != null && !rearAI.attached) rearAI.attach();
        if(edge != null && rearAI != null && rearAI.edge != null && rearAI.edge.to == edge.from
            && rearAI.edge.from != edge.to){
            appendSeed(rearAI.edge, rearAI.edge.project(rear.x, rear.y), rearAI.edge.length);
            appendSeed(edge, 0f, edge.project(unit.x, unit.y));
            rearAI.followDistance = 0f;
            return;
        }
        if(edge != null && rearAI != null && rearAI.edge != null && rearAI.edge.from == edge.to
            && rearAI.edge.to != edge.from){
            Edge back = RailPathfinder.edge(rearAI.edge.to, rearAI.edge.from);
            Edge ahead = RailPathfinder.edge(edge.to, edge.from);
            if(back != null && ahead != null){
                appendSeed(back, back.project(rear.x, rear.y), back.length);
                appendSeed(ahead, 0f, ahead.project(unit.x, unit.y));
                rearAI.followDistance = 0f;
                return;
            }
        }
        Edge support = edge != null ? edge : rearAI == null ? null : rearAI.edge;
        if(support != null){
            float start = support.project(rear.x, rear.y), end = support.project(unit.x, unit.y);
            if(start > end){
                support = RailPathfinder.edge(support.to, support.from);
                if(support != null){ start = support.project(rear.x, rear.y); end = support.project(unit.x, unit.y); }
            }
            if(support != null){
                Vec2 point = new Vec2();
                int steps = Math.max(1, Mathf.ceil(end - start));
                for(int i = 0; i <= steps; i++){
                    float at = Mathf.lerp(start, end, (float)i / steps);
                    support.sample(at, point);
                    TrainPose pose = new TrainPose(unit, at - start, support, null);
                    pose.x = point.x; pose.y = point.y;
                    trainTrail.add(pose);
                }
                trailDistance = Math.max(0f, end - start);
            }
        }
        if(trainTrail.isEmpty()){
            trainTrail.add(new TrainPose(rear, 0f, rearAI == null ? null : rearAI.edge, rearAI == null ? null : rearAI.node));
            trailDistance = rear.dst(unit);
            trainTrail.add(new TrainPose(unit, trailDistance, edge, node));
        }
        if(rearAI != null) rearAI.followDistance = 0f;
    }

    private void appendSeed(Edge support, float start, float end){
        int steps = Math.max(1, Mathf.ceil(end - start));
        Vec2 point = new Vec2();
        for(int i = 0; i <= steps; i++){
            support.sample(Mathf.lerp(start, end, (float)i / steps), point);
            if(trainTrail.any()) trailDistance += point.dst(trainTrail.peek().x, trainTrail.peek().y);
            TrainPose pose = new TrainPose(unit, trailDistance, support, null);
            pose.x = point.x; pose.y = point.y;
            trainTrail.add(pose);
        }
    }

    private void recordConsist(){
        Unit rear = RailConsist.partner(unit);
        if(rear == null){ trainTrail.clear(); return; }
        if(trainTrail.isEmpty()) seedConsist(rear);
        TrainPose last = trainTrail.peek();
        float span = unit.dst(last.x, last.y);
        if(span > 0.0001f){
            trailDistance += span;
            trainTrail.add(new TrainPose(unit, trailDistance, edge, node));
        }
        while(trainTrail.size > 2 && trainTrail.get(1).at < trailDistance - RailConsist.offset(RailConsist.partner(unit)) - 64f) trainTrail.remove(0);
    }

    private void followConsist(Unit leader){
        unit.vel.setZero();
        if(!attached) attach();
        if(attached && ((edge != null && !edge.valid()) || (edge == null && !RailPathfinder.active(node)))){
            unit.kill();
            return;
        }
        if(!(leader.controller() instanceof RailCommandAI front)) return;
        if(front.trainTrail.isEmpty()) front.seedConsist(unit);
        float target = front.trailDistance - RailConsist.offset(unit);
        float next = Math.min(target, followDistance + unit.speed() * 1.25f * Time.delta);
        if(front.targetPos == null) targetPos = null;
        else if(targetPos == null) targetPos = front.targetPos.cpy();
        else targetPos.set(front.targetPos);
        route.clear();
        if(next <= followDistance + 0.0001f){ holdPosition(); return; }
        TrainPose before = front.trainTrail.first(), after = front.trainTrail.peek();
        for(int i = 1; i < front.trainTrail.size; i++){
            after = front.trainTrail.get(i);
            if(after.at >= next) break;
            before = after;
        }
        float alpha = after.at == before.at ? 0f : Mathf.clamp((next - before.at) / (after.at - before.at));
        position.set(Mathf.lerp(before.x, after.x, alpha), Mathf.lerp(before.y, after.y, alpha));
        Edge support = null;
        float closest = Float.POSITIVE_INFINITY, at = 0f;
        Vec2 sample = new Vec2();
        for(Edge candidate : new Edge[]{before.edge, after.edge}){
            if(candidate == null || !candidate.valid()) continue;
            float progress = candidate.project(position.x, position.y);
            candidate.sample(progress, sample);
            float separation = sample.dst2(position);
            if(separation < closest){ closest = separation; support = candidate; at = progress; }
        }
        if(support != null) support.sample(at, position);
        if(!RailTraffic.canMove(unit, position.x, position.y)){
            moveSpeed = 0f;
            Unit obstruction = RailTraffic.movementBlocker(unit, position.x, position.y);
            if(obstruction != leader && obstruction != null && obstruction.controller() instanceof RailCommandAI ai){
                ai.yieldFor(leader, edge == null ? node : edge.from);
            }
            return;
        }
        moveSpeed = unit.dst(position) / Math.max(Time.delta, 0.001f);
        if(support != null) face(support.sample(at, position));
        unit.set(position);
        edge = support;
        distance = at;
        node = support == null ? after.node : support.from;
        followDistance = next;
        attached = true;
    }

    @Override
    public void commandPosition(Vec2 pos, boolean stopWhenInRange){
        Unit front = unit == null ? null : RailConsist.leader(unit);
        if(front != null && front != unit && front.controller() instanceof RailCommandAI ai){
            ai.commandPosition(pos, stopWhenInRange);
            return;
        }
        pursuitEnemy = null;
        pursuitResume = null;
        pursuitGoal = null;
        super.commandPosition(pos, stopWhenInRange);
    }

    @Override
    public void commandQueue(Position location){
        Unit front = unit == null ? null : RailConsist.leader(unit);
        if(front != null && front != unit && front.controller() instanceof RailCommandAI ai){
            ai.commandQueue(location);
            return;
        }
        super.commandQueue(location);
    }

    @Override
    public void setStance(mindustry.ai.UnitStance stance){
        super.setStance(stance);
        Unit front = unit == null ? null : RailConsist.leader(unit);
        if(front != null && front != unit && front.controller() instanceof RailCommandAI ai) ai.setStance(stance);
    }

    @Override
    public void disableStance(mindustry.ai.UnitStance stance){
        super.disableStance(stance);
        Unit front = unit == null ? null : RailConsist.leader(unit);
        if(front != null && front != unit && front.controller() instanceof RailCommandAI ai) ai.disableStance(stance);
    }

    private boolean atParking(){
        return yieldOwner != null && (parked || (edge == null && node == parking));
    }

    private void updateCargo(){
        if(yieldOwner != null || (unit.type.itemCapacity <= 0 && !(unit instanceof aquarion.world.units.LiquidRailUnit) && !(unit.type instanceof aquarion.units.type.PayloadRailUnitType))) return;
        if(patrolResume != null){
            if(targetPos != null && cargoTarget != null && !targetPos.within(cargoTarget, 0.01f)){
                patrolResume = null;
                cargoTarget = null;
            }else{
                if(cargoTarget == null || !RailPathfinder.active(cargoTarget)
                    || !aquarion.world.units.RailPayloadSupport.needsDock(unit, cargoTarget)) resumePatrol();
                return;
            }
        }
        patrolSupplyRetry -= Time.delta;
        if(cargoTarget == null && targetPos != null && attackTarget == null && patrolSupplyRetry <= 0f
            && !(world.buildWorld(targetPos.x, targetPos.y) instanceof CargoStationBuild station && station.team == unit.team)){
            patrolSupplyRetry = 30f;
            if(aquarion.world.units.RailPayloadSupport.outOfSupplies(unit)){
                CargoStationBuild dock = aquarion.world.units.RailPayloadSupport.choose(unit, edge == null ? node : edge.to);
                if(dock != null){
                    patrolResume = targetPos.cpy();
                    cargoTarget = dock;
                    super.commandPosition(new Vec2(dock.x, dock.y), false);
                    return;
                }
            }
        }
        // A player order always takes precedence over an automatic cargo trip.
        if(cargoTarget != null && targetPos != null && !targetPos.within(cargoTarget, 0.01f)) cargoTarget = null;
        if(cargoTarget == null && (targetPos != null || attackTarget != null || commandQueue.any())) return;
        boolean interrupted = false;
        cargoRetry -= Time.delta;
        if(cargoTarget != null){
            boolean valid = RailPathfinder.active(cargoTarget) && cargoTarget.team == unit.team
                && RailCargo.valid(unit, cargoTarget);
            if(valid && targetPos == null && unit.within(cargoTarget, cargoTarget.block.size * tilesize / 2f + unit.hitSize / 2f)){
                RailCargo.transfer(unit, cargoTarget);
                cargoTarget = null;
                cargoRetry = 0f;
            }else if(valid && targetPos != null && cargoRetry > 0f){
                return;
            }else if(valid && targetPos != null){
                RailPointBuild origin = edge == null ? node : edge.to;
                Route reachable = RailPathfinder.find(origin, cargoTarget.x, cargoTarget.y);
                if(reachable != null && reachable.nodes.peek() == cargoTarget){
                    // Balance at dispatch, rather than oscillating between depots while en route.
                    cargoRetry = 60f;
                    return;
                }
                targetPos = null;
                route.clear();
                cargoTarget = null;
                interrupted = true;
            }else if(!valid){
                targetPos = null;
                route.clear();
                cargoTarget = null;
                cargoRetry = 0f;
                interrupted = true;
            }
        }
        if(cargoRetry > 0f) return;
        cargoRetry = 60f;
        RailPointBuild origin = edge == null ? node : edge.to;
        CargoStationBuild chosen = RailCargo.choose(unit, origin);
        if(chosen == null){
            if(cargoTarget != null){
                targetPos = null;
                route.clear();
                cargoTarget = null;
            }
            if(interrupted && edge != null){
                // A depleted dock cancels the cargo job, not the train's clearance of
                // its current track. Finish that journey instead of stranding it mid-curve.
                commandPosition(Float.isFinite(requested.x) ? requested.cpy() : new Vec2(edge.to.x, edge.to.y));
            }
            return;
        }
        cargoTarget = chosen;
        if(targetPos == null || !targetPos.within(chosen, 0.01f)) commandPosition(new Vec2(chosen.x, chosen.y));
    }

    /** The locomotive immediately ahead in this convoy, or null when uncoupled. */
    public Unit linkedTo(){
        if(RailConsist.linked(unit)) return RailConsist.partner(unit);
        return linkedTo;
    }

    private boolean sameDestination(RailCommandAI other){
        if(yieldOwner != null || other.yieldOwner != null) return false;
        Vec2 ownDestination = targetPos == null ? requested : targetPos;
        Vec2 destination = other.targetPos == null ? other.requested : other.targetPos;
        if(Float.isNaN(destination.x) && other.node != null) return ownDestination.within(other.node, tilesize * 2f);
        return ownDestination.within(destination, tilesize * 2f);
    }

    private float convoyDistance(RailCommandAI other){
        if(edge == null) return Float.POSITIVE_INFINITY;
        if(other.edge != null){
            if(edge.from == other.edge.from && edge.to == other.edge.to) return other.distance - distance;
            if(edge.to == other.edge.from && other.edge.to != edge.from) return edge.length - distance + other.distance;
        }else if(other.node == edge.to){
            return edge.length - distance;
        }
        return Float.POSITIVE_INFINITY;
    }

    private void updateCoupling(){
        linkedTo = null;
        if(edge == null || targetPos == null || yieldOwner != null) return;
        float nearest = Float.POSITIVE_INFINITY;
        for(Unit other : Groups.unit){
            if(other == unit || RailConsist.same(unit, other) || other.dead || other.team != unit.team
                || !(other.controller() instanceof RailCommandAI ai) || !sameDestination(ai)) continue;
            float gap = convoyDistance(ai);
            if(gap > 0.01f && gap <= RailTraffic.carSpacing(unit, other) + tilesize * 3f && gap < nearest){
                nearest = gap;
                linkedTo = other;
            }
        }
    }

    public RailPointBuild trafficNode(){ return edge == null ? node : edge.from; }

    public Edge trafficEdge(){
        return edge;
    }

    public float railHeading(){
        return edge == null ? trainHeading() : edge.sample(distance, new Vec2());
    }

    public float trafficDistance(){
        return distance;
    }

    public float trafficSpeed(){
        return moveSpeed;
    }

    public boolean sectionCommitted(){ return sectionCommitted || RailConsist.leader(unit) != unit; }

    public boolean passingFor(Unit other){ return yieldOwner != null && passingJunction != null && RailConsist.same(yieldOwner, other); }

    public boolean trafficYielding(){ return yieldOwner != null; }

    public boolean trafficParked(){
        return atParking();
    }

    private boolean routeClear(Edge wanted){
        Unit obstruction = RailTraffic.blocker(unit, wanted);
        if(obstruction == null) return true;
        if(clearingReverse && RailConsist.leader(obstruction).controller() instanceof RailCommandAI owner && owner.moveSpeed < 0.01f
            && ((yieldOwner != null && RailConsist.same(obstruction, yieldOwner)) || !owner.hasCommand() || owner.atParking())){
            float start = edge != null && edge.from == wanted.from && edge.to == wanted.to ? distance : 0f;
            float stop = start + reverseRemaining;
            if(stop < wanted.length){
                boolean room = true;
                Vec2 sample = new Vec2();
                for(Unit car : RailConsist.members(obstruction)){
                    float at = wanted.project(car.x, car.y), gap = RailTraffic.clearance(unit, car);
                    wanted.sample(at, sample);
                    if(car.within(sample, gap) && at < stop + gap + 0.5f){ room = false; break; }
                }
                if(room) return true;
            }
        }        if(wanted.to instanceof RailWorkshopBuild workshop && workshop.canReceive(unit, obstruction)) return true;
        // The coupling point moves to the tail as the held train grows past its approach section.
        for(Building building : Groups.build){
            if(building instanceof RailWorkshopBuild workshop && workshop.canReceive(unit, obstruction)) return true;
        }
        // A convoy's destination is a queue, not an exclusive parking space for its leader.
        return obstruction.controller() instanceof RailCommandAI ai
            && ai.yieldOwner == null && sameDestination(ai) && obstruction.within(wanted.to, 0.5f);
    }

    /** Claims conflicting directions and junctions along the entire remaining route. */
    public boolean reserves(Edge wanted){
        if(atParking()) return false;
        if(yieldOwner != null && passingJunction != null && wanted.to == passingJunction) return true;
        RailPointBuild previous = edge == null ? node : edge.to;
        for(RailPointBuild next : route){
            if(previous == null) break;
            if((wanted.from == next && wanted.to == previous)
                || (wanted.to == next && wanted.from != previous)) return true;
            previous = next;
        }
        return false;
    }

    public boolean trafficNeeds(RailPointBuild point){
        if(point != null && RailConsist.linked(unit)){
            for(Unit car : RailConsist.members(unit)){
                if(car.controller() instanceof RailCommandAI ai && ai.edge != null
                    && (ai.edge.from == point || ai.edge.to == point)
                    && car.within(point, car.hitSize + ((RailPoint)point.block).minRadius * 2f + 2f)) return true;
            }
        }
        // A train in a refuge is waiting, not reserving its future return journey.
        if(atParking()) return unit.within(point, unit.hitSize + 2f);
        return plannedNeeds(point);
    }

    private boolean plannedNeeds(RailPointBuild point){
        if(yieldOwner != null && yieldOrigin == point) return true;
        if(unit.within(point, unit.hitSize + 2f) || waitingFor == point) return true;
        if(edge != null && (edge.to == point || (edge.from == point && distance <= unit.hitSize + 2f))) return true;
        if(route.contains(point, true)) return true;
        if(targetPos != null){
            RailPointBuild origin = yieldOwner != null ? yieldOrigin : edge != null ? edge.to : node;
            if(RailPathfinder.active(origin)){
                Route desired = RailPathfinder.find(origin, targetPos.x, targetPos.y);
                if(desired != null && desired.nodes.contains(point, true)) return true;
            }
        }
        return false;
    }

    /** Move aside without discarding the player's original order. */
    public boolean yieldFor(Unit requester, RailPointBuild forbidden){
        requester = RailConsist.leader(requester);
        if(couplingStation != null && couplingStation.holding(unit) && couplingStation.canReceive(requester, unit)) return false;
        Unit leader = RailConsist.leader(unit);
        if(leader != unit && leader.controller() instanceof RailCommandAI ai) return ai.yieldFor(requester, forbidden);
        return yieldFor(requester, forbidden, null);
    }

    private boolean yieldFor(Unit requester, RailPointBuild forbidden, RailPointBuild preferred){
        return yieldFor(requester, forbidden, preferred, false);
    }

    private boolean yieldFor(Unit requester, RailPointBuild forbidden, RailPointBuild preferred, boolean wholeTrain){
        if(yieldOwner != null || requester == unit || clearingReverse) return false;
        // A chain of trains giving way must not eventually wait on itself.
        Unit ownerUnit = requester;
        for(int i = 0; ownerUnit != null && i <= Groups.unit.size(); i++){
            if(ownerUnit == unit && ownerUnit != requester) return false;
            if(!(ownerUnit.controller() instanceof RailCommandAI ownerAI)) break;
            if(ownerAI.atParking()) break;
            Unit nextOwner = ownerAI.yieldOwner;
            if(nextOwner == unit){
                if(ownerUnit != requester || ownerAI.edge != null || ownerAI.node != ownerAI.parking) return false;
                break;
            }
            if(nextOwner == requester) return false;
            ownerUnit = nextOwner;
        }
        if(!wholeTrain && edge != null && hasCommand() && blockedTime < 30f) return false;
        if(requester.controller() instanceof RailCommandAI owner && owner.yieldOwner == unit
            && !owner.atParking()) return false;
        if(edge != null && RailConsist.linked(unit) && RailConsist.leader(unit) == unit){
            Unit tail = RailConsist.partner(unit);
            if(tail != null && tail.controller() instanceof RailCommandAI rear && rear.yieldFor(requester, forbidden, preferred, true)){
                RailConsist.promote(tail);
                return true;
            }
            return false;
        }
        if(wholeTrain && edge != null){
            Seq<Unit> cars = RailConsist.members(unit);
            if(cars.size > 1){
                Unit preceding = cars.get(cars.size - 2);
                // A follower's sampled edge may point either way; orient it toward
                // the existing head before planning a retreat from the trailing end.
                if(edge.project(preceding.x, preceding.y) < distance - 0.01f){
                    Edge towardHead = RailPathfinder.edge(edge.to, edge.from);
                    if(towardHead != null){ distance = towardHead.length - distance; edge = towardHead; }
                }
            }
        }
        Edge retreat = edge == null ? null : RailPathfinder.edge(edge.to, edge.from);
        if(edge != null){
            if(retreat == null) return false;
            // Do not back into a following train or another train's committed passing route.
            Unit behind = RailTraffic.blocker(unit, retreat);
            if(behind != null){
                if(behind == RailConsist.partner(unit) && behind.controller() instanceof RailCommandAI rear){
                    if(rear.yieldFor(requester, forbidden, preferred, true)){
                        RailConsist.promote(behind);
                        return true;
                    }
                    return false;
                }
                // Clear a backing convoy from the rear first. Reversing the front car
                // immediately would pin it against its own followers.
                if(behind.controller() instanceof RailCommandAI rear && rear.edge != null
                    && rear.edge.from == edge.from && rear.edge.to == edge.to && rear.distance < distance){
                    rear.yieldFor(requester, forbidden, preferred);
                }
                return false;
            }
            node = edge.from;
        }
        if(!RailPathfinder.active(node)){
            Building under = world.buildWorld(unit.x, unit.y);
            if(under instanceof RailPointBuild rail && rail.team == unit.team && unit.within(rail, 1f)) node = rail;
        }
        if(!RailPathfinder.active(node) || (edge == null && !unit.within(node, 1f))) return false;
        Route aside = RailPathfinder.parking(node, candidate -> (candidate != node || retreat != null) && candidate != forbidden
            && (preferred == null || candidate == preferred)
            && (!(requester.controller() instanceof RailCommandAI ai && ai.plannedNeeds(candidate))
                // A train just leaving a terminal siding may tuck back into it while
                // the incoming queue parks behind it, then follow that queue out.
                || (retreat != null && candidate == node && node.links.size == 1 && unit.within(node, unit.hitSize + 2f))),
            candidate -> (retreat == null || candidate.from != retreat.to || candidate.to != retreat.from)
                && RailTraffic.parkingPassable(unit, candidate));
        if(aside == null) return false;
        yieldOwner = requester;
        parked = false;
        yieldOrigin = node;
        parking = aside.nodes.peek();
        if(requester.controller() instanceof RailCommandAI owner){
            for(RailPointBuild shared : aside.nodes){
                if(shared != parking && owner.plannedNeeds(shared)) yieldOrigin = shared;
            }
        }
        route.clear();
        route.addAll(aside.nodes);
        route.remove(0);
        if(retreat != null){
            distance = retreat.length - distance;
            edge = retreat;
            moveSpeed = 0f;
            reverseDelay = 15f;
            float tangent = edge.sample(distance, position);
            reverseFacing = Angles.angleDist(unit.rotation, tangent + 180f) < Angles.angleDist(unit.rotation, tangent);
        }
        waitingFor = null;
        retry = 0f;
        if(RailConsist.linked(unit) && route.any()){
            Edge first = RailPathfinder.edge(node, route.first());
            if(first != null && Angles.angleDist(edge == null ? trainHeading() : edge.sample(edge.length, new Vec2()), first.sample(0f, new Vec2())) > 90f){
                passingJunction = node;
                // Claim the approach needed to clear the whole train before granting the pass.
                if(edge == null){ requestReverseClearance(); planReverseClearance(); }
            }
        }
        return true;
    }

    private void blocked(Edge wanted){
        waitingFor = wanted.to;
        Unit other = RailTraffic.blocker(unit, wanted);
        if(other != null && other.controller() instanceof RailCommandAI ai){
            if(ai.yieldOwner == null && sameDestination(ai)){
                // A follower must wait in line rather than pushing its leader into a siding.
                route.clear();
                retry = 6f;
                return;
            }
            if(blockedTime >= 30f && ai.yieldOwner == unit && ai.edge == null && ai.waitingFor == node){
                // The train giving way needs our junction to reach its refuge. Swap roles
                // rather than leaving both trains waiting for the other to move first.
                RailPointBuild oldParking = ai.parking, oldOrigin = ai.yieldOrigin;
                Seq<RailPointBuild> oldRoute = ai.route.copy();
                ai.yieldOwner = null;
                ai.parking = ai.yieldOrigin = null;
                ai.route.clear();
                if(yieldFor(other, wanted.to, oldParking)){
                    ai.waitingFor = null;
                    ai.retry = ai.blockedTime = 0f;
                    return;
                }
                ai.yieldOwner = unit;
                ai.parking = oldParking;
                ai.yieldOrigin = oldOrigin;
                ai.route.addAll(oldRoute);
            }
            if(ai.yieldFor(unit, node)) return;
            // At a passing junction the approaching train can itself take the siding.
            if(blockedTime >= 30f && yieldFor(other, wanted.to)) return;
        }
        route.clear();
        retry = 15f;
    }

    @Override
    public void updateUnit(){
        if(net.client()) return;
        Unit leader = RailConsist.leader(unit);
        if(leader != unit){
            followConsist(leader);
            return;
        }
        updateRail();
        leader = RailConsist.leader(unit);
        if(leader == unit) recordConsist();
        else followConsist(leader);
    }

    private void updateRail(){
        unit.vel.setZero();
        if(linkedTo != null && (!RailTraffic.present(linkedTo) || linkedTo.team != unit.team
            || !(linkedTo.controller() instanceof RailCommandAI leader) || !sameDestination(leader))) linkedTo = null;
        if(net.client()) return;
        boolean recovering = !attached;
        if(!attached) attach();
        if(RailConsist.linked(unit)){
            // Resolve every car's saved position before using occupancy or incoming direction.
            for(Unit car : RailConsist.members(unit)){
                if(car.controller() instanceof RailCommandAI ai && !ai.attached) ai.attach();
            }
            if(recovering) alignConsistDirection();
        }
        if(attached && ((edge != null && !edge.valid()) || (edge == null && !RailPathfinder.active(node)))){
            unit.kill();
            return;
        }
        if(attached && edge == null && RailPathfinder.active(node)){
            unit.set(node.x, node.y);
            aquarion.world.units.RailPayloadSupport.passDock(unit, node);
        }
        if(couplingStation != null && !clearingReverse){
            if(!RailPathfinder.active(couplingStation) || !couplingStation.holding(unit)) releaseCoupling();
            else if(couplingStation.shouldHold(unit)){
                holdPosition();
                moveSpeed = 0f;
                return;
            }
        }
        if(yieldOwner == null){
            RailPointBuild destination = edge == null ? node : edge.to;
            if(destination instanceof RailWorkshopBuild workshop && workshop.shouldHold(unit)){
                holdPosition();
                moveSpeed = 0f;
                return;
            }
        }
        if(waitingFor != null) blockedTime += Time.delta;
        if(atParking()){
            holdPosition();
            if(RailTraffic.present(yieldOwner) && yieldOwner.controller() instanceof RailCommandAI ai && !ai.hasCommand()){
                ai.yieldFor(unit, parking);
            }
            if(RailTraffic.present(yieldOwner) && yieldOwner.controller() instanceof RailCommandAI ai
                && ai.trafficNeeds(yieldOrigin)) return;
            yieldOwner = null;
            parked = false;
            parking = yieldOrigin = passingJunction = null;
            route.clear();
            retry = 0f;
            if(targetPos != null) chooseDirection();
        }
        if(couplingStation == null && !clearingReverse) updateCargo();
        if(RailConsist.leader(unit) != unit) return;
        if(yieldOwner == null && couplingStation == null && !clearingReverse && updatePursuit()){
            holdPosition();
            moveSpeed = 0f;
            return;
        }
        if(attackTarget != null){
            if(targetPos == null) targetPos = new Vec2();
            targetPos.set(attackTarget);
        }
        if(targetPos == null && yieldOwner == null){
            holdPosition();
            route.clear();
            waitingFor = null;
            moveSpeed = 0f;
            return;
        }
        if(yieldOwner == null && !requested.within(targetPos, 0.01f)){
            requested.set(targetPos);
            route.clear();
            retry = 0f;
            chooseDirection();
            if(RailConsist.leader(unit) != unit) return;
        }
        if(clearingReverse){
            if((targetPos == null && yieldOwner == null) || (targetPos != null && !targetPos.within(reverseOrder, 0.01f))){
                clearingReverse = false;
                route.clear();
                requested.set(Float.NaN, Float.NaN);
                return;
            }
            if(reverseRemaining > 0f && edge == null && route.isEmpty()) reverseRemaining = -1f;
            if(reverseRemaining < 0f){
                clearanceRetry -= Time.delta;
                if(clearanceRetry <= 0f){
                    clearanceRetry = 30f;
                    planReverseClearance();
                }
                if(reverseRemaining < 0f){ holdPosition(); moveSpeed = 0f; return; }
            }
            if(reverseRemaining <= 0.01f){
                holdPosition();
                moveSpeed = 0f;
                Unit tail = RailConsist.partner(unit);
                if(tail != null && tail.controller() instanceof RailCommandAI rear
                    && trailDistance - rear.followDistance > RailConsist.offset(tail) + 0.1f) return;
                Route destination = RailPathfinder.find(edge == null ? node : edge.to,
                    yieldOwner == null ? reverseOrder.x : parking.x, yieldOwner == null ? reverseOrder.y : parking.y);
                if(destination == null || !reverseRouteAvailable(destination.nodes.peek())) return;
                clearingReverse = false;
                route.clear();
                if(tail != null) RailConsist.promote(tail);
                return;
            }
        }
        updateCoupling();
        if(linkedTo != null && linkedTo.controller() instanceof RailCommandAI leader && !leader.hasCommand()
            && convoyDistance(leader) <= RailTraffic.carSpacing(unit, linkedTo) + 1f){
            // Arrive as one train, keeping the trailing cars on the rails behind the locomotive.
            moveSpeed = 0f;
            finish();
            return;
        }
        if(edge != null){
            if(RailTraffic.reservationBlocker(unit, edge) != null){
                holdPosition();
                moveSpeed = 0f;
                return;
            }
            if(RailConsist.linked(unit) && !sectionCommitted){
                if(!routeClear(edge)){
                    holdPosition();
                    moveSpeed = 0f;
                    blockedTime += Time.delta;
                    blocked(edge);
                    return;
                }
                sectionCommitted = true;
            }
            // Project any unit-physics displacement back onto the reserved track.
            edge.sample(distance, position);
            unit.set(position);
            if(reverseDelay > 0f){
                reverseDelay -= Time.delta;
                return;
            }
            float acceleration = unit.type.speed / 20f;
            float limit = route.isEmpty() ? Math.min(unit.speed(), Mathf.sqrt(2f * acceleration * (edge.length - distance))) : unit.speed();
            if(clearingReverse) limit = Math.min(limit, Mathf.sqrt(2f * acceleration * Math.max(0f, reverseRemaining)));
            if(couplingStation != null && !clearingReverse) limit = Math.min(limit, Mathf.sqrt(2f * acceleration * Math.max(0f, couplingLimit - couplingTravel)));
            Unit rear = RailConsist.partner(unit);
            if(rear != null && rear.controller() instanceof RailCommandAI tail
                && trailDistance - tail.followDistance > RailConsist.offset(rear) + unit.speed() * 2f){
                limit = 0f;
            }
            limit = Math.min(limit, RailTraffic.followingSpeed(unit, edge, distance, acceleration));
            if(linkedTo != null && linkedTo.controller() instanceof RailCommandAI leader){
                float space = Math.max(0f, convoyDistance(leader) - RailTraffic.carSpacing(unit, linkedTo) - 0.5f);
                limit = Math.min(limit, Mathf.sqrt(leader.moveSpeed * leader.moveSpeed + 2f * acceleration * space));
            }
            moveSpeed = Mathf.approach(moveSpeed, limit, acceleration * Time.delta);
            float nextDistance = Math.min(edge.length, distance + moveSpeed * Time.delta);
            if(couplingStation != null && !clearingReverse) nextDistance = Math.min(nextDistance, distance + Math.max(0f, couplingLimit - couplingTravel));
            if(clearingReverse) nextDistance = Math.min(nextDistance, distance + Math.max(0f, reverseRemaining));
            float rotation = edge.sample(nextDistance, position);
            if(!RailTraffic.canMove(unit, position.x, position.y)
                || (limit < 0.001f && moveSpeed < 0.001f && nextDistance < edge.length)){
                moveSpeed = 0f;
                // A refuge can hold a queue, provided every car clears its entrance.
                if(yieldOwner != null && edge.to == parking && route.isEmpty() && distance > unit.hitSize + 2f){
                    Unit ahead = RailTraffic.movementBlocker(unit, position.x, position.y);
                    if(ahead == null) ahead = RailTraffic.blocker(unit, edge);
                    if(ahead != null && ahead.controller() instanceof RailCommandAI ai
                        && (ai.atParking() || !ai.hasCommand())){
                        parked = true;
                        return;
                    }
                }
                blockedTime += Time.delta;
                if(blockedTime >= 30f){
                    Unit other = RailTraffic.movementBlocker(unit, position.x, position.y);
                    if(other == null) other = RailTraffic.blocker(unit, edge);
                    if(other != null && other.controller() instanceof RailCommandAI ai){
                        boolean following = (ai.edge != null && ai.edge.from == edge.from && ai.edge.to == edge.to)
                            || (ai.edge == null && ai.node == edge.to && sameDestination(ai));
                        if(!following && !ai.yieldFor(unit, edge.from) && (!ai.hasCommand() || unit.id > other.id)){
                            yieldFor(other, edge.to);
                        }
                    }
                }
                return;
            }
            blockedTime = 0f;
            if(couplingStation != null && !clearingReverse) couplingTravel += nextDistance - distance;
            if(clearingReverse) reverseRemaining -= nextDistance - distance;
            distance = nextDistance;
            face(rotation);
            unit.set(position);
            if(distance < edge.length) return;
            node = edge.to;
            aquarion.world.units.RailPayloadSupport.passDock(unit, node);
            lastDirection = rotation;
            edge = null;
            retry = 0f;
            if(node instanceof RailWorkshopBuild workshop && workshop.shouldHold(unit)){
                moveSpeed = 0f;
                return;
            }
            if(route.isEmpty()) moveSpeed = 0f;
            if(yieldOwner != null && node == parking) return;
        }
        if(!RailPathfinder.active(node) || !unit.within(node, 1f)){
            node = null;
            if(!attach()) return;
            if(edge != null) return;
        }
        retry -= Time.delta;
        if(route.isEmpty()){
            if(retry > 0f) return;
            float targetX = yieldOwner == null ? requested.x : parking.x;
            float targetY = yieldOwner == null ? requested.y : parking.y;
            Route desired = RailPathfinder.find(node, targetX, targetY);
            if(RailConsist.linked(unit) && desired != null){
                RailPointBuild destination = desired.nodes.peek();
                Route directed = directedTrain(node, destination, trainHeading(), candidate -> true);
                if(directed == null){
                    Edge back = desired.nodes.size > 1 ? RailPathfinder.edge(node, desired.nodes.get(1)) : null;
                    if(back != null && wholeTrainOn(back) && reverseRouteAvailable(destination)){
                        RailConsist.promote(RailConsist.partner(unit));
                    }else{
                        requestReverseClearance();
                    }
                    return;
                }
                Edge back = desired.nodes.size > 1 ? RailPathfinder.edge(node, desired.nodes.get(1)) : null;
                if(back != null && wholeTrainOn(back) && directed.cost > reverseRouteCost(destination) + reversalPenalty()){
                    RailConsist.promote(RailConsist.partner(unit));
                    return;
                }
                desired = directed;
            }
            retry = 30f;
            if(desired == null) return;
            RailPointBuild goal = desired.nodes.peek();
            Route found = RailConsist.linked(unit)
                ? directedTrain(node, goal, trainHeading(), candidate -> routeClear(candidate)
                    || (yieldOwner != null && candidate.to == parking && RailTraffic.parkingPassable(unit, candidate)))
                : RailPathfinder.find(node, goal.x, goal.y, candidate -> routeClear(candidate)
                    || (yieldOwner != null && candidate.to == parking && RailTraffic.parkingPassable(unit, candidate)));
            if(found == null && RailConsist.linked(unit)){
                // Request space along the selected drivable route, retaining its direction.
                for(int i = 1; i < desired.nodes.size; i++){
                    Edge section = RailPathfinder.edge(desired.nodes.get(i - 1), desired.nodes.get(i));
                    if(section == null) break;
                    Unit obstruction = RailTraffic.blocker(unit, section);
                    if(obstruction != null && obstruction.controller() instanceof RailCommandAI ai){
                        ai.yieldFor(unit, node);
                        break;
                    }
                }
            }
            if(found != null && found.nodes.peek() != goal){
                // Arrange the pass while we can still leave the junction free for it.
                // Entering the clear prefix first forces both trains to shunt backward later.
                for(int i = 1; i < desired.nodes.size; i++){
                    Edge step = RailPathfinder.edge(desired.nodes.get(i - 1), desired.nodes.get(i));
                    if(step == null) break;
                    Unit obstruction = RailTraffic.blocker(unit, step);
                    if(obstruction != null){
                        if(obstruction.controller() instanceof RailCommandAI ai && !sameDestination(ai)
                            && ai.yieldFor(unit, node)){
                            retry = 6f;
                            return;
                        }
                        break;
                    }
                }
                // A partial search result can be a dead-end spur merely closer in world space.
                // Wait along the intended path instead of repeatedly taking those detours.
                Seq<RailPointBuild> prefix = new Seq<>();
                prefix.add(node);
                for(int i = 1; i < desired.nodes.size; i++){
                    Edge step = RailPathfinder.edge(desired.nodes.get(i - 1), desired.nodes.get(i));
                    if(step == null || !routeClear(step)) break;
                    prefix.add(desired.nodes.get(i));
                }
                found.nodes.clear();
                found.nodes.addAll(prefix);
            }
            // Advance to a clear junction even when a train farther ahead occupies the goal.
            // Keeping both trains at opposite terminals would prevent either reaching a siding.
            if(found == null || (found.nodes.peek() != goal && found.nodes.size == 1)){
                if(desired.nodes.size > 1){
                    Edge wanted = RailPathfinder.edge(node, desired.nodes.get(1));
                    if(wanted != null) blocked(wanted);
                }
                return;
            }
            route.addAll(found.nodes);
            route.remove(0);
            if(route.isEmpty()){
                if(yieldOwner == null) finish();
                return;
            }
        }
        RailPointBuild next = route.first();
        Edge wanted = RailPathfinder.edge(node, next);
        if(wanted == null){
            route.clear();
            retry = 0f;
            return;
        }
        if(!routeClear(wanted)
            && !(yieldOwner != null && next == parking && RailTraffic.parkingPassable(unit, wanted))){
            blocked(wanted);
            return;
        }
        if(RailConsist.linked(unit) && !clearingReverse
            && (Angles.angleDist(trainHeading(), wanted.sample(0f, new Vec2())) > 90f || occupiedByConsist(wanted))){
            route.clear();
            retry = 0f;
            return;
        }
        edge = wanted;
        sectionCommitted = true;
        float departure = edge.sample(0f, position);
        reverseFacing = Angles.angleDist(unit.rotation, departure + 180f) < Angles.angleDist(unit.rotation, departure);
        if(!Float.isNaN(lastDirection) && Angles.angleDist(lastDirection, departure) > 90f){
            moveSpeed = 0f;
            reverseDelay = 15f;
        }
        waitingFor = null;
        blockedTime = 0f;
        route.remove(0);
        distance = 0f;
    }

    private void holdPosition(){
        if(edge != null && edge.valid()){
            edge.sample(distance, position);
            unit.set(position);
        }else if(RailPathfinder.active(node)){
            unit.set(node.x, node.y);
        }
    }

    private void face(float tangent){
        // A locomotive backs up without flipping its body by 180 degrees.
        float opposite = tangent + 180f;
        float facing = Angles.angleDist(unit.rotation, opposite) < Angles.angleDist(unit.rotation, tangent) ? opposite : tangent;
        unit.rotation = Angles.moveToward(unit.rotation, facing, unit.type.rotateSpeed * Time.delta);
    }

    private boolean wholeTrainOn(Edge section){
        if(section == null) return false;
        for(Unit car : RailConsist.members(unit)){
            if(car == unit || !(car.controller() instanceof RailCommandAI ai)) continue;
            Edge occupied = ai.edge;
            if(occupied == null){
                if(ai.node != section.from && ai.node != section.to) return false;
            }else if(!((occupied.from == section.from && occupied.to == section.to)
                || (occupied.from == section.to && occupied.to == section.from))) return false;
        }
        return true;
    }

    private void requestReverseClearance(){
        clearingReverse = true;
        reverseOrder.set(targetPos == null ? requested : targetPos);
        if(yieldOwner != null) passingJunction = edge == null ? node : edge.to;
        reverseRemaining = -1f;
        clearanceRetry = 0f;
        clearanceLeadIn = 0f;
        route.clear();
        moveSpeed = 0f;
    }

    /** Plan enough forward track for the tail and its turning clearance to pass the junction. */
    private void planReverseClearance(){
        Unit tail = RailConsist.partner(unit);
        if(tail == null){ clearingReverse = false; return; }
        float needed = clearanceLeadIn + RailConsist.offset(tail) + RailTraffic.clearance(unit, tail)
            + (supportingRail() == null ? tilesize * 4f : supportingRail().minRadius * 2f);
        RailPointBuild current = edge == null ? node : edge.to;
        if(!RailPathfinder.active(current)) return;
        float covered = edge == null ? 0f : edge.length - distance;
        float heading = edge == null ? trainHeading() : edge.sample(edge.length, new Vec2());
        Route forward = RailPathfinder.clearance(current, heading, Math.max(0f, needed - covered), candidate -> !occupiedByConsist(candidate));
        if(forward == null) return;
        route.clear();
        route.addAll(forward.nodes);
        route.remove(0);
        reverseRemaining = needed;
        clearanceLeadIn = 0f;
        retry = 0f;
    }

    /** Recovered controllers may attach to either orientation of the same physical section. */
    public void alignConsistDirection(){
        if(!attached) attach();
        if(edge != null && yieldOwner == null && consistAhead()){
            Edge reversed = RailPathfinder.edge(edge.to, edge.from);
            if(reversed != null){
                distance = reversed.length - distance;
                edge = reversed;
                route.clear();
                retry = 0f;
            }
        }
        lastDirection = edge == null ? Float.NaN : edge.sample(distance, new Vec2());
    }

    private boolean consistAhead(){
        Seq<Unit> cars = RailConsist.members(unit);
        if(edge == null || cars.size < 2) return false;
        Unit next = cars.get(1);
        float ahead = edge.project(next.x, next.y);
        if(ahead > distance + 0.01f) return true;
        if(Math.abs(ahead - distance) <= 0.01f && next.controller() instanceof RailCommandAI ai && ai.edge != null){
            return (ai.edge.from == edge.to || ai.edge.to == edge.to)
                && ai.edge.from != edge.from && ai.edge.to != edge.from;
        }
        return false;
    }

    /** A departure back into any occupied car is a reversal of the whole train. */
    public boolean reverseConsistFor(Edge departure){
        if(departure == null || !RailConsist.linked(unit)) return false;
        for(Unit car : RailConsist.members(unit)){
            if(car == unit || !(car.controller() instanceof RailCommandAI ai)) continue;
            Edge occupied = ai.edge;
            boolean sameTrack = occupied != null && ((occupied.from == departure.from && occupied.to == departure.to)
                || (occupied.from == departure.to && occupied.to == departure.from));
            if((sameTrack && departure.project(car.x, car.y) > 0.01f)
                || (occupied == null && ai.node == departure.to)){
                RailConsist.promote(RailConsist.partner(unit));
                return true;
            }
        }
        return false;
    }

    private Route directedTrain(RailPointBuild start, RailPointBuild goal, float heading, arc.func.Boolf<Edge> allowed){
        Unit tail = RailConsist.partner(unit);
        float length = tail == null ? 0f : RailConsist.offset(tail) + RailTraffic.clearance(unit, tail);
        if(edge != null && start == edge.to) length = Math.max(0f, length - (edge.length - distance));
        return RailPathfinder.findDirected(start, goal, heading, allowed, length, this::occupiedByConsist);
    }

    private boolean reverseRouteAvailable(RailPointBuild goal){ return Float.isFinite(reverseRouteCost(goal)); }

    private float reverseRouteCost(RailPointBuild goal){
        Seq<Unit> cars = RailConsist.members(unit);
        if(cars.size < 2) return Float.POSITIVE_INFINITY;
        Unit tail = cars.peek(), preceding = cars.get(cars.size - 2);
        if(!(tail.controller() instanceof RailCommandAI rear)) return Float.POSITIVE_INFINITY;
        if(!rear.attached) rear.attach();
        RailPointBuild start = rear.node;
        float heading = rear.unit.rotation + 180f, approach = 0f;
        if(rear.edge != null){
            Edge section = rear.edge;
            boolean towardStart = section.project(preceding.x, preceding.y) > rear.distance;
            Edge travel = towardStart ? RailPathfinder.edge(section.to, section.from) : section;
            if(travel == null) return Float.POSITIVE_INFINITY;
            start = travel.to;
            approach = towardStart ? rear.distance : section.length - rear.distance;
            heading = travel.sample(travel.length, new Vec2());
        }else if(start != null && preceding.controller() instanceof RailCommandAI front && front.edge != null){
            Edge incoming = front.edge.to == start ? front.edge : front.edge.from == start ? RailPathfinder.edge(front.edge.to, front.edge.from) : null;
            if(incoming != null) heading = incoming.sample(incoming.length, new Vec2());
        }
        if(!RailPathfinder.active(start)) return Float.POSITIVE_INFINITY;
        Route route = RailPathfinder.findDirected(start, goal, heading, candidate -> true,
            RailConsist.offset(tail) + RailTraffic.clearance(unit, tail), this::occupiedByConsist);
        if(route != null) return approach + route.cost;
        // Leaving a terminal refuge can require reversing to the junction, drawing
        // the whole train past it, then returning onto the destination branch.
        float length = RailConsist.offset(tail) + RailTraffic.clearance(unit, tail)
            + ((RailPoint)start.block).minRadius * 2f;
        Route room = RailPathfinder.clearance(start, heading, length, candidate -> !occupiedByConsist(candidate));
        if(room == null || room.nodes.size < 2) return Float.POSITIVE_INFINITY;
        Edge departure = RailPathfinder.edge(start, room.nodes.get(1));
        if(departure == null) return Float.POSITIVE_INFINITY;
        Route after = RailPathfinder.findDirected(start, goal, departure.sample(0f, new Vec2()) + 180f,
            candidate -> true, RailConsist.offset(tail), this::occupiedByConsist);
        return after == null ? Float.POSITIVE_INFINITY : approach + length * 2f + after.cost;
    }

    private float reversalPenalty(){
        Unit tail = RailConsist.partner(unit);
        return tail == null ? 20f : (RailConsist.offset(tail) + RailTraffic.clearance(unit, tail)) * 2f + tilesize * 4f;
    }
    private float trainHeading(){
        if(Float.isFinite(lastDirection)) return lastDirection;
        if(node != null){
            for(Unit car : RailConsist.members(unit)){
                if(car == unit || !(car.controller() instanceof RailCommandAI ai) || ai.edge == null) continue;
                Edge incoming = ai.edge.to == node ? ai.edge : ai.edge.from == node ? RailPathfinder.edge(ai.edge.to, ai.edge.from) : null;
                if(incoming != null) return incoming.sample(incoming.length, new Vec2());
            }
        }
        return unit.rotation;
    }

    private boolean occupiedByConsist(Edge section){
        for(Unit car : RailConsist.members(unit)){
            if(car == unit || !(car.controller() instanceof RailCommandAI ai)) continue;
            Edge occupied = ai.edge;
            if(occupied != null && ((occupied.from == section.from && occupied.to == section.to)
                || (occupied.from == section.to && occupied.to == section.from))) return true;
            if(occupied == null && ai.node == section.to) return true;
        }
        return false;
    }

    private void chooseDirection(){
        if(edge == null) return;
        if(RailConsist.linked(unit)){
            alignConsistDirection();
            // Prefer a complete forward route. Do not compare two independent node paths
            // and repeatedly swap leading ends as their approximate costs change.
            Route reachable = RailPathfinder.find(edge.to, requested.x, requested.y);
            if(reachable == null) return;
            Route forwardRoute = directedTrain(edge.to, reachable.nodes.peek(), edge.sample(edge.length, new Vec2()), candidate -> true);
            float reverseCost = reverseRouteCost(reachable.nodes.peek());
            if(forwardRoute != null && (edge.length - distance + forwardRoute.cost <= reverseCost + reversalPenalty()
                || !wholeTrainOn(edge))) return;
            if(wholeTrainOn(edge) && Float.isFinite(reverseCost)) RailConsist.promote(RailConsist.partner(unit));
            else{
                float leadIn = reachable.nodes.size > 1 && reachable.nodes.get(1) != edge.from ? edge.length - distance : 0f;
                requestReverseClearance();
                clearanceLeadIn = leadIn;
            }
            return;
        }
        Route forward = RailPathfinder.find(edge.to, requested.x, requested.y);
        Route backward = RailPathfinder.find(edge.from, requested.x, requested.y);
        if(forward != null && backward != null && backward.cost + distance + ((forward.nodes.size > 1 && forward.nodes.get(1) == edge.from) ? 0f : 20f) < forward.cost + edge.length - distance){
            Edge reversed = RailPathfinder.edge(edge.to, edge.from);
            if(reversed == null) return;
            distance = reversed.length - distance;
            edge = reversed;
            moveSpeed = 0f;
            reverseDelay = 15f;
        }
        float tangent = edge.sample(distance, position);
        reverseFacing = Angles.angleDist(unit.rotation, tangent + 180f) < Angles.angleDist(unit.rotation, tangent);
    }

    private boolean attach(){
        float tolerance = attached ? 0.25f : 6f;
        float closest = tolerance * tolerance;
        for(Building building : Groups.build){
            if(building instanceof RailPointBuild rail && rail.team == unit.team && RailPathfinder.active(rail)){
                float offset = unit.dst2(rail);
                if(offset < closest){ closest = offset; node = rail; }
            }
        }
        if(node != null){
            unit.set(node.x, node.y);
            attached = true;
            return true;
        }
        // Also recover units spawned or saved between nodes on a track.
        Edge nearest = null;
        float progress = 0f;
        for(Building building : Groups.build){
            if(!(building instanceof RailPointBuild from) || from.team != unit.team || !RailPathfinder.active(from)) continue;
            RailPoint block = (RailPoint)from.block;
            if(!from.within(unit, block.range + 6f)) continue;
            for(int i = 0; i < from.links.size; i++){
                Building target = world.build(from.links.get(i));
                if(!(target instanceof RailPointBuild to)) continue;
                Edge candidate = RailPathfinder.edge(from, to);
                if(candidate == null) continue;
                float at = candidate.project(unit.x, unit.y);
                candidate.sample(at, position);
                float offset = unit.dst2(position);
                if(offset < closest){ closest = offset; nearest = candidate; progress = at; }
            }
        }
        if(nearest == null) return false;
        Route forward = RailPathfinder.find(nearest.to, requested.x, requested.y);
        Route backward = RailPathfinder.find(nearest.from, requested.x, requested.y);
        if(backward != null && forward != null && backward.cost + progress < forward.cost + nearest.length - progress){
            edge = RailPathfinder.edge(nearest.to, nearest.from);
            distance = edge.length - progress;
        }else{
            edge = nearest;
            distance = progress;
        }
        float tangent = edge.sample(distance, position);
        reverseFacing = Angles.angleDist(unit.rotation, tangent + 180f) < Angles.angleDist(unit.rotation, tangent);
        unit.set(position);
        attached = true;
        return true;
    }

    private void endPursuit(){
        Vec2 destination = pursuitResume;
        pursuitEnemy = null;
        pursuitResume = null;
        pursuitGoal = null;
        route.clear();
        waitingFor = null;
        targetPos = null;
        if(destination != null) super.commandPosition(destination, false);
        pursuitRetry = 30f;
    }

    /** Returns true while the train should hold its rail position and fire. */
    private boolean updatePursuit(){
        unreachableRetry -= Time.delta;
        if(unreachableRetry <= 0f) unreachableEnemy = null;
        if(patrolResume != null) return false;
        if(!hasStance(mindustry.ai.UnitStance.pursueTarget)){
            if(pursuitEnemy != null) endPursuit();
            return false;
        }
        if(attackTarget != null){
            if(aquarion.world.units.RailPayloadSupport.canEngage(unit, attackTarget)) return true;
            pursuitRetry -= Time.delta;
            if(pursuitRetry <= 0f){
                pursuitRetry = 20f;
                Route reachable = RailPathfinder.pursuit(edge == null ? node : edge.to,
                    point -> aquarion.world.units.RailPayloadSupport.canFireFrom(unit, attackTarget, point.x, point.y));
                if(reachable == null){
                    unreachableEnemy = attackTarget;
                    unreachableRetry = 600f;
                    attackTarget = null;
                    targetPos = null;
                    if(pursuitEnemy != null) endPursuit();
                    else finish();
                }
            }
            return false;
        }
        if(pursuitEnemy != null && (!aquarion.world.units.RailPayloadSupport.validEnemy(unit, pursuitEnemy)
            || !unit.within(pursuitEnemy, aquarion.world.units.RailPayloadSupport.pursuitRange(unit) * 2f))){
            endPursuit();
            return false;
        }
        pursuitRetry -= Time.delta;
        if(pursuitEnemy == null){
            if(pursuitRetry > 0f) return false;
            pursuitRetry = 20f;
            Teamc enemy = aquarion.world.units.RailPayloadSupport.pursuitTarget(unit, unreachableEnemy);
            if(enemy == null) return false;
            pursuitResume = targetPos == null ? null : targetPos.cpy();
            pursuitEnemy = enemy;
            pursuitLastPosition.set(unit.x, unit.y);
            pursuitStall = 0f;
            cargoTarget = null;
            pursuitGoal = null;
            pursuitRetry = 0f;
            RailPointBuild start = edge == null ? node : edge.to;
            if(start != null) super.commandPosition(new Vec2(start.x, start.y), false);
        }
        if(aquarion.world.units.RailPayloadSupport.canEngage(unit, pursuitEnemy)){
            pursuitStall = 0f;
            pursuitLastPosition.set(unit.x, unit.y);
            return true;
        }
        if(pursuitLastPosition.dst2(unit.x, unit.y) < 1f) pursuitStall += Time.delta;
        else{
            pursuitLastPosition.set(unit.x, unit.y);
            pursuitStall = 0f;
        }
        if(pursuitStall >= 180f){
            unreachableEnemy = pursuitEnemy;
            unreachableRetry = 600f;
            endPursuit();
            return false;
        }
        if(pursuitRetry <= 0f){
            pursuitRetry = 20f;
            RailPointBuild goal = null;
            if(edge != null){
                Vec2 sample = new Vec2();
                int steps = Math.max(1, (int)Math.ceil((edge.length - distance) / 4f));
                for(int i = 0; i <= steps; i++){
                    edge.sample(distance + (edge.length - distance) * i / steps, sample);
                    if(aquarion.world.units.RailPayloadSupport.canFireFrom(unit, pursuitEnemy, sample.x, sample.y)){
                        goal = edge.to;
                        break;
                    }
                }
            }
            if(goal == null){
                Route path = RailPathfinder.pursuit(edge == null ? node : edge.to,
                    point -> aquarion.world.units.RailPayloadSupport.canFireFrom(unit, pursuitEnemy, point.x, point.y));
                if(path != null) goal = path.nodes.peek();
            }
            if(goal == null){
                unreachableEnemy = pursuitEnemy;
                unreachableRetry = 600f;
                endPursuit();
                return false;
            }
            if(goal != pursuitGoal || targetPos == null){
                pursuitGoal = goal;
                super.commandPosition(new Vec2(goal.x, goal.y), false);
            }
        }
        return false;
    }

    private void resumePatrol(){
        Vec2 destination = patrolResume;
        patrolResume = null;
        cargoTarget = null;
        route.clear();
        waitingFor = null;
        if(destination != null) super.commandPosition(destination, false);
        patrolSupplyRetry = 30f;
    }

    private void finish(){
        if(patrolResume != null){
            if(cargoTarget != null && unit.within(cargoTarget, cargoTarget.block.size * tilesize / 2f + unit.hitSize / 2f)){
                aquarion.world.units.RailPayloadSupport.transfer(unit, cargoTarget);
            }
            resumePatrol();
            return;
        }
        if(pursuitEnemy != null){
            route.clear();
            waitingFor = null;
            moveSpeed = 0f;
            return;
        }
        if(cargoTarget == null && targetPos != null && node instanceof CargoStationBuild dock
            && targetPos.within(dock, dock.block.size * tilesize / 2f)
            && unit.within(dock, dock.block.size * tilesize / 2f + unit.hitSize / 2f)
            && (RailCargo.waitForFullLoad(unit, dock) || RailCargo.waitForUnload(unit, dock))){
            moveSpeed = 0f;
            waitingFor = null;
            return;
        }
        Vec2 completed = targetPos == null ? null : targetPos.cpy();
        if(cargoTarget == null && node instanceof CargoStationBuild station
            && unit.within(station, station.block.size * tilesize / 2f + unit.hitSize / 2f)) RailCargo.transfer(unit, station, true);
        targetPos = null;
        attackTarget = null;
        route.clear();
        waitingFor = null;
        if(commandQueue.any()){
            Position next = commandQueue.remove(0);
            if(next instanceof Teamc target) commandTarget(target);
            else commandPosition(new Vec2(next.getX(), next.getY()));
            if(completed != null && (hasStance(mindustry.ai.UnitStance.patrol) || currentCommand() == mindustry.ai.UnitCommand.loopPayloadCommand)) commandQueue.add(completed);
        }
    }
}


































