package aquarion.world.graphics;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.world.draw.DrawParticles;

public class BetterDrawParticles extends DrawParticles {
    public float x = 0;
    public float y = 0;
    @Override
    public void draw(Building build){

        if(build.warmup() > 0f){

            float a = alpha * build.warmup();
            Draw.blend(blending);
            Draw.color(color);

            float base = (Time.time / particleLife);
            rand.setSeed(build.id);
            for(int i = 0; i < particles; i++){
                float fin = (rand.random(2f) + base) % 1f;
                if(reverse) fin = 1f - fin;
                float fout = 1f - fin;
                float angle = rand.random(360f) + (Time.time / rotateScl) % 360f;
                float len = particleRad * particleInterp.apply(fout);
                Draw.alpha(a * (1f - Mathf.curve(fin, 1f - fadeMargin)));
                Fill.circle(
                        build.x + x + Angles.trnsx(angle, len),
                        build.y + y + Angles.trnsy(angle, len),
                        particleSize * particleSizeInterp.apply(fin) * build.warmup()
                );
            }

            Draw.blend();
            Draw.reset();
        }
    }
}
