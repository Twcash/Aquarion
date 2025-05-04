package aquarion;

import aquarion.ui.ModSettings;
import aquarion.ui.UIEvents;
import arc.Events;
import arc.audio.Music;
import arc.struct.ArrayMap;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.audio.SoundControl;
import mindustry.core.GameState;
import mindustry.game.EventType.StateChangeEvent;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.gen.Musics;

import java.lang.reflect.Field;

public class ModMusic {
    public static Seq<Music> aquaAmbientMusic = new Seq<>();
    public static Seq<Music> aquaDarkMusic = new Seq<>();
    public static Seq<Music> aquaBossMusic = new Seq<>();

    public static String[] aquaAmbientList = {"underwaves", "che-go-boom, pipe-thoughts", "expansion"};
    public static String[] aquaDarkList = {"bubblerine", "value", "trance", "mold", "assault"};
    public static String[] aquaBossList = {"acceptance", "hero-brine", "not-so-distant-now", "concussive"};

    public static Seq<Music> origAmbientMusic;
    public static Seq<Music> origDarkMusic;
    public static Seq<Music> origBossMusic;

    public static Music betterLand;
    public static Music realLand;

    public static Music betterFine;
    public static Music realFine;

    public static Field currentMus;

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
    public static String cas = "Twcash";
    public static String myt = "Mythril";
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
        musics.put("value", new MusicInfo("CHE Go Boom", leo));
        musics.put("hero-brine", new MusicInfo("Hero brine", leo));
        musics.put("pipe-thoughts", new MusicInfo("Pipe Thoughts", cas));
        musics.put("not-so-distant-now", new MusicInfo("NOT SO DISTANT NOW", cas));
        musics.put("concussive", new MusicInfo("Concussive", cas));
        musics.put("trance", new MusicInfo("Trance", cas));
        musics.put("mold", new MusicInfo("mold", myt));
        musics.put("expansion", new MusicInfo("Trance", myt));
        musics.put("assault", new MusicInfo("Trance", myt));
    }

    // Don't change from outside I trust you by putting it in public
    public static boolean enabled = true;

    public static void load() {
        aquaAmbientMusic = loadMultiple(aquaAmbientList, "ambient");
        aquaDarkMusic = loadMultiple(aquaDarkList, "dark");
        aquaBossMusic = loadMultiple(aquaBossList, "boss");

        betterLand = Vars.tree.loadMusic("better-land");
        realLand = Musics.land;

        betterFine = Vars.tree.loadMusic("better-fine");
        realFine = Musics.fine;

        updateFine();
        updateLand();

        origAmbientMusic = Vars.control.sound.ambientMusic.copy();
        origDarkMusic = Vars.control.sound.darkMusic.copy();
        origBossMusic = Vars.control.sound.bossMusic.copy();
    }

    public static Seq<Music> loadMultiple(String[] filenames, String folder) {
        Seq<Music> result = new Seq<>();

        for (String filename : filenames) {
            Music music = Vars.tree.loadMusic(folder + "/" + filename);
            if (music != null) {
                result.add(music);
            } else {
                Log.warn("Failed to load music: " + filename);
            }
        }

        return result;
    }

    public static void attach() {
        Events.on(WorldLoadEvent.class, e -> {
            if (Vars.state.rules.planet.parent != null && Vars.state.rules.planet.parent.name.equals("aquarion-citun")) {
                enableCustomMusic();
            } else if (enabled) {
                disableCustomMusic();
            }
        });

        Events.on(StateChangeEvent.class, e -> {
            if (e.from != GameState.State.menu && e.to == GameState.State.menu) {
                disableCustomMusic();
            }
        });

        reflectCurMus();
        UIEvents.monitorMusic();
    }

    public static void enableCustomMusic() {
        if (!ModSettings.getModMusOnly()) {
            Vars.control.sound.ambientMusic = Seq.with(aquaAmbientMusic).addAll(origAmbientMusic);
            Vars.control.sound.darkMusic = Seq.with(aquaDarkMusic).addAll(origDarkMusic);
            Vars.control.sound.bossMusic = Seq.with(aquaBossMusic).addAll(origBossMusic);
        } else {
            Vars.control.sound.ambientMusic = Seq.with(aquaAmbientMusic);
            Vars.control.sound.darkMusic = Seq.with(aquaDarkMusic);
            Vars.control.sound.bossMusic = Seq.with(aquaBossMusic);
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

    public static void updateLand() {
        Musics.land = ModSettings.getBetterLand() ? betterLand : realLand;
    }
    public static void updateFine() {
        Musics.fine = ModSettings.getBetterFine() ? betterFine : realFine;
        Vars.control.sound.ambientMusic = Seq.with(Musics.game1, Musics.game3, Musics.game6, Musics.game8, Musics.game9, Musics.fine);
    }
}

