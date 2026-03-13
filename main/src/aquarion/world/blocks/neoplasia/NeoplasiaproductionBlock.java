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
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.util.Time;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class NeoplasiaproductionBlock extends GenericNeoplasiaBlock{
    public TextureRegion lobeBotRegion;
    public TextureRegion lobeRegion;
    public boolean shouldCraft = false;
    public float craftCost = 10;
    public float craftTime = 60;
    public ItemStack output;
    public ItemStack input;//Only have 1 for now

    @Override
    public void load(){
        super.load();
        lobeRegion = Core.atlas.find(name + "-lobe");
    }
    public NeoplasiaproductionBlock(String name) {
        super(name);
        oreGrowBonus = 0;
        selfGrowRate = 0.9f;
        shouldEmptyUpgrade = false;
    }

    public class NeoplasiaProductionBlockBuild extends NeoplasiaBuild{
        public float prog = 0;
        @Override
        public void updateTile(){
            super.updateTile();
            if(shouldCraft && amount > craftCost){
                prog += 1/craftTime*delta();
                if(input!= null) {
                    if (prog >= 1 && items.has(input.item.id) && output.amount + items.total() < itemCapacity) {
                        items.remove(input);
                        items.add(output.item, output.amount);
                        prog = 0;
                        amount -= craftCost;
                    }
                } else{
                    if (prog >= 1 &&  output.amount + items.total() < itemCapacity) {
                        items.add(output.item, output.amount);
                        prog = 0;
                        amount -= craftCost;
                    }
                }
            }
        }
        @Override
        public void draw() {
            super.draw();
            float scale = 1f;
            if(spawnTime < spawnDuration){
                float progress = spawnTime / spawnDuration;
                scale = Interp.smooth.apply(progress);
            }
            Draw.scl(scale);
            Draw.z(Renderer.Layer.blockOver + 2);
            Draw.color();
            Draw.rectv(region, tile.worldx(), tile.worldy(), region.width * region.scl() * scale, region.height * region.scl() * scale, Mathf.randomSeed(id, -45, 45), vec -> vec.add(
                    Mathf.sin(vec.y*3 + Time.time* Mathf.randomSeed(id, -0.1f, 1.3f), wscl, wmag) + Mathf.sin(vec.x*3 - Time.time * Mathf.randomSeed(id, -0.1f, 1.2f), 70 * wtscl, 0.8f * wmag2),
                    Mathf.cos(vec.x*3 + Time.time + 8* Mathf.randomSeed(id, -0.1f, 1.2f), wscl + 6f, wmag * 1.1f) + Mathf.sin(vec.y*3 - Time.time* Mathf.randomSeed(id, -0.1f, 1.2f), 50 * wtscl, 0.2f * wmag2)));
            Draw.z(Renderer.Layer.neoplasiaBase -0.2f);
            for(int i = 0; i < Mathf.randomSeed(this.id, 4, 6); i++){
               float rote = Mathf.randomSeed(this.id + i, 0, 360) + Mathf.sin(Time.time/5f, 1);
               Draw.rect(lobeRegion, x, y, rote);
            }
        }
    }
}
