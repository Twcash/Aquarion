package aquarion.units;

import aquarion.gen.EntityRegistry;
import aquarion.type.*;
import aquarion.world.graphics.AquaFx;
import arc.graphics.Color;
import arc.math.Interp;
import mindustry.ai.types.BuilderAI;
import mindustry.ai.types.CargoAI;
import mindustry.content.Fx;
import mindustry.entities.abilities.MoveEffectAbility;
import mindustry.entities.abilities.SpawnDeathAbility;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootHelix;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import aquarion.world.graphics.AquaPal;
import mindustry.type.Weapon;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.world.meta.Env;

import static aquarion.units.AquaWrecks.*;
import static mindustry.Vars.tilePayload;

public class AquaUnitTypes {
    //core units and transport

    public static UnitType cull, glean, rivulet,
    //gerb
     gerbTest,
    // gerb mechanized
    reap, maime,

    //messenger tree
     messenger, ambassador, consul, legate, monarch,

     //steward tree
     steward, curator, custodian, caretaker, warden,

     // goss tree
     goss, heed, effect, consummate, efectuate,

     //zoarcid treeb
     zoarcid, anguilli, cyprin, pycogen, batoid;

    public static void loadContent() {
        messenger = EntityRegistry.content("messenger", MechUnit.class, name -> new MechanicalUnitType(name){{
        speed = 0.65f;

        rotateSpeed = 2f;
        rotateMoveFirst = true;
        mech = true;
        envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        constructor = MechUnit::create;
        mechLegColor = AquaPal.tantDarkestTone;
            abilities.add(
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect(){{
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
                    }}, 90f, .6f){{
                    }},
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect(){{
                        particles = 3;
                        sizeFrom = 0;
                        sizeTo = 4;
                        lifetime =  80;
                        layer = 80;
                        colorFrom = Color.valueOf("262323");
                        colorTo = Color.valueOf("746f6f10");
                    }}, 15f, .4f){{
                    }}, new SpawnDeathAbility(messengerWreck, 0, 1){{ randAmount = 1;}});
            weapons.add(new Weapon("aquarion-messenger-weapon"){{
                            mirror = true;
                            alternate = true;
                            recoil = 2f;
                            x = 0;
                            outlineColor = AquaPal.tantDarkerTone;
                            recoilTime = 15f;
                            reload = 40;
                            layerOffset = -0.001f;
                            shootX = 4;
                            range = 90;
                            bullet = new ArtilleryBulletType(2f, 30){{
                                knockback = -.5f;
                                splashDamage = 30;
                                splashDamageRadius = 18;
                                width = height = 9f;;
                                shrinkX = 0.6f;
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
                                frontColor = hitColor = lightColor = Color.white;
                                backColor = trailColor = Pal.techBlue;
                            }};
                        }});
        }});
        goss = EntityRegistry.content("goss", Unitc.class, name -> new MechanicalUnitType(name){{
            constructor = UnitEntity::create;
            engineSize = 0f;
            groundLayer = 90;
            outlineColor = Color.valueOf("232826");
            speed = 2.5f;
            accel = 0.9f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            health = 270;
            hitSize = 7;
            abilities.add(
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect(){{
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
                    }}, 90f, .6f){{
                    }},
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect(){{
                        particles = 3;
                        sizeFrom = 0;
                        sizeTo = 4;
                        lifetime =  80;
                        layer = 80;
                        colorFrom = Color.valueOf("262323");
                        colorTo = Color.valueOf("746f6f10");
                    }}, 15f, .4f){{
                    }}, new SpawnDeathAbility(gossWreck, 0, 1){{ randAmount = 1;}});
            flying = true;
            lowAltitude = true;
            //damn you intellij
            drag = (float) 0.75f;

            weapons.add(new Weapon("aquarion-goss-weapon"){{
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
                bullet = new BasicBulletType(1.5f, 15, "missile-large"){{
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
        zoarcid = EntityRegistry.content("zoarcid", Unitc.class, name -> new MechanicalUnitType(name){{
            engineSize = 0f;
            groundLayer = 90;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            hitSize = 8;
            rotateSpeed = 3;
            lowAltitude = true;
            flying = true;
            outlineColor = Color.valueOf("232826");
            speed = 2.5f;
            accel = 0.9f;
            drag = 0.75f;
            abilities.add(
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect(){{
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
                    }}, 90f, .6f){{
                    }},
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect(){{
                        particles = 3;
                        sizeFrom = 0;
                        sizeTo = 4;
                        lifetime =  80;
                        layer = 80;
                        colorFrom = Color.valueOf("262323");
                        colorTo = Color.valueOf("746f6f10");
                    }}, 15f, .4f){{
                    }}, new SpawnDeathAbility(zoarcidWreck, 0, 1){{ randAmount = 1;}},
                    new MoveEffectAbility(0, -1, Pal.sapBulletBack, AquaFx.t1TrailZoarcid, 1));
            weapons.add(new Weapon("aquarion-zoarcid-weapon"){{
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
                bullet = new BasicBulletType(1f, 30){{
                    range = 60;
                    hitSize = 5;
                    width = 12;
                    height = 19;
                    frontColor = Color.white;
                    backColor = Pal.techBlue;
                    weaveMag = 0;
                    weaveScale = 0;
                    lifetime = 45;
                }};
                }},
                    new Weapon("aquarion-zoarcid-weapon"){{
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
                        bullet = new BasicBulletType(1f, 30){{
                            range = 60;
                            hitSize = 5;
                            width = 12;
                            height = 19;
                            frontColor = Color.white;
                            backColor = Pal.techBlue;
                            weaveMag = 0;
                            weaveScale = 0;
                            lifetime = 45;
                        }};
                        rotateSpeed = 4.5f;
                    }});
        }});
        // steward tree
        steward = EntityRegistry.content("steward", MechUnit.class, name -> new MechanicalUnitType(name){{
            rotateMoveFirst = true;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            constructor = MechUnit::create;
            mechLegColor = AquaPal.tantDarkestTone;
            drawCell = false;
            speed = 0.9f;
            rotateSpeed = 4;
            abilities.add(
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect(){{
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
                    }}, 90f, .6f){{
                    }},
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect(){{
                        particles = 3;
                        sizeFrom = 0;
                        sizeTo = 4;
                        lifetime =  80;
                        layer = 80;
                        colorFrom = Color.valueOf("262323");
                        colorTo = Color.valueOf("746f6f10");
                    }}, 15f, .4f){{
                    }}, new SpawnDeathAbility(stewardWreck, 0, 1){{ randAmount = 1;}});
            weapons.add(new Weapon("aquarion-steward-weapon"){{
                mirror = true;
                alternate = true;
                recoil = 1f;
                x = 0;
                outlineColor = AquaPal.tantDarkerTone;
                recoilTime = 5f;
                reload = 10;
                top = false;
                shootX = 4;
                range = 60;
                bullet = new LaserBoltBulletType(3f, 10){{
                    width = 2f;;
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
        cull = EntityRegistry.content("cull", Unitc.class, name -> new MechanicalUnitType(name){{
            outlines = false;
            hittable = isEnemy = targetable = drawCell = allowedInPayloads = drawBody = false;
            payloadCapacity = (2 * 2) * tilePayload;
            outlineColor = AquaPal.tantDarkerTone;
            lowAltitude = flying = coreUnitDock = true;
            aiController = BuilderAI::new;
            healColor = Pal.accent;
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
                        fractionRepairSpeed = 0.02f;
                        widthSinMag = 0.11f;
                        healColor = laserColor = Pal.accent;
                        bullet = new BulletType() {{
                            maxRange = 60;
                        }};
                    }}
            );
        }});
        glean = EntityRegistry.content("glean", Unitc.class, name -> new MechanicalUnitType(name){{
            hittable = isEnemy = targetable = drawCell = allowedInPayloads = drawBody = false;
            payloadCapacity = (2 * 2) * tilePayload;
            outlineColor = AquaPal.tantDarkerTone;
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
        gerbTest = EntityRegistry.content("lightInfantry", LegsUnit.class, name -> new GerbUnitType(name){{
            health = 210;
            armor = 1;
            legCount = 6;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            legStraightness = 0.3f;
            baseLegStraightness = 0.5f;
            lockLegBase = true;
            speed = 0.45f;
            abilities.add(
                    new DamageStateEffectAbility(0f, -7f, Pal.sapBulletBack, new ParticleEffect(){{
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
                    }}, 90f, .3f){{
                    }}, new SpawnDeathAbility(InfantryGerbCorpse, 0, 1){{ randAmount = 1;}});
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
            weapons.add(new Weapon("aquarion-infantry-rifle"){{
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
                        bullet = new BasicBulletType(2.5f, 9){{
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
        }});
        reap = EntityRegistry.content("reap", LegsUnit.class, name -> new MechanicalUnitType(name){{
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
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            leg = true;
            constructor = LegsUnit::create;
            legMinLength = 0.9f;
            legMaxLength = 1.1f;
            abilities.add(
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect(){{
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
                    }}, 90f, .6f){{
                    }},
                    new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect(){{
                        particles = 3;
                        sizeFrom = 0;
                        sizeTo = 4;
                        lifetime =  80;
                        layer = 80;
                        colorFrom = Color.valueOf("262323");
                        colorTo = Color.valueOf("746f6f10");
                    }}, 15f, .4f){{
                    }}, new SpawnDeathAbility(reapWreck, 0, 1){{ randAmount = 1;}});
            weapons.add(new Weapon("aquarion-reap-weapon"){
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
                }});
            }});
        maime = EntityRegistry.content("maime", Unitc.class, name -> new MechanicalUnitType(name){{
                hitSize = 18;
                speed = 0.5f;
                flying = true;
                health = 540;
                armor = 4;
                lowAltitude = true;
                envEnabled|= Env.terrestrial | Env.underwater;
                envDisabled = Env.none;
                rotateSpeed = 4;
                accel = 0.9f;
                drag = 0.1f;
                abilities.add(
                        new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect(){{
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
                        }}, 90f, .6f){{
                        }},
                        new DamageStateEffectAbility(0f, 0f, Pal.sapBulletBack, new ParticleEffect(){{
                            particles = 3;
                            sizeFrom = 0;
                            sizeTo = 4;
                            lifetime =  80;
                            layer = 80;
                            colorFrom = Color.valueOf("262323");
                            colorTo = Color.valueOf("746f6f10");
                        }}, 15f, .4f){{
                        }}, new SpawnDeathAbility(maimeWreck, 0, 1){{ randAmount = 1;}});
                weapons.add(new Weapon(){
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
                    }});
            }});
        rivulet = EntityRegistry.content("rivulet", BuildingTetherc.class, name -> new UnitType(name){{

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
                    envDisabled = 0;
                    outlineColor = AquaPal.tantDarkestTone;
                    payloadCapacity = 0f;
                    health = 200f;
                    drawCell = false;
                    engineSize = 0;
                constructor = BuildingTetherPayloadUnit::create;
                flying = true;
                    itemCapacity = 100;
                }});
        }};