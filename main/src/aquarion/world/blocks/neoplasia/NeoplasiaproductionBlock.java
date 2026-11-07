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
import arc.func.Cons;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BuildVisibility;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;
import static mindustry.content.Blocks.*;

public class NeoplasiaproductionBlock extends GenericNeoplasiaBlock{
    public TextureRegion lobeBotRegion;
    public TextureRegion lobeRegion;
    public boolean shouldCraft = false;
    public float craftCost = 10;
    public float craftTime = 60;
    public ItemStack output;
    public ItemStack input;

    @Override
    public ItemStack getOutput() {
        return output;
    }

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
        buildVisibility = BuildVisibility.sandboxOnly;
    }

    public class NeoplasiaProductionBlockBuild extends NeoplasiaBuild{
        public float prog = 0;

        final Cons<Vec2> wiggle = vec -> vec.add(
            Mathf.sin(vec.y * 3 + Time.time * Mathf.randomSeed(id, -0.1f, 1.3f), wscl, wmag) + Mathf.sin(vec.x * 3 - Time.time * Mathf.randomSeed(id, -0.1f, 1.2f), 70 * wtscl, 0.8f * wmag2),
            Mathf.cos(vec.x * 3 + Time.time + 8 * Mathf.randomSeed(id, -0.1f, 1.2f), wscl + 6f, wmag * 1.1f) + Mathf.sin(vec.y * 3 - Time.time * Mathf.randomSeed(id, -0.1f, 1.2f), 50 * wtscl, 0.2f * wmag2));

        @Override
        void tryUpgrades() {
            super.tryUpgrades();
            if (input != null) {
                needItem(input.item, input.amount);
            }
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(tile == null || tile.build != this || !shouldCraft || output == null) return;
            if(amount < craftCost + lifeReserve()) return;
            if(input != null && !items.has(input.item, input.amount)) return;
            int occupied = perItemCapacity ? items.get(output.item) : items.total();
            if(input != null && (!perItemCapacity || input.item == output.item)) occupied -= input.amount;
            if(occupied + output.amount > itemCapacity) return;
            prog = Math.min(1f, prog + delta() / Math.max(1f, craftTime));
            if(prog >= 1f - 0.00001f){
                if(input != null) items.remove(input);
                items.add(output.item, output.amount);
                amount -= craftCost;
                prog = 0f;
            }
        }

        @Override
        public byte version(){ return transportSaveRevision + 1; }

        @Override
        public void write(arc.util.io.Writes write){
            super.write(write);
            write.f(prog);
        }

        @Override
        public void read(arc.util.io.Reads read, byte revision){
            super.read(read, revision);
            prog = revision >= 1 ? read.f() : 0f;
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
            Draw.rectv(region, tile.worldx(), tile.worldy(), region.width * region.scl() * scale, region.height * region.scl() * scale, Mathf.randomSeed(id, -45, 45), wiggle);
            Draw.z(Renderer.Layer.neoplasiaBase -0.2f);
            for(int i = 0; i < Mathf.randomSeed(this.id, 4, 6); i++){
               float rote = Mathf.randomSeed(this.id + i, 0, 360) + Mathf.sin(Time.time/5f, 1);
               Draw.rect(lobeRegion, x, y, rote);
            }
        }
    }
}
