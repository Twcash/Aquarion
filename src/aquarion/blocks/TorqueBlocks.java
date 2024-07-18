package aquarion.blocks;

import aquarion.world.blocks.rotPower.TorqueProducer;
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
        torqueSource = new TorqueProducer("torque-source"){{
            buildVisibility = BuildVisibility.sandboxOnly;
            size = 1;
            torqueOutput = 30000;
            warmupRate = 1000f;
            ambientSound = Sounds.none;
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
