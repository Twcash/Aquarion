package aquarion.blocks;

import aquarion.AquaAttributes;
import aquarion.AquaItems;
import aquarion.AquaSounds;
import aquarion.world.blocks.production.*;
import aquarion.world.consumers.ConsumeLiquidNew;
import aquarion.world.consumers.ConsumeRecipes;
import aquarion.world.consumers.Recipe;
import aquarion.world.graphics.*;

import arc.func.Cons;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.content.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.RadialEffect;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.*;
import static aquarion.planets.AquaPlanets.*;
import static arc.math.Interp.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.type.ItemStack.with;

//What was all of this even for
public class AquaCrafters {
    public static Block atmosphericIntake, AnnealingOven, SolidBoiler, CentrifugalPump, harvester,galenaCrucible ,DrillDerrick, beamBore, fumeMixer, manguluminCrucible, chireniumElectroplater, saltDegradationMatrix, CaustroliteKiln, VacodurAmalgamator, InvarBlastFurnace, plasmaExtractor, towaniteReductionVat, azuriteKiln, slagRefinementAssemblage, fumeFilter, FractionalDistillery, ferroSiliconFoundry, bauxiteCentrifuge, magmaTap, chromiumExtractor, silverDrill, electrumBore, electrumDrill,
            atmoshpericSeperator, fumeSeparator,
             siliconHearth, magmaDiffser,
            carbonicBubbler, electrumCombustor, cryofluidChurn, cupronickelAlloyer, hydroponicsBasin, inconelForge;

