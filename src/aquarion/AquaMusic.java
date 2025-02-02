package aquarion;

import arc.Events;
import arc.audio.Music;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.game.EventType;

public class AquaMusic {
    public static Seq<Music> aquaAmbientMusic = new Seq<>();
    public static Seq<Music> aquaDarkMusic = new Seq<>();
    public static Seq<Music> aquaBossMusic = new Seq<>();

    public static String[] aquaAmbientList = {"underwaves", "che-go-boom"};
    public static String[] aquaDarkList = {"bubblerine", "value"};
    public static String[] aquaBossList = {"acceptance", "hero-brine"};

    public static Seq<Music> origAmbientMusic;
    public static Seq<Music> origDarkMusic;
    public static Seq<Music> origBossMusic;

    // Don't change from outside I trust you by putting it in public
    public static boolean enabled = false;

    // Replace all music instead of adding on
    public static boolean fullOverride = false;

    public static void load() {
        aquaAmbientMusic = loadMultiple(aquaAmbientList, "ambient");
        aquaDarkMusic = loadMultiple(aquaDarkList, "dark");
        aquaBossMusic = loadMultiple(aquaBossList, "boss");

        origAmbientMusic = Vars.control.sound.ambientMusic.copy();
        origDarkMusic = Vars.control.sound.darkMusic.copy();
        origBossMusic = Vars.control.sound.bossMusic.copy();
    }

    public static Seq<Music> loadMultiple(String[] filenames, String folder) {
        Seq<Music> result = new Seq<>();

        for (String filename : filenames) {
            result.add(Vars.tree.loadMusic(folder +"/"+filename));
        }

        return result;
    }

    public static void attach() {
        Events.on(EventType.WorldLoadEvent.class, e -> {
            if (Vars.state.rules.planet.parent.name.equals("aquarion-citun")) {
                enableCustomMusic();
            } else if (enabled) {
                disableCustomMusic();
            }
        });
        Events.on(EventType.StateChangeEvent.class, e -> {
            if (e.from != GameState.State.menu && e.to == GameState.State.menu) {
                disableCustomMusic();
            }
        });
    }

    public static void enableCustomMusic() {
        Vars.control.sound.ambientMusic = Seq.with(aquaAmbientMusic);
        Vars.control.sound.darkMusic = Seq.with(aquaDarkMusic);
        Vars.control.sound.bossMusic = Seq.with(aquaBossMusic);

        if (!fullOverride) {
            Vars.control.sound.ambientMusic.addAll(origAmbientMusic);
            Vars.control.sound.darkMusic.addAll(origDarkMusic);
            Vars.control.sound.bossMusic.addAll(origBossMusic);
        }
        enabled = true;
    }

    public static void disableCustomMusic() {
        Vars.control.sound.ambientMusic = Seq.with(origAmbientMusic);
        Vars.control.sound.darkMusic = Seq.with(origDarkMusic);
        Vars.control.sound.bossMusic = Seq.with(origBossMusic);
        enabled = false;
    }
}
