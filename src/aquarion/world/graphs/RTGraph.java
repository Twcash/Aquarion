package aquarion.world.graphs;

import aquarion.world.interfaces.HasRT;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.world.consumers.Consume;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
public class RTGraph {
    private HashMap<Building, Integer> buildings = new HashMap<>();
    public final Seq<HasRT> builds = new Seq<>();
    private HashSet<Building> connectedBuildings = new HashSet<>();


    public void addBuilding(Building building) {
        connectedBuildings.add(building);
        if (!buildings.containsKey(building)) {
            buildings.put(building, 0);
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
    }
    public float getProduction() {
        return builds.sumf(HasRT::rotationProduction);
    }
    public float getConsumption() {
        return builds.sumf(HasRT::rotationConsumption);
    }

    public float getTotalRotationPower() {
        return builds.sumf(b -> b.rotationConsumption() + b.rotationProduction());
    }
}