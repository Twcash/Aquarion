package aquarion.world.entities.bullet;

import aquarion.world.graphics.Renderer;
import arc.Core;
import arc.graphics.Color;
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
import mindustry.world.blocks.defense.turrets.BaseTurret;

public class TentacleBulletType extends BulletType {

    public int segmentCount = 14;
    public float segmentLength = 6f;

    public float width = 3f;
    /** Minimum ticks between contact damage checks; each target is hit once per check. */
    public float damageInterval = 6f;
    public float whip = 10;
    public float baseOffset = 0f;

    public TextureRegion backRegion;
    public TextureRegion frontRegion;
    public String backSprite;
    public String sprite;

    public TentacleBulletType(){
        speed = 0f;
        lifetime = 60f;
        range = 120f;
        homingPower = 5f;

        despawnEffect = Fx.none;
        hitEffect = Fx.none;
        smokeEffect = Fx.none;

        keepVelocity = false;
        collides = false;
        pierce = true;
        pierceBuilding = true;
        hittable = false;
        removeAfterPierce = false;
    }

    public static class TentacleData {
        public Vec2[] points;
        public Vec2[] velocity;
        public float extend = 0f;
        public float anchorX, anchorY;
        public float originX, originY, ownerRotation, direction;
    }
    @Override
    protected float calculateRange(){
        float requested = rangeOverride > 0f ? rangeOverride : range;
        return Math.max(0f, Math.min(requested, segmentLength * (segmentCount - 1)));
    }

    @Override
    public void init(){
        if(segmentCount < 2 || segmentLength <= 0f || width < 0f || damageInterval <= 0f){
            throw new IllegalArgumentException("Tentacles require at least two segments, positive length/damage interval, and nonnegative width.");
        }
        super.init();
        drawSize = Math.max(drawSize, (range + width) * 2f);
    }

    private float ownerRotation(Bullet b){
        if(b.owner instanceof Unit owner) return owner.rotation;
        if(b.owner instanceof BaseTurret.BaseTurretBuild turret) return turret.rotation;
        return 0f;
    }
    @Override
    public void load(){
        super.load();
        backRegion = Core.atlas.find(backSprite == null ? (sprite + "-back") : backSprite);
        frontRegion = Core.atlas.find(sprite);
    }
    @Override
    public void init(Bullet b){
        super.init(b);
        TentacleData data = new TentacleData();

        data.points = new Vec2[segmentCount];
        data.velocity = new Vec2[segmentCount];

        float ox = b.x;
        float oy = b.y;
        data.originX = ox;
        data.originY = oy;
        data.direction = b.rotation();
        data.ownerRotation = ownerRotation(b);

        if(b.owner instanceof Unit owner){
            data.anchorX = ox - owner.x;
            data.anchorY = oy - owner.y;
        }else if(b.owner instanceof BaseTurret.BaseTurretBuild turret){
            data.anchorX = ox - turret.x;
            data.anchorY = oy - turret.y;
        }else{
            data.anchorX = 0f;
            data.anchorY = 0f;
        }

        //The stored muzzle offset must rotate relative to the owner's firing pose.
        Vec2 offset = new Vec2(data.anchorX, data.anchorY).rotate(-data.ownerRotation);
        data.anchorX = offset.x;
        data.anchorY = offset.y;

        for(int i = 0; i < segmentCount; i++){
            data.points[i] = new Vec2(ox, oy);
            data.velocity[i] = new Vec2();
        }

        b.data = data;
        b.vel.setZero();
    }
    @Override
    public void update(Bullet b){
        if(!(b.data instanceof TentacleData data)) return;

        float ox = data.originX;
        float oy = data.originY;
        float rotation = ownerRotation(b);
        data.direction += Mathf.mod(rotation - data.ownerRotation + 180f, 360f) - 180f;
        data.ownerRotation = rotation;

        if(b.owner instanceof Unit owner){
            float cos = Mathf.cosDeg(owner.rotation());
            float sin = Mathf.sinDeg(owner.rotation());

            ox = owner.x + data.anchorX * cos - data.anchorY * sin;
            oy = owner.y + data.anchorX * sin + data.anchorY * cos;

        }
        else if(b.owner instanceof BaseTurret.BaseTurretBuild turret){
            float cos = Mathf.cosDeg(turret.rotation);
            float sin = Mathf.sinDeg(turret.rotation);

            ox = turret.x + data.anchorX * cos - data.anchorY * sin;
            oy = turret.y + data.anchorX * sin + data.anchorY * cos;

        }

        float bx = ox + Angles.trnsx(data.direction, baseOffset);
        float by = oy + Angles.trnsy(data.direction, baseOffset);

        b.set(bx, by);

        Unit unitTarget = homingPower > 0f ? Units.closestEnemy(b.team, bx, by, range,
            u -> !u.dead() && u.hittable() && u.checkTarget(collidesAir, collidesGround)) : null;
        Building tileTarget = homingPower > 0f && collidesGround && collidesTiles ?
            Units.findEnemyTile(b.team, bx, by, range, bld -> !bld.dead() && bld.collide(b)) : null;

        Position target = null;

        if(unitTarget != null && tileTarget != null){
            float du = Mathf.dst2(bx, by, unitTarget.x, unitTarget.y);
            float db = Mathf.dst2(bx, by, tileTarget.x, tileTarget.y);
            target = du < db ? unitTarget : tileTarget;
        }else if(unitTarget != null){
            target = unitTarget;
        }else if(tileTarget != null){
            target = tileTarget;
        }

        if(target != null){
            float targetAngle = Angles.angle(bx, by, target.getX(), target.getY());
            data.direction = Angles.moveToward(
                    data.direction,
                    targetAngle,
                    homingPower * Time.delta
            );
        }
        b.rotation(data.direction);

        float life = b.fin();

        float extendCurve;
        if(life < 0.2f){
            extendCurve = Interp.pow3Out.apply(life / 0.2f);
        }else{
            extendCurve = 1f;
        }

        if(life > 0.75f){
            float t = (life - 0.75f) / 0.25f;
            extendCurve = 1f - Interp.pow2In.apply(t);
        }

        data.extend = extendCurve;

        data.points[0].set(bx, by);

        float linkLength = range / (segmentCount - 1) * data.extend;
        int steps = Math.max(1, Mathf.ceil(Time.delta));
        float delta = Time.delta / steps;
        for(int step = 0; step < steps; step++){
            for(int i = 1; i < segmentCount; i++){
                Vec2 prev = data.points[i - 1], cur = data.points[i], velocity = data.velocity[i];
                float fraction = i / (float)(segmentCount - 1);
                float curl = Mathf.sin(b.time / 6f - fraction * 3f + b.id * 0.37f) * whip * fraction;
                Vec2 desired = Tmp.v1.trns(data.direction + curl, linkLength).add(prev);
                velocity.add((desired.x - cur.x) * 0.3f * delta, (desired.y - cur.y) * 0.3f * delta);
                velocity.scl(Mathf.pow(0.65f, delta));
                cur.add(velocity.x * delta, velocity.y * delta);
                cur.sub(prev).limit(linkLength).add(prev);
                if(linkLength <= 0.001f){
                    cur.set(prev);
                    velocity.setZero();
                }
            }
        }

        if(data.extend > 0.05f && b.timer(1, damageInterval)){
            applyContactDamage(b, data);
        }
    }

