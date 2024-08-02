package aquarion.world.graphs;

import mindustry.gen.Building;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class RTGraph {
    private final Map<Building, Integer> rotationPower = new HashMap<>();
    private final Set<Building> connectedBuildings = new HashSet<>();

    public void addBuilding(Building building) {
        connectedBuildings.add(building);
    }

    public void removeBuilding(Building building, boolean b) {
        connectedBuildings.remove(building);
        rotationPower.remove(building);
    }

    public void addRotationPower(Building building, int amount) {
        rotationPower.put(building, rotationPower.getOrDefault(building, 0) + amount);
    }

    public void removeRotationPower(Building building, int amount) {
        rotationPower.put(building, rotationPower.getOrDefault(building, 0) - amount);
    }

    public int getTotalRotationPower() {
        return rotationPower.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean contains(Building building) {
        return connectedBuildings.contains(building);
    }

    public void updateGraph(Building building, Consumer<Building> graphUpdater) {
        if (connectedBuildings.contains(building)) {
            connectedBuildings.forEach(graphUpdater);
        }
    }
}