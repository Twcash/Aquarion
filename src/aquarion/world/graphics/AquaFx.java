package aquarion.world.graphics;

import arc.*;
import arc.func.Floatc2;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.Liquids;
import mindustry.entities.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.graphics.*;

import static arc.graphics.g2d.Draw.rect;
import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;
import static arc.math.Angles.*;

public class AquaFx {
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
            shootHori = new Effect(12, e -> {
                color(Color.white, Pal.lightOrange, e.fin());
                float w = 1.2f + 7 * Interp.pow2Out.apply(e.fout());
                Drawf.tri(e.x, e.y, w/1.5f, 30f * e.fout(), e.rotation - 90);
                Drawf.tri(e.x, e.y, w/1.5f, 5f * e.fout(), e.rotation - 90f);
                Drawf.tri(e.x, e.y, w/1.5f, 30f * e.fout(), e.rotation + 90);
                Drawf.tri(e.x, e.y, w/1.5f * e.fout(), 5f * e.fout(), e.rotation + 90f);
                Drawf.tri(e.x, e.y, w, 45f * Interp.pow5Out.apply(e.fout()), e.rotation);
                Drawf.tri(e.x, e.y, w, 5f * Interp.pow5Out.apply(e.fout()), e.rotation + 180);
            }),
    shootLong = new Effect(12, e -> {
        color(Color.white, Pal.lightOrange, e.fin());
        float w = 1.2f + 7 * e.fout();
        Drawf.tri(e.x, e.y, w, 45f * e.fout(), e.rotation);
        Drawf.tri(e.x, e.y, w, 5f * e.fout(), e.rotation + 180f);
    }),
            ionizing = new Effect(35f, e -> {
                color(Color.valueOf("ffab84"), Color.valueOf("ba3838"), e.fin());

                randLenVectors(e.id, 3, 2f + e.fin() * 7f, (x, y) ->
                    Fill.poly(e.x + x, e.y + y, 4, 0.1f + e.fout() * 1.4f)
                );
            }),
            t1TrailZoarcid = new MultiEffect(
                    new ParticleEffect(){{
                        //it's too much of a pain to do the usual fx
                        lifetime = 35;
                        sizeFrom = 3f;
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
                        layer = Layer.flyingUnitLow - 2f;
                    }},
                    new ParticleEffect(){{
                        lifetime = 30;
                        sizeFrom = 2f;
                        baseRotation = 180;
                        rotWithParent = true;
                        sizeTo = 0;
                        particles = 3;
                        colorFrom = Color.valueOf("2e3235");
                        colorTo = Color.valueOf("2e3235");
                        randLength = true;
                        length = 8;
                        interp = Interp.linear;
                        sizeInterp = Interp.pow10Out;
                        cone = 8;
                        layer = Layer.flyingUnitLow - 1.9f;
                    }},
                    new ParticleEffect(){{
                        lifetime = 30;
                        sizeFrom = 1f;
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
                        layer = Layer.flyingUnitLow - 1.8f;
                    }}

            ),
            t2TrailAnguilli = new MultiEffect(
                    new ParticleEffect(){{
                        //it's too much of a pain to do the usual fx
                        lifetime = 90;
                        sizeFrom = 10f;
                        baseRotation = 180;
                        rotWithParent = true;
                        sizeTo = 0;
                        particles = 3;
                        colorFrom = Color.valueOf("181b1c");
                        colorTo = Color.valueOf("181b1c");
                        sizeInterp = Interp.pow10Out;
                        randLength = false;
                        length = 18;
                        interp = Interp.linear;
                        cone = 13;
                        layer = Layer.flyingUnitLow - 2f;
                    }},
                    new ParticleEffect(){{
                        lifetime = 70;
                        sizeFrom = 6f;
                        rotWithParent = true;
                        sizeTo = 0;
                        particles = 3;
                        baseRotation = 180;
                        colorFrom = Color.valueOf("2e3235");
                        colorTo = Color.valueOf("2e3235");
                        randLength = true;
                        length = 10;
                        interp = Interp.linear;
                        sizeInterp = Interp.pow10Out;
                        cone = 9;
                        layer = Layer.flyingUnitLow - 1.9f;
                    }},
                    new ParticleEffect(){{
                        lifetime = 50;
                        sizeFrom = 4f;
                        sizeTo = 0;
                        particles = 2;
                        baseRotation = 180;
                        rotWithParent = true;
                        colorFrom = Color.valueOf("6d89dd");
                        colorTo = Color.valueOf("444b5e");
                        randLength = true;
                        length = 8;
                        interp = Interp.linear;
                        sizeInterp = Interp.pow10Out;
                        cone =7;
                        layer = Layer.flyingUnitLow - 1.8f;
                    }}

            ),

