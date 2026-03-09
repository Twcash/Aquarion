package aquarion.world.blocks.distribution;

import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidRouter;

import static mindustry.Vars.state;

public class ModifiedLiquidRouter extends LiquidRouter {
    public boolean willMelt = false;

    public ModifiedLiquidRouter(String name) {
        super(name);
    }

    public class ughBuild extends LiquidRouterBuild {


        @Override
        public void updateTile() {
            super.updateTile();
            if (liquids.currentAmount() < 0.01f) return;
            Liquid liquid = liquids.current();
            if (liquids.currentAmount() > 0.1f && liquid.temperature > 0.5f && !willMelt) {
                damageContinuous(liquid.temperature / 100f);
                if (Mathf.chanceDelta(0.01f)) {
                    Fx.steam.at(x, y);
                }
            }
        }
    }
}