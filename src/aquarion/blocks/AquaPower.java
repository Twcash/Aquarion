package aquarion.blocks;

import aquarion.AquaLiquids;
import aquarion.AquaSounds;
import aquarion.world.blocks.heatBlocks.AquaheatMover;
import aquarion.world.blocks.heatBlocks.CoolingBlock;
import aquarion.world.blocks.power.*;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.DrawBetterRegion;

import arc.graphics.Color;
import arc.math.Interp;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.gen.Sounds;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatConductor;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.blocks.power.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.*;
import static arc.math.Interp.pow3Out;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.slag;
import static mindustry.content.Liquids.water;
import static mindustry.type.ItemStack.with;

public class AquaPower {
    public static Block voltageSupplyUnit, turbineDynamo, solarGenerator, hydroxideReactor, heatEngine, pylon, outlet, capacitorBank, ionBattery, radiator, compressor, channel, fumeEngine;

    public static void loadContent(){
        solarGenerator = new SolarGenerator("solar-generator"){{
            requirements(Category.power, with(lead, 350, nickel, 200, silicon, 250));
            size = 4;
            insulated = true;
            explosionDamage =640;
            explosionRadius = 5;
            powerProduction = 175/60f;
            baseExplosiveness = 1;//funny
            alwaysUnlocked = true;
            envDisabled |= Env.underwater;
        }};
        hydroxideReactor = new ConsumeGenerator("hydroxide-reactor"){{
            requirements(Category.power, with(ferricMatter, 200, aluminum, 250, silicon, 1200, copper, 500));
            powerProduction = 4500/60f;
            size = 7;
            squareSprite = false;
            researchCostMultiplier = 0.1f;
            liquidCapacity = 1200;
            ambientSound = AquaSounds.derrick;
            ambientSoundVolume = 0.06f;
            consumeLiquids(LiquidStack.with(hydroxide, 0.5f, water, 120/60f));
            insulated = true;
            generateEffectRange = 7*8/2f;
            generateEffect = new MultiEffect(
                    Fx.steam,
                    Fx.mineSmall,
                    AquaFx.hydroxideReactorGenerate
            );
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(hydroxide){{
                alpha = 0.8f;
                padTop = 6;
                padLeft = padBottom = padRight = 4;
            }}, new DrawLiquidTile(water){{
                alpha = 0.8f;
                padBottom = 6;
                padLeft = padTop = padRight = 4;
            }}, new DrawDefault(), new DrawGlowRegion(){{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.4f;
                color = Color.valueOf("f5c5aa");
            }},new DrawGlowRegion("-glow1"){{
                glowIntensity = 0.9f;
                glowScale = 8;
                alpha = 0.3f;
                color = Color.valueOf("f5c5aa");
            }},new DrawGlowRegion("-glow2"){{
                glowIntensity = 0.6f;
                glowScale = 10;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
        turbineDynamo = new ConsumeGenerator("turbine-dynamo"){{
            requirements(Category.power, with(copper, 2000, lead, 500, metaglass, 1000, nickel, 1200));
            size = 10;
            researchCostMultiplier = 0.05f;
            squareSprite = false;
            liquidCapacity = 2000;
            insulated = true;
            powerProduction = 110;
            consumeLiquid(haze, 2000/60f);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawBlurSpin("-fan", 6), new DrawBlurSpin("-fan", 4), new DrawBlurSpin("-fan", 2), new DrawLiquidTile(haze, 7){{
                alpha =0.6f;
            }}, new DrawRegion("-bars"){{
                rotateSpeed = 2;
                spinSprite = true;
            }}, new DrawParticles(){{
                color = Color.gray;
                alpha = 0.8f;
                particleRad = 5*7f;
                particleSize = 8;
                poly = true;
                sides = 8;
            }}, new DrawParticles(){{
                color = Color.white;
                alpha = 0.7f;
                particleRad = 5*6f;
                particleSize = 8;
                poly = true;
                sides = 8;
            }}, new DrawDefault(), new DrawRegion("-bars"){{
                rotateSpeed = -2;
                spinSprite = true;
            }}, new DrawRegion("-top"));
        }};
        fumeEngine = new ConsumeGenerator("fume-engine"){{

            requirements(Category.power, with(ferricMatter, 500, aluminum, 2000, silicon, 800, copper, 1200));
            size = 9;
            researchCostMultiplier = 0.05f;
            insulated = true;
            consumeLiquids(LiquidStack.with(fumes, 20)); //jesus fucking christ;
            powerProduction = 120; //1 unit of fume = 5 power/s prolly gonna EVER need 2 of these for early to midgame
            liquidCapacity = 5000;
            baseExplosiveness = 10;
            explosionDamage = 2500;
            explosionShake = 5;
            explosionRadius = 120/2;
            squareSprite = false;
            generateEffectRange = 9*8/2f;
            effectChance = 0.05f;
            generateEffect = new MultiEffect(
                    Fx.steam,
                    Fx.mineSmall
            );
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(fumes, 2), new DrawRegion("-cookie"),
                    //sigh
                    new DrawBetterRegion("-coil"){{
                        spinSprite = true;
                        rotateSpeed = 4;
                        rotation = 11.25f;
                    }},new DrawBetterRegion("-coil"){{
                spinSprite = true;
                rotateSpeed = 4;
                rotation = 22.25f;
            }},new DrawBetterRegion("-coil"){{
                spinSprite = true;
                rotateSpeed = 4;
                rotation = 0;
            }} ,new DrawBetterRegion("-coil"){{
                spinSprite = true;
                rotateSpeed = 4;
                rotation = 45;
            }} ,new DrawBetterRegion("-coil"){{
                spinSprite = true;
                rotateSpeed = 4;
                rotation = 33.75f;
            }} ,new DrawBetterRegion("-coil"){{
                spinSprite = true;
                rotateSpeed = 4;
                rotation = 67.5f;
            }} ,new DrawBetterRegion("-coil"){{
                spinSprite = true;
                rotateSpeed = 4;
                rotation = 56.25f;
            }},new DrawBetterRegion("-coil"){{
                spinSprite = true;
                rotateSpeed = 4;
                rotation =90f;
            }}  ,new DrawBetterRegion("-coil"){{
                spinSprite = true;
                rotateSpeed = 4;
                rotation = 78.75f;
            }}, new DrawParticles(){{
                reverse = true;
                alpha = 0.4f;
                particleRad = 8*2;
                particleSize = 12;
                rotateScl = 1;
                particles = 40;
                color = Color.valueOf("b4a576");
            }}, new DrawParticles(){{
                alpha = 0.4f;
                reverse = true;
                particleRad = 8*3;
                particleSize = 8;
                particles = 55;
                rotateScl = 1.5f;
                color = Color.valueOf("98936c");
            }}, new DrawParticles(){{
                reverse = true;
                alpha = 0.4f;
                particleRad = 8*4;
                particleSize = 6;
                rotateScl = 2;
                particles = 20;
                color = Color.valueOf("838362");
            }}, new DrawSoftParticles(){{
                particles = 60;
                particleLife = 30;
                particleSize = 8;
                particleRad = 12;
                rotateScl = 3;
            }}, new DrawDefault(), new DrawGlowRegion(){{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.7f;
                color = Color.valueOf("f5c5aa");
            }},new DrawGlowRegion("-glow1"){{
                glowIntensity = 0.9f;
                glowScale = 8;
                alpha = 0.6f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
        channel = new HeatConductor("channel"){{
            requirements(Category.power, with(Items.lead, 5));
            size = 2;
        }};
        radiator = new CoolingBlock("radiator"){{
            requirements(Category.power, with(Items.lead, 5));
            heatSubtraction = 10;
            heatRequirement = 0;

            size = 2;
            consumeLiquid(water, 10/60f);
            consumePower(50/60f);
        }};
        compressor = new HeatProducer("compressor"){{
            requirements(Category.power, with(Items.lead, 5));
            heatOutput = 10;
        }};
        pylon = new PowerPylon("pylon"){{
            requirements(Category.power, with(silicon, 20));
            maxRange = 10;
            laserRange = 10;
            maxNodes = 8;
            alwaysUnlocked = true;
        }};
        outlet = new PowerOutlet("outlet"){{
            requirements(Category.power, with(silicon, 15));
            rotate = true;
            rotateDraw = false;
            alwaysUnlocked = true;
            regionRotated1 = 1;
            drawer = new DrawMulti( new DrawDefault(), new DrawSideRegion());
        }};
        voltageSupplyUnit = new PowerOutlet("voltage-supply-unit"){{
            requirements(Category.power, with(copper, 90, silicon, 200, ferrosilicon, 50));
            rotate = true;
            size = 2;
            rotateDraw = false;
            drawer = new DrawMulti( new DrawDefault(), new DrawSideRegion());
        }};
        capacitorBank = new Battery("capacitor-bank"){{
            requirements(Category.power, with(copper, 50, silicon, 120, lead, 200));
            size = 4;
            insulated = true;
            consumePowerBuffered(8000);
            drawer = new DrawDefault();
        }};
        ionBattery = new Battery("ion-battery"){{
            requirements(Category.power, with(aluminum, 120, arsenic, 200, copper, 100));
            size = 3;
            insulated = true;
            researchCostMultiplier = 0;
            consumePowerBuffered(12000);
            drawer = new DrawMulti(new DrawDefault(), new DrawGlowRegion(){{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.4f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
        heatEngine = new ThermalGenerator("heat-engine"){{
            requirements(Category.power, with(silicon, 220, lead, 200));
            displayEfficiency = false;
            insulated = true;
            researchCostMultiplier = 0.05f;
            size = 3;
            squareSprite = false;
            displayEfficiencyScale = 1f / 9f;
            powerProduction = 400/60f / 9f;
            generateEffect = AquaFx.heatEngineGenerate;
            effectChance = 0.02f;
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.06f;
            outputLiquid = new LiquidStack(slag, 10/60f);
            drawer = new DrawMulti( new DrawDefault(), new DrawGlowRegion(){{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.4f;
                color = Color.valueOf("f5c5aa");
            }},new DrawGlowRegion("-glow1"){{
                glowIntensity = 0.9f;
                glowScale = 8;
                alpha = 0.3f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
    }
}
