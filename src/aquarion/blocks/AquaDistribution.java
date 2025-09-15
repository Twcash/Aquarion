package aquarion.blocks;

import aquarion.world.blocks.distribution.SealedConveyor;
import aquarion.world.blocks.distribution.SealedRouter;
import mindustry.content.Planets;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.blocks.units.UnitCargoLoader;
import mindustry.world.blocks.units.UnitCargoUnloadPoint;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static aquarion.planets.AquaPlanets.*;
import static aquarion.units.AquaUnitTypes.rivulet;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;

public class AquaDistribution {
    public static Block sealedInvertedSorter, manganeseRail, electrumSorterInverted, electrumSorter, electrumRouter, electrumDistributor,
            electrumConveyor, armoredSealedConveyor, sealedOverflow, sealedDistributor,
            sealedUnloader, sealedConveyor, sealedRouter, sealedSorter,
            sealedUnderflow, sealedJunction, exporter;
    public static Block cargoDepot, cargoDock;

    public static void loadContent() {
        sealedConveyor = new SealedConveyor("sealed-conveyor") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.distribution, with(lead, 1));
            envEnabled = 4;
            speed = 2.1F;
            alwaysUnlocked = true;
            solid = false;
            visualSpeed = 35;
            underBullets = true;
            stopSpeed = 70;
            buildCostMultiplier = 2f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        armoredSealedConveyor = new SealedConveyor("armored-sealed-conveyor") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.distribution, with(aluminum, 2));
            speed = 2;
            health = 75;
            solid = true;
            armored = true;
            visualSpeed = 35;
            underBullets = true;
            stopSpeed = 70;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        sealedRouter = new SealedRouter("sealed-router") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.distribution, with(silicon, 10));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            alwaysUnlocked = true;
            speed = 2.1f;
            hasItems = true;
        }};
        sealedDistributor = new SealedRouter("sealed-distributor") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.distribution, with(silicon, 30, ferricMatter, 5));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            speed = 2.1f;
            size = 2;
            hasItems = true;
            solid = true;
        }};
        sealedJunction = new Junction("sealed-junction") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.distribution, with(silicon, 15));
            capacity = 8;
            speed = 10;
            hasItems = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            ((SealedConveyor) sealedConveyor).junctionReplacement = this;
            envDisabled = Env.none;
        }};
        sealedUnloader = new DirectionalUnloader("sealed-unloader") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.distribution, with(metaglass, 40, silicon, 80));
            speed = 2f;
            allowCoreUnload = true;
        }};
        exporter = new Unloader("exporter") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.distribution, with(polymer, 35, cupronickel, 90));
            speed = 0.9f;
        }};
        sealedSorter = new Sorter("sealed-sorter") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.distribution, with(silicon, 15));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            hasItems = true;
            alwaysUnlocked = true;

        }};
        sealedInvertedSorter = new Sorter("sealed-inverted-sorter") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.distribution, with(silicon, 15));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            hasItems = true;
            alwaysUnlocked = true;
            invert = true;
        }};
        sealedOverflow = new OverflowGate("sealed-overflow") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.distribution, with(silicon, 5));
            invert = false;
            hasItems = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;

        }};
        sealedUnderflow = new OverflowGate("sealed-underflow") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.distribution, with(silicon, 5));
            invert = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            hasItems = true;

        }};
        cargoDock = new UnitCargoLoader("cargo-dock") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.distribution, with(aluminum, 50, silicon, 120, copper, 120));
            size = 2;
            polySides = 0;
            buildTime = 240;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            unitType = rivulet;
            itemCapacity = 200;
        }};

        cargoDepot = new UnitCargoUnloadPoint("cargo-depot") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.distribution, with(aluminum, 15));
            itemCapacity = 120;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            size = 2;
        }};

        electrumRouter = new Router("electrum-router") {{
            requirements(Category.distribution, with(electrum, 6));
        }};
        electrumDistributor = new Router("electrum-distributor") {{
            requirements(Category.distribution, with(electrum, 24, lead, 24));
            size = 2;
        }};
        electrumConveyor = new ArmoredConveyor("electrum-conveyor") {{
            requirements(Category.distribution, with(electrum, 1));
            health = 60;
            speed = 0.042f;
            displayedSpeed = 6.5f;
            buildCostMultiplier = 2f;
            researchCost = with(electrum, 5);
        }};
        electrumSorter = new Sorter("electrum-sorter") {{
            requirements(Category.distribution, with(electrum, 4, lead, 4));
        }};
        electrumSorterInverted = new Sorter("inverted-electrum-sorter") {{
            requirements(Category.distribution, with(electrum, 4, lead, 4));
            invert = true;
        }};
        manganeseRail = new StackConveyor("manganese-rail") {{
            requirements(Category.distribution, with(manganese, 3));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            speed = 2f / 60f;
            itemCapacity = 30;
            drawDisabled = false;
            outputRouter = true;
            hasPower = true;
            consumesPower = true;
            conductivePower = true;
            underBullets = true;
            baseEfficiency = 1f;
            consumePower(1f / 60f);
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
    }
}
