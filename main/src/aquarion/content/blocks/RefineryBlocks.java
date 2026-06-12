package aquarion.content.blocks;

import aquarion.content.AquaCategories;
import aquarion.content.AquaItems;
import aquarion.content.AquaSounds;
import aquarion.world.blocks.AquaBlock;
import aquarion.world.consumers.ConsumeLiquidAcidic;
import aquarion.world.drawers.*;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.NewParticleEffect;
import aquarion.world.type.AquaGenericCrafter;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.content.Planets;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.RadialEffect;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.draw.*;

import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaItems.aluminum;
import static aquarion.content.AquaItems.ferricMatter;
import static aquarion.content.AquaLiquids.*;
import static aquarion.content.AquaLiquids.muriaticAcid;
import static aquarion.content.AquaPlanets.*;
import static aquarion.world.graphics.Renderer.Layer.shadow;
import static arc.math.Interp.pow5Out;
import static mindustry.content.Items.*;
import static mindustry.content.Items.sand;
import static mindustry.content.Liquids.*;
import static mindustry.content.Liquids.water;
import static mindustry.type.ItemStack.with;

public class RefineryBlocks {
    public static Block magmaDiffuser, biotiteLeachingVessel, bauxiteCentrifuge, slagRefinementArray, azuriteKiln, towaniteReductionVat, galenaCrucible, fumeSeparator, scrapCentrifuge,
    thermalCrackingUnit, hazeCrackingUnit, vacuumFreezer, acuminiteDegredationArray, atmosphericCentrifuge, electrolysisVat, brineElectrolyzer, gasifier, ultrafamicRefinery,desulferizationAssembly;

