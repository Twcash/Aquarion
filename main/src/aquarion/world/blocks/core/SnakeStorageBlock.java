package aquarion.world.blocks.core;

import arc.struct.Seq;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;

public class SnakeStorageBlock extends StorageBlock {

    public SnakeStorageBlock(String name) {
        super(name);
    }

    public class SnakeStorageBlockBuild extends StorageBuild {
        private Seq<Building> linkedStorages = new Seq<>();
        private CoreBlock.CoreBuild core;

        @Override
        public void created() {
            super.created();
            Time.run(5f, this::updateLinks); //For the love of god stop eating my memory
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();
            updateLinks(); // Update links when this storage is destroyed
        }

        private void updateLinks() {
            linkedStorages.clear();
            core = team.core();

            for (Building tile : proximity) {
                if (tile != null && tile.team == team) {
                    if (tile instanceof SnakeStorageBlockBuild || tile.block instanceof StorageBlock) {
                        linkedStorages.add(tile);
                    }
                }
            }
        }

        @Override
        public void handleItem(Building source, Item item) {
            if (core != null) {
                core.items.add(item, 1);
            } else {
                for (Building storage : linkedStorages) {
                    storage.handleItem(source, item);
                    return;
                }
            }
        }

        @Override
        public int getMaximumAccepted(Item item) {
            if (core != null) return core.getMaximumAccepted(item);
            int total = super.getMaximumAccepted(item);
            for (Building storage : linkedStorages) {
                total += storage.getMaximumAccepted(item);
            }
            return total;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            if (core != null) return core.acceptItem(source, item);
            for (Building storage : linkedStorages) {
                if (storage.acceptItem(source, item)) return true;
            }
            return super.acceptItem(source, item);
        }

        @Override
        public int removeStack(Item item, int amount) {
            super.removeStack(item, amount);
            for (Building storage : linkedStorages) {
                storage.removeStack(item, amount);
            }
            return amount;
        }
    }
}