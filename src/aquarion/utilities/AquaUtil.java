package aquarion.utilities;

import arc.graphics.g2d.TextureRegion;

import static arc.Core.atlas;

public class AquaUtil {
    /**bittiler stuff
     * Thanks STABU for telling me abt this. Couldn't do this without them.
     * <p>
     * Original code from Serilia[<a href="https://github.com/Froomeeth/Serilia/blob/main/src/serilia/util/SeUtil.java#L64C1-L64C1">...</a>]
     */
    public static TextureRegion[][] splitLayers(String name, int size, int layerCount){
        TextureRegion[][] layers = new TextureRegion[layerCount][];

        for(int i = 0; i < layerCount; i++){
            layers[i] = split(name, size, i);
        }
        return layers;
    }

    public static TextureRegion[] split(String name, int size, int layer){
        TextureRegion tex = atlas.find(name);
        int margin = 0;
        int countX = tex.width / size;
        TextureRegion[] tiles = new TextureRegion[countX];

        for(int step = 0; step < countX; step++){
            tiles[step] = new TextureRegion(tex, step * (margin + size), layer * (margin + size), size, size);
        }
        return tiles;
    }

}
