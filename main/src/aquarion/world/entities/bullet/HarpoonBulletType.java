package aquarion.world.entities.bullet;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

public class HarpoonBulletType extends BulletType{
    public float initialPullSpeed = 0.35f, pullSpeed = 9f;
    public float pullRampTime = 60f;
    public float beamWidth = 0.28f, stopDistance = 12f;
    public Color beamColor = Color.valueOf("a9d8ff");
    private TextureRegion laser, start, end;

    private static class Tether{
        Healthc target;
        float offsetX, offsetY;
    }

    public HarpoonBulletType(){
        speed = 0f;
        damage = 0f;
        lifetime = 90f;
        rangeOverride = 144f;
        collides = collidesTiles = false;
        hittable = absorbable = reflectable = false;
        keepVelocity = false;
        shootEffect = smokeEffect = hitEffect = despawnEffect = Fx.none;
    }

    @Override
    public void load(){
        super.load();
        laser = Core.atlas.find("parallax-laser");
        end = Core.atlas.find("parallax-laser-end");
        start = Core.atlas.find("parallax-laser-start", end);
    }

    public float pullSpeedAt(float attachmentTime){
        float progress = pullRampTime <= 0f ? 1f : Mathf.clamp(attachmentTime / pullRampTime);
        return Mathf.lerp(initialPullSpeed, pullSpeed, progress * progress);
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        if(!(b.owner instanceof Unit owner)) return;
        Tether tether = new Tether();
        tether.target = Damage.linecast(b, b.x, b.y, b.rotation(), rangeOverride);
        Vec2 offset = new Vec2(b.x - owner.x, b.y - owner.y).rotate(-owner.rotation);
        tether.offsetX = offset.x;
        tether.offsetY = offset.y;
        b.data = tether;
        if(tether.target == null) b.remove();
    }

    @Override
    public void update(Bullet b){
        if(!(b.owner instanceof Unit owner) || owner.dead || !owner.isAdded() ||
            !(b.data instanceof Tether tether) || tether.target == null ||
            tether.target.dead() ||
            (tether.target instanceof Building building ? !building.isValid() : !tether.target.isAdded()) ||
            (tether.target instanceof Teamc teamTarget && teamTarget.team() == owner.team)){
            b.remove();
            return;
        }
        float mx = owner.x + Angles.trnsx(owner.rotation, tether.offsetX, tether.offsetY);
        float my = owner.y + Angles.trnsy(owner.rotation, tether.offsetX, tether.offsetY);
        b.set(mx, my);
        Healthc target = tether.target;
        b.rotation(b.angleTo(target));
        owner.impulseNet(Tmp.v1.set(target).sub(owner).limit((pullSpeedAt(b.time) + (1f - owner.dst(target) / range))));
        if(owner.dst(target) > rangeOverride + owner.hitSize ||
            Damage.linecast(b, mx, my, b.rotation(), b.dst(target) + 1f) != target){
            b.remove();
        }
    }

    @Override
    public void draw(Bullet b){
        if(!(b.data instanceof Tether tether) || tether.target == null) return;
        float z = Draw.z();
        Draw.z(Layer.bullet);
        Draw.mixcol(beamColor, 0.6f + Mathf.absin(4f, 0.3f));
        Drawf.laser(laser, start, end, b.x, b.y, tether.target.x(), tether.target.y(),
            beamWidth * Math.min(1f, b.time / 4f) * Math.min(1f, b.fout() * 8f));
        Draw.reset();
        Draw.z(z);
    }
}
