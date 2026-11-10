package aquarion.ui;

import aquarion.AquaLoader;
import aquarion.content.AquaPlanets;
import aquarion.dialogs.NewSettingsDialogue;
import aquarion.world.content.AquaHints;
import arc.Core;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.TechTree;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Saves;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.Setting;
import aquarion.dialogs.NewSettingsDialogue.SettingsCategory.*;
import aquarion.dialogs.NewSettingsDialogue.SettingsTable.CheckSetting.*;

import static aquarion.ModEventHandler.settingsDialogue;

public class ModSettings {
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        settingsDialogue.addCategory("Aquarion", "aquarion-settings-icon", root -> {
                    root.checkPref("floraFade", true);
                    root.sliderPref("floraFadePercent", 50, 0, 100, 5, s -> s + "%");
                    root.checkPref("evilMenderStats", true);
                    root.checkPref("onlyModMus", false);
                    root.checkPref("betterland", false);
                    root.checkPref("betterfine", false);
                    root.checkPref("richPresence", true);
                    root.checkPref("showUpdates", true);
                    root.checkPref("debugResearchRendering", false);
                    root.checkPref("debugHitboxRendering", false);
                    root.checkPref("aquaMenuBattles", true);
                    root.checkPref("blockFactionVisibility", true);
                    root.checkPref("customLightShader", true);

                    for (NewSettingsDialogue.SettingsTable.Setting setting : root.getSettings()) {
                        if (setting instanceof NewSettingsDialogue.SettingsTable.CheckSetting check) {
                            check.title = "@settings." + check.name;
                        }
                    }

                    root.button(
                            Core.bundle.get("settings.resethints"),
                            Icon.trash,
                            () -> Vars.ui.showConfirm(
                                    "@confirm",
                                    Core.bundle.get("settings.resethints-confirm"),
                                    AquaHints::reset
                            ));

            root.button(Core.bundle.get("settings.clearTech-category"), Icon.trash, () -> {
                Vars.ui.showConfirm("@confirm", Core.bundle.get("settings.clearTech-confirm"), () -> {
                    Vars.universe.clearLoadoutInfo();

                    for(TechTree.TechNode node : AquaPlanets.tantros2.techTree.children){
                        node.reset();
                    }
                    for(TechTree.TechNode node : AquaPlanets.fakeSerpulo.techTree.children){
                        node.reset();
                    }

                    Vars.content.each(c -> {
                        if (c instanceof UnlockableContent) {
                            UnlockableContent u = (UnlockableContent) c;
                            if (u.minfo != null && u.minfo.mod != null && "aquarion".equals(u.minfo.mod.name)) {
                                u.clearUnlock();
                            }
                        }
                    });

                    AquaPlanets.tantros2.techTree.reset();
                    AquaPlanets.fakeSerpulo.techTree.reset();
                    Core.settings.remove("unlocks");

                    aquarion.world.blocks.effect.ResearchServer.globalResearch.clear();
                    aquarion.world.blocks.effect.ResearchServer.saveGlobalResearch();
                });
            });
        });
    }

    public static boolean getFloraFade(){
        return Core.settings.getBool("floraFade", true);
    }

    public static int getFloraFadePercent(){
        return Core.settings.getInt("floraFadePercent", 50);
    }

    public static boolean getEvilMenderStats(){
        return Core.settings.getBool("evilMenderStats", true);
    }

    public static boolean getOnlyModMus(){
        return Core.settings.getBool("onlyModMus", false);
    }

    public static boolean getBetterLand(){
        return Core.settings.getBool("betterland", false);
    }

    public static boolean getBetterFine(){
        return Core.settings.getBool("betterfine", false);
    }

    public static boolean getShowUpdates(){return Core.settings.getBool("showUpdates", true);}

    public static boolean getDebugResearchRendering(){
        return Core.settings.getBool("debugResearchRendering", false);
    }

    public static boolean getDebugHitboxRendering(){
        return Core.settings.getBool("debugHitboxRendering", false);
    }

    public static boolean getBlockFactionVisibility(){
        return Core.settings.getBool("blockFactionVisibility", true);
    }

    public static boolean getCustomLightShader(){
        return Core.settings.getBool("customLightShader", true);
    }
}
