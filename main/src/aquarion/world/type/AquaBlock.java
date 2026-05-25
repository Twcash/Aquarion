package aquarion.world.type;

import aquarion.ui.AquaBarHelpers;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.world.Block;
import mindustry.world.Tile;

import static aquarion.world.graphics.Renderer.Layer.shadow;

public class AquaBlock extends Block implements AquaBarHelpers.CustomBarHolder {
    public TextureRegion fullRegion;
    public AquaBlock(String name) {
        super(name);
    }

    @Override
    public void load(){
        super.load();
        fullRegion = Core.atlas.find(name + "-full");
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
