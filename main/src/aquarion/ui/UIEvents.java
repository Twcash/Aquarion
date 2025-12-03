package aquarion.ui;

import aquarion.content.ModMusic;
import arc.audio.Music;
import arc.util.Log;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.gen.Musics;

public class UIEvents {
    private static Music musLast = null;
    public static boolean musEnabled = true;

    public static void monitorMusic() {
        Timer.schedule(UIEvents::checkMusic, 1);
    }

    public static void checkMusic() {
        if (!musEnabled) {
            Log.err("Exiting checkMusic skill issue");
            return;
        }

        if (Vars.state.getState() == GameState.State.playing) {
            Music curMus = ModMusic.getCurMusic();
            if (curMus != null && curMus != Musics.menu && curMus != Musics.launch && curMus != Musics.land && ModMusic.getCurMusic() != musLast) {
                musLast = curMus;
                String musS = musLast.toString();
                String[] musS2 = musS.substring(13,musS.length()-4).split("/");
                musS = musS2[musS2.length-1];
                if (ModMusic.musics.containsKey(musS)) {
                    ModMusic.MusicInfo musicInfo = ModMusic.musics.get(musS);
                    ModUI.showBottomToast("Now playing: " + musicInfo.name + " - " + musicInfo.author);
                }
            }
        }
        Timer.schedule(UIEvents::checkMusic, 1);
    }
}
