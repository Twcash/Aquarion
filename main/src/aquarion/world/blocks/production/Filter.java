package aquarion.world.blocks.production;

import aquarion.content.AquaItems;
import aquarion.content.AquaLiquids;
import aquarion.world.consumers.AquaConsume;
import aquarion.world.type.AquaGenericCrafter;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.type.Item;
import mindustry.type.LiquidStack;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeLiquids;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

public class Filter extends AquaGenericCrafter {
    public float clearwaterRatio = 1f;

    public float outputLiquidAmount = 6.0f;

    public int rollsPerCraft = 1;

    public Seq<FilterDrop> drops = new Seq<>();

    private float totalWeight = 0f;
    private int maxPerCraft = 0;

    public static class FilterDrop {
        public final Item item;
        public final float chance;
        public final int minAmount, maxAmount;

        public FilterDrop(Item item, float chance, int minAmount, int maxAmount) {
            this.item = item;
            this.chance = chance;
            this.minAmount = Math.min(minAmount, maxAmount);
            this.maxAmount = Math.max(minAmount, maxAmount);
        }
    }

    public void drop(Item item, float chance, int amount) {
        drop(item, chance, amount, amount);
    }

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

        outputLiquid = new LiquidStack(AquaLiquids.clearwater, outputLiquidAmount / 60f);

        super.init();

        float waterIn = findWaterInput();
        if (waterIn > 0f && outputLiquids != null && outputLiquids.length > 0) {
            outputLiquids[0].amount = waterIn * clearwaterRatio;
        }

        buildType = FilterBuild::new;
    }

    private float findWaterInput() {
        float best = 0f;
        for (Consume c : consumers) {
            if (c instanceof AquaConsume ac) {
                for (AquaConsume.Entry e : ac.entries) {
                    best = Math.max(best, waterAmount(e.consumer));
                }
            } else {
                best = Math.max(best, waterAmount(c));
            }
        }
        return best;
    }

    private float waterAmount(Consume c) {
        if (c instanceof ConsumeLiquid cl && cl.liquid == Liquids.water) return cl.amount;
        if (c instanceof ConsumeLiquids cls) {
            for (LiquidStack ls : cls.liquids) {
                if (ls.liquid == Liquids.water) return ls.amount;
            }
        }
        return 0f;
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
            if (liquids.get(AquaLiquids.clearwater) >= liquidCapacity - 0.1f) return false;

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
            super.dumpOutputs();

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
