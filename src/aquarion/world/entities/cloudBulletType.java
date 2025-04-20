package aquarion.world.entities;

import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
//Leaves a AOE cloud similar to oxide titan, except this is much simpler.
public class cloudBulletType extends BasicBulletType {
    public float range = 40f;
    public float lifetime = 120f; //How long this cloud stays.
    public float speed = 0;
    public Effect cloudEffect = Fx.smoke;
    public float updateEffectChance = 0.03f;
    public boolean damageInAOE = false;
    public float damage = 5f;
    public float damageInterval = 15f;

    public cloudBulletType(float speed, float damage) {
        super(speed, damage);
    }

    @Override
    public void despawned(Bullet b) {
        super.despawned(b);
        createCloud(b);
    }
    //no clue if this actually works or not
    private void createCloud(Bullet b) {
        float duration = lifetime;
        float interval = damageInterval;

        for (float t = 0f; t < duration; t += interval) {
            if (Mathf.chanceDelta(updateEffectChance)) {
                cloudEffect.at(b.x + Mathf.range(1 * range), b.y + Mathf.range(1 * range));
            }

            if (damageInAOE) {
                Damage.damage(b.team, b.x, b.y, range, damage, true, collidesAir, collidesGround);
            }
        };
    }
}