package aquarion.world.blocks.distribution.spacetransfer;

import arc.scene.ui.layout.Table;
import arc.util.Log;
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
        public float scanTimer = 0;

        @Override
        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(table, Vars.content.items(), () -> filter, item -> configure(item));
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return liquid == Liquids.water && liquids.get(liquid) < block.liquidCapacity;
        }

        @Override
        public void updateTile() {
            super.updateTile();

            // Чтобы не нагружать игру, проверяем орбиту раз в полсекунды (30 кадров)
            scanTimer += edelta();
            if (scanTimer >= 30f) {
                scanTimer = 0;
                tryPullFromOrbit();
            }
        }

        private void tryPullFromOrbit() {
            // Если фильтр предметов не выбран или мы не на секторе кампании — ничего не делаем
            if (filter == null || Vars.state.getSector() == null) return;

            int currentSectorId = Vars.state.getSector().id;
            int availableInOrbit = SpaceNet.getCargo(currentSectorId, filter);

            // Если на орбите этого сектора нет нужного нам ресурса — выходим
            if (availableInOrbit <= 0) return;

            // Проверяем воду для охлаждения
            float currentWater = liquids.get(Liquids.water);
            if (currentWater < waterPerReceive) return;

            // Проверяем свободное место на складе приёмника
            int currentAmount = items.get(filter);
            int maxCapacity = block.itemCapacity;
            int freeSpace = maxCapacity - currentAmount;
            if (freeSpace <= 0) return;

            // Рассчитываем, сколько можем взять за этот раз
            int toTake = Math.min(availableInOrbit, freeSpace);
            int maxByWater = (int) (currentWater / waterPerReceive);
            toTake = Math.min(toTake, maxByWater);

            if (toTake > 0) {
                // Забираем ресурсы из космоса
                SpaceNet.removeCargo(currentSectorId, filter, toTake);
                // Добавляем предметы в инвентарь приёмника
                items.add(filter, toTake);
                // Списываем воду
                liquids.remove(Liquids.water, toTake * waterPerReceive);

                Log.info("[SpaceReceiver] Successfully pulled " + toTake + " pcs of " + filter.name + " from orbit!");
            }
        }

        @Override
        public Object config() {
            return filter;
        }
    }
}