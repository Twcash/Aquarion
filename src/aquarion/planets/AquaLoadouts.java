package aquarion.planets;

import mindustry.game.Schematic;
import mindustry.game.Schematics;

public class AquaLoadouts{
    public static Schematic basicPike;

    public static void load(){
        basicPike = Schematics.readBase64("bXNjaAF4nGNgYWBhZmDJS8xNZeBIzi9KDcjMTmXgTkktTi7KLCjJzM9jYGBgy0lMSs0pZmCKjmVkEEosLE0sAkrogpTrFoDUMzAwghCQAABH6xPv");
    }
}
