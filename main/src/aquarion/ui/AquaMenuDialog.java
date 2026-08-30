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

        // Оригинальные размеры диалога и основных кнопок
        float paneWidth = Vars.mobile ? 460f : 420f;
        float paneHeight = Vars.mobile ? 350f : 650f;
        float buttonWidth = Vars.mobile ? paneWidth - 50f : 380f;
        float buttonHeight = Vars.mobile ? 85f : 100f;

        // Навигация верхних табов (оригинал)
        Table nav = new Table();
        nav.button(Core.bundle.get("aquarion.menu.tab_links", "Links"), () -> updateContent("links"))
           .size(Vars.mobile ? 150f : 160f, 50f)
           .disabled(type.equals("links"));

        nav.button(Core.bundle.get("aquarion.menu.tab_credits", "Credits"), () -> updateContent("text"))
           .size(Vars.mobile ? 150f : 160f, 50f)
           .disabled(type.equals("text"));

        nav.button(Core.bundle.get("aquarion.menu.tab_changelog", "Changelog"), () -> updateContent("changelog"))
           .size(Vars.mobile ? 150f : 160f, 50f)
           .disabled(type.equals("changelog"));

        cont.add(nav).padBottom(15f).row();

        Table body = new Table();

        if (type.equals("links")) {
            var cell = body.pane(t -> {
                t.center();

                t.button(b -> {
                    createRoundAvatar(b, "github", Icon.github, 24f);
                    b.add(Core.bundle.get("aquarion.menu.link_github", "GitHub")).padLeft(10f);
                }, () -> Core.app.openURI("https://github.com/" + GITHUB_REPO))
                .size(buttonWidth, 60f).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "discord", Icon.discord, 24f);
                    b.add(Core.bundle.get("aquarion.menu.link_discord", "Discord")).padLeft(10f);
                }, () -> Core.app.openURI("https://discord.gg/SbFhxYD797"))
                .size(buttonWidth, 60f).padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "wiki", Icon.players, 24f);
                    b.add(Core.bundle.get("aquarion.menu.link_wiki", "Wiki")).padLeft(10f);
                }, () -> Core.app.openURI("https://nullotte.github.io/MindustryModWiki/aquarion"))
                .size(buttonWidth, 60f).padBottom(10f).row();

            }).size(paneWidth, Vars.mobile ? 230f : 250f);

            if (cell.get() instanceof ScrollPane) {
                ((ScrollPane)cell.get()).setFlickScroll(true);
            }

        } else if (type.equals("changelog")) {
            changelogListTable = new Table();
            changelogListTable.top().left();

            // Отдельные размеры исключительно для нижней панели Ченджлога
            float changelogBottomBtnWidth = Vars.mobile ? 380f : 360f;

            var cell = body.pane(t -> {
                t.add(changelogListTable).growX().row();
            }).size(paneWidth, paneHeight - (Vars.mobile ? 110f : 80f));

            if (cell.get() instanceof ScrollPane) {
                ((ScrollPane)cell.get()).setFlickScroll(true);
            }

            // Фиксированная нижняя панель Ченджлога
            Table bottomNav = new Table();
            
            bottomNav.button(Core.bundle.get("aquarion.menu.open_releases", "Open Releases on GitHub"), Icon.github, () -> {
                Core.app.openURI(RELEASES_URL);
            }).size(changelogBottomBtnWidth, 40f).padBottom(8f).row();

            Table paginationTable = new Table();
            float arrowSize = 40f;

            paginationTable.button(Icon.left, () -> {
                if (page > 1 && !isLoading) {
                    page--;
                    fetchReleases();
                }
            }).size(arrowSize).disabled(t -> page <= 1 || isLoading);

            paginationTable.label(() -> String.valueOf(page)).fontScale(1.2f).padLeft(18f).padRight(18f);

            paginationTable.button(Icon.right, () -> {
                if (hasNextPage && !isLoading) {
                    page++;
                    fetchReleases();
                }
            }).size(arrowSize).disabled(t -> !hasNextPage || isLoading);

            bottomNav.add(paginationTable);
            body.add(bottomNav).padTop(6f).row();

            fetchReleases();

        } else {
            var cell = body.pane(t -> {
                t.center();

                t.add(Core.bundle.get("aquarion.menu.role_creator")).color(Color.red).center().padBottom(10f).row();

                t.button(b -> {
                    createRoundAvatar(b, "Twcash", Icon.admin, 32f);
                    b.add("Twcash").left().padLeft(15f);
                }, () -> showAuthorInfo(
                    "Twcash", Core.bundle.get("aquarion.menu.desc_creator"),
                    "https://github.com/Twcash", "Twcash", Icon.admin, true
                )).size(buttonWidth, buttonHeight).padBottom(20f).row();

                t.add(Core.bundle.get("aquarion.menu.role_helpers")).color(Color.green).center().padBottom(10f).row();

                addAuthorButton(t, "NikolayKot02", "aquarion.menu.desc_NikolayKot", "https://github.com/NikolayKot02", "nikolaykot", Icon.players, true, buttonWidth, buttonHeight);
                addAuthorButton(t, "OwO (Sentinel)", "aquarion.menu.desc_OwO", "https://github.com/SentinelDart919", "OwO", Icon.players, true, buttonWidth, buttonHeight);
                addAuthorButton(t, "Alecthe2nd", "aquarion.menu.desc_Alecthe2nd", "https://github.com/alecthe2nd", "Alecthe2nd", Icon.players, true, buttonWidth, buttonHeight);
                addAuthorButton(t, "cupcakerouter", "aquarion.menu.desc_cupcakerouter", "", "cupcakerouter", Icon.players, false, buttonWidth, buttonHeight);
                addAuthorButton(t, "Vire", "aquarion.menu.desc_Vire", "https://github.com/VireVeonix", "Vire", Icon.players, true, buttonWidth, buttonHeight);
                addAuthorButton(t, "ItsKirby", "aquarion.menu.desc_ItsKirby", "https://github.com/ItsKirby69", "ItsKirby", Icon.players, true, buttonWidth, buttonHeight);
                addAuthorButton(t, "Plooey", "aquarion.menu.desc_Thinkerdoodle", "https://github.com/BSp-2", "thinkerdoodle", Icon.players, true, buttonWidth, buttonHeight);
                addAuthorButton(t, "Leo", "aquarion.menu.desc_Leo", "https://github.com/Leo-MathGuy", "Leo", Icon.players, true, buttonWidth, buttonHeight);
                addAuthorButton(t, "Mythril", "aquarion.menu.desc_Mythril", "https://github.com/Mythril382", "Mythril", Icon.players, true, buttonWidth, buttonHeight);
                addAuthorButton(t, "Andromeda-Galaxy29", "aquarion.menu.desc_Andromeda-Galaxy29", "https://github.com/Andromeda-Galaxy29", "Andromeda-Galaxy29", Icon.players, true, buttonWidth, buttonHeight);
                addAuthorButton(t, "Sputnuc", "aquarion.menu.desc_Sputnuc", "https://github.com/Sputnuc", "Sputnuc", Icon.players, true, buttonWidth, buttonHeight);
                addAuthorButton(t, "nullotte", "aquarion.menu.desc_nullotte", "https://github.com/nullotte", "nullotte", Icon.players, true, buttonWidth, buttonHeight);
                addAuthorButton(t, "kapzduke", "aquarion.menu.desc_kapzduke", "https://github.com/kapzduke", "kapzduke", Icon.players, true, buttonWidth, buttonHeight);
                addAuthorButton(t, "camelStyleUser", "aquarion.menu.desc_camelStyleUser", "https://github.com/camelStyleUser", "camelStyleUser", Icon.players, true, buttonWidth, buttonHeight);
                addAuthorButton(t, "Henan-CN-0921", "aquarion.menu.desc_Henan-CN-0921", "https://github.com/Henan-CN-0921", "Henan-CN-0921", Icon.players, true, buttonWidth, buttonHeight);
                addAuthorButton(t, "Norax", "aquarion.menu.desc_Norax", "https://github.com/Noraxx1", "Norax", Icon.players, true, buttonWidth, buttonHeight);

            }).size(paneWidth, paneHeight);

            if (cell.get() instanceof ScrollPane) {
                ((ScrollPane)cell.get()).setFlickScroll(true);
            }
        }

        cont.add(body).row();
    }

    private void addAuthorButton(Table table, String name, String descKey, String url, String texture, Drawable fallback, boolean hasProfile, float w, float h) {
        table.button(b -> {
            createRoundAvatar(b, texture, fallback, 32f);
            b.add(name).left().padLeft(15f);
        }, () -> showAuthorInfo(name, Core.bundle.get(descKey), url, texture, fallback, hasProfile))
        .size(w, h).padBottom(10f).row();
    }

    private void fetchReleases() {
        if (isLoading || changelogListTable == null) return;
        isLoading = true;

        changelogListTable.clear();
        changelogListTable.add(Core.bundle.get("loading", "Loading...")).color(Color.lightGray).center().pad(10f).grow();

        String url = GITHUB_API_URL + "?page=" + page + "&per_page=" + perPage;

        Http.get(url).error(e -> {
            Log.err("Failed to fetch Aquarion releases", e);
            isLoading = false;
            Core.app.post(() -> {
                changelogListTable.clear();
                changelogListTable.add(Core.bundle.get("error.fetch-releases", "Failed to fetch releases.")).color(Color.scarlet).center().pad(10f).grow();
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
                        changelogListTable.add(Core.bundle.get("error.invalid-response", "Invalid response.")).color(Color.scarlet).center().pad(10f).grow();
                    });
                }
            } catch (Exception e) {
                Log.err("Failed to parse Aquarion releases", e);
                isLoading = false;
                Core.app.post(() -> {
                    changelogListTable.clear();
                    changelogListTable.add(Core.bundle.get("error.parse-failed", "Parse failed.")).color(Color.scarlet).center().pad(10f).grow();
                });
            }
        });
    }

    private void rebuildChangelogList(Seq<Jval> releases) {
        changelogListTable.clear();
        changelogListTable.top().left();

        if (releases.size == 0) {
            changelogListTable.add(Core.bundle.get("changelog.empty", "No releases found.")).color(Color.lightGray).center().pad(10f).grow();
            return;
        }

        // Собственные независимые размеры карточек Ченджлога
        float changelogCardWidth = Vars.mobile ? 410f : 380f;

        for (Jval release : releases) {
            String tagName = release.getString("tag_name", "Unknown");
            String body = release.getString("body", "");
            String name = release.getString("name", tagName);
            String htmlUrl = release.getString("html_url", RELEASES_URL);
            
            // Считаем общее количество скачиваний до лямбда-выражений
            int totalDownloads = 0;
            String downloadUrl = htmlUrl;

            if (release.has("assets") && release.get("assets").asArray().size > 0) {
                Jval firstAsset = release.get("assets").asArray().first();
                downloadUrl = firstAsset.getString("browser_download_url", htmlUrl);

                for (Jval asset : release.get("assets").asArray()) {
                    totalDownloads += asset.getInt("download_count", 0);
                }
            }

            final String finalDownloadUrl = downloadUrl;
            final int finalDownloadCount = totalDownloads; // Переменная для использования в лямбде

            changelogListTable.table(Styles.black5, t -> {
                t.top().left().margin(10f);

                t.table(header -> {
                    header.left();
                    header.add("[accent]" + name + "[white]").style(Styles.defaultLabel).growX().left();
                    header.add("[lightgray]" + tagName + "[white]").padLeft(8f);
                }).growX().row();

                t.table(stats -> {
                    stats.left();
                    stats.image(Icon.download).size(16f).color(Color.gold);
                    stats.add("[gold] " + finalDownloadCount + "[white]").padLeft(4f); // Используем finalDownloadCount
                }).padTop(4f).growX().row();

                t.image().color(Color.gray).height(1f).growX().padTop(6f).padBottom(8f).row();

                t.add(body).wrap().width(changelogCardWidth - 30f).left().padBottom(10f).row();

                t.table(actions -> {
                    actions.right();

                    actions.button(Core.bundle.get("aquarion.menu.download_release", "Download"), Icon.download, () -> {
                        Core.app.openURI(finalDownloadUrl);
                    }).height(36f).padRight(8f);

                    actions.button(Core.bundle.get("aquarion.menu.open_release_tag", "View on GitHub"), Icon.export, () -> {
                        Core.app.openURI(htmlUrl);
                    }).height(36f);

                }).growX().right();

            }).width(changelogCardWidth).padBottom(12f).row();
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
            authorDialog.buttons.button(Core.bundle.get("aquarion.menu.open_profile", "Open Profile"), () -> {
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
