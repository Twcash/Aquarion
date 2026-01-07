package aquarion.content.blocks;

import aquarion.world.blocks.distribution.SealedConveyor;
import aquarion.world.blocks.distribution.SealedRouter;
import aquarion.world.blocks.payload.Displacer;
import aquarion.world.blocks.payload.PayloadDistributor;
import aquarion.world.blocks.payload.PayloadJumper;
import arc.func.Cons;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.ctype.UnlockableContent;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.payloads.PayloadRouter;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.blocks.units.UnitCargoLoader;
import mindustry.world.blocks.units.UnitCargoUnloadPoint;
import mindustry.world.meta.Env;

import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaPlanets.*;
import static aquarion.content.AquaUnitTypes.rivulet;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;

public class DistributionBlocks {
    public static Block payloadDistributor, payloadPad, sealedInvertedSorter, manganeseRail, armoredSealedConveyor, sealedOverflow, sealedDistributor,
            sealedUnloader, sealedConveyor, sealedRouter, sealedSorter,
            sealedUnderflow, sealedJunction, payloadDisplacer;
    public static Block cargoDepot, cargoDock;
    public static <T extends UnlockableContent> void overwrite(UnlockableContent target, Cons<T> setter) {
        setter.get((T) target);
    }
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
        sealedUnderflow = new OverflowGate("sealed-undeflow") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.distribution, with(silicon, 5));
            invert = true;
            hasItems = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
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
        overwrite(Blocks.titaniumConveyor, (Conveyor r) -> {
            r.requirements = null;
            r.requirements(Category.distribution, with(Items.copper, 1, Items.lead, 1, chalkalloy, 1));
            Block conveyor = Vars.content.blocks().find(f -> f == Blocks.titaniumConveyor);

            if (conveyor != null) {
                Vars.content.blocks().find(f -> f == Blocks.titaniumConveyor).name.replace("titanium-conveyor","chalkalloy-conveyor");
            }
        });
        payloadPad = new PayloadJumper("payload-pad"){{
            requirements(Category.units, with(polymer, 45, silicon, 50, copper, 120));
        }};
        payloadDisplacer = new Displacer("payload-displacer"){{
            requirements(Category.units, with(polymer, 80, silicon, 90, ferricMatter, 25));
        }};
        payloadDistributor = new PayloadDistributor("payload-distributor"){{
            requirements(Category.units, with(polymer, 30));
        }};
    }
}
