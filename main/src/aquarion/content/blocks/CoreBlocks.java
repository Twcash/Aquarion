package aquarion.content.blocks;

import aquarion.content.AquaItems;
import aquarion.content.AquaUnitTypes;
import aquarion.content.AquaBullets;
import aquarion.world.blocks.core.AquaCoreBlock;
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
import aquarion.world.blocks.neoplasia.DefensiveNeoplasiaBlock;
import aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock;
import aquarion.world.blocks.neoplasia.NeoplasmHeart;
import aquarion.world.blocks.neoplasia.NeoplasiaGraph;
import aquarion.world.blocks.neoplasia.NeoplasmTreeBase;
import aquarion.world.blocks.neoplasia.NeoplasmTurret;
import aquarion.world.blocks.neoplasia.NeoplasmVein;
import aquarion.world.entities.bullet.NeoplasmGlobBulletType;
import aquarion.world.blocks.neoplasia.NeoplasiaproductionBlock;
import aquarion.world.AI.PopperAI;
import aquarion.world.content.AquaItem;
import aquarion.world.graphics.AquaFx;
import arc.func.Cons;
import arc.graphics.Color;
import mindustry.content.*;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.effect.MultiEffect;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.BuildTurret;
import mindustry.world.blocks.environment.OverlayFloor;
import mindustry.world.blocks.logic.MessageBlock;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaLiquids.fumes;
import static aquarion.content.AquaLiquids.haze;
import static aquarion.content.AquaPlanets.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.nitrogen;
import static mindustry.type.ItemStack.with;


public class CoreBlocks {
    public static Block bomb, toggler, splitter, channel, storageReader, merger, buzzSaw, reception, infomatic, mendPyre, mendSubstation, mendPylon, cache, coreCuesta, overClockProjector,
            coreEscarpment, laboratory, petal, reconstruct,  corePike, buildCairn, constructionTower, crate, deflectorWell,             neoplasiaMass, OreSlurper, oreSlurperer, oresplurpererer, callus, thicBlob, enzyme, heart, vein, tree, neoplasmBlobber, researchVoider;

    public static <T extends UnlockableContent> void overwrite(UnlockableContent target, Cons<T> setter) {
        setter.get((T) target);
    }

