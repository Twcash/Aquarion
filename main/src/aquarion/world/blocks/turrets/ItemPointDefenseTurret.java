package aquarion.world.blocks.turrets;

import arc.util.Nullable;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class ItemPointDefenseTurret extends ItemTurret {
    public ItemPointDefenseTurret(String name) {
        super(name);
        targetInterval = 10;
        playerControllable = false;
    }
    public class IPDTBuild extends ItemTurretBuild{
        @Override
        protected void findTarget(){
            target = Groups.bullet.intersect(x - range, y - range, range*2, range*2).min(b -> b.team != this.team && b.type().hittable, b -> b.dst2(x, y));
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
