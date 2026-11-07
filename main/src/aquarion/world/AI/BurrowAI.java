package aquarion.world.AI;

import aquarion.units.type.BurrowUnitType;
import aquarion.world.units.BurrowCombat;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;

/** Burrowed movement can take a direct route underneath solid terrain. */
public class BurrowAI extends AIController{
    private float combatRetarget;

    @Override
    public void updateTargeting(){
        super.updateTargeting();
        if((combatRetarget -= Time.delta) <= 0f || (target != null && !BurrowCombat.valid(unit, target))){
            combatRetarget = 20f;
            target = Units.closestTarget(unit.team, unit.x, unit.y, ((BurrowUnitType)unit.type).breachRange * 2f,
                u -> u.isGrounded(), b -> true);
        }
    }
    @Override
    public void updateMovement(){
        var destination = target != null ? target : unit.closestEnemyCore();
        if(destination == null){
            destination = Units.closestTarget(unit.team, unit.x, unit.y, ((BurrowUnitType)unit.type).breachRange * 2f,
                u -> u.isGrounded(), b -> true);
        }
        if(destination != null) BurrowCombat.move(unit, destination);
    }
}
