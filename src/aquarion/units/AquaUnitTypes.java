package aquarion.units;

import aquarion.type.*;
import aquarion.world.graphics.AquaFx;
import arc.graphics.Color;
import arc.math.Interp;
import mindustry.ai.types.BuilderAI;
import mindustry.content.Fx;
import mindustry.entities.abilities.MoveEffectAbility;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.part.RegionPart;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import aquarion.world.graphics.AquaPal;
import mindustry.type.Weapon;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.world.meta.Env;

import static mindustry.Vars.tilePayload;

public class AquaUnitTypes {
    //core units and transport
    public static UnitType cull, glean,
    //gerb
     gerbTest,

    //messenger tree
     messenger, ambassador, consul, legate, monarch,

     //steward tree
     steward, curator, custodian, caretaker, warden,

     // goss tree
     goss, heed, effect, consummate, efectuate,

     //zoarcid treeb
     zoarcid, anguilli, cyprin, pycogen, batoid;

    public static void loadContent() {
        messenger = new MechanicalUnitType("messenger"){{
        speed = 0.4f;
        rotateSpeed = 1.8f;
        rotateMoveFirst = true;
        constructor = MechUnit::create;
        mechLegColor = AquaPal.tantDarkestTone;
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
        }};
        goss = new MechanicalUnitType("goss"){{
            constructor = UnitEntity::create;
            engineSize = 0.6f;
            groundLayer = 90;
            speed = 0.9f;
            accel = 0.9f;
            flying = true;
            //damn you intellij
            drag = (float) 0.75f;
        }};
        cull = new UnitType("cull") {{
            constructor = UnitEntity::create;
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
            envDisabled |= Env.spores | Env.scorching;
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
        }};
        glean = new UnitType("glean") {{
            constructor = UnitEntity::create;
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
            envDisabled |= Env.spores | Env.scorching;
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
            health = 240;
            armor = 1;
            constructor = LegsUnit::create;
            legCount = 6;
            legStraightness = 0.3f;
            baseLegStraightness = 0.5f;
            legContinuousMove = true;
            lockLegBase = true;
            speed = 0.85f;
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
                    }});
            legMinLength = 0.9f;
            legMaxLength = 1.1f;
            hitSize = 12;
            legMoveSpace = 1.2f;
            legLength = 9;
            legPairOffset = 1;
            /*veryDamagedEffect = new ParticleEffect(){{
                particles = 5;
                sizeFrom = 6;
                sizeTo = 0;
                sizeInterp = Interp.pow10Out;
                length = 0;
                baseLength = 6;
                colorFrom = Color.valueOf("65d453");
                colorTo = Color.valueOf("48903c");
            }};*/
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
        }};
    }
}
