package aquarion.content;

import mindustry.game.Schematic;
import mindustry.game.Schematics;

public class AquaLoadouts{
    public static Schematic basicPike;
    public static Schematic basicCuesta;
    public static Schematic reception;

    public static void load(){
        basicPike = Schematics.readBase64("bXNjaAF4nGNgYWBhZmDJS8xNZeBIzi9KDcjMTmXgTkktTi7KLCjJzM9jYGBgy0lMSs0pZmCKjmVkEEosLE0sAkrogpTrFoDUMzAwghCQAABH6xPv");
        basicCuesta = Schematics.readBase64("bXNjaAF4nCXLQQrCMBBA0d+gEerWa/QUrj2BuBjTAQNNWjPTlXh3W+Rv3ycSj/Rprq7Vb7IQPl/iJE+djHB/cKhSdBdNr6uaC+dRLbW8eJ4rcDJXKXmkt/TSIp6TdVzkvUrbxLCfQ/qv0EHY4gcq4yHs");
        reception = Schematics.readBase64("bXNjaAF4nGNgYmBiYeBKzs8rSc0r8U0sYGCqrmVgy0lMSs0pZmCKjmVgyUvMTWXgDEpNTi0oyczPY+BOSS1OLsqEcBgYGYQSC0sTi4Ac3SK4GpA4BAAACJYZyQ==");
    }
}
