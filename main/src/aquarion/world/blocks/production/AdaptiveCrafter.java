package aquarion.world.blocks.production;

import aquarion.world.consumers.Recipe;
import aquarion.world.type.AquaGenericCrafter;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.consumers.ConsumeLiquids;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class AdaptiveCrafter extends AquaGenericCrafter {
    public Seq<Recipe> recipes = new Seq<>();
    public float updateEffectChance = 0.04f;
    public float updateEffectSpread = 4f;

    public AdaptiveCrafter(String name) {
        super(name);
        hasPower = true;
        consumesPower = true;
    }

    public AdaptiveCrafter addRecipe(Recipe recipe) {
        recipes.add(recipe);
        return this;
    }

    @Override
    public void init(){
        super.init();
        for(Recipe r : recipes){
            r.apply(this);
        }
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.remove(Stat.productionTime);

        stats.add(Stat.output, table -> {
            for(Recipe recipe : recipes){
                table.row();

                table.add("Time: " + recipe.craftTime / 60f + "s");

                if(recipe.outputItems != null){
                    for(ItemStack stack : recipe.outputItems){
                        table.add(stack.item.localizedName + " x" + stack.amount);
                    }
                }

                if(recipe.outputLiquids != null){
                    for(LiquidStack stack : recipe.outputLiquids){
                        table.add(stack.liquid.localizedName + " " + stack.amount);
                    }
                }
            }
        });
    }

    public class AdaptiveCrafterBuild extends AquaGenericCrafterBuild {
        private Recipe currentRecipe;

        @Override
        public void updateTile() {
            if (currentRecipe == null || !currentRecipe.isValid(this)) {
                currentRecipe = selectRecipe();
            }

            if (currentRecipe != null) {
                craftTime = currentRecipe.craftTime;

                efficiency = currentRecipe.efficiency(this);

                currentRecipe.update(this);

                progress += getProgressIncrease(craftTime) * efficiency;

                warmup = Mathf.approachDelta(warmup, efficiency, warmupSpeed);

                // output liquids (FIXED: use recipe outputs)
                if (currentRecipe.outputLiquids != null) {
                    float inc = getProgressIncrease(1f);
                    for (var output : currentRecipe.outputLiquids) {
                        handleLiquid(this, output.liquid,
                                Math.min(output.amount * inc,
                                        liquidCapacity - liquids.get(output.liquid)));
                    }
                }

                if (wasVisible && Mathf.chanceDelta(updateEffectChance)) {
                    updateEffect.at(
                            x + Mathf.range(size * updateEffectSpread),
                            y + Mathf.range(size * updateEffectSpread)
                    );
                }
            } else {
                efficiency = 0f;
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            totalProgress += warmup * Time.delta;

            if (progress >= 1f && currentRecipe != null) {
                craft();
            }

            dumpOutputs();
        }

        private Recipe selectRecipe() {
            for (Recipe recipe : recipes) {
                if (recipe.efficiency(this) > 0f || canAcceptAny(recipe)) {
                    return recipe;
                }
            }
            return null;
        }

        private boolean canAcceptAny(Recipe recipe){
            for(var consume : recipe.consumers){
                if(consume instanceof ConsumeItems || consume instanceof ConsumeLiquids){
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            if (!block.hasItems) return false;

            for (Recipe recipe : recipes) {
                if (recipe.acceptsItem(item, this)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if (!block.hasLiquids) return false;

            if (block.liquidFilter != null) {
                int id = liquid.id;
                if (id < 0 || id >= block.liquidFilter.length || !block.liquidFilter[id]) {
                    return false;
                }
            }

            for (Recipe recipe : recipes) {
                if (recipe.acceptsLiquid(liquid) &&
                        liquids.get(liquid) < liquidCapacity) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount) {
            if (!acceptLiquid(source, liquid)) return;

            liquids.add(liquid,
                    Math.min(amount, liquidCapacity - liquids.get(liquid)));
        }

        @Override
        public void craft() {
            // trigger item consumption
            currentRecipe.trigger(this);

            if (currentRecipe.outputItems != null) {
                for (var output : currentRecipe.outputItems) {
                    for (int i = 0; i < output.amount; i++) {
                        offload(output.item);
                    }
                }
            }

            if (wasVisible) {
                craftEffect.at(x, y);
            }

            progress %= 1f;
        }

        public float getPowerUsage(){
            if(currentRecipe == null) return 0f;
            return currentRecipe.powerf.get(this) * warmup;
        }
    }
}