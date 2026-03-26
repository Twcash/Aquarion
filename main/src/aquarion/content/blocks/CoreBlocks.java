package aquarion.content.blocks;

import aquarion.content.AquaItems;
import aquarion.content.AquaUnitTypes;
import aquarion.world.blocks.core.AquaCoreBlock;
import aquarion.world.blocks.core.Bomb;
import aquarion.world.blocks.core.InfomaticBlock;
import aquarion.world.blocks.core.OverclockProjector;
import aquarion.world.blocks.defense.ChainsawTurret;
import aquarion.world.blocks.defense.RegenPylon;
import aquarion.world.blocks.defense.deflectorShield;
import aquarion.world.blocks.neoplasia.DefensiveNeoplasiaBlock;
import aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock;
import aquarion.world.blocks.neoplasia.NeoplasiaGraph;
import aquarion.world.blocks.neoplasia.NeoplasiaproductionBlock;
import aquarion.world.content.AquaItem;
import arc.func.Cons;
import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.Planets;
import mindustry.ctype.UnlockableContent;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.BuildTurret;
import mindustry.world.blocks.environment.OverlayFloor;
import mindustry.world.blocks.logic.MessageBlock;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.meta.Env;

import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaLiquids.fumes;
import static aquarion.content.AquaLiquids.haze;
import static aquarion.content.AquaPlanets.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.nitrogen;
import static mindustry.type.ItemStack.with;


public class CoreBlocks {
    public static Block bomb, buzzSaw, reception, infomatic, mendPyre, mendSubstation, mendPylon, cache, coreCuesta, overClockProjector,
            coreEscarpment,petal, reconstruct,  corePike, buildCairn, constructionTower, crate, deflectorWell, neoplasiaMass, OreSlurper, oreSlurperer, oresplurpererer, callus, thicBlob, enzyme;

    public static <T extends UnlockableContent> void overwrite(UnlockableContent target, Cons<T> setter) {
        setter.get((T) target);
    }

    public static void loadContent() {
        infomatic = new InfomaticBlock("infomatic"){{
            requirements(Category.logic, with(silicon, 10));
            size = 1;
        }};
        reception = new CoreBlock("reception"){{
            size = 2;
            itemCapacity = 0;
            unitType = AquaUnitTypes.visitor;
            solid = false;
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
        constructionTower = new BuildTurret("construction-tower") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            requirements(Category.effect, with(silicon, 700, polymer, 250, copper, 2000));
            size = 3;
            outlineRadius = 0;
            squareSprite = false;
            consumeLiquid(nitrogen, 2);
            buildSpeed = 0.8f;
            range = 300;
            rotateSpeed = 0.85f;
            schematicPriority = 8;
            consumePower(4);
        }};
        reconstruct = new BuildTurret("reconstruct") {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
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
        mendSubstation = new RegenPylon("mend-substation") {{
            requirements(Category.effect, with(silicon, 900, ferricMatter,240, polymer, 500));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            size = 3;
            schematicPriority = 7;
            lightningDamage = 10;
            lightningReload = 90;
            consumePower(6);
            consumeLiquid(haze, 3f);
            range = 70;
            healPercent = 10;
            squareSprite = false;
            reload = 120;
            phaseBoost = 0;
            phaseRangeBoost = 0;
            lightningRange = 50;
            liquidCapacity = 700;
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
        neoplasiaMass = new GenericNeoplasiaBlock("neoplasia-mass") {{
            requirements(Category.effect, with(silicon, 1));
            base = this;
            itemCapacity = 5;
            emptyUpgrade = (GenericNeoplasiaBlock) thicBlob;
            emptyUpgradeCost = 800;
            oreUpgrade = (GenericNeoplasiaBlock) (OreSlurper = new NeoplasiaproductionBlock("ore-slurper") {{
                requirements(Category.effect, with(silicon, 1));
                maxAmount = 1500;
                itemCapacity = 20;
                cost = 500;
                itemCost = ItemStack.with(crystal, 1);
                oreUpgrade = (GenericNeoplasiaBlock) (oreSlurperer = new NeoplasiaproductionBlock("ore-slurperer") {{
                    maxAmount = 2000;
                    requirements(Category.effect, with(silicon, 1));
                    burstDelay = 100;
                    burstLength = 15;
                    selfGrowRate = 1.5f;
                    itemCapacity = 15;
                    itemCost = ItemStack.with(crystal, 10, pearl, 5);
                    bursts = 3;
                    cost = 1500;
                    baseSize = 20;
                    oreUpgrade = (GenericNeoplasiaBlock) (oresplurpererer = new NeoplasiaproductionBlock("ore-slurpererer") {{
                        maxAmount = 2500;
                        burstDelay = 50;
                        cost = 2500;
                        requirements(Category.effect, with(silicon, 1));
                        burstLength = 15;
                        itemCapacity = 30;
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
            emptyUpgrade = (GenericNeoplasiaBlock) (thicBlob = new GenericNeoplasiaBlock("thic-neoplasia-blob") {{
                base = (GenericNeoplasiaBlock) neoplasiaMass;
                baseSize = 16;
                maxAmount = 2500;
                burstLength = 15;
                itemCapacity = 10;
                oreGrowBonus = 0.12f;
                selfGrowRate = 0.12f;
                burstDelay = 25;
                shouldEmptyUpgrade = false;
                oreUpgrade = (GenericNeoplasiaBlock) OreSlurper;
                damageUpgrade = (GenericNeoplasiaBlock) callus;
                colFrom = Color.valueOf("cf683b");
                colTo = Color.valueOf("e2c451");
            }});
        }};
        enzyme = new NeoplasiaproductionBlock("enzyme") {{
            requirements(Category.effect, with(silicon, 1));
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
            NeoplasiaGraph.registerProducer(AquaItems.crystal, this);
        }};
        petal = new NeoplasiaproductionBlock("petal") {{
            requirements(Category.effect, with(silicon, 1));
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
            selfGrowRate = 0.01f;
            burstDelay = 50;
            cost = 200;
            craftTime = 240;
            shouldEmptyUpgrade = false;
            damageUpgrade = (GenericNeoplasiaBlock) callus;
            NeoplasiaGraph.registerProducer(AquaItems.pearl, this);
        }};
        //Holy Jank...
        overwrite(OreSlurper, (NeoplasiaproductionBlock r) -> {
            r.oreUpgrade = (GenericNeoplasiaBlock) oreSlurperer;
            r.base = (GenericNeoplasiaBlock) neoplasiaMass;
        });
        overwrite(oreSlurperer, (NeoplasiaproductionBlock r) -> {
            r.oreUpgrade = (GenericNeoplasiaBlock) oresplurpererer;
            r.base = (GenericNeoplasiaBlock) neoplasiaMass;
        });
        overwrite(oresplurpererer, (NeoplasiaproductionBlock r) -> {
            r.oreUpgrade = null;
            r.base = (GenericNeoplasiaBlock) thicBlob;
        });
        overwrite(callus, (DefensiveNeoplasiaBlock r) -> {
            r.oreUpgrade = null;
            //DamageUpgrade works in reverse here.
            r.damageUpgrade = (GenericNeoplasiaBlock) neoplasiaMass;
        });
        overwrite(neoplasiaMass, (GenericNeoplasiaBlock r) -> {
            r.base = (GenericNeoplasiaBlock) neoplasiaMass;
        });
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
    }
}
