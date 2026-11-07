package aquarion.world.entities.bullet;

import aquarion.world.graphics.CircleDistort;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.IntFloatMap;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.game.Team;
import arc.struct.IntSet;
import arc.struct.Seq;
import mindustry.type.ItemStack;
import mindustry.graphics.Layer;

import static mindustry.Vars.*;

/** Escaped reactor core. Mass is measured as area so consuming blocks grows it gradually. */
public class LooseSingularityBulletType extends BulletType{
    public float initialRadius = 24f, maxRadius = 100f;
    public float shrinkPerTick = 0.7f, buildingGrowth = 10f;
    public float pullRange = 7f, pullForce = 0.8f, throwForce = 24f;
    public float unitDamage = 120f;
    public float safeFlightTime, blackHoleRadius = 60f;
    public float bulletGrowth = 2f, itemGrowth = 10f;
    public float blackHoleFlingChance = 0.8f;

    private void stretch(Bullet core, TextureRegion region, float x, float y, float size){
        float range = radius(core) * pullRange, distance = core.dst(x, y);
        if(distance <= radius(core) || distance >= range) return;
        float strength = 1f - distance / range;
        float angle = Angles.angle(x, y, core.x, core.y);
        Draw.color(Color.white, strength * 0.55f);
        if(region != null){
            Draw.rect(region, x, y, size * (1f + strength * 4f), size * (1f - strength * 0.75f), angle);
        }
        Lines.stroke(0.5f + strength);
        Lines.lineAngle(x, y, angle, size * strength * 3f);
        Draw.reset();
    }

