package aquarion.blocks;

import arc.graphics.Color;
import mindustry.entities.effect.ParticleEffect;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.power.ThermalGenerator;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static arc.math.Interp.pow3Out;
import static mindustry.content.Items.lead;
import static mindustry.type.ItemStack.with;

public class AquaPower {
    public static Block Relay, GeothermalGenerator;
    public static void loadContent(){
    Relay = new PowerNode("relay"){{
        requirements(Category.power, with(lead, 15, bauxite, 5));
        laserColor1 = Color.valueOf("ffffff90");
        laserColor2 = Color.valueOf("7d4f9d10");
        customShadow = true;
        consumesPower = outputsPower = true;
        laserRange = 7;
        consumePowerBuffered(1500f);
        consumePower(1/60f);
        envEnabled |= Env.terrestrial | Env.underwater;
        envDisabled |= Env.spores | Env.scorching;
    }};
    GeothermalGenerator = new ThermalGenerator("geothermal-generator"){{
    requirements(Category.power, with(lead, 35, bauxite, 20));
    size = 3;
    canOverdrive = false;
    effectChance = 0.1f;
    ambientSound = Sounds.hum;
    ambientSoundVolume = 0.06f;
    displayEfficiencyScale = 1/9f;
    displayEfficiency = false;
    minEfficiency = 9f - 0.0001f;
    attribute = Attribute.steam;
    generateEffect = new ParticleEffect(){{
    particles = 3;
    length = 80;
    sizeFrom = 0;
    sizeTo = 7.5f;
    powerProduction = 3.5f/9;
    interp = pow3Out;
    sizeInterp = pow3Out;
    cone= 35;
    lifetime = 120;
    colorFrom = Color.valueOf("e5e0ec80");
    colorTo = Color.valueOf("877e9210");
    }};
    drawer = new DrawMulti( new DrawDefault(), new DrawGlowRegion(){{alpha = 0.7f; color = Color.valueOf("ffd37f"); glowIntensity = 0.2f; glowScale = 6;}}, new DrawRegion("-rotator"){{
    spinSprite = true;
    rotateSpeed = 5;
    }}, new DrawBlurSpin("-rotator", 5){{blurThresh = 0.1f;}}, new DrawRegion("-top"));
    }};
    }
}