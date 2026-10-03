package aquarion.world.AI;

/**
 * The tactical roles a Gerb unit can be in. The brain AI swaps the unit's
 * controller to match the current role; state itself lives in {@code GerbComp}
 * so it survives the controller swap.
 *
 * <p>Deliberately lives outside the {@code entities.comp} package: that whole
 * package is stripped from the built mod jar (components are compile-time only),
 * but this enum is referenced at runtime by the entity and the AIs.</p>
 */
public enum GerbRole {
    ASSAULT, REINFORCE, COVER, FLANK, HOLD
}