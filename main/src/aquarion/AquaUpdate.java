package aquarion;

import arc.*;
import arc.files.Fi;
import arc.scene.event.Touchable;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import arc.util.*;
import arc.util.serialization.*;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.gen.Tex;
import mindustry.mod.Mod;
import mindustry.ui.Bar;
import mindustry.ui.dialogs.BaseDialog;
import arc.scene.ui.CheckBox;
import arc.scene.ui.Tooltip;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.Image;
import arc.scene.style.TextureRegionDrawable;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AquaUpdate {

    public static final String GITHUB_REPO = "Twcash/Aquarion";

    private String currentVersion = "unknown";
    private boolean isCancelled = false;

    private float downloadProgress = 0f;
    private String progressText = "";
    private String releaseNotes = "";
    private int downloadCount = 0;
    private Table disclaimerBanner;

    public void check(Class<? extends Mod> mainClass) {
        mindustry.mod.Mods.LoadedMod modContainer = Vars.mods.getMod(mainClass);
        if (modContainer != null && modContainer.meta != null) {
            currentVersion = modContainer.meta.version.trim();
        }

        checkUpdates();
    }

    private void checkUpdates() {
        showDisclaimer();
        Http.get("https://api.github.com/repos/" + GITHUB_REPO + "/releases/latest", response -> {
            try {
                Jval json = Jval.read(response.getResultAsString());
                String remoteVersion = json.getString("tag_name", "").trim();
                releaseNotes = json.getString("body", "");
                if (remoteVersion.isEmpty() ||
                        compareVersions(remoteVersion, currentVersion) <= 0) {
                    return;
                }
                Jval assets = json.get("assets");
                if (assets == null || !assets.isArray() || assets.asArray().size == 0) {
                    return;
                }
                String downloadUrl = null;
                for (Jval asset : assets.asArray()) {
                    String name = asset.getString("name", "");
                    if (name.endsWith(".jar")) {
                        downloadUrl = asset.getString("browser_download_url", null);
                        downloadCount = asset.getInt("download_count", 0);
                        break;
                    }
                }
                if (downloadUrl == null) return;
                final String finalUrl = downloadUrl;

                // Fetch only mod.hjson (tiny file) instead of the whole JAR for compatibility check
                String rawMetaUrl = "https://raw.githubusercontent.com/" + GITHUB_REPO + "/" + remoteVersion + "/mod.hjson";
                Http.get(rawMetaUrl, metaResponse -> {
                    boolean compatible = true;
                    try {
                        Jval meta = Jval.read(metaResponse.getResultAsString());
                        String minGameVersion = meta.getString("minGameVersion", "");
                        if (!minGameVersion.isEmpty()) {
                            int required = Integer.parseInt(minGameVersion.trim());
                            compatible = Version.build >= required;
                        }
                    } catch (Exception ignored) {
                        // If meta parse fails, assume compatible
                    }
                    if (compatible) {
                        Core.app.post(() -> showUpdateDialog(remoteVersion, finalUrl));
                    } else {
                        Log.info("[AquarionUpdate] Ignoring update - requires newer Mindustry version.");
                    }
                }, err -> {
                    // If raw meta fetch fails (e.g. file not at expected path), show dialog anyway
                    Log.info("[AquarionUpdate] Could not fetch remote mod.hjson, skipping compat check: " + err.getMessage());
                    Core.app.post(() -> showUpdateDialog(remoteVersion, finalUrl));
                });

            } catch (Exception e) {
                Log.err("[AquarionUpdate] Error", e);
            }
        }, error -> Log.err("[AquarionUpdate] Network error: " + error.getMessage()));
    }

    //This is so stupid. Too late to switch to whole numbers now...
    private int compareVersions(String v1, String v2) {
        try {
            String[] a = v1.split("\\.");
            String[] b = v2.split("\\.");

            int max = Math.max(a.length, b.length);

            for (int i = 0; i < max; i++) {
                int n1 = i < a.length ? Integer.parseInt(a[i]) : 0;
                int n2 = i < b.length ? Integer.parseInt(b[i]) : 0;

                if (n1 > n2) return 1;
                if (n1 < n2) return -1;
            }

            return 0;
        } catch (Exception e) {
            Log.err("[AquarionUpdate] Failed version compare", e);
            return v1.equals(v2) ? 0 : -1;
        }
    }

    private void showDisclaimer() {
        if (!Vars.state.isMenu()) return;
        if (Core.settings.getBool("aquarion.disclaimer.dismissed", false)) {
            return;
        }

        if (disclaimerBanner != null) {
            disclaimerBanner.remove();
        }

        disclaimerBanner = new Table(Tex.pane2);
        disclaimerBanner.touchable = Touchable.enabled;
        disclaimerBanner.margin(12f);

        String disclaimerTitle = Core.bundle.get("aquarion.disclaimer.title");
        String disclaimerText = Core.bundle.get("aquarion.disclaimer.text");

        Table header = new Table();
        header.add(disclaimerTitle)
                .fontScale(1.2f)
                .left()
                .growX();

        TextButton close = new TextButton(Core.bundle.get("aquarion.disclaimer.close"));
        close.clicked(() -> {
            Core.settings.put("aquarion.disclaimer.dismissed", true);
            Core.settings.manualSave();
            disclaimerBanner.remove();
            disclaimerBanner = null;
        });

        header.add(close).size(40f, 40f).right();

        disclaimerBanner.add(header).growX().row();
        disclaimerBanner.add(disclaimerText)
                .wrap()
                .width(500f)
                .left();

        Core.scene.add(disclaimerBanner);

        disclaimerBanner.pack();

        disclaimerBanner.setPosition(
                (Core.graphics.getWidth() - disclaimerBanner.getWidth()) / 2f,
                Core.graphics.getHeight() - disclaimerBanner.getHeight() -150f
        );

        disclaimerBanner.update(() -> {
            if (!Vars.state.isMenu()) {
                disclaimerBanner.remove();
                disclaimerBanner = null;
            }
        });
    }
    private void showUpdateDialog(String newVersion, String downloadUrl) {
        BaseDialog dialog = new BaseDialog(Core.bundle.get("aquarion.update.title"));

        String headerText = Core.bundle.get("aquarion.update.header");
        dialog.cont.label(() -> headerText).padBottom(25f).row();

        String message = Core.bundle.format("aquarion.update.message", currentVersion, newVersion);
        dialog.cont.add(message).padBottom(4f).row();

        String downloadsText = Core.bundle.format("aquarion.update.downloads", downloadCount);
        dialog.cont.add(downloadsText).color(Color.lightGray).fontScale(0.85f).padBottom(20f).row();

        CheckBox checkBox = new CheckBox(Core.bundle.get("settings.showUpdates"));
        checkBox.setChecked(true);

        checkBox.changed(() -> {
            Core.settings.put("showUpdates", checkBox.isChecked());
            Core.settings.manualSave();
        });

        dialog.cont.add(checkBox).padBottom(5f).row();

        if (Vars.mobile) {
            dialog.cont.add(Core.bundle.get("aquarion.update.hint_settings"))
                    .color(Color.lightGray)
                    .fontScale(0.85f)
                    .wrap()
                    .width(400f)
                    .padBottom(15f)
                    .row();
        } else {
            checkBox.addListener(new Tooltip(t -> {
                t.background(mindustry.gen.Tex.button)
                        .add(Core.bundle.get("aquarion.update.hint_settings"))
                        .wrap()
                        .width(400f)
                        .pad(8f);
            }));
            checkBox.getCell(checkBox.getLabel()).padBottom(15f);
            dialog.cont.row();
        }

        dialog.buttons.button(Core.bundle.get("aquarion.update.download"), () -> {
            dialog.hide();
            downloadAndInstall(downloadUrl);
        }).size(160f, 60f);

        dialog.buttons.button(Core.bundle.get("aquarion.update.changelog"), this::showChangelogDialog).size(160f, 60f);

        dialog.buttons.button(Core.bundle.get("aquarion.update.cancel"), dialog::hide).size(130f, 60f);
        dialog.show();
    }

    private String fixGithubImageUrl(String url) {
        if (url == null) return null;
        if (url.contains("github.com") && url.contains("user-attachments/assets")) {
            return url.replace("github.com", "raw.githubusercontent.com").replace("/user-attachments/assets/", "/main/user-attachments/assets/");
        }
        return url;
    }

    private String parseImageUrl(String markdown) {
        if (markdown == null || markdown.isEmpty()) return null;

        Pattern htmlTagPattern = Pattern.compile("<img[^>]+src\\s*=\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher htmlMatcher = htmlTagPattern.matcher(markdown);
        if (htmlMatcher.find()) {
            return fixGithubImageUrl(htmlMatcher.group(1).trim());
        }

        Pattern mdTagPattern = Pattern.compile("!\\[[^]]*]\\(([^)]+)\\)");
        Matcher mdMatcher = mdTagPattern.matcher(markdown);
        if (mdMatcher.find()) {
            return fixGithubImageUrl(mdMatcher.group(1).trim().split(" ")[0]);
        }

        return null;
    }

    private void showChangelogDialog() {
        BaseDialog logDialog = new BaseDialog(Core.bundle.get("aquarion.update.changelog"));
        String imageUrl = parseImageUrl(releaseNotes);

        logDialog.cont.pane(table -> {
            if (imageUrl != null) {
                Http.get(imageUrl, imgResponse -> {
                    byte[] bytes = imgResponse.getResult();
                    Core.app.post(() -> {
                        try {
                            Texture texture = new Texture(new arc.graphics.Pixmap(bytes));
                            Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));

                            float maxW = Vars.mobile ? Core.graphics.getWidth() * 0.65f : 400f;
                            float maxH = Vars.mobile ? Core.graphics.getHeight() * 0.25f : 240f;

                            table.add(image).maxWidth(maxW).maxHeight(maxH).scaling(arc.util.Scaling.fit).padBottom(15f).row();
                            table.add(releaseNotes.isEmpty() ? "No description provided." : releaseNotes).left().wrap().width(400f);
                        } catch (Exception e) {
                            Log.err("[AquarionUpdate] Failed to load changelog image", e);
                            table.add(releaseNotes.isEmpty() ? "No description provided." : releaseNotes).left().wrap().width(400f);
                        }
                    });
                }, imgError -> {
                    Log.err("[AquarionUpdate] Failed to download changelog image: " + imgError.getMessage());
                    Core.app.post(() -> table.add(releaseNotes.isEmpty() ? "No description provided." : releaseNotes).left().wrap().width(400f));
                });
            } else {
                table.add(releaseNotes.isEmpty() ? "No description provided." : releaseNotes).left().wrap().width(400f);
            }
        }).size(450f, 320f).pad(10f).row();

        logDialog.buttons.button(Core.bundle.get("aquarion.update.open_link"),
                () -> Core.app.openURI("https://github.com/" + GITHUB_REPO + "/releases/latest")).size(180f, 60f);

        logDialog.buttons.button(Core.bundle.get("aquarion.update.back"), logDialog::hide).size(150f, 60f);
        logDialog.show();
    }

    private void downloadAndInstall(String urlString) {
        isCancelled = false;
        downloadProgress = 0f;
        progressText = "0%";

        BaseDialog progressDialog = new BaseDialog(Core.bundle.get("aquarion.update.titledownl"));
        progressDialog.cont.add(Core.bundle.get("aquarion.update.downloading_text")).pad(10f).row();

        Color startColor = Color.valueOf("#ff0000");
        Color endColor = Color.valueOf("#ffd37f");
        Color currentColor = new Color();

        Bar progressBar = new Bar(
                () -> progressText,
                () -> currentColor.set(startColor).lerp(endColor, downloadProgress),
                () -> downloadProgress
        );

        progressDialog.cont.add(progressBar).size(400f, 40f).pad(10f).row();

        progressDialog.buttons.button(Core.bundle.get("aquarion.update.cancel"), () -> {
            isCancelled = true;
            progressDialog.hide();
        }).size(150f, 60f).pad(10f);

        progressDialog.show();

        Threads.daemon(() -> {
            HttpURLConnection connection = null;
            InputStream input = null;

            Fi tempFile = Core.files.local("cache/aquarion_tmp.jar");
            java.io.OutputStream output = null;

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

                            mindustry.mod.Mods.LoadedMod oldMod = Vars.mods.getMod(AquaLoader.class);
                            if (oldMod != null) {
                                Vars.mods.removeMod(oldMod);
                            }

                            Vars.mods.importMod(tempFile);
                            Vars.mods.reload();

                            tempFile.delete();
                            showSuccessDialog();
                        } catch (Exception e) {
                            Log.err("[AquarionUpdate] Import error", e);
                            Vars.ui.showException(Core.bundle.get("aquarion.update.install_error"), e);
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
                        Vars.ui.showException(Core.bundle.get("aquarion.update.install_error"), e);
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

    private void showSuccessDialog() {
        BaseDialog successDialog = new BaseDialog(Core.bundle.get("aquarion.update.success_title"));
        String successMessage = Core.bundle.get("aquarion.update.success_text");
        successDialog.cont.add(successMessage).pad(20).row();

        successDialog.buttons.button(Core.bundle.get("aquarion.update.ok"), Core.app::exit).size(150f, 60f);

        successDialog.show();
    }
}
