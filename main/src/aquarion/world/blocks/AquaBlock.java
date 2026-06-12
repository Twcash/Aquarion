package aquarion.world.blocks;

import aquarion.AquarionMod;
import aquarion.world.consumers.AquaConsume;
import arc.Core;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.consumers.*;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class AquaBlock extends Block {
    public boolean hasHeat = false;
    public float heatRequirement = 0f;
    public float overheatScale = 0.8f;
    public float maxEfficiency = 1f;
    public float baseEfficiency = 1f;
    public boolean flipHeatScale = false;

    public AquaBlock(String name) {
        super(name);
    }
    public AquaConsume consume(Liquid liquid, float amount){
        AquaConsume ac = new AquaConsume(new ConsumeLiquid(liquid, amount));
        ac.entries.first().required = true;
        return consume(ac);
    }
    public AquaConsume consumeBoost(Liquid liquid, float amount, float multiplier){
        AquaConsume ac = new AquaConsume(new ConsumeLiquid(liquid, amount)).set(multiplier, false);
        ac.booster = true;
        return consume(ac);
    }
    public AquaConsume consumeBoost(Item item, int amount, float multiplier){
        AquaConsume ac = new AquaConsume(new ConsumeItems(ItemStack.with(item, amount))).set(multiplier, false);
        ac.booster = true;
        return consume(ac);
    }
    public AquaConsume consume(LiquidStack... stacks){
        AquaConsume ac = new AquaConsume(new ConsumeLiquids(stacks));
        for(var e : ac.entries) e.required = true;
        return consume(ac);
    }

    public AquaConsume consume(Item item, int amount){
        AquaConsume ac = new AquaConsume(new ConsumeItems(ItemStack.with(item, amount)));
        ac.entries.first().required = true;
        return consume(ac);
    }

    public AquaConsume consumeItemStack(ItemStack... items){
        AquaConsume ac = new AquaConsume(new ConsumeItems(items));
        for(var e : ac.entries) e.required = true;
        return consume(ac);
    }
    public AquaConsume consumers(){
        return consume(new AquaConsume());
    }
    public AquaConsume consumeWrapped(Consume inner){
        return consume(new AquaConsume(inner));
    }
    /** Finds the efficiency of a booster consumer matching the given type, looking inside AquaConsume wrappers */
    public float getEfficiency(Building build, Class<? extends Consume> type){
        for(var c : consumers){
            if(!c.booster) continue;
            if(type.isInstance(c)) return c.efficiency(build);
            if(c instanceof AquaConsume ac){
                for(var e : ac.entries){
                    if(type.isInstance(e.consumer)) return e.consumer.efficiency(build);
                }
            }
        }
        return 0f;
    }
    /** Finds a booster consumer by type, unwrapping AquaConsume if needed */
    @SuppressWarnings("unchecked")
    public <T extends Consume> T findConsume(Class<T> type) {
        for (var c : consumers) {
            if (!c.booster) continue;
            if (type.isInstance(c)) return (T) c;
            if (c instanceof AquaConsume ac) {
                for (var e : ac.entries) {
                    if (type.isInstance(e.consumer)) return (T) e.consumer;
                }
            }
        }
        return null;
    }
    @Override
    public void setStats(){
        super.setStats();
        if(hasHeat && heatRequirement != 0f){
            if(baseEfficiency <= 0){
                stats.add(Stat.input, heatRequirement, StatUnit.heatUnits);
            }else{
                stats.add(Stat.booster, heatRequirement, StatUnit.heatUnits);
            }
        }
        stats.add(Stat.maxEfficiency, (int)(maxEfficiency * 100f), StatUnit.percent);
        for(var c : consumers){
            if(c instanceof AquaConsume ac){
                ac.displayStats(stats, stats.timePeriod);
            }
        }
    }
    @Override
    public void setBars(){
        super.setBars();
        if(hasHeat && heatRequirement != 0f){
            addBar("heat", (AquaBuilding entity) ->
                    new Bar(() ->
                            Core.bundle.format("bar.heatpercent",
                                    (int)(entity.heat + 0.01f),
                                    (int)(entity.efficiencyScale() * 100 + 0.01f)),
                            () -> {
                                float max = heatRequirement * 5f;
                                float heat = entity.heat;
                                if(heat < 0f){
                                    float t = Mathf.clamp(1f + heat / max);
                                    return Tmp.c1.set(Color.black).lerp(Pal.techBlue, t);
                                }else{
                                    float t = Mathf.clamp(heat / max);
                                    return Tmp.c1.set(Pal.lightOrange).lerp(Color.white, t);
                                }
                            },
                            () -> Mathf.clamp(Math.abs(entity.heat) / Math.abs(heatRequirement))
                    )
            );
        }
    }
    public class AquaBuilding extends Building implements HeatConsumer {
        public float heat = 0f;
        public float[] sideHeat = new float[4];
        public int version = AquarionMod.blockVersions;
        @Override
        public void updateTile(){
            if(hasHeat){
                heat = calculateHeat(sideHeat);
            }
        }

