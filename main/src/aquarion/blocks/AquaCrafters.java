package aquarion.blocks;

import aquarion.AquaAttributes;
import aquarion.AquaItems;
import aquarion.AquaSounds;
import aquarion.world.blocks.heatBlocks.HotHeatConductor;
import aquarion.world.blocks.production.ModifiedbeamDrill;
import aquarion.world.consumers.ConsumeLiquidAcidic;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.AquaPal;
import aquarion.world.graphics.NewParticleEffect;
import aquarion.world.graphics.drawers.*;
import aquarion.world.type.AquaGenericCrafter;
import aquarion.world.type.GroundDrill;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.content.Planets;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.RadialEffect;
import mindustry.entities.effect.SeqEffect;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatConductor;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.Pump;
import mindustry.world.consumers.ConsumeItemExplode;
import mindustry.world.consumers.ConsumeItemFlammable;
import mindustry.world.consumers.ConsumeLiquidFlammable;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static aquarion.AquaAttributes.iron;
import static aquarion.AquaAttributes.metamorphic;
import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.*;
import static aquarion.planets.AquaPlanets.*;
import static aquarion.world.graphics.Renderer.Layer.heat;
import static aquarion.world.graphics.Renderer.Layer.shadow;
import static arc.math.Interp.linear;
import static arc.math.Interp.pow5Out;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.type.ItemStack.with;

public class AquaCrafters {
    public static Block coolingTower, glassPulverizer, evaporationPool, nuetralizationChamber, thermalEvaporator, leachingVessel, sporeProcessor, coalLiquefactor, coalHeater, polymerPress, fluxExcavator, graphiteConcentrator, cupronickelAlloyer, brineMixer, brineElectrolyzer, ferricGrinder, SilicaOxidator, arcFurnace, desulferizationAssembly, heatChannel, convectionHeater, combustionHeater, thermalCrackingUnit, steamCrackingUnit, ultrafamicRefinery, gasifier, algalTerrace, atmosphericCentrifuge, steelFoundry, pinDrill, inlet, inletArray, acuminiteDegredationArray, vacuumFreezer, atmosphericIntake, AnnealingOven, SolidBoiler, CentrifugalPump, pumpAssembly, harvester, galenaCrucible, DrillDerrick, beamBore, fumeMixer, chireniumElectroplater, saltDegradationMatrix, plasmaExtractor, towaniteReductionVat, azuriteKiln, slagRefinementAssemblage, fumeFilter, ferroSiliconFoundry, bauxiteCentrifuge, magmaTap, fumeSeparator, magmaDiffser;

    public static void loadContent() {
        disableVanilla();
        magmaDiffser = new AquaGenericCrafter("magma-diffuser") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(lead, 150, zinc, 120, silicon, 80));
            size = 5;
            squareSprite = false;
            alwaysUnlocked = true;

