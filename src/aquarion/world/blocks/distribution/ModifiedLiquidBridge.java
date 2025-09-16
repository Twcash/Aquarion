package aquarion.world.blocks.distribution;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Puddles;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.blocks.liquid.LiquidBridge;

public class ModifiedLiquidBridge extends LiquidBridge {
    public ModifiedLiquidBridge(String name) {
        super(name);
    }
    public boolean willMelt = true;
    public class ModLiquidBridgeBuild extends  LiquidBridgeBuild{

        @Override
        public void updateTransport(Building other){
            if(warmup >= 0.25f){
                moved |= moveLiquid(other, liquids.current()) > 0.05f;
            }
            Liquid liquid = liquids.current();

            if(liquids.currentAmount() > 0.1f && liquid.temperature > 0.5f && !willMelt){
                damageContinuous(liquid.temperature/100f);
                if(Mathf.chanceDelta(0.01)){
                    Fx.steam.at(x, y);
                }
            }
        }
        public float moveLiquid(Building next, Liquid liquid){
            if(next == null) return 0;

            next = next.getLiquidDestination(self(), liquid);

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
                flow = Math.min(flow, next.block.liquidCapacity - next.liquids.get(liquid));
                if(flow > 0f && next.acceptLiquid(self(), liquid)){
                    next.handleLiquid(self(), liquid, flow);
                    liquids.remove(liquid, flow);
                    return flow;
                } else if (!next.block.consumesLiquid(liquid) && next.liquids.currentAmount() / next.block.liquidCapacity > 0.1f ) {
                    //TODO !IMPORTANT! uses current(), which is 1) wrong for multi-liquid blocks and 2) causes unwanted reactions, e.g. hydrogen + slag in pump
                    //TODO these are incorrect effect positions
                    float fx = (x + next.x) / 2f, fy = (y + next.y) / 2f;

                    Liquid other = next.liquids.current();
                    if(other.blockReactive && liquid.blockReactive){
                        //TODO liquid reaction handler for extensibility
                        if((other.flammability > 0.3f && liquid.temperature > 0.7f) || (liquid.flammability > 0.3f && other.temperature > 0.7f) && !willMelt){
                            damageContinuous(liquid.temperature/100f);
                            next.damageContinuous(liquid.temperature/100f);
                            if(Mathf.chanceDelta(0.01)){
                                Fx.steam.at(fx, fy);
                            }

                        }else if((liquid.temperature > 0.7f && other.temperature < 0.55f) || (other.temperature > 0.7f && liquid.temperature < 0.55f)){
                            liquids.remove(liquid, Math.min(liquids.get(liquid), 0.7f * Time.delta));
                            if(Mathf.chanceDelta(0.2f)){
                                Fx.steam.at(fx, fy);
                            }
                        }
                    }
                }
            }
            return 0;
        }
    }
}
