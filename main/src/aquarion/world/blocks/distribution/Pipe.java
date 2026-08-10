package aquarion.world.blocks.distribution;

import aquarion.ui.LiquidBar;
import aquarion.world.content.LiquidReactions;
import aquarion.world.content.LiquidUtil;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
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
    public TextureRegion[][][] topRegions;
    public TextureRegion[][] liquidRegions;
    public TextureRegion bottomRegion;
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

        regions = Core.atlas.find(name + "-sheet").split(32, 32);
        bottomRegion = regions[0][1];

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

        int[] blending = blendIndices[bits[3]];
        int index1 = blending[0];
        int index2 = blending[1];

        Draw.rect(bottomRegion, plan.drawx(), plan.drawy());
        Draw.rect(regions[index1][index2], plan.drawx(), plan.drawy());
    }



    public class PipeBuild extends LiquidRouterBuild {
        public int tiling = 0, blending;
        public int index1, index2, underBlending;

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
                liquids.each((liquid, amount) -> {
                    if (amount <= 0.0001f) return;

                    //push every present liquid out to all connected neighbors using the conduit flow formula
                    float remaining = liquids.get(liquid);
                    for (int i = 0; i < 4 && remaining > 0.01f; i++) {
                        Building next = nearby(i);
                        if (next == null || next.team != team) continue;
                        if (!next.block.hasLiquids && !next.block.outputsLiquid) continue;

                        float flow = Math.min(remaining, LiquidUtil.flow(self(), liquid, next) * delta());
                        if (flow > 0.01f && next.acceptLiquid(this, liquid)) {
                            next.handleLiquid(this, liquid, flow);
                            remaining -= flow;
                        }
                    }
                    if (remaining < amount - 0.001f) {
                        liquids.remove(liquid, amount - remaining);
                    }
                });
                noSleep();
            } else {
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

        public void transferLiquid(Building next, float amount, Liquid liquid) {
            float flow = Math.min(LiquidUtil.flow(self(), liquid, next) * delta(), amount);
            if (flow > 0.01f && next.acceptLiquid(self(), liquid)) {
                next.handleLiquid(self(), liquid, flow);
                liquids.remove(liquid, flow);
            }
        }



        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            int[] bits = buildBlending(tile, 0, null, true);
            underBlending = bits[4];

            int[] blending = blendIndices[bits[3]];
            index1 = blending[0];
            index2 = blending[1];
        }
        public void drawUnderPipes(float x, float y, int index1, int index2, boolean blending) {
            Draw.rect(bottomRegion, x, y);

            if(LiquidUtil.total(liquids) > 0.0001f){
                //draw every mixed liquid as a flat fill stacked over the previous one
                LiquidUtil.drawOverlayedLiquid(fluid -> {
                    int frame = fluid.getAnimationFrame();
                    int gas = fluid.gas ? 1 : 0;
                    return index1 == 1 ? rotateRegions[index2][gas][frame] : Vars.renderer.fluidFrames[gas][frame];
                }, x, y, liquidCapacity, liquids);
            }

            if(blending) {
                Draw.rect(regions[3][4], x, y);
            } else {
                Draw.rect(regions[index1][index2], x, y);
            }
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