    public boolean blackHole(Bullet b){
        CoreState state = (CoreState)b.data;
        if(b.fdata >= blackHoleRadius * blackHoleRadius) state.blackHole = true;
        return state.blackHole;
    }

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
        b.team = Team.derelict;
        b.fdata = initialRadius * initialRadius;
        b.data = new CoreState(b.id);
    }

    public float radius(Bullet b){
        return Mathf.sqrt(Math.max(0f, b.fdata));
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        if(b.time < safeFlightTime) return;
        boolean hole = blackHole(b);
        if(hole) b.lifetime = Math.max(b.lifetime, b.time + 120f);
        if(!net.client()){
            // EntityGroup.each shares the group's update cursor. Never nest it in update().
            Seq<Bullet> cores = new Seq<>();
            for(int i = 0; i < Groups.bullet.size(); i++){
                Bullet other = Groups.bullet.index(i);
                cores.add(other);
            }
            for(Bullet other : cores){
                if(other == b || !other.isAdded()) continue;
                if(!(other.type instanceof LooseSingularityBulletType type)){
                    if(((CoreState)b.data).get(other.id, 0f) > b.time) continue;
                    // Yeeted stacks are non-hittable bullets, so consume them explicitly too.
                    if(b.within(other, radius(b) + other.hitSize / 2f)){
                        float mass = other.type instanceof DumpItemBulletType && other.data instanceof ItemStack stack
                            ? itemGrowth * Math.max(0, stack.amount) : bulletGrowth;
                        boolean consumingHole = blackHole(b);
                        if(consumingHole && Mathf.chance(blackHoleFlingChance)){
                            float angle = b.dst(other) < 0.1f ? Mathf.randomSeed(other.id, 360f) : b.angleTo(other);
                            other.vel.trns(angle, Math.max(throwForce, other.vel.len() * 2f));
                            other.set(b.x + Angles.trnsx(angle, radius(b) + other.hitSize + 2f),
                                b.y + Angles.trnsy(angle, radius(b) + other.hitSize + 2f));
                            other.lifetime = Math.max(other.lifetime, other.time + 60f);
                            ((CoreState)b.data).put(other.id, b.time + 120f);
                            continue;
                        }
                        other.absorb();
                        b.fdata = consumingHole ? Math.max(0f, b.fdata - mass)
                            : Math.min(maxRadius * maxRadius, b.fdata + mass);
                        if(b.fdata <= 1f) break;
                    }
                    continue;
                }
                if(other.time < type.safeFlightTime) continue;
                boolean otherHole = type.blackHole(other);
                float distance = b.dst(other), contact = radius(b) + type.radius(other);
                if(blackHole(b) || otherHole){
                    if(distance < contact * 4f){
                        float angle = distance < 0.01f ? (b.id < other.id ? 0f : 180f) : other.angleTo(b);
                        b.set(b.x + Angles.trnsx(angle, 2f * Time.delta), b.y + Angles.trnsy(angle, 2f * Time.delta));
                    }
                }else if(distance <= contact){
                    // Either core can resolve contact, regardless of entity/update order.
                    float mass = b.fdata + other.fdata;
                    b.set((b.x * b.fdata + other.x * other.fdata) / mass,
                        (b.y * b.fdata + other.y * other.fdata) / mass);
                    b.vel.scl(b.fdata / mass).mulAdd(other.vel, other.fdata / mass);
                    b.fdata = mass;
                    b.lifetime = Math.max(b.lifetime, b.time + other.lifetime - other.time);
                    other.absorb();
                    blackHole(b);
                }else if(distance < contact * 3f){
                    // Their visible outer fields overlap before the solid centers do.
                    // Pull the centers together instead of letting wandering carry them apart.
                    float step = Math.min(distance - contact, pullForce * Time.delta);
                    b.set(b.x + (other.x - b.x) / distance * step,
                        b.y + (other.y - b.y) / distance * step);
                }
            }
            hole = blackHole(b);
        }
        b.fdata = Math.max(0f, b.fdata - (hole ? 0f : shrinkPerTick * Time.delta));
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
        // Visit all teams directly without depending on the global unit spatial index.
        for(int i = 0; i < Groups.unit.size(); i++){
            var unit = Groups.unit.index(i);
            if(unit.dead || !unit.isAdded() || !unit.within(b, range)) continue;
            float distance = unit.dst(b);
            // Environmental damage bypasses armor, which otherwise swallowed the small tick damage.
            unit.damagePierce(unitDamage / 600f * Time.delta);
            if(distance <= radius + unit.hitSize / 2f){
                if(hole && !released.containsKey(unit.id)){
                    if(Mathf.chance(blackHoleFlingChance)){
                        unit.damagePierce(unitDamage);
                        float angle = distance < 0.1f ? Mathf.randomSeed(unit.id, 360f) : b.angleTo(unit);
                        unit.impulseNet(Tmp.v1.trns(angle, throwForce * unit.mass()));
                        released.put(unit.id, b.time + 120f);
                    }else{
                        float mass = Math.max(1f, unit.hitSize * unit.hitSize);
                        unit.kill();
                        b.fdata = Math.max(0f, b.fdata - mass);
                    }
                }else if(!released.containsKey(unit.id)){
                    unit.damagePierce(unitDamage);
                    float angle = distance < 0.1f ? Mathf.randomSeed(unit.id, 360f) : b.angleTo(unit);
                    unit.impulseNet(Tmp.v1.trns(angle, throwForce * unit.mass()));
                    released.put(unit.id, b.time + 120f);
                }
            }
            if(!released.containsKey(unit.id)){
                float force = pullForce * (hole ? 3f : 1f) * (0.15f + 1f - distance / range) * Time.delta;
                unit.impulseNet(Tmp.v1.set(b.x - unit.x, b.y - unit.y).setLength(force * unit.mass()));
            }
        }
        if(b.timer(1, 6f)){
            // Scan occupied tiles, including large buildings whose centers lie outside the core.
            IntSet visited = new IntSet();
            int reach = (int)Math.ceil(radius / tilesize) + 1;
            int cx = (int)(b.x / tilesize), cy = (int)(b.y / tilesize);
            for(int tx = cx - reach; tx <= cx + reach; tx++){
                for(int ty = cy - reach; ty <= cy + reach; ty++){
                var tile = world.tile(tx, ty);
                if(tile == null || tile.build == null) continue;
                var build = tile.build;
                if(!build.isValid() || !visited.add(build.id)) continue;
                float half = build.block.size * tilesize / 2f;
                float dx = Math.max(0f, Math.abs(b.x - build.x) - half);
                float dy = Math.max(0f, Math.abs(b.y - build.y) - half);
                if(dx * dx + dy * dy > radius * radius) continue;
                float area = build.block.size * build.block.size;
                Fx.breakBlock.at(build.x, build.y, build.block.size, build.block.mapColor);
                build.kill();
                b.fdata = blackHole(b) ? Math.max(0f, b.fdata - area * buildingGrowth)
                    : Math.min(maxRadius * maxRadius, b.fdata + area * buildingGrowth);
                }
            }
        }
    }

    @Override
    public void draw(Bullet b){
        float radius = radius(b);
        if(b.time >= safeFlightTime){
            Draw.z(Layer.effect - 0.01f);
            for(int i = 0; i < Groups.unit.size(); i++){
                var unit = Groups.unit.index(i);
                stretch(b, unit.type.fullIcon, unit.x, unit.y, unit.hitSize);
            }
            for(int i = 0; i < Groups.bullet.size(); i++){
                var other = Groups.bullet.index(i);
                if(other == b || other.type instanceof LooseSingularityBulletType) continue;
                TextureRegion icon = other.data instanceof ItemStack stack ? stack.item.fullIcon : null;
                stretch(b, icon, other.x, other.y, icon == null ? Math.max(3f, other.hitSize) : 8f);
            }
            float range = radius * pullRange;
            IntSet seen = new IntSet();
            int reach = (int)Math.ceil(range / tilesize);
            int cx = (int)(b.x / tilesize), cy = (int)(b.y / tilesize);
            for(int tx = Math.max(0, cx - reach); tx <= Math.min(world.width() - 1, cx + reach); tx++){
                for(int ty = Math.max(0, cy - reach); ty <= Math.min(world.height() - 1, cy + reach); ty++){
                    var build = world.build(tx, ty);
                    if(build != null && seen.add(build.id))
                        stretch(b, build.block.fullIcon, build.x, build.y, build.block.size * tilesize);
                }
            }
        }
        if(blackHole(b)){
            // Render after distortion and lighting so the event horizon stays black.
            Draw.draw(Layer.light + 2f, () -> {
                for(int i = 16; i >= 1; i--){
                    Draw.color(Color.black, 0.035f);
                    Fill.circle(b.x, b.y, radius * (1f + i / 5f));
                }
                Draw.color(Color.black);
                Fill.circle(b.x, b.y, radius);
                for(int i = 0; i < 64; i++){
                    float phase = (b.time / 140f + Mathf.randomSeed(i + b.id, 1f)) % 1f;
                    float angle = i * 137.5f + b.time * 0.7f;
                    float distance = radius * (1.1f + (1f - phase) * 3f);
                    Fill.circle(b.x + Angles.trnsx(angle, distance), b.y + Angles.trnsy(angle, distance),
                        1f + 3f * Mathf.sin(phase * Mathf.pi));
                }
                Draw.reset();
            });
        }
        Draw.z(Layer.effect);
        Draw.color(blackHole(b) ? Color.black : Color.white);
        Fill.circle(b.x, b.y, radius);
        Draw.color(blackHole(b) ? Color.orange : Color.white);
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
        public boolean blackHole;

        public CoreState(int seed){
            this.seed = seed;
        }
    }
}
