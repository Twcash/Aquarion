package aquarion.world.graphs;

import aquarion.world.interfaces.HasRT;
import arc.struct.Seq;
import mindustry.gen.Building;

import java.util.HashMap;
import java.util.HashSet;

public class RTGraph extends Graph {
    private final HashMap<Building, Integer> buildings = new HashMap<>();
    public final Seq<HasRT> builds = new Seq<>();
    private final HashSet<Building> connectedBuildings = new HashSet<>();

    public RTGraph() {
        super();
        addGraph();
    }



    public void update() {
        // Implement the update logic for the RTGraph
        float totalProduction = getProduction();
        float totalConsumption = getConsumption();

        // Distribute rotation power among connected buildings
        for (Building building : connectedBuildings) {
            Integer currentPower = buildings.get(building);
            // Update each building's rotation power based on the total production and consumption
            buildings.put(building, currentPower + (int)(totalProduction - totalConsumption));
        }
    }

    public void removeBuild(HasRT build, boolean propagate) {
        if (propagate) {
            builds.remove(build);
            new RTGraph().addBuild(build);
        } else {
            builds.remove(build);
        }
    }

    public void addBuild(HasRT build) {
        if (builds.contains(build)) return;
        builds.addUnique(build);
        build.rTGraph().removeBuild(build, false);
        build.rotationPower().graph = this;
        build.nextBuilds().each(this::addBuild);
        build.update();
    }

    public void merge(RTGraph other, boolean priority) {
        if (other.builds.size > builds.size && !priority) {
            other.merge(this, false);
        } else {
            other.builds.each(this::addBuild);
        }
        builds.each(HasRT::onRTupdate);
    }

    public void remove(HasRT build, boolean split) {
        builds.remove(build);
        if (split) {
            build.nextBuilds().each(p -> {
                p.rTGraph().merge(new RTGraph(), true);
            });
        }
    }

    public float getProduction() {
        return builds.sumf(HasRT::rotationProduction);
    }

    public float getConsumption() {
        return builds.sumf(HasRT::rotationConsumption);
    }

    public float getTotalRotationPower() {
        return builds.sumf(b -> b.rotationProduction() - b.rotationConsumption());
    }

    public Seq<HasRT> getBuildings() {
        return builds;
    }
}
