package aquarion.world.blocks.distribution;

import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidJunction;

public class ModifiedLiquidJunction extends LiquidJunction {
    public ModifiedLiquidJunction(String name) {
        super(name);
    }
    public class wtfBuild extends LiquidJunctionBuild{
        @Override
        public Building getLiquidDestination(Building source, Liquid liquid){
            if(!enabled) return this;

            int dir = (source.relativeTo(tile.x, tile.y) + 4) % 4;
            Building next = nearby(dir);
            if(next == null || (!next.acceptLiquid(this, liquid) && !(next.block instanceof LiquidJunction))){
                return this;
            }
            if(liquid.temperature > 0.5f){
                damageContinuous(liquid.temperature/100f);
                if(Mathf.chanceDelta(0.01)){
                    Fx.steam.at(x, y);
                }
            }
            return next.getLiquidDestination(this, liquid);

        }
    }
}
