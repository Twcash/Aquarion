package aquarion.world.interfaces;

import aquarion.world.graphs.RTGraph;
import aquarion.world.meta.RTConfig;
import aquarion.world.meta.RTModule;
import arc.struct.*;
import mindustry.gen.*;

public interface HasRT extends Buildingc {
    default boolean connects(HasRT to) {
        return to.rTConfig().tier == rTConfig().tier || to.rTConfig().tier == -1 || rTConfig().tier == -1;
    }

    default HasRT getRTDest(HasRT from) {
        return this;
    }

    default Seq<HasRT> nextBuilds() {
        return Seq.with(nearby(0), nearby(1), nearby(2), nearby(3))
                .select(b -> b instanceof HasRT other && connects(other))
                .map(b -> ((HasRT) b).getRTDest(this))
                .removeAll(b -> !connects(b) && !b.connects(this) && b.rTConfig().graphs);
    }

    RTModule rotationPower();
    RTConfig rTConfig();

    default RTGraph rTGraph() {
        return rotationPower().graph;
    }

    default float RotationPower() {
        return rTConfig().rotationPower;
    }
}