package aquarion.blocks;

import aquarion.AquaSounds;
import aquarion.units.AquaUnitTypes;
import aquarion.world.blocks.units.UnitBlock;
import arc.func.Cons;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.ctype.UnlockableContent;
import mindustry.type.Category;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;
import mindustry.world.modules.ItemModule;

import static aquarion.AquaItems.*;
import static aquarion.units.AquaUnitTypes.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.oil;
import static mindustry.type.ItemStack.with;

public class AquaUnitFactories {
    public static Block weld, bulwark, pugnate, rampart, crest, reave, soar, raze, shatter, castellan, unitByte, index, tuple;

    public static <T extends UnlockableContent> void overwrite(UnlockableContent target, Cons<T> setter) {
        setter.get((T) target);
    }

    public static void loadContent() {
        bulwark = new UnitBlock("bulwark") {{
            requirements(Category.units, with(silicon, 80, metaglass, 60, lead, 50, graphite, 40));
            unit = AquaUnitTypes.bulwark;
            size = 2;
            time = 15 * 60;
            consumePower(0.75f);
        }};
        weld = new UnitBlock("weld") {{
            requirements(Category.units, with(metaglass, 120, lead, 80));
            unit = AquaUnitTypes.weld;
            size = 2;
            time = 25 * 60;
            floating = true;
            placeableLiquid = true;
            envDisabled = Env.underwater | Env.space;
            consumePower(2);
        }};
        pugnate = new UnitBlock("pugnate") {{
            requirements(Category.units, with(silicon, 90, aluminum, 50, copper, 70, graphite, 45));
            unit = AquaUnitTypes.pugnate;
            size = 2;
            time = 20 * 60;
            consumePower(0.5f);
        }};
        rampart = new UnitBlock("rampart") {{
            requirements(Category.units, with(cupronickel, 150, silicon, 120, metaglass, 70));
            unit = AquaUnitTypes.rampart;
            size = 2;
            time = 10 * 60;
            consumePower(0.75f);
        }};
        crest = new UnitBlock("crest") {{
            requirements(Category.units, with(silicon, 40, graphite, 60, metaglass, 50));
            unit = AquaUnitTypes.crest;
            size = 2;
            time = 5 * 60;
            destroySound = AquaSounds.start5;
            consumePower(2);
            consumeLiquid(oil, 0.25f);
        }};
        unitByte = new UnitBlock("byte") {{
            requirements(Category.units, with(silicon, 120, metaglass, 90, copper, 40));
            unit = AquaUnitTypes.byteUnit;
            size = 2;
            time = 7 * 60;
            destroySound = AquaSounds.start5;
            consumePower(1.5f);
            consumeLiquid(oil, 0.125f);
        }};
        reave = new UnitBlock("reave") {{
            requirements(Category.units, with(polymer, 80, graphite, 120, ferricMatter, 150));
            unit = AquaUnitTypes.reave;
            size = 3;
            consumePower(3);
            time = 20 * 60;
            destroySound = AquaSounds.start4;
        }};
        soar = new UnitBlock("soar") {{
            requirements(Category.units, with(cupronickel, 140, polymer, 120, metaglass, 90));
            unit = AquaUnitTypes.soar;
            size = 3;
            time = 15 * 60;
            consumePower(5);
            destroySound = AquaSounds.start2;
        }};
        raze = new UnitBlock("raze") {{
            requirements(Category.units, with(polymer, 100, silicon, 150, cupronickel, 120));
            unit = AquaUnitTypes.raze;
            size = 3;
            consumePower(3);
            time = 20 * 60;
            destroySound = AquaSounds.start4;
        }};
        shatter = new UnitBlock("shatter") {{
            requirements(Category.units, with(graphite, 120, metaglass, 250));
            unit = AquaUnitTypes.shatter;
            size = 3;
            consumePower(4);
            time = 30 * 60;
            destroySound = AquaSounds.start;
        }};
        castellan = new UnitBlock("castellan") {{
            requirements(Category.units, with(cupronickel, 400, ferrosilicon, 100, aluminum, 60));
            unit = AquaUnitTypes.castellan;
            size = 3;
            time = 25 * 60;
            destroySound = AquaSounds.start4;
            buildVisibility = BuildVisibility.hidden;
        }};

        overwrite(Blocks.groundFactory, (UnitFactory r) -> r.plans.addAll(
                new UnitFactory.UnitPlan(isop, 60f * 20, with(Items.silicon, 25, nickel, 10))

        ));

        overwrite(Blocks.airFactory, (UnitFactory r) -> r.plans.addAll(
                new UnitFactory.UnitPlan(cog, 60f * 35, with(Items.silicon, 20, brimstone, 30))
        ));

        overwrite(Blocks.navalFactory, (UnitFactory r) -> r.plans.addAll(
                new UnitFactory.UnitPlan(frost, 60f * 10, with(Items.silicon, 10, titanium, 20))
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
