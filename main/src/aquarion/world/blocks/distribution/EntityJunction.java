package aquarion.world.blocks.distribution;

import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import aquarion.world.items.AquaItemEntity;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.distribution.Junction;

/** Junction that keeps each buffered item's age and state while it crosses lanes. */
public class EntityJunction extends Junction {
    public EntityJunction(String name) {
        super(name);
        buildType = EntityJunctionBuild::new;
    }

    public class EntityJunctionBuild extends JunctionBuild implements AquaItemEntity.Container {
        private final Seq<AquaItemEntity> trackedItems = new Seq<>();

        @Override
        public Seq<AquaItemEntity> aquaItemEntities() {
            return trackedItems;
        }

        @Override
        public void handleItem(Building source, Item item) {
            int relative = source.relativeTo(tile);
            buffer.accept(relative, item);
            AquaItemEntity.addIfTracked(trackedItems, AquaItemEntity.capture(source, item));
        }

        @Override
        public void updateTile() {
            // Junction's native directional buffer stores item IDs, so age items here and
            // let the downstream tracked container perform the eventual spoil transform.
            AquaItemEntity.advance(trackedItems, delta());
            super.updateTile();
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
        }
    }
}
