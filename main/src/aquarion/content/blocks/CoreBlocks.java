package aquarion.content.blocks;

import aquarion.content.AquaItems;
import aquarion.content.AquaUnitTypes;
import aquarion.world.blocks.core.AquaCoreBlock;
import aquarion.world.blocks.core.OverclockProjector;
import aquarion.world.blocks.defense.ChainsawTurret;
import aquarion.world.blocks.defense.RegenPylon;
import aquarion.world.blocks.defense.deflectorShield;
import aquarion.world.blocks.neoplasia.DefensiveNeoplasiaBlock;
import aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock;
import aquarion.world.blocks.neoplasia.NeoplasiaproductionBlock;
import aquarion.world.content.AquaItem;
import arc.func.Cons;
import arc.graphics.Color;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.Planets;
import mindustry.ctype.UnlockableContent;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.BuildTurret;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.Env;

import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaLiquids.fumes;
import static aquarion.content.AquaLiquids.haze;
import static aquarion.content.AquaPlanets.*;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;


public class CoreBlocks {
    public static Block buzzSaw, mendPyre, mendPylon, cache, coreCuesta, overClockProjector,
            coreEscarpment, corePike, buildCairn, crate, deflectorWell, neoplasiaMass, OreSlurper, oreSlurperer, oresplurpererer, callus, thicBlob, enzyme;
    public static <T extends UnlockableContent> void overwrite(UnlockableContent target, Cons<T> setter) {
        setter.get((T) target);
    }
    public static void loadContent() {
        cache = new StorageBlock("cache") {{
            requirements(Category.effect, with(aluminum, 160, silicon, 150, ferricMatter, 300));
            itemCapacity = 900;
            coreMerge = true;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            squareSprite = false;
            size = 3;
            researchCostMultiplier = 0.02f;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            //drawer = new DrawMulti(new DrawBetterRegion("-shadow"){{layer = shadow;drawIcon = false;}}, new DrawDefault());
        }};
        crate = new StorageBlock("crate") {{
            requirements(Category.effect, with(cupronickel, 400, silicon, 1200));
            itemCapacity = 150;
            coreMerge = false;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
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
            itemCapacity = 8000;
            incinerateNonBuildable = true;

                    size = 4;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            unitCapModifier = 25;
            unitType = AquaUnitTypes.cull;
            alwaysUnlocked = true;
            hasItems = true;
            hasColor = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        buildCairn = new BuildTurret("build-cairn") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.effect, with(silicon, 120, copper, 50));
            size = 2;
            outlineRadius = 0;
            buildSpeed = 0.4f;
            range = 145;
            rotateSpeed = 0.85f;
            schematicPriority = 8;
            consumePower(1.5f);
        }};
        mendPyre = new RegenPylon("mend-pyre") {{
            requirements(Category.effect, with(lead, 55, silicon, 80));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            size = 1;
            schematicPriority = 7;
            consumePower(0.75f);
            consumeItem(Items.silicon).boost();
            range = 25;
            phaseRangeBoost = 3f;
            phaseBoost = 2;
            healPercent = 10f;
            squareSprite = false;
            reload = 280;
            alwaysUnlocked = true;
        }};
        mendPylon = new RegenPylon("mend-pylon") {{
            requirements(Category.effect, with(silicon, 60, aluminum, 40));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            size = 2;
            schematicPriority = 7;

            consumePower(0.125f);
            consumeLiquid(fumes, 0.05f);
            range = 35;
            healPercent = 14;
            squareSprite = false;
            reload = 240;
            phaseBoost = 0;
            phaseRangeBoost = 0;
            liquidCapacity = 60;
            researchCostMultiplier = 0;
        }};
        deflectorWell = new deflectorShield("deflector-well") {{
            requirements(Category.effect, with(polymer, 800, metaglass, 900, silicon, 1200));
            size = 4;
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
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            size = 3;
            consumePower(4);
            researchCostMultiplier = 0.02f;
            damage = 15;
            range = 180;
        }};
        neoplasiaMass = new GenericNeoplasiaBlock("neoplasia-mass"){{
            requirements(Category.effect, with(silicon, 1));
            base = this;
            emptyUpgrade = thicBlob;
            emptyUpgradeCost = 800;
            oreUpgradeItemCost = new ItemStack(crystal, 2);
            oreUpgrade = OreSlurper = new NeoplasiaproductionBlock("ore-slurper"){{
                requirements(Category.effect, with(silicon, 1));
                maxAmount = 1500;
                oreUpgradeCost = 1250;
                oreUpgrade = oreSlurperer = new NeoplasiaproductionBlock("ore-slurperer"){{
                    maxAmount = 2000;
                    requirements(Category.effect, with(silicon, 1));
                    burstDelay = 100;
                    burstLength = 15;
                    selfGrowRate = 1.5f;
                    oreUpgradeCost = 1500;
                    itemCapacity = 15;
                    oreUpgradeItemCost = new ItemStack(crystal, 10);
                    bursts = 3;
                    baseSize = 20;
                    oreUpgrade = oresplurpererer = new NeoplasiaproductionBlock("ore-slurpererer"){{
                        maxAmount = 2500;
                        burstDelay = 50;
                        requirements(Category.effect, with(silicon, 1));
                        burstLength = 15;
                        itemCapacity = 30;
                        selfGrowRate = 2.6f;;
                        bursts = 5;
                        oreUpgrade = null;
                        baseSize = 32;
                    }};
                }};
                burstDelay = 300;
                burstLength = 10;
                baseSize = 16;
            }};
            damageUpgrade = callus = new DefensiveNeoplasiaBlock("callus"){{
                requirements(Category.effect, with(silicon, 1));
                baseSize = 16;
            }};
            shouldEmptyUpgrade = true;
            empty2Upgrade = enzyme = new NeoplasiaproductionBlock("enzyme"){{
                requirements(Category.effect, with(silicon, 1));
                baseSize = 16;
                maxAmount = 1500;
                burstLength = 15;
                shouldCraft = true;
                base = neoplasiaMass;
                output = new ItemStack(crystal,1);
                craftCost = 20;
                oreGrowBonus = 0.01f;
                selfGrowRate = 0.01f;
                burstDelay = 50;
                shouldEmptyUpgrade = false;;
                damageUpgrade = callus;
            }};
            emptyUpgrade = thicBlob = new GenericNeoplasiaBlock("thic-neoplasia-blob"){{
                base = neoplasiaMass;
                baseSize = 16;
                maxAmount = 2500;
                burstLength = 15;
                itemCapacity = 10;
                oreGrowBonus = 0.12f;
                selfGrowRate = 0.12f;
                burstDelay = 25;
                shouldEmptyUpgrade = false;
                oreUpgrade = OreSlurper;
                damageUpgrade = callus;
                colFrom = Color.valueOf("cf683b");
                colTo = Color.valueOf("e2c451");
            }};
        }};

