package aquarion;

import aquarion.gen.EntityRegistry;
import aquarion.tools.IconLoader;
import aquarion.world.graphics.AquaShaders;
import arc.Events;
import mindustry.mod.Mod;
import mindustry.game.*;

import static arc.Core.app;

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

        //try {
        //Field databaseField = Vars.ui.getClass().getDeclaredField("database");
        //databaseField.setAccessible(true);
        //databaseField.set(Vars.ui, new OverDatabaseDialog());
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }
    @Override
    public void loadContent() {
        EntityRegistry.register();
        AquarionMod.loadContent();
    }

}
