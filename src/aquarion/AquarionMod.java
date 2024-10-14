package aquarion;

import aquarion.planets.AquaPlanets;
import aquarion.units.AquaUnitTypes;
import aquarion.units.AquaWrecks;
import aquarion.world.AquaTeams;
import aquarion.world.graphics.AquaCacheLayers;
import aquarion.world.graphics.AquaShaders;
import arc.Core;
import arc.Events;
import mindustry.game.EventType;
import mindustry.mod.*;
import aquarion.blocks.*;

import static mindustry.Vars.headless;

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
        if (!headless) {
            AquaShaders.init();
            AquaCacheLayers.init();
        }
        AquaTeams.load();
        AquaAttributes.load();
        AquaEffect.loadContent();
        AquaItems.loadContent();
        AquaDistribution.loadContent();
        AquaPayload.loadContent();
        AquaLiquids.loadContent();
        AquaEnv.loadContent();
        AquaDefense.loadContent();
        AquaWrecks.loadContent();
        AquaUnitTypes.loadContent();
        AquaCore.loadContent();
        AquaCrafters.loadContent();
        AquaPlanets.loadContent();
        AquaTurrets.loadContent();
         AquaPower.loadContent();
        AquaLiquid.loadContent();
    }
}
