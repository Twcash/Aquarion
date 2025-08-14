package aquarion;

import aquarion.planets.AquaLoadouts;
import aquarion.planets.AquaPlanets;
import aquarion.planets.AquaSectorPresets;
import aquarion.planets.TantrosTechTree;
import aquarion.units.AquaUnitTypes;
import aquarion.units.AquaWrecks;
import aquarion.units.ProspectorUnitTypes;
import aquarion.world.graphics.AquaMenuRenderer;
import aquarion.world.graphics.AquaWeather;
import aquarion.world.graphics.MenuReplacer;
import arc.Events;
import arc.assets.Loadable;
import arc.util.Log;
import arc.util.Reflect;
import arc.util.Timer;
import aquarion.blocks.*;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.ui.fragments.MenuFragment;

import static mindustry.Vars.ui;

public class AquarionMod  implements Loadable{

    public static void loadContent() {
        //stuff that needs to be loaded first
        AquaStatuses.load();
        AquaLiquids.loadContent();
        AquaSounds.load();
        AquaItems.load();
        AquaAttributes.load();
        AquaEffect.loadContent();
        AquaWeather.load();
        //actual content needs items liquids FX ect
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
        AquaUnitFactories.loadContent();
        AquaCore.loadContent();
        //distribution blocks need to be loaded here bc of unittypes
        AquaDistribution.loadContent();
        //keep these at the back
        AquaLoadouts.load();



        //Other faction stuff
        ProspectorUnitTypes.loadContent();

        MenuReplacer.replaceMenu(ui.menufrag);
        AquaPlanets.loadContent();
        AquaSectorPresets.load();
        TantrosTechTree.load();

    }

    public static AquaMenuRenderer getMenuRenderer() {
        try{
            return Reflect.get(MenuFragment.class, ui.menufrag, "renderer");
        }catch(Exception ex){
            Log.err("Failed to return renderer", ex);
            return new AquaMenuRenderer();
        }

    }

}
