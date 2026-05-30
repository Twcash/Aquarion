package aquarion.world.blocks.distribution;

import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class ItemHopper extends Block {
    public ItemHopper(String name) {
        super(name);
        itemCapacity = 10;
        hasItems = true;
        update = true;
    }
    public class HopperBuild extends Building{
        @Override
        public void updateTile(){
            float size = block.size * 4f - 0.5f;
            Groups.bullet.intersect(x - size / 2f, y - size / 2f, size, size).each(b -> {
                if (b != null && b.data instanceof ItemStack) {
                    ItemStack item = (ItemStack) b.data;
                    if (items.get(item.item) < itemCapacity) {
                        Fx.smoke.at(b.x, b.y);
                        b.remove();
                        items.add(item.item, item.amount);
                    }
                }
            });
            dump();
        }
        @Override
        public boolean acceptItem(Building source, Item item){
            return items.get(item) < itemCapacity;
        }
    }
}