            outputItems = new ItemStack[]{
                    new ItemStack(lead, 25),
                    new ItemStack(zinc, 15),
                    new ItemStack(AquaItems.biotite, 30),
                    new ItemStack(silicon, 25)};
            itemCapacity = 300;
            outputLiquid = new LiquidStack(slag, 0.5f);
            ignoreLiquidFullness = true;
            liquidCapacity = 500;
            craftTime = 300;
            consumeLiquid(magma, 2);
            itemBoostIntensity = 2;
            consumeItem(salt, 1).boost();
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawRegion("-ring"), new DrawAdvancedPistons() {{
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
            updateEffect = AquaFx.diffuserSmoke;
            craftEffect = new MultiEffect(new RadialEffect() {{
                rotationOffset = 45;
                lengthOffset = 16;
                effect = AquaFx.diffuserCraft;
            }});
        }};
        evaporationPool = new AquaGenericCrafter("evaporation-pool"){{
            requirements(Category.production, with(lead, 225, silicon, 100));
        }};
        magmaTap = new AttributeCrafter("magma-tap") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.production, with(lead, 225, silicon, 100));
            size = 4;
            floating = true;
            updateEffect = Fx.steam;
            attribute = Attribute.heat;
            minEfficiency = 1;
            maxBoost = 3;
            consumePower(0.5f);
            baseEfficiency = 0;
            updateEffectChance = 0.02f;
            boostScale = 1f / 8f;
            craftTime = 5 * 60f;
            ambientSound = Sounds.smelter;
            ambientSoundVolume = 0.05f;
            squareSprite = false;

            liquidCapacity = 900;
            maxBoost = 3;
            outputLiquid = new LiquidStack(magma, 1.5f);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(magma, 5), new DrawDefault(), new DrawGlowRegion() {{
                alpha = 0.65f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.3f;
                glowScale = 6f;
            }});
        }};
        leachingVessel = new AquaGenericCrafter("biotite-leaching-vessel"){{
            requirements(Category.crafting, with(zinc, 400, lead, 400, silicon, 500, copper, 200));
            size = 8;
            squareSprite = false;
            liquidCapacity = 800;
            baseEfficiency = 0.5f;
            rotate = true;
            rotateDraw = false;
            hasHeat = true;
            heatRequirement = 30;
            maxEfficiency = 3;
            overheatScale = 1;
            consumePower(4);
            liquidOutputDirections = new int[]{1,2,3};
            craftTime = 60;
            itemCapacity = 200;
            consumeLiquid(muriaticAcid, 2);
            consumeItem(biotite, 35);
            outputItems = new ItemStack[]{
                    new ItemStack(sand, 6),
                    new ItemStack(ferricMatter, 10),
                    new ItemStack(aluminum, 5)
            };
            outputLiquids = new LiquidStack[]{
                    new LiquidStack(hydroxide, 0.5f),
                    new LiquidStack(oxygen, 2),
                    new LiquidStack(rareSludge, 0.1f)
            };
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(muriaticAcid, 5), new DrawRegion("-spinny", 0.8f, true), new DrawDefault(), new AquaDrawLiquidOutputs(), new DrawHeatInputBitmask("-heats"));
        }};
        thermalEvaporator = new AttributeCrafter("geothermal-evaporator"){{
            requirements(Category.crafting, with(lead, 250, zinc, 150, silicon, 150));
            size = 5;
            squareSprite = false;
            minEfficiency = 0.5f;
            updateEffect = Fx.steam;
            attribute = Attribute.heat;
            updateEffectChance = 0.02f;
            liquidCapacity = 500;
            consumePower(2);
            boostScale = 1f / 25f;
            ambientSound = Sounds.smelter;
            floating = true;
            ambientSoundVolume = 0.06f;
            baseEfficiency = 0;
            consumeLiquid(halideWater, 4f);
            craftTime = 240;
            outputItem = new ItemStack(salt, 1);
            outputLiquid = new LiquidStack(muriaticAcid, 3.5f);
        }};
        nuetralizationChamber = new HeatProducer("nuetralization-vessel"){{
            requirements(Category.crafting, with(copper, 150, zinc, 250, silicon, 500));
            size = 4;
            squareSprite = false;
            liquidCapacity = 500;
            heatOutput = 20;
            consume(new ConsumeLiquidAcidic(1f));
            consumeLiquid(hydroxide, 1f);
            outputLiquid = new LiquidStack(halideWater, 2);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawDefault(), new DrawHeatOutput());
        }};
        bauxiteCentrifuge = new AquaGenericCrafter("bauxite-centrifuge") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(lead, 450, silicon, 600, copper, 300));
            craftTime = 60;
            consumeItem(bauxite, 10);
            consumePower(2);
            outputItems = new ItemStack[]{
                    new ItemStack(sand, 6),
                    new ItemStack(ferricMatter, 6),
                    new ItemStack(aluminum, 6)
            };
            outputLiquid = new LiquidStack(oxygen, .25f);
            size = 6;
            itemCapacity = 60;
            warmupSpeed = 0.01f;
            squareSprite = false;
            ignoreLiquidFullness = true;
            dumpExtraLiquid = true;
            updateEffectChance = 0.05f;
            updateEffect = new MultiEffect(new ParticleEffect() {{
                length = 7f;
                lifetime = 10;
                layer = 80;
                colorFrom = Color.valueOf("fdbda6");
                colorTo = Color.valueOf("fdbda6");

            }});
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawOrbitRegions("-capsule", 8, 11f, 2f), new DrawRegion("-lower-toob"), new DrawLiquidTile(oxygen) {{
                padBottom = 3;
                padRight = 3;
                padTop = 22;
                padLeft = 22;
            }}, new DrawDefault(), new DrawRegion("-toob"), new DrawAdvancedPistons() {{
                angleOffset = 270;
                sides = 1;
            }});
        }};
        ferroSiliconFoundry = new AquaGenericCrafter("ferrosilicon-foundry") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(silicon, 900, metaglass, 500, copper, 500, ferricMatter, 700, graphite, 450, aluminum, 600));
            size = 6;
            squareSprite = false;
            itemCapacity = 120;
            heatRequirement = 30;
            hasHeat = true;
            maxEfficiency = 5;
            overheatScale = 0.25f;
            craftTime = 10 * 60f;
            consumePower(4);
            consumeItems(with(ferricMatter, 15, silicon, 35));
            outputItem = new ItemStack(ferrosilicon, 40);
            consumeLiquid(water, 2);
            liquidCapacity = 600;
            updateEffect = new MultiEffect(Fx.coalSmeltsmoke, new ParticleEffect() {{
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
            }}, new ParticleEffect() {{
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawRegion("-mid"), new DrawArcSmelt() {{
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
            }}, new DrawCrucibleFlame() {{
                circleStroke = 1.6f;
                particleLife = 60;
                particleSize = 5;
                particleRad = 12;
                circleSpace = 5;
                flameRadiusScl = 14;
                flameRadiusMag = 0.9f;
                rotateScl = 3;
            }}, new DrawDefault(), new DrawGlowRegion() {{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }}, new DrawGlowRegion("-glow-2") {{
                glowIntensity = 0.9f;
                glowScale = 8;
                alpha = 0.6f;
                color = Color.valueOf("f5c5aa");
            }}, new DrawHeatInputBitmask(), new AquaHeatRegion("-heats"));
        }};
        fumeFilter = new AttributeCrafter("fume-filter") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.production, with(copper, 250, silicon, 500));
            size = 6;
            squareSprite = false;
            outputLiquid = new LiquidStack(fumes, 3);
            attribute = Attribute.steam;
            boostScale = 1 / 18f;
            minEfficiency = 0.1f;
            maxBoost = 2;
            baseEfficiency = 0;
            liquidCapacity = 250f;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(fumes, 2) {{
                alpha = 0.7f;
            }}, new DrawDefault(), new DrawBetterRegion("-fan") {{
                y = 4;
                x = 4;
                spinSprite = true;
                rotateSpeed = 2;
            }}, new DrawBetterRegion("-fan") {{
                y = 4;
                x = 4;
                rotation = 45;
                spinSprite = true;
                rotateSpeed = 2;
            }}, new DrawBetterRegion("-fan") {{
                y = 4;
                x = -8;
                spinSprite = true;
                rotateSpeed = 2;
            }}, new DrawBetterRegion("-fan") {{
                y = 4;
                x = -8;
                rotation = 45;
                spinSprite = true;
                rotateSpeed = 2;
            }}, new BetterDrawParticles() {{
                color = Color.valueOf("836c59");
                alpha = 0.4f;
                y = 4;
                sides = 12;
                x = -8;
                particleSize = 2f;
                particles = 12;
                particleRad = 6f;
                particleLife = 60f;
            }}, new BetterDrawParticles() {{
                color = Color.valueOf("9f846d");
                alpha = 0.4f;
                y = 4;
                x = -8;
                particleSize = 2f;
                particles = 12;
                sides = 12;
                particleRad = 6f;
                particleLife = 60f;
            }}, new BetterDrawParticles() {{
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
            }}, new BetterDrawParticles() {{
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
            }}, new DrawLiquidTile(fumes, 1) {{
                alpha = 0.6f;
            }}, new DrawRegion("-top"), new DrawGlowRegion() {{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }}, new DrawGlowRegion("-glow1") {{
                glowIntensity = 0.9f;
                glowScale = 8;
                alpha = 0.6f;
                color = Color.valueOf("ffc99e");
            }});
        }};
        slagRefinementAssemblage = new AquaGenericCrafter("slag-refinement-array") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(graphite, 900, zinc, 1200, metaglass, 1000));
            size = 7;
            consumePower(6f);
            itemCapacity = 300;
            liquidCapacity = 900;
            updateEffect = Fx.ventSteam;
            updateEffectChance = 0.01f;
            squareSprite = false;

            craftTime = 600;
            consumeLiquids(LiquidStack.with(slag,6, fumes, 2));
            outputItems = new ItemStack[]{
                    new ItemStack(metaglass, 40),
                    new ItemStack(sand, 5),
                    new ItemStack(zinc, 30),
                    new ItemStack(ferricMatter, 25)};
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(fumes, 1), new DrawLiquidTile(slag) {{
                padBottom = 13;
                padTop = 13;
                padRight = 9;
                padLeft = 9;

            }}, new DrawDefault(), new DrawGlowRegion());
        }};
        azuriteKiln = new AquaGenericCrafter("azurite-kiln") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(silicon, 750, zinc, 250, lead, 750));
            craftTime = 5 * 60f;
            squareSprite = false;
            consumePower(4.5f);
            consumeLiquid(muriaticAcid, 0.5f);
            consumeItem(azurite, 15);
            itemCapacity = 300;
            outputItem = new ItemStack(copper, 45);
            outputLiquid = new LiquidStack(hydroxide, 8.5f);
            liquidCapacity = 1500;
            ignoreLiquidFullness = true;
            dumpExtraLiquid = true;
            size = 5;
            updateEffect = Fx.steam;
            updateEffectChance = 0.05f;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(magma, 1.5f) {{
                alpha = 0.6f;
            }}, new DrawDefault(), new DrawGlowRegion() {{
                alpha = 0.65f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.3f;
                glowScale = 6f;
            }});
            graphiteConcentrator = new AttributeCrafter("graphite-concentrator") {{
                requirements(Category.production, with(copper, 40, silicon, 80));
                attribute = metamorphic;
                craftTime = 150;
                boostScale = .25f;
                outputItem = new ItemStack(graphite, 2);
                consumeLiquid(water, 0.25f);
                liquidCapacity = 80;
                baseEfficiency = 0;
                itemCapacity = 18;
                size = 2;
                minEfficiency = 0.1f;
                squareSprite = false;
                drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                    layer = shadow;
                    drawIcon = false;
                }}, new DrawParticles() {{
                    color = Color.valueOf("67a0cd");
                    alpha = 0.7f;
                    particleSize = 2f;
                    particles = 12;
                    particleRad = 5f;
                    particleLife = 160f;
                    rotateScl = 2f;
                }}, new DrawDefault());
            }};
            ferricGrinder = new AttributeCrafter("ferric-macerator") {{
                requirements(Category.production, with(cupronickel, 250, silicon, 500, metaglass, 200, graphite, 700, polymer, 800));
                size = 6;
                itemCapacity = 100;
                liquidCapacity = 200;
                craftTime = 240;
                attribute = iron;
                baseEfficiency = 0;
                minEfficiency = 1;
                displayEfficiency = true;
                maxBoost = 2.5f;
                boostScale = 1 / 36f;
                consumePower(4);
                consumeLiquid(water, 3);
                outputItems = new ItemStack[]{
                        new ItemStack(sand, 20),
                        new ItemStack(ferricMatter, 8),
                };
                drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                    layer = shadow;
                    drawIcon = false;
                }}, new DrawRegion("-bottom"), new DrawParticles() {{
                    color = Color.valueOf("1c4538");
                    alpha = 0.5f;
                    particleSize = 4f;
                    particles = 45;
                    particleRad = 8 * 3f;
                    particleLife = 20;
                    rotateScl = 1.5f;
                }}, new DrawOrbitRegions() {{
                    orbitSpeed = 3f;
                    radius = 11;
                    suffix = "-gear";
                    rotateSpeed = 2.5f;
                    spinSprite = true;
                    countRotOffset = 22.5f;
                    regionCount = 7;
                }}, new DrawParticles() {{
                    color = Color.valueOf("356e6c");
                    alpha = 0.5f;
                    particleSize = 2.5f;
                    particles = 20;
                    particleRad = 8 * 2.5f;
                    particleLife = 15;
                    rotateScl = 1.6f;
                }}, new DrawRegion("-ring") {{
                    spinSprite = true;
                    rotateSpeed = 2;
                }}, new DrawParticles() {{
                    color = Color.valueOf("747ae1");
                    alpha = 0.2f;
                    particleSize = 1.5f;
                    particles = 15;
                    particleRad = 8 * 2.5f;
                    particleLife = 10;
                    rotateScl = 1.7f;
                }}, new DrawRegion("-bit") {{
                    spinSprite = true;
                    rotateSpeed = -4;
                }}, new DrawDefault());
            }};
            towaniteReductionVat = new AquaGenericCrafter("towanite-reduction-vat") {{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.crafting, with(copper, 150, silicon, 500));
                size = 7;
                itemCapacity = 150;
                craftTime = 3 * 60f;
                squareSprite = false;
                consumePower(5);
                consumeItem(towanite, 15);
                consumeLiquid(fumes, 0.5f);
                outputItems = new ItemStack[]{
                        new ItemStack(copper, 15),
                        new ItemStack(brimstone, 30),
                        new ItemStack(ferricMatter, 15)};

                drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                    layer = shadow;
                    drawIcon = false;
                }}, new DrawRegion("-bottom"), new DrawPistons() {{
                    sides = 1;
                    angleOffset = -90;
                    sinMag = 2.5f;
                    lenOffset = 1;
                }}, new DrawBetterArcSmelt() {{
                    x = -16;
                    y = -4;
                }}, new DrawBetterArcSmelt() {{
                    x = 6;
                    y = -4;
                }}, new DrawDefault(), new DrawGlowRegion());
            }};
            plasmaExtractor = new ModifiedbeamDrill("plasma-extractor") {{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.production, with(silicon, 200, zinc, 350));
                tier = 1;
                itemCapacity = 50;
                squareSprite = false;
                consumePower(0.25f);

                drillTime = 240;
                size = 4;
                range = 8;
                consumeLiquid(muriaticAcid, 0.125f);
                consumeLiquid(oxygen, 1).boost();
                optionalBoostIntensity = 1.5f;
                heatColor = Color.valueOf("9d8cf2");
                boostHeatColor = Color.valueOf("e1f28c");

            }};
            beamBore = new ModifiedbeamDrill("beam-bore") {{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.production, with(aluminum, 250, copper, 75));
                tier = 1;
                consumePower(.25f);
                itemCapacity = 50;
                squareSprite = false;
                drillTime = 120;
                size = 3;
                range = 6;
                consumeLiquid(fumes, 0.125f).boost();
                optionalBoostIntensity = 1f;
                heatColor = Color.valueOf("9d8cf2");
                boostHeatColor = Color.valueOf("e1f28c");
            }};
            fumeMixer = new AquaGenericCrafter("fume-mixer") {{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.crafting, with(ferrosilicon, 800, metaglass, 1200, cupronickel, 2000, graphite, 1500));
                size = 6;
                craftTime = 5 * 60f;
                squareSprite = false;
                baseExplosiveness = 10;
                consumeLiquids(LiquidStack.with(haze, 3, oxygen, 2));
                outputLiquid = new LiquidStack(fumes, 4);
                consumeItems(ItemStack.with(coal, 35, brimstone, 16));
                itemCapacity = 350;
                liquidCapacity = 900;
                drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                    layer = shadow;
                    drawIcon = false;
                }}, new DrawRegion("-bottom"), new DrawRegion("-rotator") {{
                    y = 35 / 4f;
                    x = 20 / 4f;
                    spinSprite = true;
                    rotateSpeed = 2;
                }}, new DrawRegion("-rotator") {{
                    y = 35 / 4f;
                    x = -20 / 4f;
                    spinSprite = true;
                    rotateSpeed = 2;
                }}, new DrawLiquidTile(fumes) {{
                    alpha = 0.8f;
                    padding = 2;
                }}, new DrawDefault()
                        , new PhaseOffsetGlowRegion("-glow") {{
                    alpha = 0.55f;
                    color = Color.valueOf("e68569");
                    glowIntensity = 0.4f;
                    glowScale = 9f;
                    layer = Layer.block + 3;
                    blending = Blending.additive;
                    phaseOffset = 20;
                }}, new PhaseOffsetGlowRegion("-glow1") {{
                    alpha = 0.40f;
                    color = Color.valueOf("e68569");
                    glowIntensity = 0.4f;
                    glowScale = 7f;
                    layer = Layer.block + 3;
                    blending = Blending.additive;
                    phaseOffset = 10;
                }});
            }};
            DrillDerrick = new GroundDrill("drill-derrick") {{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.production, with(aluminum, 150, copper, 200, silicon, 700));
                size = 5;
                drillTime = 125;
                consumePower(2);
                consumeLiquid(fumes, 0.125f).booster = false;
                consumeLiquid(muriaticAcid, 0.05f).boost();
                liquidBoostIntensity = 1.5f;
                squareSprite = false;
                itemCapacity = 200;
                liquidCapacity = 120;
                tier = 4;
                drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                    layer = shadow;
                }}, new DrawDefault(), new DrawRegion("-rotator") {{
                    rotateSpeed = 1.5f;
                    spinSprite = true;
                }}, new DrawRegion("-top"));
            }};
            galenaCrucible = new AquaGenericCrafter("galena-crucible") {{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.crafting, with(zinc, 500, silicon, 200, graphite, 120));
                size = 6;
                consumeLiquids(LiquidStack.with(muriaticAcid, 0.5f));
                itemCapacity = 150;
                consumePower(6);
                liquidCapacity = 150;
                squareSprite = false;
                craftTime = 5 * 60f;
                consumeItem(galena, 40);
                outputItems = ItemStack.with(lead, 40, brimstone, 25, copper, 15);
                drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                    layer = shadow;
                    drawIcon = false;
                }}, new DrawDefault(), new DrawGlowRegion("-glow") {{
                    alpha = 0.4f;
                    color = Color.valueOf("e68569");
                    glowIntensity = 0.3f;
                    glowScale = 5f;
                    layer = Layer.block + 3;
                    blending = Blending.additive;
                }}, new DrawGlowRegion("-glow1") {{
                    alpha = 0.45f;
                    color = Color.valueOf("e68569");
                    glowIntensity = 0.35f;
                    glowScale = 6f;
                    layer = Layer.block + 3;
                    blending = Blending.additive;
                }});
            }};
            glassPulverizer = new AquaGenericCrafter("metaglass-pulverizer"){{
                requirements(Category.crafting, with(silicon, 500, zinc, 450, metaglass, 120));
                size = 4;
                craftTime = 240;
                itemCapacity = 40;
                consumePower(5);
                consumeItem(metaglass, 15);
                outputItems = new ItemStack[]{
                     new ItemStack(lead, 5),
                        new ItemStack(sand, 7),
                        new ItemStack(silicon, 1)
                };
                outputLiquid = new LiquidStack(oxygen, 0.0125f);
                liquidCapacity = 20;
            }};
            fumeSeparator = new AquaGenericCrafter("fume-separator") {{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.crafting, with(silicon, 500, zinc, 450, metaglass, 200));
                size = 6;
                rotate = true;
                rotateDraw = false;
                itemCapacity = 36;
                consumePower(5);
                liquidCapacity = 120;
                consumeLiquid(fumes, 2);
                outputLiquids = LiquidStack.with(oil, 0.5f, haze, 2f, muriaticAcid, 1f);
                outputItem = new ItemStack(brimstone, 4);
                liquidOutputDirections = new int[]{3, 1, 2};
            }};
        }};
        coolingTower = new AquaGenericCrafter("cooling-tower"){{
            size = 10;
            consumePower(10);
            consumeLiquid(haze, 20);
            outputLiquid = new LiquidStack(water, 16);
            squareSprite = false;
            liquidOutputDirections = new int[]{1};
            liquidCapacity = 10000;
        }};
        harvester = new GroundDrill("harvester") {{
            alwaysUnlocked = true;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.production, with(nickel, 70, lead, 50, silicon, 90));
            size = 3;
            drillTime = 150;
            liquidBoostIntensity = 1.25f;
            consumePower(.125f);
            consumeLiquid(Liquids.water, 0.75f).boost();
            consumeItems(new ItemStack(cupronickel, 2)).boost();
            itemCapacity = 25;
            separateItemCapacity = true;
            ItemBoostUseTime = 6 * 60;
            itemBoostIntensity = 1.2f;
            tier = 2;
            squareSprite = false;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawPistons() {{
                sides = 1;
                angleOffset = 90;
                sinMag = 2.2f;
            }}, new DrawDefault(), new DrawGlowRegion("-glow") {{
                alpha = 0.4f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.3f;
                glowScale = 5f;
                layer = Layer.block + 3;
                blending = Blending.additive;
            }}, new DrawGlowRegion("-glow1") {{
                alpha = 0.45f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.35f;
                glowScale = 6f;
                layer = Layer.block + 3;
                blending = Blending.additive;
            }}, new DrawGlowRegion("-glow2") {{
                alpha = 0.3f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 7f;
                layer = Layer.block + 3;
                blending = Blending.additive;
            }}, new DrawRegion("-rotator") {{
                spinSprite = true;
                y = -3f;
                x = -2f;
                layer = Layer.block + 4;
                rotateSpeed = 1.5f;
            }}, new DrawRegion("-rotator") {{
                spinSprite = true;
                rotateSpeed = -1.5f;
                rotation = 45;
                y = -28 / 4f;
                x = -24 / 4f;
                layer = Layer.block + 4;
            }});
        }};
        fluxExcavator = new GroundDrill("flux-excavator") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.production, with(nickel, 70, lead, 50, silicon, 90));
            size = 9;
            drillTime = 5500;
            heatRequirement = 50;
            hasHeat = true;

            overheatScale = 0.75f;
            maxEfficiency = 10;
            consumeLiquid(oil, 34).boost();

            consumeItem(steel, 10).boost();
            consumePower(25);
            itemBoostIntensity = 1.54f;
            liquidBoostIntensity = 1.54f;
            itemCapacity = 2500;
            liquidCapacity = 8000;
            ItemBoostUseTime = 240;
            tier = 5;
            drawer = new DrawDefault();
        }};
        CentrifugalPump = new Pump("centrifugal-pump") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.liquid, with(copper, 120, metaglass, 50));
            size = 3;
            consumePower(0.75f);
            squareSprite = true;
            pumpAmount = 0.375f;
            liquidCapacity = 300;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(water, 1.5f) {{
                alpha = 0.6f;
            }}
                    , new DrawLiquidTile(oil, 1.5f) {{
                alpha = 0.6f;
            }}, new DrawLiquidTile(water, 1.5f) {{
                alpha = 0.6f;
            }}, new DrawDefault(), new DrawGlowRegion("-glow") {{
                alpha = 0.3f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 7f;
                layer = Layer.block + 3;
                blending = Blending.additive;
            }}, new DrawGlowRegion("-glow1") {{
                alpha = 0.3f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.3f;
                glowScale = 5f;
                layer = Layer.block + 3;
                blending = Blending.additive;
            }});
        }};
        pumpAssembly = new Pump("pump-assembly") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.liquid, with(cupronickel, 500, ferricMatter, 250, metaglass, 250, graphite, 400));
            size = 6;
            squareSprite = false;
            consumePower(3.5f);
            squareSprite = true;
            pumpAmount = 0.625f;
            liquidCapacity = 4800;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(water, 1.5f) {{
                alpha = 0.6f;
            }}
                    , new DrawBlurSpin() {{
                suffix = "-fan";
                x = -31 / 4f;
                y = -14 / 4f;
                rotateSpeed = 4f;
            }}, new DrawBlurSpin() {{
                suffix = "-fan";
                x = 29 / 4f;
                y = 44 / 4f;
                rotateSpeed = 4f;
            }}, new DrawBlurSpin() {{
                suffix = "-fan";
                x = -31 / 4f;
                y = -14 / 4f;
                rotateSpeed = -2f;
            }}, new DrawBlurSpin() {{
                suffix = "-fan";
                x = 29 / 4f;
                y = 44 / 4f;
                rotateSpeed = -2f;
            }}, new DrawLiquidTile(oil, 1.5f) {{
                alpha = 0.6f;
            }}, new DrawLiquidTile(water, 1.5f) {{
                alpha = 0.6f;
            }}, new DrawDefault(), new DrawGlowRegion("-glow") {{
                alpha = 0.3f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 7f;
                layer = heat;
                blending = Blending.additive;
            }});
        }};
        SolidBoiler = new AquaGenericCrafter("solid-boiler") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(lead, 700, copper, 900, metaglass, 500, graphite, 800));
            size = 7;
            itemCapacity = 60;
            squareSprite = false;

            updateEffect = Fx.steam;
            updateEffectChance = 0.05f;
            craftEffect = AquaFx.boilerSmoke;
            craftTime = 10f;
            liquidCapacity = 1000;
            consume(new ConsumeItemFlammable(0.25f));
            consume(new ConsumeItemExplode(0.2f));
            consumeLiquids(new LiquidStack(water, 9), new LiquidStack(air, 3.5f));
            outputLiquid = new LiquidStack(haze, 8.5f);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new BetterDrawParticles() {{
                color = Color.valueOf("dd5f41");
                alpha = 0.4f;
                y = 4;
                sides = 12;
                x = 18;
                particleSize = 2f;
                particles = 18;
                particleRad = 8f;
                particleLife = 60f;
            }}, new BetterDrawParticles() {{
                color = Color.valueOf("fbc14e");
                alpha = 0.6f;
                y = 4;
                sides = 12;
                x = 18;
                particleSize = 2f;
                particles = 12;
                particleRad = 6f;
                particleLife = 60f;
            }}, new BetterDrawParticles() {{
                color = Color.valueOf("dd5f41");
                alpha = 0.4f;
                y = 12;
                sides = 12;
                x = 18;
                particleSize = 2f;
                particles = 18;
                particleRad = 8f;
                particleLife = 60f;
            }}, new BetterDrawParticles() {{
                color = Color.valueOf("fbc14e");
                alpha = 0.6f;
                y = 12;
                sides = 12;
                x = 18;
                particleSize = 2f;
                particles = 12;
                particleRad = 6f;
                particleLife = 60f;
            }}, new BetterDrawParticles() {{
                color = Color.valueOf("dd5f41");
                alpha = 0.4f;
                y = -8;
                sides = 12;
                x = 18;
                particleSize = 2f;
                particles = 18;
                particleRad = 8f;
                particleLife = 60f;
            }}, new BetterDrawParticles() {{
                color = Color.valueOf("fbc14e");
                alpha = 0.6f;
                y = -8;
                sides = 12;
                x = 18;
                particleSize = 2f;
                particles = 12;
                particleRad = 6f;
                particleLife = 60f;
            }}, new BetterDrawParticles() {{
                color = Color.valueOf("dd5f41");
                alpha = 0.4f;
                y = -12;
                sides = 12;
                x = 18;
                particleSize = 2f;
                particles = 18;
                particleRad = 8f;
                particleLife = 60f;
            }}, new BetterDrawParticles() {{
                color = Color.valueOf("fbc14e");
                alpha = 0.6f;
                y = -12;
                sides = 12;
                x = 18;
                particleSize = 2f;
                particles = 12;
                particleRad = 6f;
                particleLife = 60f;
            }}, new DrawRegion("-cover"), new DrawLiquidTile(water, 2) {{
                padRight = 10;
                padLeft = 10;
                padBottom = 5;
                padTop = 2.5f;
            }}, new DrawDefault(), new PhaseOffsetGlowRegion("-glow") {{
                alpha = 0.35f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.3f;
                glowScale = 5f;
                layer = heat;
                blending = Blending.additive;
            }}, new PhaseOffsetGlowRegion("-glow1") {{
                alpha = 0.4f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.25f;
                glowScale = 6f;
                layer = heat;
                blending = Blending.additive;
                phaseOffset = 10;
            }}, new PhaseOffsetGlowRegion("-glow2") {{
                alpha = 0.45f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 8f;
                layer = heat;
                blending = Blending.additive;
                phaseOffset = 15;
            }}, new PhaseOffsetGlowRegion("-glow3") {{
                alpha = 0.55f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.5f;
                glowScale = 10f;
                layer = heat;
                blending = Blending.additive;
                phaseOffset = 20;
            }}, new DrawPistons() {{
                sides = 1;
            }});
        }};
        cupronickelAlloyer = new AquaGenericCrafter("cupronickel-alloying-crucible") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(nickel, 700, copper, 400, graphite, 150));
            consumePower(1.5f);
            squareSprite = false;
            size = 4;
            baseEfficiency = 1;
            heatRequirement = 30;
            hasHeat = true;

            maxEfficiency = 4;
            craftTime = 120;
            consumeItems(new ItemStack(copper, 9), new ItemStack(nickel, 3));
            outputItem = new ItemStack(cupronickel, 12);
            updateEffect = Fx.coalSmeltsmoke;
            updateEffectChance = 0.07f;
            ambientSound = AquaSounds.derrick;
            consumeLiquid(air, 3);
            liquidCapacity = 700;
            itemCapacity = 60;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawSoftParticles() {{
                color = Color.valueOf("921f12");
                color2 = Color.valueOf("ff9f4f");
                alpha = 0.5f;
                x = -2f;
                particleRad = 8f;
                particleSize = 6f;
                particleLife = 45f;
                particles = 35;
            }}, new DrawDefault(), new DrawHeatInputBitmask(), new AquaHeatRegion("-heats"));
        }};
        AnnealingOven = new AquaGenericCrafter("metaglass-annealing-furnace") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(lead, 300, copper, 550));
            size = 5;
            itemCapacity = 60;
            squareSprite = false;
            baseEfficiency = 1;
            heatRequirement = 15;
            maxEfficiency = 4;
            overheatScale = 0.5f;
            hasHeat = true;

            liquidCapacity = 600;
            craftTime = 120;
            consumeItems(new ItemStack(lead, 10), new ItemStack(sand, 10));
            outputItem = new ItemStack(metaglass, 15);
            updateEffect = Fx.coalSmeltsmoke;
            craftEffect = new SeqEffect() {{
                effects = new mindustry.entities.Effect[]{
                        AquaFx.ovenCraft,
                        AquaFx.ovenCraft,
                        AquaFx.ovenCraft,
                        AquaFx.ovenCraft,
                        AquaFx.ovenCraft
                };
            }};
            updateEffectChance = 0.07f;
            ambientSound = AquaSounds.refine;
            ambientSoundVolume = 0.07f;
            consumeLiquid(air, 4);
            consumePower(3);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom")
                    , new DrawWheel() {{
                width = 152 / 4f;
                height = 106 / 4f;
                rotation = 0;
                sideCount = 36;
                rotationSpeed = 0.8f;
                suffix = "-tick";
                x = 0;
                y = 0;
                wheelColors = new Color[]{
                        //I should set this as a Pallete or smth
                        Color.valueOf("8da6ab"),
                        Color.valueOf("333f4b"),
                        Color.valueOf("0f151b")
                };
            }}, new DrawWheel() {{
                width = 25 / 4f;
                height = 110 / 4f;
                rotation = 0;
                sideCount = 12;
                rotationSpeed = -1f;
                suffix = "-tick";
                x = -6;
                y = 0;
                wheelColors = new Color[]{
                        //I should set this as a Pallete or smth
                        Color.valueOf("8da6ab"),
                        Color.valueOf("333f4b"),
                        Color.valueOf("0f151b")
                };
            }}, new DrawWheel() {{
                width = 25 / 4f;
                height = 110 / 4f;
                rotation = 0;
                sideCount = 12;
                rotationSpeed = -1f;
                suffix = "-tick";
                x = 6;
                y = 0;
                wheelColors = new Color[]{
                        //I should set this as a Pallete or smth
                        Color.valueOf("8da6ab"),
                        Color.valueOf("333f4b"),
                        Color.valueOf("0f151b")
                };
            }}, new DrawPistons() {{
                sides = 1;
                sinMag = 18;
                sinScl = 12;
                lenOffset = -6f;
            }}, new DrawPistons() {{
                suffix = "-piston2";
                sides = 1;
                sinMag = 18;
                sinScl = 7;
                lenOffset = -6f;
            }}, new DrawDefault(), new DrawGlowRegion("-glow") {{
                alpha = 0.55f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 9f;
                layer = heat;
                blending = Blending.additive;
            }}, new DrawGlowRegion("-glow1") {{
                alpha = 0.40f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 7f;
                layer = heat;
                blending = Blending.additive;
            }}, new DrawGlowRegion("-glow2") {{
                alpha = 0.350f;
                color = Pal.turretHeat;
                glowIntensity = 0.5f;
                glowScale = 8f;
                layer = heat;
                blending = Blending.additive;
            }}, new DrawGlowRegion("-glow3") {{
                alpha = 0.450f;
                color = Pal.turretHeat;
                glowIntensity = 0.8f;
                glowScale = 5f;
                layer = heat;
                blending = Blending.additive;
            }}, new DrawHeatInputBitmask());
        }};
        thermalCrackingUnit = new AquaGenericCrafter("thermal-cracking-unit") {{
            requirements(Category.crafting, with(copper, 250, silicon, 600, metaglass, 900));
            shownPlanets.addAll(tantros2);
            heatRequirement = 30;
            maxEfficiency = 5;
            hasHeat = true;
            craftTime = 2.5f * 60f;
            overheatScale = 0.75f;
            rotate = true;
            rotateDraw = false;
            size = 5;
            updateEffectChance = 0.06f;
            updateEffect = AquaFx.heatEngineGenerate;
            squareSprite = false;
            consumeLiquid(oil, 3);
            outputLiquids = LiquidStack.with(methane, 1, petroleum, 1.5f);
            liquidOutputDirections = new int[]{1, 3};
            outputItem = new ItemStack(graphite, 3);
            liquidCapacity = 300;
            itemCapacity = 60;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawRegion("-mider"), new DrawLiquidTile(petroleum, 4) {{
                padBottom = 7;
            }}, new DrawRegion("-mid"), new DrawLiquidTile(methane) {{
                padRight = padLeft = 4;
                padBottom = 12;
                padTop = 73 / 4f;
            }}, new DrawDefault(), new DrawGlowRegion(), new DrawHeatInputBitmask(), new AquaDrawLiquidOutputs());
        }};
        polymerPress = new AquaGenericCrafter("polymer-press") {{
            requirements(Category.crafting, with(silicon, 700, graphite, 250, metaglass, 500));
            size = 4;
            liquidCapacity = 400;
            squareSprite = false;
            itemCapacity = 60;
            outputItem = new ItemStack(polymer, 30);
            craftTime = 30 / 4f * 60f;
            heatRequirement = 20;
            overheatScale = 1f;
            baseEfficiency = 0;
            hasHeat = true;
            maxEfficiency = 3;
            consumePower(4);
            consumeLiquids(LiquidStack.with(petroleum, 4, haze, 8.5f));
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(petroleum, 3), new DrawPump("-press") {{
                timeOffset = 0;
                x = 16 / 4f;
                y = -30 / 4f;
            }}, new DrawPump("-press") {{
                timeOffset = 30;
                x = -32 / 4f;
                y = -30 / 4f;
            }}, new DrawPump("-press") {{
                timeOffset = 60;
                x = -32 / 4f;
                y = 16 / 4f;
            }}, new DrawPump("-press") {{
                timeOffset = 90;
                x = 16 / 4f;
                y = 16 / 4f;
            }}, new DrawDefault(), new DrawHeatInputBitmask());
        }};
        SilicaOxidator = new AquaGenericCrafter("silicon-oxidator") {{
            requirements(Category.crafting, with(copper, 300, graphite, 200, metaglass, 200));
            size = 3;
            squareSprite = false;
            itemCapacity = 60;
            liquidCapacity = 200;
            craftTime = 150;
            consumeItem(silicon, 20);
            consumeLiquid(oxygen, 0.5f);
            outputItem = new ItemStack(sand, 20);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawParticles() {{
                color = Color.valueOf("ffb5ad");
                alpha = 0.3f;
                particleSize = 3f;
                particles = 9;
                particleRad = 6f;
                particleLife = 90f;
                rotateScl = 1.5f;
            }}, new DrawLiquidTile(oxygen, 3) {{
                alpha = 0.5f;
            }}, new DrawDefault());
        }};
        arcFurnace = new AquaGenericCrafter("arc-furnace") {{
            requirements(Category.crafting, with(graphite, 250, copper, 120, metaglass, 150));
            size = 4;
            squareSprite = false;
            craftTime = 300;
            itemCapacity = 80;
            liquidCapacity = 200;
            consumePower(4);
            consumeItem(sand, 40);
            consumeItem(graphite, 2).boost();
            outputLiquid = new LiquidStack(oxygen, 0.75f);
            outputItem = new ItemStack(silicon, 40);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawSoftParticles() {{
                alpha = 0.35f;
                particleRad = 5f;
                particleSize = 9f;
                particleLife = 120f;
                particles = 15;
            }}, new DrawSoftParticles() {{
                color = Color.valueOf("88d9eb");
                color2 = Color.white;
                alpha = 0.25f;
                particleRad = 3f;
                particleSize = 7f;
                particleLife = 120f;
                particles = 8;
            }}, new DrawSoftParticles() {{
                alpha = 0.35f;
                particleRad = 5f;
                y = 34 / 4f;
                particleSize = 9f;
                particleLife = 120f;
                particles = 15;
            }}, new DrawSoftParticles() {{
                color = Color.valueOf("88d9eb");
                color2 = Color.white;
                alpha = 0.25f;
                y = 34 / 4f;
                particleRad = 3f;
                particleSize = 7f;
                particleLife = 120f;
                particles = 8;
            }}, new DrawSoftParticles() {{
                alpha = 0.35f;
                particleRad = 5f;
                y = 27 / 4f;
                x = -37 / 4f;
                particleSize = 9f;
                particleLife = 120f;
                particles = 15;
            }}, new DrawSoftParticles() {{
                color = Color.valueOf("88d9eb");
                color2 = Color.white;
                alpha = 0.25f;
                y = 27 / 4f;
                x = -37 / 4f;
                particleRad = 3f;
                particleSize = 7f;
                particleLife = 120f;
                particles = 8;
            }}, new DrawSoftParticles() {{
                alpha = 0.35f;
                particleRad = 5f;
                x = 32 / 4f;
                particleSize = 9f;
                particleLife = 120f;
                particles = 15;
            }}, new DrawSoftParticles() {{
                color = Color.valueOf("88d9eb");
                color2 = Color.white;
                alpha = 0.25f;
                x = 32 / 4f;
                particleRad = 3f;
                particleSize = 7f;
                particleLife = 120f;
                particles = 8;
            }}, new DrawDefault(), new DrawGlowRegion() {{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
//        steamCrackingUnit = new GenericCrafter("haze-cracking-unit"){{
//            requirements(Category.crafting, with( steel, 500, ferrosilicon, 250, metaglass, 1000));
//            shownPlanets.addAll(tantros2);
//            craftTime = 1.5f*60f;
//            size = 5;
//            updateEffectChance = 0.08f;
//            updateEffect = Fx.steamCoolSmoke;
//            squareSprite = false;
//            consumeLiquids(LiquidStack.with(petroleum, 4, haze, 2));
//            outputLiquid = new LiquidStack(methane, 2);
//            outputItems = ItemStack.with(brimstone, 3);
//            liquidCapacity = 300;
//            itemCapacity = 60;
//            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawDefault(), new DrawGlowRegion());
//        }};
        desulferizationAssembly = new AquaGenericCrafter("desulferization-assembly") {{
            requirements(Category.crafting, with(metaglass, 250, copper, 500, lead, 1200, graphite, 450));
            shownPlanets.addAll(tantros2);
            craftTime = 4 * 60f;
            heatRequirement = 45;
            hasHeat = true;

            maxEfficiency = 2;
            craftEffect = Fx.reactorsmoke;
            updateEffectChance = 0.08f;
            updateEffect = Fx.reactorsmoke;
            consumeLiquid(petroleum, 7.5f);
            outputItem = new ItemStack(brimstone, 15);
            outputLiquid = new LiquidStack(methane, 2.5f);
            liquidCapacity = 900;
            itemCapacity = 120;
            size = 5;
            squareSprite = false;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(petroleum, 3), new DrawDefault(), new DrawGlowRegion(), new DrawHeatInputBitmask(), new AquaHeatRegion("-heats"));
        }};
        coalLiquefactor = new AquaGenericCrafter("coal-liquefactor"){{
            requirements(Category.crafting, with(copper, 450, ferricMatter, 200, silicon, 800, aluminum, 250));
            hasHeat = true;
            heatRequirement = 25;
            overheatScale = 1;
            baseEfficiency = 0;
            maxEfficiency = 10;
            craftTime = 240;
            itemCapacity = 60;
            size = 4;
            squareSprite = false;
            liquidCapacity = 500;
            consumePower(2.5f);
            consumeItem(coal, 12);
            consumeLiquid(oxygen, 0.25f);
            outputLiquid = new LiquidStack(oil, 3f);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(oil, 4), new DrawDefault(), new DrawHeatInputBitmask("-heats"));
        }};

        inlet = new AquaGenericCrafter("inlet") {{
            requirements(Category.production, with(silicon, 70));
            shownPlanets.addAll(tantros2);
            craftTime = 10;
            researchCost = ItemStack.with(copper, 90);
            outputLiquid = new LiquidStack(halideWater, 0.25f);
            envDisabled = Env.groundOil | Env.scorching | Env.spores;
            liquidCapacity = 90;
            squareSprite = false;
            ambientSound = AquaSounds.waterAir;
            ambientSoundVolume = 0.01f;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawDefault(), new DrawParticles() {{
                color = Color.valueOf("455a5e");
                alpha = 0.2f;
                particleSize = 2.5f;
                particles = 20;
                particleRad = 2.5f;
                particleLife = 90f;
            }}, new AquaHeatRegion("-heats"));
        }};
        inletArray = new AquaGenericCrafter("inlet-array") {{
            requirements(Category.production, with(copper, 150, ferricMatter, 100, metaglass, 75));
            shownPlanets.addAll(tantros2);
            craftTime = 10;
            size = 3;
            consumePower(1.25f);
            outputLiquid = new LiquidStack(halideWater, 4);
            envDisabled = Env.groundOil | Env.scorching | Env.spores;
            liquidCapacity = 700;
            squareSprite = false;
            ambientSound = AquaSounds.waterAir;
            ambientSoundVolume = 0.01f;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(halideWater) {{
                padRight = 11 / 4f;
                padLeft = 11 / 4f;
                padBottom = 22 / 4f;
                padTop = 19 / 4f;
            }}, new DrawRegion("-top"), new DrawParticles() {{
                color = Color.valueOf("455a5e");
                alpha = 0.25f;
                particleSize = 3f;
                particles = 20;
                particleRad = 3.5f;
                particleLife = 90f;
                x = 0;
                y = 16 / 4f;
            }}, new DrawParticles() {{
                color = Color.valueOf("455a5e");
                alpha = 0.25f;
                particleSize = 3f;
                particles = 20;
                particleRad = 3.5f;
                particleLife = 90f;
                x = 25 / 4f;
                y = -13 / 4f;
            }}, new DrawParticles() {{
                color = Color.valueOf("455a5e");
                alpha = 0.25f;
                particleSize = 3f;
                particles = 20;
                particleRad = 3.5f;
                particleLife = 90f;
                x = -25 / 4f;
                y = -13 / 4f;
            }});
        }};
        vacuumFreezer = new AquaGenericCrafter("vacuum-freezer") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(copper, 120, aluminum, 420));
            size = 6;
            squareSprite = false;
            ambientSound = Sounds.electricHum;
            consumePower(3);
            consumeLiquid(halideWater, 6.5f);
            craftTime = 10;
            outputLiquids = LiquidStack.with(air, 4f, water, 6.375f);
            liquidOutputDirections = new int[]{1, 4};
            rotate = true;
            rotateDraw = false;
            liquidCapacity = 600;
            regionRotated1 = 3;
            //heatRequirement = -10;
            //overheatScale = 0.8f;
            //maxEfficiency = 10;
            //flipHeatScale = true;
            craftEffect = new MultiEffect(new NewParticleEffect() {{
                y = -6 / 4f;
                baseRotation = 45;
                cone = 20;
                length = 110;
                colorFrom = Color.valueOf("558dbe");
                colorTo = Color.valueOf("ffffff").a(0);
                sizeInterp = Interp.linear;
                interp = pow5Out;
                sizeFrom = 0;
                lifetime = 220;
                sizeTo = 6;
                particles = 5;
            }}, new NewParticleEffect() {{
                y = -6 / 4f;
                x = -49 / 4f;
                baseRotation = 45;
                cone = 20;
                length = 110;
                colorFrom = Color.valueOf("558dbe");
                colorTo = Color.valueOf("ffffff").a(0);
                sizeInterp = Interp.linear;
                interp = pow5Out;
                sizeFrom = 0;
                lifetime = 220;
                sizeTo = 6;
                particles = 5;
            }}, new NewParticleEffect() {{
                y = -6 / 4f;
                x = 44 / 4f;
                baseRotation = 45;
                cone = 20;
                length = 110;
                colorFrom = Color.valueOf("558dbe");
                colorTo = Color.valueOf("ffffff").a(0);
                sizeInterp = Interp.linear;
                interp = pow5Out;
                sizeFrom = 0;
                lifetime = 220;
                sizeTo = 6;
                particles = 5;
            }});
            updateEffect = Fx.steam;
            updateEffectChance = 0.01f;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawDefault() {
            }, new DrawPump("-pump") {{
                y = 45 / 4f;
                x = -38 / 4f;
            }}, new DrawPump("-pump") {{
                y = 45 / 4f;
                x = -63 / 4f;
                timeOffset = 10;
            }}, new DrawLiquidTile(air) {{
                padTop = 37 / 4f;
                padRight = 121 / 4f;
                padBottom = 132 / 4f;
                padLeft = 49 / 4f;
            }}, new DrawLiquidTile(water) {{
                padTop = 37 / 4f;
                padRight = 148 / 4f;
                padBottom = 132 / 4f;
                padLeft = 22 / 4f;
            }}, new DrawRegion("-top"), new PhaseOffsetGlowRegion("-glow") {{
                alpha = 0.55f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 9f;
                layer = Layer.block + 3;
                blending = Blending.additive;
                phaseOffset = 20;
            }}, new PhaseOffsetGlowRegion("-glow1") {{
                alpha = 0.40f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 7f;
                layer = Layer.block + 3;
                blending = Blending.additive;
                phaseOffset = 10;
            }}, new AquaDrawLiquidOutputs());
        }};
        atmosphericIntake = new AquaGenericCrafter("air-intake") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.production, with(nickel, 50, silicon, 150));
            size = 2;
            squareSprite = false;
            craftTime = 10;
            liquidCapacity = 200;
            consumePower(.25f);
            envDisabled = Env.underwater | Env.space;
            outputLiquid = new LiquidStack(air, 1);
            ambientSound = Sounds.windhowl;
            ambientSoundVolume = 0.02f;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
            }}, new DrawDefault(), new DrawBlurSpin("-fan", 8f), new DrawRegion("-top"), new DrawParticles() {{
                color = Color.valueOf("d4f0ff");
                alpha = 0.3f;
                particleSize = 2.5f;
                particles = 14;
                particleRad = 4f;
                particleLife = 140f;
            }});
        }};
        acuminiteDegredationArray = new AquaGenericCrafter("acuminite-degredation-array") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(manganese, 550, aluminum, 700, strontium, 500));
            squareSprite = false;
            size = 8;
            ignoreLiquidFullness = true;
            dumpExtraLiquid = true;
            consumeItem(acuminite, 60);
            craftTime = 5 * 60f;
            liquidCapacity = 1000;
            itemCapacity = 180;
            ambientSound = AquaSounds.waterRumble;
            ambientSoundVolume = 0.09f;
            updateEffectChance = 0.05f;
            updateEffect = Fx.coalSmeltsmoke;
            outputLiquid = new LiquidStack(fluorine, 0.75f);
            outputItems = ItemStack.with(aluminum, 60, strontium, 60);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawCellsNew() {{
                range = 70 / 4f;
                radius = 2.5f;
                particles = 400;
                lifetime = 90f * 5f;
                particleColorFrom = Color.valueOf("9aa86d");
                particleColorTo = Color.valueOf("9d6b44");
                color = Color.valueOf("715041");
            }}, new DrawBubblesNew(Color.valueOf("edcc97").a(0.5f)) {{
                spread = 70 / 4f;
                amount = 70;
                timeScl = 20;
                circle = true;
            }}, new DrawRegion("-panel"),
                    new DrawBlurSpin("-fan", 0.6f * 9f) {{
                        blurThresh = 0.6f;
                        x = -96 / 4f;
                        y = -10 / 4f;
                    }},
                    new DrawBlurSpin("-fan", 0.6f * 9f) {{
                        blurThresh = 0.6f;
                        x = -96 / 4f;
                        y = 40 / 4f;
                    }},
                    new DrawBlurSpin("-fan", 0.6f * 9f) {{
                        blurThresh = 0.6f;
                        x = -96 / 4f;
                        y = 90 / 4f;
                    }}, new DrawPistons() {{
                suffix = "-pistone";
                sides = 1;
                angleOffset = 90;
                sinMag = 3.5f;
                lenOffset = 1;
            }}, new DrawWheel() {{
                width = 42 / 4f;
                height = 42 / 4f;
                rotation = -45;
                rotationSpeed = 1.2f;
                suffix = "-tick";
                x = 45 / 4f;
                y = -50 / 4f;
                wheelColors = new Color[]{
                        //I should set this as a Pallete or smth
                        Color.valueOf("8da6ab"),
                        Color.valueOf("333f4b"),
                        Color.valueOf("0f151b")
                };
            }}, new DrawLiquidTile(fluorine) {{
                padBottom = 61 / 4f;
                padRight = 70 / 4f;
                padTop = 160 / 4f;
                padLeft = 150 / 4f;
            }}, new DrawAdvancedPistons() {{
                angleOffset = 270;
                sides = 1;
                sinMag = 2.5f;
            }}, new DrawCircles() {{
                y = 35 / 4f;
                x = -2f;
                color = Color.valueOf("edcc97");
                radius = 18;
                amount = 2;
                timeScl = 220;
            }}, new DrawRegion("-rotator") {{
                spinSprite = true;
                rotateSpeed = 0.5f;
                y = 35 / 4f;
                x = -2f;
            }}, new DrawDefault(), new DrawRegion("-shadow"), new DrawGlowRegion() {{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }}, new DrawGlowRegion("-glow2") {{
                glowIntensity = 0.8f;
                glowScale = 12;
                alpha = 0.6f;
                color = Color.valueOf("ef6a60");
            }}, new DrawGlowRegion("-glow3") {{
                glowIntensity = 0.5f;
                glowScale = 10;
                alpha = 0.6f;
                color = Color.valueOf("ef6a60");
            }});
        }};
        atmosphericCentrifuge = new AquaGenericCrafter("atmospheric-centrifuge") {{
            liquidOutputDirections = new int[]{1, 2, 3};
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(ferricMatter, 150, copper, 100));
            size = 4;
            rotateDraw = false;
            consumePower(2.5f);

            rotate = true;
            regionRotated1 = 1;
            liquidCapacity = 400;
            consumeLiquid(air, 3.25f);
            outputLiquids = LiquidStack.with(argon, 0.25f, oxygen, 2f, nitrogen, 1.25f);
            squareSprite = false;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawOrbitRegions("-centrifuge", 4, 7f, 3.5f), new DrawLiquidTile(air) {{
                alpha = 0.6f;
                padding = 2;
            }}, new DrawLiquidTile(oxygen) {{
                padLeft = 7 / 4f;
                padBottom = 8 / 4f;
                padTop = 45 / 4f;
                padRight = 99 / 4f;
            }}, new DrawLiquidTile(nitrogen) {{
                padLeft = 41 / 4f;
                padBottom = 30 / 4f;
                padTop = 16 / 4f;
                padRight = 18 / 4f;
            }}, new DrawDefault(), new DrawLiquidTile(argon) {{
                padLeft = 32 / 4f;
                padBottom = 37 / 4f;
                padTop = 68 / 4f;
                padRight = 87 / 4f;
            }}, new DrawRegion("-top"), new DrawPump("-pump") {{
                y = 11 / 4f;
                x = 1;
                timeOffset = 0;
            }}, new DrawPump("-pump") {{
                y = -23 / 4f;
                x = 1;
                timeOffset = 10;
            }}, new DrawPump("-pump") {{
                y = -7 / 4f;
                x = 1;
                timeOffset = 20;
            }}, new PhaseOffsetGlowRegion("-glow") {{
                alpha = 0.35f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.3f;
                glowScale = 5f;
                layer = Layer.block + 3;
                blending = Blending.additive;
                phaseOffset = 10;
            }}, new PhaseOffsetGlowRegion("-glow1") {{
                alpha = 0.40f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 7f;
                layer = Layer.block + 3;
                blending = Blending.additive;
                phaseOffset = 20;
            }}, new AquaDrawLiquidOutputs());
        }};
        steelFoundry = new AquaGenericCrafter("blast-furnace") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(polymer, 900, ferrosilicon, 1200, cupronickel, 600, metaglass, 450, graphite, 650, ferricMatter, 900, aluminum, 1200));
            size = 7;
            rotateDraw = false;
            rotate = true;
            liquidCapacity = 2000;
            itemCapacity = 200;
            craftTime = 5 * 60f;
            consumeItems(ItemStack.with(ferricMatter, 50, coal, 80));
            outputItems = ItemStack.with(steel, 50, graphite, 40);
            consumeLiquid(oxygen, 2);
            outputLiquid = new LiquidStack(slag, 4);
            squareSprite = false;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(slag) {{
                alpha = 0.7f;
                padBottom = 2;
                padLeft = 22;
                padTop = 2;
                padRight = 2;
            }}, new DrawDefault(), new PhaseOffsetGlowRegion("-glow") {{
                alpha = 0.3f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 6f;
                layer = Layer.block + 3;
                blending = Blending.additive;
                phaseOffset = 10;
            }}, new PhaseOffsetGlowRegion("-glow1") {{
                alpha = 0.40f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 7f;
                layer = Layer.block + 3;
                blending = Blending.additive;
                phaseOffset = 20;
            }}, new PhaseOffsetGlowRegion("-glow2") {{
                alpha = 0.50f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.6f;
                glowScale = 7f;
                layer = Layer.block + 3;
                blending = Blending.additive;
                phaseOffset = 30;
            }});
        }};
        pinDrill = new GroundDrill("pin-drill") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.production, with(manganese, 40, aluminum, 50, strontium, 20));
            drillTime = 100;
            size = 1;
            squareSprite = false;
            tier = 3;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawDefault(), new DrawPump("-bit"));
        }};
        brineMixer = new AquaGenericCrafter("brine-mixer") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(zinc, 200, metaglass, 350, graphite, 250));
            size = 5;
            itemCapacity = 750;
            liquidCapacity = 2000;
            squareSprite = false;
            craftTime = 10 * 60f;
            consumeItem(salt, 250);
            consumeLiquid(water, 8.5f);
            consumePower(10);
            outputLiquid = new LiquidStack(brine, 8.5f);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(brine) {{
                padTop = 51 / 4f;
                padLeft = 3;
                padBottom = 78 / 4f;
                padRight = 3;
            }}, new DrawLiquidTile(water) {{
                padTop = 103 / 4f;
                padLeft = 3;
                padBottom = 29 / 4f;
                padRight = 3;
            }}, new DrawRegion("-top"), new DrawGlowRegion() {{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.5f;
                color = Color.valueOf("dd5858");
            }}, new DrawGlowRegion("-glow1") {{
                glowIntensity = 0.65f;
                glowScale = 6;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
        brineElectrolyzer = new AquaGenericCrafter("brine-electrolysis-manifold") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(aluminum, 1500, silicon, 3000, metaglass, 500, ferrosilicon, 500));
            size = 7;
            rotate = true;
            liquidCapacity = 4000;
            rotateDraw = false;
            consumePower(20);
            craftTime = 10f * 60f;
            liquidOutputDirections = new int[]{3, 2, 1};
            consumeLiquid(brine, 17);
            outputLiquids = new LiquidStack[]{
                    new LiquidStack(chlorine, 17),
                    new LiquidStack(hydrogen, 17),
                    new LiquidStack(hydroxide, 34)
            };
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(hydroxide) {{
                padBottom = 111 / 4f;
                alpha = 0.8f;
            }}, new DrawLiquidTile(chlorine) {{
                padTop = 113 / 4f;
                alpha = 0.8f;
            }}, new DrawRegion("-middle"), new DrawLiquidTile(hydrogen) {{
                padTop = 67 / 4f;
                padBottom = 130 / 4f;
                padRight = 151 / 4f;
                alpha = 0.8f;
            }}, new DrawRegion("-top"), new AquaDrawLiquidOutputs(), new DrawGlowRegion() {{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.5f;
                color = Color.valueOf("dd5858");
            }}, new DrawGlowRegion("-glow1") {{
                glowIntensity = 0.65f;
                glowScale = 6;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }}, new DrawGlowRegion("-glow2") {{
                glowIntensity = 0.9f;
                glowScale = 7;
                alpha = 0.6f;
                color = Color.valueOf("f5c5aa");
            }}, new DrawGlowRegion("-glow3") {{
                glowIntensity = 0.8f;
                glowScale = 8;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
        sporeProcessor = new AquaGenericCrafter("spore-processor"){{
            requirements(Category.crafting, with(cupronickel, 90, silicon, 150, metaglass, 40));
            size = 3;
            squareSprite = false;
            craftTime = 720;
            itemCapacity = 36;
            consumeItem(sporePod, 9);
            consumePower(0.5f);
            outputLiquid = new LiquidStack(bioPulp, 0.125f);
            liquidCapacity = 120;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(bioPulp, 4), new DrawPump("-pump"), new DrawDefault());
        }};
        algalTerrace = new AttributeCrafter("algal-terrace") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.production, with(metaglass, 1200, lead, 1200, ferricMatter, 500));
            size = 7;
            baseEfficiency = 0.5f;
            squareSprite = false;
            attribute = AquaAttributes.fertility;
            boostScale = 1 / (49 / 2f);
            consumePower(5);
            maxBoost = 2;
            liquidCapacity = 1200;
            itemCapacity = 20;
            minEfficiency = 0.25f;
            outputLiquid = new LiquidStack(bioPulp, 6.5f);
            consumeLiquids(LiquidStack.with(water, 10));
            craftTime = 90;
            consumeItem(brimstone, 1);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(water) {{
                padding = 3;
            }}, new DrawCells() {{
                range = 8 * 7 / 2f - 4;
                radius = 3;
                particleColorTo = Color.valueOf("9c592d");
                particleColorFrom = Color.valueOf("c5a74b");
                color = Color.valueOf("7b4624");
                particles = 400;
            }}, new DrawLiquidTile(bioPulp) {{
                padding = 3;
            }}, new DrawDefault());
        }};
        gasifier = new HeatProducer("gasifier") {{
            size = 6;
            squareSprite = false;
            liquidCapacity = 500;
            itemCapacity = 100;
            craftTime = 120;
            consumePower(1.25f);
            heatOutput = 65;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(ferricMatter, 250, silicon, 700, metaglass, 500));
            consumeLiquid(bioPulp, 1.25f);
            outputItem = new ItemStack(coal, 5);
            outputLiquid = new LiquidStack(haze, 0.5f);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawDefault(), new DrawHeatOutput(), new DrawGlowRegion() {{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.5f;
                color = Color.valueOf("dd5858");
            }}, new DrawGlowRegion("-glow1") {{
                glowIntensity = 0.9f;
                glowScale = 8;
                alpha = 0.6f;
                color = Color.valueOf("f5c5aa");
            }}, new DrawGlowRegion("-glow1") {{
                glowIntensity = 0.7f;
                glowScale = 6;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
        ultrafamicRefinery = new AquaGenericCrafter("ultramafic-refinery") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(cupronickel, 700, metaglass, 500, silicon, 2000, polymer, 800));
            size = 7;
            squareSprite = false;
            itemCapacity = 200;
            liquidCapacity = 500;
            craftTime = 5 * 60f;
            hasHeat = false;
            consumePower(10);
            consumeLiquid(air, 12);
            consumeItem(serpentine, 20);
            outputLiquids = LiquidStack.with(hydroxide, 4.25);
            outputItems = new ItemStack[]{
                    new ItemStack(sand, 4),
                    new ItemStack(aluminum, 2),
                    new ItemStack(ferricMatter, 8),
                    new ItemStack(nickel, 6),
                    new ItemStack(magnesiumPowder, 10)
            };
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"),
                    new DrawWheel() {{
                        x = 4;
                        y = -4f;
                        rotationSpeed = 1;
                        width = 150 / 4f;
                        sideCount = 17;
                        height = 100 / 4f;
                        wheelColors = new Color[]{
                                //I should set this as a Pallete or smth
                                Color.valueOf("8da6ab"),
                                Color.valueOf("333f4b"),
                                Color.valueOf("0f151b")
                        };
                    }}, new DrawWheel() {{
                x = 4;
                y = 4f;
                rotationSpeed = 1;
                width = 150 / 4f;
                sideCount = 12;
                height = 100 / 4f;
                wheelColors = new Color[]{
                        //I should set this as a Pallete or smth
                        Color.valueOf("8da6ab"),
                        Color.valueOf("333f4b"),
                        Color.valueOf("0f151b")
                };
            }}, new DrawDefault());
        }};
        convectionHeater = new HeatProducer("convection-heater") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(silicon, 150, copper, 350, metaglass, 200, graphite, 120));
            size = 3;
            squareSprite = false;
            consumePower(6);
            regionRotated1 = 1;
            consumeLiquid(water, 1);
            rotateDraw = false;
            heatOutput = 10;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawDefault(), new DrawGlowRegion() {{
                color = new Color(1f, 0.22f, 0.22f, 0.4f);
            }}, new DrawHeatOutput() {{
                heatColor = new Color(1f, 0.22f, 0.22f, 0.4f);
            }});
        }};

        coalHeater = new HeatProducer("solid-heater") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(metaglass, 120, silicon, 300, copper, 400));
            size = 2;
            squareSprite = false;
            liquidCapacity = 150;
            regionRotated1 = 1;
            consumeLiquid(oxygen, .0125f);
            consume(new ConsumeItemFlammable(0.8f));
            craftTime =90;
            heatOutput = 15;
            rotateDraw = false;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawDefault(), new DrawHeatOutput() {{
                heatColor = new Color(1f, 0.22f, 0.22f, 0.4f);
            }});
        }};
        combustionHeater = new HeatProducer("combustion-heater") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(metaglass, 400, copper, 500, polymer, 200));
            size = 4;
            squareSprite = false;
            liquidCapacity = 150;
            regionRotated1 = 1;
            consume(new ConsumeLiquidFlammable(1f, 1));
            consumeLiquid(oxygen, .2f);
            heatOutput = 30;
            rotateDraw = false;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawDefault(), new DrawLiquidTile(oil, 4), new DrawSoftParticles() {{
                alpha = 0.35f;
                particleRad = 5f;
                particleSize = 9f;
                particleLife = 120f;
                particles = 27;
            }}, new DrawRegion("-top"), new DrawGlowRegion() {{
                color = new Color(1f, 0.22f, 0.22f, 0.4f);
            }}, new DrawHeatOutput() {{
                heatColor = new Color(1f, 0.22f, 0.22f, 0.4f);
            }});
        }};
        heatChannel = new HotHeatConductor("heat-channel") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(copper, 60));
            visualMaxHeat = 150;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawDefault(), new DrawHeatOutput(), new DrawHeatInput("-heat"));
            size = 2;
        }};
    }

    public static void disableVanilla() {
        Blocks.landingPad.buildVisibility = BuildVisibility.shown;
    }
}
