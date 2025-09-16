package aquarion.blocks;

import aquarion.AquaSounds;
import aquarion.world.blocks.power.HeatGenerator;
import aquarion.world.blocks.power.PowerOutlet;
import aquarion.world.blocks.power.PowerPylon;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.drawers.*;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import mindustry.content.Fx;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.power.*;
import mindustry.world.draw.*;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.*;
import static aquarion.world.graphics.Renderer.Layer.heat;
import static aquarion.world.graphics.Renderer.Layer.shadow;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.type.ItemStack.with;

public class AquaPower {
    public static Block petroleumEngine, heatExchanger, energyBank, voltageSupplyUnit, turbineDynamo, solarGenerator, hydroxideReactor, heatEngine, pylon, outlet, capacitorBank, ionBattery, radiator, compressor, channel, fumeEngine;

    public static void loadContent() {
        solarGenerator = new SolarGenerator("solar-generator") {{
            requirements(Category.power, with(lead, 250, nickel, 200, silicon, 250));
            size = 4;
            insulated = true;
            explosionDamage = 640;
            explosionRadius = 5;
            powerProduction = 2.75f;
            baseExplosiveness = 1;//funny
            alwaysUnlocked = true;
            envDisabled |= Env.underwater;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
            }}, new DrawDefault(), new DrawGlowRegion() {{
                glowIntensity = 0.7f;
                glowScale = 12;
                alpha = 0.4f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
        hydroxideReactor = new HeaterGenerator("hydroxide-reactor") {{
            requirements(Category.power, with(ferricMatter, 500, silicon, 1200, copper, 900, graphite, 500));
            powerProduction = 25;
            size = 7;
            squareSprite = false;
            outputLiquid = new LiquidStack(hydrogen, 2);
            heatOutput = 35;
            researchCostMultiplier = 0.1f;
            liquidCapacity = 1200;
            ambientSound = AquaSounds.derrick;
            ambientSoundVolume = 0.06f;
            consumeLiquids(LiquidStack.with(hydroxide, 8.5, water, 4.25));
            insulated = true;
            generateEffectRange = 7 * 8 / 2f;
            generateEffect = new MultiEffect(
                    Fx.steam,
                    Fx.mineSmall,
                    AquaFx.hydroxideReactorGenerate
            );
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(hydroxide) {{
                alpha = 0.8f;
                padTop = 6;
                padLeft = padBottom = padRight = 4;
            }}, new DrawLiquidTile(water) {{
                alpha = 0.8f;
                padBottom = 6;
                padLeft = padTop = padRight = 4;
            }}, new DrawDefault(), new DrawHeatOutput(), new DrawGlowRegion() {{
                layer = heat;
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.4f;
                color = Color.valueOf("f5c5aa");
            }}, new DrawGlowRegion("-glow1") {{
                layer = heat;
                glowIntensity = 0.9f;
                glowScale = 8;
                alpha = 0.3f;
                color = Color.valueOf("f5c5aa");
            }}, new DrawGlowRegion("-glow2") {{
                layer = heat;
                glowIntensity = 0.6f;
                glowScale = 10;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
        turbineDynamo = new ConsumeGenerator("turbine-dynamo") {{
            requirements(Category.power, with(lead, 1500, metaglass, 1200, cupronickel, 1200, graphite, 400));
            size = 10;
            squareSprite = false;
            effectChance = 0.09f;
            generateEffect = AquaFx.turbineGenerate;
            generateEffectRange = 12 / 4f;
            liquidCapacity = 2000;
            insulated = true;
            powerProduction = 155;
            consumeLiquid(haze, 34);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
            }}, new DrawRegion("-bottom"), new DrawBlurSpin("-fan", 6), new DrawBlurSpin("-fan", 4), new DrawBlurSpin("-fan", 2), new DrawLiquidTile(haze, 7) {{
                alpha = 0.6f;
            }}, new DrawRegion("-mid"), new DrawRadialEngine() {{
                radius = 7;
                rodLength = 20;
                pistonCount = 6;
                speed = 0.08f;
            }}, new DrawRegion("-coggers") {{
                rotateSpeed = 1.2f;
                spinSprite = true;
                x = -70 / 4f;
                y = -60 / 4f;
            }}, new DrawRegion("-coggers") {{
                rotateSpeed = 1.2f;
                spinSprite = true;
                x = -20 / 4f;
                y = 60 / 4f;
                rotation = 45;
            }}, new DrawParticles() {{
                color = Color.gray;
                alpha = 0.2f;
                particleRad = 5 * 7f;
                particleSize = 8;
                poly = true;
                sides = 8;
            }}, new DrawWheel() {{
                width = 63 / 4f;
                height = 74 / 4f;
                rotation = -45;
                rotationSpeed = 1.2f;
                suffix = "-tick";
                x = -110 / 4f;
                y = 110 / 4f;
                wheelColors = new Color[]{
                        //I should set this as a Pallete or smth
                        Color.valueOf("8da6ab"),
                        Color.valueOf("333f4b"),
                        Color.valueOf("0f151b")
                };
            }}, new DrawWheel() {{
                width = 70 / 4f;
                height = 60 / 4f;
                rotation = -45;
                rotationSpeed = -1.2f;
                suffix = "-tick";
                x = -110 / 4f;
                y = 110 / 4f;
                wheelColors = new Color[]{
                        //I should set this as a Pallete or smth
                        Color.valueOf("8da6ab"),
                        Color.valueOf("333f4b"),
                        Color.valueOf("0f151b")
                };
            }}, new DrawWheel() {{
                width = 160 / 4f;
                height = 30 / 4f;
                rotation = -45;
                rotationSpeed = 1.3f;
                suffix = "-tick";
                x = -60 / 4f;
                y = 60 / 4f;
                wheelColors = new Color[]{
                        //I should set this as a Pallete or smth
                        Color.valueOf("8da6ab"),
                        Color.valueOf("333f4b"),
                        Color.valueOf("0f151b")
                };
            }}, new DrawWheel() {{
                width = 90 / 4f;
                height = 48 / 4f;
                rotation = -45;
                rotationSpeed = 1.2f;
                suffix = "-tick";
                x = -80 / 4f;
                y = 80 / 4f;
                wheelColors = new Color[]{
                        //I should set this as a Pallete or smth
                        Color.valueOf("8da6ab"),
                        Color.valueOf("333f4b"),
                        Color.valueOf("0f151b")
                };
            }}, new DrawDefault(), new DrawLiquidTile(haze) {{
                padTop = 135 / 4f;
                padBottom = 130 / 4f;
                padLeft = 130 / 4f;
                padRight = 135 / 4f;
            }}, new DrawRegion("-top"));
        }};
        petroleumEngine = new ConsumeGenerator("petroleum-engine") {
            {
                requirements(Category.power, with(polymer, 2000, aluminum, 3500, ferrosilicon, 1200, metaglass, 6000));
                size = 8;
                squareSprite = false;
                insulated = true;
                liquidCapacity = 3000;
                warmupSpeed = 0.002f;
                consumeLiquids(LiquidStack.with(petroleum, 24, oxygen, 12));
                powerProduction = 360;
                baseExplosiveness = 20;
                explosionShake = 100;
                explosionPuddles = 70;
                explosionPuddleLiquid = petroleum;
                explosionPuddleRange = 350;
                explosionShakeDuration = 240;
                explosionRadius = 190;
                explosionDamage = 7000;
                generateEffect = new MultiEffect(
                        Fx.steam,
                        Fx.mineSmall
                );
                DrawBlock[] draws = new DrawBlock[5];
                draws[0] = new DrawDefault();

                for (int i = 0; i < 4; i++) {
                    int f = i;
                    draws[i + 1] = new DrawPistons() {{
                        sides = 2;
                        suffix = "-piston" + (f+1);
                        sinOffset = (f*10f);
                        angleOffset=90;
                        sinMag = 4.5f;
                        this.
                        sinScl = 1f;
                        sideOffset = 1;
                    }};
                }
                drawer = new DrawMulti(draws[0],draws[1],draws[2],draws[3],draws[4], new DrawRegion("-top"));
            }};
        fumeEngine = new ConsumeGenerator("fume-engine") {{

            requirements(Category.power, with(ferricMatter, 500, aluminum, 2000, silicon, 800, copper, 1200));
            size = 9;
            researchCostMultiplier = 0.05f;
            insulated = true;
            consumeLiquids(LiquidStack.with(fumes, 30));
            powerProduction = 200;
            liquidCapacity = 5000;
            baseExplosiveness = 10;
            explosionDamage = 2500;
            explosionShake = 5;
            explosionRadius = 120 / 2;
            squareSprite = false;
            generateEffectRange = 9 * 8 / 2f;
            effectChance = 0.05f;
            generateEffect = new MultiEffect(
                    Fx.steam,
                    Fx.mineSmall
            );
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(fumes, 2), new DrawRegion("-cookie"),
                    //sigh
                    new DrawBetterRegion("-coil") {{
                        spinSprite = true;
                        rotateSpeed = 4;
                        rotation = 11.25f;
                    }}, new DrawBetterRegion("-coil") {{
                spinSprite = true;
                rotateSpeed = 4;
                rotation = 22.25f;
            }}, new DrawBetterRegion("-coil") {{
                spinSprite = true;
                rotateSpeed = 4;
                rotation = 0;
            }}, new DrawBetterRegion("-coil") {{
                spinSprite = true;
                rotateSpeed = 4;
                rotation = 45;
            }}, new DrawBetterRegion("-coil") {{
                spinSprite = true;
                rotateSpeed = 4;
                rotation = 33.75f;
            }}, new DrawBetterRegion("-coil") {{
                spinSprite = true;
                rotateSpeed = 4;
                rotation = 67.5f;
            }}, new DrawBetterRegion("-coil") {{
                spinSprite = true;
                rotateSpeed = 4;
                rotation = 56.25f;
            }}, new DrawBetterRegion("-coil") {{
                spinSprite = true;
                rotateSpeed = 4;
                rotation = 90f;
            }}, new DrawBetterRegion("-coil") {{
                spinSprite = true;
                rotateSpeed = 4;
                rotation = 78.75f;
            }}, new DrawParticles() {{
                reverse = true;
                alpha = 0.4f;
                particleRad = 8 * 2;
                particleSize = 12;
                rotateScl = 1;
                particles = 40;
                color = Color.valueOf("b4a576");
            }}, new DrawParticles() {{
                alpha = 0.4f;
                reverse = true;
                particleRad = 8 * 3;
                particleSize = 8;
                particles = 55;
                rotateScl = 1.5f;
                color = Color.valueOf("98936c");
            }}, new DrawParticles() {{
                reverse = true;
                alpha = 0.4f;
                particleRad = 8 * 4;
                particleSize = 6;
                rotateScl = 2;
                particles = 20;
                color = Color.valueOf("838362");
            }}, new DrawSoftParticles() {{
                particles = 60;
                particleLife = 30;
                particleSize = 8;
                particleRad = 12;
                rotateScl = 3;
            }}, new DrawDefault(), new DrawGlowRegion() {{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.7f;
                color = Color.valueOf("f5c5aa");
            }}, new DrawGlowRegion("-glow1") {{
                glowIntensity = 0.9f;
                glowScale = 8;
                alpha = 0.6f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
        pylon = new PowerPylon("pylon") {{
            requirements(Category.power, with(silicon, 15));
            maxRange = 12;
            laserRange = 12;
            maxNodes = 9;
            alwaysUnlocked = true;
        }};
        outlet = new PowerOutlet("outlet") {{
            requirements(Category.power, with(silicon, 10));
            rotate = true;
            rotateDraw = false;
            powerProduction = 1;
            alwaysUnlocked = true;
            regionRotated1 = 1;
            drawer = new DrawMulti(new DrawDefault(), new DrawSideRegion());
        }};
        voltageSupplyUnit = new PowerOutlet("power-supply-unit") {{
            requirements(Category.power, with(copper, 90, silicon, 200, ferrosilicon, 50, graphite, 140));
            rotate = true;
            size = 2;
            powerProduction = 4;
            rotateDraw = false;
            drawer = new DrawMulti(new DrawDefault(), new DrawSideRegion());
        }};
        energyBank = new Battery("energy-bank") {{
            requirements(Category.power, with(copper, 30));
            size = 1;
            insulated = true;
            researchCostMultiplier = 0;
            consumePowerBuffered(500);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
            }}, new DrawDefault(), new DrawGlowRegion() {{
                glowIntensity = 0.7f;
                glowScale = 3;
                alpha = 0.4f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
        capacitorBank = new Battery("capacitor-bank") {{
            requirements(Category.power, with(copper, 50, silicon, 120, lead, 200));
            size = 4;
            insulated = true;
            consumePowerBuffered(11000);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
            }}, new DrawDefault());
        }};
        ionBattery = new Battery("ion-battery") {{
            requirements(Category.power, with(aluminum, 120, arsenic, 200, copper, 100));
            size = 3;
            insulated = true;
            researchCostMultiplier = 0;
            consumePowerBuffered(12000);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
            }}, new DrawDefault(), new DrawGlowRegion() {{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.4f;
                color = Color.valueOf("f5c5aa");
            }});
        }};

        heatEngine = new ThermalGenerator("heat-engine") {{
            requirements(Category.power, with(silicon, 220, lead, 200));
            displayEfficiency = false;
            insulated = true;
            researchCostMultiplier = 0.05f;
            size = 3;
            squareSprite = false;
            displayEfficiencyScale = 1f / 9f;
            powerProduction = 4 / 9f;
            generateEffect = AquaFx.heatEngineGenerate;
            effectChance = 0.02f;
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.06f;
            outputLiquid = new LiquidStack(slag, 0.125f);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
            }}, new DrawDefault(), new DrawGlowRegion() {{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.4f;
                color = Color.valueOf("f5c5aa");
            }}, new DrawGlowRegion("-glow1") {{
                glowIntensity = 0.9f;
                glowScale = 8;
                alpha = 0.3f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
        heatExchanger = new HeatGenerator("basic-heat-exchanger") {{
            requirements(Category.power, with(silicon, 1500, metaglass, 950, cupronickel, 500));
            powerProduction = 1f;
            maxHeat = 150;
            size = 8;
            liquidCapacity = 12000;
            consumeLiquid(water, 17f);
            outputLiquid = new LiquidStack(haze, 17f);
            explodeOnFull = true;
            squareSprite = false;
            insulated = true;
            unstableSpeed = 0.0001f;
            flashSpeed = 10;
            effect = AquaFx.heatEngineGenerate;
            generateEffectRange = this.size * 8/2f;
            warmupSpeed = 0.0001f;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
            }},new DrawRegion("-bottom"), new DrawLiquidTile(water, 4), new DrawOrbitRegions("-weeeeee", 24, 24f, 3f){{
                spinSprite = true;
                rotateSpeed = -1.5f;
            }}, new DrawRadialEngine(){{
                pistonCount = 3;
                radius = 5;
                speed = 0.25f;
                rodLength = 15;
            }}, new DrawLiquidTile(water, 4){{alpha = 0.4f;}}, new DrawRegion("-mid"), new DrawLiquidTile(haze, 4), new DrawAdvancedPistons(){{
                    angleOffset = 0;
                suffix = "-pistone";
                    sinMag = 2.5f;
                    sides = 1;
            }}, new DrawDefault(), new DrawHeatInputBitmask("-heats"));
        }};
    }
}
