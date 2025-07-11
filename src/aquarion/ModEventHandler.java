package aquarion;

import aquarion.dialogs.AquaResearchDialog;
import aquarion.ui.AquaHud;
import aquarion.ui.ModSettings;
import aquarion.world.graphics.AquaMenuRenderer;
import aquarion.world.graphics.MenuReplacer;
import arc.Events;
import arc.util.Log;
import arc.util.Reflect;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.ui.fragments.MenuFragment;
import mindustry.world.meta.Env;

import java.lang.reflect.Field;
import java.util.Map;

public class ModEventHandler {
    public static AquaResearchDialog techDialog;
    public static void load(){
        techDialog = new AquaResearchDialog();
    }
    public static void init() {
        Events.on(EventType.ClientLoadEvent.class, e -> {
            try {
                Reflect.set(MenuFragment.class, Vars.ui.menufrag, "renderer", new AquaMenuRenderer());
            } catch (Exception ex) {
                Log.err("Failed to replace renderer", ex);
            }
        });
        Events.on(EventType.ClientLoadEvent.class, e -> ModMusic.attach());
        Events.on(EventType.ClientLoadEvent.class, e -> ModSettings.init());

        Events.on(EventType.MusicRegisterEvent.class, e -> ModMusic.load());
    }
}
