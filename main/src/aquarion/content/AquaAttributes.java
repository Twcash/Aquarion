package aquarion.content;
import mindustry.world.meta.Attribute;

public class AquaAttributes {
    public static Attribute
            iron,
            fertility,
            bauxite,
            gallium,
    metamorphic,
    chromium;

    public static void load() {
        bauxite = Attribute.add("bauxite");
        gallium = Attribute.add("gallium");
        metamorphic = Attribute.add("metamorphic");
        chromium = Attribute.add("chromium");
        iron = Attribute.add("iron");
        fertility = Attribute.add("fertility");
    }
}