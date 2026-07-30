package aquarion;

import aquarion.annotations.Annotations;
import aquarion.content.ModMusic;
import aquarion.dialogs.AquaResearchDialog;
import aquarion.ui.ModSettings;
import aquarion.world.blocks.effect.ResearchServer;
import arc.Events;
import arc.util.Interval;
import arc.util.Time;
import mindustry.game.EventType;
import arc.scene.ui.layout.*;

import static mindustry.Vars.*;

@Annotations.LoadRegs("error")
@Annotations.EnsureLoad
public class ModEventHandler {
    public static AquaResearchDialog techDialog;
    public static WidgetGroup hudGroup;
    public static float autoResearchTimer = 0f;
    public static Interval timers = new Interval();

    public static void load(){
        techDialog = new AquaResearchDialog();
    }

    public static void init() {
        Events.on(EventType.ClientLoadEvent.class, e -> ModMusic.attach());
        Events.on(EventType.ClientLoadEvent.class, e -> ModSettings.init());
        Events.on(EventType.ClientLoadEvent.class, e -> ResearchServer.loadGlobalResearch());
        Events.on(EventType.MusicRegisterEvent.class, e -> ModMusic.load());

        Events.run(EventType.Trigger.update, () -> {
            if (net.client()) return;
            if (!state.isCampaign() || state.getSector() == null) return;

            if (timers.get(60)) {
                ResearchServer.updateResearchFromExports();
            }

            if (!AquaResearchDialog.autoResearch) return;

            autoResearchTimer += Time.delta;
            if (autoResearchTimer < 30f) return;
            autoResearchTimer = 0f;

            techDialog.autoSpend();
        });
    }
}
