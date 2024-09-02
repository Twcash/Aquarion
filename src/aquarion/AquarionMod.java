package aquarion;

import aquarion.planets.AquaPlanets;
import aquarion.units.AquaUnitTypes;
import arc.Core;
import arc.util.Log;
import mindustry.Vars;
import mindustry.mod.*;
import aquarion.blocks.*;

public class AquarionMod extends Mod {
//TODO proper load order DO NOT DO UNTIL ALL CONTENT IS PORTED
    //crafters
    //defense
    //effect
    //transport
    //liquidBlocks
    //power
    //production (drills and other)
    //cores
    //turrets
    //unit blocks
    //logic
    @Override
    public void loadContent() {
        AquaAttributes.load();
        AquaEffect.loadContent();
        AquaLiquid.loadContent();
        AquaItems.loadContent();
        AquaDistribution.loadContent();
        AquaPayload.loadContent();
        AquaLiquids.loadContent();
        AquaEnv.loadContent();
        AquaDefense.loadContent();
        AquaUnitTypes.loadContent();
        AquaCore.loadContent();
        AquaCrafters.loadContent();
        AquaPlanets.loadContent();
        AquaTurrets.loadContent();
        AquaPower.loadContent();
    }
}
