package aquarion.blocks;

import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.units.UnitCargoLoader;
import mindustry.world.blocks.units.UnitCargoUnloadPoint;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static mindustry.content.Items.lead;
import static mindustry.type.ItemStack.with;

public class AquaDistribution {
    // Armored and Sealed Conveyors
    public static Block armoredSealedConveyor, sealedOverflow, sealedUnloader, sealedConveyor, sealedRouter, sealedSorter, sealedUnderflow;

    // Cargo
    public static Block cargoDepot, cargoDock, cargoTerminal;

    // Flare Towers
    public static Block flareTower;

    // Mass Drivers
    public static Block largeMassDriver, smallMassDriver;

    // Manganese Transport
    public static Block manganeseBridge, manganeseConveyor;

    public static void loadContent() {
        sealedConveyor = new Duct("sealedconveyor"){{
            requirements(Category.distribution, with(lead, 3, sodium,2));
            envEnabled = 4;
            speed = 10F;
            buildCostMultiplier = 2f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled |= Env.spores | Env.scorching;
        }};

        armoredSealedConveyor = new Duct("armored-sealed-conveyor"){{
            requirements(Category.distribution, with(lead, 3, sodium,2));
            speed = 10F;
            solid = true;
            underBullets = false;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled |= Env.spores | Env.scorching;
        }};
        manganeseConveyor = new Conveyor("manganese-conveyor"){{
            requirements(category.distribution, with(manganese, 1, gallium, 1));
            displayedSpeed = 15.5f;
            speed = 0.12f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled |= Env.spores | Env.scorching;
        }};
        manganeseBridge = new BufferedItemBridge("manganese-bridge"){{
            requirements(category.distribution, with(gallium, 35, manganese, 15));
            consumePower(0.083f);
            range = 5;
            speed = 90;
            fadeIn = moveArrows = pulse = true;
            arrowSpacing = 4;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled |= Env.spores | Env.scorching;
        }};

        sealedSorter = new Sorter("sealedsorter"){{
            requirements(category.distribution, with(lead, 5));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled |= Env.spores | Env.scorching;
        }};
        sealedOverflow = new OverflowGate("sealed-overflow"){{
            requirements(category.distribution, with(lead, 5));
            invert = false;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled |= Env.spores | Env.scorching;
        }};
        sealedUnderflow = new OverflowGate("sealed-underflow"){{
            requirements(category.distribution, with(lead, 5));
            invert = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled |= Env.spores | Env.scorching;
        }};



        // WARNING: this requires the unitType but theres none implemented yet...
        /* cargoDock = new UnitCargoLoader("cargo-dock"){{
            requirements(Category.distribution, with(lead,40 ,bauxite, 5));
            size = 2;
            buildTime = 240;
            unitType = rivulet;
            itemCapacity = 200;
        }};

         cargoDepot = new UnitCargoUnloadPoint("cargo-depot"){{
            requirements(Category.distribution, with(lead,15));
            size = 2;
        }}; */
    }

}