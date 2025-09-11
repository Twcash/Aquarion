package aquarion.world.blocks.distribution;

import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidRouter;

public class ModifiedLiquidRouter extends LiquidRouter {
    public boolean willMelt = false;
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
                    float levelHere = liquids.get(liquid) / block.liquidCapacity;
                    float levelNext = other.liquids.get(liquid) / other.block.liquidCapacity;
                    float deltaLevel = Math.max(levelHere - levelNext, 0f) * 50;

                    float rho = 1f;
                    float viscosityFactor = Mathf.clamp(1f - liquid.viscosity * 0.5f, 0.2f, 1f);
                    float Cd = 0.8f;
                    float A = 1f;

                    float flow = Cd * A * Mathf.sqrt(2f * deltaLevel / rho) * viscosityFactor;

                    flow *= 10f;

                    flow = Math.min(flow, liquids.get(liquid));
                    flow = Math.min(flow, other.block.liquidCapacity - other.liquids.get(liquid));
                    if(flow > 0f && other.acceptLiquid(self(), liquid)){
                        other.handleLiquid(self(), liquid, flow);
                        liquids.remove(liquid, flow);
                    } else if (!other.block.consumesLiquid(liquid) && other.liquids.currentAmount() / other.block.liquidCapacity > 0.1f ) {
                        other.handleLiquid(self(), liquid, flow);
                        liquids.remove(liquid, flow);
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
            if(liquids.currentAmount() > 0.1f && liquid.temperature > 0.5f && !willMelt){
                damageContinuous(liquid.temperature/100f);
                if(Mathf.chanceDelta(0.01)){
                    Fx.steam.at(x, y);
                }
            }
        }

    }
}
