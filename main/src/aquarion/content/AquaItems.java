package aquarion.content;

import aquarion.world.content.AquaItem;
import arc.graphics.Color;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.type.Item;

import static aquarion.content.AquaPlanets.*;

public class AquaItems {
    public static Item polymer, cuprite;
    public static Item serpentine;
    public static Item pentlandite;
    public static Item magnesiumPowder;
    public static Item steel, brass;
    public static Item minium, uranium, radium, pitchblende;
    public static Item chalkalloy, crystal, pearl;
    public static Item acuminite;
    public static Item hexogen;
    public static Item galena;
    public static Item biotite;
    public static Item towanite;
    public static Item brimstone;
    public static Item azurite;
    public static Item ferrosilicon;
    public static Item aluminum;
    public static Item ferricMatter;
    public static Item cupronickel;
    public static Item salt;
    public static Item nickel;
    public static Item bauxite;
    public static Item zinc;
    public static void load() {
        Items.lead.alwaysUnlocked = true;
        Items.silicon.alwaysUnlocked = true;
        Items.pyratite.hidden = true;
        Items.sporePod.hidden = true;
        bauxite = new Item("bauxite", Color.valueOf("#895841")) {{
            hardness = 3;
            cost = 1.1F;
            buildable = false;
        }};
        salt = new Item("salt", Color.valueOf("#ffffff")) {{
            buildable = false;
        }};
        nickel = new AquaItem("nickel", Color.valueOf("#c3b994")) {{
            hardness = 2;
            alwaysUnlocked = true;
            cost = 1.1f;
            magnetism = 1;
        }};
        minium = new AquaItem("minium", Color.valueOf("#ec5e33")) {{
            buildable = false;
        }};
        zinc = new Item("zinc", Color.valueOf("#a0b9af")) {{
            hardness = 3;
            alwaysUnlocked = true;
        }};
        biotite = new Item("biotite", Color.valueOf("#2e3e43")) {{
            buildable = false;
        }};
        crystal = new Item("crystal", Color.valueOf("#d7e0e4")) {{
        }};
        pearl = new Item("pearl", Color.valueOf("#d7e0e4")) {{
        }};
        cupronickel = new AquaItem("cupronickel", Color.valueOf("#dfaf88")) {{
            cost = 1f;
            magnetism = 0.5f;
        }};
        brass = new AquaItem("brass", Color.valueOf("#bfa96d")) {{
            cost = 1f;
        }};
        polymer = new Item("polymer", Color.valueOf("#eaeadf")) {{
            cost = 1.3f;
        }};
        ferricMatter = new AquaItem("ferric-matter", Color.valueOf("#675b53")) {{
            cost = 0.5f;
            magnetism = 1;
            hardness = 4;
        }};
        aluminum = new Item("aluminum", Color.valueOf("#7489a5")) {{
            cost = 0.3f;
            flammability = 0.1f;
        }};
        ferrosilicon = new AquaItem("ferrosilicon", Color.valueOf("#98a1ab")) {{
            cost = 0.5f;
            magnetism = 0.5f;
        }};
        azurite = new Item("azurite", Color.valueOf("#636acf")) {{
            buildable = false;
        }};
        towanite = new Item("towanite", Color.valueOf("#faffcd")) {{
            buildable = false;
        }};
        cuprite = new Item("cuprite", Color.valueOf("#97114d")) {{
            buildable = false;
        }};
        brimstone = new Item("brimstone", Color.valueOf("#fafd81")) {{
            cost = 0.6f;
            explosiveness = 0.5f;
            flammability = 0.7f;
            buildable = false;
        }};

        galena = new Item("galena", Color.valueOf("d9cff2")) {{
            buildable = false;
        }};
        hexogen = new Item("hexogen", Color.valueOf("fff47a")) {{
            explosiveness = 8f;
            radioactivity = 0.2f;
            buildable = false;
        }};
        acuminite = new Item("acuminite", Color.valueOf("fff475")){{
            buildable = false;
        }};
        steel = new Item("steel", Color.valueOf("ffffff")){{
        }};
        magnesiumPowder = new Item("magnesium-powder", Color.valueOf("ffffff")){{
            flammability = 0.4f;
            explosiveness = 0.8f;
            buildable = false;
        }};
        serpentine = new Item("serpentine", Color.valueOf("e7e58f")){{
            hardness = 3;
            buildable = false;
        }};
        pentlandite = new Item("pentlandite", Color.valueOf("9a948b")){{
            hardness = 2;
            buildable = false;
        }};
        chalkalloy = new Item("chalkalloy", Color.valueOf("#9dccba")) {{
            cost = 1.1f;
        }};
        pitchblende = new AquaItem("pitchblende", Color.valueOf("#f1ffb2")) {{
            radioactivity = 2f;
            hardness = 4;
            buildable = false;
        }};
        uranium = new AquaItem("uranium", Color.valueOf("#ffe1b4")) {{
            radioactivity = 2.5f;
            hardness = 4;
            decayTo = Items.thorium;
            buildable = false;
        }};
        radium = new AquaItem("radium", Color.valueOf("#c7f77e")) {{
            radioactivity = 2f;
            hardness = 4;
            decayTo = Items.lead;
            buildable = false;
        }};
        Items.thorium = null;
        Items.thorium = new AquaItem("thorium", Color.valueOf("#141314")) {{
            radioactivity = 1.5f;
            decayTo = radium;
        }};
        Items.sporePod = null;
        Items.sporePod = new AquaItem("spore-pod", Color.valueOf("#a15bc4")) {{
            biomass = true;
            flammability = 1.15f;
            buildable = false;
        }};
    }
}