    /** Contact follows the rendered links, independent of the tentacle's world position. */
    protected boolean touches(TentacleData data, Rect hitbox){
        for(int i = 0; i < data.points.length - 1; i++){
            Vec2 a = data.points[i], c = data.points[i + 1];
            if(a.epsilonEquals(c, 0.001f)) continue;
            float radius = width * (1f - i / (float)segmentCount);
            if(Intersector.intersectSegmentRectangle(a, c, Tmp.r2.set(hitbox).grow(radius))) return true;
        }
        return false;
    }

    protected void applyContactDamage(Bullet b, TentacleData data){
        //Each candidate is checked once, even if it overlaps many links.
        Units.nearbyEnemies(b.team, b.x - range - width, b.y - range - width,
            (range + width) * 2f, (range + width) * 2f, unit -> {
                if(unit.dead || !unit.hittable() || !unit.checkTarget(collidesAir, collidesGround)) return;
                unit.hitbox(Tmp.r1);
                if(touches(data, Tmp.r1)) hitEntity(b, unit, unit.health);
            });
        if(collidesGround && collidesTiles){
            Units.nearbyBuildings(b.x, b.y, range + width, build -> {
                if(build.team == b.team || build.dead() || !build.collide(b)) return;
                build.hitbox(Tmp.r1);
                if(touches(data, Tmp.r1)){
                    float health = build.health;
                    build.collision(b);
                    hitTile(b, build, build.x, build.y, health, true);
                }
            });
        }
    }
    @Override
    public void draw(Bullet b){
        if(!(b.data instanceof TentacleData data)) return;
        if(layer == -1){
            Draw.z(Renderer.Layer.flyingUnitLow-2f);
        } else {
            Draw.z(layer);
        }
        float fade = 1f - Mathf.clamp((b.fin() - 0.75f) / 0.25f);

        Draw.color(Color.white);
        Draw.alpha(fade);

        for(int i = 0; i < segmentCount - 1; i++){
            Vec2 a = data.points[i];
            Vec2 c = data.points[i + 1];
            float length = a.dst(c);
            if(length <= 0.001f) continue;

            float angle = Angles.angle(a.x, a.y, c.x, c.y);
            float w = width * (1f - i / (float)segmentCount);

            TextureRegion region = (i == segmentCount - 2 && frontRegion != null)
                    ? frontRegion
                    : backRegion;

            if(region != null && region.found()){
                Draw.rect(
                        region,
                        (a.x + c.x) / 2f,
                        (a.y + c.y) / 2f,
                        length,
                        w * 2f,
                        angle
                );
            }else{
                Lines.stroke(w * 2f);
                Lines.line(a.x, a.y, c.x, c.y);
            }
        }
        Draw.reset();
    }
}
