package aquarion.world.graphics;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.world.draw.DrawBlock;

public class DrawBetterArcSmelt extends DrawBlock {
    public Color flameColor = Color.valueOf("f58349"), midColor = Color.valueOf("f2d585");
    public float flameRad = 1f, circleSpace = 2f, flameRadiusScl = 3f, flameRadiusMag = 0.3f, circleStroke = 1.5f;
    public float x = 0, y = 0;
    public float alpha = 0.68f;
    public int particles = 25;
    public float particleLife = 40f, particleRad = 7f, particleStroke = 1.1f, particleLen = 3f;
    public boolean drawCenter = true;
    public Blending blending = Blending.additive;

    @Override
    public void draw(Building build){
        if(build.warmup() > 0f && flameColor.a > 0.001f){
            Lines.stroke(circleStroke * build.warmup());

            float si = Mathf.absin(flameRadiusScl, flameRadiusMag);
            float a = alpha * build.warmup();
            Draw.blend(blending);

            Draw.color(midColor, a);
            if(drawCenter) Fill.circle(build.x + x, build.y + y, flameRad + si);

            Draw.color(flameColor, a);
            if(drawCenter) Lines.circle(build.x + x, build.y + y, (flameRad + circleSpace + si) * build.warmup());

            Lines.stroke(particleStroke * build.warmup());

            float base = (Time.time / particleLife);
            rand.setSeed(build.id);
            for(int i = 0; i < particles; i++){
                float fin = (rand.random(1f) + base) % 1f, fout = 1f - fin;
                float angle = rand.random(360f);
                float len = particleRad * Interp.pow2Out.apply(fin);
                Lines.lineAngle(build.x + Angles.trnsx(angle, len) + x, build.y + Angles.trnsy(angle, len) + y, angle, particleLen * fout * build.warmup());
            }

            Draw.blend();
            Draw.reset();
        }
    }
}
