package aquarion.world.blocks.production;

import aquarion.content.AquaLiquids;
import aquarion.world.type.AquaGenericCrafter;
import mindustry.content.Items;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.Separator;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import mindustry.world.meta.BlockStatus;
import arc.math.Mathf;
import aquarion.content.AquaItems;

public class Filter extends AquaGenericCrafter {
    public float outputLiquidAmount = 6.0f;
    public int seed;
    public ItemStack[] results;

    private final int[] itemChances = {29, 17, 17, 17, 17}; //chance items
    private final Item[] itemPool = {Items.sand, Items.copper, Items.lead, Items.silicon, AquaItems.nickel}; //items

    public Filter(String name) {
        super(name);

        hasItems = true;
        hasLiquids = true;
        hasPower = true;
        outputsLiquid = true;

        results = new ItemStack[]{
                new ItemStack(Items.sand, 13),
                new ItemStack(Items.copper, 12),
                new ItemStack(Items.lead, 12),
                new ItemStack(Items.silicon, 12),
                new ItemStack(AquaItems.nickel, 12)
        };
    }

    @Override
    public void setStats() {
        super.setStats();

        int[] sum = {0};
        for(var r : results) sum[0] += r.amount;

        stats.add(Stat.output, table -> {
            for(ItemStack stack : results){
                table.add(StatValues.displayItemPercent(stack.item, (int)((float)stack.amount / sum[0] * 100), true)).padRight(5);
            }
        });
        stats.add(Stat.output, StatValues.liquid(AquaLiquids.clearwater, outputLiquidAmount, false));
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("clearwater-bar", (FilterBuild entity) -> new Bar(
                () -> AquaLiquids.clearwater.localizedName,
                () -> AquaLiquids.clearwater.color,
                () -> entity.liquids.get(AquaLiquids.clearwater) / liquidCapacity
        ));
    }

    public class FilterBuild extends AquaGenericCrafterBuild {
        @Override
        public void updateTile() {
            if (efficiency > 0 && liquids.get(AquaLiquids.clearwater) < liquidCapacity) {
                totalProgress += warmup * delta();
                progress += getProgressIncrease(craftTime);
                warmup = Mathf.lerpDelta(warmup, 1f, 0.02f);

                liquids.add(AquaLiquids.clearwater, (outputLiquidAmount / 60f) * edelta());
            } else {
                warmup = Mathf.lerpDelta(warmup, 0f, 0.02f);
            }

            if (progress >= 1f) {
                progress %= 1f;

                int rand = Mathf.randomSeed(seed++, 0, 99);
                int count = 0;
                Item chosenItem = null;

                for (int index = 0; index < itemPool.length; index++) {
                    if (rand >= count && rand < count + itemChances[index]) {
                        chosenItem = itemPool[index];
                        break;
                    }
                    count += itemChances[index];
                }

                consume();

                if (chosenItem != null) {
                    int amountToDrop = 1;
                    for (ItemStack stack : results) {
                        if (stack.item == chosenItem) {
                            amountToDrop = stack.amount;
                            break;
                        }
                    }

                    if (items.get(chosenItem) < itemCapacity) {
                        for (int j = 0; j < amountToDrop; j++) {
                            offload(chosenItem);
                        }
                    }
                }
            }

            if (liquids.get(AquaLiquids.clearwater) > 0) {
                dumpLiquid(AquaLiquids.clearwater);
            }

            if (timer(timerDump, dumpTime / timeScale)) {
                dump();
            }
        }

        @Override
        public BlockStatus status() {
            if (liquids.get(AquaLiquids.clearwater) >= liquidCapacity) {
                return BlockStatus.noOutput;
            }
            if (efficiency > 0 && warmup > 0.5f) {
                return BlockStatus.active;
            }
            return BlockStatus.noInput;
        }
    }
}