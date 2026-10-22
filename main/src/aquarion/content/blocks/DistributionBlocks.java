package aquarion.content.blocks;

import aquarion.content.AquaCategories;
import aquarion.world.blocks.distribution.ItemHopper;
import aquarion.world.blocks.distribution.RailPoint;
import aquarion.world.blocks.distribution.CargoStation;
import aquarion.world.blocks.distribution.ItemYeeter;
import aquarion.world.blocks.distribution.SealedConveyor;
import aquarion.world.blocks.distribution.SealedRouter;
import aquarion.world.blocks.distribution.EntitySorter;
import aquarion.world.blocks.distribution.EntityJunction;
import aquarion.world.blocks.distribution.EntityDirectionalUnloader;
import aquarion.world.blocks.distribution.EntityDirectionalUnloader.EntityDirectionalUnloaderBuild;
import aquarion.world.blocks.distribution.EntityUnloader.EntityUnloaderBuild;
import aquarion.world.blocks.core.EntityStorageBuild;
import aquarion.world.blocks.core.EntityCoreBuild;
import aquarion.world.blocks.payload.*;
import aquarion.world.blocks.production.DelayIncinerator;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.WaterReflections;
import arc.func.Cons;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.effect.MultiEffect;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.blocks.distribution.*;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;

import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaPlanets.*;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;

