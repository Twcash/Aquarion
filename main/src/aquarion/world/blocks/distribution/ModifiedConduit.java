package aquarion.world.blocks.distribution;

import aquarion.ui.LiquidBar;
import aquarion.world.content.LiquidReactions;
import aquarion.world.content.LiquidUtil;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.AquaPuddles;
import aquarion.world.graphics.PipeBubble;
import aquarion.world.graphics.PipeBubbles;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Liquid;
import mindustry.world.Tile;
import mindustry.world.blocks.Autotiler.SliceMode;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.meta.StatUnit;

import static aquarion.world.Uti.AquaStats.MaxFlow;
import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

public class ModifiedConduit extends Conduit {
    public static final int maxBubbles = 6;
    private static final Rand rand = new Rand();
    //copy of Conduit's package-private rotateOffsets, needed for the rotated liquid regions
    static final float rotatePad = 6, hpad = rotatePad / 2f / 4f;
    static final float[][] rotateOffsets = {{hpad, hpad}, {-hpad, hpad}, {-hpad, -hpad}, {hpad, -hpad}};
    @Override
    public void load(){
        super.load();
    }
    public boolean willMelt = true;
    public ModifiedConduit(String name) {
        super(name);
    }
    @Override
    public void setStats(){
        super.setStats();

        //have to add a custom calculated speed, since the actual movement speed is apparently not linear
        stats.add(MaxFlow, liquidCapacity*60/2, StatUnit.liquidUnits);
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("liquid");
    }
    public class ModifiedConduitBuild extends ConduitBuild {
        public Seq<PipeBubble> bubbles = new Seq<>();
        public float bubbleAccum;

        @Override
        public void displayBars(Table bars){
            super.displayBars(bars);
            liquids.each((liquid, amount) -> {
                if(amount > 0.001f){
                    bars.add(new LiquidBar(self(), liquid));
                    bars.row();
                }
            });
        }
        @Override
        public void drawCached(){
            draw(true);
        }

        @Override
        public void draw(){
            draw(false);
        }

        public void draw(boolean under){
            int r = this.rotation;

            if(under) Draw.color(botColor);

            //draw extra conduits facing this one for tiling purposes
            Draw.z(Layer.blockUnder);
            for(int i = 0; i < 4; i++){
                if((blending & (1 << i)) != 0){
                    int dir = r - i;
                    drawAt(x + Geometry.d4x(dir) * tilesize*0.75f, y + Geometry.d4y(dir) * tilesize*0.75f, 0, i == 0 ? r : dir, i != 0 ? SliceMode.bottom : SliceMode.top, under);
                }
            }

            Draw.z(Layer.block);

            Draw.scl(xscl, yscl);
            drawAt(x, y, blendbits, r, SliceMode.none, under);
            Draw.reset();

            if(!under) return;

            if(capped && capRegion.found()) Draw.rect(capRegion, x, y, rotdeg());
            if(backCapped && capRegion.found()) Draw.rect(capRegion, x, y, rotdeg() + 180);
        }

        @Override
        protected void drawAt(float x, float y, int bits, int rotation, SliceMode slice, boolean under){
            if(under){
                Draw.rect(sliced(botRegions[bits], slice), x, y, rotation * 90f);
                return;
            }

            int offset = yscl == -1 ? 3 : 0;
            int wrapRot = (rotation + offset) % 4;
            float ox = 0f, oy = 0f;
            if(bits == 1 && padCorners){
                ox = ModifiedConduit.rotateOffsets[wrapRot][0];
                oy = ModifiedConduit.rotateOffsets[wrapRot][1];
            }

            boolean main = slice == SliceMode.none;
            if(main) Draw.z(Layer.block - 0.02f);

            if(LiquidUtil.total(liquids) > 0.0001f){
                //draw every mixed liquid as a flat fill stacked over the previous one, not just the dominant liquid
                float xscl = Draw.xscl, yscl = Draw.yscl;
                Draw.scl(1f, 1f);
                LiquidUtil.drawOverlayed(fluid -> sliced(bits == 1 && padCorners
                        ? rotateRegions[wrapRot][fluid.gas ? 1 : 0][fluid.getAnimationFrame()]
                        : renderer.fluidFrames[fluid.gas ? 1 : 0][fluid.getAnimationFrame()], slice),
                    x + ox, y + oy, Mathf.clamp(smoothLiquid, 0f, 1f), liquids);
                Draw.scl(xscl, yscl);
            }

            if(main){
                Draw.z(Layer.block - 0.01f);
                drawBubbles();
                Draw.z(Layer.block);
            }

            Draw.rect(sliced(topRegions[bits], slice), x, y, rotation * 90f);
        }

