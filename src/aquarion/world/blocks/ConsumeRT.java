package aquarion.world.blocks;

import aquarion.world.graphs.RTGraph;
import aquarion.world.interfaces.HasRT;
import aquarion.world.meta.AquaStat;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

public class ConsumeRT extends Consume {
    public float min, max;
    public boolean stableRT = false;
    public float amount;

    public ConsumeRT(float amount) {
        this.amount = amount;
    }

    public HasRT cast(Building build) {
        try {
            return (HasRT) build;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Building is not a HasRT instance", e);
        }
    }

    @Override
    public void display(Stats stats) {
        stats.add(AquaStat.requiredRT, "@ to @ @", min, max, AquaStat.rotationUnits.localized());
    }

    @Override
    public float efficiency(Building build) {
        if (build instanceof HasRT b) {
            float currentPower = b.rTGraph().getTotalRotationPower();
            return Mathf.clamp((currentPower - amount) / amount);
        }
        return 0f;
    }

    @Override
    public void update(Building build) {
        if (build instanceof HasRT next) {
            float consumption = amount * efficiencyMultiplier(build) * Time.delta;
            float consumed = pull(next);
            next.rotationPower().rotationPower -= consumed; // Reduce the rotation power
        }
    }


    @Override
    public float efficiencyMultiplier(Building build) {
        if (build instanceof HasRT b) {
            float currentPower = b.rTGraph().getTotalRotationPower();
            float ratio = Mathf.map(currentPower, amount, amount * 2, 0, 1);
            return 1f - ratio; // Adjusted to reduce efficiency as power is consumed
        }
        return 0f;
    }

    public float pull(HasRT build) {
        RTGraph graph = build.rTGraph();
        float availablePower = graph.getTotalRotationPower();
        final float[] consumed = {Math.min(amount, availablePower)};
        graph.getBuildings().each(b -> {
            if (consumed[0] > 0) {
                float toRemove = Math.min(amount, b.rotationPower().rotationPower);
                b.rotationPower().rotationPower -= toRemove;
                consumed[0] -= toRemove;
            }
        });
        return amount - consumed[0]; // Return the amount of power that was actually consumed
    }

    public Consume consumeRT() {
        stableRT = true;
        return this;
    }
}