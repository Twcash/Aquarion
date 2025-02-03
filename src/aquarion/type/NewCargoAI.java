package aquarion.type;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.units.UnitCargoUnloadPoint.*;
import mindustry.world.meta.*;

@SuppressWarnings({"unused"})
public class NewCargoAI extends AIController {
    static Seq<UnitCargoUnloadPointBuild> targets = new Seq<>();

    public static float emptyWaitTime = 60f * 2f, dropSpacing = 60f * 1.5f;
    public static float transferRange = 20f, moveRange = 6f, moveSmoothing = 20f;

    public @Nullable UnitCargoUnloadPointBuild unloadTarget;
    public @Nullable Item itemTarget;
    public float noDestTimer = 0f;
    public int targetIndex = 0;

    @Override
    public void updateMovement() {
        if (!(unit instanceof BuildingTetherc tether) || tether.building() == null) return;

        var build = tether.building();

        if (build.items == null) return;

        // empty, approach the loader, even if there's nothing to pick up (units hanging around doing nothing looks bad)
        if (!unit.hasItem()) {
            moveTo(build, moveRange, moveSmoothing);

            // check if ready to pick up
            if (build.items.any() && unit.within(build, transferRange)) {
                if (retarget()) {
                    findAnyTarget(build);

                    // target has been found, grab items and go
                    if (unloadTarget != null) {
                        Call.takeItems(build, itemTarget, Math.min(unit.type.itemCapacity, build.items.get(itemTarget)), unit);
                    }
                }
            }
        } else { // the unit has an item, deposit it somewhere.

            // there may be no current target, try to find one
            if (unloadTarget == null) {
                if (retarget()) {
                    findDropTarget(unit.item(), 0, null);

                    // if there is not even a single place to unload, dump items.
                    if (unloadTarget == null) {
                        unit.clearItem();
                    }
                }
            } else {

                // what if some prankster reconfigures or picks up the target while the unit is moving? we can't have that!
                if (unloadTarget.item != itemTarget || unloadTarget.isPayload()) {
                    unloadTarget = null;
                    return;
                }

                moveTo(unloadTarget, moveRange, moveSmoothing);

                // deposit in bursts, unloading can take a while
                if (unit.within(unloadTarget, transferRange) && timer.get(timerTarget2, dropSpacing)) {
                    int max = unloadTarget.acceptStack(unit.item(), unit.stack.amount, unit);

                    // deposit items when it's possible
                    if (max > 0) {
                        noDestTimer = 0f;
                        Call.transferItemTo(unit, unit.item(), max, unit.x, unit.y, unloadTarget);

                        // try the next target later
                        if (!unit.hasItem()) {
                            targetIndex++;
                        }
                    } else if ((noDestTimer += dropSpacing) >= emptyWaitTime) {
                        // oh no, it's out of space - wait for a while, and if nothing changes, try the next destination

                        // next targeting attempt will try the next destination point
                        targetIndex = findDropTarget(unit.item(), targetIndex, unloadTarget) + 1;

                        // nothing found at all, clear item
                        if (unloadTarget == null) {
                            unit.clearItem();
                        }
                    }
                }
            }
        }
    }

    /** find target for the unit's current item */
    @SuppressWarnings({"unchecked", "rawtypes", "unused"})
    public int findDropTarget(Item item, int offset, UnitCargoUnloadPointBuild ignore) {
        unloadTarget = null;
        itemTarget = item;

        // get all flagged cargo unload points and filter
        targets.selectFrom((Seq<UnitCargoUnloadPointBuild>) (Seq) Vars.indexer.getFlagged(unit.team, BlockFlag.unitCargoUnloadPoint), u -> u.item == item);

        // hardcoded bc I dummy
        targets.retainAll(t -> t.items.total() < 100);

        if (targets.isEmpty()) return 0;

        // sort by most distant with least items first
        targets.sort(Structs.comps(Structs.comparingInt(b -> b.items.total()), Structs.comparingFloat(b -> -b.dst2(unit))));

        offset %= targets.size;
        unloadTarget = targets.get(offset);

        targets.clear();
        return offset;
    }

    @SuppressWarnings({"unchecked", "rawtypes", "unused"})
    public void findAnyTarget(Building build) {
        unloadTarget = null;
        itemTarget = null;

        Seq<UnitCargoUnloadPointBuild> baseTargets = (Seq<UnitCargoUnloadPointBuild>) (Seq) Vars.indexer.getFlagged(unit.team, BlockFlag.unitCargoUnloadPoint);

        if (baseTargets.isEmpty()) return;

        // sort targets based on fewest items and furthest distance
        baseTargets.sort(Structs.comps(Structs.comparingInt(t -> t.items.total()), Structs.comparingFloat(t -> -t.dst2(unit))));

        for (var target : baseTargets) {
            if (!target.stale) {
                unloadTarget = target;
                break;
            }
        }

        targets.clear();
    }
}