        @Override
        public void updateTile() {
            smoothLiquid = Mathf.lerpDelta(smoothLiquid, LiquidUtil.total(liquids) / liquidCapacity, 0.05f);

            //reactions between mixed liquids
            LiquidReactions.react(self());

            if (LiquidUtil.total(liquids) > 0.0001f) {
                liquids.each((liquid, amount) -> {
                    if (amount > 0.0001f) {
                        moveLiqFor(leaks, liquid);
                    }
                });
                noSleep();
                updateBubbles();
            } else {
                bubbles.clear();
                sleep();
            }

            if (willMelt) {
                liquids.each((liquid, amount) -> {
                    if (amount > 0.1f && liquid.temperature > 0.5f) {
                        damageContinuous(liquid.temperature / 100f);
                        if (Mathf.chanceDelta(0.01)) {
                            Fx.steam.at(x, y);
                        }
                    }
                });
            }
        }

        public float moveLiqFor(boolean leaks, Liquid liquid) {
            Tile next = tile.nearby(rotation);

            if (next == null) return 0;

            if (next.build != null) {
                return moveLiqNew(next.build, liquid);
            } else if (leaks && !next.block().solid && !next.block().hasLiquids) {
                float leakAmount = liquids.get(liquid) / 1.5f;
                AquaPuddles.deposit(next, tile, liquid, leakAmount, true, true);
                liquids.remove(liquid, leakAmount);
            }
            return 0;

        }

        public float moveLiqNew(Building next, Liquid liquid) {
            if (next == null) return 0;

            next = next.getLiquidDestination(self(), liquid);

            if (next == null) return 0;

            if (next.team == team && next.block.hasLiquids && liquids.get(liquid) > 0f) {
                float flow = LiquidUtil.flow(self(), liquid, next) * delta();
                if (flow > 0.01f && next.acceptLiquid(self(), liquid)) {
                    next.handleLiquid(self(), liquid, flow);
                    liquids.remove(liquid, flow);
                    return flow;
                } else if (!next.block.consumesLiquid(liquid)) {
                    //the two liquids can't mix (next is full or won't accept), so they react at the boundary
                    LiquidReactions.reactAtBoundary(self(), liquid, next);
                }
            }
            return 0;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            noSleep();
            return LiquidUtil.freeSpace(self()) > 0.01f
                    && (tile == null || source == this || (source.relativeTo(tile.x, tile.y) + 2) % 4 != rotation);
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount){
            float free = LiquidUtil.freeSpace(self());
            if(free <= 0.01f) return;
            liquids.add(liquid, Math.min(amount, free));

            //bubbles only spawn where liquid enters from a non-pipe source; pipe-to-pipe
            //flow is covered by the handoff in updateBubbles so bubbles stay continuous
            if(!(source instanceof ModifiedConduitBuild || source instanceof Pipe.PipeBuild)){
                bubbleAccum += amount;
                if(bubbleAccum > liquidCapacity * 0.25f && bubbles.size < maxBubbles){
                    bubbleAccum = 0f;
                    int src = relativeTo(source.tile.x, source.tile.y);
                    if(src < 0) src = (rotation + 2) % 4;
                    bubbles.add(new PipeBubble(liquid, src, rotation, rand.range(PipeBubbles.spread), rand.random(0.7f, 1.2f)));
                }
            }
        }

        public void updateBubbles(){
            for(int i = bubbles.size - 1; i >= 0; i--){
                PipeBubble b = bubbles.get(i);
                b.t += Time.delta * PipeBubbles.speed * Mathf.clamp(smoothLiquid, 0.05f, 1f);
                if(b.t >= 1f){
                    Building nb = nearby(rotation);
                    if(nb != null && nb.acceptLiquid(this, b.liquid)){
                        if(nb instanceof ModifiedConduitBuild c){
                            c.receiveBubble(b.liquid, (rotation + 2) % 4, -b.lat, b.size);
                        }else if(nb instanceof Pipe.PipeBuild p){
                            p.receiveBubble(b.liquid, (rotation + 2) % 4, -b.lat, b.size);
                        }
                    }
                    bubbles.remove(i);
                }
            }
        }

        public void receiveBubble(Liquid liquid, int src, float lat, float size){
            if(bubbles.size >= maxBubbles) return;
            bubbles.add(new PipeBubble(liquid, src, rotation, lat, size));
        }

        public void drawBubbles(){
            if(bubbles.isEmpty()) return;
            float half = tilesize / 2f;
            for(PipeBubble b : bubbles){
                PipeBubbles.drawBubble(b, x, y, Mathf.clamp(smoothLiquid, 0.05f, 1f), half);
            }
            Draw.color();
        }

        @Nullable
        @Override
        public Building next(){
            Tile next = tile.nearby(rotation);
            if(next != null && next.build instanceof ConduitBuild){
                return next.build;
            }
            return null;
        }
    }
}
