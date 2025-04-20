package aquarion.world.blocks.distribution;

import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.ChainedBuilding;

import static mindustry.Vars.*;

public class ItemTram extends Block {
    public float itemSpace = 0.4f;
    public int range = 5;
    public float speed = 5f;

    public ItemTram(String name) {
        super(name);
    }

    @Override
    public void init(){
        updateClipRadius((range + 0.5f) * tilesize);
        super.init();
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation){
        Placement.calculateNodes(points, this, rotation, (point, other) -> Math.max(Math.abs(point.x - other.x), Math.abs(point.y - other.y)) <= range);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        drawPlace(x, y, rotation, valid, true, null);
    }

    public void drawPlace(int x, int y, int rotation, boolean valid, boolean line, @Nullable Item item){
        int length = range;
        Building found = null;
        int dx = Geometry.d4x(rotation), dy = Geometry.d4y(rotation);

        for(int i = 1; i <= range; i++){
            Tile other = world.tile(x + dx * i, y + dy * i);

            if(other != null && other.build != null && item != null &&
                    other.build.team == player.team()){
                length = i;
                found = other.build;
                break;
            }
        }

        if(line || found != null){
            Drawf.dashLine(Pal.placing,
                    x * tilesize + dx * (tilesize / 2f + 2),
                    y * tilesize + dy * (tilesize / 2f + 2),
                    x * tilesize + dx * (length) * tilesize,
                    y * tilesize + dy * (length) * tilesize
            );
        }

        if(found != null){
            if(line){
                Drawf.square(found.x, found.y, found.block.size * tilesize/2f + 2.5f, 0f);
            }else{
                Drawf.square(found.x, found.y, 2f);
            }
        }
    }

    public class ItemTramBuild extends Building {
        public ItemTramBuild[] occupied = new ItemTramBuild[4];
        public int capacity = 0;
        public Item[] ids = new Item[capacity];
        public float[] xs = new float[capacity], ys = new float[capacity];
        public @Nullable Building next;
        public @Nullable ItemTramBuild nextc;
        public float progress = 0f;
        public @Nullable ItemTramBuild lastLink;

        @Override
        public void drawSelect(){
            drawPlace(tile.x, tile.y, rotation, true, false, ids.length > 0 ? ids[0] : null);

            for(int dir = 0; dir < 4; dir++){
                if(dir != rotation){
                    int dx = Geometry.d4x(dir), dy = Geometry.d4y(dir);
                    Building found = occupied[(dir + 2) % 4];

                    if(found != null){
                        int length = Math.max(Math.abs(found.tileX() - tileX()), Math.abs(found.tileY() - tileY()));
                        Drawf.dashLine(Pal.place,
                                found.x - dx * (tilesize / 2f + 2),
                                found.y - dy * (tilesize / 2f + 2),
                                found.x - dx * (length) * tilesize,
                                found.y - dy * (length) * tilesize
                        );

                        Drawf.square(found.x, found.y, 2f, 45f, Pal.place);
                    }
                }
            }
        }

        @Override
        public void updateTile(){
            updateLink();

            if(lastLink != null){
                nextc = lastLink;
                next = lastLink;

                if(canMoveNext() && ids[0] != null){
                    if(nextc.acceptTrainItem(ids[0])){
                        nextc.pushTrainItem(ids[0]);
                        shiftItemsLeft();
                    }
                }
            }

            if(items.any() && itemCount() < capacity){
                Item item = items.take();
                if(item != null){
                    pushItemToTrain(item);
                }
            }
        }

        public void pushItemToTrain(Item item){
            for(int i = 0; i < capacity; i++){
                if(ids[i] == null){
                    ids[i] = item;
                    break;
                }
            }
        }

        public void shiftItemsLeft(){
            for(int i = 0; i < capacity - 1; i++){
                ids[i] = ids[i + 1];
            }
            ids[capacity - 1] = null;
        }

        public boolean acceptTrainItem(Item item){
            return itemCount() < capacity;
        }

        public void pushTrainItem(Item item){
            pushItemToTrain(item);
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            if(findLink() == null) return false;
            int rel = this.relativeToEdge(source.tile);
            return items.total() < itemCapacity && rel != rotation && occupied[(rel + 2) % 4] == null;
        }

        @Nullable
        public ItemTramBuild findLink(){
            for(int i = 1; i <= range; i++){
                Tile other = tile.nearby(Geometry.d4x(rotation) * i, Geometry.d4y(rotation) * i);
                if(other != null && other.build instanceof ItemTramBuild build && build.block == ItemTram.this && build.team == team){
                    return build;
                }
            }
            return null;
        }

        public void updateLink(){
            lastLink = findLink();
            if(lastLink != null){
                int dist = Math.max(Math.abs(tileX() - lastLink.tileX()), Math.abs(tileY() - lastLink.tileY()));
                capacity = dist * 3;

                if(ids.length != capacity){
                    ids = new Item[capacity];
                    xs = new float[capacity];
                    ys = new float[capacity];
                }
            }else{
                capacity = 0;
                ids = new Item[0];
                xs = ys = new float[0];
            }
        }

        boolean canMoveNext(){
            return nextc != null && nextc.capacity > nextc.itemCount();
        }

        int itemCount(){
            int count = 0;
            for(Item item : ids){
                if(item != null) count++;
            }
            return count;
        }
    }
}