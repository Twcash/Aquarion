package aquarion.world.content;

import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.modules.LiquidModule;

/**
 * Registry of {@link LiquidReaction}s, plus the logic that runs them inside
 * siphon-family blocks and at the boundary between two blocks.
 */
public class LiquidReactions {
    private static final Seq<LiquidReaction> all = new Seq<>();

    public static void register(LiquidReaction reaction){
        all.add(reaction);
    }

    public static @Nullable LiquidReaction find(Liquid a, Liquid b){
        for(int i = 0; i < all.size; i++){
            LiquidReaction r = all.get(i);
            if((r.a == a && r.b == b) || (r.a == b && r.b == a)) return r;
        }
        return null;
    }

    public static boolean react(Building build){
        if(build.liquids == null) return false;
        return react(build.liquids, build, build.block.liquidCapacity);
    }

    /** Runs reactions between the liquids stored in a single liquid module (e.g. one side of a junction). */
    public static boolean react(LiquidModule liquids, Building build){
        return react(liquids, build, build == null ? Float.MAX_VALUE : build.block.liquidCapacity);
    }

    /**
     * Runs reactions between the liquids stored in a single liquid module with the given
     * capacity, so blocks with per-side buffers (junctions, sorters) react per side and
     * products can never push the module over its real capacity.
     */
    public static boolean react(LiquidModule liquids, Building build, float capacity){
        if(liquids == null) return false;

        Seq<Liquid> present = new Seq<>();
        liquids.each((liquid, amount) -> {
            if(amount > 0.1f) present.add(liquid);
        });
        if(present.size < 2) return false;

        boolean reacted = false;
        for(int i = 0; i < present.size; i++){
            for(int j = i + 1; j < present.size; j++){
                LiquidReaction r = find(present.get(i), present.get(j));
                if(r != null && apply(liquids, build, r, capacity)){
                    reacted = true;
                }
            }
        }
        return reacted;
    }

    /**
     * Runs a reaction between a liquid being pushed from {@code source} and the
     * contents of {@code next}, used when the transfer is blocked (e.g. the next
     * block is full of a different liquid).
     */
    public static void reactAtBoundary(Building source, Liquid moving, Building next){
        if(source.liquids == null || next.liquids == null) return;
        float amtMoving = source.liquids.get(moving);
        if(amtMoving <= 0.1f) return;

        next.liquids.each((other, amtOther) -> {
            if(other == moving || amtOther <= 0.1f) return;

            LiquidReaction r = find(moving, other);
            if(r == null) return;

            //figure out which side each reactant is on
            boolean movingIsA = r.a == moving;
            float scaleMoving = movingIsA ? r.aAmount : r.bAmount;
            float scaleOther = movingIsA ? r.bAmount : r.aAmount;

            float step = r.rate * Time.delta;
            float consume = Math.min(step, Math.min(amtMoving / scaleMoving, amtOther / scaleOther));

            Building dest = null;
            if(r.result != null && r.resultAmount > 0f){
                dest = boundaryDest(source, next);
                if(dest.liquids == null) return;

                //limit the reaction to what the destination can hold, so it never exceeds its capacity
                float scaleDest = dest == source ? scaleMoving : scaleOther;
                float net = r.resultAmount - scaleDest;
                if(net > 0f){
                    float headroom = Math.max(LiquidUtil.freeSpace(dest), 0f);
                    consume = Math.min(consume, headroom / net);
                }
            }

            if(consume <= 0.001f) return;

            source.liquids.remove(moving, consume * scaleMoving);
            next.liquids.remove(other, consume * scaleOther);

            if(dest != null){
                dest.liquids.add(r.result, consume * r.resultAmount);
            }

            if(r.damage > 0f){
                source.damageContinuous(r.damage);
                next.damageContinuous(r.damage);
            }

            if(r.effect != null && Mathf.chanceDelta(r.effectChance)){
                r.effect.at((source.x + next.x) / 2f, (source.y + next.y) / 2f, 0f);
            }
        });
    }

    /**
     * Where a boundary reaction's product goes. Side-buffered blocks (junctions, sorters) keep
     * their main liquid module unused, so product stored there would be stuck forever; they are
     * never picked as the destination.
     */
    private static Building boundaryDest(Building source, Building next){
        boolean nextBuffered = LiquidUtil.isSideBuffered(next);
        boolean sourceBuffered = LiquidUtil.isSideBuffered(source);
        if(nextBuffered && !sourceBuffered) return source;
        if(sourceBuffered && !nextBuffered) return next;
        return LiquidUtil.freeSpace(next) > LiquidUtil.freeSpace(source) ? next : source;
    }

    private static boolean apply(LiquidModule liquids, Building build, LiquidReaction r, float capacity){
        float amtA = liquids.get(r.a);
        float amtB = liquids.get(r.b);
        if(amtA <= 0.01f || amtB <= 0.01f) return false;

        float step = r.rate * Time.delta;
        float consume = Math.min(step, Math.min(amtA / r.aAmount, amtB / r.bAmount));

        //reactions that produce more than they consume stall while the container is full,
        //so they can never push it over capacity
        if(r.result != null && r.resultAmount > 0f){
            float net = r.resultAmount - (r.aAmount + r.bAmount);
            if(net > 0f){
                float headroom = Math.max(capacity - LiquidUtil.total(liquids), 0f);
                consume = Math.min(consume, headroom / net);
            }
        }

        if(consume <= 0.001f) return false;

        liquids.remove(r.a, consume * r.aAmount);
        liquids.remove(r.b, consume * r.bAmount);

        if(r.result != null && r.resultAmount > 0f){
            liquids.add(r.result, consume * r.resultAmount);
        }

        if(r.damage > 0f){
            build.damageContinuous(r.damage);
        }

        if(r.effect != null && Mathf.chanceDelta(r.effectChance)){
            r.effect.at(build.x, build.y, 0f);
        }

        return true;
    }
}
