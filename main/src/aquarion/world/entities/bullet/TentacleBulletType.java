package aquarion.world.entities.bullet;

import aquarion.world.graphics.Renderer;
import arc.Core;
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
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.type.Weapon;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.blocks.defense.turrets.Turret;

public class TentacleBulletType extends BulletType {

    public int segmentCount = 14;
    public float segmentLength = 6f;

    public float width = 3f;
    public float range = 120f;
    public float homingPower = 5f;
    public float baseOffset = 0f;

    public TextureRegion backRegion;
    public TextureRegion frontRegion;
    public String backSprite;
    public String sprite;

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
    public void load(){
        super.load();
        backRegion = Core.atlas.find(backSprite == null ? (sprite + "-back") : backSprite);
        frontRegion = Core.atlas.find(sprite);
    }
    @Override
    public void init(Bullet b){
        TentacleData data = new TentacleData();

        data.points = new Vec2[segmentCount];
        data.velocity = new Vec2[segmentCount];

        float ox = b.x;
        float oy = b.y;

        Vec2 forward = Tmp.v1.trns(b.rotation(), 6f);

        for(int i = 0; i < segmentCount; i++){
            data.points[i] = new Vec2(ox, oy);
            data.velocity[i] = new Vec2(forward).scl(1f + i * 0.2f);
        }

        b.data = data;
    }
    @Override
    public void update(Bullet b){
        if(!(b.data instanceof TentacleData data)) return;

        float ox = b.x;
        float oy = b.y;

        if(b.owner instanceof Unit owner){
            ox = owner.x;
            oy = owner.y;
            b.rotation(owner.rotation());
        }else if(b.owner instanceof BaseTurret.BaseTurretBuild turret){
            ox = turret.x;
            oy = turret.y;
            b.rotation(turret.rotation);
        }

        float bx = ox + Angles.trnsx(b.rotation(), baseOffset);
        float by = oy + Angles.trnsy(b.rotation(), baseOffset);

        b.set(bx, by);

        Unit unitTarget = Units.closestEnemy(b.team, bx, by, range, u -> !u.dead());
        Building tileTarget = Units.findEnemyTile(b.team, bx, by, range, bld -> !bld.dead());

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
            b.rotation(Angles.moveToward(
                    b.rotation(),
                    targetAngle,
                    homingPower * Time.delta * 60f
            ));
        }

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

        float totalLength = segmentLength * (segmentCount - 1) * data.extend;

        Vec2 forward = Tmp.v1.trns(b.rotation(), 1f);

        for(int i = 1; i < segmentCount; i++){
            Vec2 prev = data.points[i - 1];
            Vec2 cur = data.points[i];

            Vec2 dir = Tmp.v2.set(cur).sub(prev);
            float dist = dir.len();

            if(dist > 0.0001f){
                float desired = segmentLength * data.extend;
                dir.scl(1f / dist);
                cur.set(prev).add(dir.scl(desired));
            }

            float attack = Mathf.clamp(data.extend * 2f);
            cur.add(forward.x * attack * 2f, forward.y * attack * 2f);

            float chainDist = cur.dst(data.points[0]);
            if(chainDist > totalLength){
                Vec2 clamp = Tmp.v2.set(cur).sub(data.points[0]).setLength(totalLength);
                cur.set(data.points[0]).add(clamp);
            }

            if(data.extend < 0.05f){
                cur.set(data.points[0]);
            }
        }

        for(int i = 0; i < segmentCount; i++){
            Vec2 p = data.points[i];
            Damage.damage( b.team,damage, p.y, p.x, width * 2f);
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

        Draw.alpha(fade);
        Draw.color(Color.white);

        for(int i = 0; i < segmentCount - 1; i++){
            Vec2 a = data.points[i];
            Vec2 c = data.points[i + 1];

            float angle = Angles.angle(a.x, a.y, c.x, c.y);
            float w = width * (1f - i / (float)segmentCount);

            TextureRegion region = (i == segmentCount - 2 && frontRegion != null)
                    ? frontRegion
                    : backRegion;

            if(region != null){
                Draw.rect(
                        region,
                        (a.x + c.x) / 2f,
                        (a.y + c.y) / 2f,
                        segmentLength,
                        w * 2f,
                        angle
                );
            }else{
                Lines.stroke(w);
                Lines.line(a.x, a.y, c.x, c.y);
            }
        }

        Draw.reset();
    }
}