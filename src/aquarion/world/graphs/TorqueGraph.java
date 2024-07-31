package aquarion.world.graphs;

import aquarion.world.interfaces.HasTorque;
import arc.struct.Seq;

public class TorqueGraph {
    public final Seq<HasTorque> builds = new Seq<>();

    /**
     * Adds a build and its links into this graph.
     */
    public void addBuild(HasTorque build) {
        if (builds.contains(build)) return;
        builds.addUnique(build);
        build.torqueGraph().removeBuild(build, false);
        build.torque().graph = this;
        build.nextBuilds().each(this::addBuild);
    }

    /**
     * Removes a build from this graph.
     * @param propagate creates another graph for the removed build, if false, the build "stays" linked to this graph.
     */
    public void removeBuild(HasTorque build, boolean propagate) {
        if (propagate) {
            builds.remove(build);
            new TorqueGraph().addBuild(build);
        } else {
            builds.remove(build);
        }
    }

    /**
     * Calculates the total torque for all builds in this graph.
     */
    public float getTotalTorque() {
        return builds.sumf(HasTorque::Torque);
    }

    @Override
    public String toString() {
        return "TorqueGraph{" +
                "builds=" + builds +
                '}';
    }
}