package aquarion.world.AI;

import mindustry.gen.Unit;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;

/**
 * Cover behavior for low-health Gerbs: hides behind a bulkier ally, or behind a
 * solid wall on the far side from the nearest threat, or pulls back out of range.
 * Keeps peeking and firing while hidden.
 */
public class GerbCoverAI extends GerbAI {
    @Override
    public void updateMovement(){
        Unit threat = nearestEnemy(160f);
        Unit protector = findStrongerAlly(100f);

        if(protector != null){
            //hide behind a bulkier ally, opposite the enemy
            hideBehind(threat, protector, unit.type.hitSize * 3f);
        }else{
            Tile cover = findCover(160f, tilesize * 8f);
            if(cover != null){
                takeCover(cover, 160f);
            }else if(threat != null){
                //nothing to hide behind - pull back out of its range
                fleeFrom(threat, unit.range() * 1.5f);
            }else{
                //not being pressured - don't camp, keep pushing toward the core
                approachCore();
                return;
            }
        }

        //peek and fire while hiding
        engageNearest(unit.range());
        faceTarget();
    }
}