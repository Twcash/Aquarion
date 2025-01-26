package aquarion.blocks;

import aquarion.units.AquaUnitTypes;
import aquarion.world.blocks.core.QeralterCoreBlock;
import aquarion.world.blocks.defense.RegenPylon;
import aquarion.world.blocks.distribution.DistributionCatalyst;
import arc.graphics.Color;
import mindustry.content.UnitTypes;
import mindustry.gen.Unit;
import mindustry.type.Category;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.BuildTurret;
import mindustry.world.blocks.defense.OverdriveProjector;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.fumes;
import static aquarion.AquaLiquids.magma;
import static aquarion.units.AquaUnitTypes.iris;
import static aquarion.units.AquaUnitTypes.mite;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.cryofluid;
import static mindustry.type.ItemStack.with;

public class AquaCore {
    public static Block mendPyre, mendPylon, cache, coreCuesta, coreEscarpment, corePike, coreExpedite, buildSentry, overdriveCatalyst,
    //storing trunte blocks here
    TrunteVein, TrunteNode;

    public static void loadContent(){
        cache = new StorageBlock("cache") {{
            requirements(Category.effect, with(lead, 200, silicon, 150, ferricMatter, 300));
            itemCapacity = 900;
            coreMerge = true;
            squareSprite = false;
            size = 3;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
    }};
        corePike = new CoreBlock("core-pike") {{
            requirements(Category.effect, with(bauxite, 750, lead, 1250, silicon, 2000));
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
            envDisabled = Env.none;
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
            envDisabled = Env.none;
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
            envDisabled = Env.none;
        }};
        coreExpedite = new QeralterCoreBlock("core-expedite"){{
            requirements(Category.effect, with(silicon, 800, silver, 1200, copper, 800, lead, 200));
            squareSprite = false;
            size = 3;
            isFirstTier = true;
            unitType = iris;
            droneType = mite;
            category = Category.effect;
            itemCapacity = 2000;
            buildSpeed = 1.5f;
            rotateSpeed = 1.1f;
            range = 200;
        }};
        buildSentry = new BuildTurret("build-sentry"){{
            requirements(Category.effect, with(electrum, 80, lead, 25));
            size = 2;
            outlineRadius = 0;
            buildSpeed = 0.7f;
            range = 160;
            rotateSpeed = 0.7f;
            consumePower(32/60f);
        }};
        overdriveCatalyst = new DistributionCatalyst("distribution-catalyst"){{
            requirements(Category.effect, with(silver, 220, lead, 90, electrum, 150, silicon, 90, arsenic, 80));
            size = 2;
            range = 225;
            baseColor = Color.valueOf("ffea77");
            phaseColor = Color.valueOf("ffddce");
            phaseRangeBoost = 100;
            consumeLiquid(cryofluid, 36/60f);
            consumePower(256/60f);
        }};
        mendPyre = new RegenPylon("mend-pyre"){{
            requirements(Category.effect, with(lead,40, silicon, 40));
            size = 1;
            consumeLiquid(magma, 5/60f);
            range = 20;
            healPercent = 1.5f;
            squareSprite = false;
            reload = 250;
            liquidCapacity = 120;
            alwaysUnlocked = true;
        }};
        mendPylon = new RegenPylon("mend-pylon"){{
            requirements(Category.effect, with(silicon,60, aluminum, 40));
            size = 2;
            consumeLiquid(fumes, 1.25f/60f);
            range = 30;
            healPercent = 3;
            squareSprite = false;
            reload = 240;
            liquidCapacity = 60;
        }};
    }
}
