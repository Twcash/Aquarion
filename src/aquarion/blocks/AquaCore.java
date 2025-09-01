package aquarion.blocks;

import aquarion.units.AquaUnitTypes;
import aquarion.world.blocks.core.AquaStorageBlock;
import aquarion.world.blocks.core.TantrosCoreBlock;
import aquarion.world.blocks.defense.BlockingForceProjector;
import aquarion.world.blocks.defense.ChainsawTurret;
import aquarion.world.blocks.defense.RegenPylon;

import aquarion.world.graphics.drawers.DrawBetterRegion;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.BuildTurret;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.Env;


import static aquarion.planets.AquaPlanets.*;
import static aquarion.world.graphics.Renderer.Layer.shadow;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;

import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.*;


public class AquaCore {
    public static Block coreAnnex, buzzSaw, mendPyre, mendPylon, cache, coreCuesta,
            coreEscarpment, corePike, buildCairn, forceBarrier, crate;

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
            //drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}}, new DrawDefault());
        }};
        crate = new StorageBlock("crate") {{
            requirements(Category.effect, with(cupronickel, 400, silicon, 1200));
            itemCapacity = 150;
            coreMerge = false;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            squareSprite = false;
            size = 2;
            researchCostMultiplier = 0.02f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            //drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}}, new DrawDefault());
        }};
        corePike = new CoreBlock("core-pike") {{
            requirements(Category.effect, with( silicon, 1500));
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
            buildSpeed = 0.4f;
            range = 145;
            rotateSpeed = 0.85f;
            consumePower(1.5f);
        }};
        mendPyre = new RegenPylon("mend-pyre"){{
            requirements(Category.effect, with(lead,55, silicon, 80));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            size = 1;
            consumePower(0.75f);
            consumeItem(Items.silicon).boost();
            range = 20;
            phaseRangeBoost = 1.25f;
            phaseBoost = 2;
            healPercent = 1.4f;
            squareSprite = false;
            reload = 400;
            alwaysUnlocked = true;
        }};
        mendPylon = new RegenPylon("mend-pylon"){{
            requirements(Category.effect, with(silicon,60, aluminum, 40));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            size = 2;
            consumePower(0.125f);
            consumeLiquid(fumes, 0.05f);
            range = 30;
            healPercent = 3;
            squareSprite = false;
            reload = 240;
            liquidCapacity = 60;
            researchCostMultiplier = 0;
        }};
        buzzSaw = new ChainsawTurret("buzzsaw"){{
            requirements(Category.turret, with(silicon, 250, lead, 300));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            size = 3;
            consumePower(4);
            researchCostMultiplier = 0.02f;
            damage = 15;
            range = 180;
        }};
    }
}
