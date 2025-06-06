package aquarion.units;

import aquarion.AquaStatuses;
import aquarion.type.*;
import aquarion.world.graphics.AquaFx;
import aquarion.world.units.newTankUnitType;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.geom.Rect;
import arc.struct.Seq;
import mindustry.ai.UnitCommand;
import mindustry.ai.types.BuilderAI;
import mindustry.ai.types.CargoAI;
import mindustry.ai.types.MinerAI;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.entities.abilities.MoveEffectAbility;
import mindustry.entities.abilities.ShieldRegenFieldAbility;
import mindustry.entities.abilities.SpawnDeathAbility;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootSine;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import aquarion.world.graphics.AquaPal;
import mindustry.type.Weapon;
import mindustry.type.unit.TankUnitType;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.electrum;
import static aquarion.units.AquaWrecks.*;
import static mindustry.Vars.tilePayload;

public class AquaUnitTypes {
    //core units and transport

    public static UnitType
        //Sharded units
            bulwark, pugnate, rampart, crest, reave, soar, raze, shatter, castellan, retaliate,
            //qeralter Units
        weep,
            //core/mining
            mite, iris,
        //strut tree
        strut, truss, joist, buttress, stanchion,

            //Qeralter Units End Region
    //messenger tree
  ambassador, consul, legate, monarch,

     //steward tree
 curator, custodian, caretaker, warden;

