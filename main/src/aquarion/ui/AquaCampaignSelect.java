package aquarion.ui;

import aquarion.AquaLoader;
import aquarion.content.AquaPlanets;
import arc.Core;
import arc.files.Fi;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.Dialog;
import arc.scene.style.TextureRegionDrawable;
import arc.struct.ObjectMap;
import arc.util.Align;
import arc.util.Log;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.mod.Mods;
import mindustry.type.Planet;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import static mindustry.Vars.ui;

public class AquaCampaignSelect {
    private static Dialog customDialog;
    private static boolean active = false;

    private static final String AQUA_CAMPAIGN_KEY = "aqua-campaign-selected";
    
    private static final ObjectMap<String, TextureRegion> textureCache = new ObjectMap<>();

    public static void init() {
        if (Vars.headless || ui == null || ui.planet == null) return;

        ui.planet.update(() -> {
            if (ui.planet.isShown()) {
                if (shouldShowCustomSelect()) {
                    if (!active) {
                        active = true;
                        ui.planet.hide();
                        showCustomCampaignDialog();
                        Log.info("[AquaCampaignSelect] Отображение кастомного выбора кампании.");
                    }
                }
            } else {
                active = false;
            }
        });
    }

    private static boolean shouldShowCustomSelect() {
        return !Core.settings.getBool(AQUA_CAMPAIGN_KEY, false);
    }

    public static void showCustomCampaignDialog() {
        if (customDialog != null && customDialog.isShown()) return;

        BaseDialog diag = new BaseDialog("@campaign.select");
        customDialog = diag;

        Planet[] selected = {null};
        ButtonGroup<arc.scene.ui.Button> group = new ButtonGroup<>();
        group.setMinCheckCount(0);

        boolean isMobile = Core.graphics.isPortrait() || Vars.mobile;
        float buttonSize = isMobile ? 150f : 300f;

        if (isMobile) {
            addPlanetButton(diag, Planets.serpulo, selected, group, buttonSize, 1);
            addPlanetButton(diag, Planets.erekir, selected, group, buttonSize, 1);
            diag.cont.row();

            addPlanetButton(diag, AquaPlanets.fakeSerpulo, selected, group, buttonSize, 2);
            diag.cont.row();
        } else {
            addPlanetButton(diag, Planets.serpulo, selected, group, buttonSize, 1);
            addPlanetButton(diag, Planets.erekir, selected, group, buttonSize, 1);
            addPlanetButton(diag, AquaPlanets.fakeSerpulo, selected, group, buttonSize, 1);
            diag.cont.row();
        }

        diag.cont.label(() -> {
                    if (selected[0] == null) return Core.bundle.get("campaign.none", "Выберите кампанию");

                    String key = "campaign." + selected[0].name;

                    if (Core.bundle.has(key)) {
                        return Core.bundle.get(key);
                    }

                    if (Core.bundle.has("campaign.aquarion-fakeSerpulo") && selected[0] == AquaPlanets.fakeSerpulo) {
                        return Core.bundle.get("campaign.aquarion-fakeSerpulo");
                    }

                    return selected[0].description != null ? selected[0].description : selected[0].localizedName;
                })
                .labelAlign(Align.center)
                .style(Styles.outlineLabel)
                .width(isMobile ? 320f : 460f)
                .wrap()
                .colspan(isMobile ? 2 : 3);

        diag.buttons.button("@ok", Icon.ok, () -> {
            if (selected[0] != null) {
                ui.planet.state.planet = selected[0];
                if (selected[0].getStartSector() != null) {
                    ui.planet.lookAt(selected[0].getStartSector());
                    ui.planet.selectSector(selected[0].getStartSector());
                }

                Core.settings.put(AQUA_CAMPAIGN_KEY, true);
                Core.settings.put("campaignselect", true);

                diag.hide();
                active = false;
                ui.planet.show();
            }
        }).size(300f, 64f).disabled(b -> selected[0] == null);

        diag.show();
    }

    private static void addPlanetButton(BaseDialog diag, Planet planet, Planet[] selected, ButtonGroup<arc.scene.ui.Button> group, float buttonSize, int colSpan) {
        TextureRegion tex = getPlanetTexture(planet);

        var cell = diag.cont.button(b -> {
            b.top();
            b.add(planet.localizedName).color(Pal.accent).style(Styles.outlineLabel);
            b.row();
            if (tex != null) {
                b.image(new TextureRegionDrawable(tex)).grow().scaling(Scaling.fit);
            } else {
                b.image(Core.atlas.find("clear")).grow().scaling(Scaling.fit);
            }
        }, Styles.togglet, () -> selected[0] = planet)
        .size(buttonSize)
        .group(group);

        if (colSpan > 1) {
            cell.colspan(colSpan);
        }
    }

    private static TextureRegion getPlanetTexture(Planet planet) {
        if (planet == null) return Core.atlas.find("clear");

        if (textureCache.containsKey(planet.name)) {
            return textureCache.get(planet.name);
        }

        TextureRegion region = null;

        if (planet == AquaPlanets.fakeSerpulo || (planet.name != null && planet.name.toLowerCase().contains("fake"))) {
            region = loadModPlanetTexture("fakesrpulo", "fakeserpulo", planet.name);
        }

        if (region == null) {
            region = loadVanillaOrAtlasTexture(planet);
        }

        if (region == null) {
            region = Core.atlas.find("clear");
        }

        textureCache.put(planet.name, region);
        return region;
    }

