package aquarion.blocks;

import aquarion.AquaLiquids;
import arc.graphics.Color;
import arc.math.Interp;
import mindustry.content.Liquids;
import mindustry.entities.effect.ParticleEffect;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.power.ThermalGenerator;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.hydroxide;
import static arc.math.Interp.pow3Out;
import static mindustry.content.Items.lead;
import static mindustry.content.Liquids.arkycite;
import static mindustry.type.ItemStack.with;

public class AquaPower {
    public static Block Relay, GeothermalGenerator, hydroxideGenerator;
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
    }
}