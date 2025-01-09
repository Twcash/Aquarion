package aquarion.blocks;

import aquarion.AquaAttributes;
import aquarion.AquaItems;
import aquarion.AquaSounds;
import aquarion.world.blocks.production.*;
import aquarion.world.consumers.ConsumeLiquidNew;
import aquarion.world.graphics.*;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Point2;
import mindustry.content.Fx;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.RadialEffect;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.Consume;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.gallium;
import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.*;
import static aquarion.AquaSounds.wallDrill;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.type.ItemStack.with;
//What was all of this even for
public class AquaCrafters {
    public static Block fumeFilter, brineCatalysisArray, ferroSiliconFoundry, bauxiteCentrifuge, magmaTap, chromiumExtractor, silverDrill, electrumBore, electrumDrill,
            atmoshpericSeperator, wallExtractor, clarifier, chireniumElectroplater,
            bauxiteHarvester, siliconHearth, CompressionDrill, ramDrill, cultivationChamber, ceramicKiln, magmaDiffser,
            carbonicBubbler, electrumCombustor, cryofluidChurn, cupronickelAlloyer, hydroponicsBasin, inconelForge;

    public static void loadContent() {

        bauxiteHarvester = new WallCrafter("bauxite-harvester") {{
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
        wallExtractor = new WallPayloadDrill("wall-extractor") {{
            requirements(Category.production, with(lead, 25, AquaItems.bauxite, 30));
            size = 3;
            attributeBlockMap.put(AquaAttributes.gallium, AquaDefense.galliumWall);
            attributeBlockMap.put(AquaAttributes.bauxite, AquaDefense.bauxiteWall);
            envEnabled |= Env.underwater;
        }};
        CompressionDrill = new RechargeDrill("compression-drill") {{
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
        ramDrill = new BurstDrill("ram-drill") {{
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
        ceramicKiln = new GenericCrafter("ceramic-kiln") {{
            requirements(Category.crafting, with(lead, 120, bauxite, 90));
            ambientSound = Sounds.torch;
            craftTime = 60;
            researchCostMultiplier = 0.03f;
            size = 3;
            consumeItems(with(lead, 2, bauxite, 1));
            consumeLiquid(arkycite, 12 / 60f);
            envEnabled |= Env.underwater;
            ambientSoundVolume = 0.02f;
            itemCapacity = 15;
            liquidCapacity = 20f * 5;
            outputItem = new ItemStack(ceramic, 2);
            consumePower(1.5f);
            craftEffect = new ParticleEffect() {{
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
        cultivationChamber = new GenericCrafter("cultivation-chamber") {{
            requirements(Category.crafting, with(lead, 120, bauxite, 90));
            size = 3;
            ambientSound = Sounds.machine;
            researchCostMultiplier = 0.2f;
            ambientSoundVolume = 0.07f;
            liquidCapacity = 10f * 5;
            squareSprite = false;
            craftTime = 90;
            consumeItems(with(bauxite, 1));
            outputLiquid = new LiquidStack(arkycite, 6 / 60f);
            envEnabled |= Env.underwater;
            craftEffect = new ParticleEffect() {{
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
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawCells() {{
                range = 9;
                particles = 200;
                particleColorFrom = Color.valueOf("18c87b");
                particleColorTo = Color.valueOf("2ca197");
                color = Color.valueOf("266b4d");
            }}, new DrawCircles() {{
                timeScl = 400;
                amount = 6;
                radius = 9;
                strokeMax = 1.2f;
                color = Color.valueOf("18c87b");
            }}, new DrawRegion("-top"));
        }};
        chireniumElectroplater = new GenericCrafter("chirenium-electroplater") {{
            requirements(Category.crafting, with(lead, 200, bauxite, 150, nickel, 60, gallium, 40));
            envEnabled |= Env.underwater;
            researchCostMultiplier = 0.03f;
            liquidCapacity = 25f * 5;
            craftTime = 90;
            ambientSound = Sounds.bioLoop;
            consumeLiquid(arkycite, 12 / 60f);
            ambientSoundVolume = 0.06f;
            squareSprite = false;
            outputItem = new ItemStack(chirenium, 1);
            liquidCapacity = 90;
            size = 4;
            itemCapacity = 30;
            consumeItems(with(lead, 2, nickel, 1));
            consumePower(2f);
            craftEffect = new ParticleEffect() {{
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
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(arkycite, 2), new DrawRegion("-cell1"), new DrawLiquidTile(arkycite, 2) {{
                alpha = 0.6f;
            }}, new DrawBubbles(Color.valueOf("84b886")) {{
                sides = 8;
                amount = 120;
                spread = 12;
            }}, new DrawRegion("-cell2"), new DrawRegion("-top"));
        }};
        clarifier = new GenericCrafter("clarifier") {{
            requirements(Category.crafting, with(lead, 90, nickel, 90, ceramic, 30, gallium, 110));
            size = 3;
            squareSprite = false;
            liquidCapacity = 90;
            envEnabled |= Env.underwater;
            outputItem = new ItemStack(salt, 1);
            consumeLiquids(LiquidStack.with(brine, 24 / 60f, arkycite, 4 / 60));
            consumePower(90 / 60f);
            outputLiquid = new LiquidStack(hydroxide, 12 / 60f);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(brine, 2), new DrawCultivator() {{
                timeScl = 120;
                bottomColor = Color.valueOf("85966a");
                plantColorLight = Color.valueOf("f1ffdc");
                plantColor = Color.valueOf("728259");
                radius = 2.5f;
                bubbles = 32;
                spread = 8;
            }}, new DrawCells() {{
                range = 9;
                particles = 160;
                lifetime = 60f * 5f;
                particleColorFrom = Color.valueOf("f1ffdc");
                particleColorTo = Color.valueOf("728259");
                color = Color.valueOf("oooooooo");
            }}, new DrawRegion("-top"), new DrawGlowRegion("-glow") {{
                alpha = 0.7f;
                glowScale = 6;
            }});
        }};
        electrumDrill = new Drill("electrum-drill") {{
            requirements(Category.production, with(electrum, 20));
            size = 2;
            tier = 3;
            //hella hard to boost so a bit extra boost intensity
            liquidBoostIntensity = 1.5f;
            itemCapacity = 20;
            drillTime = 430;
            consumeLiquid(dioxide, 2 / 60f);
            consumeLiquid(water, 8 / 60f).boost();
        }};
        electrumBore = new BeamDrill("electrum-bore") {{
            requirements(Category.production, with(electrum, 35, lead, 20));
            size = 2;
            drillTime = 400;
            liquidCapacity = 45;
            range = 3;
            tier = 4;
            sparkColor = Color.valueOf("f28c8c");
            glowColor = Color.valueOf("ffd0d0");
            boostHeatColor = Color.valueOf("f28c8c");
            consumePower(16 / 60f);
            consumeLiquid(oxygen, 1 / 60f);
        }};
        silverDrill = new Drill("silver-drill") {{
            requirements(Category.production, with(electrum, 45, silver, 20, silicon, 40));
            size = 3;
            liquidCapacity = 60;
            tier = 4;
            itemCapacity = 35;
            drillTime = 280;
            //this drill has to power scale a lot
            // so any extra complexity for blocks like this needs to be met with high rewards
            liquidBoostIntensity = 1.8f;
            warmupSpeed = 0.08f;
            consumeLiquid(dioxide, 6 / 60f);
            consumeLiquid(cryofluid, 6 / 60f).boost();
            consumePower(64 / 60f);
        }};
        chromiumExtractor = new WallCrafter("chromium-extractor") {{
            requirements(Category.production, with(silver, 15, silicon, 20, titanium, 10, cupronickel, 5));
            size = 1;
            drillTime = 160;
            consumePower(16 / 60f);
            itemCapacity = 60;
            attribute = AquaAttributes.chromium;
            output = AquaItems.chromium;
        }};
        atmoshpericSeperator = new GenericCrafter("atmospheric-separator") {{
            requirements(Category.crafting, with(lead, 50, electrum, 75));
            size = 3;
            rotateDraw = false;
            liquidCapacity = 50;
            consumeLiquid(water, 15 / 60f);
            outputLiquids = LiquidStack.with(dioxide, 16f / 60f, oxygen, 5f / 60f);
            liquidOutputDirections = new int[]{3, 1};
            consumePower(1f);
            craftTime = 10f;
            rotate = true;
            invertFlip = true;
            warmupSpeed = 0.01f;
            group = BlockGroup.liquids;
            itemCapacity = 0;
            drawer = new DrawMulti(new DrawRegion("-bottom"),
                    new DrawBlurSpin("-rotator", 0.6f * 9f) {{
                        blurThresh = 0.6f;
                    }}, new DrawDefault(), new DrawLiquidOutputs(),
                    new DrawParticles() {{
                        color = Color.valueOf("cbcbce");
                        alpha = 0.3f;
                        particleSize = 2.5f;
                        particles = 6;
                        particleRad = 12f;
                        particleLife = 160f;
                    }}
            );
        }};
        carbonicBubbler = new GenericCrafter("carbonic-bubbler") {{
            requirements(Category.crafting, with(lead, 20, titanium, 30, electrum, 20));
            size = 2;
            craftTime = 60;
            hasLiquids = true;
            liquidCapacity = 45;
            craftEffect = Fx.coalSmeltsmoke;
            outputLiquid = new LiquidStack(carbonicAcid, 12 / 60f);
            consumeLiquids(LiquidStack.with(water, 20 / 60f, dioxide, 8 / 60f));
            consumePower(16 / 60f);
            drawer = new DrawMulti(new DrawRegion("-bottom"),
                    new DrawLiquidTile(water),
                    new DrawBubbles(Color.valueOf("88a4ff")) {{
                        spread = 4;
                        amount = 48;
                        radius = 1.5f;
                    }},
                    new DrawLiquidTile(dioxide) {{
                        alpha = 0.6f;
                    }},
                    new DrawDefault());
        }};
        electrumCombustor = new GenericCrafter("electrum-combustor") {{
            requirements(Category.crafting, with(electrum, 30, titanium, 25, lead, 40));
            size = 2;
            craftTime = 45;
            hasLiquids = true;
            liquidCapacity = 30;
            consumeLiquid(carbonicAcid, 4 / 60f);
            craftEffect = Fx.smeltsmoke;
            consumeItem(electrum, 3);
            outputItems = new ItemStack[]{new ItemStack(copper, 1), new ItemStack(silver, 2)};
        }};
        cryofluidChurn = new GenericCrafter("cryofluid-churn") {{
            requirements(Category.crafting, with(lead, 35, titanium, 40, silicon, 35));
            size = 2;
            hasLiquids = true;
            liquidCapacity = 90;
            itemCapacity = 20;
            consumeLiquid(water, 36 / 60f);
            consumeItem(titanium, 2);
            outputLiquid = new LiquidStack(cryofluid, 18 / 60f);
            consumePower(64 / 60f);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(cryofluid), new DrawDefault());
        }};
        siliconHearth = new GenericCrafter("silicon-hearth") {{
            requirements(Category.crafting, with(lead, 90, copper, 40, titanium, 40));
            size = 3;
            itemCapacity = 35;
            craftEffect = new MultiEffect(Fx.smeltsmoke, Fx.mine);
            craftTime = 60;
            consumePower(64 / 60f);
            consumeItems(with(sand, 5, arsenic, 2));
            outputItem = new ItemStack(silicon, 5);
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame());
        }};
        hydroponicsBasin = new GenericCrafter("hydroponics-basin") {{
            requirements(Category.production, with(inconel, 45, cupronickel, 120, titanium, 90, silicon, 80));
            size = 4;
            warmupSpeed = 0.01f;
            consumePower(128 / 60f);
            consumeLiquid(water, 48 / 60f);
            outputLiquid = new LiquidStack(bioPulp, 24 / 60f);
            drawer = new DrawMulti(new DrawRegion("-bottom"),
                    new DrawLiquidTile(water) {{
                        alpha = 0.6f;
                    }},
                    new DrawRegion("-bed-shadow"),
                    new DrawRegion("-bed"),
                    new DrawLiquidTile(bioPulp),
                    new DrawCells() {{
                        recurrence = 3;
                        radius = 2;
                        lifetime = 240;
                        range = 12;
                        particles = 24;
                        particleColorFrom = Color.valueOf("72744c");
                        particleColorTo = Color.valueOf("92ba76");
                    }},
                    new DrawDefault());
            cupronickelAlloyer = new GenericCrafter("cupronickel-alloyer") {{
                requirements(Category.crafting, with(silicon, 90, copper, 180, lead, 90, nickel, 120));
                size = 3;
                updateEffect = Fx.steam;
                updateEffectChance = 0.02f;
                craftTime = 30;
                ambientSound = Sounds.smelter;
                craftEffect = new MultiEffect(Fx.surgeCruciSmoke, Fx.mineBig);
                consumeItems(with(copper, 2, nickel, 3));
                outputItem = new ItemStack(cupronickel, 2);
                itemCapacity = 35;
                liquidCapacity = 90;
                consumePower(256 / 60f);
                consumeLiquid(cryofluid, 9 / 60f);
                drawer = new DrawMulti(new DrawDefault(), new DrawFlame(), new DrawGlowRegion() {{
                    alpha = 0.65f;
                    color = Color.valueOf("e68569");
                    glowIntensity = 0.3f;
                    glowScale = 6f;
                }});
            }};
        }};
        inconelForge = new GenericCrafter("inconel-forge") {{
            requirements(Category.crafting, with(silicon, 200, cupronickel, 180, titanium, 150, nickel, 150, lead, 200));
            size = 4;
            craftTime = 45;
            outputItem = new ItemStack(inconel, 3);
            consumeItems(with(nickel, 3, titanium, 2, chromium, 3));
            consumeLiquids(LiquidStack.with(water, 48 / 60f, oil, 24 / 60f));
            consumePower(256 / 60f);
            craftEffect = new MultiEffect(new RadialEffect(Fx.surgeCruciSmoke, 4, 90f, 8f) {{
                offset = 5;
            }});
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawSoftParticles() {{
                alpha = 0.35f;
                particleRad = 12f;
                particleSize = 9f;
                particleLife = 120f;
                particles = 27;
            }}, new DrawLiquidTile(water) {{
                alpha = 0.9f;
                padLeft = 20;
            }}, new DrawLiquidTile(oil) {{
                alpha = 0.9f;
                padRight = 20;
            }}, new DrawDefault());
        }};
        magmaDiffser = new MagmaProcessor("magma-diffuser") {{
            requirements(Category.crafting, with(lead, 450, bauxite, 200));
            size = 5;
            squareSprite = false;
            itemCapacity = 300;
            liquidCapacity = 500;
            consumeItem(borax, 5).boost();
            craftTime = 300;
            outputItems = new ItemStack[]{new ItemStack(lead, 25), new ItemStack(bauxite, 30), new ItemStack(silicon, 25)};
            consumeLiquid(magma, 120 / 60f);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawRegion("-ring"), new DrawAdvancedPistons() {{
                sinMag = 2f;
                sinScl = 10f;
                sideOffset = Mathf.pi * 2;
            }}, new DrawRegion("-fan-shadow") {{
                rotation = 0;
                x = -1;
                y = -1;
                rotateSpeed = 1.5f;
            }}, new DrawRegion("-fan") {{
                rotation = 15;
                spinSprite = true;
                rotateSpeed = 1.5f;
            }}, new DrawRegion("-fan") {{
                rotation = 45;
                spinSprite = true;
                rotateSpeed = 1.5f;
            }}, new DrawRegion("-fan") {{
                spinSprite = true;
                rotateSpeed = 1.5f;
            }}, new DrawRegion("-fan") {{
                rotation = 30;
                spinSprite = true;
                rotateSpeed = 1.5f;
            }}, new DrawRegion("-fan") {{
                rotation = 75;
                spinSprite = true;
                rotateSpeed = 1.5f;
            }}, new DrawRegion("-fan") {{
                rotation = 60;
                spinSprite = true;
                rotateSpeed = 1.5f;
            }}, new DrawDefault(), new DrawLiquidTile(magma, 13.25f), new DrawRegion("-top"));
        }};
        magmaTap = new AquaAttributeCrafter("magma-tap") {{
            requirements(Category.production, with(lead, 250, bauxite, 120));
            size = 4;
            updateEffect = Fx.steam;
            attribute = Attribute.heat;
            minEfficiency = 1;
            maxBoost = 3;
            baseEfficiency = 0;
            updateEffectChance = 0.02f;
            boostScale = 1f / 8f;
            craftTime = 5*60f;
            ambientSound = Sounds.smelter;
            squareSprite = false;
            liquidCapacity = 900;
            outputLiquid = new LiquidStack(magma, 1f);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(magma, 5), new DrawDefault(), new DrawGlowRegion() {{
                alpha = 0.65f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.3f;
                glowScale = 6f;
            }});
        }};
        bauxiteCentrifuge = new GenericCrafter("bauxite-centrifuge") {{
            requirements(Category.crafting, with(lead, 400, bauxite, 300, silicon, 250));
            craftTime = 60;
            consumeItem(bauxite, 10);

            outputItems = new ItemStack[]{
                    new ItemStack(silicon, 3),
                    new ItemStack(ferricMatter, 5),
                    new ItemStack(aluminum, 2)
            };

            outputLiquid = new LiquidStack(oxygen, 20 / 60f);
            //   liquidOutputPoints = new Point2[]{
            //           new Point2(0, 0)
            //    };

            size = 6;
            itemCapacity = 60;
            warmupSpeed = 0.01f;
            squareSprite = false;

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawOrbitRegions("-capsule", 8, 11f, 2f), new DrawRegion("-lower-toob"), new DrawLiquidTile(oxygen){{
                padBottom = 3;
                padRight = 3;
                padTop = 22;
                padLeft = 22;
            }}, new DrawDefault(), new DrawRegion("-toob"), new DrawAdvancedPistons(){{
                angleOffset = 270;
                sides = 1;
            }});
        }};
        ferroSiliconFoundry = new AquaGenericCrafter("ferrosilicon-foundry") {{
            requirements(Category.crafting, with(lead, 680, bauxite, 250, silicon, 500, aluminum, 90));
            size = 6;
            squareSprite = false;
            itemCapacity = 80;

            liquidBoostIntensity = 2;
            boostItemUseTime = 90f;
            craftTime = 600;
            consumeItems(with(ferricMatter, 5, silicon, 15));
            outputItem = new ItemStack(ferrosilicon, 20);
            consumeLiquid(magma, 80 / 60f).optional = false;
            itemBooster =  consumeItem(borax, 2).boost();
            booster = consume(new ConsumeLiquidNew(fumes, 5/60f)).boost();
            liquidCapacity = 200;
            updateEffect = Fx.coalSmeltsmoke;
            updateEffectChance = 0.05f;
            craftEffect = Fx.reactorsmoke;
            ambientSound = Sounds.electricHum;
            ambientSoundVolume = 0.1f;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(magma){{
                padRight = 40;
                padTop = 25;
                padBottom = 7;
            }}, new DrawRegion("-mid"),new DrawArcSmelt(){{
                circleStroke = 1.2f;
                particles = 10;
                particleLife = 35;
                particleStroke = 1;
                particleRad = 6;
                particleLen = 2;

                circleSpace = 6;
                flameRadiusScl = 12;
                flameRadiusMag = 0.7f;
                flameColor = Color.valueOf("ffd2c5");
                midColor = Color.valueOf("e17166");
            }}, new DrawCrucibleFlame(){{
                circleStroke = 1.6f;
                particleLife = 60;
                particleSize = 5;
                particleRad = 12;
                circleSpace = 5;
                flameRadiusScl = 14;
                flameRadiusMag = 0.9f;
                rotateScl = 3;
            }}, new DrawDefault(), new DrawGlowRegion(){{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }},new DrawGlowRegion("-glow-2"){{
                glowIntensity = 0.9f;
                glowScale = 8;
                alpha = 0.6f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
        brineCatalysisArray = new GenericCrafter("brine-catalysis-array") {{
            requirements(Category.crafting, with(lead, 900, silicon, 500, aluminum, 150, ferricMatter, 150));
            //bigg boi
            size = 5;
            craftTime = 30*60f;
            liquidCapacity = 900;
            itemCapacity = 120;
            outputLiquid = new LiquidStack(hydroxide, 90/60f);
            outputItems = new ItemStack[]{new ItemStack(borax, 20), new ItemStack(salt, 60)};
            consumeLiquids(LiquidStack.with(brine, 160 / 60f, magma, 20 / 60f));
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawCultivator() {{
                timeScl = 180;
                bottomColor = Color.valueOf("85966a");
                plantColorLight = Color.valueOf("f1ffdc");
                plantColor = Color.valueOf("728259");
                radius = 2.5f;
                bubbles = 320;
                spread = 14;
            }}, new DrawCells() {{
                range = 14;
                particles = 350;
                lifetime = 90f * 5f;
                particleColorFrom = Color.valueOf("f1ffdc");
                particleColorTo = Color.valueOf("728259");
                color = Color.valueOf("oooooooo");
            }}, new DrawLiquidTile(brine, 1), new DrawLiquidTile(hydroxide, 1),
                    new DrawBubbles(){{
                        spread = 14;
                        color =  Color.valueOf("e7cfff");
                        timeScl = 15;
                        sides = 8;
                        amount = 140;
                    }}, new DrawSoftParticles(){{
                particleLife = 120;
                particles = 45;
                rotateScl = 3;
                particleRad = 8f;
                particleSize = 5f;
                color = Color.valueOf("f0c180");
                color2 = Color.valueOf("fff89d");
            }}, new DrawDefault(), new DrawGlowRegion("-glow") {{
                alpha = 0.5f;
                glowScale = 10;
                glowIntensity = 0.4f;
                color = Color.valueOf("ea9565");
            }}, new DrawGlowRegion("-glow-2") {{
                alpha = 0.75f;
                glowScale = 8;
                glowIntensity = 0.8f;
                color = Color.valueOf("ffbeb0");
            }});
        }};
        fumeFilter = new AttributeCrafter("fume-filter"){{
            requirements(Category.crafting, with(lead, 500, silicon, 800, aluminum, 300, ferricMatter, 300));
            size = 6;
            outputLiquid = new LiquidStack(fumes, 20/60f);
            attribute = Attribute.steam;
            boostScale = 1/18f;
            maxBoost = 2;
            baseEfficiency = 0;
            liquidCapacity = 250f;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(fumes, 2){{
                alpha = 0.7f;
            }}, new DrawDefault(), new DrawRegion("-fan"){{
                y = 4;
                x = 4;
                spinSprite = true;
                rotateSpeed = 2;
            }}, new DrawRegion("-fan"){{
                y = 4;
                x = 4;
                rotation = 45;
                spinSprite = true;
                rotateSpeed = 2;
            }}, new DrawRegion("-fan"){{
                y = 4;
                x = -8;
                spinSprite = true;
                rotateSpeed = 2;
            }}, new DrawRegion("-fan"){{
                y = 4;
                x = -8;
                rotation = 45;
                spinSprite = true;
                rotateSpeed = 2;
            }}, new DrawLiquidTile(fumes, 1){{
                alpha = 0.6f;
            }}, new DrawRegion("-top"));
        }};
    }}