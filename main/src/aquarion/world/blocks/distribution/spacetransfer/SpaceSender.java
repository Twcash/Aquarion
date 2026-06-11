package aquarion.world.blocks.distribution.spacetransfer;

import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.content.Fx;
import mindustry.type.Item;
import mindustry.world.Block;

public class SpaceSender extends Block {
    public int maxLaunchStorage = 100;
    public float launchCooldown = 300f;

    public SpaceSender(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        configurable = true;
    }

    public class SpaceSenderBuild extends Building {
        public float progress = 0;

        @Override
        public void updateTile() {
            if (items.total() >= maxLaunchStorage && progress >= launchCooldown) {
                launchResources();
                progress = 0;
            }
            if (progress < launchCooldown) {
                progress += edelta();
            }
        }

        public void launchResources() {
            Fx.launch.at(x, y);

            // Безопасный поиск приёмника через группу всех построек на карте
            Groups.build.each(b -> {
                if (b instanceof SpaceReceiver.SpaceReceiverBuild receiver && receiver.team == this.team) {
                    items.each((item, amount) -> {
                        receiver.handleIncomingItems(item, amount);
                    });
                }
            });
            items.clear();
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return items.total() < maxLaunchStorage && items.get(item) < getMaximumAccepted(item);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(progress);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            progress = read.f();
        }
    }
}
