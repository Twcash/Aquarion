package aquarion.content.blocks;

import aquarion.content.*;
import aquarion.world.blocks.AquaBlock;
import aquarion.world.blocks.core.AquaCoreBlock;
import aquarion.world.blocks.core.SpecializationCatalyst;
import aquarion.world.blocks.core.TrackedStorageBlock;
import aquarion.world.blocks.AlternativeBuildCosts;
import aquarion.world.blocks.core.Bomb;
import aquarion.world.blocks.core.InfomaticBlock;
import aquarion.world.blocks.core.OverclockProjector;
import aquarion.world.blocks.effect.ResearchVoider;
import aquarion.world.blocks.defense.ChainsawTurret;
import aquarion.world.blocks.defense.RegenPylon;
import aquarion.world.blocks.defense.deflectorShield;
import aquarion.world.blocks.logic.BinaryChannel;
import aquarion.world.blocks.logic.BinarySplitter;
import aquarion.world.blocks.logic.StorageReader;
import aquarion.world.blocks.logic.toggler;
import aquarion.world.blocks.neoplasia.*;
import aquarion.world.entities.bullet.NeoplasmGlobBulletType;
import aquarion.world.entities.bullet.TentacleBulletType;
import aquarion.world.AI.PopperAI;
import aquarion.world.content.AquaItem;
import aquarion.world.graphics.AquaFx;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import mindustry.content.*;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.entities.effect.MultiEffect;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.BuildTurret;
import mindustry.world.blocks.environment.OverlayFloor;
import mindustry.world.blocks.logic.MessageBlock;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.sandbox.ItemSource;
import mindustry.world.blocks.sandbox.LiquidSource;
import mindustry.world.blocks.sandbox.PowerSource;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaLiquids.fumes;
import static aquarion.content.AquaLiquids.haze;
import static aquarion.content.AquaPlanets.*;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.nitrogen;
import static mindustry.type.ItemStack.with;


public class CoreBlocks {
    public static Block bomb, dPowerSource, dItemSource, dLiquidSource, speaker, toggler, splitter, channel, storageReader, merger, buzzSaw, coreReception, infomatic, mendPyre, mendSubstation, mendPylon, cache, coreCuesta, overClockProjector,
            coreEscarpment, laboratory, petal, reconstruct, specializationCatalyst,  corePike, buildCairn, constructionTower, crate, deflectorWell,blister, liquidSlurper, neoplasiaMass, OreSlurper, oreSlurperer, oresplurpererer, callus, thicBlob, enzyme, heart, vein, tree, tentaclePit, largeTentaclePit, neoplasmBlobber, largeBlobber, researchVoider;

    public static <T extends UnlockableContent> void overwrite(UnlockableContent target, Cons<T> setter) {
        setter.get((T) target);
    }

