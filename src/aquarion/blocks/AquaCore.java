package aquarion.blocks;

import aquarion.units.AquaUnitTypes;
import arc.graphics.Color;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.OverdriveProjector;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static mindustry.content.Items.lead;
import static mindustry.content.Items.metaglass;
import static mindustry.type.ItemStack.with;

public class AquaCore {
    public static Block cache, coreCuesta, coreEscarpment, corePike;

    public static void loadContent(){
        //TODO remove?
        cache = new StorageBlock("cache") {{
            requirements(Category.effect, with(lead, 120, metaglass, 350, sodium, 90, bauxite, 90));
            itemCapacity = 300;
            coreMerge = false;
            squareSprite = false;
            size = 3;
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
            unitType = AquaUnitTypes.cull;
            alwaysUnlocked = true;
            category = Category.effect;
            hasItems = true;
            hasColor = true;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
        }};

        coreCuesta = new CoreBlock("core-cuesta") {{
            requirements(Category.effect, with(nitride, 3500, duralumin, 2500, metaglass, 1200, lead, 5000));
            squareSprite = false;
            itemCapacity = 12000;
            size = 5;
            unitCapModifier = 50;
            unitType = AquaUnitTypes.cull;
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
            unitType = AquaUnitTypes.cull;
            category = Category.effect;
            hasItems = true;
            hasColor = true;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
        }};
    }
}
