package aquarion.world.AI;

import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.gen.Unit;

import static mindustry.Vars.state;

/**
 * Default Gerb combat behavior: always pushes toward the enemy core so units
 * never idle away from the fight. Engages anything in weapon range, strafing
 * around it when pinned down, and only stops pushing when actually taking fire.
 * Falls back to hunting the nearest enemy (or the spawner) when no core exists.
 *
 * <p>{@link #updateStuck()} is deliberately the last movement call each frame so
 * an unstick sidestep is never overwritten by the advance that follows it.</p>
 */
public class GerbAssaultAI extends GerbAI {
    @Override
    public void updateMovement(){
        //never drown: march straight back out of deep water
        if(inDeepWater()){
            fleeWater();
            faceMovement();
            updateStuck();
            return;
        }

        Building core = nearestEnemyCore();
        boolean attacked = underFire();

        //aim at anything in range; shoot while advancing
        if(engageNearest(unit.range())){
            //pinned down: strafe around the target instead of standing still
            if(attacked){
                strafeRing(target, unit.range() * 0.6f);
                updateStuck();
                return;
            }
            //otherwise keep pushing while firing
        }

        if(core != null){
            //keep closing on the core, staying in squad formation
            approachCore();
        }else{
            //no core to push on - hunt the nearest enemy
            Unit far = nearestEnemyAnywhere();
            if(far != null){
                pathMoveTo(Tmp.v1.set(far), unit.range() * 0.6f);
                faceMovement();
            }else if(state.rules.waves){
                //nothing left to fight: push toward the enemy spawner
                moveToSpawn(state.rules.dropZoneRadius + 120f);
                faceMovement();
            }else{
                halt();
            }
        }

        //run last so an unstick sidestep actually breaks the unit free
        updateStuck();
    }
}