    fomentShootSmoke = new MultiEffect(new Effect(85f, e -> {
        color(Color.gray, Color.black, e.color, e.fin());

        randLenVectors(e.id, 9, Interp.pow5Out.apply(e.finpow() )* 25f, e.rotation, 35f, (x, y) -> {
                    Draw.alpha(0.5f);
                    Fill.circle(e.x + x / Interp.pow2Out.apply(e.fout()), e.y + y / Interp.pow2Out.apply(e.fout()), Interp.pow2In.apply(e.fout()) * 4f + 0.1f);
                    Draw.alpha(1);
                }
        );

    }),new Effect(45f, e -> {
        color(Color.white, AquaPal.fireLight1, Color.black, e.fin());

        randLenVectors(e.id, 12, Interp.pow5Out.apply(e.finpow() )* 25f, e.rotation, 26f, (x, y) ->
            Fill.circle(e.x + x, e.y + y, Interp.pow2Out.apply(e.fout())  * 2.5f + 0.1f)
        );

    }), new Effect(40, e->{
        color(AquaPal.fireLight2, AquaPal.fireLight1, e.color, e.fin());
        stroke(2f *e.fout());
        rand.setSeed(e.id);

        for(int i = 0; i < 6; i++){
            float rot = e.rotation + rand.range(34f);
            v.trns(rot, rand.random(e.fin() * 27f));
            lineAngle(e.x + v.x, e.y + v.y, rot, Interp.pow2In.apply(e.fout() )* rand.random(4f, 12f) + 2.5f);
        }
    })),
            fomentHitColor = new Effect(18, e -> {
                color(Color.white, e.color, e.fin());

                e.scaled(7f, s -> {
                    stroke(0.5f + s.fout());
                    Lines.circle(e.x, e.y, s.fin() * 5f);
                });
                color(Color.gray, Color.black, e.color, e.fin());
                stroke(0.5f + e.fout());

                randLenVectors(e.id, 5, e.fin() * 17f, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.fout() * 4.2f);
                });
                color(AquaPal.fireLight2, AquaPal.fireLight1, e.color, e.fin());
                randLenVectors(e.id + 1, 7, e.fin() * 17f, (x, y) -> {
                    Fill.poly(e.x + x, e.y + y,  3, Interp.pow2Out.apply(e.fout()) * 2.2f, rand.random(360f));
                });
                Drawf.light(e.x, e.y, 20f, e.color, 0.6f * e.fout());
            }),
    shootSmokeFormentGallium = new Effect(35f, e -> {
        color(Color.white, e.color, e.fin());

        randLenVectors(e.id, 6, e.finpow() * 29f, e.rotation, 26f, (x, y) ->
            Fill.circle(e.x + x, e.y + y, e.fout() * 2f + 0.1f)
        );
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
                    heatEngineGenerate = new Effect(180f, e -> {
                        color(Pal.lightOrange, AquaPal.smoke, Interp.pow2In.apply(e.fin()));
                        alpha(Interp.pow2In.apply(e.fout()));
                        randLenVectors(e.id, 4,Interp.pow5In.apply(e.finpow()) * 56f, 32f, 26f, (x, y) ->
                                Fill.circle(e.x + x , e.y + y, Interp.pow5In.apply(e.finpow()) * 3.5f + 0.1f)
                        );
                    }),
                    diffuserSmoke = new Effect(40, e->{
                        color(Pal.darkestestGray, Pal.darkishGray, e.foutpowdown());
                        Draw.z(Layer.effect -1);
                        blend(Blending.normal);
                        Draw.z(Layer.effect);
                        randLenVectors(e.id, 4, e.fin()*8, (x, y)->{
                            Fill.circle(e.x+x, e.y+y, 6*e.fout());
                        });
                        rand.setSeed(e.id);
                        Color base = Pal.turretHeat.cpy().lerp(Pal.accent, rand.random(1f));
                        Draw.alpha(e.foutpowdown());
                        Draw.z(Layer.effect+1);

                        blend(Blending.additive);
                        color( Pal.darkestestGray, base, Interp.circleIn.apply(e.foutpowdown()));
                        randLenVectors(e.id+1, 3, e.fin()*6, (x, y)->{
                            Fill.circle(e.x+x, e.y+y, 4*e.fout());
                        });
                        Draw.alpha(1);
                        blend();
                    }),
                    diffuserCraft = new Effect(110, e->{
                        color(Color.black, Pal.darkishGray, e.fin());
                        alpha(1);
                        blend(Blending.normal);
                        z(Layer.effect-1);
                        randLenVectors(e.id, 18, Interp.circleOut.apply(e.finpow())*44, 0+e.rotation, 18, (x, y)->{
                            Fill.circle(e.x+x, e.y+y, 8*e.fout());
                        });
                        rand.setSeed(e.id);

                        Color base = Pal.turretHeat.cpy().lerp(Pal.accent, rand.random(1f));
                        Draw.alpha(e.foutpowdown());
                        Draw.z(Layer.effect+1);

                        blend(Blending.additive);
                        color( Pal.darkestestGray, base, Interp.circleOut.apply(e.foutpowdown()));
                        alpha(e.foutpowdown());
                        randLenVectors(e.id+1, 7, Interp.circleOut.apply(e.finpow())*25, 0+e.rotation, 12, (x, y)->{
                            Fill.circle(e.x+x, e.y+y, 4.5f*e.fout());
                        });
                        blend();
                    }),
                    ovenCraft = new Effect(70, e->{
                        color(Pal.darkishGray, Pal.lightishGray, e.fin());
                        alpha(1*e.fout());
                        blend(Blending.normal);
                        z(Layer.effect-1);
                        randLenVectors(e.id, 6, Interp.circleOut.apply(e.finpow())*15, 0+e.rotation, 180, (x, y)->{
                            Fill.circle(e.x+x - 27/4f, e.y+y-66/4f, 3.5f*e.fin());
                        });
                        randLenVectors(e.id, 6, Interp.circleOut.apply(e.finpow())*15, 0+e.rotation, 180, (x, y)->{
                            Fill.circle(e.x+x - 56/4f, e.y+y-66/4f, 3.5f*e.fin());
                        });
                        rand.setSeed(e.id);
                    }),
                    boilerSmoke = new Effect(220f, e -> {
                        for(int i = 0; i < 4; i++){
                            int f = i;
                            rand.setSeed(e.id);
                            blend(Blending.normal);
                            Color base2 = Pal.darkestGray.cpy().lerp(Pal.darkerMetal, rand.random(1f));
                            color(base2, Pal.darkestestGray, Interp.circleIn.apply(e.foutpowdown()));
                            Draw.alpha(e.foutpowdown());
                            Draw.z(Layer.effect - 1);
                            randLenVectors(e.id+i,  3, Interp.pow5In.apply(e.finpow()) * 38f, 13f, 15f, 1.5f, (x, y) ->
                                    Fill.circle(e.x + x +24, e.y + y + (16 - (f *8.5f)), Interp.pow2In.apply(e.finpow()) * 5.5f + 0.1f));
                            blend(Blending.additive);
                            Draw.z(Layer.effect);
                            Color base = Pal.turretHeat.cpy().lerp(Pal.accent, rand.random(1f));
                            color(base, Pal.darkestestGray, Interp.circleIn.apply(e.foutpowdown()));
                            Draw.alpha(e.foutpowdown());
                            randLenVectors(e.id+i, 2, Interp.pow5In.apply(e.finpow()) * 24f, 13f, 12f, 1.5f, (x, y) ->
                                    Fill.circle(e.x + x +24, e.y + y + (16 - (f *8.5f)), Interp.pow2In.apply(e.finpow()) * 2.5f + 0.1f));

                        }
                        Draw.alpha(1);
                        blend();
                    }),
                    vent1 = new Effect(220f, e -> {
                        rand.setSeed(e.id+1);
                        Color base = Color.valueOf("f2f17f").lerp(Color.valueOf("99784f"), rand.random(0, 1));
                        rand.setSeed(e.id+5);
                        color(base, AquaPal.smoke.a(0.5f).lerp(Color.valueOf("4a3f3c"), rand.random(0, 1)), Interp.pow2Out.apply(e.fin()));
                        alpha(Interp.pow2In.apply(e.fout()));
                        rand.setSeed(e.id+2);
                        randLenVectors(e.id, 4,Interp.pow5In.apply(e.finpow()) * 110f, 32f+Time.time/100, 25f, (x, y) ->
                        {
                            rand.setSeed(e.id+4);
                            float lift = Interp.pow2Out.apply(e.fin()) * rand.random(0, 72);
                            rand.setSeed(e.id+7);
                            Fill.circle(e.x+x+rand.random(-4, 4), e.y + lift+y+rand.random(-4, 4), Interp.pow5In.apply(e.finpow()) * 9.5f + 0.4f);
                        });
                    }),
                    turbineGenerate = new Effect(180f, e -> {
                        color(Color.white, AquaPal.smoke.a(0.5f), Interp.pow2In.apply(e.fin()));
                        alpha(Interp.pow2In.apply(e.fout()));
                        randLenVectors(e.id, 2,Interp.pow5In.apply(e.finpow()) * 80f, 32f, 30f, (x, y) ->
                        {
                            Fill.circle(e.x + x + 77 / 4f, e.y + y - 90 / 4f, Interp.pow5In.apply(e.finpow()) * 6.5f + 0.1f);
                        });
                        randLenVectors(e.id + 1,  2,Interp.pow5In.apply(e.finpow()) * 80f, 32f, 30f, (x, y) ->
                        {
                            Fill.circle(e.x + x+77/4f, e.y + y-72/4f, Interp.pow5In.apply(e.finpow()) * 6.5f + 0.1f);
                        });
                        randLenVectors(e.id +2, 2,Interp.pow5In.apply(e.finpow()) * 80f, 32f, 30f, (x, y) ->
                        {
                            Fill.circle(e.x + x+77/4f, e.y + y-110/4f, Interp.pow5In.apply(e.finpow()) * 6.5f + 0.1f);
                        });
                    }),
                    hydroxideReactorGenerate = new Effect(220f, e -> {
                        color(Pal.sap, AquaPal.smoke, Interp.pow2In.apply(e.fin()));
                        alpha(Interp.pow2In.apply(e.fout()));
                        randLenVectors(e.id, 5,Interp.pow5In.apply(e.finpow()) * 70f, 32f, 28f, (x, y) ->
                                Fill.circle(e.x + x, e.y + y, Interp.pow5In.apply(e.finpow()) * 5f + 0.1f)
                        );
                    }),
                            thrashExplosion = new Effect(90f, 160f, e -> {
                                color(Pal.lightOrange);
                                stroke(e.fout() * 4f);
                                float circleRad = 6f + e.finpow() * 64f;
                                Lines.circle(e.x, e.y, circleRad);

                                rand.setSeed(e.id);
                                for(int i = 0; i < 6; i++){
                                    float angle = rand.random(360f);
                                    float lenRand = rand.random(0.8f, 1f);
                                    Tmp.v1.trns(angle, circleRad);

                                    for(int s : Mathf.signs){
                                        Drawf.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.foutpow() * 30f, e.fout() * 50f * lenRand + 7f, angle - 90f + s * 90f);
                                    }
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
                            shootSmokeMassive = new Effect(120f, e -> {
                                rand.setSeed(e.id);
                                for(int i = 0; i < 18; i++){
                                    v.trns(e.rotation + rand.range(25f), rand.random(e.finpow() * 60f));
                                    e.scaled(e.lifetime * rand.random(0.6f, 1f), b -> {
                                        color(e.color, Pal.lightishGray, b.fin());
                                        Fill.circle(e.x + v.x, e.y + v.y, b.fout() * 7.4f + 0.3f);
                                    });
                                }
                            }),
                            shootLudicrous = new Effect(14, e -> {
                                color(Pal.lighterOrange, Pal.lightOrange, e.fin());
                                float w = 1.2f + 12 * e.fout();
                                Drawf.tri(e.x, e.y, w, 40f * e.fout(), e.rotation);
                                Drawf.tri(e.x, e.y, w, 15f * e.fout(), e.rotation-90);
                                Drawf.tri(e.x, e.y, w, 15f * e.fout(), e.rotation+90);
                                Drawf.tri(e.x, e.y, w, 7f * e.fout(), e.rotation + 180f);

                            }),
                            thrashShootSmoke = new Effect(145f, e -> {
                                color(Pal.lightOrange, Color.black, e.color, e.fin());

                                randLenVectors(e.id, 12, Interp.pow5Out.apply(e.fin() )* 40f, e.rotation, 35f, (x, y) -> {
                                            Fill.circle(e.x + x / Interp.pow2In.apply(e.fout()), e.y + y / Interp.pow2In.apply(e.fout()), Interp.pow2In.apply(e.fout()) * 6f + 0.1f);
                                        }
                                );

                            }),
                            flagellateExplosion = new Effect(30, 500f, b -> {
                                float intensity = 4.8f;
                                float baseLifetime = 25f + intensity * 11f;
                                b.lifetime = 50f + intensity * 65f;
                                rand.setSeed(b.id);

                                color(Pal.lighterOrange, Pal.stoneGray, Interp.pow2In.apply(b.fin()));
                                alpha(0.7f);
                                for(int i = 0; i < 4; i++){
                                    rand.setSeed(b.id*2 + i);
                                    float lenScl = rand.random(0.4f, 1f);
                                    int fi = i;
                                    b.scaled(b.lifetime * lenScl, e -> {
                                        randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(2.9f * intensity), 24f * intensity, (x, y, in, out) -> {
                                            float fout = e.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                                            float rad = fout * ((3f + intensity) * 2.35f);

                                            Fill.circle(e.x + x, e.y + y, rad);
                                            Drawf.light(e.x + x, e.y + y, rad * 3.5f, Pal.reactorPurple, 1f);
                                        });
                                    });
                                }

                                b.scaled(baseLifetime, e -> {
                                    Draw.color();
                                    e.scaled(15 + intensity * 2f, i -> {
                                        stroke((4.1f + intensity/5f) * i.fout());
                                        Lines.circle(e.x, e.y, (3f + i.fin() * 20f) * intensity);
                                        Drawf.light(e.x, e.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * e.fout());
                                    });

                                    color(Pal.lighterOrange, Pal.turretHeat, e.fin());
                                    stroke((2f * e.fout()));

                                    Draw.z(Layer.effect + 0.001f);
                                    randLenVectors(e.id + 1, e.finpow() + 0.001f, (int)(8 * intensity), 32f * intensity, (x, y, in, out) -> {
                                        lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * 6 * (4f + intensity));
                                        Drawf.light(e.x + x, e.y + y, (out * 5 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
                                    });
                                });
                            }),
                            thrashTrailSmoke = new Effect(90f, e -> {
                                color(Pal.lightOrange, Color.black, e.color, e.fin());

                                randLenVectors(e.id, 4, Interp.pow5Out.apply(e.fin() )* 5f, e.rotation, 180f, (x, y) -> {
                                            Fill.circle(e.x + x / Interp.pow2In.apply(e.fout()), e.y + y / Interp.pow2In.apply(e.fout()), Interp.pow2In.apply(e.fout()) * 3f + 0.1f);
                                        }
                                );

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
                    SiliconHearthSmoke = new Effect(65f, e ->
                        randLenVectors(e.id, 0.65f + e.fin(), 6, 14.3f, (x, y, fin, out) -> {
                            color(Color.darkGray, Pal.coalBlack, e.finpowdown());
                            Fill.circle(e.x + x, e.y + y, out * 8.5f + 0.45f);
                        })
                    ),
                    cuproNickelSmeltSmoke = new Effect(190f, e -> {
                        color(Color.valueOf("df9c887f"));
                        rand.setSeed(e.id);
                        for(int i = 0; i < 3; i++){
                            float len = rand.random(12f);

                            e.scaled(e.lifetime * rand.random(0.45f, 1f), b ->
                                Fill.circle(e.x + v.x, e.y + v.y, 3.5f * b.fslope() + 0.4f)
                            );
                        }
                    }),

    hitCryoSmall = new Effect(14, e -> {
        color(Pal.techBlue, Color.blue, e.fin());
        stroke(0.5f + e.fout());

        randLenVectors(e.id, 2, 1f + e.fin() * 15f, e.rotation, 50f, (x, y) -> {
            float ang = Mathf.angle(x, y);
            lineAngle(e.x + x, e.y + y, ang, e.fout() * 3 + 1f);
        });
    }),
            shootSmallCryo = new Effect(32f, 80f, e -> {
                color(Pal.techBlue, Color.blue, Liquids.cryofluid.color, e.fin());

                randLenVectors(e.id, 8, e.finpow() * 60f, e.rotation, 10f, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * 1.5f);
                });
            }),
            shootBigCryo = new Effect(42f, 120f, e -> {
                color(Pal.techBlue, Color.blue, Liquids.cryofluid.color, e.fin());

                randLenVectors(e.id, 12, e.finpow() * 85f, e.rotation, 14f, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, 1.23f + e.fout() * 1.5f);
                });
            }),
    parzilDebrisSmall = new Effect(85, e -> {
                rand.setSeed(e.id);
                color( Color.valueOf("ffffff"), Color.valueOf("ffffff").a(e.fin()), e.fin());

                for (int i = 0; i < 25; i++) {
                    float rot = e.rotation + rand.range(360f); // Similar to shootSmokeSquare
                    int regionId = rand.random(1, 3);
                    TextureRegion region = Core.atlas.find("aquarion-parzil-debris" + regionId);
                    v.trns(rot, rand.random(e.finpow() * 21f));
                    float fout = Math.max(e.fout(), 0.1f);
                    float size = fout * 24f + 0.2f;
                    Draw.rect(region, e.x + v.x, e.y + v.y, size, size, rand.random(45) + Interp.pow2In.apply(rand.random(10f, 20f) * e.fout()));
                }
            }).layer(Layer.debris),
            parzilDebrisLarge = new Effect(120, e -> {
                rand.setSeed(e.id);
                color( Color.valueOf("ffffff"), Color.valueOf("ffffff").a(e.fin()), e.fin());

                for (int i = 0; i < 40; i++) {
                    float rot = e.rotation + rand.range(360f);
                    int regionId = rand.random(1, 3);
                    TextureRegion region = Core.atlas.find("aquarion-parzil-debris" + regionId);
                    v.trns(rot, rand.random(e.finpow() * 30f));
                    float fout = Math.max(e.fout(), 0.1f);
                    float size = fout * 28f + 0.2f;
                    Draw.rect(region, e.x + v.x, e.y + v.y, size, size, rand.random(45) + Interp.pow2In.apply(rand.random(10f, 20f) * e.fout()));
                }
            }).layer(Layer.debris),
            PermafrostExplosion = new Effect(30, 500f, b -> {
                float intensity = 6f;
                float baseLifetime = 25f + intensity * 15f;
                b.lifetime = 65f + intensity * 64f;

                color(Pal.techBlue);
                alpha(0.8f);
                for(int i = 0; i < 5; i++){
                    rand.setSeed(b.id*4 + i);
                    float lenScl = rand.random(0.35f, 1.5f);
                    int fi = i;
                    b.scaled(b.lifetime * lenScl, e -> {
                        randLenVectors(e.id + fi - 1, e.fin(Interp.pow10Out), (int)(3f * intensity), 40f * intensity, (x, y, in, out) -> {
                            float fout = e.fout(Interp.pow10Out) * rand.random(0.5f, 1f);
                            float rad = fout * ((1.5f + intensity) * 2.35f);

                            Fill.circle(e.x + x, e.y + y, rad);
                            Drawf.light(e.x + x, e.y + y, rad * 3.6f, Color.valueOf("8ca9e8"), 0.7f);
                        });
                    });
                }

                b.scaled(baseLifetime, e -> {
                    Draw.color();
                    e.scaled(5 + intensity * 2f, i -> {
                        stroke((3.1f + intensity/5f) * i.fout());
                        Lines.circle(e.x, e.y, (3f + i.fin() * 14f) * intensity);
                        Drawf.light(e.x, e.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * e.fout());
                    });

                    color(Color.white, Pal.lighterOrange, e.fin());
                    stroke((2f * e.fout()));

                    Draw.z(Layer.effect + 0.001f);
                    randLenVectors(e.id + 1, e.finpow() + 0.001f, (int)(8 * intensity), 30f * intensity, (x, y, in, out) -> {
                        lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * 4 * (4f + intensity));
                        Drawf.light(e.x + x, e.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
                    });
                });
            }),
            casing1 = new Effect(60f, e -> {
                color(Pal.lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
                alpha(e.fout(0.5f));
                float rot = Math.abs(e.rotation) + 90f;
                int i = -Mathf.sign(e.rotation);

                float len = (2f + e.finpow() * 6f) * i;
                float lr = rot + e.fin() * 30f * i;
                Fill.rect(
                        e.x + trnsx(lr, len) + Mathf.randomSeedRange(e.id + i + 7, 3f * e.fin()),
                        e.y + trnsy(lr, len) + Mathf.randomSeedRange(e.id + i + 8, 3f * e.fin()),
                        4f, 6f, rot + e.fin() * 50f * i
                );

            }).layer(Layer.bullet),
            liquidSpill = new Effect(20f, e -> {
                color(e.color);
                randLenVectors(e.id, 4, 1.5f + e.finpow() * 5f, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, 0.8f + e.fin() * 5f);
                });
            }),
            azuriteSmelt = new Effect(45, e -> {
                color(Color.valueOf("9eb8f5"), Color.lightGray, e.fin());
                randLenVectors(e.id, 4, e.fin() * 5f, (x, y) ->
                    Fill.square(e.x + x, e.y + y, e.fout() + 0.5f, 0)
                );
            });
    private static final Vec2 rv = new Vec2();
    public static void randMinLenVectors(long seed, int amount, float minLength, float maxLength, float angle, float range, float spread, Floatc2 cons) {
        rand.setSeed(seed);

        for(int i = 0; i < amount; ++i){
            float len = minLength + rand.random(maxLength - minLength); // ensures len >= minLength
            rv.trns(angle + rand.range(range), len);
            cons.get(rv.x + rand.range(spread), rv.y + rand.range(spread));
        }
    }
}
