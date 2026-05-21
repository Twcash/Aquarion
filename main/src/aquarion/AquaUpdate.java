package aquarion;

import arc.*;
import arc.files.*;
import arc.util.*;
import arc.util.serialization.*;
import mindustry.Vars;
import mindustry.mod.Mod;
import mindustry.ui.Bar;
import mindustry.ui.dialogs.BaseDialog;
import arc.scene.ui.CheckBox;
import arc.scene.ui.Tooltip;
import arc.graphics.Color;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AquaUpdate {

    public static final String GITHUB_REPO = "Twcash/Aquarion"; 
    
    private String currentVersion = "unknown";
    private Fi modFile;
    private boolean isCancelled = false;
    
    private float downloadProgress = 0f;
    private String progressText = "";

    public void check(Class<? extends Mod> mainClass) {
        var modContainer = Vars.mods.getMod(mainClass);
        if (modContainer != null) {
            if (modContainer.meta != null) {
                currentVersion = modContainer.meta.version.trim(); 
            }
            modFile = modContainer.file;
        }
        
        checkUpdates();
    }

    private void checkUpdates() {
        Http.get("https://api.github.com/repos/" + GITHUB_REPO + "/releases/latest", response -> {
            try {
                Jval json = Jval.read(response.getResultAsString());
                String remoteVersion = json.getString("tag_name", "").trim();

                if (!remoteVersion.isEmpty() && !remoteVersion.equals(currentVersion)) {
                    Jval assets = json.get("assets");
                    if (assets != null && assets.isArray() && assets.asArray().size > 0) {
                        String downloadUrl = null;
                        
                        for (Jval asset : assets.asArray()) {
                            String name = asset.getString("name", "");
                            if (name.endsWith(".jar")) {
                                downloadUrl = asset.getString("browser_download_url", null);
                                break;
                            }
                        }

                        if (downloadUrl != null) {
                            final String finalUrl = downloadUrl;
                            Core.app.post(() -> showUpdateDialog(remoteVersion, finalUrl));
                        }
                    }
                }
            } catch (Exception e) {
                Log.err("[AquarionUpdate] Error", e);
            }
        }, error -> {
            Log.err("[AquarionUpdate] Network error: " + error.getMessage());
        });
    }

    private void showUpdateDialog(String newVersion, String downloadUrl) {
        BaseDialog dialog = new BaseDialog(Core.bundle.get("aquarion.update.title"));
        
        String headerText = Core.bundle.get("aquarion.update.header");
        dialog.cont.label(() -> headerText).padBottom(25f).row();
        
        String message = Core.bundle.format("aquarion.update.message", currentVersion, newVersion);
        dialog.cont.add(message).padBottom(20f).row();
        
        CheckBox checkBox = new CheckBox(Core.bundle.get("settings.showUpdates"));
        checkBox.setChecked(true);
        
        checkBox.changed(() -> {
            boolean value = checkBox.isChecked();
            Core.settings.put("showUpdates", value);
            Core.settings.manualSave();
        });
        
        checkBox.addListener(new Tooltip(t -> {
            t.background(mindustry.gen.Tex.button) 
             .add(Core.bundle.get("aquarion.update.hint_settings"))
             .pad(8f);
        }));
        
        dialog.cont.add(checkBox).padBottom(15f).row();
        
        dialog.buttons.button(Core.bundle.get("aquarion.update.download"), () -> {
            dialog.hide();
            downloadAndInstall(downloadUrl);
        }).size(200f, 60f);

        dialog.buttons.button(Core.bundle.get("aquarion.update.cancel"), dialog::hide).size(150f, 60f);
        dialog.show();
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
            ByteArrayOutputStream output = null;
            
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
                output = new ByteArrayOutputStream();

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

                if (!isCancelled) {
                    byte[] fileBytes = output.toByteArray();
                    
                    if (modFile != null) {
                        modFile.writeBytes(fileBytes);
                        
                        Core.app.post(() -> {
                            progressDialog.hide();
                            showSuccessDialog();
                        });
                    } else {
                        throw new Exception("Mod file reference is null! Cannot update.");
                    }
                }

            } catch (Exception e) {
                Log.err("[AquarionUpdate] Error", e);
                Core.app.post(() -> {
                    if (!isCancelled) {
                        progressDialog.hide();
                        Vars.ui.showException(Core.bundle.get("aquarion.update.install_error"), e);
                    }
                });
            } finally {
                try {
                    if (output != null) output.close();
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
        
        successDialog.buttons.button(Core.bundle.get("aquarion.update.ok"), () -> {
            Core.app.exit();
        }).size(150f, 60f);
        
        successDialog.show();
    }
}