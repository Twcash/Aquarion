package aquarion;

import arc.files.Fi;
import arc.struct.Seq;
import mindustry.game.Schematic;
import mindustry.game.Schematics;

public class SchematicLoader {

    public static Seq<Schematic> loadSchematics(String folderPath) {
        Seq<Schematic> schematics = new Seq<>();

        Fi folder = new Fi(folderPath);
        if (!folder.exists() || !folder.isDirectory()) return schematics;

        for (Fi file : folder.list()) {
            if (file.name().endsWith(".msch")) {
                try {
                    schematics.add(Schematics.read(file));
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        return schematics;
    }
}
