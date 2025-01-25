package aquarion.blocks;

import aquarion.world.blocks.distribution.SealedConveyor;
import aquarion.world.blocks.distribution.SealedRouter;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.units.UnitCargoLoader;
import mindustry.world.blocks.units.UnitCargoUnloadPoint;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static aquarion.units.AquaUnitTypes.rivulet;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;

public class AquaDistribution {
    // Armored and Sealed Conveyors
    public static Block  electrumSorterInverted, electrumSorter, electrumRouter, electrumDistributor, electrumConveyor, armoredSealedConveyor, sealedOverflow,sealedDistributor,  sealedUnloader, sealedConveyor, sealedRouter, sealedSorter, sealedUnderflow, sealedJunction;
    // heat
    public static Block heaterSource, heaterVoid, heatPipe;
    // Cargo
    public static Block cargoDepot, cargoDock, cargoTerminal;

    // Flare Towers
    public static Block flareTower;

    // Mass Drivers
    public static Block largeMassDriver, smallMassDriver;

    // Manganese Transport
    public static Block manganeseBridge, manganeseConveyor;

    public static void loadContent() {
        sealedConveyor = new SealedConveyor("sealed-conveyor"){{
            requirements(Category.distribution, with(lead, 1));
            envEnabled = 4;
            speed = 2F;
            solid = false;
            visualSpeed = 36;
            underBullets = true;
            stopSpeed = 70;
            researchCostMultiplier = 0.2f;
            buildCostMultiplier = 2f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};

        armoredSealedConveyor = new SealedConveyor("armored-sealed-conveyor"){{
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

        sealedRouter = new SealedRouter("sealed-router"){{
            requirements(category.distribution, with(silicon, 10));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.1f;
            speed = 2;
            hasItems = true;

        }};
        sealedDistributor = new SealedRouter("sealed-distributor"){{
            requirements(category.distribution, with(silicon, 30, ferricMatter, 5));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            size = 2;
            speed = 2;
            hasItems = true;
            solid = true;
            researchCostMultiplier = 0.1f;
        }};
        //debating whether this should even exist
        sealedJunction = new Junction("sealed-junction"){{
            requirements(category.distribution, with(silicon, 15));
            capacity = 8;
            speed = 10;
            hasItems = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            ((SealedConveyor)sealedConveyor).junctionReplacement = this;
            envDisabled = Env.none;
        }};
        sealedUnloader = new DirectionalUnloader("sealed-unloader"){{
            requirements(category.distribution, with(aluminum, 20, silicon, 20));
            speed = 2f;
        }};
        sealedSorter = new Sorter("sealed-sorter"){{
            requirements(category.distribution, with(silicon, 15));
            envEnabled |= Env.terrestrial | Env.underwater;
            researchCostMultiplier = 0.2f;
            envDisabled = Env.none;
            hasItems = true;
        }};

        sealedOverflow = new OverflowGate("sealed-overflow"){{
            requirements(category.distribution, with(silicon, 5));
            invert = false;
            hasItems = true;

            researchCostMultiplier = 0.2f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};

        sealedUnderflow = new OverflowGate("sealed-underflow"){{
            requirements(category.distribution, with(silicon, 5));
            invert = true;
            researchCostMultiplier = 0.2f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            hasItems = true;

        }};

         cargoDock = new UnitCargoLoader("cargo-dock"){{
            requirements(Category.distribution, with(aluminum, 50, silicon, 120, copper, 120));
            size = 2;
            polySides = 0;
            buildTime = 240;
            researchCostMultiplier = 0.2f;
             envEnabled |= Env.terrestrial | Env.underwater;
             envDisabled = Env.none;
            unitType = rivulet;
            itemCapacity = 200;
        }};

         cargoDepot = new UnitCargoUnloadPoint("cargo-depot"){{
            requirements(Category.distribution, with(aluminum,15));
             researchCostMultiplier = 0.2f;
             itemCapacity = 120;
             envEnabled |= Env.terrestrial | Env.underwater;
             envDisabled = Env.none;
            size = 2;
        }};
         electrumRouter = new Router("electrum-router"){{
             requirements(Category.distribution, with(electrum, 6));
         }};
        electrumDistributor = new Router("electrum-distributor"){{
            requirements(Category.distribution, with(electrum, 24, lead, 24));
            size = 2;
        }};
        electrumConveyor = new ArmoredConveyor("electrum-conveyor"){{
            requirements(Category.distribution, with(electrum, 1));
            health = 60;
            speed = 0.042f;
            displayedSpeed = 6.5f;
            buildCostMultiplier = 2f;
            researchCost = with(electrum, 5);
        }};
        electrumSorter = new Sorter("electrum-sorter"){{
            requirements(Category.distribution, with(electrum, 4, lead, 4));
        }};
        electrumSorterInverted = new Sorter("inverted-electrum-sorter"){{
            requirements(Category.distribution, with(electrum, 4, lead, 4));
            invert = true;
        }};
    }

}