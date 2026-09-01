package aquarion.world.blocks.distribution;

import aquarion.annotations.Annotations;
import aquarion.ui.LiquidBar;
import aquarion.world.content.LiquidReactions;
import aquarion.world.content.LiquidUtil;
import aquarion.world.graphics.PipeBubble;
import aquarion.world.graphics.PipeBubbles;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.Autotiler;
import mindustry.world.blocks.liquid.LiquidRouter;

public class Pipe extends LiquidRouter implements Autotiler {
    public static final int maxBubbles = 6;
    private static final Rand rand = new Rand();
    public TextureRegion[][][] topRegions;
    public @Annotations.Load("@-bottom") TextureRegion bottomRegion;
    //I think this is the second time I've ever just blatantly stole from a mod
    public static final int[][] blendIndices = {
            //Labeled these bc there's no way I'm remembering this
            //P.s Am I a vibe coder for using Chatgpt for labeling these for me bc I'm a lazy ass :troll:
            {0, 0}, // 0000 - none
            {1, 0}, // 0001 - right
            {1, 1}, // 0010 - up
            {2, 0}, // 0011 - up + right
            {1, 2}, // 0100 - left
            {2, 1}, // 0101 - left + right
            {2, 2}, // 0110 - left + up
            {3, 0}, // 0111 - left + up + right
            {1, 3}, // 1000 - down
            {2, 3}, // 1001 - down + right
            {2, 4}, // 1010 - down + up
            {3, 1}, // 1011 - down + up + right
            {2, 5}, // 1100 - down + left
            {3, 2}, // 1101 - down + left + right
            {3, 3}, // 1110 - down + left + up
            {3, 4}  // 1111 - all
    };
    public static final float rotatePad = 6, hpad = rotatePad / 2f / 4f;
    public static final float[][] rotateOffsets = {{hpad, hpad}, {-hpad, hpad}, {-hpad, -hpad}, {hpad, -hpad}};

    /**
     * {@code buildBlending} reports bit 1 as a "down" connection and bit 3 as an "up" connection
     * (because it checks {@code nearbyBuild(mod(rotation - i, 4))}), but the tile sheet and
     * {@code blendIndices} use the opposite convention (bit 1 = up, bit 3 = down). Swap the two
     * bits so the drawn tile matches the actual connections.
     */
    static int normalizeTiling(int mask){
        return (mask & ~0b1010) | ((mask & 0b0010) != 0 ? 0b1000 : 0) | ((mask & 0b1000) != 0 ? 0b0010 : 0);
    }
    public TextureRegion[][] regions;
    public TextureRegion[][][] rotateRegions;

