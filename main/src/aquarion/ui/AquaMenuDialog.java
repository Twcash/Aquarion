package aquarion.ui;

import arc.*;
import arc.files.Fi;
import arc.graphics.Color;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.*;
import arc.scene.ui.ScrollPane;
import arc.struct.Seq;
import arc.util.Http;
import arc.util.Log;
import arc.util.Threads;
import arc.util.serialization.Jval;
import mindustry.Vars;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.gen.Icon;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AquaMenuDialog extends BaseDialog {

    private int page = 1;
    private final int perPage = 10;
    private Table changelogListTable;
    private boolean isLoading = false;
    private boolean hasNextPage = true;

    private boolean isCancelled = false;
    private float downloadProgress = 0f;
    private String progressText = "";

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

        float buttonWidth = Vars.mobile ? 410f : 410f;
        float buttonHeight = Vars.mobile ? 85f : 100f;
        float avatarSize = 32f;
        float padSize = 10f;

        Table nav = new Table();

        nav.button(Core.bundle.get("aquarion.menu.tab_links", "Links"), () -> updateContent("links"))
           .height(50f).growX().padRight(4f)
           .disabled(type.equals("links"));

        nav.button(Core.bundle.get("aquarion.menu.tab_credits", "Credits"), () -> updateContent("text"))
           .height(50f).growX().padRight(4f)
           .disabled(type.equals("text"));

        nav.button(Core.bundle.get("aquarion.menu.tab_changelog", "Changelog"), () -> updateContent("changelog"))
           .height(50f).growX()
           .disabled(type.equals("changelog"));

        cont.add(nav).width(buttonWidth).padBottom(15f).row();

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

            ScrollPane pane = new ScrollPane(inner);
            pane.setOverscroll(false, false);
            cont.add(pane).width(Vars.mobile ? 460f : 420f).row();

        } else if (type.equals("changelog")) {
            changelogListTable = new Table();
            changelogListTable.top().left();

            inner.add(changelogListTable).growX().row();

            ScrollPane pane = new ScrollPane(inner);
            pane.setOverscroll(false, false);
            cont.add(pane).width(400f).row();

            Table bottomNav = new Table();
            bottomNav.button(Core.bundle.get("aquarion.menu.open_releases", "Open Releases on GitHub"), Icon.github, () -> {
                Core.app.openURI(RELEASES_URL);
            }).size(buttonWidth, Vars.mobile ? 40f : 34f).padTop(4f).padBottom(4f).row();

            Table paginationTable = new Table();
            float arrowSize = Vars.mobile ? 36f : 30f;

            paginationTable.button(Icon.left, () -> {
                if (page > 1 && !isLoading) {
                    page--;
                    fetchReleases();
                }
            }).size(arrowSize).disabled(t -> page <= 1 || isLoading);

            paginationTable.label(() -> String.valueOf(page)).fontScale(1.1f).padLeft(12f).padRight(12f);

            paginationTable.button(Icon.right, () -> {
                if (hasNextPage && !isLoading) {
                    page++;
                    fetchReleases();
                }
            }).size(arrowSize).disabled(t -> !hasNextPage || isLoading);

            bottomNav.add(paginationTable);
            cont.add(bottomNav).padTop(4f).row();

            fetchReleases();

        } else {
            inner.center();

            inner.add(Core.bundle.get("aquarion.menu.role_creator")).color(Color.red).center().padBottom(padSize).row();

            inner.button(b -> {
                createRoundAvatar(b, "Twcash", Icon.admin, avatarSize);
                b.add("Twcash").left().padLeft(8f);
            }, () -> showAuthorInfo(
                "Twcash", Core.bundle.get("aquarion.menu.desc_creator"),
                "https://github.com/Twcash", "Twcash", Icon.admin, true
            )).size(buttonWidth, buttonHeight).padBottom(padSize).row();

            inner.add(Core.bundle.get("aquarion.menu.role_helpers")).color(Color.green).center().padBottom(20).row();

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

            ScrollPane pane = new ScrollPane(inner);
            pane.setOverscroll(false, false);
            cont.add(pane).width(Vars.mobile ? 460f : 420f).row();
        }
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

        for (Jval release : releases) {
            String tagName = release.getString("tag_name", "Unknown");
            String body = release.getString("body", "");
            String name = release.getString("name", tagName);
            String htmlUrl = release.getString("html_url", RELEASES_URL);
            int downloadCount = 0;
            String downloadUrl = htmlUrl;

            if (release.has("assets") && release.get("assets").asArray().size > 0) {
                Jval firstAsset = release.get("assets").asArray().first();
                downloadUrl = firstAsset.getString("browser_download_url", htmlUrl);

                for (Jval asset : release.get("assets").asArray()) {
                    downloadCount += asset.getInt("download_count", 0);
                }
            }

            final int finalDownloadCount = downloadCount;
            final String finalDownloadUrl = downloadUrl;
            final String finalTagName = tagName;

            changelogListTable.table(Styles.black5, t -> {
                t.top().left().margin(8f);

                t.table(header -> {
                    header.left();
                    header.add("[accent]" + name + "[white]").style(Styles.defaultLabel).growX().left();
                    header.add("[lightgray]" + finalTagName + "[white]").padLeft(8f);
                }).growX().row();

                t.table(stats -> {
                    stats.left();
                    stats.image(Icon.download).size(14f).color(Color.gold);
                    stats.add("[gold] " + finalDownloadCount + "[white]").padLeft(4f);
                }).padTop(2f).growX().row();

                t.image().color(Color.gray).height(1f).growX().padTop(4f).padBottom(6f).row();

                t.add(body).wrap().width(340f).left().padBottom(8f).row();

                t.table(actions -> {
                    actions.right();
                    
                    float actionBtnHeight = Vars.mobile ? 48f : 48f;

                    actions.button(Core.bundle.get("aquarion.menu.download_release", "Download"), Icon.download, () -> {
                        downloadAndInstall(finalDownloadUrl, finalTagName);
                    }).width(160f).height(actionBtnHeight).padRight(8f);

                    actions.button(Core.bundle.get("aquarion.menu.open_release_tag", "View on GitHub"), Icon.export, () -> {
                        Core.app.openURI(htmlUrl);
                    }).width(140f).height(actionBtnHeight);

                }).growX().right();

            }).width(370f).padBottom(4f).row();
        }
    }

    private void downloadAndInstall(String urlString, String version) {
        isCancelled = false;
        downloadProgress = 0f;
        progressText = "0%";

        String dialogTitle = Core.bundle.format("aquarion.changelog.titledownl", version);
        BaseDialog progressDialog = new BaseDialog(dialogTitle);
        
        String downloadingText = Core.bundle.format("aquarion.changelog.downloading_text", version);
        progressDialog.cont.add(downloadingText).pad(10f).row();

        Color startColor = Color.valueOf("#ff0000");
        Color endColor = Color.valueOf("#ffd37f");
        Color currentColor = new Color();

        Bar progressBar = new Bar(
                () -> progressText,
                () -> currentColor.set(startColor).lerp(endColor, downloadProgress),
                () -> downloadProgress
        );

        progressDialog.cont.add(progressBar).size(400f, 40f).pad(10f).row();

        progressDialog.buttons.button(Core.bundle.get("aquarion.changelog.cancel", "Cancel"), () -> {
            isCancelled = true;
            progressDialog.hide();
        }).size(150f, 60f).pad(10f);

        progressDialog.show();

        Threads.daemon(() -> {
            HttpURLConnection connection = null;
            InputStream input = null;
            OutputStream output = null;

            Fi tempFile = Core.files.local("cache/aquarion_tmp.jar");

            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mindustry-Mod-Updater");
                connection.setInstanceFollowRedirects(true);
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP ||
                            connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM) {
                        String newUrl = connection.getHeaderField("Location");
                        connection.disconnect();
                        url = new URL(newUrl);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestProperty("User-Agent", "Mindustry-Mod-Updater");
                        connection.connect();
                    } else {
                        throw new Exception("HTTP " + connection.getResponseCode());
                    }
                }

                long fileLength = connection.getContentLengthLong();
                input = new BufferedInputStream(connection.getInputStream());
                output = tempFile.write(false);

                byte[] data = new byte[4096];
                long total = 0;
                int count;

                while ((count = input.read(data)) != -1) {
                    if (isCancelled) break;
                    total += count;

                    if (fileLength > 0) {
                        float progress = (float) total / fileLength;
                        int percent = (int) (progress * 100);

                        downloadProgress = progress;
                        progressText = percent + "%";
                    }
                    output.write(data, 0, count);
                }

                output.close();
                output = null;
                input.close();
                input = null;

                if (!isCancelled) {
                    Core.app.post(() -> {
                        try {
                            progressDialog.hide();

                            mindustry.mod.Mods.LoadedMod oldMod = Vars.mods.getMod("aquarion");
                            if (oldMod != null) {
                                Vars.mods.removeMod(oldMod);
                            }

                            Vars.mods.importMod(tempFile);
                            Vars.mods.reload();

                            tempFile.delete();
                            showSuccessDialog(version);
                        } catch (Exception e) {
                            Log.err("[AquarionUpdate] Import error", e);
                            Vars.ui.showException(Core.bundle.format("aquarion.changelog.install_error", version), e);
                        }
                    });
                } else {
                    tempFile.delete();
                }

            } catch (Exception e) {
                Log.err("[AquarionUpdate] Error", e);
                if (output != null) {
                    try { output.close(); } catch (Exception ignored) {}
                }
                tempFile.delete();

                if (!isCancelled) {
                    Core.app.post(() -> {
                        progressDialog.hide();
                        Vars.ui.showException(Core.bundle.format("aquarion.changelog.install_error", version), e);
                    });
                }
            } finally {
                try {
                    if (input != null) input.close();
                } catch (Exception ignored) {}
                if (connection != null) connection.disconnect();
            }
        });
    }

    private void showSuccessDialog(String version) {
        BaseDialog successDialog = new BaseDialog(Core.bundle.format("aquarion.changelog.success_title", version));
        String successMessage = Core.bundle.format("aquarion.changelog.success_text", version);
        successDialog.cont.add(successMessage).pad(20).row();

        successDialog.buttons.button(Core.bundle.get("aquarion.changelog.ok", "Ok"), Core.app::exit).size(150f, 60f);

        successDialog.show();
    }

    private void showAuthorInfo(String name, String description, String profileUrl, String textureName, Drawable fallbackIcon, boolean hasProfile) {
        BaseDialog authorDialog = new BaseDialog(name);
        authorDialog.addCloseButton();
        float dialogWidth1 = Vars.mobile ? 400f : 440f;
        float dialogHeight2 = Vars.mobile ? 300f : 360f;

        authorDialog.cont.pane(t -> {
            t.left();

            Table leftTable = new Table();
            createRoundAvatar(leftTable, textureName, fallbackIcon, 140f);
            t.add(leftTable).top().padRight(15f);

            Table rightTable = new Table();
            rightTable.left();

            rightTable.add(name).left().fontScale(1.1f).row();

            var label = rightTable.add(description).width(dialogWidth1 - 150f).wrap().padTop(10f).left().get();
            label.setAlignment(arc.util.Align.left);

            t.add(rightTable).top().expandX().fillX();
        }).size(dialogWidth1, dialogHeight2);

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
