package aquarion.world.blocks.production;

import aquarion.world.consumers.ConsumeLiquidBaseNew;
import arc.Core;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Structs;
import arc.util.Time;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

public class AquaGenericCrafter extends GenericCrafter {
    public float boostItemUseTime = 120f;
    public float itemBoostIntensity = 1.5f;
    public float liquidBoostIntensity = 1.5f;
    public @Nullable Consume itemBooster;
    public final int timerUse = timers++;
    public float updateRange;
    public boolean hasLiquidBooster;

    public AquaGenericCrafter(String name) {
        super(name);
    }



    @Override
    public void init(){
        super.init();
        hasLiquidBooster = findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) != null;
    }
    @Override
    public void setBars(){
        super.setBars();

        addBar("efficiency", (AquaGenericCrafterBuild entity) ->
                new Bar(() ->
                        Core.bundle.format("bar.efficiency", (int)(entity.efficiency + 0.01f), (int)(entity.efficiencyScale() + 0.01f)),
                        () -> Pal.accent,
                        () -> entity.efficiency));
    }
    @Override
    public void setStats(){
        super.setStats();

        boolean consItems = itemBooster != null;

        if(consItems) stats.timePeriod = boostItemUseTime;

        if(consItems && itemBooster instanceof ConsumeItems coni){
            stats.remove(Stat.booster);
            stats.add(Stat.booster, StatValues.itemBoosters("{0}" + StatUnit.timesSpeed.localized(), stats.timePeriod, itemBoostIntensity, 0f, coni.items));
        }

        if(liquidBoostIntensity != 1 && findConsumer(f -> f instanceof ConsumeLiquidBase && f.booster) instanceof ConsumeLiquidBase consBase){
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

            if (block.findConsumer(c -> c.optional && c.booster && c instanceof ConsumeLiquidBaseNew) instanceof ConsumeLiquidBaseNew liquidConsumer) {
                optionalEfficiency *= liquidConsumer.efficiency(this);
            }
            if (block.findConsumer(c -> c.optional && c.booster && c instanceof ConsumeItems) instanceof ConsumeItems ItemConsumer) {
                optionalEfficiency *= ItemConsumer.efficiency(this);
            }

            super.updateEfficiencyMultiplier();
        }

        @Override
        public void updateTile() {
            super.updateTile();

            boolean itemValid = itemBooster != null && itemBooster.efficiency(this) > 0;

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
            if(itemValid && eff * efficiency > 0 && timer(timerUse, boostItemUseTime)){
                consume();
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
        public boolean shouldConsume(){
            if(outputItems != null){
                for(var output : outputItems){
                    if(items.get(output.item) + output.amount > itemCapacity){
                        return false;
                    }
                }
            }
            if(outputLiquids != null && !ignoreLiquidFullness){
                boolean allFull = true;
                for(var output : outputLiquids){
                    if(liquids.get(output.liquid) >= liquidCapacity - 0.001f){
                        if(!dumpExtraLiquid){
                            return false;
                        }
                    }else{
                        //if there's still space left, it's not full for all liquids
                        allFull = false;
                    }
                }

                //if there is no space left for any liquid, it can't reproduce
                if(allFull){
                    return false;
                }
            }

            return enabled;
        }

    }

}
