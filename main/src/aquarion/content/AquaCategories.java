package aquarion.content;

import mindustry.type.Category;

public class AquaCategories {
    public static Category aquaCategory;

    public static void init() {
        try {
            java.lang.reflect.Constructor<Category> constructor = Category.class.getDeclaredConstructor(String.class, int.class);
            constructor.setAccessible(true);
            
            int newId = Category.all.length;
            aquaCategory = constructor.newInstance("aquaCategory", newId);

            Category[] newCategories = new Category[newId + 1];
            System.arraycopy(Category.all, 0, newCategories, 0, newId);
            newCategories[newId] = aquaCategory;

            java.lang.reflect.Field allField = Category.class.getDeclaredField("all");
            allField.setAccessible(true);
            allField.set(null, newCategories);
        } catch (Exception e) {
            System.err.println("Failed to inject custom category: " + e.getMessage());
            aquaCategory = Category.distribution;
        }
    }
}
