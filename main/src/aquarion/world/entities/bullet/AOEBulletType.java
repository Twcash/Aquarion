package aquarion.world.entities.bullet;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;

public class AOEBulletType extends BasicBulletType {
    public float fadeTime = 90f;
    public float radius = 8f;
    public float damageInterval = 10f;
    public boolean timescaleDamage = false;
    public Color color = Items.oxide.color;

    public AOEBulletType(float damage, float lifetime, float radius, Color color){
        this.damage = damage;
        this.radius = radius;
        this.color = color;
        this.lifetime = lifetime;
        this.speed = 0f;
        this.collides = false;
        this.collidesTiles = false;
        this.keepVelocity = false;
        shootEffect = smokeEffect = despawnEffect = hitEffect = Fx.none;
    }

    @Override
    public void draw(Bullet b){
        Draw.z(Layer.effect);
        Draw.color(color);
        float fout = Interp.pow2In.apply(Mathf.clamp(
                b.time > b.lifetime - fadeTime
                        ? 1f - (b.time - (b.lifetime - fadeTime)) / fadeTime
                        : 1f
        ));
        Draw.alpha(fout - 0.1f);
        Fill.circle(b.x, b.y, fout * radius);
        Draw.reset();
    }

    @Override
    public void update(Bullet b){
        if(b.timer(2, damageInterval)){
            applyDamage(b);
        }
        super.update(b);
    }

    public void applyDamage(Bullet b){
        float fout = Mathf.clamp(
                b.time > b.lifetime - fadeTime
                        ? 1f - (b.time - (b.lifetime - fadeTime)) / fadeTime
                        : 1f
        );

        float baseDamage = b.damage;
        if(timescaleDamage && b.owner instanceof Building build){
            baseDamage *= build.timeScale();
        }

        Damage.damage(b.team, b.x, b.y, radius * fout, baseDamage);
        if(status != null)Damage.status(b.team, b.x, b.y, radius * fout, status, statusDuration,true,true);

    }
}