package aquarion.world.interfaces;

import aquarion.world.TorqueModule;
import aquarion.world.graphs.TorqueGraph;
import aquarion.world.meta.TorqueConfig;
import arc.struct.Seq;
import mindustry.gen.Buildingc;

public interface HasTorque extends Buildingc {
    default boolean connects(HasTorque to) {
        return true;
    }

    default HasTorque getTensionDestination(HasTorque from) {
        return this;
    }

    default Seq<HasTorque> nextBuilds() {
        return proximity().select(b -> b instanceof HasTorque other && connects(other))
                .map(b -> ((HasTorque) b).getTensionDestination(this))
                .removeAll(b -> !connects(b) && !b.connects(this) && b.torqueConfig().graphs);
    }

    TorqueModule torque();
    TorqueConfig torqueConfig();

    default TorqueGraph torqueGraph() {
        return torque().graph;
    }

    default float Torque() {
        return 0f;
    }
}