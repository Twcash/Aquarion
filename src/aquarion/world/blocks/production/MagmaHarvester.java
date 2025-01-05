package aquarion.world.blocks.production;

import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.production.AttributeCrafter;
//useless and doesnt even work
public class MagmaHarvester extends AttributeCrafter {
    public MagmaHarvester(String name) {
        super(name);
    }
    public class MagmaProcessorBuild extends AttributeCrafterBuild {
        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            if(block instanceof MagmaProcessor){
                return false;
            }
            return block.hasLiquids && block.consumesLiquid(liquid);
        }
    }
}
