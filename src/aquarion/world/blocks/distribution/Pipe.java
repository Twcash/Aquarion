package aquarion.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.geom.Geometry;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;

import java.util.HashSet;
import java.util.Set;

import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;
import static mindustry.type.Liquid.animationFrames;

public class Pipe extends mindustry.world.blocks.liquid.LiquidRouter  implements Autotiler{
    public TextureRegion[] topRegions = new TextureRegion[16]; // Array to store top textures for each flowMask
    public TextureRegion[][] liquidRegions;
    public TextureRegion[][][] rotateRegions;
    public TextureRegion otherRegion;
    public int blendbits, xscl = 1, yscl = 1, blending;
    public boolean padCorners = true;
    static final float rotatePad = 6, hpad = rotatePad / 2f / 4f;
    static final float[][] rotateOffsets = {{hpad, hpad}, {-hpad, hpad}, {-hpad, -hpad}, {hpad, -hpad}};


    public Pipe(String name) {
        super(name);
        solid = false;
        noUpdateDisabled = true;
        canOverdrive = false;
        floating = true;
        liquidCapacity = 30;
    }

    @Override
    public void load() {
        super.load();
        rotateRegions = new TextureRegion[4][2][animationFrames];
        bottomRegion = Core.atlas.find(name + "-bottom");
        otherRegion = Core.atlas.find(name + "-other");
        // Load textures for each possible flowMask (1 to 16)
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
        super.drawPlanRegion(plan, list);
        int[] tiling = getTiling(plan, list);
        if (tiling == null || tiling.length == 0) return;
        int selectedTiling = tiling[0];
        if (selectedTiling < 0 || selectedTiling >= topRegions.length) return;
        int rotation = plan.rotation;
        float rotationAngle = (selectedTiling != 0) ? 0 : (rotation + 90) % 180 - 90;
        Draw.rect(topRegions[selectedTiling], plan.x, plan.y, rotationAngle);
    }
    @Override
    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock){
        return otherblock.hasLiquids || otherblock.outputsLiquid;
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{bottomRegion, region};
    }

    public class PipeBuild extends LiquidRouterBuild {
        public int tiling = 0;
        public float smoothLiquid;
        private final Set<Building> visited = new HashSet<>();

        public void equalizeLiquids() {
            if (!block.hasLiquids || liquids.currentAmount() <= 0.01f) return;

            visited.clear(); // Clear visited set for each equalization call
            propagateEqualization(this);
        }

        private void propagateEqualization(Building current) {
            // Avoid revisiting blocks
            if (visited.contains(current)) return;
            visited.add(current);

            Liquid liquid = liquids.current();
            float currentAmount = current.liquids.get(liquid);
            float currentFraction = currentAmount / current.block.liquidCapacity;

            for (int i = 0; i < 4; i++) {
                Building neighbor = current.nearby(i);

                // Skip invalid neighbors
                if (neighbor == null || neighbor.team != team || visited.contains(neighbor)) {
                    continue;
                }

                if (neighbor.block instanceof Pipe) {
                    // Equalize with neighboring Pipe blocks
                    float neighborAmount = neighbor.liquids.get(liquid);
                    float neighborFraction = neighborAmount / neighbor.block.liquidCapacity;
                    float delta = (currentFraction - neighborFraction) / 2f;

                    if (Math.abs(delta) < 0.01f) continue; // Skip negligible differences

                    float transferAmount = Math.abs(delta) * block.liquidCapacity;

                    if (delta > 0 && neighbor.acceptLiquid(current, liquid)) {
                        current.transferLiquid(neighbor, transferAmount, liquid);
                    } else if (delta < 0 && current.acceptLiquid(neighbor, liquid)) {
                        neighbor.transferLiquid(current, transferAmount, liquid);
                    }

                    // Propagate only if there was a meaningful transfer
                    if (transferAmount > 0.01f) {
                        propagateEqualization(neighbor);
                    }
                } else if (neighbor.acceptLiquid(current, liquid)) {
                    // Dump liquid into non-Pipe blocks
                    float transferAmount = Math.min(neighbor.block.liquidCapacity - neighbor.liquids.get(liquid), currentAmount);
                    if (transferAmount > 0.01f) {
                        current.transferLiquid(neighbor, transferAmount, liquid);
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
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return (liquids.current() == liquid || liquids.currentAmount() < 0.2f);
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            // Reset tiling for current block
            tiling = 0;

            // Check nearby blocks to determine tiling
            for (int i = 0; i < 4; i++) {
                Building build = nearby(i);
                if (build != null && build.block.hasLiquids && build.team == team) {
                    tiling |= (1 << i);
                }
            }
        }
        @Override
        public void draw() {
            int r = this.rotation;
            Draw.z(Layer.blockUnder);

            for (int i = 0; i < 4; i++) {
                if ((blending & (1 << i)) != 0) {
                    int dir = r - i;
                    drawAt(
                            x + Geometry.d4x(dir) * tilesize * 0.75f,
                            y + Geometry.d4y(dir) * tilesize * 0.75f,
                            0,
                            i == 0 ? r : dir,
                            i != 0 ? SliceMode.bottom : SliceMode.top
                    );
                }
            }

            Draw.z(Layer.block);
            Draw.rect(bottomRegion, x, y);

            if (liquids().currentAmount() > 0.01f) {
                int frame = liquids.current().getAnimationFrame();
                int gas = liquids.current().gas ? 1 : 0;

                float xscl = Draw.xscl, yscl = Draw.yscl;
                Draw.scl(1f, 1f);
                Drawf.liquid(
                        liquidRegions[gas][frame],
                        x,
                        y,
                        liquids.currentAmount() / liquidCapacity,
                        liquids.current().color.write(Tmp.c1).a(1f)
                );
                Draw.scl(xscl, yscl);
            }

            Draw.rect(topRegions[tiling], x, y, tiling != 0 ? 0 : (rotdeg() + 90) % 180 - 90);
            Draw.z(Layer.blockUnder);

            drawAt(x, y, blendbits, r, SliceMode.none);
            Draw.reset();
        }

        protected void drawAt(float x, float y, int bits, int rotation, SliceMode slice) {
            int offset = yscl == -1 ? 3 : 0;
            int frame = liquids.current().getAnimationFrame();
            int gas = liquids.current().gas ? 1 : 0;
            float ox = 0f, oy = 0f;
            int wrapRot = (rotation + offset) % 4;

            TextureRegion liquidr = bits == 1 && padCorners
                    ? rotateRegions[wrapRot][gas][frame]
                    : renderer.fluidFrames[gas][frame];

            if (bits == 1 && padCorners) {
                ox = rotateOffsets[wrapRot][0];
                oy = rotateOffsets[wrapRot][1];
            }

            float xscl = Draw.xscl, yscl = Draw.yscl;
            Draw.scl(1f, 1f);
            Drawf.liquid(
                    sliced(liquidr, slice),
                    x + ox,
                    y + oy,
                    smoothLiquid,
                    liquids.current().color.write(Tmp.c1).a(1f)
            );
            Draw.scl(xscl, yscl);

            Draw.rect(sliced(topRegions[bits], slice), x, y, 0);
        }
    }
}