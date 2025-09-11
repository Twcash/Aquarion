package aquarion.blocks;

import aquarion.world.blocks.distribution.*;
import mindustry.content.Liquids;
import mindustry.content.Planets;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.DirectionLiquidBridge;
import mindustry.world.blocks.liquid.LiquidJunction;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.production.Pump;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static aquarion.planets.AquaPlanets.*;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;


public class AquaLiquid {
    public static Block siphonOverflow, siphonUnderflow, pipeTank, pipeJunction, liquidVessel, siphonReservoir, liquidReservoir, pipe, pipeBridge, electrumPump, pulseSiphonBridge, pulseSiphon, siphonBridge, siphonJunction, siphonRouter, siphon;

    public static void loadContent() {
        siphonBridge = new ModifiedLiquidBridge("siphon-bridge"){{
            requirements(Category.liquid, with(silicon, 35));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            fadeIn = false;
            range = 4;
            willMelt = false;
            hasPower = false;
            liquidCapacity = 80;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        pulseSiphonBridge = new ModifiedLiquidBridge("pulse-siphon-bridge"){{
            //long boi. Prolly unbalanced asf but I'm working on it
            requirements(Category.liquid, with(manganese, 45));
            willMelt = true;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            range = 8;
            hasPower = false;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        siphonOverflow = new LiquidUnderflow("siphon-overflow"){{
            requirements(Category.liquid, with(silicon, 70));

        }};
        siphonUnderflow = new LiquidUnderflow("siphon-underflow"){{
            requirements(Category.liquid, with(silicon, 70));
            invert = true;
        }};
        siphon = new ModifiedConduit("siphon") {{
            requirements(Category.liquid, with(silicon, 3));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            willMelt = false;
            junctionReplacement = siphonJunction;
            bridgeReplacement = siphonBridge;
            liquidCapacity = 80;
            health = 110;
            leaks = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            alwaysUnlocked = true;
        }};
        pulseSiphon = new ModifiedConduit("pulse-siphon") {{
            //but what if siligone? SOLVEDDD!
            requirements(Category.liquid, with(manganese, 2));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            junctionReplacement = siphonJunction;
            bridgeReplacement = pulseSiphonBridge;
            liquidCapacity = 120;

            willMelt = false;
            leaks = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        siphonRouter = new ModifiedLiquidRouter("siphon-router"){{
            requirements(Category.liquid, with(silicon, 30));
            envEnabled |= Env.terrestrial | Env.underwater;
            liquidCapacity = 80;
            envDisabled = Env.none;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            alwaysUnlocked = true;
        }};
        siphonJunction = new ModifiedLiquidJunction("siphon-junction"){{
            requirements(Category.liquid, with(silicon, 70));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            ((ModifiedConduit)siphon).junctionReplacement = this;
        }};
        Liquids.oil.explosiveness = 0.7f;
        pipeTank = new ModifiedLiquidRouter("siphon-tank"){{
            requirements(Category.liquid, with(silicon, 150, ferricMatter, 50));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            liquidPadding = 3;
            squareSprite = false;
            liquidCapacity = 4000;
            size = 3;
            willMelt = false;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        siphonReservoir = new ModifiedLiquidRouter("siphon-reservoir"){{
            requirements(Category.liquid, with(aluminum, 50, manganese, 250));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            liquidPadding = 3;
            squareSprite = false;
            liquidCapacity = 8500;
            size = 4;
            willMelt = true;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        electrumPump = new Pump("electrum-pump"){{
            requirements(Category.liquid, with(electrum, 35));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            pumpAmount = 0.1f;
            squareSprite = false;
            liquidCapacity = 60f;
            hasPower = true;
            size = 2;
        }};
        pipe = new Pipe("pipe"){{
            requirements(Category.liquid, with(ferrosilicon, 2));
            buildVisibility = BuildVisibility.sandboxOnly;
            liquidPressure =  10000;
            liquidCapacity = 50;
        }};
        pipeJunction = new LiquidJunction("pipe-junction"){{
            requirements(Category.liquid, with(electrum, 5, lead, 12));
            buildVisibility = BuildVisibility.sandboxOnly;

        }};
        pipeBridge = new DirectionLiquidBridge("pipe-bridge"){{
            requirements(Category.liquid, with(electrum, 15, lead, 20));
            buildVisibility = BuildVisibility.sandboxOnly;

            range = 5;
        }};
        liquidVessel = new LiquidRouter("liquid-vessel"){{
            requirements(Category.liquid, with(electrum, 25, lead, 30));
            buildVisibility = BuildVisibility.sandboxOnly;

            size = 2;
            liquidCapacity = 700f;
            solid = true;
        }};
        liquidReservoir = new LiquidRouter("liquid-reservoir"){{
            requirements(Category.liquid, with(electrum, 110, lead, 50, silver, 45));
            buildVisibility = BuildVisibility.sandboxOnly;
            liquidCapacity = 2000f;
            size = 3;
            solid = true;
        }};
    }
}
