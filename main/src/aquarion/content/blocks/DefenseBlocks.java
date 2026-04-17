package aquarion.content.blocks;

import aquarion.world.AI.NewMissileAI;
import aquarion.world.MultiBlockLib.drawrr.DrawRegionCenterSymmetry;
import aquarion.world.MultiBlockLib.drawrr.DrawRegionRotated;
import aquarion.world.blocks.defense.AquaWall;
import aquarion.world.blocks.defense.MissileBlock;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.AquaPal;
import arc.graphics.Color;
import mindustry.ai.types.CommandAI;
import mindustry.ai.types.FlyingAI;
import mindustry.ai.types.MissileAI;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.content.Planets;
import mindustry.entities.abilities.MoveEffectAbility;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.EmptyBulletType;
import mindustry.entities.bullet.ExplosionBulletType;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.Weapon;
import mindustry.type.unit.MissileUnitType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaPlanets.*;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;

public class DefenseBlocks {
    public static Block forceGenerator, meteor, defunctWall, smallDefunctWall, chalkalloyWall, chalkalloyWallLarge, zincWall, hugeZincWall, polymerWall, hugePolymerWall, steelWall, hugeSteelWall, nickelWall, hugeNickelWall, nickelBarricade, bauxiteWall, hugeBauxiteWall, aluminumWall, hugeAluminumWall,
            cupronickelWall, hugeCupronickelWall, vesta, ferrosilconWall, hugeFerrosiliconWall, bauxiteBarricade;


