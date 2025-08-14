package aquarion.blocks;

import aquarion.AquaSounds;
import aquarion.units.AquaUnitTypes;
import aquarion.world.blocks.units.QeralterUnitFactory;
import aquarion.world.blocks.units.UnitBlock;
import arc.func.Cons;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.ctype.UnlockableContent;
import mindustry.type.Category;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;

import static aquarion.AquaItems.*;
import static aquarion.units.AquaUnitTypes.*;
import static mindustry.content.Blocks.additiveReconstructor;
import static mindustry.content.Blocks.titan;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.oil;
import static mindustry.type.ItemStack.with;

public class AquaUnitFactories {
    public static <T extends UnlockableContent> void overwrite(UnlockableContent target, Cons<T> setter){
        setter.get((T)target);
    }
    public static Block bulwark, pugnate, rampart, crest, reave, soar, raze, shatter, castellan, unitByte, index, tuple;

    public static void loadContent(){
        bulwark = new UnitBlock("bulwark"){{
            requirements(Category.units, with(silicon, 80, metaglass, 60, lead, 50));
            unit = AquaUnitTypes.bulwark;
            size = 2;
            time = 15*60;
            consumePower(75/60f);
        }};
       pugnate = new UnitBlock("pugnate"){{
            requirements(Category.units, with(silicon, 90, aluminum, 50, copper, 70));
            unit = AquaUnitTypes.pugnate;
            size = 2;
           time = 20*60;
           consumePower(50/60f);
       }};
        rampart = new UnitBlock("rampart"){{
            requirements(Category.units, with( cupronickel, 150, silicon, 120));
            unit = AquaUnitTypes.rampart;
            size = 2;
            time = 10*60;
            consumePower(75/60f);
        }};
        crest = new UnitBlock("crest"){{
            requirements(Category.units, with(silicon, 40, copper, 60, metaglass, 50));
            unit = AquaUnitTypes.crest;
            size = 2;
            time = 5*60;
            destroySound = AquaSounds.start5;
            consumePower(200/60f);
            consumeLiquid(oil, 5/60f);
       }};
        unitByte = new UnitBlock("byte"){{
            requirements(Category.units, with(silicon, 120, metaglass, 90, copper, 40));
            unit = AquaUnitTypes.byteUnit;
            size = 2;
            time = 7*60;
            destroySound = AquaSounds.start5;
            consumePower(150/60f);
            consumeLiquid(oil, 15/60f);
        }};
        reave = new UnitBlock("reave"){{
            requirements(Category.units, with(steel, 100, copper, 120, metaglass, 150));
            unit = AquaUnitTypes.reave;
            size = 3;
            time = 20*60;
            destroySound = AquaSounds.start4;
        }};
        soar = new UnitBlock("soar"){{
            requirements(Category.units, with(cupronickel, 120, bauxite, 90, ferrosilicon, 40));
            unit = AquaUnitTypes.soar;
            size = 3;
            time = 15*60;
            destroySound = AquaSounds.start2;
        }};
        raze = new UnitBlock("raze"){{
            requirements(Category.units, with(copper, 120, bauxite, 90, ferrosilicon, 40));
            unit = AquaUnitTypes.raze;
            size = 3;
            time = 20*60;
            destroySound = AquaSounds.start4;
        }};
        shatter = new UnitBlock("shatter"){{
            requirements(Category.units, with(cupronickel, 250, silicon, 100, steel, 60));
            unit = AquaUnitTypes.shatter;
            size = 3;
            time = 20*60;
            destroySound = AquaSounds.start;
        }};
        castellan = new UnitBlock("castellan"){{
            requirements(Category.units, with(cupronickel, 400, ferrosilicon, 100, aluminum, 60));
            unit = AquaUnitTypes.castellan;
            size = 3;
            time = 25*60;
            destroySound = AquaSounds.start4;
        }};

        overwrite(Blocks.groundFactory, (UnitFactory r) -> r.plans.addAll(
                new UnitFactory.UnitPlan(isop, 60f * 20, with(Items.silicon, 25, nickel, 10))


        ));
        overwrite(Blocks.airFactory, (UnitFactory r) -> r.plans.addAll(
                new UnitFactory.UnitPlan(frost, 60f * 10, with(Items.silicon, 10, titanium, 20)),
                new UnitFactory.UnitPlan(cog, 60f * 35, with(Items.silicon, 20, brimstone, 30))


        ));
        overwrite(Blocks.additiveReconstructor, (Reconstructor r) -> r.upgrades.addAll(
                new UnitType[]{isop, empusa},
                new UnitType[]{frost, rime},
                new UnitType[]{cog, tenon}

        ));
        overwrite(Blocks.multiplicativeReconstructor, (Reconstructor r) -> r.upgrades.addAll(
                new UnitType[]{empusa, oratoria},
                 new UnitType[]{rime, verglas}

        ));
        overwrite(Blocks.exponentialReconstructor, (Reconstructor r) -> r.upgrades.addAll(
                new UnitType[]{oratoria, rhombodera},
                new UnitType[]{verglas, glaciate}

        ));
        overwrite(Blocks.exponentialReconstructor, (Reconstructor r) -> r.upgrades.addAll(
                new UnitType[]{rhombodera, parasphendale},
                new UnitType[]{glaciate, permafrost}

        ));
    }
}
