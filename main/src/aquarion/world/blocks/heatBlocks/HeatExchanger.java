package aquarion.world.blocks.heatBlocks;

import arc.struct.IntSet;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConsumer;

public class HeatExchanger extends Block {
    public float heatRequirement = 1f;
    public boolean consumeCoolness = false;
    public float heatOutput;
    public HeatExchanger(String name){
        super(name);
        update = true;
        hasItems = false;
        hasLiquids = false;
    }

    public class HeatExchangerBuild extends Building implements HeatBlock {
        public float[] sideHeat = new float[4];

        @Override
        public void updateTile(){
            float heatDelta = heatRequirement * Time.delta;

            for(int i = 0; i < 4; i++){
                Building other = nearby(i);
                if(other instanceof HeatConsumer consumer){
                    float[] targetHeat = consumer.sideHeat();
                    if(consumeCoolness){
                        // Pull heat *from* others
                        targetHeat[(i + 2) % 4] -= heatDelta;
                    }else{
                        // Push heat *to* others
                        targetHeat[(i + 2) % 4] += heatDelta;
                    }
                }
            }
        }


        @Override
        public float calculateHeat(float[] sideHeat, IntSet cameFrom) {
            return 0; // Does not receive heat itself
        }

        @Override
        public float heat() {
            return heatOutput;
        }

        @Override
        public float heatFrac() {
            return 0;
        }
    }

    @Override
    public void init() {
        super.init();
        buildType = HeatExchangerBuild::new;
    }
}