package aquarion;

import aquarion.planets.AquaLoadouts;
import aquarion.planets.AquaPlanets;
import aquarion.planets.AquaSectorPresets;
import aquarion.planets.TantrosTechTree;
import aquarion.tools.IconLoader;
import aquarion.units.AquaUnitTypes;
import aquarion.units.AquaWrecks;
import aquarion.world.AquaTeams;
import aquarion.world.OverDatabaseDialog;
import aquarion.world.graphics.AquaCacheLayers;
import aquarion.world.graphics.AquaMenuRenderer;
import aquarion.world.graphics.AquaShaders;
import aquarion.world.graphics.AquaWeather;
import arc.assets.Loadable;
import arc.util.Log;
import arc.util.Reflect;
import mindustry.Vars;
import aquarion.blocks.*;
import mindustry.ui.fragments.MenuFragment;

import java.lang.reflect.Field;

public class AquarionMod implements Loadable {

    public static void loadContent() {
        //stuff that needs to be loaded first
        // EXPERIMENTAL
        AquaLiquids.loadContent();
        AquaSounds.load();
        AquaTeams.load();
        AquaItems.load();
        AquaAttributes.load();
        AquaEffect.loadContent();
        AquaStatuses.load();
        AquaWeather.load();
        //actual content needs items liquids FX ect
        AquaEnv.loadContent();
        AquaPower.loadContent();
        AquaLiquid.loadContent();
        AquaPayload.loadContent();
        AquaDefense.loadContent();
        AquaTurrets.loadContent();
        AquaCrafters.loadContent();
        AquaUnitFactories.loadContent();
        //units and cores, keep these after blocks
        AquaWrecks.loadContent();
        AquaUnitTypes.loadContent();
        AquaCore.loadContent();
        //distribution blocks need to be loaded here bc of unittypes
        AquaDistribution.loadContent();
        //keep these at the back
        AquaLoadouts.load();
        AquaPlanets.loadContent();
        AquaSectorPresets.load();
        TantrosTechTree.load();
    }

    public static AquaMenuRenderer getMenuRenderer() {
        try{
            return Reflect.get(MenuFragment.class, Vars.ui.menufrag, "renderer");
        }catch(Exception ex){
            Log.err("Failed to return renderer", ex);
            return new AquaMenuRenderer();
        }
    }
    public static void init() {
        IconLoader.loadIcons();
        if (!Vars.headless) {
            AquaShaders.load();
            AquaCacheLayers.load();
        }
        //try {
            //Field databaseField = Vars.ui.getClass().getDeclaredField("database");
            //databaseField.setAccessible(true);
            //databaseField.set(Vars.ui, new OverDatabaseDialog());
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }
}
