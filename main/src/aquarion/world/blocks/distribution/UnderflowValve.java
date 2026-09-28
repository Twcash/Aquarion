package aquarion.world.blocks.distribution;

import aquarion.ui.LiquidBar;
import aquarion.world.content.LiquidUtil;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Nullable;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.blocks.liquid.LiquidJunction;
import mindustry.world.meta.Stat;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class UnderflowValve extends LiquidBlock {
    public boolean invert = false;
    public UnderflowValve(String name) {
        super(name);
        canOverdrive = false;
        solid = false;
        update = true;
        liquidCapacity = 120;
        hasLiquids = true;
        instantTransfer = true;
    }
    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.liquidCapacity);
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("liquid");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region};
    }

    public class UnderflowValveBuild extends LiquidBuild {
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
        public boolean acceptLiquid(Building source, Liquid item){
            Building to = getTileTarget(source, item, false);
            if(to!=null && to instanceof LiquidJunction.LiquidJunctionBuild junct){
                if(junct.getLiquidDestination(source, item) != null){
                    return to.team == team();
                }
            }
            return to != null && to.acceptLiquid(this, item) && to.team == team;
        }

        @Override
        public void handleLiquid(Building source, Liquid item, float amount){
            Building target = getTileTarget(source, item, true);

            if(target != null) target.handleLiquid(source, item, amount);
        }


//        private void forwardLiquid(Building source, Liquid liquid, float amount, Set<Building> visited){
//            if(!visited.add(this)) return;
//
//            Building target = getTileTarget(source, liquid, false);
//
//            if(target != null && target != this && target.team == team && (target instanceof liqUnderBuild u ? u.acceptLiquid(this, liquid) : target.acceptLiquid(this, liquid))){
//                //only forward what the resolved output can actually hold, so nothing is deleted mid-chain
//                float moved = Math.min(amount, Math.max(LiquidUtil.freeSpaceFor(target, this, liquid), 0f));
//                if(moved > 0.0001f){
//                    if(target instanceof liqUnderBuild v){
//                        v.forwardLiquid(this, liquid, moved, visited);
//                    }else{
//                        target.handleLiquid(this, liquid, moved);
//                    }
//                }
//            }
//            if(liquid.temperature > 0.5f){
//                damageContinuous(liquid.temperature / 100f);
//                if(Mathf.chanceDelta(0.01f)){
//                    Fx.steam.at(x, y);
//                }
//            }
//        }


        private @Nullable Building getTileTarget(Building src, Liquid liquid, boolean flip){
            int from = (src.relativeTo(tile.x, tile.y) + 4) % 4;
            if(from == -1) return null;
            Building a = nearby(Mathf.mod(from - 1, 4));
            Building b = nearby(Mathf.mod(from + 1, 4));
            Building next = nearby(from);
            if(a instanceof UnderflowValveBuild) return a;
            if(b instanceof UnderflowValveBuild) return a;
            if(a != null && a.acceptLiquid(this, liquid)) return a;
            if(b != null && b.acceptLiquid(this, liquid)) return a;
            if(next instanceof UnderflowValveBuild) return next;
            if(next != null && acceptLiquid(this, liquid)) return next;
            return null;
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
        }
    }
}
