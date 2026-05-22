package aquarion.ui;

import arc.*;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.*;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.gen.Icon;
import mindustry.gen.Tex;

public class AquaMenuDialog extends BaseDialog {

    public AquaMenuDialog() {
        super(Core.bundle.get("aquarion.menu.title"));
        setup();
    }

    private void setup() {
        addCloseButton();
        updateContent("links");
    }

    private Drawable getModIcon(String name, Drawable fallback) {
        String modPrefix = "aquarion-"; 
        var region = Core.atlas.find(modPrefix + name);
        
        if (region != null && region.found()) {
            Log.info("[Aquarion-UI] Successfully found texture in atlas: " + modPrefix + name);
            return new TextureRegionDrawable(region);
        } else {
            Log.warn("[Aquarion-UI] Texture NOT found in atlas: " + modPrefix + name + ". Using fallback icon.");
            return fallback;
        }
    }

    private void createRoundAvatar(Table table, String textureName, Drawable fallback, float size) {
        table.image(getModIcon(textureName, fallback)).size(size).scaling(arc.util.Scaling.fit);
    }

    private void updateContent(String type) {
        cont.clear();

        Table nav = new Table();
        nav.button(Core.bundle.get("aquarion.menu.tab_links"), () -> updateContent("links"))
           .size(160f, 50f)
           .disabled(type.equals("links"));
        nav.button(Core.bundle.get("aquarion.menu.tab_credits"), () -> updateContent("text"))
           .size(160f, 50f)
           .disabled(type.equals("text"));
        
        cont.add(nav).padBottom(20f).row();

        Table body = new Table();
        if (type.equals("links")) {
            body.pane(t -> {
                t.button(b -> {
                    createRoundAvatar(b, "github", Icon.github, 24f);
                    b.add(Core.bundle.get("aquarion.menu.link_github")).padLeft(10f);
                }, () -> Core.app.openURI("https://github.com/Twcash/Aquarion"))
                .size(380f, 60f)
                .padBottom(10f)
                .row();
                 
                t.button(b -> {
                    createRoundAvatar(b, "discord", Icon.discord, 24f);
                    b.add(Core.bundle.get("aquarion.menu.link_discord")).padLeft(10f);
                }, () -> Core.app.openURI("https://discord.gg/SbFhxYD797"))
                .size(380f, 60f)
                .padBottom(10f)
                .row();
            }).size(420f, 250f);
        } else {
            body.pane(t -> {
                t.add(Core.bundle.get("aquarion.menu.role_creator")).color(arc.graphics.Color.red).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "Twcash", Icon.admin, 32f);
                    b.add("Twcash").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Twcash", 
                    Core.bundle.get("aquarion.menu.desc_creator"), 
                    "https://github.com/Twcash", 
                    "Twcash", 
                    Icon.admin
                )).size(380f, 100f).padBottom(20f).row();

                t.add(Core.bundle.get("aquarion.menu.role_helpers")).color(arc.graphics.Color.green).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "nikolaykot", Icon.players, 32f);
                    b.add("NikolayKot02").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "NikolayKot", 
                    Core.bundle.get("aquarion.menu.desc_NikolayKot"), 
                    "https://github.com/NikolayKot02", 
                    "nikolaykot", 
                    Icon.players
                )).size(380f, 100f).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "thinkerdoodle", Icon.players, 32f);
                    b.add("Thinkerdoodle").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Thinkerdoodle", 
                    Core.bundle.get("aquarion.menu.desc_Thinkerdoodle"), 
                    "https://github.com/BSp-2", 
                    "thinkerdoodle", 
                    Icon.players
                )).size(380f, 100f).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "Vire", Icon.players, 32f);
                    b.add("Vire").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Vire", 
                    Core.bundle.get("aquarion.menu.desc_Vire"), 
                    "https://github.com/VireVeonix", 
                    "Vire", 
                    Icon.players
                )).size(380f, 100f).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "helper4", Icon.players, 32f);
                    b.add("ItsKirby").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "ItsKirby", 
                    Core.bundle.get("aquarion.menu.desc_ItsKirby"), 
                    "https://github.com/ItsKirby69", 
                    "helper4", 
                    Icon.players
                )).size(380f, 100f).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "helper5", Icon.players, 32f);
                    b.add("Alecthe2nd").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Alecthe2nd", 
                    Core.bundle.get("aquarion.menu.desc_Alecthe2nd"), 
                    "https://github.com/alecthe2nd", 
                    "helper5", 
                    Icon.players
                )).size(380f, 100f).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "OwO", Icon.players, 32f);
                    b.add("OwO (Sentinel)").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "OwO (Sentinel)", 
                    Core.bundle.get("aquarion.menu.desc_OwO"), 
                    "https://github.com/SentinelDart919", 
                    "OwO", 
                    Icon.players
                )).size(380f, 100f).padBottom(10f).row();

            }).size(420f, 650f);
        }

        cont.add(body).row();
    }

    private void showAuthorInfo(String name, String description, String profileUrl, String textureName, Drawable fallbackIcon) {
        BaseDialog authorDialog = new BaseDialog(name);
        authorDialog.addCloseButton();

        authorDialog.cont.pane(t -> {
            createRoundAvatar(t, textureName, fallbackIcon, 64f);
            t.row();

            var label = t.add(description).width(360f).wrap().padTop(15f).padBottom(20f).get();
            label.setAlignment(arc.util.Align.center);
            t.row();
        }).size(400f, 320f).row();

        authorDialog.buttons.button(Core.bundle.get("aquarion.menu.open_profile"), () -> {
            Core.app.openURI(profileUrl);
        }).size(180f, 50f);

        authorDialog.show();
    }

    public static void attach() {
        Events.on(mindustry.game.EventType.ClientLoadEvent.class, e -> {
            Vars.ui.menufrag.addButton(Core.bundle.get("aquarion.menu.button_main"), Icon.info, () -> {
                new AquaMenuDialog().show();
            });
        });
    }
}