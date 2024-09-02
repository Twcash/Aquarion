package aquarion.world.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.math.Rand;
import arc.math.geom.Vec2;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;

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