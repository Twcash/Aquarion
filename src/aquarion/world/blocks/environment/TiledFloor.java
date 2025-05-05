package aquarion.world.blocks.environment;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Log;
import mindustry.content.Blocks;
import mindustry.graphics.CacheLayer;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import java.util.Arrays;

import static mindustry.Vars.headless;
import static mindustry.Vars.tilesize;

public class TiledFloor extends Floor {
    public TiledFloor(String name, int variants) {
        super(name, variants);
    }
    public TiledFloor(String name) {
        super(name);
    }

    public int tilingVariants = 0;

    protected TextureRegion[][][] tilingRegions;
    protected int tilingSize;
    @Override
    public void load(){
        super.load();

        int tsize = (int)(tilesize / Draw.scl);

        if(tilingVariants > 0 && !headless){
            tilingRegions = new TextureRegion[tilingVariants][][];
            for(int i = 0; i < tilingVariants; i++){
                TextureRegion tile = Core.atlas.find(name + "-tile" + (i + 1));
                tilingRegions[i] = tile.split(tsize, tsize);
                tilingSize = tilingRegions[i].length;
            }

            for(int i = 0; i < tilingVariants; i++){
                if(tilingRegions[i].length != tilingSize || tilingRegions[i][0].length != tilingSize){
                    Log.warn("Block: @: In order to prevent crashes, tiling regions must all be valid regions with the same size. Tiling has been disabled. Sprite '@' has a width or height inconsistent with other tiles.", name, name + "-tile" + (i + 1));
                    tilingVariants = 0;
                }
            }
        }

        if(variants > 0){
            variantRegions = new TextureRegion[variants];
            for(int i = 0; i < variants; i++){
                variantRegions[i] = Core.atlas.find(name + (i + 1));
            }
        }else{
            variantRegions = new TextureRegion[1];
            variantRegions[0] = Core.atlas.find(name);
        }

        if(Core.atlas.has(name + "-edge")){
            edges = Core.atlas.find(name + "-edge").split(tsize, tsize);
        }
        region = variantRegions[0];
        edgeRegion = Core.atlas.find("edge");
    }
    @Override
    public void drawBase(Tile tile) {
        if (tilingVariants > 0) {
            int index = Mathf.randomSeed(Point2.pack(tile.x / tilingSize, tile.y / tilingSize), 0, tilingVariants - 1);
            TextureRegion[][] regions = tilingRegions[index];
            Draw.rect(regions[tile.x % tilingSize][tilingSize - 1 - tile.y % tilingSize], tile.worldx(), tile.worldy());
        } else {
            Draw.rect(variantRegions[variant(tile.x, tile.y)], tile.worldx(), tile.worldy());
        }
        Draw.alpha(1f);
        drawEdges(tile);
    }
}

