package aquarion.world.blocks.distribution.spacetransfer;

import arc.struct.IntMap;
import mindustry.type.Item;
import mindustry.world.modules.ItemModule;

public class SpaceNet {
    public static IntMap<ItemModule> orbitCargo = new IntMap<>();

    public static void addCargo(int sectorId, Item item, int amount) {
        if (!orbitCargo.containsKey(sectorId)) {
            orbitCargo.put(sectorId, new ItemModule());
        }
        orbitCargo.get(sectorId).add(item, amount);
    }

    public static int getCargo(int sectorId, Item item) {
        if (!orbitCargo.containsKey(sectorId)) return 0;
        return orbitCargo.get(sectorId).get(item);
    }

    public static void removeCargo(int sectorId, Item item, int amount) {
        if (orbitCargo.containsKey(sectorId)) {
            orbitCargo.get(sectorId).remove(item, amount);
        }
    }
}