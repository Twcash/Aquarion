package aquarion;

import aquarion.annotations.Annotations;
import aquarion.content.ModMusic;
import aquarion.dialogs.AquaResearchDialog;
import aquarion.dialogs.DialogueDialog;
import aquarion.dialogs.NewSettingsDialogue;
import aquarion.ui.ModSettings;
import aquarion.world.MultiBlockLib.LinkBlock;
import aquarion.world.blocks.AlternativeBuildCosts;
import aquarion.world.blocks.effect.ResearchServer;
import aquarion.world.dialogue.StoryProgress;
import aquarion.world.graphics.MenuReplacer;
import arc.Events;
import arc.util.Interval;
import arc.util.Log;
import arc.util.Time;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import arc.scene.ui.layout.*;

import static mindustry.Vars.*;

@Annotations.LoadRegs("error")
@Annotations.EnsureLoad
public class ModEventHandler {
    public static AquaResearchDialog techDialog;
    public static NewSettingsDialogue settingsDialogue;
    public static DialogueDialog storyDialog;
    public static float autoResearchTimer = 0f;
    public static Interval timers = new Interval();

    public static void load(){
        techDialog = new AquaResearchDialog();
        settingsDialogue = new NewSettingsDialogue();
        storyDialog = new DialogueDialog();
        // register the story-progress save chunk before any save can be read/written
        StoryProgress.register();
    }

    public static void init() {
        Events.on(EventType.ClientLoadEvent.class, e -> ModMusic.attach());
        Events.run(EventType.Trigger.update, AlternativeBuildCosts::updatePreviews);
        Events.run(EventType.Trigger.update, LinkBlock::redirectDeconstructionPlans);
        Events.run(EventType.Trigger.update, LinkBlock::pickLinkedBlock);
        // Mindustry reads active weather attributes before updating weather entities.
        Events.run(EventType.Trigger.update, () -> Groups.weather.each(weather -> {
            if(weather.weather == null){
                Log.warn("Removing weather entity @ with an unresolved weather type", weather.id);
                weather.remove();
            }
        }));
        Events.on(EventType.ClientLoadEvent.class, e -> ModSettings.init());
        Events.on(EventType.ClientLoadEvent.class, e -> ResearchServer.loadGlobalResearch());
        Events.on(EventType.ClientLoadEvent.class, e -> StoryProgress.load());
        //swap the vanilla menu background for the preset battle arena once the UI is built
        //Events.on(EventType.ClientLoadEvent.class, e -> MenuReplacer.replaceMenu(ui.menufrag));
        // a reset means a new world or a fresh save is about to be read; clear stale per-save state
        Events.on(EventType.ResetEvent.class, e -> StoryProgress.load());
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
