package aquarion.blocks;

import aquarion.world.blocks.distribution.PayloadTram;
import mindustry.type.Category;
import mindustry.world.Block;

import static aquarion.AquaItems.bauxite;
import static aquarion.AquaItems.sodium;
import static mindustry.Vars.tilesize;
import static mindustry.content.Items.lead;
import static mindustry.content.Items.metaglass;
import static mindustry.type.ItemStack.with;

public class AquaPayload {
    public static Block payloadTram;
    public static void loadContent() {
        payloadTram = new PayloadTram("payload-tram"){{
            requirements(Category.units, with(lead, 120, metaglass, 350, sodium, 90, bauxite, 90));
            size = 3;
            range = tilesize  * 20;
            speed = 30;
            distMultiplier = 0.9f;
        }};
    }
}
