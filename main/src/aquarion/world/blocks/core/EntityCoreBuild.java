package aquarion.world.blocks.core;

import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import aquarion.world.items.AquaItemEntity;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.storage.CoreBlock;

/** CoreBuild variant that retains per-unit state for items held in a core inventory. */
public class EntityCoreBuild extends CoreBlock.CoreBuild implements AquaItemEntity.Container {
    private final Seq<AquaItemEntity> trackedItems = new Seq<>();

    public EntityCoreBuild(CoreBlock owner) {
        owner.super();
    }

    public static void install(CoreBlock block) {
        block.buildType = () -> new EntityCoreBuild(block);
    }

    @Override
    public Seq<AquaItemEntity> aquaItemEntities() {
        return trackedItems;
    }

    @Override
    public void handleItem(Building source, Item item) {
        int before = items.get(item);
        super.handleItem(source, item);
        if(items.get(item) > before) AquaItemEntity.addIfTracked(trackedItems, AquaItemEntity.capture(source, item));
    }

    @Override
    public void updateTile() {
        AquaItemEntity.update(trackedItems, items, edelta());
        super.updateTile();
    }

    @Override
    public int removeStack(Item item, int amount) {
        int removed = super.removeStack(item, amount);
        for(int i = 0; i < removed; i++) AquaItemEntity.take(trackedItems, item);
        return removed;
    }

    @Override
    public byte version() {
        return (byte)(super.version() + 1);
    }

    @Override
    public void write(Writes write) {
        super.write(write);
        AquaItemEntity.write(write, trackedItems);
    }

    @Override
    public void read(Reads read, byte revision) {
        super.read(read, revision);
        if(revision > super.version()) AquaItemEntity.read(read, trackedItems);
        AquaItemEntity.reconcile(items, trackedItems);
    }
}
