package aquarion.world.blocks.distribution;

import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidRouter;

public class ModifiedLiquidRouter extends LiquidRouter {
    public ModifiedLiquidRouter(String name) {
        super(name);
    }
    public class ughBuild extends LiquidRouterBuild{
        //Possible that this will not evenly dump liquid
        public void dumpLiquid(Liquid liquid, float scaling, int outputDir){
            int dump = this.cdump;

            if(liquids.get(liquid) <= 0.0001f) return;

            for(int i = 0; i < proximity.size; i++){
                incrementDump(proximity.size);

                Building other = proximity.get((i + dump) % proximity.size);
                if(outputDir != -1 && (outputDir + rotation) % 4 != relativeTo(other)) continue;

                other = other.getLiquidDestination(self(), liquid);

                if(other != null && other.block.hasLiquids && canDumpLiquid(other, liquid) && other.liquids != null){
                    float ofract = other.liquids.get(liquid) / other.block.liquidCapacity;
                    float fract = liquids.get(liquid) / block.liquidCapacity * block.liquidPressure;

                    float maxTransfer = (other.block.liquidCapacity - other.liquids.get(liquid)) / 2f;
                    float flow = Math.min(Math.max(liquids.get(liquid) - other.liquids.get(liquid), 0f), maxTransfer);

                    if(flow > 0f && ofract <= fract && other.acceptLiquid(self(), liquid)){
                        other.handleLiquid(self(), liquid, flow);
                        liquids.remove(liquid, flow);
                        return;
                    }
                }
            }
        }

        @Override
        public void updateTile(){
            Liquid liquid = liquids.current();
            if(liquids.currentAmount() > 0.01f){
                dumpLiquid(liquids.current());
            }
            if(liquids.currentAmount() > 0.1f && liquid.temperature > 0.5f){
                damageContinuous(liquid.temperature/100f);
                if(Mathf.chanceDelta(0.01)){
                    Fx.steam.at(x, y);
                }
            }
        }

    }
}
