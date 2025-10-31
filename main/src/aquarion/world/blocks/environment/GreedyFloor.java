package aquarion.world.blocks.environment;

import aquarion.world.graphics.Renderer;
import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.ObjectMap;
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
public class GreedyFloor extends Floor {
    public int maxSize = 2;
    public Effect effect = Fx.none;
    public Color effectColor = Color.white;
    public boolean[][] claimed;
    public float effectSpacing = 60;
    public ObjectMap<Integer, TextureRegion[]> sizeRegions = new ObjectMap<>();

    public GreedyFloor(String name, int variants, int maxSize){
        super(name, variants);
        this.maxSize = maxSize;
    }
    private final ObjectMap<Integer, Anchor> anchorMap = new ObjectMap<>();

    private static class Anchor {
        public int tx, ty, size, variant;
        public Anchor(int tx, int ty, int size, int variant){
            this.tx = tx; this.ty = ty;
            this.size = size; this.variant = variant;
        }
    }
    @Override
    public boolean updateRender(Tile tile){
        if(Vars.state.isEditor() && !built){
            buildAnchorMap();
        }
        return true;
    }
    @Override
    public void init(){
        super.init();
        clipSize = maxSize*8*2;
        Events.on(EventType.WorldLoadEvent.class, e -> {
            built = false;
        });
        Events.on(EventType.TileFloorChangeEvent.class, e -> {
            if(e.tile != null && e.tile.floor() == this){
                Time.run(1f, () -> {built = false;anchorMap.clear();});//BAD
            }
        });
    }
    @Override
    public void renderUpdate(UpdateRenderState state){
        if( (state.data += Time.delta) >= 120){
            built = false;
        }
        if(state.tile.nearby(-1, -1) != null && state.tile.block() == Blocks.air && (state.data += Time.delta) >= effectSpacing){
            Anchor a = anchorMap.get(posKey(state.tile.x, state.tile.y));
            if(a==null) return;
            if (claimed[a.tx][a.ty]) {
                float wx = (a.tx + a.size / 2f) * tilesize-tilesize;
                float wy = (a.ty + a.size / 2f) * tilesize-tilesize;
                effect.at(wx, wy, effectColor);
            }
            state.data = 0f;
        }
    }
    public GreedyFloor(String name, int variants){
        super(name);
        this.variants = Math.max(1, variants);
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
                regs[v] = found.found() ? found : null;
            }
            sizeRegions.put(size, regs);
        }
        built = false;
    }

    private int pickRegion(int tx, int ty){
        return  Mathf.randomSeed(Point2.pack(tx, ty), 0, Math.max(0, variants - 1));
    }
    private void buildAnchorMap(){
        anchorMap.clear();
        int w = Vars.world.width();
        int h = Vars.world.height();

        boolean[][] claimeds = new boolean[w][h];
        claimed = claimeds;
        for(int y = 0; y < h; y++){
            for(int x = 0; x < w; x++){
                if(claimeds[x][y]) continue;

                Tile tile = Vars.world.tile(x, y);
                if(tile == null || tile.floor() != this) continue;
                int maxSize = this.maxSize;
                for(int size = maxSize; size >= 1; size--){
                    if(regionMatches(x, y, size, claimeds)){
                        int variant = pickRegion(x, y);
                        Anchor a = new Anchor(x, y, size, variant);

                        for(int yy = y; yy < y + size; yy++){
                            for(int xx = x; xx < x + size; xx++){
                                anchorMap.put(posKey(xx, yy), a);
                                claimeds[xx][yy] = true;
                            }
                        }
                        break;
                    }
                }
            }
        }

        built = true;
    }
    private boolean regionMatches(int tx, int ty, int size, boolean[][] claimed){
        int w = Vars.world.width();
        int h = Vars.world.height();
        if(tx + size > w || ty + size > h) return false;

        for(int yy = ty; yy < ty + size; yy++){
            for(int xx = tx; xx < tx + size; xx++){
                Tile t = Vars.world.tile(xx, yy);
                if(t == null || t.floor() != this || claimed[xx][yy]) return false;
            }
        }
        return true;
    }
    private int posKey(int x, int y){
        return x + y * Vars.world.width();
    }
    public boolean built = false;
    private void ensureAnchorMap(){
        if(!built)buildAnchorMap();
    }
    @Override
    public void drawBase(Tile tile){
        drawOverlay(tile);
            buildAnchorMap();
        ensureAnchorMap();
        Anchor a = anchorMap.get(posKey(tile.x, tile.y));
        if(a == null) return;
        if(tile.x != a.tx || tile.y != a.ty) return;

        TextureRegion reg = sizeRegions.get(a.size)[Mathf.randomSeed(Point2.pack(tile.x, tile.y), 0, Math.max(0, variants - 1))];
        if(reg == null) return;
        float drawSize = a.size * 8f;
        float cx = tile.worldx() + (drawSize - 8f)/2f;
        float cy = tile.worldy() + (drawSize - 8f)/2f;
        Draw.rect(reg, cx, cy, drawSize, drawSize);
        drawOverlay(tile);
        Draw.reset();

    }
    }