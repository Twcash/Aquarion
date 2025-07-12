package aquarion;

import aquarion.world.content.AquaLiquid;
import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.content.Planets;
import mindustry.content.StatusEffects;
import mindustry.graphics.Pal;
import mindustry.type.CellLiquid;
import mindustry.type.Liquid;

import static aquarion.AquaStatuses.*;
import static aquarion.planets.AquaPlanets.*;
import static mindustry.content.Liquids.*;
import static mindustry.content.StatusEffects.*;

public class AquaLiquids {
    public static Liquid argon, muriaticAcid, halideWater, air, haze, methane, petroleum, fluorine, chlorine, fumes, magma, bioPulp, carbonicAcid, oxygen, dioxide, brine, helium, hydroxide, tritium;

    public static void loadContent() {

        brine = new Liquid("brine", Color.valueOf("#b8c89f")) {{
            coolant = false;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            viscosity = 0.8f;
            explosiveness = 0.1f;
            effect = slow;
            canStayOn.addAll(water, petroleum, halideWater);
        }};

        helium = new Liquid("helium", Color.valueOf("#ff8080")) {{
            coolant = false;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            gas = true;
            effect = slow;
        }};
        hydroxide = new Liquid("hydroxide", Color.valueOf("#a97abf")) {{
            coolant = false;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            flammability = 0.4f;
            effect = corroding;
            boilPoint = 0.8f;
            gasColor = Color.valueOf("b7a8b3");
        }};

        tritium = new Liquid("tritium", Color.valueOf("#b6f498")) {{
            explosiveness = 0.6f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            coolant = false;
            effect = ionized;
            gas = true;
        }};
        dioxide = new Liquid("dioxide", Color.valueOf("#6b6565")) {{
            explosiveness = 0f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            flammability = 0f;
            coolant = false;
            gas = true;
            effect = slow;
        }};
        oxygen = new Liquid("oxygen", Color.valueOf("#fdbda6")) {{
            explosiveness = 0.25f;
            flammability = 0.5f;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            coolant = false;
            effect = corroding;
            gas = true;
        }};
        carbonicAcid = new Liquid("carbonic-acid", Color.valueOf("#ac6656")){{
            coolant = false;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            effect = corroding;
            explosiveness = 0.1f;
            boilPoint = 0;
            gasColor = Color.grays(0.8f);
        }};
        bioPulp = new Liquid("bio-pulp", Color.valueOf("#c7904f")){{
            coolant = false;
            effect = wet;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            explosiveness = 0.1f;
            viscosity = 0.8f;
            flammability = 0.5f;
        }};
        magma = new Liquid("magma", Color.valueOf("d54040")){{
            coolant = false;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            flammability = 0.1f;
            temperature = 0.9f;
            viscosity = 0.9f;
            effect = StatusEffects.melting;
            lightColor = Color.valueOf("ffb477").a(0.7f);
        }};
        fumes = new AquaLiquid("fumes", Color.valueOf("83746b")){{
            lightColor = Color.valueOf("d68a7e").a(0.7f);
            flammability = 1;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            explosiveness = 0.4f;
            effect = corroding;
            coolant = false;
            gas = true;
            acidity = 0.6f;
            temperature = 1.01f;
        }};
        chlorine = new Liquid("chlorine", Color.valueOf("e1f7bc")){{
            coolant = false;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            effect = corroding;
            gas = true;
        }};
        fluorine = new Liquid("fluorine", Color.valueOf("c3eff2")){{
            coolant = false;shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            effect = melting;
            flammability = 1.1f;
            gas = true;
        }};
        petroleum = new Liquid("petroleum", Color.valueOf("6d7944")){{
            coolant = false;
            effect = tarred;
            flammability = 1.5f;
            explosiveness = 0.2f;
            viscosity = 0.8f;
            boilPoint = 0.7f;
            particleSpacing = 30;
            particleEffect = Fx.coalSmeltsmoke;
            gasColor = Color.black;
            canStayOn.addAll( water, halideWater, hydroxide, cryofluid, oil);
        }};
        methane = new Liquid("methane", Color.valueOf("e3428f")){{
            coolant = false;
            effect = corroding;
            gas = true;
            viscosity = 0.1f;
            flammability = 1.1f;
            explosiveness = 0.1f;
            boilPoint = 0.1f;
            canStayOn.add(water);
        }};
        haze = new Liquid("haze", Color.valueOf("ffffff")){{
            coolant = false;
            effect = burning;
            temperature = 1.1f;
            gas = true;
        }};
        air = new Liquid("air", Color.valueOf("bcced5")){{
            gas = true;
            effect = concussed;
            heatCapacity = 0.05f;
        }};
        halideWater = new Liquid("halide-water", Color.valueOf("a3a7be")){{
            heatCapacity = 0.1f;
            effect = StatusEffects.wet;
            boilPoint = 0.5f;
            gasColor = Color.grays(0.9f);
            alwaysUnlocked = true;
            canStayOn.addAll(cryofluid);
        }};
        muriaticAcid = new AquaLiquid("muriatic-acid", Color.valueOf("faf3a0")){{
            acidity = 1.1f;
            effect = corroding;
            coolant = false;
            boilPoint = 1.1f;
        }};
        argon = new Liquid("argon", Color.valueOf("c891cc")){{
            gas = true;
            heatCapacity = 0.5f;
        }};
    }
}
