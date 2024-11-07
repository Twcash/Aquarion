package aquarion.blocks;

import aquarion.AquaAttributes;
import aquarion.AquaItems;
import aquarion.AquaSounds;
import aquarion.world.blocks.production.RechargeDrill;
import aquarion.world.blocks.production.WallPayloadDrill;
import aquarion.world.graphics.AquaFx;
import arc.graphics.Color;
import arc.math.Interp;
import mindustry.content.Fx;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.Attributes;
import mindustry.world.blocks.production.BurstDrill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.WallCrafter;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.brine;
import static aquarion.AquaLiquids.hydroxide;
import static aquarion.AquaSounds.wallDrill;
import static aquarion.blocks.AquaDefense.bauxiteWall;
import static aquarion.blocks.AquaDefense.galliumWall;
import static mindustry.content.Items.lead;
import static mindustry.content.Liquids.arkycite;
import static mindustry.type.ItemStack.with;

public class AquaCrafters {
    public static Block wallExtractor, clarifier, chireniumElectroplater, bauxiteHarvester, CompressionDrill, ramDrill, cultivationChamber, ceramicKiln;
    public static void loadContent(){

    bauxiteHarvester = new WallCrafter("bauxite-harvester"){{
    size = 4;
    researchCostMultiplier = 0.03f;
    requirements(Category.production, with(lead, 25, AquaItems.bauxite, 30));
    researchCost = with(lead, 15, AquaItems.bauxite, 10);
    drillTime = 75;
    itemCapacity = 60;
    attribute = AquaAttributes.bauxite;
    output = AquaItems.bauxite;
    ambientSound = wallDrill;
    ambientSoundVolume = 0.04F;
    envEnabled |= Env.underwater;
    squareSprite = false;
    consumePower(1.5f);
    }};
    wallExtractor = new WallPayloadDrill("wall-extractor"){{
        requirements(Category.production, with(lead, 25, AquaItems.bauxite, 30));
        size = 3;
        attributeBlockMap.put(AquaAttributes.gallium, AquaDefense.galliumWall);
        attributeBlockMap.put(AquaAttributes.bauxite, AquaDefense.bauxiteWall);
        envEnabled |= Env.underwater;
    }};
    CompressionDrill = new RechargeDrill("compression-drill"){{
    requirements(Category.production, with(lead, 10, AquaItems.bauxite, 15));
        researchCost = with(lead, 5, AquaItems.bauxite, 10);
        drillTime = 320;
        size = 2;
        tier = 2;
        itemCapacity = 50;
        //arrows = 2;
        //invertedTime = 45;
        updateEffectChance = 0.5f;
        drillEffect = new MultiEffect(Fx.mineBig, AquaFx.smallShockwave);
        updateEffect = Fx.drillSteam;
        //arrowSpacing = 2;
        ambientSound = AquaSounds.compressDrill;
        //drillSound = AquaSounds.compressDrillImpact;
        //shake = 0.5f;
        envEnabled |= Env.underwater;
        drillEffectRnd = 0.5f;
        ambientSoundVolume = 0.01f;
    }};
        ramDrill = new BurstDrill("ram-drill"){{
            requirements(Category.production, with(lead, 110, AquaItems.bauxite, 65, ceramic, 45));
            drillTime = 280;
            size = 3;
            tier = 4;
            invertedTime = 60;
            itemCapacity = 90;
            arrows = 1;
            ambientSoundVolume = 0.01f;
            updateEffectChance = 0.05f;
            ambientSound = AquaSounds.compressDrill;
            drillSound = AquaSounds.compressDrillImpact;
            drillEffect = new MultiEffect(Fx.mineHuge, AquaFx.smallShockwave);
            updateEffect = Fx.pulverizeRed;
            hardnessDrillMultiplier = 3.5f;
            arrowSpacing = 0;
            researchCostMultiplier = 0.03f;
            shake = 0.9f;
            envEnabled |= Env.underwater;
            ambientSound = Sounds.drillCharge;
            ambientSoundVolume = 0.06f;
            consumePower(0.875f);
        }};
        //crafters
        ceramicKiln = new GenericCrafter("ceramic-kiln"){{
            requirements(Category.crafting, with(lead, 120, bauxite, 90));
            ambientSound = Sounds.torch;
            craftTime = 60;
            researchCostMultiplier = 0.03f;
            size = 3;
            consumeItems(with(lead, 2,  bauxite, 1));
            consumeLiquid(arkycite, 12/60f);
            envEnabled |= Env.underwater;
            ambientSoundVolume = 0.02f;
            itemCapacity = 15;
            liquidCapacity = 20f * 5;
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
            researchCostMultiplier = 0.2f;
            ambientSoundVolume = 0.07f;
            liquidCapacity = 10f * 5;
            squareSprite = false;
            craftTime = 90;
            consumeItems(with(bauxite, 1));
            outputLiquid = new LiquidStack(arkycite, 6/60f);
            envEnabled |= Env.underwater;
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
        chireniumElectroplater = new GenericCrafter("chirenium-electroplater"){{
            requirements(Category.crafting, with(lead, 200, bauxite, 150, nickel, 60, gallium, 40));
            envEnabled |= Env.underwater;
            researchCostMultiplier = 0.03f;
            liquidCapacity = 25f * 5;
            craftTime = 90;
            ambientSound = Sounds.bioLoop;
            consumeLiquid(arkycite, 12/60f);
            ambientSoundVolume = 0.06f;
            squareSprite = false;
            outputItem = new ItemStack(chirenium, 1);
            liquidCapacity = 90;
            size = 4;
            itemCapacity = 30;
            consumeItems(with(lead, 2, nickel, 1 ));
            consumePower(2f);
            craftEffect = new ParticleEffect(){{
                particles = 5;
                length = 90;
                lifetime = 270;
                cone = 30;
                baseRotation = 17;
                sizeFrom = 0;
                layer = 80;
                sizeTo = 6;
                colorFrom = Color.valueOf("53473e90");
                colorTo = Color.valueOf("3a3a3a10");
                sizeInterp = Interp.pow2Out;
                interp = Interp.pow2Out;
            }};
            drawer = new DrawMulti( new DrawRegion("-bottom"),new DrawLiquidTile(arkycite, 2), new DrawRegion("-cell1"), new DrawLiquidTile(arkycite, 2){{alpha = 0.6f;}}, new DrawBubbles(Color.valueOf("84b886")){{
                sides = 8;
                amount = 120;
                spread = 12;
            }}, new DrawRegion("-cell2"), new DrawRegion("-top"));
        }};
        clarifier = new GenericCrafter("clarifier"){{
            requirements(Category.crafting, with(lead, 90, nickel, 90, ceramic, 30, gallium, 110));
            size = 3;
            squareSprite = false;
            liquidCapacity = 90;
            envEnabled |= Env.underwater;
            outputItem = new ItemStack(salt, 1);
            consumeLiquids(LiquidStack.with(brine, 24/60f, arkycite, 4/60));
            consumePower(90/60f);
            outputLiquid = new LiquidStack(hydroxide, 12/60f);
            drawer = new DrawMulti(new DrawRegion("-bottom"),new DrawLiquidTile(brine, 2){{
            }} , new DrawCultivator(){{
                timeScl = 120;
                bottomColor = Color.valueOf("85966a");
                plantColorLight = Color.valueOf("f1ffdc");
                plantColor = Color.valueOf("728259");
                radius = 2.5f;
                bubbles = 32;
                spread = 8;
            }}, new DrawCells(){{
                range = 9;
                particles = 160;
                lifetime = 60f * 5f;
                particleColorFrom = Color.valueOf("f1ffdc");
                particleColorTo = Color.valueOf("728259");
                color = Color.valueOf("oooooooo");
            }}, new DrawRegion("-top"), new DrawGlowRegion("-glow"){{
                alpha = 0.7f;
                glowScale = 6;
            }});
        }};
        }}