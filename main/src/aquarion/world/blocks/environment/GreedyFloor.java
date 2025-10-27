package aquarion.world.blocks.environment;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import static mindustry.Vars.world;


public class GreedyFloor extends Floor {
    //Honestly scared to remove one. I already forgot what they do and it's 2Am.
    public int maxPower = 2;
    public int maxSize = 2;

    public ObjectMap<Integer, TextureRegion[]> sizeRegions = new ObjectMap<>();
    public GreedyFloor(String name, int variants, int maxPower){
        super(name, variants);
        this.maxPower = maxPower;
        this.maxSize = 1 << maxPower;
    }
    private final ObjectMap<Integer, Anchor> anchorMap = new ObjectMap<>();

    private static class Anchor {
        public int tx, ty, size, variant;
        public Anchor(int tx, int ty, int size, int variant){
            this.tx = tx; this.ty = ty;
            this.size = size; this.variant = variant;
        }
    }

    public GreedyFloor(String name, int variants){
        super(name);
        this.variants = Math.max(1, variants);
    }

    @Override
    public void load(){
        super.load();

        this.maxSize = 1 << this.maxPower;

        sizeRegions.clear();
        for(int size = 1; size <= maxSize; size++){
            TextureRegion[] regs = new TextureRegion[variants];
            for(int v = 0; v < variants; v++){
                // Try variant first
                TextureRegion found = Core.atlas.find(name + "-" + size + "-" + v);
                // Fallback to no-variant texture
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
        boolean[][] claimed = new boolean[w][h];

        // Loop over all tiles in the world
        for(int y = 0; y < h; y++){
            for(int x = 0; x < w; x++){
                if(claimed[x][y]) continue;

                Tile tile = Vars.world.tile(x, y);
                if(tile == null || tile.floor() != this) continue;

                // <-- REPLACE THE OLD POWER LOOP WITH THIS -->
                int maxSize = this.maxSize; // largest possible square
                for(int size = maxSize; size >= 1; size--){
                    if(regionMatches(x, y, size, claimed)){
                        int variant = pickRegion(x, y);
                        Anchor a = new Anchor(x, y, size, variant);

                        // Mark all tiles in this region as claimed
                        for(int yy = y; yy < y + size; yy++){
                            for(int xx = x; xx < x + size; xx++){
                                anchorMap.put(posKey(xx, yy), a);
                                claimed[xx][yy] = true;
                            }
                        }
                        break; // done with this anchor
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
        Events.on(EventType.WorldLoadEvent.class, t-> built = false);
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
    }
    }