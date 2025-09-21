package aquarion.world.blocks.heatBlocks;

import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConductor;
import mindustry.world.blocks.heat.HeatConsumer;

public class HotHeatConductor extends HeatConductor {
    public int meltThresh = 120;
    public HotHeatConductor(String name) {
        super(name);
    }

    public class HotHeatConductorBuild extends HeatConductorBuild implements HeatBlock, HeatConsumer {
        public void updateTile() {
            this.updateHeat();
            if(heat > meltThresh){
                damageContinuous(heat / 1000f);
                if (Mathf.chanceDelta(0.01)) {
                    Fx.steam.at(x + Mathf.range(-this.block.size/2f ,this.block.size/2f), y + Mathf.range(-this.block.size/2f ,this.block.size/2f));
                }
            }
        }
    }
}
