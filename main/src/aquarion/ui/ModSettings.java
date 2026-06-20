package aquarion.ui;

import aquarion.content.AquaPlanets;
import aquarion.world.content.AquaHints;
import arc.Core;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.TechTree;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Saves;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.Setting;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.CheckSetting;

public class ModSettings {
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        Vars.ui.settings.addCategory("Aquarion", "aquarion-settings-icon", root -> {
            root.checkPref("floraFade", true);
            root.sliderPref("floraFadePercent", 50,0,100,5,s -> s + "%");
            root.checkPref("evilMenderStats", true);
            root.checkPref("onlyModMus", false);
            root.checkPref("betterland", false);
            root.checkPref("betterfine", false);
            root.checkPref("richPresence", true);
            root.checkPref("showUpdates", true);
            root.checkPref("debugResearchRendering", false);
            root.checkPref("debugHitboxRendering", false);
            
            for (Setting setting : root.getSettings()) {
                if (setting instanceof CheckSetting) {
                    CheckSetting check = (CheckSetting) setting;
                    check.title = "@settings." + check.name;
                }
            }

            root.pref(new ButtonPref(
                    Core.bundle.get("settings.resethints"),
                    Icon.trash,
                    () -> Vars.ui.showConfirm(
                            "@confirm",
                            Core.bundle.get("settings.resethints-confirm"),
                            AquaHints::reset
                    )
            ));

            root.pref(new ButtonPref(Core.bundle.get("settings.clearTech-category"), Icon.trash, () -> {
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
                });
            }));

            root.pref(new ButtonPref(Core.bundle.get("settings.clearCampaign"), Icon.trash, () -> {
                Vars.ui.showConfirm("@confirm", Core.bundle.get("settings.clearCampaign-confirm"), () -> {
                    Seq<Saves.SaveSlot> toDelete = new Seq<>();
                    Vars.control.saves.getSaveSlots().each(s -> {
                        if(s.getSector() == null) return;
                        if((s.getSector().planet == AquaPlanets.tantros2 || s.getSector().planet == AquaPlanets.fakeSerpulo)) {
                            toDelete.add(s);
                        }
                    });
                    toDelete.each(Saves.SaveSlot::delete);

                    Vars.ui.showInfoOnHidden(Core.bundle.get("settings.clearCampaign-closeConfirm"), () -> {
                        Core.app.exit();
                    });
                });
            }));
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

    public static boolean getRichPresence(){
        return Core.settings.getBool("richPresence", true);
    }

    public static boolean getShowUpdates(){return Core.settings.getBool("showUpdates", true);}

    public static boolean getDebugResearchRendering(){
        return Core.settings.getBool("debugResearchRendering", false);
    }

    public static boolean getDebugHitboxRendering(){
        return Core.settings.getBool("debugHitboxRendering", false);
    }
}
