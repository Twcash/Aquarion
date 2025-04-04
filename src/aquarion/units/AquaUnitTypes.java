package aquarion.units;

import aquarion.gen.EntityRegistry;
import aquarion.type.*;
import aquarion.world.AquaGroundAI;
import aquarion.world.graphics.AquaFx;
import arc.graphics.Color;
import arc.math.Interp;
import arc.struct.Seq;
import ent.anno.Annotations;
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
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootSine;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import aquarion.world.graphics.AquaPal;
import mindustry.type.Weapon;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.electrum;
import static aquarion.units.AquaWrecks.*;
import static mindustry.Vars.tilePayload;

public class AquaUnitTypes {
    //core units and transport

    public static UnitType

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
    public static @Annotations.EntityDef({Unitc.class, Legsc.class})GerbUnitType gerbTest;
    //legUnits mechanical
    public static @Annotations.EntityDef({Unitc.class, Legsc.class}) MechanicalUnitType reap;
    // mechs
    public static @Annotations.EntityDef({Unitc.class, Mechc.class}) MechanicalUnitType messenger, steward;
    // flying
    public static @Annotations.EntityDef({Unitc.class, Unitc.class}) MechanicalUnitType zoarcid, anguilli, cyprin, pycogen, batoid, goss, heed, effect, consummate, efectuate,
    cull, glean, maime;
    //non normal flying
    public static @Annotations.EntityDef({Unitc.class, Unitc.class}) UnitType rivulet;
    public static void loadContent() {
        messenger = EntityRegistry.content("messenger", MechUnit.class, name -> new MechanicalUnitType(name) {{
            speed = 0.65f;
            health = 550;
            armor = 1;
            rotateSpeed = 2f;
            rotateMoveFirst = true;
            mech = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            constructor = MechUnit::create;
            mechLegColor = AquaPal.tantDarkestTone;
            abilities.add(new ShieldRegenFieldAbility(25f, 200f, 60f * 5, 90f),

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
                bullet = new ArtilleryBulletType(2f, 30) {{
                    knockback = -.5f;
                    splashDamage = 45;
                    splashDamageRadius = 8 * 3.2f;
                    width = height = 9f;
                    shrinkX = 0.6f;
                    shrinkY = 0.1f;
                    collidesGround = true;
                    shootEffect = Fx.shootSmall;
                    shootSound = Sounds.laser;
                    smokeEffect = Fx.shootBigSmoke;
                    trailLength = 5;
                    trailWidth = 2;
                    lifetime = 45f;
                    maxRange = 80;
                    trailEffect = Fx.none;
                    collidesTiles = true;
                    frontColor = hitColor = lightColor = Color.white;
                    backColor = trailColor = Pal.techBlue;
                }};
            }});
        }});
        ambassador = EntityRegistry.content("ambassador", Legsc.class, name -> new MechanicalUnitType(name) {{
            constructor = LegsUnit::create;
            health = 1250;
            armor = 12;
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
            speed = 1.2f;
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
        }});
        consul = EntityRegistry.content("consul", Legsc.class, name -> {
            return new MechanicalUnitType(name) {{
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
                    layerOffset = -0.0001f;
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
        });
        goss = EntityRegistry.content("goss", Unitc.class, name -> new MechanicalUnitType(name) {{
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
        }});
        zoarcid = EntityRegistry.content("zoarcid", Unitc.class, name -> new MechanicalUnitType(name) {{
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
        }});
        anguilli = EntityRegistry.content("anguilli", Unitc.class, name -> new MechanicalUnitType(name) {
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
        });

        // steward tree
        steward = EntityRegistry.content("steward", MechUnit.class, name -> new MechanicalUnitType(name) {{
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
        }});
        cull = EntityRegistry.content("cull", Unitc.class, name -> new MechanicalUnitType(name) {{
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
        }});
        glean = EntityRegistry.content("glean", Unitc.class, name -> new MechanicalUnitType(name) {{
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
        }});
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
        reap = EntityRegistry.content("reap", LegsUnit.class, name -> new MechanicalUnitType(name) {{
            speed = 0.25f;
            lockLegBase = true;
            legLength = 8;
            legCount = 4;
            legBaseOffset = 7;
            rotateMoveFirst = true;
            rotateSpeed = 0.9f;
            health = 350;
            legPhysicsLayer = false;
            groundLayer = Layer.groundUnit;
            armor = 2;
            hitSize = 15;
            mechStepParticles = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            leg = true;
            constructor = LegsUnit::create;
            legMinLength = 0.9f;
            legMaxLength = 1.1f;
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
                    }}, new SpawnDeathAbility(reapWreck, 0, 1) {{
                        randAmount = 1;
                    }});
            weapons.add(new Weapon("aquarion-reap-weapon") {
                {
                    mirror = false;
                    x = 0;
                    y = 0f;
                    recoil = 2.5f;
                    layerOffset = 1f;
                    recoilTime = 35f;
                    rotate = true;
                    rotateSpeed = 1.5f;
                    inaccuracy = 2;
                    reload = 15;
                    range = 50;
                    shoot = new ShootAlternate(3.5f);
                    bullet = new BasicBulletType(2f, 10) {{
                        width = 9f;
                        height = 12f;
                        shootEffect = AquaFx.shootLong;
                        shootSound = Sounds.blaster;
                        trailLength = 9;
                        trailWidth = 2;
                        lifetime = 45f;
                        maxRange = 60;
                        frontColor = hitColor = lightColor = Color.white;
                        backColor = trailColor = Pal.techBlue;
                    }};
                }
            });
        }});
        maime = EntityRegistry.content("maime", Unitc.class, name -> new MechanicalUnitType(name) {{
            hitSize = 18;
            speed = 0.5f;
            flying = true;
            health = 540;
            armor = 4;
            lowAltitude = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            constructor = UnitEntity::create;
            rotateSpeed = 4;
            accel = 0.9f;
            drag = 0.1f;
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
                    }}, new SpawnDeathAbility(maimeWreck, 0, 1) {{
                        randAmount = 1;
                    }});
            weapons.add(new Weapon() {
                {
                    mirror = true;
                    x = 7;
                    y = 5f;
                    recoilTime = 35f;
                    rotate = false;
                    inaccuracy = 8;
                    reload = 5;
                    range = 45;
                    bullet = new MissileBulletType(4f, 5) {{
                        width = 9f;
                        height = 12f;
                        weaveMag = 6;
                        weaveScale = 10;
                        homingPower = 0.1f;
                        homingDelay = 10;
                        shootSound = Sounds.blaster;
                        trailLength = 12;
                        trailWidth = 2.5f;
                        lifetime = 45f;
                        maxRange = 50;
                        frontColor = hitColor = lightColor = Color.white;
                        backColor = trailColor = Pal.techBlue;
                    }};
                }
            });
        }});
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
    }}