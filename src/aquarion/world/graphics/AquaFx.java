package aquarion.world.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import mindustry.entities.Effect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;

import static arc.graphics.Color.alpha;
import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;

public class AquaFx {
    public static final Rand rand = new Rand();
    public static final Vec2 v = new Vec2();

    public static final Effect


    shootLong = new Effect(12, e -> {
        color(Color.white, Pal.lightOrange, e.fin());
        float w = 1.2f + 7 * e.fout();
        Drawf.tri(e.x, e.y, w, 45f * e.fout(), e.rotation);
        Drawf.tri(e.x, e.y, w, 5f * e.fout(), e.rotation + 180f);
    }),
            t1TrailZoarcid = new MultiEffect(
                    new ParticleEffect(){{
                        //it's too much of a pain to do the usual fx
                        lifetime = 35;
                        sizeFrom = 6f;
                        baseRotation = 180;
                        rotWithParent = true;
                        sizeTo = 0;
                        particles = 3;
                        colorFrom = Color.valueOf("181b1c");
                        colorTo = Color.valueOf("181b1c");
                        sizeInterp = Interp.pow10Out;
                        randLength = true;
                        length = 14;
                        interp = Interp.linear;
                        cone = 12;
                        layer = Layer.bullet - 1.2f;
                    }},
                    new ParticleEffect(){{
                        lifetime = 30;
                        sizeFrom = 4f;
                        rotWithParent = true;
                        sizeTo = 0;
                        particles = 3;
                        baseRotation = 180;
                        colorFrom = Color.valueOf("2e3235");
                        colorTo = Color.valueOf("2e3235");
                        randLength = true;
                        length = 8;
                        interp = Interp.linear;
                        sizeInterp = Interp.pow10Out;
                        cone = 8;
                        layer = Layer.bullet - 1.1f;
                    }},
                    new ParticleEffect(){{
                        lifetime = 30;
                        sizeFrom = 3f;
                        sizeTo = 0;
                        particles = 2;
                        baseRotation = 180;
                        rotWithParent = true;
                        colorFrom = Color.valueOf("6d89dd");
                        colorTo = Color.valueOf("444b5e");
                        randLength = true;
                        length = 4;
                        interp = Interp.linear;
                        sizeInterp = Interp.pow10Out;
                        cone = 6;
                        layer = Layer.bullet - 1;
                    }}

            ),
    shootSmokeFormentBauxite = new Effect(35f, e -> {
        color(AquaPal.bauxiteShoot, e.color, e.fin());

        randLenVectors(e.id, 6, e.finpow() * 29f, e.rotation, 26f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 2f + 0.1f);
        });
    }),

    shootSmokeFormentGallium = new Effect(35f, e -> {
        color(Color.white, e.color, e.fin());

        randLenVectors(e.id, 6, e.finpow() * 29f, e.rotation, 26f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 2f + 0.1f);
        });
    }),

    shootSmokeTri = new Effect(45f, e -> {
        color(e.color, e.color, e.fin());

        rand.setSeed(e.id);
        for(int i = 0; i < 3; i++){
            float rot = e.rotation + rand.range(35f);
            v.trns(rot, rand.random(e.finpow() * 30f));
            Fill.poly(e.x + v.x, e.y + v.y, 3, e.fout() * 3.8f + 0.2f, rand.random(360f));
        }
    });
}