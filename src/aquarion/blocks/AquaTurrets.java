package aquarion.blocks;

import aquarion.AquaItems;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.AquaPal;
import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.*;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.draw.DrawTurret;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.ceramic;
import static aquarion.AquaItems.chirenium;
import static mindustry.gen.Sounds.shootAltLong;
import static mindustry.type.ItemStack.with;

public class AquaTurrets {
    public static Block Forment, Fragment, gyre, deviate;

    public static void loadContent() {
        //oogly boogly
        Forment = new ItemTurret("forment") {{
            {
                outlineColor = AquaPal.tantDarkestTone;
                ammo(
                        Items.lead,  new MissileBulletType(2.5f, 18, "bullet"){{
                            width = 10f;
                            height = 16f;
                            trailLength = 12;

                            lifetime = 60f;
                            ammoMultiplier = 1;
                            shootEffect = AquaFx.shootLong;
                            smokeEffect =  new MultiEffect(AquaFx.shootSmokeTri, AquaFx.shootSmokeFormentBauxite);
                            trailEffect = Fx.none;
                            weaveMag = 2;
                            homingPower = 0.05f;
                            homingDelay = 5;
                            weaveScale = 1.75f;
                            shrinkX = 0.2f;
                            shrinkY = 0.8f;
                            frontColor = lightColor = hitColor = AquaPal.bauxiteShoot;
                            backColor = trailColor = AquaPal.bauxiteLightTone;
                            buildingDamageMultiplier = 0.3f;
                        }},
                        AquaItems.gallium,  new MissileBulletType(2f, 26, "bullet"){{
                            width = 12f;
                            height = 18f;


                            trailLength = 8;
                            lifetime = 60f;
                            ammoMultiplier = 2;
                            heatColor = AquaPal.galliumLightTone;
                            trailEffect = Fx.none;
                            shootEffect = AquaFx.shootLong;
                            smokeEffect =  new MultiEffect(AquaFx.shootSmokeTri, AquaFx.shootSmokeFormentGallium);
                            weaveMag = 2;
                            homingPower = 0.02f;
                            homingDelay = 10;
                            weaveScale = 1.75f;
                            shrinkX = 0.2f;
                            shrinkY = 0.8f;
                            frontColor = lightColor = hitColor = Color.white;
                            backColor = trailColor = AquaPal.galliumLightTone;
                            buildingDamageMultiplier = 0.3f;
                        }},
                        AquaItems.nitride,  new MissileBulletType(3.5f, 20, "bullet"){{
                            width = 9f;
                            height = 15f;
                            trailLength = 10;
                            lifetime = 60f;


                            ammoMultiplier = 3;
                            rangeChange = 32;
                            trailEffect = Fx.none;
                            heatColor = Color.white;
                            shootEffect = AquaFx.shootLong;
                            smokeEffect =  new MultiEffect(AquaFx.shootSmokeTri, AquaFx.shootSmokeFormentGallium);
                            weaveMag = 2;
                            homingPower = 0.09f;
                            homingDelay = 10;
                            weaveScale = 1.75f;
                            shrinkX = 0.2f;
                            shrinkY = 0.8f;
                            frontColor = lightColor = hitColor = Color.white;
                            backColor = trailColor = Color.gray;
                            buildingDamageMultiplier = 0.3f;
                        }});
                requirements(Category.turret, with(Items.lead, 90, AquaItems.bauxite, 60f));
                size = 3;
                squareSprite = false;
                range = 120;
                limitRange(1.1f);
                reload = 60;
                consumeAmmoOnce = true;
                maxAmmo = 45;
                inaccuracy = 2;
                shoot.shots = 3;
                ammoPerShot = 2;
                researchCostMultiplier = 0.03f;
                xRand = 0;
                shoot.shotDelay = 5;
                shoot = new ShootMulti() {{
                    firstShotDelay = 35;
                    source = new ShootAlternate(); // Set ShootAlternate as the source

                    dest = new ShootPattern[] {
                            new ShootBarrel() {{
                                shots = 3;
                                shotDelay = 5;
                                barrels = new float[] {
                                        0, -6, 0,
                                        5, -8f, 0,
                                        -5, -8f, 0
                                };
                            }}
                    };
                }};

                drawer = new DrawTurret() {{
                    parts.add(new RegionPart("-sides") {{
                        moveY = -3;
                        mirror = false;
                        progress = PartProgress.recoil;
                    }}, new RegionPart("-mid") {{
                        progress = PartProgress.recoil;
                        under = true;
                        mirror = true;
                        moveY = -4.5f;
                        moves.add(new PartMove(PartProgress.warmup, -1.5f, -1.5f, -5f));
                    }}, new RegionPart("-barrel"){{
                        layerOffset = -0.001f;
                        moveY = -1.5f;
                        under = true;
                        mirror = false;
                        progress = PartProgress.recoil;
                    }});
                }};
            }
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        gyre = new ItemTurret("gyre") {{
            researchCostMultiplier = 0.1f;
            requirements(Category.turret, with(Items.lead, 150, AquaItems.bauxite, 90f, ceramic, 60, chirenium, 30));
            ammo(
            chirenium,  new BasicBulletType(2.5f, 30, "bullet"){{
                width = 10f;
                height = 16f;
                trailLength = 12;
                trailWidth = 2;
                lifetime = 60f;
                ammoMultiplier = 1;
                shootEffect = AquaFx.shootLong;
                smokeEffect =  new MultiEffect(AquaFx.shootSmokeTri, Fx.shootSmokeSquareSparse);
                trailRotation = true;
                trailEffect = AquaFx.shootSmokeTri;
                trailInterval = 4;
                frontColor = lightColor = hitColor = AquaPal.chireniumLight;
                hitEffect = despawnEffect = Fx.hitSquaresColor;
                backColor = trailColor = AquaPal.chireniumDark;
                buildingDamageMultiplier = 0.3f;
            }});
            shoot = new ShootSpread(5, 3f);
            inaccuracy = 3f;
            velocityRnd = 0.17f;
            shake = 1f;
            range = 96;
            ammoPerShot = 3;
            maxAmmo = 30;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            consumeAmmoOnce = true;
            reload = 45;
            recoil = 3;
            recoilTime = 30;
            warmupMaintainTime = 20;
            minWarmup = 0.8f;
            shootWarmupSpeed = 0.05f;
            cooldownTime = 30;
            heatColor = Color.red;
            targetUnderBlocks = false;
            shootSound = shootAltLong;
            loopSound = Sounds.bioLoop;
            loopSoundVolume = 0.06f;
            shootY = 5f;
            outlineColor = AquaPal.tantDarkestTone;
            size = 3;
            envEnabled|= Env.terrestrial | Env.underwater;
            drawer = new DrawTurret("reinforced-"){{
                parts.add(new RegionPart("-side"){{
                    progress = PartProgress.warmup;
                    moveX = 2.25f;
                    moveY = 1.5f;
                    mirror = true;
                    moves.add(new PartMove(PartProgress.recoil, -0.5f, -3f, 0));
                    heatColor = Color.red;
                }});
            }};
        }};
        Fragment = new ItemTurret("fragment"){{
            requirements(Category.turret, with(Items.lead, 90, AquaItems.bauxite, 90, ceramic, 80));
            size = 3;
            outlineColor = AquaPal.tantDarkestTone;
            squareSprite = false;
            reload = 45;
            targetGround = false;
            range = 18 * 8;
            limitRange();
            shootY = 4;
            shootSound = shootAltLong;
            shoot = new ShootBarrel() {{
                    firstShotDelay = 35;
                            barrels = new float[] {
                                    -4, 4, 0,
                                    0, 4f, 0,
                                    4, 4f, 0
                            };
            }};
            ammo(
                    ceramic,  new BasicBulletType(2.5f, 45, "bullet"){{
                        width = 10f;
                        height = 19f;
                        pierceCap = 2;
                        pierce = true;
                        homingDelay = 30;
                        homingPower = 0.1f;
                        collidesGround = false;
                        trailLength = 12;
                        trailWidth = 2;
                        lifetime = 90f;
                        ammoMultiplier = 1;
                        shootEffect = AquaFx.shootLong;
                        smokeEffect =  new MultiEffect(AquaFx.shootSmokeTri, Fx.shootSmokeSquareSparse);
                        trailRotation = true;
                        trailEffect = new MultiEffect(AquaFx.shootSmokeTri, Fx.disperseTrail);
                        trailInterval = 7;
                        frontColor = lightColor = hitColor = Color.white;
                        hitEffect = despawnEffect = Fx.hitSquaresColor;
                        backColor = trailColor = Pal.gray;
                        buildingDamageMultiplier = 0.3f;
                        fragBullets = 3;
                        fragBullet = new BasicBulletType(1.5f, 30, "bullet"){{
                            width = 5f;
                            height = 11f;
                            collidesGround = false;
                            trailLength = 6;
                            trailWidth = 2;
                            lifetime = 30f;
                            trailRotation = true;
                            trailEffect = new MultiEffect(AquaFx.shootSmokeTri, Fx.disperseTrail);
                            trailInterval = 9;
                            frontColor = lightColor = hitColor = Color.white;
                            hitEffect = despawnEffect = Fx.hitSquaresColor;
                            backColor = trailColor = Pal.gray;
                            buildingDamageMultiplier = 0.3f;
                        }};
                    }});
            recoils = 3;
            drawer = new DrawTurret("reinforced-"){{
                parts.add(new RegionPart("-barrel-1"){{
                    moveY = -1f;
                    progress = PartProgress.recoil;
                    recoilIndex = 2;
                }});
                parts.add(new RegionPart("-barrel-2"){{
                    moveY = -1f;
                    progress = PartProgress.recoil;
                    recoilIndex = 1;
                }});
                parts.add(new RegionPart("-barrel-3"){{
                    moveY = -1f;
                    progress = PartProgress.recoil;
                    recoilIndex = 0;
                }});
                parts.add(new RegionPart("-back-1"){{
                    moveY = -0.5f;
                    progress = PartProgress.recoil;
                    recoilIndex = 2;
                }});
                parts.add(new RegionPart("-back-2"){{
                    moveY = -0.5f;
                    progress = PartProgress.recoil;
                    recoilIndex = 0;
                }});
            }};
        }};
    }
}
