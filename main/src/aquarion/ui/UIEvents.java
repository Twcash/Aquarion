package aquarion.ui;

import aquarion.content.ModMusic;
import arc.Core;
import arc.audio.Music;
import arc.input.KeyCode;
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
                    ModUI.showBottomToast(Core.bundle.format("aquarion.music.now_playing", musicInfo.name, musicInfo.author));
                }
            }
        }
        Timer.schedule(UIEvents::checkMusic, 1);
    }

    public static void showCurrentMusic() {
        Music curMus = ModMusic.getCurMusic();
        if (curMus == null) {
            ModUI.showBottomToast(Core.bundle.get("aquarion.music.nothing"));
            return;
        }
        String musS = curMus.toString();
        String[] musS2 = musS.substring(13, musS.length() - 4).split("/");
        musS = musS2[musS2.length - 1];
        if (ModMusic.musics.containsKey(musS)) {
            ModMusic.MusicInfo info = ModMusic.musics.get(musS);
            ModUI.showBottomToast(Core.bundle.format("aquarion.music.now_playing", info.name, info.author));
        } else {
            ModUI.showBottomToast(Core.bundle.format("aquarion.music.now_playing_unknown", musS));
        }
    }

    public static void registerControls() {
        if (!Vars.mobile) {
            arc.Events.run(mindustry.game.EventType.Trigger.update, () -> {
                if (Core.input.keyTap(KeyCode.f3)) {
                    showCurrentMusic();
                }
            });
        } else {
            Vars.ui.paused.shown(() -> {
                Vars.ui.paused.cont.row();
                Vars.ui.paused.cont.button(Core.bundle.get("aquarion.music.button"), () -> {
                    Vars.ui.paused.hide();
                    showCurrentMusic();
                }).size(270f, 50f).pad(4f);
            });
        }
    }
}
