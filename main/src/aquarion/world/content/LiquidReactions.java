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
        LiquidModule liquids = build.liquids;

        Seq<Liquid> present = new Seq<>();
        liquids.each((liquid, amount) -> {
            if(amount > 0.1f) present.add(liquid);
        });
        if(present.size < 2) return false;

        boolean reacted = false;
        for(int i = 0; i < present.size; i++){
            for(int j = i + 1; j < present.size; j++){
                LiquidReaction r = find(present.get(i), present.get(j));
                if(r != null && apply(liquids, build, r)){
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
            if(consume <= 0.001f) return;

            source.liquids.remove(moving, consume * scaleMoving);
            next.liquids.remove(other, consume * scaleOther);

            if(r.result != null && r.resultAmount > 0f){
                Building dest = LiquidUtil.freeSpace(next) > LiquidUtil.freeSpace(source) ? next : source;
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

    private static boolean apply(LiquidModule liquids, Building build, LiquidReaction r){
        float amtA = liquids.get(r.a);
        float amtB = liquids.get(r.b);
        if(amtA <= 0.01f || amtB <= 0.01f) return false;

        float step = r.rate * Time.delta;
        float consume = Math.min(step, Math.min(amtA / r.aAmount, amtB / r.bAmount));
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
