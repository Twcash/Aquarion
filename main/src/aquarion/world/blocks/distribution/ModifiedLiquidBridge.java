package aquarion.world.blocks.distribution;

import aquarion.world.content.LiquidReactions;
import aquarion.world.content.LiquidUtil;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidBridge;

import static mindustry.Vars.world;

public class ModifiedLiquidBridge extends LiquidBridge {
    public ModifiedLiquidBridge(String name) {
        super(name);
    }
    public boolean willMelt = true;
    public class ModLiquidBridgeBuild extends  LiquidBridgeBuild{

        @Override
        public void updateTransport(Building other){
            if(warmup >= 0.25f){
                liquids.each((liquid, amount) -> {
                    if(amount > 0.0001f){
                        moved |= moveLiquid(other, liquid) > 0.05f;
                    }
                });
            }

            //reactions between mixed liquids
            LiquidReactions.react(self());

            if(willMelt){
                liquids.each((liquid, amount) -> {
                    if(amount > 0.1f && liquid.temperature > 0.5f){
                        damageContinuous(liquid.temperature/100f);
                        if(Mathf.chanceDelta(0.01)){
                            Fx.steam.at(x, y);
                        }
                    }
                });
            }
        }

        @Override
        public void doDump(){
            liquids.each((liquid, amount) -> {
                if(amount > 0.0001f) dumpLiquid(liquid, 1f);
            });
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return hasLiquids && team == source.team && LiquidUtil.freeSpace(self()) > 0.01f
                    && checkAccept(source, world.tile(link));
        }

        public float moveLiquid(Building next, Liquid liquid){
            if(next == null) return 0;

            next = next.getLiquidDestination(self(), liquid);

            if(next == null) return 0;

            if(next.team == team && next.block.hasLiquids && liquids.get(liquid) > 0f){
                float levelHere = liquids.get(liquid) / block.liquidCapacity;
                float levelNext = next.liquids.get(liquid) / next.block.liquidCapacity;
                float deltaLevel = Math.max(levelHere - levelNext, 0f) * 50;

                float rho = 1f;
                float viscosityFactor = Mathf.clamp(1f - liquid.viscosity * 0.5f, 0.2f, 1f);
                float Cd = 0.8f;
                float A = 1f;

                float flow = Cd * A * Mathf.sqrt(2f * deltaLevel / rho) * viscosityFactor;

                flow *= 10f;

                flow = Math.min(flow, liquids.get(liquid));
                flow = Math.min(flow, LiquidUtil.freeSpace(next));
                if(flow > 0f && next.acceptLiquid(self(), liquid)){
                    next.handleLiquid(self(), liquid, flow);
                    liquids.remove(liquid, flow);
                    return flow;
                } else if (!next.block.consumesLiquid(liquid)) {
                    //the two liquids can't mix (next is full or won't accept), so they react at the boundary
                    LiquidReactions.reactAtBoundary(self(), liquid, next);
                }
            }
            return 0;
        }
    }
}
