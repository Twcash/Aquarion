package aquarion.units;

import aquarion.AquaStatuses;
import aquarion.type.*;
import aquarion.world.AI.DroneAI;
import aquarion.world.abilities.LightningFieldAbility;
import aquarion.world.entities.DroneSpawnerBulletType;
import aquarion.world.graphics.AquaFx;
import aquarion.world.units.newTankUnitType;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.struct.Seq;
import mindustry.ai.UnitCommand;
import mindustry.ai.types.BuilderAI;
import mindustry.ai.types.CargoAI;
import mindustry.ai.types.MinerAI;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.abilities.MoveEffectAbility;
import mindustry.entities.abilities.ShieldRegenFieldAbility;
import mindustry.entities.abilities.SpawnDeathAbility;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.part.HoverPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootSine;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import aquarion.world.graphics.AquaPal;
import mindustry.type.Weapon;
import mindustry.type.unit.MissileUnitType;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.electrum;
import static aquarion.units.AquaWrecks.*;
import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.tilePayload;
import static mindustry.gen.Sounds.spray;

public class AquaUnitTypes {
    //cruxahh
    public static UnitType fabricantDrone;
    public static UnitType isop, empusa, oratoria, rhombodera, parasphendale;
    public static UnitType frost, rime, verglas, glaciate, permafrost;
    public static UnitType cog, tenon, assembly, fabricant, architect;

    //core units and transport

    public static UnitType
        //Sharded units
            bulwark, pugnate, rampart, crest, reave, soar, raze, shatter, castellan, retaliate, index, byteUnit, truple,
            //sharded "support" units

            //qeralter Units
        weep,
            //core/mining
            mite, iris,
        //strut tree
        strut, truss, joist, buttress, stanchion, cull,

            //Qeralter Units End Region
    //messenger tree
  ambassador, consul, legate, monarch,

     //steward tree
 curator, custodian, caretaker, warden, fish1;

    //gerbUnits
    public static GerbUnitType gerbTest;
    //legUnits mechanical
    public static  MechanicalUnitType reap;
    // mechs
    public static MechanicalUnitType messenger, steward;
    public static  MechanicalUnitType zoarcid, anguilli, cyprin, pycogen, batoid, goss, heed, effect, consummate, efectuate;
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
        consul = new MechanicalUnitType("consul") {{
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
            }
        };

