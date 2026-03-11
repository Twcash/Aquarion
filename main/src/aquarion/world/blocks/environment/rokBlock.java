package aquarion.world.blocks.environment;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.Tile;

public class rokBlock extends Block {

    public float layer = Layer.power + 10;
    public float rotationRand = 10;
    public rokBlock(String name) {
        super(name);
        breakable = true;
        solid = true;
        breakEffect = Fx.breakProp;
        update = true;
        breakSound = Sounds.rockBreak;
        destroySound = Sounds.rockBreak;
    }

    @Override
    public void drawBase(Tile tile) {
        Draw.z(layer);
        float rot = Mathf.randomSeed(tile.pos(), -rotationRand, rotationRand);
        Draw.rect(variants > 0 ? variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))] : region,tile.worldx(), tile.worldy(), rot);
    }

    @Override
    public TextureRegion[] icons() {
        return variants == 0 ? super.icons() : new TextureRegion[]{Core.atlas.find(name + "1")};
    }
}