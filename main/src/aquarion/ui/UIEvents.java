package aquarion.ui;

import aquarion.content.ModMusic;
import arc.Core;
import arc.audio.Music;
import arc.input.KeyBind;
import arc.input.KeyCode;
import arc.util.Log;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.gen.Musics;

public class UIEvents {
    private static Music musLast = null;
    public static boolean musEnabled = true;

    public static final KeyBind showMusicBind = KeyBind.add("aquarion_show_music", KeyCode.f3, "Aquarion");

    public static void monitorMusic() {
        Timer.schedule(UIEvents::checkMusic, 1);
    }

    private static String getValidIcon(String iconName) {
        if (iconName == null || iconName.isEmpty() || !Core.atlas.has(iconName)) {
            return ModMusic.DEFAULT_ICON;
        }
        return iconName;
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
                String[] musS2 = musS.substring(13, musS.length() - 4).split("/");
                musS = musS2[musS2.length - 1];
                if (ModMusic.musics.containsKey(musS)) {
                    ModMusic.MusicInfo musicInfo = ModMusic.musics.get(musS);

                    String finalIcon = getValidIcon(musicInfo.iconName);
                    String message = Core.bundle.format("aquarion.music.now_playing", musicInfo.name, musicInfo.author);

                    ModUI.showBottomToast(message, finalIcon);
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

            String finalIcon = getValidIcon(info.iconName);
            String message = Core.bundle.format("aquarion.music.now_playing", info.name, info.author);

            ModUI.showBottomToast(message, finalIcon);
        } else {
            String message = Core.bundle.format("aquarion.music.now_playing_unknown", musS);
            ModUI.showBottomToast(message, ModMusic.DEFAULT_ICON);
        }
    }

    public static void registerControls() {
        showMusicBind.load();

        if (!Vars.mobile) {
            arc.Events.run(mindustry.game.EventType.Trigger.update, () -> {
                if (Core.input.keyTap(showMusicBind)) {
                    showCurrentMusic();
                }
            });
        }
        if (Vars.mobile) {
            Vars.ui.paused.shown(() -> {
                arc.scene.ui.layout.Table cont = Vars.ui.paused.cont;
                cont.row();
                cont.button(b -> {
                    b.defaults().center();
                    b.add(new arc.scene.ui.Image(mindustry.gen.Icon.effect)).size(42f).row();
                    b.add(Core.bundle.get("aquarion.music.button")).wrap().width(120f).center().labelAlign(arc.util.Align.center);
                }, () -> {
                    Vars.ui.paused.hide();
                    showCurrentMusic();
                }).size(140f).pad(4f);
            });
        }
    }
}