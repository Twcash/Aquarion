package aquarion.content;

import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.AquaPal;
import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;

import static mindustry.content.Items.copper;
import static mindustry.content.Items.graphite;
import static mindustry.content.StatusEffects.burning;

public class AquaBullets {
    public static BulletType
    pointSilicon = new BasicBulletType(20,15, "aquarion-long-bullet") {{
        frontColor = AquaPal.redDecal1;
        backColor = AquaPal.redDecal1Dark;
        lifetime = 5;
        knockback = 0.75f;
        hitSize = 9;
        ammoMultiplier = 2f;
        width = 5;
        height = 14;
        pierceArmor = true;
        shootEffect = Fx.shootPyraFlame;
        smokeEffect = Fx.shootSmallFlame;
        hitEffect = Fx.hitFlameSmall;
        despawnEffect = Fx.reactorsmoke;
    }},
    pointMinium = new BasicBulletType(20,15, "aquarion-long-bullet") {{
        frontColor = AquaPal.redDecal1;
        backColor = AquaPal.redDecal1Dark;
        lifetime = 5;
        knockback = 0.75f;
        width = 5;
        height = 14;
        hitSize = 9;
        ammoMultiplier = 2f;
        pierceArmor = true;
        shootEffect = Fx.shootPyraFlame;
        smokeEffect = Fx.shootSmallFlame;
        hitEffect = Fx.hitFlameSmall;
        despawnEffect = Fx.reactorsmoke;
    }},
    pointGraphite = new BasicBulletType(25,20,"aquarion-long-bullet") {{
        frontColor = graphite.color;
        backColor = graphite.color;
        lifetime = 9;
        hitSize = 11;
        reloadMultiplier = 1.3f;
        knockback = 0.5f;
        ammoMultiplier = 2f;
        rangeChange = 16;
        width = 5;
        height = 14;
        pierceArmor = true;
        shootEffect = Fx.shootPyraFlame;
        smokeEffect = AquaFx.shootPoint;
        hitEffect = Fx.hitFlameSmall;
        despawnEffect = Fx.reactorsmoke;
    }},
    pointCoal =new BasicBulletType(20,12, "aquarion-long-bullet") {{
        frontColor = AquaPal.redDecal1;
        backColor = AquaPal.redDecal1Dark;
        lifetime = 8;
        hitSize = 14;
        status = burning;
        statusDuration = 8 * 60f;
        reloadMultiplier = 1.8f;
        rangeChange = 8 * 5f;
        width = 5;
        height = 14;
        ammoMultiplier = 3f;
        shootEffect = Fx.shootPyraFlame;
        smokeEffect = AquaFx.shootPoint;
        hitEffect = Fx.hitFlameBeam;
        despawnEffect = Fx.hitFlameBeam;
    }},
    pointBrimstone = new BasicBulletType(40, 45, "aquarion-long-bullet") {{
        frontColor = AquaItems.brimstone.color;
        backColor = AquaItems.brimstone.color;
        hitSize = 12;
        lifetime = 2;
        pierce = true;
        pierceCap = 3;
        makeFire = true;
        status = StatusEffects.burning;
        statusDuration = 240;
        width = 5;
        height = 14;
        knockback = 2f;
        rangeChange = 48;
        reloadMultiplier = 0.1f;
        shootEffect = Fx.shootSmall;
        smokeEffect = AquaFx.shootPoint;
        hitEffect = Fx.hitFlameSmall;
        despawnEffect = Fx.smokePuff;
    }},
    peltLead = new BasicBulletType(9, 55, "aquarion-bolt") {{
        pierce = true;
        pierceBuilding = true;
        pierceCap = 2;
        width = 8;
        height = 14f;
        ammoMultiplier = 4;
        reloadMultiplier = 1.8f;
        rangeChange = -24;
        shrinkY = 0;
        shrinkX = 0.1f;
        hitSize = 8;
        trailWidth = 2f;
        trailLength = 12;
        frontColor = hitColor = Color.white;
        backColor = lightColor = trailColor = Color.valueOf("8d70ab");
        despawnEffect = hitEffect = AquaFx.hitBulletColor2;
        shootEffect = AquaFx.shootHori;
        smokeEffect = AquaFx.shootSmoke2;
    }},
    peltNickel = new BasicBulletType(5, 65, "aquarion-bolt") {{
        pierce = true;
        pierceBuilding = false;
        pierceCap = 3;
        width = 10;
        height = 18f;
        shrinkY = 0;
        shrinkX = 0.2f;
        trailWidth = 3f;
        trailLength = 16;
        reloadMultiplier = 0.8f;
        hitSize = 8;
        ammoMultiplier = 2;
        frontColor = hitColor = Color.white;
        backColor = lightColor = trailColor = Color.valueOf("e1d9bc");
        despawnEffect = hitEffect = AquaFx.hitBulletColor2;
        status = AquaStatuses.concussed;
        statusDuration = 5 * 60f;
        knockback = 4;
        shootEffect = AquaFx.shootHori;
        smokeEffect = AquaFx.shootSmoke2;
    }}, peltCupronickel = new BasicBulletType(5, 75,"aquarion-bolt") {{
        pierce = true;
        pierceBuilding = false;
        pierceCap = 2;
        width = 8;
        height = 12f;
        shrinkY = 0;
        rangeChange = 54;
        shrinkX = 0.2f;
        trailWidth = 3f;
        trailLength = 16;
        reloadMultiplier = 1.75f;
        shieldDamageMultiplier = 2.5f;
        hitSize = 6;
        ammoMultiplier = 3f;
        frontColor = hitColor = Color.white;
        backColor = lightColor = trailColor = copper.color;
        despawnEffect = hitEffect = AquaFx.hitBulletColor2;
        shootEffect = AquaFx.shootHori;
        smokeEffect = AquaFx.shootSmoke2;
    }};
}
