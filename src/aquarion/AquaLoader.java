package aquarion;

import aquarion.gen.EntityRegistry;
import mindustry.mod.Mod;

public class AquaLoader extends Mod {

    public AquaLoader() {
        ModEventHandler.init();
    }
    @Override
    public void init() {
        super.init();
        AquarionMod.init();
    }
    @Override
    public void loadContent() {
        EntityRegistry.register();
        AquarionMod.loadContent();
    }
}
