package aquarion.world.blocks.power;

import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.gen.Building;
import mindustry.world.blocks.power.PowerGenerator;

public class ThermoelectricModule extends PowerGenerator {
    public float heatConsumption = 5;
    public float useInterval = 60;
    public float warmupSpeed = 0.019f;


    public ThermoelectricModule(String name) {
        super(name);
        rotate = true;
        drawArrow = true;
    }

    public class ThermoelectricBuild extends GeneratorBuild {
        public @Nullable Building next;
        public @Nullable Reactor.ReactorBuild nextc;
        public float progress;
        public float warmup;


        @Override
        public void updateTile(){
            if(nextc != null) {

                if (progress >= 1f) {
                    warmup = Mathf.approachDelta(warmup, 1, warmupSpeed);
                    if (nextc.heat - heatConsumption >= 0) {
                        productionEfficiency = 1;
                         nextc.removeHeat(heatConsumption);
                         progress = 0;
                    } else {
                         progress = 0;
                         productionEfficiency = 0;
                     }
                } else {
                    progress += getProgressIncrease(useInterval);
                }
            } else {
                progress = 0;
                productionEfficiency = 0;
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