package aquarion.content;

import aquarion.world.content.LiquidReaction;
import aquarion.world.content.LiquidReactions;
import mindustry.content.Fx;

import static aquarion.content.AquaLiquids.*;
import static mindustry.content.Liquids.*;

public class AquaReactions {
    public static void load() {
        LiquidReactions.register(new LiquidReaction(water, magma, haze) {{
            aAmount = 1f;
            bAmount = 1f;
            resultAmount = 0.8f;
            rate = 0.9f;
            effect = Fx.steam;
            damage = 0.1f;
            effectChance = 0.12f;
        }});
        LiquidReactions.register(new LiquidReaction(water, slag, haze) {{
            aAmount = 1f;
            bAmount = 1f;
            resultAmount = 0.8f;
            rate = 0.9f;
            effect = Fx.steam;
            damage = 0.1f;
            effectChance = 0.12f;
        }});
        LiquidReactions.register(new LiquidReaction(clearwater, magma, haze) {{
            aAmount = 1f;
            bAmount = 1f;
            resultAmount = 0.9f;
            rate = 0.9f;
            effect = Fx.steam;
            damage = 0.1f;
            effectChance = 0.12f;
        }});
        LiquidReactions.register(new LiquidReaction(clearwater, slag, haze) {{
            aAmount = 1f;
            bAmount = 1f;
            resultAmount = 0.9f;
            rate = 0.9f;
            damage = 0.1f;
            effect = Fx.steam;
            effectChance = 0.12f;
        }});
        LiquidReactions.register(new LiquidReaction(water, fluorine, haze) {{
            aAmount = 1f;
            bAmount = 1f;
            resultAmount = 2f;
            rate = 1f;
            damage = 1.5f;
            effect = Fx.steam;
            effectChance = 0.15f;
        }});
        LiquidReactions.register(new LiquidReaction(muriaticAcid, hydroxide, halideWater) {{
            aAmount = 1f;
            bAmount = 1f;
            resultAmount = 1.5f;
            rate = 1f;
            effect = Fx.bubble;

            effectChance = 0.1f;
        }});
        LiquidReactions.register(new LiquidReaction(vitriol, hydroxide, halideWater) {{
            aAmount = 1f;
            bAmount = 1f;
            resultAmount = 2f;
            rate = 1f;
            effect = Fx.bubble;
            effectChance = 0.1f;
        }});
        LiquidReactions.register(new LiquidReaction(brine, chlorine, hydroxide) {{
            aAmount = 1f;
            bAmount = 1f;
            resultAmount = 0.8f;
            rate = 0.5f;
            damage = 0.3f;
            effect = Fx.steamCoolSmoke;
            effectChance = 0.08f;
        }});
    }
}
