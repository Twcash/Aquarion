package aquarion.world.items;

import aquarion.world.content.RadiationItem;
import arc.struct.Seq;
import java.util.Arrays;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.modules.ItemModule;

public class AquaItemEntity {
    public static AquaItemEntity create(Item item) {
        return item instanceof aquarion.world.content.AquaItem aqua ? aqua.createBuild() : new AquaItemEntity(item);
    }

    public boolean tracked() {
        return data != 0 || rule(item, data) != null;
    }

    public boolean hasTag(String tag) {
        return false;
    }

    public int writeState() { return data; }
    public void readState(int state) { data = state; }

    /** Create the resulting unit after update changes this unit's item. */
    public AquaItemEntity replacement() {
        return create(item);
    }

    public void draw(float x, float y, float size) {
        draw(item, data, age, x, y, size);
    }
    private static final Seq<SpoilRule> spoilRules = new Seq<>();
    private static final Seq<FreshTransfer> freshTransfers = new Seq<>();
    private static final Seq<Transfer> transfers = new Seq<>();
    private static int[] reconcileCounts = new int[0];

    public Item item;
    /** Generic, saveable per-unit state for mod-specific uses (for example enrichment level). */
    public int data;
    public float age;

    public AquaItemEntity(Item item) {
        this(item, 0, 0f);
    }

    public AquaItemEntity(Item item, int data, float age) {
        this.item = item;
        this.data = data;
        this.age = age;
    }

    public static void registerSpoil(Item from, Item to, float ticks) {
        registerSpoil(from, to, ticks, -1);
    }

    public static void registerSpoil(Item from, Item to, float ticks, int requiredData) {
        if(from == null || to == null || ticks <= 0f) throw new IllegalArgumentException("Invalid item spoil rule");
        spoilRules.remove(s -> s.from == from && s.requiredData == requiredData);
        spoilRules.add(new SpoilRule(from, to, ticks, requiredData));
    }

    public static void removeSpoil(Item from) {
        spoilRules.remove(s -> s.from == from);
    }

    public boolean update(float delta) {
        SpoilRule rule = rule(item, data);
        if(rule == null) return false;
        age += Math.max(0f, delta);
        if(age < rule.ticks) return false;
        item = rule.to;
        data = 0;
        age = 0f;
        return true;
    }

    private static @Nullable SpoilRule rule(Item item, int data) {
        return spoilRules.find(s -> s.from == item && (s.requiredData < 0 || s.requiredData == data));
    }

    public static TextureRegion icon(Item item, int data) {
        return item.fullIcon;
    }
    public static void draw(Seq<AquaItemEntity> entities, Item item, float x, float y, float size) {
        AquaItemEntity entity = entities.find(e -> e.item == item);
        if(entity != null) entity.draw(x, y, size);
        else Draw.rect(item.fullIcon, x, y, size, size);
    }
    public static void draw(Item item, int data, float age, float x, float y, float size) {
        SpoilRule spoil = rule(item, data);
        if(spoil == null) {
            Draw.rect(icon(item, data), x, y, size, size);
            return;
        }

        float progress = Mathf.clamp(age / spoil.ticks);
        if(progress <= 0f) {
            Draw.rect(icon(item, data), x, y, size, size);
        } else {
            Draw.color(Color.white);
            Draw.alpha(1f - progress);
            Draw.rect(icon(item, data), x, y, size, size);
            Draw.alpha(progress);
            Draw.rect(spoil.to.fullIcon, x, y, size, size);
            Draw.reset();
        }
    }

    private static class SpoilRule {
        final Item from, to;
        final float ticks;
        final int requiredData;

        SpoilRule(Item from, Item to, float ticks, int requiredData) {
            this.from = from;
            this.to = to;
            this.ticks = ticks;
            this.requiredData = requiredData;
        }
    }

    private static class FreshTransfer {
        final Building source;
        final Item item;
        final AquaItemEntity entity;
        boolean received;

        FreshTransfer(Building source, Item item, int data) {
            this.source = source;
            this.item = item;
            this.entity = create(item);
            this.entity.readState(data);
        }
    }

