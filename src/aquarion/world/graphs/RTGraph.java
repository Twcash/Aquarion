package aquarion.world.graphs;

import mindustry.gen.Building;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
public class RTGraph {
    private HashMap<Building, Integer> buildings = new HashMap<>();
    private HashSet<Building> connectedBuildings = new HashSet<>();

    public void addBuilding(Building building) {
        connectedBuildings.add(building);
        if (!buildings.containsKey(building)) {
            buildings.put(building, 0);
        }
    }

    public void removeBuilding(Building building, boolean removeConnections) {
        connectedBuildings.remove(building);
        if (removeConnections) {
            buildings.remove(building);
        }
    }

    public boolean contains(Building building) {
        return connectedBuildings.contains(building);
    }

    public void addRotationPower(Building building, int amount) {
        buildings.put(building, buildings.getOrDefault(building, 0) + amount);
    }

    public void removeRotationPower(Building building, int amount) {
        buildings.put(building, buildings.getOrDefault(building, 0) - amount);
    }

    public int getTotalRotationPower() {
        return buildings.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void merge(RTGraph otherGraph) {
        otherGraph.buildings.forEach(this::addRotationPower);
        this.connectedBuildings.addAll(otherGraph.connectedBuildings);
    }
}