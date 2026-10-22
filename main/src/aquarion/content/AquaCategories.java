package aquarion.content;

import mindustry.type.Category;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AquaCategories {

    public static Category refinery;
    public static Category heat;
    public static Category railways;
    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;

        try {
            Category[] original = Category.all;
            int nextOrdinal = original.length;
            Category[] extended = new Category[original.length + 3];
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

                refinery = (Category) allocateInstance.invoke(unsafe, Category.class);
                heat = (Category) allocateInstance.invoke(unsafe, Category.class);
                railways = (Category) allocateInstance.invoke(unsafe, Category.class);

                Field nameField = Enum.class.getDeclaredField("name");
                long nameOffset = (long) objectFieldOffset.invoke(unsafe, nameField);
                Field ordinalField = Enum.class.getDeclaredField("ordinal");
                long ordinalOffset = (long) objectFieldOffset.invoke(unsafe, ordinalField);

                putObject.invoke(unsafe, refinery, nameOffset, "refinery");
                putInt.invoke(unsafe, refinery, ordinalOffset, nextOrdinal);
                putObject.invoke(unsafe, heat, nameOffset, "heat");
                putInt.invoke(unsafe, heat, ordinalOffset, nextOrdinal + 1);
                putObject.invoke(unsafe, railways, nameOffset, "railways");
                putInt.invoke(unsafe, railways, ordinalOffset, nextOrdinal + 2);
                extended[original.length] = refinery;
                extended[original.length + 1] = heat;
                extended[original.length + 2] = railways;
                Field allField = Category.class.getDeclaredField("all");
                setCategoryArray(allField, extended, unsafeClass, unsafe, putObject);
                for (Field f : Category.class.getDeclaredFields()) {
                    if (f.getType() == Category[].class && !f.getName().equals("all")) {
                        setCategoryArray(f, extended, unsafeClass, unsafe, putObject);
                        break;
                    }
                }
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Could not extend build categories", e);
            }

            try {
                Field constantDir = Class.class.getDeclaredField("enumConstantDirectory");
                constantDir.setAccessible(true);
                constantDir.set(Category.class, null);
            } catch (Exception ignored) {
            }

            initialized = true;
        } catch (Exception e) {
            throw new RuntimeException("Custom Category failed to initialize", e);
        }
    }
    private static void setCategoryArray(Field field, Category[] values, Class<?> unsafeClass,
                                         Object unsafe, Method putObject) throws ReflectiveOperationException{
        // ART allows accessible field writes; its Unsafe omits the desktop static-field methods.
        field.setAccessible(true);
        try{
            field.set(null, values);
            return;
        }catch(IllegalAccessException desktopFinalField){
            // Desktop Java requires Unsafe to replace static final enum arrays.
        }
        Method offsetMethod = unsafeClass.getMethod("staticFieldOffset", Field.class);
        Method baseMethod = unsafeClass.getMethod("staticFieldBase", Field.class);
        long offset = (long)offsetMethod.invoke(unsafe, field);
        Object base = baseMethod.invoke(unsafe, field);
        putObject.invoke(unsafe, base, offset, values);
    }
}