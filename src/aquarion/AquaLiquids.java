package aquarion;

import arc.graphics.Color;
import mindustry.type.CellLiquid;
import mindustry.type.Liquid;

public class AquaLiquids {
    public static Liquid artroGoop, brine, cyan, gerbGoop, helium, hydrogenChloride, hydroxide, molusGoop, pyridine, reactorCoolant, tritium;

    public static void loadContent() {
        artroGoop = new CellLiquid("artro-goop", Color.valueOf("#8cae8e80")) {{
            colorFrom = Color.valueOf("#c6eafb");
            colorTo = Color.valueOf("#5c8192");
            lightColor = Color.valueOf("#60a1ae");
            maxSpread = 2;
            hidden = true;
            coolant = false;
        }};

        gerbGoop = new CellLiquid("gerb-goop", Color.valueOf("#65d45390")) {{
            colorFrom = Color.valueOf("#ac8e76");
            colorTo = Color.valueOf("#85664c");
            lightColor = Color.valueOf("#65d453");
            viscosity = 0.95F;
            coolant = false;
        }};

        molusGoop = new CellLiquid("molus-goop", Color.valueOf("#92287d")) {{
            colorFrom = Color.valueOf("#c6eafb");
            colorTo = Color.valueOf("#a2bfcc");
            lightColor = Color.valueOf("#f21fc8");
            viscosity = 0.8f;
            hidden = true;
            coolant = false;
        }};

        brine = new Liquid("brine", Color.valueOf("#e5f0d5")) {{
            coolant = false;
            viscosity = 0.8f;
        }};

        cyan = new Liquid("cyan", Color.valueOf("#c7ecee80")) {{
            heatCapacity = 1;
            viscosity = 0.9f;
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

        reactorCoolant = new Liquid("reactor-coolant", Color.valueOf("#d2c1ff90")) {{
            heatCapacity = 1;
            explosiveness = 0.9f;
            hidden = true;
        }};

        tritium = new Liquid("tritium", Color.valueOf("#b6f498")) {{
            explosiveness = 0.6f;
            coolant = false;
            gas = true;
        }};
    }
}
