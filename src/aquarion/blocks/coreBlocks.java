package aquarion.blocks;

import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Env;

import static aquarion.aquarionItems.*;
import static mindustry.content.Items.lead;
import static mindustry.content.Items.metaglass;
import static mindustry.type.ItemStack.with;

public class coreBlocks {
    public static Block cache, coreCuesta, coreEscarpment, corePike, overdriveSubstation;

    public static void loadContent(){
        cache = new CoreBlock("cache") {{
            itemCapacity = 250;
            coreMerge = false;
            squareSprite = false;
            health = 180;
            size = 3;
            requirements(Category.crafting, with(lead, 200, metaglass, 500, sodium, 120));
    }};


        coreCuesta = new CoreBlock("core-cuesta") {{
            squareSprite = false;
            itemCapacity = 12000;
            size = 6;
            unitCapModifier = 50;
            // unitType: cull // Commented out as per your request
            requirements(Category.effect, with(
                    nitride, 3500,
                    duralumin, 2500,
                    metaglass, 1200,
                    lead, 5000
            ));
            category = Category.effect;
            hasItems = true;
            hasColor = true;
            envEnabled = Env.space | Env.terrestrial; //| Env.aquatic | Env.eruptable;
        }};

        coreEscarpment = new CoreBlock("core-escarpment") {{
            description = "The core of the base. Small, once destroyed the sector is lost.";
            squareSprite = false;
            itemCapacity = 12000;
            size = 6;
            unitCapModifier = 50;
            // unitType = UnitTypes.cull;
            requirements(Category.effect, with(
                    nitride, 3500,
                    duralumin, 2500,
                    metaglass, 1200,
                    lead, 5000
            ));
            category = Category.effect;
            hasItems = true;
            hasColor = true;
            envEnabled = Env.space | Env.terrestrial; // | Env.aquatic | Env.eruptable;
        }};

        corePike = new CoreBlock("core-pike") {{
            description = "The core of the base. Small, once destroyed the sector is lost.";
            squareSprite = false;
            health = 1500;
            itemCapacity = 7500;
            size = 4;
            unitCapModifier = 25;
            // unitType = UnitTypes.cull;
            alwaysUnlocked = true;
            requirements(Category.effect, with(
                    bauxite, 750,
                    lead, 1250
            ));
            category = Category.effect;
            hasItems = true;
            hasColor = true;
            envEnabled = Env.space | Env.terrestrial; // | Env.aquatic | Env.eruptable;
        }};


    }
}
