package aquarion.blocks;

import aquarion.units.ProspectorUnitTypes;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.power.Battery;
import mindustry.world.blocks.power.SolarGenerator;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

public class ProspectorBlocks {
    public static Block fort, barricade,
            capacitor, solarArray, nexus, guardian,
    //turrets
            bend, splinter, fracture,disparate
            // unit funnies
            , synthesisPad;

    public static void loadContent() {
        //This is a core
        nexus = new CoreBlock("nexus"){{
            requiresCoreZone = false;
            buildCostMultiplier = 60*10f;
            unitType = ProspectorUnitTypes.synthesis;
            conductivePower = true;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};
        //walls. I most likely will only ever have two.
        fort = new Wall("fort"){{
            buildVisibility = BuildVisibility.sandboxOnly;
            buildCostMultiplier = 60*1.5f;
            size = 2;
            health = 32*4;
            conductivePower = true;
        }};
        barricade = new Wall("barricade"){{
            buildVisibility = BuildVisibility.sandboxOnly;
            size = 3;
            //Lmao I hope I never do this again
            buildCostMultiplier = 60*1.5f/4f*9f;
            health = 32*4;
            conductivePower = true;
        }};
        //power related stuffs
        solarArray = new SolarGenerator("solar-array"){{
            buildVisibility = BuildVisibility.sandboxOnly;
            size = 2;
            buildCostMultiplier = 60*2f;
            health = 90;
            powerProduction = 1;
            conductivePower = true;
        }};
        capacitor = new Battery("capacitor"){{
            buildVisibility = BuildVisibility.sandboxOnly;
            size = 3;
            consumePowerBuffered(4000f);
            buildCostMultiplier = 60*2.5f;
            conductivePower = true;
        }};
        //turrets

    }
}
