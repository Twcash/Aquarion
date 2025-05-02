package aquarion.world.blocks.turrets;

import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.entities.Units;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class ItemPointDefenseTurret extends ItemTurret {
    public float damageTargetWeight = 10;
    public ItemPointDefenseTurret(String name) {
        super(name);
    }
    public class IPDTBuild extends ItemTurretBuild{
        @Override
        protected void findTarget(){
            target = Groups.bullet.intersect(x - range, y - range, range*2, range*2).min(b -> b.team != this.team && b.type().hittable && !(b.type.collidesAir && !b.type.collidesTiles), b -> b.dst2(x, y) - b.damage * damageTargetWeight);
        }
        @Override
        protected void handleBullet(@Nullable Bullet bullet, float offsetX, float offsetY, float angleOffset){
            super.handleBullet(bullet, offsetX, offsetY, angleOffset);
            if(target instanceof Bullet b){
                bullet.data = b;
            }
        }
    }
}
