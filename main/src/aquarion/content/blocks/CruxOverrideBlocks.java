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
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.power.ImpactReactor;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.GenericCrafter;
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

        overwrite(Blocks.plastaniumCompressor, (GenericCrafter r) ->{
            r.requirements = null;
            r.requirements(Category.crafting, ItemStack.with(silicon, 120, chalkalloy, 60, graphite, 40));
            r.removeConsumer(r.findConsumer(f -> f instanceof ConsumeItems));
            r.consumeItems(ItemStack.with(chalkalloy, 2));
        });
        overwrite(Blocks.cultivator, (AttributeCrafter r) ->{
            r.outputItem = new ItemStack(sporePod, 3);
            r.craftTime = 240f;
        });
        overwrite(Blocks.pyratiteMixer, (GenericCrafter r) ->{
            r.removeConsumer(r.findConsumer(f -> f instanceof ConsumeItems));
            r.consumeItems(ItemStack.with(coal, 2, lead, 4, sand, 4));
            r.outputItem = new ItemStack(Items.pyratite, 3);
            r.craftTime = 120;
        });
        overwrite(Blocks.blastMixer, (GenericCrafter r) ->{
            r.outputItem = new ItemStack(Items.blastCompound, 3);
            r.removeConsumer(r.findConsumer(f -> f instanceof ConsumeItems));
            r.consumeItems(ItemStack.with(pyratite, 3, sporePod, 3));
            r.craftTime = 120;
        });
        overwrite(Blocks.cryofluidMixer, (GenericCrafter r) ->{
            r.requirements = null;
            r.requirements(Category.crafting, ItemStack.with(silicon, 120, chalkalloy, 90, graphite, 40, metaglass, 60));
            r.removeConsumer(r.findConsumer(f -> f instanceof ConsumeItems));
            r.consumeItems(ItemStack.with(chalkalloy, 1));
        });
        overwrite(Blocks.siliconCrucible, (GenericCrafter r) ->{
            r.outputItem = new ItemStack(Items.silicon, 12);
            r.removeConsumer(r.findConsumer(f -> f instanceof ConsumeItems));
            r.consumeItems(ItemStack.with(coal, 6, sand, 9, pyratite, 1));
            r.craftTime = 120;
        });
        overwrite(Blocks.sporePress, (GenericCrafter r) ->{
            r.outputLiquid = new LiquidStack(oil, 18/60f);
            r.removeConsumer(r.findConsumer(f -> f instanceof ConsumeItems));
            r.consumeItems(ItemStack.with(sporePod, 1));
            r.craftTime = 60;
        });
        overwrite(Blocks.laserDrill, (GenericCrafter r) ->{
            r.requirements = null;
            r.requirements(Category.crafting, ItemStack.with(copper, 35, graphite, 30, chalkalloy, 40, silicon, 30));
            r.removeConsumer(r.findConsumer(f -> f instanceof ConsumeItems));
            r.consumeItems(ItemStack.with(chalkalloy, 2));
        });
        overwrite(Blocks.titaniumConveyor, (Conveyor r) -> {
            r.requirements = null;
            r.requirements(Category.distribution, with(Items.copper, 1, Items.lead, 1, chalkalloy, 1));
        });
        overwrite(Blocks.impactReactor, (ImpactReactor r) -> {
            r.itemDuration = 120f;
        });
        overwrite(Blocks.ripple, (ItemTurret r) -> {
                    r.requirements = null;
                    r.requirements(Category.turret, ItemStack.with(Items.copper, 150, Items.graphite, 135, chalkalloy, 60));
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
    }
    public static void disableVanilla() {
        Blocks.landingPad.buildVisibility = BuildVisibility.shown;
    }
}
