package aquarion.blocks;

import aquarion.AquaItems;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.AquaPal;
import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootMulti;
import mindustry.entities.pattern.ShootPattern;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.draw.DrawTurret;
import mindustry.world.meta.Env;

import static mindustry.type.ItemStack.with;

public class AquaTurrets {
    public static Block Forment, Fragment;

    public static void loadContent() {
        //oogly boogly
        Forment = new ItemTurret("forment") {{
            {
                ammo(
                        AquaItems.bauxite,  new MissileBulletType(2.5f, 10, "bullet"){{
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
                        AquaItems.gallium,  new MissileBulletType(2f, 18, "bullet"){{
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
                        AquaItems.nitride,  new MissileBulletType(3.5f, 14, "bullet"){{
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
            envDisabled |= Env.spores | Env.scorching;
        }};
    }
}
