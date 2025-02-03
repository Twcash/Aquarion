package aquarion.world.blocks.power;

import arc.util.Time;
import mindustry.world.blocks.power.PowerGenerator;

public class ThermoelectricModule extends PowerGenerator {
    public float heatToPowerEfficiency = 0.3f; // Conversion efficiency for heat to power

    public ThermoelectricModule(String name) {
        super(name);
    }

    public class ThermoelectricBuild extends GeneratorBuild {

        public void processHeat(ModularReactor.ReactorBuild reactor) {
            if (reactor.heat > 0) {
                float usedHeat = Math.min(reactor.heat, 5f * Time.delta);
                reactor.heat -= usedHeat;
                productionEfficiency = usedHeat * heatToPowerEfficiency / powerProduction;
            }
        }

    }
}