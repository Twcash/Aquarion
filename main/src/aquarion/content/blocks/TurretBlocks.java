package aquarion.content.blocks;

import aquarion.content.*;
import aquarion.world.blocks.turrets.AquaItemTurret;
import aquarion.world.blocks.turrets.AquaPowerTurret;
import aquarion.world.blocks.turrets.ItemPointDefenseTurret;
import aquarion.world.drawers.AquaDrawTurret;
import aquarion.world.entities.bullet.AOEBulletType;
import aquarion.world.entities.bullet.AquaLaserBulletType;
import aquarion.world.entities.bullet.GambleBulletType;
import aquarion.world.entities.parts.NewRegPart;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.AquaPal;
import arc.func.Cons;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.content.*;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.entities.UnitSorts;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.EffectSpawnerPart;
import mindustry.entities.part.FlarePart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.*;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawTurret;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static aquarion.content.AquaBullets.*;

import static aquarion.content.AquaItems.brimstone;
import static aquarion.content.AquaItems.cupronickel;
import static aquarion.content.AquaItems.ferrosilicon;
import static aquarion.content.AquaItems.nickel;
import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaLiquids.fumes;
import static aquarion.content.AquaLiquids.*;
import static aquarion.content.AquaPlanets.*;
import static aquarion.world.graphics.AquaFx.rand;
import static aquarion.world.graphics.AquaFx.v;
import static aquarion.world.graphics.AquaPal.*;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static arc.math.Interp.*;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.content.StatusEffects.*;
import static mindustry.entities.part.DrawPart.PartProgress.*;
import static mindustry.gen.Sounds.*;
import static mindustry.type.ItemStack.with;

public class TurretBlocks {
    public static Block javelin, aftershock, suffocate,concuss, volt, grace, perforate, nostalgia, memorial, finite, mayhem, illustrate, acquit, clobber, flagellate, truncate, thrash, dislocate, refraction, confront, focus, douse, pelt, point, vector, sentry, maelstrom, Foment, redact, torrefy,
            blaze, ensign, hack;
    public static <T extends UnlockableContent> void overwrite(UnlockableContent target, Cons<T> setter) {
        setter.get((T) target);
    }

