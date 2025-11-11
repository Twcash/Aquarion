package aquarion.planets;

import mindustry.content.Blocks;
import mindustry.world.Block;

public class TileGen {
    public Block floor;
    public Block block;
    public Block overlay;

    public int width, height;
    private TileGen[][] tiles;

    public TileGen() {
        this(0, 0);
    }

    public TileGen(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new TileGen[width][height];
        reset();
    }

    public void reset() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new TileGen();
                tiles[x][y].floor = Blocks.stone;
                tiles[x][y].block = Blocks.air;
                tiles[x][y].overlay = Blocks.air;
            }
        }
    }

    public TileGen get(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) return null;
        return tiles[x][y];
    }

    public void set(int x, int y, Block floor, Block block, Block overlay) {
        if (x < 0 || y < 0 || x >= width || y >= height) return;
        TileGen t = tiles[x][y];
        t.floor = floor;
        t.block = block;
        t.overlay = overlay;
    }
}
