package aquarion.world.entities;

import arc.util.Tmp;
import mindustry.entities.EntityCollisions;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;

public class NewInterceptorBulletType extends BasicBulletType {
    public NewInterceptorBulletType(float speed, float damage) {
        super(speed, damage);
    }

    public NewInterceptorBulletType() {
    }

    public NewInterceptorBulletType(float speed, float damage, String bulletSprite) {
        super(speed, damage, bulletSprite);
    }

    public void update(Bullet b) {
        super.update(b);
        Object var3 = b.data;
        if (var3 instanceof Bullet) {
            Bullet other = (Bullet)var3;
            if (other.isAdded()) {
                if (EntityCollisions.collide(b.x, b.y, b.hitSize, b.hitSize, b.deltaX, b.deltaY, other.x, other.y, other.hitSize, other.hitSize, other.deltaX, other.deltaY, Tmp.v1)) {
                    b.set(Tmp.v1);
                    this.hit(b, b.x, b.y);
                    b.remove();
                    if (other.damage > this.damage) {
                        other.damage -= b.damage;
                    } else {
                        other.remove();
                    }
                }
            } else {
                b.data = null;
            }
        }

    }
}

