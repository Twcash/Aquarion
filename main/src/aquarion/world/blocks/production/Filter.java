package aquarion.world.blocks.production;

import aquarion.content.AquaItems;
import aquarion.content.AquaLiquids;
import aquarion.world.type.AquaGenericCrafter;
import arc.math.Mathf;
import mindustry.content.Items;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Bar;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

public class Filter extends AquaGenericCrafter {
    /** Сколько очищенной воды выдаётся в секунду. */
    public float outputLiquidAmount = 6.0f;

    /** Что и сколько выпадает за один цикл. Вес шанса = amount. */
    public ItemStack[] results;

    private int totalWeight = 0;

    public Filter(String name) {
        super(name);

        hasItems = true;
        hasLiquids = true;
        hasPower = true;
        outputsLiquid = true;

        itemCapacity = 40;
        liquidCapacity = 60f;
        // ВАЖНО: craftTime, consumePower(...) и consumeLiquid(...) задаются в определении блока.
    }

    @Override
    public void init() {
        // Предметы создаются к этому моменту, поэтому null-ов в пуле не будет.
        if (results == null) {
            results = new ItemStack[]{
                    new ItemStack(Items.sand, 12),
                    new ItemStack(Items.copper, 12),
                    new ItemStack(Items.lead, 12),
                    new ItemStack(AquaItems.calcium, 15)
            };
        }

        totalWeight = 0;
        int maxAmount = 0;
        for (ItemStack s : results) {
            totalWeight += s.amount;
            maxAmount = Math.max(maxAmount, s.amount);
        }
        // Чтобы самая большая порция всегда помещалась.
        itemCapacity = Math.max(itemCapacity, maxAmount * 2);

        super.init();
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
        stats.add(Stat.output, StatValues.liquid(AquaLiquids.clearwater, outputLiquidAmount, false));
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("clearwater-bar", entity -> new Bar(
                () -> AquaLiquids.clearwater.localizedName,
                () -> AquaLiquids.clearwater.color,
                () -> entity.liquids.get(AquaLiquids.clearwater) / liquidCapacity
        ));
    }

    public class FilterBuild extends AquaGenericCrafterBuild {

        @Override
        public boolean shouldConsume() {
            // Работаем, пока есть место под воду и предметы.
            return enabled
                    && liquids.get(AquaLiquids.clearwater) < liquidCapacity - 0.01f
                    && items.total() < itemCapacity;
        }

        @Override
        public void updateTile() {
            if (efficiency > 0 && shouldConsume()) {
                totalProgress += warmup * delta();
                progress += getProgressIncrease(craftTime);
                warmup = Mathf.lerpDelta(warmup, 1f, 0.02f);

                liquids.add(AquaLiquids.clearwater, (outputLiquidAmount / 60f) * edelta());
                if (liquids.get(AquaLiquids.clearwater) > liquidCapacity) {
                    liquids.set(AquaLiquids.clearwater, liquidCapacity);
                }
            } else {
                warmup = Mathf.lerpDelta(warmup, 0f, 0.02f);
            }

            if (progress >= 1f) {
                progress %= 1f;
                consume();

                Item chosen = rollItem();
                if (chosen != null && items.get(chosen) < itemCapacity) {
                    int amount = 1;
                    for (ItemStack s : results) {
                        if (s.item == chosen) {
                            amount = s.amount;
                            break;
                        }
                    }
                    for (int i = 0; i < amount; i++) {
                        if (items.total() >= itemCapacity) break;
                        offload(chosen);
                    }
                }
            }

            if (liquids.get(AquaLiquids.clearwater) > 0.001f) {
                dumpLiquid(AquaLiquids.clearwater);
            }

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

        @Override
        public BlockStatus status() {
            if (liquids.get(AquaLiquids.clearwater) >= liquidCapacity - 0.01f) {
                return BlockStatus.noOutput;
            }
            if (efficiency > 0 && warmup > 0.5f) {
                return BlockStatus.active;
            }
            return BlockStatus.noInput;
        }
    }
}
