package aquarion.world.consumers;

import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Strings;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;
import mindustry.type.LiquidStack;

import static mindustry.world.meta.StatValues.*;

public class AquaConsume extends Consume {
    public static class Entry {
        public Consume consumer;
        public boolean required = false;
        public float multiplier = 1.0f;

        public Entry(Consume consumer, float multiplier){
            this.consumer = consumer;
            this.multiplier = multiplier;
        }
    }

    public Seq<Entry> entries = new Seq<>();

    public AquaConsume(){}

    public AquaConsume set(float mult, boolean req){
        if(!entries.isEmpty()){
            entries.first().multiplier = mult;
            entries.first().required = req;
        }
        return this;
    }

    public AquaConsume(Consume consumer){
        add(consumer);
    }

    public AquaConsume(Consume consumer, float multiplier){
        add(consumer, multiplier);
    }

    public AquaConsume add(Consume consumer){
        return add(consumer, 1.0f);
    }

    public AquaConsume add(Consume consumer, float multiplier){
        entries.add(new Entry(consumer, multiplier));
        return this;
    }

    /** Updates to match first entry config for single-consumer use */
    public AquaConsume set(float mult){
        if(!entries.isEmpty()) entries.first().multiplier = mult;
        return this;
    }

    @Override
    public void apply(Block block){
        for(var e : entries){
            if(e.consumer instanceof ConsumeLiquid cl){
                block.liquidFilter[cl.liquid.id] = true;
            }else if(e.consumer instanceof ConsumeLiquids cls){
                for(var ls : cls.liquids){
                    block.liquidFilter[ls.liquid.id] = true;
                }
            }else if(e.consumer instanceof ConsumeItems CI){
                block.hasItems = true;
                for(ItemStack item : CI.items){
                    block.itemFilter[item.item.id] = true;
                }
            }else{
                //Unknown types (power, wrapped, custom) need full apply()
                e.consumer.apply(block);
            }
        }
    }

    @Override
    public void build(Building build, Table table){
        for(var e : entries) e.consumer.build(build, table);
    }

    @Override
    public void update(Building build){
        for(var e : entries) e.consumer.update(build);
    }

    @Override
    public void trigger(Building build){
        for(var e : entries) e.consumer.trigger(build);
    }

    /** Returns the minimum efficiency across required entries only (boosters skipped) */
    @Override
    public float efficiency(Building build){
        float min = 1f;
        for(var e : entries){
            if(!e.required) continue;
            min = Math.min(min, e.consumer.efficiency(build));
        }
        return min;
    }

    /** Returns the product of all entry multipliers and their efficiency multipliers */
    @Override
    public float efficiencyMultiplier(Building build){
        float prod = 1f;
        for(var e : entries){
            prod *= e.multiplier * e.consumer.efficiencyMultiplier(build);
        }
        return prod;
    }

    /** Adds custom formatted stat entries for each consumer in this group */
    public void displayStats(Stats stats, float timePeriod){
        for(var e : entries){
            Stat stat = e.required ? Stat.input : Stat.booster;
            if(e.consumer instanceof ConsumeLiquid cl){
                stats.add(stat, entryTable(cl.liquid, cl.amount, e.multiplier, timePeriod, !e.required, true));
            }else if(e.consumer instanceof ConsumeLiquids cls){
                for(var ls : cls.liquids){
                    stats.add(stat, entryTable(ls.liquid, ls.amount, e.multiplier, timePeriod, !e.required, true));
                }
            }else if(e.consumer instanceof ConsumeItems ci){
                for(var is : ci.items){
                    stats.add(stat, entryTable(is.item, is.amount, e.multiplier, timePeriod, !e.required, false));
                }
            }
        }
    }

    private static StatValue entryTable(Object iconObj, float baseAmount, float mult, float timePeriod, boolean booster, boolean isLiquid){
        return table -> {
            table.row();
            table.table(Styles.grayPanel, b -> {
                b.defaults().pad(5).left();
                if(isLiquid){
                    b.add(displayLiquid((mindustry.type.Liquid)iconObj, baseAmount * mult * 60f, true)).pad(10f).left();
                }else{
                    b.add(displayItem((mindustry.type.Item)iconObj, Math.round(baseAmount * mult), timePeriod, true)).pad(10f).left();
                }
                if(mult != 1f){
                    b.add("[lightgray]" + Strings.autoFixed(mult, 2) + "x").pad(10f).right();
                }
                b.add(booster ? "[accent]Booster" : "[white]Required").pad(10f).padRight(15f).right();
            }).growX().pad(3).row();
        };
    }
}
