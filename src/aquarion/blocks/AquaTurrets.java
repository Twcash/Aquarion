package aquarion.blocks;

import aquarion.AquaItems;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.AquaPal;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.FlakBulletType;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.bullet.RailBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.*;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.draw.DrawTurret;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Interp.pow2Out;
import static arc.math.Interp.pow5Out;
import static mindustry.content.Items.lead;
import static mindustry.content.Items.titanium;
import static mindustry.gen.Sounds.shootAlt;
import static mindustry.gen.Sounds.shootAltLong;
import static mindustry.type.ItemStack.with;

public class AquaTurrets {
    public static Block Forment, Fragment, gyre, Coaxis, deviate,
            ensign;

    public static void loadContent() {
        //oogly boogly
        Forment = new ItemTurret("forment") {{
            {
                outlineColor = AquaPal.tantDarkestTone;
                ammo(
                        lead, new MissileBulletType(2.5f, 18, "bullet") {{
                            width = 10f;
                            height = 16f;
                            trailLength = 12;

                            lifetime = 60f;
                            ammoMultiplier = 1;
                            shootEffect = AquaFx.shootLong;
                            smokeEffect = new MultiEffect(AquaFx.shootSmokeTri, AquaFx.shootSmokeFormentBauxite);
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
                        gallium, new MissileBulletType(2f, 26, "bullet") {{
                            width = 12f;
                            height = 18f;


                            trailLength = 8;
                            lifetime = 60f;
                            ammoMultiplier = 2;
                            heatColor = AquaPal.galliumLightTone;
                            trailEffect = Fx.none;
                            shootEffect = AquaFx.shootLong;
                            smokeEffect = new MultiEffect(AquaFx.shootSmokeTri, AquaFx.shootSmokeFormentGallium);
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
                        AquaItems.nitride, new MissileBulletType(3.5f, 20, "bullet") {{
                            width = 9f;
                            height = 15f;
                            trailLength = 10;
                            lifetime = 60f;


                            ammoMultiplier = 3;
                            rangeChange = 32;
                            trailEffect = Fx.none;
                            heatColor = Color.white;
                            shootEffect = AquaFx.shootLong;
                            smokeEffect = new MultiEffect(AquaFx.shootSmokeTri, AquaFx.shootSmokeFormentGallium);
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
                requirements(Category.turret, with(lead, 90, AquaItems.bauxite, 60f));
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

                    dest = new ShootPattern[]{
                            new ShootBarrel() {{
                                shots = 3;
                                shotDelay = 5;
                                barrels = new float[]{
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
                    }}, new RegionPart("-barrel") {{
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
            requirements(Category.turret, with(lead, 150, AquaItems.bauxite, 90f, ceramic, 60, chirenium, 30));
            ammo(
                    chirenium, new BasicBulletType(2.5f, 30, "bullet") {{
                        width = 10f;
                        height = 16f;
                        trailLength = 12;
                        trailWidth = 2;
                        lifetime = 60f;
                        ammoMultiplier = 1;
                        shootEffect = AquaFx.shootLong;
                        smokeEffect = new MultiEffect(AquaFx.pentagonShootSmoke, AquaFx.GyreShootSmoke);
                        trailRotation = true;
                        trailEffect = AquaFx.pentagonShootSmoke;
                        trailInterval = 8;
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
            envEnabled |= Env.terrestrial | Env.underwater;
            drawer = new DrawTurret("reinforced-") {{
                parts.add(new RegionPart("-side") {{
                    progress = PartProgress.warmup;
                    moveX = 2.25f;
                    moveY = 1.5f;
                    mirror = true;
                    moves.add(new PartMove(PartProgress.recoil, -0.5f, -3f, 0));
                    heatColor = Color.red;
                }});
            }};
        }};
        Fragment = new ItemTurret("fragment") {{
            requirements(Category.turret, with(lead, 90, AquaItems.bauxite, 90, ceramic, 80));
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
                barrels = new float[]{
                        -6, 4, 0,
                        0, 4f, 0,
                        6, 4f, 0
                };
            }};
            ammo(
                    lead, new MissileBulletType(2.5f, 18, "bullet") {{
                        width = 10f;
                        height = 16f;
                        trailLength = 12;

                        lifetime = 60f;
                        ammoMultiplier = 1;
                        shootEffect = AquaFx.shootLong;
                        smokeEffect = new MultiEffect(AquaFx.shootSmokeTri, AquaFx.shootSmokeFormentBauxite);
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
                    }});
            recoils = 3;
            drawer = new DrawTurret("reinforced-") {{
                parts.add(new RegionPart("-barrel-1") {{
                    moveY = -1f;
                    progress = PartProgress.recoil;
                    recoilIndex = 1;
                }});
                parts.add(new RegionPart("-barrel-2") {{
                    moveY = -1f;
                    progress = PartProgress.recoil;
                    recoilIndex = 2;
                }});
                parts.add(new RegionPart("-barrel-3") {{
                    moveY = -1f;
                    progress = PartProgress.recoil;
                    recoilIndex = 0;
                }});
                parts.add(new RegionPart("-back-1") {{
                    moveY = -0.5f;
                    progress = PartProgress.recoil;
                    recoilIndex = 2;
                }});
                parts.add(new RegionPart("-back-2") {{
                    moveY = -0.5f;
                    progress = PartProgress.recoil;
                    recoilIndex = 1;
                }});
            }};
        }};
        deviate = new ItemTurret("deviate") {{
            requirements(Category.turret, with(lead, 140, gallium, 200, ceramic, 140, lead, 240));
            size = 4;
            ammo(
                    lead, new MissileBulletType(2.5f, 18, "bullet") {{
                        width = 10f;
                        height = 16f;
                        trailLength = 12;

                        lifetime = 60f;
                        ammoMultiplier = 1;
                        shootEffect = AquaFx.shootLong;
                        smokeEffect = new MultiEffect(AquaFx.shootSmokeTri, AquaFx.shootSmokeFormentBauxite);
                        trailEffect = Fx.none;
                        weaveMag = 2;
                        homingPower = 0.05f;
                        homingDelay = 5;
                        weaveScale = 1.75f;
                        frontColor = lightColor = hitColor = AquaPal.bauxiteShoot;
                        backColor = trailColor = AquaPal.bauxiteLightTone;
                        buildingDamageMultiplier = 0.3f;
                    }});
            outlineColor = AquaPal.tantDarkestTone;
            recoil = 2;
            recoil = 3;
            recoilTime = 45;
            reload = 25;
            targetAir = false;
            shootSound = shootAltLong;
            shootWarmupSpeed = 0.08f;
            minWarmup = 0.8f;
            drawer = new DrawTurret() {
                {
                    parts.addAll(
                            new RegionPart("-mid") {{
                                mirror = true;
                                progress = PartProgress.warmup;
                                under = true;
                                moveX = 0.25f;
                                moveY = -0.5f;
                                moveRot = -4f;
                                layerOffset = -0.00001f;
                                moves.add(new PartMove(PartProgress.recoil, 0.25f, -0.5f, -3f));
                            }},
                            new RegionPart("-barrel") {{
                                progress = PartProgress.warmup;
                                moveRot = -1f;
                                moveX = 0.25f;
                                mirror = true;
                                moves.add(new PartMove(PartProgress.recoil, 0f, -1.5f, -1.5f));
                                moveY = -3.5f;
                            }},
                            new RegionPart("-side") {{
                                progress = PartProgress.warmup;
                                moveRot = -1.5f;
                                moveX = 0.5f;
                                mirror = true;
                                moves.add(new PartMove(PartProgress.recoil, 0f, -1f, -1f));
                                moveY = -2;
                            }},
                            new RegionPart("-back") {{
                                mirror = true;
                                progress = PartProgress.warmup;
                                moveY = 0.5f;
                                moveRot = 5f;
                            }});
                }
            };
        }};
        Coaxis = new ItemTurret("coaxis") {
            {
                requirements(Category.turret, with(lead, 140, chirenium, 100, ceramic, 160));
                ammo(
                        bauxite, new FlakBulletType(2.5f, 30) {{
                            width = 12f;
                            height = 18f;
                            sprite = "missile-large";
                            explodeRange = 40f;
                            lightRadius = 60f;
                            lightOpacity = 0.7f;
                            lightColor = AquaPal.bauxiteLightTone;
                            lifetime = 60f;
                            splashDamageRadius = 30f;
                            splashDamage = 25f;
                            ammoMultiplier = 1;
                            shootEffect = AquaFx.shootLong;
                            smokeEffect = new MultiEffect(AquaFx.shootSmokeTri, AquaFx.shootSmokeFormentBauxite);
                            trailEffect = Fx.none;
                            weaveMag = 1;
                            weaveScale = 8;
                            homingPower = 0.05f;
                            homingDelay = 5;
                            velocityRnd = 0.1f;
                            frontColor = Color.white;
                            trailWidth = 4.5f;
                            trailLength = 15;
                            backColor = trailColor = AquaPal.bauxiteLightTone;
                            buildingDamageMultiplier = 0.3f;
                        }});
                size = 4;
                outlineColor = AquaPal.tantDarkestTone;
                squareSprite = false;
                recoil = 3;
                recoilTime = 45;
                elevation = 5;
                rotateSpeed = 0.8f;
                targetAir = false;
                shootSound = shootAlt;
                shoot = new ShootBarrel() {{
                    firstShotDelay = 10;
                    barrels = new float[]{
                            -8, -4, 0,
                            -3, -4f, 0,
                            3, -4f, 0,
                            8, -4, 0
                    };
                }};
                reload = 25;
                inaccuracy = 1;
                minWarmup = 0.9f;
                warmupMaintainTime = 45;
                shootWarmupSpeed = 0.07f;
                recoils = 4;
                drawer = new DrawTurret() {{
                    parts.addAll(
                            new RegionPart("-front") {{
                                layerOffset = -0.3f;
                                progress = PartProgress.warmup;
                                under = true;
                                moveY = -3;
                            }},
                            new RegionPart("-barrel-2") {{
                                progress = PartProgress.warmup;
                                moveY = -2.5f;
                                moveX = -0.5f;
                                recoilIndex = 0;
                                moves.add(new PartMove(PartProgress.recoil, 0f, -3.5f, 0f));
                                children.add(new RegionPart("-heat2") {{
                                    progress = DrawPart.PartProgress.recoil;
                                    blending = Blending.additive;
                                    outline = false;
                                    color = Color.valueOf("000000");
                                    colorTo = Pal.turretHeat;
                                }});
                            }},
                            new RegionPart("-barrel-1") {{
                                progress = PartProgress.warmup;
                                moveY = -2.5f;
                                moveX = -0.5f;
                                moveRot = -3;
                                recoilIndex = 1;
                                moves.add(new PartMove(PartProgress.recoil, 0f, -3.5f, -5f));
                                children.add(new RegionPart("-heat1") {{
                                    progress = DrawPart.PartProgress.recoil;
                                    blending = Blending.additive;
                                    outline = false;
                                    color = Color.valueOf("000000");
                                    colorTo = Pal.turretHeat;
                                }});
                            }},
                            new RegionPart("-barrel-3") {{
                                progress = PartProgress.warmup;
                                moveY = -2.5f;
                                moveX = 0.5f;
                                recoilIndex = 2;
                                moves.add(new PartMove(PartProgress.recoil, 2f, -2f, 0f));
                                children.add(new RegionPart("-heat3") {{
                                    progress = DrawPart.PartProgress.recoil;
                                    blending = Blending.additive;
                                    outline = false;
                                    color = Color.valueOf("000000");
                                    colorTo = Pal.turretHeat;
                                }});
                            }},
                            new RegionPart("-barrel-4") {{
                                progress = PartProgress.warmup;
                                moveY = -2.5f;
                                moveX = 0.5f;
                                moveRot = -3;
                                recoilIndex = 3;
                                moves.add(new PartMove(PartProgress.recoil, -2f, -2f, 5f));
                                children.add(new RegionPart("-heat4") {{
                                    progress = DrawPart.PartProgress.recoil;
                                    blending = Blending.additive;
                                    outline = false;
                                    color = Color.valueOf("000000");
                                    colorTo = Pal.turretHeat;
                                }});
                            }}
                    );
                }};
                ensign = new ItemTurret("ensign") {{
                    requirements(Category.turret, with(lead, 45, electrum, 60, titanium, 20));
                    size = 2;
                    reload = 45;
                    recoil = 0.5f;
                    consumeCoolant(24/60f);
                    recoilTime = 40;
                    shootCone = 2;
                    shootSound = Sounds.bolt;
                    rotateSpeed = 1.4f;
                    range = 150;
                    cooldownTime = 80;
                    final Color[] col = {Color.valueOf("f9350f")};
                    heatColor = col[0];
                    ammo(
                            lead, new RailBulletType() {{
                                length = 155f;
                                damage = 55f;
                                smokeEffect = Fx.colorSpark;
                                hitColor = Color.valueOf("b397f0");
                                hitEffect = endEffect = Fx.hitBulletColor;
                                pierceDamageFactor = 0.6f;

                                shootEffect = new Effect(10, e -> {
                                    color(e.color);
                                    float w = 1.2f + 7 * e.fout();

                                    Drawf.tri(e.x, e.y, w, 30f * e.fout(), e.rotation);
                                    color(e.color);

                                    for(int i : Mathf.signs){
                                        Drawf.tri(e.x, e.y, w * 1.1f, 18f * e.fout(), e.rotation + i * 90f);
                                    }

                                    Drawf.tri(e.x, e.y, w, 4f * e.fout(), e.rotation + 180f);
                                });

                                lineEffect = new Effect(20f, e -> {
                                    if (!(e.data instanceof Vec2 v)) return;

                                    color(e.color);
                                    stroke(e.fout() * 0.9f + 0.6f);

                                    Fx.rand.setSeed(e.id);
                                    for (int i = 0; i < 7; i++) {
                                        Fx.v.trns(e.rotation, Fx.rand.random(8f, v.dst(e.x, e.y) - 8f));
                                        Lines.lineAngleCenter(e.x + Fx.v.x, e.y + Fx.v.y, e.rotation + e.finpow(), e.foutpowdown() * 20f * Fx.rand.random(0.5f, 1f) + 0.3f);
                                    }

                                    e.scaled(14f, b -> {
                                        stroke(b.fout() * 1.5f);
                                        color(e.color);
                                        Lines.line(e.x, e.y, v.x, v.y);
                                    });
                                });
                            }},
                            nickel, new RailBulletType() {{
                                length = 160f;
                                damage = 70f;
                                knockback = 0.5f;
                                smokeEffect = Fx.colorSpark;
                                hitColor = Color.valueOf("f7e7be");
                                col[0] = Color.valueOf("ff956e");
                                hitEffect = endEffect = Fx.hitBulletColor;
                                pierceDamageFactor = 0.8f;

                                shootEffect = new Effect(13, e -> {
                                    color(e.color);
                                    float w = 1.4f + 7 * e.fout();

                                    Drawf.tri(e.x, e.y, w, 35f * e.fout(), e.rotation);
                                    color(e.color);

                                    for(int i : Mathf.signs){
                                        Drawf.tri(e.x, e.y, w * 1.2f, 22f * e.fout(), e.rotation + i * 90f);
                                    }

                                    Drawf.tri(e.x, e.y, w, 5f * e.fout(), e.rotation + 180f);
                                });

                                lineEffect = new Effect(25f, e -> {
                                    if (!(e.data instanceof Vec2 v)) return;

                                    color(e.color);
                                    stroke(e.fout() * 1.1f + 0.8f);

                                    Fx.rand.setSeed(e.id);
                                    for (int i = 0; i < 9; i++) {
                                        Fx.v.trns(e.rotation, Fx.rand.random(9f, v.dst(e.x, e.y) - 7f));
                                        Lines.lineAngleCenter(e.x + Fx.v.x, e.y + Fx.v.y, e.rotation + e.finpow(), e.foutpowdown() * 20f * Fx.rand.random(0.5f, 1f) + 0.3f);
                                    }

                                    e.scaled(18f, b -> {
                                        stroke(b.fout() * 1.7f);
                                        color(e.color);
                                        Lines.line(e.x, e.y, v.x, v.y);
                                    });
                                });
                            }},
                            inconel, new RailBulletType() {{
                                length = 165f;
                                damage = 65f;
                                smokeEffect = Fx.colorSpark;
                                hitColor = Color.valueOf("fdff84");
                                hitEffect = endEffect = Fx.hitBulletColor;
                                pierceDamageFactor = 1.1f;
                                fragBullets = 3;
                                fragRandomSpread = 5;
                                fragAngle = 0;
                                fragOnHit = true;
                                fragVelocityMax = 1.1f;
                                fragLifeMin = 0.9f;
                                fragSpread = 2;
                                fragBullet = new BasicBulletType(2.5f, 20){{
                                    lifetime = 15;
                                    hitColor = Color.valueOf("fdff84");
                                    hitEffect = endEffect = Fx.hitBulletColor;
                                    frontColor = Color.white;
                                    backColor = trailColor = lightColor = Color.valueOf("fdff84");
                                    trailLength = 9;
                                    trailWidth = 2f;
                                    width = 8;
                                    height = 12;
                                    shrinkX = 0.9f;
                                }};
                                col[0] = Color.valueOf("fdff84");

                                shootEffect = new Effect(10, e -> {
                                    color(e.color);
                                    float w = 1.2f + 7 * e.fout();

                                    Drawf.tri(e.x, e.y, w, 30f * e.fout(), e.rotation);
                                    color(e.color);

                                    for(int i : Mathf.signs){
                                        Drawf.tri(e.x, e.y, w * 1.1f, 18f * e.fout(), e.rotation + i * 90f);
                                    }

                                    Drawf.tri(e.x, e.y, w, 4f * e.fout(), e.rotation + 180f);
                                });

                                lineEffect = new Effect(20f, e -> {
                                    if (!(e.data instanceof Vec2 v)) return;

                                    color(e.color);
                                    stroke(e.fout() * 0.9f + 0.6f);

                                    Fx.rand.setSeed(e.id);
                                    for (int i = 0; i < 7; i++) {
                                        Fx.v.trns(e.rotation, Fx.rand.random(8f, v.dst(e.x, e.y) - 8f));
                                        Lines.lineAngleCenter(e.x + Fx.v.x, e.y + Fx.v.y, e.rotation + e.finpow(), e.foutpowdown() * 20f * Fx.rand.random(0.5f, 1f) + 0.3f);
                                    }

                                    e.scaled(14f, b -> {
                                        stroke(b.fout() * 1.5f);
                                        color(e.color);
                                        Lines.line(e.x, e.y, v.x, v.y);
                                    });
                                });
                            }});
                }};
            }
        };
    }
}