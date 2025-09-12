package aquarion.blocks;

import aquarion.AquaItems;
import aquarion.AquaLiquids;
import aquarion.AquaSounds;
import aquarion.AquaStatuses;
import aquarion.world.blocks.turrets.ItemPointDefenseTurret;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.AquaPal;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.UnitSorts;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.EffectSpawnerPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.*;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.draw.DrawTurret;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static aquarion.AquaItems.cupronickel;
import static aquarion.AquaItems.ferrosilicon;
import static aquarion.AquaItems.manganese;
import static aquarion.AquaItems.nickel;
import static aquarion.AquaLiquids.*;
import static aquarion.AquaLiquids.fumes;
import static aquarion.planets.AquaPlanets.*;
import static aquarion.world.graphics.AquaFx.rand;
import static aquarion.world.graphics.AquaFx.v;
import static aquarion.world.graphics.AquaPal.*;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static arc.math.Interp.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.nitrogen;
import static mindustry.content.Liquids.water;
import static mindustry.content.StatusEffects.*;
import static mindustry.entities.part.DrawPart.PartProgress.charge;
import static mindustry.entities.part.DrawPart.PartProgress.warmup;
import static mindustry.gen.Sounds.*;
import static mindustry.type.ItemStack.with;

public class AquaTurrets {
    public static Block  flagellate,truncate,  thrash, dislocate, refraction, confront, focus, douse, pelt, point, vector, sentry, bend, maelstrom, Foment, redact, Fragment, gyre, Coaxis, deviate, torrefy,
            blaze, ensign, hack, azimuth, condolence, grace;

