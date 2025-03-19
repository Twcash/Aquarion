package aquarion.world.blocks.power;

import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.world.blocks.power.PowerGenerator;

public class RadiationModule extends PowerGenerator {
    public float radiationConsumption = 5;
    public float useInterval = 240;
    public float warmupSpeed = 0.019f;

    public RadiationModule(String name) {
        super(name);
        rotate = true;
        drawArrow = true;
    }

    public class RadiationModuleBuild extends GeneratorBuild {
        public @Nullable Building next;
        public @Nullable Reactor.ReactorBuild nextc;
        public float progress;
        public float warmup;

        @Override
        public void updateTile(){
            if(nextc != null) {
                if (progress >= 1f) {
                    warmup = Mathf.approachDelta(warmup, 1, warmupSpeed);
                    if (nextc.radiation - radiationConsumption >= 0) {
                        productionEfficiency = 1 * warmup;
                        nextc.removeRadiation(radiationConsumption * edelta());
                        progress = 0;
                    } else {
                        warmup = Mathf.approachDelta(warmup, 0, warmupSpeed);
                        progress *= warmup;
                        productionEfficiency = 0;
                    }
                } else {
                    progress += getProgressIncrease(useInterval);
                }
            } else {
                warmup = Mathf.approachDelta(warmup, 0, warmupSpeed);
                progress *= warmup;
                productionEfficiency *= warmup;
            }
        }
        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            next = front();
            nextc = next instanceof Reactor.ReactorBuild d ? d : null;
        }
    }
}