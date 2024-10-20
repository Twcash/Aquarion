package aquarion;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.type.Item;

import static mindustry.content.Items.lead;

public class AquaItems {
    public static Item salt, chirenium, nickel, bauxite, duralumin, gallium, lithium, lithoniteAlloy, manganese, nitride, sodium, ceramic;
    public static final Seq<Item> tantrosItems = new Seq<>();
    public static void load() {
        bauxite = new Item("bauxite", Color.valueOf("#895841")) {{
            hardness = 3;
            cost = 1.1F;
        }};
        salt = new Item("salt", Color.valueOf("#ffffff")) {{
            cost = 1.1F;
        }};

        duralumin = new Item("duralumin", Color.valueOf("#549af7")) {{
            hardness = 3;
            cost = 2;
        }};
        nickel = new Item("nickel", Color.valueOf("#c3b994")) {{
            hardness = 4;
            cost = 2.5f;
        }};
        chirenium = new Item("chirenium", Color.valueOf("b2acb5")){{
            cost = 3;
        }};
        gallium = new Item("gallium", Color.valueOf("#b2d3cf")) {{
            hardness = 4;
            cost = 2;
        }};

        lithium = new Item("lithium", Color.valueOf("#b2d3cf")) {{
            hardness = 4;
            explosiveness = 0.2F;
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
        tantrosItems.addAll(
                ceramic, sodium, nitride, manganese,
                lithium, gallium, chirenium, bauxite,
                duralumin, nickel, salt, lead
        );
    }

}