    public static void loadContent() {
        magmaDiffuser = new AquaGenericCrafter("magma-diffuser") {{
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(AquaCategories.refinery, with(lead, 150, silicon, 80));
            size = 5;
            squareSprite = false;
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
            consumeLiq(magma, 2);
            itemBoostIntensity = 2;
            consumeBoost(salt, 1, 1f);
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
        biotiteLeachingVessel = new AquaGenericCrafter("biotite-leaching-vessel") {{
            requirements(AquaCategories.refinery, with(zinc, 400, lead, 400, silicon, 500, copper, 200));
            size = 8;
            squareSprite = false;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            liquidCapacity = 800;
            baseEfficiency = 0.5f;
            rotate = true;
            rotateDraw = false;
            hasHeat = true;
            heatRequirement = 30;
            maxEfficiency = 3;
            overheatScale = 1;
            consumePower(4);
            liquidOutputDirections = new int[]{1, 2, 3};
            craftTime = 60;
            itemCapacity = 200;
            consumeLiq(muriaticAcid, 2);
            consumeItemStack(new ItemStack(biotite, 35));
            outputItems = new ItemStack[]{
                    new ItemStack(sand, 6),
                    new ItemStack(ferricMatter, 10),
                    new ItemStack(aluminum, 5)
            };
            outputLiquids = new LiquidStack[]{
                    new LiquidStack(hydroxide, 8.5f),
                    new LiquidStack(oxygen, 5.5f),
                    new LiquidStack(rareSludge, 0.1f)
            };
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(muriaticAcid, 5), new DrawRegion("-spinny", 0.8f, true), new DrawDefault(), new AquaDrawLiquidOutputs(), new DrawHeatInputBitmask("-heats"));
        }};
        bauxiteCentrifuge = new AquaGenericCrafter("bauxite-centrifuge") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(AquaCategories.refinery, with(lead, 450, silicon, 600, copper, 300));
            craftTime = 60;
            consumeItemStack(new ItemStack(bauxite, 10));
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
        slagRefinementArray = new AquaGenericCrafter("slag-refinement-array") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(AquaCategories.refinery, with(graphite, 900, silicon, 1200, metaglass, 1000));
            size = 7;
            consumePower(6f);
            itemCapacity = 300;
            liquidCapacity = 900;
            updateEffect = Fx.ventSteam;
            updateEffectChance = 0.01f;
            squareSprite = false;
            craftTime = 600;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            consume(new LiquidStack(slag, 6), new LiquidStack(fumes, 2));
            outputItems = new ItemStack[]{
                    new ItemStack(metaglass, 40),
                    new ItemStack(sand, 5),
                    new ItemStack(zinc, 30),
                    new ItemStack(ferricMatter, 25)};
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawRegion("-bottom"), new DrawLiquidTile(fumes, 1) {{
                alpha = 0.6f;
            }}, new DrawRegion("-mid"), new DrawLiquidTile(slag) {{
                padBottom = 166 / 4f;
                padTop = 166 / 4f;
                padRight = 10;
                padLeft = 10;
            }}, new DrawWheel() {{
                width = 120 / 4f;
                height = 2;
                rotation = 0;
                sideCount = 12;
                rotationSpeed = 0.1f;
                suffix = "-tick";
                x = 0;
                y = 36 / 4f;
                wheelColors = new Color[]{
                        //I should set this as a Pallete or smth
                        Color.valueOf("8da6ab"),
                        Color.valueOf("333f4b"),
                        Color.valueOf("0f151b")
                };
            }}, new DrawWheel() {{
                width = 120 / 4f;
                height = 2;
                rotation = 0;
                sideCount = 12;
                rotationSpeed = 0.1f;
                suffix = "-tick";
                x = 0;
                y = -25 / 4f;
                wheelColors = new Color[]{
                        //I should set this as a Pallete or smth
                        Color.valueOf("8da6ab"),
                        Color.valueOf("333f4b"),
                        Color.valueOf("0f151b")
                };
            }}, new DrawPistons() {{
                sides = 2;
                sinMag = 11;
                sinScl = 12;
                lenOffset = -6f;
            }}, new DrawPistons() {{
                suffix = "-piston1";
                sides = 2;
                sinMag = 11;
                sinScl = 12;
                lenOffset = -6f;
            }}, new DrawLiquidTile(slag) {{
                padBottom = 12;
                padTop = 12;
                padRight = 12;
                padLeft = 12;
                alpha = 0.7f;
            }}, new DrawRegion("-midder"), new DrawBlurSpin("-fan", 8f) {{
                x = 5 / 4f;
                y = -4f;
            }}, new DrawBlurSpin("-fan", 8f) {{
                x = 5 / 4f;
                y = 30 / 4f;
            }}, new DrawDefault(), new DrawGlowRegion() {{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }}, new DrawGlowRegion("-glow1") {{
                glowIntensity = 0.9f;
                glowScale = 8;
                alpha = 0.6f;
                color = Color.valueOf("f5c5aa");
            }}, new DrawGlowRegion("-glow2") {{
                glowIntensity = 0.75f;
                glowScale = 6;
                alpha = 0.7f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
        azuriteKiln = new AquaGenericCrafter("azurite-kiln") {
            {
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(AquaCategories.refinery, with(silicon, 900, lead, 900, zinc, 250));
                craftTime = 5 * 60f;
                destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
                squareSprite = false;
                itemCapacity = 300;
                hasPower = true;
                consumeItemStack(new ItemStack(azurite, 15));
                outputItem = new ItemStack(copper, 45);
                consumeWrapped(new ConsumeLiquidAcidic(1, 0.5f)).set(1f, true);
                outputLiquid = new LiquidStack(hydroxide, 8.5f);
                consumePower(4.5f);
                liquidCapacity = 1500;
                ignoreLiquidFullness = true;
                size = 5;
                updateEffect = Fx.steam;
                updateEffectChance = 0.05f;
                drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                    layer = shadow;
                    drawIcon = false;
                }}, new DrawRegion("-bottom"), new DrawLiquidTile(muriaticAcid, 1.5f) {{
                    alpha = 0.6f;
                }}, new DrawDefault(), new DrawGlowRegion() {{
                    alpha = 0.65f;
                    color = Color.valueOf("e68569");
                    glowIntensity = 0.3f;
                    glowScale = 6f;
                }}, new DrawGlowRegion("-glow1") {{
                    alpha = 0.65f;
                    color = Color.valueOf("e68569");
                    glowIntensity = 0.3f;
                    glowScale = 6f;
                }}, new DrawGlowRegion("-glow2") {{
                    alpha = 0.7f;
                    color = Color.valueOf("e68569");
                    glowIntensity = 0.4f;
                    glowScale = 5f;
                }}, new DrawGlowRegion("-glow3") {{
                    alpha = 0.55f;
                    color = Color.valueOf("e68569");
                    glowIntensity = 0.5f;
                    glowScale = 9f;
                }});
            }
        };
        towaniteReductionVat = new AquaGenericCrafter("towanite-reduction-vat") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(AquaCategories.refinery, with(copper, 150, silicon, 500, aluminum, 1000));
            size = 7;
            itemCapacity = 150;
            craftTime = 3 * 60f;
            squareSprite = false;
            consumePower(5);
            consumeItemStack(new ItemStack(towanite, 15));
            consumeLiq(fumes, 0.5f);
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
        galenaCrucible = new AquaGenericCrafter("galena-crucible") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(AquaCategories.refinery, with(zinc, 500, silicon, 200, graphite, 120));
            size = 6;
            consume(new LiquidStack(muriaticAcid, 0.5f));
            itemCapacity = 150;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            consumePower(6);
            liquidCapacity = 150;
            squareSprite = false;
            craftTime = 5 * 60f;
            consumeItemStack(new ItemStack(galena, 40));
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
        fumeSeparator = new AquaGenericCrafter("fume-separator") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(AquaCategories.refinery, with(silicon, 500, copper, 500, ferricMatter, 250));
            size = 6;
            rotate = true;
            rotateDraw = false;
            itemCapacity = 36;
            consumePower(5);
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            liquidCapacity = 1500;
            consumeLiq(fumes, 2);
            outputLiquids = LiquidStack.with(oil, 3/2f, haze, 2f, muriaticAcid, 1f);
            outputItem = new ItemStack(brimstone, 4);
            liquidOutputDirections = new int[]{3, 1, 2};
            drawer = new DrawMulti(new DrawBetterRegion("-shadow") {{
                layer = shadow;
                drawIcon = false;
            }}, new DrawDefault(), new DrawRegion("-middle"), new DrawRegion("-top"), new AquaDrawLiquidOutputs(), new DrawGlowRegion("-glow") {{
                alpha = 0.4f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.3f;
                glowScale = 5f;
                layer = Layer.block + 3;
                blending = Blending.additive;
            }});
        }};
        scrapCentrifuge = new AquaGenericCrafter("scrap-centrifuge"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(AquaCategories.refinery, with(silicon, 250, nickel, 200, copper, 100));
            consumeItemStack(new ItemStack(scrap, 10));
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            itemCapacity = 100;
            squareSprite = false;
            size = 3;
            craftTime = 120;
            outputItems = ItemStack.with(sand, 8, copper, 1, nickel, 2, lead, 2);
            consumePower(3f);
        }};
        thermalCrackingUnit = new AquaGenericCrafter("thermal-cracking-unit") {{
            requirements(AquaCategories.refinery, with(copper, 250, silicon, 600, metaglass, 700));
            shownPlanets.addAll(tantros2);
            heatRequirement = 30;
            maxEfficiency = 5;
            hasHeat = true;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            craftTime = 2.5f * 60f;
            overheatScale = 0.75f;
            rotate = true;
            rotateDraw = false;
            size = 5;
            updateEffectChance = 0.06f;
            updateEffect = AquaFx.heatEngineGenerate;
            squareSprite = false;
            consumeLiq(oil, 3);
            outputLiquids = LiquidStack.with(methane, 1, petroleum, 1.5f);
            consumeBoost(haze, 2, 1f);
            liquidOutputDirections = new int[]{1, 3};
            outputItem = new ItemStack(graphite, 5);
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
        hazeCrackingUnit = new AquaGenericCrafter("haze-cracking-unit"){{
            requirements(AquaCategories.refinery, with( ferricMatter, 1500, copper, 2000, aluminum, 900));
            shownPlanets.addAll(tantros2);
            craftTime = 2f*60f;
            size = 6;
            updateEffectChance = 0.08f;
            updateEffect = Fx.steamCoolSmoke;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            squareSprite = false;
            consume(new LiquidStack(petroleum, 4), new LiquidStack(haze, 2));
            outputLiquid = new LiquidStack(ammonia, 2);
            outputItems = ItemStack.with(polymer, 4);
            liquidCapacity = 300;
            itemCapacity = 60;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawDefault(), new DrawGlowRegion());
        }};
        desulferizationAssembly = new AquaGenericCrafter("desulferization-assembly") {{
            requirements(AquaCategories.refinery, with(metaglass, 250, copper, 500, lead, 900, graphite, 450));
            shownPlanets.addAll(tantros2);
            craftTime = 4 * 60f;
            heatRequirement = 45;
            hasHeat = true;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            maxEfficiency = 2;
            craftEffect = Fx.reactorsmoke;
            updateEffectChance = 0.08f;
            updateEffect = Fx.reactorsmoke;
            consumeLiq(petroleum, 1.5f);
            consumeLiq(petroleum, 1.5f);
            outputItem = new ItemStack(brimstone, 30);
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
        vacuumFreezer = new AquaGenericCrafter("vacuum-freezer") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(AquaCategories.refinery, with(copper, 120, aluminum, 300));
            size = 6;
            squareSprite = false;
            ambientSound = Sounds.loopCircuit;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            outputItem = new ItemStack(salt, 1);
            consumePower(3);
            consumeLiq(halideWater, 6.5f);
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
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
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
        acuminiteDegredationArray = new AquaGenericCrafter("acuminite-degredation-array") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(AquaCategories.refinery, with(copper, 550, aluminum, 700, silicon, 500));
            squareSprite = false;
            size = 8;
            ignoreLiquidFullness = true;
            dumpExtraLiquid = true;
            consumeItemStack(new ItemStack(acuminite, 60));
            craftTime = 5 * 60f;
            liquidCapacity = 1000;
            itemCapacity = 180;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            ambientSound = AquaSounds.waterRumble;
            ambientSoundVolume = 0.09f;
            updateEffectChance = 0.05f;
            updateEffect = Fx.coalSmeltsmoke;
            outputLiquid = new LiquidStack(fluorine, 0.75f);
            outputItems = ItemStack.with(aluminum, 60, copper, 60);
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
            requirements(AquaCategories.refinery, with(metaglass, 150, copper, 255, ferricMatter, 150));
            size = 4;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            rotateDraw = false;
            consumePower(3f);

            rotate = true;
            regionRotated1 = 1;
            liquidCapacity = 400;
            consumeLiq(air, 4f);
            outputLiquids = LiquidStack.with(argon, 0.25f, oxygen, 2f, nitrogen, 1.75f);
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
        electrolysisVat = new AquaGenericCrafter("electrolysis-vat"){{
            requirements(Category.crafting, with(metaglass, 250, copper, 300, silicon, 150));
            consumePower(3);
            size = 2;
            squareSprite = false;
            liquidCapacity = 400;
            consumeLiq(Liquids.water, 3);
            outputLiquids = new LiquidStack[]{
                    new LiquidStack(oxygen, 1),
                    new LiquidStack(hydrogen, 2)
            };
            craftTime = 10;
            shareOutputLiquids = true;
            shareInputLiquids = true;
            liquidOutputDirections = new int[]{0, 2};
            rotate = true;
            rotateDraw = false;
            craftEffect = Fx.steamCoolSmoke;
            drawer = new DrawMulti(new DrawDefault(), new DrawLiquidTile(water, 1), new DrawRegion("-top"), new AquaDrawLiquidOutputs());
        }};
        brineElectrolyzer = new AquaGenericCrafter("brine-electrolysis-manifold") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(AquaCategories.refinery, with(aluminum, 1500, silicon, 3000, metaglass, 500, copper, 5000));
            size = 7;
            rotate = true;
            liquidCapacity = 4000;
            rotateDraw = false;
            consumePower(20);
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            craftTime = 10f * 60f;
            liquidOutputDirections = new int[]{3, 2, 1};
            consumeLiq(brine, 17);
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
        gasifier = new HeatProducer("gasifier") {{
            size = 6;
            squareSprite = false;
            liquidCapacity = 500;
            itemCapacity = 100;
            craftTime = 120;
            consumePower(1.25f);
            heatOutput = 60;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(AquaCategories.refinery, with(ferricMatter, 250, silicon, 700, metaglass, 500));
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
            requirements(AquaCategories.refinery, with(metaglass, 500, silicon, 200, lead, 1500));
            size = 7;
            squareSprite = false;
            itemCapacity = 200;
            liquidCapacity = 500;
            craftTime = 2.5f * 60f;
            hasHeat = false;
            consumePower(10);
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            consumeLiq(air, 12);
            consumeItemStack(new ItemStack(serpentine, 20));
            outputLiquids = LiquidStack.with(hydroxide, 4.25);
            outputItems = new ItemStack[]{
                    new ItemStack(sand, 10),
                    new ItemStack(aluminum, 7),
                    new ItemStack(ferricMatter, 7),
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
    }
}
