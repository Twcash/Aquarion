package aquarion.blocks;

import aquarion.world.blocks.core.OverdrivePylon;
import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.OverdriveProjector;
import mindustry.world.meta.Env;

import static aquarion.AquaItems.*;
import static aquarion.AquaLiquids.magma;
import static aquarion.planets.AquaPlanets.*;
import static mindustry.content.Items.lead;
import static mindustry.content.Items.silicon;
import static mindustry.type.ItemStack.with;

public class AquaEffect {
    public static Block overdrivePylon, overdriveTerminus;
    public static void loadContent() {
        overdrivePylon = new OverdrivePylon("overdrive-pylon") {{
            requirements(Category.effect, with(ferrosilicon, 200, invar, 500, aluminum, 250 ));
            reload = 90;
            size = 2;
            range = 4;
            useTime = 420;
            category = Category.effect;
            baseColor = Color.valueOf("f6675b");
            phaseColor = Color.valueOf("f6675b");
            consumeLiquid(magma, 40/60f);
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            researchCostMultiplier = 0.25f;
            consumePower(40/60f);
        }};
        overdriveTerminus = new OverdriveProjector("overdrive-terminus") {
                    {
                        requirements(Category.effect, with(nickel, 120, invar, 450, silicon, 120));
                        reload = 90;
                        size = 5;
                        range = 110;
                        useTime = 420;
                        shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
                        category = Category.effect;
                        baseColor = Color.valueOf("f6675b");
                        phaseColor = Color.valueOf("f6675b");
                        envEnabled |= Env.terrestrial | Env.underwater;
                        envDisabled = Env.none;
                        researchCostMultiplier = 0.25f;
                        consumePower(1f);
                    }
                };
    }
}
