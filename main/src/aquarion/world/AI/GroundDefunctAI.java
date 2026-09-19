package aquarion.world.AI;

import aquarion.world.blocks.environment.CheckpointBlock;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.ai.types.GroundAI;
import mindustry.entities.Units;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.world.Tile;

/**
 * Defunct ground unit behavior: follows the numbered ground checkpoints in
 * ascending order, looping after the last one. When a map has no ground
 * checkpoints, attacks the nearest enemy anywhere on the map instead.
 */
public class GroundDefunctAI extends GroundAI {
    private static final float mapRange = 1000000f;

    /** Distance in world units at which a checkpoint counts as reached. */
    public float arriveRadius = 12f;

    protected int checkpointIndex;
    protected boolean started;

    @Override
    public Teamc findMainTarget(float x, float y, float range, boolean air, boolean ground){
        Unit result = Units.closestEnemy(unit.team, x, y, mapRange, u -> u.checkTarget(air, ground));
        if(result != null) return result;

        return super.findMainTarget(x, y, range, air, ground);
    }

    @Override
    public void updateMovement(){
        Seq<Tile> points = CheckpointBlock.checkpoints;
        if(points.isEmpty()){
            //no ground route on this map: hunt the nearest enemy
            super.updateMovement();
            return;
        }

        if(!started){
            started = true;
            checkpointIndex = nearestCheckpoint(points);
        }

        if(checkpointIndex >= points.size) checkpointIndex = 0;

        Tile checkpoint = points.get(checkpointIndex);
        if(unit.within(checkpoint.worldx(), checkpoint.worldy(), arriveRadius)){
            checkpointIndex = (checkpointIndex + 1) % points.size;
            checkpoint = points.get(checkpointIndex);
        }

        moveTo(Tmp.v1.set(checkpoint.worldx(), checkpoint.worldy()), 0f);
        faceTarget();
    }

    int nearestCheckpoint(Seq<Tile> points) {
        int nearest = 0;
        float minDst = Float.MAX_VALUE;
        for (int i = 0; i < points.size; i++) {
            Tile tile = points.get(i);
            float dst = unit.dst2(tile.worldx(), tile.worldy());
            if (dst < minDst) {
                minDst = dst;
                nearest = i;
            }
        }
        return nearest;
    }
}
