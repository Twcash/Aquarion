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

        boolean isPortrait = Core.graphics.isPortrait();
        float screenH = Core.graphics.getHeight();
        float screenW = Core.graphics.getWidth();

        // Динамические размеры диалога под пропорции экрана
        float dialogWidth = Vars.mobile ? (isPortrait ? screenW * 0.9f : 460f) : 480f;
        float maxContentHeight = isPortrait ? screenH * 0.55f : screenH * 0.45f;

        float buttonWidth = dialogWidth - 40f;
        float buttonHeight = isPortrait ? 50f : 36f;
        float avatarSize = isPortrait ? 26f : 20f;
        float padSize = isPortrait ? 6f : 3f;
        float navBtnWidth = isPortrait ? 105f : 90f;
        float navBtnHeight = isPortrait ? 38f : 30f;

        // 1. Верхняя панель переключения вкладок (всегда видна)
        Table nav = new Table();
        nav.button(Core.bundle.get("aquarion.menu.tab_links", "Links"), () -> updateContent("links"))
           .size(navBtnWidth, navBtnHeight)
           .disabled(type.equals("links"));

        nav.button(Core.bundle.get("aquarion.menu.tab_credits", "Credits"), () -> updateContent("text"))
           .size(navBtnWidth, navBtnHeight)
           .disabled(type.equals("text"));

        nav.button(Core.bundle.get("aquarion.menu.tab_changelog", "Changelog"), () -> updateContent("changelog"))
           .size(navBtnWidth, navBtnHeight)
           .disabled(type.equals("changelog"));

        cont.add(nav).padBottom(padSize).row();

        // 2. Внутренняя таблица с содержимым
        Table inner = new Table();

        if (type.equals("links")) {
            inner.center();
            inner.button(b -> {
                createRoundAvatar(b, "github", Icon.github, avatarSize);
                b.add(Core.bundle.get("aquarion.menu.link_github")).padLeft(8f);
            }, () -> Core.app.openURI("https://github.com/" + GITHUB_REPO))
            .size(buttonWidth, buttonHeight).padBottom(padSize).row();

            inner.button(b -> {
                createRoundAvatar(b, "discord", Icon.discord, avatarSize);
                b.add(Core.bundle.get("aquarion.menu.link_discord")).padLeft(8f);
            }, () -> Core.app.openURI("https://discord.gg/SbFhxYD797"))
            .size(buttonWidth, buttonHeight).padBottom(padSize).row();

            inner.button(b -> {
                createRoundAvatar(b, "wiki", Icon.players, avatarSize);
                b.add(Core.bundle.get("aquarion.menu.link_wiki")).padLeft(8f);
            }, () -> Core.app.openURI("https://nullotte.github.io/MindustryModWiki/aquarion"))
            .size(buttonWidth, buttonHeight).padBottom(padSize).row();

        } else if (type.equals("changelog")) {
            changelogListTable = new Table();
            changelogListTable.top().left();

            inner.add(changelogListTable).growX().row();

            inner.button(Core.bundle.get("aquarion.menu.open_releases", "Open Releases on GitHub"), Icon.github, () -> {
                Core.app.openURI(RELEASES_URL);
            }).size(buttonWidth, navBtnHeight).padTop(padSize).padBottom(padSize).row();

            Table paginationTable = new Table();
            paginationTable.button(Icon.left, () -> {
                if (page > 1 && !isLoading) {
                    page--;
                    fetchReleases();
                }
            }).size(30f).disabled(t -> page <= 1 || isLoading);

            paginationTable.label(() -> String.valueOf(page)).fontScale(0.9f).padLeft(8f).padRight(8f);

            paginationTable.button(Icon.right, () -> {
                if (hasNextPage && !isLoading) {
                    page++;
                    fetchReleases();
                }
            }).size(30f).disabled(t -> !hasNextPage || isLoading);

            inner.add(paginationTable);
            fetchReleases();

        } else {
            // Вкладка авторов (Credits)
            inner.center();

            inner.add(Core.bundle.get("aquarion.menu.role_creator")).color(Color.red).center().padBottom(padSize).row();

            inner.button(b -> {
                createRoundAvatar(b, "Twcash", Icon.admin, avatarSize);
                b.add("Twcash").left().padLeft(8f);
            }, () -> showAuthorInfo(
                "Twcash", Core.bundle.get("aquarion.menu.desc_creator"),
                "https://github.com/Twcash", "Twcash", Icon.admin, true
            )).size(buttonWidth, buttonHeight).padBottom(padSize).row();

            inner.add(Core.bundle.get("aquarion.menu.role_helpers")).color(Color.green).center().padBottom(padSize).row();

            addAuthorButton(inner, "NikolayKot02", "aquarion.menu.desc_NikolayKot", "https://github.com/NikolayKot02", "nikolaykot", Icon.players, true, buttonWidth, buttonHeight, avatarSize, padSize);
            addAuthorButton(inner, "OwO (Sentinel)", "aquarion.menu.desc_OwO", "https://github.com/SentinelDart919", "OwO", Icon.players, true, buttonWidth, buttonHeight, avatarSize, padSize);
            addAuthorButton(inner, "Alecthe2nd", "aquarion.menu.desc_Alecthe2nd", "https://github.com/alecthe2nd", "Alecthe2nd", Icon.players, true, buttonWidth, buttonHeight, avatarSize, padSize);
            addAuthorButton(inner, "cupcakerouter", "aquarion.menu.desc_cupcakerouter", "", "cupcakerouter", Icon.players, false, buttonWidth, buttonHeight, avatarSize, padSize);
            addAuthorButton(inner, "Vire", "aquarion.menu.desc_Vire", "https://github.com/VireVeonix", "Vire", Icon.players, true, buttonWidth, buttonHeight, avatarSize, padSize);
            addAuthorButton(inner, "ItsKirby", "aquarion.menu.desc_ItsKirby", "https://github.com/ItsKirby69", "ItsKirby", Icon.players, true, buttonWidth, buttonHeight, avatarSize, padSize);
            addAuthorButton(inner, "Plooey", "aquarion.menu.desc_Thinkerdoodle", "https://github.com/BSp-2", "thinkerdoodle", Icon.players, true, buttonWidth, buttonHeight, avatarSize, padSize);
            addAuthorButton(inner, "Leo", "aquarion.menu.desc_Leo", "https://github.com/Leo-MathGuy", "Leo", Icon.players, true, buttonWidth, buttonHeight, avatarSize, padSize);
            addAuthorButton(inner, "Mythril", "aquarion.menu.desc_Mythril", "https://github.com/Mythril382", "Mythril", Icon.players, true, buttonWidth, buttonHeight, avatarSize, padSize);
            addAuthorButton(inner, "Andromeda-Galaxy29", "aquarion.menu.desc_Andromeda-Galaxy29", "https://github.com/Andromeda-Galaxy29", "Andromeda-Galaxy29", Icon.players, true, buttonWidth, buttonHeight, avatarSize, padSize);
            addAuthorButton(inner, "Sputnuc", "aquarion.menu.desc_Sputnuc", "https://github.com/Sputnuc", "Sputnuc", Icon.players, true, buttonWidth, buttonHeight, avatarSize, padSize);
            addAuthorButton(inner, "nullotte", "aquarion.menu.desc_nullotte", "https://github.com/nullotte", "nullotte", Icon.players, true, buttonWidth, buttonHeight, avatarSize, padSize);
            addAuthorButton(inner, "kapzduke", "aquarion.menu.desc_kapzduke", "https://github.com/kapzduke", "kapzduke", Icon.players, true, buttonWidth, buttonHeight, avatarSize, padSize);
            addAuthorButton(inner, "camelStyleUser", "aquarion.menu.desc_camelStyleUser", "https://github.com/camelStyleUser", "camelStyleUser", Icon.players, true, buttonWidth, buttonHeight, avatarSize, padSize);
            addAuthorButton(inner, "Henan-CN-0921", "aquarion.menu.desc_Henan-CN-0921", "https://github.com/Henan-CN-0921", "Henan-CN-0921", Icon.players, true, buttonWidth, buttonHeight, avatarSize, padSize);
            addAuthorButton(inner, "Norax", "aquarion.menu.desc_Norax", "https://github.com/Noraxx1", "Norax", Icon.players, true, buttonWidth, buttonHeight, avatarSize, padSize);
        }

        // Зажимаем только внутренний список элементов в максимальную высоту, чтобы диалог сжимался пропорционально
        ScrollPane pane = new ScrollPane(inner);
        pane.setOverscroll(false, false);
        cont.add(pane).size(dialogWidth, maxContentHeight).row();
    }

    private void addAuthorButton(Table table, String name, String descKey, String url, String texture, Drawable fallback, boolean hasProfile, float w, float h, float avatarSize, float pad) {
        table.button(b -> {
            createRoundAvatar(b, texture, fallback, avatarSize);
            b.add(name).left().padLeft(8f);
        }, () -> showAuthorInfo(name, Core.bundle.get(descKey), url, texture, fallback, hasProfile))
        .size(w, h).padBottom(pad).row();
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

        boolean isPortrait = Core.graphics.isPortrait();
        float cardWidth = Vars.mobile ? (isPortrait ? 380f : 400f) : 360f;

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
                t.top().left().margin(6f);

                t.table(header -> {
                    header.left();
                    header.add("[accent]" + name + "[white]").style(Styles.defaultLabel).growX().left();
                    header.add("[lightgray]" + tagName + "[white]").padLeft(8f);
                }).growX().row();

                t.table(stats -> {
                    stats.left();
                    stats.image(Icon.download).size(12f).color(Color.gold);
                    stats.add("[gold] " + finalDownloadCount + "[white]").padLeft(4f);
                }).padTop(2f).growX().row();

                t.image().color(Color.gray).height(1f).growX().padTop(3f).padBottom(3f).row();

                t.add(body).wrap().width(cardWidth - 25f).left().padBottom(6f).row();

                t.button(Core.bundle.get("aquarion.menu.open_release_tag", "View on GitHub"), Icon.export, () -> {
                    Core.app.openURI(htmlUrl);
                }).size(120f, 26f).right();

            }).width(cardWidth).padBottom(6f).row();
        }
    }

    private void showAuthorInfo(String name, String description, String profileUrl, String textureName, Drawable fallbackIcon, boolean hasProfile) {
        BaseDialog authorDialog = new BaseDialog(name);
        authorDialog.addCloseButton();

        boolean isPortrait = Core.graphics.isPortrait();
        float dialogWidth = Vars.mobile ? (isPortrait ? 360f : 400f) : 380f;
        float dialogHeight = isPortrait ? 240f : 160f;

        authorDialog.cont.pane(t -> {
            t.left();

            Table leftTable = new Table();
            createRoundAvatar(leftTable, textureName, fallbackIcon, isPortrait ? 60f : 44f);
            t.add(leftTable).top().padRight(10f);

            Table rightTable = new Table();
            rightTable.left();

            rightTable.add(name).left().fontScale(1.0f).row();

            var label = rightTable.add(description).width(dialogWidth - 110f).wrap().padTop(4f).left().get();
            label.setAlignment(arc.util.Align.left);

            t.add(rightTable).top().expandX().fillX();
        }).size(dialogWidth, dialogHeight);

        if (hasProfile) {
            authorDialog.buttons.button(Core.bundle.get("aquarion.menu.open_profile"), () -> {
                Core.app.openURI(profileUrl);
            }).size(Vars.mobile ? 130f : 150f, 34f);
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
