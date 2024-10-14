package aquarion.blocks;

import aquarion.AquaAttributes;
import aquarion.AquaItems;
import aquarion.world.graphics.AquaFx;
import arc.graphics.Color;
import arc.math.Interp;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.effect.ParticleEffect;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.production.BurstDrill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.WallCrafter;
import mindustry.world.draw.*;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.bauxite;
import static aquarion.AquaItems.ceramic;
import static mindustry.content.Items.lead;
import static mindustry.content.Liquids.arkycite;
import static mindustry.gen.Sounds.drill;
import static mindustry.type.ItemStack.with;

public class AquaCrafters {
    public static Block bauxiteHarvester, CompressionDrill, ramDrill, cultivationChamber, ceramicKiln;
    public static void loadContent(){
    bauxiteHarvester = new WallCrafter("bauxite-harvester"){{
    size = 4;
    requirements(Category.production, with(lead, 30, AquaItems.bauxite, 45));
    researchCost = with(lead, 15, AquaItems.bauxite, 10);
    drillTime = 95;
    attribute = AquaAttributes.bauxite;
    output = AquaItems.bauxite;
    ambientSound = drill;
    ambientSoundVolume = 0.04F;
    envEnabled|= Env.terrestrial | Env.underwater;
    envDisabled|= Env.spores | Env.scorching;
    squareSprite = false;
    consumePower(1.5f);
    }};
    CompressionDrill = new BurstDrill("compression-drill"){{
    requirements(Category.production, with(lead, 10, AquaItems.bauxite, 25));
    drillTime = 350;
    size = 2;
    tier = 2;
    arrows = 2;
    updateEffectChance = 0.5f;
    drillEffect = Fx.mineBig;
    updateEffect = Fx.drillSteam;
    arrowSpacing = 2;
    shake = 0.5f;
    envEnabled|= Env.terrestrial | Env.underwater;
    envDisabled|= Env.spores | Env.scorching;
        ambientSound = Sounds.conveyor;
        ambientSoundVolume = 0.07f;
    }};
        ramDrill = new BurstDrill("ram-drill"){{
            requirements(Category.production, with(lead, 80, AquaItems.bauxite, 65, Items.metaglass, 45));
            drillTime = 250;
            size = 3;
            tier = 4;
            itemCapacity = 30;
            arrows = 1;
            updateEffectChance = 0.05f;
            drillEffect = Fx.mineHuge;
            updateEffect = Fx.pulverizeRed;
            hardnessDrillMultiplier = 3.5f;
            arrowSpacing = 0;
            shake = 0.7f;
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
            ambientSound = Sounds.drillCharge;
            ambientSoundVolume = 0.07f;
            consumePower(0.875f);
        }};
        //crafters
        ceramicKiln = new GenericCrafter("ceramic-kiln"){{
            requirements(Category.crafting, with(lead, 120, bauxite, 90));
            ambientSound = Sounds.torch;
            craftTime = 60;
            size = 3;
            consumeItems(with(lead, 2,  bauxite, 1));
            consumeLiquid(arkycite, 12/60f);
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
            ambientSoundVolume = 0.02f;
            itemCapacity = 15;
            outputItem = new ItemStack(ceramic, 2);
            consumePower(1.5f);
            craftEffect = new ParticleEffect(){{
                particles = 5;
                length = 95;
                lifetime = 290;
                layer = 80;
                cone = 25;
                baseRotation = 17;
                sizeFrom = 0;
                sizeTo = 7;
                colorFrom = Color.valueOf("292020");
                colorTo = Color.valueOf("c7c6c610");
                sizeInterp = Interp.pow2Out;
                interp = Interp.pow2Out;
            }};
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffef99")));
        }};
        cultivationChamber = new GenericCrafter("cultivation-chamber"){{
            requirements(Category.crafting, with(lead, 120, bauxite, 90));
            size = 3;
            ambientSound = Sounds.machine;
            ambientSoundVolume = 0.07f;
            squareSprite = false;
            craftTime = 90;
            consumeItems(with(bauxite, 1));
            outputLiquid = new LiquidStack(arkycite, 6/60f);
            envEnabled|= Env.terrestrial | Env.underwater;
            envDisabled|= Env.spores | Env.scorching;
            craftEffect = new ParticleEffect(){{
                particles = 5;
                length = 90;
                lifetime = 270;
                cone = 30;
                baseRotation = 17;
                sizeFrom = 0;
                layer = 80;
                sizeTo = 6;
                colorFrom = Color.valueOf("18c87b90");
                colorTo = Color.valueOf("18c87b10");
                sizeInterp = Interp.pow2Out;
                interp = Interp.pow2Out;
            }};
            drawer = new DrawMulti( new DrawRegion("-bottom"), new DrawCells(){{
                range = 9;
                particles = 200;
                particleColorFrom = Color.valueOf("18c87b");
                particleColorTo = Color.valueOf("2ca197");
                color = Color.valueOf("266b4d");
            }}, new DrawCircles(){{
                timeScl = 400;
                amount = 6;
                radius = 9;
                strokeMax = 1.2f;
                color = Color.valueOf("18c87b");
            }}, new DrawRegion("-top"));
        }};
    }
}