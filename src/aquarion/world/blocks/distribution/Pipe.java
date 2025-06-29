package aquarion.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Tmp;
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
    public TextureRegion[] topRegions = new TextureRegion[16];
    public TextureRegion[][] liquidRegions;
    public TextureRegion bottomRegion, otherRegion;

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
        return otherblock.hasLiquids && (otherblock.outputsLiquid || (lookingAt(tile, rotation, otherx, othery, otherblock))) && lookingAtEither(tile, rotation, otherx, othery, otherrot, otherblock);
    }
    @Override
    public void load() {
        super.load();
        bottomRegion = Core.atlas.find(name + "-bottom");
        otherRegion = Core.atlas.find(name + "-other");

        for (int i = 0; i < 16; i++) {
            topRegions[i] = Core.atlas.find(name + "-top-" + (i + 1));
        }

        liquidRegions = new TextureRegion[2][animationFrames];
        if (renderer != null) {
            var frames = renderer.getFluidFrames();
            for (int fluid = 0; fluid < 2; fluid++) {
                for (int frame = 0; frame < animationFrames; frame++) {
                    TextureRegion base = frames[fluid][frame];
                    TextureRegion result = new TextureRegion();
                    result.set(base);
                    result.setHeight(result.height - liquidPadding);
                    result.setWidth(result.width - liquidPadding);
                    result.setX(result.getX() + liquidPadding);
                    result.setY(result.getY() + liquidPadding);
                    liquidRegions[fluid][frame] = result;
                }
            }
        }
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        int tiling = 0;
        BuildPlan[] proximity = new BuildPlan[4];

        list.each(next -> {
            for (int i = 0; i < 4; i++) {
                Point2 side = new Point2(plan.x, plan.y).add(Geometry.d4[i]);
                if (new Point2(next.x, next.y).equals(side) &&
                        ((next.block instanceof Pipe) ? (plan.rotation % 2 == i % 2 || next.rotation % 2 == i % 2)
                                : next.block.outputsLiquid)) {
                    proximity[i] = next;
                    break;
                }
            }
        });

        for (int i = 0; i < 4; i++) {
            if (proximity[i] != null) tiling |= (1 << i);
        }

        Draw.rect(bottomRegion, plan.drawx(), plan.drawy(), 0);
        Draw.rect(topRegions[tiling], plan.drawx(), plan.drawy(), 0);
    }



    public class PipeBuild extends LiquidRouterBuild {
        public int tiling = 0, blendbits, blending;
        public float smoothLiquid;

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
            noSleep();
            for (int i = 0; i < 4; i++) {
                int[] bits = buildBlending(tile, rotation, null, true);
                tiling = bits[3];
                blendbits = bits[0];
                blending = bits[4];
            }
        }

        @Override
        public void draw() {
            Draw.z(Layer.blockUnder);
            Draw.rect(bottomRegion, x, y);

            if (liquids.currentAmount() > 0.01f) {
                int frame = liquids.current().getAnimationFrame();
                int gas = liquids.current().gas ? 1 : 0;

                float xs = Draw.xscl, ys = Draw.yscl;
                Draw.scl(1f, 1f);

                Drawf.liquid(
                        liquidRegions[gas][frame],
                        x, y,
                        liquids.currentAmount() / liquidCapacity,
                        liquids.current().color.write(Tmp.c1).a(1f)
                );

                Draw.scl(xs, ys);
            }

            Draw.rect(topRegions[tiling], x, y);
            Draw.reset();
        }
    }
}