    public static void loadContent() {
        meteor = new MissileBlock("meteor"){{
            requirements(Category.turret, with(copper, 90, lead, 60, graphite, 90));
            size = 1;
            canMirror = false;
            drawer = new DrawRegionRotated(){{
                suffix = "-rot";
                x = 4;
            }};
            consumePower(2);
            addLink(1,0,1);
            spawn = new MissileUnitType("meteor-unit"){{
                speed = 4.6f;
                maxRange = 6f;
                lifetime = 60f * 3f;
                omniMovement = false;
                hitSize = 10f;
                outlineColor = AquaPal.tantDarkestTone;
                engineColor = trailColor = Pal.redLight;
                engineLayer = Layer.effect;
                engineSize = 3.1f;
                engineOffset = 10f;
                controller = u -> new NewMissileAI();
                rotateSpeed = 0.25f;
                drawCell = false;
                circleTarget = true;
                trailLength = 18;
                missileAccelTime = 50f;
                lowAltitude = true;
                loopSound = Sounds.loopMissileTrail;
                loopSoundVolume = 0.6f;
                deathSound = Sounds.explosionMissile;
                range = 250;
                targetAir = true;
                targetUnderBlocks = false;
                fogRadius = 6f;
                health = 240;
                weapons.add(new Weapon(){{
                    shootCone = 360f;
                    mirror = false;
                    reload = 1f;
                    deathExplosionEffect = Fx.massiveExplosion;
                    shootOnDeath = true;
                    shake = 10f;
                    bullet = new ExplosionBulletType(300f, 25f){{
                        hitColor = Pal.redLight;
                        shootEffect = new MultiEffect(Fx.massiveExplosion, new WaveEffect(){{
                            lifetime = 20f;
                            strokeFrom = 2f;
                            sizeTo = 90f;;
                        }});
                        collidesAir = true;
                    }};
                }});

                abilities.add(new MoveEffectAbility(){{
                    effect = Fx.missileTrailSmoke;
                    rotation = 180f;
                    y = -9f;
                    color = Color.grays(0.6f).lerp(Pal.redLight, 0.5f).a(0.4f);
                    interval = 7f;
                }});
            }};
        }};
        vesta = new MissileBlock("vesta"){{
            requirements(Category.turret, with(metaglass, 250, silicon, 500, copper, 500, polymer, 150));
            size = 1;
            canMirror = false;
            drawer = new DrawRegionRotated(){{
                suffix = "-rot";
                x = 4;
            }};
            range = 400;
            time = 600;
            consumePower(6);
            addLink(1,0,1, -1,0,1,2,0,1);
            spawn = new MissileUnitType("vesta-unit"){{
                speed = 2.6f;
                maxRange = 6f;
                lifetime = 60f * 3f;
                range = 450;
                omniMovement = false;
                hitSize = 10f;
                outlineColor = AquaPal.tantDarkestTone;
                engineColor = trailColor = Pal.redLight;
                engineLayer = Layer.effect;
                engineSize = 3.1f;
                engineOffset = 10f;
                controller = u -> new NewMissileAI();
                rotateSpeed = 0.25f;
                drawCell = false;
                circleTarget = true;
                trailLength = 18;
                missileAccelTime = 50f;
                lowAltitude = true;
                loopSound = Sounds.loopMissileTrail;
                loopSoundVolume = 0.6f;
                deathSound = Sounds.explosionMissile;

                targetAir = true;
                targetUnderBlocks = false;
                fogRadius = 6f;
                health = 400;
                weapons.add(new Weapon(){{
                    shootCone = 360f;
                    mirror = false;
                    reload = 1f;
                    deathExplosionEffect = Fx.massiveExplosion;
                    shootOnDeath = true;
                    shake = 10f;
                    bullet = new ExplosionBulletType(500f, 45f){{
                        hitColor = Pal.redLight;
                        shootEffect = new MultiEffect(Fx.massiveExplosion, Fx.scatheExplosionSmall, new WaveEffect(){{
                            lifetime = 20f;
                            strokeFrom = 2f;
                            sizeTo = 90f;;
                        }});
                        collidesAir = true;
                    }};
                }});

                abilities.add(new MoveEffectAbility(){{
                    effect = Fx.missileTrailSmoke;
                    rotation = 180f;
                    y = -9f;
                    color = Color.grays(0.6f).lerp(Pal.redLight, 0.5f).a(0.4f);
                    interval = 7f;
                }});
            }};
        }};
        bauxiteWall = new AquaWall("bauxite-wall") {{
            requirements(Category.defense, with(bauxite, 24));
            health = 1000;
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.bauxiteDestroy);

        }};
        hugeBauxiteWall = new AquaWall("huge-bauxite-wall") {{
            requirements(Category.defense, with(bauxite, 54));
            health = 2250;
            size = 3;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.bauxiteDestroy);

            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

        }};
        zincWall = new AquaWall("zinc-wall") {{
            requirements(Category.defense, with(zinc, 24));
            health = 1200;
            size = 2;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.zincDestroy);

            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeZincWall = new AquaWall("huge-zinc-wall") {{
            requirements(Category.defense, with(zinc, 54));
            health = 2450;
            size = 3;
            envEnabled |= Env.terrestrial | Env.underwater;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.zincDestroy);

            envDisabled = Env.none;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        bauxiteBarricade = new AquaWall("bauxite-barricade") {{
            requirements(Category.defense, with(bauxite, 500, metaglass, 200, silicon, 600, copper, 250));
            size = 5;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            health = (int) (6250 * 0.75f);
            armor = 12;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.bauxiteDestroy);

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
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.nickelDestroy);
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
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.nickelDestroy);
            alwaysUnlocked = true;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            researchCostMultiplier = 0.25f;
        }};
        cupronickelWall = new AquaWall("cupronickel-wall") {{
            requirements(Category.defense, with(cupronickel, 24));
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.cuproDestroy);

            health = 450 * 4;
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            researchCostMultiplier = 0.1f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeCupronickelWall = new AquaWall("huge-cupronickel-wall") {{
            requirements(Category.defense, with(cupronickel, 54));
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.cuproDestroy);

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
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.nickelDestroy);

            armor = 15;
            researchCostMultiplier = 0.5f;
        }};
        aluminumWall = new AquaWall("aluminum-wall") {{
            requirements(Category.defense, with(aluminum, 24));
            health = 2400;
            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

            envEnabled |= Env.terrestrial | Env.underwater;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.aluminumDestroy);

            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
        hugeAluminumWall = new AquaWall("huge-aluminum-wall") {{
            requirements(Category.defense, with(aluminum, 54));
            health = 5400;
            size = 3;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.aluminumDestroy);

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
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.polymerDestroy);

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
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.polymerDestroy);

            insulated = true;
            chanceDeflect = 0.1f;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            researchCostMultiplier = 0.25f;
        }};
        steelWall = new AquaWall("steel-wall") {{
            requirements(Category.defense, with(steel, 24));
            health = 400 * 4;
            size = 2;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.steelDestroy);

            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            researchCostMultiplier = 0.1f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        hugeSteelWall = new AquaWall("huge-steel-wall") {{
            requirements(Category.defense, with(steel, 54));
            health = 400 * 9;
            size = 3;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.steelDestroy);

            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            researchCostMultiplier = 0.25f;
        }};
        ferrosilconWall = new AquaWall("ferrosilicon-wall") {{
            requirements(Category.defense, with(ferrosilicon, 24));
            health = 3200;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.ferroDestroy);

            size = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            researchCostMultiplier = 0.25f;
        }};
        hugeFerrosiliconWall = new AquaWall("huge-ferrosilicon-wall") {{
            requirements(Category.defense, with(ferrosilicon, 54));
            health = 7200;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.ferroDestroy);

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
        forceGenerator = new ForceProjector("force-generator"){{
            requirements(Category.effect, ItemStack.with(silicon, 250, copper, 1000, metaglass, 200));
            size = 3;
            squareSprite = false;
            consumeLiquid(Liquids.water, 2);
            consumePower(9);
            shieldHealth = 1500;
            cooldownBrokenBase = 2f;
            cooldownNormal = 0.5f;
            radius = 200;
            sides = 4;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
        }};

    }
}
