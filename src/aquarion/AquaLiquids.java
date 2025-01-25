package aquarion;

import arc.graphics.Color;
import mindustry.content.StatusEffects;
import mindustry.type.CellLiquid;
import mindustry.type.Liquid;

public class AquaLiquids {
    public static Liquid chlorine, fumes, magma, bioPulp, carbonicAcid, oxygen, dioxide, artroGoop, brine, nitronite, helium, hydrogenChloride, hydroxide, pyridine, tritium;

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
        dioxide = new Liquid("dioxide", Color.valueOf("#6b6565")) {{
            explosiveness = 0f;
            flammability = 0f;
            coolant = false;
            gas = true;
        }};
        oxygen = new Liquid("oxygen", Color.valueOf("#fdbda6")) {{
            explosiveness = 0.25f;
            flammability = 0.5f;
            coolant = false;
            gas = true;
        }};
        carbonicAcid = new Liquid("carbonic-acid", Color.valueOf("#ac6656")){{
            coolant = false;
            explosiveness = 0.1f;
        }};
        bioPulp = new Liquid("bio-pulp", Color.valueOf("#92ba76")){{
            coolant = false;
            explosiveness = 0.1f;
            flammability = 0.5f;
        }};
        magma = new Liquid("magma", Color.valueOf("d54040")){{
            coolant = false;
            flammability = 0.1f;
            temperature = 0.9f;
            viscosity = 0.9f;
            effect = StatusEffects.melting;
            lightColor = Color.valueOf("ffb477").a(0.7f);
        }};
        fumes = new Liquid("fumes", Color.valueOf("83746b")){{
            lightColor = Color.valueOf("d68a7e").a(0.7f);
            flammability = 1;
            explosiveness = 0.4f;
            coolant = false;
            gas = true;
            temperature = 1.01f;
        }};
        chlorine = new Liquid("chlorine", Color.valueOf("83746b")){{
            coolant = false;
            gas = true;
        }};
    }
}
