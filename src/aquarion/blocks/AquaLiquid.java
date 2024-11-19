package aquarion.blocks;

import aquarion.AquaLiquids;
import aquarion.world.blocks.distribution.Pipe;
import aquarion.world.graphics.DrawPump;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.DirectionLiquidBridge;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidBridge;
import mindustry.world.blocks.liquid.LiquidJunction;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.production.Pump;
import mindustry.world.draw.*;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static mindustry.content.Items.*;
import static mindustry.type.ItemStack.with;


public class AquaLiquid {
    public static Block pipeJunction, liquidVessel, liquidReservoir, pipe, pipeBridge, electrumPump, pulseSiphonBridge, pulseSiphon, siphonBridge, siphonDistributor, siphonJunction, siphonRouter, siphon, ThermalPump;

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
        siphon = new Conduit("siphon") {{
            requirements(Category.liquid, with(lead, 2, bauxite, 1));
            junctionReplacement = siphonJunction;
            bridgeReplacement = pulseSiphonBridge;
            liquidCapacity = 20;
            leaks = false;

            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        pulseSiphon = new Conduit("pulse-siphon") {{
            requirements(Category.liquid, with(lead, 3, manganese, 1));
            junctionReplacement = siphonJunction;
            bridgeReplacement = pulseSiphonBridge;
            liquidCapacity = 20;
            leaks = false;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        siphonRouter = new LiquidRouter("siphon-router"){{
            requirements(Category.liquid, with(lead, 15, bauxite, 5));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        siphonJunction = new LiquidJunction("siphon-junction"){{
            requirements(Category.liquid, with(bauxite, 3, lead, 10));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        siphonBridge = new LiquidBridge("siphon-bridge"){{
            requirements(Category.liquid, with(lead, 20, bauxite, 20));
            fadeIn = false;
            range = 3;
            hasPower = false;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        pulseSiphonBridge = new LiquidBridge("pulse-siphon-bridge"){{
            requirements(Category.liquid, with(lead, 35, manganese, 15));
            range = 5;
            hasPower = false;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
        }};
        siphonDistributor = new LiquidRouter("siphon-distributor"){{
            requirements(Category.liquid, with(metaglass, 40, lead, 20, bauxite, 40));
            liquidPadding = 3;
            size = 2;
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