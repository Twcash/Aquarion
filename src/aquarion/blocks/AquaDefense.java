package aquarion.blocks;

import aquarion.world.blocks.defense.BarricadeAbsorb;
import mindustry.content.Planets;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.haze;
import static aquarion.planets.AquaPlanets.*;
import static mindustry.content.Items.metaglass;
import static mindustry.content.Items.silicon;
import static mindustry.type.ItemStack.with;

public class AquaDefense {
    public static Block nickelWall, hugeNickelWall, nickelBarricade, bauxiteWall, hugeBauxiteWall, aluminumWall, hugeAluminumWall,
            cupronickelWall, hugeCupronickelWall, ferrosilconWall, hugeFerrosiliconWall;


    public static void loadContent() {
        //TODO balance walls

        bauxiteWall = new Wall("bauxite-wall") {{
            requirements(Category.defense, with(bauxite, 24));
            health = 1000;
            size = 2;
            alwaysUnlocked = true;
            researchCostMultiplier = 0.1f;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeBauxiteWall = new Wall("huge-bauxite-wall") {{
            requirements(Category.defense, with(bauxite, 54));
            health = 2250;
            size = 3;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

            researchCostMultiplier = 0.25f;
        }};
        nickelWall = new Wall("nickel-wall") {{
            requirements(Category.defense, with(nickel, 24));
            health = 290*4;
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            alwaysUnlocked = true;
            researchCostMultiplier = 0.1f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeNickelWall = new Wall("huge-nickel-wall") {{
            requirements(Category.defense, with(nickel, 54));
            health = 290*9;
            size = 3;
            alwaysUnlocked = true;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            researchCostMultiplier = 0.25f;
        }};
        nickelBarricade = new BarricadeAbsorb("nickel-barricade"){{
            requirements(Category.defense, with(nickel, 250, silicon, 500, metaglass, 150));
            size = 5;
            range = 10f;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            health = 5000;
            researchCostMultiplier = 0.3f;
        }};
        aluminumWall = new Wall("aluminum-wall") {{
            requirements(Category.defense, with(aluminum, 24));
            health = 2400;
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
        hugeAluminumWall = new Wall("huge-aluminum-wall") {{
            requirements(Category.defense, with(aluminum, 54));
            health = 5400;
            size = 3;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
        ferrosilconWall = new Wall("ferrosilicon-wall") {{
            requirements(Category.defense, with(ferrosilicon, 24));
            health = 3200;
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
        hugeFerrosiliconWall = new Wall("huge-ferrosilicon-wall") {{
            requirements(Category.defense, with(ferrosilicon, 54));
            health = 7200;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

            size = 3;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
        cupronickelWall = new Wall("cupronickel-wall") {{
            requirements(Category.defense, with(cupronickel, 24));
            health = (int) (2250 *1.5f);
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

            researchCostMultiplier = 0.25f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            alwaysUnlocked = true;
        }};

        hugeCupronickelWall = new Wall("huge-cupronickel-wall") {{
            requirements(Category.defense, with(cupronickel, 54));
            health = (int) (2250 *1.5f);
            size = 3;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

            researchCostMultiplier = 0.25f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            alwaysUnlocked = true;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

        }};
    }
}
