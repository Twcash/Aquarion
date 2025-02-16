package aquarion.units;
import arc.math.*;
import arc.util.*;
import mindustry.entities.pattern.ShootPattern;

public class uhhShootSummon extends ShootPattern {
    public float x, y, radius, spread;

    public uhhShootSummon(float x, float y, float radius, float spread){
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.spread = spread;
    }

    public uhhShootSummon(){
    }

    @Override
    public void shoot(int totalShots, BulletHandler handler, @Nullable Runnable barrelIncrementer){
        for(int i = 0; i < shots; i++){
            Tmp.v1.trns(Mathf.random(360f), Mathf.random(radius));

            handler.shoot(x + Tmp.v1.x, y + Tmp.v1.y, Mathf.range(spread), firstShotDelay + shotDelay * i);
        }
    }
}