package aquarion.world.blocks.distribution;

import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidRouter;

public class ModifiedLiquidRouter extends LiquidRouter {
    public ModifiedLiquidRouter(String name) {
        super(name);
    }
    public class ughBuild extends LiquidRouterBuild{
        @Override
        public void updateTile(){
            Liquid liquid = liquids.current();
            if(liquids.currentAmount() > 0.01f){
                dumpLiquid(liquids.current());
            }
            if(liquids.currentAmount() > 0.1f && liquid.temperature > 0.5f){
                damageContinuous(liquid.temperature/100f);
                if(Mathf.chanceDelta(0.01)){
                    Fx.steam.at(x, y);
                }
            }
        }

    }
}
