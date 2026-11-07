package aquarion.world.blocks.production;

import aquarion.content.AquaItems;
import aquarion.world.items.AquaItemEntity;
import aquarion.world.type.AquaGenericCrafter;
import arc.Core;
import arc.graphics.g2d.TextureRegion;
import mindustry.type.ItemStack;
import mindustry.world.draw.DrawDefault;

/** Enriches uranium while marking each produced unit with its spoil state. */
public class LaserEnricher extends AquaGenericCrafter {
    public LaserEnricher(String name) {
        super(name);
        size = 3;
        itemCapacity = 20;
        craftTime = 60f * 5f;
        outputItem = new ItemStack(AquaItems.uraniumEnriched, 10);
        consumeItems(new ItemStack(AquaItems.uranium, 10));
        consumePower(8f);
        drawer = new DrawDefault();
    }

    @Override
    public void init() {
        super.init();
        buildType = LaserEnricherBuild::new;
    }

    @Override
    public void load() {
        super.load();
        // Reuse the existing arc furnace art until this block has dedicated artwork.
        region = Core.atlas.find("arc-furnace");
        fullRegion = region;
        drawer.load(this);
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{region};
    }

    public class LaserEnricherBuild extends AquaGenericCrafterBuild {
        @Override
        public void craft() {
            consume();
            for(int i = 0; i < 10; i++) offload(AquaItems.uraniumEnriched);
            if(wasVisible) craftEffect.at(x, y);
            progress %= 1f;
        }
    }
}
