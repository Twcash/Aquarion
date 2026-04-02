package aquarion.world.entities.bullet;

import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class TentacleBulletType extends BulletType {

    public int segmentCount = 14;
    public float segmentLength = 6f;

    public float stiffness = 0.25f;
    public float damping = 0.82f;

    public float width = 3f;
    public float range = 120f;
    public float homingPower = 5f;
    public float baseOffset = 0f;

    public TextureRegion backRegion;
    public TextureRegion frontRegion;

    public TentacleBulletType(){
        speed = 0f;
        lifetime = 60f;

        despawnEffect = Fx.none;
        hitEffect = Fx.none;
        smokeEffect = Fx.none;

        keepVelocity = true;
        collides = false;
        pierce = true;
    }

    public static class TentacleData {
        public Vec2[] points;
        public Vec2[] velocity;
        public float extend = 0f;
    }

    @Override
    public void init(Bullet b){
        TentacleData data = new TentacleData();

        data.points = new Vec2[segmentCount];
        data.velocity = new Vec2[segmentCount];

        Vec2 forward = Tmp.v1.trns(b.rotation(), 2f);

        for(int i = 0; i < segmentCount; i++){
            data.points[i] = new Vec2(b.x, b.y);
            data.velocity[i] = new Vec2(forward).scl(i * 0.3f); // initial outward push
        }

        b.data = data;
    }

    @Override
    public void update(Bullet b){
        if(!(b.data instanceof TentacleData data)) return;

        Unit target = Units.closestEnemy(b.team, b.x, b.y, range, u -> !u.dead());

        if(target != null){
            float targetAngle = b.angleTo(target);

            b.rotation(Angles.moveToward(
                    b.rotation(),
                    targetAngle,
                    homingPower * Time.delta * 60f
            ));
        }

        // === EXTEND / RETRACT CURVE ===
        float life = b.fin();

        float extendCurve = life < 0.3f
                ? Interp.pow3Out.apply(life / 0.3f) // fast extend
                : Interp.pow2In.apply(1f - (life - 0.3f) / 0.7f); // slow retract

        data.extend = extendCurve;

        float targetLength = segmentLength * data.extend;

        float bx = b.x + Angles.trnsx(b.rotation(), baseOffset);
        float by = b.y + Angles.trnsy(b.rotation(), baseOffset);

        data.points[0].set(bx, by);
        data.velocity[0].setZero();

        Vec2 forwardDir = Tmp.v2.trns(b.rotation(), 1f);
        for(int i = 1; i < segmentCount; i++){
            Vec2 prev = data.points[i - 1];
            Vec2 cur = data.points[i];

            Vec2 dir = Tmp.v1.set(prev).sub(cur);
            float dist = dir.len();

            if(dist > 0.001f){
                float force = (dist - targetLength) * stiffness;

                dir.nor().scl(force);

                data.velocity[i].add(dir);

                // forward push (makes it feel alive)
                data.velocity[i].add(forwardDir.x * 0.1f * data.extend, forwardDir.y * 0.1f * data.extend);

                data.velocity[i].scl(damping);

                // clamp extreme stretching
                if(dist > segmentLength * 2f){
                    dir.setLength(segmentLength * 2f);
                    cur.set(prev).sub(dir);
                }

                cur.add(data.velocity[i]);
            }

            // subtle wobble
            cur.add(
                    Mathf.sin(Time.time + i) * 0.15f,
                    Mathf.cos(Time.time + i * 1.3f) * 0.15f
            );
        }

        // === DAMAGE ALONG BODY ===
        for(int i = 0; i < segmentCount; i++){
            Vec2 p = data.points[i];

            Units.nearbyEnemies(b.team, p.x, p.y, width * 2f, u -> {
                if(!u.dead()){
                    u.damage(damage * Time.delta);
                }
            });
        }
    }

    @Override
    public void draw(Bullet b){
        if(!(b.data instanceof TentacleData data)) return;

        Draw.color(Color.white);

        for(int i = 0; i < segmentCount - 1; i++){
            Vec2 a = data.points[i];
            Vec2 c = data.points[i + 1];

            float w = width * (1f - i / (float)segmentCount);
            Lines.stroke(w);

            Lines.line(a.x, a.y, c.x, c.y);
        }

        Draw.reset();
    }
}