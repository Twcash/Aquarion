package aquarion.content;

import arc.util.Reflect;
import mindustry.type.Category;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class AquaCategories {

    public static Category refinery;
    public static Category heat;

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        try {
            Category[] original = Category.all;
            int nextOrdinal = original.length;

            refinery = Reflect.constructor(Category.class, String.class, int.class).newInstance("refinery", nextOrdinal);
            heat = Reflect.constructor(Category.class, String.class, int.class).newInstance("heat", nextOrdinal + 1);

            Category[] extended = new Category[original.length + 2];
            System.arraycopy(original, 0, extended, 0, original.length);
            extended[original.length] = refinery;
            extended[original.length + 1] = heat;

            Reflect.set(Category.class, "all", extended);

            for (Field f : Category.class.getDeclaredFields()) {
                if (f.getType() == Category[].class && !f.getName().equals("all")) {
                    Reflect.set(Category.class, f.getName(), extended);
                    break;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Custom Category failed to initialize", e);
        }
    }
}
