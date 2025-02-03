package aquarion.blocks;

import aquarion.AquaLiquids;
import aquarion.world.blocks.power.FuelInputModule;
import aquarion.world.blocks.power.ModularReactor;
import aquarion.world.blocks.power.RadiationModule;
import aquarion.world.blocks.power.ThermoelectricModule;
import aquarion.world.graphics.DrawBetterRegion;
import arc.graphics.Color;
import arc.math.Interp;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.power.*;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.*;
import static arc.math.Interp.pow3Out;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.water;
import static mindustry.type.ItemStack.with;

public class AquaPower {
    public static Block fumeEngine, ablativeFissionReactor, radioisotopeModule, fuelPelletFeeder, thermoelectricModule, steamEngine, electrumBattery, electrumPowerNode, solarAccumulator, Relay, GeothermalGenerator, hydroxideGenerator;
    public static void loadContent(){
    Relay = new PowerNode("relay"){{
        requirements(Category.power, with(lead, 15, bauxite, 5));
        laserColor1 = Color.valueOf("ffffff90");
        laserColor2 = Color.valueOf("7d4f9d10");
        customShadow = true;
        consumesPower = outputsPower = true;
        laserRange = 7;
        researchCostMultiplier = 0.05f;
        consumePowerBuffered(1500f);
        consumePower(1/60f);
        envEnabled |= Env.terrestrial | Env.underwater;
        envDisabled = Env.none;
    }};
    GeothermalGenerator = new ThermalGenerator("geothermal-generator"){{
    requirements(Category.power, with(lead, 35, bauxite, 20));
    size = 3;
    canOverdrive = false;
    effectChance = 0.1f;
        researchCostMultiplier = 0.1f;
    ambientSound = Sounds.hum;
    ambientSoundVolume = 0.06f;
    displayEfficiencyScale = 1/9f;
    displayEfficiency = false;
    minEfficiency = 9f - 0.0001f;
        powerProduction = 3.5f/9;
    attribute = Attribute.steam;
    generateEffect = new ParticleEffect(){{
    particles = 3;
    length = 80;
    sizeFrom = 0;
    sizeTo = 7.5f;

    interp = pow3Out;
        researchCostMultiplier = 0.03f;
    sizeInterp = pow3Out;
    cone= 35;
    lifetime = 120;
    colorFrom = Color.valueOf("e5e0ec80");
    colorTo = Color.valueOf("877e9210");
    envEnabled |= Env.terrestrial | Env.underwater;
        envDisabled = Env.none;
    }};
    drawer = new DrawMulti( new DrawDefault(), new DrawGlowRegion(){{alpha = 0.7f; color = Color.valueOf("ffd37f"); glowIntensity = 0.2f; glowScale = 6;}}, new DrawRegion("-rotator"){{
    spinSprite = true;
    rotateSpeed = 5;
    }}, new DrawBlurSpin("-rotator", 5){{blurThresh = 0.1f;}}, new DrawRegion("-top"));
    }};
    hydroxideGenerator = new ConsumeGenerator("hydroxide-generator"){{
        requirements(Category.power, with(lead, 60, bauxite, 90, chirenium, 40, ceramic, 60));
        envEnabled |= Env.terrestrial | Env.underwater;
        envDisabled = Env.none;
        powerProduction = 4;
        ambientSound = Sounds.torch;
        liquidCapacity = 30f * 5;
        ambientSoundVolume = 0.02f;
        generateEffectRange = 4;
        effectChance = 0.1f;
        size = 3;
        itemDuration = 90;
        consumeItems(with(bauxite, 1));
        consumeLiquids(LiquidStack.with(Liquids.arkycite, 3f / 60f, AquaLiquids.hydroxide, 6f / 60f));
        generateEffect = new ParticleEffect(){{
            particles = 2;
            length = 90;
            lifetime = 270;
            cone = 30;
            baseRotation = 17;
            sizeFrom = 0;
            layer = 80;
            sizeTo = 6;
            colorFrom = Color.valueOf("e9c2f090");
            colorTo = Color.valueOf("74617310");
            sizeInterp = Interp.pow2Out;
            interp = Interp.pow5Out;
        }};
        drawer = new DrawMulti( new DrawRegion("-bottom"), new DrawCells(){{
            range = 9;
            particles = 200;
            particleColorFrom = Color.valueOf("cc9bc9");
            particleColorTo = Color.valueOf("783b74");
            color = Color.valueOf("c369bd");
        }},  new DrawBubbles(Color.valueOf("ddabda")){{
            sides = 8;
            amount = 100;
            spread = 8;
        }}, new DrawRegion("-top"), new DrawGlowRegion("-glow"){{
            alpha = 1f;
            glowScale = 5f;
            color = Color.red;
        }}
        );
    }};
        electrumPowerNode = new PowerNode("electrum-power-node"){{
            requirements(Category.power, with(Items.lead, 5, electrum, 5));
            laserRange = 5;
            maxNodes = 6;
        }};

        electrumBattery = new Battery("electrum-battery"){{
            requirements(Category.power, with(Items.lead, 50, electrum, 60, arsenic, 30));
            consumePowerBuffered(6500f);
            baseExplosiveness = 0.5f;
            size = 2;
        }};
        solarAccumulator = new SolarGenerator("solar-accumulator"){{
            requirements(Category.power, with(Items.lead, 10, electrum, 30));
            powerProduction = 8/60f;
            size = 2;
        }};
        steamEngine = new ConsumeGenerator("steam-engine"){{
            requirements(Category.power, with(silver, 45, copper, 20, titanium, 60));
            consumeItems(ItemStack.with(silver, 1, arsenic, 1));
            consumeLiquid(water, 30/60f);
            baseExplosiveness = 3f;
            itemDuration = 120;
            size = 2;
            powerProduction = 160/60f;
            drawer = new DrawMulti(new DrawRegion("-bottom"),new DrawBubbles(Color.valueOf("88a4ff")){{
                spread = 4;
                amount = 48;
                radius = 1.5f;
            }}, new DrawLiquidTile(water){{alpha = 0.8f;}}, new DrawDefault());
        }};
        ablativeFissionReactor = new ModularReactor("ablative-fission-reactor"){{
            requirements(Category.power, with(Items.lead, 10, electrum, 30));
            size = 6;
            maxHeat = 1500;
            maxRadiationCap = 700;
        }};
        fuelPelletFeeder = new FuelInputModule("fuel-pellet-feeder-module"){{
            requirements(Category.power, with(Items.lead, 10, electrum, 30));
            size = 1;
            consumeItem(thorium, 2);
        }};
        radioisotopeModule = new RadiationModule("radioisotope-module"){{
            requirements(Category.power, with(Items.lead, 10, electrum, 30));
            size = 2;
            powerProduction = 96 / 60f;
        }};
        thermoelectricModule = new ThermoelectricModule("thermoelectric-module"){{
            requirements(Category.power, with(Items.lead, 10, electrum, 30));
            size = 3;
            powerProduction = 256 / 60f;
        }};
        fumeEngine = new ConsumeGenerator("fume-engine"){{

            requirements(Category.power, with(Items.lead, 2500, aluminum, 900, ferrosilicon, 1500, bauxite, 3000));
            size = 9;
            consumeLiquids(LiquidStack.with(fumes, 20, magma, 2, oxygen, 1)); //jesus fucking christ;
            powerProduction = 100; //1 unit of fume = 5 power/s prolly gonna only need 2 of these for early to midgame
            liquidCapacity = 5000;
            baseExplosiveness = 5;
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
    }
}