package aquarion.world.blocks.distribution.spacetransfer;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;

public class SpaceReceiver extends Block {

    public SpaceReceiver(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        itemCapacity = 500;
    }

    public class SpaceReceiverBuild extends Building {

        public void handleIncomingItems(Item item, int amount) {
            items.add(item, amount);
        }

        @Override
        public void updateTile() {
            if (items.total() > 0) {
                dump(); 
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return false; 
        }
    }
}
