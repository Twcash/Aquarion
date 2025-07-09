package aquarion;

import aquarion.planets.AquaLoadouts;
import aquarion.planets.AquaPlanets;
import aquarion.planets.AquaSectorPresets;
import aquarion.planets.TantrosTechTree;
import aquarion.ui.AquaHud;
import aquarion.units.AquaUnitTypes;
import aquarion.units.AquaWrecks;
import aquarion.units.ProspectorUnitTypes;
import aquarion.world.AI.FishAI;
import aquarion.world.AI.FishAIDangerMap;
import aquarion.world.AI.ProspectorBaseBuilderAI;
import aquarion.world.AquaTeams;
import aquarion.world.Uti.AquaStates;
import aquarion.world.graphics.AquaMenuRenderer;
import aquarion.world.graphics.AquaWeather;
import aquarion.world.graphics.MenuReplacer;
import arc.ApplicationCore;
import arc.ApplicationListener;
import arc.Events;
import arc.assets.Loadable;
import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.Reflect;
import arc.util.Timer;
import mindustry.Vars;
import aquarion.blocks.*;
import mindustry.game.EventType;
import mindustry.game.Teams;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.ui.fragments.MenuFragment;
import mindustry.ui.fragments.PlacementFragment;

import java.lang.reflect.Field;

import static arc.Core.assets;
import static mindustry.Vars.state;
import static mindustry.Vars.ui;

public class AquarionMod  implements Loadable{

    public static void loadContent() {
        //stuff that needs to be loaded first
        AquaStatuses.load();
        AquaLiquids.loadContent();
        AquaSounds.load();
        AquaTeams.load();
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
        ProspectorBlocks.loadContent();

        MenuReplacer.replaceMenu(ui.menufrag);
        //THIS IS STUPID PLEASE DO NOT REPLICATE
        ProspectorBaseBuilderAI ai = new ProspectorBaseBuilderAI();

        Events.on(EventType.WorldLoadEvent.class, e -> {
            Timer.schedule(ai::updateUnit, 0f, 0.3f);

        });
        Events.run(EventType.Trigger.draw, FishAIDangerMap::draw);
        Events.on(EventType.WorldLoadEvent.class, e -> {
            Timer.schedule(() -> {
                boolean hasFishAI = false;

                for(Unit u : Groups.unit){
                    if(u.controller() instanceof FishAI){
                        hasFishAI = true;
                        break;
                    }
                }

                if(hasFishAI){
                    FishAIDangerMap.update(); // update only if needed
                }

            }, 0f, 0.3f);
        });
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
