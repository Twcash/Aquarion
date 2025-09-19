package aquarion.world.consumers;

import aquarion.world.Uti.AquaStats;
import aquarion.world.content.AquaLiquid;
import mindustry.type.Liquid;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;

public class ConsumeLiquidAcidic extends ConsumeLiquidFilter {
    public float minAcidity;

    public ConsumeLiquidAcidic(float minAcidity, float amount){
        super(l -> l instanceof AquaLiquid a && a.acidity >= minAcidity, amount);
        this.minAcidity = minAcidity;
    }

    public ConsumeLiquidAcidic(float amount){
        this(0.2f, amount);
    }

    public ConsumeLiquidAcidic(){
        this(0.2f);
    }

    @Override
    public void display(Stats stats){
        stats.add(
                booster ? Stat.booster : Stat.input,
                AquaStats.liquidEffMultiplier(
                        l -> l instanceof AquaLiquid a ? a.acidity : 0f,
                        amount * 60f,
                        filter
                )
        );
    }

    @Override
    public float liquidEfficiencyMultiplier(Liquid liquid){
        return liquid instanceof AquaLiquid a ? a.acidity : 0f;
    }
}
