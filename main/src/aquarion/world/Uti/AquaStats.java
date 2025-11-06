package aquarion.world.Uti;

import arc.Core;
import arc.func.Boolf;
import arc.func.Floatf;
import arc.graphics.Color;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.gen.Iconc;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.ui.Styles;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;

import static mindustry.Vars.tilesize;
import static mindustry.world.meta.StatValues.*;

public class AquaStats {
    public static final Stat
            prodTime = new Stat("production-time"),
            MaxFlow = new Stat("maxflow");
    public static StatValue heatBooster(float heatRequirement, float overheatScale, float maxEfficiency, boolean flipHeat){
        return table -> {
            float overheatHeat = (maxEfficiency - 1f) * heatRequirement / overheatScale;
            float totalHeat = heatRequirement + overheatHeat;
            table.row();
            table.table(Styles.grayPanel, b -> {
                b.defaults().pad(5).left();
                b.add("[accent]Max Heat:[white] " + Strings.autoFixed(totalHeat, 1) + " " +
                        (flipHeat ? "[royal]" : "[red]") + Iconc.waves + "[]").row();
                b.add("[accent]Max Efficiency:[white] " +
                        Strings.autoFixed(maxEfficiency, 2) + StatUnit.timesSpeed.localized()).row();
            }).growX().pad(10f);

            table.row();
        };

    }
    public static StatValue itemBoosters(String unit, float timePeriod, float speedBoost, float rangeBoost, ItemStack[] items, float craftTime){
        return table -> {
            table.row();
            table.table(c -> {
                c.table(Styles.grayPanel, b -> {
                    b.table(it -> {
                        for(ItemStack stack : items) {
                            if (timePeriod < 0) {
                                it.add(displayItem(stack.item, stack.amount, true)).pad(10f).padLeft(15f).left();
                            } else {
                                it.add(displayItem(stack.item, stack.amount, timePeriod, true)).pad(10f).padLeft(15f).left();
                            }
                            for (ItemStack i : items) {
                                it.add(Strings.autoFixed(i.amount / (craftTime / 60f), 20) + StatUnit.perSecond.localized()).left().color(Color.lightGray);
                            }
                        }
                    }).left();

                    b.table(bt -> {
                        bt.right().defaults().padRight(3).left();
                        if(rangeBoost != 0) bt.add("[lightgray]+[stat]" + Strings.autoFixed(rangeBoost / tilesize, 2) + "[lightgray] " + StatUnit.blocks.localized()).row();
                        if(speedBoost != 0) bt.add("[lightgray]" + unit.replace("{0}", "[stat]" + Strings.autoFixed(speedBoost, 2) + "[lightgray]"));
                    }).right().top().grow().pad(10f).padRight(15f);
                }).growX().pad(5).padBottom(-5).row();
            }).growX().colspan(table.getColumns());
            table.row();
        };
    }
    public static StatValue liquidEffMultiplier(Floatf<Liquid> efficiency, float amount, Boolf<Liquid> filter){
        return (Table table) -> {
            if(table.getCells().size > 0){
                ((Cell<?>)table.getCells().peek()).growX();
            }

            table.row();
            table.table(c -> {
                for(Liquid liquid : Vars.content.liquids().select(l -> filter.get(l) && l.unlockedNow() && !l.isHidden())){
                    c.table(Styles.grayPanel, b -> {
                        b.add(displayLiquid(liquid, amount, true)).pad(10f).left().grow();
                        b.add(Core.bundle.format("stat.efficiency", fixValue(efficiency.get(liquid) * 100f)))
                                .right().pad(10f).padRight(15f);
                    }).growX().pad(5f).row();
                }
            }).growX().colspan(table.getColumns()).row();
        };
    }
    public static StatValue itemOutputBoosters(String unit, float timePeriod, float outputBoost, float rangeBoost, ItemStack[] items, float craftTime){
        return table -> {
            table.row();
            table.table(c -> {
                c.table(Styles.grayPanel, b -> {
                    b.table(it -> {
                        for(ItemStack stack : items) {
                            if (timePeriod < 0) {
                                it.add(displayItem(stack.item, stack.amount, true)).pad(10f).padLeft(15f).left();
                            } else {
                                it.add(displayItem(stack.item, stack.amount, timePeriod, true)).pad(10f).padLeft(15f).left();
                            }
                            for (ItemStack i : items) {
                                it.add(Strings.autoFixed(i.amount / (craftTime / 60f), 20) + StatUnit.perSecond.localized()).left().color(Color.lightGray);
                            }
                        }
                    }).left();

                    b.table(bt -> {
                        bt.right().defaults().padRight(3).left();
                        if(rangeBoost != 0)
                            bt.add("[lightgray]+[stat]" + Strings.autoFixed(rangeBoost / tilesize, 2)
                                    + "[lightgray] " + StatUnit.blocks.localized()).row();

                        if(outputBoost != 0)
                            bt.add("[lightgray]" + unit.replace("{0}",
                                    "[stat]" + Strings.autoFixed(outputBoost, 2) + "[lightgray]× output"));
                    }).right().top().grow().pad(10f).padRight(15f);
                }).growX().pad(5).padBottom(-5).row();
            }).growX().colspan(table.getColumns());
            table.row();
        };
    }
    public static StatValue liquidOutputMultiplier(Floatf<Liquid> outputMult, float amount, Boolf<Liquid> filter){
        return (Table table) -> {
            if(table.getCells().size > 0){
                ((Cell<?>)table.getCells().peek()).growX();
            }

            table.row();
            table.table(c -> {
                for(Liquid liquid : Vars.content.liquids().select(l -> filter.get(l) && l.unlockedNow() && !l.isHidden())){
                    c.table(Styles.grayPanel, b -> {
                        b.add(displayLiquid(liquid, amount, true)).pad(10f).left().grow();
                        b.add(Core.bundle.format("stat.outputmultiplier",
                                        Strings.autoFixed(outputMult.get(liquid) * 100f, 1)))
                                .right().pad(10f).padRight(15f)
                                .color(Pal.accent);
                    }).growX().pad(5f).row();
                }
            }).growX().colspan(table.getColumns()).row();
        };
    }

}