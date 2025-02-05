package aquarion;

import aquarion.ui.ModSettings;
import aquarion.world.graphics.AquaMenuRenderer;
import arc.Events;
import arc.util.Log;
import arc.util.Reflect;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.ui.fragments.MenuFragment;

public class ModEventHandler {
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
