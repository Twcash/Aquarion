package aquarion;
import aquarion.ui.ModSettings;
import aquarion.ui.ModUI;
import aquarion.ui.UIEvents;
import arc.Core;
import arc.discord.DiscordRPC;
import arc.util.*;
import mindustry.gen.Groups;

import static mindustry.Vars.*;

public class RichHandler {
    public static float time = Time.time;
        public static final long appId = 1281506459309441079L;
        public static DiscordRPC.RichPresence presence = new DiscordRPC.RichPresence();

        public static void init() {
            if (mobile || !Core.settings.getBool("aquarion-rich", true)) return;

            presence.startTimestamp = System.currentTimeMillis();

            try {
                Log.info("Assasination");
                DiscordRPC.close();
                Reflect.set(platform, "useDiscord", false);
                DiscordRPC.connect(appId);
                send();
            } catch (Exception e) {
                Log.err("Aquarion being dumb, try to reload", e);
                return;
            }

            Timer.schedule(RichHandler::send, 0f, 5f, -1);
        }

        static void send() {
            boolean useDiscord = !OS.hasProp("nodiscord");
            if (useDiscord) {
                try {
                    var planet = state.rules.planet;

                    String gameMode = "", gamePlayersSuffix = "", uiState = "";

                    if (state.isGame()) {
                        String gameMapWithWave;
                        gameMapWithWave = Strings.capitalize(Strings.stripColors(state.map.name()));

                        if (state.rules.waves) {
                            gameMapWithWave += " | Wave " + state.wave;
                        }
                        gameMode = state.rules.pvp ? "Gang Violence" : state.rules.attackMode ? "Violence" : state.rules.infiniteResources ? "Sandbox" : "Survival";
                        if (net.active() && Groups.player.size() > 1) {
                            gamePlayersSuffix = " | " + Groups.player.size() + " Friends";
                        }

                        presence.details = gameMapWithWave;
                        presence.state = gameMode + gamePlayersSuffix;
                    } else {
                        if (ui.editor != null && ui.editor.isShown()) {
                            uiState = "Cooking";
                        } else if (ui.planet != null && ui.planet.isShown()) {
                            uiState = "Deciding, but closer";
                        } else {
                            uiState = "Deciding";
                        }

                        presence.details = "";
                        presence.state = uiState;
                    }

                    presence.largeImageKey = "logo";
                    if(time % 100 == 0) {
                        presence.label1 = "PLEASE PLEASE PLAY AQUARION";
                    } else if(time % 50 == 0){
                        presence.label1 = "I AM BEGGING YOU PLAY THE MOD";
                    } else if (time % 1000 == 0){
                        presence.label1 = "Meld is a good mod too";
                    } else {
                        presence.label1 = "Try Aquarion";
                    }
                    presence.url1 = "https://github.com/Twcash/Aquarion/releases";

                    DiscordRPC.send(presence);


                } catch (Exception e) {
                    Log.err("Aquarion being dumb, try again", e);
                }
            }
        }
}
