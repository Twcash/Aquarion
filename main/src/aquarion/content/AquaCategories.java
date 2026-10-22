package aquarion.content;

import mindustry.type.Category;

import java.lang.reflect.Constructor;
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
            Category[] original = Category.all;
            int nextOrdinal = original.length;
            Category[] extended = new Category[original.length + 2];
            System.arraycopy(original, 0, extended, 0, original.length);

            try {
                Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                Object unsafe = unsafeField.get(null);

                Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
                Method objectFieldOffset = unsafeClass.getMethod("objectFieldOffset", Field.class);
                Method putObject = unsafeClass.getMethod("putObject", Object.class, long.class, Object.class);
                Method putInt = unsafeClass.getMethod("putInt", Object.class, long.class, int.class);
                Method staticFieldOffset = unsafeClass.getMethod("staticFieldOffset", Field.class);
                Method staticFieldBase = unsafeClass.getMethod("staticFieldBase", Field.class);

                refinery = (Category) allocateInstance.invoke(unsafe, Category.class);
                heat = (Category) allocateInstance.invoke(unsafe, Category.class);

                Field nameField = Enum.class.getDeclaredField("name");
                long nameOffset = (long) objectFieldOffset.invoke(unsafe, nameField);
                Field ordinalField = Enum.class.getDeclaredField("ordinal");
                long ordinalOffset = (long) objectFieldOffset.invoke(unsafe, ordinalField);

                putObject.invoke(unsafe, refinery, nameOffset, "refinery");
                putInt.invoke(unsafe, refinery, ordinalOffset, nextOrdinal);
                putObject.invoke(unsafe, heat, nameOffset, "heat");
                putInt.invoke(unsafe, heat, ordinalOffset, nextOrdinal + 1);

                extended[original.length] = refinery;
                extended[original.length + 1] = heat;

                Field allField = Category.class.getDeclaredField("all");
                long allOffset = (long) staticFieldOffset.invoke(unsafe, allField);
                Object allBase = staticFieldBase.invoke(unsafe, allField);
                putObject.invoke(unsafe, allBase, allOffset, extended);

                for (Field f : Category.class.getDeclaredFields()) {
                    if (f.getType() == Category[].class && !f.getName().equals("all")) {
                        long fOffset = (long) staticFieldOffset.invoke(unsafe, f);
                        Object fBase = staticFieldBase.invoke(unsafe, f);
                        putObject.invoke(unsafe, fBase, fOffset, extended);
                        break;
                    }
                }
            } catch (Throwable e) {
                Constructor<Category> constructor = Category.class.getDeclaredConstructor(String.class, int.class);
                constructor.setAccessible(true);

                refinery = constructor.newInstance("refinery", nextOrdinal);
                heat = constructor.newInstance("heat", nextOrdinal + 1);

                extended[original.length] = refinery;
                extended[original.length + 1] = heat;

                Field allField = Category.class.getDeclaredField("all");
                allField.setAccessible(true);
                allField.set(null, extended);

                for (Field f : Category.class.getDeclaredFields()) {
                    if (f.getType() == Category[].class && !f.getName().equals("all")) {
                        f.setAccessible(true);
                        f.set(null, extended);
                        break;
                    }
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
