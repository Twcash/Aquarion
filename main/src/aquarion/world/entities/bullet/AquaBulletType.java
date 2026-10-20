package aquarion.world.entities.bullet;

import arc.Events;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.Fires;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.game.EventType;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.Turret;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class AquaBulletType extends BasicBulletType {

    public AquaBulletType(float speed, float damage, String bulletSprite){
        super(speed, damage);
        this.sprite = bulletSprite;
    }
    //Can this bullet extinguish fires?
    public boolean extinguishFires = false;
    public float extinguishIntensity = 150;
    //Determines if the bullet should follow the parent unit's rotation'
    public boolean followParentRotation = false;
    //Recoil rotation behavior
    public boolean shouldRecoilRotation = false;
    public float rotationRecoil = 180;
    public float rotationRecoilTime = 60;
    public boolean flipRotationRecoil = true;
    public static class rotting {
        public float remaining;
        public float perTick;
        public Unit owner;
    }

    //Really jank.
    public static ObjectMap<WeaponMount, Seq<rotting>> tasks = new ObjectMap<>();
    //Really jank.
    public static ObjectMap<Turret.TurretBuild, Seq<rotting>> turtasks = new ObjectMap<>();
    static {
        Events.run(EventType.Trigger.update, AquaBulletType::updateRotationRecoil);
        Events.on(EventType.ResetEvent.class, event -> {
            tasks.clear();
            turtasks.clear();
        });
    }

    private static float recoilStep(rotting task){
        return Math.copySign(Math.min(task.perTick * Time.delta, Math.abs(task.remaining)), task.remaining);
    }

    private static void updateRotationRecoil(){
        var mounts = tasks.entries();
        while(mounts.hasNext()){
            var entry = mounts.next();
            WeaponMount mount = entry.key;
            Seq<rotting> list = entry.value;
            for(int i = list.size - 1; i >= 0; i--){
                rotting task = list.get(i);
                if(task.owner == null || !task.owner.isAdded() || task.owner.dead){
                    list.remove(i);
                    continue;
                }
                float step = recoilStep(task);
                if(mount.weapon.rotate){
                    mount.rotation += step;
                }else{
                    task.owner.rotation += step;
                }
                task.remaining -= step;
                if(Math.abs(task.remaining) <= 0.0001f) list.remove(i);
            }
            if(list.isEmpty()) mounts.remove();
        }

        var turrets = turtasks.entries();
        while(turrets.hasNext()){
            var entry = turrets.next();
            Seq<rotting> list = entry.value;
            if(!entry.key.isAdded() || entry.key.dead()){
                turrets.remove();
                continue;
            }
            for(int i = list.size - 1; i >= 0; i--){
                rotting task = list.get(i);
                float step = recoilStep(task);
                entry.key.rotation += step;
                task.remaining -= step;
                if(Math.abs(task.remaining) <= 0.0001f) list.remove(i);
            }
            if(list.isEmpty()) turrets.remove();
        }
    }
    @Override
    public void init(Bullet b) {
        super.init(b);
        //Unit code for mount rotation recoil behavior
        if (shouldRecoilRotation) {
            if ((b.owner instanceof Unit u)) {
                for (WeaponMount mount : u.mounts) {
                    if (mount.weapon.bullet == this) {
                        rotting task = new rotting();
                        task.remaining = flipRotationRecoil ? mount.totalShots % 2 == 0 ? rotationRecoil : -rotationRecoil : rotationRecoil;
                        task.perTick = Math.abs(rotationRecoil) / Math.max(rotationRecoilTime, 0.001f);
                        task.owner = u;
                        tasks.get(mount, Seq::new).add(task);
                    }
                }
            }
            //Turret support for rotation recoil behavior
            if (b.owner instanceof Turret.TurretBuild turret) {
                rotting task = new rotting();
                task.remaining = flipRotationRecoil ? turret.totalShots % 2 == 0 ? rotationRecoil : -rotationRecoil : rotationRecoil;
                task.perTick = Math.abs(rotationRecoil) / Math.max(rotationRecoilTime, 0.001f);
                turtasks.get(turret, Seq::new).add(task);
            }
        }
    }
    @Override
    public void update(Bullet b){
        super.update(b);

        if(followParentRotation) {
            float rot = (b.owner instanceof Unitc c) ? c.rotation() : (b.owner instanceof Turret.TurretBuild turret) ? turret.rotation : 0;
            b.vel.setAngle(rot).setLength(speed);
            b.rotation(rot);
        }
        if(extinguishFires){
            Tile tile = world.tileWorld(b.x, b.y);
            if(tile != null && Fires.has(tile.x, tile.y)){
                Fires.extinguish(tile, 100f);
                b.remove();
                hit(b);
            }
        }
    }
    @Override
    public void hit(Bullet b, float hitx, float hity, boolean createFrags){
        super.hit(b,hitx,hity,createFrags);
        if(extinguishFires){
            Fires.extinguish(world.tileWorld(hitx, hity), extinguishIntensity);
            for(Point2 p : Geometry.d4){
                Fires.extinguish(world.tileWorld(hitx + p.x * tilesize, hity + p.y * tilesize), extinguishIntensity);
            }
        }
    }
}
