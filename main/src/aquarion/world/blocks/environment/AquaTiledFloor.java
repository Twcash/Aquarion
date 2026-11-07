package aquarion.world.blocks.environment;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Log;
import mindustry.graphics.MultiPacker;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.headless;
import static mindustry.Vars.tilesize;

public class AquaTiledFloor extends Floor {
    /** [variant][subtile x][subtile image y][edge x][edge image y]. */
    protected TextureRegion[][][][][] tilingEdges;

    public AquaTiledFloor(String name, int variants) {
        super(name, variants);
    }

    public AquaTiledFloor(String name) {
        super(name);
    }

    @Override
    public void load(){
        int tsize = subtilePixels();
        tilingEdges = null;
        if(!headless && tilingVariants > 0 && tileSize(tsize) == 0){
            Log.warn("Block: @: Tiling disabled; tile sprites must be square, tile-sized multiples with matching dimensions.", name);
            tilingVariants = 0;
        }
        super.load();

        // Floor.load uses Draw.scl, which is not initialized during mod icon packing.
        // Reload our grids using the atlas scale so both loading passes agree.
        if(!headless && tilingVariants > 0){
            tilingRegions = new TextureRegion[tilingVariants][][];
            for(int v = 0; v < tilingVariants; v++){
                tilingRegions[v] = Core.atlas.find(name + "-tile" + (v + 1)).split(tsize, tsize);
            }
            tilingSize = tilingRegions[0].length;
        }
        if(Core.atlas.has(name + "-edge")){
            TextureRegion edge = Core.atlas.find(name + "-edge");
            edges = edge.width == tsize * 3 && edge.height == tsize * 3 ? edge.split(tsize, tsize) : null;
        }

        if(headless || tilingVariants <= 0 || blendGroup != this) return;
        tilingEdges = new TextureRegion[tilingVariants][tilingSize][tilingSize][][];
        for(int v = 0; v < tilingVariants; v++){
            for(int x = 0; x < tilingSize; x++){
                for(int y = 0; y < tilingSize; y++){
                    String edgeName = subtileEdgeName(v, x, y);
                    if(Core.atlas.has(edgeName)){
                        TextureRegion edge = Core.atlas.find(edgeName);
                        if(edge.width == tsize * 3 && edge.height == tsize * 3){
                            tilingEdges[v][x][y] = edge.split(tsize, tsize);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void createIcons(MultiPacker packer){
        super.createIcons(packer);
        if(tilingVariants <= 0 || blendGroup != this) return;

        int tsize = subtilePixels();
        int size = tileSize(tsize);
        if(size == 0) return;
        var stencil = packer.get(Core.atlas.find(name + "-edge-stencil", "edge-stencil"));
        for(int v = 0; v < tilingVariants; v++){
            var image = packer.get(Core.atlas.find(name + "-tile" + (v + 1)));
            for(int tx = 0; tx < size; tx++){
                for(int ty = 0; ty < size; ty++){
                    String edgeName = subtileEdgeName(v, tx, ty);
                    if(Core.atlas.has(edgeName) || packer.has(edgeName)) continue;
                    Pixmap result = new Pixmap(stencil.width, stencil.height);
                    try{
                        for(int x = 0; x < stencil.width; x++){
                            for(int y = 0; y < stencil.height; y++){
                                result.set(x, y, Color.muli(stencil.get(x, y),
                                    image.get(tx * tsize + x % tsize, ty * tsize + y % tsize)));
                            }
                        }
                        packer.add(MultiPacker.PageType.environment, edgeName, result);
                    }finally{
                        result.dispose();
                    }
                }
            }
        }
    }

    /** Coordinates in sprite names use the source image's top-left origin. */
    protected String subtileEdgeName(int variant, int x, int y){
        return name + "-tile" + (variant + 1) + "-edge-" + x + "-" + y;
    }

    private int subtilePixels(){
        // Control.loadAsync initializes Draw.scl only after Mods generates icons.
        // This is the same marker used by Control, available in both atlas passes.
        return Core.atlas.has("scale_marker") ? tilesize * Core.atlas.find("scale_marker").width
            : (int)(tilesize / Draw.scl);
    }

    /** Validate before splitting so missing or undersized sprites cannot create empty grids. */
    private int tileSize(int tsize){
        int size = 0;
        for(int v = 0; v < tilingVariants; v++){
            TextureRegion tile = Core.atlas.find(name + "-tile" + (v + 1));
            if(!tile.found() || tile.width <= 0 || tile.width != tile.height || tile.width % tsize != 0) return 0;
            int current = tile.width / tsize;
            if(size != 0 && current != size) return 0;
            size = current;
        }
        return size;
    }

    protected int tilingVariant(int x, int y){
        return Mathf.randomSeed(Point2.pack(x / tilingSize, y / tilingSize), 0, tilingVariants - 1);
    }

    @Override
    protected TextureRegion[][] edges(int x, int y){
        if(tilingEdges != null){
            TextureRegion[][] result = tilingEdges[tilingVariant(x, y)][x % tilingSize][tilingSize - 1 - y % tilingSize];
            if(result != null) return result;
        }
        return super.edges(x, y);
    }

    @Override
    public void drawBase(Tile tile) {
        if (tilingVariants > 0) {
            TextureRegion[][] regions = tilingRegions[tilingVariant(tile.x, tile.y)];
            Draw.rect(regions[tile.x % tilingSize][tilingSize - 1 - tile.y % tilingSize], tile.worldx(), tile.worldy());
        } else {
            Draw.rect(variantRegions[variant(tile.x, tile.y)], tile.worldx(), tile.worldy());
        }
        drawOverlay(tile);
        Draw.alpha(1f);
        drawEdges(tile);
    }
}
