package aquarion.ui;

import arc.Core;
import mindustry.Vars;

public class ModSettings {
    public static void init() {
        Vars.ui.settings.addCategory("Aquarion", root -> {
            root.checkPref("@settings.onlyModMus", false);
            root.checkPref("@settings.betterland", false);
            root.checkPref("@settings.betterfine", false);
            root.checkPref("@settings.richPrescense", true);
        });
    }

    public static boolean getModMusOnly() {
        return Core.settings.getBool("@settings.onlyModMus", false);
    }
    public static boolean getBetterLand() {
        return Core.settings.getBool("@settings.betterland", false);
    }
    public static boolean getBetterFine() {
        return Core.settings.getBool("@settings.betterfine", false);
    }
    public static boolean rich() {
        return Core.settings.getBool("@settings.richPrescense", true);
    }
}
