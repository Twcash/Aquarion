package aquarion.ui;

import arc.Core;
import arc.util.Log;
import mindustry.Vars;

public class AquaSettings {
    public static void init() {
        Vars.ui.settings.addCategory("Aquarion", root -> {
            root.checkPref("@settings.betterland", false);
        });
    }

    public static boolean getBetterLand() {
        return Core.settings.getBool("@settings.betterland", false);
    }
}
