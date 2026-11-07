package aquarion.world.blocks.neoplasia;

import aquarion.annotations.Annotations;
import aquarion.content.AquaItems;
import aquarion.world.graphics.Renderer;
import arc.func.Cons;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.type.ItemStack;
import mindustry.world.Tile;
import arc.math.geom.Geometry;
import arc.util.io.Reads;
import arc.util.io.Writes;
import static mindustry.Vars.world;
import static mindustry.content.Blocks.air;

public class NeoplasiaBlister extends NeoplasiaproductionBlock{
    public @Annotations.Load("@-bottom") TextureRegion bottomRegion;
    public @Annotations.Load("@-top") TextureRegion topRegion;

    public float growthAmount = 5000;
    public NeoplasiaBlister(String name) {
        super(name);
        maxAmount = 10000;
        baseSize = 5;
        itemCost = ItemStack.with(AquaItems.crystal, 20);
        input = new ItemStack(AquaItems.crystal, 2);
        shouldCraft = true;
        craftTime = 600f;
         burstThresholdFraction = 0.2f;
         burstDelay = 600f;
         bursts = 1;
         burstLength = 60;
         selfGrowRate = 0.5f;
         burstTransfer = 75;
    }
    public class BlisterBuild extends NeoplasiaBuild {
        public float prog;
        boolean charged;
        final Cons<Vec2> wiggle = vec -> vec.add(
            Mathf.sin(vec.y * 3f + Time.time * 0.4f, wscl, wmag),
            Mathf.cos(vec.x * 3f + Time.time * 0.3f, wscl + 6f, wmag));

        @Override
        void tryUpgrades(){
            super.tryUpgrades();
            if(input != null && !charged && burstDir < 0 && amount - craftCost + growthAmount <= maxAmount) needItem(input.item, input.amount);
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(!shouldCraft || amount < craftCost || charged || burstDir >= 0) return;
            if(amount - craftCost + growthAmount > maxAmount) return;
            if(input != null && !items.has(input.item, input.amount)){
                pullItems(input.item, input.amount);
                return;
            }
            prog = Math.min(1f, prog + delta() / Math.max(1f, craftTime));
            if(prog >= 1f - 0.00001f){
                if(input != null) items.remove(input);
                amount = Math.min(maxAmount, amount - craftCost + growthAmount);
                prog = 0f;
                charged = true;
            }
        }

        @Override
        void burstSpread(){
            if(!charged && burstDir < 0){
                burstCooldown = Math.max(0f, burstCooldown - delta());
                return;
            }
            Tile previous = burstTile == null ? tile : burstTile;
            super.burstSpread();
            if(burstTile != null && burstTile != previous) widenTrail(previous, burstTile);
            if(burstDir >= 0){
                charged = false;
                currentBurstLength = burstLength;
            }
        }

        int trailWidth(){
            float progress = (float)burstStep / Math.max(1, burstLength);
            return progress < 0.25f ? 2 : progress < 0.65f ? 1 : 0;
        }

        @Override
        float burstTransferAmount(){
            float progress = Mathf.clamp((float)burstStep / Math.max(1, burstLength));
            return burstTransfer * (1f - 0.6f * progress) / (1 + trailWidth() * 2);
        }

        void widenTrail(Tile previous, Tile center){
            int dx = center.x - previous.x, dy = center.y - previous.y;
            float share = burstTransferAmount();
            for(int side = -1; side <= 1; side += 2) for(int width = 1; width <= trailWidth(); width++){
                Tile target = world.tile(center.x - dy * side * width, center.y + dx * side * width);
                if(target == null || target.solid() || target.floor().isDeep()) continue;
                if(target.block() != air && (target.block() != base || target.team() != team)) continue;
                float flow = Math.min(share, Math.max(0f, amount - lifeReserve()));
                if(flow <= 0f || base == null) continue;
                if(target.block() == air){
                    target.setBlock(base, team);
                    if(target.build instanceof NeoplasiaBuild tissue) tissue.amount = 0f;
                }
                if(target.build instanceof NeoplasiaBuild tissue){
                    flow = Math.min(flow, Math.max(0f, tissue.massCapacity() - tissue.amount));
                    amount -= flow;
                    tissue.receiveMass(flow);
                }
            }
        }

        @Override
        Tile pickBestNeighbor(Tile origin){
            Tile best = null;
            float score = -Float.MAX_VALUE;
            float radius = origin.dst2(tile);
            float distance = origin.dst(tile) / mindustry.Vars.tilesize;
            float heading = Mathf.randomSeed(tile.pos() * 11L + burstCount, 0f, 360f)
                + Mathf.sin(distance, 5f, 32f) + Mathf.sin(distance, 2.8f, 12f);
            for(var offset : Geometry.d8){
                Tile next = world.tile(origin.x + offset.x, origin.y + offset.y);
                if(next == null || next.solid() || next.floor().isDeep() || next.dst2(tile) <= radius) continue;
                if(next.block() != air && !(next.build instanceof NeoplasiaBuild && next.team() == team)) continue;
                float stepLength = Mathf.sqrt(offset.x * offset.x + offset.y * offset.y);
                float candidate = (offset.x * Mathf.cosDeg(heading) + offset.y * Mathf.sinDeg(heading)) / stepLength
                    + Mathf.randomSeed(next.pos() * 17L + burstCount, 0f, 0.12f);
                if(candidate > score){ score = candidate; best = next; }
            }
            return best;
        }

        @Override
        public byte version(){ return transportSaveRevision + 1; }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(prog);
            write.bool(charged || burstDir >= 0);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 1){ prog = read.f(); charged = read.bool(); }
        }
        @Override
        public void draw() {
            super.draw();
            float scale = 0.8f + 0.2f * prog;
            if(spawnTime < spawnDuration){
                float progress = spawnTime / spawnDuration;
                scale = Interp.smooth.apply(progress);
            }
            Draw.rectv(bottomRegion, tile.worldx(), tile.worldy(), region.width * region.scl() * scale, region.height * region.scl() * scale, Mathf.randomSeed(id, -45, 45), wiggle);
            Draw.scl(scale);
            Draw.z(Renderer.Layer.blockOver + 2);
            Draw.color();
            Draw.rectv(region, tile.worldx(), tile.worldy(), region.width * region.scl() * scale, region.height * region.scl() * scale, Mathf.randomSeed(id, -45, 45), wiggle);
            Draw.z(Renderer.Layer.blockOver + 3);
            Draw.rectv(topRegion, tile.worldx(), tile.worldy(), region.width * region.scl() * scale, region.height * region.scl() * scale, Mathf.randomSeed(id, -45, 45), wiggle);
            Draw.reset();
            Draw.z(Renderer.Layer.neoplasiaBase -0.2f);
        }
    }
}