    public static void loadContent() {
        point = new AquaTemplates.AquaItemTurretTemplate("point") {{
            requirements(Category.turret, with(silicon, 35f, lead, 30));
            alwaysUnlocked = true;
            health = 250;
            shootSound = shootLaser;
            ammoUseEffect = Fx.casing1;
            ammoPerShot = 5;
            shootCone = 2;
            shoot.shots = 5;
            inaccuracy = 10;
            reload = 90;
            range = 110;
            ammo(silicon, AquaBullets.pointSilicon,
                    minium, AquaBullets.pointMinium,
                    graphite, AquaBullets.pointGraphite,
                    coal, AquaBullets.pointCoal,
                    brimstone, AquaBullets.pointBrimstone);
            limitRange(1.2f);
            consumeCoolant(10 / 60f);
            shoot.firstShotDelay = 15;
            moveWhileCharging = false;
            drawer = new DrawTurret(){{
                parts.addAll(new RegionPart("-rack"){{
                    moveY = -3.5f;
                    under = true;
                    progress = charge;
                }}, new RegionPart("-hammer"){{
                    moveX = 3f;
                    under = true;
                    progress = warmup;
                    moves.add(new PartMove(PartProgress.reload, 0,0,-180));
                    moves.add(new PartMove(charge.curve(pow10Out), 0,-0.5f,-180));
                }});
            }};
        }};
        volt = new AquaPowerTurret("volt"){{
            requirements(Category.turret, with(AquaItems.brass, 10, silicon, 90, copper, 150));
            health = 300;
            range = 80;
            squareSprite = false;
            outlineColor = tantDarkestTone;
            destroyEffect = new MultiEffect(AquaFx.factoryDestroy, Fx.dynamicExplosion);
            rotateSpeed = 2.2f;
            targetAir = false;
            recoil = 0;
            shootSound = shockBullet;
            reload = 5;
            shoot.shots = 3;
            consumePower(4);
            shoot.shotDelay = 1;
            shootType =  AquaBullets.voltShoot;
        }};
        pelt = new AquaTemplates.AquaItemTurretTemplate("pelt") {{
            requirements(Category.turret, with(lead, 85, nickel, 90f, silicon, 90, graphite, 60));
            health = 650;
            range = 190;
            rotateSpeed = 1.6f;
            recoil = 3;
            recoilTime = 85;
            shootCone = 3f;
            reload = 190;
            soundPitchMax = 1.5f;
            soundPitchMin = 1.2f;
            shootSound = shootBreach;
            size = 2;
            ammoPerShot = 10;
            maxAmmo = 40;
            liquidCapacity = 80;
            consumeCoolant(20 / 60f);
            ammo(
                    lead, AquaBullets.peltLead,
                    nickel, AquaBullets.peltNickel,
                    cupronickel, AquaBullets.peltCupronickel

            );
            limitRange(1.15f);
            drawer = new AquaDrawTurret() {{
                parts.add(new RegionPart("-boioioioing") {{
                    x = -13 / 4f;
                    moveRot = 360;
                    progress = PartProgress.reload.curve(Interp.pow2In);
                }}, new NewRegPart("-spike") {{
                    moveX = 5;
                    progress = PartProgress.reload.curve(Interp.pow5In);
                    alphaTo = 0;
                    alpha = 1;
                }}, new RegionPart("-top"), new RegionPart("-plate") {{
                    moveY = 5;
                    progress = PartProgress.recoil;
                }});
            }};
        }};
        suffocate = new AquaTemplates.AquaItemTurretTemplate("suffocate"){{
            requirements(Category.turret, with(silicon, 150, graphite, 80, nickel, 90));
            size = 2;
            range = 250;
            extinguish = true;
            recoilTime = 60;
            reload = 90;
            ammoPerShot = 10;
            maxAmmo = 60;
            shootSound = shootDiffuse;
            recoil = 1.5f;
            ammo(sand, AquaBullets.suffocateSand,
                    lead, AquaBullets.suffocateLead
            );
            limitRange(1.1f);
            drawer = new AquaDrawTurret(){{
                setAmmoParts(sand, Seq.with(new NewRegPart("-sand"){{
                    growProgress = PartProgress.reload.inv().mul(smoothReload.curve(Interp.pow5In).clamp());
                    yScl = 0.01f;
                    growY = 1;
                    y = -32/4f;
                    moveY = 32/4f;
                    under = true;
                    progress = PartProgress.reload.inv().mul(smoothReload.curve(Interp.pow5In).clamp());
                }}),lead, Seq.with(new NewRegPart("-lead"){{
                    growProgress = PartProgress.reload.inv().mul(smoothReload.curve(Interp.pow5In).clamp());
                    yScl = 0.01f;
                    growY = 1;
                    y = -32/4f;
                    moveY = 32/4f;
                    under = true;
                    progress = PartProgress.reload.inv().mul(smoothReload.curve(Interp.pow5In).clamp());
                }}));
                parts.addAll(new RegionPart("-pad"){{
                    moveY = 6f;
                    under = true;
                    progress = PartProgress.recoil.curve(pow5In);
                }}, new RegionPart("-top"){{
                    moveY = -0.5f;
                    progress = PartProgress.recoil;
                }});
            }};
        }};
        concuss = new AquaTemplates.AquaItemTurretTemplate("concuss"){{
            requirements(Category.turret, with(graphite, 200, silicon, 120, AquaItems.ferricMatter, 150));
            health = 650;
            size = 2;
            reload = 240;
            shoot.shots = 2;
            shoot = new ShootAlternate(2);
            shoot.shots = 2;
            ammoPerShot = 8;
            maxAmmo = 20;
            shootSound = shootMissileLong;
            range = 300;
            ammo(
                    graphite, AquaBullets.concussGraphite
            );
            limitRange(1.5f);
        }};
        vector = new AquaTemplates.AquaItemTurretTemplate("vector") {{
            requirements(Category.turret, with(cupronickel, 120, silicon, 110, metaglass, 150, graphite, 70));
            health = 650;
            size = 3;
            reload = 220;
            ammoPerShot = 9;
            range = 260;
            shootSound = AquaSounds.vectorShot;
            maxAmmo = 40;
            recoilTime = 45;
            warmupMaintainTime = 120;
            shootWarmupSpeed = 0.05f;
            minWarmup = 0.9f;
            rotateSpeed = 0.95f;
            recoils = 4;
            coolantMultiplier = 0.9f;
            liquidCapacity = 200;
            ammo(
                    copper, AquaBullets.vectorCopper,
                    cupronickel, AquaBullets.vectorCupronickel,
                    metaglass, AquaBullets.vectorMetaglass,
                    steel, AquaBullets.vectorSteel,
                    uranium, AquaBullets.vectorUranium
            );
            shoot = new ShootBarrel() {{
                barrels = new float[]{0, 6, 0, 0, -2, 0, 0, -11, 0, 0, -12, 0};
                shots = 4;
                shotDelay = 10;
            }};
            drawer = new DrawTurret() {{
                parts.addAll(new RegionPart("-puck") {{
                    growProgress = PartProgress.recoil;
                    progress = PartProgress.recoil;
                    recoilIndex = 0;
                    colorTo = new Color(1f, 1f, 1f, 0f);
                    color = Color.white;
                    y = 15 / 4f;
                    growX = -1f;
                    growY = -1f;
                }}, new RegionPart("-puck") {{
                    recoilIndex = 1;
                    growProgress = PartProgress.recoil;
                    progress = PartProgress.recoil;
                    colorTo = new Color(1f, 1f, 1f, 0f);
                    color = Color.white;
                    growX = -1f;
                    growY = -1f;
                }}, new RegionPart("-puck") {{
                    recoilIndex = 2;
                    growProgress = PartProgress.recoil;
                    progress = PartProgress.recoil;
                    colorTo = new Color(1f, 1f, 1f, 0f);
                    color = Color.white;
                    y = -15 / 4f;
                    growX = -1f;
                    growY = -1f;
                }}, new RegionPart("-puck") {{
                    recoilIndex = 3;
                    growProgress = PartProgress.recoil;
                    progress = PartProgress.recoil;
                    colorTo = new Color(1f, 1f, 1f, 0f);
                    color = Color.white;
                    y = -30 / 4f;
                    growX = -1f;
                    growY = -1f;
                }});
                for(int  i = 0; i < 4; i++){{
                    int finalI = i;
                    parts.add(new RegionPart("-yub"){{
                        moveX = 3;
                        y = (22/4f) * finalI;
                        recoilIndex = 3-finalI;
                        progress = PartProgress.recoil.curve(pow5In);
                        moves.add(new PartMove(PartProgress.warmup, -1, 0, 0));
                    }});
                }};
                parts.addAll( new RegionPart("-top"),new RegionPart("-shield-l"){{
                    growX = -0.5f;
                    growProgress = PartProgress.warmup.curve(Interp.pow5Out);
                    progress = PartProgress.warmup.curve(Interp.pow5Out);
                    moveX = -6/4f;
                }},new RegionPart("-shield-l"){{
                    growX = -0.5f;
                    growProgress = PartProgress.warmup.curve(Interp.pow5Out);
                    progress = PartProgress.warmup.curve(Interp.pow5Out);
                    moveX = -8/4f;
                }},new RegionPart("-shield-r"){{
                    growX = -0.5f;
                    growProgress = PartProgress.warmup.curve(Interp.pow5Out);
                    progress = PartProgress.warmup.curve(Interp.pow5Out);
                    moveX = 5/4f;
                }},new RegionPart("-shield-r"){{
                    growX = -0.5f;
                    growProgress = PartProgress.warmup.curve(Interp.pow5Out);
                    progress = PartProgress.warmup.curve(Interp.pow5Out);
                    moveX = 6/4f;
                }});
            }};
            consumeCoolant(40 / 60f);
            limitRange(1.2f);
        }};
        truncate = new AquaTemplates.AquaItemTurretTemplate("truncate") {{
            requirements(Category.turret, with(AquaItems.ferricMatter, 500, polymer, 350, graphite, 200));
            health = 1120;
            size = 4;
            consumeLiquid(methane, 2);
            shootSound = shootAtrax;
            soundPitchMin = 0.6f;
            soundPitchMax = 1.2f;
            ammoUseEffect = Fx.casing3;
            shootCone = 9;
            shoot.shots = 2;
            shoot.shotDelay = 10;
            inaccuracy = 6;
            recoil = 8;
            recoilTime = 300;
            reload = 240;
            range = 250;
            maxAmmo = 25;
            ammoPerShot = 5;
            targetAir = false;
            scaleLifetimeOffset = 60;
            ammo(
                    graphite, truncateGraphite
            );
            drawer = new DrawTurret() {{
                parts.addAll(new RegionPart("-shell") {{
                                 growProgress = progress = PartProgress.reload;
                                 x = 11f;
                                 moveX = -1;
                                 y = -1;
                                 xScl = 0.5f;
                                 growX = -0.5f;
                                 colorTo = Color.black;
                                 color = Color.grays(0.5f);
                             }}, new RegionPart("-shell") {{
                                 growProgress = progress = PartProgress.reload;
                                 x = 0;
                                 y = -1;
                                 moveX = 11;
                                 xScl = -0.75f;
                                 growX = 1.25f;
                                 colorTo = Color.grays(0.5f);
                                 color = Color.white;
                             }},
                        new RegionPart("-shell") {{
                            growProgress = progress = PartProgress.reload;
                            x = -11f;
                            moveX = 1;
                            y = -1;
                            xScl = 0.5f;
                            growX = -0.5f;
                            colorTo = Color.black;
                            color = Color.grays(0.5f);
                        }}, new RegionPart("-shell") {{
                            growProgress = progress = PartProgress.reload;
                            x = 0;
                            y = -1;
                            moveX = -11;
                            xScl = -0.75f;
                            growX = 1.25f;
                            colorTo = Color.grays(0.5f);
                            color = Color.white;
                        }}, new RegionPart("-tap") {{
                            progress = PartProgress.recoil.curve(pow2In);
                            moveY = -8f;
                        }}, new RegionPart("-mid"), new RegionPart("-cover") {{
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
                            moveX = -86 / 4f;
                        }}, new RegionPart("-cover") {{
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
                            moveX = -85 / 4f;
                        }}, new RegionPart("-cover") {{
                            mirror = true;
                            color = new Color(1f, 1f, 1f, 0f);
                            colorTo = new Color(1f, 1f, 1f, 1f);
                            progress = PartProgress.recoil.curve(pow5Out);
                            growProgress = PartProgress.recoil.curve(pow5Out);
                            rotation = 180;
                            yScl = -1;
                            growX = -1.5f;
                            moveX = -84 / 4f;
                        }}, new RegionPart("-cover") {{
                            mirror = true;
                            progress = PartProgress.recoil.curve(pow5Out);
                            growProgress = PartProgress.recoil.curve(pow5Out);
                            color = new Color(1f, 1f, 1f, 1f);
                            colorTo = new Color(1f, 1f, 1f, 0f);
                            growX = -1.5f;
                            moveX = 86 / 4f;
                        }}, new RegionPart("-cover") {{
                            mirror = true;
                            progress = PartProgress.recoil.curve(pow5Out);
                            growProgress = PartProgress.recoil.curve(pow5Out);
                            color = new Color(1f, 1f, 1f, 1f);
                            colorTo = new Color(1f, 1f, 1f, 0f);
                            growX = -1.5f;
                            moveX = 85 / 4f;
                        }}, new RegionPart("-cover") {{
                            mirror = true;
                            color = new Color(1f, 1f, 1f, 1f);
                            colorTo = new Color(1f, 1f, 1f, 0f);
                            progress = PartProgress.recoil.curve(pow5Out);
                            growProgress = PartProgress.recoil.curve(pow5Out);
                            growX = -1.5f;
                            moveX = 84 / 4f;
                        }});
            }};
            limitRange(1.6f);
            consumeCoolant(10 / 60f);
        }};
        perforate = new AquaTemplates.AquaItemTurretTemplate("perforate"){{
            requirements(Category.turret, with(AquaItems.steel, 300, polymer, 400, cupronickel, 200));
            size = 5;
            itemCapacity = 90;
            ammoPerShot = 15;
            reload = 400;
            shoot.shots = 40;
            shoot.shotDelay = 2;
            inaccuracy = 5;
            range = 400;
            recoilTime = 90;
            recoil = 2;
            ammo(
                    minium, perforateMinium,
                    brimstone, perforateBrimstone,
                    lead, perforateLead,
                    AquaItems.ferricMatter, perforateFerricMatter
            );
            maxAmmo = 120;
            rotateSpeed = 1.1f;
            consumeAmmoOnce = true;
            shootSound = shootFlame;
            trackingRange = 600;
            drawer = new DrawTurret(){{
                parts.addAll(new NewRegPart("-blade"){{
                    progress = smoothReload.curve(Interp.pow2In);
                    moveRot = 360*5;
                    under = true;
                }},new NewRegPart("-blade"){{
                    alpha = 1;
                    alphaTo = 0;
                    moveRot = 360*5;
                    under = true;
                    progress = PartProgress.smoothReload.curve(Interp.pow2In);
                }},new NewRegPart("-blade"){{
                    alpha = 0;
                    alphaTo = 1;
                    xScl = -1;
                    yScl = -1;
                    moveRot = 360*5;
                    under = true;
                    progress = PartProgress.smoothReload.curve(Interp.pow2In);
                }}, new RegionPart("-wing"){{
                    moveRot =  -5;
                    moveX = -1;
                    moveY = 0.5f;
                    mirror = true;
                    progress = PartProgress.recoil;
                }}, new RegionPart("-side"){{
                    moveRot =  7;
                    moveX = -2;
                    moveY = -0.5f;
                    mirror = true;
                    progress = PartProgress.recoil;
                }});
            }};
            limitRange(1.2f);
        }};
        aftershock = new AquaTemplates.AquaItemTurretTemplate("aftershock") {{
                requirements(Category.turret, with(AquaItems.ferricMatter, 300, polymer, 400, cupronickel, 200));
                size = 5;
                reload = 90;
                ammoPerShot = 15;
                itemCapacity = 80;
                range = 300;
                shootSound = AquaSounds.shootAftershock;
                maxAmmo = 60;
                warmupMaintainTime = 120;
                shootWarmupSpeed = 0.05f;
                minWarmup = 0.9f;
                rotateSpeed = 0.95f;
                shoot.shots = 45;
                ammoUseEffect = AquaFx.casing1;
                shoot.shotDelay = 0;
                consumeCoolant(4);
                coolantMultiplier = 0.3f;
                recoilTime = 80;
                velocityRnd = 0.7f;
                inaccuracy = 25;
                ammo(
                        minium, aftershockMinium,
                        metaglass, aftershockMetaglass,
                        scrap, aftershockScrap
                );
                limitRange(1.1f);
                drawer = new DrawTurret() {{
                    parts.addAll(new NewRegPart("-tur") {{
                        progress = PartProgress.reload.curve(Interp.pow2In);
                        moveRot = 180;
                    }}, new NewRegPart("-tur") {{
                        alpha = 1;
                        alphaTo = 0;
                        progress = PartProgress.reload.curve(Interp.pow2In);
                        moveRot = 180;
                    }}, new NewRegPart("-tur2") {{
                        alpha = 0;
                        alphaTo = 1;
                        progress = PartProgress.reload.curve(Interp.pow2In);
                        moveRot = 180;
                    }});
                }};
            }};
        sentry = new ItemPointDefenseTurret("sentry") {{
            requirements(Category.turret, with(metaglass, 85, copper, 60f, silicon, 80, graphite, 90));
            shootCone = 20;
            outlineColor = tantDarkestTone;
            size = 2;
            squareSprite = false;
            range = 150;
            inaccuracy = 7;
            reload = 7;
            recoil = 0.1f;
            xRand = 1;
            rotateSpeed = 5;
            maxAmmo = 20;
            shoot.shotDelay = 0.5f;
            ammoUseEffect = Fx.casing2;
            shootSound = shootScatter;
            warmupMaintainTime = 90;
            shootWarmupSpeed = 0.01f;
            minWarmup = 0.01f;
            ammo(
                    silicon, sentrySilicon,
                    copper, sentryCopper
            );
            consumeCoolant(30 / 60f);
            limitRange(1.1f);
            coolantMultiplier = 0.5f;
            drawer = new DrawTurret() {{

                parts.addAll(new RegionPart("-t") {{
                    drawRegion = false;
                    heatLight = true;
                    heatProgress = warmup;
                    heatColor = Color.valueOf("ff9389");
                }}, new RegionPart("-barrel1") {{
                    x = 10 / 4f;
                    moveX = 0;
                    y = 0;
                    progress = PartProgress.smoothReload.curve(Interp.slope);
                    layerOffset = -.01f;
                    color = Color.valueOf("333f4b");
                    colorTo = Color.valueOf("25303a");
                }}, new RegionPart("-barrel1") {{
                    color = Color.valueOf("25303a");
                    colorTo = Color.valueOf("333f4b");
                    x = -10 / 4f;
                    moveX = 0;
                    y = 0;
                    progress = PartProgress.smoothReload.curve(Interp.slope);
                    layerOffset = -.01f;
                }}, new RegionPart("-barrel1") {{
                    color = Color.valueOf("333f4b");
                    colorTo = Color.valueOf("697d85");
                    x = -10 / 4f;
                    moveX = 10 / 4f;
                    y = 0;
                    progress = PartProgress.smoothReload.curve(Interp.slope);
                    layerOffset = -.01f;
                }}, new RegionPart("-barrel1") {{
                    x = 0;
                    moveX = 10 / 4f;
                    color = Color.valueOf("697d85");
                    colorTo = Color.valueOf("8da6ab");
                    y = 0;
                    progress = PartProgress.smoothReload.curve(Interp.slope);
                    layerOffset = -.01f;
                }});
            }};
        }};
        thrash = new AquaTemplates.AquaItemTurretTemplate("thrash") {{
            requirements(Category.turret, with(polymer, 400, metaglass, 450, lead, 900));
            size = 4;
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
            velocityRnd = 0.1f;
            accurateDelay = true;;
            shootSound = shootArtillerySmall;
            scaleLifetimeOffset = 2;
            ammo(
                    coal, thrashCoal,
                    magnesiumPowder, thrashMagnesium,
                    brimstone, thrashBrimstone,
                    sporePod, thrashSporePod);
            drawer = new DrawTurret() {{
                parts.addAll(new RegionPart("-bump"){{
                    moveY = 8;
                    progress = PartProgress.recoil.curve(pow2In);
                }});
                for(int i = 0; i < 4; i++){
                    int finalI = i;
                    parts.add(new RegionPart("-springe"){{
                        y = -7/4f-(finalI *(9/4f));
                        growY = -0.9f;
                        moveY = 4 + finalI *0.5f;
                        mirror = true;
                        growProgress = PartProgress.recoil.curve(Interp.pow2In);
                        progress = PartProgress.recoil.curve(Interp.pow2In);
                    }});
                }
                parts.addAll(new RegionPart("-eject") {{
                    moveY = -5f;
                    progress = PartProgress.recoil.curve(pow5Out);
                    moves.add(new PartMove(PartProgress.smoothReload.curve(pow2Out), 0, -7, 0));
                }}, new RegionPart("-mid") {{
                    moveY = -7f;
                    progress = PartProgress.smoothReload.curve(pow2Out);
                }}, new RegionPart("-barrel") {{
                    moveY = -10f;
                    growY = 0.4f;
                    growProgress = PartProgress.smoothReload.curve(pow2Out);
                    progress = PartProgress.smoothReload.curve(pow2Out);
                }});
            }};
            limitRange(1.1f);
        }};
        flagellate = new AquaTemplates.AquaItemTurretTemplate("flagellate") {{
            requirements(Category.turret, with(steel, 2500, cupronickel, 1200, ferrosilicon, 2000, metaglass, 4000, nickel, 8000));
            size = 7;
            rotateSpeed = 0.1f;
            recoil = 0;
            reload = 540;
            shake = 12;
            targetAir = false;
            ammoUseEffect = AquaFx.casing1;
            ammoEjectBack = 22;
            shoot = new ShootAlternate(92 / 4f);
            shootY = 216 / 4f;
            shootSound = shootArtillery;
            recoils = 2;
            range = 1025;
            maxAmmo = 240;
            ammoPerShot = 60;
            consumeAmmoOnce = false;
            ammo(
                    steel, flagellateSteel);

            limitRange(1.1f);
            drawer = new DrawTurret() {{
                turretLayer = Layer.legUnit + 10;
                parts.addAll(new RegionPart("-end") {{
                    progress = PartProgress.recoil.curve(pow4In);
                    y = 0f;
                    heatProgress = PartProgress.recoil.curve(pow5Out);
                    moveY = -80 / 4f;
                    recoilIndex = 0;
                    turretHeatLayer = Layer.legUnit + 11;
                    moves.add(new PartMove(PartProgress.reload.curve(Interp.pow2In).inv(), 0, 100 / 4f, 0));
                }}, new RegionPart("-end") {{
                    progress = PartProgress.recoil.curve(pow4In);
                    y = 0f;
                    moveY = -80 / 4f;
                    x = 91 / 4f;
                    turretHeatLayer = Layer.legUnit + 11;
                    heatProgress = PartProgress.recoil.curve(pow5Out);
                    recoilIndex = 1;
                    moves.add(new PartMove(PartProgress.reload.curve(Interp.pow2In).inv(), 0, 100 / 4f, 0));
                }}, new RegionPart("-barrel") {{
                    progress = PartProgress.recoil.curve(pow4In);
                    y = -12f;
                    moveY = -8;
                    recoilIndex = 0;
                    moves.add(new PartMove(PartProgress.reload.curve(Interp.pow2In).inv(), 0, 12, 0));
                }}, new RegionPart("-barrel") {{
                    progress = PartProgress.recoil.curve(pow4In);
                    y = -12f;
                    moveY = -8;
                    x = 91 / 4f;
                    recoilIndex = 1;
                    moves.add(new PartMove(PartProgress.reload.curve(Interp.pow2In).inv(), 0, 12, 0));
                }}, new RegionPart("-case") {{
                    moveY = -8f;
                    progress = PartProgress.recoil.curve(Interp.pow2In);
                }});
            }};
        }};
        dislocate = new AquaTemplates.AquaItemTurretTemplate("dislocate") {{
                requirements(Category.turret, with(ferrosilicon, 700, metaglass, 900, graphite, 250));
                shootCone = 45;
                size = 5;
                range = 300;
                inaccuracy = 12;
                reload = 6;
                recoil = 0.2f;
                xRand = 1;
                rotateSpeed = 7;
                itemCapacity = 30;
                ammoUseEffect = Fx.casing2;
                shootSound = shootScepter;
                warmupMaintainTime = 90;
                shootWarmupSpeed = 0.01f;
                minWarmup = 0.01f;
            velocityRnd = 0.2f;
                ammo(
                        AquaItems.ferricMatter, dislocateFerricMatter,
                        ferrosilicon, dislocateFerrosilicon,
                        AquaItems.steel, dislocateSteel
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
                            new RegionPart("-barrel1") {{
                                growProgress = PartProgress.smoothReload.curve(circleIn).mul(-1);
                                progress = PartProgress.smoothReload.curve(circleIn);
                                growX = -0.1f;
                                xScl = 0.9f;
                                mixColor = Pal.darkestestGray.a(0.2f);
                                mixColorTo = Pal.darkestestGray.a(0.4f);
                                moveX = -22 / 4f;
                                moveY = 3 / 4f;
                                y = 12 - 6 / 4f;
                                x = 44 / 4f;
                            }},
                            //mid right
                            new RegionPart("-barrel1") {{
                                growProgress = PartProgress.smoothReload.curve(circleIn).mul(-1);
                                progress = PartProgress.smoothReload.curve(circleIn);
                                growX = -0.1f;
                                moveX = -22 / 4f;
                                mixColor = Pal.darkestestGray.a(0.0f);
                                mixColorTo = Pal.darkestestGray.a(0.1f);
                                moveY = 3 / 4f;
                                y = 12 - 3 / 4f;
                                x = 22 / 4f;
                            }},
                            //abs left
                            new RegionPart("-barrel1") {{
                                growProgress = PartProgress.smoothReload.curve(circleIn);
                                progress = PartProgress.smoothReload.curve(circleIn);
                                growX = -0.1f;
                                mixColor = Pal.darkestestGray.a(0.4f);
                                mixColorTo = Pal.darkestestGray.a(0.2f);
                                xScl = 0.9f;
                                moveX = 22 / 4f;
                                moveY = 3 / 4f;
                                y = 12 - 6 / 4f;
                                x = -44 / 4f;
                            }},
                            //mid left
                            new RegionPart("-barrel1") {{
                                growProgress = PartProgress.smoothReload.curve(circleIn);
                                progress = PartProgress.smoothReload.curve(circleIn);
                                mixColor = Pal.darkestestGray.a(0.2f);
                                mixColorTo = Pal.darkestestGray.a(0.1f);
                                growX = -0.1f;
                                moveX = -22 / 4f;
                                moveY = -3 / 4f;
                                y = 12 - 3 / 4f;
                                x = -22 / 4f;
                            }},
                            //Abs middle
                            new RegionPart("-barrel1") {{
                                progress = PartProgress.smoothReload.curve(circleIn);
                                mixColor = Pal.darkestestGray.a(0.1f);
                                mixColorTo = Pal.darkestestGray.a(0);
                                moveX = -22 / 4f;
                                moveY = -3 / 4f;
                                y = 12f;
                                x = 0;
                            }},
                            new RegionPart("-plunger") {{
                                progress = DrawPart.PartProgress.recoil;
                                recoilIndex = 0;
                                moveY = -5f;
                                y = 0.25f;
                            }},
                            new RegionPart("-plunger") {{
                                progress = PartProgress.recoil;
                                recoilIndex = 1;
                                x = 55 / 4f;
                                moveY = -5f;
                                y = 0.25f;
                            }},
                            new RegionPart("-mid") {{
                                progress = PartProgress.recoil;
                                moveY = -1f;
                                heatProgress = warmup;
                            }},
                            new RegionPart("-back") {{
                                progress = PartProgress.recoil;
                                moveY = 1f;
                            }},
                            new RegionPart("-side") {{
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
            }
        };
        refraction = new ContinuousTurret("refraction") {{
            requirements(Category.turret, with(metaglass, 90, copper, 120, lead, 100, graphite, 120));
            size = 2;
            shootType = new PointLaserBulletType() {{
                damage = 240 / 6f;
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
            loopSound = beamLustre;
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
            consumePower(2);
        }};
        douse = new LiquidTurret("douse") {{
            requirements(Category.turret, with(metaglass, 90, nickel, 60, copper, 20, graphite, 25));
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
                    water, douseWater,
                    AquaLiquids.clearwater, douseClearWater,
                    Liquids.slag, douseSlag,
                    Liquids.cryofluid, douseCryofluid,
                    Liquids.oil, douseOil,
                    petroleum, dousePetrol,
                    AquaLiquids.magma, douseMagma,
                    haze, douseHaze
            );
        }};
        Foment = new AquaTemplates.AquaItemTurretTemplate("foment") {{
            health = 925;
            ammo(
                    lead, AquaBullets.fomentLead,
                    AquaItems.ferricMatter, AquaBullets.fomentFerric,
                    ferrosilicon, AquaBullets.fomentFerrosilicon, brass, AquaBullets.fomentBrass);
            requirements(Category.turret, with(lead, 90, AquaItems.zinc, 60f, silicon, 90));
            size = 3;
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
            shoot.shotDelay = 5;
            recoils = 2;
            shootWarmupSpeed = 0.001f;
            warmupMaintainTime = 360;
            heatColor = Pal.redLight;
            consumeCoolant(40 / 60f);
            coolantMultiplier = 1f;
            shoot = new ShootAlternate(4);
            drawer = new DrawTurret() {{
                for (int i = 0; i < 2; i++) {
                    int f = i;
                    parts.add(new RegionPart("-barrel-" + (i == 0 ? "l" : "r")) {{
                        progress = PartProgress.recoil.curve(pow2In);
                        recoilIndex = f;
                        under = true;
                        heatProgress = warmup.curve(pow5Out);
                        moveY = -2f;
                    }});
                }
            }};
        }};
        grace = new AquaTemplates.AquaItemTurretTemplate("grace") {{
            requirements(Category.turret, with(cupronickel, 150, metaglass, 100, lead, 260, graphite, 200f));
            size = 3;
            shootSound = AquaSounds.shootGrace;
            consumeLiquid(water, 12);
            coolantMultiplier = 0.3f;
            reload = 25;
            ammoUseEffect = Fx.casing2;
            liquidCapacity = 1200;
            consumePower(5);
            range = 300;
            inaccuracy = 3;
            targetAir = false;
            targetGround = true;
            maxAmmo = 24;
            ammoPerShot = 3;
            ammo(
                    silicon, graceSilicon,
                    magnesiumPowder, graceMagnesium,
                    copper, graceCopper
            );
            limitRange(1.1f);
        }};
        confront = new AquaTemplates.AquaItemTurretTemplate("confront") {{
            health = 1125;
            requirements(Category.turret, with(brass, 250, graphite, 150, AquaItems.ferricMatter, 300));

            size = 4;
            range = 300;
            limitRange(1f);
            reload = 10;
            consumeAmmoOnce = true;
            maxAmmo = 30;
            inaccuracy = 2;
            ammoPerShot = 1;
            recoils = 4;
            shootWarmupSpeed = 0.05f;
            minWarmup = 0.65f;
            warmupMaintainTime = 360;
            heatColor = Pal.redLight;
            shootY = 0;
            coolantMultiplier = 0.2f;
            ammo(
                    lead, confrontLead,
                    AquaItems.ferricMatter, confrontFerricMatter,
                    brass, confrontBrass);
            shoot = new ShootBarrel() {{
                barrels = new float[]{
                        8, 8, 0,
                        -8, 8f, 0,
                        3, 18f, 0,
                        -3, 18f, 0
                };
            }};
            drawer = new DrawTurret() {{
                parts.add(new RegionPart("-barrel1-r") {{
                    recoilIndex = 0;
                    progress = PartProgress.recoil;
                    moveY = -3;
                    layerOffset = -.002f;
                    moves.add(new PartMove(PartProgress.warmup, 5, -4, 0));
                }}, new RegionPart("-barrel1-l") {{
                    recoilIndex = 1;
                    layerOffset = -.002f;
                    progress = PartProgress.recoil;
                    moveY = -3;
                    xScl = -1;
                    moves.add(new PartMove(PartProgress.warmup, -5, -4, 0));
                }}, new RegionPart("-barrel-r") {{
                    layerOffset = -.001f;
                    moveY = -3f;
                    recoilIndex = 2;
                    progress = PartProgress.recoil;
                }}, new RegionPart("-barrel-l") {{
                    layerOffset = -.001f;
                    recoilIndex = 3;
                    moveY = -3f;
                    xScl = -1;
                    progress = PartProgress.recoil;
                }});
            }};
            consumeCoolant(120 / 60f);
        }};
        redact = new AquaTemplates.AquaItemTurretTemplate("redact") {{
            health = 825;
            coolantMultiplier = 0.5f;
            requirements(Category.turret, with(AquaItems.ferricMatter, 90, silicon, 120, AquaItems.aluminum, 150));
            ammo(
                    AquaItems.cuprite, redactCuprite,
                    AquaItems.zinc, redactZinc,
                    AquaItems.aluminum, redactAluminum,
                    AquaItems.steel, redactSteel);
            size = 3;
            range = 350;
            limitRange(1.2f);
            reload = 45;
            consumeAmmoOnce = true;
            maxAmmo = 45;
            inaccuracy = 0;
            ammoPerShot = 3;
            shoot.shotDelay = 5;
            recoils = 2;
            shootWarmupSpeed = 0.01f;
            minWarmup = 0.65f;
            warmupMaintainTime = 360;
            heatColor = Pal.redLight;
            drawer = new DrawTurret() {{
                parts.add(new RegionPart("-side") {{
                    moveY = -1;
                    moveX = -0.5f;
                    progress = warmup;
                    mirror = true;
                }}, new RegionPart("-back") {{
                    moveY = -.5f;
                    moveX = 0.75f;
                    progress = warmup;
                    mirror = true;
                    moves.add(new PartMove(PartProgress.recoil, 0.5f, -0.5f, 0f));
                }});
            }};
            consumeCoolant(120 / 60f);
        }};
        focus = new AquaTemplates.AquaItemTurretTemplate("focus") {{
            requirements(Category.turret, with(AquaItems.aluminum, 500, lead, 600, metaglass, 500, AquaItems.ferricMatter, 250));
            size = 4;
            shootY = 85 / 4f;
            itemCapacity = 60;
            liquidCapacity = 300;
            health = 700;
            chargeSound = chargeLancer;
            shootSound = shootMalign;
            consumePower(8);
            consumeLiquid(nitrogen, 2f);
            shoot.firstShotDelay = 60;
            range = 400;
            reload = 5 * 60f;
            shootWarmupSpeed = 0.01f;
            rotateSpeed = 0.4f;
            minWarmup = 0.95f;
            warmupMaintainTime = 250;
            ammoPerShot = 20;
            recoilTime = 90;
            ammo(
                    AquaItems.towanite, focusTowanite,
                    AquaItems.acuminite, focusAcuminite,
                    AquaItems.azurite, focusAzurite
            );
            heatColor = Color.valueOf("d3f5ff");
            coolantMultiplier = 0.45f;
            consumeCoolant(80 / 60f);
            drawer = new DrawTurret() {{
                turretLayer = Layer.turret;
                parts.addAll(
                        new RegionPart("-barrel") {{
                            moveY = 24 / 4f;
                            progress = warmup.curve(circleIn);
                            heatProgress = charge;
                            layer = Layer.turret - 0.001f;
                            heatLayer = Layer.turret - 0.0005f;
                            moves.add(new PartMove(PartProgress.recoil.curve(pow2Out), 0, -10, 0));
                        }},
                        new RegionPart("-barrel1") {{
                            moveY = 70 / 4f;
                            progress = warmup.curve(circleIn);
                            heatProgress = charge;
                            layer = Layer.turret - 0.001f;
                            heatLayer = Layer.turret - 0.0005f;
                            moves.add(new PartMove(PartProgress.recoil.curve(pow2Out), 0, -10, 0));
                        }}
                );
            }};
        }};
        maelstrom = new LiquidTurret("maelstrom") {{
            requirements(Category.turret, with(AquaItems.brass, 250, copper, 100));
            shoot.firstShotDelay = 20f;
            shoot.shotDelay = 2;
            shoot.shots = 5;
            ammoPerShot = 2;
            inaccuracy = 25;
            extinguish = false;
            liquidCapacity = 200;
            outlineColor = tantDarkestTone;
            loopSound = Sounds.loopBio;
            loopSoundVolume = 0.09f;
            range = 260;
            size = 3;
            squareSprite = false;
            shootSound = shootSap;
            targetGround = false;
            warmupMaintainTime = 50;
            shootWarmupSpeed = 0.07f;
            minWarmup = 0.85f;
            trackingRange = 320;
            loopSound = loopSpray;
            loopSoundVolume = 0.02f;
            ammo(
                    fumes, maelstromFumes,
                    AquaLiquids.argon, maelstromArgon,
                    AquaLiquids.fluorine, maelstromFluorine
            );

        }};
        torrefy = new LaserTurret("torrefy") {{
            requirements(Category.turret, with(copper, 3000, steel, 1500, ferrosilicon, 900, lead, 4000, metaglass, 2000));
            reload = 8 * 60f;
            minWarmup = 0.99f;
            shootWarmupSpeed = 0.01f;
            recoil = 0;
            outlineColor = tantDarkestTone;
            recoilTime = 10;
            range = 70 * 8f;
            size = 8;
            squareSprite = false;
            liquidCapacity = 250;
            ammoPerShot = 20;
            itemCapacity = 60;
            warmupMaintainTime = 20 * 60f;
            cooldownTime = 15 * 60f;
            shootY = 2f;
            rotateSpeed = 0.9f;
            firingMoveFract = 0.25f;
            shake = 3f;
            shootDuration = 12 * 60f;
            consumePower(2000 / 60f);
            loopSound = beamMeltdown;
            shootY = 138 / 4f;
            loopSoundVolume = 2f;
            consumeLiquids(LiquidStack.with(water, 5000 / 60f, haze, 1000 / 60f));
            shootType = new ContinuousLaserBulletType(145) {{
                length = 600f;
                hitEffect = Fx.hitMeltdown;
                hitColor = Pal.meltdownHit;
                status = StatusEffects.melting;
                drawSize = 420f;
                timescaleDamage = true;
                width = 8 * 2.5f;

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
            shootSound = shootLaser;
            baseExplosiveness = 5;
            drawer = new DrawTurret() {{
                for (int i = 0; i < 5; i++) {
                    int f = i;
                    parts.addAll(new RegionPart("-peen") {{
                        mirror = true;
                        progress = warmup.curve(linear).delay(f * 0.2f);
                        moveY = 30 / 4f * f;
                        moveX = 1;
                        moves.add(new PartMove(PartProgress.recoil.curve(pow2In).delay(0.2f * f), 2, 0, 0));
                    }});
                }
                for (int i = 0; i < 9; i++) {
                    int f = i;
                    parts.addAll(new RegionPart("-bar") {{
                        moveX = 1.5f;
                        progress = warmup.curve(pow5In).slope().delay(f * 0.1f);
                        moves.add(new PartMove(warmup.curve(pow2In), 0.5f, 3, 0));
                        moveY = 0;
                        x = 0;
                        y = f * (25 / 4f) - 25 / 4f;
                        mirror = true;
                    }}, new RegionPart("-bar1") {{
                        moves.addAll(new PartMove(warmup.curve(pow2In), 0.5f, 3, 0), new PartMove(warmup.curve(pow5In).slope().delay(f * 0.1f), 1.5f, 0, 0));
                        progress = warmup.delay(0.8f).add(-0.1f * f).add(p -> Mathf.sin(12f, 0.4f) * p.warmup);
                        color = Color.valueOf("e25353").a(0);
                        colorTo = Color.valueOf("e25353").a(0.6f);
                        moveY = 0;
                        x = 0;
                        y = f * (25 / 4f) - 25 / 4f;
                        mirror = true;
                    }});
                }
                parts.addAll(new RegionPart("-laser") {{
                    xScl = 0.01f;
                    growX = 2;
                    moveY = 3;
                    color = Color.valueOf("ffffff").a(0);
                    colorTo = Color.valueOf("ffffff").a(1f);
                    mixColor = Color.valueOf("e25353").a(0);
                    mixColorTo = Color.valueOf("ffffff").a(1f);
                    progress = warmup;
                    growProgress = PartProgress.recoil.delay(0.2f).add(-0.1f).add(p -> Mathf.sin(9f, 0.2f) * p.recoil);
                }}, new RegionPart("-barrel") {{
                    moveX = 2f;
                    progress = warmup.curve(pow2In);
                    moveY = 3;
                    mirror = true;
                }}, new RegionPart("-panel1") {{
                    mirror = true;
                    moveX = 3f;
                    moveY = -2;
                    progress = warmup.curve(pow2In).delay(0.6f);
                }}, new RegionPart("-tank") {{
                    mirror = true;
                    moveX = 5;
                    progress = warmup.curve(pow2In).delay(0.6f);
                }}, new RegionPart("-tank") {{
                    mirror = true;
                    moveX = 5;
                    y = 25f;
                    moveY = 20 / 4f;
                    progress = warmup.curve(pow2In).delay(0.6f);
                }}, new RegionPart("-panel") {{
                    mirror = true;
                    moveX = 9;
                    moveY = -1;
                    progress = warmup.curve(pow2In).delay(0.5f);
                }}, new RegionPart("-panel2") {{
                    mirror = true;
                    moveX = 6f;
                    moveY = -2;
                    moveRot = -5;
                    progress = warmup.curve(pow2In).delay(0.7f);
                }}, new RegionPart("-panel3") {{
                    mirror = true;
                    moveX = 4.5f;
                    moveY = 0;
                    moveRot = 2;
                    progress = warmup.curve(pow2Out).delay(0.8f);
                }}, new RegionPart("-panel4") {{
                    mirror = true;
                    moveX = 3f;
                    moveY = 5;
                    moveRot = 15;
                    progress = warmup.curve(pow2Out).delay(0.9f);
                }}, new EffectSpawnerPart() {{
                    x = y = 0;
                    height = 256 / 4f;
                    width = 60 / 4f;
                    effect = Fx.coalSmeltsmoke;
                }}, new EffectSpawnerPart() {{
                    x = y = 0;
                    height = 200 / 4f;
                    width = 45 / 4f;
                    effectRandRot = 5;
                    effectRot = -180;

                    effectColor = Color.valueOf("e9f984");
                    effect = Fx.colorSpark;
                    progress = warmup.delay(0.75f);
                }}, new EffectSpawnerPart() {{
                    x = 90 / 4f;
                    y = -50 / 4f;
                    rotation = 45;
                    height = 70 / 4f;
                    width = 45 / 4f;
                    effectRandRot = 5;
                    effectRot = -180;
                    mirror = true;
                    effectColor = Color.valueOf("e9f984");
                    effect = Fx.colorSpark;
                    progress = warmup.delay(0.8f);
                }}, new EffectSpawnerPart() {{
                    x = 90 / 4f;
                    y = -60 / 4f;
                    rotation = 45;
                    height = 40 / 4f;
                    width = 40 / 4f;
                    effectRandRot = 5;
                    effectRot = -180;
                    mirror = true;
                    effectColor = Color.valueOf("e9f984");
                    effect = new Effect(90f, 200f, b -> {
                        float intensity = 1.3f;

                        color(b.color, 0.7f);
                        for (int i = 0; i < 3; i++) {
                            rand.setSeed(b.id * 2L + i);
                            float lenScl = rand.random(0.5f, 1f);
                            int fi = i;
                            b.scaled(b.lifetime * lenScl, e -> {
                                randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int) (2.9f * intensity), 13f * intensity, (x, y, in, out) -> {
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
                for (int i = 0; i < 8; i++) {
                    int f = i;
                    parts.addAll(new RegionPart("-barrel-heat") {{
                        mirror = true;
                        outline = false;
                        progress = warmup.delay(0.8f).add(-0.1f * f).add(p -> Mathf.sin(9f, 0.2f) * p.warmup);
                        y = 0 + (f * 18 / 4f);
                        color = Color.valueOf("e25353").a(0);
                        colorTo = Color.valueOf("e25353").a(0.6f);
                        moves.add(new PartMove(warmup.curve(pow2In), 2, 3, 0));
                    }});
                }
            }};
        }};
        javelin = new ItemTurret("javelin") {{
            requirements(Category.turret, with(polymer, 250, metaglass, 750, AquaItems.ferricMatter, 500, silicon, 400));
            shownPlanets.addAll(fakeSerpulo,fakeErekir,tantros2);
            size = 3;
            shoot.shots = 4;
            shoot.shotDelay = 5f;
            shootCone = 10f;
            rotateSpeed = 15f;
            reload = 30f;
            inaccuracy = 5f;
            consumeAmmoOnce = true;
            range = 300f;
            ammoPerShot = 2;
            itemCapacity = 30;
            liquidCapacity = 150;
            targetAir = true;
            targetGround = false;

            ammo(
                    copper, javelinTest
            );
        }};
        nostalgia = new PowerTurret("nostalgia"){{
            requirements(Category.turret, with(silicon, 300));
            size = 2;
            category = Category.turret;
            buildVisibility = BuildVisibility.sandboxOnly;
            range = 190;
            consumePower(5);
            consumeCoolant(0.2f);

            reload = 20;
            ammoUseEffect = Fx.casing1;
            inaccuracy = 7;
            outlineColor = Color.valueOf("2d2e37");
            shootSound = shootDuo;
            shootType = new BasicBulletType(5, 25, "missile"){{
                trailLength = 14;
                width = 6;
                height = 8;
                frontColor = Color.white;
                backColor = trailColor = hitColor = lightColor = Pal.berylShot;
                shootEffect = Fx.shootSmallColor;
                smokeEffect = Fx.shootSmallSmoke;
                homingDelay = 5;
                homingPower = 0.03f;
                homingRange = 20;
                trailInterval = 5;
                trailEffect = Fx.artilleryTrail;
            }};
        }};
        memorial = new PowerTurret("memorial"){{
            requirements(Category.turret, with(silicon, 2000, metaglass, 150, graphite, 250));
            size = 3;
            category = Category.turret;
            buildVisibility = BuildVisibility.sandboxOnly;
            range = 300;
            consumePower(12);
            consumeCoolant(0.4f);

            reload = 90;
            ammoUseEffect = Fx.casing1;
            inaccuracy = 11;
            outlineColor = Color.valueOf("2d2e37");
            shootSound = shootDuo;
            shoot.shots = 3;
            shoot.shotDelay = 10;

            shootType = new BasicBulletType(3, 55, "missile"){{
                trailLength = 10;
                width = 8;
                height = 14;
                frontColor = Color.white;
                backColor = trailColor = hitColor = lightColor = Pal.berylShot;
                shootEffect = Fx.shootSmallColor;
                smokeEffect = Fx.shootSmallSmoke;
                homingDelay = 40;
                homingPower = 0.01f;
                homingRange = 45;
                trailInterval = 5;
                lifetime = 190;
                trailEffect = Fx.artilleryTrailSmoke;
            }};
        }};
        overwrite(Blocks.ripple, (ItemTurret r) -> {
                    r.requirements = null;
                    r.requirements(Category.turret, ItemStack.with(Items.copper, 150, Items.graphite, 135, chalkalloy, 60));
                }
        );
        overwrite(Blocks.fuse, (ItemTurret r) -> {
            r.ammoTypes = null;
            float brange = r.range + 10f;
            r.ammo(
                    chalkalloy, new ShrapnelBulletType(){{
                        length = brange;
                        damage = 66f;
                        ammoMultiplier = 4f;
                        width = 17f;
                        reloadMultiplier = 1.3f;
                    }},
                    steel, new ShrapnelBulletType(){{
                        length = brange;
                        damage = 105f;
                        ammoMultiplier = 5f;
                        toColor = Color.gray;
                        shootEffect = smokeEffect = Fx.thoriumShoot;
                    }}
            );
        });
        ensign = new ItemTurret("ensign") {{
            shownPlanets.addAll(Planets.serpulo, fakeSerpulo);
            requirements(Category.turret, with(lead, 45, silicon, 60, chalkalloy, 20));
            size = 2;
            reload = 70;
            recoil = 0.5f;
            consumeCoolant(24 / 60f);
            recoilTime = 40;
            shootCone = 2;
            shootSound = shootMerui;
            rotateSpeed = 1.4f;
            range = 150;
            cooldownTime = 80;
            final Color[] col = {Color.valueOf("f9350f")};
            heatColor = col[0];
            ammo(
                    lead, ensignLead,
                    nickel, ensignNickel,
                    chalkalloy, ensignChalkalloy
            );

        }};
        hack = new ItemTurret("hack") {{
            shownPlanets.addAll(Planets.serpulo, fakeSerpulo);
            ammo(
                    chalkalloy, hackChalkalloy,
                    nickel, hackNickel,
                    surgeAlloy, hackSurgeAlloy);
            requirements(Category.turret, with(silicon, 90, metaglass, 40, chalkalloy, 65));
            size = 2;
            ammoPerShot = 3;
            consumeCoolant(24 / 60f);
            reload = 35;
            range = 170;
            limitRange(1.1f);
            recoil = 0.75f;
            shootCone = 30;
            inaccuracy = 8;
            shoot = new ShootMulti(new ShootAlternate(6), new ShootSpread(6, 15), new ShootSine() {{
                scl = 4f;
                mag = 3f;
            }});
        }};
        blaze = new PowerTurret("blaze") {{
            shownPlanets.addAll(Planets.serpulo, fakeSerpulo);
            requirements(Category.turret, with(graphite, 35, lead, 40));
            consumePower(128 / 60f);
            size = 2;
            consumeCoolant(24 / 60f);
            consumeCoolant(24 / 60f);
            shootSound = shootAlpha;
            reload = 90;
            range = 120;
            recoil = 0.75f;
            shootCone = 30;
            inaccuracy = 9;
            minWarmup = 0.8f;
            shoot.shots = 2;
            shoot.shotDelay = 8;
            shootType = new MissileBulletType(3.5f, 15, "large-orb") {{
                width = 8;
                height = 8;
                shrinkX = 0;
                lifetime = 30;
                shrinkY = 0;
                status = AquaStatuses.ionized;
                lightning = 3;
                statusDuration = 160;
                lightningLength = 4;
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
        clobber = new ItemTurret("clobber"){{
            shownPlanets.addAll(Planets.serpulo, fakeSerpulo);
            requirements(Category.turret, with(copper, 120, lead, 90, graphite, 60));
            size = 2;
            ammoPerShot = 2;
            reload = 45;
            maxAmmo = 16;
            shootSound = shootScepter;
            inaccuracy = 2;
            range = 8*22;
            shake = 0.5f;
            scaledHealth = 200;
            shootCone = 3f;
            coolant = consumeCoolant(0.2f);
            limitRange(1.2f);
            ammoUseEffect = Fx.casing2;
            ammo(
                    lead, clobberLead,
                    chalkalloy, clobberChalkalloy
            );
            drawer = new DrawTurret(){{
                parts.addAll(new RegionPart("-barrel"){{
                    mirror = false;
                    moveY = -4;
                    progress = PartProgress.reload;
                    layerOffset = -.00001f;
                    heatLayer = Layer.turret - 0.0001f;
                    heatProgress = PartProgress.heat;
                }});
            }};
        }};
        mayhem = new LiquidTurret("mayhem"){{
            shownPlanets.addAll(Planets.serpulo, fakeSerpulo);
            requirements(Category.turret, with(copper, 90, metaglass, 60, silicon, 45));
            loopSound = Sounds.loopFire;
            size = 2;
            recoilTime = 90;
            shootY = 2;
            scaledHealth = 200;
            shootSound = Sounds.none;
            range = 8*19;
            maxAmmo = 90;
            inaccuracy = 6;
            shoot.shots = 2;
            liquidCapacity = 90;
            ammoPerShot = 2;
            reload = 4;
            limitRange(new BasicBulletType(1.2f, 10){{
                sprite = "circle";
                puddleLiquid = oil;
                velocityRnd = 0.03f;
                despawnHit = true;
                makeFire = true;
                puddles = 3;
                puddleRange = 12;
                puddleAmount = 10;
                width = height = 5;
                lifetime = 90;
                status = burning;
                frontColor = backColor = trailColor = lightColor = hitColor = Pal.lighterOrange;
                trailInterval = 2;
                trailLength = 9;
                hitEffect = Fx.fireHit;
                shootEffect = Fx.fireSmoke;
                trailEffect = new MultiEffect(Fx.ballfire, Fx.fireballsmoke);
            }}, 1.1f);
            ammo(
                    oil, mayhemOil
            );
            drawer = new DrawTurret(){{
                for(int i = 0; i < 2; i ++){
                    int finalI = i;
                    parts.add(new RegionPart("-barrel-" + (finalI == 0 ? "l" : "r")){{
                        progress = warmup;
                        under = true;
                        xScl = (finalI == 0) ? -1 : 1;
                        moveY = -1f;
                        moveX = (finalI == 1) ? -1 : 1;
                        moveRot = (finalI == 1) ? 7 : -7;
                    }});
                }
            }};
        }};
        illustrate = new ItemTurret("illustrate"){{
            shownPlanets.addAll(Planets.serpulo, fakeSerpulo);
            requirements(Category.turret, with(silicon, 120, chalkalloy, 110, graphite, 80, lead, 100));
            reload = 15;
            shoot = new ShootAlternate(6);
            recoils = 2;
            recoil = 2;
            recoilTime = 12;
            targetGround = false;
            ammoUseEffect = Fx.casing1;
            range = 8*35f;
            limitRange(1.1f);
            size = 3;
            scaledHealth = 250;
            consumeCoolant(0.3f);
            inaccuracy = 12;
            shootSound = shootSalvo;
            soundPitchMax = 0.6f;
            soundPitchMin = 0.4f;
            velocityRnd = 0.1f;
            ammo(
                    metaglass, illustrateMetaglass,
                    AquaItems.ferricMatter, illustrateFerricMatter,
                    plastanium, illustratePlastanium
            );
            drawer = new DrawTurret(){{
                for(int i = 0; i < 2; i ++){
                    int finalI = i;
                    parts.add(new RegionPart("-barrel-" + (finalI == 0 ? "l" : "r")){{
                        progress = PartProgress.recoil;
                        under = true;
                        recoilIndex = finalI;
                        xScl = (finalI == 0) ? -1 : 1;
                        moveY = -5f;
                    }});
                }
            }};
        }};
        finite = new PointDefenseTurret("finite"){{
            requirements(Category.turret, with(Items.silicon, 130, Items.thorium, 80, Items.phaseFabric, 40, Items.titanium, 40));
            shownPlanets.add(Planets.serpulo);
            scaledHealth = 125;
            range = 90f;
            hasPower = true;
            consumePower(5f);
            size = 1;
            shootLength = 7f;
            bulletDamage = 10f;
            reload = 12f;
            envEnabled |= Env.space;
        }};

    }
}
