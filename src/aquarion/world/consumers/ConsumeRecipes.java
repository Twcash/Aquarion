package aquarion.world.consumers;

import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.world.consumers.Consume;

public class ConsumeRecipes extends Consume {
    public Seq<Recipe> recipes = new Seq<>();
    private Recipe currentRecipe = null;

    public ConsumeRecipes(Recipe... recipes) {
        for (Recipe recipe : recipes) {
            this.recipes.add(recipe);
        }
    }

    @Override
    public void update(Building build) {
        currentRecipe = findValidRecipe(build);
        if (currentRecipe != null) {
            for (Consume consumer : currentRecipe.consumers) {
                consumer.update(build);
            }
        }
    }

    private Recipe findValidRecipe(Building build) {
        for (Recipe recipe : recipes) {
            if (recipe.isValid(build)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public boolean ignore() {
        return recipes.isEmpty();
    }
}