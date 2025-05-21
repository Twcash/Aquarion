package aquarion.blocks;

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
import static mindustry.type.ItemStack.with;

public class AquaUnitFactories {
    public static Block bulwark, pugnate, rampart, crest;

    public static void loadContent(){
        bulwark = new UnitBlock("bulwark"){{
            requirements(Category.units, with(silicon, 80, metaglass, 60, nickel, 50));
            unit = AquaUnitTypes.bulwark;
            size = 2;
            time = 15*60;
        }};
       pugnate = new UnitBlock("pugnate"){{
            requirements(Category.units, with(silicon, 90, aluminum, 50, copper, 70));
            unit = AquaUnitTypes.pugnate;
            size = 2;
           time = 20*60;
       }};
        rampart = new UnitBlock("rampart"){{
            requirements(Category.units, with(arsenic, 120, copper, 90, silicon, 60));
            unit = AquaUnitTypes.rampart;
            size = 2;
            time = 10*60;
        }};
        crest = new UnitBlock("crest"){{
            requirements(Category.units, with(silicon, 40, copper, 60, metaglass, 50));
            unit = AquaUnitTypes.crest;
            size = 2;
            time = 5*60;
       }};
    }
}