    public static void loadContent() {
        disableVanilla();

        electrumDrill = new Drill("electrum-drill") {{
            requirements(Category.production, with(electrum, 20));
            size = 2;
            buildVisibility = BuildVisibility.sandboxOnly;
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
            tier = 4;
            liquidCapacity = 45;
            drillTime = 400;
            buildVisibility = BuildVisibility.sandboxOnly;
            range = 3;
            sparkColor = Color.valueOf("f28c8c");
            glowColor = Color.valueOf("ffd0d0");
            boostHeatColor = Color.valueOf("f28c8c");
            consumePower(16 / 60f);
            consumeLiquid(oxygen, 1 / 60f);
        }};
        silverDrill = new Drill("silver-drill") {{
            requirements(Category.production, with(electrum, 45, silver, 20, silicon, 40));
            size = 3;
            tier = 4;
            buildVisibility = BuildVisibility.sandboxOnly;
            liquidBoostIntensity = 1.8f;
            liquidCapacity = 60;
            itemCapacity = 35;
            drillTime = 280;
            //this drill has to power scale a lot
            //so any extra complexity for blocks like this needs to be met with high rewards
            warmupSpeed = 0.08f;
            consumeLiquid(dioxide, 6 / 60f);
            consumeLiquid(cryofluid, 6 / 60f).boost();
            consumePower(64 / 60f);
        }};
        chromiumExtractor = new WallCrafter("chromium-extractor") {{
            requirements(Category.production, with(silver, 15, silicon, 20, titanium, 10, cupronickel, 5));
            buildVisibility = BuildVisibility.sandboxOnly;
            size = 1;
            itemCapacity = 60;
            drillTime = 160;
            attribute = AquaAttributes.chromium;
            output = AquaItems.chromium;
            consumePower(16 / 60f);
        }};
        atmoshpericSeperator = new GenericCrafter("atmospheric-separator") {{
            requirements(Category.crafting, with(lead, 50, electrum, 75));
            size = 3;
            buildVisibility = BuildVisibility.sandboxOnly;
            rotateDraw = false;
            outputLiquids = LiquidStack.with(dioxide, 16f / 60f, oxygen, 5f / 60f);
            liquidOutputDirections = new int[]{3, 1};
            liquidCapacity = 50;
            itemCapacity = 0;
            craftTime = 10f;
            rotate = true;
            invertFlip = true;
            warmupSpeed = 0.01f;
            group = BlockGroup.liquids;
            consumeLiquid(water, 15 / 60f);
            consumePower(1f);
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
            buildVisibility = BuildVisibility.sandboxOnly;
            craftTime = 60;
            hasLiquids = true;
            outputLiquid = new LiquidStack(carbonicAcid, 12 / 60f);
            liquidCapacity = 45;
            craftEffect = Fx.coalSmeltsmoke;
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
            liquidCapacity = 30;
            buildVisibility = BuildVisibility.sandboxOnly;
            hasLiquids = true;
            outputItems = new ItemStack[]{new ItemStack(copper, 1), new ItemStack(silver, 2)};
            consumeLiquid(carbonicAcid, 4 / 60f);
            consumeItem(electrum, 3);
            craftEffect = Fx.smeltsmoke;
        }};
        cryofluidChurn = new GenericCrafter("cryofluid-churn") {{
            requirements(Category.crafting, with(lead, 35, titanium, 40, silicon, 35));
            size = 2;
            hasLiquids = true;
            buildVisibility = BuildVisibility.sandboxOnly;
            outputLiquid = new LiquidStack(cryofluid, 18 / 60f);
            liquidCapacity = 90;
            itemCapacity = 20;
            consumeLiquid(water, 36 / 60f);
            consumeItem(titanium, 2);
            consumePower(64 / 60f);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(cryofluid), new DrawDefault());
        }};
        siliconHearth = new GenericCrafter("silicon-hearth") {{
            requirements(Category.crafting, with(lead, 90, copper, 40, titanium, 40));
            size = 3;
            outputItem = new ItemStack(silicon, 5);
            itemCapacity = 35;
            buildVisibility = BuildVisibility.sandboxOnly;
            craftEffect = new MultiEffect(Fx.smeltsmoke, Fx.mine);
            craftTime = 60;
            consumePower(64 / 60f);
            consumeItems(with(sand, 5, arsenic, 2));
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame());
        }};
        hydroponicsBasin = new GenericCrafter("hydroponics-basin") {{
            requirements(Category.production, with(inconel, 45, cupronickel, 120, titanium, 90, silicon, 80));
            size = 4;
            warmupSpeed = 0.01f;
            buildVisibility = BuildVisibility.sandboxOnly;
            outputLiquid = new LiquidStack(bioPulp, 24 / 60f);
            consumePower(128 / 60f);
            consumeLiquid(water, 48 / 60f);
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
                    }}, new DrawDefault());
            cupronickelAlloyer = new GenericCrafter("cupronickel-alloyer") {{
                requirements(Category.crafting, with(silicon, 90, copper, 180, lead, 90, nickel, 120));
                size = 3;
                updateEffect = Fx.steam;
                buildVisibility = BuildVisibility.sandboxOnly;
                updateEffectChance = 0.02f;
                outputItem = new ItemStack(cupronickel, 2);
                itemCapacity = 35;
                liquidCapacity = 90;
                craftTime = 30;
                ambientSound = Sounds.smelter;
                craftEffect = new MultiEffect(Fx.surgeCruciSmoke, Fx.mineBig);
                consumePower(256 / 60f);
                consumeLiquid(cryofluid, 9 / 60f);
                consumeItems(with(copper, 2, nickel, 3));
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
            buildVisibility = BuildVisibility.sandboxOnly;
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
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(lead, 150, bauxite, 120, silicon, 80));
            size = 5;
            squareSprite = false;
            alwaysUnlocked = true;

            outputItems = new ItemStack[]{new ItemStack(lead, 25), new ItemStack(bauxite, 30), new ItemStack(silicon, 25)};
            itemCapacity = 300;
            outputLiquid = new LiquidStack(slag, 40/60f);
            ignoreLiquidFullness = true;
            liquidCapacity = 500;
            craftTime = 300;
            consumeLiquid(magma, 120 / 60f);
            consumeItem(borax, 5).boost();
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
            ambientSound = AquaSounds.waterRumble;
            ambientSoundVolume = 0.1f;
            updateEffectChance = 0.01f;
            updateEffect = new MultiEffect(new ParticleEffect(){{
                sizeFrom = 2;
                sizeTo = 0;
                particles = 5;
                lifetime = 15f;
                length = 4;
                interp = linear;
                sizeInterp = linear;
                colorFrom = AquaPal.smoke;
                colorTo = AquaPal.smokeLight.a(0.2f);
            }},new ParticleEffect(){{
                sizeFrom = 1;
                sizeTo = 0;
                particles = 3;
                length = 4;
                lifetime = 15f;
                interp = linear;
                sizeInterp = linear;
                colorFrom = AquaPal.fire2;
                colorTo = AquaPal.fire2;
            }});
            craftEffect = new MultiEffect(new RadialEffect(){{
                rotationOffset = 45;
                lengthOffset = 16;
                effect = new MultiEffect(new ParticleEffect(){{
                    particles = 8;
                    layer = 80;
                    sizeFrom = 7;
                    sizeTo = 0;
                    cone = 25;
                    length = 40;
                    lifetime = 120;
                    sizeInterp = linear;
                    interp = linear;
                    colorFrom = AquaPal.smoke;
                    colorTo = AquaPal.smokeLight.a(0.2f);
                }}, new ParticleEffect(){{
                    particles = 6;
                    layer = 81;
                    length = 38;
                    sizeFrom = 6;
                    sizeTo = 0;
                    cone = 20;
                    lifetime = 120;
                    sizeInterp = linear;
                    interp = linear;
                    colorFrom = AquaPal.smoke;
                    colorTo = AquaPal.smokeLight.a(0.2f);
                }},new ParticleEffect(){{
                    sizeFrom = 3;
                    sizeTo = 0;
                    cone = 15;
                    length = 35;
                    lifetime = 120;
                    sizeInterp = linear;
                    interp = linear;
                    colorFrom = AquaPal.fire2;
                    colorTo = AquaPal.fireLight2;
                }});
            }});
        }};
        magmaTap = new AttributeCrafter("magma-tap") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.production, with(lead, 225, silicon, 100));
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
            ambientSoundVolume = 0.05f;
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
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(lead, 400, bauxite, 300, silicon, 250, copper, 150));
            craftTime = 60;
            consumeItem(bauxite, 10);
            researchCostMultiplier = 0.02f;

            outputItems = new ItemStack[]{
                    new ItemStack(silicon, 3),
                    new ItemStack(ferricMatter, 5),
                    new ItemStack(aluminum, 2)
            };
            outputLiquid = new LiquidStack(oxygen, 20 / 60f);
            size = 6;
            itemCapacity = 60;
            warmupSpeed = 0.01f;
            squareSprite = false;
            ignoreLiquidFullness = true;
            dumpExtraLiquid = true;
            updateEffectChance = 0.05f;
            updateEffect = new MultiEffect(new ParticleEffect(){{
                length = 7f;
                lifetime = 10;
                layer = 80;
                colorFrom = Color.valueOf("fdbda6");
                colorTo = Color.valueOf("fdbda6");

            }});
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
        ferroSiliconFoundry = new GenericCrafter("ferrosilicon-foundry") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with( silicon, 1250, aluminum, 500, copper, 800, ferricMatter, 600));
            size = 6;
            squareSprite = false;
            itemCapacity = 120;
            researchCostMultiplier = 0.02f;
            craftTime = 600;
            consumeItems(with(ferricMatter, 15, silicon, 25));
            outputItem = new ItemStack(ferrosilicon, 40);
            consumeLiquids(LiquidStack.with(magma, 90 / 60f, hydroxide, 30/60f));
            liquidCapacity = 600;
            updateEffect = new MultiEffect(Fx.coalSmeltsmoke, new ParticleEffect(){{
                layer = Layer.debris;
                length = 45;
                colorFrom = AquaPal.smokeLight.a(0.7f);
                colorTo = AquaPal.smoke.a(0.5f);
                sizeFrom = 8;
                lifetime = 80;
                sizeTo = 0;
                particles = 3;
                interp = linear;
                sizeInterp = linear;
            }}, new ParticleEffect(){{
                layer = Layer.debris + 0.1f;
                length = 45;
                colorFrom = AquaPal.fireLight1.a(0.7f);
                colorTo = AquaPal.fireLight1.a(0.5f);
                sizeFrom = 6;
                sizeTo = 0;
                lifetime = 80;
                particles = 2;
                interp = linear;
                sizeInterp = linear;
            }});
            updateEffectChance = 0.05f;
            craftEffect = Fx.reactorsmoke;
            ambientSound = AquaSounds.waterHum;
            ambientSoundVolume = 0.09f;
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
        FractionalDistillery = new GenericCrafter("fractional-distillery") {{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.crafting, with(nickel, 250, silicon, 100, lead, 300, metaglass, 150));
                size = 5;
                rotateDraw = false;

                rotate = true;
                dumpExtraLiquid = false;
                ignoreLiquidFullness = false;
                invertFlip = true;
                craftTime = 5 * 60f;
                liquidCapacity = 900;
                researchCostMultiplier = 0.02f;
                itemCapacity = 100;
                outputLiquids = LiquidStack.with(petroleum, 25f / 60, ethylene, 50f / 60);
                outputItems = new ItemStack[]{new ItemStack(coke, 30)};
                consumeLiquids(new LiquidStack(oil, 250 / 60f), new LiquidStack(haze, 100/60f));
                regionRotated1 = 3;
                liquidOutputDirections = new int[]{2, 3};
                drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawCultivator() {{
                    timeScl = 180;
                    bottomColor = Color.valueOf("353a25");
                    plantColorLight = Color.valueOf("9aa86d");
                    plantColor = Color.valueOf("6d7944");
                    radius = 2.5f;
                    bubbles = 320;
                    spread = 14;
                }}, new DrawCells() {{
                    range = 14;
                    particles = 350;
                    lifetime = 90f * 5f;
                    particleColorFrom = Color.valueOf("9aa86d");
                    particleColorTo = Color.valueOf("6d7944");
                    color = Color.valueOf("oooooooo");
                }}, new DrawLiquidTile(petroleum, 1) {{
                    alpha = 0.6f;
                }},
                        new DrawBubbles() {{
                            spread = 14;
                            color = Color.valueOf("e7cfff");
                            timeScl = 15;
                            sides = 8;
                            amount = 140;
                        }}, new DrawSoftParticles() {{
                    particleLife = 120;
                    particles = 45;
                    rotateScl = 3;
                    particleRad = 8f;
                    particleSize = 5f;
                    color = Color.valueOf("f0c180");
                    color2 = Color.valueOf("fff89d");
                }}, new DrawLiquidTile(ethylene, 15),
                        new DrawDefault(), new DrawGlowRegion("-glow") {{
                    alpha = 0.5f;
                    glowScale = 10;
                    glowIntensity = 0.4f;
                    color = Color.valueOf("ea9565");
                }}, new DrawLiquidOutputs(), new DrawGlowRegion("-glow-2") {{
                    alpha = 0.75f;
                    glowScale = 8;
                    glowIntensity = 0.8f;
                    color = Color.valueOf("ffbeb0");
                }});
            }};
        fumeFilter = new AttributeCrafter("fume-filter"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.production, with(aluminum, 250, silicon, 500));
            size = 6;
            squareSprite = false;
            outputLiquid = new LiquidStack(fumes, 80/60f);
            attribute = Attribute.steam;
            boostScale = 1/18f;
            maxBoost = 2;
            baseEfficiency = 0;
            liquidCapacity = 250f;
            researchCostMultiplier = 0;
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
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(graphite, 1500, nickel, 1200, ferricMatter, 300, silicon, 3000));
            size = 7;
            consumePower(80/60f);
            itemCapacity = 300;
            liquidCapacity = 400;
            updateEffect = Fx.ventSteam;
            updateEffectChance = 0.01f;
            squareSprite = false;
            researchCostMultiplier = 0.25f;

            craftTime = 600;
            consumeLiquids(LiquidStack.with(slag, 80 / 60f, haze, 75 / 60));
            outputItems = new ItemStack[]{
                    new ItemStack(nickel, 50),
                    new ItemStack(silicon, 80),
                    new ItemStack(titanium, 40),
                    new ItemStack(ferricMatter, 60)};
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(fumes, 1), new DrawLiquidTile(slag){{
                padBottom = 13;
                padTop = 13;
                padRight = 9;
                padLeft = 9;

            }}, new DrawDefault(), new DrawGlowRegion());
        }};
        InvarBlastFurnace = new GenericCrafter("invar-blast-furnace"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(silicon, 500, ferricMatter, 200, nickel, 1500, metaglass, 800));
            size = 7;
            craftTime = 10*60f;
            outputLiquid = new LiquidStack(dioxide, 40/60f);
            consumeItems(with(nickel, 10, ferricMatter, 30));
            consumeLiquids(LiquidStack.with(haze, 125 / 60f));
            consumePower(256/60f);
            squareSprite = false;
            researchCostMultiplier = 0.05f;
            outputItem = new ItemStack(invar, 40);
            liquidCapacity = 200;
            itemCapacity = 120;
        }};
        manguluminCrucible = new GenericCrafter("mangalumin-crucible"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
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
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
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
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
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
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
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
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
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
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(silicon, 500, bauxite, 750, lead, 750));
            craftTime = 5*60f;
            squareSprite = false;
            consumeLiquid(magma, 45/60f);
            consumeItem(azurite, 15);
            itemCapacity = 300;
            outputItem = new ItemStack(copper, 45);
            outputLiquid = new LiquidStack(hydroxide, 15/60f);
            liquidCapacity = 250;
            ignoreLiquidFullness = true;
            dumpExtraLiquid = true;
            size = 5;
            researchCostMultiplier = 0.02f;
            updateEffect = Fx.steam;
            updateEffectChance = 0.05f;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(magma, 1.5f){{ alpha = 0.6f;}}, new DrawDefault(), new DrawGlowRegion() {{
                alpha = 0.65f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.3f;
                glowScale = 6f;
            }});
            towaniteReductionVat = new GenericCrafter("towanite-reduction-vat"){{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.crafting, with( towanite, 150, silicon ,500, aluminum, 300));
                size = 7;
                itemCapacity = 150;
                craftTime = 3*60f;
                squareSprite = false;
                researchCostMultiplier = 0.25f;
                consumeItem(towanite, 15);
                consumeLiquid(fumes, 30/60f);
                researchCostMultiplier = 0;
                outputItems = new ItemStack[]{
                        new ItemStack(copper, 15),
                        new ItemStack(brimstone, 30),
                        new ItemStack(ferricMatter, 15)};

                drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawPistons(){{
                    sides  =1;
                    angleOffset = -90;
                    sinMag = 2.5f;
                    lenOffset = 1;
                }}, new DrawBetterArcSmelt(){{
                    x = -16;
                    y = -4;
                }}, new DrawBetterArcSmelt(){{
                    x = 6;
                    y = -4;
                }}, new DrawDefault(), new DrawGlowRegion());
            }};
            plasmaExtractor = new ModifiedbeamDrill("plasma-extractor"){{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.production, with(silicon, 300, lead ,250));
                tier = 1;
                itemCapacity = 50;
                squareSprite = false;
                researchCostMultiplier = 0.05f;

                drillTime = 250;
                size = 4;
                range = 8;
                consumeLiquid(slag, 10/60f);
                consumeLiquid(fumes, 5/60f).boost();
                optionalBoostIntensity = 1.5f;
                heatColor  = Color.valueOf("9d8cf2");
                boostHeatColor = Color.valueOf("e1f28c");

            }};
            beamBore = new ModifiedbeamDrill("beam-bore"){{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.production, with(silicon, 350, aluminum, 250, towanite, 75));
                tier = 1;
                itemCapacity = 50;
                squareSprite = false;
                researchCostMultiplier = 0.25f;
                drillTime = 135;
                size = 3;
                range = 6;
                consumeLiquid(fumes, 5/60f);
                consumeLiquid(oxygen, 15/60f).boost();
                optionalBoostIntensity = 1.35f;
                heatColor  = Color.valueOf("9d8cf2");
                boostHeatColor = Color.valueOf("e1f28c");
                researchCostMultiplier = 0;
            }};
            fumeMixer = new GenericCrafter("fume-mixer"){{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
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
            DrillDerrick = new GroundDrill("drill-derrick"){{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.production, with( silicon, 450, aluminum, 150, copper, 65));
                size = 5;
                drillTime = 125;
                consumeLiquid(fumes, 10/60f).booster = false;
                consumeLiquid(oxygen, 15/60f).boost();
                liquidBoostIntensity = 1.5f;
                squareSprite = false;
                itemCapacity = 200;
                liquidCapacity = 120;
                tier = 1;
                researchCostMultiplier = 0;
            }};
            galenaCrucible = new GenericCrafter("galena-reduction-vat"){{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.crafting, with( silicon, 500, aluminum, 250));
                size = 6;
                consumeLiquids(LiquidStack.with(fumes, 15/60f, fluorine, 5/60f));
                itemCapacity = 125;
                liquidCapacity = 150;
                squareSprite = false;
                craftTime = 5*60f;
                outputItem = new ItemStack(lead, 80);
            }};
            fumeSeparator = new GenericCrafter("fume-separator"){{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.crafting, with( silicon, 500, aluminum, 450, copper, 200));
                size = 6;
                consumeLiquid(fumes, 30/60f);
                outputLiquids = LiquidStack.with(oxygen, 45/60f, chlorine, 15/60f);
                liquidOutputDirections = new int[]{3, 1};
            }};
        }};
        harvester = new GroundDrill("harvester"){{
            alwaysUnlocked = true;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.production, with( nickel, 80, lead, 50, silicon, 90));
            size = 3;
            drillTime= 160;
            liquidBoostIntensity = 1.5f;
            consumeLiquid(Liquids.water, 90/60f).boost();
            tier = 2;
            squareSprite = false;
            drawer = new DrawMulti(new DrawPistons(){{
                sides = 1;
                angleOffset = 90;
                sinMag = 2.2f;
            }}, new DrawDefault(), new DrawGlowRegion("-glow"){{
                alpha = 0.4f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.3f;
                glowScale = 5f;
                layer = Layer.block +3;
                blending = Blending.additive;
            }}, new DrawGlowRegion("-glow1"){{
                alpha = 0.45f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.35f;
                glowScale = 6f;
                layer = Layer.block +3;
                blending = Blending.additive;
            }}, new DrawGlowRegion("-glow2"){{
                alpha = 0.3f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 7f;
                layer = Layer.block +3;
                blending = Blending.additive;
            }}, new DrawRegion("-rotator"){{
                spinSprite = true;
                y = -3f;
                x = -2f;
                layer = Layer.block +4;
                rotateSpeed = 1.5f;
            }}, new DrawRegion("-rotator"){{
                spinSprite = true;
                rotateSpeed = -1.5f;
                rotation = 45;
                y = -28/4f;
                x = -24/4f;
                layer = Layer.block +4;
            }});
        }};
        CentrifugalPump = new Pump("centrifugal-pump"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.liquid, with( nickel, 90, silicon, 220, lead, 120, metaglass, 60));
            size = 3;
            squareSprite = true;
            pumpAmount = 0.25f;
            liquidCapacity = 250;
            drawer = new DrawMulti(new DrawRegion("-bottom"),new DrawLiquidTile(water, 1.5f){{ alpha = 0.6f;}}
                    ,new DrawLiquidTile(oil, 1.5f){{ alpha = 0.6f;}}, new DrawDefault(), new DrawGlowRegion("-glow"){{
                alpha = 0.3f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 7f;
                layer = Layer.block +3;
                blending = Blending.additive;
            }}, new DrawGlowRegion("-glow1"){{
                alpha = 0.3f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.3f;
                glowScale = 5f;
                layer = Layer.block +3;
                blending = Blending.additive;
            }});
        }};
        SolidBoiler = new GenericCrafter("solid-boiler"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with( nickel, 250, silicon, 1200, metaglass, 220, lead, 500, copper, 900));
            size = 7;
            itemCapacity = 60;
            squareSprite = false;
            updateEffect = Fx.steam;
            updateEffectChance = 0.05f;
            craftEffect = new MultiEffect(new MultiEffect(new ParticleEffect(){{
                offsetX = 24;
                offsetY = 8;
                baseRotation = 0;
                cone = 15f;
                baseLength = 12;
                sizeFrom = 0;
                sizeTo = 6;
                lifetime = 600;
                sizeInterp = slope;
                interp = pow2Out;
                length = 90/2f;
                layer = 80;
                colorFrom = AquaPal.smoke;
                colorTo = AquaPal.smokeLight.a(0);
            }},new ParticleEffect(){{
                offsetX = 24;
                offsetY = 8;
                baseRotation = 0;
                cone = 13f;
                baseLength = 4;
                sizeFrom = 0;
                sizeTo = 4;
                lifetime = 600;
                sizeInterp = slope;
                interp = pow2Out;
                length = 80/2f;
                colorFrom = AquaPal.fireLight1;
                colorTo = Color.black.a(0);
            }}),new MultiEffect(new ParticleEffect(){{
                offsetX = 24;
                offsetY = 16;
                baseRotation = 0;
                cone = 15f;
                baseLength = 12;
                sizeFrom = 0;
                sizeTo = 6;
                lifetime = 600;
                sizeInterp = slope;
                interp = pow2Out;
                length = 90/2f;
                layer = 80;
                colorFrom = AquaPal.smoke;
                colorTo = AquaPal.smokeLight.a(0);
            }},new ParticleEffect(){{
                offsetX = 24;
                offsetY = 16;
                baseRotation = 0;
                cone = 13f;
                baseLength = 12;
                sizeFrom = 0;
                sizeTo = 4;
                lifetime = 600;
                sizeInterp = slope;
                interp = pow2Out;
                length = 80/2f;
                colorFrom = AquaPal.fireLight1;
                colorTo = Color.black.a(0);
            }}),new MultiEffect(new ParticleEffect(){{
                offsetX = 24;
                offsetY = -2;
                baseRotation = 0;
                cone = 15f;
                baseLength = 12;
                sizeFrom = 0;
                sizeTo = 6;
                lifetime = 600;
                sizeInterp = slope;
                interp = pow2Out;
                length = 90/2f;
                layer = 80;
                colorFrom = AquaPal.smoke;
                colorTo = AquaPal.smokeLight.a(0);
            }},new ParticleEffect(){{
                offsetX = 24;
                offsetY = -2;
                baseRotation = 0;
                cone = 13f;
                baseLength = 12;
                sizeFrom = 0;
                sizeTo = 4;
                lifetime = 600;
                sizeInterp = slope;
                interp = pow2Out;
                length = 80/2f;
                colorFrom = AquaPal.fireLight1;
                colorTo = Color.black.a(0);
            }}),new MultiEffect(new ParticleEffect(){{
                offsetX = 24;
                offsetY = -13;
                baseRotation = 0;
                cone = 15f;
                baseLength = 12;
                sizeFrom = 0;
                sizeTo = 6;
                lifetime = 600;
                sizeInterp = slope;
                interp = pow2Out;
                length = 90/2f;
                layer = 80;
                colorFrom = AquaPal.smoke;
                colorTo = AquaPal.smokeLight.a(0);
            }},new ParticleEffect(){{
                offsetX = 24;
                offsetY = -13;
                baseRotation = 0;
                cone = 13f;
                baseLength = 12;
                sizeFrom = 0;
                sizeTo = 4;
                lifetime = 600;
                sizeInterp = slope;
                interp = pow2Out;
                length = 80/2f;
                colorFrom = AquaPal.fireLight1;
                colorTo = Color.black.a(0);
            }}));
            craftTime = 10f;
            liquidCapacity = 1000;
            consume(new ConsumeItemFlammable(0.25f));
            consume(new ConsumeItemExplode(0.2f));
            consumeLiquids(new LiquidStack(water, 500/60f), new LiquidStack(air, 200/60f));
            outputLiquid = new LiquidStack(haze, 500/60f);
            drawer = new DrawMulti(new DrawMulti(new DrawRegion("-bottom"), new BetterDrawParticles(){{
                color = Color.valueOf("dd5f41");
                alpha = 0.4f;
                y = 4;
                sides = 12;
                x = 18;
                particleSize = 2f;
                particles = 18;
                particleRad = 8f;
                particleLife = 60f;
            }}, new BetterDrawParticles(){{
                color = Color.valueOf("fbc14e");
                alpha = 0.6f;
                y = 4;
                sides = 12;
                x = 18;
                particleSize = 2f;
                particles = 12;
                particleRad = 6f;
                particleLife = 60f;
            }}, new BetterDrawParticles(){{
                color = Color.valueOf("dd5f41");
                alpha = 0.4f;
                y = 12;
                sides = 12;
                x = 18;
                particleSize = 2f;
                particles = 18;
                particleRad = 8f;
                particleLife = 60f;
            }}, new BetterDrawParticles(){{
                color = Color.valueOf("fbc14e");
                alpha = 0.6f;
                y = 12;
                sides = 12;
                x = 18;
                particleSize = 2f;
                particles = 12;
                particleRad = 6f;
                particleLife = 60f;
            }}, new BetterDrawParticles(){{
                color = Color.valueOf("dd5f41");
                alpha = 0.4f;
                y = -8;
                sides = 12;
                x = 18;
                particleSize = 2f;
                particles = 18;
                particleRad = 8f;
                particleLife = 60f;
            }}, new BetterDrawParticles(){{
                color = Color.valueOf("fbc14e");
                alpha = 0.6f;
                y = -8;
                sides = 12;
                x = 18;
                particleSize = 2f;
                particles = 12;
                particleRad = 6f;
                particleLife = 60f;
            }}, new BetterDrawParticles(){{
                color = Color.valueOf("dd5f41");
                alpha = 0.4f;
                y = -12;
                sides = 12;
                x = 18;
                particleSize = 2f;
                particles = 18;
                particleRad = 8f;
                particleLife = 60f;
            }}, new BetterDrawParticles(){{
                color = Color.valueOf("fbc14e");
                alpha = 0.6f;
                y = -12;
                sides = 12;
                x = 18;
                particleSize = 2f;
                particles = 12;
                particleRad = 6f;
                particleLife = 60f;
            }}, new DrawRegion("-cover"), new DrawLiquidTile(water, 2){{
               padRight = 10;
               padLeft = 10;
               padBottom = 5;
               padTop = 2.5f;
            }}, new DrawDefault(), new PhaseOffsetGlowRegion("-glow"){{
                alpha = 0.35f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.3f;
                glowScale = 5f;
                layer = Layer.block +3;
                blending = Blending.additive;
            }}, new PhaseOffsetGlowRegion("-glow1"){{
                alpha = 0.4f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.25f;
                glowScale = 6f;
                layer = Layer.block +3;
                blending = Blending.additive;
                phaseOffset = 10;
            }}, new PhaseOffsetGlowRegion("-glow2"){{
                alpha = 0.45f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 8f;
                layer = Layer.block +3;
                blending = Blending.additive;
                phaseOffset = 15;
            }}, new PhaseOffsetGlowRegion("-glow3"){{
                alpha = 0.55f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.5f;
                glowScale = 10f;
                layer = Layer.block +3;
                blending = Blending.additive;
                phaseOffset = 20;
            }}, new DrawPistons(){{
                sides = 1;
            }}));
        }};
        AnnealingOven = new GenericCrafter("metaglass-annealing-furnace"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with( nickel, 250, lead, 300, silicon, 1100, copper, 550));
            size = 5;
            itemCapacity = 60;
            squareSprite = false;
            researchCostMultiplier = 0.02f;
            craftTime = 120;
            consumeItems(new ItemStack(lead, 10), new ItemStack(sand, 10));
            outputItem = new ItemStack(metaglass, 20);
            updateEffect = Fx.coalSmeltsmoke;
            updateEffectChance = 0.07f;
            ambientSound = AquaSounds.refine;
            ambientSoundVolume = 0.07f;
            consumeLiquid(air, 150/60f);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawDefault(), new PhaseOffsetGlowRegion("-glow"){{
                alpha = 0.55f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 9f;
                layer = Layer.block +3;
                blending = Blending.additive;
                phaseOffset = 20;
            }}, new PhaseOffsetGlowRegion("-glow1"){{
                alpha = 0.40f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 7f;
                layer = Layer.block +3;
                blending = Blending.additive;
                phaseOffset = 10;
            }});
        }};
        atmosphericIntake = new GenericCrafter("air-intake"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.production, with( nickel, 50, silicon, 150));
            size = 2;
            researchCostMultiplier = 0.02f;
            squareSprite = false;
            craftTime = 10;
            liquidCapacity = 200;
            envDisabled = Env.underwater|Env.space;
            outputLiquid = new LiquidStack(air, 50/60f);
            ambientSound = Sounds.windhowl;
            ambientSoundVolume = 0.02f;
            drawer = new DrawMulti(new DrawDefault(), new DrawBlurSpin("-fan", 8f), new DrawRegion("-top"),new DrawParticles(){{
                color = Color.valueOf("d4f0ff");
                alpha = 0.3f;
                particleSize = 2.5f;
                particles = 14;
                particleRad = 4f;
                particleLife = 140f;
            }});
        }};
    }

    public static void disableVanilla() {
        Blocks.siliconSmelter.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.siliconCrucible.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.kiln.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.graphitePress.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.plastaniumCompressor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.multiPress.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.phaseWeaver.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.surgeSmelter.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.pyratiteMixer.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.blastMixer.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.cryofluidMixer.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.melter.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.separator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.disassembler.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.sporePress.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.pulverizer.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.incinerator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.coalCentrifuge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.siliconArcFurnace.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.electrolyzer.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.oxidationChamber.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.atmosphericConcentrator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.electricHeater.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.slagHeater.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.phaseHeater.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.heatRedirector.buildVisibility = BuildVisibility.sandboxOnly;
        //Blocks.smallHeatRedirector.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.heatRouter.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.slagIncinerator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.carbideCrucible.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.slagCentrifuge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.surgeCrucible.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.cyanogenSynthesizer.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.phaseSynthesizer.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.heatReactor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.powerSource.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.powerVoid.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.itemSource.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.itemVoid.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.liquidSource.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.liquidVoid.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.payloadSource.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.payloadVoid.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.illuminator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.heatSource.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.copperWall.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.copperWallLarge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.titaniumWall.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.titaniumWallLarge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.plastaniumWall.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.plastaniumWallLarge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.thoriumWall.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.thoriumWallLarge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.door.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.doorLarge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.phaseWall.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.phaseWallLarge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.surgeWall.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.surgeWallLarge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.berylliumWall.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.berylliumWallLarge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.tungstenWall.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.tungstenWallLarge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.blastDoor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.reinforcedSurgeWall.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.reinforcedSurgeWallLarge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.carbideWall.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.carbideWallLarge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.shieldedWall.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.mender.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.mendProjector.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.overdriveProjector.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.overdriveDome.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.forceProjector.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.shockMine.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.scrapWall.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.scrapWallLarge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.scrapWallHuge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.scrapWallGigantic.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.thruster.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.radar.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.buildTower.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.regenProjector.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.shockwaveTower.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.shieldProjector.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.largeShieldProjector.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.conveyor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.titaniumConveyor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.plastaniumConveyor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.armoredConveyor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.distributor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.junction.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.itemBridge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.phaseConveyor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.sorter.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.invertedSorter.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.router.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.overflowGate.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.underflowGate.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.massDriver.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.duct.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.armoredDuct.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.ductRouter.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.overflowDuct.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.underflowDuct.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.ductBridge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.ductUnloader.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.surgeConveyor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.surgeRouter.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.unitCargoLoader.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.unitCargoUnloadPoint.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.mechanicalPump.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.rotaryPump.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.impulsePump.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.conduit.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.pulseConduit.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.platedConduit.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.liquidRouter.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.liquidContainer.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.liquidTank.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.liquidJunction.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.bridgeConduit.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.phaseConduit.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.reinforcedPump.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.reinforcedConduit.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.reinforcedLiquidJunction.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.reinforcedBridgeConduit.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.reinforcedLiquidRouter.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.reinforcedLiquidContainer.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.reinforcedLiquidTank.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.combustionGenerator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.thermalGenerator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.steamGenerator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.differentialGenerator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.rtgGenerator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.solarPanel.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.largeSolarPanel.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.thoriumReactor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.impactReactor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.battery.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.batteryLarge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.powerNode.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.powerNodeLarge.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.surgeTower.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.diode.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.turbineCondenser.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.ventCondenser.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.chemicalCombustionChamber.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.pyrolysisGenerator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.fluxReactor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.neoplasiaReactor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.beamNode.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.beamTower.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.beamLink.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.mechanicalDrill.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.pneumaticDrill.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.laserDrill.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.blastDrill.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.waterExtractor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.oilExtractor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.cultivator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.cliffCrusher.buildVisibility = BuildVisibility.sandboxOnly;
        //Blocks.largeCliffCrusher.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.plasmaBore.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.largePlasmaBore.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.impactDrill.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.eruptionDrill.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.coreShard.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.coreFoundation.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.coreNucleus.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.vault.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.container.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.unloader.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.coreBastion.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.coreCitadel.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.coreAcropolis.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.reinforcedContainer.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.reinforcedVault.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.duo.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.scatter.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.scorch.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.hail.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.arc.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.wave.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.lancer.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.swarmer.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.salvo.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.fuse.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.ripple.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.cyclone.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.foreshadow.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.spectre.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.meltdown.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.segment.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.parallax.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.tsunami.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.breach.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.diffuse.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.sublimate.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.titan.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.disperse.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.afflict.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.lustre.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.scathe.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.smite.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.malign.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.groundFactory.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.airFactory.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.navalFactory.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.additiveReconstructor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.multiplicativeReconstructor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.exponentialReconstructor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.tetrativeReconstructor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.repairPoint.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.repairTurret.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.tankFabricator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.shipFabricator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.mechFabricator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.tankRefabricator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.shipRefabricator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.mechRefabricator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.primeRefabricator.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.tankAssembler.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.shipAssembler.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.mechAssembler.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.basicAssemblerModule.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.unitRepairTower.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.payloadConveyor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.payloadRouter.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.reinforcedPayloadConveyor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.reinforcedPayloadRouter.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.payloadMassDriver.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.largePayloadMassDriver.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.smallDeconstructor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.deconstructor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.constructor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.largeConstructor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.payloadLoader.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.payloadUnloader.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.message.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.switchBlock.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.microProcessor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.logicProcessor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.hyperProcessor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.largeLogicDisplay.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.logicDisplay.buildVisibility = BuildVisibility.sandboxOnly;
        //Blocks.logicDisplayTile.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.memoryCell.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.memoryBank.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.canvas.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.reinforcedMessage.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.worldProcessor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.worldCell.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.worldMessage.buildVisibility = BuildVisibility.sandboxOnly;
        //Blocks.worldSwitch.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.launchPad.buildVisibility = BuildVisibility.sandboxOnly;
        //Blocks.advancedLaunchPad.buildVisibility = BuildVisibility.sandboxOnly;
        //Blocks.landingPad.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.interplanetaryAccelerator.buildVisibility = BuildVisibility.sandboxOnly;
    }
}
