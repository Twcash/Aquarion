package aquarion.world.blocks;

import aquarion.content.AquaLiquids;
import mindustry.content.Blocks;
import mindustry.world.blocks.production.GenericCrafter;

public class SteamVentExtractor extends GenericCrafter {

    public SteamVentExtractor(String name) {
        super(name);
        this.outputsLiquid = true;
        this.liquidCapacity = 20f;
    }

    public class SteamVentExtractorBuild extends GenericCrafterBuild {
        @Override
        public void updateTile() {
            if (tile.floor() == Blocks.steamgeyser) {
                this.handleLiquid(this, AquaLiquids.haze, 0.05f);
            }
            
            super.updateTile();
        }
    }
}
