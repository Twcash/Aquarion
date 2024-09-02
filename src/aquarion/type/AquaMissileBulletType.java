package aquarion.type;

import mindustry.gen.Sounds;
import mindustry.graphics.Pal;

public class AquaMissileBulletType extends AquaBasicBulletType{

    public AquaMissileBulletType(float speed, float damage, String bulletSprite){
        super(speed, damage, bulletSprite);
        backColor = Pal.missileYellowBack;
        frontColor = Pal.missileYellow;
        homingPower = 0.08f;
        shrinkY = 0f;
        width = 8f;
        height = 8f;
        hitSound = Sounds.explosion;
        trailChance = 0.2f;
        lifetime = 52f;
    }

    public AquaMissileBulletType(float speed, float damage){
        this(speed, damage, "missile");
    }

    public AquaMissileBulletType(){
        this(1f, 1f, "missile");
    }
}