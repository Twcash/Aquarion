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
import mindustry.content.Blocks;
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
import static aquarion.world.graphics.AquaFx.azuriteSmelt;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.type.ItemStack.with;

//What was all of this even for
public class AquaCrafters {
    public static Block fumeMixer, manguluminCrucible, chireniumElectroplater, saltDegradationMatrix, CaustroliteKiln, VacodurAmalgamator, InvarBlastFurnace, plasmaExtractor, towaniteReductionVat, azuriteKiln, slagRefinementAssemblage, fumeFilter, brineCatalysisArray, ferroSiliconFoundry, bauxiteCentrifuge, magmaTap, chromiumExtractor, silverDrill, electrumBore, electrumDrill,
            atmoshpericSeperator,
             siliconHearth, magmaDiffser,
            carbonicBubbler, electrumCombustor, cryofluidChurn, cupronickelAlloyer, hydroponicsBasin, inconelForge;

    public static void loadContent() {
        Blocks.conveyor.envDisabled = Env.underwater;
        Blocks.itemBridge.envDisabled = Env.underwater;
        Blocks.invertedSorter.envDisabled = Env.underwater;
        Blocks.router.envDisabled = Env.underwater;
        Blocks.distributor.envDisabled = Env.underwater;
        Blocks.graphitePress.envDisabled = Env.underwater;
        Blocks.siliconSmelter.envDisabled = Env.underwater;
        Blocks.junction.envDisabled = Env.underwater;
        Blocks.sorter.envDisabled = Env.underwater;
        Blocks.pulverizer.envDisabled = Env.underwater;
        Blocks.pyratiteMixer.envDisabled = Env.underwater;
        Blocks.sporePress.envDisabled = Env.underwater;
        Blocks.battery.envDisabled = Env.underwater;
        Blocks.powerNode.envDisabled = Env.underwater;
        Blocks.solarPanel.envDisabled = Env.underwater;
        Blocks.combustionGenerator.envDisabled = Env.underwater;
        Blocks.copperWall.envDisabled = Env.underwater;
        Blocks.copperWallLarge.envDisabled = Env.underwater;
        Blocks.groundFactory.envDisabled = Env.underwater;
        Blocks.airFactory.envDisabled = Env.underwater;
        Blocks.repairPoint.envDisabled = Env.underwater;
        Blocks.overflowGate.envDisabled = Env.underwater;
        Blocks.underflowGate.envDisabled = Env.underwater;
        Blocks.cultivator.envDisabled = Env.underwater;
        Blocks.mechanicalDrill.envDisabled = Env.underwater;
        Blocks.duo.envDisabled = Env.underwater;
        Blocks.arc.envDisabled = Env.underwater;
        Blocks.scatter.envDisabled = Env.underwater;
        Blocks.mender.envDisabled = Env.underwater;
        Blocks.coreShard.envDisabled = Env.underwater;
        Blocks.logicDisplay.envDisabled = Env.underwater;
        Blocks.shockMine.envDisabled = Env.underwater;
        Blocks.additiveReconstructor.envDisabled = Env.underwater;


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
            requirements(Category.crafting, with(lead, 150, bauxite, 120, silicon, 80));
            size = 5;
            squareSprite = false;
            alwaysUnlocked = true;
            itemCapacity = 300;
            liquidCapacity = 500;
            ignoreLiquidFullness = true;
            consumeItem(borax, 5).boost();
            craftTime = 300;
            outputLiquid = new LiquidStack(slag, 40/60f);
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
            }}, new DrawBetterRegion("-fan") {{
                rotation = 15;
                spinSprite = true;
                rotateSpeed = 1.5f;
            }}, new DrawBetterRegion("-fan") {{
                rotation = 45;
                spinSprite = true;
                rotateSpeed = 1.5f;
            }}, new DrawBetterRegion("-fan") {{
                spinSprite = true;
                rotateSpeed = 1.5f;
            }}, new DrawBetterRegion("-fan") {{
                rotation = 30;
                spinSprite = true;
                rotateSpeed = 1.5f;
            }}, new DrawBetterRegion("-fan") {{
                rotation = 75;
                spinSprite = true;
                rotateSpeed = 1.5f;
            }}, new DrawBetterRegion("-fan") {{
                rotation = 60;
                spinSprite = true;
                rotateSpeed = 1.5f;
            }}, new DrawDefault(), new DrawLiquidTile(magma, 13.25f), new DrawRegion("-top"));
        }};
        magmaTap = new AttributeCrafter("magma-tap") {{
            requirements(Category.production, with(lead, 100, silicon, 100));
            size = 4;
            alwaysUnlocked = true;
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
            requirements(Category.crafting, with(lead, 400, bauxite, 300, silicon, 250, copper, 150));
            craftTime = 60;
            consumeItem(bauxite, 10);
            researchCostMultiplier = 1;

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
            ignoreLiquidFullness = true;
            dumpExtraLiquid = true;
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
            requirements(Category.crafting, with( silicon, 750, aluminum, 200, copper, 350));
            size = 6;
            squareSprite = false;
            itemCapacity = 80;
            researchCostMultiplier = 0.25f;
            liquidBoostIntensity = 2;
            boostItemUseTime = 90f;
            craftTime = 600;
            consumeItems(with(ferricMatter, 5, silicon, 15));
            outputItem = new ItemStack(ferrosilicon, 20);
            consumeLiquids(LiquidStack.with(fumes, 15 / 60f, hydroxide, 15/60f)).optional = false;
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
            requirements(Category.crafting, with(aluminum, 200, silicon, 250, cobalt, 600));
            //bigg boi
            size = 5;
            craftTime = 30*60f;
            liquidCapacity = 900;
            researchCostMultiplier = 0.25f;
            itemCapacity = 250;
            outputLiquid = new LiquidStack(hydroxide, 40/60f);
            outputItems = new ItemStack[]{new ItemStack(borax, 90), new ItemStack(salt, 150)};
            consumeLiquids(LiquidStack.with(brine, 160 / 60f, fumes, 20 / 60f));
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
            requirements(Category.crafting, with(silicon, 200, copper, 250, ferricMatter, 300));
            size = 6;
            outputLiquid = new LiquidStack(fumes, 40/60f);
            attribute = Attribute.steam;
            boostScale = 1/18f;
            maxBoost = 2;
            baseEfficiency = 0;
            liquidCapacity = 250f;
            researchCostMultiplier = 0.25f;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(fumes, 2){{
                alpha = 0.7f;
            }}, new DrawDefault(), new DrawBetterRegion("-fan"){{
                y = 4;
                x = 4;
                spinSprite = true;
                rotateSpeed = 2;
            }}, new DrawBetterRegion("-fan"){{
                y = 4;
                x = 4;
                rotation = 45;
                spinSprite = true;
                rotateSpeed = 2;
            }}, new DrawBetterRegion("-fan"){{
                y = 4;
                x = -8;
                spinSprite = true;
                rotateSpeed = 2;
            }}, new DrawBetterRegion("-fan"){{
                y = 4;
                x = -8;
                rotation = 45;
                spinSprite = true;
                rotateSpeed = 2;
            }}, new BetterDrawParticles(){{
                color = Color.valueOf("836c59");
                alpha = 0.4f;
                y = 4;
                sides = 12;
                x = -8;
                particleSize = 2f;
                particles = 12;
                particleRad = 6f;
                particleLife = 60f;
            }}, new BetterDrawParticles(){{
                color = Color.valueOf("9f846d");
                alpha = 0.4f;
                y = 4;
                x = -8;
                particleSize = 2f;
                particles = 12;
                sides = 12;
                particleRad = 6f;
                particleLife = 60f;
            }}, new BetterDrawParticles(){{
                color = Color.valueOf("836c59");
                alpha = 0.4f;
                y = 4;
                x = 4;
                sides = 12;
                particleSize = 1f;
                particles = 14;
                particleRad = 7f;
                rotateScl = 2;
                particleLife = 45f;
            }}, new BetterDrawParticles(){{
                color = Color.valueOf("9f846d");
                alpha = 0.4f;
                sides = 12;
                y = 4;
                x = 4;
                particleSize = 1f;
                particles = 14;
                particleRad = 7f;
                particleLife = 45f;
                rotateScl = 2;
            }}, new DrawLiquidTile(fumes, 1){{
                alpha = 0.6f;
            }}, new DrawRegion("-top"), new DrawGlowRegion(){{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }},new DrawGlowRegion("-glow1"){{
                glowIntensity = 0.9f;
                glowScale = 8;
                alpha = 0.6f;
                color = Color.valueOf("ffc99e");
            }});
        }};
        slagRefinementAssemblage = new GenericCrafter("slag-refinement-array"){{
            requirements(Category.crafting, with(lead, 2000, ferrosilicon, 1200, aluminum, 300, silicon, 3000));
            size = 7;
            consumePower(80/60f);
            itemCapacity = 300;
            liquidCapacity = 400;
            updateEffect = Fx.ventSteam;
            updateEffectChance = 0.01f;
            squareSprite = false;
            researchCostMultiplier = 0.25f;

            craftTime = 600;
            consumeLiquids(LiquidStack.with(slag, 40 / 60f, fumes, 15 / 60));
            outputItems = new ItemStack[]{new ItemStack(nickel, 50),
                    new ItemStack(silicon, 80),
                    new ItemStack(bauxite, 30),
                    new ItemStack(ferricMatter, 40)};
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(fumes, 1), new DrawLiquidTile(slag){{
                padBottom = 13;
                padTop = 13;
                padRight = 9;
                padLeft = 9;

            }}, new DrawDefault(), new DrawGlowRegion());
        }};
        InvarBlastFurnace = new GenericCrafter("invar-blast-furnace"){{
            requirements(Category.crafting, with(silicon, 500, aluminum, 200, bauxite, 1500));
            size = 7;
            craftTime = 30*60f;
            outputLiquid = new LiquidStack(dioxide, 40/60f);
            consumeItems(with(nickel, 25, ferricMatter, 75));
            consumeLiquids(LiquidStack.with(fumes, 60 / 60f));
            consumePower(256/60f);
            squareSprite = false;
            researchCostMultiplier = 0.25f;
            outputItem = new ItemStack(invar, 100);
            liquidCapacity = 200;
            itemCapacity = 120;
        }};
        manguluminCrucible = new GenericCrafter("mangalumin-crucible"){{
            requirements(Category.crafting, with(silicon, 500, aluminum, 200, bauxite, 1500));
            size = 7;
            craftTime = 15*60f;
            outputLiquid = new LiquidStack(dioxide, 35/60f);
            consumeItems(with(manganese, 30, aluminum, 30, copper, 15));
            consumeLiquids(LiquidStack.with(fumes, 60 / 60f));
            consumePower(256/60f);
            squareSprite = false;
            outputItem = new ItemStack(mangalumin, 75);
            liquidCapacity = 200;
            itemCapacity = 90;
        }};
        VacodurAmalgamator = new GenericCrafter("vacodur-almalgamator"){{
            requirements(Category.crafting, with(silicon, 500, aluminum, 200, bauxite, 1500));
            size = 8;
            craftTime = 20*60f;
            consumeItems(with(cobalt, 80, ferricMatter, 120));
            ignoreLiquidFullness = true;
            outputLiquid = new LiquidStack(dioxide, 20/60f);
            consumeLiquids(LiquidStack.with(fumes, 20 / 60f));
            consumePower(256*2/60f);
            squareSprite = false;
            outputItem = new ItemStack(vacodur, 200);
            liquidCapacity = 200;
            itemCapacity = 90;
        }};
        CaustroliteKiln = new GenericCrafter("caustrolite-kiln"){{
            requirements(Category.crafting, with(silicon, 500, aluminum, 200, bauxite, 1500));
            researchCostMultiplier = 0.25f;
            size = 5;
            consumeItems(with(arsenic, 40, lead, 200));
            consumeLiquids(LiquidStack.with(fumes, 20 / 60f, chlorine, 10/60f, hydroxide, 1));
            consumePower(160/60f);
            craftTime = 10*60f;
            outputItem = new ItemStack(caustrolite, 120);
            squareSprite = false;
            liquidCapacity = 200;
            itemCapacity = 90;
        }};
        saltDegradationMatrix = new GenericCrafter("salt-degredation-matrix"){{
            size = 8;
            squareSprite = false;
            consumePower(64/60f);
            craftTime = 20*60f;
            consumeItem(salt, 120);
            researchCostMultiplier = 0.25f;

            outputLiquids = LiquidStack.with(chlorine, 1.0f, hydroxide, 1.0f);
            outputItem = new ItemStack(borax, 20);
            liquidCapacity = 200;
            itemCapacity = 90;
        }};
        chireniumElectroplater = new GenericCrafter("chirenium, electroplater"){{
            requirements(Category.crafting, with(silicon, 500, aluminum, 200, bauxite, 1500));
            size = 10;
            squareSprite = false;
            researchCostMultiplier = 0.25f;

            consumeLiquids(LiquidStack.with(fumes, 80 / 60f, chlorine, 90/60f, magma, 1, carbonicAcid, 40/60f));
            consumePower(160/60f);
            consumeItems(ItemStack.with(aluminum, 25, arsenic, 20, manganese, 40));
            craftTime = 10*60f;

            outputItem = new ItemStack(chirenium, 40);
            liquidCapacity = 1000;
            itemCapacity = 120;
        }};
        azuriteKiln = new GenericCrafter("azurite-kiln"){{
            requirements(Category.crafting, with(silicon, 500, bauxite, 750, lead, 750));
            craftTime = 5*60f;
            consumeLiquid(magma, 15/60f);
            consumeItem(azurite, 30);
            itemCapacity = 300;
            outputItem = new ItemStack(copper, 90);
            outputLiquid = new LiquidStack(hydroxide, 10/60f);
            liquidCapacity = 500;
            ignoreLiquidFullness = true;
            dumpExtraLiquid = true;
            size = 5;
            researchCostMultiplier = 0.25f;
            updateEffect = Fx.steam;
            updateEffectChance = 0.05f;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(magma, 1.5f){{ alpha = 0.6f;}}, new DrawDefault(), new DrawGlowRegion() {{
                alpha = 0.65f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.3f;
                glowScale = 6f;
            }});
            towaniteReductionVat = new GenericCrafter("towanite-reduction-vat"){{
                requirements(Category.crafting, with( lead, 500, silicon, 250, bauxite, 500));
                size = 7;
                itemCapacity = 150;
                craftTime = 3*60f;
                researchCostMultiplier = 0.25f;
                consumeItem(towanite, 15);
                consumeLiquid(fumes, 30/60f);
                outputItems = new ItemStack[]{
                        new ItemStack(copper, 15),
                        new ItemStack(brimstone, 30),
                        new ItemStack(ferricMatter, 15)};

                drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawDefault(), new DrawGlowRegion());
            }};
            plasmaExtractor = new ModifiedbeamDrill("plasma-extractor"){{
                requirements(Category.production, with(silicon, 300, lead ,250));
                tier = 1;
                itemCapacity = 50;
                squareSprite = false;
                researchCostMultiplier = 0.25f;

                drillTime = 300;
                size = 4;
                range = 8;
                consumeLiquid(slag, 10/60f);
                consumeLiquid(fumes, 5/60f).boost();
                optionalBoostIntensity = 1.5f;
                heatColor  = Color.valueOf("9d8cf2");
                boostHeatColor = Color.valueOf("e1f28c");
            }};
            fumeMixer = new GenericCrafter("fume-mixer"){{
                requirements(Category.crafting, with(silicon, 250, aluminum, 300, copper, 200));
                //Leave your skin at the door. One wall. Two Wall. No floor. No walls.
                size = 6;
                craftTime = 10*60f;
                squareSprite = false;
                baseExplosiveness = 10;
                researchCostMultiplier = 0.25f;
                consumeLiquids(LiquidStack.with(magma, 120 / 60f, dioxide, 40/60f));
                outputLiquid = new LiquidStack(fumes, 120);
                consumeItems(ItemStack.with(borax, 60, brimstone, 150));
                itemCapacity = 350;
                liquidCapacity = 900;
            }};
        }};
    }}