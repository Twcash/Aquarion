package aquarion.content.blocks;

import aquarion.content.AquaLiquids;
import aquarion.content.AquaSounds;
import aquarion.content.AquaStatuses;
import aquarion.content.AquaUnitTypes;
import aquarion.world.blocks.payload.InitializationBay;
import aquarion.world.blocks.units.UnitBlock;
import aquarion.world.blocks.units.UnitBlockStatusApplierThingWhat;
import aquarion.world.graphics.AquaFx;
import arc.func.Cons;
import arc.struct.Seq;
import mindustry.content.*;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.effect.RadialEffect;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaUnitTypes.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.oil;
import static mindustry.type.ItemStack.with;

public class UnitBlocks {
    public static Block initializationBay, statusApplier ,pillage, solder,  weld, bulwark, pugnate, rampart, crest, reave, soar, raze, shatter, castellan, unitByte, index, tuple;
    
    public static <T extends UnlockableContent> void overwrite(UnlockableContent target, Cons<T> setter) {
        setter.get((T) target);
    }

    public static void loadContent() {
        initializationBay = new InitializationBay("initialization-bay"){{
            requirements(Category.units, with(polymer, 500, ferricMatter, 300, silicon, 1500));
            buildVisibility = BuildVisibility.sandboxOnly;
            consumePower(36);
            size = 6;
        }};
        statusApplier = new UnitBlockStatusApplierThingWhat("manipulation-bay"){{
            requirements(Category.units, with(polymer, 200, silicon, 900, copper, 1000, metaglass, 500));
            plans = Seq.with(new StatusPlan(AquaStatuses.weighted, 300) {{
                requirements = PayloadStack.list(DefenseBlocks.nickelWall, 3);
                itemReq = new ItemStack[]{new ItemStack(Items.lead, 50)};
                fx = new RadialEffect(AquaFx.statusSmoke, 4, 90f, 10f);
            }},new  StatusPlan(AquaStatuses.rearmed, 500) {{
                requirements = PayloadStack.list(PowerBlocks.outlet, 10);
                itemReq = new ItemStack[]{new ItemStack(Items.coal, 100)};
                liquidReq = new LiquidStack[]{new LiquidStack(AquaLiquids.ammonia, 1)};
                fx = new RadialEffect(AquaFx.statusSmoke, 4, 90f, 10f);
            }},new  StatusPlan(AquaStatuses.retrofit, 600) {{
                requirements = PayloadStack.list(DefenseBlocks.nickelWall, 3);
                liquidReq = new LiquidStack[]{new LiquidStack(AquaLiquids.ammonia, 1)};
                itemReq = new ItemStack[]{new ItemStack(graphite, 100), new ItemStack(silicon, 100)};
                fx = new RadialEffect(AquaFx.statusSmoke, 4, 90f, 10f);
            }}, new StatusPlan(AquaStatuses.armored, 900) {{
                requirements = PayloadStack.list(DefenseBlocks.aluminumWall, 5);
                requirements = PayloadStack.list(DefenseBlocks.polymerWall, 2);
                fx = new RadialEffect(AquaFx.statusSmoke2, 4, 90f, 10f);
            }});
            consumePower(10);
            size =5;
        }};
        bulwark = new UnitBlock("bulwark-inactive") {{
            requirements(Category.units, with(silicon, 80, metaglass, 30, lead, 50, graphite, 40));
            unit = AquaUnitTypes.bulwark;
            size = 2;
            time = 15 * 60;
            consumePower(0.75f);
        }};
        weld = new UnitBlock("weld-inactive") {{
            requirements(Category.units, with(metaglass, 80, lead, 80));
            unit = AquaUnitTypes.weld;
            size = 2;
            time = 25 * 60;
            floating = true;
            placeableLiquid = true;
            envDisabled = Env.underwater | Env.space;
            consumePower(2);
        }};
        solder = new UnitBlock("solder-inactive") {{
            requirements(Category.units, with(metaglass, 80, silicon, 280, ferricMatter, 150));
            unit = AquaUnitTypes.solder;
            size = 3;
            time = 33 * 60;
            floating = true;
            placeableLiquid = true;
            envDisabled = Env.underwater | Env.space;
            consumePower(5);
        }};
        pugnate = new UnitBlock("pugnate-inactive") {{
            requirements(Category.units, with(silicon, 90, aluminum, 25, copper, 70, graphite, 45));
            unit = AquaUnitTypes.pugnate;
            size = 2;
            time = 20 * 60;
            consumePower(0.5f);
        }};
        rampart = new UnitBlock("rampart-inactive") {{
            requirements(Category.units, with(cupronickel, 40, silicon, 120, metaglass, 80));
            unit = AquaUnitTypes.rampart;
            size = 2;
            time = 10 * 60;
            consumePower(0.75f);
        }};
        crest = new UnitBlock("crest-inactive") {{
            requirements(Category.units, with(silicon, 40, graphite, 60, metaglass, 50));
            unit = AquaUnitTypes.crest;
            size = 2;
            time = 5 * 60;
            destroySound = AquaSounds.start5;
            consumePower(2);
            consumeLiquid(oil, 0.25f);
        }};
        unitByte = new UnitBlock("byte-inactive") {{
            requirements(Category.units, with(silicon, 120, metaglass, 90, copper, 40));
            unit = AquaUnitTypes.byteUnit;
            size = 2;
            time = 7 * 60;
            destroySound = AquaSounds.start5;
            consumePower(1.5f);
            consumeLiquid(oil, 0.125f);
        }};
        reave = new UnitBlock("reave-inactive") {{
            requirements(Category.units, with(polymer, 80, graphite, 120, ferricMatter, 150));
            unit = AquaUnitTypes.reave;
            size = 3;
            consumePower(3);
            time = 20 * 60;
            destroySound = AquaSounds.start4;
        }};
        pillage = new UnitBlock("pillage-inactive") {{
            requirements(Category.units, with(graphite, 110, metaglass, 90, silicon, 200, copper, 180));
            unit = AquaUnitTypes.pillage;
            size = 2;
            consumePower(4);
            time = 33 * 60f;
            destroySound = AquaSounds.start2;
        }};
        soar = new UnitBlock("soar-inactive") {{
            requirements(Category.units, with(cupronickel, 90, polymer, 50, metaglass, 90));
            unit = AquaUnitTypes.soar;
            size = 3;
            time = 15 * 60;
            consumePower(5);
            destroySound = AquaSounds.start2;
        }};
        raze = new UnitBlock("raze-inactive") {{
            requirements(Category.units, with(polymer, 100, silicon, 150, cupronickel, 120));
            unit = AquaUnitTypes.raze;
            size = 3;
            consumePower(3);
            time = 20 * 60;
            destroySound = AquaSounds.start4;
        }};
        shatter = new UnitBlock("shatter-inactive") {{
            requirements(Category.units, with(graphite, 120, metaglass, 250));
            unit = AquaUnitTypes.shatter;
            size = 3;
            consumePower(4);
            time = 30 * 60;
            destroySound = AquaSounds.start;
        }};
//        castellan = new UnitBlock("castellan") {{
//            requirements(Category.units, with(cupronickel, 400, ferrosilicon, 100, aluminum, 60));
//            unit = AquaUnitTypes.castellan;
//            size = 3;
//            time = 25 * 60;
//            destroySound = AquaSounds.start4;
//            buildVisibility = BuildVisibility.hidden;
//        }};

        overwrite(Blocks.groundFactory, (UnitFactory r) -> {
            r.plans.remove(2);
            r.plans.addAll(
                    new UnitFactory.UnitPlan(UnitTypes.nova, 60f * 40, with(Items.silicon, 30, Items.lead, 20, chalkalloy, 20)),
            new UnitFactory.UnitPlan(isop, 60f * 20, with(Items.silicon, 25, nickel, 10))

        );
        });
        overwrite(Blocks.navalFactory, (UnitFactory r) -> {
            r.plans.remove(1);
            r.plans.addAll(
                    new UnitFactory.UnitPlan(UnitTypes.retusa, 60f * 25, with(Items.silicon, 25, chalkalloy, 10)),
                    new UnitFactory.UnitPlan(frost, 60f * 10, with(Items.silicon, 10, chalkalloy, 20))
            );
        });
        overwrite(Blocks.airFactory, (UnitFactory r) -> r.plans.addAll(
                new UnitFactory.UnitPlan(cog, 60f * 35, with(Items.silicon, 20, brimstone, 30))
        ));

        overwrite(Blocks.additiveReconstructor, (Reconstructor r) -> r.upgrades.addAll(
                new UnitType[]{isop, empusa},
                new UnitType[]{frost, rime},
                new UnitType[]{cog, tenon}

        ));
        overwrite(Blocks.multiplicativeReconstructor, (Reconstructor r) ->{
            r.requirements = null;
            r.requirements(Category.units, with(Items.lead, 650, Items.silicon, 450, chalkalloy, 350, plastanium, 650));
            r.removeConsumer(r.findConsumer(f -> f instanceof ConsumeItems));
            r.consumeItems(with(Items.silicon, 130, chalkalloy, 80, Items.metaglass, 40));
            r.upgrades.addAll(
                new UnitType[]{empusa, oratoria},
                new UnitType[]{rime, verglas}

        );});
        overwrite(Blocks.exponentialReconstructor, (Reconstructor r) -> {
            r.requirements = null;
            r.requirements(Category.units, with(Items.lead, 2000, Items.silicon, 1000, chalkalloy, 2000, Items.plastanium, 450));
            r.removeConsumer(r.findConsumer(f -> f instanceof ConsumeItems));
            r.consumeItems(with(Items.silicon, 850, chalkalloy, 750, Items.plastanium, 650));
            r.upgrades.addAll(
                new UnitType[]{oratoria, rhombodera},
                new UnitType[]{verglas, glaciate}
        );
        });
        overwrite(Blocks.tetrativeReconstructor, (Reconstructor r) -> r.upgrades.addAll(
                new UnitType[]{rhombodera, parasphendale},
                new UnitType[]{glaciate, permafrost}

        ));
    }
}
