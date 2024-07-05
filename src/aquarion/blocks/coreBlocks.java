package aquarion.blocks;

import arc.graphics.Color;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.OverdriveProjector;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Env;

import static aquarion.aquarionItems.*;
import static mindustry.content.Items.lead;
import static mindustry.content.Items.metaglass;
import static mindustry.type.ItemStack.with;

public class coreBlocks {
    public static Block cache, coreCuesta, coreEscarpment, corePike, overdriveSubstation, overdriveTerminus;
    
    public static void loadContent(){
        cache = new CoreBlock("cache") {{
            requirements(Category.crafting, with(lead, 120, metaglass, 350, sodium, 90, bauxite, 90));
            itemCapacity = 300;
            coreMerge = false;
            squareSprite = false;
            size = 3;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
    }};


        coreCuesta = new CoreBlock("core-cuesta") {{
            requirements(Category.effect, with(nitride, 3500, duralumin, 2500, metaglass, 1200, lead, 5000));
            squareSprite = false;
            itemCapacity = 12000;
            size = 6;
            unitCapModifier = 50;
            // unitType: cull // Commented out as per your request
            //P.S Norax you do not have to press enter after every resource /:
            category = Category.effect;
            hasItems = true;
            hasColor = true;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
        }};

        coreEscarpment = new CoreBlock("core-escarpment") {{
            requirements(Category.effect, with(nitride, 3500, duralumin, 2500, metaglass, 1200, lead, 5000));
            squareSprite = false;
            itemCapacity = 12000;
            size = 6;
            unitCapModifier = 50;
            // unitType = UnitTypes.cull;
            category = Category.effect;
            hasItems = true;
            hasColor = true;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
        }};

        corePike = new CoreBlock("core-pike") {{
            requirements(Category.effect, with(bauxite, 750, lead, 1250));
            squareSprite = false;
            health = 1500;
            itemCapacity = 7500;
            size = 4;
            unitCapModifier = 25;
            // unitType = UnitTypes.cull;
            alwaysUnlocked = true;
            category = Category.effect;
            hasItems = true;
            hasColor = true;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
        }};
        //TODO overdrive projectors are bad. need to find a way to make it only able to boost defense/crafting related structures
        overdriveSubstation = new OverdriveProjector("overdrive-substation") {{
            requirements(Category.effect, with(manganese, 120, nitride, 250, lead, 450, bauxite, 120));
            reload = 90;
            range = 110;
            useTime = 420;
            category = Category.effect;
            baseColor = Color.valueOf("f6675b");
            phaseColor = Color.valueOf("f6675b");
            // TODO rebalance power consumption for literally everything
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;

            consumePower(1f);
        }};
        overdriveTerminus = new OverdriveProjector("overdrive-terminus") {{
            //TODO actually make the thing
            requirements(Category.effect, with(manganese, 120, nitride, 250, lead, 450, bauxite, 120));
            reload = 90;
            range = 110;
            useTime = 420;
            category = Category.effect;
            baseColor = Color.valueOf("f6675b");
            phaseColor = Color.valueOf("f6675b");
            // TODO rebalance power consumption for literally everything
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;

            consumePower(1f);
        }};


    }
}
