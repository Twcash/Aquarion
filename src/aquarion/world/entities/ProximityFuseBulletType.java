package aquarion.world.entities;

import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;

public class ProximityFuseBulletType extends BasicBulletType {
    //Bullet will create Frags before hitting an enemy.
    public float proximityRange = 32f; //In world units
    public boolean fragOnProximity = true;
    @Override
    public void update(Bullet b){
        super.update(b);

        if(fragOnProximity && proximityRange > 0f){
            Units.nearbyEnemies(b.team, b.x, b.y, proximityRange, other -> {
                if(other != null && !other.dead() && other.hitSize > 0f && other.checkTarget(collidesAir, collidesGround)){
                    createFrags(b, b.x, b.y);
                    b.remove();
                }
            });
        }
    }
}
