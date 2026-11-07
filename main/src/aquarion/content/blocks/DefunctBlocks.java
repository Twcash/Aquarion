package aquarion.content.blocks;

import aquarion.content.AquaSounds;
import aquarion.world.blocks.core.InfomaticBlock;
import aquarion.world.blocks.environment.DefunctCheckpointBlock;
import aquarion.world.blocks.units.DefunctUnitBlock;
import aquarion.world.blocks.units.DefunctBeacon;
import aquarion.world.graphics.AquaFx;
import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import mindustry.content.Fx;
import mindustry.content.Planets;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.Effect;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.sandbox.*;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.meta.BuildVisibility;
import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaLiquids.methane;
import static aquarion.content.AquaPlanets.*;
import static aquarion.content.AquaUnitTypes.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.water;
import static mindustry.gen.Sounds.*;
import static mindustry.type.ItemStack.with;

/** Content belonging to the Defunct faction. */
public class DefunctBlocks {
    public static Block defunctNode, defunctGenerator, largeDefunctGenerator;
    public static Block defunctDrill, largeDefunctDrill, speaker;
    public static Block smallDefunctWall, defunctWall, defunctForceSynthesizer, nostalgia, memorial;
    public static Block defunctCheckpoint, defunctFlooring, defunctFramingCross, smallDefunctRadarTower;
    public static Block maintainerInactive, defunctBeacon1, dPowerSource, dLiquidSource, dItemSource;

