package aquarion.world.entities.bullet;

import arc.Events;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.game.EventType;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import mindustry.world.blocks.defense.turrets.Turret;

public class AquaBulletType extends BasicBulletType {
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
    }

    //Really jank.
    public static ObjectMap<WeaponMount, Seq<rotting>> tasks = new ObjectMap<>();
    //Really jank.
    public static ObjectMap<Turret.TurretBuild, Seq<rotting>> turtasks = new ObjectMap<>();
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
                        task.perTick = rotationRecoil / rotationRecoilTime;
                        tasks.get(mount, Seq::new).add(task);
                        Events.run(EventType.Trigger.update, () -> {
                            for (ObjectMap.Entry<WeaponMount, Seq<rotting>> entry : tasks) {
                                WeaponMount mounte = entry.key;
                                Seq<rotting> list = entry.value;
                                for (int i = list.size - 1; i >= 0; i--) {
                                    rotting taske = list.get(i);
                                    float step = Math.min(taske.perTick, taske.remaining);
                                    mounte.rotation += step;
                                    taske.remaining -= step;
                                    if (taske.remaining <= 0f) {
                                        list.remove(i);
                                    }
                                }
                            }
                        });
                    }
                }
            }
            //Turret support for rotation recoil behavior
            if (b.owner instanceof Turret.TurretBuild turret) {
                rotting task = new rotting();
                task.remaining = flipRotationRecoil ? turret.totalShots % 2 == 0 ? rotationRecoil : -rotationRecoil : rotationRecoil;
                task.perTick = rotationRecoil / rotationRecoilTime;
                turtasks.get(turret, Seq::new).add(task);
                Events.run(EventType.Trigger.update, () -> {
                    for (ObjectMap.Entry<Turret.TurretBuild, Seq<rotting>> entry : turtasks) {
                        Turret.TurretBuild mounte = entry.key;
                        Seq<rotting> list = entry.value;
                        for (int i = list.size - 1; i >= 0; i--) {
                            rotting taske = list.get(i);
                            float step = Math.min(taske.perTick, taske.remaining);
                            mounte.rotation += step;
                            taske.remaining -= step;
                            if (taske.remaining <= 0f) {
                                list.remove(i);
                            }
                        }
                    }
                });
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
    }
}
