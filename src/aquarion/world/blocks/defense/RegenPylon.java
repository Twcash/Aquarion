package aquarion.world.blocks.defense;

import mindustry.world.blocks.defense.MendProjector;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static arc.graphics.g2d.Draw.*;
import static mindustry.Vars.*;

public class RegenPylon extends MendProjector {
    Effect effect2 = new Effect(40, e -> {
        if(!(e.data instanceof Block block)) return;
        mixcol(e.color, 1f);
        alpha(e.fout());
        Draw.rect(block.fullIcon, e.x, e.y);
    });
Effect effect =  new Effect(100f, e -> {
    color(Pal.heal);

    Fill.square(e.x, e.y, e.fslope() * 1.5f + 0.14f, 45f);
});
    @Override
    public void setStats(){
        stats.timePeriod = useTime;
        super.setStats();
        stats.remove(Stat.repairTime);
        stats.remove(Stat.range);
        stats.add(Stat.repairTime, (int)(100f / healPercent * reload / 60f), StatUnit.seconds);
        stats.add(Stat.range, range / tilesize, StatUnit.blocks);

        if(findConsumer(c -> c instanceof ConsumeItems) instanceof ConsumeItems cons){
            stats.remove(Stat.booster);
            stats.add(Stat.booster, StatValues.itemBoosters(
                    "{0}" + StatUnit.timesSpeed.localized(),
                    stats.timePeriod, (phaseBoost + healPercent) / healPercent, range * phaseRangeBoost,
                    cons.items)
            );
        }
    }
    public RegenPylon(String name) {
        super(name);
        phaseBoost = 6;
    }
    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        float r = range  * phaseRangeBoost;
        indexer.eachBlock(player.team(), Tmp.r1.setCentered(x, y, r * tilesize), b -> true, t ->
                Drawf.selected(t, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)))
        );
        Draw.alpha(0.25f);
        Drawf.dashSquare(baseColor.a(0.9f), x * tilesize + offset, y * tilesize + offset, r * tilesize );
        Draw.alpha(1f);
        Drawf.dashSquare(baseColor.a(0.9f), x * tilesize + offset, y * tilesize + offset, r * tilesize );
    }

    public class RegenPylonBuild extends MendBuild{

        public Seq<Building> targets = new Seq<>();

        @Override
        public void drawSelect(){
            float r = realRange(); // boosted range
            indexer.eachBlock(team, Tmp.r1.setCentered(x, y, realRange() * tilesize), b -> true, t ->
                    Drawf.selected(t, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)))
            );
            Drawf.dashSquare(baseColor.a(0.7f), x, y, r * tilesize);
        }


        @Override
        public void updateTile(){
            boolean canHeal = !checkSuppression();

            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
            heat = Mathf.lerpDelta(heat, efficiency > 0 && canHeal ? 1f : 0f, 0.08f);
            charge += heat * delta();

            phaseHeat = Mathf.lerpDelta(phaseHeat, optionalEfficiency, 0.1f);

            if(optionalEfficiency > 0 && timer(timerUse, useTime) && canHeal){
                consume();
            }

            if(charge >= reload/(phaseHeat*phaseBoost) && canHeal){
                charge = 0f;
                float r = realRange(); // use the boosted range here

                indexer.eachBlock(team, Tmp.r1.setCentered(x, y, realRange() * tilesize),b -> b.damaged() && !b.isHealSuppressed(), t -> {
                    t.heal(t.maxHealth() * (healPercent + phaseHeat * phaseBoost) / 100f * efficiency);
                    t.recentlyHealed();
                    effect2.at(t.x, t.y, t.block.size, baseColor, t.block);
                    if(Mathf.chanceDelta(0.9f * t.block.size * t.block.size) && t.damaged()){
                        effect.at(t.x + Mathf.range(t.block.size * tilesize/2f - 1f), t.y + Mathf.range(t.block.size * tilesize/2f - 1f));
                    }
                });
            }
        }
        public float realRange(){
            return range + phaseHeat * phaseRangeBoost;
        }
    }
}
