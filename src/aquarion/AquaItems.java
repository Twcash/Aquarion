package aquarion;

import arc.graphics.Color;
import mindustry.type.Item;

public class AquaItems {
    public static Item bauxite, duralumin, gallium, lithium, lithoniteAlloy, manganese, nitride, sodium, ceramic;

    public static void loadContent() {
        bauxite = new Item("bauxite", Color.valueOf("#895841")) {{
            hardness = 3;
            cost = 1.1F;
        }};

        duralumin = new Item("duralumin", Color.valueOf("#549af7")) {{
            hardness = 3;
            cost = 2;
        }};

        gallium = new Item("gallium", Color.valueOf("#b2d3cf")) {{
            hardness = 4;
            cost = 2;
        }};

        lithium = new Item("lithium", Color.valueOf("#b2d3cf")) {{
            hardness = 4;
            explosiveness = 0.2F;
        }};

        lithoniteAlloy = new Item("lithonite-alloy", Color.valueOf("#444b49")) {{
            cost = 4;
        }};

        manganese = new Item("manganese", Color.valueOf("#a0ac9d")) {{
            cost = 1.5F;
            hardness = 5;
        }};

        nitride = new Item("nitride", Color.valueOf("#ded6ca")) {{
            explosiveness = 0.2F;
        }};

        sodium = new Item("sodium", Color.valueOf("#ded6ca")) {{
            explosiveness = 0.2F;
            cost = 1.5F;
        }};
        ceramic = new Item("ceramic", Color.valueOf("#ffffff")) {{
            cost = 1.5f;
        }};
    }
}
