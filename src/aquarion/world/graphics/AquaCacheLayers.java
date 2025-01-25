package aquarion.world.graphics;
import aquarion.world.blocks.production.AquaGenericCrafter;
import mindustry.gen.Building;
import mindustry.graphics.CacheLayer;
import mindustry.world.Block;
import mindustry.world.Tile;

public class AquaCacheLayers {
    public static CacheLayer brine;

    public static void load() {
        CacheLayer.add(
                brine = new CacheLayer.ShaderLayer(AquaShaders.brine)
        );
    }
}