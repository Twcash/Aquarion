package aquarion;

import aquarion.ui.UIEvents;
import arc.Events;
import arc.audio.Music;
import arc.struct.ArrayMap;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.audio.SoundControl;
import mindustry.core.GameState;
import mindustry.game.EventType;

import java.lang.reflect.Field;

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

    public static Field currentMus;
    public static Field lastMus;

    public static class MusicInfo {
        public String name;
        public String author;

        MusicInfo(String name, String author) {
            this.name = name;
            this.author = author;
        }
    }

    public static String god = "Anuken";
    public static String leo = "Leo";
    public static String hyp = "HYPERIUM";

    public static ArrayMap<String, MusicInfo> musics = new ArrayMap<>();

    static {
        musics.put("game1", new MusicInfo("Game 1", god));
        musics.put("game2", new MusicInfo("Game 2", god));
        musics.put("game3", new MusicInfo("Game 3", god));
        musics.put("game4", new MusicInfo("Game 4", god));
        musics.put("game5", new MusicInfo("Game 5", god));
        musics.put("game6", new MusicInfo("Game 6", god));
        musics.put("game7", new MusicInfo("Game 7", god));
        musics.put("game8", new MusicInfo("Game 8", god));
        musics.put("game9", new MusicInfo("Game 9", god));
        musics.put("fine", new MusicInfo("Fine", god));
        musics.put("boss1", new MusicInfo("Boss 1", god));
        musics.put("boss2", new MusicInfo("Boss 2", god));
        musics.put("underwaves", new MusicInfo("Underwaves", leo));
        musics.put("bubblerine", new MusicInfo("Bubblerine", leo));
        musics.put("che-go-boom", new MusicInfo("Che Go Boom", leo));
        musics.put("acceptance", new MusicInfo("Acceptance", hyp));
        musics.put("value", new MusicInfo("Che Go Boom", leo));
        musics.put("hero-brine", new MusicInfo("Hero brine", leo));
    }

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

        reflectCurMus();
        UIEvents.monitorMusic();
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

    public static void reflectCurMus() {
        try {
            currentMus = SoundControl.class.getDeclaredField("current");
            currentMus.setAccessible(true);

            lastMus = SoundControl.class.getDeclaredField("lastRandomPlayed");
            lastMus.setAccessible(true);
        } catch (Exception e) {
            Log.err("Failed to set visibility of music things");
            Log.err(e);
            UIEvents.musEnabled = false;
        }
    }

    public static Music getCurMusic() {
        try {
            return UIEvents.musEnabled ? (Music)currentMus.get(Vars.control.sound) : null;
        } catch (Exception e) {
            Log.err("Failed to get current music");
            Log.err(e);
            return null;
        }
    }
}
