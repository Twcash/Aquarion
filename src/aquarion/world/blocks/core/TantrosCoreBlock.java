package aquarion.world.blocks.core;

import arc.audio.Music;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.gen.Musics;
import mindustry.type.Item;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;

public class TantrosCoreBlock extends CoreBlock {

    public TantrosCoreBlock(String name) {
        super(name);
    }

    public class LinkedCoreBuild extends CoreBuild {
        private Seq<SnakeStorageBlock.SnakeStorageBlockBuild> linkedStorages = new Seq<>();


        public void updateLinks() {
            linkedStorages.clear();
            for (Building b : team.data().buildings) {
                if (b.block instanceof StorageBlock) {
                    linkedStorages.add((SnakeStorageBlock.SnakeStorageBlockBuild) b);
                }
            }
        }
        @Override
        public boolean owns(Building core, Building tile){
            return tile instanceof AquaStorageBlock.AStorageBuild b && ((AquaStorageBlock)b.block).coreMerge && (b.linkedCore == core || b.linkedCore == null);
        }
        @Override
        public void handleItem(Building source, Item item) {
            super.handleItem(source, item);
            for (Building storage : linkedStorages) {
                storage.handleItem(source, item);
            }
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