    public static class Transfer {
        public final AquaItemEntity entity;
        private final Building source;
        private final Item item;
        private boolean received;

        private Transfer(Building source, Item item, AquaItemEntity entity) {
            this.source = source;
            this.item = item;
            this.entity = entity;
        }

        public boolean received() {
            return received;
        }
    }

    /** Implement on a Building whose items should retain per-unit state. */
    public interface Container {
        Seq<AquaItemEntity> aquaItemEntities();
    }

    /** Enrich one ordinary unit, including units not yet in the sparse ledger. */
    public static boolean enrich(Building build, Item item) {
        if(build.items == null || !(build instanceof Container container)) return false;
        Seq<AquaItemEntity> entities = container.aquaItemEntities();
        reconcile(build.items, entities);
        for(AquaItemEntity entity : entities) {
            if(entity.item == item && entity instanceof RadiationItem.RadiationComp thorium && !thorium.enriched) {
                thorium.enrich();
                return true;
            }
        }
        return false;
    }

    /** Receive an item while inheriting its per-unit state from another aware building if possible. */
    public static void receive(Building receiver, Building source, Item item, ItemModule inventory,
                               Seq<AquaItemEntity> entities) {
        AquaItemEntity entity = capture(source, item);
        // The ledger follows the ItemModule count, but the stock module remains authoritative.
        inventory.add(item, 1);
        addIfTracked(entities, entity);
    }

    /** Keep only items with active decay rules or explicit metadata in the per-unit ledger. */
    public static void addIfTracked(Seq<AquaItemEntity> entities, AquaItemEntity entity) {
        if(entity != null && entity.tracked()) entities.add(entity);
    }

    /** Take an item's state from the active handoff or an aware source building. */
    public static AquaItemEntity capture(Building source, Item item) {
        AquaItemEntity entity = takeFreshTransfer(source, item);
        if(entity == null) entity = takePending(source, item);
        if(entity == null && source instanceof Container container) entity = take(container.aquaItemEntities(), item);
        return entity == null ? create(item) : entity;
    }

    /** Temporarily associate an incoming item with its source while a block applies routing logic. */
    public static Transfer beginTransfer(Building source, Building receiver, Item item) {
        AquaItemEntity entity = capture(source, item);
        Transfer transfer = new Transfer(receiver, item, entity);
        transfers.add(transfer);
        return transfer;
    }

    /** Forward an already captured unit, exposing its state to the receiver's normal item hook. */
    public static void forward(Building source, Building receiver, Item item, AquaItemEntity entity) {
        Transfer transfer = new Transfer(source, item, entity);
        transfers.add(transfer);
        try {
            receiver.handleItem(source, item);
        } finally {
            transfers.remove(transfer, true);
        }
    }

    /** Advance age while an item is waiting in a transport buffer without transforming its item ID. */
    public static void advance(Seq<AquaItemEntity> entities, float delta) {
        float elapsed = Math.max(0f, delta);
        for(AquaItemEntity entity : entities) entity.age += elapsed;
    }

    /** Remove the temporary routing association after the block's item hook returns. */
    public static void endTransfer(Transfer transfer) {
        transfers.remove(transfer, true);
    }

    private static @Nullable AquaItemEntity takePending(Building source, Item item) {
        for(int i = transfers.size - 1; i >= 0; i--) {
            Transfer transfer = transfers.get(i);
            if(!transfer.received && transfer.source == source && transfer.item == item) {
                transfer.received = true;
                return transfer.entity;
            }
        }
        return null;
    }

    /** Route a newly produced item through the usual receiver hook without inheriting input state. */
    public static void handleProduced(Building source, Building receiver, Item item) {
        handleProduced(source, receiver, item, 0);
    }

    /** Route a produced per-unit item with an explicit saved data value. */
    public static void handleProduced(Building source, Building receiver, Item item, int data) {
        freshTransfers.add(new FreshTransfer(source, item, data));
        try {
            receiver.handleItem(source, item);
        } finally {
            freshTransfers.remove(freshTransfers.size - 1);
        }
    }

