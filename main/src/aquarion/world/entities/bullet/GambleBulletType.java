package aquarion.world.entities.bullet;
import arc.math.Mathf;
import mindustry.entities.bullet.BasicBulletType;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.game.*;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Teamc;

import java.util.Arrays;

//LETS GO GAMBLING
public class GambleBulletType extends BasicBulletType {
    public BulletType[] bullets = {};
    public float[] bias = {};

    public GambleBulletType(BulletType... bullets) {
        this.bullets = bullets;
        this.bias = new float[bullets.length];
        Arrays.fill(this.bias, 1f);
    }

    public GambleBulletType(float[] bias, BulletType... bullets) {
        if (bias.length != bullets.length) throw new IllegalArgumentException("Bias length must match bullets length.");
        this.bullets = bullets;
        this.bias = bias;
    }


    public GambleBulletType() {
    }

    @Override
    public float estimateDPS() {
        float sum = 0f;
        for (var b : bullets) {
            sum += b.estimateDPS();
        }
        return sum / Math.max(bullets.length, 1);
    }

    @Override
    protected float calculateRange() {
        float max = 0f;
        for (BulletType b : bullets) {
            try {
                java.lang.reflect.Method method = BulletType.class.getDeclaredMethod("calculateRange");
                method.setAccessible(true);
                float range = (float) method.invoke(b);
                max = Math.max(max, range);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return max;
    }

    private BulletType pickBullet() {
        if (bullets.length == 0) return this; // fallback

        float total = 0f;
        for (float f : bias) total += f;
        float rand = Mathf.random(total);

        float cumulative = 0f;
        for (int i = 0; i < bullets.length; i++) {
            cumulative += bias[i];
            if (rand <= cumulative) {
                return bullets[i];
            }
        }
        return bullets[bullets.length - 1];//Should not ever happen.
    }

    @Override
    public @Nullable Bullet create(
            @Nullable Entityc owner, @Nullable Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl,
            float lifetimeScl, Object data, @Nullable Mover mover, float aimX, float aimY, @Nullable Teamc target
    ) {
        angle += angleOffset;
        Bullet last = null;

        BulletType chosen = pickBullet();
        speed = chosen.speed;//Surely there will be no repercussions for this.

        last = chosen.create(owner, shooter, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target);

        return last;
    }
}