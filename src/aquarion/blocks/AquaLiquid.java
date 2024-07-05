package aquarion.blocks;

import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidBridge;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.blocks.defense.RegenProjector;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.liquid.LiquidJunction;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.type.ItemStack.with;


public class AquaLiquid {
    public static Block pulseSiphonBridge, pulseSiphon, siphonBridge, siphonDistributor, siphonJunction, siphonRouter, siphon;

    public static void loadContent() {

        siphon = new Conduit("siphon") {{
            requirements(Category.liquid, with(lead, 2, bauxite, 1));
            junctionReplacement = siphonJunction;
            bridgeReplacement = pulseSiphonBridge;
            liquidCapacity = 20;
            liquidPressure = 0.8F;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled |= Env.spores | Env.scorching;
        }};
        pulseSiphon = new Conduit("pulse-siphon") {{
            requirements(Category.liquid, with(lead, 3, manganese, 1));
            junctionReplacement = siphonJunction;
            bridgeReplacement = pulseSiphonBridge;
            liquidCapacity = 20;
            liquidPressure = 1.5F;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled |= Env.spores | Env.scorching;
        }};
        siphonRouter = new LiquidRouter("siphon-router"){{
            requirements(Category.liquid, with(lead, 15, bauxite, 5));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled |= Env.spores | Env.scorching;
        }};
        siphonJunction = new LiquidJunction("siphon-junction"){{
            requirements(Category.liquid, with(bauxite, 3, lead, 10));
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled |= Env.spores | Env.scorching;
        }};
        siphonBridge = new LiquidBridge("siphon-bridge"){{
            requirements(Category.liquid, with(lead, 20, bauxite, 20));
            fadeIn = false;
            range = 3;
            hasPower = false;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled |= Env.spores | Env.scorching;
        }};
        pulseSiphonBridge = new LiquidBridge("pulse-siphon-bridge"){{
            requirements(Category.liquid, with(lead, 35, manganese, 15));
            range = 5;
            hasPower = false;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled |= Env.spores | Env.scorching;
        }};
        siphonDistributor = new LiquidRouter("siphon-distributor"){{
            requirements(Category.liquid, with(metaglass, 40, lead, 20, bauxite, 40));
            liquidPadding = 3;
            size = 2;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled |= Env.spores | Env.scorching;
        }};
    }

}