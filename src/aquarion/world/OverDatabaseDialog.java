package aquarion.world;

import arc.*;
import arc.graphics.*;
import arc.input.*;
import arc.math.*;
import arc.scene.event.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.DatabaseDialog;
import mindustry.world.*;

import static arc.Core.*;
import static mindustry.Vars.*;

import arc.Core;
import arc.scene.ui.Label;
import arc.scene.ui.TextButton;
import arc.scene.ui.TextField;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.type.Planet;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.DatabaseDialog;
import arc.struct.Seq;
import arc.struct.ObjectSet;

public class OverDatabaseDialog extends DatabaseDialog {
    private TextField search;
    private Table all = new Table();

    private Seq<UnlockableContent> allTabs;
    private UnlockableContent tab = Planets.sun;

    public OverDatabaseDialog(){
        shouldPause = true;
        addCloseButton();

        shown(() -> {
            checkTabList();
            if (Vars.state.isCampaign() && allTabs.contains(Vars.state.getPlanet())) {
                tab = Vars.state.getPlanet();
            } else if (Vars.state.isGame() && Vars.state.rules.planet != null && allTabs.contains(Vars.state.rules.planet)) {
                tab = Vars.state.rules.planet;
            }
            rebuild();
        });

        onResize(this::rebuild);
        all.margin(20).marginTop(0f).marginRight(30f);

        cont.top();
        cont.table(s -> {
            s.image(Icon.zoom).padRight(8);
            search = s.field(null, text -> rebuild()).growX().get();
            search.setMessageText("@players.search");
        }).fillX().padBottom(4).row();

        cont.pane(all).scrollX(false);
    }

    /** Collects and organizes all content by type */
    void checkTabList() {
        if (allTabs == null) {
            Seq<Content>[] allContent = Vars.content.getContentMap();
            ObjectSet<UnlockableContent> all = new ObjectSet<>();

            for (Seq<Content> contents : allContent) {
                for (Content content : contents) {
                    if (content instanceof UnlockableContent unlock) {
                        all.add(unlock);
                    }
                }
            }

            // Sort content alphabetically
            allTabs = all.toSeq().sort((a, b) -> a.localizedName.compareToIgnoreCase(b.localizedName));
        }
    }

    public void rebuild() {
        cont.clear();
        all.clear();

        // Group content by type
        Seq<UnlockableContent> items = new Seq<>();
        Seq<UnlockableContent> liquids = new Seq<>();
        Seq<UnlockableContent> units = new Seq<>();

        // Blocks are now categorized by `contentType`
        Seq<UnlockableContent> turrets = new Seq<>();
        Seq<UnlockableContent> crafting = new Seq<>();
        Seq<UnlockableContent> power = new Seq<>();
        Seq<UnlockableContent> production = new Seq<>();
        Seq<UnlockableContent> distribution = new Seq<>();
        Seq<UnlockableContent> liquid = new Seq<>();
        Seq<UnlockableContent> defense = new Seq<>();
        Seq<UnlockableContent> effect = new Seq<>();
        Seq<UnlockableContent> logic = new Seq<>();

        for (UnlockableContent unlock : allTabs) {
            if (unlock instanceof Block block) {
                switch (block.category) {
                    case turret -> turrets.add(unlock);
                    case crafting -> crafting.add(unlock);
                    case power -> power.add(unlock);
                    case production -> production.add(unlock);
                    case distribution -> distribution.add(unlock);
                    case liquid -> liquid.add(unlock);
                    case defense -> defense.add(unlock);
                    case effect -> effect.add(unlock);
                    case logic -> logic.add(unlock);
                    default -> effect.add(unlock); // If uncategorized, put it in 'effect'
                }
            } else {
                switch (unlock.getContentType()) {
                    case item -> items.add(unlock);
                    case liquid -> liquids.add(unlock);
                    case unit -> units.add(unlock);
                }
            }
        }

        // Display grouped content
        addContentGroup("Items", items);
        addContentGroup("Liquids", liquids);
        addContentGroup("Units", units);
        addContentGroup("Turrets", turrets);
        addContentGroup("Crafting", crafting);
        addContentGroup("Power Blocks", power);
        addContentGroup("Production", production);
        addContentGroup("Distribution", distribution);
        addContentGroup("Liquid Handling", liquid);
        addContentGroup("Defenses", defense);
        addContentGroup("Effect Blocks", effect);
        addContentGroup("Logic Blocks", logic);
    }

    /** Adds a section for a specific type of content */
    private void addContentGroup(String title, Seq<UnlockableContent> contentGroup) {
        if (contentGroup.isEmpty()) return;

        all.table(t -> {
            t.add("[accent]" + title + "[/]").row(); // Title header
            for (UnlockableContent unlock : contentGroup) {
                String localizedText = Core.bundle.get("database." + unlock.name, unlock.localizedName);
                t.add(new Label(localizedText, Styles.outlineLabel)).row();
            }
        }).padBottom(10).row();
    }
}