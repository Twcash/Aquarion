package aquarion.ui;

import aquarion.content.AquaPlanets;
import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.Dialog;
import arc.scene.style.TextureRegionDrawable;
import arc.util.Align;
import arc.util.Log;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.type.Planet;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import static mindustry.Vars.ui;

public class AquaCampaignSelect {
    private static Dialog customDialog;
    private static boolean active = false;

    private static final String AQUA_CAMPAIGN_KEY = "aqua-campaign-selected";

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

        Planet[] choices = {Planets.serpulo, Planets.erekir, AquaPlanets.fakeSerpulo};

        for (Planet planet : choices) {
            TextureRegion tex = getPlanetTexture(planet);

            diag.cont.button(b -> {
                        b.top();
                        b.add(planet.localizedName).color(Pal.accent).style(Styles.outlineLabel);
                        b.row();
                        b.image(new TextureRegionDrawable(tex)).grow().scaling(Scaling.fit);
                    }, Styles.togglet, () -> selected[0] = planet)
                    .size(Core.graphics.isPortrait() || Vars.mobile ? 220f : 320f)
                    .group(group);
        }

        diag.cont.row();

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
                .width(440f)
                .wrap()
                .colspan(choices.length);

        diag.buttons.button("@ok", Icon.ok, () -> {
            if (selected[0] != null) {
                ui.planet.state.planet = selected[0];
                ui.planet.lookAt(selected[0].getStartSector());
                ui.planet.selectSector(selected[0].getStartSector());

                Core.settings.put(AQUA_CAMPAIGN_KEY, true);
                Core.settings.put("campaignselect", true);

                diag.hide();
                active = false;
                ui.planet.show();
            }
        }).size(300f, 64f).disabled(b -> selected[0] == null);

        diag.show();
    }

    private static TextureRegion getPlanetTexture(Planet planet) {
        if (planet.uiIcon != null && planet.uiIcon.found()) {
            return planet.uiIcon;
        }

        if (Core.atlas.has("planet-" + planet.name)) {
            return Core.atlas.find("planet-" + planet.name);
        }

        if (Core.atlas.has("planets/" + planet.name)) {
            return Core.atlas.find("planets/" + planet.name);
        }

        if (Planets.serpulo != null && Planets.serpulo.uiIcon != null) {
            return Planets.serpulo.uiIcon;
        }

        return Core.atlas.find("clear");
    }
}