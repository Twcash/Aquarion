package aquarion.blocks;

import aquarion.AquaItems;
import aquarion.AquaStatuses;
import aquarion.units.uhhShootSummon;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.AquaPal;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
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
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.draw.DrawTurret;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.fumes;
import static aquarion.world.graphics.AquaFx.rand;
import static aquarion.world.graphics.AquaFx.v;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mindustry.content.Items.*;
import static mindustry.entities.part.DrawPart.PartProgress.warmup;
import static mindustry.gen.Sounds.shootAlt;
import static mindustry.gen.Sounds.shootAltLong;
import static mindustry.type.ItemStack.with;

public class AquaTurrets {
    public static Block maelstrom, Foment, redact, Fragment, gyre, Coaxis, deviate,
            blaze, ensign, hack;

    public static void loadContent() {
        //oogly boogly
        Foment = new ItemTurret("foment") {{
            {
                outlineColor = AquaPal.tantDarkestTone;
                ammo(
                        lead, new MissileBulletType(2.5f, 75, "bullet") {{
                            width = 10f;
                            height = 16f;
                            trailLength = 12;

                            lifetime = 60f;
                            ammoMultiplier = 1;
                            shootEffect = AquaFx.shootLong;
                            smokeEffect = new MultiEffect(AquaFx.shootSmokeTri, AquaFx.shootSmokeFormentBauxite);
                            trailEffect = Fx.none;
                            weaveMag = 2;
                            homingPower = 0.01f;
                            homingDelay = 5;
                            weaveScale = 1.75f;
                            shrinkX = 0.2f;
                            shrinkY = 0.8f;
                            frontColor = lightColor = hitColor = Color.white;
                            backColor = trailColor = Color.valueOf("8d70ab");
                        }},

                        ferricMatter, new MissileBulletType(2.5f, 150, "bullet") {{
                            width = 9f;
                            height = 15f;
                            trailLength = 10;
                            lifetime = 60f;
                            reloadMultiplier = 0.4f;
                            rangeChange = 32;
                            trailEffect = Fx.none;
                            heatColor = Color.white;
                            shootEffect = AquaFx.shootLong;
                            smokeEffect = new MultiEffect(Fx.shootSmokeSquareSparse, AquaFx.shootSmokeFormentGallium);
                            weaveMag = 2;
                            homingPower = 0.01f;
                            homingDelay = 10;
                            pierce = true;
                            pierceCap = 2;
                            pierceDamageFactor = 0.5f;
                            weaveScale = 1.75f;
                            shrinkX = 0.2f;
                            shrinkY = 0.8f;
                            reloadMultiplier = 0.7f;
                            ammoMultiplier = 2;
                            frontColor = lightColor = hitColor = Color.white;
                            backColor = trailColor = Color.valueOf("8d706a");
                        }},
                        ferrosilicon, new MissileBulletType(7.5f, 120, "bullet") {{
                            width = 12f;
                            height = 18f;
                            rangeChange = 16;
                            trailLength = 8;
                            lifetime = 60f;
                            reloadMultiplier = 1.1f;
                            pierce = true;
                            pierceCap = 4;
                            pierceDamageFactor = 0.8f;
                            ammoMultiplier = 3;
                            heatColor = AquaPal.galliumLightTone;
                            trailEffect = Fx.none;
                            shootEffect = AquaFx.shootLong;
                            smokeEffect = new MultiEffect(AquaFx.pentagonShootSmoke, AquaFx.shootSmokeFormentGallium);
                            weaveMag = 0;
                            homingPower = 0.00f;
                            weaveScale = 0f;
                            shrinkX = 0.2f;
                            knockback = 4;
                            shrinkY = 0.8f;
                            frontColor = lightColor = hitColor = Color.white;
                            backColor = trailColor = Color.valueOf("98a1ab");
                        }});
                requirements(Category.turret, with(lead, 90, AquaItems.bauxite, 60f));
                size = 3;
                squareSprite = false;
                range = 170;
                limitRange(1.1f);
                reload = 45;
                alwaysUnlocked = true;
                consumeAmmoOnce = true;
                maxAmmo = 15;
                inaccuracy = 3;
                ammoPerShot = 3;
                researchCostMultiplier = 0.03f;
                xRand = 0.5f;
                outlineColor = AquaPal.tantDarkestTone;
                shoot.shotDelay = 5;
                recoils = 2;
                shootWarmupSpeed = 0.001f;
                warmupMaintainTime = 360;
                heatColor = Pal.redLight;
                shoot = new ShootAlternate(4);
                drawer = new DrawTurret(){{
                    for(int i = 0; i < 2; i ++){
                        int f = i;
                        parts.add(new RegionPart("-barrel-" + (i == 0 ? "l" : "r")){{
                            progress = PartProgress.recoil;
                            recoilIndex = f;
                            under = true;
                            heatProgress = warmup;
                            moveY = -2f;
                        }});
                    }
                }};

            }
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        redact  = new ItemTurret("redact"){{
            requirements(Category.turret, with(ferricMatter, 80, lead, 200, bauxite, 300));
            ammo(
                    bauxite, new BasicBulletType(7f, 75, "missile-large") {{
                        width = 15f;
                        pierce = true;
                        pierceCap = 2;
                        height = 19f;
                        trailLength = 34;
                        trailWidth = 3;
                        lifetime = 60f;
                        rangeChange = -5*8f;
                        ammoMultiplier = 1;
                        shootEffect = new Effect(10, e -> {
                            color(e.color);
                            float w = 1.5f + 9 * e.fout();

                            Drawf.tri(e.x, e.y, w, 60f * e.fout(), e.rotation);
                            color(e.color);

                            for(int i : Mathf.signs){
                                Drawf.tri(e.x, e.y, w * 1.2f, 90f * e.fout(), e.rotation + i * 45f);
                            }

                            Drawf.tri(e.x, e.y, w, 60f * e.fout(), e.rotation + 180-45f);
                        });
                        smokeEffect = new MultiEffect(AquaFx.pentagonShootSmoke, Fx.colorSpark);
                        trailRotation = true;
                        trailEffect = AquaFx.pentagonShootSmoke;
                        trailInterval = 2;
                        frontColor = lightColor = hitColor = Color.white;
                        hitEffect  = Fx.hitSquaresColor;
                        despawnEffect = new Effect(17f, e -> {
                            color(e.color);
                            Drawf.tri(e.x, e.y, e.fout() * 1.7f, 12f, e.rotation);
                        });
                        backColor = trailColor = AquaPal.bauxiteLightTone;
                    }},
                    aluminum, new BasicBulletType(5f, 120, "missile-large") {{
                        width = 15f;
                        pierce = false;
                        height = 19f;
                        trailLength = 34;
                        trailWidth = 3;
                        lifetime = 60f;
                        reloadMultiplier = 0.7f;
                        ammoMultiplier = 1;
                        shootEffect = new Effect(10, e -> {
                            color(e.color);
                            float w = 1.5f + 9 * e.fout();

                            Drawf.tri(e.x, e.y, w, 60f * e.fout(), e.rotation);
                            color(e.color);

                                Drawf.tri(e.x, e.y, w * 1.2f, 90f * e.fout(), e.rotation * 45f);
                            Drawf.tri(e.x, e.y, w, 60f * e.fout(), e.rotation + 180-45f);
                        });
                        smokeEffect = new MultiEffect(new Effect(90f, e -> {
                            color(e.color, e.fin());
                            rand.setSeed(e.id);
                            for(int i = 0; i < 6; i++){
                                float rot = e.rotation + rand.range(40f);
                                v.trns(rot, rand.random(e.finpow() * 8f));
                                float randomRotationSpeed = rand.random(0f, 180f);
                                float slowRotation = Interp.pow2In.apply(randomRotationSpeed * e.fout());
                                Fill.poly(e.x + v.x, e.y + v.y, 5, e.fout() * 5f, rand.random(700f) + slowRotation);
                            }
                        }), Fx.colorSpark);
                        trailRotation = true;
                        trailEffect = new Effect(80f, e -> {
                            color(e.color, e.fin());
                            rand.setSeed(e.id);
                            for(int i = 0; i < 3; i++){
                                float rot = e.rotation + rand.range(15f);
                                v.trns(rot, rand.random(e.finpow() * 8f));
                                float randomRotationSpeed = rand.random(0f, 180f);
                                float slowRotation = Interp.pow2In.apply(randomRotationSpeed * e.fout());
                                Fill.poly(e.x + v.x, e.y + v.y, 5, e.fout() * 4f, rand.random(700f) + slowRotation);
                            }
                        });
                        trailInterval = 2;
                        frontColor = lightColor = hitColor = Color.white;
                        hitEffect  = Fx.hitSquaresColor;
                        despawnEffect = new Effect(17f, e -> {
                            color(e.color);
                            Drawf.tri(e.x, e.y, e.fout() * 1.7f, 12f, e.rotation);
                        });
                        backColor = trailColor = Color.valueOf("a3bbc8");
                    }},
                    invar, new BasicBulletType(4f, 350, "missile-large") {{
                        width = 15f;
                        pierce = false;
                        height = 19f;
                        splashDamage = 50;
                        
                        splashDamageRadius = 16;
                        trailLength = 34;
                        trailWidth = 3;
                        lifetime = 60f;
                        reloadMultiplier = 0.7f;
                        ammoMultiplier = 1;
                        shootEffect = new Effect(10, e -> {
                            color(e.color);
                            float w = 1.5f + 9 * e.fout();

                            Drawf.tri(e.x, e.y, w, 60f * e.fout(), e.rotation);
                            color(e.color);

                            Drawf.tri(e.x, e.y, w * 1.2f, 90f * e.fout(), e.rotation * 45f);
                            Drawf.tri(e.x, e.y, w, 60f * e.fout(), e.rotation + 180-45f);
                        });
                        smokeEffect = new MultiEffect(new Effect(90f, e -> {
                            color(e.color, e.fin());
                            rand.setSeed(e.id);
                            for(int i = 0; i < 6; i++){
                                float rot = e.rotation + rand.range(40f);
                                v.trns(rot, rand.random(e.finpow() * 8f));
                                float randomRotationSpeed = rand.random(0f, 180f);
                                float slowRotation = Interp.pow2In.apply(randomRotationSpeed * e.fout());
                                Fill.poly(e.x + v.x, e.y + v.y, 5, e.fout() * 5f, rand.random(700f) + slowRotation);
                            }
                        }), Fx.colorSpark);
                        trailRotation = true;
                        trailEffect = new Effect(80f, e -> {
                            color(e.color, e.fin());
                            rand.setSeed(e.id);
                            for(int i = 0; i < 3; i++){
                                float rot = e.rotation + rand.range(15f);
                                v.trns(rot, rand.random(e.finpow() * 8f));
                                float randomRotationSpeed = rand.random(0f, 180f);
                                float slowRotation = Interp.pow2In.apply(randomRotationSpeed * e.fout());
                                Fill.poly(e.x + v.x, e.y + v.y, 5, e.fout() * 4f, rand.random(700f) + slowRotation);
                            }
                        });
                        trailInterval = 2;
                        frontColor = lightColor = hitColor = Color.white;
                        hitEffect  = Fx.hitSquaresColor;
                        despawnEffect = new Effect(17f, e -> {
                            color(e.color);
                            Drawf.tri(e.x, e.y, e.fout() * 1.7f, 12f, e.rotation);
                        });
                        backColor = trailColor = Color.valueOf("f3efa7");
                    }});
            size = 3;
            squareSprite = false;
            range = 350;
            limitRange(1.2f);
            reload = 45;
            consumeAmmoOnce = true;
            maxAmmo = 45;
            inaccuracy = 3;
            ammoPerShot = 3;
            shoot.shotDelay = 5;
            recoils = 2;
            shootWarmupSpeed = 0.01f;
            minWarmup = 0.65f;
            warmupMaintainTime = 360;
            heatColor = Pal.redLight;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            drawer = new DrawTurret(){{
                parts.add(new RegionPart("-side"){{
                    moveY = -1;
                    moveX = -0.5f;
                    progress = warmup;
                    mirror = true;
                }},new RegionPart("-back"){{
                    moveY = -.5f;
                    moveX = 0.75f;
                    progress = warmup;
                    mirror = true;
                    moves.add(new PartMove(PartProgress.recoil, 0.5f, -0.5f, 0f));
                }});
            }};
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
                    progress = warmup;
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
                                progress = warmup;
                                under = true;
                                moveX = 0.25f;
                                moveY = -0.5f;
                                moveRot = -4f;
                                layerOffset = -0.00001f;
                                moves.add(new PartMove(PartProgress.recoil, 0.25f, -0.5f, -3f));
                            }},
                            new RegionPart("-barrel") {{
                                progress = warmup;
                                moveRot = -1f;
                                moveX = 0.25f;
                                mirror = true;
                                moves.add(new PartMove(PartProgress.recoil, 0f, -1.5f, -1.5f));
                                moveY = -3.5f;
                            }},
                            new RegionPart("-side") {{
                                progress = warmup;
                                moveRot = -1.5f;
                                moveX = 0.5f;
                                mirror = true;
                                moves.add(new PartMove(PartProgress.recoil, 0f, -1f, -1f));
                                moveY = -2;
                            }},
                            new RegionPart("-back") {{
                                mirror = true;
                                progress = warmup;
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
                                progress = warmup;
                                under = true;
                                moveY = -3;
                            }},
                            new RegionPart("-barrel-2") {{
                                progress = warmup;
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
                                progress = warmup;
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
                                progress = warmup;
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
                                progress = warmup;
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

            }
        };
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
        hack = new ItemTurret("hack"){{
            ammo(
                    titanium, new BasicBulletType(3, 9){{
                        frontColor = Color.white;
                        backColor  = trailColor = Color.valueOf("667fba");
                        lightColor = Color.valueOf("667fba");
                        trailLength = 9;
                        width = 9;
                        height = 12;
                        ammoMultiplier = 2;
                        shootEffect = Fx.shootSmokeSquareSparse;
                        hitEffect = despawnEffect = Fx.hitSquaresColor;
                    }},
                    arsenic, new BasicBulletType(2.5f, 6){{
                        frontColor = Color.white;
                        backColor = trailColor = Color.valueOf("ffbaba");
                        lightColor = Color.valueOf("ffbaba");
                        trailLength = 9;
                        splashDamage = 3;
                        splashDamageRadius = 10.5f;
                        width = 9;
                        height = 14;
                        reloadMultiplier = 1.25f;
                        ammoMultiplier = 1;
                        knockback = 1;
                        shootEffect = Fx.shootSmokeSquareSparse;
                        hitEffect = despawnEffect = Fx.hitSquaresColor;
                    }},
                    surgeAlloy, new BasicBulletType(4f, 30){{
                        frontColor = Color.white;
                        backColor = trailColor = Color.valueOf("f5e459");
                        lightColor = Color.valueOf("f5e459");
                        trailLength = 12;
                        splashDamage = 12;
                        pierce = true;
                        pierceCap = 3;
                        pierceDamageFactor = 0.9f;
                        pierceBuilding = true;
                        ammoMultiplier = 5;
                        knockback = 4;
                        reloadMultiplier = 0.8f;
                        splashDamageRadius = 18f;
                        width = 12;
                        height = 16;
                        shootEffect = Fx.shootSmokeSquareSparse;
                        hitEffect = despawnEffect = Fx.hitSquaresColor;
                    }});
            requirements(Category.turret, with(electrum, 90, silver, 40, arsenic, 65));
            size = 2;
            ammoPerShot = 3;
            consumeCoolant(24/60f);
            reload = 35;
            range = 170;
            limitRange(1.1f);
            recoil = 0.75f;
            shootCone = 30;
            inaccuracy = 8;
            shoot = new ShootMulti(new ShootAlternate(6), new ShootSpread(6, 15), new ShootSine(){{
                scl = 4f;
                mag = 3f;
            }});
        }};
        blaze = new PowerTurret("blaze"){{
            requirements(Category.turret, with(electrum, 35, lead, 40));
            consumePower(128/60f);
            size = 2;
            consumeCoolant(24/60f);
            consumeCoolant(24/60f);
            shootSound = Sounds.bolt;
            reload = 90;
            range = 120;
              recoil = 0.75f;
            shootCone = 30;
            inaccuracy = 9;
            minWarmup = 0.8f;
            shoot.shots = 2;
            shoot.shotDelay = 8;
            shootType = new MissileBulletType(3.5f, 15, "large-orb"){{
            width = 8;
            height = 8;
            shrinkX = 0;
            lifetime = 30;
            shrinkY = 0;
            status = AquaStatuses.ionized;
            lightning = 3;
            statusDuration = 160;
            lightningLength = 8;
            lightningDamage = 1;
            lightningColor = Color.valueOf("f25353");
            frontColor = lightColor = hitColor = Color.valueOf("ffbcbc");
            backColor = trailColor = Color.valueOf("f25353");
            hitSize = 6;
            homingPower = 0.05f;
            trailLength = 12;
            weaveMag = 2;
            weaveScale = 4;
            trailWidth = 4;
            shootEffect = AquaFx.pentagonShootSmoke;
            despawnEffect = hitEffect = Fx.hitSquaresColor;
            }};

        }};
        maelstrom = new LiquidTurret("maelstrom"){
            {
                shoot = new uhhShootSummon(0f, 4f, 5, 85f);
                shoot.shotDelay = 2;
                shoot.shots = 5;
                reload = 2;
                outlineColor = AquaPal.tantDarkestTone;
                loopSound = Sounds.bioLoop;
                loopSoundVolume = 0.09f;
                shoot.firstShotDelay = 20;
                warmupMaintainTime = 50;
                shootWarmupSpeed = 0.07f;
                minWarmup = 0.85f;
                range = 260;
                size = 3;
                squareSprite = false;
                shootSound = Sounds.blaster;
                targetGround = false;
                requirements(Category.turret, with(aluminum, 250, towanite, 90, ferricMatter, 100));
                ammo(
                        fumes, new MissileBulletType(7, 35) {{
                            knockback = 2f;
                            drag = -0.02f;
                            lifetime = 45;
                            trailLength = 18;
                            trailWidth = 2;
                            weaveScale = 4;
                            weaveMag = 2;
                            homingPower = 0.09f;
                            collidesTiles = false;
                            collidesGround = false;
                            shootEffect = Fx.shootSmokeSquareSparse;
                            backColor = trailColor = hitColor = lightColor = Color.gray;
                            frontColor = Color.white;
                            hitEffect = Fx.hitSquaresColor;
                            layer = Layer.bullet - 2f;
                            backSprite = "aquarion-star-bullet";
                            sprite = "aquarion-star-bullet";
                        }});
            }};
    }
}