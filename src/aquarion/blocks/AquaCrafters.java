package aquarion.blocks;

import aquarion.AquaAttributes;
import aquarion.AquaSounds;
import aquarion.world.blocks.production.*;
import aquarion.world.graphics.*;

import aquarion.world.graphics.drawers.*;
import aquarion.world.graphics.drawers.DrawBetterArcSmelt;
import aquarion.world.type.AquaGenericCrafter;
import aquarion.world.type.GroundDrill;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.content.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.RadialEffect;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatConductor;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static aquarion.AquaAttributes.iron;
import static aquarion.AquaAttributes.metamorphic;
import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.*;
import static aquarion.planets.AquaPlanets.*;
import static aquarion.world.graphics.Renderer.Layer.heat;
import static aquarion.world.graphics.Renderer.Layer.shadow;
import static arc.math.Interp.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.type.ItemStack.with;

public class AquaCrafters {
    public static Block fluxExcavator, graphiteConcentrator, cupronickelAlloyer, brineMixer, brineElectrolyzer, ferricGrinder, SilicaOxidator, arcFurnace, desulferizationAssembly, heatChannel, convectionHeater, combustionHeater, thermalCrackingUnit, steamCrackingUnit, ultrafamicRefinery, gasifier, algalTerrace, atmosphericCentrifuge, steelFoundry, pinDrill, inlet, inletArray, acuminiteDegredationArray, vacuumFreezer,atmosphericIntake, AnnealingOven, SolidBoiler, CentrifugalPump, pumpAssembly, harvester,galenaCrucible ,DrillDerrick, beamBore, fumeMixer, chireniumElectroplater, saltDegradationMatrix, plasmaExtractor, towaniteReductionVat, azuriteKiln, slagRefinementAssemblage, fumeFilter, FractionalDistillery, ferroSiliconFoundry, bauxiteCentrifuge, magmaTap, fumeSeparator, magmaDiffser;

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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawRegion("-ring"), new DrawAdvancedPistons() {{
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawLiquidTile(magma, 5), new DrawDefault(), new DrawGlowRegion() {{
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawOrbitRegions("-capsule", 8, 11f, 2f), new DrawRegion("-lower-toob"), new DrawLiquidTile(oxygen){{
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
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with( silicon, 900, metaglass, 500, copper, 500, ferricMatter, 500));
            size = 6;
            squareSprite = false;
            itemCapacity = 120;
            heatRequirement = 30;
            maxEfficiency = 5;
            overheatScale = 0.25f;
            researchCostMultiplier = 0.02f;
            craftTime = 10*60f;
            consumePower(400/60f);
            consumeItems(with(ferricMatter, 15, silicon, 35));
            outputItem = new ItemStack(ferrosilicon, 40);
            consumeLiquid(water, 200/60f);
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawLiquidTile(magma){{
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawLiquidTile(fumes, 2){{
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawLiquidTile(fumes, 1), new DrawLiquidTile(slag){{
                padBottom = 13;
                padTop = 13;
                padRight = 9;
                padLeft = 9;

            }}, new DrawDefault(), new DrawGlowRegion());
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
            outputLiquid = new LiquidStack(hydroxide, 500/60f);
            liquidCapacity = 1500;
            ignoreLiquidFullness = true;
            dumpExtraLiquid = true;
            size = 5;
            researchCostMultiplier = 0.02f;
            updateEffect = Fx.steam;
            updateEffectChance = 0.05f;
            lightRadius = 0;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawLiquidTile(magma, 1.5f){{ alpha = 0.6f;}}, new DrawDefault(), new DrawGlowRegion() {{
                alpha = 0.65f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.3f;
                glowScale = 6f;
            }});
            graphiteConcentrator = new AttributeCrafter("graphite-concentrator"){{
                requirements(Category.production, with( copper, 40, silicon, 80));
                attribute = metamorphic;
                craftTime = 120;
                outputItem = new ItemStack(graphite, 2);
                consumeLiquid(water, 20);
                liquidCapacity = 80;
                itemCapacity = 18;
                size = 2;
                squareSprite = false;
                consumePower(20/60f);
                drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}}, new BetterDrawParticles(){{
                    color = Color.valueOf("50706d");
                    alpha = 0.1f;
                    particleSize =  1.5f;
                    particles = 12;
                    particleRad = 3f;
                    particleLife = 160f;
                    rotateScl = 2f;
                }}, new DrawDefault());
            }};
            ferricGrinder = new AttributeCrafter("ferric-macerator"){{
                requirements(Category.production, with( nickel, 250, copper, 100, silicon, 500, metaglass, 200));
                size = 6;
                itemCapacity = 100;
                liquidCapacity = 200;
                craftTime = 240;
                attribute = iron;
                baseEfficiency = 0;
                minEfficiency = 1;
                displayEfficiency = true;
                maxBoost = 2.5f;
                boostScale = 1/36f;
                consumePower(400/60f);
                consumeLiquid(water, 300/60f);
                outputItems = new ItemStack[]{
                        new ItemStack(sand, 20),
                        new ItemStack(ferricMatter, 8),
                };
                drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawParticles(){{
                    color = Color.valueOf("1c4538");
                    alpha = 0.5f;
                    particleSize =  4f;
                    particles = 45;
                    particleRad = 8*3f;
                    particleLife = 20;
                    rotateScl = 1.5f;
                }}, new DrawOrbitRegions(){{
                    orbitSpeed = 3f;
                    radius = 11;
                    suffix = "-gear";
                    rotateSpeed = 2.5f;
                    spinSprite = true;
                    countRotOffset = 22.5f;
                    regionCount = 7;
                }}, new DrawParticles(){{
                    color = Color.valueOf("356e6c");
                    alpha = 0.5f;
                    particleSize =  2.5f;
                    particles = 20;
                    particleRad = 8*2.5f;
                    particleLife = 15;
                    rotateScl = 1.6f;
                }}, new DrawRegion("-ring"){{
                    spinSprite = true;
                    rotateSpeed = 2;
                }}, new DrawParticles(){{
                    color = Color.valueOf("747ae1");
                    alpha = 0.2f;
                    particleSize = 1.5f;
                    particles = 15;
                    particleRad = 8*2.5f;
                    particleLife = 10;
                    rotateScl = 1.7f;
                }}, new DrawRegion("-bit"){{
                    spinSprite = true;
                    rotateSpeed = -4;
                }}, new DrawDefault());
            }};
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

                drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawPistons(){{
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
                drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawRegion("-rotator"){{
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
                consumePower(200/60f);
                consumeLiquid(fumes, 20/60f).booster = false;
                consumeLiquid(chlorine, 15/60f).boost();
                liquidBoostIntensity = 1.5f;
                squareSprite = false;
                itemCapacity = 200;
                liquidCapacity = 120;
                tier = 4;
                researchCostMultiplier = 0;
                drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;}}, new DrawDefault(), new DrawRegion("-rotator"){{
                    rotateSpeed = 1.5f;
                    spinSprite = true;
                }}, new DrawRegion("-top"));
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
                drawer= new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawDefault(), new DrawGlowRegion("-glow"){{
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
            consumePower(10/60f);
            consumeLiquid(Liquids.water, 60/60f).boost();
            consumeItems(new ItemStack(cupronickel, 2)).boost();
            itemCapacity = 25;
            separateItemCapacity = true;
            ItemBoostUseTime = 6*60;
            itemBoostIntensity = 1.2f;
            tier = 2;
            squareSprite = false;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawPistons(){{
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
        fluxExcavator = new GroundDrill("flux-excavator"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.production, with( nickel, 70, lead, 50, silicon, 90));
            size = 9;
            drillTime = 5500;
            heatRequirement = 50;
            overheatScale = 0.75f;
            maxEfficiency = 10;
            consumeLiquid(oil, (float) 2000 /60).boost();

            consumeItem(steel, 10).boost();
            consumePower(2500/60f);
            itemBoostIntensity = 1.54f;
            liquidBoostIntensity = 1.54f;
            itemCapacity = 2500;
            liquidCapacity = 8000;
            ItemBoostUseTime = 240;
            tier = 5;
            drawer = new DrawDefault();
        }};
        CentrifugalPump = new Pump("centrifugal-pump"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.liquid, with( copper, 120, metaglass, 50));
            size = 3;
            consumePower(25/60f);
            squareSprite = true;
            researchCostMultiplier = 0.02f;
            pumpAmount = 0.3f;
            liquidCapacity = 300;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"),new DrawLiquidTile(water, 1.5f){{ alpha = 0.6f;}}
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
        pumpAssembly = new Pump("pump-assembly"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.liquid, with( cupronickel, 500, ferrosilicon, 250, metaglass, 250));
            size = 6;
            squareSprite = false;
            consumePower(350/60f);
            squareSprite = true;
            researchCostMultiplier = 0.02f;
            pumpAmount = 0.7f;
            liquidCapacity = 2000;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"),new DrawLiquidTile(water, 1.5f){{ alpha = 0.6f;}}
                    ,new DrawBlurSpin(){{
                        suffix = "-fan";
                        x = -31/4f;
                        y = -14/4f;
                        rotateSpeed = 4f;
            }},new DrawBlurSpin(){{
                suffix = "-fan";
                x = 29/4f;
                y = 44/4f;
                rotateSpeed = 4f;
            }},new DrawBlurSpin(){{
                suffix = "-fan";
                x = -31/4f;
                y = -14/4f;
                rotateSpeed = -2f;
            }},new DrawBlurSpin(){{
                suffix = "-fan";
                x = 29/4f;
                y = 44/4f;
                rotateSpeed = -2f;
            }},new DrawLiquidTile(oil, 1.5f){{ alpha = 0.6f;}},new DrawLiquidTile(water, 1.5f){{ alpha = 0.6f;}}, new DrawDefault(), new DrawGlowRegion("-glow"){{
                alpha = 0.3f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 7f;
                layer =  heat;
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new BetterDrawParticles(){{
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
                layer = heat;
                blending = Blending.additive;
            }}, new PhaseOffsetGlowRegion("-glow1"){{
                alpha = 0.4f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.25f;
                glowScale = 6f;
                layer = heat;
                blending = Blending.additive;
                phaseOffset = 10;
            }}, new PhaseOffsetGlowRegion("-glow2"){{
                alpha = 0.45f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 8f;
                layer = heat;
                blending = Blending.additive;
                phaseOffset = 15;
            }}, new PhaseOffsetGlowRegion("-glow3"){{
                alpha = 0.55f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.5f;
                glowScale = 10f;
                layer = heat;
                blending = Blending.additive;
                phaseOffset = 20;
            }}, new DrawPistons(){{
                sides = 1;
            }});
        }};
        cupronickelAlloyer = new AquaGenericCrafter("cupronickel-alloying-crucible"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with( nickel, 700, copper, 400));
            consumePower(150/60f);
            squareSprite = false;
            size = 4;
            baseEfficiency = 1;
            heatRequirement = 30;
            maxEfficiency = 4;
            craftTime = 120;
            consumeItems(new ItemStack(copper, 9), new ItemStack(nickel, 3));
            outputItem = new ItemStack(cupronickel, 12);
            updateEffect = Fx.coalSmeltsmoke;
            updateEffectChance = 0.07f;
            ambientSound = AquaSounds.derrick;
            consumeLiquid(air, 100/60f);
            liquidCapacity = 200;
            itemCapacity = 60;
            researchCostMultiplier = 0.02f;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"),new DrawSoftParticles(){{
                color = Color.valueOf("921f12");
                color2 = Color.valueOf("ff9f4f");
                alpha = 0.5f;
                x = -2f;
                particleRad = 8f;
                particleSize = 6f;
                particleLife = 45f;
                particles = 35;
            }}, new DrawDefault());
        }};
        AnnealingOven = new AquaGenericCrafter("metaglass-annealing-furnace"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with( lead, 300, copper, 550));
            size = 5;
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawDefault(), new PhaseOffsetGlowRegion("-glow"){{
                alpha = 0.55f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 9f;
                layer = heat;
                blending = Blending.additive;
                phaseOffset = 20;
            }}, new PhaseOffsetGlowRegion("-glow1"){{
                alpha = 0.40f;
                color = Color.valueOf("e68569");
                glowIntensity = 0.4f;
                glowScale = 7f;
                layer = heat;
                blending = Blending.additive;
                phaseOffset = 10;
            }});
        }};
        thermalCrackingUnit = new HeatCrafter("thermal-cracking-unit"){{
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
            outputLiquids = LiquidStack.with(methane, 1, petroleum, 90/60f);
            liquidOutputDirections = new int[]{1, 3};
            outputItem = new ItemStack(coal, 5);
            liquidCapacity = 300;
            itemCapacity = 60;
            researchCostMultiplier = 0.1f;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawDefault(), new DrawGlowRegion());
        }};
        SilicaOxidator = new GenericCrafter("silicon-oxidator"){{
            requirements(Category.crafting, with( copper, 300, graphite, 200, metaglass, 200));
            size = 3;
            squareSprite = false;
            itemCapacity = 60;
            liquidCapacity = 200;
            craftTime = 150;
            consumeItem(silicon, 20);
            consumeLiquid(oxygen, 40/60f);
            outputItem = new ItemStack(sand, 20);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawParticles(){{
                color = Color.valueOf("ffb5ad");
                alpha = 0.3f;
                particleSize =  3f;
                particles = 9;
                particleRad = 6f;
                particleLife = 90f;
                rotateScl = 1.5f;
            }}, new DrawLiquidTile(oxygen, 3){{
                alpha = 0.5f;
            }}, new DrawDefault());
        }};
        arcFurnace = new GenericCrafter("arc-furnace"){{
            requirements(Category.crafting, with( graphite, 250, copper, 120, metaglass, 150));
            size = 4;
            squareSprite = false;
            craftTime = 300;
            itemCapacity = 80;
            liquidCapacity = 200;
            consumePower(400/60f);
            consumeLiquid(water, 50/60f);
            consumeItem(sand, 40);
            outputLiquid = new LiquidStack(oxygen, 40/60f);
            outputItem = new ItemStack(silicon, 40);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"),new DrawSoftParticles(){{
                alpha = 0.35f;
                particleRad = 5f;
                particleSize = 9f;
                particleLife = 120f;
                particles = 15;
            }},new DrawSoftParticles(){{
                color = Color.valueOf("88d9eb");
                color2 = Color.white;
                alpha = 0.25f;
                particleRad = 3f;
                particleSize = 7f;
                particleLife = 120f;
                particles = 8;
            }},new DrawSoftParticles(){{
                alpha = 0.35f;
                particleRad = 5f;
                y = 34/4f;
                particleSize = 9f;
                particleLife = 120f;
                particles = 15;
            }},new DrawSoftParticles(){{
                color = Color.valueOf("88d9eb");
                color2 = Color.white;
                alpha = 0.25f;
                y = 34/4f;
                particleRad = 3f;
                particleSize = 7f;
                particleLife = 120f;
                particles = 8;
            }},new DrawSoftParticles(){{
                alpha = 0.35f;
                particleRad = 5f;
                y = 27/4f;
                x = -37/4f;
                particleSize = 9f;
                particleLife = 120f;
                particles = 15;
            }},new DrawSoftParticles(){{
                color = Color.valueOf("88d9eb");
                color2 = Color.white;
                alpha = 0.25f;
                y = 27/4f;
                x = -37/4f;
                particleRad = 3f;
                particleSize = 7f;
                particleLife = 120f;
                particles = 8;
            }},new DrawSoftParticles(){{
                alpha = 0.35f;
                particleRad = 5f;
                x = 32/4f;
                particleSize = 9f;
                particleLife = 120f;
                particles = 15;
            }},new DrawSoftParticles(){{
                color = Color.valueOf("88d9eb");
                color2 = Color.white;
                alpha = 0.25f;
                x = 32/4f;
                particleRad = 3f;
                particleSize = 7f;
                particleLife = 120f;
                particles = 8;
            }}, new DrawDefault(), new DrawGlowRegion(){{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }});
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
            outputLiquid = new LiquidStack(methane, 2);
            outputItems = ItemStack.with(brimstone, 3);
            liquidCapacity = 300;
            itemCapacity = 60;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawDefault(), new DrawGlowRegion());
        }};
        desulferizationAssembly = new AquaGenericCrafter("desulferization-assembly"){{
            requirements(Category.crafting, with( metaglass, 250, copper, 500, lead, 1200));
            shownPlanets.addAll(tantros2);
            craftTime = 4*60f;
            heatRequirement = 45;
            maxEfficiency = 2;
            craftEffect = Fx.reactorsmoke;
            updateEffectChance = 0.08f;
            updateEffect = Fx.reactorsmoke;
            researchCostMultiplier = 0.1f;
            consumeLiquid(petroleum, 450/60f);
            outputItem = new ItemStack(brimstone, 30);
            outputLiquid = new LiquidStack(methane, 150/60f);
            liquidCapacity = 900;
            itemCapacity = 120;
            size = 5;
            squareSprite = false;
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawLiquidTile(petroleum, 3), new DrawDefault(), new DrawGlowRegion());
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawDefault(),new DrawParticles(){{
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawLiquidTile(halideWater){{
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
            requirements(Category.crafting, with( copper, 120, aluminum, 420, graphite, 150));
            size = 6;
            researchCostMultiplier = 0.08f;
            squareSprite = false;
            ambientSound = Sounds.electricHum;
            consumePower(350/60f);
            consumeLiquid(halideWater, 400/60f);
            craftTime = 10;
            outputLiquids = LiquidStack.with( air, 200/60f,water, 400/60f);
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawDefault(){
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;}},new DrawDefault(), new DrawBlurSpin("-fan", 8f), new DrawRegion("-top"),new DrawParticles(){{
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawCellsNew(){{
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawOrbitRegions("-centrifuge", 4, 7f, 3.5f), new DrawLiquidTile(air){{
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawLiquidTile(slag){{
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawDefault(), new DrawPump("-bit"));
        }};
        brineMixer = new GenericCrafter("brine-mixer"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with( cupronickel, 200, metaglass, 350));
            size = 5;
            itemCapacity = 750;
            liquidCapacity = 2000;
            squareSprite = false;
            craftTime = 10*60f;
            consumeItem(salt, 250);
            consumeLiquid(water, 500/60f);
            consumePower(1000/60f);
            outputLiquid = new LiquidStack(brine, 500/60f);
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawLiquidTile(brine){{
                padTop = 51/4f;
                padLeft = 3;
                padBottom = 78/4f;
                padRight = 3;
            }}, new DrawLiquidTile(water){{
                padTop = 103/4f;
                padLeft = 3;
                padBottom = 29/4f;
                padRight = 3;
            }}, new DrawRegion("-top"), new DrawGlowRegion(){{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.5f;
                color = Color.valueOf("dd5858");
            }},new DrawGlowRegion("-glow1"){{
                glowIntensity = 0.65f;
                glowScale = 6;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }});
        }};
        brineElectrolyzer = new GenericCrafter("brine-electrolysis-manifold"){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.crafting, with( cupronickel, 2500, silicon, 3000, metaglass, 500));
            size = 7;
            rotate = true;
            liquidCapacity = 4000;
            rotateDraw = false;
            consumePower(2000/60f);
            craftTime = 10*60f;
            liquidOutputDirections = new int[]{3, 2, 1};
            consumeLiquid(brine, 1000/60f);
            outputLiquids = new LiquidStack[]{
                    new LiquidStack(chlorine, 1000/60f),
                    new LiquidStack(hydrogen, 1000/60f),
                    new LiquidStack(hydroxide, 2000/60f)
            };
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawLiquidTile(hydroxide){{
                padBottom = 111/4f;
                alpha = 0.8f;
            }}, new DrawLiquidTile(chlorine){{
                padTop = 113/4f;
                alpha = 0.8f;
            }}, new DrawRegion("-middle"), new DrawLiquidTile(hydrogen){{
                padTop = 67/4f;
                padBottom = 130/4f;
                padRight = 151/4f;
                alpha = 0.8f;
            }}, new DrawRegion("-top"), new DrawLiquidOutputs(), new DrawGlowRegion(){{
                glowIntensity = 0.7f;
                glowScale = 9;
                alpha = 0.5f;
                color = Color.valueOf("dd5858");
            }},new DrawGlowRegion("-glow1"){{
                glowIntensity = 0.65f;
                glowScale = 6;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }},new DrawGlowRegion("-glow2"){{
                glowIntensity = 0.9f;
                glowScale = 7;
                alpha = 0.6f;
                color = Color.valueOf("f5c5aa");
            }},new DrawGlowRegion("-glow3"){{
                glowIntensity = 0.8f;
                glowScale = 8;
                alpha = 0.5f;
                color = Color.valueOf("f5c5aa");
            }});
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawRegion("-bottom"), new DrawLiquidTile(water){{
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawDefault(), new DrawGlowRegion(){{
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
        ultrafamicRefinery = new AquaGenericCrafter("ultrafamic-refinery"){{
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
            outputLiquids = LiquidStack.with(hydroxide, 100/60f);
            outputItems = new ItemStack[]{
                    new ItemStack(sand, 4),
                    new ItemStack(ferricMatter, 8),
                    new ItemStack(nickel, 6),
                    new ItemStack(magnesiumPowder, 10)
            };
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawDefault(), new DrawFramesNew(){{
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawDefault(), new DrawGlowRegion(){{
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawDefault(), new DrawLiquidTile(oil, 4),new DrawSoftParticles(){{
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
            drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}},new DrawDefault(), new DrawHeatOutput(), new DrawHeatInput("-heat"));
            size = 2;
        }};
    }

    public static void disableVanilla() {
        Blocks.landingPad.buildVisibility = BuildVisibility.shown;
    }
}
