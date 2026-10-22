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
                var constructor = Category.class.getDeclaredConstructor(String.class, int.class);
                constructor.setAccessible(true);
                // Use the actual enum constructor without Constructor.newInstance's enum restriction.
                // Reflective method-handle lookup keeps newer Android API types out of class signatures.
                Class<?> handles = Class.forName("java.lang.invoke.MethodHandles");
                Object lookup = handles.getMethod("lookup").invoke(null);
                Class<?> lookupType = Class.forName("java.lang.invoke.MethodHandles$Lookup");
                Object factory = lookupType.getMethod("unreflectConstructor", java.lang.reflect.Constructor.class)
                    .invoke(lookup, constructor);
                Method invoke = Class.forName("java.lang.invoke.MethodHandle")
                    .getMethod("invokeWithArguments", Object[].class);
                refinery = (Category)invoke.invoke(factory, new Object[]{new Object[]{"refinery", nextOrdinal}});
                heat = (Category)invoke.invoke(factory, new Object[]{new Object[]{"heat", nextOrdinal + 1}});
                railways = (Category)invoke.invoke(factory, new Object[]{new Object[]{"railways", nextOrdinal + 2}});
                extended[original.length] = refinery;
                extended[original.length + 1] = heat;
                extended[original.length + 2] = railways;
                Field allField = Category.class.getDeclaredField("all");
                setCategoryArray(allField, extended);
                for (Field f : Category.class.getDeclaredFields()) {
                    if (f.getType() == Category[].class && !f.getName().equals("all")) {
                        setCategoryArray(f, extended);
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
    private static void setCategoryArray(Field field, Category[] values) throws ReflectiveOperationException{
        // ART allows accessible field writes; its Unsafe omits the desktop static-field methods.
        field.setAccessible(true);
        try{
            field.set(null, values);
            return;
        }catch(IllegalAccessException desktopFinalField){
            // Desktop Java requires Unsafe to replace static final enum arrays.
        }
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Object unsafe = unsafeField.get(null);
        Method putObject = unsafeClass.getMethod("putObject", Object.class, long.class, Object.class);
        Method offsetMethod = unsafeClass.getMethod("staticFieldOffset", Field.class);
        Method baseMethod = unsafeClass.getMethod("staticFieldBase", Field.class);
        long offset = (long)offsetMethod.invoke(unsafe, field);
        Object base = baseMethod.invoke(unsafe, field);
        putObject.invoke(unsafe, base, offset, values);
    }
}
