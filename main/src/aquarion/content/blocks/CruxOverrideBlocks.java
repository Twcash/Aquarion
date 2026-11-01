package aquarion.content.blocks;

import arc.func.Cons;
import arc.graphics.Color;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.bullet.ShrapnelBulletType;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Door;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.blocks.defense.OverdriveProjector;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidBridge;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.power.Battery;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.ImpactReactor;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.blocks.storage.Unloader;
import mindustry.world.consumers.ConsumeItems;

import static aquarion.content.AquaItems.chalkalloy;
import mindustry.content.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.BuildVisibility;

import static aquarion.content.AquaItems.steel;
import static mindustry.content.Items.*;
import static mindustry.content.Items.silicon;
import static mindustry.content.Liquids.oil;
import static mindustry.type.ItemStack.with;

public class CruxOverrideBlocks {
    public static <T extends UnlockableContent> void overwrite(UnlockableContent target, Cons<T> setter) {
        setter.get((T) target);
    }
    public static void loadContent() {
        disableVanilla();

        overwrite(Blocks.multiPress, (GenericCrafter r) -> {
                    r.requirements = null;
                    r.requirements(Category.crafting, ItemStack.with(Items.lead, 100, Items.graphite, 50, chalkalloy, 140, silicon, 25));
                }
        );

        overwrite(Blocks.siliconCrucible, (GenericCrafter r) ->{
            r.outputItem = new ItemStack(Items.silicon, 12);
            r.removeConsumer(r.findConsumer(f -> f instanceof ConsumeItems));
            r.consumeItems(ItemStack.with(coal, 6, sand, 9, pyratite, 1));
            r.craftTime = 120;
        });

        overwrite(Blocks.plastaniumCompressor, (GenericCrafter r) ->{
            r.requirements = null;
            r.requirements(Category.crafting, ItemStack.with(silicon, 120, chalkalloy, 60, graphite, 40));
            r.removeConsumer(r.findConsumer(f -> f instanceof ConsumeItems));
            r.consumeItems(ItemStack.with(chalkalloy, 2));
        });

        overwrite(Blocks.surgeSmelter, (GenericCrafter r) -> {
            r.removeConsumer(r.findConsumer(f -> f instanceof ConsumeItems));
            r.consumeItems(with(Items.copper, 3, Items.lead, 4, Items.titanium, 2, Items.silicon, 3));
        });

        overwrite(Blocks.cryofluidMixer, (GenericCrafter r) ->{
            r.requirements = null;
            r.requirements(Category.crafting, ItemStack.with(silicon, 120, chalkalloy, 90, graphite, 40, metaglass, 60));
            r.removeConsumer(r.findConsumer(f -> f instanceof ConsumeItems));
            r.consumeItems(ItemStack.with(chalkalloy, 1));
        });

        overwrite(Blocks.pyratiteMixer, (GenericCrafter r) ->{
            r.removeConsumer(r.findConsumer(f -> f instanceof ConsumeItems));
            r.consumeItems(ItemStack.with(coal, 2, lead, 4, sand, 4));
            r.outputItem = new ItemStack(Items.pyratite, 3);
            r.craftTime = 120;
        });

        overwrite(Blocks.blastMixer, (GenericCrafter r) ->{
            r.requirements = null;
            r.requirements(Category.crafting, ItemStack.with(lead, 30, chalkalloy, 50));
            r.outputItem = new ItemStack(Items.blastCompound, 3);
            r.removeConsumer(r.findConsumer(f -> f instanceof ConsumeItems));
            r.consumeItems(ItemStack.with(pyratite, 3, sporePod, 3));
            r.craftTime = 120;
        });

        overwrite(Blocks.separator, (Separator r) -> {
                    r.requirements = null;
                    r.requirements(Category.crafting, with(Items.copper, 30, chalkalloy, 25));
                    r.results = with(Items.copper, 5, Items.lead, 3, Items.graphite, 2, chalkalloy, 2);
                }
        );

        overwrite(Blocks.sporePress, (GenericCrafter r) ->{
            r.outputLiquid = new LiquidStack(oil, 18/60f);
            r.removeConsumer(r.findConsumer(f -> f instanceof ConsumeItems));
            r.consumeItems(ItemStack.with(sporePod, 1));
            r.craftTime = 60;
        });

        overwrite(Blocks.coalCentrifuge, (GenericCrafter r) -> {
                    r.requirements = null;
                    r.requirements(Category.crafting, with(chalkalloy, 20, Items.graphite, 40, Items.lead, 30));
                }
        );

        overwrite(Blocks.door, (Door r) -> {
                    r.requirements = null;
            r.requirements(Category.defense, with(chalkalloy, 6, Items.silicon, 4));
                }
        );

        overwrite(Blocks.overdriveProjector, (OverdriveProjector r) -> {
                    r.requirements = null;
                    r.requirements(Category.effect, with(Items.lead, 100, chalkalloy, 75, Items.silicon, 75, Items.plastanium, 30));
                }
        );

        overwrite(Blocks.overdriveDome, (OverdriveProjector r) -> {
                    r.requirements = null;
                    r. requirements(Category.effect, with(Items.lead, 200, chalkalloy, 130, Items.silicon, 130, Items.plastanium, 80, Items.surgeAlloy, 120));
                }
        );

        overwrite(Blocks.forceProjector, (ForceProjector r) -> {
                    r.requirements = null;
                    r.requirements(Category.effect, with(Items.lead, 100, chalkalloy, 75, Items.silicon, 125));
                }
        );

        overwrite(Blocks.titaniumConveyor, (Conveyor r) -> {
            r.requirements = null;
            r.requirements(Category.distribution, with(Items.copper, 1, Items.lead, 1, chalkalloy, 1));
        });

        overwrite(Blocks.unloader, (Unloader r) -> {
                    r.requirements = null;
                    r.requirements(Category.distribution, with(chalkalloy, 25, Items.silicon, 30));
                }
        );

        overwrite(Blocks.massDriver, (MassDriver r) -> {
                    r.requirements = null;
                    r.requirements(Category.distribution, with(chalkalloy, 125, Items.silicon, 75, Items.lead, 125, Items.thorium, 50));
                }
        );

        overwrite(Blocks.rotaryPump, (Pump r) -> {
                    r.requirements = null;
                    r.requirements(Category.liquid, with(Items.copper, 70, Items.metaglass, 50, Items.silicon, 20, chalkalloy, 35));
                }
        );

        overwrite(Blocks.impulsePump, (Pump r) -> {
                    r.requirements = null;
                    r.requirements(Category.liquid, with(Items.copper, 80, Items.metaglass, 90, Items.silicon, 30, chalkalloy, 40, Items.thorium, 35));
                }
        );

        overwrite(Blocks.pulseConduit, (Conduit r) -> {
                    r.requirements = null;
                    r.requirements(Category.liquid, with(chalkalloy, 3, Items.metaglass, 1));
                }
        );

        overwrite(Blocks.liquidContainer, (LiquidRouter r) -> {
                    r.requirements = null;
                    r.requirements(Category.liquid, with(chalkalloy, 10, Items.metaglass, 15));
                }
        );

        overwrite(Blocks.liquidTank, (LiquidRouter r) -> {
                    r.requirements = null;
                    r.requirements(Category.liquid, with(chalkalloy, 30, Items.metaglass, 40));
                }
        );

        overwrite(Blocks.phaseConduit, (LiquidBridge r) -> {
                    r.requirements = null;
                    r.requirements(Category.liquid, with(Items.phaseFabric, 5, Items.silicon, 7, Items.metaglass, 20, chalkalloy, 15));
                }
        );

        overwrite(Blocks.powerNodeLarge, (PowerNode r) -> {
                    r.requirements = null;
                    r.requirements(Category.power, with(chalkalloy, 5, Items.lead, 10, Items.silicon, 3));
                }
        );

        overwrite(Blocks.surgeTower, (PowerNode r) -> {
                    r.requirements = null;
                    r.requirements(Category.power, with(chalkalloy, 7, Items.lead, 10, Items.silicon, 15, Items.surgeAlloy, 15));
                }
        );

        overwrite(Blocks.batteryLarge, (Battery r) -> {
                    r.requirements = null;
                    r.requirements(Category.power, with(chalkalloy, 20, Items.lead, 50, Items.silicon, 30));
                }
        );

        overwrite(Blocks.differentialGenerator, (ConsumeGenerator r) -> {
                    r.requirements = null;
                    r.requirements(Category.power, with(Items.copper, 70, Items.titanium, 50, Items.lead, 100, Items.silicon, 65, Items.metaglass, 50));
                }
        );

        overwrite(Blocks.impactReactor, (ImpactReactor r) -> {
            r.itemDuration = 120f;
        });

        overwrite(Blocks.laserDrill, (Drill r) ->{
            r.requirements = null;
            r.requirements(Category.production, with(Items.copper, 35, Items.graphite, 30, Items.silicon, 30, chalkalloy, 20));
        });

        overwrite(Blocks.blastDrill, (Drill r) ->{
            r.requirements = null;
            r.requirements(Category.production, with(Items.copper, 65, Items.silicon, 60, chalkalloy, 50, Items.thorium, 75));
        });

        overwrite(Blocks.cultivator, (AttributeCrafter r) ->{
            r.outputItem = new ItemStack(sporePod, 3);
            r.craftTime = 240f;
        });

        overwrite(Blocks.container, (StorageBlock r) -> {
                    r.requirements = null;
                    r.requirements(Category.effect, with(Items.titanium, 100));
                }
        );

        overwrite(Blocks.vault, (StorageBlock r) -> {
                    r.requirements = null;
                    r.requirements(Category.effect, with(Items.titanium, 250, Items.thorium, 125));
                }
        );

        overwrite(Blocks.lancer, (PowerTurret r) -> {
                    r.requirements = null;
                    r.requirements(Category.turret, with(Items.copper, 60, Items.lead, 70, Items.silicon, 60, Items.titanium, 30));
                }
        );

        overwrite(Blocks.parallax, (TractorBeamTurret r) -> {
                    r.requirements = null;
                    r.requirements(Category.turret, with(Items.silicon, 160, Items.titanium, 110, Items.graphite, 50));
                }
        );

        overwrite(Blocks.swarmer, (ItemTurret r) -> {
                    r.requirements = null;
                    r.requirements(Category.turret, with(Items.graphite, 35, Items.titanium, 35, Items.plastanium, 45, Items.silicon, 30));
                }
        );

        overwrite(Blocks.salvo, (ItemTurret r) -> {
                    r.requirements = null;
                    r.requirements(Category.turret, with(Items.copper, 100, Items.graphite, 80, Items.titanium, 50));
                }
        );

        overwrite(Blocks.segment, (PointDefenseTurret r) -> {
                    r.requirements = null;
                    r.requirements(Category.turret, with(Items.silicon, 130, Items.thorium, 80, Items.phaseFabric, 40, Items.titanium, 40));
                }
        );

        overwrite(Blocks.tsunami, (LiquidTurret r) -> {
                    r.requirements = null;
                    r.requirements(Category.turret, with(Items.metaglass, 100, Items.lead, 400, Items.titanium, 250, Items.thorium, 100));
                }
        );

        overwrite(Blocks.fuse, (ItemTurret r) -> {
            r.ammoTypes = null;
            float brange = r.range + 10f;
            r.ammo(
                    chalkalloy, new ShrapnelBulletType(){{
                        length = brange;
                        damage = 66f;
                        ammoMultiplier = 4f;
                        width = 17f;
                        reloadMultiplier = 1.3f;
                    }},
                    steel, new ShrapnelBulletType(){{
                        length = brange;
                        damage = 105f;
                        ammoMultiplier = 5f;
                        toColor = Color.gray;
                        shootEffect = smokeEffect = Fx.thoriumShoot;
                    }}
            );
        });

        overwrite(Blocks.ripple, (ItemTurret r) -> {
                    r.requirements = null;
                    r.requirements(Category.turret, ItemStack.with(Items.copper, 150, Items.graphite, 135, chalkalloy, 60));
                }
        );


        overwrite(Blocks.cyclone, (ItemTurret r) -> {
                    r.requirements = null;
                    r.requirements(Category.turret, with(Items.copper, 200, Items.titanium, 125, Items.plastanium, 80));
                }
        );
    }
    public static void disableVanilla() {
        Blocks.landingPad.buildVisibility = BuildVisibility.shown;
    }
}
