package aquarion.world.entities.bullet;

import arc.Events;
import arc.math.Angles;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.units.WeaponMount;
import mindustry.game.EventType;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import mindustry.world.blocks.defense.turrets.Turret;

public class AquaLaserBulletType extends LaserBulletType {
    //Recoil rotation behavior
    public boolean shouldRecoilRotation = false;
    public float rotationRecoil = 180;
    public float rotationRecoilTime = 60;
    public boolean flipRotationRecoil = true;
    //Determines if the bullet should follow the parent unit's rotation'
    public boolean followParentRotation = false;

    public boolean followParentPosition = false;

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
        if(followParentPosition) {
            if(b.owner instanceof Unit u){
                for(WeaponMount mount : u.mounts){
                    if(mount.weapon.bullet == this){

                        float rot = u.rotation - 90f;

                        float wx = Angles.trnsx(rot, mount.weapon.x, mount.weapon.y);
                        float wy = Angles.trnsy(rot, mount.weapon.x, mount.weapon.y);

                        float shootX = u.x + wx;
                        float shootY = u.y + wy;

                        shootX += Angles.trnsx(mount.rotation, mount.weapon.shootX + mount.weapon.shootX + u.x + mount.weapon.x, mount.weapon.shootY + mount.weapon.shootY + u.y + mount.weapon.y);
                        shootY += Angles.trnsy(mount.rotation, mount.weapon.shootX + u.x + mount.weapon.x, mount.weapon.shootY + u.y + mount.weapon.y);
                        b.set(shootX, shootY);

                    }
                }
            } else if(b.owner instanceof Turret.TurretBuild turret) {
                if (turret.block instanceof Turret t) {
                    Tmp.v1.trns(turret.rotation, t.shootX + turret.x, t.shootY + turret.y);

                    float bulletX = turret.x + Tmp.v1.x;
                    float bulletY = turret.y + Tmp.v1.y;

                    b.set(bulletX, bulletY);
                }
            }
        }
    }
}
