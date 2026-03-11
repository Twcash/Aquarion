package aquarion.world.blocks.core;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.OverdriveProjector;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.indexer;

public class OverclockProjector extends OverdriveProjector{

    public float damagePercent = 0.01f;

    public OverclockProjector(String name){
        super(name);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        if(!super.canPlaceOn(tile, team, rotation)) return false;

        float wx = tile.worldx();
        float wy = tile.worldy();

        boolean blocked = indexer.eachBlock(team, wx, wy, range,
                other -> other.block instanceof OverclockProjector,
                other -> {}
        );

        return !blocked;
    }

    boolean validTarget(Building other){
        BlockGroup g = other.block.group;

        return g != BlockGroup.projectors &&
                g != BlockGroup.liquids &&
                g != BlockGroup.transportation;
    }

    public class OverclockBuild extends OverdriveBuild{

        @Override
        public void updateTile(){
            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
            heat = Mathf.lerpDelta(heat, efficiency > 0 ? 1f : 0f, 0.08f);
            charge += heat * Time.delta;

            if(hasBoost){
                phaseHeat = Mathf.lerpDelta(phaseHeat, optionalEfficiency, 0.1f);
            }

            float realRange = range + phaseHeat * phaseRangeBoost;

            if(efficiency > 0){
                indexer.eachBlock(this, realRange,
                        OverclockProjector.this::validTarget,
                        other -> {
                            float healthFrac = other.healthf();
                            float slow = Mathf.clamp(healthFrac, 0.1f, 1f);
                            float damage = other.block.health * (damagePercent + phaseHeat * 0.02f) * slow * Time.delta;
                            other.damage(damage);
                        }
                );

                useProgress += delta();
            }

            if(useProgress >= useTime){
                consume();
                useProgress %= useTime;
            }
        }
    }
}
