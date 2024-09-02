package aquarion.units;

import aquarion.type.AquaBasicBulletType;
import aquarion.type.AquaBulletType;
import aquarion.type.GerbUnitType;
import aquarion.world.graphics.AquaFx;
import arc.graphics.Color;
import arc.math.Interp;
import mindustry.ai.types.BuilderAI;
import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.part.RegionPart;
import mindustry.gen.LegsUnit;
import mindustry.gen.Sounds;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import aquarion.world.graphics.AquaPal;
import mindustry.type.Weapon;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.world.meta.Env;

import static mindustry.Vars.tilePayload;

public class AquaUnitTypes {
    public static UnitType cull, glean, gerbTest;

    public static void loadContent() {
        cull = new UnitType("cull") {{
            constructor = UnitEntity::create;
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
            speed = 0.85f;
            legMinLength = 0.9f;
            legMaxLength = 1.1f;
            hitSize = 12;
            legLength = 11;
            legPairOffset = 1;
            legExtension = 0.5f;
            lockLegBase = true;
            variants = 7;
            rotateSpeed = 6;
            legBaseOffset = 2;
            kineticResistance = 0.2f;
            heatResistance = -0.2f;
            concussionResistance = 0.4f;
            energyResistance = 0.2f;
            allowLegStep = false;
            drownTimeMultiplier = 9000; //too lazy to implement java
            mechStepParticles = true;
            weapons.add(new Weapon("aquarion-infantry-rifle"){{
                        mirror = false;
                        x = 6;
                        y = -1f;
                        recoil = 2f;
                        outlineColor = AquaPal.tantDarkerTone;
                        recoilTime = 15f;
                        rotate = true;
                        rotateSpeed = 1.2f;
                        reload = 20;
                        range = 90;
                        bullet = new AquaBasicBulletType(2.5f, 9){{
                         width = 9f;
                          height = 12f;
                          shootEffect = AquaFx.shootLong;
                          shootSound = Sounds.blaster;
                          trailLength = 9;
                          trailWidth = 2;
                          lifetime = 45f;
                          maxRange = 80;
                          kinetic = true;
                          frontColor = hitColor = lightColor = Color.white;
                          backColor = trailColor = Pal.techBlue;
                      }};
                }}
            );
        }};
    }
}
