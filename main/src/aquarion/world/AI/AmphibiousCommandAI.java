package aquarion.world.AI;

import aquarion.world.units.AmphibiousPathfinder;
import aquarion.world.units.HopLanding;
import aquarion.units.type.AquaUnitType;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import mindustry.ai.UnitCommand;
import mindustry.ai.UnitStance;
import mindustry.ai.types.CommandAI;
import mindustry.gen.Building;
import mindustry.gen.Teamc;

import static mindustry.Vars.tilesize;

/** Keeps normal targeting and queued move/attack orders, replacing only movement routing. */
public class AmphibiousCommandAI extends CommandAI {
    private final AmphibiousPathfinder path = new AmphibiousPathfinder();
    private final Vec2 destination = new Vec2();
    private final Vec2 requestedDestination = new Vec2(Float.NaN, Float.NaN);
    private final Vec2 safeDestination = new Vec2();
    private float destinationRefresh;

    @Override
    public void defaultBehavior(){
        if(currentCommand() != UnitCommand.moveCommand){
            super.defaultBehavior();
            return;
        }
        updateVisuals();
        updateTargeting();
        if(attackTarget != null && invalid(attackTarget)){
            attackTarget = null;
            targetPos = null;
        }
        if(targetPos == null && attackTarget == null) advanceQueue();
        if(attackTarget != null){
            if(targetPos == null) targetPos = new Vec2();
            targetPos.set(attackTarget);
        }
        if(targetPos != null){
            destination.set(targetPos);
            if(group != null && group.valid && groupIndex >= 0 && groupIndex < group.units.size){
                float[] positions = group.positions;
                if(positions != null && groupIndex * 2 + 1 < positions.length){
                    destination.add(positions[groupIndex * 2], positions[groupIndex * 2 + 1]);
                }
            }
            float minimum = unit.type instanceof AquaUnitType type ? type.hopMinDistance : 0f;
            float arrival = Math.max(Math.max(3f, minimum), unit.hitSize / 2f);
            if(attackTarget == null && unit.type instanceof AquaUnitType type) resolveDestination(type);
            float stop = attackTarget == null || !AmphibiousPathfinder.clearShot(unit, attackTarget)
                ? arrival : Math.max(arrival, unit.range() * 0.85f);
            //A friendly occupied formation slot is reached beside the occupant;
            //do not keep jumping back into it to hit the exact command coordinate.
            if(attackTarget == null && !HopLanding.clear(unit, destination.x, destination.y)){
                stop = Math.max(stop, unit.physicSize() * 2f + 2f);
            }
            if(attackTarget instanceof Building build) stop += build.block.size * tilesize / 2f;
            path.move(unit, destination.x, destination.y, stop);
            if(attackTarget == null && unit.within(destination, stop)) advanceQueue();
            else if(stopWhenInRange && unit.within(destination, stop)){
                advanceQueue();
                stopWhenInRange = false;
            }
        }
        faceTarget();
    }

    /** A click or formation slot inside a wall means the nearest clear footing, not the solid tile's center. */
    private void resolveDestination(AquaUnitType type){
        destinationRefresh -= arc.util.Time.delta;
        if(destinationRefresh <= 0f || !requestedDestination.within(destination, 0.01f) ||
            !AmphibiousPathfinder.canLand(type, safeDestination.x, safeDestination.y)){
            requestedDestination.set(destination);
            safeDestination.set(destination);
            destinationRefresh = type.hopRepathInterval;
            if(!AmphibiousPathfinder.canLand(type, destination.x, destination.y)){
                int tx = Math.round(destination.x / tilesize), ty = Math.round(destination.y / tilesize);
                float best = Float.POSITIVE_INFINITY;
                int radius = Math.max(6, (int)Math.ceil(unit.hitSize / tilesize));
                for(int ring = 1; ring <= radius; ring++){
                    for(int dx = -ring; dx <= ring; dx++){
                        for(int dy = -ring; dy <= ring; dy++){
                            if(Math.abs(dx) != ring && Math.abs(dy) != ring) continue;
                            float x = (tx + dx) * tilesize, y = (ty + dy) * tilesize;
                            if(!AmphibiousPathfinder.canLand(type, x, y)) continue;
                            float score = destination.dst2(x, y) + unit.dst2(x, y) * 0.001f;
                            if(score < best){ best = score; safeDestination.set(x, y); }
                        }
                    }
                    if(best < Float.POSITIVE_INFINITY) break;
                }
            }
        }
        destination.set(safeDestination);
    }

    private void advanceQueue(){
        Vec2 previous = targetPos;
        targetPos = null;
        attackTarget = null;
        if(commandQueue.any()){
            Position next = commandQueue.remove(0);
            if(previous != null && hasStance(UnitStance.patrol)) commandQueue.add(previous.cpy());
            if(next instanceof Teamc enemy) commandTarget(enemy, stopAtTarget);
            else commandPosition(new Vec2(next.getX(), next.getY()));
        }
    }
}
