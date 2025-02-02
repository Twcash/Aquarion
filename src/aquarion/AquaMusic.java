package aquarion;

import arc.Events;
import arc.audio.Music;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.core.GameState;
import arc.util.Timer;
import mindustry.game.EventType.StateChangeEvent;
import mindustry.game.EventType.WorldLoadEvent;

public class AquaMusic {
    public static Seq<Music> aquaAmbientMusic = new Seq<>();
    public static Seq<Music> aquaDarkMusic = new Seq<>();
    public static Seq<Music> aquaBossMusic = new Seq<>();

    public static String[] aquaAmbientList = {"hero-brine", "underwaves", "che-go-boom"};
    public static String[] aquaDarkList = {"bubblerine", "value"};
    public static String[] aquaBossList = {"acceptance"};

    public static Seq<Music> origAmbientMusic;
    public static Seq<Music> origDarkMusic;
    public static Seq<Music> origBossMusic;

    public static boolean enabled = true;
    public static boolean fullOverride = true;

    private static Music currentMusic;
    private static final float fadeDuration = 3f;
    private static float fadeTime = 0f;
    private static boolean isCrossfading = false;

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
    }

    public static void enableCustomMusic() {
        if (!fullOverride) {
            Vars.control.sound.ambientMusic = Seq.with(aquaAmbientMusic).addAll(origAmbientMusic);
            Vars.control.sound.darkMusic = Seq.with(aquaDarkMusic).addAll(origDarkMusic);
            Vars.control.sound.bossMusic = Seq.with(aquaBossMusic).addAll(origBossMusic);
        } else {
            Vars.control.sound.ambientMusic = Seq.with(aquaAmbientMusic);
            Vars.control.sound.darkMusic = Seq.with(aquaDarkMusic);
            Vars.control.sound.bossMusic = Seq.with(aquaBossMusic);
        }

        enabled = true;
        playNextTrack(); // Start playing music immediately

        // Ensure music continues playing without gaps
        Timer.schedule(() -> {
            if (enabled && (currentMusic == null || !currentMusic.isPlaying())) {
                playNextTrack();
            }
        }, 0.1f, 0.1f);  // Runs every 0.1s
    }

    public static void disableCustomMusic() {
        Vars.control.sound.ambientMusic = Seq.with(origAmbientMusic);
        Vars.control.sound.darkMusic = Seq.with(origDarkMusic);
        Vars.control.sound.bossMusic = Seq.with(origBossMusic);
        enabled = false;

        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }

    public static void playNextTrack() {
        if (!enabled) return;

        Seq<Music> musicPool = Vars.control.sound.ambientMusic;
        if (musicPool.isEmpty()) return;

        Music newMusic = musicPool.random();
        playMusic(newMusic);
    }

    public static void playMusic(Music newMusic) {
        if (currentMusic == newMusic) return;

        if (currentMusic != null) {
            // Crossfade transition
            fadeTime = 0f;
            isCrossfading = true;

            // Ensure current music fades out while the next fades in
            Timer.schedule(() -> {
                if (!isCrossfading) return;

                fadeTime += Time.delta / 60f;
                float progress = Math.min(fadeTime / fadeDuration, 1f);

                if (currentMusic != null) currentMusic.setVolume(1f - progress);
                newMusic.setVolume(progress);

                if (progress >= 1f) {
                    if (currentMusic != null) {
                        currentMusic.stop();
                    }
                    currentMusic = newMusic;
                    isCrossfading = false;
                }
            }, 0.1f, 0.1f); // Check every 0.1s
        } else {
            // Play instantly if no music is currently playing
            currentMusic = newMusic;
            currentMusic.setVolume(1f);
            currentMusic.play();
        }
    }
}

