package aquarion;

import aquarion.annotations.Annotations;
import aquarion.blocks.*;
import aquarion.planets.AquaLoadouts;
import aquarion.planets.AquaPlanets;
import aquarion.planets.AquaSectorPresets;
import aquarion.planets.TantrosTechTree;
import aquarion.units.AquaUnitTypes;
import aquarion.units.AquaWrecks;
import aquarion.units.ProspectorUnitTypes;
import aquarion.world.content.AquaHints;
import aquarion.world.graphics.AquaMenuRenderer;
import aquarion.world.graphics.AquaWeather;
import aquarion.world.graphics.MenuReplacer;
import arc.Events;
import arc.assets.Loadable;
import arc.util.Log;
import arc.util.Reflect;
import mindustry.game.EventType;
import mindustry.ui.fragments.MenuFragment;

import static mindustry.Vars.ui;

@Annotations.LoadRegs("error")// Need this temporarily, so the class gets generated.
@Annotations.EnsureLoad
public class AquarionMod  implements Loadable{
    public static AquaHints hints = new AquaHints();
    public static void loadContent() {
        Events.on(EventType.ClientLoadEvent.class, e -> {
            clientLoaded();
        });

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

        //MenuReplacer.replaceMenu(ui.menufrag);
        AquaPlanets.loadContent();
        AquaSectorPresets.load();
        TantrosTechTree.load();


    }
    public static void clientLoaded(){
        hints.load();
    }
//    public static AquaMenuRenderer getMenuRenderer() {
//        try {
//            return Reflect.get(MenuFragment.class, ui.menufrag, "renderer");
//        } catch (Exception ex) {
//            Log.err("Failed to return renderer", ex);
//            return new AquaMenuRenderer();
//        }
//
//    }
}