public class DistributionBlocks {
    public static Block payloadDistributor, itemHopper, itemYeeter, tungstenYeeter, tungstenHopper, cremator, payloadPad, sealedInvertedSorter,steelRouter, steelConveyor, armoredSealedConveyor, sealedOverflow, sealedDistributor,
            sealedUnloader, sealedConveyor, massDistributor, sealedRouter, sealedSorter,
            sealedUnderflow, sealedJunction, payloadDisplacer;
    public static Block liquidCargoDock, liquidCargoDepot, cargoDepot, cargoDock, railPoint, railJunction;
    public static <T extends UnlockableContent> void overwrite(UnlockableContent target, Cons<T> setter) {
        setter.get((T) target);
    }
    public static void loadContent() {
        liquidCargoDock = new CargoStation("liquid-dock", true, true){{
            requirements(AquaCategories.railways, with(concrete, 100, lead, 150, metaglass, 50));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        liquidCargoDepot = new CargoStation("liquid-depot", false, true){{
            requirements(AquaCategories.railways, with(concrete, 150, lead, 200, metaglass, 75));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        cargoDock = new CargoStation("cargo-dock", true){{
            requirements(AquaCategories.railways, with(concrete, 100, lead, 150, copper, 200));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        cargoDepot = new CargoStation("cargo-depot", false){{
            requirements(AquaCategories.railways, with(concrete, 150, lead, 200, copper, 200));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        railPoint = new RailPoint("rail-node"){{
            requirements(AquaCategories.railways, with(concrete, 50, lead, 20));
            size = 2;
            rotateDraw = false;
            range = 120;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};

        railJunction = new RailPoint("rail-junction"){{
            requirements(Category.distribution, with(concrete, 100, lead, 150));
            size = 2;
            rotateDraw = false;
            fourDirections = true;
            range = 120;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};

        EntityStorageBuild.install((StorageBlock)Blocks.container);
        EntityStorageBuild.install((StorageBlock)Blocks.vault);
        EntityUnloaderBuild.install((Unloader)Blocks.unloader);
        EntityCoreBuild.install((CoreBlock)Blocks.coreShard);
        EntityCoreBuild.install((CoreBlock)Blocks.coreFoundation);
        EntityCoreBuild.install((CoreBlock)Blocks.coreNucleus);
        EntityCoreBuild.install((CoreBlock)Blocks.coreBastion);
        EntityCoreBuild.install((CoreBlock)Blocks.coreCitadel);
        EntityCoreBuild.install((CoreBlock)Blocks.coreAcropolis);

        sealedConveyor = new SealedConveyor("sealed-conveyor") {{
            requirements(Category.distribution, with(lead, 1));
            envEnabled = 4;
            siphonReplacement = LiquidBlocks.siphonJunction;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.distriDestroy);
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
            requirements(Category.distribution, with(silicon, 10));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            alwaysUnlocked = true;
            speed = 2.1f;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.distriDestroy);

            hasItems = true;
        }};
        itemYeeter = new ItemYeeter("conveyor-yeeter"){{
            requirements(Category.distribution, with(silicon, 30, copper, 90, graphite, 50));
            size = 1;
            consumePower(3);
            itemCapacity = 15;
        }};
        itemHopper = new ItemHopper("item-hopper"){{
            requirements(Category.distribution, with(graphite, 50, copper, 90, polymer, 35));
            size = 1;
            itemCapacity = 30;
        }};
        tungstenYeeter = new ItemYeeter("tungsten-yeeter"){{
            requirements(Category.distribution, with(tungsten, 120, graphite, 80));
            size = 1;
            consumePower(3);
            itemCapacity = 30;
            reload = 20f;
        }};
        tungstenHopper = new ItemHopper("tungsten-hopper"){{
            requirements(Category.distribution, with(graphite, 60, beryllium, 140, tungsten, 70));
            size = 2;
            itemCapacity = 60;
        }};
        sealedDistributor = new SealedRouter("sealed-distributor") {{
            requirements(Category.distribution, with(silicon, 50));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            speed = 1.2f;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.distriDestroy);

            size = 2;
            hasItems = true;
            solid = true;
        }};
        massDistributor = new SealedRouter("mass-distributor") {{
            requirements(Category.distribution, with(silicon, 120));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.distriDestroy);

            speed = 1.5f;
            size = 4;
            hasItems = true;
            solid = true;
        }};
        sealedJunction = new EntityJunction("sealed-junction") {{
            requirements(Category.distribution, with(silicon, 15));
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.distriDestroy);
            speed = 15;
            displayedSpeed = 30;
            capacity = 8;
            envEnabled |= Env.terrestrial | Env.underwater;
            ((SealedConveyor) sealedConveyor).junctionReplacement = this;
            envDisabled = Env.none;
        }};
        sealedUnloader = new EntityDirectionalUnloader("sealed-unloader") {{
            requirements(Category.distribution, with(metaglass, 40, silicon, 80));
            speed = 2f;
            allowCoreUnload = true;
        }};
        sealedSorter = new EntitySorter("sealed-sorter") {{
            requirements(Category.distribution, with(silicon, 15));
            envEnabled |= Env.terrestrial | Env.underwater;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.distriDestroy);
            envDisabled = Env.none;
            hasItems = true;

        }};
        sealedInvertedSorter = new EntitySorter("sealed-inverted-sorter") {{
            requirements(Category.distribution, with(silicon, 15));
            envEnabled |= Env.terrestrial | Env.underwater;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.distriDestroy);
            envDisabled = Env.none;
            hasItems = true;
            invert = true;
        }};
        sealedOverflow = new OverflowGate("sealed-overflow") {{
            requirements(Category.distribution, with(silicon, 5));
            invert = false;
            hasItems = true;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.distriDestroy);

            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        sealedUnderflow = new OverflowGate("sealed-underflow") {{
            requirements(Category.distribution, with(silicon, 5));
            invert = true;
            hasItems = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.distriDestroy);

        }};
        steelConveyor = new StackConveyor("steel-conveyor") {{
            requirements(Category.distribution, with(steel, 1, aluminum, 2));
            speed = 2f / 60f;
            itemCapacity = 150;
            drawDisabled = false;
            outputRouter = true;
            underBullets = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        steelRouter = new StackRouter("steel-router"){{
            requirements(Category.distribution, with(steel, 15, aluminum, 20));
            underBullets = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            baseEfficiency = 1;
        }};
        cremator = new DelayIncinerator("cremator"){{
            requirements(Category.crafting, with(copper, 50));
            size = 2;
            incinTime = 6;
            itemCapacity = 30;
            consumePower(1/2f);
        }};
        overwrite(Blocks.titaniumConveyor, (Conveyor r) -> {
            r.requirements = null;
            r.requirements(Category.distribution, with(Items.copper, 1, Items.lead, 1, chalkalloy, 1));
        });
        payloadPad = new PayloadJumper("payload-pad"){{
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            requirements(Category.units, with(graphite, 100, silicon, 50, copper, 120));
            size = 3;
        }};
        payloadDisplacer = new Displacer("payload-displacer"){{
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);

            requirements(Category.units, with(graphite, 150, silicon, 200, metaglass, 100));
        }};
        payloadDistributor = new PayloadDistributor("payload-distributor"){{
            requirements(Category.units, with(graphite, 30));
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
        }};
        //this can be used as example if you want to edit stuff from reflections on specific blocks
        WaterReflections.set(itemYeeter, 1f, 0.3f, true);
    }
}
