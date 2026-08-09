package aquarion.world.blocks.distribution;

import aquarion.world.content.LiquidReactions;
import aquarion.world.content.LiquidUtil;
import aquarion.world.graphics.AquaPuddles;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Nullable;
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
    public class ModifiedConduitBuild extends ConduitBuild {
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

            Draw.rect(sliced(topRegions[bits], slice), x, y, rotation * 90f);
        }

        @Override
        public void updateTile() {
            smoothLiquid = Mathf.lerpDelta(smoothLiquid, LiquidUtil.total(liquids) / liquidCapacity, 0.05f);

            //reactions between mixed liquids
            LiquidReactions.react(self());

            if (LiquidUtil.total(liquids) > 0.0001f && timer(timerFlow, 1)) {
                liquids.each((liquid, amount) -> {
                    if (amount > 0.0001f) {
                        moveLiqFor(leaks, liquid);
                    }
                });
                noSleep();
            } else {
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
                float levelHere = liquids.get(liquid) / block.liquidCapacity;
                float levelNext = next.liquids.get(liquid) / next.block.liquidCapacity;
                float deltaLevel = Math.max(levelHere - levelNext, 0f) * 50;

                float rho = 1f;
                float viscosityFactor = Mathf.clamp(1f - liquid.viscosity * 0.5f, 0.2f, 1f);
                float Cd = 0.8f;
                float A = 1f;

                float flow = Cd * A * Mathf.sqrt(2f * deltaLevel / rho) * viscosityFactor;

                flow *= 10f;

                flow = Math.min(flow, liquids.get(liquid));
                flow = Math.min(flow, LiquidUtil.freeSpace(next));

                if (flow > 0f && next.acceptLiquid(self(), liquid)) {
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
