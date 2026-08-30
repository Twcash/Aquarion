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

    // Переменные пагинации и состояния ченджлога
    private int page = 1;
    private final int perPage = 10;
    private Table changelogListTable;
    private boolean isLoading = false;
    private boolean hasNextPage = true;

    // Ссылки на GitHub репозиторий Aquarion
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

        float paneWidth = Vars.mobile ? 460f : 420f;
        float paneHeight = Vars.mobile ? 350f : 650f;
        float buttonWidth = Vars.mobile ? paneWidth - 50f : 380f;
        float buttonHeight = Vars.mobile ? 85f : 100f;
        float navBtnWidth = Vars.mobile ? 100f : 120f;

        // Вкладки навигации
        Table nav = new Table();
        nav.button(Core.bundle.get("aquarion.menu.tab_links", "Links"), () -> updateContent("links"))
           .size(navBtnWidth, 50f)
           .disabled(type.equals("links"));

        nav.button(Core.bundle.get("aquarion.menu.tab_changelog", "Changelog"), () -> updateContent("changelog"))
           .size(navBtnWidth, 50f)
           .disabled(type.equals("changelog"));

        nav.button(Core.bundle.get("aquarion.menu.tab_credits", "Credits"), () -> updateContent("text"))
           .size(navBtnWidth, 50f)
           .disabled(type.equals("text"));

        cont.add(nav).padBottom(15f).row();

        Table body = new Table();

        if (type.equals("links")) {
            body.pane(t -> {
                t.center();
                t.button(b -> {
                    createRoundAvatar(b, "github", Icon.github, 24f);
                    b.add(Core.bundle.get("aquarion.menu.link_github")).padLeft(10f);
                }, () -> Core.app.openURI("https://github.com/" + GITHUB_REPO))
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

                t.button(b -> {
                    createRoundAvatar(b, "wiki", Icon.players, 24f);
                    b.add(Core.bundle.get("aquarion.menu.link_wiki")).padLeft(10f);
                }, () -> Core.app.openURI("https://nullotte.github.io/MindustryModWiki/aquarion"))
                .size(buttonWidth, 60f)
                .padBottom(10f)
                .row();
            }).size(paneWidth, Vars.mobile ? 230f : 250f);

        } else if (type.equals("changelog")) {
            Table changelogContainer = new Table();
            changelogListTable = new Table();
            changelogListTable.top().left();

            var cell = changelogContainer.pane(changelogListTable).size(paneWidth, paneHeight - 100f);
            if (cell.get() instanceof ScrollPane scrollPane) {
                scrollPane.setFlickScroll(true);
            }
            changelogContainer.row();

            // Кнопка перехода на внешнюю страницу всех релизов GitHub
            changelogContainer.button(Core.bundle.get("aquarion.menu.open_releases", "Open Releases on GitHub"), Icon.github, () -> {
                Core.app.openURI(RELEASES_URL);
            }).size(paneWidth - 20f, 40f).padTop(8f).padBottom(8f).row();

            // Элементы пагинации (переключение страниц)
            Table paginationTable = new Table();
            paginationTable.button(Icon.left, () -> {
                if (page > 1 && !isLoading) {
                    page--;
                    fetchReleases();
                }
            }).size(40f).disabled(t -> page <= 1 || isLoading);

            paginationTable.label(() -> String.valueOf(page)).fontScale(1.1f).padLeft(10f).padRight(10f);

            paginationTable.button(Icon.right, () -> {
                if (hasNextPage && !isLoading) {
                    page++;
                    fetchReleases();
                }
            }).size(40f).disabled(t -> !hasNextPage || isLoading);

            changelogContainer.add(paginationTable);
            body.add(changelogContainer);

            // Загрузка списка релизов
            fetchReleases();

        } else {
            var cell = body.pane(t -> {
                t.center();

                t.add(Core.bundle.get("aquarion.menu.role_creator")).color(Color.red).center().padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "Twcash", Icon.admin, 32f);
                    b.add("Twcash").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Twcash",
                    Core.bundle.get("aquarion.menu.desc_creator"),
                    "https://github.com/Twcash",
                    "Twcash",
                    Icon.admin,
                    true
                )).size(buttonWidth, buttonHeight).padBottom(20f).row();

                t.add(Core.bundle.get("aquarion.menu.role_helpers")).color(Color.green).center().padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "nikolaykot", Icon.players, 32f);
                    b.add("NikolayKot02").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "NikolayKot",
                    Core.bundle.get("aquarion.menu.desc_NikolayKot"),
                    "https://github.com/NikolayKot02",
                    "nikolaykot",
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
                    createRoundAvatar(b, "cupcakerouter", Icon.players, 32f);
                    b.add("cupcakerouter").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "cupcakerouter",
                    Core.bundle.get("aquarion.menu.desc_cupcakerouter"),
                    "",
                    "cupcakerouter",
                    Icon.players,
                    false
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
                    createRoundAvatar(b, "thinkerdoodle", Icon.players, 32f);
                    b.add("Plooey").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Plooey",
                    Core.bundle.get("aquarion.menu.desc_Thinkerdoodle"),
                    "https://github.com/BSp-2",
                    "thinkerdoodle",
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

            if (cell.get() instanceof ScrollPane) {
                ((ScrollPane)cell.get()).setFlickScroll(true);
            }
        }

        cont.add(body).row();
    }

    // Загрузка списков релизов с GitHub API
    private void fetchReleases() {
        if (isLoading || changelogListTable == null) return;
        isLoading = true;

        changelogListTable.clear();
        changelogListTable.add(Core.bundle.get("loading", "Loading...")).color(Color.lightGray).center().pad(20f).grow();

        String url = GITHUB_API_URL + "?page=" + page + "&per_page=" + perPage;

        Http.get(url).error(e -> {
            Log.err("Failed to fetch Aquarion releases", e);
            isLoading = false;
            Core.app.post(() -> {
                changelogListTable.clear();
                changelogListTable.add(Core.bundle.get("error.fetch-releases", "Failed to fetch releases.")).color(Color.scarlet).center().pad(20f).grow();
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
                        changelogListTable.add(Core.bundle.get("error.invalid-response", "Invalid response.")).color(Color.scarlet).center().pad(20f).grow();
                    });
                }
            } catch (Exception e) {
                Log.err("Failed to parse Aquarion releases", e);
                isLoading = false;
                Core.app.post(() -> {
                    changelogListTable.clear();
                    changelogListTable.add(Core.bundle.get("error.parse-failed", "Parse failed.")).color(Color.scarlet).center().pad(20f).grow();
                });
            }
        });
    }

    // Построение UI с релизами
    private void rebuildChangelogList(Seq<Jval> releases) {
        changelogListTable.clear();
        changelogListTable.top().left();

        if (releases.size == 0) {
            changelogListTable.add(Core.bundle.get("changelog.empty", "No releases found.")).color(Color.lightGray).center().pad(20f).grow();
            return;
        }

        float cardWidth = Vars.mobile ? 400f : 380f;

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
                t.top().left().margin(10f);

                t.table(header -> {
                    header.left();
                    header.add("[accent]" + name + "[white]").style(Styles.defaultLabel).growX().left();
                    header.add("[lightgray]" + tagName + "[white]").padLeft(10f);
                }).growX().row();

                t.table(stats -> {
                    stats.left();
                    stats.image(Icon.download).size(16f).color(Color.gold);
                    stats.add("[gold] " + finalDownloadCount + "[white]").padLeft(5f);
                }).padTop(5f).growX().row();

                t.image().color(Color.gray).height(2f).growX().padTop(5f).padBottom(5f).row();

                // Описание релиза
                t.add(body).wrap().width(cardWidth - 20f).left().padBottom(10f).row();

                // Кнопка для перехода к конкретному релизу на GitHub
                t.button(Core.bundle.get("aquarion.menu.open_release_tag", "View on GitHub"), Icon.export, () -> {
                    Core.app.openURI(htmlUrl);
                }).size(160f, 36f).right();

            }).width(cardWidth).padBottom(10f).row();
        }
    }

    private void showAuthorInfo(String name, String description, String profileUrl, String textureName, Drawable fallbackIcon, boolean hasProfile) {
        BaseDialog authorDialog = new BaseDialog(name);
        authorDialog.addCloseButton();

        float dialogWidth = Vars.mobile ? 400f : 440f;
        float dialogHeight = Vars.mobile ? 300f : 360f;

        authorDialog.cont.pane(t -> {
            t.left();

            Table leftTable = new Table();
            createRoundAvatar(leftTable, textureName, fallbackIcon, 120f);
            t.add(leftTable).top().padRight(15f);

            Table rightTable = new Table();
            rightTable.left();

            rightTable.add(name).left().fontScale(1.1f).row();

            var label = rightTable.add(description).width(dialogWidth - 150f).wrap().padTop(10f).left().get();
            label.setAlignment(arc.util.Align.left);

            t.add(rightTable).top().expandX().fillX();
        }).size(dialogWidth, dialogHeight);

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
