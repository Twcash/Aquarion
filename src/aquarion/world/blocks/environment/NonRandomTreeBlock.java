package aquarion.world.blocks.environment;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.*;
import arc.util.*;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.graphics.*;


public class NonRandomTreeBlock extends Block {

    public NonRandomTreeBlock(String name) {
        super(name);
        solid = true;
        clipSize = 90;
        customShadow = true;
    }
    public TextureRegion shadow;
    public float shadowOffset = -1;

    @Override
    public void drawBase(Tile tile) {
        float x = tile.worldx(), y = tile.worldy(),
                w = region.width * region.scl(), h = region.height * region.scl(),
                scl = 30f, mag = 0.2f;

        // Get the appropriate region based on whether there are variants or not.
        TextureRegion reg = variants == 0 ? region : variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))];
        TextureRegion shad = variants == 0 ? customShadowRegion : variantShadowRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantShadowRegions.length - 1))];
        if(shad.found()){
            Draw.z(Layer.power - 1);
            Draw.rect(shad, tile.worldx() + shadowOffset, tile.worldy() + shadowOffset, 0);
        }
        // Set the drawing layer
        Draw.z(Layer.power + 1);

        // Apply distortion but without rotating the texture.
        Draw.rectv(reg, x, y, w, h, 0, vec -> vec.add(
                Mathf.sin(vec.y * 3 + Time.time, scl, mag) + Mathf.sin(vec.x * 3 - Time.time,  Mathf.randomSeed(tile.pos(), 50 , 75), 0.9f),
                        Mathf.cos(vec.x * 3 + Time.time + 8, scl + 6f, mag * 1.1f) + Mathf.sin(vec.y * 3 - Time.time, Mathf.randomSeed(tile.pos(), 35 , 50), 0.5f)
        ));
    }

    @Override
    public void drawShadow(Tile tile) {
        // No shadow drawing in this version
    }
    @Override
    public void load() {
        super.load();

        for (int i = 0; i < variants; i++) {
            shadow = Core.atlas.find(name + "-shadow" + (1+ i));
        }
    }
}