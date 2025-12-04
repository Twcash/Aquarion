package aquarion.world.consumers;

import arc.func.Floatf;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.consumers.ConsumeLiquids;

public class Recipe {
    public final Consume[] consumers;
    public final ItemStack[] outputItems;
    public final LiquidStack[] outputLiquids;
    public final float craftTime;
    public Floatf<Building> multiplier = b -> 1f;

    public Recipe(float craftTime, ItemStack[] outputs, LiquidStack[] outputLiquids, Consume... consumers) {
        this.craftTime = craftTime;
        this.outputItems = outputs;
        this.outputLiquids = outputLiquids;
        this.consumers = consumers;
    }

    public boolean isValid(Building build) {
        for (Consume consume : consumers) {
            if (!consume.ignore() && consume.efficiency(build) <= 0) {
                return false;
            }
        }
        return true;
    }

    public void trigger(Building build){
        for (var consume : consumers) {
            if (consume instanceof ConsumeItems) {
                for (ItemStack stack : ((ConsumeItems) consume).items) {
                    build.items.remove(stack.item, Math.round(stack.amount * multiplier.get(build)));
                }
            }
        }
    }

    public void update(Building build){
        float mult = multiplier.get(build);
        for (var consume : consumers) {
            if (consume instanceof ConsumeLiquids) {
                for (LiquidStack stack : ((ConsumeLiquids) consume).liquids) {
                    build.liquids.remove(stack.liquid, stack.amount * build.edelta() * mult);
                }
            }
        }
    }

    public void apply(Block block) {
        block.hasItems = true;
        block.acceptsItems = true;
        block.hasLiquids = true;
        for (var consume : consumers) {
            if (consume instanceof ConsumeItems) {
                for (ItemStack stack : ((ConsumeItems) consume).items) {
                    int itemIndex = findItemIndex(stack.item);
                    if (itemIndex >= 0) {
                        block.itemFilter[itemIndex] = true;
                    }
                }
            } else if (consume instanceof ConsumeLiquids) {
                for (LiquidStack stack : ((ConsumeLiquids) consume).liquids) {
                    int liquidIndex = findLiquidIndex(stack.liquid);
                    if (liquidIndex >= 0) {
                        block.liquidFilter[liquidIndex] = true;
                    }
                }
            }
        }
    }

    public int findItemIndex(Item item) {
        for (int i = 0; i < consumers.length; i++) {
            if (consumers[i] instanceof ConsumeItems) {
                for (ItemStack stack : ((ConsumeItems) consumers[i]).items) {
                    if (stack.item == item) {
                        return i; // Return index of the consumer
                    }
                }
            }
        }
        return -1; // Item not found
    }

    public int findLiquidIndex(Liquid liquid) {
        for (int i = 0; i < consumers.length; i++) {
            if (consumers[i] instanceof ConsumeLiquids) {
                for (LiquidStack stack : ((ConsumeLiquids) consumers[i]).liquids) {
                    if (stack.liquid == liquid) {
                        return i;
                    }
                }
            }
        }
        return -1; // Liquid not found
    }

    public boolean findConsumer(Item item) {
        for (Consume consume : consumers) {
            if (consume instanceof ConsumeItems) {
                for (ItemStack stack : ((ConsumeItems) consume).items) {
                    if (stack.item == item) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean findLiquid(Liquid liquid) {
        for (Consume consume : consumers) {
            if (consume instanceof ConsumeLiquids) {
                for (LiquidStack stack : ((ConsumeLiquids) consume).liquids) {
                    if (stack.liquid == liquid) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean acceptsItem(Item item, Building build) {
        return findConsumer(item);
    }

    public boolean containsItem(Item item) {
        return findConsumer(item);
    }

    public boolean acceptsLiquid(Liquid liquid) {
        return findLiquid(liquid);
    }

    public boolean containsLiquid(Liquid liquid) {
        return findLiquid(liquid);
    }

    public boolean hasLiquids() {
        return outputLiquids != null && outputLiquids.length > 0;
    }
    public float efficiency(Building build) {
        float mult = multiplier.get(build);
        float ed = build.edelta() * build.efficiencyScale();
        if (ed <= 0.00000001f) return 0f;
        float min = 1f;

        for (var consume : consumers) {
            if (consume instanceof ConsumeLiquids) {
                for (LiquidStack stack : ((ConsumeLiquids) consume).liquids) {
                    min = Math.min(build.liquids.get(stack.liquid) / (stack.amount * ed * mult), min);
                }
            } else if (consume instanceof ConsumeItems) {
                for (ItemStack stack : ((ConsumeItems) consume).items) {
                    if (!build.items.has(stack.item, (int) (stack.amount * mult))) {
                        return 0f;  // If any required item is missing, return 0 efficiency
                    }
                }
            }
        }
        return min;
    }
}