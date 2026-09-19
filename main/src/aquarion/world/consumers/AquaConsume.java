package aquarion.world.consumers;
import mindustry.Vars;
import arc.func.*;
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
    public boolean separateEntries = false;
    public boolean preferLast = false;

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

    public AquaConsume set(float multi, boolean req){
        if(!entries.isEmpty()){
            entries.first().multiplier = multi;
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
            }else if(e.consumer instanceof ConsumeItems ci){
                block.hasItems = true;
                for(ItemStack item : ci.items){
                    block.itemFilter[item.item.id] = true;
                }
            }else{
                e.consumer.apply(block);
            }
        }
    }

    @Override
    public void build(Building build, Table table){
        for(Entry e : entries){
            e.consumer.build(build, table);
        }
    }

    @Override
    public void update(Building build){
        if(separateEntries){
            Entry selected = selectedEntry(build);
            if(selected != null){
                selected.consumer.update(build);
            }
        }else{
            for(Entry e : entries){
                e.consumer.update(build);
            }
        }
    }

    @Override
    public void trigger(Building build){
        if(separateEntries){
            Entry selected = selectedEntry(build);
            if(selected != null){
                selected.consumer.trigger(build);
            }
        }else{
            for(Entry e : entries){
                e.consumer.trigger(build);
            }
        }
    }
    private Entry selectedEntry(Building build){
        for(int i = entries.size - 1; i >= 0; i--){
            Entry e = entries.get(i);

            if(!e.required) continue;

            if(e.consumer.efficiency(build) >= 0.9999f){
                return e;
            }
        }

        for(int i = entries.size - 1; i >= 0; i--){
            Entry e = entries.get(i);

            if(!e.required) continue;

            if(e.consumer.efficiency(build) > 0f){
                return e;
            }
        }

        return null;
    }

    @Override
    public float efficiency(Building build){
        if(separateEntries){
            Entry selected = selectedEntry(build);
            return selected == null ? 0f : selected.consumer.efficiency(build);
        }

        float min = 1f;

        for(Entry e : entries){
            if(!e.required) continue;
            min = Math.min(min, e.consumer.efficiency(build));
        }

        return min;
    }

    @Override
    public float efficiencyMultiplier(Building build){
        if(separateEntries){
            Entry selected = selectedEntry(build);
            if(selected == null) return 1f;

            return selected.multiplier * selected.consumer.efficiencyMultiplier(build);
        }

        float prod = 1f;

        for(Entry e : entries){
            prod *= e.consumer.efficiencyMultiplier(build);
        }

        return prod;
    }
    public float outputMultiplier(Building build){
        if(!separateEntries) return 1f;

        Entry selected = selectedEntry(build);
        return selected == null ? 1f : selected.multiplier;
    }
    public void display(Stats stats, float timePeriod){
        if(separateEntries){
            stats.add(Stat.input, orEntryTable(timePeriod));
            return;
        }

        for(Entry e : entries){
            Stat stat = e.required ? Stat.input : Stat.booster;

            if(e.consumer instanceof ConsumeLiquid cl){
                stats.add(stat, entryTable(
                        cl.liquid,
                        cl.amount,
                        e.multiplier,
                        timePeriod,
                        !e.required,
                        true,
                        false
                ));
            }else if(e.consumer instanceof ConsumeLiquids cls){
                for(LiquidStack ls : cls.liquids){
                    stats.add(stat, entryTable(
                            ls.liquid,
                            ls.amount,
                            e.multiplier,
                            timePeriod,
                            !e.required,
                            true,
                            false
                    ));
                }
            }else if(e.consumer instanceof ConsumeItems ci){
                for(ItemStack is : ci.items){
                    stats.add(stat, entryTable(
                            is.item,
                            is.amount,
                            e.multiplier,
                            timePeriod,
                            !e.required,
                            false,
                            false
                    ));
                }
            }
        }
    }
    private StatValue orEntryTable(float timePeriod){
        return table -> {
            table.row();

            table.table(Styles.grayPanel, b -> {
                b.defaults().pad(5).left();

                for(int i = 0; i < entries.size; i++){
                    Entry e = entries.get(i);

                    if(e.consumer instanceof ConsumeLiquid cl){
                        b.add(displayLiquid(
                                cl.liquid,
                                cl.amount * 60f,
                                true
                        )).pad(10f).left();
                    }else if(e.consumer instanceof ConsumeItems ci){
                        for(ItemStack is : ci.items){
                            b.add(displayItem(
                                    is.item,
                                    Math.round(is.amount),
                                    timePeriod,
                                    true
                            )).pad(10f).left();
                        }
                    }

                    if(i < entries.size - 1){
                        b.add("[accent]OR")
                                .pad(10f)
                                .center();
                    }
                }

                Entry last = entries.peek();

                if(last.multiplier != 1f){
                    b.add(
                                    "[lightgray]Output x" +
                                            Strings.autoFixed(last.multiplier, 2)
                            )
                            .pad(10f)
                            .right();
                }

                b.add("[gray]Required")
                        .pad(10f)
                        .padRight(15f)
                        .right();

            }).growX().pad(3f).row();
        };
    }

    private static StatValue entryTable(
            Object iconObj,
            float baseAmount,
            float mult,
            float timePeriod,
            boolean booster,
            boolean isLiquid,
            boolean or
    ){
        return table -> {
            table.row();

            table.table(Styles.grayPanel, b -> {
                b.defaults().pad(5).left();

                if(isLiquid){
                    b.add(displayLiquid(
                            (mindustry.type.Liquid)iconObj,
                            baseAmount * 60f,
                            true
                    )).pad(10f).left();
                }else{
                    b.add(displayItem(
                            (mindustry.type.Item)iconObj,
                            Math.round(baseAmount),
                            timePeriod,
                            true
                    )).pad(10f).left();
                }

                if(or){
                    b.add("[accent]OR").pad(10f).right();
                }

                if(mult != 1f){
                    b.add("[lightgray]Output x" + Strings.autoFixed(mult, 2))
                            .pad(10f)
                            .right();
                }

                b.add(booster ? "[accent]Booster" : "[gray]Required")
                        .pad(10f)
                        .padRight(15f)
                        .right();
            }).growX().pad(3).row();
        };
    }
}