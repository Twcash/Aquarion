package aquarion.blocks;

import aquarion.AquaSounds;
import aquarion.units.AquaUnitTypes;
import aquarion.world.blocks.units.QeralterUnitFactory;
import aquarion.world.blocks.units.UnitBlock;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.type.Category;
import mindustry.world.Block;

import static aquarion.AquaItems.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.oil;
import static mindustry.type.ItemStack.with;

public class AquaUnitFactories {
    public static Block bulwark, pugnate, rampart, crest, reave, soar, raze, shatter, castellan;

    public static void loadContent(){
        bulwark = new UnitBlock("bulwark"){{
            requirements(Category.units, with(silicon, 80, metaglass, 60, nickel, 50));
            unit = AquaUnitTypes.bulwark;
            size = 2;
            time = 15*60;
            consumePower(100/60f);
        }};
       pugnate = new UnitBlock("pugnate"){{
            requirements(Category.units, with(silicon, 90, aluminum, 50, copper, 70));
            unit = AquaUnitTypes.pugnate;
            size = 2;
           time = 20*60;
           consumePower(50/60f);
       }};
        rampart = new UnitBlock("rampart"){{
            requirements(Category.units, with(nickel, 120, copper, 150, silicon, 60));
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
        reave = new UnitBlock("reave"){{
            requirements(Category.units, with(ferrosilicon, 70, copper, 90, metaglass, 110, lead, 80));
            unit = AquaUnitTypes.reave;
            size = 3;
            time = 20*60;
            destroySound = AquaSounds.start4;
        }};
        soar = new UnitBlock("soar"){{
            requirements(Category.units, with(copper, 120, bauxite, 90, ferrosilicon, 40));
            unit = AquaUnitTypes.soar;
            size = 3;
            time = 15*60;
            destroySound = AquaSounds.start2;
        }};
        raze = new UnitBlock("raze"){{
            requirements(Category.units, with(copper, 120, bauxite, 90, ferrosilicon, 40));
            unit = AquaUnitTypes.raze;
            size = 3;
            time = 25*60;
            destroySound = AquaSounds.start4;
        }};
        shatter = new UnitBlock("shatter"){{
            requirements(Category.units, with(nickel, 250, silicon, 100, titanium, 60));
            unit = AquaUnitTypes.shatter;
            size = 3;
            time = 20*60;
            destroySound = AquaSounds.start;
        }};
        castellan = new UnitBlock("castellan"){{
            requirements(Category.units, with(nickel, 400, ferrosilicon, 100, aluminum, 60));
            unit = AquaUnitTypes.castellan;
            size = 3;
            time = 30*60;
            destroySound = AquaSounds.start4;
        }};
    }
}
