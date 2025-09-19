package aquarion.world.blocks.production;

import aquarion.world.consumers.Recipe;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class AdaptiveCrafter extends GenericCrafter {
    public Seq<Recipe> recipes = new Seq<>();
    public float updateEffectChance = 0.04f;
    public float updateEffectSpread = 4f;

    public AdaptiveCrafter(String name) {
        super(name);
    }

    public AdaptiveCrafter addRecipe(Recipe recipe) {
        recipes.add(recipe);
        return this;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.productionTime, "Variable", StatUnit.seconds);
    }

    public class AdaptiveCrafterBuild extends GenericCrafterBuild {
        private Recipe currentRecipe;

        @Override
        public void updateTile() {
            if (currentRecipe == null || !currentRecipe.isValid(this)) {
                currentRecipe = selectRecipe();
            }

            if (currentRecipe != null) {
                craftTime = currentRecipe.craftTime;
                efficiency = currentRecipe.efficiency(this);

                progress += getProgressIncrease(craftTime);
                warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

                // Handle output liquids
                if (outputLiquids != null) {
                    float inc = getProgressIncrease(1f);
                    for (var output : outputLiquids) {
                        handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                    }
                }

                // Update effect with a chance
                if (wasVisible && Mathf.chanceDelta(updateEffectChance)) {
                    updateEffect.at(x + Mathf.range(size * updateEffectSpread), y + Mathf.range(size * updateEffectSpread));
                }
            } else {
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            totalProgress += warmup * Time.delta;

            if (progress >= 1f) {
                craft();
            }

            dumpOutputs();
        }

        private Recipe selectRecipe() {
            for (Recipe recipe : recipes) {
                if (recipe.isValid(this)) {
                    return recipe;
                }
            }
            return null;
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
        public void craft() {
            consume();

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
        @Override
        public boolean shouldConsume() {
            if (outputItems != null) {
                for (var output : outputItems) {
                    if (items.get(output.item) + output.amount > itemCapacity) {
                        return false;
                    }
                }
            }
            if (outputLiquids != null && !ignoreLiquidFullness) {
                boolean allFull = true;
                for (var output : outputLiquids) {
                    if (liquids.get(output.liquid) >= liquidCapacity - 0.001f) {
                        if (!dumpExtraLiquid) {
                            return false;
                        }
                    } else {
                        allFull = false;
                    }
                }

                if (allFull) {
                    return false;
                }
            }

            return enabled;
        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount) {
            for (Recipe recipe : recipes) {
                if (recipe.acceptsLiquid(liquid)) {
                    liquids.add(liquid, Math.min(amount, liquidCapacity - liquids.get(liquid)));
                    return;
                }
            }
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if (!block.hasLiquids) return false;

            for (Recipe recipe : recipes) {
                if ((recipe.acceptsLiquid(liquid) && this.liquids.get(liquid) < liquidCapacity) || currentRecipe.outputLiquids != null) {
                    return true;
                }
            }
            return false;
        }
    }
}
