package aquarion.world.blocks.distribution.spacetransfer;

import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.content.Liquids;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;

public class SpaceReceiver extends Block {
    public float waterPerReceive = 0.2f;

    public SpaceReceiver(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        hasLiquids = true;
        liquidCapacity = 50f;
        configurable = true;
        clearOnDoubleTap = true;

        config(Item.class, (SpaceReceiverBuild tile, Item item) -> tile.filter = item);
        configClear((SpaceReceiverBuild tile) -> tile.filter = null);
    }

    public class SpaceReceiverBuild extends Building {
        public Item filter = null;

        @Override
        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(table, Vars.content.items(), () -> filter, item -> configure(item));
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return liquid == Liquids.water;
        }

        @Override
        public void updateTile() {
            if (filter == null) return;

            int sectorId = Vars.state.getSector().id;
            int availableInOrbit = SpaceNet.getCargo(sectorId, filter);

            if (availableInOrbit <= 0) return;
            if (liquids.get(Liquids.water) < waterPerReceive) return;
            if (items.get(filter) >= block.itemCapacity) return;

            int toTake = Math.min(availableInOrbit, block.itemCapacity - items.get(filter));
            int maxByWater = (int) (liquids.get(Liquids.water) / waterPerReceive);
            toTake = Math.min(toTake, maxByWater);

            if (toTake > 0) {
                SpaceNet.removeCargo(sectorId, filter, toTake);
                items.add(filter, toTake);
                liquids.remove(Liquids.water, toTake * waterPerReceive);
            }
        }
    }
}