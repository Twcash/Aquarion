package aquarion.world.blocks.distribution;

import aquarion.ui.LiquidBar;
import aquarion.world.content.LiquidReactions;
import aquarion.world.content.LiquidUtil;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidRouter;

public class ModifiedLiquidRouter extends LiquidRouter {
    public boolean willMelt = false;

    public ModifiedLiquidRouter(String name) {
        super(name);
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("liquid");
    }

    public class ughBuild extends LiquidRouterBuild {
        @Override
        public void displayBars(Table bars){
            super.displayBars(bars);
            liquids.each((liquid, amount) -> {
                if(amount > 0.001f){
                    bars.add(new LiquidBar(self(), liquid));
                    bars.row();
                }
            });
        }
        @Override
        public void updateTile() {
            //reactions between mixed liquids
            LiquidReactions.react(self());

            //dump every liquid present, not just the current one
            liquids.each((liquid, amount) -> {
                if(amount > 0.0001f) dumpLiquid(liquid);
            });

            if (LiquidUtil.total(liquids) > 0.1f && willMelt) {
                liquids.each((liquid, amount) -> {
                    if (amount > 0.1f && liquid.temperature > 0.5f) {
                        damageContinuous(liquid.temperature / 100f);
                        if (Mathf.chanceDelta(0.01f)) {
                            Fx.steam.at(x, y);
                        }
                    }
                });
            }
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return LiquidUtil.freeSpace(self()) > 0.01f;
        }

        @Override
        public void transferLiquid(Building next, float amount, Liquid liquid){
            //vanilla blocks expect the standard vanilla transfer, not the custom fast flow
            if(!LiquidUtil.isCustomLiquidBlock(next)){
                super.transferLiquid(next, amount, liquid);
                return;
            }
            float flow = Math.min(LiquidUtil.flow(self(), liquid, next) * delta(), amount);
            if(flow <= 0.01f) return;
            if(next.acceptLiquid(self(), liquid)){
                next.handleLiquid(self(), liquid, flow);
                liquids.remove(liquid, flow);
            }
        }

        @Override
        public void draw(){
            Draw.rect(bottomRegion, x, y);

            if(LiquidUtil.total(liquids) > 0.001f){
                //draw every mixed liquid as a flat fill stacked over the previous one
                LiquidUtil.drawOverlayedFrames(size, x, y, liquidPadding, liquids, liquidCapacity);
            }

            Draw.rect(region, x, y);
        }
    }
}
