package aquarion.world.blocks.core;

import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import aquarion.world.items.AquaItemEntity;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.storage.StorageBlock;

/** StorageBuild implementation with a saveable per-item state ledger. */
public class EntityStorageBuild extends StorageBlock.StorageBuild implements AquaItemEntity.Container {
    private final Seq<AquaItemEntity> trackedItems = new Seq<>();

    public EntityStorageBuild(StorageBlock owner) {
        owner.super();
    }

    public static void install(StorageBlock block) {
        block.update = true;
        block.buildType = () -> new EntityStorageBuild(block);
    }

    @Override
    public Seq<AquaItemEntity> aquaItemEntities() {
        return trackedItems;
    }

    @Override
    public void handleItem(Building source, Item item) {
        int before = items.get(item);
        super.handleItem(source, item);
        if(linkedCore == null && items.get(item) > before) {
            AquaItemEntity.addIfTracked(trackedItems, AquaItemEntity.capture(source, item));
        }
    }

    @Override
    public void updateTile() {
        if(linkedCore == null) AquaItemEntity.update(trackedItems, items, edelta());
    }

    @Override
    public byte version() {
        return 1;
    }

    @Override
    public void write(Writes write) {
        super.write(write);
        AquaItemEntity.write(write, trackedItems);
    }

    @Override
    public void read(Reads read, byte revision) {
        super.read(read, revision);
        if(revision >= 1) AquaItemEntity.read(read, trackedItems);
        AquaItemEntity.reconcile(items, trackedItems);
    }
}