    public static void loadContent() {
        infomatic = new InfomaticBlock("infomatic"){{
            requirements(Category.logic, with(silicon, 10));
            size = 1;
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
        reception = new CoreBlock("reception"){{
            size = 2;
            itemCapacity = 1;
            unitType = AquaUnitTypes.visitor;
            solid = false;
            buildVisibility = BuildVisibility.sandboxOnly;
            requirements(Category.effect, with(libraryCard, 1));
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
        cache = new StorageBlock("cache") {{
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
        crate = new StorageBlock("crate") {{
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
            phaseBoost = 0;
            phaseRangeBoost = 0;
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
            phaseBoost = 0;
            phaseRangeBoost = 0;
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
            requirements(Category.effect, with(silicon, 1));
            buildVisibility = BuildVisibility.sandboxOnly;
            base = this;
            itemCapacity = 8;
            perItemCapacity = true;
            burstThresholdFraction = 0.55f;
            emptyUpgrade = (GenericNeoplasiaBlock) thicBlob;
            emptyUpgradeCost = 800;
            oreUpgrade = (GenericNeoplasiaBlock) (OreSlurper = new NeoplasiaproductionBlock("ore-slurper") {{
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
                    selfGrowRate = 1.5f;
                    itemCapacity = 60;
                    perItemCapacity = true;
                    burstThresholdFraction = 0.95f;
                    itemCost = ItemStack.with(crystal, 20, pearl, 10);
                    bursts = 3;
                    cost = 1500;
                    baseSize = 20;
                    oreUpgrade = (GenericNeoplasiaBlock) (oresplurpererer = new NeoplasiaproductionBlock("ore-slurpererer") {{
                        maxAmount = 2500;
                        burstDelay = 50;
                        cost = 2500;
                        requirements(Category.effect, with(silicon, 1));
                        burstLength = 15;
                        itemCapacity = 70;
                        perItemCapacity = true;
                        selfGrowRate = 2.6f;
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
            shouldEmptyUpgrade = true;
            emptyUpgrade = null;
//            emptyUpgrade = (GenericNeoplasiaBlock) (thicBlob = new GenericNeoplasiaBlock("thic-neoplasia-blob") {{
//                base = (GenericNeoplasiaBlock) neoplasiaMass;
//                baseSize = 16;
//                maxAmount = 2500;
//                itemCapacity = 10;
//                shouldEmptyUpgrade = false;
//                oreUpgrade = (GenericNeoplasiaBlock) OreSlurper;
//                damageUpgrade = (GenericNeoplasiaBlock) callus;
//                colFrom = Color.valueOf("cf683b");
//                colTo = Color.valueOf("e2c451");
//            }});
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
            oreGrowBonus = 0.01f;
            craftTime = 180;
            selfGrowRate = 0.01f;
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
            oreGrowBonus = 0.01f;
            selfGrowRate = 0.1f;
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
        }};
        heart = new NeoplasmHeart("neoplasm-heart") {{
            requirements(Category.effect, with(crystal, 9));
            buildVisibility = BuildVisibility.sandboxOnly;
            size = 1;
            health = 500;
        }};
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
        }};
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
        });
        overwrite(oresplurpererer, (NeoplasiaproductionBlock r) -> {
            r.oreUpgrade = null;
            r.base = (GenericNeoplasiaBlock) thicBlob;
            r.buildVisibility = BuildVisibility.sandboxOnly;
        });
        overwrite(callus, (DefensiveNeoplasiaBlock r) -> {
            r.oreUpgrade = null;
            //DamageUpgrade works in reverse here.
            r.damageUpgrade = (GenericNeoplasiaBlock) neoplasiaMass;
            r.buildVisibility = BuildVisibility.sandboxOnly;
        });
        overwrite(neoplasiaMass, (GenericNeoplasiaBlock r) -> {
            r.base = (GenericNeoplasiaBlock) neoplasiaMass;
            r.shouldEmpty2Upgrade = true;
            r.empty2Upgrade = (GenericNeoplasiaBlock) vein;
            r.empty2UpgradeCost = 1000;
            r.buildVisibility = BuildVisibility.sandboxOnly;
            r.emptyUpgrade = (GenericNeoplasiaBlock) neoplasmBlobber;
            r.emptyUpgradeCost = 200;
        });
        GenericNeoplasiaBlock.veinBlock = (GenericNeoplasiaBlock) vein;
        GenericNeoplasiaBlock.itemProducers.put(crystal, (GenericNeoplasiaBlock) enzyme);
        tree = new NeoplasmTreeBase("branching-sprout") {{
            requirements(Category.effect, with(silicon, 1));
            base = (GenericNeoplasiaBlock) neoplasiaMass;
            maxAmount = 40000;
            selfGrowRate = 2.5f;
            itemCapacity = 30;
            podCost = 100;
            branchCost = 200;
            unitGrowTime = 6f;
            maxPodsPerBranch = 3;
            unitItemCost = new ItemStack[]{new ItemStack(crystal, 2)};
            unitType = AquaUnitTypes.popper;
            spewerType = AquaUnitTypes.spewer;
            spewerChance = 0.3f;
            maxSpewers = 24;
            spewerItemCost = new ItemStack[]{new ItemStack(crystal, 2)};
            buildVisibility = BuildVisibility.sandboxOnly;
        }};
        GenericNeoplasiaBlock.treeBlock = (GenericNeoplasiaBlock) tree;
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
            requirements(Category.effect, with(silicon, 1500, nickel, 900, copper, 1000, metaglass, 200));
            size = 5;
            buildVisibility = BuildVisibility.campaignOnly;
            processRate = 1f;
            itemCapacity = 500;
            consumePower(10f);
            squareSprite = false;
            alwaysUnlocked = true;
            researchCostMultiplier = 0;
        }};
        laboratory = new ResearchVoider("laboratory"){{
            size = 5;
            squareSprite = false;
            destructible = false;
            consumePower(5);
            processRate = 2;
            itemCapacity = 250;
        }};
        ((aquarion.world.entities.bullet.NeoplasmGlobBulletType) AquaBullets.neoplasmGlob).neoplasiaBlock = (aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock) neoplasiaMass;
    }
}
