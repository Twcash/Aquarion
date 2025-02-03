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

import static arc.graphics.g2d.Draw.*;
import static mindustry.Vars.*;

public class RegenPylon extends MendProjector {
    Effect effect2 = new Effect(40, e -> {
        if(!(e.data instanceof Block block)) return;

        mixcol(e.color, 0.5f);
        alpha(e.fout());
        Draw.rect(block.fullIcon, e.x, e.y);
    });
Effect effect =  new Effect(100f, e -> {
    color(Pal.heal);

    Fill.square(e.x, e.y, e.fslope() * 1.5f + 0.14f, 45f);
});

    public RegenPylon(String name) {
        super(name);
    }
    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        indexer.eachBlock(player.team(), Tmp.r1.setCentered(x, y, range * tilesize), b -> true, t -> {
            Drawf.selected(t, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)));
        });
        Drawf.dashSquare(baseColor.a(0.9f), x * tilesize + offset, y * tilesize + offset,range * tilesize );
    }

    public class RegenPylonBuild extends MendBuild{

        public Seq<Building> targets = new Seq<>();
        public void updateTargets(){
            targets.clear();
            indexer.eachBlock(team, Tmp.r1.setCentered(x, y, range * tilesize), b -> true, targets::add);
        }
        @Override
        public void drawSelect(){


            indexer.eachBlock(player.team(), Tmp.r1.setCentered(x, y, range * tilesize), b -> true, t -> {
                Drawf.selected(t, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)));
            });
            Drawf.dashSquare(baseColor.a(0.7f), x, y,range * tilesize );

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

            if(charge >= reload && canHeal){
                float realRange = range + phaseHeat * phaseRangeBoost;
                charge = 0f;

                indexer.eachBlock(player.team(), Tmp.r1.setCentered(x, y, range * tilesize),  b -> b.damaged() && !b.isHealSuppressed(), t -> {
                    t.heal(t.maxHealth() * (healPercent + phaseHeat * phaseBoost) / 100f * efficiency);
                    t.recentlyHealed();
                    effect2.at(t.x, t.y, t.block.size, baseColor, t.block);
                    if(Mathf.chanceDelta(0.9f * t.block.size * t.block.size ) && t.damaged()){
                        effect.at(t.x + Mathf.range(t.block.size * tilesize/2f - 1f), t.y + Mathf.range(t.block.size * tilesize/2f - 1f));
                    }
                });
            }
        }

    }
}
