package aquarion.world.blocks.distribution;

import aquarion.ui.LiquidBar;
import aquarion.world.content.LiquidUtil;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.meta.Stat;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class LiquidUnderflow extends LiquidBlock {
    public boolean invert = false;
    public LiquidUnderflow(String name) {
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

    public class liqUnderBuild extends LiquidBuild {
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
        public boolean acceptLiquid(Building source, Liquid liquid){
            return acceptLiquid(source, liquid, newVisited());
        }

        private boolean acceptLiquid(Building source, Liquid liquid, Set<Building> visited){
            if(!visited.add(this)) return false;
            return accepts(getTileTarget(source, liquid, visited), liquid, visited);
        }

        /** Whether {@code to} can actually take liquid from this underflow right now, resolving through other underflows. */
        private boolean accepts(Building to, Liquid liquid, Set<Building> visited){
            if(to == null || to == this || to.team != team) return false;
            if(to instanceof liqUnderBuild u) return u.acceptLiquid(this, liquid, visited);
            return to.acceptLiquid(this, liquid) && LiquidUtil.freeSpaceFor(to, this) > 0.01f;
        }

        @Override
        public Building getLiquidDestination(Building from, Liquid liquid){
            //route straight through to the resolved target, so the underflow is never used as a passive buffer
            return resolveDest(from, liquid, newVisited());
        }

        /** Resolves the destination through any chain of underflows, cutting off when a loop is detected. */
        private Building resolveDest(Building from, Liquid liquid, Set<Building> visited){
            if(!visited.add(this)) return this;
            Building to = getTileTarget(from, liquid, visited);
            return to != null ? to : this;
        }

        private static Set<Building> newVisited(){
            return Collections.newSetFromMap(new IdentityHashMap<>());
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount){
            forwardLiquid(source, liquid, amount, newVisited());
        }

        private void forwardLiquid(Building source, Liquid liquid, float amount, Set<Building> visited){
            if(!visited.add(this)) return;

            Building target = getTileTarget(source, liquid, visited);

            if(target != null && target != this && (target instanceof liqUnderBuild u ? u.acceptLiquid(this, liquid, visited) : target.acceptLiquid(this, liquid))){
                if(target instanceof liqUnderBuild v){
                    v.forwardLiquid(this, liquid, Math.min(amount, LiquidUtil.freeSpace(target)), visited);
                }else{
                    target.handleLiquid(this, liquid, Math.min(amount, LiquidUtil.freeSpace(target)));
                }
            }
            if(liquid.temperature > 0.5f){
                damageContinuous(liquid.temperature / 100f);
                if(Mathf.chanceDelta(0.01f)){
                    Fx.steam.at(x, y);
                }
            }
        }

        public Building getTileTarget(Building source, Liquid liquid){
            return getTileTarget(source, liquid, newVisited());
        }

        private Building getTileTarget(Building source, Liquid liquid, Set<Building> visited){
            if(!enabled) return null;

            int from = relativeToEdge(source.tile);
            if(from == -1) return null;

            Building to = nearby((from + 2) % 4);
            Building resolvedFar = (to instanceof liqUnderBuild v) ? v.resolveDest(this, liquid, visited) : to;
            boolean fromInst = source.block.instantTransfer;

            boolean canForward = resolvedFar != null && !(fromInst && to != null && to.block.instantTransfer) && accepts(resolvedFar, liquid, visited);

            if(!canForward || invert){
                Building a = nearby(Mathf.mod(from - 1, 4));
                Building b = nearby(Mathf.mod(from + 1, 4));

                // resolve A
                Building resolvedA = (a instanceof liqUnderBuild va) ? va.resolveDest(this, liquid, visited) : a;

                // resolve B
                Building resolvedB = (b instanceof liqUnderBuild vb) ? vb.resolveDest(this, liquid, visited) : b;

                boolean ac = accepts(resolvedA, liquid, visited);
                boolean bc = accepts(resolvedB, liquid, visited);

                if(!ac && !bc){
                    return resolveJunction(invert && canForward ? resolvedFar : null, this);
                }

                if(ac && !bc){
                    to = resolvedA;
                }else if(!ac){
                    to = resolvedB;
                }else{
                    to = (rotation & (1 << from)) == 0 ? resolvedA : resolvedB;
                }
            }

            if(to instanceof liqUnderBuild v){
                return resolveJunction(v.resolveDest(this, liquid, visited), this);
            }

            return resolveJunction(to, this);
        }

        private Building resolveJunction(Building build, Building from) {
            //junctions are buffering endpoints with their own per-side capacity, so liquid is never routed through them
            return build;
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
        }
    }
}
