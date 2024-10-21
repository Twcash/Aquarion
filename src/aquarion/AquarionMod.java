package aquarion;

import aquarion.gen.EntityRegistry;
import aquarion.planets.AquaLoadouts;
import aquarion.planets.AquaPlanets;
import aquarion.planets.AquaSectorPresets;
import aquarion.planets.TantrosTechTree;
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

    @Override
    public void loadContent() {
        if (!headless) {
            AquaShaders.init();
            AquaCacheLayers.init();
        }
        //stuff that needs to be loaded first
        EntityRegistry.register();
        AquaLiquids.loadContent();
        AquaSounds.load();
        AquaTeams.load();
        AquaItems.load();
        AquaAttributes.load();
        AquaEffect.loadContent();
        // actual content needs items liquids FX ect
        AquaEnv.loadContent();
        AquaPower.loadContent();
        AquaLiquid.loadContent();
        AquaPayload.loadContent();
        AquaDefense.loadContent();
        AquaTurrets.loadContent();
        AquaCrafters.loadContent();
        //units and cores, keep these after blocks
        AquaWrecks.loadContent();
        AquaUnitTypes.loadContent();
        AquaCore.loadContent();
        // distribution blocks need to be loaded here bc of unittypes
        AquaDistribution.loadContent();
        //keep these at the back
        AquaLoadouts.load();
        AquaPlanets.loadContent();
        AquaSectorPresets.load();
        TantrosTechTree.load();
    }
}
