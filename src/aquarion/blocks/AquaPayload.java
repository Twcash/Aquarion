package aquarion.blocks;

import aquarion.world.blocks.distribution.PayloadTram;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.PayloadSource;
import mindustry.world.meta.BuildVisibility;

import static aquarion.AquaItems.bauxite;
import static aquarion.AquaItems.sodium;
import static mindustry.Vars.tilesize;
import static mindustry.content.Items.lead;
import static mindustry.content.Items.metaglass;
import static mindustry.type.ItemStack.with;

public class AquaPayload {
    public static Block payloadTram, largePayloadTram, largePayloadSource;

    public static void loadContent() {
        payloadTram = new PayloadTram("payload-tram"){{
            requirements(Category.units, with(lead, 70, bauxite, 50));
            size = 3;
            range = tilesize  * 25;
            speed = 15;
            thicc = 8; // OH YESSS
            distMultiplier = 1.1f;
            maxPayloadSize = 2.75f;
        }};
        largePayloadTram = new PayloadTram("large-payload-tram"){{
            requirements(Category.units, with(lead, 500, metaglass, 350, sodium, 90, bauxite, 90));
            size = 5;
            range = tilesize * 60;
            thicc = 17.5f;
            clipSize = 1000;
            distMultiplier = 0.9f;
            maxPayloadSize = 4.5f;
        }};
        largePayloadSource = new PayloadSource("large-payload-source"){{
            requirements(Category.units, BuildVisibility.sandboxOnly, with());
            size = 10;
            alwaysUnlocked = true;
        }};
    }
}
