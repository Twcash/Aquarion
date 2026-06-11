package aquarion.world.blocks;

import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.gen.Fx;
import mindustry.type.Item;
import mindustry.world.Block;

public class SpaceTransfer {
}

class SpaceSender extends Block {
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

            for (Building b : mindustry.Vars.indexer.allBuildings(SpaceReceiver.class)) {
                if (b instanceof SpaceReceiver.SpaceReceiverBuild receiver && receiver.team == this.team) {
                    items.each((item, amount) -> {
                        receiver.handleIncomingItems(item, amount);
                    });
                    items.clear();
                    break; 
                }
            }
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

class SpaceReceiver extends Block {

    public SpaceReceiver(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        itemCapacity = 500;
    }

    public class SpaceReceiverBuild extends Building {

        public void handleIncomingItems(Item item, int amount) {
            Fx.land.at(x, y);
            items.add(item, amount);
        }

        @Override
        public void updateTile() {
            if (items.total() > 0) {
                dump(); 
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return false; 
        }
    }
}
