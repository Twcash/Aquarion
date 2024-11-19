package aquarion;
import mindustry.world.meta.Attribute;

public class AquaAttributes {
    public static Attribute
            bauxite,
            gallium,
    chromium;

    public static void load() {
        bauxite = Attribute.add("bauxite");
        gallium = Attribute.add("gallium");
        gallium = Attribute.add("gallium");
        chromium = Attribute.add("chromium");
    }
}