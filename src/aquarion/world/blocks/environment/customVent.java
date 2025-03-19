package aquarion.world.blocks.environment;

import arc.math.geom.Point2;
import mindustry.world.blocks.environment.SteamVent;

public class customVent extends SteamVent {
    public customVent(String name) {
        super(name);
    }
    public static Point2[] offsets = {
            new Point2(0, 0),
            new Point2(1, 0),
            new Point2(1, 1),
            new Point2(0, 1),
            new Point2(-1, 1),
            new Point2(-1, 0),
            new Point2(-1, -1),
            new Point2(0, -1),
            new Point2(1, -1),
    };
}
