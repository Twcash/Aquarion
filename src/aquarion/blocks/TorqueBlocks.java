package aquarion.blocks;

import aquarion.world.blocks.ConsumeRT;
import aquarion.world.blocks.rotPower.RTGenericCrafter;
import aquarion.world.blocks.rotPower.TorqueShaft;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.bauxite;
import static mindustry.type.ItemStack.with;

public class TorqueBlocks {
    public static Block torqueSource, torqueShaft;

    public static void loadContent() {
        torqueSource = new RTGenericCrafter("torque-source"){{
            requirements(Category.crafting, BuildVisibility.sandboxOnly, with());
            size = 1;
            output = 10;
            ambientSound = Sounds.none;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
        }};
        torqueShaft = new TorqueShaft("torque-shaft"){{
            requirements(Category.crafting, with(bauxite, 2));
            size = 1;
            solid = true;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
        }};
    }
}
