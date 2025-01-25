package aquarion.blocks;

import aquarion.world.blocks.defense.BlockingForceProjector;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static mindustry.content.Items.lead;
import static mindustry.content.Items.metaglass;
import static mindustry.content.Liquids.hydrogen;
import static mindustry.type.ItemStack.with;

public class AquaDefense {
    public static Block bauxiteWall, hugeBauxiteWall, aluminumWall, hugeAluminumWall, cupronickelWall, hugeCupronickelWall, duraluminWall, forceBarrier, galliumWall, lithoniteWall, manganeseWall, mendHive, mendPylon, smallBauxiteWall, smallDuraluminWall, smallGalliumWall, smallLithoniteWall, smallManganeseWall;


    public static void loadContent() {
        //Walls
        //TODO balance walls

        bauxiteWall = new Wall("bauxite-wall") {{
            requirements(Category.defense, with(bauxite, 24));
            health = 512;
            size = 2;
            researchCostMultiplier = 0.1f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeBauxiteWall = new Wall("huge-bauxite-wall") {{
            requirements(Category.defense, with(bauxite, 54));
            health = 1161;
            size = 3;
            researchCostMultiplier = 0.1f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        aluminumWall = new Wall("aluminum-wall") {{
            requirements(Category.defense, with(aluminum, 24));
            health = 1000;
            size = 2;
            researchCostMultiplier = 0.1f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeAluminumWall = new Wall("huge-aluminum-wall") {{
            requirements(Category.defense, with(aluminum, 54));
            health = 2250;
            size = 3;
            researchCostMultiplier = 0.1f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        cupronickelWall = new Wall("cupronickel-wall") {{
            requirements(Category.defense, with(cupronickel, 24));
            health = (int) ((int)2250*1.5f);;
            size = 2;
            researchCostMultiplier = 0.1f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeCupronickelWall = new Wall("huge-cupronickel-wall") {{
            requirements(Category.defense, with(cupronickel, 54));
            health = (int) ((int)2250*1.5f);
            size = 3;
            researchCostMultiplier = 0.1f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        forceBarrier = new BlockingForceProjector("force-barrier"){{
            requirements(Category.defense, ItemStack.with(lead,80, duralumin, 120, metaglass, 90, nitride, 150));
            consumePower(3f / 60f);
            sides = 18;
            radius = 120;
            shieldRotation = 0;
            outputsPower = false;
            hasPower = true;
            consumesPower = true;
            conductivePower = true;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
            shieldHealth = 660;
            size = 5;
        }};
    }
}
