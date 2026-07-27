package aquarion.world.consumers;
import mindustry.Vars;
import arc.func.*;
import arc.util.*;
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
import arc.func.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.meta.*;

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
        for(Entry e : entries){
            if(e.consumer instanceof ConsumeLiquid cl){
                block.hasLiquids = true;
                block.liquidFilter[cl.liquid.id] = true;
            }else if(e.consumer instanceof ConsumeLiquids cls){
                block.hasLiquids = true;
                for(LiquidStack ls : cls.liquids){
                    block.liquidFilter[ls.liquid.id] = true;
                }
            }else if(e.consumer instanceof ConsumeItems CI){
                block.hasItems = true;
                for(ItemStack item : CI.items){
                    block.itemFilter[item.item.id] = true;
                }
            }else{
                e.consumer.apply(block);
            }
        }
    }

    @Override
    public void build(Building build, Table table){
        for(Entry e : entries) e.consumer.build(build, table);
    }

    @Override
    public void update(Building build){
        for(Entry e : entries) e.consumer.update(build);
    }

    @Override
    public void trigger(Building build){
        for(Entry e : entries) e.consumer.trigger(build);
    }

    /** Returns the minimum efficiency across required entries only (boosters skipped) */
    @Override
    public float efficiency(Building build){
        float min = 1f;
        for(Entry e : entries){
            if(!e.required) continue;
            min = Math.min(min, e.consumer.efficiency(build));
        }
        return min;
    }

    /** Returns the product of all entry multipliers and their efficiency multipliers */
    @Override
    public float efficiencyMultiplier(Building build){
        float prod = 1f;
        for(Entry e : entries){
            prod *= e.multiplier * e.consumer.efficiencyMultiplier(build);
        }
        return prod;
    }

    public void display(Stats stats, float timePeriod){
        for(Entry e : entries){
            Stat stat = e.required ? Stat.input : Stat.booster;
            if(e.consumer instanceof ConsumeLiquid cl){
                stats.add(stat, entryTable(cl.liquid, cl.amount, e.multiplier, timePeriod, !e.required, true));
            }else if(e.consumer instanceof ConsumeLiquids cls){
                for(LiquidStack ls : cls.liquids){
                    stats.add(stat, entryTable(ls.liquid, ls.amount, e.multiplier, timePeriod, !e.required, true));
                }
            }else if(e.consumer instanceof ConsumeItems ci){
                for(ItemStack is : ci.items){
                    stats.add(stat, entryTable(is.item, is.amount, e.multiplier, timePeriod, !e.required, false));
                }
            } else if(e.consumer instanceof ConsumeItemFilter CIF){
                Boolf<Item> filter = CIF.filter;
                Seq<Object> ite = new Seq();
                Vars.content.items().each(filter, item -> ite.addUnique(item));
                stats.add(stat, multiEntryTable(ite,1,timePeriod, false));
            } //else if(e.consumer instanceof ConsumeItemEfficiency CIE){
            //@Nullable ObjectFloatMap<Item> itemDurationMultipliers = CIE.itemDurationMultipliers;
            //stats.add(Stat.booster, StatValues.itemEffMultiplier(this::itemEfficiencyMultiplier, stats.timePeriod, filter, itemDurationMultipliers));
            //}
        }
    }
    private static StatValue multiEntryTable(Seq<Object> iconObjs, int baseAmount, float timePeriod, boolean booster){
        return table -> {
            table.row();
            table.table(Styles.grayPanel, b -> {
                b.defaults().pad(5).left();

                iconObjs.forEach( img ->{
                    b.add(displayItem((mindustry.type.Item)img, baseAmount, timePeriod, true)).pad(2f).left().wrap();
                });
                b.add(booster ? "[accent]Booster" : "[gray]Required").pad(10f).padRight(4f).right();
            }).growX().pad(3).row();
        };
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
                    b.add("[lightgray]" + "* " + Strings.autoFixed(mult, 2)).pad(10f).right();
                }
                b.add(booster ? "[accent]Booster" : "[gray]Required").pad(10f).padRight(15f).right();
            }).growX().pad(3).row();
        };
    }
}
