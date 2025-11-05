package aquarion.content.blocks;

import aquarion.world.blocks.defense.AquaWall;
import aquarion.world.graphics.AquaPal;
import mindustry.content.Fx;
import mindustry.content.Planets;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.EmptyBulletType;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaPlanets.*;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;

public class DefenseBlocks {
    public static Block defunctWall, smallDefunctWall, chalkalloyWall, chalkalloyWallLarge, zincWall, hugeZincWall, polymerWall, hugePolymerWall, steelWall, hugeSteelWall, nickelWall, hugeNickelWall, nickelBarricade, bauxiteWall, hugeBauxiteWall, aluminumWall, hugeAluminumWall,
            cupronickelWall, hugeCupronickelWall, ferrosilconWall, hugeFerrosiliconWall, bauxiteBarricade;


    public static void loadContent() {
        bauxiteWall = new AquaWall("bauxite-wall") {{
            requirements(Category.defense, with(bauxite, 24));
            health = 1000;
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeBauxiteWall = new AquaWall("huge-bauxite-wall") {{
            requirements(Category.defense, with(bauxite, 54));
            health = 2250;
            size = 3;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

        }};
        zincWall = new AquaWall("zinc-wall") {{
            requirements(Category.defense, with(zinc, 24));
            health = 1200;
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeZincWall = new AquaWall("huge-zinc-wall") {{
            requirements(Category.defense, with(zinc, 54));
            health = 2450;
            size = 3;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        bauxiteBarricade = new AquaWall("bauxite-barricade") {{
            requirements(Category.defense, with(bauxite, 500, metaglass, 200, silicon, 600, copper, 250));
            size = 5;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            health = (int) (6250 * 0.75f);
            armor = 12;
            destroyBulletSameTeam = true;
            destroyBullet = new EmptyBulletType() {{
                fragBullets = 24;
                fragBullet = new BasicBulletType(10, 90, "aquarion-bolt") {{
                    lifetime = 15;
                    pierce = true;
                    knockback = 15;
                    width = height = 12;
                    trailLength = 12;
                    despawnEffect = Fx.hitSquaresColor;
                    collidesGround = collidesAir = true;
                }};
            }};
            destroyEffect = new ExplosionEffect() {{
                waveColor = Pal.surge;
                smokeColor = AquaPal.bauxiteLightTone;
                sparkColor = Pal.sap;
                waveStroke = 4f;
                waveRad = 7 * 8f;
                waveLife = 20;
                smokeSize = 8;
            }};
            researchCostMultiplier = 0.3f;
        }};
        nickelWall = new AquaWall("nickel-wall") {{
            requirements(Category.defense, with(nickel, 24));
            health = 290 * 4;
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            alwaysUnlocked = true;
            researchCostMultiplier = 0.1f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeNickelWall = new AquaWall("huge-nickel-wall") {{
            requirements(Category.defense, with(nickel, 54));
            health = 290 * 9;
            size = 3;
            alwaysUnlocked = true;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            researchCostMultiplier = 0.25f;
        }};
        cupronickelWall = new AquaWall("cupronickel-wall") {{
            requirements(Category.defense, with(cupronickel, 24));
            health = 450 * 4;
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            researchCostMultiplier = 0.1f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeCupronickelWall = new AquaWall("huge-cupronickel-wall") {{
            requirements(Category.defense, with(cupronickel, 54));
            health = 450 * 9;
            size = 3;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            researchCostMultiplier = 0.25f;
        }};
        nickelBarricade = new AquaWall("nickel-barricade") {{
            requirements(Category.defense, with(nickel, 400, silicon, 750, metaglass, 300));
            size = 5;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            health = 500 * 25;
            armor = 15;
            researchCostMultiplier = 0.5f;
        }};
        aluminumWall = new AquaWall("aluminum-wall") {{
            requirements(Category.defense, with(aluminum, 24));
            health = 2400;
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
        hugeAluminumWall = new AquaWall("huge-aluminum-wall") {{
            requirements(Category.defense, with(aluminum, 54));
            health = 5400;
            size = 3;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
        polymerWall = new AquaWall("polymer-wall") {{
            requirements(Category.defense, with(polymer, 24));
            health = 200 * 4;
            size = 2;
            flammabilityScale = 2;
            insulated = true;
            chanceDeflect = 0.1f;
            absorbLasers = true;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            researchCostMultiplier = 0.1f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugePolymerWall = new AquaWall("huge-polymer-wall") {{
            requirements(Category.defense, with(polymer, 54));
            health = 200 * 9;
            size = 3;
            absorbLasers = true;
            flammabilityScale = 2;
            insulated = true;
            chanceDeflect = 0.1f;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            researchCostMultiplier = 0.25f;
        }};
        steelWall = new AquaWall("steel-wall") {{
            requirements(Category.defense, with(steel, 24));
            health = 400 * 4;
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            researchCostMultiplier = 0.1f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeSteelWall = new AquaWall("huge-steel-wall") {{
            requirements(Category.defense, with(steel, 54));
            health = 400 * 9;
            size = 3;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            researchCostMultiplier = 0.25f;
        }};
        ferrosilconWall = new AquaWall("ferrosilicon-wall") {{
            requirements(Category.defense, with(ferrosilicon, 24));
            health = 3200;
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
        hugeFerrosiliconWall = new AquaWall("huge-ferrosilicon-wall") {{
            requirements(Category.defense, with(ferrosilicon, 54));
            health = 7200;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

            size = 3;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
        chalkalloyWall = new Wall("chalkalloy-wall"){{
            requirements(Category.defense, with(chalkalloy, 6));
            health = 110 * 4;
        }};
        chalkalloyWallLarge = new Wall("chalkalloy-wall-large"){{
            requirements(Category.defense, ItemStack.mult(chalkalloyWall.requirements, 4));
            health = 110 * 4 * 4;
            size = 2;
        }};
        smallDefunctWall = new Wall("defunct-wall-small"){{
            requirements(Category.defense, ItemStack.with(lead, 10, silicon, 15));
            size = 1;
            health = 250;
            buildVisibility = BuildVisibility.sandboxOnly;
            category = Category.defense;
        }};
        defunctWall = new Wall("defunct-wall"){{
            requirements(Category.defense, ItemStack.mult(smallDefunctWall.requirements, 4));
            size = 2;
            health = 1000;
            buildVisibility = BuildVisibility.sandboxOnly;
            category = Category.defense;
        }};

    }
}
