package aquarion.world.Uti;

import arc.math.*;
import mindustry.gen.*;

public class AquaUnitSort{
    public static AquaUnitsUtil.Sortf

            closest = Unit::dst2,
            farthest = (u, x, y) -> -u.dst2(x, y),
            strongest = (u, x, y) -> -u.maxHealth + Mathf.dst2(u.x, u.y, x, y) / 6400f,
            weakest = (u, x, y) -> u.maxHealth + Mathf.dst2(u.x, u.y, x, y) / 6400f;
}