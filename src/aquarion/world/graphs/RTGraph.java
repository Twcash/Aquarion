package aquarion.world.graphs;


import aquarion.world.interfaces.HasRT;
import arc.struct.*;

public class RTGraph {
    public final Seq<HasRT> builds = new Seq<>();
    // Add a build to the graph

    public void addBuild(HasRT build) {
        if (builds.contains(build)) return;
        builds.addUnique(build);
        build.rTGraph().removeBuild(build, false);
        build.rotationPower().graph = this;
        build.nextBuilds().each(this::addBuild);
    }


    // Remove a build from the graph
    public void removeBuild(HasRT build, boolean propagate) {
        if (propagate) {
            builds.remove(build);
            new RTGraph().addBuild(build);
        } else {
            builds.remove(build);
        }
    }

    // Get the total rotation power in the graph
    public float getRT() {
        return builds.sumf(HasRT::RotationPower);
    }
}