package aquarion.world.units;

import aquarion.world.AI.RailCommandAI;
import aquarion.units.type.RailUnitType;
import arc.struct.Seq;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.Item;

/** Ordered ordinary units, persisted through native unit flags. */
public class RailConsist{
    private static final double legacy = 1_000_000_000_000d, marker = 2_000_000_000_000d;
    // Keep the ordered membership exact in a double, with room for a million cars per train.
    private static final long stride = 1_000_000L;
    private static long nextId;

    private static long code(Unit unit){
        return unit != null && unit.type instanceof RailUnitType && unit.flag >= legacy && unit.flag < 9_000_000_000_000_000d
            ? (long)(unit.flag - (unit.flag >= marker ? marker : legacy)) : -1L;
    }

    private static long group(Unit unit){ return code(unit) / (unit.flag >= marker ? stride : 2L); }
    private static long rank(Unit unit){ return code(unit) % (unit.flag >= marker ? stride : 2L); }

    public static Seq<Unit> members(Unit unit){
        Seq<Unit> result = new Seq<>();
        if(code(unit) >= 0L && Groups.unit != null){
            for(Unit other : Groups.unit){
                if(!other.dead && other.team == unit.team && code(other) >= 0L
                    && (other.flag >= marker) == (unit.flag >= marker) && group(other) == group(unit)) result.add(other);
            }
        }
        if(!result.contains(unit, true)) result.add(unit);
        result.sort((a, b) -> Long.compare(rank(a), rank(b)));
        return result;
    }

    public static Unit leader(Unit unit){ return members(unit).first(); }

    /** The tail for a leader, or the leader for a follower. */
    public static Unit partner(Unit unit){
        Seq<Unit> all = members(unit);
        return all.size < 2 ? null : all.first() == unit ? all.peek() : all.first();
    }

    public static boolean linked(Unit unit){ return members(unit).size > 1; }

    public static boolean same(Unit a, Unit b){
        return a == b || (a != null && b != null && code(a) >= 0L && code(b) >= 0L && a.team == b.team
            && (a.flag >= marker) == (b.flag >= marker) && group(a) == group(b));
    }

    public static float offset(Unit unit){
        Seq<Unit> all = members(unit);
        if(all.first() == unit) return 0f;
        float distance = 0f;
        for(int i = 1; i < all.size; i++){
            distance += RailTraffic.clearance(all.get(i - 1), all.get(i)) + 0.5f;
            if(all.get(i) == unit) return distance;
        }
        return 0f;
    }

    public static boolean link(Unit front, Unit rear){
        if(front.dead || rear.dead || same(front, rear) || front.team != rear.team
            || !(front.type instanceof RailUnitType) || !(rear.type instanceof RailUnitType)) return false;
        Seq<Unit> all = members(front);
        all.addAll(members(rear));
        for(Unit unit : Groups.unit) if(code(unit) >= 0L) nextId = Math.max(nextId, group(unit));
        long id = ++nextId;
        for(int i = 0; i < all.size; i++) all.get(i).flag = marker + id * stride + i;
        if(all.first().controller() instanceof RailCommandAI ai){
            ai.seedConsist(all.peek());
            ai.alignConsistDirection();
        }
        return true;
    }

    public static void promote(Unit front){
        Seq<Unit> all = members(front);
        Unit previous = all.first();
        if(front == previous || front != all.peek()) return;
        all.reverse();
        long id = group(front);
        for(int i = 0; i < all.size; i++) all.get(i).flag = marker + id * stride + i;
        if(front.controller() instanceof RailCommandAI ai && previous.controller() instanceof RailCommandAI old){
            ai.adoptConsistOrder(old);
            ai.seedConsist(all.peek());
        }
    }

    public static int amount(Unit unit){
        int amount = 0;
        for(Unit member : members(unit)) amount += member.stack.amount;
        return amount;
    }

    public static int amount(Unit unit, Item item){
        int amount = 0;
        for(Unit member : members(unit)) if(member.stack.item == item) amount += member.stack.amount;
        return amount;
    }

    public static int capacity(Unit unit){
        int capacity = 0;
        for(Unit member : members(unit)) capacity += member.type.itemCapacity;
        return capacity;
    }

    public static Item item(Unit unit){
        for(Unit member : members(unit)) if(member.stack.amount > 0) return member.stack.item;
        return null;
    }

    public static void add(Unit unit, Item item, int amount){
        for(Unit member : members(unit)){
            if(member.stack.amount > 0 && member.stack.item != item) continue;
            int added = Math.min(amount, member.type.itemCapacity - member.stack.amount);
            if(added > 0) member.addItem(item, added);
            amount -= added;
        }
    }

    public static void remove(Unit unit, int amount){
        Item item = item(unit);
        for(Unit member : members(unit)){
            if(member.stack.item != item) continue;
            int removed = Math.min(amount, member.stack.amount);
            member.stack.amount -= removed;
            amount -= removed;
            if(member.stack.amount == 0) member.clearItem();
        }
    }
}



