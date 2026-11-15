package aquarion.world.blocks.logic;

import aquarion.world.type.AquaBlock;
import mindustry.gen.Building;

public class BinaryBlock extends AquaBlock {
    public BinaryBlock(String name) {
        super(name);
    }
    public class BinaryBlockBuild extends Building{
        public boolean active = false;
        public float warmup;
        @Override
        public float warmup(){
            return warmup;
        }
    }
}
