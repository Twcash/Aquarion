package aquarion;

import arc.Core;
import arc.assets.loaders.MusicLoader;
import arc.audio.Music;
import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.audio.SoundControl;
import mindustry.core.Control;
import mindustry.gen.Musics;

public class AquaMusic {
    public static Seq<Music> aquaAmbientMusic = new Seq<Music>();
    public static Seq<Music> aquaDarkMusic = new Seq<Music>();
    public static Seq<Music> aquaBossMusic = new Seq<Music>();

    public static String[] aquaAmbientList = {"underwaves", "che-go-boom"};
    public static String[] aquaDarkList = {};
    public static String[] aquaBossList = {};

    public static Seq<Music> origAmbientMusic;
    public static Seq<Music> origDarkMusic;
    public static Seq<Music> origBossMusic;

    public static void load() {
        aquaAmbientMusic = loadMultiple(aquaAmbientList, "ambient");
        aquaDarkMusic = loadMultiple(aquaDarkList, "dark");
        aquaBossMusic = loadMultiple(aquaBossList, "boss");

        origAmbientMusic = Vars.control.sound.ambientMusic;
        origDarkMusic = Vars.control.sound.darkMusic;
        origBossMusic = Vars.control.sound.bossMusic;
    }

    public static Seq<Music> loadMultiple(String[] filenames, String folder) {
        Seq<Music> result = new Seq<Music>();

        for (String filename : filenames) {
            result.add(Vars.tree.loadMusic(folder +"/"+filename));
        }

        return result;
    }
}