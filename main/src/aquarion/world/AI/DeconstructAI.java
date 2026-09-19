package aquarion.world.AI;

import arc.util.Nullable;
import mindustry.ai.types.FlyingAI;
import mindustry.ai.types.GroundAI;
import mindustry.content.Blocks;
import mindustry.entities.units.AIController;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Prop;

import static mindustry.Vars.world;

/**
 * Seeks out the nearest derelict environment block and deconstructs it.
 * Teamless tiles (static walls, props, rocks) and derelict structures both count
 * as derelict. Once the target is gone the next-nearest one is picked.
 * Units that cannot build fall back to standard combat AI.
 */
public class DeconstructAI extends AIController{
    /** Ticks between world scans for a new target. */
    public float retargetInterval = 120f;
    /** Maximum search radius in world units. <= 0 searches the whole map. */
    public float searchRadius = -1f;

    protected @Nullable Tile blockTarget;

    @Override
    public boolean useFallback(){
        return unit.type.buildSpeed <= 0f;
    }

    @Override
    public @Nullable AIController fallback(){
        return unit.type.flying ? new FlyingAI() : new GroundAI();
    }

    @Override
    public void updateMovement(){
        unit.updateBuilding = true;

        if(blockTarget != null && !validTarget(blockTarget)){
            blockTarget = null;
            unit.clearBuilding();
        }

        if(blockTarget == null && timer.get(timerTarget, retargetInterval)){
            blockTarget = findTarget();
        }

        if(blockTarget == null) return;

        //keep exactly one breaking plan, aimed at the current target
        BuildPlan plan = unit.buildPlan();
        if(plan == null || plan.x != blockTarget.x || plan.y != blockTarget.y){
            unit.plans.clear();
            unit.addBuild(new BuildPlan(blockTarget.x, blockTarget.y));
        }

        float range = Math.min(unit.type.buildRange - unit.type.hitSize * 2f, 1500f);
        moveTo(blockTarget, range, 20f);

        //mirror BuilderAI aiming: only face hostiles when not busy deconstructing
        if(target != null && shouldShoot()){
            unit.lookAt(target);
        }else if(!unit.type.flying){
            unit.lookAt(unit.prefRotation());
        }
    }

    @Override
    public boolean shouldShoot(){
        return !unit.isBuilding() && unit.type.canAttack;
    }

    /** Whether a block should never be deconstructed. Props (boulders, bushes and the like) are ignored. */
    protected boolean ignored(Block block){
        return block instanceof Prop;
    }

    /** Whether the current target can still be deconstructed. Stays true while it is being broken. */
    protected boolean validTarget(Tile tile){
        return tile.block() != Blocks.air && !ignored(tile.block()) && Build.validBreak(unit.team, tile.x, tile.y);
    }

    /** Finds the nearest derelict breakable block. */
    protected @Nullable Tile findTarget(){
        float best = Float.MAX_VALUE;
        Tile found = null;
        float max2 = searchRadius > 0f ? searchRadius * searchRadius : Float.MAX_VALUE;

        for(Tile tile : world.tiles){
            if(tile.block() == Blocks.air || tile.team() != Team.derelict || ignored(tile.block())) continue;

            float dst2 = unit.dst2(tile.worldx(), tile.worldy());
            if(dst2 >= best || dst2 > max2) continue;
            if(!Build.validBreak(unit.team, tile.x, tile.y)) continue;

            best = dst2;
            found = tile;
        }
        return found;
    }
}
