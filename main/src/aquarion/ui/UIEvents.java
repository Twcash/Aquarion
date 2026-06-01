package aquarion.ui;

import aquarion.content.ModMusic;
import arc.Core;
import arc.audio.Music;
import arc.input.KeyBind;
import arc.input.KeyCode;
import arc.input.KeybindValue;
import arc.util.Log;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.gen.Musics;

public class UIEvents {
    private static Music musLast = null;
    public static boolean musEnabled = true;

    public enum AquaBinding implements KeyBind {
        show_music(KeyCode.f3, "aquarion");

        private final KeybindValue defaultValue;
        private final String category;

        AquaBinding(KeybindValue value, String category) {
            this.defaultValue = value;
            this.category = category;
        }

        @Override
        public KeybindValue defaultValue(boolean controller) {
            return defaultValue;
        }

        @Override
        public String category() {
            return category;
        }
    }

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
            Core.keybinds.setDefaults(AquaBinding.values());

            arc.Events.run(mindustry.game.EventType.Trigger.update, () -> {
                if (Core.keybinds.get(AquaBinding.show_music).key.tap()) {
                    showCurrentMusic();
                }
            });
        } else {
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
