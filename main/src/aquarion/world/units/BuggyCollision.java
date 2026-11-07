package aquarion.world.units;

import aquarion.gen.Buggyc;
import aquarion.units.type.BuggyUnitType;
import arc.math.Mathf;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.gen.*;

import static mindustry.Vars.*;

/** Tests the swept chassis before movement; physics separation does not emit unit collision callbacks. */
public class BuggyCollision{
    public static void check(Unit car, float dx, float dy){
        if(!(car instanceof Buggyc buggy) || !(car.type instanceof BuggyUnitType cfg) ||
            buggy.collisionCooldownRemaining() > 0f || cfg.collisionDamage <= 0f ||
            dx * car.vel.x + dy * car.vel.y <= 0.001f) return;
        float radius = car.hitSize * unitCollisionRadiusScale;
        Healthc[] target = {null};
        float[] first = {Float.POSITIVE_INFINITY};
        float minX = Math.min(car.x, car.x + dx) - radius, minY = Math.min(car.y, car.y + dy) - radius;
        float maxX = Math.max(car.x, car.x + dx) + radius, maxY = Math.max(car.y, car.y + dy) + radius;
        Units.nearby(null, minX, minY, maxX - minX, maxY - minY, other -> {
            if(other == car || other.dead || !other.hittable() || other.collisionLayer() != car.collisionLayer()) return;
            float extent = radius + other.hitSize * unitCollisionRadiusScale;
            float fraction = circleFraction(car.x - other.x, car.y - other.y, dx, dy, extent);
            if(fraction < first[0]){ first[0] = fraction; target[0] = other; }
        });
        //Tile collisions use a smaller, capped hitbox than unit-to-unit physics.
        float tileRadius = Math.min(car.hitSize * 0.66f, 7.8f) / 2f;
        int left = Mathf.floor((Math.min(car.x, car.x + dx) - tileRadius + tilesize / 2f) / tilesize);
        int right = Mathf.floor((Math.max(car.x, car.x + dx) + tileRadius + tilesize / 2f) / tilesize);
        int bottom = Mathf.floor((Math.min(car.y, car.y + dy) - tileRadius + tilesize / 2f) / tilesize);
        int top = Mathf.floor((Math.max(car.y, car.y + dy) + tileRadius + tilesize / 2f) / tilesize);
        for(int x = left; x <= right; x++) for(int y = bottom; y <= top; y++){
            var tile = world.tile(x, y);
            if(tile == null || !tile.solid()) continue;
            float extent = tilesize / 2f + tileRadius;
            float fraction = hitFraction(car.x, car.y, dx, dy, x * tilesize - extent, y * tilesize - extent,
                x * tilesize + extent, y * tilesize + extent);
            if(fraction < first[0]){ first[0] = fraction; target[0] = tile.build; }
        }
        if(target[0] == null) return;
        Healthc hit = target[0];
        //Allies still obstruct the chassis, but neither side takes crash damage.
        //Keep them in the sweep so they also shield objects behind them.
        if(hit instanceof Teamc ally && ally.team() == car.team) return;
        float nx = hit.x() - car.x, ny = hit.y() - car.y;
        float distance = Mathf.len(nx, ny);
        float vx = car.vel.x, vy = car.vel.y;
        if(hit instanceof Unit other){ vx -= other.vel.x; vy -= other.vel.y; }
        float closing = distance > 0.001f ? (vx * nx + vy * ny) / distance : Mathf.len(vx, vy);
        if(closing < cfg.collisionMinSpeed) return;
        float damage = closing * closing * cfg.collisionDamage;
        buggy.collisionCooldownRemaining(cfg.collisionDamageCooldown);
        //Two buggies must not each charge damage for the same head-on crash.
        if(hit instanceof Buggyc other) other.collisionCooldownRemaining(Math.max(other.collisionCooldownRemaining(), cfg.collisionDamageCooldown));
        hit.damage(damage);
        car.damage(damage * cfg.collisionSelfDamageMultiplier);
        car.vel.scl(0.35f);
        if(!headless) Fx.hitBulletSmall.at(car.x + dx * first[0], car.y + dy * first[0], car.rotation);
    }

    private static float circleFraction(float x, float y, float dx, float dy, float radius){
        float c = x * x + y * y - radius * radius;
        if(c <= 0f) return 0f;
        float a = dx * dx + dy * dy, b = x * dx + y * dy;
        float discriminant = b * b - a * c;
        if(a < 0.0001f || discriminant < 0f) return Float.POSITIVE_INFINITY;
        float fraction = (-b - (float)Math.sqrt(discriminant)) / a;
        return fraction >= 0f && fraction <= 1f ? fraction : Float.POSITIVE_INFINITY;
    }

    private static float hitFraction(float x, float y, float dx, float dy, float left, float bottom, float right, float top){
        float near = 0f, far = 1f;
        if(Math.abs(dx) < 0.0001f){ if(x < left || x > right) return Float.POSITIVE_INFINITY; }
        else{
            float a = (left - x) / dx, b = (right - x) / dx;
            near = Math.max(near, Math.min(a, b)); far = Math.min(far, Math.max(a, b));
        }
        if(Math.abs(dy) < 0.0001f){ if(y < bottom || y > top) return Float.POSITIVE_INFINITY; }
        else{
            float a = (bottom - y) / dy, b = (top - y) / dy;
            near = Math.max(near, Math.min(a, b)); far = Math.min(far, Math.max(a, b));
        }
        return near <= far ? near : Float.POSITIVE_INFINITY;
    }
}
