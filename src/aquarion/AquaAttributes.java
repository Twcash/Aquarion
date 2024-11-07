package aquarion;
import mindustry.world.meta.Attribute;

public class AquaAttributes {
    public static Attribute
            bauxite,
            gallium;

    public static void load() {
        bauxite = Attribute.add("bauxite");
        gallium = Attribute.add("gallium"); // No changes needed here
    }
}