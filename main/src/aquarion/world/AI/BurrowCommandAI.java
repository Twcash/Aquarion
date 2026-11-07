package aquarion.world.AI;

import arc.math.geom.Position;
import arc.math.geom.Vec2;
import mindustry.ai.UnitCommand;
import mindustry.ai.UnitStance;
import mindustry.ai.types.CommandAI;
import mindustry.gen.Teamc;
import aquarion.units.type.BurrowUnitType;
import aquarion.world.units.BurrowCombat;
import mindustry.entities.Units;
import arc.util.Time;

/** Normal move/attack orders, taking direct underground routes instead of ground paths. */
public class BurrowCommandAI extends CommandAI{
    private final Vec2 destination = new Vec2();
    private float combatRetarget;

    @Override
    public void updateTargeting(){
        super.updateTargeting();
        if((combatRetarget -= Time.delta) <= 0f || (target != null && !BurrowCombat.valid(unit, target))){
            combatRetarget = 20f;
            target = Units.closestTarget(unit.team, unit.x, unit.y, ((BurrowUnitType)unit.type).breachRange,
                u -> u.isGrounded(), b -> true);
        }
    }

    @Override
    public void defaultBehavior(){
        if(currentCommand() != UnitCommand.moveCommand){ super.defaultBehavior(); return; }
        updateVisuals();
        updateTargeting();
        if(attackTarget != null && invalid(attackTarget)){ attackTarget = null; targetPos = null; }
        if(targetPos == null && attackTarget == null) nextOrder();
        if(attackTarget != null){
            if(targetPos == null) targetPos = new Vec2();
            targetPos.set(attackTarget);
        }
        if(targetPos == null){
            if(BurrowCombat.valid(unit, target)) BurrowCombat.move(unit, target);
            return;
        }
        if(attackTarget != null){
            BurrowCombat.move(unit, attackTarget);
            return;
        }
        destination.set(targetPos);
        if(attackTarget == null && group != null && group.valid && group.positions != null && groupIndex >= 0 &&
            groupIndex * 2 + 1 < group.positions.length){
            destination.add(group.positions[groupIndex * 2], group.positions[groupIndex * 2 + 1]);
        }
        float stop = attackTarget == null ? Math.max(5f, unit.hitSize / 2f) : 16f;
        //Aim inside the arrival radius; stopping exactly on its edge can leave
        //the order pending forever as movement approaches zero asymptotically.
        moveTo(destination, attackTarget == null ? 0f : stop, 30f);
        if(unit.moving()) unit.lookAt(unit.vel.angle());
        if(attackTarget == null && unit.within(destination, stop)) nextOrder();
    }

    private void nextOrder(){
        Vec2 old = targetPos;
        targetPos = null;
        attackTarget = null;
        if(commandQueue.any()){
            Position next = commandQueue.remove(0);
            if(old != null && hasStance(UnitStance.patrol)) commandQueue.add(old.cpy());
            if(next instanceof Teamc enemy) commandTarget(enemy, stopAtTarget);
            else commandPosition(new Vec2(next.getX(), next.getY()));
        }
    }
}
