package aquarion.world.blocks.distribution;

import aquarion.content.AquaBullets;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Tmp;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import aquarion.world.items.AquaItemEntity;

import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

public class ItemYeeter extends Block {
    public float reload = 30;
    public float damage = 1f;
    public float bulletSpeed = 2f;
    private int range = (int)(bulletSpeed * 18.5f*8);
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
    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);
        Tmp.v1.set(range ,0).rotate(rotation*90);
        Drawf.dashCircle(Tmp.v1.x+(x * tilesize + offset), Tmp.v1.y+(y * tilesize + offset),  8, Pal.placing);
    }
    public class yeeterBuild extends Building implements AquaItemEntity.Container{
        private final Seq<AquaItemEntity> trackedItems = new Seq<>();
        public float reloadProg  = 0;

        @Override
        public Seq<AquaItemEntity> aquaItemEntities(){
            return trackedItems;
        }

        @Override
        public void updateTile(){
            AquaItemEntity.update(trackedItems, items, edelta());
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
                    AquaItemEntity.reconcile(items, trackedItems);
                    reloadProg = 0;
                }
            }
        }
        @Override
        public boolean acceptItem(Building source, Item item){
            return items.empty() || (item == items.first() && items.get(item) < itemCapacity);
        }

        @Override
        public void handleItem(Building source, Item item){
            AquaItemEntity.receive(this, source, item, items, trackedItems);
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(reloadProg);
            AquaItemEntity.write(write, trackedItems);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 1){
                reloadProg = read.f();
                AquaItemEntity.read(read, trackedItems);
            }
            AquaItemEntity.reconcile(items, trackedItems);
        }
        @Override
        public void draw(){
            Draw.rect(region, x, y, rotdeg());
            Draw.alpha(Mathf.clamp(reloadProg / reload));
            Draw.rect(glowRegion, x, y, rotdeg());
            Draw.reset();
        }
        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, range*efficiency, team.color);
            float throwDist = range * efficiency;
            Tmp.v1.set(throwDist ,0).rotate(rotation*90);
            Drawf.dashCircle(Tmp.v1.x+x, Tmp.v1.y+y,  8, team.color);
        }
    }
}

