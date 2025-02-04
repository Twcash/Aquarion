package aquarion.ui;

import arc.Core;
import mindustry.Vars;

public class AquaSettings {
    public static void init() {
        Vars.ui.settings.addCategory("Aquarion", root -> {
            root.checkPref("@settings.betterland", false);
            root.checkPref("@settings.betterfine", false);
        });
    }

    public static boolean getBetterLand() {
        return Core.settings.getBool("@settings.betterland", false);
    }

    public static boolean getBetterFine() {
        return Core.settings.getBool("@settings.betterfine", false);
    }
}
