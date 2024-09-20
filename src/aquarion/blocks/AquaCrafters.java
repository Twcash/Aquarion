package aquarion.blocks;

import aquarion.AquaAttributes;
import aquarion.AquaItems;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.production.BurstDrill;
import mindustry.world.blocks.production.WallCrafter;
import mindustry.world.meta.Env;

import static mindustry.gen.Sounds.drill;
import static mindustry.type.ItemStack.with;

public class AquaCrafters {
    public static Block bauxiteHarvester, CompressionDrill, ramDrill;
    public static void loadContent(){
    bauxiteHarvester = new WallCrafter("bauxite-harvester"){{
    size = 4;
    requirements(Category.production, with(Items.lead, 30, AquaItems.bauxite, 45));
    researchCost = with(Items.lead, 15, AquaItems.bauxite, 10);
    drillTime = 95;
    attribute = AquaAttributes.bauxite;
    output = AquaItems.bauxite;
    ambientSound = drill;
    ambientSoundVolume = 0.04F;
    envEnabled|= Env.terrestrial | Env.underwater;
    envDisabled|= Env.spores | Env.scorching;
    squareSprite = false;
    consumePower(1.5f);
    }};
    CompressionDrill = new BurstDrill("compression-drill"){{
    requirements(Category.production, with(Items.lead, 10, AquaItems.bauxite, 25));
    drillTime = 350;
    size = 2;
    tier = 2;
    arrows = 2;
    updateEffectChance = 0.5f;
    drillEffect = Fx.mineBig;
    updateEffect = Fx.drillSteam;
    arrowSpacing = 2;
    shake = 0.5f;
    envEnabled|= Env.terrestrial | Env.underwater;
    envDisabled|= Env.spores | Env.scorching;
        ambientSound = Sounds.conveyor;
        ambientSoundVolume = 0.07f;
    }};
        ramDrill = new BurstDrill("ram-drill"){{
            requirements(Category.production, with(Items.lead, 80, AquaItems.bauxite, 65, Items.metaglass, 45));
            drillTime = 250;
            size = 3;
            tier = 4;
            itemCapacity = 30;
            arrows = 1;
            updateEffectChance = 0.05f;
            drillEffect = Fx.mineHuge;
            updateEffect = Fx.pulverizeRed;
            hardnessDrillMultiplier = 3.5f;
            arrowSpacing = 0;
            shake = 0.7f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
            ambientSound = Sounds.drillCharge;
            ambientSoundVolume = 0.07f;
            consumePower(0.875f);
        }};
    }
}