package aquarion;

import aquarion.tools.IconLoader;
import aquarion.ui.AquaStyles;
import aquarion.world.graphics.AquaShaders;
import arc.Events;
import mindustry.Vars;
import mindustry.mod.Mod;
import mindustry.game.*;

import static arc.Core.app;
import static mindustry.Vars.mobile;

public class AquaLoader extends Mod {
    public AquaLoader() {
        ModEventHandler.init();
        Events.on(EventType.FileTreeInitEvent.class, e ->
                app.post(AquaShaders::init)
        );

        Events.on(EventType.DisposeEvent.class, e ->
                AquaShaders.dispose()
        );
    }
    @Override
    public void init() {

        IconLoader.loadIcons();

        if (!Vars.headless) {
            AquaStyles.load();
            ModEventHandler.load();
        }
    }
    @Override
    public void loadContent() {
        AquarionMod.loadContent();
    }

}
