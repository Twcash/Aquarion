package aquarion.world.blocks.production;

import aquarion.world.type.AquaGenericCrafter;
import arc.struct.EnumSet;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.type.Liquid;
import mindustry.world.meta.BlockFlag;
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