    public static void loadContent(){
        speaker = new InfomaticBlock("speaker"){{
            requirements(Category.logic, BuildVisibility.worldProcessorOnly, with());
            size = 1;
            privileged = true;
            targetable = false;
        }};
        defunctNode = new PowerNode("defunct-node"){{
            // Keep existing infrastructure and its links during the landing animation.
            privileged = true;
            consumesPower = outputsPower = true;
            consumePowerBuffered(500);
            health = 200;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            laserRange = 8;
            buildVisibility = BuildVisibility.sandboxOnly;
            category = Category.power;
        }};
        defunctGenerator = new PowerSource("defunct-generator"){{
            privileged = true;
            requirements(Category.power, with(silicon, 1200, lead, 500));
            size = 2;
            buildVisibility = BuildVisibility.sandboxOnly;
            category = Category.power;
            powerProduction = 4;
            baseExplosiveness = 2;
            maxNodes = 10;
            maxRange = 1;
            laserRange = 1;
        }};
        largeDefunctGenerator = new PowerSource("large-defunct-generator"){{
            privileged = true;
            requirements(Category.power, with(silicon, 1500, lead, 750, metaglass, 500));
            size = 3;
            buildVisibility = BuildVisibility.sandboxOnly;
            category = Category.power;
            powerProduction = 8;
            baseExplosiveness = 4;
            maxNodes = 10;
            maxRange = 1;
            laserRange = 1;
        }};
        defunctDrill = new Drill("defunct-drill"){{
            privileged = true;
            shownPlanets.addAll(Planets.serpulo, fakeSerpulo);
            requirements(Category.production, with(silicon, 250, lead, 100));
            size = 2;
            squareSprite = false;
            buildVisibility = BuildVisibility.sandboxOnly;
            drawMineItem = false;
            consumeLiquid(water, 1).boost();
            liquidBoostIntensity = 3;
            drillTime = 180;
            itemCapacity = 15;
            liquidCapacity = 100;
            drillEffect = Fx.mineBig;
            consumePower(1);
            tier = 1;
            category = Category.production;
        }};
        largeDefunctDrill = new Drill("large-defunct-drill"){{
            privileged = true;
            shownPlanets.addAll(Planets.serpulo, fakeSerpulo);
            requirements(Category.production, with(silicon, 500, lead, 250, copper, 300, graphite, 250));
            size = 3;
            squareSprite = false;
            buildVisibility = BuildVisibility.sandboxOnly;
            drawMineItem = false;
            consumeLiquid(methane, 3).boost();
            liquidBoostIntensity = 3;
            drillTime = 160;
            itemCapacity = 30;
            liquidCapacity = 100;
            drillEffect = Fx.mineBig;
            consumePower(4);
            tier = 2;
            category = Category.production;
        }};
        smallDefunctWall = new Wall("defunct-wall-small"){{
            requirements(Category.defense, ItemStack.with(lead, 10, silicon, 15));
            size = 1;
            destroyEffect = AquaFx.factoryDestroyDefunct;
            health = 250;
            buildVisibility = BuildVisibility.sandboxOnly;
            category = Category.defense;
        }};
        defunctWall = new Wall("defunct-wall"){{
            requirements(Category.defense, ItemStack.mult(smallDefunctWall.requirements, 4));
            size = 2;
            health = 1000;
            destroyEffect = AquaFx.factoryDestroyDefunct;
            buildVisibility = BuildVisibility.sandboxOnly;
            category = Category.defense;
        }};
        defunctForceSynthesizer = new ForceProjector("defunct-force-synthesizer"){{
            privileged = true;
            requirements(Category.effect, ItemStack.with(silicon, 250, copper, 1000, metaglass, 200));
            size = 4;
            squareSprite = false;
            consumePower(12);
            shieldHealth = 3000;
            cooldownBrokenBase = 3f;
            cooldownNormal = 0.5f;
            radius = 200;
            sides = 4;
            shieldRotation = 45f;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            shieldBreakEffect = new Effect(80, e -> {
                color(e.color);
                stroke(3f * e.fout());
                Lines.poly(e.x, e.y, 4, 200-200*(e.time/e.lifetime), 45);
            });
            forceShrinkEffect = new Effect(10, e -> {
                color(e.color);
                stroke(3f * e.fout());
                Lines.poly(e.x, e.y, 4, 150-3*e.time, 45);
            });
        }};
        defunctCheckpoint = new DefunctCheckpointBlock("defunct-checkpoint"){{
            buildVisibility = BuildVisibility.editorOnly;
        }};
        defunctFlooring = new Floor("defunct-flooring"){{
            variants = 0;
        }};
        defunctFramingCross = new Wall("defunct-framing-cross") {{
            requirements(Category.defense, with(silicon, 25, lead, 15, graphite, 25));
            variants = 2;
            health = 150;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};
        smallDefunctRadarTower = new Radar("defunct-small-radar-tower") {{
            privileged = true;
            size = 3;
            requirements(Category.effect, with(silicon, 500, lead, 750, ferricMatter, 50, copper, 750));
            health = 1500;
            customShadow = true;
            consumePower(1f);
            discoveryTime = 300f;
            fogRadius = 16;
            buildVisibility = BuildVisibility.sandboxOnly;
        }};
        maintainerInactive = new DefunctUnitBlock("maintainer-inactive"){{
            buildVisibility = BuildVisibility.sandboxOnly;
            time = 90;
            consumePower(0.5f);
            unit = maintainer;
            rebuildable = false;
            beepSound = AquaSounds.defunctDistressBeep;
            beepInterval = 240;
            ambientSoundVolume = 1f;
        }};
        defunctBeacon1 = new DefunctBeacon("defunct-beacon-small"){{
            privileged = true;
            buildVisibility = BuildVisibility.sandboxOnly;
            size = 2;
            squareSprite = false;
            consumePower(4f);
            units.addAll(vilify,vilify,vilify,vilify);
            spawnRange = 24;
        }};
        dPowerSource = new PowerSource("defunct-power-source"){{
            requirements(Category.logic, BuildVisibility.worldProcessorOnly, with());
            size = 1;
            privileged = true;
            targetable = false;
        }};
        dLiquidSource = new LiquidSource("defunct-liquid-source"){{
            requirements(Category.logic, BuildVisibility.worldProcessorOnly, with());
            size = 1;
            privileged = true;
            targetable = false;
        }};
        dItemSource = new ItemSource("defunct-item-source"){{
            requirements(Category.logic, BuildVisibility.worldProcessorOnly, with());
            size = 1;
            privileged = true;
            targetable = false;
        }};
        nostalgia = new PowerTurret("nostalgia"){{
            destroyEffect = AquaFx.factoryDestroyDefunct;
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
            destroyEffect = AquaFx.factoryDestroyDefunct;
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
    }
}
