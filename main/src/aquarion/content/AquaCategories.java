package aquarion.content;

import mindustry.type.Category;
import java.lang.reflect.Constructor;

public class AquaCategories {
    public static Category aquaCategory;

    public static void init() {
        try {
            Constructor<Category> constructor = Category.class.getDeclaredConstructor(String.class, int.class);
            constructor.setAccessible(true);
            
            int id = Category.all.length;
            aquaCategory = constructor.newInstance("aquaCategory", id);

            Category[] newCategories = new Category[id + 1];
            System.arraycopy(Category.all, 0, newCategories, 0, id);
            newCategories[id] = aquaCategory;

            java.lang.reflect.Field allField = Category.class.getDeclaredField("all");
            allField.setAccessible(true);
            allField.set(null, newCategories);
        } catch (Exception e) {
            throw new RuntimeException("Custom Category failed to initialize in v158.1: " + e.getMessage(), e);
        }
    }
}
