package aquarion.ui;

import arc.*;
import arc.graphics.Color;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.*;
import arc.scene.ui.ScrollPane;
import arc.struct.Seq;
import arc.util.Http;
import arc.util.Log;
import arc.util.serialization.Jval;
import mindustry.Vars;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.gen.Icon;

public class AquaMenuDialog extends BaseDialog {

    private int page = 1;
    private final int perPage = 10;
    private Table changelogListTable;
    private boolean isLoading = false;
    private boolean hasNextPage = true;

    private static final String GITHUB_REPO = "Twcash/Aquarion";
    private static final String RELEASES_URL = "https://github.com/" + GITHUB_REPO + "/releases";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/" + GITHUB_REPO + "/releases";

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
            return new TextureRegionDrawable(region);
        } else {
            return fallback;
        }
    }

    private void createRoundAvatar(Table table, String textureName, Drawable fallback, float size) {
        table.image(getModIcon(textureName, fallback)).size(size).scaling(arc.util.Scaling.fit);
    }

    private void updateContent(String type) {
        cont.clear();

        // Динамический расчет размеров под экран
        float screenH = Core.graphics.getHeight();
        boolean isPortrait = Core.graphics.isPortrait();

        float paneWidth = Vars.mobile ? (isPortrait ? 440f : 520f) : 460f;
        // В альбомном режиме ограничиваем высоту до 50% высоты экрана
        float maxPaneHeight = isPortrait ? screenH * 0.65f : screenH * 0.52f;
        float buttonWidth = paneWidth - 60f;
        float buttonHeight = Vars.mobile ? 55f : 65f;
        float navBtnWidth = Vars.mobile ? 100f : 120f;

        Table mainTable = new Table();

        // 1. Верхние кнопки навигации (Links -> Credits -> Changelog)
        Table nav = new Table();
        nav.button(Core.bundle.get("aquarion.menu.tab_links", "Links"), () -> updateContent("links"))
           .size(navBtnWidth, 40f)
           .disabled(type.equals("links"));

        nav.button(Core.bundle.get("aquarion.menu.tab_credits", "Credits"), () -> updateContent("text"))
           .size(navBtnWidth, 40f)
           .disabled(type.equals("text"));

        nav.button(Core.bundle.get("aquarion.menu.tab_changelog", "Changelog"), () -> updateContent("changelog"))
           .size(navBtnWidth, 40f)
           .disabled(type.equals("changelog"));

        mainTable.add(nav).padBottom(10f).row();

        // 2. Основное содержимое вкладок
        Table contentTable = new Table();

        if (type.equals("links")) {
            contentTable.center();
            contentTable.button(b -> {
                createRoundAvatar(b, "github", Icon.github, 24f);
                b.add(Core.bundle.get("aquarion.menu.link_github")).padLeft(10f);
            }, () -> Core.app.openURI("https://github.com/" + GITHUB_REPO))
            .size(buttonWidth, 50f).padBottom(8f).row();

            contentTable.button(b -> {
                createRoundAvatar(b, "discord", Icon.discord, 24f);
                b.add(Core.bundle.get("aquarion.menu.link_discord")).padLeft(10f);
            }, () -> Core.app.openURI("https://discord.gg/SbFhxYD797"))
            .size(buttonWidth, 50f).padBottom(8f).row();

            contentTable.button(b -> {
                createRoundAvatar(b, "wiki", Icon.players, 24f);
                b.add(Core.bundle.get("aquarion.menu.link_wiki")).padLeft(10f);
            }, () -> Core.app.openURI("https://nullotte.github.io/MindustryModWiki/aquarion"))
            .size(buttonWidth, 50f).padBottom(8f).row();

        } else if (type.equals("changelog")) {
            changelogListTable = new Table();
            changelogListTable.top().left();

            contentTable.add(changelogListTable).growX().row();

            contentTable.button(Core.bundle.get("aquarion.menu.open_releases", "Open Releases on GitHub"), Icon.github, () -> {
                Core.app.openURI(RELEASES_URL);
            }).size(buttonWidth, 36f).padTop(8f).padBottom(8f).row();

            Table paginationTable = new Table();
            paginationTable.button(Icon.left, () -> {
                if (page > 1 && !isLoading) {
                    page--;
                    fetchReleases();
                }
            }).size(36f).disabled(t -> page <= 1 || isLoading);

            paginationTable.label(() -> String.valueOf(page)).fontScale(1.1f).padLeft(10f).padRight(10f);

            paginationTable.button(Icon.right, () -> {
                if (hasNextPage && !isLoading) {
                    page++;
                    fetchReleases();
                }
            }).size(36f).disabled(t -> !hasNextPage || isLoading);

            contentTable.add(paginationTable);
            fetchReleases();

        } else {
            // Вкладка с помощниками (Credits)
            contentTable.center();

            contentTable.add(Core.bundle.get("aquarion.menu.role_creator")).color(Color.red).center().padBottom(6f).row();

            contentTable.button(b -> {
                createRoundAvatar(b, "Twcash", Icon.admin, 28f);
                b.add("Twcash").left().padLeft(10f);
            }, () -> showAuthorInfo(
                "Twcash", Core.bundle.get("aquarion.menu.desc_creator"),
                "https://github.com/Twcash", "Twcash", Icon.admin, true
            )).size(buttonWidth, buttonHeight).padBottom(12f).row();

            contentTable.add(Core.bundle.get("aquarion.menu.role_helpers")).color(Color.green).center().padBottom(6f).row();

            addAuthorButton(contentTable, "NikolayKot02", "aquarion.menu.desc_NikolayKot", "https://github.com/NikolayKot02", "nikolaykot", Icon.players, true, buttonWidth, buttonHeight);
            addAuthorButton(contentTable, "OwO (Sentinel)", "aquarion.menu.desc_OwO", "https://github.com/SentinelDart919", "OwO", Icon.players, true, buttonWidth, buttonHeight);
            addAuthorButton(contentTable, "Alecthe2nd", "aquarion.menu.desc_Alecthe2nd", "https://github.com/alecthe2nd", "Alecthe2nd", Icon.players, true, buttonWidth, buttonHeight);
            addAuthorButton(contentTable, "cupcakerouter", "aquarion.menu.desc_cupcakerouter", "", "cupcakerouter", Icon.players, false, buttonWidth, buttonHeight);
            addAuthorButton(contentTable, "Vire", "aquarion.menu.desc_Vire", "https://github.com/VireVeonix", "Vire", Icon.players, true, buttonWidth, buttonHeight);
            addAuthorButton(contentTable, "ItsKirby", "aquarion.menu.desc_ItsKirby", "https://github.com/ItsKirby69", "ItsKirby", Icon.players, true, buttonWidth, buttonHeight);
            addAuthorButton(contentTable, "Plooey", "aquarion.menu.desc_Thinkerdoodle", "https://github.com/BSp-2", "thinkerdoodle", Icon.players, true, buttonWidth, buttonHeight);
            addAuthorButton(contentTable, "Leo", "aquarion.menu.desc_Leo", "https://github.com/Leo-MathGuy", "Leo", Icon.players, true, buttonWidth, buttonHeight);
            addAuthorButton(contentTable, "Mythril", "aquarion.menu.desc_Mythril", "https://github.com/Mythril382", "Mythril", Icon.players, true, buttonWidth, buttonHeight);
            addAuthorButton(contentTable, "Andromeda-Galaxy29", "aquarion.menu.desc_Andromeda-Galaxy29", "https://github.com/Andromeda-Galaxy29", "Andromeda-Galaxy29", Icon.players, true, buttonWidth, buttonHeight);
            addAuthorButton(contentTable, "Sputnuc", "aquarion.menu.desc_Sputnuc", "https://github.com/Sputnuc", "Sputnuc", Icon.players, true, buttonWidth, buttonHeight);
            addAuthorButton(contentTable, "nullotte", "aquarion.menu.desc_nullotte", "https://github.com/nullotte", "nullotte", Icon.players, true, buttonWidth, buttonHeight);
            addAuthorButton(contentTable, "kapzduke", "aquarion.menu.desc_kapzduke", "https://github.com/kapzduke", "kapzduke", Icon.players, true, buttonWidth, buttonHeight);
            addAuthorButton(contentTable, "camelStyleUser", "aquarion.menu.desc_camelStyleUser", "https://github.com/camelStyleUser", "camelStyleUser", Icon.players, true, buttonWidth, buttonHeight);
            addAuthorButton(contentTable, "Henan-CN-0921", "aquarion.menu.desc_Henan-CN-0921", "https://github.com/Henan-CN-0921", "Henan-CN-0921", Icon.players, true, buttonWidth, buttonHeight);
            addAuthorButton(contentTable, "Norax", "aquarion.menu.desc_Norax", "https://github.com/Noraxx1", "Norax", Icon.players, true, buttonWidth, buttonHeight);
        }

        mainTable.add(contentTable).row();

        // Помещаем весь интерфейс в фиксированный по высоте ScrollPane
        var cell = cont.pane(mainTable).size(paneWidth, maxPaneHeight);
        if (cell.get() instanceof ScrollPane) {
            ((ScrollPane) cell.get()).setFlickScroll(true);
        }
    }

    private void addAuthorButton(Table table, String name, String descKey, String url, String texture, Drawable fallback, boolean hasProfile, float w, float h) {
        table.button(b -> {
            createRoundAvatar(b, texture, fallback, 28f);
            b.add(name).left().padLeft(10f);
        }, () -> showAuthorInfo(name, Core.bundle.get(descKey), url, texture, fallback, hasProfile))
        .size(w, h).padBottom(8f).row();
    }

    private void fetchReleases() {
        if (isLoading || changelogListTable == null) return;
        isLoading = true;

        changelogListTable.clear();
        changelogListTable.add(Core.bundle.get("loading", "Loading...")).color(Color.lightGray).center().pad(15f).grow();

        String url = GITHUB_API_URL + "?page=" + page + "&per_page=" + perPage;

        Http.get(url).error(e -> {
            Log.err("Failed to fetch Aquarion releases", e);
            isLoading = false;
            Core.app.post(() -> {
                changelogListTable.clear();
                changelogListTable.add(Core.bundle.get("error.fetch-releases", "Failed to fetch releases.")).color(Color.scarlet).center().pad(15f).grow();
            });
        }).submit(res -> {
            try {
                Jval json = Jval.read(res.getResultAsString());
                if (json.isArray()) {
                    Seq<Jval> releases = json.asArray();
                    hasNextPage = releases.size >= perPage;
                    Core.app.post(() -> {
                        isLoading = false;
                        rebuildChangelogList(releases);
                    });
                } else {
                    isLoading = false;
                    Core.app.post(() -> {
                        changelogListTable.clear();
                        changelogListTable.add(Core.bundle.get("error.invalid-response", "Invalid response.")).color(Color.scarlet).center().pad(15f).grow();
                    });
                }
            } catch (Exception e) {
                Log.err("Failed to parse Aquarion releases", e);
                isLoading = false;
                Core.app.post(() -> {
                    changelogListTable.clear();
                    changelogListTable.add(Core.bundle.get("error.parse-failed", "Parse failed.")).color(Color.scarlet).center().pad(15f).grow();
                });
            }
        });
    }

    private void rebuildChangelogList(Seq<Jval> releases) {
        changelogListTable.clear();
        changelogListTable.top().left();

        if (releases.size == 0) {
            changelogListTable.add(Core.bundle.get("changelog.empty", "No releases found.")).color(Color.lightGray).center().pad(15f).grow();
            return;
        }

        float cardWidth = Vars.mobile ? 380f : 360f;

        for (Jval release : releases) {
            String tagName = release.getString("tag_name", "Unknown");
            String body = release.getString("body", "");
            String name = release.getString("name", tagName);
            String htmlUrl = release.getString("html_url", RELEASES_URL);
            int downloadCount = 0;

            if (release.has("assets")) {
                for (Jval asset : release.get("assets").asArray()) {
                    downloadCount += asset.getInt("download_count", 0);
                }
            }

            final int finalDownloadCount = downloadCount;

            changelogListTable.table(Styles.black5, t -> {
                t.top().left().margin(8f);

                t.table(header -> {
                    header.left();
                    header.add("[accent]" + name + "[white]").style(Styles.defaultLabel).growX().left();
                    header.add("[lightgray]" + tagName + "[white]").padLeft(10f);
                }).growX().row();

                t.table(stats -> {
                    stats.left();
                    stats.image(Icon.download).size(14f).color(Color.gold);
                    stats.add("[gold] " + finalDownloadCount + "[white]").padLeft(5f);
                }).padTop(4f).growX().row();

                t.image().color(Color.gray).height(1f).growX().padTop(4f).padBottom(4f).row();

                t.add(body).wrap().width(cardWidth - 20f).left().padBottom(8f).row();

                t.button(Core.bundle.get("aquarion.menu.open_release_tag", "View on GitHub"), Icon.export, () -> {
                    Core.app.openURI(htmlUrl);
                }).size(150f, 32f).right();

            }).width(cardWidth).padBottom(8f).row();
        }
    }

    private void showAuthorInfo(String name, String description, String profileUrl, String textureName, Drawable fallbackIcon, boolean hasProfile) {
        BaseDialog authorDialog = new BaseDialog(name);
        authorDialog.addCloseButton();

        float dialogWidth = Vars.mobile ? 380f : 420f;
        float dialogHeight = Math.min(Core.graphics.getHeight() * 0.55f, 300f);

        authorDialog.cont.pane(t -> {
            t.left();

            Table leftTable = new Table();
            createRoundAvatar(leftTable, textureName, fallbackIcon, 80f);
            t.add(leftTable).top().padRight(12f);

            Table rightTable = new Table();
            rightTable.left();

            rightTable.add(name).left().fontScale(1.05f).row();

            var label = rightTable.add(description).width(dialogWidth - 130f).wrap().padTop(8f).left().get();
            label.setAlignment(arc.util.Align.left);

            t.add(rightTable).top().expandX().fillX();
        }).size(dialogWidth, dialogHeight);

        if (hasProfile) {
            authorDialog.buttons.button(Core.bundle.get("aquarion.menu.open_profile"), () -> {
                Core.app.openURI(profileUrl);
            }).size(Vars.mobile ? 140f : 160f, 40f);
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