    private static TextureRegion loadModPlanetTexture(String... candidateNames) {
        Mods.LoadedMod loadedMod = Vars.mods.getMod("aquarion");
        if (loadedMod == null) {
            loadedMod = Vars.mods.getMod(AquaLoader.class);
        }
        if (loadedMod == null) {
            loadedMod = AquaLoader.mod();
        }

        String[] folderPrefixes = {
            "assets-raw/planets/",
            "assets-raw/sprites/planets/",
            "assets-raw/",
            "assets/planets/",
            "assets/sprites/planets/",
            "assets/",
            "sprites/planets/",
            "planets/",
            "sprites/"
        };

        String[] extensions = {".png", ".PNG", ".jpg", ""};

        if (loadedMod != null && loadedMod.root != null) {
            Fi root = loadedMod.root;

            for (String folder : folderPrefixes) {
                Fi dir = root.child(folder);
                if (dir.exists()) {
                    for (String name : candidateNames) {
                        for (String ext : extensions) {
                            Fi file = root.child(folder + name + ext);
                            if (file.exists() && !file.isDirectory()) {
                                try {
                                    Texture tex = new Texture(file);
                                    tex.setFilter(Texture.TextureFilter.linear);
                                    Log.info("[AquaCampaignSelect] Успешно загружен ассет мода: " + file.path());
                                    return new TextureRegion(tex);
                                } catch (Throwable t) {
                                    Log.err("[AquaCampaignSelect] Ошибка загрузки текстуры: " + file, t);
                                }
                            }
                        }
                    }

                    if (dir.isDirectory()) {
                        for (Fi f : dir.list()) {
                            for (String name : candidateNames) {
                                if (f.nameWithoutExtension().equalsIgnoreCase(name)) {
                                    try {
                                        Texture tex = new Texture(f);
                                        tex.setFilter(Texture.TextureFilter.linear);
                                        Log.info("[AquaCampaignSelect] Загружен ассет по регистру: " + f.path());
                                        return new TextureRegion(tex);
                                    } catch (Throwable t) {
                                        Log.err("[AquaCampaignSelect] Ошибка: " + f, t);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (String folder : folderPrefixes) {
            for (String name : candidateNames) {
                for (String ext : extensions) {
                    String relPath = folder + name + ext;

                    if (Vars.tree != null) {
                        Fi treeFile = Vars.tree.get(relPath);
                        if (treeFile != null && treeFile.exists() && !treeFile.isDirectory()) {
                            try {
                                Texture tex = new Texture(treeFile);
                                tex.setFilter(Texture.TextureFilter.linear);
                                return new TextureRegion(tex);
                            } catch (Throwable ignored) {}
                        }
                    }

                    Fi internalFile = Core.files.internal(relPath);
                    if (internalFile.exists() && !internalFile.isDirectory()) {
                        try {
                            Texture tex = new Texture(internalFile);
                            tex.setFilter(Texture.TextureFilter.linear);
                            return new TextureRegion(tex);
                        } catch (Throwable ignored) {}
                    }
                }
            }
        }

        for (String name : candidateNames) {
            String[] atlasKeys = {
                "aquarion-" + name,
                "aquarion-planet-" + name,
                "planet-" + name,
                "planets-" + name,
                "planets/" + name,
                name
            };

            for (String key : atlasKeys) {
                if (Core.atlas.has(key)) {
                    TextureRegion reg = Core.atlas.find(key);
                    if (reg != null && reg.found()) return reg;
                }
            }
        }

        return null;
    }

    private static TextureRegion loadVanillaOrAtlasTexture(Planet planet) {
        String pName = planet.name;

        String[] possibleAtlasNames = {
            pName,
            "planet-" + pName,
            "planets-" + pName,
            "planets/" + pName,
            pName + "-preview",
            pName + "-banner",
            "campaign-" + pName,
            "planet-" + pName + "-preview",
            "planet-" + pName + "-banner"
        };

        for (String key : possibleAtlasNames) {
            if (Core.atlas.has(key)) {
                TextureRegion reg = Core.atlas.find(key);
                if (reg != null && reg.found()) {
                    return reg;
                }
            }
        }

        if (planet.uiIcon != null && planet.uiIcon.found()) {
            return planet.uiIcon;
        }
        if (planet.fullIcon != null && planet.fullIcon.found()) {
            return planet.fullIcon;
        }

        String[] internalPaths = {
            "sprites/planets/" + pName + ".png",
            "planets/" + pName + ".png",
            "sprites/ui/planet-" + pName + ".png",
            "sprites/ui/" + pName + ".png",
            "sprites/" + pName + ".png"
        };

        for (String path : internalPaths) {
            if (Vars.tree != null) {
                Fi fi = Vars.tree.get(path);
                if (fi != null && fi.exists() && !fi.isDirectory()) {
                    try {
                        Texture tex = new Texture(fi);
                        tex.setFilter(Texture.TextureFilter.linear);
                        return new TextureRegion(tex);
                    } catch (Throwable ignored) {}
                }
            }

            Fi fi = Core.files.internal(path);
            if (fi.exists() && !fi.isDirectory()) {
                try {
                    Texture tex = new Texture(fi);
                    tex.setFilter(Texture.TextureFilter.linear);
                    return new TextureRegion(tex);
                } catch (Throwable ignored) {}
            }
        }

        return null;
    }
}
