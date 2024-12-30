package aquarion.blocks;

import aquarion.world.blocks.units.QeralterUnitFactory;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.type.Category;
import mindustry.world.Block;

import static aquarion.AquaItems.*;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;

public class AquaUnitFactories {
    public static Block AssaultFactory;
    public static void loadContent(){
        AssaultFactory = new QeralterUnitFactory("assault-factory"){{
            requirements(Category.units, with(copper, 40, silicon, 90, titanium, 120, arsenic, 90));
            itemCapacity = 100;
            plans = Seq.with(
                    new UnitPlan(UnitTypes.dagger, 60f * 15, with(Items.silicon, 10, Items.lead, 10)),
                    new UnitPlan(UnitTypes.crawler, 60f * 10, with(Items.silicon, 8, Items.coal, 10)),
                    new UnitPlan(UnitTypes.nova, 60f * 40, with(Items.silicon, 30, Items.lead, 20, Items.titanium, 20))
            );
            size = 3;
            maxUnitsPerFactory = 4;
            consumePower(128/60f);
        }};
    }
}
