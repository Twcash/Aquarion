package aquarion.blocks;

import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static mindustry.type.ItemStack.with;

public class AquaDefense {
    public static Block bauxiteWall, hugeBauxiteWall, aluminumWall, hugeAluminumWall,
            cupronickelWall, hugeCupronickelWall, duraluminWall,
            galliumWall, lithoniteWall, manganeseWall, smallBauxiteWall,
            smallDuraluminWall, smallGalliumWall,
            smallLithoniteWall, smallManganeseWall;


    public static void loadContent() {
        //TODO balance walls

        bauxiteWall = new Wall("bauxite-wall") {{
            requirements(Category.defense, with(bauxite, 24));
            health = 512;
            size = 2;
            alwaysUnlocked = true;
            researchCostMultiplier = 0.1f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeBauxiteWall = new Wall("huge-bauxite-wall") {{
            requirements(Category.defense, with(bauxite, 54));
            health = 1161;
            size = 3;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
        aluminumWall = new Wall("aluminum-wall") {{
            requirements(Category.defense, with(aluminum, 24));
            health = 1000;
            size = 2;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
        hugeAluminumWall = new Wall("huge-aluminum-wall") {{
            requirements(Category.defense, with(aluminum, 54));
            health = 2250;
            size = 3;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
        cupronickelWall = new Wall("cupronickel-wall") {{
            requirements(Category.defense, with(cupronickel, 24));
            health = (int) (2250 *1.5f);
            size = 2;
            researchCostMultiplier = 0.25f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeCupronickelWall = new Wall("huge-cupronickel-wall") {{
            requirements(Category.defense, with(cupronickel, 54));
            health = (int) (2250 *1.5f);
            size = 3;
            researchCostMultiplier = 0.25f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
    }
}
