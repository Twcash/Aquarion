package aquarion.world.entities.bullet;

import aquarion.gen.Amphibiousc;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

import static mindustry.Vars.*;

/** A finite tractor tether that pulls its shooter toward the first enemy on its ray. */
public class HarpoonBulletType extends BulletType{
    /** Initial and maximum pull speeds, in world units per tick. */
    public float initialPullSpeed = 0.35f, pullSpeed = 9f;
    /** Ticks of attachment required to reach maximum strength. Each new tether starts fresh. */
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
        //Recheck the ray so a newly built wall or intervening enemy breaks the tether.
        if(owner.dst(target) > rangeOverride + owner.hitSize ||
            Damage.linecast(b, mx, my, b.rotation(), b.dst(target) + 1f) != target){
            b.remove();
            return;
        }
        if(!net.client() && owner instanceof Amphibiousc frog){
            float radius = target instanceof Building build ? build.block.size * tilesize / 2f :
                target instanceof Unit unit ? unit.hitSize / 2f : 0f;
            if(!frog.pullHarpoon(target.x(), target.y(), pullSpeedAt(b.time), Math.max(stopDistance, radius + owner.hitSize / 2f))){
                b.remove();
            }
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
