package aquarion.world.blocks.production;

import aquarion.world.consumers.ConsumeLiquidBaseNew;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Structs;
import arc.util.Time;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

public class AquaGenericCrafter extends GenericCrafter {
    public float boostItemUseTime = 120f;
    public float itemBoostIntensity = 1.6f;
    public float liquidBoostIntensity = 1.6f;
    public @Nullable Consume booster;
    public @Nullable Consume itemBooster;
    public final int timerUse = timers++;
    public float updateRange;

    public AquaGenericCrafter(String name) {
        super(name);
    }

    @Override
    public void setStats(){
super.setStats();
        boolean consItems = itemBooster != null;

        if (consItems) stats.timePeriod = boostItemUseTime;

        if (consItems && booster instanceof ConsumeItems coni) {
            stats.remove(Stat.booster);
            stats.add(Stat.booster,
                    StatValues.itemBoosters(
                            "{0}" + StatUnit.timesSpeed.localized(),
                            stats.timePeriod,
                            itemBoostIntensity,
                            0f,
                            coni.items,
                            i -> Structs.contains(coni.items, s -> s.item == i)
                    )
            );
        }

        if(liquidBoostIntensity != 1 && findConsumer(f -> f instanceof ConsumeLiquidBaseNew && f.booster) instanceof ConsumeLiquidBaseNew consBase){
            stats.remove(Stat.booster);
            stats.add(Stat.booster,
                    StatValues.speedBoosters("{0}" + StatUnit.timesSpeed.localized(),
                            consBase.amount,
                            liquidBoostIntensity, false, consBase::consumes)
            );
        }
    }

    public class AquaGenericCrafterBuild extends GenericCrafterBuild {
        public float lastEfficiency;

        @Override
        public void updateEfficiencyMultiplier() {
            // Apply custom efficiency logic to exclude non-boosting liquids
            if (block.findConsumer(c -> c.optional && c.booster && c instanceof ConsumeLiquidBaseNew) instanceof ConsumeLiquidBaseNew liquidConsumer) {
                optionalEfficiency *= liquidConsumer.efficiency(this);
            }

            super.updateEfficiencyMultiplier();
        }

        @Override
        public void updateTile() {
            super.updateTile();

            boolean itemValid = booster != null && booster.efficiency(this) > 0;

            warmup = Mathf.approachDelta(warmup, Mathf.num(efficiency > 0), warmupSpeed);

            // Adjust efficiency based on boosters
            float eff = efficiency *
                    Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) *
                    (itemValid ? itemBoostIntensity : 1f);

            progress += getProgressIncrease(craftTime) * eff;

            // Consume items for boosting
            if (itemValid && eff > 0 && timer(timerUse, boostItemUseTime)) {
                consume();
            }

            // Handle liquid output
            if (outputLiquids != null) {
                float inc = getProgressIncrease(1f) * eff;
                for (var output : outputLiquids) {
                    handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
                }
            }

            // Update effects
            if (wasVisible && Mathf.chanceDelta(updateEffectChance * warmup)) {
                updateEffect.at(x + Mathf.range(updateRange * size * 4f), y + Mathf.range(size * 4));
            }

            // Check crafting progress
            if (progress >= 1f) {
                craft();
            }

            totalProgress += warmup * Time.delta;

            // Dump outputs
            dumpOutputs();
        }

        @Override
        public boolean shouldConsume() {
            return super.shouldConsume() || (booster != null && booster.efficiency(this) > 0);
        }
    }
}
