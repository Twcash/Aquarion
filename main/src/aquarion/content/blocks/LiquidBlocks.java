package aquarion.content.blocks;

import aquarion.world.blocks.distribution.*;
import mindustry.content.Liquids;
import mindustry.content.Planets;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.production.Pump;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaPlanets.*;
import static mindustry.content.Items.silicon;
import static mindustry.type.ItemStack.with;


public class LiquidBlocks {
    public static Block siphonUnderflow, pipeTank, pipeJunction, liquidVessel, siphonReservoir, liquidReservoir, pipe, pipeBridge, electrumPump, pulseSiphonBridge, pulseSiphon, siphonBridge, siphonJunction, siphonRouter, siphon;

    public static void loadContent() {
        siphonBridge = new ModifiedLiquidBridge("siphon-bridge") {{
            requirements(Category.liquid, with(silicon, 35));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            fadeIn = false;
            range = 4;
            willMelt = false;
            hasPower = false;
            health = 110;
            liquidCapacity = 80;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        pulseSiphonBridge = new ModifiedLiquidBridge("pulse-siphon-bridge") {{
            //long boi. Prolly unbalanced asf but I'm working on it
            requirements(Category.liquid, with(manganese, 45));
            willMelt = true;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            range = 8;
            hasPower = false;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        siphonUnderflow = new LiquidUnderflow("siphon-underflow") {{
            requirements(Category.liquid, with(silicon, 70));
            invert = true;
            health = 110;
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
        siphonRouter = new ModifiedLiquidRouter("siphon-router") {{
            requirements(Category.liquid, with(silicon, 30));
            envEnabled |= Env.terrestrial | Env.underwater;
            health = 120;
            liquidCapacity = 80;
            envDisabled = Env.none;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            alwaysUnlocked = true;
        }};
        siphonJunction = new ModifiedLiquidJunction("siphon-junction") {{
            requirements(Category.liquid, with(silicon, 70));
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            health = 110;
            ((ModifiedConduit) siphon).junctionReplacement = this;
        }};
        Liquids.oil.explosiveness = 0.7f;
        pipeTank = new ModifiedLiquidRouter("siphon-tank") {{
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
        siphonReservoir = new ModifiedLiquidRouter("siphon-reservoir") {{
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
        pipe = new Pipe("pipe") {{
            requirements(Category.liquid, with(ferrosilicon, 2));
            liquidPressure = 10000;
            liquidCapacity = 50;
        }};
    }
}
