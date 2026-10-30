package aquarion.world.entities.bullet;

import aquarion.world.graphics.CircleDistort;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.IntFloatMap;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;

import static mindustry.Vars.*;

/** Escaped reactor core. Mass is measured as area so consuming blocks grows it gradually. */
public class LooseSingularityBulletType extends BulletType{
    public float initialRadius = 24f, maxRadius = 100f;
    public float shrinkPerTick = 0.7f, buildingGrowth = 10f;
    public float pullRange = 7f, pullForce = 0.8f, throwForce = 24f;
    public float unitDamage = 120f;

    public LooseSingularityBulletType(){
        LooseSingularitySaves.register();
        speed = 0.8f;
        lifetime = 60f * 120f;
        collides = collidesTiles = hittable = absorbable = reflectable = false;
        keepVelocity = false;
        hitEffect = despawnEffect = Fx.none;
        drawSize = 1600f;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.fdata = initialRadius * initialRadius;
        b.data = new CoreState(b.id);
    }

    public float radius(Bullet b){
        return Mathf.sqrt(Math.max(0f, b.fdata));
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        b.fdata = Math.max(0f, b.fdata - shrinkPerTick * Time.delta);
        if(b.fdata <= 1f){
            b.remove();
            return;
        }
        // Smooth, seeded wandering rather than a new random heading every frame.
        int seed = ((CoreState)b.data).seed;
        float heading = Mathf.randomSeed(seed, 360f)
            + Mathf.sin(b.time + seed * 17f, 95f, 150f)
            + Mathf.sin(b.time, 31f, 45f);
        b.vel.trns(heading, speed);
        if(b.x < 0f || b.x > world.unitWidth()) b.vel.x = b.x < 0f ? speed : -speed;
        if(b.y < 0f || b.y > world.unitHeight()) b.vel.y = b.y < 0f ? speed : -speed;
        if(net.client()) return;

        float radius = radius(b), range = radius * pullRange;
        IntFloatMap released = (IntFloatMap)b.data;
        // Expired release immunity is discarded so long-lived cores don't retain every unit ID.
        var entries = released.entries();
        while(entries.hasNext){
            if(entries.next().value <= b.time) entries.remove();
        }
        Units.nearby(null, b.x, b.y, range, unit -> {
            if(unit.dead || !unit.hittable() || released.containsKey(unit.id)) return;
            float distance = unit.dst(b);
            if(distance <= radius + unit.hitSize / 2f){
                unit.damage(unitDamage);
                float angle = distance < 0.1f ? Mathf.randomSeed(unit.id, 360f) : b.angleTo(unit);
                unit.impulseNet(Tmp.v1.trns(angle, throwForce * unit.mass()));
                released.put(unit.id, b.time + 120f);
            }else{
                float force = pullForce * (0.15f + 1f - distance / range) * Time.delta;
                unit.impulseNet(Tmp.v1.set(b.x - unit.x, b.y - unit.y).setLength(force * unit.mass()));
                unit.damageContinuous(unitDamage / 600f);
            }
        });
        if(b.timer(1, 6f)){
            Units.nearbyBuildings(b.x, b.y, radius + 32f, build -> {
                if(!build.isValid() || !build.within(b, radius + build.block.size * tilesize / 2f)) return;
                float area = build.block.size * build.block.size;
                Fx.breakBlock.at(build.x, build.y, build.block.size, build.block.mapColor);
                build.kill();
                b.fdata = Math.min(maxRadius * maxRadius, b.fdata + area * buildingGrowth);
            });
        }
    }

    @Override
    public void draw(Bullet b){
        float radius = radius(b);
        Draw.z(Layer.effect);
        Draw.color(Color.white);
        Fill.circle(b.x, b.y, radius);
        for(int i = 0; i < 36; i++){
            float progress = (b.time / 100f + Mathf.randomSeed(i + b.id, 1f)) % 1f;
            float angle = i * 137.5f + b.time * 1.8f + progress * 160f;
            float distance = radius * (1f + (1f - progress) * 2.5f);
            Fill.circle(b.x + Angles.trnsx(angle, distance), b.y + Angles.trnsy(angle, distance),
                2.5f * Mathf.sin(progress * Mathf.pi));
        }
        Draw.reset();
        CircleDistort.drawSmoke(b.x, b.y, radius * 2.5f, b.time, 1.2f);
        CircleDistort.draw(b.x, b.y, radius, 2f);
    }

    public static class CoreState extends IntFloatMap{
        public int seed;

        public CoreState(int seed){
            this.seed = seed;
        }
    }
}
