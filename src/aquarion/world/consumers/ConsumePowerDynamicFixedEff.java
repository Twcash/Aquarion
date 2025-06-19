package aquarion.world.consumers;

import arc.func.Floatf;
import mindustry.gen.Building;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.Stats;


import mindustry.gen.*;
import mindustry.world.meta.*;

/**
 * Power consumer with variable consumption, but fixed efficiency as long as the requested amount is satisfied.
 */
public class ConsumePowerDynamicFixedEff extends ConsumePower {
    public interface PowerRequest {
        float get(Building build);
    }

    private final PowerRequest dynamicRequest;

    public ConsumePowerDynamicFixedEff(float usage, PowerRequest dynamicRequest) {
        super(usage, 0f, false); // no buffer
        this.dynamicRequest = dynamicRequest;
    }

    @Override
    public float requestedPower(Building entity) {
        return dynamicRequest.get(entity);
    }

    @Override
    public float efficiency(Building build) {
        float req = requestedPower(build);
        float max = this.usage;
        float received = build.power.status;

        if (received + 0.001f >= req) return 1f; // all good
        if (req <= 0.001f) return 0f; // nothing needed
        return received / max; // scale down only if not fulfilled
    }

    @Override
    public void display(Stats stats) {
        stats.add(Stat.powerUse, usage * 60f, StatUnit.powerSecond);
    }
}