    private static @Nullable AquaItemEntity takeFreshTransfer(Building source, Item item) {
        for(int i = freshTransfers.size - 1; i >= 0; i--) {
            FreshTransfer transfer = freshTransfers.get(i);
            if(!transfer.received && transfer.source == source && transfer.item == item) {
                transfer.received = true;
                return transfer.entity;
            }
        }
        return null;
    }

    /** Update and reconcile against an inventory, transforming item counts when spoil occurs. */
    public static void update(Seq<AquaItemEntity> entities, ItemModule inventory, float delta) {
        reconcile(inventory, entities);
        for(int i = 0; i < entities.size; i++) {
            AquaItemEntity entity = entities.get(i);
            Item before = entity.item;
            if(entity.update(delta)) {
                inventory.remove(before, 1);
                inventory.add(entity.item, 1);
                entities.set(i, entity.replacement());
            }
        }
        reconcile(inventory, entities);
    }

    /** Pull the oldest matching unit, used when an aware destination receives a transferred item. */
    public static @Nullable AquaItemEntity take(Seq<AquaItemEntity> entities, Item item) {
        for(int i = 0; i < entities.size; i++) {
            AquaItemEntity entity = entities.get(i);
            if(entity.item == item) return entities.remove(i);
        }
        return null;
    }

    /** Reconcile direct consumption, production, stack removal, and other standard Mindustry APIs. */
    public static void reconcile(ItemModule inventory, Seq<AquaItemEntity> entities) {
        if(reconcileCounts.length < Vars.content.items().size) {
            reconcileCounts = new int[Vars.content.items().size];
        }
        Arrays.fill(reconcileCounts, 0);
        for(AquaItemEntity entity : entities) {
            if(entity.item != null) reconcileCounts[entity.item.id]++;
        }

        for(int i = entities.size - 1; i >= 0; i--) {
            AquaItemEntity entity = entities.get(i);
            if(entity.item == null || inventory.get(entity.item) < reconcileCounts[entity.item.id] ||
                !entity.tracked()) {
                if(entity.item != null) reconcileCounts[entity.item.id]--;
                entities.remove(i);
            }
        }
        // Nested item builds own their tracking policy, including ordinary thorium units.
        for(Item item : Vars.content.items()) {
            if(!(item instanceof aquarion.world.content.AquaItem)) continue;
            if(inventory.get(item) <= reconcileCounts[item.id]) continue;
            AquaItemEntity prototype = create(item);
            if(!prototype.tracked()) continue;
            int missing = inventory.get(item) - reconcileCounts[item.id];
            for(int i = 0; i < missing; i++) entities.add(create(item));
            reconcileCounts[item.id] += Math.max(0, missing);
        }
        for(int i = 0; i < spoilRules.size; i++) {
            Item item = spoilRules.get(i).from;
            boolean seen = false;
            for(int j = 0; j < i; j++) {
                if(spoilRules.get(j).from == item) {
                    seen = true;
                    break;
                }
            }
            if(seen) continue;
            if(rule(item, 0) == null) continue;
            int missing = inventory.get(item) - reconcileCounts[item.id];
            for(int amountIndex = 0; amountIndex < missing; amountIndex++) {
                entities.add(create(item));
                reconcileCounts[item.id]++;
            }
        }
    }

    public static void write(Writes write, Seq<AquaItemEntity> entities) {
        write.i(entities.size);
        for(AquaItemEntity entity : entities) {
            write.s(entity.item.id);
            write.i(entity.writeState());
            write.f(entity.age);
        }
    }

    public static void read(Reads read, Seq<AquaItemEntity> entities) {
        entities.clear();
        int count = read.i();
        for(int i = 0; i < count; i++) {
            Item item = Vars.content.item(read.s());
            int data = read.i();
            float age = read.f();
            if(item != null) {
                AquaItemEntity entity = create(item);
                entity.readState(data);
                entity.age = age;
                entities.add(entity);
            }
        }
    }
}