    public static void loadContent() {

        infomatic = new InfomaticBlock("infomatic"){{
            requirements(Category.logic, with(silicon, 10));
            canOverdrive = false;
            size = 1;
        }};
        speaker = new InfomaticBlock("speaker"){{
            requirements(Category.logic, BuildVisibility.worldProcessorOnly, with());
            size = 1;
            privileged = true;
            targetable = false;
        }};
        dPowerSource = new PowerSource("defunct-power-source"){{
            requirements(Category.logic, BuildVisibility.worldProcessorOnly, with());
            size = 1;
            privileged = true;
            targetable = false;
        }};
        dLiquidSource = new LiquidSource("defunct-liquid-source"){{
            requirements(Category.logic, BuildVisibility.worldProcessorOnly, with());
            size = 1;
            privileged = true;
            targetable = false;
        }};
        dItemSource = new ItemSource("defunct-item-source"){{
            requirements(Category.logic, BuildVisibility.worldProcessorOnly, with());
            size = 1;
            privileged = true;
            targetable = false;
        }};
        toggler = new toggler("toggler"){{
            requirements(Category.logic, with(silicon, 10, graphite, 5));
            size = 1;
        }};

        splitter = new BinarySplitter("splitter"){{
            requirements(Category.logic, with(silicon, 15, graphite, 10));
        }};
        channel = new BinaryChannel("binary-channel"){{
            requirements(Category.logic, with(silicon, 5, graphite, 10));
        }};
        storageReader = new StorageReader("storage-reader"){{
            requirements(Category.logic, with(silicon, 20, graphite, 10));
        }};
        coreReception = new CoreBlock("reception"){{
            requirements(Category.effect, with(libraryCard, 1));
            squareSprite = false;
            health = 2500;
            itemCapacity = 1;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            size = 2;
            unitType = AquaUnitTypes.visitor;
            alwaysUnlocked = true;
            solid = false;
            hasItems = true;
            hasColor = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        bomb = new Bomb("improvised-explosive"){{
            requirements(Category.effect, with(polymer, 50, copper, 900));
            size = 1;
            breakEffect = Fx.blastExplosion;
            radius = 2;
            overlayFloor = (OverlayFloor) EnvironmentBlocks.scorche;
            bannedFloor  = EnvironmentBlocks.rubble.asFloor();
            targetFloor = EnvironmentBlocks.rubble.asFloor();
        }};
        cache = new TrackedStorageBlock("cache") {{
            requirements(Category.effect, with(aluminum, 160, silicon, 150, ferricMatter, 300));
            itemCapacity = 900;
            coreMerge = true;
            squareSprite = false;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            size = 3;
            researchCostMultiplier = 0.02f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            //drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}}, new DrawDefault());
        }};
        crate = new TrackedStorageBlock("crate") {{
            requirements(Category.effect, with(cupronickel, 400, silicon, 800));
            itemCapacity = 150;
            coreMerge = false;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            squareSprite = false;
            size = 2;
            researchCostMultiplier = 0.02f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            //drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}}, new DrawDefault());
        }};
        corePike = new AquaCoreBlock("core-pike") {{
            requirements(Category.effect, with(silicon, 1500));
            squareSprite = false;
            health = 2500;
            itemCapacity = 5000;
            incinerateNonBuildable = true;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            size = 4;
            unitCapModifier = 25;
            unitType = AquaUnitTypes.cull;
            unitTypes.add(AquaUnitTypes.cullButScorch);
            unitTypes.add(AquaUnitTypes.cull);
            alwaysUnlocked = true;
            hasItems = true;
            hasColor = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        coreCuesta = new AquaCoreBlock("core-cuesta") {{
            requirements(Category.effect, with(silicon, 3000, ferrosilicon, 2500, steel, 1500));
            squareSprite = false;
            health = 7500;
            armor = 8;
            itemCapacity = 7500;
            incinerateNonBuildable = true;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            size = 6;
            unitCapModifier = 45;
            unitType = AquaUnitTypes.cull;
            unitTypes.add(AquaUnitTypes.cullButScorch);
            unitTypes.add(AquaUnitTypes.cull);
            alwaysUnlocked = false;
            hasItems = true;
            hasColor = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        specializationCatalyst = new SpecializationCatalyst("specialization-catalyst"){{
            requirements(Category.effect, with(silicon, 900, graphite, 600, copper, 250, metaglass, 400));
            size = 3;
            squareSprite = false;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            plans.add(new UnitPlan(AquaUnitTypes.simplify, 120f, with(metaglass, 90, silicon, 150, graphite, 50)));
            plans.add(new UnitPlan(AquaUnitTypes.multiply, 60f, with(silicon, 100, copper, 100, graphite, 20)));
            plans.add(new UnitPlan(AquaUnitTypes.quantify, 90f, with(graphite, 100, silicon, 200,lead, 150)));
        }};
        buildCairn = new BuildTurret("build-cairn") {{
            requirements(Category.effect, with(silicon, 120, copper, 50));
            size = 2;
            outlineRadius = 0;
            buildSpeed = 0.4f;
            range = 145;
            rotateSpeed = 0.85f;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            schematicPriority = -11;
            consumePower(1.5f);
        }};
        constructionTower = new BuildTurret("construction-tower") {{
            requirements(Category.effect, with(silicon, 700, polymer, 250, copper, 2000));
            size = 3;
            outlineRadius = 0;
            squareSprite = false;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            consumeLiquid(nitrogen, 2);
            buildSpeed = 0.8f;
            range = 300;
            rotateSpeed = 0.85f;
            schematicPriority = 8;
            consumePower(4);
        }};
        reconstruct = new BuildTurret("reconstruct") {{
            shownPlanets.addAll(Planets.serpulo, fakeSerpulo);
            requirements(Category.effect, with(silicon, 700));
            size = 2;
            outlineRadius = 3;
            consumeLiquid(Liquids.water, 2);
            buildSpeed = 0.8f;
            range = 100;
            rotateSpeed = 5f;
            schematicPriority = 8;
            consumePower(2);
        }};
        mendPyre = new RegenPylon("mend-pyre") {{
            requirements(Category.effect, with(lead, 55, silicon, 80));
            size = 1;
            schematicPriority = 2;
            consumePower(0.75f);
            consumeItem(Items.silicon).boost();
            range = 26;
            phaseBoost = 2f;
            phaseRangeBoost = 3f;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            healAmount = 250;
            phaseBoost = 2;
            squareSprite = false;
            reload = 300;
            alwaysUnlocked = true;
        }};
        mendPylon = new RegenPylon("mend-pylon") {{
            requirements(Category.effect, with(silicon, 60, aluminum, 40));
            size = 2;
            schematicPriority = 7;
            consumePower(0.125f);
            consumeLiquid(fumes, 0.05f);
            range = 35;
            squareSprite = false;
            reload = 240;
            healAmount = 300;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            liquidCapacity = 60;
            researchCostMultiplier = 0;
        }};
        mendSubstation = new RegenPylon("mend-substation") {{
            requirements(Category.effect, with(silicon, 900, ferricMatter,240, polymer, 500));
            size = 3;
            schematicPriority = 7;
            lightningDamage = 10;
            lightningReload = 90;
            consumePower(6);
            consumeLiquid(haze, 3f);
            range = 70;
            healAmount = 500;
            squareSprite = false;
            reload = 120;
            lightningRange = 50;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            liquidCapacity = 700;
        }};
        deflectorWell = new deflectorShield("deflector-well") {{
            requirements(Category.effect, with(polymer, 800, metaglass, 900, silicon, 1200));
            size = 4;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            squareSprite = false;
            liquidCapacity = 500;
            sides = 180;
            shieldRotation = 45;
            consumeLiquid(haze, 3.5f);
            consumePower(4);
            radius = 120;
            shieldBreakEffect = new Effect(40, e -> {
                color(e.color);
                stroke(3f * e.fout());
                Lines.poly(e.x, e.y, 4, 200, 0);
            });
            forceShrinkEffect = new Effect(40, e -> {
                            color(e.color);
                            stroke(3f * e.fout());
                            Lines.poly(e.x, e.y, 32, 40-e.time, 0);
                        });
        }};
        buzzSaw = new ChainsawTurret("buzzsaw") {{
            requirements(Category.turret, with(silicon, 250, lead, 300));
            size = 3;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            consumePower(4);
            researchCostMultiplier = 0.02f;
            damage = 15;
            range = 180;
        }};
        neoplasiaMass = new GenericNeoplasiaBlock("neoplasia-mass") {{
            veinCandidate = true;
            requirements(Category.effect, with(silicon, 1));
            buildVisibility = BuildVisibility.sandboxOnly;
            base = this;
            itemCapacity = 20;
            perItemCapacity = true;
            burstThresholdFraction = 0.55f;
            emptyUpgrade = (GenericNeoplasiaBlock) thicBlob;
            emptyUpgradeCost = 800;
            oreUpgrade = (GenericNeoplasiaBlock) (OreSlurper = new NeoplasiaproductionBlock("ore-slurper") {{
                selfGrowRate = 2f;
                oreGrowBonus = 1f;
                massExportRate = 1f;
                requirements(Category.effect, with(silicon, 1));
                maxAmount = 1600;
                itemCapacity = 50;
                perItemCapacity = true;
                burstThresholdFraction = 0.95f;
                cost = 500;
                itemCost = ItemStack.with(crystal, 1);
                oreUpgrade = (GenericNeoplasiaBlock) (oreSlurperer = new NeoplasiaproductionBlock("ore-slurperer") {{
                    maxAmount = 2700;
                    requirements(Category.effect, with(silicon, 1));
                    burstDelay = 100;
                    burstLength = 15;
                    selfGrowRate = 6f;
                    oreGrowBonus = 3f;
                    massExportRate = 3f;
                    itemCapacity = 60;
                    perItemCapacity = true;
                    burstThresholdFraction = 0.95f;
                    itemCost = ItemStack.with(crystal, 20, pearl, 10);
                    bursts = 3;
                    cost = 1500;
                    baseSize = 20;
                    oreUpgrade = (GenericNeoplasiaBlock) (oresplurpererer = new NeoplasiaproductionBlock("ore-slurpererer") {{
                        maxAmount = 10000;
                        burstDelay = 50;
                        cost = 2500;
                        requirements(Category.effect, with(silicon, 1));
                        burstLength = 15;
                        itemCapacity = 70;
                        perItemCapacity = true;
                        selfGrowRate = 18f;
                        oreGrowBonus = 9f;
                        massExportRate = 9f;
                        itemCost = ItemStack.with(crystal, 20, pearl, 10);
                        bursts = 5;
                        oreUpgrade = null;
                        baseSize = 32;
                    }});
                }});
                burstDelay = 300;
                burstLength = 10;
                baseSize = 16;
            }});
            damageUpgrade = (GenericNeoplasiaBlock) (callus = new DefensiveNeoplasiaBlock("callus") {{
                requirements(Category.effect, with(silicon, 1));
                baseSize = 16;
            }});
        }};
        enzyme = new NeoplasiaproductionBlock("enzyme") {{
            requirements(Category.effect, with(silicon, 1));
            buildVisibility = BuildVisibility.sandboxOnly;
            baseSize = 16;
            maxAmount = 1500;
            burstLength = 15;
            shouldCraft = true;
            base = (GenericNeoplasiaBlock) neoplasiaMass;
            output = new ItemStack(crystal, 1);
            craftCost = 100;
            oreGrowBonus = 0.25f;
            craftTime = 180;
            selfGrowRate = 0.75f;
            bursts = 0;
            itemCapacity = 20;
            burstDelay = 50;
            cost = 200;
            shouldEmptyUpgrade = false;
            damageUpgrade = (GenericNeoplasiaBlock) callus;
        }};
        petal = new NeoplasiaproductionBlock("petal") {{
            requirements(Category.effect, with(silicon, 1));
            buildVisibility = BuildVisibility.sandboxOnly;
            baseSize = 16;
            maxAmount = 900;
            burstLength = 15;
            itemCapacity = 30;
            shouldCraft = true;
            itemCost = ItemStack.with(crystal, 4);;
            base = (GenericNeoplasiaBlock) neoplasiaMass;
            output = new ItemStack(pearl, 2);
            craftCost = 300;
            oreGrowBonus = 0.5f;
            selfGrowRate = 1.5f;
            bursts = 0;
            burstThresholdFraction = 0.4f;
            burstDelay = 50;
            cost = 200;
            craftTime = 240;
            shouldEmptyUpgrade = false;
            damageUpgrade = (GenericNeoplasiaBlock) callus;
        }};
        vein = new NeoplasmVein("vein") {{
            requirements(Category.effect, with(silicon, 1));
            buildVisibility = BuildVisibility.sandboxOnly;
            baseSize = 16;
            maxAmount = 2000;
            itemCapacity = 40;
            bursts = 0;
            selfGrowRate = 0.01f;
            shouldCraft = false;
            shouldEmptyUpgrade = false;
            base = (GenericNeoplasiaBlock) neoplasiaMass;
            requiredTech = 1;
        }};
        heart = new NeoplasmHeart("neoplasm-heart") {{
            requirements(Category.effect, with(crystal, 9));
            buildVisibility = BuildVisibility.sandboxOnly;
            size = 1;
            health = 500;
            baseSize = 14f;
            maxAmount = 500f;
            startMass = 25f;
            selfGrowRate = 0.05f;
            techStep = 100f;
            techMax = 5;
            heartCost = 150f;
            colFrom = Color.valueOf("8B0000");
            colTo = Color.valueOf("FF6347");
        }};
        NeoplasiaGraph.heartBlock = (NeoplasmHeart) heart;
        NeoplasiaGraph.heartCost = ((NeoplasmHeart) heart).heartCost;
        neoplasmBlobber = new NeoplasmTurret("neoplasm-blobber"){{
            buildVisibility = BuildVisibility.sandboxOnly;
            maxAmount = 500;
            selfGrowRate = 0.02f;
            baseSize = 16;
            range = 100f;
            reloadTime = 120f;
            shootType = (NeoplasmGlobBulletType) AquaBullets.neoplasmGlob;
            itemCost = ItemStack.with(crystal, 3);
            colFrom = Color.valueOf("8B0000");
            colTo = Color.valueOf("FF6347");
            requiredTech = 1;
        }};
        largeBlobber = new NeoplasmTurret("large-blobber"){{
            requirements(Category.effect, BuildVisibility.sandboxOnly, with(crystal, 6));
            maxAmount = 1000f;
            selfGrowRate = 0.02f;
            baseSize = 20f;
            base = (GenericNeoplasiaBlock)neoplasiaMass;
            range = 180f;
            shootLength = 12f;
            reloadTime = 120f;
            shootType = (NeoplasmGlobBulletType)AquaBullets.neoplasmGlob;
            itemCost = with(crystal, 6);
            shouldEmptyUpgrade = false;
            colFrom = Color.valueOf("8B0000");
            colTo = Color.valueOf("FF6347");
            requiredTech = 2;
        }};
        ((GenericNeoplasiaBlock)neoplasmBlobber).emptyUpgrade = (GenericNeoplasiaBlock)largeBlobber;
        ((GenericNeoplasiaBlock)neoplasmBlobber).emptyUpgradeCost = 400f;
        blister = new NeoplasiaBlister("neoplasia-blister"){{
            base = (GenericNeoplasiaBlock) neoplasiaMass;
        }};
        liquidSlurper = new NeoplasmLiquidSlurper("liquid-slurper"){{
            requirements(Category.effect, with(silicon, 1));
            base = (GenericNeoplasiaBlock)neoplasiaMass;
        }};
        GenericNeoplasiaBlock.liquidSlurperBlock = (NeoplasmLiquidSlurper)liquidSlurper;
        //Holy Jank...
        overwrite(OreSlurper, (NeoplasiaproductionBlock r) -> {
            r.oreUpgrade = (GenericNeoplasiaBlock) oreSlurperer;
            r.base = (GenericNeoplasiaBlock) neoplasiaMass;
            r.buildVisibility = BuildVisibility.sandboxOnly;
        });
        overwrite(oreSlurperer, (NeoplasiaproductionBlock r) -> {
            r.oreUpgrade = (GenericNeoplasiaBlock) oresplurpererer;
            r.base = (GenericNeoplasiaBlock) neoplasiaMass;
            r.buildVisibility = BuildVisibility.sandboxOnly;
            r.requiredTech = 1;
        });
        overwrite(oresplurpererer, (NeoplasiaproductionBlock r) -> {
            r.oreUpgrade = null;
            r.base = (GenericNeoplasiaBlock) neoplasiaMass;
            r.buildVisibility = BuildVisibility.sandboxOnly;
            r.requiredTech = 2;
        });
        overwrite(callus, (DefensiveNeoplasiaBlock r) -> {
            r.oreUpgrade = null;
            //DamageUpgrade works in reverse here.
            r.damageUpgrade = (GenericNeoplasiaBlock) neoplasiaMass;
            r.buildVisibility = BuildVisibility.sandboxOnly;
            r.requiredTech = 2;
        });
        overwrite(neoplasiaMass, (GenericNeoplasiaBlock r) -> {
            r.base = (GenericNeoplasiaBlock) neoplasiaMass;
            r.shouldEmpty2Upgrade = false;
            r.empty2Upgrade = null;
            r.buildVisibility = BuildVisibility.sandboxOnly;
            r.emptyUpgrade = (GenericNeoplasiaBlock) neoplasmBlobber;
            r.emptyUpgradeCost = 200;
            r.ventBlock = (GenericNeoplasiaBlock) blister;
        });
        GenericNeoplasiaBlock.veinBlock = (GenericNeoplasiaBlock) vein;
        GenericNeoplasiaBlock.itemProducers.put(crystal, (GenericNeoplasiaBlock) enzyme);
        tree = new NeoplasmTreeBase("branching-sprout") {{
            requirements(Category.effect, with(crystal, 10, pearl, 2));
            cost = 750f;
            itemCost = with(crystal, 10, pearl, 2);
            base = (GenericNeoplasiaBlock) neoplasiaMass;
            maxAmount = 40000;
            selfGrowRate = 2.5f;
            itemCapacity = 30;
            perItemCapacity = true;
            podCost = 100;
            branchCost = 200;
            branchInterval = 180f;
            spacing = 32f;
            unitGrowTime = 45f * 60f;
            unitSpawnInterval = 16f * 60f;
            maxPodsPerBranch = 3;
            unitItemCost = new ItemStack[]{new ItemStack(crystal, 2)};
            unitType = AquaUnitTypes.popper;
            spewerType = AquaUnitTypes.spewer;
            spewerChance = 0.3f;
            spewerItemCost = with(crystal, 4, pearl, 1);
            buildVisibility = BuildVisibility.sandboxOnly;
        }};
        GenericNeoplasiaBlock.treeBlock = (GenericNeoplasiaBlock) tree;
        tentaclePit = new NeoplasmTentaclePit("tentacle-pit"){{
            requirements(Category.effect, BuildVisibility.sandboxOnly, with(pearl, 2, crystal, 10));
            itemCost = with(pearl, 2, crystal, 10);
            upgradeItemCost = with(pearl, 2, crystal, 10);
            base = (GenericNeoplasiaBlock)neoplasiaMass;
            maxAmount = 1500f;
            baseSize = 16f;
            shootType = new TentacleBulletType(){{
                sprite = "aquarion-neoplasia-tentacle";
                range = 104f;
                segmentCount = 19;
                segmentLength = 6f;
                segmentOffset = 4f;
                layer = mindustry.graphics.Layer.bullet - 1f;
                width = 7f;
                damage = 8f;
                damageInterval = 2f;
                lifetime = 90f;
                collidesTiles = true;
            }};
        }};
        largeTentaclePit = new NeoplasmTentaclePit("large-tentacle-pit"){{
            requirements(Category.effect, BuildVisibility.sandboxOnly, with(pearl, 4, crystal, 20));
            itemCost = with(pearl, 4, crystal, 20);
            base = (GenericNeoplasiaBlock)neoplasiaMass;
            maxAmount = 3000f;
            baseSize = 24f;
            shootType = new TentacleBulletType(){{
                sprite = "aquarion-large-neoplasia-tentacle";
                range = 180f;
                segmentCount = 25;
                segmentLength = 14;
                segmentOffset = 16f;
                layer = mindustry.graphics.Layer.bullet - 1f;
                width = 9f;
                damage = 60f;
                damageInterval = 20f;
                lifetime = 90f;
                collidesTiles = true;
            }};
        }};
        ((NeoplasmTentaclePit)tentaclePit).largeVariant = (NeoplasmTentaclePit)largeTentaclePit;
        GenericNeoplasiaBlock.tentaclePitBlock = (NeoplasmTentaclePit)tentaclePit;
        PopperAI.defaultBlobBlock = (GenericNeoplasiaBlock) neoplasiaMass;
        PopperAI.defaultBlobItems = new ItemStack[]{new ItemStack(crystal, 1)};
        GenericNeoplasiaBlock.itemProducers.put(pearl, (GenericNeoplasiaBlock) petal);
        overClockProjector = new OverclockProjector("overclock-projector") {{
            requirements(Category.effect, with(silicon, 150, copper, 900, polymer, 100, metaglass, 200, ferricMatter, 250));
            size = 2;
            consumePower(6);
            consumeLiquid(Liquids.water, 4);
            range = 150;
            hasBoost = false;
            speedBoost = 2.0f;
            squareSprite = false;
        }};
        researchVoider = new ResearchVoider("translation-matrix") {{
            requirements(Category.effect, with(silicon, 1500, lead, 1000));
            size = 5;
            buildVisibility = BuildVisibility.campaignOnly;
            processRate = 1f;
            itemCapacity = 500;
            ambientSound = AquaSounds.researchLabloop;
            researchSound = AquaSounds.researchLabVoid;
            consumePower(10f);
            squareSprite = false;
            alwaysUnlocked = true;
            researchCostMultiplier = 0;
        }};
        AlternativeBuildCosts.register(researchVoider, with(nickel, 900, zinc, 900));
        laboratory = new ResearchVoider("laboratory"){{
            size = 5;
            squareSprite = false;
            ambientSound = AquaSounds.researchLabloop;
            researchSound = AquaSounds.researchLabVoid;
            destructible = false;
            consumePower(5);
            processRate = 2;
            itemCapacity = 250;
        }};
        ((aquarion.world.entities.bullet.NeoplasmGlobBulletType) AquaBullets.neoplasmGlob).neoplasiaBlock = (aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock) neoplasiaMass;
    }
}
