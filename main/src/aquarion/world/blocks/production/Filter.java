package aquarion.world.blocks.production;

import aquarion.content.AquaItems;
import aquarion.content.AquaLiquids;
import aquarion.world.type.AquaGenericCrafter;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.type.Item;
import mindustry.type.LiquidStack;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

/**
 * Фильтр: за каждый цикл делает rollsPerCraft бросков и выдаёт предметы из drops,
 * а также постоянно производит очищенную воду.
 *
 * Всё настраивается в определении блока (CrafterBlocks.java), например:
 *   drop(Items.sand, 25, 20);        // предмет, шанс (вес), количество
 *   drop(Items.copper, 10, 5, 15);   // предмет, шанс (вес), от и до
 */
public class Filter extends AquaGenericCrafter {
    /** Сколько очищенной воды выдаётся в секунду. */
    public float outputLiquidAmount = 6.0f;

    /** Сколько бросков (выпадений) за один цикл. */
    public int rollsPerCraft = 1;

    /** Список возможных выпадений. Заполняется через drop(...). */
    public Seq<FilterDrop> drops = new Seq<>();

    private float totalWeight = 0f;
    private int maxPerCraft = 0;

    public static class FilterDrop {
        public final Item item;
        /** Вес шанса. Не обязан давать в сумме 100: шанс = chance / сумма всех chance. */
        public final float chance;
        public final int minAmount, maxAmount;

        public FilterDrop(Item item, float chance, int minAmount, int maxAmount) {
            this.item = item;
            this.chance = chance;
            this.minAmount = Math.min(minAmount, maxAmount);
            this.maxAmount = Math.max(minAmount, maxAmount);
        }
    }

    /** Фиксированное количество предметов. */
    public void drop(Item item, float chance, int amount) {
        drop(item, chance, amount, amount);
    }

    /** Случайное количество предметов от min до max. */
    public void drop(Item item, float chance, int min, int max) {
        drops.add(new FilterDrop(item, chance, min, max));
    }

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
        // Если в определении блока ничего не задано, берём значения по умолчанию.
        if (drops.isEmpty()) {
            drop(Items.sand, 25f, 12);
            drop(Items.copper, 25f, 12);
            drop(Items.lead, 25f, 12);
            drop(AquaItems.calcium, 25f, 15);
        }

        totalWeight = 0f;
        int biggest = 0;
        for (FilterDrop d : drops) {
            totalWeight += d.chance;
            biggest = Math.max(biggest, d.maxAmount);
        }
        maxPerCraft = biggest * Math.max(1, rollsPerCraft);
        itemCapacity = Math.max(itemCapacity, maxPerCraft * 2);

        // Очищенную воду производит базовый AquaGenericCrafter (значение за тик).
        outputLiquid = new LiquidStack(AquaLiquids.clearwater, outputLiquidAmount / 60f);

        super.init();

        // AquaGenericCrafter.init() принудительно ставит свой buildType, возвращаем FilterBuild.
        buildType = FilterBuild::new;
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.output, table -> {
            for (FilterDrop d : drops) {
                table.add(StatValues.displayItemPercent(
                        d.item,
                        Math.round(d.chance * 100f / totalWeight),
                        true
                )).padRight(5);
            }
        });
    }

    public class FilterBuild extends AquaGenericCrafterBuild {

        @Override
        public boolean shouldConsume() {
            // Пауза, пока блок забит очищенной водой.
            if (liquids.get(AquaLiquids.clearwater) >= liquidCapacity - 0.1f) return false;

            // Пауза, пока блок забит предметами (не помещается самая большая порция).
            if (items.total() + maxPerCraft > itemCapacity) return false;

            return super.shouldConsume();
        }

        @Override
        public void craft() {
            consume();

            for (int r = 0; r < Math.max(1, rollsPerCraft); r++) {
                FilterDrop drop = rollDrop();
                if (drop == null) continue;

                int amount = drop.minAmount == drop.maxAmount
                        ? drop.minAmount
                        : Mathf.random(drop.minAmount, drop.maxAmount);

                for (int i = 0; i < amount; i++) {
                    offload(drop.item);
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

        private FilterDrop rollDrop() {
            if (totalWeight <= 0f) return null;
            float roll = Mathf.random(totalWeight);
            float acc = 0f;
            for (FilterDrop d : drops) {
                acc += d.chance;
                if (roll < acc) return d;
            }
            return drops.peek();
        }
    }
}
