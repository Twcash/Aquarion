package aquarion.content;

import mindustry.game.Schematic;
import mindustry.game.Schematics;

public class AquaLoadouts{
    public static Schematic basicPike;
    public static Schematic reception;

    public static void load(){
        basicPike = Schematics.readBase64("bXNjaAF4nGNgYWBhZmDJS8xNZeBIzi9KDcjMTmXgTkktTi7KLCjJzM9jYGBgy0lMSs0pZmCKjmVkEEosLE0sAkrogpTrFoDUMzAwghCQAABH6xPv");
        reception = Schematics.readBase64("bXNjaAF4nD2LsQrCQBAFX45EIcEm/5FPsbGVFOvmQQ5yd/F2rcR/Vwg43cAMAkKHXkt2Zr/KjvD+4LTJg5sh3Ge0WRJx0VJ5o3L3WDKGhaY1HgKczSkpLuhNVybxqNZglOdL6q+Y6n8EGhx8AWI3IpM=");
    }
}
