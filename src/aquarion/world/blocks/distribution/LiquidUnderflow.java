package aquarion.world.blocks.distribution;

import aquarion.world.Uti.DirectionalLiquidBuffer;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.io.Reads;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.DirectionalItemBuffer;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.blocks.liquid.LiquidJunction;

public class LiquidUnderflow extends LiquidBlock {
    public boolean invert = false;
    public LiquidUnderflow(String name) {
        super(name);
        canOverdrive = false;
        solid = false;
        update = true;
        liquidCapacity = 120;
    }
    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region};
    }

    public class liqUnderBuild extends LiquidBuild{
        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return (liquids.current() == liquid || liquids.currentAmount() < 0.2f);
        }

        public @Nullable Building getLiquidDestination(Building source, Liquid liquid){
            if(!enabled) return null;
            if(liquid.temperature > 0.5f){
                damageContinuous(liquid.temperature/100f);
                if(Mathf.chanceDelta(0.01)){
                    Fx.steam.at(x, y);
                }
            }
            int from = relativeToEdge(source.tile);
            if(from == -1) return null;

            Building forward = nearby((from + 2) % 4);
            Building left    = nearby(Mathf.mod(from - 1, 4));
            Building right   = nearby(Mathf.mod(from + 1, 4));

            boolean inv = invert;

            boolean canForward = forward != null && forward.team == team &&
                    forward.acceptLiquid(this, liquid)&& forward.liquids.get(liquid) < forward.block.liquidCapacity-1;

            boolean canLeft = left != null && left.team == team &&
                    left.acceptLiquid(this, liquid) && left.liquids.get(liquid) < left.block.liquidCapacity-1;

            boolean canRight = right != null && right.team == team &&
                    right.acceptLiquid(this, liquid) && right.liquids.get(liquid) < right.block.liquidCapacity-1;
            if(!inv && canForward) return forward;

            if(canLeft && !canRight) return left;
            if(canRight && !canLeft) return right;

            if(canLeft && canRight){
                Building to = (rotation & (1 << from)) == 0 ? left : right;
                if(invert) rotation ^= (1 << from);
                return to;
            }

            if(canForward) return forward;

            return this;
        }


        @Override
        public void draw(){
            Draw.rect(region, x, y);
        }
    }
}