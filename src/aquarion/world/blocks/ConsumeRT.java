package aquarion.world.blocks;

import aquarion.world.graphs.RTGraph;
import aquarion.world.interfaces.HasRT;
import aquarion.world.meta.AquaStat;
import aquarion.world.meta.RTModule;
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
    @Override public float efficiency(Building build) {
        return build instanceof HasRT b ? Mathf.clamp(Math.abs(b.rotationPower) - amount) : 0f;
    }
    @Override public float efficiencyMultiplier(Building build) {
        return build instanceof HasRT b ? 1f + Mathf.map(Math.abs(b.rotationPower), amount, amount, 0, 1) : 0f;
    }

}