package aquarion.world.blocks.distribution;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Puddles;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.blocks.liquid.Conduit;

public class ModifiedConduit extends Conduit {
    public float tempDamage;
    public ModifiedConduit(String name) {
        super(name);
    }
    public class ModifiedConduitBuild extends ConduitBuild{

        @Override
        public void updateTile(){
            Liquid liquid = liquids.current();
            smoothLiquid = Mathf.lerpDelta(smoothLiquid, liquids.currentAmount() / liquidCapacity, 0.05f);

            if(liquids.currentAmount() > 0.0001f && timer(timerFlow, 1)){
                moveLiquidForward(leaks, liquids.current());
                noSleep();
            }else{
                sleep();
            }

            if(liquids.currentAmount() > 0.1f && liquid.temperature > 0.5f){
                damageContinuous(liquid.temperature/100f);
                if(Mathf.chanceDelta(0.01)){
                    Fx.steam.at(x, y);
                }
            }
        }

        public float moveLiquidForward(boolean leaks, Liquid liquid){
            Tile next = tile.nearby(rotation);

            if(next == null) return 0;

            if(next.build != null){
                return moveLiquid(next.build, liquid);
            }else if(leaks && !next.block().solid && !next.block().hasLiquids){
                float leakAmount = liquids.get(liquid) / 1.5f;
                Puddles.deposit(next, tile, liquid, leakAmount, true, true);
                liquids.remove(liquid, leakAmount);
            }
            return 0;

        }

        public float moveLiquid(Building next, Liquid liquid){
            if(next == null) return 0;

            next = next.getLiquidDestination(self(), liquid);

            if(next.team == team && next.block.hasLiquids && liquids.get(liquid) > 0f){
                float ofract = next.liquids.get(liquid) / next.block.liquidCapacity;
                float fract = liquids.get(liquid) / block.liquidCapacity * block.liquidPressure;
                float flow = Math.min(Mathf.clamp((fract - ofract)) * (block.liquidCapacity), liquids.get(liquid));
                flow = Math.min(flow, next.block.liquidCapacity - next.liquids.get(liquid));

                if(flow > 0f && ofract <= fract && next.acceptLiquid(self(), liquid)){
                    next.handleLiquid(self(), liquid, flow);
                    liquids.remove(liquid, flow);
                    return flow;
                    //handle reactions between different liquid types ▼
                }else if(!next.block.consumesLiquid(liquid) && next.liquids.currentAmount() / next.block.liquidCapacity > 0.1f && fract > 0.1f){
                    //TODO !IMPORTANT! uses current(), which is 1) wrong for multi-liquid blocks and 2) causes unwanted reactions, e.g. hydrogen + slag in pump
                    //TODO these are incorrect effect positions
                    float fx = (x + next.x) / 2f, fy = (y + next.y) / 2f;

                    Liquid other = next.liquids.current();
                    if(other.blockReactive && liquid.blockReactive){
                        //TODO liquid reaction handler for extensibility
                        if((other.flammability > 0.3f && liquid.temperature > 0.7f) || (liquid.flammability > 0.3f && other.temperature > 0.7f)){
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
