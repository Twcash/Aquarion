package aquarion.content;

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

    public static String[] aquaAmbientList = {"quiet-processing", "underwaves", "che-go-boom", "fih", "expansion","decaying-giants", "scarred-skies", "during-creation", "flying-fire"};
    public static String[] aquaDarkList = {"sinking", "bubblerine", "exhasperation", "mold"};
    public static String[] aquaBossList = {"acceptance","oh-the-horror", "hero-brine", "not-so-distant-now", "concussive"};

    public static Seq<Music> origAmbientMusic;
    public static Seq<Music> origDarkMusic;
    public static Seq<Music> origBossMusic;

    public static Music betterLand;
    public static Music realLand;

    public static Music betterFine;
    public static Music realFine;

    public static Field currentMus;
    //DON`T TOUCH IT`S ICON FOR ERROR OR NOT FOUND IMAGE
    public static final String DEFAULT_ICON = "aquarion-error-may";

    public static class MusicInfo {
        public String name;
        public String author;
        public String iconName;

        public MusicInfo(String iconName, String name, String author) {
            this.iconName = iconName;
            this.name = name;
            this.author = author;
        }
    }

    public static String god = "Anuken";
    public static String leo = "Leo";
    public static String hyp = "HYPERIUM";
    public static String cas = "Twcash";
    public static String myt = "Mythril";
    public static String ace = "Ace1020spawn";
    public static String nik = "NikolayKot";
    //DON`T TOUCH THIS, IT IS FOR DEFAULT ICONS
    public static String def = "aquarion-icon-mindustry";
    public static String none = "aquarion-icon-none";
    public static ArrayMap<String, MusicInfo> musics = new ArrayMap<>();

    static {
        musics.put("game1", new MusicInfo(def, "Game 1", god));
        musics.put("game2", new MusicInfo(def, "Game 2", god));
        musics.put("game3", new MusicInfo(def, "Game 3", god));
        musics.put("game4", new MusicInfo(def, "Game 4", god));
        musics.put("game5", new MusicInfo(def, "Game 5", god));
        musics.put("game6", new MusicInfo(def, "Game 6", god));
        musics.put("game7", new MusicInfo(def, "Game 7", god));
        musics.put("game8", new MusicInfo(def, "Game 8", god));
        musics.put("game9", new MusicInfo(def, "Game 9", god));
        musics.put("fine", new MusicInfo(def, "Fine", god));
        musics.put("boss1", new MusicInfo(def, "Boss 1", god));
        musics.put("boss2", new MusicInfo(def, "Boss 2", god));

        musics.put("underwaves", new MusicInfo(none, "Underwaves", leo));
        musics.put("bubblerine", new MusicInfo(none, "Bubblerine", leo));
        musics.put("che-go-boom", new MusicInfo(none, "Che Go Boom", leo));
        musics.put("acceptance", new MusicInfo(none, "Acceptance", hyp));
        musics.put("hero-brine", new MusicInfo(none, "Hero brine", leo));
        musics.put("pipe-thoughts", new MusicInfo(none, "Pipe Thoughts", cas));
        musics.put("not-so-distant-now", new MusicInfo(none, "NOT SO DISTANT NOW", cas));
        musics.put("concussive", new MusicInfo(none, "Concussive", cas));
        musics.put("mold", new MusicInfo(none, "Mold", myt));
        musics.put("assault", new MusicInfo(none, "Assault", myt));
        musics.put("quiet-processing", new MusicInfo(none, "Quiet Processing", ace));
        musics.put("scarred-skies", new MusicInfo(none, "Scarred Skies", ace));
        musics.put("exhasperation", new MusicInfo(none, "Exasperation", ace));
        musics.put("decaying-monuments", new MusicInfo(none, "Decaying Monuments", ace));
        musics.put("fih", new MusicInfo(none, "Our intuition", ace));
        musics.put("oh-the-horror", new MusicInfo(none, "Oh the horror", ace));
        musics.put("sinking", new MusicInfo(none, "Sinking", ace));
        musics.put("during-creation", new MusicInfo(none, "During Creation", nik));
        musics.put("flying-fire", new MusicInfo(none, "Flying Fire", myt));
        //###########################################################################
        //######################## !!!HOW ADD MUSIC!!! ##############################
        //musics.put("filename", new MusicInfo("aquarion-iconname", "You Music Name", "Author"));
        //               |                          |
        //         music file name         if you no icon, put none "(none, "You Music Name", "Author")"
        //###########################################################################
        //###########################################################################
    }

    // Don't change from outside I trust you by putting it in public
    public static boolean enabled = true;

    public static void load() {
        origAmbientMusic = Vars.control.sound.ambientMusic.copy();
        origDarkMusic = Vars.control.sound.darkMusic.copy();
        origBossMusic = Vars.control.sound.bossMusic.copy();

        aquaAmbientMusic = loadMultiple(aquaAmbientList, "ambient");
        aquaDarkMusic = loadMultiple(aquaDarkList, "dark");
        aquaBossMusic = loadMultiple(aquaBossList, "boss");

        betterLand = Vars.tree.loadMusic("better-land");
        realLand = Musics.land;
        betterFine = Vars.tree.loadMusic("better-fine");
        realFine = Musics.fine;

        updateFine();
        updateLand();
    }

    public static Seq<Music> loadMultiple(String[] filenames, String folder) {
        Seq<Music> result = new Seq<>();
        for (String filename : filenames) {
            try {
                Music music = Vars.tree.loadMusic(folder + "/" + filename);
                if (music != null) {
                    result.add(music);
                } else {
                    Log.warn("Failed to load music: " + filename);
                }
            } catch (Exception e) {
                Log.err("Error loading music file: " + filename, e);
            }
        }
        return result;
    }

    public static void attach() {
        Events.on(WorldLoadEvent.class, e -> {
            enableCustomMusic();
        });

        Events.on(StateChangeEvent.class, e -> {
            if (e.from != GameState.State.menu && e.to == GameState.State.menu) {
                disableCustomMusic();
            }
        });

        reflectCurMus();
        UIEvents.monitorMusic();
        UIEvents.registerControls();
    }

    public static void enableCustomMusic() {
        if (!ModSettings.getOnlyModMus()) {
            Vars.control.sound.ambientMusic = Seq.with(aquaAmbientMusic).addAll(origAmbientMusic);
            Vars.control.sound.darkMusic = Seq.with(aquaDarkMusic).addAll(origDarkMusic);
            Vars.control.sound.bossMusic = Seq.with(aquaBossMusic).addAll(origBossMusic);
        } else {
            Vars.control.sound.ambientMusic = aquaAmbientMusic.isEmpty() ? Seq.with(origAmbientMusic) : Seq.with(aquaAmbientMusic);
            Vars.control.sound.darkMusic = aquaDarkMusic.isEmpty() ? Seq.with(origDarkMusic) : Seq.with(aquaDarkMusic);
            Vars.control.sound.bossMusic = aquaBossMusic.isEmpty() ? Seq.with(origBossMusic) : Seq.with(aquaBossMusic);
        }
        enabled = true;
    }

    public static void disableCustomMusic() {
        if (origAmbientMusic != null) Vars.control.sound.ambientMusic = Seq.with(origAmbientMusic);
        if (origDarkMusic != null) Vars.control.sound.darkMusic = Seq.with(origDarkMusic);
        if (origBossMusic != null) Vars.control.sound.bossMusic = Seq.with(origBossMusic);
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
        if (betterLand != null) {
            Musics.land = ModSettings.getBetterLand() ? betterLand : realLand;
        }
    }
    public static void updateFine() {
        if (betterFine != null && ModSettings.getBetterFine()) {
            Musics.fine = betterFine;
        } else {
            Musics.fine = realFine;
        }
        Vars.control.sound.ambientMusic = Seq.with(Musics.game1, Musics.game3, Musics.game6, Musics.game8, Musics.game9, Musics.fine);
    }
}