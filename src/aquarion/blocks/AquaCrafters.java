package aquarion.blocks;

import aquarion.AquaAttributes;
import aquarion.AquaItems;
import aquarion.AquaSounds;
import aquarion.world.blocks.heatBlocks.AquaHeatCrafter;
import aquarion.world.blocks.production.*;
import aquarion.world.graphics.*;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.content.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.RadialEffect;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConductor;
import mindustry.world.blocks.heat.HeatProducer;
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

public class AquaCrafters {
    public static Block heatChannel, convectionHeater, combustionHeater, thermalCrackingUnit, steamCrackingUnit, ultrafamicRefinery, gasifier, algalTerrace, atmosphericCentrifuge, steelFoundry, pinDrill, inlet, inletArray, acuminiteDegredationArray, vacuumFreezer,atmosphericIntake, AnnealingOven, SolidBoiler, CentrifugalPump, harvester,galenaCrucible ,DrillDerrick, beamBore, fumeMixer, chireniumElectroplater, saltDegradationMatrix, plasmaExtractor, towaniteReductionVat, azuriteKiln, slagRefinementAssemblage, fumeFilter, FractionalDistillery, ferroSiliconFoundry, bauxiteCentrifuge, magmaTap, fumeSeparator, magmaDiffser;

    public static void loadContent() {
        disableVanilla();
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
            researchCostMultiplier = 0.02f;
            updateEffect = Fx.steam;
            attribute = Attribute.heat;
            minEfficiency = 1;
            maxBoost = 3;
            consumePower(50/60f);
            baseEfficiency = 0;
            updateEffectChance = 0.02f;
            boostScale = 1f / 8f;
            craftTime = 5*60f;
            ambientSound = Sounds.smelter;
            ambientSoundVolume = 0.05f;
            squareSprite = false;
            liquidCapacity = 900;
            outputLiquid = new LiquidStack(magma, 1.5f);
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
            consumePower(200/60f);
            outputItems = new ItemStack[]{
                    new ItemStack(sand, 6),
                    new ItemStack(ferricMatter, 6),
                    new ItemStack(aluminum, 6)
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
        ferroSiliconFoundry = new AquaHeatCrafter("ferrosilicon-foundry") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with( silicon, 900, metaglass, 500, copper, 500, ferricMatter, 500));
            size = 6;
            squareSprite = false;
            itemCapacity = 120;
            heatRequirement = 45;
            maxEfficiency = 5;
            overheatScale = 0.25f;
            researchCostMultiplier = 0.02f;
            craftTime = 10*60f;
            consumePower(400/60f);
            consumeItems(with(ferricMatter, 15, silicon, 35));
            outputItem = new ItemStack(ferrosilicon, 40);
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
        fumeFilter = new AttributeCrafter("fume-filter"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.production, with(aluminum, 250, silicon, 500));
            size = 6;
            squareSprite = false;
            outputLiquid = new LiquidStack(fumes, 120/60f);
            attribute = Attribute.steam;
            boostScale = 1/18f;
            minEfficiency = 0.1f;
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
            requirements(Category.crafting, with( bauxite, 750, lead, 750));
            craftTime = 5*60f;
            squareSprite = false;
            consumePower(500/60f);
            consumeLiquid(magma, 45/60f);
            consumeItem(azurite, 15);
            itemCapacity = 300;
            outputItem = new ItemStack(copper, 45);
            outputLiquid = new LiquidStack(hydroxide, 25/60f);
            liquidCapacity = 250;
            ignoreLiquidFullness = true;
            dumpExtraLiquid = true;
            size = 5;
            researchCostMultiplier = 0.02f;
            updateEffect = Fx.steam;
            updateEffectChance = 0.05f;
            lightRadius = 0;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(magma, 1.5f){{ alpha = 0.6f;}}, new DrawDefault(), new DrawGlowRegion() {{
                alpha = 0.65f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.3f;
                glowScale = 6f;
            }});
            towaniteReductionVat = new GenericCrafter("towanite-reduction-vat"){{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.crafting, with( copper, 150, silicon ,500));
                size = 7;
                itemCapacity = 150;
                craftTime = 3*60f;
                squareSprite = false;
                consumePower(500/60f);
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
                requirements(Category.production, with(silicon, 150, lead ,250));
                tier = 1;
                itemCapacity = 50;
                squareSprite = false;
                consumePower(100/60f);
                researchCostMultiplier = 0.05f;
                drillTime = 240;
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
                requirements(Category.production, with( aluminum, 250, copper, 75));
                tier = 1;
                consumePower(25/60f);
                itemCapacity = 50;
                squareSprite = false;
                researchCostMultiplier = 0.25f;
                drillTime = 120;
                size = 3;
                range = 6;
                consumeLiquid(fumes, 15/60f).boost();
                optionalBoostIntensity = 1f;
                heatColor  = Color.valueOf("9d8cf2");
                boostHeatColor = Color.valueOf("e1f28c");
                researchCostMultiplier = 0;
            }};
            fumeMixer = new GenericCrafter("fume-mixer"){{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.crafting, with(metaglass, 250, copper, 200));
                size = 6;
                craftTime = 5*60f;
                squareSprite = false;
                baseExplosiveness = 10;
                researchCostMultiplier = 0.25f;
                consumeLiquids(LiquidStack.with(haze, 250 / 60f, oxygen, 125/60f));
                outputLiquid = new LiquidStack(fumes, 240/60f);
                consumeItems(ItemStack.with(coal, 35, brimstone, 16));
                itemCapacity = 350;
                liquidCapacity = 900;
                drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawRegion("-rotator"){{
                    y = 35/4f;
                    x = 20/4f;
                    spinSprite = true;
                    rotateSpeed = 2;
                }}, new DrawRegion("-rotator"){{
                    y = 35/4f;
                    x = -20/4f;
                    spinSprite = true;
                    rotateSpeed = 2;
                }}, new DrawLiquidTile(fumes){{
                    alpha = 0.8f;
                    padding = 2;
                }}, new DrawDefault()
                        , new PhaseOffsetGlowRegion("-glow"){{
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
            DrillDerrick = new GroundDrill("drill-derrick"){{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.production, with(  aluminum, 150, copper, 200));
                size = 5;
                drillTime = 125;
                consumePower(150/60f);
                consumeLiquid(fumes, 20/60f).booster = false;
                consumeLiquid(chlorine, 15/60f).boost();
                liquidBoostIntensity = 1.5f;
                squareSprite = false;
                itemCapacity = 200;
                liquidCapacity = 120;
                tier = 4;
                researchCostMultiplier = 0;
            }};
            galenaCrucible = new GenericCrafter("galena-crucible"){{
                shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                requirements(Category.crafting, with( manganese, 500, aluminum, 250, strontium, 250));
                size = 6;
                consumeLiquids(LiquidStack.with(fluorine, 20/60f));
                itemCapacity = 150;
                liquidCapacity = 150;
                squareSprite = false;
                craftTime = 5*60f;
                consumeItem(galena, 40);
                outputItems = ItemStack.with(lead, 40, brimstone, 25, copper, 15);
                drawer= new DrawMulti(new DrawDefault(), new DrawGlowRegion("-glow"){{
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
                }});
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
            requirements(Category.production, with( nickel, 70, lead, 50, silicon, 90));
            size = 3;
            drillTime= 150;
            liquidBoostIntensity = 1.25f;
            consumePower(5/60f);
            consumeLiquid(Liquids.water, 75/60f).boost();
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
            requirements(Category.liquid, with( copper, 120, metaglass, 50));
            size = 3;
            consumePower(25/60f);
            squareSprite = true;
            pumpAmount = 0.3f;
            liquidCapacity = 300;
            drawer = new DrawMulti(new DrawRegion("-bottom"),new DrawLiquidTile(water, 1.5f){{ alpha = 0.6f;}}
                    ,new DrawLiquidTile(oil, 1.5f){{ alpha = 0.6f;}},new DrawLiquidTile(water, 1.5f){{ alpha = 0.6f;}}, new DrawDefault(), new DrawGlowRegion("-glow"){{
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
            requirements(Category.crafting, with( lead, 700, copper, 900, metaglass, 400));
            size = 7;
            itemCapacity = 60;
            squareSprite = false;
            researchCostMultiplier = 0.05f;
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
        AnnealingOven = new AquaHeatCrafter("metaglass-annealing-furnace"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with( lead, 300, copper, 550));
            size = 5;
            consumePower(200/60f);
            itemCapacity = 60;
            squareSprite = false;
            researchCostMultiplier = 0.02f;
            baseEfficiency = 1;
            heatRequirement = 15;
            maxEfficiency = 4;
            overheatScale = 0.5f;
            craftTime = 120;
            consumeItems(new ItemStack(lead, 10), new ItemStack(sand, 10));
            outputItem = new ItemStack(metaglass, 15);
            updateEffect = Fx.coalSmeltsmoke;
            updateEffectChance = 0.07f;
            ambientSound = AquaSounds.refine;
            ambientSoundVolume = 0.07f;
            consumeLiquid(air, 150/60f);
            consumePower(300/60f);
            //heatRequirement = 10;
            //overheatScale = 0.8f;
            //maxEfficiency = 5;
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
        thermalCrackingUnit = new AquaHeatCrafter("thermal-cracking-unit"){{
            requirements(Category.crafting, with( copper, 250, silicon, 600, metaglass, 900));
            shownPlanets.addAll(tantros2);
            heatRequirement = 30;
            maxEfficiency = 5;
            craftTime = 2.5f*60f;
            overheatScale = 0.75f;
            rotate = true;
            rotateDraw = false;
            size = 5;
            updateEffectChance = 0.06f;
            updateEffect = AquaFx.heatEngineGenerate;
            squareSprite = false;
            consumeLiquid(oil, 150/60f);
            outputLiquids = LiquidStack.with(ethylene, 1, petroleum, 90/60f);
            liquidOutputDirections = new int[]{1, 3};
            outputItem = new ItemStack(coal, 5);
            liquidCapacity = 300;
            itemCapacity = 60;
            drawer = new DrawMulti(new DrawDefault(), new DrawGlowRegion());
        }};
        steamCrackingUnit = new GenericCrafter("haze-cracking-unit"){{
            requirements(Category.crafting, with( steel, 500, ferrosilicon, 250, metaglass, 1000));
            shownPlanets.addAll(tantros2);
            craftTime = 1.5f*60f;
            size = 5;
            updateEffectChance = 0.08f;
            updateEffect = Fx.steamCoolSmoke;
            squareSprite = false;
            consumeLiquids(LiquidStack.with(petroleum, 225 / 60f, haze, 125));
            outputLiquid = new LiquidStack(ethylene, 2);
            outputItems = ItemStack.with(brimstone, 3);
            liquidCapacity = 300;
            itemCapacity = 60;
            drawer = new DrawMulti(new DrawDefault(), new DrawGlowRegion());
        }};
        inlet = new GenericCrafter("inlet"){{
            requirements(Category.production, with( copper, 45));
            shownPlanets.addAll(tantros2);
            craftTime = 10;
            outputLiquid = new LiquidStack(halideWater, 20/60f);
            envDisabled = Env.groundOil | Env.scorching | Env.spores;
            liquidCapacity = 90;
            squareSprite = false;
            ambientSound = AquaSounds.waterAir;
            ambientSoundVolume = 0.01f;
            drawer = new DrawMulti(new DrawDefault(),new DrawParticles(){{
                color = Color.valueOf("455a5e");
                alpha = 0.2f;
                particleSize = 2.5f;
                particles = 20;
                particleRad = 2.5f;
                particleLife = 90f;
            }});
        }};
        inletArray = new GenericCrafter("inlet-array"){{
            requirements(Category.production, with( copper, 150, ferricMatter, 100, metaglass, 75));
            shownPlanets.addAll(tantros2);
            craftTime = 10;
            size = 3;
            consumePower(100/60f);
            outputLiquid = new LiquidStack(halideWater, 240/60f);
            envDisabled = Env.groundOil | Env.scorching | Env.spores;
            liquidCapacity = 700;
            squareSprite = false;
            ambientSound = AquaSounds.waterAir;
            ambientSoundVolume = 0.01f;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(halideWater){{
                padRight = 11/4f;
                padLeft = 11/4f;
                padBottom = 22/4f;
                padTop = 19/4f;
            }}, new DrawRegion("-top"),new DrawParticles(){{
                color = Color.valueOf("455a5e");
                alpha = 0.25f;
                particleSize = 3f;
                particles = 20;
                particleRad = 3.5f;
                particleLife = 90f;
                x = 0; y = 16/4f;
            }},new DrawParticles(){{
                color = Color.valueOf("455a5e");
                alpha = 0.25f;
                particleSize = 3f;
                particles = 20;
                particleRad = 3.5f;
                particleLife = 90f;
                x = 25/4f; y = -13/4f;
            }},new DrawParticles(){{
                color = Color.valueOf("455a5e");
                alpha = 0.25f;
                particleSize = 3f;
                particles = 20;
                particleRad = 3.5f;
                particleLife = 90f;
                x = -25/4f; y = -13/4f;
            }});
        }};
        vacuumFreezer = new GenericCrafter("vacuum-freezer"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with( copper, 200, aluminum, 500));
            size = 6;
            researchCostMultiplier = 0.08f;
            squareSprite = false;
            ambientSound = Sounds.electricHum;
            consumePower(350/60f);
            consumeLiquid(halideWater, 300/60f);
            craftTime = 10;
            outputLiquids = LiquidStack.with( air, 200/60f,water, 200/60f);
            liquidOutputDirections = new int[]{1, 4};
            rotate = true;
            rotateDraw = false;
            liquidCapacity = 600;
            regionRotated1 = 3;
            //heatRequirement = -10;
            //overheatScale = 0.8f;
            //maxEfficiency = 10;
            //flipHeatScale = true;
            craftEffect = new MultiEffect(new NewParticleEffect(){{
                y = -6/4f;
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
            }}, new NewParticleEffect(){{
                y = -6/4f;
                x = -49/4f;
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
            }},new NewParticleEffect(){{
                y = -6/4f;
                x = 44/4f;
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
            drawer = new DrawMulti(new DrawDefault(){
            }, new DrawPump("-pump"){{
                y = 45/4f;
                x = -38/4f;
            }}, new DrawPump("-pump"){{
                y = 45/4f;
                x = -63/4f;
                timeOffset = 10;
            }}, new DrawLiquidTile(air){{
                padTop = 37/4f;
                padRight = 121/4f;
                padBottom = 132/4f;
                padLeft = 49/4f;
            }}, new DrawLiquidTile(water){{
                padTop = 37/4f;
                padRight = 148/4f;
                padBottom = 132/4f;
                padLeft = 22/4f;
            }}, new DrawRegion("-top"), new PhaseOffsetGlowRegion("-glow"){{
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
            }}, new DrawLiquidOutputs());
        }};
        atmosphericIntake = new GenericCrafter("air-intake"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.production, with( nickel, 50, silicon, 150));
            size = 2;
            researchCostMultiplier = 0.02f;
            squareSprite = false;
            craftTime = 10;
            liquidCapacity = 200;
            consumePower(10/60f);
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
        acuminiteDegredationArray = new GenericCrafter("acuminite-degredation-array"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with( manganese, 550, aluminum, 700, strontium, 500));
            squareSprite = false;
            size = 8;
            researchCostMultiplier = 0;
            ignoreLiquidFullness = true;
            dumpExtraLiquid = true;
            consumeItem(acuminite, 60);
            craftTime = 5*60f;
            liquidCapacity = 1000;
            itemCapacity = 180;
            ambientSound = AquaSounds.waterRumble;
            ambientSoundVolume = 0.09f;
            updateEffectChance = 0.05f;
            updateEffect = Fx.coalSmeltsmoke;
            outputLiquid = new LiquidStack(fluorine, 40/60f);
            outputItems = ItemStack.with(aluminum, 60, strontium, 60);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawCellsNew(){{
                range = 70/4f;
                radius = 2.5f;
                particles = 400;
                lifetime = 90f * 5f;
                particleColorFrom = Color.valueOf("9aa86d");
                particleColorTo = Color.valueOf("9d6b44");
                color = Color.valueOf("715041");
            }}, new DrawBubblesNew(Color.valueOf("edcc97").a(0.5f)){{
                spread = 70/4f;
                amount = 70;
                timeScl = 20;
                circle = true;
            }}, new DrawRegion("-panel"),
                    new DrawBlurSpin("-fan", 0.6f * 9f) {{
                        blurThresh = 0.6f;
                        x = -96/4f;
                        y = -10/4f;
                    }},
                    new DrawBlurSpin("-fan", 0.6f * 9f) {{
                        blurThresh = 0.6f;
                        x = -96/4f;
                        y = 40/4f;
                    }},
                    new DrawBlurSpin("-fan", 0.6f * 9f) {{
                        blurThresh = 0.6f;
                        x = -96/4f;
                        y = 90/4f;
                    }}, new DrawPistons(){{
                suffix = "-pistone";
                sides  = 1;
                angleOffset = 90;
                sinMag = 3.5f;
                lenOffset = 1;
            }}, new DrawWheel(){{
                width = 42/4f;
                height = 42/4f;
                rotation = -45;
                rotationSpeed = 1.2f;
                suffix = "-tick";
                x = 45/4f;
                y = -50/4f;
                wheelColors = new Color[]{
                        //I should set this as a Pallete or smth
                        Color.valueOf("8da6ab"),
                        Color.valueOf("333f4b"),
                        Color.valueOf("0f151b")
                };
            }}, new DrawLiquidTile(fluorine){{
                padBottom = 61/4f;
                padRight = 70/4f;
                padTop = 160/4f;
                padLeft = 150/4f;
            }}, new DrawAdvancedPistons(){{
                angleOffset = 270;
                sides = 1;
                sinMag = 2.5f;
            }}, new DrawCircles(){{
                y = 35/4f;
                x = -2f;
                color = Color.valueOf("edcc97");
                radius = 18;
                amount = 2;
                timeScl = 220;
            }}, new DrawRegion("-rotator"){{
                spinSprite = true;
                rotateSpeed = 0.5f;
                y = 35/4f;
                x = -2f;
            }}, new DrawDefault(), new DrawRegion("-shadow"), new DrawGlowRegion(){{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }}, new DrawGlowRegion("-glow2"){{
                glowIntensity = 0.8f;
                glowScale = 12;
                alpha = 0.6f;
                color = Color.valueOf("ef6a60");
            }}, new DrawGlowRegion("-glow3"){{
                glowIntensity = 0.5f;
                glowScale = 10;
                alpha = 0.6f;
                color = Color.valueOf("ef6a60");
            }});
        }};
        atmosphericCentrifuge = new GenericCrafter("atmospheric-centrifuge"){{
            liquidOutputDirections = new int[]{1, 2, 3};
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(ferricMatter, 150, copper, 100));
            size = 4;
            rotateDraw = false;
            consumePower(250/60f);
            researchCostMultiplier = 0.05f;
            rotate = true;
            regionRotated1 = 1;
            liquidCapacity = 400;
            consumeLiquid(air, 200/60f);
            outputLiquids = LiquidStack.with(argon, 15/60f, oxygen, 85/60f, nitrogen, 100/60f);
            squareSprite = false;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawOrbitRegions("-centrifuge", 4, 7f, 3.5f), new DrawLiquidTile(air){{
                alpha = 0.6f;
                padding = 2;
            }}, new DrawLiquidTile(oxygen){{
                padLeft = 7/4f;
                padBottom = 8/4f;
                padTop = 45/4f;
                padRight = 99/4f;
            }}, new DrawLiquidTile(nitrogen){{
                padLeft = 41/4f;
                padBottom = 30/4f;
                padTop = 16/4f;
                padRight = 18/4f;
            }}, new DrawDefault(), new DrawLiquidTile(argon){{
                padLeft = 32/4f;
                padBottom = 37/4f;
                padTop = 68/4f;
                padRight = 87/4f;
            }}, new DrawRegion("-top"), new DrawPump("-pump"){{
                y = 11/4f;
                x = 1;
                timeOffset = 0;
            }}, new DrawPump("-pump"){{
                y = -23/4f;
                x = 1;
                timeOffset = 10;
            }}, new DrawPump("-pump"){{
                y = -7/4f;
                x = 1;
                timeOffset = 20;
            }}, new PhaseOffsetGlowRegion("-glow"){{
                alpha = 0.35f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.3f;
                glowScale = 5f;
                layer = Layer.block +3;
                blending = Blending.additive;
                phaseOffset = 10;
            }}, new PhaseOffsetGlowRegion("-glow1"){{
                alpha = 0.40f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 7f;
                layer = Layer.block +3;
                blending = Blending.additive;
                phaseOffset = 20;
            }}, new DrawLiquidOutputs());
        }};
        steelFoundry = new GenericCrafter("blast-furnace"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with( ferrosilicon, 200, nickel, 150, metaglass, 100));
            size = 7;
            rotateDraw = false;
            rotate = true;
            liquidCapacity = 2000;
            itemCapacity = 200;
            craftTime = 5*60f;
            consumeItems(ItemStack.with(ferricMatter, 50, coal, 80));
            outputItems = ItemStack.with(steel, 50, graphite, 40);
            consumeLiquid(oxygen, 125/60f);
            outputLiquid = new LiquidStack(slag, 240/60f);
            squareSprite = false;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(slag){{
                alpha = 0.7f;
                padBottom = 2;
                padLeft = 22;
                padTop = 2;
                padRight = 2;
            }}, new DrawDefault(), new PhaseOffsetGlowRegion("-glow"){{
                alpha = 0.3f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 6f;
                layer = Layer.block +3;
                blending = Blending.additive;
                phaseOffset = 10;
            }},new PhaseOffsetGlowRegion("-glow1"){{
                alpha = 0.40f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 7f;
                layer = Layer.block +3;
                blending = Blending.additive;
                phaseOffset = 20;
            }},new PhaseOffsetGlowRegion("-glow2"){{
                alpha = 0.50f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.6f;
                glowScale = 7f;
                layer = Layer.block +3;
                blending = Blending.additive;
                phaseOffset = 30;
            }});
        }};
        pinDrill = new GroundDrill("pin-drill"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.production, with( manganese, 40, aluminum, 50, strontium, 20));
            drillTime = 100;
            size = 1;
            squareSprite = false;
            tier = 3;
            researchCostMultiplier = 0;
            drawer = new DrawMulti(new DrawDefault(), new DrawPump("-bit"));
        }};
        algalTerrace = new AttributeCrafter("algal-terrace"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.production, with( strontium, 500, lead, 700, metaglass, 500));
            size = 7;
            baseEfficiency = 0;
            squareSprite = false;
            attribute = AquaAttributes.fertility;
            boostScale = 1/(49/2f);
            maxBoost = 2;
            liquidCapacity = 1200;
            itemCapacity = 20;
            minEfficiency = 0.25f;
            outputLiquid = new LiquidStack(bioPulp, 150/60f);
            consumeLiquids(LiquidStack.with(water, 150/60f, brine, 50/60f));
            craftTime = 90;
            consumeItem(brimstone, 1);
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(water){{
                padding = 3;
            }}, new DrawCells(){{
                range = 8*7/2f-4;
                radius = 3;
                particleColorTo = Color.valueOf("9c592d");
                particleColorFrom = Color.valueOf("c5a74b");
                color = Color.valueOf("7b4624");
                particles = 400;
            }}, new DrawLiquidTile(bioPulp){{
                padding = 3;
            }}, new DrawDefault());
        }};
        gasifier = new GenericCrafter("gasifier"){{
            size = 6;
            squareSprite = false;
            liquidCapacity = 500;
            itemCapacity = 100;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with( aluminum, 500, strontium, 700, metaglass, 500));
            consumeLiquid(bioPulp, 75/60f);
            outputItem = new ItemStack(coal, 5);
            outputLiquid = new LiquidStack(haze, 25/60f);
            drawer = new DrawMulti(new DrawDefault(), new DrawGlowRegion(){{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.5f;
                color = Color.valueOf("dd5858");
            }},new DrawGlowRegion("-glow1"){{
                glowIntensity = 0.9f;
                glowScale = 8;
                alpha = 0.6f;
                color = Color.valueOf("f5c5aa");
            }},new DrawGlowRegion("-glow1"){{
                glowIntensity = 0.7f;
                glowScale = 6;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
        ultrafamicRefinery = new AquaHeatCrafter("ultrafamic-refinery"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(  silicon, 500, copper, 700, metaglass, 500));
            size = 5;
            squareSprite = false;
            itemCapacity = 200;
            liquidCapacity = 500;
            craftTime = 5*60f;
            heatRequirement = 5;
            overheatScale = 0.75f;
            maxEfficiency = 8;
            consumePower(200/60f);
            consumeLiquid(air, 200/60f);
            consumeItem(serpentine, 20);
            outputLiquids = LiquidStack.with(hydroxide, 20f / 60f);
            outputItems = new ItemStack[]{
                    new ItemStack(sand, 4),
                    new ItemStack(ferricMatter, 8),
                    new ItemStack(nickel, 6),
                    new ItemStack(magnesiumPowder, 10)
            };
            drawer = new DrawMulti(new DrawDefault(), new DrawFramesNew(){{
                frames = 4;
                interval = 5;
            }}, new DrawFramesNew(){{
                frames = 4;
                interval = 5;
                suffix = "-ore";

            }},new DrawPile(){{
                moveX = -54/4f;
                moveY = -54/4f;
                y = 26/4f;
                x = 20/4f;
            }}, new DrawRegion("-convey"));
        }};
        convectionHeater = new HeatProducer("convection-heater"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(  silicon, 150, copper, 350, metaglass, 200));
            size = 3;
            squareSprite = false;
            consumePower(300/60f);
            regionRotated1 = 1;
            consumeLiquid(water, 10/60f);
            rotateDraw = false;
            heatOutput = 15;
            drawer = new DrawMulti(new DrawDefault(), new DrawGlowRegion(){{
                color = new Color(1f, 0.22f, 0.22f, 0.4f);
            }}, new DrawHeatOutput(){{
                heatColor = new Color(1f, 0.22f, 0.22f, 0.4f);
            }});
        }};
        combustionHeater = new HeatProducer("combustion-heater"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(  metaglass, 400, copper, 500, ferrosilicon, 200));
            size = 4;
            squareSprite = false;
            regionRotated1 = 1;
            consume(new ConsumeLiquidFlammable(1f, 1));
            heatOutput = 25;
            rotateDraw = false;
            drawer = new DrawMulti(new DrawDefault(), new DrawLiquidTile(oil, 4),new DrawSoftParticles(){{
                alpha = 0.35f;
                particleRad = 5f;
                particleSize = 9f;
                particleLife = 120f;
                particles = 27;
            }}, new DrawRegion("-top"), new DrawGlowRegion(){{
                color = new Color(1f, 0.22f, 0.22f, 0.4f);
            }}, new DrawHeatOutput(){{
                heatColor = new Color(1f, 0.22f, 0.22f, 0.4f);
            }});
        }};
        heatChannel = new HeatConductor("heat-channel"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with(  copper, 30));
            visualMaxHeat = 150;
            drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput(), new DrawHeatInput("-heat"));
            size = 2;
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
        Blocks.smallHeatRedirector.buildVisibility = BuildVisibility.sandboxOnly;
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
        Blocks.largeCliffCrusher.buildVisibility = BuildVisibility.sandboxOnly;
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
        Blocks.memoryCell.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.memoryBank.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.canvas.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.reinforcedMessage.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.worldProcessor.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.worldCell.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.worldMessage.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.worldSwitch.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.launchPad.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.advancedLaunchPad.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.landingPad.buildVisibility = BuildVisibility.sandboxOnly;
        Blocks.interplanetaryAccelerator.buildVisibility = BuildVisibility.sandboxOnly;
    }
}
