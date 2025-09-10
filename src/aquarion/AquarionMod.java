package aquarion;

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
import aquarion.world.graphics.Renderer;
import arc.Core;
import arc.Events;
import arc.assets.Loadable;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.Log;
import arc.util.Reflect;
import arc.util.Timer;
import aquarion.blocks.*;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.g3d.PlanetGrid;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.ui.fragments.MenuFragment;
import mindustry.world.Block;
import mindustry.world.Tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static mindustry.Vars.ui;


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

        MenuReplacer.replaceMenu(ui.menufrag);
        AquaPlanets.loadContent();
        AquaSectorPresets.load();
        TantrosTechTree.load();


    }
    public static void clientLoaded(){
        hints.load();
    }
    public static AquaMenuRenderer getMenuRenderer() {
        try {
            return Reflect.get(MenuFragment.class, ui.menufrag, "renderer");
        } catch (Exception ex) {
            Log.err("Failed to return renderer", ex);
            return new AquaMenuRenderer();
        }

    }
}