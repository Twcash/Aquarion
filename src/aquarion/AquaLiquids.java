package aquarion;

import arc.graphics.Color;
import mindustry.type.CellLiquid;
import mindustry.type.Liquid;

public class AquaLiquids {
    public static Liquid artroGoop, brine, nitronite, gerbGoop, helium, hydrogenChloride, hydroxide, molusGoop, pyridine, reactorCoolant, tritium;

    public static void loadContent() {
        artroGoop = new CellLiquid("artro-goop", Color.valueOf("#8cae8e80")) {{
            colorFrom = Color.valueOf("#c6eafb");
            colorTo = Color.valueOf("#5c8192");
            lightColor = Color.valueOf("#60a1ae");
            maxSpread = 2;
            hidden = true;
            coolant = false;
        }};

        brine = new Liquid("brine", Color.valueOf("#b8c89f")) {{
            coolant = false;
            viscosity = 0.8f;
            explosiveness = 0.1f;
        }};

        nitronite = new Liquid("nitronite", Color.valueOf("#c7ecee80")) {{
            heatCapacity = 1;
            viscosity = 0.9f;
            explosiveness = 0.5f;
            flammability = 1.5f;
        }};

        helium = new Liquid("helium", Color.valueOf("#ff8080")) {{
            coolant = false;
            gas = true;
        }};

        hydrogenChloride = new Liquid("hydrogen-chloride", Color.valueOf("#f5e380")) {{
            coolant = false;
            explosiveness = 0.3f;
        }};

        hydroxide = new Liquid("hydroxide", Color.valueOf("#a97abf")) {{
            coolant = false;
            flammability = 0.4f;
        }};

        pyridine = new Liquid("pyridine", Color.valueOf("#d1bb56")) {{
            coolant = false;
            flammability = 0.25f;
            explosiveness = 0.1f;
            gas = true;
        }};


        tritium = new Liquid("tritium", Color.valueOf("#b6f498")) {{
            explosiveness = 0.6f;
            coolant = false;
            gas = true;
        }};
    }
}