    public static void loadContent() {
        //1 by 1 turret that can be boosted hellishly beyond what it should be
        point = new ItemTurret("point"){{
            requirements(Category.turret, with( silicon, 35f, lead, 50));
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                health = 250;
                squareSprite = true;
                shootSound = Sounds.lasershoot;
                ammoUseEffect = Fx.colorSpark;
                ammoPerShot = 4;
                shootCone = 2;
                outlineColor = tantDarkestTone;
                shoot.shots = 5;
                inaccuracy = 19;
                reload = 92;
                range = 110;
                ammo(
                        silicon, new LaserBoltBulletType() {{
                            frontColor = AquaPal.redDecal1;
                            backColor = AquaPal.redDecal1Dark;
                            speed = 20;
                            lifetime = 5;
                            knockback = 0.75f;
                            hitSize = 4;
                            damage = 15;
                            ammoMultiplier = 2f;
                            //this thing can shred late game stuff if you do it right
                            pierceArmor = true;
                            shootEffect = Fx.shootPyraFlame;
                            smokeEffect = Fx.shootSmallFlame;
                            hitEffect = Fx.hitFlameSmall;
                            despawnEffect = Fx.reactorsmoke;
                        }},
                        graphite, new LaserBoltBulletType() {{
                            frontColor = graphite.color;
                            backColor = graphite.color;
                            speed = 25;
                            lifetime = 9;
                            hitSize = 4;
                            reloadMultiplier = 1.2f;
                            ammoMultiplier = 3;
                            knockback = 0.5f;
                            damage = 45;
                            ammoMultiplier = 3f;
                            rangeChange = 24;
                            //this thing can shred late game stuff if you do it right
                            pierceArmor = true;
                            shootEffect = Fx.shootPyraFlame;
                            smokeEffect = Fx.shootSmallFlame;
                            hitEffect = Fx.hitFlameSmall;
                            despawnEffect = Fx.reactorsmoke;
                        }},
                        coal,  new LaserBoltBulletType() {{
                            frontColor = AquaPal.redDecal1;
                            backColor = AquaPal.redDecal1Dark;
                            speed = 20;
                            lifetime = 8;
                            damage = 12;
                            hitSize = 4;
                            status = burning;
                            statusDuration = 8*60f;
                            reloadMultiplier = 1.8f;
                            rangeChange = 8*5f;
                            ammoMultiplier = 3f;
                            shootEffect = Fx.shootPyraFlame;
                            smokeEffect = Fx.shootPyraFlame;
                            hitEffect = Fx.hitFlameBeam;
                            despawnEffect = Fx.hitFlameBeam;
                        }},
                        AquaItems.arsenic, new LaserBoltBulletType() {{
                            frontColor = AquaPal.redDecal1;
                            backColor = AquaPal.redDecal1Dark;
                            speed = 40;
                            hitSize = 4;
                            lifetime = 2;
                            pierce = true;
                            pierceCap = 3;
                            knockback = 2f;
                            damage = 60;
                            rangeChange = 48;
                            reloadMultiplier = 0.1f;
                            shootEffect = Fx.shootSmall;
                            smokeEffect = Fx.shootSmallSmoke;
                            hitEffect = Fx.hitFlameSmall;
                            despawnEffect = Fx.smokePuff;

                        }});
                limitRange(1.2f);
                consumeCoolant(10/60f);
            }};
        truncate = new ItemTurret("truncate"){{
            requirements(Category.turret, with( AquaItems.ferricMatter, 500, polymer, 350, graphite, 200));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            health = 1120;
            size = 4;
            squareSprite = true;
            shootSound = shootAlt;
            soundPitchMin = 0.6f;
            soundPitchMax = 1.2f;
            ammoUseEffect = Fx.casing3;
            shootCone = 9;
            outlineColor = tantDarkestTone;
            shoot.shots = 2;
            shoot.shotDelay = 10;
            inaccuracy = 6;
            recoil = 8;
            recoilTime = 300;
            reload = 240;
            range = 250;
            maxAmmo = 5;
            ammoPerShot = 5;
            ammo(
                    graphite, new BasicBulletType(4.5f, 20){{
                        splashDamage = 90;
                        splashDamageRadius = 70;
                        hitEffect = despawnEffect = new ExplosionEffect(){{
                            waveRad = 30;
                            waveLife = 90;
                            waveColor = methane.color;
                        }};
                        sprite = "aquarion-bolt";
                        width = 6;
                        height = 9;
                        frontColor =Color.white;
                        backColor = methane.color;
                        collidesGround = true;
                        collidesAir = false;
                        scaleLife = true;
                        scaleLifetimeOffset = 60;
                        sticky = true;
                        stickyExtraLifetime = 200;
                        drag = 0.02f;
                        intervalBullets = 3;
                        bulletInterval = 10;
                        trailInterval = 10;
                        despawnShake = 8;
                        despawnSound = dullExplosion;
                        fragOffsetMax = 3;
                        fragOffsetMin = 0;
                        trailEffect = new Effect(400f * 1.2f, 250f, e -> {
                            color(methane.color, 0.35f);

                            randLenVectors(e.id, 3, 15f, (x, y) -> {
                                Fill.circle(e.x + x, e.y + y, 3.75f * Mathf.clamp(e.fin() / 0.1f) * Mathf.clamp(e.fout() / 0.1f));
                            });
                        });
                        intervalBullet = new EmptyBulletType(){{
                            lifetime = 400;
                            hittable = false;
                            collides = false;
                            fragBullets = 1;
                            hitEffect = Fx.ballfire;
                            fragBullet = new BasicBulletType(0, 200){{
                                collidesAir = false;
                                width = height = 0;
                                instantDisappear = true;
                                lifetime = 0;
                                splashDamage = 45;
                                splashDamageRadius = 34;
                                hitEffect = despawnEffect = new ExplosionEffect(){{
                                    waveRad = 30;
                                    waveLife = 90;
                                    waveColor = methane.color;
                                }};
                                despawnShake = 1;

                            }};
                        }};
                    }});
            drawer = new DrawTurret(){{
                parts.addAll(new RegionPart("-shell"){{
                    growProgress = progress = PartProgress.reload;
                    x = 11f;
                    moveX = -1;
                    y = -1;
                    xScl = 0.5f;
                    growX = -0.5f;
                    colorTo = Color.black;
                    color = Color.grays(0.5f);
                }},new RegionPart("-shell"){{
                    growProgress = progress = PartProgress.reload;
                    x = 0;
                    y = -1;
                    moveX = 11;
                    xScl = -0.75f;
                    growX = 1.25f;
                    colorTo = Color.grays(0.5f);
                    color = Color.white;
                }},
                        new RegionPart("-shell"){{
                            growProgress = progress = PartProgress.reload;
                            x = -11f;
                            moveX = 1;
                            y = -1;
                            xScl = 0.5f;
                            growX = -0.5f;
                            colorTo = Color.black;
                            color = Color.grays(0.5f);
                        }},new RegionPart("-shell"){{
                            growProgress = progress = PartProgress.reload;
                            x = 0;
                            y = -1;
                            moveX = -11;
                            xScl = -0.75f;
                            growX = 1.25f;
                            colorTo = Color.grays(0.5f);
                            color = Color.white;
                        }}, new RegionPart("-tap"){{
                            progress = PartProgress.recoil.curve(pow2In);
                            moveY = -8f;
                        }}, new RegionPart("-mid"){{}}, new RegionPart("-cover"){{
                            mirror = true;
                            progress = PartProgress.recoil.curve(pow5Out);
                            growProgress = PartProgress.recoil.curve(pow5Out);
                            color = new Color(1f, 1f, 1f, 0f);
                            colorTo = new Color(1f, 1f, 1f, 1f);
                            progress = PartProgress.recoil.curve(pow5Out);
                            growProgress = PartProgress.recoil.curve(pow5Out);
                            rotation = 180;
                            yScl = -1;
                            growX = -1.5f;
                            moveX = -86/4f;
                        }}, new RegionPart("-cover"){{
                            mirror = true;
                            progress = PartProgress.recoil.curve(pow5Out);
                            growProgress = PartProgress.recoil.curve(pow5Out);
                            color = new Color(1f, 1f, 1f, 0f);
                            colorTo = new Color(1f, 1f, 1f, 1f);
                            progress = PartProgress.recoil.curve(pow5Out);
                            growProgress = PartProgress.recoil.curve(pow5Out);
                            rotation = 180;
                            yScl = -1;
                            growX = -1.5f;
                            moveX = -85/4f;
                        }}, new RegionPart("-cover"){{
                            mirror = true;
                            color = new Color(1f, 1f, 1f, 0f);
                            colorTo = new Color(1f, 1f, 1f, 1f);
                            progress = PartProgress.recoil.curve(pow5Out);
                            growProgress = PartProgress.recoil.curve(pow5Out);
                            rotation = 180;
                            yScl = -1;
                            growX = -1.5f;
                            moveX = -84/4f;
                        }}, new RegionPart("-cover"){{
                            mirror = true;
                            progress = PartProgress.recoil.curve(pow5Out);
                            growProgress = PartProgress.recoil.curve(pow5Out);
                            color = new Color(1f, 1f, 1f, 1f);
                            colorTo = new Color(1f, 1f, 1f, 0f);
                            growX = -1.5f;
                            moveX = 86/4f;
                        }}, new RegionPart("-cover"){{
                            mirror = true;
                            progress = PartProgress.recoil.curve(pow5Out);
                            growProgress = PartProgress.recoil.curve(pow5Out);
                            color = new Color(1f, 1f, 1f, 1f);
                            colorTo = new Color(1f, 1f, 1f, 0f);
                            growX = -1.5f;
                            moveX = 85/4f;
                        }}, new RegionPart("-cover"){{
                            mirror = true;
                            color = new Color(1f, 1f, 1f, 1f);
                            colorTo = new Color(1f, 1f, 1f, 0f);
                            progress = PartProgress.recoil.curve(pow5Out);
                            growProgress = PartProgress.recoil.curve(pow5Out);
                            growX = -1.5f;
                            moveX = 84/4f;
                        }});
            }};
            limitRange(1.6f);
            consumeCoolant(10/60f);
        }};
        vector = new ItemTurret("vector"){{
            requirements(Category.turret, with(cupronickel, 85, silicon, 110, metaglass, 80, graphite, 70));
            health = 700;
            size = 3;
            squareSprite = false;
            reload = 170;
            ammoPerShot = 9;
            itemCapacity = 80;
            range = 255;
            shootSound = AquaSounds.vectorShot;
            outlineColor = tantDarkestTone;
            maxAmmo = 40;
            recoilTime = 45;
            warmupMaintainTime = 120;
            shootWarmupSpeed = 0.05f;
            minWarmup = 0.9f;
            rotateSpeed = 0.95f;
            recoils = 4;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            ammo(
                    cupronickel, new BasicBulletType(20, 95){{
                        ammoMultiplier = 2;
                        height= 15;
                        width = 24;
                        trailWidth = 12;
                        trailLength = 12;
                        shootEffect = Fx.shootBig2;
                        trailInterp = Interp.slope;
                        smokeEffect = Fx.shootSmokeSquareBig;
                        hitEffect = despawnEffect = Fx.hitSquaresColor;
                        knockback = 8f;
                        frontColor = AquaPal.redDecal1;
                        backColor = AquaPal.redDecal1Dark;
                    }},
                    metaglass, new BasicBulletType(){{
                        damage = 80;
                        ammoMultiplier = 4;
                        height= 15;
                        speed = 16f;
                        width = 24;
                        trailWidth = 12;
                        trailLength = 12;
                        trailInterp = Interp.slope;
                        shootEffect = Fx.shootBig2;
                        smokeEffect = Fx.shootSmokeSquareBig;
                        hitEffect = despawnEffect = Fx.hitSquaresColor;
                        knockback = 12f;
                        frontColor = Color.white;
                        backColor = Color.lightGray;
                    }},
                    //LOAD THE 20 POUNDER CHAPS
                    steel, new BasicBulletType(){{
                        damage = 140;
                        ammoMultiplier = 4;
                        height= 30;
                        speed = 16f;
                        width = 30;
                        trailWidth = 5;
                        reloadMultiplier = 0.5f;
                        rangeChange = 40;
                        trailLength = 18;
                        pierce = true;
                        trailInterp = Interp.slope;
                        shootEffect = Fx.shootBig2;
                        smokeEffect = Fx.shootSmokeSquareBig;
                        hitEffect = despawnEffect = Fx.hitSquaresColor;
                        knockback = 18f;
                        frontColor = Color.white;
                        backColor= trailColor = Color.lightGray;
                    }}
            );
            coolantMultiplier = 0.75f;
            shoot = new ShootBarrel(){{
                barrels = new float[]{
                        0, 9, 0,
                        0, 0, 0,
                        0, -9, 0,
                        0, -10, 0
                };
                shots = 4;
                shotDelay = 10;
            }};
            drawer = new DrawTurret(){{
                parts.addAll(new RegionPart("-puck"){{
                    colorTo = new Color(1f, 1f, 1f, 0f);
                    color =  Color.white;
                    growProgress = PartProgress.recoil;
                    progress = PartProgress.recoil;
                    recoilIndex = 0;
                    mixColor = new Color(1f, 1f, 1f, 0f);
                    mixColorTo = Color.white;
                    y = 15/4f;
                    growX = -1f;
                    growY = -1f;

                }}, new RegionPart("-puck"){{
                    recoilIndex = 1;
                    colorTo = new Color(1f, 1f, 1f, 0f);
                    growProgress = PartProgress.recoil;
                    progress = PartProgress.recoil;
                    color =  Color.white;
                    recoilIndex = 1;
                    mixColor = new Color(1f, 1f, 1f, 0f);
                    mixColorTo = Color.white;
                    growX = -1f;
                    growY = -1f;
                }}, new RegionPart("-puck"){{
                    recoilIndex = 2;
                    colorTo = new Color(1f, 1f, 1f, 0f);
                    color =  Color.white;
                    growProgress = PartProgress.recoil;
                    progress = PartProgress.recoil;
                    recoilIndex = 2;
                    mixColor = new Color(1f, 1f, 1f, 0f);
                    mixColorTo = Color.white;
                    y = -15/4f;
                    growX = -1f;
                    growY = -1f;
                }}, new RegionPart("-puck"){{
                    recoilIndex = 3;
                    colorTo = new Color(1f, 1f, 1f, 0f);
                    color =  Color.white;
                    growProgress = PartProgress.recoil;
                    progress = PartProgress.recoil;
                    recoilIndex = 3;
                    mixColor = new Color(1f, 1f, 1f, 0f);
                    mixColorTo = Color.white;
                    y = -30/4f;
                    growX = -1f;
                    growY = -1f;
                }}, new RegionPart("-side"){{
                    mirror = true;
                    moveY = -7/4f;
                    moveX = 10/4f;
                    x = 26/4f;
                    moveRot = 5;
                    progress = warmup;
                    moves.add(new PartMove(PartProgress.recoil, -.25f, -.25f, -12));
                }},new RegionPart("-barrel"){{
                    mirror = true;
                    moveY = -.5f;
                    moveX = 24/4f;
                    y = 0.25f;
                    progress = warmup.delay(0.2f);
                    moves.add(new PartMove(PartProgress.recoil, -.75f, 0.25f, 8));
                }});
            }};
            consumeCoolant(20/60f);
            limitRange(1.2f);
        }};
        sentry = new ItemPointDefenseTurret("sentry"){{
            requirements(Category.turret, with(metaglass, 85, copper, 60f, silicon, 80, graphite, 90));
            shootCone = 20;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            outlineColor = tantDarkestTone;
            size = 2;
            squareSprite = false;
            range = 150;
            inaccuracy = 7;
            reload = 7;
            recoil = 0.1f;
            xRand = 1;
            rotateSpeed  = 5;
            maxAmmo = 20;
            shoot.shotDelay = 0.5f;
            ammoUseEffect = Fx.casing2;
            shootSound = shootSnap;
            warmupMaintainTime = 90;
            shootWarmupSpeed = 0.01f;
            minWarmup = 0.01f;
            ammo(
                    silicon, new InterceptorBulletType(10, 30f, "aquarion-flechette"){{
                        collidesGround = false;
                        trailLength = 5;
                        hitSize = 7;
                        collidesTiles = false;
                        collidesAir = false;
                        ammoMultiplier = 5;
                        width = height = 8;
                        frontColor = lightColor = Pal.siliconAmmoFront;
                        backColor = trailColor = Pal.siliconAmmoBack;
                        trailInterp = v -> Math.max(Mathf.slope(v), 0.9f);
                    }},

                    copper, new InterceptorBulletType(25, 45f, "aquarion-flechette"){{
                        collidesGround = false;
                        trailLength = 7;
                        hitSize = 8;
                        width = height = 8;
                        collidesTiles = false;
                        collidesAir = false;
                        ammoMultiplier = 6;
                        reloadMultiplier = 2;
                        trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);
                    }}
            );
            consumeCoolant(30/60f);
            limitRange(1.1f);
            coolantMultiplier = 0.5f;
            drawer = new DrawTurret(){{

                parts.addAll(new RegionPart("-t"){{
                    drawRegion = false;
                    heatLight = true;
                    heatProgress = warmup;
                    heatColor = Color.valueOf("ff9389");
                    }},new RegionPart("-barrel1"){{
                        x = 10/4f;
                        moveX = 0;
                        y = 0;
                        progress = PartProgress.smoothReload.curve(Interp.slope);
                        layerOffset = -.01f;
                        color = Color.valueOf("333f4b");
                        colorTo = Color.valueOf("25303a");
                    }},new RegionPart("-barrel1"){{
                    color = Color.valueOf("25303a");
                    colorTo = Color.valueOf("333f4b");
                    x = -10/4f;
                    moveX = 0;
                    y = 0;
                    progress = PartProgress.smoothReload.curve(Interp.slope);
                    layerOffset = -.01f;
                }},new RegionPart("-barrel1"){{
                    color = Color.valueOf("333f4b");
                    colorTo = Color.valueOf("697d85");
                    x = -10/4f;
                    moveX = 10/4f;
                    y = 0;
                    progress = PartProgress.smoothReload.curve(Interp.slope);
                    layerOffset = -.01f;
                }},new RegionPart("-barrel1"){{
                    x =  0;
                    moveX = 10/4f;
                    color = Color.valueOf("697d85");
                    colorTo = Color.valueOf("8da6ab");
                    y = 0;
                    progress = PartProgress.smoothReload.curve(Interp.slope);
                    layerOffset = -.01f;
                }});
            }};
        }};
        thrash = new ItemTurret("thrash"){{
            requirements(Category.turret, with(polymer,400, metaglass, 450, lead, 1200));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            size = 4;
            squareSprite = false;
            outlineColor = tantDarkestTone;
            rotateSpeed = 0.7f;
            inaccuracy = 5;
            range = 440;
            reload = 240;
            recoil = 4;
            recoilTime = 120;
            cooldownTime = 90;
            ammoUseEffect = AquaFx.casing1;
            ammoEjectBack = 9f;
            maxAmmo = 80;
            ammoPerShot = 20;
            consumeCoolant(2);
            coolantMultiplier = 0.08f;
            targetAir = false;
            liquidCapacity = 200;
            shake = 4;
            shootEffect = AquaFx.shootLong;
            consumeLiquid(petroleum, 1);
            shootSound = mediumCannon;
            ammo(
                    coal, new ArtilleryBulletType(18f, 220f) {{
                        smokeEffect = AquaFx.thrashShootSmoke;
                        trailEffect = AquaFx.thrashTrailSmoke;
                        despawnShake = 3;
                        trailInterval = 2;
                        despawnEffect = hitEffect = AquaFx.thrashExplosion;
                        velocityRnd = 0.1f;
                        splashDamage = 340;
                        splashDamageRadius = 64f;
                        drag = 0.018f;
                        makeFire = true;
                        status = burning;
                        shrinkX = 0.35F;
                        shrinkY = 0.63F;
                        width = 12;
                        height = 18;
                        statusDuration = 600;
                        shrinkInterp = Interp.pow2Out;
                        despawnSound = dullExplosion;
                    }},
                    magnesiumPowder, new ArtilleryBulletType(22f, 320f) {{
                        smokeEffect = AquaFx.thrashShootSmoke;
                        trailEffect = AquaFx.thrashTrailSmoke;
                        despawnShake = 3;
                        trailInterval = 2;
                        despawnEffect = hitEffect = AquaFx.thrashExplosion;
                        velocityRnd = 0.1f;
                        splashDamage = 125;
                        splashDamageRadius = 80f;
                        drag = 0.016f;
                        makeFire = true;
                        status = burning;
                        shrinkX = 0.35F;
                        shrinkY = 0.63F;
                        width = 12;
                        height = 18;
                        statusDuration = 1200;
                        shrinkInterp = Interp.pow2Out;
                        despawnSound = dullExplosion;
                    }},
                    AquaItems.brimstone, new ArtilleryBulletType(18f, 250f) {{
                        smokeEffect = AquaFx.thrashShootSmoke;
                        trailEffect = AquaFx.thrashTrailSmoke;
                        despawnShake = 7;
                        trailInterval = 1;
                        despawnEffect = hitEffect = AquaFx.thrashExplosion;
                        velocityRnd = 0.12f;
                        splashDamage = 400;
                        splashDamageRadius = 50f;
                        drag = 0.013f;
                        makeFire = true;
                        status = burning;
                        shrinkX = 0.35F;
                        shrinkY = 0.63F;
                        width = 12;
                        height = 18;
                        statusDuration = 450;
                        shrinkInterp = Interp.pow2Out;
                        despawnSound = dullExplosion;
                    }});
            drawer = new DrawTurret(){{
                parts.addAll(new RegionPart("-eject"){{
                    moveY = -5f;
                    progress = PartProgress.recoil.curve(pow5Out);
                    moves.add(new PartMove(PartProgress.smoothReload.curve(pow2Out), 0, -7, 0));
                }},new RegionPart("-mid"){{
                    moveY = -7f;
                    progress = PartProgress.smoothReload.curve(pow2Out);
                }},new RegionPart("-barrel"){{
                    moveY = -10f;
                    growY = 0.4f;
                    growProgress = PartProgress.smoothReload.curve(pow2Out);
                    progress = PartProgress.smoothReload.curve(pow2Out);
                }} );
            }};
            limitRange(1.1f);
        }};
        flagellate = new ItemTurret("flagellate"){{
            requirements(Category.turret, with(steel, 2500, cupronickel, 1200, ferrosilicon, 2000, metaglass, 4000, nickel, 8000));
            size = 7;
            squareSprite = false;
            rotateSpeed = 0.1f;
            recoil = 0;
            reload = 540;
            shake = 12;
            targetAir = false;
            outlineColor = tantDarkestTone;
            ammoUseEffect = AquaFx.casing1;
            ammoEjectBack = 22;
            shoot = new ShootAlternate(92/4f);
            shootY = 216/4f;
            shootSound = largeCannon;
            recoils = 2;
            range = 1025;
            maxAmmo = 240;
            ammoPerShot = 60;
            consumeAmmoOnce = false;
            ammo(
                    steel, new ArtilleryBulletType(14, 300){{
                        drag = 0.007f;
                        splashDamage = 1800;
                        splashDamageRadius = 12*8f;
                        width = 20;
                        frontColor = hitColor = Pal.turretHeat;
                        backColor = trailColor = lightColor =  Pal.accentBack;
                        height = 32;
                        trailLength = 18;
                        trailWidth = 8;
                        ammoMultiplier = 1;
                        shrinkY = 0.8f;
                        shrinkInterp = Interp.pow2In;
                        trailInterp = Interp.pow2In;
                        shootEffect = Fx.shootBig2;
                        smokeEffect = AquaFx.shootSmokeMassive;
                        despawnEffect = AquaFx.flagellateExplosion;
                    }});

            limitRange(1.1f);
            drawer = new DrawTurret(){{
                turretLayer = Layer.legUnit +10;
                parts.addAll(new RegionPart("-end"){{
                    progress = PartProgress.recoil.curve(pow4In);
                    y = 0f;
                    heatProgress = PartProgress.recoil.curve(pow5Out);
                    moveY = -80/4f;
                    recoilIndex = 0;
                    turretHeatLayer = Layer.legUnit + 11;
                    moves.add(new PartMove(PartProgress.reload.curve(Interp.pow2In).inv(), 0,  100/4f, 0));
                }}, new RegionPart("-end"){{
                    progress = PartProgress.recoil.curve(pow4In);
                    y = 0f;
                    moveY = -80/4f;
                    x = 91/4f;
                    turretHeatLayer = Layer.legUnit + 11;
                    heatProgress = PartProgress.recoil.curve(pow5Out);
                    recoilIndex = 1;
                    moves.add(new PartMove(PartProgress.reload.curve(Interp.pow2In).inv(), 0, 100/4f, 0));
                }},new RegionPart("-barrel"){{
                    progress = PartProgress.recoil.curve(pow4In);
                    y = -12f;
                    moveY = -8;
                    recoilIndex = 0;
                    moves.add(new PartMove(PartProgress.reload.curve(Interp.pow2In).inv(), 0, 12, 0));
                }}, new RegionPart("-barrel"){{
                    progress = PartProgress.recoil.curve(pow4In);
                    y = -12f;
                    moveY = -8;
                    x = 91/4f;
                    recoilIndex = 1;
                    moves.add(new PartMove(PartProgress.reload.curve(Interp.pow2In).inv(), 0, 12, 0));
                }}, new RegionPart("-case"){{
                    moveY = -8f;
                    progress = PartProgress.recoil.curve(Interp.pow2In);
                }});
            }};
        }};
        dislocate = new ItemTurret("dislocate"){
            {
                requirements(Category.turret, with(ferrosilicon, 700, metaglass, 900, graphite, 250));
                shootCone = 45;
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                outlineColor = tantDarkestTone;
                size = 5;
                squareSprite = false;
                range = 300;
                inaccuracy = 15;
                reload = 6;
                recoil = 0.2f;
                xRand = 1;
                rotateSpeed = 7;
                itemCapacity = 30;
                ammoUseEffect = Fx.casing2;
                shootSound = shootBig;
                warmupMaintainTime = 90;
                shootWarmupSpeed = 0.01f;
                minWarmup = 0.01f;
                ammo(
                        ferrosilicon, new FlakBulletType(25, 15f) {{
                            trailLength = 7;
                            hitSize = 8;
                            scaleLife = true;
                            ammoMultiplier = 10;
                            explodeRange = 80;
                            fragBullets = 4;
                            collidesGround = true;
                            fragBullet = new BasicBulletType(4, 10f, "aquarion-flechette"){{
                                width = 4;
                                height = 6;
                                lifetime = 12;
                                frontColor = lightColor = Pal.siliconAmmoFront;
                                backColor = trailColor = Pal.siliconAmmoBack;
                            }};
                            width = 8;
                            height = 12;
                            frontColor = lightColor = Pal.siliconAmmoFront;
                            backColor = trailColor = Pal.siliconAmmoBack;
                            velocityRnd = 0.2f;
                            trailInterp = v -> Math.max(Mathf.slope(v), 0.9f);
                        }},
                        AquaItems.steel, new FlakBulletType(30, 45) {{
                            trailLength = 9;
                            hitSize = 12;
                            ammoMultiplier = 20;
                            explodeRange = 90;
                            splashDamage = 60;
                            scaleLife = true;
                            fragBullets = 4;
                            collidesGround = true;
                            fragBullet = new BasicBulletType(4, 10f, "aquarion-flechette"){{
                                width = 4;
                                height = 6;
                                lifetime = 12;
                                frontColor = lightColor = Color.white;
                                backColor = trailColor = Pal.gray;
                            }};
                            width = 8;
                            height = 12;
                            frontColor = lightColor = Color.white;
                            backColor = trailColor = Pal.gray;
                            velocityRnd = 0.2f;
                            trailInterp = v -> Math.max(Mathf.slope(v), 0.9f);
                        }}
                );
                consumeCoolant(4);
                limitRange(1.2f);
                coolantMultiplier = 0.2f;
                shoot.shots = 5;
                cooldownTime = 240;
                warmupMaintainTime = 240;
                shootWarmupSpeed = 0.02f;
                recoils = 2;
                shoot = new ShootAlternate(1);
                drawer = new DrawTurret() {{
                    parts.addAll(

                            //abs right
                            new RegionPart("-barrel1"){{
                                growProgress =  PartProgress.smoothReload.curve(circleIn).mul(-1);
                                progress = PartProgress.smoothReload.curve(circleIn);
                                growX = -0.1f;
                                xScl = 0.9f;
                                mixColor = Pal.darkestestGray.a(0.2f);
                                mixColorTo = Pal.darkestestGray.a(0.4f);
                                moveX = -22/4f;
                                moveY = 3/4f;
                                y = 12-6/4f;
                                x = 44/4f;
                            }},
                            //mid right
                            new RegionPart("-barrel1"){{
                                growProgress =  PartProgress.smoothReload.curve(circleIn).mul(-1);
                                progress = PartProgress.smoothReload.curve(circleIn);
                                growX = -0.1f;
                                moveX = -22/4f;
                                mixColor = Pal.darkestestGray.a(0.0f);
                                mixColorTo = Pal.darkestestGray.a(0.1f);
                                moveY = 3/4f;
                                y = 12-3/4f;
                                x = 22/4f;
                            }},
                            //abs left
                            new RegionPart("-barrel1"){{
                                growProgress =  PartProgress.smoothReload.curve(circleIn);
                                progress = PartProgress.smoothReload.curve(circleIn);
                                growX = -0.1f;
                                mixColor = Pal.darkestestGray.a(0.4f);
                                mixColorTo = Pal.darkestestGray.a(0.2f);
                                xScl = 0.9f;
                                moveX = 22/4f;
                                moveY = 3/4f;
                                y = 12-6/4f;
                                x = -44/4f;
                            }},
                            //mid left
                            new RegionPart("-barrel1"){{
                                growProgress =  PartProgress.smoothReload.curve(circleIn);
                                progress = PartProgress.smoothReload.curve(circleIn);
                                mixColor = Pal.darkestestGray.a(0.2f);
                                mixColorTo = Pal.darkestestGray.a(0.1f);
                                growX = -0.1f;
                                moveX = -22/4f;
                                moveY = -3/4f;
                                y = 12-3/4f;
                                x = -22/4f;
                            }},
                            //Abs middle
                            new RegionPart("-barrel1"){{
                                progress = PartProgress.smoothReload.curve(circleIn);
                                mixColor = Pal.darkestestGray.a(0.1f);
                                mixColorTo = Pal.darkestestGray.a(0);
                                moveX = -22/4f;
                                moveY = -3/4f;
                                y = 12f;
                                x = 0;
                            }},
                            new RegionPart("-plunger"){{
                        progress = DrawPart.PartProgress.recoil;
                        recoilIndex = 0;
                        moveY = -5f;
                                y = 0.25f;
                    }},
                    new RegionPart("-plunger"){{
                        progress = PartProgress.recoil;
                        recoilIndex = 1;
                        x = 55/4f;
                        moveY = -5f;
                        y = 0.25f;
                    }},
                            new RegionPart("-mid") {{
                                progress = PartProgress.recoil;
                                moveY = -1f;
                                heatProgress = warmup;
                            }},
                            new RegionPart("-back"){{
                                progress = PartProgress.recoil;
                                moveY = 1f;
                            }},
                            new RegionPart("-side"){{
                                progress = PartProgress.recoil;
                                moveX = 0.5f;
                                moveY = -1.5f;
                                moveRot = -8f;
                                mirror = true;
                                x = 9.25f;
                                y = 5.5f;
                            }}
                    );
                }};
            }};
        pelt = new ItemTurret("pelt"){{
            requirements(Category.turret, with(lead, 85, nickel, 50f, silicon, 90, graphite, 35));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            health = 800;
            outlineColor = tantDarkestTone;
            range = 190;
            rotateSpeed = 1.8f;
            recoil = 3;
            recoilTime = 85;
            shootCone = 3f;
            reload = 130;
            soundPitchMax = 1.5f;
            soundPitchMin = 1.2f;
            shootSound = shootAltLong;

            size = 2;
            ammoPerShot = 8;
            maxAmmo = 24;
            consumeCoolant(20/60f);
            ammo(
                    lead, new BasicBulletType(9, 55){{
                        pierce = true;
                        pierceBuilding = true;
                        pierceCap = 2;
                        sprite = "aquarion-bolt";
                        width = 8;
                        height = 14f;
                        ammoMultiplier = 3;
                        reloadMultiplier = 1.8f;
                        rangeChange = -24;
                        shrinkY = 0;
                        shrinkX = 0.1f;
                        hitSize = 8;
                        trailWidth = 2f;
                        trailLength = 12;
                        frontColor = hitColor = Color.white;
                        backColor = lightColor = trailColor = Color.valueOf("8d70ab");
                        despawnEffect = hitEffect = Fx.hitSquaresColor;
                        shootEffect = Fx.shootBig2;
                        smokeEffect = Fx.shootSmokeDisperse;
                    }},
                    nickel, new BasicBulletType(5, 90){{
                        pierce = true;
                        pierceBuilding = true;
                        pierceCap = 3;
                        sprite = "aquarion-bolt";
                        width = 10;
                        height = 18f;
                        shrinkY = 0;
                        shrinkX = 0.2f;
                        trailWidth = 3f;
                        trailLength = 16;
                        reloadMultiplier = 0.8f;
                        hitSize = 8;
                        rangeChange = 30;
                        ammoMultiplier = 2;
                        frontColor = hitColor = Color.white;
                        backColor = lightColor = trailColor = Color.valueOf("e1d9bc");
                        despawnEffect = hitEffect = Fx.hitSquaresColor;
                        shootEffect = Fx.shootBig2;
                        smokeEffect = Fx.shootSmokeDisperse;
                    }},
                    cupronickel, new BasicBulletType(5, 120){{
                        pierce = true;
                        pierceBuilding = false;
                        pierceCap = 2;
                        sprite = "aquarion-bolt";
                        width = 8;
                        height = 12f;
                        shrinkY = 0;
                        rangeChange = 54;
                        shrinkX = 0.2f;
                        trailWidth = 3f;
                        trailLength = 16;
                        reloadMultiplier = 2f;
                        shieldDamageMultiplier = 4;
                        hitSize = 6;
                        ammoMultiplier = 3;
                        frontColor = hitColor = Color.white;
                        backColor = lightColor = trailColor = copper.color;
                        despawnEffect = hitEffect = Fx.hitSquaresColor;
                        shootEffect = Fx.shootBig2;
                        smokeEffect = Fx.shootSmokeDisperse;
                    }}
            );
            limitRange(1.1f);
            squareSprite = false;
            drawer = new DrawTurret(){{
                parts.add(new RegionPart("-bolt"){{
                    moveY = -5;
                    progress = PartProgress.reload.curve(Interp.pow2In);
                    mixColorTo = Pal.accent;
                    mixColor = new Color(1f, 1f, 1f, 0f);
                    colorTo = new Color(1f, 1f, 1f, 0f);
                    color = Color.white;
                    under = true;
                    layer = Layer.turret -1;
                }});
            }};
        }};
        refraction = new ContinuousTurret("refraction"){{
            requirements(Category.turret, with(metaglass, 90, cupronickel, 120, lead, 100, graphite, 120));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            size = 2;
            shootType = new PointLaserBulletType(){{
                damage = 240/6f;
                damageInterval = 10;
                buildingDamageMultiplier = 0.3f;
                collidesGround = false;
                collidesAir = true;
                hitColor = Color.valueOf("e3f759");
                sprite = "aquarion-refraction-point";
                maxRange = 260;
            }};
            scaleDamageEfficiency = true;
            shootSound = Sounds.none;
            loopSoundVolume = 1f;
            loopSound = Sounds.laserbeam;
            targetGround = false;
            targetAir = true;

            shootWarmupSpeed = 0.08f;
            shootCone = 360f;

            aimChangeSpeed = 3f;
            rotateSpeed = 6f;

            shootY = 1f;
            outlineColor = tantDarkestTone;
            size = 2;
            envEnabled |= Env.space;
            range = 240f;
            scaledHealth = 210;

            unitSort = UnitSorts.strongest;

            consumeLiquid(water, 15f / 60f);
            consumePower( 2);
        }};
        douse = new LiquidTurret("douse"){{
            requirements(Category.turret, with(metaglass, 90, nickel, 60, copper, 20, graphite, 25));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            size = 2;
            liquidCapacity = 90;
            ammoPerShot = 4;
            reload = 5;
            shoot.shots = 5;
            shootCone = 30;
            inaccuracy = 35;
            range = 170;
            health = 800;
            shootEffect = Fx.shootLiquid;
            velocityRnd = 0.1f;
            ammo(
                    water, new LiquidBulletType(water){{
                        lifetime = 49f;
                        speed = 4f;
                        knockback = 1.7f;
                        puddleSize = 8f;
                        orbSize = 4f;
                        drag = 0.001f;
                        ammoMultiplier = 0.4f;
                        statusDuration = 60f * 4f;
                        damage = 0.2f;
                        layer = Layer.bullet - 2f;
                    }},
                    Liquids.slag,  new LiquidBulletType(Liquids.slag){{
                        lifetime = 49f;
                        speed = 4f;
                        knockback = 1.3f;
                        puddleSize = 8f;
                        orbSize = 4f;
                        damage = 4.75f;
                        drag = 0.001f;
                        ammoMultiplier = 0.4f;
                        statusDuration = 60f * 4f;
                    }},
                    Liquids.cryofluid, new LiquidBulletType(Liquids.cryofluid){{
                        lifetime = 49f;
                        speed = 4f;
                        knockback = 1.3f;
                        puddleSize = 8f;
                        orbSize = 4f;
                        drag = 0.001f;
                        ammoMultiplier = 0.4f;
                        statusDuration = 60f * 4f;
                        damage = 0.2f;
                    }},
                    Liquids.oil, new LiquidBulletType(Liquids.oil){{
                        lifetime = 49f;
                        speed = 4f;
                        knockback = 1.3f;
                        puddleSize = 8f;
                        orbSize = 4f;
                        drag = 0.001f;
                        ammoMultiplier = 0.4f;
                        statusDuration = 60f * 4f;
                        damage = 0.2f;
                        layer = Layer.bullet - 2f;
                    }},
                    petroleum, new LiquidBulletType(petroleum){{
                        lifetime = 52f;
                        speed = 3.5f;
                        knockback = 1.5f;
                        puddleSize = 9f;
                        orbSize = 4f;
                        drag = 0.001f;
                        status = tarred;
                        ammoMultiplier = 0.4f;
                        statusDuration = 60f * 8f;
                        damage = 0.8f;
                        layer = Layer.bullet - 2f;
                    }},
                    AquaLiquids.magma, new LiquidBulletType(AquaLiquids.magma){{
                        lifetime = 49f;
                        speed = 4f;
                        knockback = 0.5f;
                        puddleSize = 9f;
                        orbSize = 4f;
                        drag = 0.001f;
                        status = melting;
                        ammoMultiplier = 0.8f;
                        statusDuration = 60f * 5f;
                        damage = 5f;
                        layer = Layer.bullet - 2f;
                    }},
                    AquaLiquids.magma, new LiquidBulletType(AquaLiquids.magma){{
                        lifetime = 49f;
                        speed = 4f;
                        knockback = 0.5f;
                        puddleSize = 9f;
                        orbSize = 4f;
                        drag = 0.001f;
                        status = melting;
                        ammoMultiplier = 0.8f;
                        statusDuration = 60f * 5f;
                        damage = 5f;
                        layer = Layer.bullet - 2f;
                    }},
                    haze, new LiquidBulletType(haze){{
                        lifetime = 49f;
                        speed = 7f;
                        knockback = 0.5f;
                        puddleSize = 9f;
                        reloadMultiplier = 5;
                        orbSize = 4f;
                        drag = 0.001f;
                        status = burning;
                        ammoMultiplier = 0.8f;
                        statusDuration = 60f * 2f;
                        damage = 1f;
                        layer = Layer.bullet - 2f;
                    }}
            );
        }};
        Foment = new ItemTurret("foment") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            health = 925;
            coolantMultiplier = 0.75f;
            {
                outlineColor = tantDarkestTone;
                ammo(
                    lead, new MissileBulletType(3f, 75, "bullet") {{
                        width = 10f;
                        height = 16f;
                        trailLength = 12;

                        lifetime = 60f;
                        ammoMultiplier = 1;
                        shootEffect = AquaFx.shootHori;
                        smokeEffect = new MultiEffect(AquaFx.shootSmokeTri, AquaFx.fomentShootSmoke);
                        trailEffect = Fx.none;
                        weaveMag = 2;
                        homingPower = 0.01f;
                        homingDelay = 5;
                        weaveScale = 1.75f;
                        shrinkX = 0.2f;
                        shrinkY = 0.8f;
                        frontColor = lightColor = hitColor = Color.white;
                        backColor = trailColor = Color.valueOf("8d70ab");
                            hitEffect = despawnEffect = AquaFx.fomentHitColor;
                    }},
                        AquaItems.ferricMatter, new MissileBulletType(3.2f, 150, "bullet") {{
                        width = 9f;
                        height = 15f;
                        trailLength = 10;
                        lifetime = 60f;
                        reloadMultiplier = 0.4f;
                        rangeChange = 32;
                        trailEffect = Fx.none;
                        heatColor = Color.white;
                        shootEffect = AquaFx.shootHori;
                        smokeEffect = new MultiEffect(Fx.shootSmokeSquareSparse, AquaFx.pentagonShootSmoke, AquaFx.fomentShootSmoke);
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
                        ammoMultiplier = 3;
                        frontColor = lightColor = hitColor = Color.white;
                        backColor = trailColor = Color.valueOf("8d706a");
                        hitEffect = despawnEffect = AquaFx.fomentHitColor;
                    }},
                        ferrosilicon, new MissileBulletType(7.5f, 190, "bullet") {{
                        width = 12f;
                        height = 18f;
                        rangeChange = 16;
                        trailLength = 8;
                        lifetime = 60f;
                        reloadMultiplier = 0.9f;
                        pierce = true;
                        pierceCap = 3;
                        pierceDamageFactor = 0.8f;
                        ammoMultiplier = 5;
                        trailEffect = Fx.none;
                        shootEffect = AquaFx.shootHori;
                        hitEffect = despawnEffect = AquaFx.fomentHitColor;
                        smokeEffect = new MultiEffect(AquaFx.pentagonShootSmoke, Fx.shootSmokeSquareSparse, AquaFx.fomentShootSmoke);
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
                ammoEjectBack = 2;
                ammoUseEffect = Fx.casing3;
                reload = 45;
                consumeAmmoOnce = true;
                maxAmmo = 15;
                inaccuracy = 3;
                ammoPerShot = 3;
                xRand = 0.5f;
                outlineColor = tantDarkestTone;
                shoot.shotDelay = 5;
                recoils = 2;
                shootWarmupSpeed = 0.001f;
                warmupMaintainTime = 360;
                heatColor = Pal.redLight;
                consumeCoolant(40/60f);
                shoot = new ShootAlternate(4);
                drawer = new DrawTurret(){{
                    for(int i = 0; i < 2; i ++){
                        int f = i;
                        parts.add(new RegionPart("-barrel-" + (i == 0 ? "l" : "r")){{
                            progress = PartProgress.recoil.curve(pow2In);
                            recoilIndex = f;
                            under = true;
                            heatProgress = warmup.curve(pow5Out);
                            moveY = -2f;
                        }});
                    }
                }};

            }
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        grace = new ItemTurret("grace"){{
            requirements(Category.turret, with(cupronickel, 150, metaglass, 100, lead, 260, graphite, 200f));
            size = 3;
            shootSound = AquaSounds.graceShot;
            squareSprite = false;
            consumeLiquid(water, 12);
            coolantMultiplier = 0.3f;
            reload = 25;
            liquidCapacity = 1200;
            //consumePower(5);
            range = 300;
            inaccuracy = 3;
            targetAir = false;
            targetGround = true;
            maxAmmo = 24;
            outlineColor = tantDarkestTone;
            ammoPerShot = 3;
            ammo(
                    silicon, new ArtilleryBulletType(3, 45){{
                        width = 8;
                        height = 10;
                        status = shocked;
                        statusDuration = 10*60;
                        trailLength = 12;
                        trailEffect = Fx.mineSmall;
                        trailInterval = 5;
                        frontColor = Color.white;
                        hitColor = backColor = lightColor = trailColor = silicon.color;
                        splashDamage = 45f;
                        splashDamageRadius = 3f*8f;
                        despawnEffect = hitEffect = new ExplosionEffect(){{
                            sparks = 12;
                            sparkLen = 20;
                            sparkStroke = 2;
                            smokes = 8;
                            smokeSize = 5;
                            waveLife = 40;
                            waveStroke = 2;
                            waveRad = 25;
                            waveColor = Items.silicon.color;
                            smoke = Color.gray;
                            sparkColor = Pal.sap;
                        }};
                    }},
                    magnesiumPowder, new ArtilleryBulletType(2.5f, 80){{
                        width = 8;
                        height = 10;
                        status = blasted;
                        statusDuration = 10*60;
                        trailLength = 12;
                        trailEffect = Fx.mineSmall;
                        trailInterval = 5;
                        frontColor = Color.white;
                        hitColor = backColor = lightColor = trailColor = Color.gray;
                        splashDamage = 75f;
                        splashDamageRadius = 5*8f;
                        despawnEffect = hitEffect = new ExplosionEffect(){{
                            sparks = 12;
                            sparkLen = 20;
                            sparkStroke = 2;
                            smokes = 8;
                            smokeSize = 5;
                            waveLife = 40;
                            waveStroke = 2;
                            waveRad = 25;
                            waveColor = Color.white;
                            smoke = Color.gray;
                            sparkColor = Color.red;
                        }};
                    }},
                    copper, new ArtilleryBulletType(6f, 45){{
                        width = 8;
                        height = 10;
                        trailLength = 12;
                        status = electrified;
                        statusDuration = 10*60;
                        trailEffect = Fx.mineSmall;
                        trailInterval = 5;
                        frontColor = Color.white;
                        ammoMultiplier = 2;
                        hitColor = backColor = lightColor = trailColor = copper.color;
                        splashDamage = 35f;
                        splashDamageRadius = 3.5f*8f;
                        despawnEffect = hitEffect = new ExplosionEffect(){{
                            sparks = 12;
                            sparkLen = 20;
                            sparkStroke = 2;
                            smokes = 8;
                            smokeSize = 5;
                            waveLife = 40;
                            waveStroke = 2;
                            waveRad = 25;
                            waveColor = Items.copper.color;
                            smoke = Items.coal.color;
                            sparkColor = Pal.accent;
                        }};
                    }}
            );
            limitRange(1.1f);
        }};
        confront = new ItemTurret("confront"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.turret, with(lead, 350, strontium, 400, metaglass, 250, AquaItems.ferricMatter, 150));
            health = 1125;
            size = 4;
            squareSprite = false;
            range = 300;
            limitRange(1f);
            reload = 10;
            consumeAmmoOnce = true;
            maxAmmo = 30;
            inaccuracy = 2;
            ammoPerShot = 1;
            outlineColor = tantDarkestTone;
            recoils = 4;
            shootWarmupSpeed = 0.05f;
            minWarmup = 0.65f;
            warmupMaintainTime = 360;
            heatColor = Pal.redLight;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            shootY = 0;
            coolantMultiplier = 0.2f;
            ammo(
                    lead, new BasicBulletType(8, 65, "bullet") {{
                        width = 10f;
                        height = 16f;
                        trailLength = 12;
                        lifetime = 60f;
                        ammoMultiplier = 1;
                        shootEffect = AquaFx.shootLong;
                        smokeEffect =  Fx.smoke;
                        trailEffect = Fx.none;
                        despawnEffect = hitEffect = AquaFx.fomentHitColor;
                        shrinkX = 0.2f;
                        shrinkY = 0.8f;
                        frontColor = lightColor = hitColor = Color.white;
                        backColor = trailColor = Color.valueOf("8d70ab");
                    }},
                    AquaItems.ferricMatter, new BasicBulletType(6, 80, "bullet") {{
                        width = 10f;
                        height = 18f;
                        trailLength = 10;
                        lifetime = 60f;
                        ammoMultiplier = 4;
                        shootEffect = AquaFx.shootLong;
                        smokeEffect = Fx.smoke;
                        trailEffect = Fx.none;
                        shrinkX = 0.2f;
                        shrinkY = 0.8f;
                        frontColor = lightColor = hitColor = Color.white;
                        backColor = trailColor = AquaItems.ferricMatter.color;
                        splashDamage = 40;
                        splashDamageRadius = 8*3;
                        hitEffect = despawnEffect = new ExplosionEffect(){{
                            smoke = smokeLight;
                            sparks = 12;
                            sparkLen = 4;
                            smokeRad = 2;
                            smokes = 5;
                            waveLife = 10;
                            waveColor = Pal.accent;
                            sparkColor = Pal.accent;
                            waveRad = 8;
                        }};
                    }},
                    manganese, new BasicBulletType(6, 50, "bullet") {{
                        width = 10f;
                        height = 18f;
                        trailLength = 10;
                        lifetime = 60f;
                        ammoMultiplier = 1;
                        reloadMultiplier = 2f;
                        shootEffect = AquaFx.shootLong;
                        smokeEffect =  Fx.smoke;
                        trailEffect = Fx.none;
                        despawnEffect = hitEffect = AquaFx.fomentHitColor;
                        trailEffect = Fx.none;
                        shrinkX = 0.2f;
                        shrinkY = 0.8f;
                        frontColor = lightColor = hitColor = Color.white;
                        backColor = trailColor = AquaItems.manganese.color;
                    }});
            shoot = new ShootBarrel() {{
                barrels = new float[]{
                        8, 8, 0,
                        -8, 8f, 0,
                        3, 18f, 0,
                        -3, 18f, 0
                };
            }};
            drawer = new DrawTurret(){{
                parts.add(new RegionPart("-barrel1-r"){{
                    recoilIndex = 0;
                    progress = PartProgress.recoil;
                    moveY = -3;
                    layerOffset = -.002f;
                    moves.add(new PartMove(PartProgress.warmup, 5, -4, 0));
                }},new RegionPart("-barrel1-l"){{
                    recoilIndex = 1;
                    layerOffset = -.002f;
                    progress = PartProgress.recoil;
                    moveY = -3;
                    xScl = -1;
                    moves.add(new PartMove(PartProgress.warmup, -5, -4, 0));
                }},new RegionPart("-barrel-r"){{
                    layerOffset = -.001f;
                    moveY = -3f;
                    recoilIndex = 2;
                    progress = PartProgress.recoil;
                }},new RegionPart("-barrel-l"){{
                    layerOffset = -.001f;
                    recoilIndex = 3;
                    moveY = -3f;
                    xScl = -1;
                    progress = PartProgress.recoil;
                }});
            }};
            consumeCoolant(120/60f);
        }};
        redact  = new ItemTurret("redact"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            health = 825;
            requirements(Category.turret, with(AquaItems.ferricMatter, 90, silicon, 120, AquaItems.aluminum, 150));
            ammo(
                    AquaItems.arsenic, new BasicBulletType(6f, 50, "missile-large") {{
                        width = 8f;
                        pierce = true;
                        height = 14f;
                        splashDamage = 20;
                        homingPower = 0.001f;
                        coolantMultiplier = 0.2f;
                        splashDamageRadius = 8;
                        trailLength = 25;
                        trailWidth = 2.5f;
                        lifetime = 55f;
                        reloadMultiplier = 2f;
                        ammoMultiplier = 2;
                        shootEffect = new Effect(15, e -> {
                            color(e.color);
                            float w = 1.5f + 9 * e.fout();

                            Drawf.tri(e.x, e.y, w, 60f * e.fout(), e.rotation);
                            color(e.color);

                            Drawf.tri(e.x, e.y, w * 1.2f, 90f * e.fout(), e.rotation * 45f);
                            Drawf.tri(e.x, e.y, w, 60f * e.fout(), e.rotation + 180-45f);
                        });
                        smokeEffect = new MultiEffect(new Effect(110f, e -> {
                            color(e.color, e.fin());
                            rand.setSeed(e.id);
                            for(int i = 0; i < 7; i++){
                                float rot = e.rotation + rand.range(40f);
                                v.trns(rot, rand.random(e.finpow() * 8f));
                                float randomRotationSpeed = rand.random(0f, 180f);
                                float slowRotation = Interp.pow2In.apply(randomRotationSpeed * e.fout());
                                Fill.poly(e.x + v.x, e.y + v.y, 5, e.fout() * 5f, rand.random(700f) + slowRotation);
                            }
                        }), Fx.colorSpark);
                        trailRotation = true;
                        trailEffect = new Effect(60f, e -> {
                            color(e.color, e.fin());
                            rand.setSeed(e.id);
                            for(int i = 0; i < 5; i++){
                                float rot = e.rotation + rand.range(15f);
                                v.trns(rot, rand.random(e.finpow() * 8f));
                                float randomRotationSpeed = rand.random(0f, 180f);
                                float slowRotation = Interp.pow2In.apply(randomRotationSpeed * e.fout());
                                Fill.poly(e.x + v.x, e.y + v.y, 5, e.fout() * 4f, rand.random(700f) + slowRotation);
                            }
                        });
                        trailInterval = 1;
                        frontColor = lightColor = hitColor = Color.white;
                        hitEffect  = Fx.hitSquaresColor;
                        despawnEffect = new ExplosionEffect(){{
                            waveRad = 33;
                            waveStroke = 6;
                            waveLife = 20;
                            smokes = 8;
                            smokeRad = 6;
                            sparks = 12;
                            sparkLen = 6;
                            sparkStroke = 3;
                            sparkColor = AquaPal.arsenic;
                            waveColor = AquaPal.arsenic;
                            smoke = smokeLight;
                        }};
                        backColor = trailColor = Color.valueOf("e58ca0");
                        fragBullets = 3;
                        fragBullet = new BasicBulletType(4, 10, "missile-large"){{
                            width = height = 8;
                            trailLength = 8;
                            frontColor = Color.white;
                            backColor = trailColor = lightColor = hitColor = AquaPal.arsenic;
                            despawnEffect = new ExplosionEffect(){{
                                waveRad = 10;
                                waveStroke = 6;
                                waveLife = 10;
                                smokes = 3;
                                smokeRad = 2;
                                sparks = 6;
                                sparkLen = 3;
                                sparkStroke = 2;
                                sparkColor = AquaPal.arsenic;
                                waveColor = AquaPal.arsenic;
                                smoke = smokeLight;
                            }};
                        }};
                    }},
                    AquaItems.bauxite, new BasicBulletType(7f, 55, "missile-large") {{
                    width = 15f;
                    pierce = true;
                    pierceCap = 2;
                    height = 19f;
                    trailLength = 34;
                    trailWidth = 3;
                    lifetime = 60f;
                    rangeChange = -5*8f;
                    ammoMultiplier = 1;
                    homingPower = 0.001f;
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
                    AquaItems.aluminum, new BasicBulletType(5f, 155, "missile-large") {{
                    width = 15f;
                    pierce = false;
                    height = 19f;
                    trailLength = 34;
                    trailWidth = 3;
                    homingPower = 0.001f;
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
                    AquaItems.invar, new BasicBulletType(4f, 350f, "missile-large") {{
                    width = 15f;
                    pierce = false;
                    height = 19f;
                    splashDamage = 50;
                    homingPower = 0.001f;
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
            inaccuracy = 0;
            ammoPerShot = 3;
            outlineColor = tantDarkestTone;
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
            consumeCoolant(120/60f);
        }};
        focus = new ItemTurret("focus"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.turret, with(AquaItems.aluminum, 500, lead, 600, metaglass, 500, AquaItems.ferricMatter, 250));
            size = 4;
            shootY = 85/4f;
            itemCapacity = 60;
            chargeSound = lasercharge2;
            shootSound = malignShoot;
            squareSprite = false;
            consumeLiquid(nitrogen, 0.25f);
            shoot.firstShotDelay = 60;
            range = 400;
            reload = 5*60f;
            shootWarmupSpeed = 0.01f;
            rotateSpeed = 0.6f;
            minWarmup = 0.95f;
            warmupMaintainTime = 250;
            ammoPerShot = 20;
            recoilTime = 90;
            ammo(
                    AquaItems.towanite, new LaserBulletType(){{
                        length = 400;
                        damage = 650;
                        sideAngle = 45;

                        shootEffect = Fx.shootTitan;
                        smokeEffect = AquaFx.GyreShootSmoke;
                        hitEffect = Fx.blastExplosion;
                        colors = new Color[]{
                                Color.valueOf("fffe27"),
                                Color.valueOf("e3ba0d").a(0.8f),
                                Color.valueOf("a5340d").a(0.5f),
                                Color.valueOf("5d0303").a(0.2f),
                        };
                    }},
                    acuminite, new LaserBulletType(){{
                        length = 250;
                        damage = 350;
                        sideAngle = 45;
                        rangeChange = -150;
                        shootEffect = Fx.shootTitan;
                        smokeEffect = AquaFx.GyreShootSmoke;
                        hitEffect = Fx.blastExplosion;
                        colors = new Color[]{
                                Color.valueOf("fffe27"),
                                Color.valueOf("e3ba0d").a(0.8f),
                                Color.valueOf("a5340d").a(0.5f),
                                Color.valueOf("5d0303").a(0.2f),
                        };
                    }},
                    AquaItems.azurite, new LaserBulletType(){{
                        length = 510;
                        damage = 300;
                        reloadMultiplier = 1.5f;
                        sideAngle = 45;
                        rangeChange = 90;
                        shootEffect = Fx.shootTitan;
                        smokeEffect = AquaFx.GyreShootSmoke;
                        hitEffect = Fx.blastExplosion;
                        colors = new Color[]{

                                Color.valueOf("6d5fff"),
                                Color.valueOf("7a35da").a(0.8f),
                                Color.valueOf("621a8b").a(0.5f),
                                Color.valueOf("6b0e6a").a(0.2f),
                        };
                    }}
            );
            outlineColor = tantDarkestTone;
            heatColor = Color.valueOf("d3f5ff");
            coolantMultiplier = 0.35f;
            consumeCoolant(80/60f);
            drawer = new DrawTurret(){{
                turretLayer = Layer.turret;
                parts.addAll(
                        new RegionPart("-barrel"){{
                            moveY = 24/4f;
                            progress = warmup.curve(circleIn);
                            heatProgress = charge;
                            layer = Layer.turret - 0.001f;
                            heatLayer = Layer.turret - 0.0005f;
                            moves.add(new PartMove(PartProgress.recoil.curve(pow2Out), 0, -10, 0));
                        }},
                        new RegionPart("-barrel1"){{
                            moveY = 70/4f;
                            progress = warmup.curve(circleIn);
                            heatProgress = charge;
                            layer = Layer.turret - 0.001f;
                            heatLayer = Layer.turret - 0.0005f;
                            moves.add(new PartMove(PartProgress.recoil.curve(pow2Out), 0, -10, 0));
                        }}
                );
            }};
        }};
        gyre = new ItemTurret("gyre") {{
            requirements(Category.turret, with(lead, 150, AquaItems.bauxite, 90f, AquaItems.ceramic, 60, AquaItems.chirenium, 30));
            ammo(
                    AquaItems.chirenium, new BasicBulletType(2.5f, 30, "bullet") {{
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
            outlineColor = tantDarkestTone;
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
            requirements(Category.turret, with(lead, 90, AquaItems.bauxite, 90, AquaItems.ceramic, 80));
            size = 3;
            outlineColor = tantDarkestTone;
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
                        smokeEffect = new MultiEffect(AquaFx.shootSmokeTri, AquaFx.fomentShootSmoke);
                        trailEffect = Fx.none;
                        weaveMag = 2;
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
            requirements(Category.turret, with(lead, 140, AquaItems.gallium, 200, AquaItems.ceramic, 140, lead, 240));
            size = 4;
            ammo(
                    lead, new MissileBulletType(2.5f, 18, "bullet") {{
                        width = 10f;
                        height = 16f;
                        trailLength = 12;

                        lifetime = 60f;
                        ammoMultiplier = 1;
                        shootEffect = AquaFx.shootLong;
                        smokeEffect = new MultiEffect(AquaFx.shootSmokeTri, AquaFx.fomentShootSmoke);
                        trailEffect = Fx.none;
                        weaveMag = 2;
                        homingDelay = 5;
                        weaveScale = 1.75f;
                        frontColor = lightColor = hitColor = AquaPal.bauxiteShoot;
                        backColor = trailColor = AquaPal.bauxiteLightTone;
                        buildingDamageMultiplier = 0.3f;
                    }});
            outlineColor = tantDarkestTone;
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
                requirements(Category.turret, with(lead, 140, AquaItems.chirenium, 100, AquaItems.ceramic, 160));
                ammo(
                        AquaItems.bauxite, new FlakBulletType(2.5f, 30) {{
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
                            smokeEffect = new MultiEffect(AquaFx.shootSmokeTri, AquaFx.fomentShootSmoke);
                            trailEffect = Fx.none;
                            weaveMag = 1;
                            weaveScale = 8;
                            homingDelay = 5;
                            velocityRnd = 0.1f;
                            frontColor = Color.white;
                            trailWidth = 4.5f;
                            trailLength = 15;
                            hitEffect = despawnEffect = AquaFx.fomentHitColor;
                            backColor = trailColor = AquaPal.bauxiteLightTone;
                            buildingDamageMultiplier = 0.3f;
                        }});
                size = 4;
                outlineColor = tantDarkestTone;
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
            requirements(Category.turret, with(lead, 45, AquaItems.electrum, 60, titanium, 20));
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
                    AquaItems.inconel, new RailBulletType() {{
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
                    nickel, new BasicBulletType(2.5f, 6){{
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
            requirements(Category.turret, with(AquaItems.electrum, 90, AquaItems.silver, 40, AquaItems.arsenic, 65));
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
            requirements(Category.turret, with(AquaItems.electrum, 35, lead, 40));
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
        maelstrom = new LiquidTurret("maelstrom"){{
                shoot.shotDelay = 2;
                shoot.shots = 5;
                ammoPerShot = 2;
                inaccuracy = 25;
                liquidCapacity = 200;
                reload = 2;
                outlineColor = tantDarkestTone;
                loopSound = Sounds.bioLoop;
                loopSoundVolume = 0.09f;
                shoot.firstShotDelay = 20;
                warmupMaintainTime = 50;
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                shootWarmupSpeed = 0.07f;
                minWarmup = 0.85f;
                range = 260;
                size = 3;
                squareSprite = false;
                shootSound = Sounds.blaster;
                targetGround = false;
                requirements(Category.turret, with(AquaItems.aluminum, 250, copper, 100));
                ammo(
                        fumes, new MissileBulletType(7, 10) {{
                            knockback = 2f;
                            drag = -0.02f;
                            lifetime = 25;
                            trailLength = 18;
                            trailWidth = 2;
                            weaveScale = 4;
                            weaveMag = 2;
                            splashDamage = 30f;
                            splashDamageRadius = 24f;
                            homingPower = 0.09f;
                            collidesTiles = false;
                            ammoMultiplier = 0.05f;
                            collidesGround = false;
                            shootEffect = Fx.shootSmokeSquareSparse;
                            backColor = trailColor = hitColor = lightColor = fumes.color;
                            frontColor = Color.white;
                            hitEffect = Fx.hitSquaresColor;
                            layer = Layer.bullet - 2f;
                            backSprite = "aquarion-star-bullet";
                            sprite = "aquarion-star-bullet";
                            hitEffect = new Effect(60f * 1.2f, 250f, e -> {
                                color(fumes.color, 0.65f);

                                randLenVectors(e.id, 10, 25f, (x, y) -> {
                                    Fill.circle(e.x + x, e.y + y, 4f * Mathf.clamp(e.fin() / 0.1f) * Mathf.clamp(e.fout() / 0.1f));
                                });
                            });
                            fragBullet = new EmptyBulletType(){{
                                lifetime = 60f;
                                bulletInterval = 10f;
                                loopSound = spray;
                                loopSoundVolume = 0.02f;
                                hitEffect = Fx.none;
                                despawnEffect = Fx.none;
                                intervalBullet = new EmptyBulletType(){{
                                    splashDamage = 5f;
                                    collidesGround = false;
                                    collidesAir = true;
                                    collides = false;
                                    hitEffect = Fx.none;
                                    pierce = true;
                                    despawnEffect = Fx.none;
                                    instantDisappear = true;
                                    splashDamageRadius = 20f;
                                    buildingDamageMultiplier = 0.2f;
                                }};
                            }};
                        }},
                        AquaLiquids.argon, new FlakBulletType(9, 15) {{
                            knockback = 2f;
                            drag = -0.02f;
                            splashDamage = 25f;
                            ammoMultiplier = 0.1f;
                            splashDamageRadius = 18f;
                            lifetime = 20;
                            trailLength = 20;
                            trailWidth = 2;
                            weaveScale = 4;
                            weaveMag = 2;
                            homingPower = 0.04f;
                            collidesTiles = false;
                            collidesGround = false;
                            shootEffect = Fx.shootSmokeSquareSparse;
                            backColor = trailColor = hitColor = lightColor = argon.color;
                            frontColor = Color.white;
                            hitEffect = Fx.hitSquaresColor;
                            layer = Layer.bullet - 2f;
                            backSprite = "aquarion-star-bullet";
                            sprite = "aquarion-star-bullet";
                            fragBullets = 6;
                            fragBullet = new BasicBulletType(3f, 4){{
                                width = 5f;
                                height = 12f;
                                shrinkY = 1f;
                                lifetime = 20f;
                                backColor = trailColor = Color.white;
                                hitColor = frontColor = argon.color;
                                despawnEffect = Fx.none;
                                collidesGround = false;
                            }};
                        }},AquaLiquids.fluorine, new BasicBulletType(9, 15) {{
                            knockback = 2f;
                            drag = -0.02f;
                            splashDamage = 30f * 1.5f;
                            splashDamageRadius = 24f;
                            lifetime = 20;
                            trailLength = 20;
                            trailWidth = 2;
                            weaveScale = 4;
                            ammoMultiplier = 0.05f;
                            reloadMultiplier = 0.5f;
                            weaveMag = 2;
                            homingPower = 0.04f;
                            collidesTiles = false;
                            collidesGround = false;
                            shootEffect = Fx.shootSmokeSquareSparse;
                            backColor = trailColor = hitColor = lightColor = fluorine.color;
                            frontColor = Color.white;
                            hitEffect = Fx.hitSquaresColor;
                            layer = Layer.bullet - 2f;
                            backSprite = "aquarion-star-bullet";
                            sprite = "aquarion-star-bullet";
                            bulletInterval = 8;
                            intervalBullets = 3;
                            intervalDelay = 5;
                            intervalBullet = new BasicBulletType(3, 5){{
                                weaveMag = 2;
                                weaveScale = 15;
                                collidesTiles = false;
                                collidesGround = false;
                                homingPower = 0.01f;
                                frontColor = Color.white;
                                backColor = trailColor = lightColor = hitColor = fluorine.color;
                                width = height = 4;
                                lifetime = 20;
                                shrinkX = shrinkY = 0;
                                trailLength = 8;
                                backSprite = "aquarion-star-bullet";
                                sprite = "aquarion-star-bullet";
                                despawnEffect = hitEffect = new ExplosionEffect(){{
                                    waveColor = fluorine.color;
                                    waveStroke = 2;
                                    waveRad = 5;
                                    smokes = 0;
                                    waveLife = 25;
                                    sparkStroke = 1;
                                    sparkLen = 3;
                                    sparkColor = Color.white;
                                }};
                            }};
                            despawnEffect = hitEffect = new ExplosionEffect(){{
                                waveColor = fluorine.color;
                                waveStroke = 4;
                                waveRad = 10;
                                waveLife = 25;
                                sparkStroke = 2;
                                sparkLen = 6;
                                smokes = 3;
                                smokeSize = 4;
                                smoke = Color.gray;
                                sparkColor = Color.white;
                            }};
                        }});

            }};
        torrefy = new LaserTurret("torrefy"){{
            requirements(Category.turret, with( copper, 3000, steel, 1500, ferrosilicon, 900, lead, 4000, metaglass, 2000));
            reload = 8*60f;
            minWarmup = 0.99f;
            shootWarmupSpeed = 0.01f;
            recoil = 0;
            outlineColor = tantDarkestTone;
            recoilTime = 10;
            range = 70*8f;
            size = 8;
            squareSprite = false;
            liquidCapacity = 250;
            ammoPerShot = 20;
            itemCapacity = 60;
            warmupMaintainTime = 20*60f;
            cooldownTime = 15*60f;
            shootY = 2f;
            rotateSpeed = 0.9f;
            firingMoveFract = 0.25f;
            shake = 3f;
            shootDuration = 12*60f;
            consumePower(2000/60f);
            loopSound = Sounds.beam;
            shootY = 138/4f;
            loopSoundVolume = 2f;
            consumeLiquids(LiquidStack.with(water, 5000/60f, haze, 1000/60f));
            shootType = new ContinuousLaserBulletType(145){{
                length = 600f;
                hitEffect = Fx.hitMeltdown;
                hitColor = Pal.meltdownHit;
                status = StatusEffects.melting;
                drawSize = 420f;
                timescaleDamage = true;
                width = 8*2.5f;

                incendChance = 0.4f;
                incendSpread = 5f;
                incendAmount = 1;
                ammoMultiplier = 1f;
                colors = new Color[]{
                        Color.valueOf("c07237").a(0.5f),
                        Color.valueOf("e1bc57").a(0.75f),
                        Color.valueOf("fefffb"),
                        Color.valueOf("f5ed7e"),
                        Color.valueOf("e1e4b1")
                };
            }};

            liquidCapacity = 2000;
            health = 7000;
            shootSound = Sounds.laserbig;
            baseExplosiveness = 5;
            drawer = new DrawTurret(){{
                for(int i = 0; i < 5; i ++) {
                    int  f = i;
                    parts.addAll(new RegionPart("-peen"){{
                        mirror = true;
                        progress = warmup.curve(linear).delay(f * 0.2f);
                        moveY = 30/4f * f;
                        moveX = 1;
                        moves.add(new PartMove(PartProgress.recoil.curve(pow2In).delay(0.2f * f), 2, 0, 0));
                    }});
                }
                for(int i = 0; i < 9; i ++) {
                    int f = i;
                    parts.addAll(new RegionPart("-bar"){{
                        moveX = 1.5f;
                        progress = warmup.curve(pow5In).slope().delay(f*0.1f);
                        moves.add(new PartMove(warmup.curve(pow2In), 0.5f, 3, 0));
                        moveY = 0;
                        x = 0;
                        y = f*(25/4f)-25/4f;
                        mirror = true;
                    }},new RegionPart("-bar1"){{
                        moves.addAll(new PartMove(warmup.curve(pow2In), 0.5f, 3, 0), new PartMove(warmup.curve(pow5In).slope().delay(f*0.1f), 1.5f, 0, 0));
                        progress = warmup.delay(0.8f).add(-0.1f * f).add(p -> Mathf.sin(12f, 0.4f) * p.warmup);
                        color = Color.valueOf("e25353").a(0);
                        colorTo = Color.valueOf("e25353").a(0.6f);
                        moveY = 0;
                        x = 0;
                        y = f*(25/4f)-25/4f;
                        mirror = true;
                    }});
                };
                    parts.addAll(new RegionPart("-laser"){{
                        xScl = 0.01f;
                        growX = 2;
                        moveY = 3;
                        color = Color.valueOf("ffffff").a(0);
                        colorTo = Color.valueOf("ffffff").a(1f);
                        mixColor = Color.valueOf("e25353").a(0);
                        mixColorTo = Color.valueOf("ffffff").a(1f);
                        progress = warmup;
                        growProgress = PartProgress.recoil.delay(0.2f).add(-0.1f).add(p -> Mathf.sin(9f, 0.2f) * p.recoil);
                    }},new RegionPart("-barrel"){{
                        moveX = 2f;
                        progress = warmup.curve(pow2In);
                        moveY = 3;
                        mirror = true;
                    }}, new RegionPart("-panel1"){{
                        mirror = true;
                        moveX = 3f;
                        moveY = -2;
                        progress = warmup.curve(pow2In).delay(0.6f);
                    }}, new RegionPart("-tank"){{
                        mirror = true;
                        moveX = 5;
                        progress = warmup.curve(pow2In).delay(0.6f);
                    }}, new RegionPart("-tank"){{
                        mirror = true;
                        moveX = 5;
                        y = 25f;
                        moveY = 20/4f;
                        progress = warmup.curve(pow2In).delay(0.6f);
                    }}, new RegionPart("-panel"){{
                        mirror = true;
                        moveX = 9;
                        moveY = -1;
                        progress = warmup.curve(pow2In).delay(0.5f);
                    }}, new RegionPart("-panel2"){{
                        mirror = true;
                        moveX = 6f;
                        moveY = -2;
                        moveRot = -5;
                        progress = warmup.curve(pow2In).delay(0.7f);
                    }}, new RegionPart("-panel3"){{
                        mirror = true;
                        moveX = 4.5f;
                        moveY = 0;
                        moveRot = 2;
                        progress = warmup.curve(pow2Out).delay(0.8f);
                    }}, new RegionPart("-panel4"){{
                        mirror = true;
                        moveX = 3f;
                        moveY = 5;
                        moveRot = 15;
                        progress = warmup.curve(pow2Out).delay(0.9f);
                    }}, new EffectSpawnerPart(){{
                        x = y = 0;
                        height = 256/4f;
                        width = 60/4f;
                        effect = Fx.coalSmeltsmoke;
                    }}, new EffectSpawnerPart(){{
                        x = y = 0;
                        height = 200/4f;
                        width = 45/4f;
                        effectRandRot = 5;
                        effectRot = -180;

                        effectColor = Color.valueOf("e9f984");
                        effect = Fx.colorSpark;
                        progress = warmup.delay(0.75f);
                    }}, new EffectSpawnerPart(){{
                        x = 90/4f;
                        y = -50/4f;
                        rotation = 45;
                        height = 70/4f;
                        width = 45/4f;
                        effectRandRot = 5;
                        effectRot = -180;
                        mirror = true;
                        effectColor = Color.valueOf("e9f984");
                        effect = Fx.colorSpark;
                        progress = warmup.delay(0.8f);
                    }}, new EffectSpawnerPart(){{
                        x = 90/4f;
                        y = -60/4f;
                        rotation = 45;
                        height = 40/4f;
                        width = 40/4f;
                        effectRandRot = 5;
                        effectRot = -180;
                        mirror = true;
                        effectColor = Color.valueOf("e9f984");
                        effect = new Effect(90f, 200f, b -> {
                            float intensity = 1.3f;

                            color(b.color, 0.7f);
                            for(int i = 0; i < 3; i++){
                                rand.setSeed(b.id*2 + i);
                                float lenScl = rand.random(0.5f, 1f);
                                int fi = i;
                                b.scaled(b.lifetime * lenScl, e -> {
                                    randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(2.9f * intensity), 13f * intensity, (x, y, in, out) -> {
                                        float fout = e.fout(Interp.pow5Out) * rand.random(0.25f, 0.5f);
                                        float rad = fout * ((2f + intensity) * 2.35f);

                                        Fill.circle(e.x + x, e.y + y, rad);
                                        Drawf.light(e.x + x, e.y + y, rad * 1.5f, b.color, 0.5f);
                                    });
                                });
                            }
                        }).layer(Layer.turret - 1f);
                        progress = warmup.delay(0.9f);
                    }});
                for(int i = 0; i < 8; i ++) {
                    int f = i;
                    parts.addAll(new RegionPart("-barrel-heat"){{
                        mirror = true;
                        outline = false;
                        progress = warmup.delay(0.8f).add(-0.1f * f).add(p -> Mathf.sin(9f, 0.2f) * p.warmup);
                        y = 0 + (f*18/4f);
                        color = Color.valueOf("e25353").a(0);
                        colorTo = Color.valueOf("e25353").a(0.6f);
                        moves.add(new PartMove(warmup.curve(pow2In),2, 3, 0));
                    }});
                }
            }};
        }};
        bend = new PowerTurret("bend"){{
            requirements(Category.turret, with(Items.copper, 60, Items.lead, 70, Items.silicon, 60, Items.titanium, 30));
            size = 4;
            consumePower(6f);
            outlineColor = Color.valueOf("111424");

            shootType = new LaserBulletType(140){{
                colors = new Color[]{Pal.lancerLaser.cpy().a(0.4f), Pal.lancerLaser, Color.white};
                //TODO merge
                chargeEffect = new MultiEffect(Fx.lancerLaserCharge, Fx.lancerLaserChargeBegin);

                buildingDamageMultiplier = 0.25f;
                hitEffect = Fx.hitLancer;
                hitSize = 4;
                lifetime = 16f;
                drawSize = 400f;
                collidesAir = false;
                length = 173f;
                ammoMultiplier = 1f;
                pierceCap = 4;
            }};
        }};
        azimuth = new PowerTurret("azimuth"){{
            requirements(Category.turret, with(Items.copper, 60, Items.lead, 70, Items.silicon, 60, Items.titanium, 30));
            size = 4;
            consumePower(6f);
            outlineColor = Color.valueOf("111424");

            shootType = new LaserBulletType(140){{
                colors = new Color[]{Pal.lancerLaser.cpy().a(0.4f), Pal.lancerLaser, Color.white};
                //TODO merge
                chargeEffect = new MultiEffect(Fx.lancerLaserCharge, Fx.lancerLaserChargeBegin);

                buildingDamageMultiplier = 0.25f;
                hitEffect = Fx.hitLancer;
                hitSize = 4;
                lifetime = 16f;
                drawSize = 400f;
                collidesAir = false;
                length = 173f;
                ammoMultiplier = 1f;
                pierceCap = 4;
            }};
        }};
    }
}
