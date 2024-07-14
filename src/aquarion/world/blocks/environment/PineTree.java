package aquarion.world.blocks.environment;
import arc.Core;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.Floor;

public class PineTree extends Block {
    public float shadowOffset = -4f;
    public float layer = Layer.power + 10;
    public Effect effect = Fx.ventSteam;
    public float effectSpacing = 15f;
    public float shadowLayer = Layer.power - 1;
    public float updateEffectChance = 0.5f;
    public float rotationRand = 30;
    public float shadowAlpha = 0.5f; // Default value for shadowAlpha
    public TextureRegion baseShadow, baseRegion, topShadow, topRegion;

    public class PineTreeBuild extends Building {
        @Override
        public void updateTile() {

            if (Mathf.chanceDelta(updateEffectChance)) {
                if ((Time.delta) >= effectSpacing) {
                    effect.at(tile.x * 1 + Mathf.range(size * 4f), tile.y * 1 + Mathf.range(size * 4f));
                }
            }
        }
    }

    public PineTree(String name) {
        super(name);
        solid = true;
        clipSize = 90;
        update = true;
        customShadow = true;
    }

    @Override
    public void load() {
        super.load();
        baseShadow = Core.atlas.find(name + "-shadow");
        baseRegion = Core.atlas.find(name);
    }

    @Override
    public void init(){
        super.init();
        hasShadow = true;
    }

    @Override
    public void drawBase(Tile tile){

        float
                x = tile.worldx(), y = tile.worldy(),
                rot = Mathf.randomSeed(tile.pos(), 0, 4) * 90 + Mathf.sin(Time.time + x, 50f, 0.5f) + Mathf.sin(Time.time - y, 65f, 0.9f) + Mathf.sin(Time.time + y - x, 85f, 0.9f),
                w = region.width * region.scl(), h = region.height * region.scl(),
                scl = 30f, mag = 0.2f;
                 float rot2 = Mathf.randomSeedRange(tile.pos() + 1, rotationRand);
        TextureRegion shad = variants == 0 ? customShadowRegion : variantShadowRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantShadowRegions.length - 1))];

        if(shad.found()){
            Draw.alpha(shadowAlpha);
            Draw.z(Layer.power - 1);
            Draw.rect(shad, tile.worldx() + shadowOffset, tile.worldy() + shadowOffset, rot + rot2);
        }

        TextureRegion reg = variants == 0 ? region : variantRegions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, variantRegions.length - 1))];

        Draw.z(Layer.power + 10);
        Draw.alpha(1);
        Draw.rectv(reg, x, y, w, h, rot + + rot2, vec -> vec.add(
                Mathf.sin(vec.y*3 + Time.time, scl, mag) + Mathf.sin(vec.x*3 - Time.time, 70, 0.8f),
                Mathf.cos(vec.x*3 + Time.time + 8, scl + 6f, mag * 1.1f) + Mathf.sin(vec.y*3 - Time.time, 50, 0.2f)
        ));
    }

    @Override
    public void drawShadow(Tile tile){}

}