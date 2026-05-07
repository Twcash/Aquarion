package aquarion.content;

import aquarion.world.entities.bullet.AOEBulletType;
import aquarion.world.entities.bullet.GambleBulletType;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.AquaPal;
import arc.graphics.Color;
import arc.math.Interp;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.*;
import mindustry.graphics.Pal;

import static aquarion.content.AquaItems.uranium;
import static mindustry.content.Items.copper;
import static mindustry.content.Items.graphite;
import static mindustry.content.StatusEffects.burning;

public class AquaBullets {
    public static BulletType
    pointSilicon = new BasicBulletType(14,15, "aquarion-long-bullet") {{
        frontColor = Color.white;
        backColor = AquaPal.redDecal1Dark;
        lifetime = 5;
        knockback = 0.75f;
        hitSize = 12;
        ammoMultiplier = 2f;
        width = 5;
        height = 14;
        pierceArmor = true;
        shootEffect = Fx.shootBig;
        smokeEffect = AquaFx.shootSmoke1;
        hitEffect = Fx.hitFlameSmall;
        despawnEffect = Fx.reactorsmoke;
    }},
    pointMinium = new BasicBulletType(13,15, "aquarion-long-bullet") {{
        frontColor = Color.white;
        backColor = AquaPal.redDecal1Dark;
        lifetime = 5;
        knockback = 0.75f;
        width = 5;
        height = 14;
        hitSize = 15;
        ammoMultiplier = 2f;
        pierceArmor = true;
        shootEffect = Fx.shootBig;
        smokeEffect = AquaFx.shootSmoke1;
        hitEffect = Fx.hitFlameSmall;
        despawnEffect = Fx.reactorsmoke;
    }},
    pointGraphite = new BasicBulletType(11,20,"aquarion-long-bullet") {{
        frontColor = Color.white;
        backColor = graphite.color;
        lifetime = 9;
        hitSize = 15;
        reloadMultiplier = 1.3f;
        knockback = 0.5f;
        ammoMultiplier = 2f;
        rangeChange = 16;
        width = 5;
        height = 14;
        pierceArmor = true;
        shootEffect = Fx.shootBig;
        smokeEffect = AquaFx.shootSmoke1;
        hitEffect = Fx.hitFlameSmall;
        despawnEffect = Fx.reactorsmoke;
    }},
    pointCoal =new BasicBulletType(8,12, "aquarion-long-bullet") {{
        frontColor = Color.white;
        backColor = AquaPal.redDecal1Dark;
        lifetime = 8;
        hitSize = 11;
        status = burning;
        statusDuration = 8 * 60f;
        reloadMultiplier = 1.8f;
        rangeChange = 8 * 5f;
        width = 5;
        height = 14;
        ammoMultiplier = 3f;
        shootEffect = Fx.shootBig;
        smokeEffect = AquaFx.shootSmoke1;
        hitEffect = Fx.hitFlameBeam;
        despawnEffect = Fx.hitFlameBeam;
    }},
    pointBrimstone = new BasicBulletType(17, 45, "aquarion-long-bullet") {{
        frontColor = Color.white;
        backColor = AquaItems.brimstone.color;
        hitSize = 19;
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
        shootEffect = Fx.shootBig;
        smokeEffect = AquaFx.shootSmoke1;
        hitEffect = Fx.hitFlameSmall;
        despawnEffect = Fx.smokePuff;
    }},voltShoot = new GambleBulletType( new float[]{0.99f, 0.01f}, new LightningBulletType(){{
        damage = 4;
        lightningLength = 12;
        collidesAir = false;
        ammoMultiplier = 1f;
        buildingDamageMultiplier = 0.25f;
        lightningType = new BulletType(0.0001f, 0f){{
            lifetime = Fx.lightning.lifetime;
            hitEffect = Fx.hitLancer;
            despawnEffect = Fx.none;
            status = StatusEffects.shocked;
            hittable = false;
            lightColor = Color.white;
            collidesAir = false;
            buildingDamageMultiplier = 0.25f;
            shieldDamageMultiplier = 0.2f;
        }};
    }}, new ExplosionBulletType(){{
        splashDamageRadius = 300;
        damage = 300;
        killShooter = true;
    }}),
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
    }},concussGraphite = new MissileBulletType(1.8f, 40){{
        splashDamage = 90;
        splashDamageRadius = 48;
        weaveMag = 6;
        weaveScale = 10;
        weaveRandom = true;
        homingPower = 0.01f;
        status = AquaStatuses.concussed;
        trailEffect = Fx.incendTrail ;
        trailRotation = true;
        trailInterval = 10;
        trailLength = 12;
        backColor = trailColor = lightColor = Pal.techBlue;
        frontColor = Color.white;
        despawnEffect = hitEffect = Fx.flakExplosionBig;
    }},vectorCupronickel = new BasicBulletType(20, 60) {{
        ammoMultiplier = 2;
        height = 15;
        width = 24;
        trailWidth = 4;
        trailLength = 12;
        shieldDamageMultiplier = 1.5f;
        shootEffect = AquaFx.shootBigger;
        trailInterp = Interp.slope;
        smokeEffect = AquaFx.shootSmoke3;
        hitEffect = despawnEffect = Fx.hitBulletBig;
        knockback = 8f;
        frontColor = AquaPal.redDecal1;
        backColor = AquaPal.redDecal1Dark;
    }},vectorCopper = new BasicBulletType(15, 40) {{
        ammoMultiplier = 2;
        height = 15;
        width = 24;
        trailWidth = 4;
        trailLength = 12;
        shootEffect = AquaFx.shootBigger;
        trailInterp = Interp.slope;
        smokeEffect = AquaFx.shootSmoke3;
        hitEffect = despawnEffect = Fx.hitBulletBig;
        frontColor = AquaPal.redDecal1;
        backColor = AquaPal.redDecal1Dark;
    }},vectorMetaglass = new BasicBulletType() {{
        damage = 45;
        ammoMultiplier = 3;
        height = 15;
        speed = 16f;
        width = 24;
        trailWidth = 4;
        trailLength = 12;
        shootEffect = AquaFx.shootBigger;
        trailInterp = Interp.slope;
        smokeEffect = AquaFx.shootSmoke3;
        hitEffect = despawnEffect = Fx.hitBulletBig;
        knockback = -12f;
        frontColor = Color.white;
        backColor = Color.lightGray;
        fragBullets = 5;
        fragBullet= new BulletType(2, 15){{
            frontColor = Color.white;
            backColor = Pal.lightishGray;
            trailLength = 5;
            width = 12;
            height = 7;
            lifetime = 15;
        }};
    }},vectorSteel = new BasicBulletType() {{
        damage = 140;
        ammoMultiplier = 6;
        height = 30;
        speed = 16f;
        width = 30;
        trailWidth = 4;
        reloadMultiplier = 0.5f;
        rangeChange = 40;
        trailLength = 18;
        pierce = true;
        shootEffect = AquaFx.shootBigger;
        trailInterp = Interp.slope;
        smokeEffect = AquaFx.shootSmoke3;
        hitEffect = despawnEffect = Fx.hitBulletBig;
        knockback = -18f;
        frontColor = Color.white;
        backColor = trailColor = Color.lightGray;
    }},
    vectorUranium = new BasicBulletType(){{
        damage = 200;
        speed = 12;
        trailWidth = 4;
        width = 34;
        height = 34;
        shrinkX = 0;
        shrinkY = 0.1f;
        trailLength = 20;
        fragBullets = 8;
        fragBullet = new AOEBulletType(10, 300, 9, uranium.color.cpy().a(0.6f)){{
            speed = 20;
            drag = 0.2f;
            fadeTime = 120;
            collidesAir = false;
        }};
        shootEffect = AquaFx.shootBigger;
        trailInterp = Interp.slope;
        smokeEffect = AquaFx.shootSmokeRadioactive;
        frontColor =Color.white;
        backColor = trailColor = lightColor = hitColor = AquaItems.uranium.color;
        hitEffect = despawnEffect = AquaFx.uraniumExplosion;
    }};
}
