package aquarion.world.consumers;

import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.LiquidStack;
import mindustry.ui.ReqImage;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import mindustry.world.meta.Stats;

public class NewConsumeLiquids extends Consume {
    public final LiquidStack[] liquids;

    public NewConsumeLiquids(LiquidStack[] liquids){
        this.liquids = liquids;
    }

    /** Mods.*/
    protected NewConsumeLiquids(){
        this(LiquidStack.empty);
    }

    @Override
    public void apply(Block block){
        block.hasLiquids = true;
        for(var stack : liquids){
            block.liquidFilter[stack.liquid.id] = true;
        }
    }

    @Override
    public void build(Building build, Table table){
        table.table(c -> {
            int i = 0;
            for(var stack : liquids){
                c.add(new ReqImage(stack.liquid.uiIcon,
                        () -> build.liquids.get(stack.liquid) > 0)).size(Vars.iconMed).padRight(8);
                if(++i % 4 == 0) c.row();
            }
        }).left();
    }

    @Override
    public void update(Building build){
        float mult = multiplier.get(build);
        for(var stack : liquids){
            build.liquids.remove(stack.liquid, stack.amount * build.edelta() * mult);
        }
    }

    @Override
    public float efficiency(Building build){
        float mult = multiplier.get(build);
        float ed = build.edelta() * build.efficiencyScale();
        if(ed <= 0.00000001f) return 0f;
        float min = 1f;
        for(var stack : liquids){
            min = Math.min(build.liquids.get(stack.liquid) / (stack.amount * ed * mult), min);
        }
        return min;
    }

    @Override
    public void display(Stats stats){
        stats.add(booster ? Stat.booster : Stat.input, StatValues.liquids(1f, true, liquids));
    }

}