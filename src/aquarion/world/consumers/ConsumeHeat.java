package aquarion.world.consumers;


import aquarion.world.interfaces.HasHeat;
import arc.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.ui.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;


import static mindustry.Vars.*;

public class ConsumeHeat extends Consume {
    /**
     * Amount consumed per tick or per craft.
     */
    public float amount = 0;

    /**
     * Range at which this consumer work.
     */
    public float minHeat = 0, maxHeat = 8;

    /**
     * Efficiency mapping based on pressure amount.
     */
    public float minEfficiency = 1f, maxEfficiency = 1f;

    /**
     * Consume continuously per tick instead of consuming per craft.
     */
    public boolean continuous;

    public float baseEfficiency(HasHeat build) {
        if (build.getHeat() < minHeat || build.getHeat() > maxHeat) return 0f;
        return Mathf.map(build.getHeat(), minHeat, maxHeat, minEfficiency, maxEfficiency);
    }

    @Override public void build(Building build, Table table) {
        table.add(new ReqImage(Core.atlas.find("sw-steam"), () -> baseEfficiency(cast(build)) > 0.0001f)).size(iconMed).top().left();
    }

    public HasHeat cast(Building build) {
        try {
            if (!((HasHeat) build).heatConfig().hasHeat) throw new RuntimeException("This block doesn't use the gas system");
            return (HasHeat) build;
        } catch (ClassCastException e) {
            throw new RuntimeException("This block cannot use the gas system!", e);
        }
    }

   // @Override public void display(Stats stats) {
     //   stats.add("SWStat.consumeGas, Strings.fixed(amount * (continuous ? 60f : 1f), 2), continuous ? SWStat.gasSecond : SWStat.gasUnit");
   // }

    @Override public float efficiency(Building build) {
        return baseEfficiency(cast(build));
    }
    @Override public float efficiencyMultiplier(Building build) {
        return baseEfficiency(cast(build));
    }

    @Override public void trigger(Building build) {
        if (!continuous) cast(build).heat().subAmount(Math.min(cast(build).getHeat(), amount * build.edelta()));
    }

    @Override public void update(Building build) {
        if (continuous) cast(build).heat().subAmount(Math.min(cast(build).getHeat(), amount * baseEfficiency(cast(build))));
    }
}