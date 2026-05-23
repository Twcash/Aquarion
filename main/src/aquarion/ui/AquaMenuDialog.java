package aquarion.ui;

import arc.*;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.*;
import arc.scene.ui.ScrollPane;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.gen.Icon;

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

        // Адаптивные размеры под ПК и мобилки
        float paneWidth = Vars.mobile ? 460f : 420f;
        float paneHeight = Vars.mobile ? 350f : 650f;
        float buttonWidth = Vars.mobile ? paneWidth - 20f : 380f;
        float buttonHeight = Vars.mobile ? 85f : 100f;

        Table nav = new Table();
        nav.button(Core.bundle.get("aquarion.menu.tab_links"), () -> updateContent("links"))
           .size(Vars.mobile ? 150f : 160f, 50f)
           .disabled(type.equals("links"));
        nav.button(Core.bundle.get("aquarion.menu.tab_credits"), () -> updateContent("text"))
           .size(Vars.mobile ? 150f : 160f, 50f)
           .disabled(type.equals("text"));
        
        cont.add(nav).padBottom(15f).row();

        Table body = new Table();
        if (type.equals("links")) {
            body.pane(t -> {
                t.button(b -> {
                    createRoundAvatar(b, "github", Icon.github, 24f);
                    b.add(Core.bundle.get("aquarion.menu.link_github")).padLeft(10f);
                }, () -> Core.app.openURI("https://github.com/Twcash/Aquarion"))
                .size(buttonWidth, 60f)
                .padBottom(10f)
                .row();
                 
                t.button(b -> {
                    createRoundAvatar(b, "discord", Icon.discord, 24f);
                    b.add(Core.bundle.get("aquarion.menu.link_discord")).padLeft(10f);
                }, () -> Core.app.openURI("https://discord.gg/SbFhxYD797"))
                .size(buttonWidth, 60f)
                .padBottom(10f)
                .row();
            }).size(paneWidth, Vars.mobile ? 140f : 250f);
        } else {
            // Список участников с прокруткой
            var cell = body.pane(t -> {
                t.add(Core.bundle.get("aquarion.menu.role_creator")).color(arc.graphics.Color.red).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "Twcash", Icon.admin, 32f);
                    b.add("Twcash").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Twcash", 
                    Core.bundle.get("aquarion.menu.desc_creator"), 
                    "https://github.com/Twcash", 
                    "Twcash", 
                    Icon.admin,
                    true // Есть кнопка профиля
                )).size(buttonWidth, buttonHeight).padBottom(20f).row();

                t.add(Core.bundle.get("aquarion.menu.role_helpers")).color(arc.graphics.Color.green).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "nikolaykot", Icon.players, 32f);
                    b.add("NikolayKot02").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "NikolayKot", 
                    Core.bundle.get("aquarion.menu.desc_NikolayKot"), 
                    "https://github.com/NikolayKot02", 
                    "nikolaykot", 
                    Icon.players,
                    true // Измени на false, если надо выключить кнопку
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "thinkerdoodle", Icon.players, 32f);
                    b.add("Thinkerdoodle").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Thinkerdoodle", 
                    Core.bundle.get("aquarion.menu.desc_Thinkerdoodle"), 
                    "https://github.com/BSp-2", 
                    "thinkerdoodle", 
                    Icon.players,
                    true 
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "Vire", Icon.players, 32f);
                    b.add("Vire").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Vire", 
                    Core.bundle.get("aquarion.menu.desc_Vire"), 
                    "https://github.com/VireVeonix", 
                    "Vire", 
                    Icon.players,
                    true
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "ItsKirby", Icon.players, 32f);
                    b.add("ItsKirby").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "ItsKirby", 
                    Core.bundle.get("aquarion.menu.desc_ItsKirby"), 
                    "https://github.com/ItsKirby69", 
                    "ItsKirby", 
                    Icon.players,
                    true
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "Alecthe2nd", Icon.players, 32f);
                    b.add("Alecthe2nd").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Alecthe2nd", 
                    Core.bundle.get("aquarion.menu.desc_Alecthe2nd"), 
                    "https://github.com/alecthe2nd", 
                    "Alecthe2nd", 
                    Icon.players,
                    true
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "OwO", Icon.players, 32f);
                    b.add("OwO (Sentinel)").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "OwO (Sentinel)", 
                    Core.bundle.get("aquarion.menu.desc_OwO"), 
                    "https://github.com/SentinelDart919", 
                    "OwO", 
                    Icon.players,
                    true
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "Leo", Icon.players, 32f);
                    b.add("Leo").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Leo", 
                    Core.bundle.get("aquarion.menu.desc_Leo"), 
                    "https://github.com/Leo-MathGuy", 
                    "Leo", 
                    Icon.players,
                    true
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "Mythril", Icon.players, 32f);
                    b.add("Mythril").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Mythril", 
                    Core.bundle.get("aquarion.menu.desc_Mythril"), 
                    "https://github.com/Mythril382", 
                    "Mythril", 
                    Icon.players,
                    true
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "Andromeda-Galaxy29", Icon.players, 32f);
                    b.add("Andromeda-Galaxy29").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Andromeda-Galaxy29", 
                    Core.bundle.get("aquarion.menu.desc_Andromeda-Galaxy29"), 
                    "https://github.com/Andromeda-Galaxy29", 
                    "Andromeda-Galaxy29", 
                    Icon.players,
                    true
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "Sputnuc", Icon.players, 32f);
                    b.add("Sputnuc").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Sputnuc", 
                    Core.bundle.get("aquarion.menu.desc_Sputnuc"), 
                    "https://github.com/Sputnuc", 
                    "Sputnuc", 
                    Icon.players,
                    true
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "CY4NIDE", Icon.players, 32f);
                    b.add("CY4NIDE").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "CY4NIDE", 
                    Core.bundle.get("aquarion.menu.desc_CY4NIDE"), 
                    "https://github.com/cyanide863", 
                    "CY4NIDE", 
                    Icon.players,
                    true
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "nullotte", Icon.players, 32f);
                    b.add("nullotte").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "nullotte", 
                    Core.bundle.get("aquarion.menu.desc_nullotte"), 
                    "https://github.com/nullotte", 
                    "nullotte", 
                    Icon.players,
                    true
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "kapzduke", Icon.players, 32f);
                    b.add("kapzduke").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "kapzduke", 
                    Core.bundle.get("aquarion.menu.desc_kapzduke"), 
                    "https://github.com/kapzduke", 
                    "kapzduke", 
                    Icon.players,
                    true
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "camelStyleUser", Icon.players, 32f);
                    b.add("camelStyleUser").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "camelStyleUser", 
                    Core.bundle.get("aquarion.menu.desc_camelStyleUser"), 
                    "https://github.com/camelStyleUser", 
                    "camelStyleUser", 
                    Icon.players,
                    true
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "Henan-CN-0921", Icon.players, 32f);
                    b.add("Henan-CN-0921").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Henan-CN-0921", 
                    Core.bundle.get("aquarion.menu.desc_Henan-CN-0921"), 
                    "https://github.com/Henan-CN-0921", 
                    "Henan-CN-0921", 
                    Icon.players,
                    true
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "actions-user", Icon.players, 32f);
                    b.add("actions-user").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "actions-user", 
                    Core.bundle.get("aquarion.menu.desc_actions-user"), 
                    "https://github.com/actions-user", 
                    "actions-user", 
                    Icon.players,
                    true
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "Norax", Icon.players, 32f);
                    b.add("Norax").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Norax", 
                    Core.bundle.get("aquarion.menu.desc_Norax"), 
                    "https://github.com/Noraxx1", 
                    "Norax", 
                    Icon.players,
                    true
                )).size(buttonWidth, buttonHeight).padBottom(10f).row();

            }).size(paneWidth, paneHeight);

            if(cell.get() instanceof ScrollPane){
                ((ScrollPane)cell.get()).setFlickScroll(true);
            }
        }

        cont.add(body).row();
    }

    private void showAuthorInfo(String name, String description, String profileUrl, String textureName, Drawable fallbackIcon, boolean hasProfile) {
        BaseDialog authorDialog = new BaseDialog(name);
        authorDialog.addCloseButton();

        float dialogWidth = Vars.mobile ? 380f : 400f;
        float dialogHeight = Vars.mobile ? 260f : 320f;

        authorDialog.cont.pane(t -> {
            createRoundAvatar(t, textureName, fallbackIcon, 64f);
            t.row();

            var label = t.add(description).width(dialogWidth - 40f).wrap().padTop(15f).padBottom(20f).get();
            label.setAlignment(arc.util.Align.center);
            t.row();
        }).size(dialogWidth, dialogHeight);

        // Кнопка создаётся только если аргумент hasProfile равен true
        if (hasProfile) {
            authorDialog.buttons.button(Core.bundle.get("aquarion.menu.open_profile"), () -> {
                Core.app.openURI(profileUrl);
            }).size(Vars.mobile ? 150f : 180f, 50f);
        }

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