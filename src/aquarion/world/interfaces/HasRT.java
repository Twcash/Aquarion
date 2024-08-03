package aquarion.world.interfaces;

import aquarion.world.graphs.RTGraph;
import aquarion.world.meta.RTConfig;
import aquarion.world.meta.RTModule;
import arc.struct.*;
import mindustry.gen.*;
public interface HasRT extends Buildingc {

    default boolean connected(HasRT to) {
        return connects(to) && to.connects(this);
    }

    default boolean connects(HasRT to) {
        return rTConfig().outputsRT || rTConfig().acceptsRT;
    }

    default float getRotationPower() {
        return rotationPower().rotationPower;
    }

    default void setRotationPower(float rotationPower) {
        rotationPower().rotationPower = rotationPower;
    }

    default HasRT getRTDest(HasRT from, float rotationPower) {
        return this;
    }

    default Seq<HasRT> nextBuilds(boolean flow) {
        return proximity().select(
                b -> b instanceof HasRT
        ).<HasRT>as().map(
                b -> b.getRTDest(this, 0)
        ).removeAll(
                b -> !connected(b) && proximity().contains((Building) b));
    }

    RTModule rotationPower();

    RTConfig rTConfig();

    default RTGraph rTGraph() {
        return rotationPower().graph;
    }

    default void updateGraph() {
        RTGraph currentGraph = rTGraph();
        for (Building b : proximity()) {
            if (b instanceof HasRT && connected((HasRT) b)) {
                RTGraph neighborGraph = ((HasRT) b).rTGraph();
                if (neighborGraph != currentGraph) {
                    currentGraph.merge(neighborGraph);
                }
            }
        }
    }
}