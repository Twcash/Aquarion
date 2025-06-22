package aquarion.blocks;

import aquarion.world.blocks.defense.AquaWall;
import aquarion.world.blocks.defense.BarricadeAbsorb;
import aquarion.world.graphics.AquaPal;
import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.content.Planets;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.EmptyBulletType;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.haze;
import static aquarion.planets.AquaPlanets.*;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;

public class AquaDefense {
    public static Block nickelWall, hugeNickelWall, nickelBarricade, bauxiteWall, hugeBauxiteWall, aluminumWall, hugeAluminumWall,
            cupronickelWall, hugeCupronickelWall, ferrosilconWall, hugeFerrosiliconWall, bauxiteBarricade;


    public static void loadContent() {
        //TODO balance walls

        bauxiteWall = new AquaWall("bauxite-wall") {{
            requirements(Category.defense, with(bauxite, 24));
            health = 1000;
            size = 2;
            researchCostMultiplier = 0.02f;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeBauxiteWall = new AquaWall("huge-bauxite-wall") {{
            requirements(Category.defense, with(bauxite, 54));
            health = 2250;
            size = 3;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

            researchCostMultiplier = 0.25f;
        }};
        bauxiteBarricade = new Wall("bauxite-barricade"){{
            requirements(Category.defense, with(bauxite, 500, metaglass, 200, silicon, 600, copper, 250));
            size = 5;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            health = (int) (6250 * 0.75f);
            armor = 12;
            destroyBulletSameTeam = true;
            destroyBullet = new EmptyBulletType(){{
                fragBullets = 16;
                fragBullet = new BasicBulletType(10, 120, "aquarion-bolt"){{
                    lifetime = 5;
                    pierce = true;
                    knockback = 5;
                    width = height = 12;
                    trailLength = 12;
                    despawnEffect = Fx.hitSquaresColor;
                    collidesGround = collidesAir = true;
                }};
            }};
            destroyEffect =  new ExplosionEffect(){{
                waveColor = Pal.surge;
                smokeColor = AquaPal.bauxiteLightTone;
                sparkColor = Pal.sap;
                waveStroke = 4f;
                waveRad = 7*8f;
                waveLife = 20;
                smokeSize = 8;
            }};
            researchCostMultiplier = 0.3f;
        }};
        nickelWall = new AquaWall("nickel-wall") {{
            requirements(Category.defense, with(nickel, 24));
            health = 290*4;
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            alwaysUnlocked = true;
            researchCostMultiplier = 0.1f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeNickelWall = new AquaWall("huge-nickel-wall") {{
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
        aluminumWall = new AquaWall("aluminum-wall") {{
            requirements(Category.defense, with(aluminum, 24));
            health = 2400;
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
        hugeAluminumWall = new AquaWall("huge-aluminum-wall") {{
            requirements(Category.defense, with(aluminum, 54));
            health = 5400;
            size = 3;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
        ferrosilconWall = new AquaWall("ferrosilicon-wall") {{
            requirements(Category.defense, with(ferrosilicon, 24));
            health = 3200;
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
        hugeFerrosiliconWall = new AquaWall("huge-ferrosilicon-wall") {{
            requirements(Category.defense, with(ferrosilicon, 54));
            health = 7200;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

            size = 3;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
    }
}
