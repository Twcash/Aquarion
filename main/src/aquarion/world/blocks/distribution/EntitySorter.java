package aquarion.world.blocks.distribution;

import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import aquarion.world.items.AquaItemEntity;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.distribution.Sorter;

/** A standard sorter that also preserves Aquarion per-unit item state. */
public class EntitySorter extends Sorter {
    public EntitySorter(String name) {
        super(name);
        buildType = EntitySorterBuild::new;
        itemCapacity = 0;
    }

    public class EntitySorterBuild extends SorterBuild implements AquaItemEntity.Container {
        private final Seq<AquaItemEntity> trackedItems = new Seq<>();

        @Override
        public Seq<AquaItemEntity> aquaItemEntities() {
            return trackedItems;
        }

        @Override
        public void handleItem(Building source, Item item) {
            int before = items.get(item);
            AquaItemEntity.Transfer transfer = AquaItemEntity.beginTransfer(source, this, item);
            try {
                // Keep SorterBuild's native directional filtering and immediate forwarding behavior.
                super.handleItem(source, item);
                if(!transfer.received() && items.get(item) > before) AquaItemEntity.addIfTracked(trackedItems, transfer.entity);
            } finally {
                AquaItemEntity.endTransfer(transfer);
            }
        }

        @Override
        public void updateTile() {
            AquaItemEntity.update(trackedItems, items, edelta());
            super.updateTile();
            AquaItemEntity.reconcile(items, trackedItems);
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
}
