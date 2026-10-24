package aquarion.world.blocks.production;

import aquarion.content.AquaItems;
import aquarion.content.AquaLiquids;
import aquarion.world.type.AquaGenericCrafter;
import arc.math.Mathf;
import mindustry.content.Items;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

/**
 * Фильтр: за каждый цикл выдаёт один случайный предмет из results
 * и постоянно производит очищенную воду.
 *
 * Работа построена поверх AquaGenericCrafter: прогресс, потребление,
 * вывод жидкости и выгрузка жидкости делает базовый класс.
 */
public class Filter extends AquaGenericCrafter {
    /** Сколько очищенной воды выдаётся в секунду. */
    public float outputLiquidAmount = 6.0f;

    /** Что и сколько выпадает за один цикл. Вес шанса = amount. */
    public ItemStack[] results;

    private int totalWeight = 0;
    private int maxAmount = 0;

    public Filter(String name) {
        super(name);

        hasItems = true;
        hasLiquids = true;
        hasPower = true;

        itemCapacity = 40;
        liquidCapacity = 60f;
        
    }

    @Override
    public void init() {
        if (results == null) {
            results = new ItemStack[]{
                    new ItemStack(Items.sand, 12),
                    new ItemStack(Items.copper, 12),
                    new ItemStack(Items.lead, 12),
                    new ItemStack(AquaItems.calcium, 15)
            };
        }

        totalWeight = 0;
        maxAmount = 0;
        for (ItemStack s : results) {
            totalWeight += s.amount;
            maxAmount = Math.max(maxAmount, s.amount);
        }
        itemCapacity = Math.max(itemCapacity, maxAmount * 2);

        // Очищенную воду производит базовый AquaGenericCrafter (значение за тик).
        outputLiquid = new LiquidStack(AquaLiquids.clearwater, outputLiquidAmount / 60f);

        super.init();

        // ВАЖНО: AquaGenericCrafter.init() принудительно ставит buildType = AquaGenericCrafterBuild,
        // из-за чего FilterBuild никогда не использовался. Возвращаем свой.
        buildType = FilterBuild::new;
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.output, table -> {
            for (ItemStack stack : results) {
                table.add(StatValues.displayItemPercent(
                        stack.item,
                        Math.round(stack.amount * 100f / totalWeight),
                        true
                )).padRight(5);
            }
        });
    }

    public class FilterBuild extends AquaGenericCrafterBuild {

        @Override
        public boolean shouldConsume() {
            // Не запускаем цикл, если самая большая порция не поместится.
            return super.shouldConsume() && items.total() + maxAmount <= itemCapacity;
        }

        @Override
        public void craft() {
            consume();

            Item chosen = rollItem();
            if (chosen != null) {
                int amount = 1;
                for (ItemStack s : results) {
                    if (s.item == chosen) {
                        amount = s.amount;
                        break;
                    }
                }
                for (int i = 0; i < amount; i++) {
                    offload(chosen);
                }
            }

            if (wasVisible) {
                craftEffect.at(x, y);
            }

            progress %= 1f;
        }

        @Override
        public void dumpOutputs() {
            // Базовый класс выгружает жидкость.
            super.dumpOutputs();

            // Предметы выгружаем сами, т.к. outputItems у фильтра не задан.
            if (timer(timerDump, dumpTime / timeScale)) {
                dump();
            }
        }

        private Item rollItem() {
            int roll = Mathf.random(0, totalWeight - 1);
            int acc = 0;
            for (ItemStack s : results) {
                acc += s.amount;
                if (roll < acc) return s.item;
            }
            return null;
        }
    }
}
