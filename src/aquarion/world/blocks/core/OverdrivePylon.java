package aquarion.world.blocks.core;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.defense.OverdriveProjector;

import static arc.graphics.g2d.Draw.color;
import static mindustry.Vars.*;

public class OverdrivePylon extends OverdriveProjector {
    Effect effect =  new Effect(100f, e -> {
        color(Pal.redDust);

        Fill.square(e.x, e.y, e.fslope() * 1.5f + 0.14f, 45f);
    });
    public OverdrivePylon(String name) {
        super(name);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        indexer.eachBlock(player.team(), Tmp.r1.setCentered(x, y, range * tilesize), b -> true, t -> {
            Drawf.selected(t, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)));
        });
        Drawf.dashSquare(baseColor.a(0.9f), x * tilesize + offset, y * tilesize + offset, range * tilesize);
    }

    public class OverdrivePylonBuild extends OverdriveBuild {
        public float heat, charge = Mathf.random(reload), phaseHeat, smoothEfficiency, useProgress;
        @Override
        public void drawSelect(){


            indexer.eachBlock(player.team(), Tmp.r1.setCentered(x, y, range * tilesize), b -> true, t -> {
                Drawf.selected(t, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)));
            });
            Drawf.dashSquare(baseColor.a(0.7f), x, y,range * tilesize );

        }

        public void draw(){
            super.draw();

            float f = 1f - (Time.time / 100f) % 1f;

            Draw.color(baseColor, phaseColor, phaseHeat);
            Draw.alpha(heat * Mathf.absin(Time.time, 50f / Mathf.PI2, 1f) * 0.5f);
            Draw.rect(topRegion, x, y);
            Draw.alpha(1f);
            Lines.stroke((2f * f + 0.1f) * heat);
            float r = Math.max(0f, Mathf.clamp(2f - f * 2f) * size * tilesize / 2f - f - 0.2f), w = Mathf.clamp(0.5f - f) * size * tilesize;
            Lines.beginLine();
            for(int i = 0; i < 4; i++){
                Lines.linePoint(x + Geometry.d4(i).x * r + Geometry.d4(i).y * w, y + Geometry.d4(i).y * r - Geometry.d4(i).x * w);
                if(f < 0.5f) Lines.linePoint(x + Geometry.d4(i).x * r - Geometry.d4(i).y * w, y + Geometry.d4(i).y * r + Geometry.d4(i).x * w);
            }
            Lines.endLine(true);

            Draw.reset();
        }

        @Override
        public void updateTile() {
            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
            heat = Mathf.lerpDelta(heat, efficiency > 0 ? 1f : 0f, 0.08f);
            charge += heat * Time.delta;

            if (hasBoost) {
                phaseHeat = Mathf.lerpDelta(phaseHeat, optionalEfficiency, 0.1f);
            }

            if (charge >= reload) {
                float realRange = range + phaseHeat * phaseRangeBoost;

                charge = 0f;
                indexer.eachBlock(this, realRange, other -> other.block.canOverdrive, other -> {
                    other.applyBoost(realBoost(), reload + 1f);
                    if(Mathf.chanceDelta(0.2f * other.block.size * other.block.size )){
                        effect.at(other.x + Mathf.range(other.block.size * tilesize/2f - 1f), other.y + Mathf.range(other.block.size * tilesize/2f - 1f));
                    }
                });
            }

            if (efficiency > 0) {
                useProgress += delta();
            }

            if (useProgress >= useTime) {
                consume();
                useProgress %= useTime;
            }
        }

    }
}