    public Pipe(String name) {
        super(name);
        solid = false;
        noUpdateDisabled = true;
        canOverdrive = false;
        floating = true;
        liquidCapacity = 30;
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("liquid");
    }
    @Override
    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock){
        return otherblock.hasLiquids;
    }
    @Override
    public void load() {
        super.load();

        //load each of the 16 tiling states as its own 32x32 region, placed at its blendIndices cell.
        //this avoids splitting a packed sheet, whose orientation/dimensions can vary after atlas packing
        regions = new TextureRegion[4][6];
        for(int i = 0; i < blendIndices.length; i++){
            regions[blendIndices[i][0]][blendIndices[i][1]] = Core.atlas.find(name + "-top-" + (i + 1));
        }
        topRegions = new TextureRegion[4][2][Liquid.animationFrames];

        rotateRegions = new TextureRegion[4][2][Liquid.animationFrames];

        if (Vars.renderer != null) {
            float pad = rotatePad;
            TextureRegion[][] frames = Vars.renderer.getFluidFrames();

            for (int rot = 0; rot < 4; rot++) {
                for (int fluid = 0; fluid < 2; fluid++) {
                    for (int frame = 0; frame < Liquid.animationFrames; frame++) {
                        TextureRegion base = frames[fluid][frame];
                        TextureRegion result = new TextureRegion();
                        result.set(base);
                        rotateRegions[rot][fluid][frame] = result;
                    }
                }
            }
        }
    }
    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        int[] bits = getTiling(plan, list);

        if (bits == null) return;

        int[] blending = blendIndices[normalizeTiling(bits[3])];
        int index1 = blending[0];
        int index2 = blending[1];

        Draw.rect(bottomRegion, plan.drawx(), plan.drawy());
        Draw.rect(regions[index1][index2], plan.drawx(), plan.drawy());
    }



    public class PipeBuild extends LiquidRouterBuild {
        public int tiling = 0, blending;
        public int index1, index2, underBlending;
        public Seq<PipeBubble> bubbles = new Seq<>();
        public float spawnTimer;

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
        public void updateTile() {
            //reactions between mixed liquids
            LiquidReactions.react(self());

            if (LiquidUtil.total(liquids) > 0.001f) {
                //instant shared liquid: no throughput limit, dump every present liquid to connected blocks
                liquids.each((liquid, amount) -> {
                    if (amount > 0.0001f) dumpLiquid(liquid, 2f);
                });
                noSleep();
                updateBubbles();
            } else {
                bubbles.clear();
                sleep();
            }
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            noSleep();
            return LiquidUtil.freeSpace(self()) > 0.01f;
        }

        public void handleLiquid(Building source, Liquid liquid, float amount) {
            noSleep();
            liquids.add(liquid, amount);
        }



        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            int[] bits = buildBlending(tile, 0, null, true);
            underBlending = bits[4];

            int[] blending = blendIndices[normalizeTiling(bits[3])];
            index1 = blending[0];
            index2 = blending[1];
        }
        public void drawUnderPipes(float x, float y, int index1, int index2, boolean blending) {
            //the bottom plate goes under the liquid so the fill shows through the pipe channel
            if(!blending) Draw.z(Layer.block - 0.02f);
            Draw.rect(bottomRegion, x, y);

            if(LiquidUtil.total(liquids) > 0.0001f){
                //draw every mixed liquid as a flat fill stacked over the previous one
                LiquidUtil.drawOverlayedLiquid(fluid -> {
                    int frame = fluid.getAnimationFrame();
                    int gas = fluid.gas ? 1 : 0;
                    return index1 == 1 ? rotateRegions[index2][gas][frame] : Vars.renderer.fluidFrames[gas][frame];
                }, x, y, liquidCapacity, liquids);
            }

            if(!blending){
                Draw.z(Layer.block - 0.01f);
                drawBubbles();
                Draw.z(Layer.block);
            }

            if(blending) {
                Draw.rect(regions[3][4], x, y);
            } else {
                Draw.rect(regions[index1][index2], x, y);
            }
        }


        public void drawBubbles(){
            if(bubbles.isEmpty()) return;
            float half = Vars.tilesize / 2f;
            for(PipeBubble b : bubbles){
                float fill = Mathf.clamp(liquids.get(b.liquid) / liquidCapacity, 0f, 1f);
                PipeBubbles.drawBubble(b, x, y, fill, half);
            }
            Draw.color();
        }

        public void updateBubbles(){
            for(int i = bubbles.size - 1; i >= 0; i--){
                PipeBubble b = bubbles.get(i);
                b.t += Time.delta * PipeBubbles.speed * Mathf.clamp(liquids.get(b.liquid) / liquidCapacity, 0f, 1f);
                if(b.t >= 1f){
                    Building nb = nearby(b.dst);
                    if(nb instanceof PipeBuild p){
                        p.receiveBubble(b.liquid, (b.dst + 2) % 4, -b.lat, b.size);
                    }
                    bubbles.remove(i);
                }
            }

            spawnTimer -= Time.delta;
            if(spawnTimer <= 0f){
                spawnTimer = 0.3f;
                if(LiquidUtil.total(liquids) > 0.01f && bubbles.size < maxBubbles){
                    Liquid dom = dominantLiquid();
                    if(dom != null){
                        int dst = downhillDir(dom);
                        if(dst != -1){
                            bubbles.add(new PipeBubble(dom, -1, dst, rand.range(PipeBubbles.spread), rand.random(0.7f, 1.2f)));
                        }
                    }
                }
            }
        }

        public void receiveBubble(Liquid liquid, int src, float lat, float size){
            if(bubbles.size >= maxBubbles) return;
            int dst = downhillDir(liquid);
            if(dst == -1) return;
            bubbles.add(new PipeBubble(liquid, src, dst, lat, size));
        }

        public int downhillDir(Liquid liquid){
            float fill = liquids.get(liquid) / liquidCapacity;
            int[] cands = new int[4];
            int found = 0;
            for(int d = 0; d < 4; d++){
                Building nb = nearby(d);
                if(nb == null || nb.team != team) continue;
                if(!nb.block.hasLiquids || nb.liquids == null || nb.block.liquidCapacity <= 0f) continue;
                if(nb.liquids.get(liquid) / nb.block.liquidCapacity >= fill - 0.0001f) continue;
                cands[found++] = d;
            }
            return found == 0 ? -1 : cands[rand.random(0, found - 1)];
        }

        public Liquid dominantLiquid(){
            final Liquid[] dom = {null};
            final float[] max = {0f};
            liquids.each((liquid, amount) -> {
                if(amount > max[0]){
                    max[0] = amount;
                    dom[0] = liquid;
                }
            });
            return dom[0];
        }

        @Override
        public void draw() {
            Draw.z(Layer.blockUnder);
            for (int i = 0; i < 4; i++) {
                if ((underBlending & (1 << i)) != 0) {
                    int j = i % 2 == 0 ? i : i + 2;
                    drawUnderPipes(
                            x + Geometry.d4x(j) * Vars.tilesize,
                            y + Geometry.d4y(j) * Vars.tilesize,
                            0, i % 2, true
                    );
                }
            }
            Draw.z(Layer.block);
            drawUnderPipes(x, y, index1, index2, false);
        }
    }
}