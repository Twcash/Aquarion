package aquarion.world.blocks.distribution.spacetransfer;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;

public class SpaceReceiver extends Block {
    public float waterPerReceive = 40f;

    public SpaceReceiver(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        hasLiquids = true;
    }

    public class SpaceReceiverBuild extends Building {

        public boolean canReceive() {
            return liquids.get(Liquids.water) >= waterPerReceive && items.total() < itemCapacity;
        }

        public void handleIncomingItems(Item item, int amount) {
            Fx.spawn.at(x, y); 
            liquids.remove(Liquids.water, waterPerReceive);
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

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return liquid == Liquids.water && liquids.get(liquid) < liquidCapacity;
        }
    }
}
