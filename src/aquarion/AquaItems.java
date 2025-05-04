package aquarion;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.type.Item;

import static mindustry.content.Items.*;

public class AquaItems {
    public static Item coke, galena, boron, cryolite, caustrolite, mangalumin, vacodur, towanite, brimstone, cobalt, azurite, invar, borax, ferrosilicon, aluminum, ferricMatter,cupronickel, inconel, electrum, arsenic, chromium, silver, salt, chirenium, nickel, bauxite, duralumin, gallium, lithium, manganese, sodium, ceramic;
    public static final Seq<Item> tantrosItems = new Seq<>();
    public static final Seq<Item> qeraltarItems = new Seq<>();
    public static void load() {
        Items.lead.alwaysUnlocked = true;
        Items.silicon.alwaysUnlocked = true;
        bauxite = new Item("bauxite", Color.valueOf("#895841")) {{
            hardness = 3;
            cost = 1.1F;
            alwaysUnlocked = true;
        }};
        salt = new Item("salt", Color.valueOf("#ffffff")) {{
            cost = 1.1F;
        }};

        duralumin = new Item("duralumin", Color.valueOf("#549af7")) {{
            hardness = 3;
            cost = 2;
        }};
        nickel = new Item("nickel", Color.valueOf("#c3b994")) {{
            hardness = 2;
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

        manganese = new Item("manganese", Color.valueOf("#75d978")) {{
            cost = 1.5F;
            hardness = 5;
        }};
        sodium = new Item("sodium", Color.valueOf("#ded6ca")) {{
            explosiveness = 0.2F;
            cost = 1.5F;
        }};
        ceramic = new Item("ceramic", Color.valueOf("#ffffff")) {{
            cost = 1.5f;
        }};
        electrum = new Item("electrum", Color.valueOf("#eedeaf")) {{
            cost = 0.8f;
        }};
        silver = new Item("silver", Color.valueOf("#d7e0e4")) {{
            cost = 0.9f;
        }};
        arsenic = new Item("arsenic", Color.valueOf("#ff8f8f")) {{
            cost = 0.9f;
        }};
        cupronickel = new Item("cupronickel", Color.valueOf("#dfaf88")) {{
            cost = 1f;
        }};
        chromium = new Item("chromium", Color.valueOf("#d2c8d9")) {{
            cost = 1f;
        }};
        inconel = new Item("inconel", Color.valueOf("#e9ecd2")) {{
            cost = 1f;
        }};
        ferricMatter = new Item("ferric-matter", Color.valueOf("#675b53")) {{
            cost = 0.5f;
            radioactivity = 0.05f;
        }};
        aluminum = new Item("aluminum", Color.valueOf("#7489a5")) {{
            cost = 0.3f;
            flammability = 0.1f;
        }};
        ferrosilicon = new Item("ferrosilicon", Color.valueOf("#98a1ab")) {{
            cost = 0.5f;
        }};
        borax = new Item("borax", Color.valueOf("#d4ceb1")) {{
            buildable = false;
        }};
        invar = new Item("invar", Color.valueOf("#cbaa6d")) {{
            cost = 0.6f;
        }};
        azurite = new Item("azurite", Color.valueOf("#636acf")) {{
            buildable = false;

        }};
        towanite = new Item("towanite", Color.valueOf("#faffcd")) {{
            buildable = false;
        }};
        cobalt = new Item("cobalt", Color.valueOf("#8c96bb")) {{
            cost = 0.9f;
        }};
        brimstone = new Item("brimstone", Color.valueOf("#fafd81")) {{
            cost = 0.6f;
            explosiveness = 0.5f;
            flammability = 0.7f;
        }};
        vacodur = new Item("vacodur", Color.valueOf("#99899d")) {{
            cost = 1.1f;
        }};
        mangalumin = new Item("mangalumin", Color.valueOf("# 93d588")) {{
            cost = 1.2f;
        }};
        caustrolite = new Item("caustrolite", Color.valueOf("# 93d588")) {{
            cost = 2f;
            flammability = 1.5f;
            explosiveness = 1.1f;

        }};
        galena = new Item("galena", Color.valueOf("d9cff2")) {{
        }};
        boron = new Item("boron", Color.valueOf("d9cff2")) {{
        }};
        coke = new Item("coke", Color.valueOf("8e947c")) {{
            hardness = 3;
            buildable = false;
            flammability = 1f;
            explosiveness = 0.1f;
            radioactivity = 0.05f;
        }};
        qeraltarItems.addAll(
           lead, copper, electrum, silver,
                nickel, cupronickel, titanium, arsenic,
                ceramic, silicon, inconel
        );

        tantrosItems.addAll(
                lead, bauxite, silicon, ferricMatter, aluminum, ferrosilicon, salt, borax, invar, nickel, boron, cryolite
        );
    }

}
