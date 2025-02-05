package aquarion.world.graphics;

import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.entities.*;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;

public class AquaFx {
    //I tried... I couldnt
    public static final Rand rand = new Rand();
    public static final Vec2 v = new Vec2();

    public static final Effect strutBulletTrail = new Effect(16, e -> {
        color(Color.white, e.color, e.fin());
        stroke(0.5f + e.fout() * 1.2f);
        rand.setSeed(e.id);

        for(int i = 0; i < 2; i++){
            float rot = e.rotation + rand.range(90f) + 180f;
            v.trns(rot, rand.random(e.fin() * 27f));
            lineAngle(e.x + v.x, e.y + v.y, rot, e.fout() * rand.random(2f, 7f) + 1.5f);
        }
    }),
    shootLong = new Effect(12, e -> {
        color(Color.white, Pal.lightOrange, e.fin());
        float w = 1.2f + 7 * e.fout();
        Drawf.tri(e.x, e.y, w, 45f * e.fout(), e.rotation);
        Drawf.tri(e.x, e.y, w, 5f * e.fout(), e.rotation + 180f);
    }),
            ionizing = new Effect(35f, e -> {
                color(Color.valueOf("ffab84"), Color.valueOf("ba3838"), e.fin());

                randLenVectors(e.id, 3, 2f + e.fin() * 7f, (x, y) -> {
                    Fill.poly(e.x + x, e.y + y, 4, 0.1f + e.fout() * 1.4f);
                });
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
            smallShockwave = new Effect(25f, 80f, e -> {
                color(e.color, Color.lightGray , e.fin());
                stroke(e.fout() * 2f + 0.4f);
                Lines.circle(e.x, e.y, e.fin() * 18f);
            }).layer(Layer.debris),

            pentagonShootSmoke = new Effect(65f, e -> {
                color(Color.valueOf("ffbfc8"),Color.valueOf("e8586c"), e.fin());

                rand.setSeed(e.id);
                for(int i = 0; i < 3; i++){
                    float rot = e.rotation + rand.range(15f);
                    v.trns(rot, rand.random(e.finpow() * 15f));
                    float randomRotationSpeed = rand.random(180f, 360f);
                    float slowRotation = Interp.pow2In.apply(randomRotationSpeed * e.fout());
                    Fill.poly(e.x + v.x, e.y + v.y, 5, e.fout() * 2.5f, rand.random(700f) + slowRotation);
                }
            }),
            GyreShootSmoke = new Effect(48, e -> {
                color(Color.valueOf("e8586c"), e.fin());
                stroke(0.8f + e.fout() * 2.7f);
                rand.setSeed(e.id);

                for(int i = 0; i < 2; i++){
                    float rot = e.rotation + rand.range(15f);
                    v.trns(rot, rand.random(e.fin() * 32f));
                    lineAngle(e.x + v.x, e.y + v.y, rot, e.fout() * rand.random(6f, 7f) + 1.5f);
                }
            }),
    shootSmokeTri = new Effect(45f, e -> {
        color(e.color, e.color, e.fin());

        rand.setSeed(e.id);
        for(int i = 0; i < 3; i++){
            float rot = e.rotation + rand.range(35f);
            v.trns(rot, rand.random(e.finpow() * 30f));
            Fill.poly(e.x + v.x, e.y + v.y, 3, e.fout() * 3.8f + 0.2f, rand.random(360f));
        }
    }),
                    SiliconHearthSmoke = new Effect(65f, e -> {
                        randLenVectors(e.id, 0.65f + e.fin(), 6, 14.3f, (x, y, fin, out) -> {
                            color(Color.darkGray, Pal.coalBlack, e.finpowdown());
                            Fill.circle(e.x + x, e.y + y, out * 8.5f + 0.45f);
                        });
                    }),
                    cuproNickelSmeltSmoke = new Effect(190f, e -> {
                        color(Color.valueOf("df9c887f"));
                        rand.setSeed(e.id);
                        for(int i = 0; i < 3; i++){
                            float len = rand.random(12f);

                            e.scaled(e.lifetime * rand.random(0.45f, 1f), b -> {
                                Fill.circle(e.x + v.x, e.y + v.y, 3.5f * b.fslope() + 0.4f);
                            });
                        }
                    });
}