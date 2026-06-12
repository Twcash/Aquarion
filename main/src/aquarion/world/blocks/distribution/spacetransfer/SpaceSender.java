package aquarion.world.blocks.distribution.spacetransfer;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.graphics.Pal;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.content.Fx;
import mindustry.core.ContentType;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.consumers.ConsumePower;
import aquarion.world.blocks.distribution.spacetransfer.SpaceReceiver;

public class SpaceSender extends Block {
    public float launchCooldown = 300f;
    public float kerosenePerLaunch = 50f;
    public float powerPerTick = 2f;

    public SpaceSender(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        hasLiquids = true;
        hasPower = true;
        configurable = true;
        
        consume(new ConsumePower(powerPerTick, 0.0f, false));
    }

    public class SpaceSenderBuild extends Building {
        public float progress = 0;

        public Liquid getKerosene() {
            return Vars.content.get(ContentType.liquid, "aquarion-kerosene");
        }

        @Override
        public void updateTile() {
            Liquid kerosene = getKerosene();
            if (kerosene == null) return;

            boolean hasResources = items.total() >= itemCapacity && liquids.get(kerosene) >= kerosenePerLaunch;
            boolean hasPowerSupply = power != null && power.status > 0;
            boolean receiverReady = checkReceiverReady();

            if (hasResources && hasPowerSupply && receiverReady) {
                if (progress < launchCooldown) {
                    progress += edelta();
                } else {
                    launchResources();
                    progress = 0;
                }
            } else {
                if (progress > 0) {
                    progress -= edelta() * 0.5f;
                }
            }
        }

        public boolean checkReceiverReady() {
            final boolean[] ready = {false};
            Groups.build.each(b -> {
                if (b instanceof SpaceReceiver.SpaceReceiverBuild receiver) {
                    if (receiver.team == this.team && receiver.canReceive()) {
                        ready[0] = true;
                    }
                }
            });
            return ready[0];
        }

        public void launchResources() {
            Liquid kerosene = getKerosene();
            if (kerosene != null) {
                liquids.remove(kerosene, kerosenePerLaunch);
            }
            
            Fx.launch.at(x, y);

            Groups.build.each(b -> {
                if (b instanceof SpaceReceiver.SpaceReceiverBuild receiver) {
                    if (receiver.team == this.team && receiver.canReceive()) {
                        items.each((item, amount) -> {
                            receiver.handleIncomingItems(item, amount);
                        });
                    }
                }
            });
            items.clear();
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            float barWidth = size * 8f;
            float barHeight = 4f;
            float bx = x - barWidth / 2f;
            float by = y + (size * 8f) / 2f + 4f;

            Draw.color(Pal.darkerGray);
            Fill.rect(x, by + barHeight / 2f, barWidth, barHeight);
            
            if (progress > 0) {
                Draw.color(Pal.accent);
                float currentWidth = (barWidth - 2f) * (progress / launchCooldown);
                Fill.rect(bx + 1f + currentWidth / 2f, by + barHeight / 2f, currentWidth, barHeight - 2f);
            }
            Draw.reset();
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return items.total() < itemCapacity;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return liquid == getKerosene() && liquids.get(liquid) < liquidCapacity;
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
