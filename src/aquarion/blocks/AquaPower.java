package aquarion.blocks;

import aquarion.world.blocks.power.PowerRelay;
import arc.graphics.Color;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static mindustry.content.Items.lead;
import static mindustry.type.ItemStack.with;

public class AquaPower {
    public static Block Relay;
    public static void loadContent(){
    Relay = new PowerRelay("relay"){{
        requirements(Category.power, with(lead, 15, bauxite, 5));
        laserColor1 = Color.valueOf("ffffff90");
        laserColor2 = Color.valueOf("7d4f9d10");
        customShadow = true;
        consumesPower = outputsPower = true;
        range = 5;
        consumePowerBuffered(1500f);
        consumePower(1/60f);
        envEnabled |= Env.terrestrial | Env.underwater;
        envDisabled |= Env.spores | Env.scorching;
    }};
    }
}