package aquarion.world.blocks.distribution;

import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.type.Item;
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
            //TODO There is a *tiny* gap where the items can duplicate. Although it's not like a massive issue and quite rare.
            Groups.bullet.intersect(x, y, block.size * 4f, block.size * 4f).each(b ->{
                if(b!=null&&b.data instanceof Item item){
                    if(acceptItem(this, item)) {
                        Fx.smoke.at(b.x, b.y);
                        b.remove();
                        items.add(item, 1);
                    }
                }
            });
            dump();
        }
        @Override
        public boolean acceptItem(Building source, Item item){
            //This building should not accept items from anything but itself.
            return items.get(item) < itemCapacity && source == this;
        }
    }
}
