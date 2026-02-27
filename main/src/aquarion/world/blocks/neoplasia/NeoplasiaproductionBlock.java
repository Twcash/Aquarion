package aquarion.world.blocks.neoplasia;

import aquarion.annotations.Annotations;
import aquarion.content.blocks.CoreBlocks;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.Renderer;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.util.Time;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class NeoplasiaproductionBlock extends GenericNeoplasiaBlock{
    public TextureRegion lobeBotRegion;
    public TextureRegion lobeRegion;
    @Override
    public void load(){
        super.load();
        lobeBotRegion = Core.atlas.find(name + "-lobe");
        lobeRegion =  Core.atlas.find(name + "-lobe-bot");
    }
    public NeoplasiaproductionBlock(String name) {
        super(name);
        oreGrowBonus = 0;
        selfGrowRate = 0.9f;
    }

    static Rand rand = new Rand();

    public class NeoplasiaProductionBlockBuild extends NeoplasiaBuild{

        @Override
        public void draw() {
            float fullness = amount / (maxAmount * 0.75f);
            Draw.z(Renderer.Layer.neoplasiaBase);
            float radius = (tilesize * 2) / 2f;
            Draw.color(Color.valueOf("701e1e"), Color.valueOf("cf5a3b"), fullness);
            Fill.circle(x, y, radius);
            Draw.z(Renderer.Layer.neoplasiaUnder);
            Fill.circle(x, y, radius);
            Draw.z(Renderer.Layer.neoplasiaBase + .1f);
            float rot = Mathf.randomSeedRange(tile.pos() + 1, 22.5f);
            Draw.color();
            Draw.scl();
            Draw.rectv(region, tile.worldx(), tile.worldy(), region.width * region.scl(), region.height * region.scl(), 0, vec -> vec.add(
                    Mathf.sin(vec.y*3 + Time.time, wscl, wmag) + Mathf.sin(vec.x*3 - Time.time, 70 * wtscl, 0.8f * wmag2),
                    Mathf.cos(vec.x*3 + Time.time + 8, wscl + 6f, wmag * 1.1f) + Mathf.sin(vec.y*3 - Time.time, 50 * wtscl, 0.2f * wmag2)));
            Draw.z(Renderer.Layer.neoplasiaBase -0.2f);
            for(int i = 0; i < Mathf.randomSeed(this.id, 4, 6); i++){
               float rote = Mathf.randomSeed(this.id + i, 0, 360) + Mathf.sin(Time.time/5f, 1);
               Draw.rect(lobeRegion, x, y, rote);
            }
        }
    }
}
