package aquarion.blocks;

import aquarion.world.blocks.distribution.SealedConveyor;
import aquarion.world.blocks.distribution.SealedRouter;
import mindustry.ai.types.CargoAI;
import mindustry.gen.UnitEntity;
import mindustry.type.Category;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.units.UnitCargoLoader;
import mindustry.world.blocks.units.UnitCargoUnloadPoint;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static aquarion.units.AquaUnitTypes.rivulet;
import static mindustry.content.Items.lead;
import static mindustry.type.ItemStack.with;

public class AquaDistribution {
    // Armored and Sealed Conveyors
    public static Block armoredSealedConveyor, sealedOverflow, sealedUnloader, sealedConveyor, sealedRouter, sealedSorter, sealedUnderflow, sealedJunction;

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
            requirements(Category.distribution, with(lead, 1, bauxite,1));
            envEnabled = 4;
            speed = 8F;
            solid = false;
            visualSpeed = 10;
            underBullets = true;
            stopSpeed = 70;
            researchCostMultiplier = 0.2f;
            buildCostMultiplier = 2f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};

        armoredSealedConveyor = new SealedConveyor("armored-sealed-conveyor"){{
            requirements(Category.distribution, with(lead, 2,bauxite, 1, sodium,1));
            speed = 8F;
            health = 75;
            solid = true;
            armored = true;
            visualSpeed = 10;
            underBullets = true;
            stopSpeed = 70;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};

        sealedRouter = new SealedRouter("sealed-router"){{
            requirements(category.distribution, with(lead, 10, bauxite, 5));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.1f;
        }};
        //debating whether this should even exist
        sealedJunction = new Junction("sealed-junction"){{
            requirements(category.distribution, with(lead, 15, bauxite, 10, nickel, 5));
            capacity = 8;
            speed = 48;
            envEnabled |= Env.terrestrial | Env.underwater;
            ((SealedConveyor)sealedConveyor).junctionReplacement = this;
            envDisabled = Env.none;
        }};

        manganeseBridge = new BufferedItemBridge("manganese-bridge"){{
            requirements(category.distribution, with(gallium, 35, manganese, 15));
            consumePower(0.083f);
            range = 5;
            speed = 90;
            fadeIn = moveArrows = pulse = true;
            arrowSpacing = 4;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        
        manganeseConveyor = new SealedConveyor("manganese-conveyor"){{
            //Since `manganeseBridge instanceof DuctBridge` is false this doesn't work
            //bridgeReplacement = manganeseBridge;
            requirements(category.distribution, with(manganese, 1, gallium, 1, bauxite, 2));
            speed = 4f;
            stopSpeed = 45;
            visualSpeed = 20;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};

        sealedSorter = new Sorter("sealed-sorter"){{
            requirements(category.distribution, with(lead, 5));
            envEnabled |= Env.terrestrial | Env.underwater;
            researchCostMultiplier = 0.2f;
            envDisabled = Env.none;
        }};

        sealedOverflow = new OverflowGate("sealed-overflow"){{
            requirements(category.distribution, with(lead, 5));
            invert = false;
            researchCostMultiplier = 0.2f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};

        sealedUnderflow = new OverflowGate("sealed-underflow"){{
            requirements(category.distribution, with(lead, 5));
            invert = true;
            researchCostMultiplier = 0.2f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};

         cargoDock = new UnitCargoLoader("cargo-dock"){{
            requirements(Category.distribution, with(lead,40 ,bauxite, 5));
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
            requirements(Category.distribution, with(lead,15));
             researchCostMultiplier = 0.2f;
             itemCapacity = 120;
             envEnabled |= Env.terrestrial | Env.underwater;
             envDisabled = Env.none;
            size = 2;
        }};
    }

}