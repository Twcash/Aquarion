package aquarion.world.blocks.distribution;

import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class ItemHopper extends Block {
    public float reload = 10;
    public ItemHopper(String name) {
        super(name);
        itemCapacity = 10;
        hasItems = true;
        update = true;
    }
    public class HopperBuild extends Building{
        public float curReload = 0;
        @Override
        public void updateTile(){
            curReload += edelta();
            if(curReload >= reload) {
                Groups.bullet.intersect(x, y, (block.size * 4f, block.size * 4f)-1.5f).each(b -> {
                    if (b != null && b.data instanceof ItemStack item) {
                        if (acceptItem(this, item.item)) {
                            Fx.smoke.at(b.x, b.y);
                            b.remove();
                            items.add(item.item, item.amount);
                        } else {
                            return;
                        }
                    }
                });
                reload = 0;
            }
            dump();
        }
        @Override
        public boolean acceptItem(Building source, Item item){
            //This building should not accept items from anything but itself.
            return items.get(item) < (itemCapacity - 5) && source == this;
        }
    }
}