    //gerbUnits
    public static GerbUnitType gerbTest;
    //legUnits mechanical
    public static  MechanicalUnitType reap;
    // mechs
    public static MechanicalUnitType messenger, steward;
    public static  MechanicalUnitType zoarcid, anguilli, cyprin, pycogen, batoid, goss, heed, effect, consummate, efectuate,
    cull, glean;
    public static  UnitType rivulet;
    public static void loadContent() {
        messenger = new MechanicalUnitType("messenger") {{
            speed = 0.65f;
            health = 480;
            armor = 2;
            rotateSpeed = 1.25f;
            rotateMoveFirst = true;
            mech = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            constructor = MechUnit::create;
            mechLegColor = AquaPal.tantDarkestTone;
            abilities.add(new ShieldRegenFieldAbility(25f, 250f, 60f * 2.5f, 110f),

                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 3;
                        sizeFrom = 8;
                        sizeTo = 0;
                        lenFrom = 0;
                        lenTo = 6;
                        line = true;
                        length = 15;
                        baseLength = 2;
                        layer = 90;
                        lifetime = 10;
                        colorFrom = Color.valueOf("ffea97");
                        colorTo = Color.valueOf("ffea9710");
                    }}, 90f, .6f) {{
                    }},
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 3;
                        sizeFrom = 0;
                        sizeTo = 4;
                        lifetime = 80;
                        layer = 80;
                        colorFrom = Color.valueOf("262323");
                        colorTo = Color.valueOf("746f6f10");
                    }}, 15f, .4f) {{
                    }}, new SpawnDeathAbility(messengerWreck, 0, 1) {{
                        randAmount = 1;
                    }});
            weapons.add(new Weapon("aquarion-messenger-weapon") {{
                mirror = true;
                alternate = true;
                recoil = 2f;
                x = 0;
                outlineColor = AquaPal.tantDarkerTone;
                recoilTime = 15f;
                reload = 40;
                layerOffset = -0.001f;
                shootX = 4;
                range = 125;
                bullet = new BasicBulletType(2f, 5) {{
                    knockback = -.5f;
                    splashDamage = 45;
                    splashDamageRadius = 8 * 3.2f;
                    splashDamagePierce = false;
                    width = height = 9f;
                    shrinkX = 0.6f;
                    shake = 1;
                    shrinkY = 0.1f;
                    shootEffect = Fx.shootSmall;
                    shootSound = Sounds.laser;
                    smokeEffect = Fx.shootBigSmoke;
                    trailLength = 5;
                    trailWidth = 2;
                    lifetime = 45f;
                    maxRange = 80;
                    trailEffect = Fx.none;
                    collidesTiles = true;
                    collidesGround = true;
                    frontColor = hitColor = lightColor = Color.white;
                    backColor = trailColor = Pal.techBlue;
                }};
            }});
        }};
        ambassador = new MechanicalUnitType("ambassador") {{
            constructor = LegsUnit::create;
            health = 1150;
            armor = 10;
            legLength = 16;
            legPairOffset = 2;
            abilities.add(new ShieldRegenFieldAbility(25f, 75f, 60f * 5, 90f),

                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 3;
                        sizeFrom = 8;
                        sizeTo = 0;
                        lenFrom = 0;
                        lenTo = 6;
                        line = true;
                        length = 15;
                        baseLength = 2;
                        layer = 90;
                        lifetime = 10;
                        colorFrom = Color.valueOf("ffea97");
                        colorTo = Color.valueOf("ffea9710");
                    }}, 90f, .6f) {{
                    }},
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 3;
                        sizeFrom = 0;
                        sizeTo = 4;
                        lifetime = 80;
                        layer = 80;
                        colorFrom = Color.valueOf("262323");
                        colorTo = Color.valueOf("746f6f10");
                    }}, 15f, .4f) {{
                    }}, new SpawnDeathAbility(ambassadorWreck, 0, 1) {{
                        randAmount = 1;
                    }});
            hitSize = 13;
            legCount = 4;
            speed = 1f;
            rotateSpeed = 1.7f;
            lockLegBase = true;
            legMaxLength = 1.2f;
            legMinLength = 0.1f;
            weapons.add(new Weapon("aquarion-ambassador-weapon") {{
                mirror = true;
                x = 8.75f;
                recoil = 0.5f;
                parts.addAll(new RegionPart("-blade") {{
                    moveRot = -10f;
                    moveX = 1;
                    progress = PartProgress.warmup;
                }}, new RegionPart("-panel") {{
                    moveX = 1.25f;
                    moveY = -1.25f;
                    progress = PartProgress.warmup;
                }});
                alternate = false;
                continuous = true;
                alwaysContinuous = true;
                shootStatus = StatusEffects.slow;
                shootStatusDuration = 10;
                shootSound = Sounds.torch;
                shootY = 4;
                shootCone = 25;
                top = false;
                rotate = false;
                bullet = new ContinuousFlameBulletType() {{
                    lightStroke = 35;
                    divisions = 40;
                    width = 3.8f;
                    drawFlare = false;
                    length = 110;
                    pierce = true;
                    pierceCap = 2;
                    pierceBuilding = true;
                    damage = 24;
                    damageInterval = 10;
                    colors = new Color[]{Color.valueOf("465ab850"), Color.valueOf("66a6d290"), Color.valueOf("98d5ff"), Color.valueOf("d1efff")};
                }};
            }});
        }};
        consul = new MechanicalUnitType("consul"){{
                constructor = LegsUnit::create;
                health = 3500;
                abilities.add(new ShieldRegenFieldAbility(25f, 75f, 60f * 5, 90f),

                        new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                            particles = 3;
                            sizeFrom = 8;
                            sizeTo = 0;
                            lenFrom = 0;
                            lenTo = 6;
                            line = true;
                            length = 15;
                            baseLength = 2;
                            layer = 90;
                            lifetime = 10;
                            colorFrom = Color.valueOf("ffea97");
                            colorTo = Color.valueOf("ffea9710");
                        }}, 90f, .6f) {{
                        }},
                        new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                            particles = 3;
                            sizeFrom = 0;
                            sizeTo = 4;
                            lifetime = 80;
                            layer = 80;
                            colorFrom = Color.valueOf("262323");
                            colorTo = Color.valueOf("746f6f10");
                        }}, 15f, .4f) {{
                        }}, new SpawnDeathAbility(consulWreck, 0, 1) {{
                            randAmount = 1;
                        }});
                armor = 20;
                shadowElevation = 0.3f;
                legLength = 16;
                legPairOffset = 2;
                legGroupSize = 2;
                legMoveSpace = 1.5f;
                drawCell = false;
                hitSize = 18;
                legCount = 4;
                speed = 0.65f;
                rotateSpeed = 1.4f;
                targetAir = false;
                lockLegBase = true;
                legMaxLength = 1.2f;
                legMinLength = 0.7f;
                weapons.add(new Weapon("aquarion-consul-weapon") {{
                    mirror = true;
                    x = 12f;
                    recoil = 1f;
                    top = false;
                    layerOffset = -0.00001f;
                    parts.addAll(
                            new RegionPart("-barrel") {{
                                layerOffset = -.00001f;
                                y = 11f;
                                x = 0f;
                                mixColor = Color.valueOf("909698");
                                mixColorTo = Color.valueOf("909698");
                                moveX = 3.25f;
                                moveY = 0;
                                progress = PartProgress.reload.curve(Interp.sineIn);
                            }},
                            new RegionPart("-barrel") {{
                                layerOffset = -.00001f;
                                y = 11f;
                                x = -3.25f;
                                mixColor = Color.valueOf("909698");
                                mixColorTo = Color.valueOf("909698").a(0);
                                moveX = 3.25f;
                                moveY = 0;
                                progress = PartProgress.reload.curve(Interp.sineIn);
                            }},
                            new RegionPart("-barrel") {{
                                layerOffset = -.00001f;
                                y = 11f;
                                x = 0f;
                                mixColor = Color.valueOf("909698").a(0);
                                mixColorTo = Color.valueOf("909698");
                                moveX = -3.25f;
                                moveY = 0;
                                progress = PartProgress.reload.curve(Interp.sineIn);
                            }});
                    reload = 15;
                    shootSound = Sounds.bolt;
                    shootY = 12;
                    shootCone = 25;
                    top = false;
                    rotate = true;
                    rotationLimit = 15;
                    flipSprite = true;
                    bullet = new RailBulletType() {{
                        pierce = true;
                        pierceCap = 3;
                        pierceBuilding = true;
                        damage = 95;
                        length = 110;
                        pointEffectSpace = 6;
                        pierceDamageFactor = 0.5f;
                        collidesAir = false;
                        shootEffect = Fx.shootBigColor;
                        hitEffect = Fx.hitSquaresColor;
                        smokeEffect = new MultiEffect(new ParticleEffect() {{
                            line = true;
                            cone = 60;
                            particles = 6;
                            lenFrom = 7;
                            lenTo = 0;
                            length = 15;
                            sizeFrom = 10;
                            sizeTo = 10;
                            rotWithParent = true;
                            baseRotation = 0;
                            lifetime = 15;
                            colorFrom = Color.valueOf("ffffff");
                            colorTo = Color.valueOf("ff5656");
                        }}, new ParticleEffect() {{
                            cone = 65;
                            particles = 5;
                            layer = 60;
                            sizeFrom = 6;
                            length = 30;
                            sizeTo = 0;
                            sizeInterp = Interp.linear;
                            interp = Interp.linear;
                            rotWithParent = true;
                            baseRotation = 0;
                            lifetime = 25;
                            colorFrom = Color.valueOf("716363");
                            colorTo = Color.valueOf("423a3a");
                        }});
                        endEffect = new ParticleEffect() {{
                            line = true;
                            cone = 60;
                            particles = 4;
                            lenFrom = 7;
                            lenTo = 0;
                            sizeFrom = 10;
                            sizeTo = 12;
                            rotWithParent = true;
                            baseRotation = 0;
                            lifetime = 35;
                            colorFrom = Color.valueOf("ff5656");
                            colorTo = Color.valueOf("c80f0f");
                        }};
                        pointEffect = new MultiEffect(new ParticleEffect() {{
                            line = true;
                            cone = 0;
                            particles = 1;
                            lenFrom = 10;
                            lifetime = 10;
                            lenTo = 0;
                            sizeFrom = 7;
                            sizeTo = 0;
                            colorFrom = Color.valueOf("ffa9a9");
                            colorTo = Color.valueOf("ffa9a9");
                            rotWithParent = true;
                            baseRotation = 0;
                        }}, new ParticleEffect() {{
                            line = true;
                            cone = 0;
                            particles = 1;
                            lenFrom = 10;
                            lifetime = 10;
                            lenTo = 0;
                            sizeFrom = 10;
                            sizeTo = 0;
                            colorFrom = Color.valueOf("ff5656");
                            colorTo = Color.valueOf("ff5656");
                            rotWithParent = true;
                            baseRotation = 0;
                        }});
                    }};
                }}, new Weapon("aquarion-consul-missiles") {{
                    rotate = true;
                    rotateSpeed = 1.1f;
                    reload = 90;
                    mirror = true;
                    x = 10;
                    y = -2f;
                    shoot.shots = 2;
                    bullet = new MissileBulletType() {{
                        weaveScale = 4;
                        weaveMag = 6;
                        collidesAir = false;
                        shootEffect = Fx.shootSmall;
                        smokeEffect = Fx.shootSmallSmoke;
                        ejectEffect = Fx.casing2;
                        despawnEffect = Fx.blastExplosion;
                        hitEffect = Fx.blastsmoke;
                        homingPower = 0;
                        shrinkX = 0.8f;
                        shrinkY = 0;
                        width = 12;
                        height = 12;
                        sprite = "missile-large";
                        trailLength = 12;
                        trailInterp = Interp.slope;
                        speed = 2.2f;
                        hitSize = 5;
                        splashDamageRadius = 3.1f * 8;
                        splashDamage = 80;
                        frontColor = hitColor = lightColor = Color.white;
                        backColor = trailColor = Pal.techBlue;
                    }};
                }});
            }};
        goss = new MechanicalUnitType("goss") {{
            constructor = UnitEntity::create;
            engineSize = 0f;
            health = 380f;
            armor = 2;
            groundLayer = 90;
            outlineColor = Color.valueOf("232826");
            speed = 2.5f;
            accel = 0.9f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            health = 270;
            hitSize = 7;
            abilities.add(
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 3;
                        sizeFrom = 8;
                        sizeTo = 0;
                        lenFrom = 0;
                        lenTo = 6;
                        line = true;
                        length = 15;
                        baseLength = 2;
                        layer = 90;
                        lifetime = 10;
                        colorFrom = Color.valueOf("ffea97");
                        colorTo = Color.valueOf("ffea9710");
                    }}, 90f, .6f) {{
                    }},
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 3;
                        sizeFrom = 0;
                        sizeTo = 4;
                        lifetime = 80;
                        layer = 80;
                        colorFrom = Color.valueOf("262323");
                        colorTo = Color.valueOf("746f6f10");
                    }}, 15f, .4f) {{
                    }}, new SpawnDeathAbility(gossWreck, 0, 1) {{
                        randAmount = 1;
                    }});
            flying = true;
            lowAltitude = true;
            //damn you intellij
            drag = 0.75f;

            weapons.add(new Weapon("aquarion-goss-weapon") {{
                shoot.shots = 2;
                shootSound = Sounds.blaster;
                reload = 45;
                layerOffset = -0.01f;
                x = 0;
                y = 0f;
                shootY = 5;
                recoil = 1;
                rotate = false;
                mirror = false;
                bullet = new BasicBulletType(1.5f, 30, "missile-large") {{
                    weaveScale = 6;
                    weaveMag = 4;
                    shrinkX = 0.1f;
                    shrinkY = 0.1f;
                    homingPower = 0.02f;
                    trailLength = 7;
                    hitColor = trailColor = backColor = lightColor = Color.valueOf("ffa665");
                    frontColor = Color.white;
                    keepVelocity = false;
                    drag = -0.02f;
                }};
            }});
        }};
        zoarcid = new MechanicalUnitType("zoarcid") {{
            engineSize = 0f;
            armor = 4;
            groundLayer = 90;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            hitSize = 8;
            rotateSpeed = 3;
            constructor = UnitEntity::create;
            lowAltitude = true;
            health = 320f;
            flying = true;
            outlineColor = Color.valueOf("232826");
            speed = 2.5f;
            accel = 0.9f;
            drag = 0.75f;
            abilities.add(
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 3;
                        sizeFrom = 8;
                        sizeTo = 0;
                        lenFrom = 0;
                        lenTo = 6;
                        line = true;
                        length = 15;
                        baseLength = 2;
                        layer = 90;
                        lifetime = 10;
                        colorFrom = Color.valueOf("ffea97");
                        colorTo = Color.valueOf("ffea9710");
                    }}, 90f, .6f) {{
                    }},
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 3;
                        sizeFrom = 0;
                        sizeTo = 4;
                        lifetime = 80;
                        layer = 80;
                        colorFrom = Color.valueOf("262323");
                        colorTo = Color.valueOf("746f6f10");
                    }}, 15f, .4f) {{
                    }}, new SpawnDeathAbility(zoarcidWreck, 0, 1) {{
                        randAmount = 1;
                    }},
                    new MoveEffectAbility(0, -3, Pal.sapBulletBack, AquaFx.t1TrailZoarcid, 1));
            weapons.add(new Weapon("aquarion-zoarcid-weapon") {{
                            shootSound = Sounds.missileSmall;
                            reload = 60;
                            layerOffset = -0.01f;
                            shootY = 5;
                            baseRotation = -5;
                            x = 0.5f;
                            y = 1.5f;
                            recoil = 1;
                            shootCone = 25;
                            rotate = false;
                            alternate = true;
                            mirror = true;
                            bullet = new BasicBulletType(1.5f, 40) {{
                                range = 60;
                                hitSize = 5;
                                width = 6;
                                height = 8;
                                frontColor = Color.white;
                                backColor = Pal.techBlue;
                                weaveMag = 0;
                                weaveScale = 0;
                                lifetime = 45;
                            }};
                        }},
                    new Weapon("aquarion-zoarcid-weapon") {{
                        shootSound = Sounds.missileSmall;
                        reload = 60;
                        layerOffset = -0.01f;
                        shootY = 5;
                        baseRotation = 5;
                        x = -2.5f;
                        flipSprite = true;
                        y = 0f;
                        recoil = 1;
                        shootCone = 25;
                        rotate = false;
                        alternate = true;
                        mirror = true;
                        bullet = new BasicBulletType(1.5f, 40) {{
                            range = 60;
                            hitSize = 5;
                            width = 6;
                            height = 8;
                            frontColor = Color.white;
                            backColor = Pal.techBlue;
                            weaveMag = 0;
                            weaveScale = 0;
                            lifetime = 45;
                        }};
                        rotateSpeed = 4.5f;
                    }});
        }};
        anguilli = new MechanicalUnitType("anguilli") {
            {
                abilities.add(
                        new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                            particles = 3;
                            sizeFrom = 8;
                            sizeTo = 0;
                            lenFrom = 0;
                            lenTo = 6;
                            line = true;
                            length = 15;
                            baseLength = 2;
                            layer = 90;
                            lifetime = 10;
                            colorFrom = Color.valueOf("ffea97");
                            colorTo = Color.valueOf("ffea9710");
                        }}, 90f, .6f) {{
                        }},
                        new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                            particles = 3;
                            sizeFrom = 0;
                            sizeTo = 4;
                            lifetime = 80;
                            layer = 80;
                            colorFrom = Color.valueOf("262323");
                            colorTo = Color.valueOf("746f6f10");
                        }}, 15f, .4f) {{
                        }}, new SpawnDeathAbility(anguilliWreck, 0, 1) {{
                            randAmount = 1;
                        }},
                        new MoveEffectAbility(6, -3, Pal.sapBulletBack, AquaFx.t1TrailZoarcid, 1));
                abilities.add(new MoveEffectAbility(-6, -5, Pal.sapBulletBack, AquaFx.t1TrailZoarcid, 1),
                        new MoveEffectAbility(0, -3, Pal.sapBulletBack, AquaFx.t2TrailAnguilli, 1));
                weapons.add(new Weapon("anguilli-weapon") {{
                    x = 2;
                    y = 0;
                    rotate = true;
                    shake = 1;
                    shoot.shots = 4;
                    reload = 110;
                    rotateSpeed = 180;
                    shootSound = Sounds.mineDeploy;
                    shoot.shotDelay = 6;
                    bullet = new BasicBulletType(2f, 45) {{
                        sprite = "mine-bullet";
                        ignoreRotation = true;
                        homingDelay = 10;
                        lifetime = 80;
                        keepVelocity = false;
                        shootEffect = smokeEffect = Fx.none;
                        shrinkX = 0.5f;
                        shrinkY = 0.5f;
                        weaveMag = 5f;
                        weaveScale = 4f;
                        hitSize = 8;
                        width = 15;
                        height = 15;
                        trailWidth = 3.5f;
                        trailLength = 12;
                        hitSoundVolume = 0.5f;
                        frontColor = hitColor = Color.white;
                        backColor = trailColor = lightColor = Color.valueOf("8ca4fc");
                        hitSound = Sounds.plasmaboom;
                        inaccuracy = 2f;
                        hitEffect = Fx.blastExplosion;
                    }};
                }});
                engineSize = 0f;
                groundLayer = 90;
                envEnabled |= Env.terrestrial | Env.underwater;
                envDisabled = Env.none;
                hitSize = 18;
                rotateSpeed = 2.5f;
                constructor = UnitEntity::create;
                lowAltitude = true;
                health = 1700f;
                flying = true;
                omniMovement = false;
                outlineColor = Color.valueOf("232826");
                speed = 1.5f;
                accel = 0.9f;
                drag = 0.8f;
                armor = 10;
            }};

        // steward tree
        steward = new MechanicalUnitType("steward"){{
            rotateMoveFirst = true;
            armor = 0;
            health = 420;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            constructor = MechUnit::create;
            mechLegColor = AquaPal.tantDarkestTone;
            drawCell = false;
            mechFrontSway = 0.1f;
            mechSideSway = 0.1f;
            speed = 0.9f;
            rotateSpeed = 4;
            abilities.add(
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 3;
                        sizeFrom = 8;
                        sizeTo = 0;
                        lenFrom = 0;
                        lenTo = 6;
                        line = true;
                        length = 15;
                        baseLength = 2;
                        layer = 90;
                        lifetime = 10;
                        colorFrom = Color.valueOf("ffea97");
                        colorTo = Color.valueOf("ffea9710");
                    }}, 90f, .6f) {{
                    }},
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 3;
                        sizeFrom = 0;
                        sizeTo = 4;
                        lifetime = 80;
                        layer = 80;
                        colorFrom = Color.valueOf("262323");
                        colorTo = Color.valueOf("746f6f10");
                    }}, 15f, .4f) {{
                    }}, new SpawnDeathAbility(stewardWreck, 0, 1) {{
                        randAmount = 1;
                    }});
            weapons.add(new Weapon("aquarion-steward-weapon") {{
                mirror = true;
                alternate = true;
                recoil = 1f;
                x = 0.25f;
                outlineColor = AquaPal.tantDarkerTone;
                recoilTime = 5f;
                reload = 25;
                top = false;
                shootX = -4;
                range = 75;
                bullet = new LaserBoltBulletType(3f, 20) {{
                    width = 2f;
                    height = 5;
                    shootEffect = Fx.shootSmall;
                    shootSound = Sounds.blaster;
                    smokeEffect = Fx.shootBigSmoke;
                    lifetime = 45f;
                    maxRange = 80;
                    trailEffect = Fx.none;
                    collidesTiles = true;
                    hitColor = lightColor = Color.white;
                    frontColor = Color.white;
                    backColor = AquaPal.techGreen;
                    healPercent = 0.2f;
                    collidesTeam = true;
                }};
            }});
        }};
        cull = new MechanicalUnitType("cull"){{
            outlines = false;
            hittable = isEnemy = targetable = drawCell = allowedInPayloads = drawBody = false;
            payloadCapacity = (2 * 2) * tilePayload;
            outlineColor = AquaPal.tantDarkerTone;
            lowAltitude = flying = coreUnitDock = true;
            aiController = BuilderAI::new;
            healColor = Pal.accent;
            constructor = UnitEntity::create;
            armor = 2;
            speed = 9;
            fogRadius = 0;
            buildRange = 150;
            mineSpeed = 9;
            accel = 0.08f;
            drag = 0.1f;
            mineWalls = true;
            mineFloor = true;
            buildSpeed = 1.2f;
            rotateSpeed = 12;
            health = 250;
            engineSize = 0;
            mineTier = 3;
            hitSize = 9;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            parts.addAll(
                    new RegionPart("-front") {{
                        progress = PartProgress.warmup;
                        moveY = 0.5f;
                        weaponIndex = 0;
                    }},
                    new RegionPart("-crystal1") {{
                        moveY = -2;
                        moveX = 0;
                        mirror = true;
                        heatColor = Pal.accent;
                        weaponIndex = 0;
                        progress = heatProgress = PartProgress.warmup;
                    }},
                    new RegionPart("-side") {{
                        moveY = -.25f;
                        moveX = -.5f;
                        moveRot = -10;
                        weaponIndex = 0;
                        mirror = true;
                        progress = heatProgress = PartProgress.warmup;
                        heatColor = Pal.accent;
                    }},
                    new RegionPart("-mid") {{
                    }},
                    new RegionPart("-crystal2") {{
                        moveY = -0.5f;
                        moveX = -.5f;
                        moveRot = -5;
                        weaponIndex = 0;
                        progress = heatProgress = PartProgress.warmup;
                        heatColor = Pal.accent;
                        mirror = true;
                    }}
            );
            weapons.add(
                    new RepairBeamWeapon("cull-b") {{
                        mirror = false;
                        rotate = true;
                        autoTarget = true;
                        name = "cull-b";
                        x = 0;
                        y = 0;
                        controllable = false;
                        targetUnits = true;
                        targetBuildings = true;
                        fractionRepairSpeed = 0.08f;
                        widthSinMag = 0.11f;
                        healColor = laserColor = Pal.accent;
                        bullet = new BulletType() {{
                            maxRange = 80;
                        }};
                    }}
            );
        }};
        glean = new MechanicalUnitType("glean"){{
            hittable = isEnemy = targetable = drawCell = allowedInPayloads = drawBody = false;
            payloadCapacity = (2 * 2) * tilePayload;
            outlineColor = AquaPal.tantDarkerTone;
            constructor = UnitEntity::create;
            lowAltitude = flying = coreUnitDock = true;
            aiController = BuilderAI::new;
            healColor = Pal.accent;
            armor = 3;
            speed = 15;
            fogRadius = 0;
            buildRange = 250;
            mineSpeed = 12;
            accel = 0.12f;
            drag = 0.13f;
            mineWalls = true;
            mineFloor = true;
            buildSpeed = 2f;
            rotateSpeed = 15;
            health = 550;
            engineSize = 0;
            mineTier = 3;
            hitSize = 9;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            parts.addAll(
                    new RegionPart("-crystal2") {{
                        moveY = -0.5f;
                        moveX = -1;
                        moveRot = 10;
                        weaponIndex = 0;
                        progress = heatProgress = PartProgress.warmup;
                        heatColor = Pal.accent;
                        mirror = true;
                    }},
                    new RegionPart("-mid") {{
                        progress = PartProgress.warmup;
                        moveY = 0.5f;
                        weaponIndex = 0;
                    }},
                    new RegionPart("-side") {{
                        moveY = -1;
                        moveX = 1;
                        moveRot = -5;
                        weaponIndex = 0;
                        mirror = true;
                        progress = heatProgress = PartProgress.warmup;
                        heatColor = Pal.accent;
                    }},
                    new RegionPart("-crystal1") {{
                        moveY = -0.5f;
                        moveX = -0.5f;
                        moveRot = -1;
                        mirror = true;
                        heatColor = Pal.accent;
                        weaponIndex = 0;
                    }}
            );
            weapons.add(
                    new RepairBeamWeapon("cull-b") {{
                        mirror = false;
                        rotate = true;
                        autoTarget = true;
                        name = "cull-b";
                        x = 0;
                        y = 0;
                        controllable = false;
                        targetUnits = true;
                        targetBuildings = true;
                        fractionRepairSpeed = 0.04f;
                        widthSinMag = 0.11f;
                        healColor = laserColor = Pal.accent;
                        bullet = new BulletType() {{
                            maxRange = 60;
                        }};
                    }}
            );
        }};
        gerbTest = new GerbUnitType("light-infantry") {{
            health = 210;
            armor = 1;
            legCount = 6;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            legStraightness = 0.3f;
            baseLegStraightness = 0.5f;
            lockLegBase = true;
            speed = 0.45f;
            constructor = LegsUnit::create;
            abilities.add(
                    new DamageStateEffectAbility(0f, -7f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 1;
                        sizeFrom = 8;
                        sizeTo = 0;
                        sizeInterp = Interp.pow10Out;
                        length = 0;
                        baseLength = 9;
                        layer = 10;
                        lifetime = 2000;
                        colorFrom = Color.valueOf("65d45330");
                        colorTo = Color.valueOf("48903c0");
                    }}, 90f, .3f) {{
                    }}, new SpawnDeathAbility(InfantryGerbCorpse, 0, 1) {{
                        randAmount = 1;
                    }});
            legMinLength = 0.9f;
            legMaxLength = 1.1f;
            hitSize = 12;
            legMoveSpace = 1.2f;
            legLength = 9;
            legPairOffset = 1;
            legExtension = 0.5f;
            variants = 7;
            rotateSpeed = 6;
            legBaseOffset = 4;
            faceTarget = false;
            allowLegStep = false;
            drownTimeMultiplier = 9000; //too lazy to implement java
            mechStepParticles = true;
            weapons.add(new Weapon("aquarion-infantry-rifle") {{
                            mirror = false;
                            x = 6;
                            y = -1f;
                            recoil = 2f;
                            layerOffset = 1f;
                            outlineColor = AquaPal.tantDarkerTone;
                            recoilTime = 15f;
                            rotate = true;
                            rotateSpeed = 1.2f;
                            reload = 20;
                            shootY = 22;
                            range = 90;
                            bullet = new BasicBulletType(2.5f, 9) {{
                                width = 9f;
                                height = 12f;
                                shootEffect = AquaFx.shootLong;
                                shootSound = Sounds.blaster;
                                trailLength = 9;
                                trailWidth = 2;
                                lifetime = 45f;
                                maxRange = 80;
                                frontColor = hitColor = lightColor = Color.white;
                                backColor = trailColor = Pal.techBlue;
                            }};
                        }}
            );
        }};
        rivulet = new UnitType("rivulet") {{
            controller = u -> new CargoAI();
            isEnemy = false;
            allowedInPayloads = false;
            logicControllable = false;
            playerControllable = false;
            envDisabled = 0;

            drag = 0.06f;
            rotateSpeed = 9f;
            accel = 0.15f;

            speed = 0.6f;
            hidden = true;
            targetable = false;
            envEnabled |= Env.underwater;
            outlineColor = AquaPal.tantDarkestTone;
            payloadCapacity = 0f;
            health = 200f;
            drawCell = false;
            engineSize = 0;
            constructor = BuildingTetherPayloadUnit::create;
            flying = true;
            itemCapacity = 100;
        }};
        iris = new UnitType("iris") {{
            constructor = UnitEntity::create;
            hitSize = 4;
            speed = 6f;
            accel = 0.04f;
            drag = 0.02f;
            isEnemy = false;
            coreUnitDock = true;
            itemCapacity = 30;
            //builds slow bc I hate fun
            buildSpeed = 0.05f;
            health = 30;
            //more armored
            armor = 5;
            engineSize = 0;
            drawCell = false;
            targetable = false;
            hittable = false;
            buildRange = 200;
            allowedInPayloads = false;
            canAttack = false;
            lowAltitude = false;
            rotateMoveFirst = true;
            outlineColor = AquaPal.tantDarkestTone;
            drawBuildBeam = true;
            rotateSpeed = 360;
            flying = true;
        }};
        mite = new UnitType("mite") {{
            constructor = UnitEntity::create;
            flying = true;
            speed = 0.65f;
            useUnitCap = false;
            isEnemy = false;
            outlineColor = AquaPal.tantDarkestTone;
            canAttack = false;
            mineSpeed = 3.5f;
            itemCapacity = 20;
            rotateSpeed = 5;
            rotateMoveFirst = true;
            mineTier = 2;
            drag = 0.06f;
            accel = 0.12f;
            mineFloor = true;
            mineWalls = false;
            logicControllable = false;
            playerControllable = false;
            mineRange = 60;
            mineItems = Seq.with(electrum, Items.lead, Items.titanium);
            hidden = true;
            allowedInPayloads = false;
            controller = u -> new MinerAI();
            defaultCommand = UnitCommand.mineCommand;
            range = 60;
            engineSize = 0;
            lowAltitude = true;
            health = 45;
            hitSize = 9;
            armor = 2;

        }};
        weep = new MultiLegSegmentUnit("weep") {{
            constructor = LegsUnit::create;
            legCount = 6;
            legLength = 35;
            legSegments = 5;
            hitSize = 25;
            speed = 0.65f;
            legBaseOffset = 5;
            rotateSpeed = 1.6f;
            lockLegBase = true;
            drawCell = false;
            baseLegStraightness = 0.2f;
            legStraightness = 0.1f;
            shadowElevation = 0.25f;
            stepShake = 0;
            legPairOffset = 1;
            outlineColor = Color.valueOf("0e0512");
            hovering = true;
            health = 540;
            armor = 4;
        }};
        strut = new MechanicalUnitType("strut") {{
            weapons.add(new Weapon("aquarion-strut-weapon") {{
                mirror = true;
                reload = 25;
                x = 7.5f;
                top = false;
                recoil = 0;
                flipSprite = true;
                rotate = false;
                shootY = 5;
                shootCone = 5;
                shootSound = Sounds.shootAlt;
                bullet = new BasicBulletType(4f, 25, "aquarion-bolt") {{
                    frontColor = hitColor = Color.white;
                    width = 18;
                    shrinkX = 0f;
                    shrinkY = 0.75f;
                    height = 18;
                    trailLength = 9;
                    trailWidth = 2;
                    shootEffect = Fx.shootBig2;
                    hitEffect = despawnEffect = Fx.hitBulletColor;
                    trailRotation = true;
                    trailEffect = AquaFx.strutBulletTrail;
                    trailInterval = 5;
                    backColor = trailColor = lightColor = Pal.lightOrange;
                    lifetime = 25;

                }};
            }});
            constructor = MechUnit::create;
            speed = 0.8f;
            stepShake = 0.5f;
            rotateSpeed = 1.75f;
            drawCell = false;
            baseRotateSpeed = 2f;
            drag = 0.5f;
            accel = 0.75f;
            health = 550;
            hitSize = 15;
            armor = 0;
            mechLegColor = AquaPal.tantDarkestTone;
            outlineColor = AquaPal.tantDarkestTone;
            abilities.add(
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 3;
                        sizeFrom = 8;
                        sizeTo = 0;
                        lenFrom = 0;
                        lenTo = 6;
                        line = true;
                        length = 15;
                        baseLength = 2;
                        layer = 90;
                        lifetime = 10;
                        colorFrom = Color.valueOf("ffea97");
                        colorTo = Color.valueOf("ffea9710");
                    }}, 90f, .6f) {{
                    }},
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 3;
                        sizeFrom = 0;
                        sizeTo = 4;
                        lifetime = 80;
                        layer = 80;
                        colorFrom = Color.valueOf("262323");
                        colorTo = Color.valueOf("746f6f10");
                    }}, 15f, .4f) {{
                    }});
        }};
        truss = new MechanicalUnitType("truss") {{
            weapons.add(new Weapon() {{
                mirror = true;
                reload = 60;
                x = 8f;
                top = false;
                recoil = 0;
                flipSprite = true;
                rotate = false;
                shootY = 5;
                inaccuracy = 4;
                shoot = new ShootSine() {{
                    shots = 10;
                    shotDelay = 5;
                }};
                shootSound = Sounds.shootSnap;
                bullet = new MissileBulletType(4f, 25, "missile-large") {{
                    frontColor = hitColor = Color.white;
                    width = 12;
                    homingPower = 0.05f;
                    shrinkX = 0.1f;
                    shrinkY = 0f;
                    velocityRnd = 0.17f;
                    height = 12;
                    knockback = 0.1f;
                    trailLength = 9;
                    trailWidth = 2;
                    shootEffect = Fx.shootBig;
                    hitEffect = despawnEffect = Fx.hitBulletColor;
                    trailRotation = true;
                    trailEffect = AquaFx.strutBulletTrail;
                    trailInterval = 2;
                    backColor = trailColor = lightColor = Pal.lightOrange;
                    lifetime = 30;

                }};
            }});
            constructor = MechUnit::create;
            speed = 0.65f;
            stepShake = 0.6f;
            rotateSpeed = 1.95f;
            drawCell = false;
            baseRotateSpeed = 3.5f;
            drag = 0.7f;
            accel = 0.85f;
            health = 950;
            hitSize = 20;
            armor = 3;
            mechLegColor = AquaPal.tantDarkestTone;
            outlineColor = AquaPal.tantDarkestTone;
            abilities.add(
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 3;
                        sizeFrom = 8;
                        sizeTo = 0;
                        lenFrom = 0;
                        lenTo = 6;
                        line = true;
                        length = 15;
                        baseLength = 2;
                        layer = 90;
                        lifetime = 10;
                        colorFrom = Color.valueOf("ffea97");
                        colorTo = Color.valueOf("ffea9710");
                    }}, 90f, .6f) {{
                    }},
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 3;
                        sizeFrom = 0;
                        sizeTo = 4;
                        lifetime = 80;
                        layer = 80;
                        colorFrom = Color.valueOf("262323");
                        colorTo = Color.valueOf("746f6f10");
                    }}, 15f, .4f) {{
                    }});
        }};
        joist = new MechanicalUnitType("joist") {{
            weapons.add(new Weapon() {{
                mirror = true;
                reload = 60;
                x = 12f;
                top = false;
                recoil = 0;
                rotate = false;
                shootY = 9;
                shootCone = 5;
                shoot.shots = 3;
                shoot.shotDelay = 5;
                shootSound = Sounds.shootBig;
                bullet = new FlakBulletType(4f, 25) {{
                    frontColor = hitColor = Color.white;
                    width = 16;
                    sprite = "missile-large";
                    homingPower = 0.01f;
                    shrinkX = 0f;
                    shrinkY = 0.85f;
                    height = 19;
                    trailLength = 12;
                    trailWidth = 6;
                    homingDelay = 15f;
                    explodeRange = 5f;
                    collidesGround = true;
                    collidesAir = false;
                    explodeDelay = 0f;
                    flakInterval = 20f;
                    despawnShake = 1f;
                    shootEffect = Fx.shootSmokeSquareBig;
                    smokeEffect = Fx.shootSmokeDisperse;
                    hitEffect = despawnEffect = Fx.hitBulletColor;
                    trailRotation = true;
                    trailEffect = AquaFx.strutBulletTrail;
                    trailInterval = 5;
                    backColor = trailColor = lightColor = Color.valueOf("ffcb64");
                    lifetime = 45;
                    fragBullets = 3;
                    fragBullet = new BasicBulletType(4f, 5, "aquarion-bolt") {{
                        frontColor = hitColor = Color.white;
                        width = 18;
                        shrinkX = 0f;
                        shrinkY = 0.75f;
                        height = 18;
                        trailLength = 12;
                        trailWidth = 3;
                        shootEffect = Fx.shootBig2;
                        hitEffect = despawnEffect = Fx.hitBulletColor;
                        trailRotation = true;
                        trailEffect = AquaFx.strutBulletTrail;
                        trailInterval = 5;
                        lightning = 3;
                        lightningLength = 5;
                        lightningDamage = 5;
                        backColor = trailColor = lightColor = Pal.lightOrange;
                        lifetime = 25;

                    }};
                }};
            }});
            abilities.add(
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 6;
                        sizeFrom = 10;
                        sizeTo = 0;
                        lenFrom = 0;
                        lenTo = 9;
                        line = true;
                        length = 15;
                        baseLength = 2;
                        layer = 90;
                        lifetime = 10;
                        colorFrom = Color.valueOf("ffea97");
                        colorTo = Color.valueOf("ffea9710");
                    }}, 90f, .6f) {{
                    }},
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 6;
                        sizeFrom = 0;
                        sizeTo = 6;
                        lifetime = 80;
                        layer = 80;
                        colorFrom = Color.valueOf("262323");
                        colorTo = Color.valueOf("746f6f10");
                    }}, 15f, .4f) {{
                    }});
            constructor = MechUnit::create;
            speed = 0.5f;
            stepShake = 0.7f;
            rotateSpeed = 1.25f;
            drawCell = false;
            baseRotateSpeed = 2f;
            drag = 0.7f;
            accel = 0.85f;
            health = 2250;
            hitSize = 25;
            armor = 6;
            mechLegColor = AquaPal.tantDarkestTone;
            outlineColor = AquaPal.tantDarkestTone;
            shadowElevation = 0.5f;
        }};
        buttress = new MechanicalUnitType("buttress") {{
            weapons.add(new Weapon() {{
                mirror = true;
                reload = 60;
                x = 12f;
                top = false;
                recoil = 0;
                rotate = false;
                shootY = 9;
                shootCone = 5;
                shoot.shots = 3;
                shoot.shotDelay = 5;
                shootSound = Sounds.shootBig;
            }});
            constructor = MechUnit::create;
            speed = 0.35f;
            stepShake = 0.8f;
            rotateSpeed = 1.15f;
            drawCell = false;
            baseRotateSpeed = 1.75f;
            drag = 0.7f;
            accel = 0.85f;
            health = 6500;
            hitSize = 35;
            armor = 12;
            mechLegColor = AquaPal.tantDarkestTone;
            outlineColor = AquaPal.tantDarkestTone;
            shadowElevation = 0.9f;
            abilities.add(
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, Fx.burning, 5, 25),
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 6;
                        sizeFrom = 10;
                        sizeTo = 0;
                        lenFrom = 0;
                        lenTo = 9;
                        line = true;
                        length = 15;
                        baseLength = 2;
                        layer = 90;
                        lifetime = 10;
                        colorFrom = Color.valueOf("ffea97");
                        colorTo = Color.valueOf("ffea9710");
                    }}, 90f, .6f) {{
                    }},
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 9;
                        sizeFrom = 0;
                        sizeTo = 9;
                        lifetime = 80;
                        layer = 80;
                        colorFrom = Color.valueOf("262323");
                        colorTo = Color.valueOf("746f6f10");
                    }}, 15f, .4f) {{
                    }});
        }};
        stanchion = new MechanicalUnitType("stanchion") {{
            weapons.add(new Weapon() {{
                mirror = true;
                reload = 60;
                x = 12f;
                top = false;
                recoil = 0;
                rotate = false;
                shootY = 9;
                shootCone = 5;
                shoot.shots = 3;
                shoot.shotDelay = 5;
                shootSound = Sounds.shootBig;
            }});
            constructor = MechUnit::create;

            speed = 0.25f;
            stepShake = 1f;
            rotateSpeed = 1f;
            drawCell = false;
            baseRotateSpeed = 1.5f;
            drag = 0.7f;
            accel = 0.85f;
            health = 15280;
            hitSize = 58;
            armor = 34;
            mechLegColor = AquaPal.tantDarkestTone;
            outlineColor = AquaPal.tantDarkestTone;
            shadowElevation = 1.2f;
            abilities.add(
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 6;
                        sizeFrom = 10;
                        sizeTo = 0;
                        lenFrom = 0;
                        lenTo = 9;
                        line = true;
                        length = 15;
                        baseLength = 2;
                        layer = 90;
                        lifetime = 10;
                        colorFrom = Color.valueOf("ffea97");
                        colorTo = Color.valueOf("ffea9710");
                    }}, 90f, .6f) {{
                    }},
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect() {{
                        particles = 12;
                        sizeFrom = 0;
                        sizeTo = 18;
                        lifetime = 80;
                        layer = 80;
                        colorFrom = Color.valueOf("262323");
                        colorTo = Color.valueOf("746f6f10");
                    }}, 10f, .4f) {{
                    }});
        }};
        bulwark = new UnitType("bulwark"){{
            constructor = MechUnit::create;
            speed = 0.45f;
            hitSize = 8;
            range = 110;
            health = 300;
            armor = 4;
            targetAir = false;
            rotateMoveFirst = true;
            rotateSpeed = 0.6f;
            shadowElevation = 0.1f;
            outlines = true;
            drawCell = false;
            outlineColor = AquaPal.tantDarkestTone;
            weapons.addAll(new Weapon("aquarion-bulwark-weapon"){{
                rotate = true;
                rotateSpeed = 0.9f;
                mirror = false;
                x = 0;
                y = -1;
                recoil = 2;
                shootSound = Sounds.shootAltLong;
                shootY = 3;
                reload = 90;
                bullet = new BasicBulletType(3, 50){{
                    collidesAir = false;
                    shrinkY = 0;
                    shrinkX = 0.2f;
                    hitSize = 4;
                    recoil = 1;
                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootSmallColor;
                    frontColor = AquaPal.fireLight1;
                    backColor = trailColor = lightColor = trailColor = AquaPal.fireLight2;
                    width = height = 12;
                    knockback = 2;
                    trailLength = 12;
                }};
            }});
        }};
        rampart = new UnitType("rampart"){{
            constructor = MechUnit::create;
            speed = 0.7f;
            hitSize = 8;
            range = 110;
            health = 180;
            armor = 6;
            targetAir = true;
            rotateSpeed = 1.2f;
            shadowElevation = 0.2f;
            outlines = true;
            drawCell = false;
            outlineColor = AquaPal.tantDarkestTone;
            weapons.addAll(new Weapon("aquarion-rampart-weapon"){{
                rotate = false;
                rotateSpeed = 0.9f;
                mirror = false;
                x = 5;
                y = 0;
                recoil = 2;
                shootSound = Sounds.shootAltLong;
                shootY = 4;
                reload = 35;
                bullet = new BasicBulletType(4, 15, "aquarion-bolt"){{
                    shrinkY = 0;
                    shrinkX = 0.2f;
                    hitSize = 2;
                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootSmallColor;
                    frontColor = AquaPal.fireLight1;
                    backColor = trailColor = lightColor = trailColor = AquaPal.fireLight2;
                    width = height = 10;
                    trailLength = 8;
                }};
            }});
        }};
        pugnate = new UnitType("pugnate"){{
            constructor = LegsUnit::create;
            speed = 0.55f;
            hitSize = 8;
            range = 110;
            health = 180;
            stepShake = 0;
            armor = 2;
            targetAir = true;
            rotateSpeed = 1.2f;
            shadowElevation = 0.2f;
            outlines = true;
            drawCell = false;
            outlineColor = AquaPal.tantDarkestTone;

            legCount = 4;
            legLength = 12f;
            lockLegBase = true;
            legContinuousMove = true;
            legExtension = -1f;
            legBaseOffset = 3f;
            legMaxLength = 1f;
            legMinLength = 0.7f;
            legLengthScl = 0.95f;
            legForwardScl = 0.75f;

            legMoveSpace = 1f;

            weapons.addAll(new Weapon("aquarion-pugnate-weapon"){{
                rotate = true;
                rotateSpeed = 0.9f;
                mirror = false;
                x = 3;
                recoil = 2;
                shootSound = Sounds.cannon;
                shootY = 4;
                reload = 160;
                bullet = new BasicBulletType(8f, 110, "aquarion-flechette"){{
                    shrinkY = 0;
                    shrinkX = 0.2f;
                    hitSize = 4;
                    lifetime = 10;
                    pierceCap = 2;
                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootSmallColor;
                    frontColor = AquaPal.fireLight1;
                    backColor = trailColor = lightColor  = AquaPal.fireLight2;
                    width = height = 15;
                    trailLength = 14;
                }};
            }});
        }};
        crest = new UnitType("crest"){{
            flying = true;
            lowAltitude = true;
            constructor = UnitEntity::create;
            speed = 2f;
            hitSize = 8;
            range = 110;
            health = 125;
            armor = 0;
            engineSize = 0;
            setEnginesMirror(
                    new UnitEngine(25 / 4f, 25 / 4f, 2.3f, 45f),
                    new UnitEngine(25 / 4f, -25 / 4f, 2.3f, 315f)
            );
            targetAir = true;
            rotateSpeed = 5f;
            shadowElevation = 0.2f;
            outlines = true;
            drawCell = false;
            outlineColor = AquaPal.tantDarkestTone;
            weapons.addAll(new Weapon("aquarion-crest-weapon"){{
                rotate = true;
                rotateSpeed = 0.9f;
                mirror = false;
                x = 0;
                y = -1;
                recoil = 2;
                shootSound = Sounds.pew;
                shootY = 3;
                reload = 5;
                inaccuracy = 5;
                bullet = new BasicBulletType(7, 5, "aquarion-bolt"){{
                    shrinkY = 0;
                    lifetime = 15;
                    shrinkX = 0.2f;
                    hitSize = 2;
                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootSmallColor;
                    frontColor = AquaPal.fireLight1;
                    backColor = trailColor = lightColor = AquaPal.fireLight2;
                    width = height = 7;
                    trailLength = 8;
                }};
            }});
        }};
        reave = new UnitType("reave"){{
            constructor = LegsUnit::create;
            speed = 0.45f;
            hitSize = 16;
            range = 120;
            health = 280;
            stepShake = 0.1f;
            armor = 5;
            targetAir = true;
            rotateSpeed = 1.4f;
            shadowElevation = 0.25f;
            outlines = true;
            drawCell = false;
            outlineColor = AquaPal.tantDarkestTone;

            legCount = 4;
            baseLegStraightness = 0.25f;
            legStraightness = 0.1f;
            legLength = 18f;
            lockLegBase = true;
            legContinuousMove = true;
            legExtension = -.5f;
            legBaseOffset = 6f;
            legMaxLength = 1.05f;
            legMinLength = 0.8f;
            legLengthScl = 0.95f;
            legForwardScl = 0.8f;
            legPairOffset = 2;

            legMoveSpace = 0.9f;

            weapons.addAll(new Weapon("aquarion-reave-weapon"){{
                rotate = true;
                rotateSpeed = 0.9f;
                mirror = false;
                x = -4;
                y = -3;
                recoil = 2;
                shootSound = Sounds.shootAltLong;
                shootY = 4;
                reload = 90;
                bullet = new BasicBulletType(8f, 60, "aquarion-bolt"){{
                    shrinkY = 0;
                    shrinkX = 0.2f;
                    hitSize = 4;
                    lifetime = 15;
                    pierceCap = 3;
                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootSmallColor;
                    frontColor = AquaPal.fireLight1;
                    backColor = trailColor = lightColor  = AquaPal.fireLight2;
                    width = height = 15;
                    trailLength = 14;
                }};
            }}, new Weapon("aquarion-reave-side-weapon"){{
                rotate = true;
                rotateSpeed = 1.1f;
                mirror = false;
                x = 6f;
                y = 6f;
                shootSound = Sounds.shootAlt;
                shootY = 4;
                reload = 160;
                inaccuracy = 7;
                bullet = new BasicBulletType(2.5f, 45, "aquarion-flechette"){{
                    shrinkY = 0.1f;
                    shrinkX = 0.4f;
                    knockback = 5;
                    hitSize = 4;
                    lifetime = 20;
                    shootEffect = Fx.shootSmallColor;
                    smokeEffect = Fx.shootSmallColor;
                    frontColor = AquaPal.fireLight1;
                    backColor = trailColor = lightColor  = AquaPal.fireLight2;
                    width = height = 8;
                    trailLength = 7;
                }};
            }});
        }};
        soar = new UnitType("soar"){{
            parts.add(new RegionPart("-glow"){{
                color = Color.red;
                blending = Blending.additive;
                layer = -1f;
                outline = false;
            }});
            flying = true;
            lowAltitude = true;
            constructor = UnitEntity::create;
            speed = 1.8f;
            omniMovement = false;
            circleTarget = true;
            hitSize = 16;
            range = 40;
            health = 550;
            armor = 8;
            engineOffset = 6;
            engineSize = 4;
            setEnginesMirror(
                    new UnitEngine(25 / 4f, -25 / 4f, 2.3f, -315f)
            );
            targetAir = true;
            rotateSpeed = 5f;
            shadowElevation = 1.5f;
            outlines = true;
            autoDropBombs = true;
            faceTarget = false;
            targetAir = false;
            accel = 0.08f;
            drag = 0.016f;
            drawCell = false;
            lowAltitude = false;
            outlineColor = AquaPal.tantDarkestTone;
            weapons.add(new Weapon(){{
                minShootVelocity = 0.75f;
                x = 3f;
                shootY = 0f;
                shootCone = 180f;
                lowAltitude = false;
                ejectEffect = Fx.casing2;
                inaccuracy = 15f;
                ignoreRotation = true;
                shoot.shots = 5;
                shoot.shotDelay = 10;
                shootSound = Sounds.mineDeploy;
                reload = 200;
                bullet = new BombBulletType(60f, 40f){{
                    lifetime = 60;
                    shrinkX = 0.5f;
                    shrinkY = 0.5f;
                    width = 10f;
                    height = 10f;
                    despawnEffect = Fx.blastExplosion;
                    hitEffect = Fx.blastExplosion;
                    shootEffect = Fx.none;
                    smokeEffect = Fx.none;
                    frontColor = AquaPal.fireLight1;
                    backColor = lightColor = hitColor = AquaPal.fireLight2;
                    status = AquaStatuses.concussed;
                    statusDuration = 3*60f;
                    collidesAir = false;
                }};
            }});
        }};
        raze = new UnitType("raze"){{
            constructor = LegsUnit::create;
            speed = 0.35f;
            hitSize = 16;
            range = 150;
            health = 350;
            stepShake = 0.2f;
            armor = 3;
            targetAir = true;
            rotateSpeed = 1.2f;
            shadowElevation = 0.2f;
            outlines = true;
            drawCell = false;
            outlineColor = AquaPal.tantDarkestTone;
            legCount = 6;
            baseLegStraightness = 0.6f;
            legStraightness = 0.5f;
            legLength = 18f;
            lockLegBase = true;
            legContinuousMove = true;
            legExtension = -.5f;
            legBaseOffset = 6f;
            legMaxLength = 1.2f;
            legMinLength = 0.4f;
            legLengthScl = 0.95f;
            legForwardScl = 0.9f;
            legPairOffset = 2;
            legGroupSize = 3;

            legMoveSpace = 0.9f;
            weapons.addAll(new Weapon("aquarion-raze-weapon"){{
                mirror = false;
                x = 0;
                y = 0;
                recoil = 2;
                rotate = false;
                shootSound = Sounds.flame;
                reload = 5;
                shootX = -7;
                shootY = 7;
                shootCone = 15;
                inaccuracy = 15;
                bullet = new BulletType(4.2f, 15f){{
                    hitSize = 7f;
                    lifetime = 13f;
                    pierce = true;
                    pierceBuilding = true;
                    pierceCap = 2;
                    statusDuration = 60f * 4;
                    status = StatusEffects.burning;
                    shootEffect = Fx.shootSmallFlame;
                    hitEffect = Fx.hitFlameSmall;
                    keepVelocity = false;
                    hittable = false;
                }};
            }});
        }};
        shatter = new UnitType("shatter"){{
            constructor = MechUnit::create;
            speed = 0.25f;
            hitSize = 16;
            range = 110;
            health = 300;
            armor = 3;
            targetAir = true;
            rotateSpeed = 1.1f;
            shadowElevation = 0.3f;
            outlines = true;
            drawCell = false;
            outlineColor = AquaPal.tantDarkestTone;
            weapons.addAll(new Weapon("aquarion-shatter-weapon"){{
                rotate = false;
                rotateSpeed = 0.9f;
                mirror = false;
                x = 0;
                y = 0;
                recoil = 3;
                shootSound = Sounds.shootAltLong;
                shootY = 4;
                reload = 160;
                bullet = new BasicBulletType(4, 90, "aquarion-flechette"){{
                    shrinkY = 0;
                    shrinkX = 0.2f;
                    hitSize = 4;
                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootSmallColor;
                    frontColor = AquaPal.fireLight1;
                    backColor = trailColor = lightColor  = AquaPal.fireLight2;
                    width = height = 16;
                    trailLength = 12;
                }};
            }});
        }};
        retaliate = new newTankUnitType("retaliate"){{
            constructor = TankUnit::create;
            speed = 0.45f;
            armor = 20;
            health = 2250;
            omniMovement = true;
            targetAir = false;
            shadowElevation = 0.1f;
            hitSize = 8*6/2f;
            outlines = true;
            drawCell = false;
            treadFrames = 4;
            outlineColor = AquaPal.tantDarkestTone;
            //NOTE Do not yoink code for vanilla tread rects.
            treadRects = new Rect[]{
                    new Rect(-192, -192, 192, 192),
                    new Rect(-192, -192, 192, 192),
            };
            weapons.addAll(new Weapon("aquarion-retaliate-weapon"){{
                rotate = true;
                rotateSpeed = 0.9f;
                mirror = false;
                x = -2;
                y = -1;
                recoil = 4;
                shootY = 8;
                shootSound = Sounds.largeCannon;
                reload = 150;
                bullet = new ArtilleryBulletType(9f, 150, "aquarion-bolt"){{
                    shrinkY = 0.4f;
                    shrinkX = 0.3f;
                    lifetime = 30;
                    splashDamage = 120;
                    splashDamageRadius = 50f;
                    collidesAir = false;
                    trailRotation = true;
                    trailEffect = AquaFx.pentagonShootSmoke;
                    shootEffect = AquaFx.shootLong;
                    smokeEffect = AquaFx.GyreShootSmoke;
                    frontColor = AquaPal.fireLight1;
                    backColor = trailColor = lightColor  = AquaPal.fireLight2;
                    width = height = 15;
                    trailLength = 5;
                    despawnShake = 2;
                    despawnSound = Sounds.largeExplosion;
                }};
            }});
        }};
        castellan = new newTankUnitType("castellan"){{
            constructor = TankUnit::create;
            speed = 0.3f;
            armor = 12;
            health = 250;
            omniMovement = false;
            targetAir = false;
            shadowElevation = 0.05f;
            hitSize = 16;
            outlines = true;
            drawCell = false;
            treadFrames = 4;
            outlineColor = AquaPal.tantDarkestTone;
            //NOTE Do not yoink code for vanilla tread rects.
            treadRects = new Rect[]{
                    new Rect(-96f/2f, -96/2f, 96, 96),
                    new Rect(-96/2f, -96/2f, 96, 96)
            };
            weapons.addAll(new Weapon("aquarion-castellan-weapon"){{
                rotate = true;
                rotateSpeed = 0.9f;
                mirror = false;
                x = -2;
                y = -1;
                recoil = 2;
                shootSound = Sounds.mediumCannon;
                shootY = 5;
                reload = 90;
                shoot = new ShootBarrel(){{
                    barrels = new float[]{
                            -3, 0f, 0,
                            0, 0, 0,
                            3, 0f, 0
                    };
                    shots = 3;
                    shotDelay = 2f;
                }};
                bullet = new ArtilleryBulletType(1f, 0, "aquarion-bolt"){{
                    shrinkY = 0.4f;
                    shrinkX = 0.3f;
                    lifetime = 140;
                    splashDamage = 40;
                    splashDamageRadius = 45f;
                    collidesAir = false;
                    trailRotation = true;
                    trailEffect = AquaFx.pentagonShootSmoke;
                    shootEffect = AquaFx.shootLong;
                    smokeEffect = AquaFx.GyreShootSmoke;
                    frontColor = AquaPal.fireLight1;
                    backColor = trailColor = lightColor  = AquaPal.fireLight2;
                    width = height = 15;
                    trailLength = 2;
                }};
            }});
        }};
    }}