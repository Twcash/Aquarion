package aquarion;

import mindustry.mod.*;
import aquarion.blocks.*;

public class AquarionMod extends Mod {

    @Override
    public void loadContent() {
        aquarionItems.loadContent();
        aquarionLiquids.loadContent();
        envBlocks.loadContent();
        defenseBlocks.loadContent();
        coreBlocks.loadContent();
    }
}
