package aquarion.world.entities.bullet;

import aquarion.ui.ModSettings;
import aquarion.world.blocks.distribution.ItemHopper;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Fires;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;

public class DumpItemBulletType extends BulletType {
    public float spin = 0.5f;
    public float elevation  = 8;
    public float layer = Layer.flyingUnitLow -0.1f;
    public DumpItemBulletType(){
        super(6.5f, 0);
        drag = 0.1f;
        collides = false;
        lifetime = 30f;
        despawnEffect = Fx.none;
        hitSize = 0.5f;
        hitEffect = Fx.none;
        smokeEffect = Fx.none;
        shootEffect = Fx.none;
        drag = 0.01f;
        hittable = false;
    }

    @Override
    public void draw(Bullet b){
        super.draw(b);
        Draw.z(layer);
        if(!(b.data instanceof ItemStack item)) return;
        Draw.rect(item.item.fullIcon, b.x, b.y, b.rotation() + spin + b.lifetime);
        Draw.color(Pal.shadow);
        Draw.z(Layer.debris);
        float elevation = b.lifetime/b.type.lifetime;
        Draw.rect(item.item.fullIcon, b.x -(elevation *b.fslope()), b.y - (elevation *b.fslope()), b.rotation() + spin + b.lifetime);
        if(ModSettings.getDebugHitboxRendering()) {
            Draw.z(Layer.overlayUI);
            Draw.color(Color.red);
            b.hitbox(Tmp.r1);
            Lines.rect(Tmp.r1);
        }
        Draw.reset();
    }
    public void despawned(Bullet b) {
        if(b.tileOn().build instanceof ItemHopper.HopperBuild) return;
        super.despawned(b);
        if(b.tileOn().floor().isLiquid){
            Fx.ripple.at(b.x, b.y, 1f, b.tileOn().floor().liquidDrop.color);
        } else {
            if (b.data instanceof ItemStack item) {
                if (item.item.explosiveness > 0)
                    Damage.damage(Team.derelict, b.x, b.y, item.item.explosiveness * 30f, item.item.explosiveness * 80f, false, true);
                Fx.dynamicExplosion.at(b, item.item.explosiveness * 2);
                if (b.tileOn() != null && item.item.flammability > 0.5f) Fires.create(b.tileOn());
            }
        }
    }
}
