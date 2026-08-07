package aquarion.world.blocks.neoplasia;

import aquarion.world.graphics.Renderer;
import arc.func.Cons;
import arc.graphics.g2d.Draw;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.type.Item;

public class NeoplasmHeart extends GenericNeoplasiaBlock {
    public float techStep = 100f;
    public int techMax = 5;
    public float heartCost = 150f;
    public float dissolveRate = 0.005f;

    public NeoplasmHeart(String name) {
        super(name);
        update = true;
        solid = true;
        destructible = true;
        rebuildable = false;
    }

    public class NeoplasmHeartBuild extends GenericNeoplasiaBlock.NeoplasiaBuild {
        public boolean isPrime = false;
        public boolean dissolving = false;

        @Override
        public void created() {
            super.created();
            NeoplasiaGraph.addHeart(this);
        }

        @Override
        public void onRemoved() {
            super.onRemoved();
            NeoplasiaGraph.removeHeart(this);
        }

        @Override
        public void updateTile() {
            if (tile == null) return;
            if (spawnTime < spawnDuration) spawnTime += delta();
            health = amount;
            maxHealth = amount;
            if (amount <= 0f) {
                kill();
                return;
            }
            if (dissolving) {
                disconnectionTime += delta();
                amount -= amount * dissolveRate * delta();
            } else {
                grow();
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return false;
        }

        final Cons<Vec2> wiggle = vec -> vec.add(
            Mathf.sin(vec.y * 3 + Time.time, wscl, wmag) + Mathf.sin(vec.x * 3 - Time.time, 70 * wtscl, 0.8f * wmag2),
            Mathf.cos(vec.x * 3 + Time.time + 8, wscl + 6f, wmag * 1.1f) + Mathf.sin(vec.y * 3 - Time.time, 50 * wtscl, 0.2f * wmag2));

        @Override
        public void draw() {
            super.draw();
            float scale = 1f;
            if(spawnTime < spawnDuration){
                float progress = spawnTime / spawnDuration;
                scale = Interp.smooth.apply(progress);
            }
            Draw.scl(scale);
            Draw.color();
            Draw.z(Renderer.Layer.blockOver + 2);
            Draw.rectv(region, tile.worldx(), tile.worldy(), region.width * region.scl() * scale, region.height * region.scl() * scale, 0, wiggle);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.bool(isPrime);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            isPrime = read.bool();
        }

        public int techLevel() {
            int level = 1 + (int) (amount / techStep);
            return Math.min(techMax, level);
        }
    }
}
