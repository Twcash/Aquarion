package aquarion.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.liquid.LiquidRouter;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;
import static mindustry.type.Liquid.animationFrames;
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

        private final Set<Building> visited = new HashSet<>();

        public void equalizeLiquids() {
            if (!block.hasLiquids || liquids.currentAmount() <= 0.01f) return;
            visited.clear();
            aggregateAndDistributeLiquids(this);
        }

        private void aggregateAndDistributeLiquids(Building origin) {
            Queue<Building> queue = new LinkedList<>();
            queue.add(origin);
            visited.add(origin);

            Liquid liquidType = liquids.current();
            float totalLiquid = 0f;
            float totalCapacity = 0f;
            Set<Building> eligibleBlocks = new HashSet<>();

            while (!queue.isEmpty()) {
                Building current = queue.poll();

                if (current.liquids == null || !current.acceptLiquid(origin, liquidType)) continue;

                eligibleBlocks.add(current);
                totalLiquid += current.liquids.get(liquidType);
                totalCapacity += current.block.liquidCapacity;

                for (int i = 0; i < 4; i++) {
                    Building neighbor = current.nearby(i);

                    if (neighbor == null || visited.contains(neighbor) || neighbor.team != team) continue;

                    if ((neighbor.block.hasLiquids || neighbor.block.outputsLiquid)
                            && neighbor.acceptLiquid(current, liquidType)
                            && neighbor.relativeTo(current.tile) == (i + 2) % 4) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }

            if (eligibleBlocks.isEmpty()) return;

            float averageFraction = totalLiquid / totalCapacity;

            for (Building block : eligibleBlocks) {
                float targetAmount = averageFraction * block.block.liquidCapacity;
                float currentAmount = block.liquids.get(liquidType);
                float delta = targetAmount - currentAmount;

                if (Math.abs(delta) > 0.01f) {
                    if (delta > 0) {
                        block.handleLiquid(self(), liquidType, delta);
                    } else {
                        block.liquids.remove(liquidType, -delta);
                    }
                }
            }
        }

        @Override
        public void updateTile() {
            if (liquids.currentAmount() > 0.01f) {
                equalizeLiquids();
            }
        }

        public void handleLiquid(Building source, Liquid liquid, float amount) {
            liquids.add(liquid, amount);
        }

        public void transferLiquid(Building next, float amount, Liquid liquid) {
            float flow = Math.min(next.block.liquidCapacity - next.liquids.get(liquid), amount);
            if (flow > 0) {
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
        public void drawUnderPipes(float x, float y, int index1, int index2, Liquid liquid, float fullness, boolean blending) {
            Draw.rect(bottomRegion, x, y);

            int frame = liquid.getAnimationFrame();
            int gas = liquid.gas ? 1 : 0;
            float ox = 0f, oy = 0f;
            TextureRegion liquidr = index1 == 1 ? rotateRegions[index2][gas][frame] : Vars.renderer.fluidFrames[gas][frame];


            Drawf.liquid(liquidr, x, y, fullness, liquid.color.write(Tmp.c1).a(1f));

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
                            0, i % 2,
                            liquids.current(), liquids.currentAmount() / liquidCapacity, true
                    );
                }
            }
            Draw.z(Layer.block);
            drawUnderPipes(x, y, index1, index2, liquids.current(), liquids.currentAmount() / liquidCapacity, false);
        }
    }
}