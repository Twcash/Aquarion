package aquarion.world.blocks.distribution;

import aquarion.content.AquaBullets;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class ItemYeeter extends Block {
    public float reload = 30;
    public float damage = 1f;
    public float bulletSpeed = 2f;
    public TextureRegion glowRegion;
    @Override
    public void load(){
        super.load();
        glowRegion = Core.atlas.find(name  + "-glow");
    }
    public ItemYeeter(String name) {
        super(name);
        rotate = true;
        rotateDraw = true;
        drawArrow = true;
        hasItems = true;
        itemCapacity = 5;
        acceptsItems = true;
        update = true;
    }
    public class yeeterBuild extends Building{
        public float reloadProg  = 0;
        @Override
        public void updateTile(){
            reloadProg += edelta();
            if(reloadProg >= reload){
                if(!items.empty()) {
                    AquaBullets.throwItem.create(
                            null,
                            this.team,
                            x,
                            y,
                            rotdeg() + Mathf.range(2f),
                            damage,
                            1 * efficiency,
                            bulletSpeed,
                            new ItemStack(items.first(), items.get(items.first()))
                    );
                    items.remove(items.first(), items.get(items.first()));
                    reloadProg = 0;
                }
            }
        }
        @Override
        public boolean acceptItem(Building source, Item item){
            return items.empty() || (item == items.first() && items.get(item) < itemCapacity);
        }
        @Override
        public void draw(){
            Draw.rect(region, x, y, rotdeg());
            Draw.alpha(Mathf.clamp(reloadProg / reload));
            Draw.rect(glowRegion, x, y, rotdeg());
            Draw.reset();
        }
    }
}
