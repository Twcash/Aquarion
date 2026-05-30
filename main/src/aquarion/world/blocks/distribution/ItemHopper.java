package aquarion.world.blocks.distribution;

import aquarion.ui.ModSettings;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.geom.Rect;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class ItemHopper extends Block {
    public float reload = 10;

    public static Rect tempRect = new Rect();

    /* Hitbox size in fractions of this block's size. e.g. 1 = this blocks's size, 0.5 = half the block's size, 2 = twice the block's size, etc*/
    public float hitscale = 1;

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
                tempRect.setCentered(this.x, this.y, size * Vars.tilesize * hitscale);
                Groups.bullet.intersect(tempRect.x, tempRect.y, tempRect.width, tempRect.height).each(b -> {
                    if (b != null && b.isAdded() && b.data instanceof ItemStack item) {
                        if (acceptItem(this, item.item)) {
                            Fx.smoke.at(b.x, b.y);
                            Fx.smoke.at(x, y);
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
            return items.get(item) < itemCapacity && source == this;
        }

        @Override
        public void draw() {
            super.draw();
            if(ModSettings.getDebugHitboxRendering()){
                float z = Draw.z();
                Draw.z(Layer.overlayUI);
                Draw.color(Color.red);
                tempRect.setCentered(this.x, this.y, size * Vars.tilesize * hitscale);
                Lines.rect(tempRect);
                Draw.reset();
                Draw.z(z);
            }
        }
    }
}
