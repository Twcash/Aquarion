package aquarion.ui;

import aquarion.AquaLoader;
import arc.Core;
import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.core.GameState.State;
import mindustry.core.Version;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.EventType.Trigger;
import mindustry.ui.dialogs.BaseDialog;

/** Blocks unsupported clients and offers removal of Aquarion. */
public final class ClientCompatibility {
    private static boolean installed;

    private ClientCompatibility(){}

    public static void install(){
        if(installed || Vars.headless) return;
        installed = true;
        if(!hasFoosClient()) return;

        Events.on(ClientLoadEvent.class, event -> Core.app.post(() -> {
            BaseDialog dialog = new BaseDialog(Core.bundle.get("aquarion.client-compatibility.title"));
            dialog.cont.add(Core.bundle.get("aquarion.client-compatibility.message"))
                .width(620f).wrap().pad(24f);
            dialog.buttons.button(Core.bundle.get("aquarion.client-compatibility.uninstall"), () -> {
                try{
                    var mod = Vars.mods.getMod(AquaLoader.class);
                    //Linux
                    if(mod == null) throw new IllegalStateException("How?");
                    Vars.mods.removeMod(mod);
                    Core.app.exit();
                }catch(Exception error){
                    Log.err("Unable to uninstall Aquarion", error);
                    dialog.cont.clearChildren();
                    dialog.cont.add(Core.bundle.get("aquarion.client-compatibility.uninstall-error"))
                        .width(620f).wrap().pad(24f);
                }
            }).size(310f, 64f);
            dialog.buttons.button(Core.bundle.get("aquarion.client-compatibility.exit"), () -> Core.app.exit()).size(210f, 64f);
            // No back/escape listener: the player must exit and use a supported client.
            dialog.show();
            Events.run(Trigger.update, () -> {
                if(Vars.state.isGame()) Vars.state.set(State.paused);
                if(!dialog.isShown()) dialog.show();
                dialog.toFront();
            });
        }));
    }

    static boolean hasFoosClient(){
        for(java.lang.reflect.Field field : Version.class.getDeclaredFields()){
            if(field.getName().equals("foos")) return true;
        }
        return false;
    }
}
