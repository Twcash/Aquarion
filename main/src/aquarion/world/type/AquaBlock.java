package aquarion.world.type;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.world.Block;
import mindustry.world.Tile;

import static aquarion.world.graphics.Renderer.Layer.shadow;

public class AquaBlock extends Block {
    public AquaBlock(String name) {
        super(name);
    }
    @Override
    public void drawShadow(Tile tile){
        Draw.z(shadow);
        Draw.rect(
                variants == 0 ? customShadowRegion :
                        variantShadowRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantShadowRegions.length - 1))],
                tile.drawx(), tile.drawy());
    }
}
