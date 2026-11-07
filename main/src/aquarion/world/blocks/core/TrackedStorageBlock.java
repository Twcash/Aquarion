package aquarion.world.blocks.core;

import mindustry.world.blocks.storage.StorageBlock;

/** Storage block variant that keeps age/state for items in its own inventory. */
public class TrackedStorageBlock extends StorageBlock {
    public TrackedStorageBlock(String name) {
        super(name);
        update = true;
        buildType = () -> new EntityStorageBuild(this);
    }
}
