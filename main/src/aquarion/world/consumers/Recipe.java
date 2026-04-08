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

    public float power = 0f;
    public Floatf<Building> powerf = b -> power;

    public Recipe(float craftTime, ItemStack[] outputs, LiquidStack[] outputLiquids, Consume... consumers) {
        this.craftTime = craftTime;
        this.outputItems = outputs;
        this.outputLiquids = outputLiquids;
        this.consumers = consumers;
    }

    public boolean isValid(Building build) {
        return efficiency(build) > 0f;
    }

    public void trigger(Building build){
        float mult = multiplier.get(build);

        for (var consume : consumers) {
            if (consume instanceof ConsumeItems) {
                for (ItemStack stack : ((ConsumeItems) consume).items) {
                    build.items.remove(stack.item, Math.round(stack.amount * mult));
                }
            }
        }
    }

    public void update(Building build){
        float mult = multiplier.get(build);

        for (var consume : consumers) {
            if (consume instanceof ConsumeLiquids) {
                for (LiquidStack stack : ((ConsumeLiquids) consume).liquids) {
                    build.liquids.remove(
                            stack.liquid,
                            stack.amount * build.edelta() * mult
                    );
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
                    int id = stack.item.id;
                    if(id >= 0 && id < block.itemFilter.length){
                        block.itemFilter[id] = true;
                    }
                }
            } else if (consume instanceof ConsumeLiquids) {
                for (LiquidStack stack : ((ConsumeLiquids) consume).liquids) {
                    int id = stack.liquid.id;
                    if(id >= 0 && id < block.liquidFilter.length){
                        block.liquidFilter[id] = true;
                    }
                }
            }
        }
    }

    public boolean acceptsItem(Item item, Building build) {
        for (Consume consume : consumers) {
            if (consume instanceof ConsumeItems) {
                for (ItemStack stack : ((ConsumeItems) consume).items) {
                    if (stack.item == item) return true;
                }
            }
        }
        return false;
    }

    public boolean acceptsLiquid(Liquid liquid) {
        for (Consume consume : consumers) {
            if (consume instanceof ConsumeLiquids) {
                for (LiquidStack stack : ((ConsumeLiquids) consume).liquids) {
                    if (stack.liquid == liquid) return true;
                }
            }
        }
        return false;
    }

    public float efficiency(Building build) {
        float mult = multiplier.get(build);
        float ed = build.edelta() * build.efficiencyScale();

        if (ed <= 0.0000001f) return 0f;

        float min = 1f;

        for (var consume : consumers) {
            if (consume instanceof ConsumeLiquids) {
                for (LiquidStack stack : ((ConsumeLiquids) consume).liquids) {
                    float required = stack.amount * ed * mult;
                    if(required <= 0f) continue;

                    min = Math.min(
                            build.liquids.get(stack.liquid) / required,
                            min
                    );
                }
            } else if (consume instanceof ConsumeItems) {
                for (ItemStack stack : ((ConsumeItems) consume).items) {
                    if (!build.items.has(stack.item, (int)(stack.amount * mult))) {
                        return 0f;
                    }
                }
            }
        }

        return min;
    }
}