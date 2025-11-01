package aquarion.world.blocks.environment;

import aquarion.world.graphics.Renderer;
import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.IntSet;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.core.GameState;
import mindustry.entities.Effect;
import mindustry.game.EventType;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

//TODO This can work with non-square blocks. I could also add the ability for shape variants
//TODO Massive issue when map is resized in the editor... I can't seem to replicate it anymore so maybe it's a specific map issue?
public class GreedyFloor extends Floor {
    public int maxSize = 2;
    public Effect effect = Fx.none;
    public Color effectColor = Color.white;
    public float effectSpacing = 60;

    private boolean[][] claimed;
    private final ObjectMap<Integer, Anchor> anchorMap = new ObjectMap<>();
    public boolean built = false;
    public ObjectMap<Integer, TextureRegion[]> sizeRegions = new ObjectMap<>();

    private static class Anchor {
        public int tx, ty, size, variant;

        public Anchor(int tx, int ty, int size, int variant) {
            this.tx = tx;
            this.ty = ty;
            this.size = size;
            this.variant = variant;
        }
    }

    public GreedyFloor(String name, int variants, int maxSize){
        super(name, variants);
        this.maxSize = maxSize;
    }

    public GreedyFloor(String name, int variants){
        super(name);
        this.variants = Math.max(1, variants);
    }

    @Override
    public void init(){
        super.init();

        Events.on(EventType.WorldLoadEvent.class, e -> built = false);

        Events.on(EventType.TileFloorChangeEvent.class, e -> {
            if(e.tile != null && e.tile.floor() == this){
                floorChanged(e.tile);
            }
        });
    }

    @Override
    public void load(){
        super.load();
        sizeRegions.clear();

        for(int size = 1; size <= maxSize; size++){
            TextureRegion[] regs = new TextureRegion[variants];
            for(int v = 0; v < variants; v++){
                TextureRegion found = Core.atlas.find(name + "-" + size + "-" + v);
                if(!found.found()) found = Core.atlas.find(name + "-" + size);
                if(found.found() && size > 1){
                    regs[v] = found.split(32, 32)[0][0];
                } else {
                    regs[v] = found.found() ? found : null;
                }
            }
            sizeRegions.put(size, regs);
        }

        Events.on(EventType.WorldLoadEvent.class, e -> built = false);
        built = false;
    }

    private int pickRegion(int tx, int ty){
        return Mathf.randomSeed(Point2.pack(tx, ty), 0, Math.max(0, variants - 1));
    }

    private boolean regionMatches(int tx, int ty, int size, boolean[][] claimed){
        int w = Vars.world.width();
        int h = Vars.world.height();
        if(tx < 0 || ty < 0 || tx + size > w || ty + size > h) return false;

        for(int yy = ty; yy < ty + size; yy++){
            for(int xx = tx; xx < tx + size; xx++){
                if(xx < 0 || yy < 0 || xx >= w || yy >= h) return false;
                Tile t = Vars.world.tile(xx, yy);
                if(t == null || t.floor() != this || claimed[xx][yy]) return false;
            }
        }
        return true;
    }

    private int posKey(int x, int y){
        int w = Vars.world.width();
        int h = Vars.world.height();
        if(x < 0 || y < 0 || x >= w || y >= h) return -1;
        return x + y * w;
    }

    private void buildAnchorMap(){
        int w = Vars.world.width();
        int h = Vars.world.height();

        claimed = new boolean[w][h];
        anchorMap.clear();

        for(int y = 0; y < h; y++){
            for(int x = 0; x < w; x++){
                if(claimed[x][y]) continue;

                Tile tile = Vars.world.tile(x, y);
                if(tile == null || tile.floor() != this) continue;

                for(int size = maxSize; size >= 1; size--){
                    if(regionMatches(x, y, size, claimed)){
                        int variant = pickRegion(x, y);
                        Anchor a = new Anchor(x, y, size, variant);

                        for(int yy = y; yy < y + size; yy++){
                            for(int xx = x; xx < x + size; xx++){
                                int key = posKey(xx, yy);
                                if(key != -1) anchorMap.put(key, a);
                                claimed[xx][yy] = true;
                            }
                        }
                        break;
                    }
                }
            }
        }

        built = true;
    }

    private void ensureAnchorMap(){
        if(!built) buildAnchorMap();
    }

