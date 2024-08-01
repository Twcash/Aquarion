package aquarion.world.graphs;

import aquarion.world.interfaces.HasRT;
import arc.struct.*;
public class RTGraph {
    public final Seq<HasRT> builds = new Seq<>();

    // Add a build to the graph
    public void addBuild(HasRT build) {
        if (!builds.contains(build)) {
            builds.addUnique(build);
            build.rotationPower().graph = this;
            build.nextBuilds().each(this::addBuild);
        }
    }

    // Remove a build from the graph
    public void removeBuild(HasRT build, boolean propagate) {
        if (builds.remove(build)) {
            if (propagate) {
                // Re-add remaining builds to the graph
                Seq<HasRT> toReprocess = new Seq<>(builds);
                builds.clear();
                toReprocess.each(this::addBuild);
            } else {
                build.rotationPower().graph = new RTGraph(); // Reset the graph for the removed build
            }
        }
    }

    // Get the total rotation power in the graph
    public float getRT() {
        return builds.sumf(HasRT::RotationPower);
    }
}