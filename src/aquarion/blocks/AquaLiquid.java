package aquarion.blocks;

import aquarion.AquaLiquids;
import aquarion.world.blocks.distribution.*;
import aquarion.world.graphics.DrawPump;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.DirectionLiquidBridge;
import mindustry.world.blocks.liquid.LiquidJunction;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.production.Pump;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawLiquidTile;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;


public class AquaLiquid {
    public static Block pipeTank, pipeJunction, liquidVessel, liquidReservoir, pipe, pipeBridge, electrumPump, pulseSiphonBridge, pulseSiphon, siphonBridge, siphonDistributor, siphonJunction, siphonRouter, siphon, ThermalPump;

    public static void loadContent() {
        ThermalPump = new Pump("thermal-pump"){{
            requirements(Category.liquid, with(lead, 25, bauxite, 10, ceramic, 35, nickel, 60));
            size = 2;
            squareSprite = false;
            liquidCapacity = 35f * 5;
            pumpAmount = 0.2f;
            hasPower = true;
            consumePower(30/60f);
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(AquaLiquids.brine, 2f), new DrawDefault(), new DrawPump());
        }};
        siphon = new ModifiedConduit("siphon") {{
            requirements(Category.liquid, with(silicon, 3));
            junctionReplacement = siphonJunction;
            bridgeReplacement = pulseSiphonBridge;
            liquidCapacity = 40;
            leaks = false;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            alwaysUnlocked = true;
        }};
        pulseSiphon = new ModifiedConduit("pulse-siphon") {{
            requirements(Category.liquid, with(lead, 3, manganese, 1));
            junctionReplacement = siphonJunction;
            bridgeReplacement = pulseSiphonBridge;
            liquidCapacity = 80;
            leaks = false;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        siphonRouter = new ModifiedLiquidRouter("siphon-router"){{
            requirements(Category.liquid, with(silicon, 30));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            alwaysUnlocked = true;
        }};
        siphonJunction = new ModifiedLiquidJunction("siphon-junction"){{
            requirements(Category.liquid, with(bauxite, 3, lead, 10));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            ((ModifiedConduit)siphon).junctionReplacement = this;
        }};
        siphonBridge = new ModifiedLiquidBridge("siphon-bridge"){{
            requirements(Category.liquid, with(silicon, 35));
            fadeIn = false;
            range = 4;
            hasPower = false;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            ((ModifiedConduit)siphon).bridgeReplacement = this;
        }};
        pulseSiphonBridge = new ModifiedLiquidBridge("pulse-siphon-bridge"){{
            requirements(Category.liquid, with(lead, 35, manganese, 15));
            range = 8;
            hasPower = false;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        pipeTank = new ModifiedLiquidRouter("siphon-tank"){{
            requirements(Category.liquid, with(silicon, 150, ferricMatter, 50));
            liquidPadding = 3;
            squareSprite = false;
            size = 3;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        electrumPump = new Pump("electrum-pump"){{
            requirements(Category.liquid, with(electrum, 35));
            pumpAmount = 0.1f;
            squareSprite = false;
            liquidCapacity = 60f;
            hasPower = true;
            size = 2;
        }};
        pipe = new Pipe("pipe"){{
            requirements(Category.liquid, with(electrum, 1, lead, 1));
            liquidPressure =  10000;
            liquidCapacity = 50;
        }};
        pipeJunction = new LiquidJunction("pipe-junction"){{
            requirements(Category.liquid, with(electrum, 5, lead, 12));
        }};
        pipeBridge = new DirectionLiquidBridge("pipe-bridge"){{
            requirements(Category.liquid, with(electrum, 15, lead, 20));
            range = 5;
        }};
        liquidVessel = new LiquidRouter("liquid-vessel"){{
            requirements(Category.liquid, with(electrum, 25, lead, 30));
            size = 2;
            liquidCapacity = 700f;
            solid = true;
        }};
        liquidReservoir = new LiquidRouter("liquid-reservoir"){{
            requirements(Category.liquid, with(electrum, 110, lead, 50, silver, 45));
            liquidCapacity = 2000f;
            size = 3;
            solid = true;
        }};
    }

}