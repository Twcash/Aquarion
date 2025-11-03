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
//TODO Crash when using max brush size.
//TODO Massive issue when map is resized in the editor... I can't seem to replicate it anymore so maybe it's a specific map issue?


public class GreedyFloor extends Floor {
    public int maxSize = 2;
    public Effect effect = Fx.none;
    public Color effectColor = Color.white;
    public float effectSpacing = 60f;
    private TextureRegion[][][][] sizeRegions2D;

    private boolean[][] claimed;
    private final ObjectMap<Integer, Anchor> anchorMap = new ObjectMap<>();
    public boolean built = false;
    public ObjectMap<Integer, TextureRegion[]> sizeRegions = new ObjectMap<>();

    private static class Anchor {
        public final int x, y, size, variant;
        public Anchor(int x, int y, int size, int variant) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.variant = variant;
        }
    }

    public GreedyFloor(String name, int variants, int maxSize) {
        super(name, variants);
        this.maxSize = maxSize;
    }

    public GreedyFloor(String name, int variants) {
        super(name, variants);
    }

    @Override
    public void init() {
        super.init();
        Events.on(EventType.WorldLoadEvent.class, e -> {
            built = false;
            claimed = null;
            anchorMap.clear();
        });
    }

    @Override
    public void load() {
        super.load();
        sizeRegions2D = new TextureRegion[maxSize + 1][variants][][];
        for (int size = 1; size <= maxSize; size++) {
            for (int v = 0; v < variants; v++) {
                TextureRegion base = Core.atlas.find(name + "-" + size + "-" + v);
                if (!base.found()) base = Core.atlas.find(name + "-" + size);
                if (!base.found()) base = region;

                int chunkW = base.width / size;
                int chunkH = base.height / size;
                sizeRegions2D[size][v] = base.split(chunkW, chunkH);
            }
        }

        built = false;
    }

    private int pickVariant(int tx, int ty) {
        return Mathf.randomSeed(Point2.pack(tx, ty), 0, Math.max(0, variants - 1));
    }

    private int tileKey(int x, int y) {
        return (x << 16) | (y & 0xFFFF);
    }

    @Override
    public void floorChanged(Tile tile) {
        built = false;
        claimed = null;
        anchorMap.clear();
    }

    private void buildAnchorMap() {
        if (Vars.world == null || Vars.world.tiles == null) return;

        int w = Vars.world.width(), h = Vars.world.height();
        claimed = new boolean[w][h];
        anchorMap.clear();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                Tile t = Vars.world.tile(x, y);
                if (t == null || claimed[x][y]) continue;
                if (t.floor() != this) continue;

                int size = Math.min(maxSize, findSize(x, y));
                int variant = pickVariant(x, y);
                markClaimed(x, y, size);

                anchorMap.put(tileKey(x, y), new Anchor(x, y, size, variant));
            }
        }

        built = true;
    }

    private int findSize(int x, int y) {
        int size = 1;
        outer:
        for (int s = 2; s <= maxSize; s++) {
            for (int dx = 0; dx < s; dx++) {
                for (int dy = 0; dy < s; dy++) {
                    Tile t = Vars.world.tile(x + dx, y + dy);
                    if (t == null || t.floor() != this || claimed[x + dx][y + dy]) {
                        break outer;
                    }
                }
            }
            size = s;
        }
        return size;
    }

    private void markClaimed(int x, int y, int size) {
        for (int dx = 0; dx < size; dx++) {
            for (int dy = 0; dy < size; dy++) {
                int xx = x + dx, yy = y + dy;
                if (xx < 0 || yy < 0 || xx >= claimed.length || yy >= claimed[0].length) continue;
                claimed[xx][yy] = true;
            }
        }
    }

    private Anchor findAnchorFor(Tile tile) {
        if (anchorMap.isEmpty() || tile == null) return null;

        int tx = tile.x, ty = tile.y;
        for (int sx = Math.max(0, tx - maxSize + 1); sx <= tx; sx++) {
            for (int sy = Math.max(0, ty - maxSize + 1); sy <= ty; sy++) {
                int key = (sx << 16) | (sy & 0xFFFF);
                Anchor a = anchorMap.get(key);
                if (a != null && tx >= a.x && ty >= a.y &&
                        tx < a.x + a.size && ty < a.y + a.size) {
                    return a;
                }
            }
        }
        return null;
    }

    @Override
    public void drawBase(Tile tile) {
        if (!built) buildAnchorMap();
        Anchor a = findAnchorFor(tile);
        if (a == null) return;
        TextureRegion[][] chunks = sizeRegions2D[a.size][a.variant];
        if (chunks == null) return;
        int relX = tile.x - a.x;
        int relY = tile.y - a.y;
        //Anchor starts at bottom left. However, texture coords starts at top left. Flip Y axis...
        Draw.rect(chunks[relX][a.size - 1 - relY], tile.worldx(), tile.worldy());
        drawOverlay(tile);
    }
}