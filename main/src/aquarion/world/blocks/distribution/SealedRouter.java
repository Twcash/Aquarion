package aquarion.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Router;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import aquarion.world.items.AquaItemEntity;

import static mindustry.Vars.itemSize;
import static mindustry.Vars.tilesize;

public class SealedRouter extends Router {
    public float speed = 8f;
    public TextureRegion topRegion;

    public SealedRouter(String name) {
        super(name);
        drawCached = false;
        drawDynamic = true;
        unloadable = true;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.itemsMoved, 60f / speed * itemCapacity, StatUnit.itemsSecond);
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{region, topRegion};
    }

    public class SealedRouterBuild extends RouterBuild implements AquaItemEntity.Container {
        private final Seq<AquaItemEntity> trackedItems = new Seq<>();
        public float progress;
        public @Nullable Item current;
        public int currentData;
        public float currentAge;
        public int recDir = 0;
        public Tile lastInput;
        public int r = 0;
        public @Nullable Building target;

        @Override
        public Seq<AquaItemEntity> aquaItemEntities(){
            return trackedItems;
        }

        @Override
        public void updateTile() {
            AquaItemEntity.update(trackedItems, items, edelta());
            lastItem = trackedItems.isEmpty() ? null : trackedItems.first().item;
            current = lastItem;
            AquaItemEntity trackedCurrent = trackedItems.find(entity -> entity.item == current);
            currentData = trackedCurrent == null ? 0 : trackedCurrent.data;
            currentAge = trackedCurrent == null ? 0f : trackedCurrent.age;
            if (lastItem == null && items.any()) {
                lastItem = items.first();
                current = lastItem;
            }
            progress += edelta() / speed * 2f;
            if(current != null && current.radioactivity > 0){
                if(Mathf.chance(0.05f)) Fx.smoke.at(this);
            }
            if (lastItem != null) {
                // Reset target to null at the beginning of each cycle
                target = null;
                target = getTileTarget(lastItem, lastInput, false);
                if (target != null) {
                    r = relativeToEdge(target.tile);
                    if (progress >= (1f - 1f / speed)) {
                        getTileTarget(lastItem, lastInput, true);
                        target.handleItem(this, lastItem);
                        items.remove(lastItem, 1);
                        lastItem = null;
                        progress = 0;
                        current = null;
                        currentData = 0;
                        currentAge = 0f;
                    }
                }
            } else {
                progress = 0;
            }
        }
        @Override
        public void draw() {
            Draw.z(Layer.blockUnder + 0.3f);
                 Draw.rect(topRegion, x, y);

            Draw.z(Layer.blockUnder + 0.1f);
            Draw.rect(region, x, y);

            // Draw the current item
            if (current != null) {
                Draw.z(Layer.blockUnder + 0.2f);
                Draw.z(Layer.blockUnder + 0.1f);
                Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
                        .lerp(Geometry.d4x(r) * tilesize / 2f, Geometry.d4y(r) * tilesize / 2f,
                                Mathf.clamp((progress + 1f) / 2f));


                AquaItemEntity.draw(current, currentData, currentAge, x + Tmp.v1.x, y + Tmp.v1.y, itemSize);
                Draw.reset();
            }
            Draw.reset();
        }

        @Override
        public void handleItem(Building source, Item item) {
            recDir = relativeToEdge(source.tile);
            lastInput = source.tile;
            current = item;
            progress = -1f;
            AquaItemEntity.receive(this, source, item, items, trackedItems);
            AquaItemEntity tracked = trackedItems.find(entity -> entity.item == item);
            currentData = tracked == null ? 0 : tracked.data;
            currentAge = tracked == null ? 0f : tracked.age;
            noSleep();
        }

        @Override
        public byte version(){
            return 2;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.b(recDir);
            AquaItemEntity.write(write, trackedItems);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 1){
                recDir = read.b();
            }
            if(revision >= 2) AquaItemEntity.read(read, trackedItems);
            AquaItemEntity.reconcile(items, trackedItems);
            current = items.first();
            AquaItemEntity trackedCurrent = trackedItems.find(entity -> entity.item == current);
            currentData = trackedCurrent == null ? 0 : trackedCurrent.data;
            currentAge = trackedCurrent == null ? 0f : trackedCurrent.age;
        }
    }
}

