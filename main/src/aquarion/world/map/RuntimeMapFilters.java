package aquarion.world.map;

import arc.struct.Seq;
import arc.struct.IntSeq;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.maps.Map;
import mindustry.maps.filters.FilterOption;
import mindustry.maps.filters.GenerateFilter;
import mindustry.maps.filters.BlendFilter;
import mindustry.maps.filters.DistortFilter;
import mindustry.maps.filters.MedianFilter;
import mindustry.maps.filters.OreMedianFilter;
import mindustry.io.JsonIO;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.Tiles;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OreBlock;
//YOUR WELCOME VIRE
public class RuntimeMapFilters{
    /** Keep vanilla JSON tags so existing maps load through every World/SaveIO entry point. */
    public static void install(){
        JsonIO.classTag("blend", RuntimeBlendFilter.class);
        JsonIO.classTag("distort", RuntimeDistortFilter.class);
        JsonIO.classTag("median", RuntimeMedianFilter.class);
        JsonIO.classTag("oreMedian", RuntimeOreMedianFilter.class);
    }

    public static class RuntimeBlendFilter extends BlendFilter{
        @Override
        public String simpleName(){ return "blend"; }

        @Override
        public void apply(Tiles tiles, GenerateInput in){
            new BufferedFilter(this).apply(tiles, in);
        }
    }

    public static class RuntimeDistortFilter extends DistortFilter{
        @Override
        public String simpleName(){ return "distort"; }

        @Override
        public void apply(Tiles tiles, GenerateInput in){
            new BufferedFilter(this).apply(tiles, in);
        }
    }

    public static class RuntimeMedianFilter extends MedianFilter{
        @Override
        public String simpleName(){ return "median"; }

        private final transient IntSeq runtimeFloors = new IntSeq(), runtimeBlocks = new IntSeq();

        @Override
        public void apply(GenerateInput in){
            runtimeFloors.clear();
            runtimeBlocks.clear();
            int rad = (int)radius;
            for(int x = -rad; x <= rad; x++){
                for(int y = -rad; y <= rad; y++){
                    if(Mathf.dst2(x, y) > rad * rad) continue;
                    Tile tile = in.tile(in.x + x, in.y + y);
                    runtimeFloors.add(tile.floor().id);
                    runtimeBlocks.add(tile.block().id);
                }
            }
            runtimeFloors.sort();
            runtimeBlocks.sort();
            in.floor = Vars.content.blocks().get(runtimeFloors.get(Math.min((int)(runtimeFloors.size * percentile), runtimeFloors.size - 1)));
            Block block = Vars.content.blocks().get(runtimeBlocks.get(Math.min((int)(runtimeBlocks.size * percentile), runtimeBlocks.size - 1)));
            if(!block.synthetic() && !in.block.synthetic()) in.block = block;
        }

        @Override
        public void apply(Tiles tiles, GenerateInput in){
            new BufferedFilter(this).apply(tiles, in);
        }
    }

    public static class RuntimeOreMedianFilter extends OreMedianFilter{
        @Override
        public String simpleName(){ return "oremedian"; }

        private final transient IntSeq runtimeOverlays = new IntSeq();

        @Override
        public void apply(GenerateInput in){
            if(in.overlay == Blocks.spawn) return;
            int cx = (in.x / 2) * 2, cy = (in.y / 2) * 2;
            if(in.overlay != Blocks.air && !(in.tile(cx + 1, cy).overlay() == in.overlay
                && in.tile(cx, cy).overlay() == in.overlay && in.tile(cx + 1, cy + 1).overlay() == in.overlay
                && in.tile(cx, cy + 1).overlay() == in.overlay && !in.tile(cx + 1, cy).block().isStatic()
                && !in.tile(cx, cy).block().isStatic() && !in.tile(cx + 1, cy + 1).block().isStatic()
                && !in.tile(cx, cy + 1).block().isStatic())){
                in.overlay = Blocks.air;
            }
            runtimeOverlays.clear();
            int rad = (int)radius;
            for(int x = -rad; x <= rad; x++){
                for(int y = -rad; y <= rad; y++){
                    if(Mathf.dst2(x, y) > rad * rad) continue;
                    Tile tile = in.tile(in.x + x, in.y + y);
                    if(tile.overlay() != Blocks.spawn) runtimeOverlays.add(tile.overlay().id);
                }
            }
            runtimeOverlays.sort();
            in.overlay = Vars.content.blocks().get(runtimeOverlays.get(Math.min((int)(runtimeOverlays.size * percentile), runtimeOverlays.size - 1)));
        }

        @Override
        public void apply(Tiles tiles, GenerateInput in){
            new BufferedFilter(this).apply(tiles, in);
        }
    }

    public static void apply(Map map, Tiles tiles){
        GenerateFilter.GenerateInput input = new GenerateFilter.GenerateInput();
        for(GenerateFilter filter : map.filters()){
            filter.randomize();
            input.begin(tiles.width, tiles.height, (x, y) -> tiles.getn(x, y));
            if(filter.isBuffered()){
                new BufferedFilter(filter).apply(tiles, input);
            }else{
                filter.apply(tiles, input);
            }
        }
    }
    //Surely this isn't the culprit as I proceed to reload the map 20 times
    public static class BufferedFilter extends GenerateFilter{
        private final GenerateFilter delegate;

        public BufferedFilter(GenerateFilter delegate){
            this.delegate = delegate;
        }

        @Override
        public FilterOption[] options(){
            return delegate.options();
        }

        @Override
        public void randomize(){
            delegate.randomize();
        }

        @Override
        public void apply(Tiles tiles, GenerateInput in){
            long[] buffer = new long[tiles.width * tiles.height];
            for(int i = 0; i < buffer.length; i++){
                in.set(tiles.geti(i));
                delegate.apply(in);
                buffer[i] = (in.floor.id & 0xffffL) | ((in.overlay.id & 0xffffL) << 16) | ((in.block.id & 0xffffL) << 32);
            }

            // content.block(id) interprets IDs through the save's temporary mapper here.
            // These IDs came from live tile objects, so use the runtime registry directly.
            Seq<Block> blocks = Vars.content.blocks();
            for(int i = 0; i < buffer.length; i++){
                Tile tile = tiles.geti(i);
                long packed = buffer[i];
                Block floorBlock = blocks.get((int)(packed & 0xffffL));
                Block overlay = blocks.get((int)((packed >>> 16) & 0xffffL));
                Block block = blocks.get((int)((packed >>> 32) & 0xffffL));
                if(floorBlock instanceof Floor floor){
                    tile.setFloor(floor);
                    tile.setOverlay(!floor.hasSurface() && overlay instanceof OreBlock ore && ore.needsSurface ? Blocks.air : overlay);
                }
                if(!tile.block().synthetic() && !block.synthetic()){
                    tile.setBlock(block);
                }
            }
        }
    }
}
