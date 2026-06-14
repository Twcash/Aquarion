package aquarion.content;

import arc.util.Reflect;
import mindustry.type.Category;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AquaCategories {

    public static Category refinery;
    public static Category heat;

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Object unsafe = unsafeField.get(null);

            Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
            Method objectFieldOffset = unsafeClass.getMethod("objectFieldOffset", Field.class);
            Method putObject = unsafeClass.getMethod("putObject", Object.class, long.class, Object.class);
            Method putInt = unsafeClass.getMethod("putInt", Object.class, long.class, int.class);

            refinery = (Category) allocateInstance.invoke(unsafe, Category.class);
            heat = (Category) allocateInstance.invoke(unsafe, Category.class);

            Field nameField = Enum.class.getDeclaredField("name");
            long nameOffset = (long) objectFieldOffset.invoke(unsafe, nameField);
            Field ordinalField = Enum.class.getDeclaredField("ordinal");
            long ordinalOffset = (long) objectFieldOffset.invoke(unsafe, ordinalField);

            Category[] oldValues = Category.all;
            int nextOrdinal = oldValues.length;

            putObject.invoke(unsafe, refinery, nameOffset, "refinery");
            putInt.invoke(unsafe, refinery, ordinalOffset, nextOrdinal);
            putObject.invoke(unsafe, heat, nameOffset, "heat");
            putInt.invoke(unsafe, heat, ordinalOffset, nextOrdinal + 1);

            Category[] newValues = new Category[oldValues.length + 2];
            System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
            newValues[oldValues.length] = refinery;
            newValues[oldValues.length + 1] = heat;

            Reflect.set(Category.class, "all", newValues);

            for (Field f : Category.class.getDeclaredFields()) {
                if (f.getType() == Category[].class && !f.getName().equals("all")) {
                    Reflect.set(Category.class, f.getName(), newValues);
                    break;
                }
            }

            try {
                Field constantDir = Class.class.getDeclaredField("enumConstantDirectory");
                constantDir.setAccessible(true);
                constantDir.set(Category.class, null);
            } catch (Exception ignored) {
            }

        } catch (Exception e) {
            throw new RuntimeException("Custom Category failed to initialize", e);
        }
    }
}
