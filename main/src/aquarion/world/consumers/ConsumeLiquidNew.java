package aquarion.world.consumers;


import arc.scene.ui.layout.Table;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.ui.ReqImage;
import mindustry.world.Block;
import mindustry.world.meta.Stat;
import mindustry.world.meta.Stats;

import static mindustry.Vars.iconMed;

public class ConsumeLiquidNew extends ConsumeLiquidBaseNew {
    public final Liquid liquid;

    public ConsumeLiquidNew(Liquid liquid, float amount){
        super();
        this.liquid = liquid;
    }

    protected ConsumeLiquidNew(){
        this(null, 0f);
    }


    public void apply(Block block){
        super.apply(block);
        block.liquidFilter[liquid.id] = true;
    }

    @Override
    public void build(Building build, Table table){
        table.add(new ReqImage(liquid.uiIcon, () -> build.liquids.get(liquid) > 0)).size(iconMed).top().left();
    }

    @Override
    public void update(Building build){
        build.liquids.remove(liquid, amount * build.edelta() * multiplier.get(build));
    }

    @Override
    public float efficiency(Building build){
        float ed = build.edelta() * build.efficiencyScale();
        if(ed <= 0.00000001f) return 0f;
        //there can be more liquid than necessary, so cap at 1
        return Math.min(build.liquids.get(liquid) / (amount * ed * multiplier.get(build)), 1f);
    }

    @Override
    public void display(Stats stats){
        stats.add(booster ? Stat.booster : Stat.input, liquid, amount * 60f, true);
    }

    public boolean consumes(Liquid liquid){
        return liquid == this.liquid;
    }
}