package aquarion.content.blocks;

import aquarion.content.AquaUnitTypes;
import aquarion.world.blocks.core.AquaCoreBlock;
import aquarion.world.blocks.defense.ChainsawTurret;
import aquarion.world.blocks.defense.RegenPylon;
import aquarion.world.blocks.defense.deflectorShield;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.BuildTurret;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.Env;

import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaLiquids.fumes;
import static aquarion.content.AquaLiquids.haze;
import static aquarion.planets.AquaPlanets.*;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;


public class CoreBlocks {
    public static Block coreAnnex, buzzSaw, mendPyre, mendPylon, cache, coreCuesta,
            coreEscarpment, corePike, buildCairn, forceBarrier, crate, deflectorWell;

    public static void loadContent() {
        cache = new StorageBlock("cache") {{
            requirements(Category.effect, with(aluminum, 160, silicon, 150, ferricMatter, 300));
            itemCapacity = 900;
            coreMerge = true;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            squareSprite = false;
            size = 3;
            researchCostMultiplier = 0.02f;
            envEnabled |= Env.terrestrial | Env.underwater;
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
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            //drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}}, new DrawDefault());
        }};
        corePike = new AquaCoreBlock("core-pike") {{
            requirements(Category.effect, with(silicon, 1500));
            squareSprite = false;
            health = 2500;
            itemCapacity = 8000;
            size = 4;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            unitCapModifier = 25;
            unitType = AquaUnitTypes.cull;
            alwaysUnlocked = true;
            hasItems = true;
            hasColor = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        coreCuesta = new CoreBlock("core-cuesta") {{
            requirements(Category.effect, with(duralumin, 2500, metaglass, 1200, lead, 5000));
            squareSprite = false;
            itemCapacity = 12000;
            size = 6;
            absorbLasers = true;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            unitCapModifier = 50;
            unitType = AquaUnitTypes.cull;
            hasItems = true;
            hasColor = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        coreEscarpment = new CoreBlock("core-escarpment") {{
            requirements(Category.effect, with(duralumin, 2500, metaglass, 1200, lead, 5000));
            squareSprite = false;
            itemCapacity = 12000;
            size = 6;
            unitCapModifier = 50;
            unitType = AquaUnitTypes.cull;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            category = Category.effect;
            hasItems = true;
            hasColor = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        buildCairn = new BuildTurret("build-cairn") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.effect, with(silicon, 120, copper, 50));
            size = 2;
            outlineRadius = 0;
            buildSpeed = 0.4f;
            range = 145;
            rotateSpeed = 0.85f;
            schematicPriority = 8;
            consumePower(1.5f);
        }};
        mendPyre = new RegenPylon("mend-pyre") {{
            requirements(Category.effect, with(lead, 55, silicon, 80));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            size = 1;
            schematicPriority = 7;
            consumePower(0.75f);
            consumeItem(Items.silicon).boost();
            range = 25;
            phaseRangeBoost = 3f;
            phaseBoost = 2;
            healPercent = 10f;
            squareSprite = false;
            reload = 280;
            alwaysUnlocked = true;
        }};
        mendPylon = new RegenPylon("mend-pylon") {{
            requirements(Category.effect, with(silicon, 60, aluminum, 40));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            size = 2;
            schematicPriority = 7;

            consumePower(0.125f);
            consumeLiquid(fumes, 0.05f);
            range = 35;
            healPercent = 14;
            squareSprite = false;
            reload = 240;
            phaseBoost = 0;
            phaseRangeBoost = 0;
            liquidCapacity = 60;
            researchCostMultiplier = 0;
        }};
        deflectorWell = new deflectorShield("deflector-well") {{
            requirements(Category.effect, with(polymer, 800, metaglass, 900, silicon, 1200));
            size = 4;
            squareSprite = false;
            liquidCapacity = 500;
            sides = 180;
            shieldRotation = 45;
            consumeLiquid(haze, 3.5f);
            consumePower(4);
            radius = 120;
        }};
        buzzSaw = new ChainsawTurret("buzzsaw") {{
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
