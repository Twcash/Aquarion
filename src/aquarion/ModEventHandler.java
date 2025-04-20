package aquarion;

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
    public static void init() {
        Events.on(EventType.ClientLoadEvent.class, e -> {
            try {
                Reflect.set(MenuFragment.class, Vars.ui.menufrag, "renderer", new AquaMenuRenderer());
            } catch (Exception ex) {
                Log.err("Failed to replace renderer", ex);
            }
            try {
                //April 1st stuffs
                Class<?> rendererClass = Class.forName("aquarion.world.graphics.Renderer");
                Field envRenderersField = rendererClass.getDeclaredField("envRenderers");
                envRenderersField.setAccessible(true);

                Object rendererInstance = rendererClass.getDeclaredMethod("getInstance").invoke(null);

                @SuppressWarnings("unchecked")
                Map<Object, Object> envRenderers = (Map<Object, Object>) envRenderersField.get(rendererInstance);

                envRenderers.replace(Env.underwater, Env.scorching);

            } catch (Exception f ) {
                f.printStackTrace();
            }
        });

        Events.on(EventType.ClientLoadEvent.class, e -> ModMusic.attach());
        Events.on(EventType.ClientLoadEvent.class, e -> ModSettings.init());

        Events.on(EventType.MusicRegisterEvent.class, e -> ModMusic.load());
    }
}
