package aquarion;

import aquarion.annotations.Annotations;
import aquarion.dialogs.AquaResearchDialog;
import aquarion.ui.ModSettings;
import aquarion.world.graphics.AquaMenuRenderer;
import arc.Events;
import arc.util.Log;
import arc.util.Reflect;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.ui.fragments.MenuFragment;

@Annotations.LoadRegs("error")// Need this temporarily, so the class gets generated.
@Annotations.EnsureLoad
public class ModEventHandler {
    public static AquaResearchDialog techDialog;
    public static void load(){
        techDialog = new AquaResearchDialog();
    }
    public static void init() {
//        Events.on(EventType.ClientLoadEvent.class, e -> {
//            if(Vars.ui.menufrag != null) {
//                try {
//                    // Reflect.set(MenuFragment.class, Vars.ui.menufrag, "renderer", new AquaMenuRenderer());
//                } catch (Exception ex) {
//                    Log.err("Failed to replace renderer", ex);
//                }
//            } else {
//                Log.info("Vars.ui.menufrag was null. Unable to replace menu");
//            }
//        });
        Events.on(EventType.ClientLoadEvent.class, e -> ModMusic.attach());
        Events.on(EventType.ClientLoadEvent.class, e -> ModSettings.init());

        Events.on(EventType.MusicRegisterEvent.class, e -> ModMusic.load());
    }
}
