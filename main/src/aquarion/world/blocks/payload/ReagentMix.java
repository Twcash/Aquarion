package aquarion.world.blocks.payload;

import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class ReagentMix extends Block {
    public Seq<ItemStack> containing = new Seq<>();
    public ReagentMix(String name) {
        super(name);
    }
    public class ReagentMixBuild extends Building{

    }
}
