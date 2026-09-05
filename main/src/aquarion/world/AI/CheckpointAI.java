package aquarion.world.AI;

import aquarion.world.blocks.environment.CheckpointBlock;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.ai.types.GroundAI;
import mindustry.entities.units.AIController;
import mindustry.world.Tile;

/**
 * Follows the numbered checkpoint markers placed in the map editor.
 * The unit walks from checkpoint to checkpoint in ascending number order without pathfinding,
 * looping back to the lowest number after reaching the last one, while still rotating to
 * shoot at any targets its weapons acquire along the way.
 * Falls back to standard ground attack behavior on maps without checkpoints.
 */
public class CheckpointAI extends AIController {
    /** Distance in world units at which a checkpoint counts as reached and the next one is targeted. */
    public float arriveRadius = 12f;

    protected int checkpointIndex;
    protected boolean started;

    @Override
    public boolean useFallback() {
        return CheckpointBlock.checkpoints.isEmpty();
    }

    @Override
    public @Nullable AIController fallback() {
        return new GroundAI();
    }

    @Override
    public void updateMovement() {
        Seq<Tile> points = CheckpointBlock.checkpoints;
        if (points.isEmpty()) return;

        if (!started) {
            started = true;
            checkpointIndex = nearestCheckpoint(points);
        }

        if (checkpointIndex >= points.size) checkpointIndex = 0;

        Tile checkpoint = points.get(checkpointIndex);
        if (unit.within(checkpoint.worldx(), checkpoint.worldy(), arriveRadius)) {
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
