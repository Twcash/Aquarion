package aquarion.world.entities.bullet;

import aquarion.world.graphics.Renderer;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.type.Item;

public class DumpItemBulletType extends BulletType {
    public float spin = 0.5f;
    public DumpItemBulletType(){
        super(6.5f, 0);
        drag = 0.1f;
        collides = false;
        lifetime = 30f;
        despawnEffect = Fx.none;
        hitEffect = Fx.none;
        smokeEffect = Fx.none;
        shootEffect = Fx.none;
        drag = 0.01f;
        hittable = false;
    }

    @Override
    public void draw(Bullet b){
        super.draw(b);
        Draw.z(Renderer.Layer.blockUnder-0.1f);
        if(!(b.data instanceof Item item)) return;
        Draw.scl(0.9f*Interp.pow3Out.apply(b.fslope()));
        Draw.alpha(1*Interp.pow10Out.apply(b.fslope()));
        Draw.rect(item.fullIcon, b.x, b.y, b.rotation() + spin + b.lifetime);
        Draw.reset();
    }
    public void despawned(Bullet b) {
        super.despawned(b);
        if(b.tileOn().floor().isLiquid){
            Fx.ripple.at(b.x, b.y, 1f, b.tileOn().floor().liquidDrop.color);
        } else {
            if (b.data instanceof Item item) {
                if (item.explosiveness > 0)
                    Damage.damage(b.team, b.x, b.y, item.explosiveness * 30f, item.explosiveness * 80f, false, true);
                Fx.dynamicExplosion.at(b, item.explosiveness * 2);
                if (b.tileOn() != null && item.flammability > 0.5f) Fires.create(b.tileOn());
            }
        }
    }
}
