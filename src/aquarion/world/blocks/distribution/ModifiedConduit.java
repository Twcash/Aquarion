package aquarion.world.blocks.distribution;

import aquarion.world.graphics.AquaPuddles;
import aquarion.world.graphics.AquaShaders;
import aquarion.world.graphics.Renderer;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Puddles;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.meta.StatUnit;

import static aquarion.world.Uti.AquaStats.MaxFlow;

public class ModifiedConduit extends Conduit {
    @Override
    public void load(){
        super.load();
    }

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
        public void draw(){
            Liquid liquid = liquids.current();
            super.draw();
        }
        @Override
        public void updateTile() {
            Liquid liquid = liquids.current();
            smoothLiquid = Mathf.lerpDelta(smoothLiquid, liquids.currentAmount() / liquidCapacity, 0.05f);

            if (liquids.currentAmount() > 0.0001f && timer(timerFlow, 1)) {
                moveLiqFor(leaks, liquids.current());
                noSleep();
            } else {
                sleep();
            }

            if (liquids.currentAmount() > 0.1f && liquid.temperature > 0.5f) {
                damageContinuous(liquid.temperature / 100f);
                if (Mathf.chanceDelta(0.01)) {
                    Fx.steam.at(x, y);
                }
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

            if (next.team == team && next.block.hasLiquids && liquids.get(liquid) > 0f) {
                float ofract = next.liquids.get(liquid) / next.block.liquidCapacity;
                float fract = liquids.get(liquid) / block.liquidCapacity * block.liquidPressure;
                float flow = Math.min(Mathf.clamp((fract - ofract)) * (block.liquidCapacity), liquids.get(liquid));
                flow = Math.min(flow, next.block.liquidCapacity - next.liquids.get(liquid));

                if(flow > 0f && ofract <= fract && next.acceptLiquid(self(), liquid)){
                    next.handleLiquid(self(), liquid, flow);
                    liquids.remove(liquid, flow);
                    return flow;
                } else if (!next.block.consumesLiquid(liquid) && next.liquids.currentAmount() / next.block.liquidCapacity > 0.1f && fract > 0.1f) {
                    float fx = (x + next.x) / 2f, fy = (y + next.y) / 2f;
                    Liquid other = next.liquids.current();
                    if (other.blockReactive && liquid.blockReactive) {
                        if ((other.flammability > 0.3f && liquid.temperature > 0.7f) || (liquid.flammability > 0.3f && other.temperature > 0.7f)) {
                            damageContinuous(liquid.temperature / 100f);
                            next.damageContinuous(liquid.temperature / 100f);
                            if (Mathf.chanceDelta(0.01)) {
                                Fx.steam.at(fx, fy);
                            }
                        } else if ((liquid.temperature > 0.7f && other.temperature < 0.55f) || (other.temperature > 0.7f && liquid.temperature < 0.55f)) {
                            liquids.remove(liquid, Math.min(liquids.get(liquid), 0.7f * Time.delta));
                            if (Mathf.chanceDelta(0.2f)) {
                                Fx.steam.at(fx, fy);
                            }
                        }
                    }
                }
            }
            return 0;
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            int[] bits = buildBlending(tile, rotation, null, true);
            blendbits = bits[0];
            xscl = bits[1];
            yscl = bits[2];
            blending = bits[4];

            Building next = front(), prev = back();
            capped = next == null || next.team != team || !next.block.hasLiquids;
            backCapped = blendbits == 0 && (prev == null || prev.team != team || !prev.block.hasLiquids);
        }
        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            noSleep();
            return (liquids.current() == liquid || liquids.currentAmount() < 0.2f)
                    && (tile == null || source == this || (source.relativeTo(tile.x, tile.y) + 2) % 4 != rotation);
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
