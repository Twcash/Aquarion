package aquarion.content;

import arc.util.Reflect;
import mindustry.type.Category;

public class AquaCategories {

    public static Category refinery;
    public static Category heat;

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        try {
            refinery = new Category("refinery");
            heat = new Category("heat");

            Category[] original = Category.all;
            Category[] extended = new Category[original.length + 2];
            System.arraycopy(original, 0, extended, 0, original.length);
            
            extended[original.length] = refinery;
            extended[original.length + 1] = heat;

            Reflect.set(Category.class, "all", extended);

        } catch (Exception e) {
            throw new RuntimeException("Custom Category failed to initialize", e);
        }
    }
}
