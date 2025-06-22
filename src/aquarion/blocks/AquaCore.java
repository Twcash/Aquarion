package aquarion.blocks;

import aquarion.units.AquaUnitTypes;
import aquarion.world.blocks.core.QeralterCoreBlock;
import aquarion.world.blocks.core.SnakeStorageBlock;
import aquarion.world.blocks.core.TantrosCoreBlock;
import aquarion.world.blocks.defense.BlockingForceProjector;
import aquarion.world.blocks.defense.ChainsawTurret;
import aquarion.world.blocks.defense.RegenPylon;
import aquarion.world.blocks.distribution.DistributionCatalyst;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.core.GameState;
import mindustry.core.World;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.BuildTurret;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;


import static aquarion.planets.AquaPlanets.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.cryofluid;
import static mindustry.type.ItemStack.with;

import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.*;
import static aquarion.units.AquaUnitTypes.*;


public class AquaCore {
    public static Block coreAnnex, buzzSaw, mendPyre, mendPylon, cache, coreCuesta,
            coreEscarpment, corePike, coreExpedite, buildCairn,
            overdriveCatalyst, forceBarrier;

    public static void loadContent(){
        cache = new StorageBlock("cache") {{
            requirements(Category.effect, with(aluminum, 160, silicon, 150, ferricMatter, 300));
            itemCapacity = 900;
            coreMerge = true;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            squareSprite = false;
            size = 3;
            researchCostMultiplier = 0.02f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        corePike = new TantrosCoreBlock("core-pike") {{
            requirements(Category.effect, with( silicon, 2000));
            squareSprite = false;
            health = 1500;
            itemCapacity = 8000;
            size = 4;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            unitCapModifier = 25;
            unitType = AquaUnitTypes.cull;
            alwaysUnlocked = true;
            hasItems = true;
            hasColor = true;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        coreCuesta = new CoreBlock("core-cuesta") {{
            requirements(Category.effect, with( duralumin, 2500, metaglass, 1200, lead, 5000));
            squareSprite = false;
            itemCapacity = 12000;
            size = 6;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            unitCapModifier = 50;
            unitType = AquaUnitTypes.cull;
            hasItems = true;
            hasColor = true;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        coreEscarpment = new CoreBlock("core-escarpment") {{
            requirements(Category.effect, with( duralumin, 2500, metaglass, 1200, lead, 5000));
            squareSprite = false;
            itemCapacity = 12000;
            size = 6;
            unitCapModifier = 50;
            unitType = AquaUnitTypes.cull;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            category = Category.effect;
            hasItems = true;
            hasColor = true;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        buildCairn = new BuildTurret("build-cairn"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.effect, with(silicon, 120, copper, 50));
            size = 2;
            outlineRadius = 0;
            buildSpeed = 0.2f;
            range = 120;
            rotateSpeed = 0.7f;
            consumePower(150/60f);
        }};
        mendPyre = new RegenPylon("mend-pyre"){{
            requirements(Category.effect, with(lead,40, silicon, 40));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            size = 1;
            consumeLiquid(magma, 10/60f);
            range = 20;
            healPercent = 1.6f;
            squareSprite = false;
            reload = 400;
            liquidCapacity = 120;
            alwaysUnlocked = true;
        }};
        mendPylon = new RegenPylon("mend-pylon"){{
            requirements(Category.effect, with(silicon,60, aluminum, 40));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            size = 2;
            consumePower(5/60f);
            consumeLiquid(fumes, 1.25f/60f);
            range = 30;
            healPercent = 3;
            squareSprite = false;
            reload = 240;
            liquidCapacity = 60;
            researchCostMultiplier = 0;
        }};
        forceBarrier = new BlockingForceProjector("force-barrier"){{
            requirements(Category.defense, ItemStack.with(lead,80, duralumin, 120, metaglass, 90));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            consumePower(3f / 60f);
            sides = 18;
            radius = 120;
            shieldRotation = 0;
            outputsPower = false;
            hasPower = true;
            consumesPower = true;
            conductivePower = true;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
            shieldHealth = 660;
            size = 5;
        }};
        buzzSaw = new ChainsawTurret("buzzsaw"){{
            requirements(Category.turret, with(silicon, 250, lead, 300));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            size = 3;
            consumePower(400/60f);
            researchCostMultiplier = 0.02f;
            damage = 15;
            range = 180;
        }};
    }
}