    @Override
    public void floorChanged(Tile tile){
        if(tile == null || tile.floor() != this) return;

        int w = Vars.world.width();
        int h = Vars.world.height();

        if(claimed == null){
            claimed = new boolean[w][h];
        }

        int startX = Math.max(0, tile.x - maxSize + 1);
        int startY = Math.max(0, tile.y - maxSize + 1);
        int endX = Math.min(w, tile.x + maxSize);
        int endY = Math.min(h, tile.y + maxSize);

        // remove previous anchors
        for(int yy = startY; yy < endY; yy++){
            for(int xx = startX; xx < endX; xx++){
                int key = posKey(xx, yy);
                Anchor a = key == -1 ? null : anchorMap.get(key);
                if(a != null){
                    for(int y2 = a.ty; y2 < a.ty + a.size; y2++){
                        for(int x2 = a.tx; x2 < a.tx + a.size; x2++){
                            int removeKey = posKey(x2, y2);
                            if(removeKey != -1) anchorMap.remove(removeKey);
                            if(x2 >= 0 && y2 >= 0 && x2 < w && y2 < h) claimed[x2][y2] = false;
                        }
                    }
                }
            }
        }

        // rebuild anchors
        for(int yy = startY; yy < endY; yy++){
            for(int xx = startX; xx < endX; xx++){
                if(xx < 0 || yy < 0 || xx >= w || yy >= h) continue;
                if(claimed[xx][yy]) continue;

                Tile t = Vars.world.tile(xx, yy);
                if(t == null || t.floor() != this) continue;

                for(int size = maxSize; size >= 1; size--){
                    if(regionMatches(xx, yy, size, claimed)){
                        int variant = pickRegion(xx, yy);
                        Anchor a = new Anchor(xx, yy, size, variant);

                        TextureRegion baseReg = Core.atlas.find(name + "-" + size + "-" + variant);
                        int chunkWidth = 1;
                        int chunkHeight = 1;
                        if(baseReg != null && baseReg.found()){
                            TextureRegion[][] chunks = baseReg.split(32,32);
                            chunkWidth = chunks.length;
                            chunkHeight = chunks[0].length;
                        }

                        for(int y2 = 0; y2 < size; y2++){
                            for(int x2 = 0; x2 < size; x2++){
                                int wx = xx + x2;
                                int wy = yy + y2;
                                int key2 = posKey(wx, wy);
                                if(key2 != -1) anchorMap.put(key2, a);
                                if(wx >= 0 && wy >= 0 && wx < w && wy < h) claimed[wx][wy] = true;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void drawBase(Tile tile){
        ensureAnchorMap();
        drawOverlay(tile);
        int key = posKey(tile.x, tile.y);
        Anchor a = key == -1 ? null : anchorMap.get(key);
        if(a == null) return;

        TextureRegion baseReg = Core.atlas.find(name + "-" + a.size + "-" + a.variant);
        if(baseReg == null || !baseReg.found()) return;

        if(a.size == 1){
            Draw.rect(baseReg, tile.worldx(), tile.worldy());
            return;
        }

        TextureRegion[][] chunks = baseReg.split(32, 32);
        int chunkWidth = chunks.length;
        int chunkHeight = chunks[0].length;

        for(int yy = 0; yy < chunkHeight; yy++){
            for(int xx = 0; xx < chunkWidth; xx++){
                int wx = a.tx + xx;
                int wy = a.ty + (chunkHeight - 1 - yy);

                if(wx < 0 || wy < 0 || wx >= Vars.world.width() || wy >= Vars.world.height()) continue;

                Tile t = Vars.world.tile(wx, wy);
                if(t == null) continue;

                TextureRegion chunk = chunks[xx][yy];
                Draw.rect(chunk, t.worldx(), t.worldy());
                drawOverlay(tile);
            }
        }
    }

    @Override
    public void renderUpdate(UpdateRenderState state){
        if(state.tile.nearby(-1, -1) == null || state.tile.block() != Blocks.air) return;

        int key = posKey(state.tile.x, state.tile.y);
        Anchor a = key == -1 ? null : anchorMap.get(key);
        if(a == null) return;
        if(!claimed[a.tx][a.ty]) return;

        state.data += Time.delta;
        if(state.data >= effectSpacing){
            float wx = (a.tx + a.size / 2f) * tilesize - tilesize;
            float wy = (a.ty + a.size / 2f) * tilesize - tilesize;
            effect.at(wx, wy, effectColor);
            state.data = 0f;
        }
    }
}
