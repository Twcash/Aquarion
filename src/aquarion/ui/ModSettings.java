package aquarion.ui;

import aquarion.planets.AquaPlanets;
import arc.Core;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.TechTree;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Saves;
import mindustry.gen.Icon;

public class ModSettings {
    public static void init() {
        Vars.ui.settings.addCategory("Aquarion", root -> {
            root.checkPref("@settings.onlyModMus", false);
            root.checkPref("@settings.betterland", false);
            root.checkPref("@settings.betterfine", false);
            root.checkPref("@settings.richPrescense", true);

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
                        if(c instanceof UnlockableContent u && u.minfo != null && u.minfo.mod != null && u.minfo.mod.name.equals("src/aquarion")){
                            u.clearUnlock();
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
        return Core.settings.getBool("richPrescense", true);
    }
}
