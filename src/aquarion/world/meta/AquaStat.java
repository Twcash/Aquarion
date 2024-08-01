package aquarion.world.meta;

import mindustry.world.meta.*;

public class AquaStat {
    public static final StatCat rotationPow = new StatCat("aqua-rotation-power");

    public static final StatUnit rotationUnits = new StatUnit("aqua-rotation-units");

    public static final Stat
            totalRT = new Stat("aqua-total-rotation-power", rotationPow),
            requiredRT = new Stat("aqua-required-rotation-power", rotationPow);

}