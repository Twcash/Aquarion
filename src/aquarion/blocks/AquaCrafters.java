package aquarion.blocks;

import aquarion.AquaAttributes;
import aquarion.AquaItems;
import aquarion.world.blocks.rotPower.RTWallCrafter;
import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.production.WallCrafter;
import mindustry.world.meta.Env;

import static mindustry.gen.Sounds.drill;
import static mindustry.type.ItemStack.with;

public class AquaCrafters {
    public static Block bauxiteHarvester;
    public static void loadContent(){
    bauxiteHarvester = new RTWallCrafter("bauxite-harvester"){{
    size = 4;
    requirements(Category.production, with(Items.lead, 30, AquaItems.bauxite, 65));
    researchCost = with(Items.lead, 15, AquaItems.bauxite, 10);
    drillTime = 95;
    attribute = AquaAttributes.bauxite;
    output = AquaItems.bauxite;
    ambientSound = drill;
    ambientSoundVolume = 0.04F;
    envEnabled|= Env.terrestrial | Env.underwater;
    envDisabled|= Env.spores | Env.scorching;
    }};
    }
}