package aquarion.blocks;

import aquarion.AquaLiquids;
import aquarion.world.blocks.heatBlocks.AquaHeatProducer;
import aquarion.world.blocks.heatBlocks.AquaheatMover;
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
    public static Block heatEngine, pylon, outlet, capacitorBank, radiator, compressor, channel, fumeEngine, ablativeFissionReactor, radioisotopeModule, fuelPelletFeeder, thermoelectricModule, steamEngine, electrumBattery, electrumPowerNode, solarAccumulator, Relay, GeothermalGenerator, hydroxideGenerator;

    public static void loadContent(){
        fumeEngine = new ConsumeGenerator("fume-engine"){{

            requirements(Category.power, with(Items.lead, 2500, aluminum, 900, ferrosilicon, 1500, bauxite, 3000));
            size = 9;
            consumeLiquids(LiquidStack.with(fumes, 20)); //jesus fucking christ;
            powerProduction = 100; //1 unit of fume = 5 power/s prolly gonna only need 2 of these for early to midgame
            liquidCapacity = 5000;
            baseExplosiveness = 10;
            explosionDamage = 5000;
            explosionShake = 5;
            explosionRadius = 240;
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
        radiator = new HeatProducer("radiator"){{
            requirements(Category.power, with(Items.lead, 5));
            heatOutput = -10;
        }};
        compressor = new HeatProducer("compressor"){{
            requirements(Category.power, with(Items.lead, 5));
            heatOutput = 10;
        }};
        pylon = new PowerPylon("pylon"){{
            requirements(Category.power, with(silicon, 20));
            laserRange = 10;
            maxNodes = 8;
        }};
        outlet = new PowerOutlet("outlet"){{
            requirements(Category.power, with(silicon, 15));
            rotate = true;
            rotateDraw = false;
            drawer = new DrawMulti( new DrawDefault(), new DrawSideRegion(){{
            }});
        }};
        capacitorBank = new Battery("capacitor-bank"){{
            requirements(Category.power, with(copper, 50, silicon, 120, lead, 200));
            size = 4;
            insulated = true;
            consumePowerBuffered(8000);
            drawer = new DrawDefault();
        }};
        heatEngine = new ThermalGenerator("heat-engine"){{
            requirements(Category.power, with(silicon, 220, lead, 200));
            displayEfficiency = false;
            insulated = true;
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