        //Holy Jank...
        overwrite(OreSlurper, (NeoplasiaproductionBlock r) ->{
            r.oreUpgrade = oreSlurperer;
            r.base = neoplasiaMass;

        });
        overwrite(oreSlurperer, (NeoplasiaproductionBlock r) ->{
            r.oreUpgrade = oresplurpererer;
            r.base = neoplasiaMass;
        });
        overwrite(oresplurpererer, (NeoplasiaproductionBlock r) ->{
            r.oreUpgrade = null;
            r.base = thicBlob;
        });
        overwrite(callus, (DefensiveNeoplasiaBlock r) ->{
            r.oreUpgrade = null;
            //DamageUpgrade works in reverse here.
            r.damageUpgrade = neoplasiaMass;
        });
        overwrite(neoplasiaMass, (GenericNeoplasiaBlock r) ->{
            r.base = neoplasiaMass;
        });
        overClockProjector = new OverclockProjector("overclock-projector"){{
            requirements(Category.effect, with(silicon, 150, copper, 900, polymer, 100, metaglass, 200, ferricMatter, 250));
            size = 2;
            consumePower(6);
            consumeLiquid(Liquids.water, 4);
            range = 150;
            hasBoost = false;
            speedBoost = 2.0f;
            squareSprite = false;
        }};
    }
}
