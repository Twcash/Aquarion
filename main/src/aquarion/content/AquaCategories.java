package aquarion.content;

import mindustry.type.Category;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class AquaCategories {
    public static Category aquaCategory;

    public static void init() {
        try {
            Constructor<Category> constructor = Category.class.getDeclaredConstructor(String.class, int.class);
            constructor.setAccessible(true);
            
            int newId = Category.all.length;
            aquaCategory = constructor.newInstance("aquaCategory", newId);

            Category[] newCategories = new Category[newId + 1];
            System.arraycopy(Category.all, 0, newCategories, 0, newId);
            newCategories[newId] = aquaCategory;

            Field allField = Category.class.getDeclaredField("all");
            allField.setAccessible(true);
            allField.set(null, newCategories);

        } catch (Exception e) {
            System.err.println("Failed to inject custom category via Reflection: " + e.getMessage());
            aquaCategory = Category.distribution;
        }
    }
}
