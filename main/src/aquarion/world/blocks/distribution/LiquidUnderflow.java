package aquarion.world.blocks.distribution;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.blocks.liquid.LiquidJunction;
import mindustry.world.meta.Stat;

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
        public boolean acceptLiquid(Building source, Liquid liquid){
            Building to = getTileTarget(source, liquid);

            return to != null && to.team == team && (
                    to instanceof ModifiedLiquidJunction.wtfBuild ||
                            to instanceof liqUnderBuild ||
                            (to.acceptLiquid(this, liquid) && to.liquids.get(liquid) < to.block.liquidCapacity)
            );
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount){
            Building target = getTileTarget(source, liquid);

            if(target != null) target.handleLiquid(this, liquid, amount);
            if(liquid.temperature > 0.5f){
                damageContinuous(liquid.temperature / 100f);
                if(Mathf.chanceDelta(0.01f)){
                    Fx.steam.at(x, y);
                }
            }
        }

        public Building getTileTarget(Building source, Liquid liquid){
            if(!enabled) return null;

            int from = relativeToEdge(source.tile);
            if(from == -1) return null;

            Building to = nearby((from + 2) % 4);
            boolean fromInst = source.block.instantTransfer;

            boolean canForward = to != null && to.team == team && !(fromInst && to.block.instantTransfer) && (
                    to instanceof ModifiedLiquidJunction.wtfBuild ||
                            to instanceof liqUnderBuild ||
                            (to.acceptLiquid(this, liquid) && to.liquids.get(liquid) < to.block.liquidCapacity)
            );

            if(!canForward || invert){
                Building a = nearby(Mathf.mod(from - 1, 4));
                Building b = nearby(Mathf.mod(from + 1, 4));

                // resolve A
                Building resolvedA = (a instanceof liqUnderBuild va) ? va.getLiquidDestination(this, liquid) : a;
                resolvedA = resolveJunction(resolvedA, this);

                // resolve B
                Building resolvedB = (b instanceof liqUnderBuild vb) ? vb.getLiquidDestination(this, liquid) : b;
                resolvedB = resolveJunction(resolvedB, this);

                boolean ac = resolvedA != null && resolvedA.team == team && (
                        resolvedA instanceof ModifiedLiquidJunction.wtfBuild ||
                                resolvedA instanceof liqUnderBuild ||
                                (resolvedA.acceptLiquid(this, liquid) && resolvedA.liquids.get(liquid) < resolvedA.block.liquidCapacity)
                );

                boolean bc = resolvedB != null && resolvedB.team == team && (
                        resolvedB instanceof ModifiedLiquidJunction.wtfBuild ||
                                resolvedB instanceof liqUnderBuild ||
                                (resolvedB.acceptLiquid(this, liquid) && resolvedB.liquids.get(liquid) < resolvedB.block.liquidCapacity)
                );

                if(!ac && !bc){
                    return resolveJunction(invert && canForward ? to : null, this);
                }

                if(ac && !bc){
                    to = resolvedA;
                }else if(bc && !ac){
                    to = resolvedB;
                }else{
                    to = (rotation & (1 << from)) == 0 ? resolvedA : resolvedB;
                }
            }

            if(to instanceof liqUnderBuild v){
                return resolveJunction(v.getLiquidDestination(this, liquid), this);
            }

            return resolveJunction(to, this);
        }

        private Building resolveJunction(Building build, Building from) {
            while(build != null && build.block instanceof LiquidJunction){
                int rel = build.relativeTo(from.tile.x, from.tile.y);
                Building next = build.nearby((rel + 2) % 4);

                if(next == from) break; // avoid infinite loop
                from = build;
                build = next;
            }
            return build;
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
        }
    }
}
