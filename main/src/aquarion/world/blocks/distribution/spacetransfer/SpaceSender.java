package aquarion.world.blocks.distribution.spacetransfer;

import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.type.SectorPreset;
import mindustry.world.Block;

public class SpaceSender extends Block {
    public float launchTime = 60f * 5f;
    public float kerosenePerLaunch = 0f;
    public float powerPerTick = 0f;

    public SpaceSender(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        itemCapacity = 100;
        configurable = true;

        hasLiquids = true;
        liquidCapacity = 100f;
        hasPower = true;

        config(Integer.class, (SpaceSenderBuild tile, Integer sectorId) -> tile.destinationSectorId = sectorId);
    }

    @Override
    public void init() {
        super.init();
        if (powerPerTick > 0) consumePower(powerPerTick);
    }

    public class SpaceSenderBuild extends Building {
        public int destinationSectorId = -1;
        public float progress = 0;

        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.planet, () -> {
                Vars.ui.planet.showSelect(Vars.state.getSector(), otherSector -> {
                    configure(otherSector.id);
                });
            }).size(50f);
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return block.hasLiquids && liquids.get(liquid) < block.liquidCapacity;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return block.hasItems && items.get(item) < block.itemCapacity;
        }

        @Override
        public void display(Table table) {
            super.display(table);
            table.row();

            SectorPreset dest = Vars.content.sectors().find(s -> s.id == destinationSectorId);

            if (dest != null) {
                table.add("[lightgray]Destination: [accent]" + dest.localizedName).left().padTop(4f);
            } else {
                table.add("[lightgray]Destination: [scarlet]None").left().padTop(4f);
            }
        }

        @Override
        public void updateTile() {
            if (destinationSectorId == -1) return;

            if (kerosenePerLaunch > 0 && liquids.currentAmount() < kerosenePerLaunch) return;

            Item toSend = items.first();
            if (toSend != null && items.get(toSend) >= block.itemCapacity) {
                progress += edelta();
                if (progress >= launchTime) {
                    progress = 0;
                    if (kerosenePerLaunch > 0) liquids.remove(liquids.current(), kerosenePerLaunch);
                    items.remove(toSend, block.itemCapacity);
                    Fx.launchPod.at(x, y);
                    SpaceNet.addCargo(destinationSectorId, toSend, block.itemCapacity);
                }
            } else {
                progress = 0;
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(destinationSectorId);
            write.f(progress);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            destinationSectorId = read.i();
            progress = read.f();
        }

        @Override
        public Integer config() {
            return destinationSectorId;
        }
    }
}