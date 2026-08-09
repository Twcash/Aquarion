package aquarion.world.content;

import arc.util.Nullable;
import mindustry.entities.Effect;
import mindustry.type.Liquid;

/**
 * Defines a reaction between two liquids. When both reactants are present in a
 * siphon-family block, they are consumed at {@link #rate} units per tick (scaled
 * by whichever reactant runs out first) to produce {@link #result}, optionally
 * damaging the containing block.
 */
public class LiquidReaction {
    /** First reactant. */
    public Liquid a;
    /** Second reactant. */
    public Liquid b;
    /** Result liquid produced by this reaction, or null if none. */
    public @Nullable Liquid result;
    /** Units of reactant {@link #a} consumed per reaction unit. */
    public float aAmount = 1f;
    /** Units of reactant {@link #b} consumed per reaction unit. */
    public float bAmount = 1f;
    /** Units of {@link #result} produced per reaction unit. */
    public float resultAmount = 1f;
    /** Reaction speed, in reaction units per tick. */
    public float rate = 0.5f;
    /** Damage applied to the containing block per tick while reacting. */
    public float damage = 0f;
    /** Visual effect shown while reacting, or null for none. */
    public @Nullable Effect effect;
    /** Chance per tick of the effect being shown. */
    public float effectChance = 0.05f;

    public LiquidReaction(Liquid a, Liquid b, @Nullable Liquid result){
        this.a = a;
        this.b = b;
        this.result = result;
    }
}
