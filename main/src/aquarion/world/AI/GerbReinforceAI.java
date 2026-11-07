package aquarion.world.AI;

import arc.util.Tmp;
import mindustry.entities.Units;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;

/**
 * Body-shield behavior: the bigger Gerbs move between a weaker/little ally and
 * whatever is shooting it, soaking the shots. Works against enemy units and
 * buildings alike. Falls back to pushing toward the core when nobody needs
 * shielding.
 */
public class GerbReinforceAI extends GerbAI {
    @Override
    public void updateMovement(){
        Unit ally = findReinforceTarget(reinforceRange);
        if(ally == null){
            //no little ally to shield - keep pushing toward the core
            approachCore();
            return;
        }

        //body-block: stand between the ally and its nearest threat (unit or turret)
        Teamc threat = Units.closestTarget(unit.team, ally.x, ally.y, 200f);
        if(threat != null){
            float ang = ally.angleTo(threat);
            Tmp.v1.trns(ang, Math.max(unit.type.hitSize * 2f, 10f)).add(ally);
        }else{
            Tmp.v1.set(ally);
        }

        pathMoveTo(Tmp.v1, unit.type.hitSize * 2f);
        engageNearest(unit.range());
        faceTarget();
    }
}