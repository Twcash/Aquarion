package aquarion.world.blocks.neoplasia;

import aquarion.world.graphics.Renderer;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.Interp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.type.Item;

public class NeoplasmVein extends NeoplasiaproductionBlock {
    public NeoplasmVein(String name) {
        super(name);
        colFrom = Color.valueOf("5b132a");
        colTo = Color.valueOf("ba1334");
    }

    public class NeoplasmVeinBuild extends NeoplasiaProductionBlockBuild {
        float activity = 0f;
        int tissueDistance = Integer.MAX_VALUE;
        boolean crowded;
        float age;
        private final Color strandColor = new Color();

        Color veinColor(Color result){
            float brightness = 0.25f + 0.75f / (1f + Math.max(0, tissueDistance - 1) * 0.2f);
            return result.set(colFrom).lerp(colTo, Mathf.clamp(amount / maxAmount)).mul(brightness, brightness, brightness, 1f);
        }

        @Override
        public void created() {
            super.created();
            activity = 60f;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return hasItemRoom(item);
        }

        @Override
        public void handleItem(Building source, Item item) {
            super.handleItem(source, item);
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if (tile == null || tile.build != this) return;
            age += delta();
            if(crowded){
                activity = Math.max(0f, activity - delta() * 0.5f);
                routeTraffic = 0f;
            }else if (routeTraffic > 1f) {
                activity = Math.min(100f, activity + delta() * 5f);
            }
            if (activity <= 0f && base != null && crowded) {
                upgradeTo(base);
                if(tile.build instanceof NeoplasiaBuild tissue){
                    tissue.routeTraffic = 0f;
                    tissue.veinRegrowDelay = 36000f;
                }
            }
        }

        @Override
        void pushItems() {
            super.pushItems();
        }

        @Override
        public void draw(){
            float previousZ = Draw.z();
            float growth = Interp.smooth.apply(Mathf.clamp(spawnTime / Math.max(1f, spawnDuration)));
            float radius = baseSize * 0.5f * growth;
            // Both layers must match the mass shader's draw ranges exactly.
            veinColor(strandColor);
            Draw.color(strandColor);
            Draw.z(Renderer.Layer.neoplasiaUnder);
            Fill.circle(x, y, radius);
            Draw.z(Renderer.Layer.neoplasiaBase - 0.01f);
            Draw.color();
            if(region != null && region.found()){
                Draw.rect(region, x, y, region.width * region.scl() * growth,
                    region.height * region.scl() * growth);
            }
            Draw.z(Renderer.Layer.neoplasiaBase);
            Draw.color(strandColor);
            Fill.circle(x, y, radius);
            Draw.reset();
            Draw.z(previousZ);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(activity);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            activity = read.f();
        }
    }
}
