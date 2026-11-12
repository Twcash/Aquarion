package aquarion.world.consumers;

import aquarion.world.items.AquaItemEntity;
import arc.scene.ui.layout.Table;
import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.world.consumers.ConsumeItems;

/** Requires each input unit to match its item ID and every required tag. */
public class ConsumeTaggedItems extends ConsumeItems {
    public final String[] requiredTags;

    public ConsumeTaggedItems(ItemStack[] items, String... requiredTags) {
        super(items);
        this.requiredTags = requiredTags.clone();
        for(int i = 0; i < items.length; i++) {
            if(items[i].amount <= 0) throw new IllegalArgumentException("Input amount must be positive");
            for(int j = 0; j < i; j++) {
                if(items[i].item == items[j].item) throw new IllegalArgumentException("Combine duplicate item inputs");
            }
        }
    }

    protected boolean matches(AquaItemEntity entity, ItemStack stack) {
        if(entity.item != stack.item) return false;
        for(String tag : requiredTags) if(!entity.hasTag(tag)) return false;
        return true;
    }

    @Override
    public float efficiency(Building build) {
        if(!(build instanceof AquaItemEntity.Container container)) return 0f;
        AquaItemEntity.reconcile(build.items, container.aquaItemEntities());
        for(ItemStack stack : items) {
            int amount = Math.round(stack.amount * multiplier.get(build));
            int count = container.aquaItemEntities().count(e -> matches(e, stack));
            // Ordinary units can expose shared content tags without needing a ledger entry.
            AquaItemEntity ordinary = AquaItemEntity.create(stack.item);
            if(matches(ordinary, stack)) {
                count += build.items.get(stack.item) - container.aquaItemEntities().count(e -> e.item == stack.item);
            }
            if(count < amount) return 0f;
        }
        return 1f;
    }

    @Override
    public void trigger(Building build) {
        if(efficiency(build) == 0f) return;
        AquaItemEntity.Container container = (AquaItemEntity.Container)build;
        for(ItemStack stack : items) {
            int amount = Math.round(stack.amount * multiplier.get(build));
            for(int i = 0; i < amount; i++) {
                AquaItemEntity entity = container.aquaItemEntities().find(e -> matches(e, stack));
                if(entity != null) container.aquaItemEntities().remove(entity, true);
            }
            build.items.remove(stack.item, amount);
        }
    }

    @Override
    public void build(Building build, Table table) {
        table.label(() -> String.join(", ", requiredTags));
        table.row();
        for(ItemStack stack : items) {
            table.add(new mindustry.ui.ReqImage(
                mindustry.world.meta.StatValues.stack(stack.item, Math.round(stack.amount * multiplier.get(build))),
                () -> efficiency(build) > 0f)).padRight(8);
        }
    }
}
