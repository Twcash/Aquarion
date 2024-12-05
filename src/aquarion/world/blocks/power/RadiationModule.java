package aquarion.world.blocks.power;

import arc.util.Time;
import mindustry.gen.Building;
import mindustry.mod.Mod;
import mindustry.world.Block;
import mindustry.world.blocks.power.PowerGenerator;

public class RadiationModule extends PowerGenerator {
    public float radiationToPowerEfficiency = 0.5f; // Conversion efficiency for radiation to power

    public RadiationModule(String name) {
        super(name);
    }

    public class RadiationBuild extends GeneratorBuild {
        public void processRadiation(ModularReactor.ReactorBuild reactor) {
            if (reactor.radiation > 0) {
                float usedRadiation = Math.min(reactor.radiation, 5f * Time.delta); // Example usage
                reactor.radiation -= usedRadiation;
                productionEfficiency = usedRadiation * radiationToPowerEfficiency / powerProduction;
            }
        }

        @Override
        public float getPowerProduction() {
            return powerProduction * productionEfficiency;
        }
    }
}