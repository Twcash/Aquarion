package aquarion.world.blocks;

import aquarion.world.interfaces.HasRT;
import aquarion.world.meta.AquaStat;
import mindustry.gen.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

public class RTConsumer extends Consume {
    public float min, max;
    public boolean stableRT = false;

    public RTConsumer(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public HasRT cast(Building build) {
        try {
            return build.as();
        } catch (Exception e) {
            throw new IllegalArgumentException("buh gruh", e);
        }
    }

    @Override public void display(Stats stats) {
        stats.add(AquaStat.requiredRT, "@ to @ @", min, max, AquaStat.rotationUnits.localized());
    }

    @Override public float efficiency(Building build) {
        return (min <= pull(cast(build)) && pull(cast(build)) <= max) ? 1f : 0f;
    }
    @Override public float efficiencyMultiplier(Building build) {
        return (min <= pull(cast(build)) && pull(cast(build)) <= max) ? 1f : 0f;
    }

    public float pull(HasRT build) {
        return stableRT ? build.rTGraph().getConsumption() : build.rTGraph().getProduction();
    }

    public Consume consumeRT() {
        stableRT = true;
        return this;
    }
}
