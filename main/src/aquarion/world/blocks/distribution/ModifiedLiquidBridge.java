package aquarion.world.blocks.distribution;

import aquarion.world.content.LiquidReactions;
import aquarion.world.content.LiquidUtil;
import arc.math.Mathf;
import arc.util.Time;
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

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount){
            //never store more than the bridge end holds, no matter how much the source claims to send
            float free = LiquidUtil.freeSpace(self());
            if(free > 0f) liquids.add(liquid, Math.min(amount, free));
        }

        public float moveLiquid(Building next, Liquid liquid){
            if(next == null) return 0;

            //vanilla blocks expect the standard vanilla transfer, not the custom fast flow
            if(!LiquidUtil.isCustomLiquidBlock(next)){
                if (next == null) {
                    return 0.0F;
                } else {
                    next = next.getLiquidDestination(this, liquid);
                    if (next.team == this.team && next.block.hasLiquids && this.liquids.get(liquid) > 0.0F) {
                        float ofract = next.liquids.get(liquid) / next.block.liquidCapacity;
                        float fract = this.liquids.get(liquid) / this.block.liquidCapacity * this.block.liquidPressure;
                        float flow = Math.min(Mathf.clamp(fract - ofract) * this.block.liquidCapacity, this.liquids.get(liquid));
                        flow = Math.min(flow, next.block.liquidCapacity - next.liquids.get(liquid));
                        if (flow > 0.0F && ofract <= fract && next.acceptLiquid(this, liquid)) {
                            next.handleLiquid(this, liquid, flow);
                            this.liquids.remove(liquid, flow);
                            return flow;
                        }

                        if (!next.block.consumesLiquid(liquid) && next.liquids.currentAmount() / next.block.liquidCapacity > 0.1F && fract > 0.1F) {
                            float fx = (this.x + next.x) / 2.0F;
                            float fy = (this.y + next.y) / 2.0F;
                            Liquid other = next.liquids.current();
                            if (other.blockReactive && liquid.blockReactive) {
                                if ((!(other.flammability > 0.3F) || !(liquid.temperature > 0.7F)) && (!(liquid.flammability > 0.3F) || !(other.temperature > 0.7F))) {
                                    if (liquid.temperature > 0.7F && other.temperature < 0.55F || other.temperature > 0.7F && liquid.temperature < 0.55F) {
                                        this.liquids.remove(liquid, Math.min(this.liquids.get(liquid), 0.7F * Time.delta));
                                        if (Mathf.chanceDelta((double)0.2F)) {
                                            Fx.steam.at(fx, fy);
                                        }
                                    }
                                } else {
                                    if (Mathf.chanceDelta(0.1)) {
                                        Fx.fire.at(fx, fy);
                                    }
                                }
                            }
                        }
                    }

                    return 0.0F;
                }
            }

            next = next.getLiquidDestination(self(), liquid);

            if(next == null) return 0;

            if(next.team == team && next.block.hasLiquids && liquids.get(liquid) > 0f){
                float flow = LiquidUtil.flow(self(), liquid, next) * delta();
                if(flow > 0.01f && next.acceptLiquid(self(), liquid)){
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
