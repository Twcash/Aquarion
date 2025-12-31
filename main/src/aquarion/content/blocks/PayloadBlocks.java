package aquarion.content.blocks;

import aquarion.world.blocks.payload.PayloadTram;
import mindustry.type.Category;
import mindustry.world.Block;

import static aquarion.content.AquaItems.zinc;
import static mindustry.content.Items.silicon;
import static mindustry.type.ItemStack.with;

public class PayloadBlocks {
    public static Block payloadTram, largePayloadTram, largePayloadSource;

    public static void loadContent() {
        payloadTram = new PayloadTram("tram"){{
            requirements(Category.defense, with(silicon, 24));
            size = 3;
        }};
    }
}
