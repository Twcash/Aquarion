package aquarion.content;

import arc.util.EnumHelper;
import mindustry.type.Category;

public class AquaCategories {
    public static Category aquaCategory;

    public static void init() {
        try {
            aquaCategory = EnumHelper.add(Category.class, "aquaCategory");
        } catch (Exception e) {
            throw new RuntimeException("Custom Category failed to initialize in v158.1: " + e.getMessage(), e);
        }
    }
}
