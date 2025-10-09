package aquarion;

import aquarion.world.content.AquaItem;
import arc.graphics.Color;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.type.Item;

import static aquarion.planets.AquaPlanets.*;

public class AquaItems {
    public static Item polymer;
    public static Item serpentine;
    public static Item pentlandite;
    public static Item magnesiumPowder;
    public static Item steel;
    public static Item chalkalloy;
    public static Item acuminite;
    public static Item strontium;
    public static Item hexogen;
    public static Item galena;
    public static Item boron;
    public static Item biotite;
    public static Item cryolite;
    public static Item caustrolite;
    public static Item mangalumin;
    public static Item vacodur;
    public static Item towanite;
    public static Item brimstone;
    public static Item cobalt;
    public static Item azurite;
    public static Item invar;
    public static Item borax;
    public static Item ferrosilicon;
    public static Item aluminum;
    public static Item ferricMatter;
    public static Item cupronickel;
    public static Item inconel;
    public static Item electrum;
    public static Item arsenic;
    public static Item chromium;
    public static Item silver;
    public static Item salt;
    public static Item chirenium;
    public static Item nickel;
    public static Item bauxite;
    public static Item duralumin;
    public static Item zinc;
    public static Item gallium;
    public static Item lithium;
    public static Item manganese;
    public static Item sodium;
    public static Item ceramic;
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
            cost = 1.1f;
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
        zinc = new Item("zinc", Color.valueOf("#a0b9af")) {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            hardness = 3;
            alwaysUnlocked = true;
        }};
        biotite = new Item("biotite", Color.valueOf("#2e3e43")) {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        silver = new Item("silver", Color.valueOf("#d7e0e4")) {{
            cost = 0.9f;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
        arsenic = new Item("arsenic", Color.valueOf("#ff8f8f")) {{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            cost = 0.9f;
        }};
        cupronickel = new AquaItem("cupronickel", Color.valueOf("#dfaf88")) {{
            cost = 1f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            magnetism = 0.5f;
        }};
        polymer = new AquaItem("polymer", Color.valueOf("#eaeadf")) {{
            cost = 1.3f;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
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
            buildable = false;
        }};
        serpentine = new Item("serpentine", Color.valueOf("e7e58f")){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            hardness = 1;
        }};
        pentlandite = new Item("pentlandite", Color.valueOf("9a948b")){{
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            hardness = 2;
        }};
        chalkalloy = new Item("chalkalloy", Color.valueOf("#9dccba")) {{
            cost = 1.1f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
        }};
    }
}
