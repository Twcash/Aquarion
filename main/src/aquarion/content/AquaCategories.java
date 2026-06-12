package aquarion.content;

import mindustry.type.Category;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class AquaCategories {
    public static Category aquaCategory;
    public static Category heat;
    public static Category refinery;

    public static void init() {
        try {
            Constructor<Category> constructor = Category.class.getDeclaredConstructor(String.class, int.class);
            constructor.setAccessible(true);
            
            int id = Category.all.length;
            
            aquaCategory = constructor.newInstance("aquaCategory", id++);
            heat = constructor.newInstance("heat", id++);
            refinery = constructor.newInstance("refinery", id++);

            Category[] newCategories = new Category[id];
            System.arraycopy(Category.all, 0, newCategories, 0, Category.all.length);
            
            newCategories[Category.all.length] = aquaCategory;
            newCategories[Category.all.length + 1] = heat;
            newCategories[Category.all.length + 2] = refinery;

            Field allField = Category.class.getDeclaredField("all");
            allField.setAccessible(true);
            allField.set(null, newCategories);

        } catch (Exception e) {
            System.err.println("Failed to inject custom categories: " + e.getMessage());
            aquaCategory = Category.distribution;
            heat = Category.distribution;
            refinery = Category.distribution;
        }
    }
}
