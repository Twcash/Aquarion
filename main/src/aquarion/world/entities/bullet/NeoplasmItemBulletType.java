package aquarion.world.entities.bullet;

import aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock;
import aquarion.world.graphics.Renderer;
import arc.graphics.g2d.Draw;
import arc.util.Time;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.type.Item;

/** Invisible homing bullet that carries an item from one blob to another and delivers it on arrival, so items visibly travel instead of teleporting. */
public class NeoplasmItemBulletType extends BulletType {
    public float travelSpeed = 1.2f;

    public NeoplasmItemBulletType() {
        collides = false;
        collidesTiles = false;
        hittable = false;
        absorbable = false;
        reflectable = false;
        keepVelocity = false;
        drag = 0f;
        hitSize = 0f;
        lifetime = 300f;
    }

    @Override
    public void update(Bullet b) {
        if (!(b.data instanceof Cargo cargo)) {
            b.remove();
            return;
        }
        Building target = cargo.target;
        if (target == null || target.tile == null || !target.isValid()) {
            returnItem(b, cargo);
            b.remove();
            return;
        }
        float step = travelSpeed * Time.delta;
        if (b.dst(target.x, target.y) <= step) {
            deliver(b, cargo);
            b.remove();
            return;
        }
        b.vel.set(target.x - b.x, target.y - b.y).setLength(travelSpeed);
        b.rotation(b.vel.angle());
    }

    @Override
    public void despawned(Bullet b) {
        //No super call: base despawned plays despawn effects/sound/shake and can trigger despawnHit.
        if (b.data instanceof Cargo cargo && !cargo.delivered) {
            returnItem(b, cargo);
        }
    }

    void deliver(Bullet b, Cargo cargo) {
        if (cargo.delivered) return;
        cargo.delivered = true;
        unregister(cargo);
        if (cargo.target.isValid() && cargo.target.acceptItem(cargo.source, cargo.item)) {
            cargo.target.handleItem(cargo.source, cargo.item);
        } else if (cargo.source != null && cargo.source.isValid()) {
            cargo.source.items.add(cargo.item, 1);
        }
    }

    void returnItem(Bullet b, Cargo cargo) {
        if (cargo.delivered) return;
        cargo.delivered = true;
        unregister(cargo);
        if (cargo.source != null && cargo.source.isValid()) {
            cargo.source.items.add(cargo.item, 1);
        }
    }

    void unregister(Cargo cargo) {
        if (cargo.source instanceof GenericNeoplasiaBlock.NeoplasiaBuild nb) {
            nb.inFlight = Math.max(0, nb.inFlight - 1);
        }
    }

    @Override
    public void draw(Bullet b) {
        if (!(b.data instanceof Cargo cargo) || cargo.item == null) return;
        Draw.z(Renderer.Layer.neoplasiaBase - 0.2f);
        Draw.rect(cargo.item.fullIcon, b.x, b.y, 6f, 6f, 0f);
    }

    public static class Cargo {
        public Item item;
        public Building source;
        public Building target;
        public boolean delivered;

        public Cargo(Item item, Building source, Building target) {
            this.item = item;
            this.source = source;
            this.target = target;
        }
    }
}