        // steward tree
        steward = new MechanicalUnitType("steward") {{
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
        cull = new UnitType("cull") {{
            payloadCapacity = (2 * 2) * tilePayload;
            outlineColor = AquaPal.tantDarkerTone;
            lowAltitude = flying = coreUnitDock = true;
            aiController = BuilderAI::new;
            health = 25;
            healColor = Pal.accent;
            constructor = UnitEntity::create;
            engines.add(
                    new UnitEngine(0, -18 / 4f, 2.5f, -90),
                    new UnitEngine(0, 18 / 4f, 2.5f, 90)
            );
            setEnginesMirror(
                    new UnitEngine(16 / 4f, 0, 2.5f, 0)
            );
            speed = 9;
            fogRadius = 2;
            buildRange = 160;
            mineSpeed = 9;
            accel = 0.08f;
            drag = 0.1f;
            mineWalls = true;
            mineFloor = true;
            buildSpeed = 1.3f;
            rotateSpeed = 12;
            engineSize = 0;
            mineTier = 2;
            hitSize = 16;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
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
                        fractionRepairSpeed = 0.03f;
                        widthSinMag = 0.11f;
                        healColor = laserColor = Pal.accent;
                        bullet = new BulletType() {{
                            maxRange = 80;
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
        bulwark = new UnitType("bulwark") {{
            constructor = MechUnit::create;
            speed = 0.45f;
            hitSize = 8;
            range = 110;
            health = 550;
            armor = 2;
            targetAir = false;
            rotateMoveFirst = true;
            mechLegColor = AquaPal.tantDarkestTone;
            rotateSpeed = 0.75f;
            shadowElevation = 0.12f;
            outlines = true;
            drawCell = false;
            outlineColor = AquaPal.tantDarkestTone;
            weapons.addAll(new Weapon("aquarion-bulwark-weapon") {{
                rotate = true;
                rotateSpeed = 1.1f;
                mirror = false;
                x = 0;
                y = -1;
                recoil = 2;
                shootSound = Sounds.shootAltLong;
                shootY = 3;
                reload = 90;
                bullet = new BasicBulletType(3, 25) {{
                    collidesAir = false;
                    shrinkY = 0;
                    shrinkX = 0.2f;
                    hitSize = 4;
                    recoil = 1;
                    fragBullets = 3;
                    fragBullet = new BasicBulletType(4, 10) {{
                        frontColor = AquaPal.fireLight1;
                        backColor = trailColor = lightColor = trailColor = AquaPal.fireLight2;
                        lifetime = 20;
                        status = StatusEffects.burning;
                        statusDuration = 1.5f * 60f;
                        trailLength = 4;
                        width = 8;
                        height = 4;
                    }};
                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootSmallColor;
                    frontColor = AquaPal.fireLight1;
                    status = StatusEffects.burning;
                    statusDuration = 4 * 60f;
                    backColor = trailColor = lightColor = trailColor = AquaPal.fireLight2;
                    width = height = 12;
                    knockback = 2;
                    trailLength = 12;
                }};
            }});
        }};
        rampart = new UnitType("rampart") {{
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
            weapons.addAll(new Weapon("aquarion-rampart-weapon") {{
                rotate = false;
                rotateSpeed = 0.9f;
                mirror = false;
                x = 5;
                y = 0;
                recoil = 2;
                shootSound = Sounds.shootAltLong;
                shootY = 4;
                reload = 35;
                bullet = new BasicBulletType(4, 15, "aquarion-bolt") {{
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
        pugnate = new UnitType("pugnate") {{
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

            weapons.addAll(new Weapon("aquarion-pugnate-weapon") {{
                rotate = true;
                rotateSpeed = 0.9f;
                mirror = false;
                x = 3;
                recoil = 2;
                shootSound = Sounds.cannon;
                shootY = 4;
                reload = 160;
                bullet = new BasicBulletType(8f, 110, "aquarion-flechette") {{
                    shrinkY = 0;
                    shrinkX = 0.2f;
                    hitSize = 4;
                    lifetime = 10;
                    pierceCap = 2;
                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootSmallColor;
                    frontColor = AquaPal.fireLight1;
                    backColor = trailColor = lightColor = AquaPal.fireLight2;
                    width = height = 15;
                    trailLength = 14;
                }};
            }});
        }};
        crest = new UnitType("crest") {{
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
            weapons.addAll(new Weapon("aquarion-crest-weapon") {{
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
                bullet = new BasicBulletType(7, 5, "aquarion-bolt") {{
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
        byteUnit = new UnitType("byte") {{
            flying = true;
            lowAltitude = true;
            constructor = UnitEntity::create;
            speed = 2f;
            hitSize = 8;
            range = 110;
            health = 100;
            armor = 3;
            engineSize = 3.5f;
            defaultCommand = UnitCommand.repairCommand;
            engineOffset = 3;
            isEnemy = false;
            canAttack = false;
            rotateSpeed = 5f;
            shadowElevation = 0.4f;
            outlines = true;
            drawCell = false;
            outlineColor = AquaPal.tantDarkestTone;
            weapons.add(new RepairBeamWeapon() {{
                widthSinMag = 0.12f;
                reload = 20f;
                x = 0f;
                y = 2f;
                rotate = false;
                shootY = 0f;
                beamWidth = 0.6f;
                repairSpeed = 3.1f;
                fractionRepairSpeed = 0.035f;
                aimDst = 0f;
                shootCone = 15f;
                mirror = false;

                targetUnits = false;
                targetBuildings = true;
                autoTarget = false;
                controllable = true;
                laserColor = Pal.heal;
                healColor = Pal.heal;

                bullet = new BulletType() {{
                    maxRange = 75f;
                }};
            }});
            range = 60;
        }};
        reave = new UnitType("reave") {{
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

            weapons.addAll(new Weapon("aquarion-reave-weapon") {{
                rotate = true;
                rotateSpeed = 0.9f;
                mirror = false;
                x = -4;
                y = -3;
                recoil = 2;
                shootSound = Sounds.shootAltLong;
                shootY = 4;
                reload = 90;
                bullet = new BasicBulletType(8f, 60, "aquarion-bolt") {{
                    shrinkY = 0;
                    shrinkX = 0.2f;
                    hitSize = 4;
                    lifetime = 15;
                    pierceCap = 3;
                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootSmallColor;
                    frontColor = AquaPal.fireLight1;
                    backColor = trailColor = lightColor = AquaPal.fireLight2;
                    width = height = 15;
                    trailLength = 14;
                }};
            }}, new Weapon("aquarion-reave-side-weapon") {{
                rotate = true;
                rotateSpeed = 1.1f;
                mirror = false;
                x = 6f;
                y = 6f;
                shootSound = Sounds.shootAlt;
                shootY = 4;
                reload = 160;
                inaccuracy = 7;
                bullet = new BasicBulletType(2.5f, 45, "aquarion-flechette") {{
                    shrinkY = 0.1f;
                    shrinkX = 0.4f;
                    knockback = 5;
                    hitSize = 4;
                    lifetime = 20;
                    shootEffect = Fx.shootSmallColor;
                    smokeEffect = Fx.shootSmallColor;
                    frontColor = AquaPal.fireLight1;
                    backColor = trailColor = lightColor = AquaPal.fireLight2;
                    width = height = 8;
                    trailLength = 7;
                }};
            }});
        }};
        soar = new UnitType("soar") {{
            parts.add(new RegionPart("-glow") {{
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
            weapons.add(new Weapon() {{
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
                bullet = new BombBulletType(60f, 40f) {{
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
                    statusDuration = 3 * 60f;
                    collidesAir = false;
                }};
            }});
        }};
        raze = new UnitType("raze") {{
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
            weapons.addAll(new Weapon("aquarion-raze-weapon") {{
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
                bullet = new BulletType(4.2f, 15f) {{
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
        shatter = new UnitType("shatter") {{
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
            weapons.addAll(new Weapon("aquarion-shatter-weapon") {{
                rotate = false;
                rotateSpeed = 0.9f;
                mirror = false;
                x = 0;
                y = 0;
                recoil = 3;
                shootSound = Sounds.shootAltLong;
                shootY = 4;
                reload = 160;
                bullet = new BasicBulletType(4, 90, "aquarion-flechette") {{
                    shrinkY = 0;
                    shrinkX = 0.2f;
                    hitSize = 4;
                    shootEffect = Fx.shootBig;
                    smokeEffect = Fx.shootSmallColor;
                    frontColor = AquaPal.fireLight1;
                    backColor = trailColor = lightColor = AquaPal.fireLight2;
                    width = height = 16;
                    trailLength = 12;
                }};
            }});
        }};
        retaliate = new newTankUnitType("retaliate") {{
            constructor = TankUnit::create;
            speed = 0.45f;
            armor = 20;
            health = 2250;
            omniMovement = true;
            targetAir = false;
            shadowElevation = 0.1f;
            hitSize = 8 * 6 / 2f;
            outlines = true;
            drawCell = false;
            outlineColor = AquaPal.tantDarkestTone;
            //NOTE Do not yoink code for vanilla tread rects.
            treadRects = new Rect[]{
                    new Rect(-192, -192, 192, 192),
                    new Rect(-192, -192, 192, 192),
            };
            weapons.addAll(new Weapon("aquarion-retaliate-weapon") {{
                rotate = true;
                rotateSpeed = 0.9f;
                mirror = false;
                x = -2;
                y = -1;
                recoil = 4;
                shootY = 8;
                shootSound = Sounds.largeCannon;
                reload = 150;
                bullet = new ArtilleryBulletType(9f, 150, "aquarion-bolt") {{
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
                    backColor = trailColor = lightColor = AquaPal.fireLight2;
                    width = height = 15;
                    trailLength = 5;
                    despawnShake = 2;
                    despawnSound = Sounds.largeExplosion;
                }};
            }});
        }};
        castellan = new newTankUnitType("castellan") {{
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
                    new Rect(-96f / 2f, -96 / 2f, 96, 96),
                    new Rect(-96 / 2f, -96 / 2f, 96, 96)
            };
            weapons.addAll(new Weapon("aquarion-castellan-weapon") {{
                rotate = true;
                rotateSpeed = 0.9f;
                mirror = false;
                x = -2;
                y = -1;
                recoil = 2;
                shootSound = Sounds.mediumCannon;
                shootY = 5;
                reload = 90;
                shoot = new ShootBarrel() {{
                    barrels = new float[]{
                            -3, 0f, 0,
                            0, 0, 0,
                            3, 0f, 0
                    };
                    shots = 3;
                    shotDelay = 2f;
                }};
                bullet = new ArtilleryBulletType(1f, 0, "aquarion-bolt") {{
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
                    backColor = trailColor = lightColor = AquaPal.fireLight2;
                    width = height = 15;
                    trailLength = 2;
                }};
            }});
        }};
        isop = new UnitType("isop") {{
            squareShape = true;
            omniMovement = false;
            rotateMoveFirst = true;
            rotateSpeed = 1.5f;
            envDisabled = Env.none;
            speed = 0.65f;
            crushDamage = 0.002f;
            constructor = TankUnit::create;
            treadRects = new Rect[] {
                    new Rect(-15.5f, -25f, 30, 52)
            };
            hitSize = 14;
            treadPullOffset = 3;
            health = 320;
            armor = 2;
            weapons.add(new Weapon("aquarion-small-missiles") {{
                rotate = true;
                x = 4;
                y = -0.5f;
                reload = 90;
                mirror = true;
                shootSound = Sounds.missile;
                shoot.shots = 2;
                inaccuracy = 5f;
                shake = 0.5f;
                bullet = new MissileBulletType(2.5f, 30) {{
                    width = 8f;
                    height = 8;
                    trailLength = 5;
                    sprite = "missile";
                    shrinkY = 0.2f;
                    drag = -0.002f;
                    homingRange = 80f;
                    keepVelocity = false;
                    lifetime = 65f;
                    trailColor = Color.valueOf("974861");
                    backColor = Color.valueOf("974861");
                    frontColor = Color.valueOf("ff81a8");
                    hitEffect = Fx.blastExplosion;
                    despawnEffect = Fx.blastExplosion;
                    weaveScale = 4f;
                    weaveMag = 1.5f;
                    velocityRnd = 0.3f;
                }};
            }});
        }};
        empusa = new UnitType("empusa") {{
            squareShape = true;
            omniMovement = false;
            rotateMoveFirst = true;
            rotateSpeed = 1.5f;
            envDisabled = Env.none;
            speed = 0.75f;
            crushDamage = 0.002f;
            constructor = TankUnit::create;
            treadRects = new Rect[]{
                    new Rect(-36f, -17f, 17, 49),
                    new Rect(-18f, -43f, 35, 84)

            };
            hitSize = 14;
            treadPullOffset = 3;
            health = 500;
            armor = 4;
            weapons.add(new Weapon("aquarion-missile-weapon") {{
                rotate = true;
                x = 0;
                y = -3;
                reload = 70;
                mirror = false;
                shootSound = Sounds.missile;
                shoot.shots = 3;
                shoot.shotDelay = 3;
                inaccuracy = 5f;
                shake = 0.5f;
                bullet = new MissileBulletType(2.5f, 15) {{
                    width = 10f;
                    height = 10f;
                    trailLength = 7;
                    sprite = "missile-large";
                    shrinkY = 0.2f;
                    drag = -0.002f;
                    homingRange = 80f;
                    keepVelocity = false;
                    splashDamage = 20;
                    splashDamageRadius = 20;
                    lifetime = 65f;
                    trailColor = Color.valueOf("974861");
                    backColor = Color.valueOf("974861");
                    frontColor = Color.valueOf("ff81a8");
                    hitEffect = Fx.blastExplosion;
                    despawnEffect = Fx.blastExplosion;
                    weaveScale = 4f;
                    weaveMag = 1.5f;
                    velocityRnd = 0.3f;
                }};
            }});

        }};
        oratoria = new UnitType("oratoria") {{
            squareShape = true;
            omniMovement = false;
            rotateMoveFirst = true;
            rotateSpeed = 1.2f;
            envDisabled = Env.none;
            speed = 0.45f;
            crushDamage = 0.004f;
            constructor = TankUnit::create;
            treadRects = new Rect[]{
                    new Rect(-40f, -44.5f, 15, 96)
            };
            hitSize = 18;
            treadPullOffset = 2;
            health = 1200;
            armor = 7;

            weapons.add(new Weapon("aquarion-small-missiles") {{
                rotate = true;
                x = 9;
                y = -2;
                reload = 45;
                shootSound = Sounds.missile;
                shoot.shots = 2;
                inaccuracy = 5f;
                shake = 1;
                bullet = new MissileBulletType(3, 8) {{
                    width = 8f;
                    height = 8f;
                    shrinkY = 0.2f;
                    drag = -0.003f;
                    homingRange = 60f;
                    keepVelocity = false;
                    splashDamage = 15;
                    splashDamageRadius = 12;
                    lifetime = 40f;
                    trailColor = Color.valueOf("974861");
                    backColor = Color.valueOf("974861");
                    frontColor = Color.valueOf("ff81a8");
                    hitEffect = Fx.blastExplosion;
                    despawnEffect = Fx.blastExplosion;
                    weaveScale = 3f;
                    weaveMag = 1.5f;
                    velocityRnd = 0.2f;
                }};
            }});
            weapons.addAll(new Weapon("aquarion-large-missiles") {{
                rotate = true;
                x = 0;
                y = -1;
                shadowElevation = 0.1f;
                shadow = 8f;
                rotateSpeed = 1.1f;
                reload = 100;
                mirror = false;
                shootSound = Sounds.shootAlt;
                shoot.shots = 6;
                shoot.shotDelay = 1;
                inaccuracy = 5f;
                shake = 0.25f;
                xRand = 5;
                bullet = new MissileBulletType(4f, 15) {{
                    width = 10f;
                    height = 10f;

                    trailLength = 7;
                    shrinkY = 0.2f;
                    drag = -0.003f;
                    homingRange = 60f;
                    keepVelocity = false;
                    splashDamage = 25;
                    splashDamageRadius = 25;
                    lifetime = 60f;
                    trailColor = Color.valueOf("974861");
                    backColor = Color.valueOf("974861");
                    frontColor = Color.valueOf("ff81a8");
                    hitEffect = Fx.blastExplosion;
                    despawnEffect = Fx.blastExplosion;
                    weaveScale = 2f;
                    weaveMag = 1f;
                    velocityRnd = 0.4f;
                }};
            }});
        }};
        rhombodera = new UnitType("rhombodera") {{
            squareShape = true;
            omniMovement = false;
            rotateMoveFirst = true;
            rotateSpeed = 1f;
            envDisabled = Env.none;
            speed = 0.35f;
            crushDamage = 0.006f;
            constructor = TankUnit::create;
            treadRects = new Rect[]{
                    new Rect(-41f, -62f, 25, 132),
                    new Rect(-74f, 30f, 15, 21)
            };
            hitSize = 22;
            treadPullOffset = 3;
            health = 7800;
            armor = 8;

            weapons.add(new Weapon("aquarion-small-missiles") {{
                rotate = true;
                x = 11;
                y = -8;
                reload = 45;
                shootSound = Sounds.missile;
                shoot.shots = 2;
                inaccuracy = 5f;
                shake = 1;
                bullet = new MissileBulletType(3, 8) {{
                    width = 8f;
                    height = 8f;
                    shrinkY = 0.2f;
                    drag = -0.003f;
                    homingRange = 60f;
                    keepVelocity = false;
                    splashDamage = 15;
                    splashDamageRadius = 12;
                    lifetime = 40f;
                    trailColor = Color.valueOf("974861");
                    backColor = Color.valueOf("974861");
                    frontColor = Color.valueOf("ff81a8");
                    hitEffect = Fx.blastExplosion;
                    despawnEffect = Fx.blastExplosion;
                    weaveScale = 3f;
                    weaveMag = 1.5f;
                    velocityRnd = 0.2f;
                }};
            }});
            weapons.add(new Weapon("aquarion-small-missiles") {{
                rotate = true;
                x = 11;
                y = 9;
                reload = 45;
                shootSound = Sounds.missile;
                shoot.shots = 2;
                inaccuracy = 5f;
                shake = 1;
                bullet = new MissileBulletType(3, 8) {{
                    width = 8f;
                    height = 8f;
                    shrinkY = 0.2f;
                    drag = -0.003f;
                    homingRange = 60f;
                    keepVelocity = false;
                    splashDamage = 15;
                    splashDamageRadius = 12;
                    lifetime = 40f;
                    trailColor = Color.valueOf("974861");
                    backColor = Color.valueOf("974861");
                    frontColor = Color.valueOf("ff81a8");
                    hitEffect = Fx.blastExplosion;
                    despawnEffect = Fx.blastExplosion;
                    weaveScale = 3f;
                    weaveMag = 1.5f;
                    velocityRnd = 0.2f;
                }};
            }});
            weapons.addAll(new Weapon("aquarion-huge-missiles") {{
                rotate = true;
                x = 0;
                y = -3;
                shadowElevation = 0.1f;
                shadow = 8f;
                rotateSpeed = 1.1f;
                reload = 10;
                mirror = false;
                shootSound = Sounds.shootAlt;
                shoot.shots = 2;
                shoot.shotDelay = 1;
                inaccuracy = 5f;
                shake = 0.25f;
                xRand = 7;
                bullet = new MissileBulletType(4f, 20) {{
                    width = 9f;
                    height = 9f;

                    trailLength = 9;
                    shrinkY = 0.2f;
                    drag = -0.003f;
                    homingRange = 60f;
                    keepVelocity = false;
                    splashDamage = 30;
                    splashDamageRadius = 30;
                    lifetime = 75f;
                    trailColor = Color.valueOf("974861");
                    backColor = Color.valueOf("974861");
                    frontColor = Color.valueOf("ff81a8");
                    hitEffect = Fx.blastExplosion;
                    despawnEffect = Fx.blastExplosion;
                    weaveScale = 4f;
                    weaveMag = 1f;
                    velocityRnd = 0.3f;
                }};
            }});
        }};
        parasphendale = new UnitType("parasphendale") {{
            squareShape = true;
            omniMovement = false;
            rotateMoveFirst = true;
            rotateSpeed = 0.8f;
            envDisabled = Env.none;
            speed = 0.25f;
            crushDamage = 0.01f;
            constructor = TankUnit::create;
            treadRects = new Rect[]{
                    new Rect(-82f, -101.5f, 39, 41),
                    new Rect(-115f, -13.5f, 55, 54),
                    new Rect(-60f, 74.5f, 28, 40),
                    new Rect(-81f, -60.5f, 20, 151)
            };
            treadFrames = 151;
            hitSize = 28;
            treadPullOffset = 1;
            health = 10500;
            armor = 12;
            weapons.addAll(new Weapon("aquarion-massive-missiles") {{
                rotate = true;
                x = 0;
                y = -12;
                shadowElevation = 0.2f;
                shadow = 16f;
                rotateSpeed = 0.8f;
                reload = 120;
                mirror = false;
                shootSound = Sounds.missileLarge;
                shake = 2f;
                parts.add(new RegionPart("-missile") {{
                    progress = PartProgress.reload.curve(Interp.pow2In);

                    colorTo = new Color(1f, 1f, 1f, 0f);
                    color = Color.white;
                    mixColorTo = Pal.accent;
                    mixColor = new Color(1f, 1f, 1f, 0f);

                    moves.add(new PartMove(PartProgress.warmup.inv(), 0f, 4f, 0f));
                }});
                bullet = new MissileBulletType(3, 8) {
                    {
                        smokeEffect = Fx.shootSmokeTitan;
                        spawnUnit = new MissileUnitType("parasphendale-missile") {{
                            speed = 6f;
                            maxRange = 15f;
                            health = 200;
                            engineLayer = Layer.effect;
                            homingDelay = 10f;
                            lowAltitude = true;
                            engineSize = 3f;
                            loopSoundVolume = 0.1f;
                            weapons.add(new Weapon() {{
                                shootCone = 360f;
                                mirror = false;
                                reload = 1f;
                                shootOnDeath = true;
                                bullet = new ExplosionBulletType(600, 85f) {{
                                    collidesAir = true;
                                    shootEffect = new ExplosionEffect() {{
                                        lifetime = 50f;
                                        waveStroke = 5f;
                                        waveLife = 8f;
                                        waveColor = Color.white;
                                        sparkColor = smokeColor = Color.valueOf("ff81a8");
                                        waveRad = 40f;
                                        smokeSize = 4f;
                                        smokes = 7;
                                        smokeSizeBase = 0f;
                                        sparks = 10;
                                        sparkRad = 40f;
                                        sparkLen = 6f;
                                        sparkStroke = 2f;
                                    }};
                                }};
                            }});
                        }};
                    }
                };
            }});
        }};
        frost = new UnitType("frost") {{
            hitSize = 10;
            constructor = ElevationMoveUnit::create;
            useEngineElevation = false;
            faceTarget = false;

            shadowElevation = 0.1f;
            hovering = true;
            canDrown = false;
            speed = 2f;
            rotateSpeed = 6;
            engineSize = 2.5f;
            health = 120;
            armor = 0;
            engineOffset = 6;
            abilities.add(new MoveEffectAbility(0f, -7f, Pal.sapBulletBack, Fx.missileTrailShort, 4f) {{
                teamColor = true;
            }});
            for (float f : new float[]{-3f, 3f}) {
                parts.add(new HoverPart() {{
                    x = 4f;
                    y = f;
                    mirror = true;
                    radius = 5f;
                    phase = 90f;
                    stroke = 2f;
                    sides = 5;
                    layerOffset = -0.001f;
                    color = Color.valueOf("d1efff");
                }});
            }
            immunities.add(StatusEffects.freezing);
            weapons.add(new Weapon("aquarion-frost-weapon") {{
                mirror = false;
                x = 0;
                y = -1;
                rotate = true;
                reload = 5;
                shootSound = Sounds.flame;
                recoil = 1f;
                ejectEffect = Fx.none;
                bullet = new BulletType(5f, 15f) {{
                    ammoMultiplier = 3f;
                    hitSize = 7f;
                    pierce = true;
                    pierceBuilding = true;
                    pierceCap = 2;
                    lifetime = 13;
                    statusDuration = 60f * 4;
                    shootEffect = AquaFx.shootSmallCryo;
                    hitEffect = AquaFx.hitCryoSmall;
                    despawnEffect = Fx.none;
                    status = StatusEffects.freezing;
                    keepVelocity = false;
                    hittable = false;
                }};
            }});
        }};
        rime = new UnitType("rime") {{
            hitSize = 15;
            faceTarget = false;

            constructor = ElevationMoveUnit::create;
            useEngineElevation = false;
            shadowElevation = 0.1f;
            hovering = true;
            canDrown = false;
            speed = 1.8f;
            engineSize = 3.5f;
            rotateSpeed = 3.5f;
            health = 320;
            armor = 2;
            engineOffset = 8;
            abilities.add(new MoveEffectAbility(0f, -7f, Pal.sapBulletBack, Fx.missileTrailShort, 4f) {{
                teamColor = true;
            }});
            for (float f : new float[]{-6f, 6f}) {
                parts.add(new HoverPart() {{
                    x = 5f;
                    y = f;
                    mirror = true;
                    radius = 6f;
                    phase = 90f;
                    stroke = 2f;
                    sides = 6;
                    layerOffset = -0.001f;
                    color = Color.valueOf("d1efff");
                }}, new HoverPart() {{
                    x = 5.5f;
                    y = 2;
                    mirror = true;
                    radius = 6f;
                    phase = 90f;
                    stroke = 2f;
                    sides = 6;
                    layerOffset = -0.001f;
                    color = Color.valueOf("d1efff");
                }});
            }
            immunities.add(StatusEffects.freezing);
            weapons.add(new Weapon("aquarion-rime-weapon") {{
                mirror = true;
                flipSprite = true;
                x = 4;
                y = -4;
                lifetime = 30;
                rotate = true;
                reload = 25;
                shootSound = Sounds.laser;
                recoil = 1.5f;
                ejectEffect = Fx.none;
                bullet = new BasicBulletType(2f, 45) {{
                    ammoMultiplier = 3f;
                    hitSize = 7f;
                    lifetime = 45;
                    pierce = true;
                    width = height = 6;
                    pierceBuilding = true;
                    pierceCap = 2;
                    statusDuration = 60f * 4;
                    shootEffect = Fx.shootSmall;
                    hitEffect = AquaFx.hitCryoSmall;
                    despawnEffect = Fx.none;
                    splashDamage = 15;
                    splashDamageRadius = 10;
                    trailLength = 6;
                    frontColor = hitColor = Pal.techBlue;
                    backColor = trailColor = lightColor = Liquids.cryofluid.color;
                    status = StatusEffects.freezing;
                    despawnEffect = new ExplosionEffect() {{
                        waveColor = Color.blue;
                        smokeColor = sparkColor = Pal.techBlue;
                    }};
                    keepVelocity = false;
                    hittable = false;
                }};
            }});
        }};
        verglas = new UnitType("verglas") {{
            hitSize = 18;
            faceTarget = false;

            constructor = ElevationMoveUnit::create;
            useEngineElevation = false;
            shadowElevation = 0.1f;
            hovering = true;
            rotateSpeed = 2.5f;
            canDrown = false;
            speed = 1.2f;
            setEnginesMirror(
                    new UnitEngine() {{
                        x = 9f;
                        y = -14f;
                        engineSize = 4.5f;
                        rotation = 22.5f;
                    }}
            );
            engineSize = 0f;
            health = 1600;
            armor = 7;
            rotateSpeed = 0.9f;
            engineOffset = 8;
            for (float f : new float[]{-9f, 9f}) {
                parts.add(new HoverPart() {{
                    x = 8.5f;
                    y = f;
                    mirror = true;
                    radius = 8f;
                    phase = 120f;
                    stroke = 2.5f;
                    sides = 8;
                    layerOffset = -0.001f;
                    color = Color.valueOf("d1efff");
                }});
            }
            immunities.add(StatusEffects.freezing);
            weapons.add(new Weapon("aquarion-verglas-weapon") {{
                mirror = false;
                x = 0;
                y = -6;
                lifetime = 30;
                rotate = true;
                reload = 100;
                shootSound = Sounds.blaster;
                recoil = 2f;
                ejectEffect = Fx.none;
                bullet = new BasicBulletType(4f, 85) {{
                    ammoMultiplier = 3f;
                    hitSize = 7f;
                    lifetime = 70;
                    pierce = true;
                    width = height = 12;
                    pierceBuilding = true;
                    pierceCap = 2;
                    sprite = "circle-bullet";

                    statusDuration = 60f * 6;
                    shootEffect = Fx.shootBig2;
                    despawnEffect = Fx.none;
                    splashDamage = 40;
                    splashDamageRadius = 35;
                    trailLength = 12;
                    bulletInterval = 5;
                    hitEffect = despawnEffect = new Effect(45f * 1.2f, 250f, e -> {
                        color(Liquids.cryofluid.color, 0.55f);

                        randLenVectors(e.id, 12, 25f, (x, y) -> {
                            Fill.circle(e.x + x, e.y + y, 4f * Mathf.clamp(e.fin() / 0.1f) * Mathf.clamp(e.fout() / 0.1f));
                        });
                    });
                    trailInterval = 5;
                    trailEffect = new Effect(45f * 1.2f, 250f, e -> {
                        color(Liquids.cryofluid.color, 0.55f);
                        randLenVectors(e.id, 12, 25f, (x, y) -> {
                            Fill.circle(e.x + x, e.y + y, 4f * Mathf.clamp(e.fin() / 0.1f) * Mathf.clamp(e.fout() / 0.1f));
                        });
                    });
                    intervalBullet = new EmptyBulletType() {{
                        fragBullets = 3;
                        fragBullet = new EmptyBulletType() {{
                            lifetime = 35f;
                            bulletInterval = 10f;
                            loopSound = spray;
                            loopSoundVolume = 0.02f;
                            hitEffect = despawnEffect = Fx.none;
                            intervalBullet = new EmptyBulletType() {{
                                splashDamage = 20f;
                                collidesGround = true;
                                collidesAir = true;
                                collides = false;
                                hitEffect = Fx.none;
                                pierce = true;
                                despawnEffect = Fx.none;
                                instantDisappear = true;
                                splashDamageRadius = 20f;
                                statusDuration = 60 * 5f;
                                status = StatusEffects.freezing;
                                buildingDamageMultiplier = 0.2f;
                            }};
                        }};
                    }};
                    frontColor = hitColor = Color.white;
                    backColor = trailColor = lightColor = Pal.techBlue;
                    status = StatusEffects.freezing;
                    keepVelocity = false;
                }};
            }});
        }};
        glaciate = new UnitType("glaciate") {{
            hitSize = 22;
            constructor = ElevationMoveUnit::create;
            useEngineElevation = false;
            shadowElevation = 0.15f;
            hovering = true;
            canDrown = false;
            speed = 1.6f;
            drag = 0.4f;
            accel = 0.45f;
            rotateSpeed = 0.85f;
            setEnginesMirror(
                    new UnitEngine() {{
                        x = 13f;
                        y = -14f;
                        engineSize = 4.5f;
                        rotation = 22.5f;
                    }}
            );
            engineSize = 0f;
            health = 9500;
            armor = 12;
            faceTarget = false;

            engineOffset = 8;
            for (float f : new float[]{-13f, 11f}) {
                parts.add(new HoverPart() {{
                    x = 13.5f;
                    y = f;
                    mirror = true;
                    radius = 8f;
                    phase = 120f;
                    stroke = 2.5f;
                    sides = 8;
                    layerOffset = -0.001f;
                    color = Color.valueOf("d1efff");
                }});
            }
            immunities.add(StatusEffects.freezing);
            weapons.add(new Weapon("aquarion-glaciate-weapon") {{
                mirror = false;
                x = 0;
                y = -8;
                rotate = true;
                reload = 8.5f;
                rotateSpeed = 1.5f;
                shootSound = Sounds.flame;
                recoil = 1f;
                ejectEffect = Fx.none;
                bullet = new BulletType(8f, 55) {{
                    ammoMultiplier = 3f;
                    hitSize = 9f;
                    lifetime = 12;
                    pierce = true;
                    pierceBuilding = true;
                    pierceCap = 3;
                    statusDuration = 60f * 8;
                    shootEffect = AquaFx.shootBigCryo;
                    despawnEffect = Fx.none;
                    hitEffect = AquaFx.hitCryoSmall;
                    splashDamage = 40;
                    splashDamageRadius = 35;
                    bulletInterval = 5;
                    status = StatusEffects.freezing;
                    keepVelocity = false;
                }};
            }});
        }};
        permafrost = new UnitType("permafrost") {{
            hitSize = 28;
            constructor = ElevationMoveUnit::create;
            useEngineElevation = false;
            shadowElevation = 0.2f;
            faceTarget = false;
            rotateSpeed = 0.8f;
            hovering = true;
            canDrown = false;
            speed = 0.7f;
            drag = 0.3f;
            accel = 0.35f;
            setEnginesMirror(
                    new UnitEngine() {{
                        x = 15f;
                        y = -18f;
                        engineSize = 4.5f;
                        rotation = 22.5f;
                    }}
            );
            engineSize = 0f;
            health = 22000;
            deathExplosionEffect = AquaFx.PermafrostExplosion;
            armor = 18;
            engineOffset = 8;
            for (float f : new float[]{-15f, 14f}) {
                parts.add(new HoverPart() {{
                    x = 15.5f;
                    y = f;
                    mirror = true;
                    radius = 12f;
                    phase = 120f;
                    stroke = 2.5f;
                    sides = 8;
                    layerOffset = -0.001f;
                    color = Color.valueOf("d1efff");
                }}, new HoverPart() {{
                    x = 16.5f;
                    y = 7;
                    mirror = true;
                    radius = 12f;
                    phase = 120f;
                    stroke = 2.5f;
                    sides = 8;
                    layerOffset = -0.001f;
                    color = Color.valueOf("d1efff");
                }});
            }
            immunities.add(StatusEffects.freezing);
            weapons.add(new Weapon("aquarion-permafrost-weapon") {{
                mirror = false;
                x = 0;
                y = -12;
                rotate = true;
                continuous = true;
                alwaysContinuous = true;

                rotateSpeed = 0.9f;
                shootSound = Sounds.torch;
                recoil = 1f;
                ejectEffect = Fx.none;
                bullet = new ContinuousFlameBulletType() {{
                    lightStroke = 35;
                    divisions = 45;
                    width = 4.5f;
                    drawFlare = false;
                    length = 170;
                    pierce = true;
                    pierceBuilding = true;
                    pierceCap = 3;
                    pierceBuilding = true;
                    damage = 75;
                    damageInterval = 5;
                    colors = new Color[]{Color.valueOf("465ab850"), Color.valueOf("66a6d290"), Color.valueOf("98d5ff"), Color.valueOf("d1efff")};
                }};
            }}, new Weapon() {{
                controllable = false;
                shootOnDeath = true;
                reload = 0;
                shootCone = 360;
                bullet = new ExplosionBulletType() {{
                    killShooter = true;
                    splashDamagePierce = true;
                    splashDamage = 12000;
                    splashDamageRadius = 80 * 4;
                    fragBullets = 20;
                    fragOffsetMax = 300;
                    fragOffsetMin = 30;
                    fragBullet = new EmptyBulletType() {{
                        instantDisappear = true;
                        hitEffect = despawnEffect = Fx.none;
                        fragBullets = 8;
                        fragOffsetMax = 35;
                        fragOffsetMin = 0;
                        fragBullet = new EmptyBulletType() {{
                            hitEffect = despawnEffect = new Effect(240f * 1.4f, 250f, e -> {
                                color(Liquids.cryofluid.color, 0.55f);
                                randLenVectors(e.id, 19, 40f, (x, y) -> {
                                    Fill.circle(e.x + x, e.y + y, 8.5f * Mathf.clamp(e.fin() / 0.1f) * Mathf.clamp(e.fout() / 0.1f));
                                });
                                fragBullet = new EmptyBulletType() {{
                                    lifetime = 240f;
                                    bulletInterval = 10f;
                                    loopSound = spray;
                                    loopSoundVolume = 0.02f;
                                    hitEffect = despawnEffect = Fx.none;
                                    intervalBullet = new EmptyBulletType() {{
                                        splashDamage = 80f;
                                        collidesGround = true;
                                        collidesAir = true;
                                        collides = false;
                                        hitEffect = Fx.none;
                                        pierce = true;
                                        despawnEffect = Fx.none;
                                        instantDisappear = true;
                                        splashDamageRadius = 45f;
                                        statusDuration = 60 * 10f;
                                        status = StatusEffects.freezing;
                                        buildingDamageMultiplier = 0.3f;
                                    }};
                                }};
                            });


                        }};
                    }};
                }};
            }});
        }};
        cog = new UnitType("cog") {{
            health = 110;
            armor = 2;
            lowAltitude = true;
            flying = true;
            constructor = UnitEntity::create;
            speed = 2.2f;
            rotateSpeed = 3f;
            accel = 0.08f;
            drag = 0.04f;
            buildSpeed = 0.25f;
            targetFlags = new BlockFlag[]{BlockFlag.turret, null};
            hitSize = 9;
            engineOffset = 5f;
            abilities.add(new LightningFieldAbility(35f, 110f, 110f) {{
                statusDuration = 60f * 6f;
                status = AquaStatuses.ionized;
                color = AquaStatuses.ionized.color;
                sectors = 0;
                effectRadius = 0.5f;
                maxTargets = 3;
            }});
        }};
        tenon = new UnitType("tenon") {{
            health = 360;
            armor = 3;
            lowAltitude = true;
            flying = true;
            constructor = UnitEntity::create;
            speed = 2.3f;
            rotateSpeed = 2.2f;
            accel = 0.08f;
            drag = 0.016f;
            buildSpeed = 0.4f;
            targetFlags = new BlockFlag[]{BlockFlag.turret, null};
            hitSize = 12;
            engineOffset = 7f;
            abilities.add(new LightningFieldAbility(50f, 90f, 135f) {{
                statusDuration = 60f * 8f;
                status = AquaStatuses.ionized;
                color = AquaStatuses.ionized.color;
                effectRadius = 1f;
                sectors = 3;
                maxTargets = 4;
            }});
            weapons.add(new Weapon() {{
                x = 0;
                y = 0;
                rotate = false;
                mirror = false;
                reload = 60;
                velocityRnd = 0.1f;
                inaccuracy = 1f;
                bullet = new FlakBulletType() {{
                    lifetime = 35f;
                    sprite = "missile-large";
                    collidesGround = collidesAir = true;
                    explodeRange = 40f;
                    width = height = 4.5f;
                    frontColor = Color.white;
                    lightColor = backColor = trailColor = hitColor = Pal.accent;
                    hitEffect = new ExplosionEffect() {{
                        smokeColor = Pal.accentBack;
                        waveColor = Color.white;
                        sparkColor = Pal.accent;
                        smokes = 8;
                    }};
                    splashDamageRadius = 30;
                    splashDamage = 10;
                    fragBullets = 10;
                    homingPower = 0.05f;
                    weaveScale = 8f;
                    weaveMag = 1f;
                    fragBullet = new ArtilleryBulletType(2.3f, 5) {{
                        lifetime = 15;
                        frontColor = Pal.accent;
                        backColor = lightColor = hitColor = Pal.accentBack;
                        width = 7;
                        homingPower = 0.2f;
                    }};
                }};
            }});
        }};
        assembly = new UnitType("assembly") {{
            health = 850;
            armor = 5;
            lowAltitude = true;
            flying = true;
            constructor = UnitEntity::create;
            speed = 1.8f;
            rotateSpeed = 1.9f;
            accel = 0.04f;
            drag = 0.08f;
            buildSpeed = 1.2f;
            targetFlags = new BlockFlag[]{BlockFlag.turret, null};
            hitSize = 16;
            engineOffset = 8f;
            abilities.add(new LightningFieldAbility(50f, 140f, 180f) {{
                statusDuration = 60f * 12f;
                status = AquaStatuses.ionized;
                color = AquaStatuses.ionized.color;
                effectRadius = 1f;
                sectors = 5;
                maxTargets = 6;
            }});
            weapons.add(new Weapon() {{
                x = 0;
                y = 0;
                rotate = false;
                mirror = false;
                reload = 10;
                velocityRnd = 0.1f;
                inaccuracy = 5f;
                bullet = new BombBulletType() {{
                    lifetime = 10f;
                    sprite = "missile-large";
                    collidesGround = true;
                    width = height = 4.5f;
                    frontColor = Color.white;
                    lightColor = backColor = trailColor = hitColor = Pal.accent;
                    hitEffect = new ExplosionEffect() {{
                        smokeColor = Pal.accentBack;
                        waveColor = Color.white;
                        sparkColor = Pal.accent;
                        smokeRad = 14;
                        waveRad = 17;
                        smokes = 12;
                    }};
                    splashDamageRadius = 8 * 6f;
                    splashDamage = 45;
                }};
            }});
        }};
        fabricantDrone = new UnitType("fabricant-drone") {{
            constructor = TimedKillUnit::create;
            useUnitCap = false;
            flying = true;
            allowedInPayloads = false;
            lifetime = 60*60f;
            speed = 2.7f;
            rotateSpeed = 2.5f;
            controller = u -> new DroneAI();
            lowAltitude = true;
            playerControllable = false;
            physics = false;
            bounded = false;
            trailLength = 7;
            targetPriority = -1;
            health = 250;

            armor = 4;
            weapons.add(new Weapon("large-weapon") {{
                reload = 13f;
                x = 4f;
                y = 2f;
                top = false;
                shootStatus = StatusEffects.melting;
                shootStatusDuration = 10;
                ejectEffect = Fx.casing1;
                bullet = new BasicBulletType(2.5f, 9) {{
                    width = 7f;
                    height = 9f;
                    lifetime = 60f;
                }};
            }});
        }};
        fabricant = new UnitType("fabricant") {{
                health = 1950;
                armor = 8;
                lowAltitude = false;
                flying = true;
                constructor = UnitEntity::create;
                speed = 0.9f;
                rotateSpeed = 1f;
                accel = 0.04f;
                drag = 0.08f;
                buildSpeed = 3f;
                targetFlags = new BlockFlag[]{BlockFlag.turret, null};
                hitSize = 20;
                engineOffset = 8f;
                weapons.add(new Weapon() {{
                        x = 0;
                        y = 0;
                        alwaysShooting = true;
                        rotate = false;
                        mirror = false;
                        reload = 180;
                        velocityRnd = 0.1f;
                        inaccuracy = 5f;
                        bullet = new DroneSpawnerBulletType() {{
                            drone = fabricantDrone;
                        }};
                    }
                });
            }
        };
    }};