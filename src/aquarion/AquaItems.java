package aquarion;

import aquarion.world.content.AquaItem;
import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.type.Item;

import static aquarion.planets.AquaPlanets.*;
import static mindustry.content.Items.*;

public class AquaItems {
    public static Item serpentine, pentlandite, magnesiumPowder, steel, acuminite, strontium, hexogen, coke, galena, boron, cryolite, caustrolite, mangalumin, vacodur, towanite, brimstone, cobalt, azurite, invar, borax, ferrosilicon, aluminum, ferricMatter,cupronickel, inconel, electrum, arsenic, chromium, silver, salt, chirenium, nickel, bauxite, duralumin, gallium, lithium, manganese, sodium, ceramic;
    public static void load() {
        Items.lead.alwaysUnlocked = true;
        Items.silicon.alwaysUnlocked = true;
        bauxite = new Item("bauxite", Color.valueOf("#895841")) {{
            hardness = 3;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            cost = 1.1F;
            alwaysUnlocked = true;
        }};
        salt = new Item("salt", Color.valueOf("#ffffff")) {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};

        duralumin = new Item("duralumin", Color.valueOf("#549af7")) {{
            hardness = 3;
            cost = 2;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        nickel = new AquaItem("nickel", Color.valueOf("#c3b994")) {{
            hardness = 2;
            alwaysUnlocked = true;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            cost = 1.5f;
            magnetism = 1;
        }};
        chirenium = new Item("chirenium", Color.valueOf("b2acb5")){{
            cost = 3;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        gallium = new Item("gallium", Color.valueOf("#b2d3cf")) {{
            hardness = 4;
            cost = 2;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

        }};

        lithium = new Item("lithium", Color.valueOf("#b2d3cf")) {{
            hardness = 4;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            explosiveness = 0.2F;
        }};

        manganese = new Item("manganese", Color.valueOf("#75d978")) {{
            cost = 1.2F;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        sodium = new Item("sodium", Color.valueOf("#ded6ca")) {{
            explosiveness = 2F;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            cost = 1.5F;
        }};
        ceramic = new Item("ceramic", Color.valueOf("#ffffff")) {{
            cost = 1.5f;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        electrum = new Item("electrum", Color.valueOf("#eedeaf")) {{
            cost = 0.8f;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        silver = new Item("silver", Color.valueOf("#d7e0e4")) {{
            cost = 0.9f;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        arsenic = new Item("arsenic", Color.valueOf("#ff8f8f")) {{
            cost = 0.9f;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        cupronickel = new AquaItem("cupronickel", Color.valueOf("#dfaf88")) {{
            cost = 1f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            magnetism = 0.5f;
        }};
        chromium = new Item("chromium", Color.valueOf("#d2c8d9")) {{
            cost = 1f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        inconel = new AquaItem("inconel", Color.valueOf("#e9ecd2")) {{
            cost = 1f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            magnetism =0.75f;
        }};
        ferricMatter = new AquaItem("ferric-matter", Color.valueOf("#675b53")) {{
            cost = 0.5f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            radioactivity = 0.05f;
            magnetism = 1;
            hardness = 4;
        }};
        aluminum = new Item("aluminum", Color.valueOf("#7489a5")) {{
            cost = 0.3f;
            flammability = 0.1f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        ferrosilicon = new AquaItem("ferrosilicon", Color.valueOf("#98a1ab")) {{
            cost = 0.5f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            magnetism = 0.5f;
        }};
        borax = new Item("borax", Color.valueOf("#d4ceb1")) {{
            buildable = false;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        invar = new AquaItem("invar", Color.valueOf("#cbaa6d")) {{
            cost = 0.6f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

        }};
        azurite = new Item("azurite", Color.valueOf("#636acf")) {{
            buildable = false;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);

        }};
        towanite = new Item("towanite", Color.valueOf("#faffcd")) {{
            buildable = false;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        cobalt = new AquaItem("cobalt", Color.valueOf("#8c96bb")) {{
            cost = 0.9f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            magnetism = 1;
        }};
        brimstone = new Item("brimstone", Color.valueOf("#fafd81")) {{
            cost = 0.6f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            explosiveness = 0.5f;
            flammability = 0.7f;
        }};
        vacodur = new Item("vacodur", Color.valueOf("#99899d")) {{
            cost = 1.1f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        mangalumin = new Item("mangalumin", Color.valueOf("# 93d588")) {{
            cost = 1.2f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        caustrolite = new Item("caustrolite", Color.valueOf("# 93d588")) {{
            cost = 2f;
            flammability = 1.5f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            explosiveness = 1.1f;

        }};
        galena = new Item("galena", Color.valueOf("d9cff2")) {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        boron = new Item("boron", Color.valueOf("d9cff2")) {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        coke = new Item("coke", Color.valueOf("8e947c")) {{
            buildable = false;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            flammability = 1f;
            explosiveness = 0.1f;
            radioactivity = 0.05f;
        }};
        hexogen = new Item("hexogen", Color.valueOf("fff47a")) {{
            explosiveness = 8f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            radioactivity = 0.2f;
        }};
        strontium = new AquaItem("strontium", Color.valueOf("b99e82")){{
            magnetism = 0.8f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        acuminite = new Item("acuminite", Color.valueOf("fff475")){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        steel = new Item("steel", Color.valueOf("ffffff")){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        magnesiumPowder = new Item("magnesium-powder", Color.valueOf("ffffff")){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            flammability = 0.4f;
            explosiveness = 0.8f;
        }};
        serpentine = new Item("serpentine", Color.valueOf("e7e58f")){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            hardness = 2;
        }};
        pentlandite = new Item("pentlandite", Color.valueOf("9a948b")){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            hardness = 2;
        }};
    }
}
