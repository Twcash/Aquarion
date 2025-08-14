package aquarion.world.blocks.production;

import aquarion.world.type.AquaGenericCrafter;
import mindustry.world.blocks.production.GenericCrafter;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.meta.*;
//same case as MagmaHarvester it fucking sucks
public class MagmaProcessor extends AquaGenericCrafter {

    public MagmaProcessor(String name){
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        ambientSound = Sounds.machine;
        sync = true;
        ambientSoundVolume = 0.03f;
        flags = EnumSet.of(BlockFlag.factory);
        drawArrow = false;
    }
    public class MagmaProcessorBuild extends AquaGenericCrafterBuild {
        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if (block instanceof MagmaHarvester) {
                return false;
            }
            return block.hasLiquids && block.consumesLiquid(liquid);
        }
    }
}
