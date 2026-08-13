package aquarion.content;

import aquarion.world.entities.bullet.AOEBulletType;
import aquarion.world.entities.bullet.AquaBulletType;
import aquarion.world.entities.bullet.DumpItemBulletType;
import aquarion.world.entities.bullet.GambleBulletType;
import aquarion.world.entities.bullet.NeoplasmGlobBulletType;
import aquarion.world.graphics.AquaFx;
import aquarion.world.graphics.AquaPal;
import aquarion.world.graphics.Renderer;
import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WrapEffect;
import mindustry.entities.part.FlarePart;
import mindustry.entities.pattern.ShootPattern;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaLiquids.*;
import static aquarion.content.AquaLiquids.methane;
import static aquarion.world.graphics.AquaFx.rand;
import static aquarion.world.graphics.AquaFx.v;
import static aquarion.world.graphics.AquaPal.smoke;
import static aquarion.world.graphics.AquaPal.smokeLight;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.randLenVectors;
import static arc.math.Interp.bounce;
import static arc.math.Interp.pow2Out;
import static mindustry.content.Items.*;
import static mindustry.content.Liquids.*;
import static mindustry.content.StatusEffects.*;
import static mindustry.gen.Sounds.explosion;
import static mindustry.gen.Sounds.loopSpray;

public class AquaBullets {
    public static BulletType
            dumpitem = new DumpItemBulletType(){{
                layer = Renderer.Layer.blockUnder - 0.1f;
                elevation = 1;
    }},
            throwItem = new DumpItemBulletType(){{}},
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
    }},
    voltShoot = new GambleBulletType( new float[]{0.99f, 0.01f}, new LightningBulletType(){{
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
    }},
    suffocateSand = new AquaBulletType(5f, 10, "aquarion-sand-clump"){{
        shootPattern = new ShootPattern(){{
            shots = 9;
        }};
        inaccuracy = 12;
        extinguishFires = true;
        extinguishIntensity = 400f;
        spin = 0.1f;
        hitSize = 14;
        velocityScaleRandMax = 1.1f;
        velocityScaleRandMin = 0.9f;
        ammoMultiplier = 2;
        sprite = "aquarion-sand-clump";
        frontColor = backColor = Color.white;
        knockback = 1;
        width = height = 8;
        layer = Renderer.Layer.flyingUnitLow + 0.1f;
        hitEffect = despawnEffect = new WrapEffect(Fx.breakProp, sand.color);
    }},
    suffocateLead = new AquaBulletType(4, 150, "aquarion-lead-slab"){{
        scaleLife = true;
        hitSize = 18;
        knockback = 20;
        status = AquaStatuses.concussed;
        statusDuration = 1 * Time.toMinutes;
        frontColor = Color.white;
        inaccuracy = 0;
        shrinkX = 0.5f;
        reloadMultiplier = 0.4f;
        shrinkY = 0.5f;
        sprite = "aquarion-lead-slab";
        layer = Renderer.Layer.flyingUnitLow + 0.1f;
        width = height = 16;
        ammoMultiplier = 1f;
        hitEffect = despawnEffect = new WrapEffect(Fx.breakProp, lead.color);
    }},
    concussGraphite = new MissileBulletType(1.8f, 40){{
        splashDamage = 90;
        splashDamageRadius = 48;
        weaveMag = 6;
        weaveScale = 10;
        weaveRandom = true;
        homingPower = 0.01f;
        status = AquaStatuses.concussed;
        trailEffect = Fx.incendTrail ;
        trailInterval = 10;
        trailLength = 12;
        backColor = trailColor = lightColor = Pal.techBlue;
        frontColor = Color.white;
        despawnEffect = hitEffect = Fx.flakExplosionBig;
    }},
    vectorCupronickel = new BasicBulletType(20, 60) {{
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
    }},
    vectorCopper = new BasicBulletType(15, 40) {{
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
    }},
    vectorMetaglass = new BasicBulletType() {{
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
    }},
    vectorSteel = new BasicBulletType() {{
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
    }},
    truncateGraphite = new BasicBulletType(4.5f, 20) {{
        splashDamage = 90;
        splashDamageRadius = 70;
        hitEffect = despawnEffect = new ExplosionEffect() {{
            waveRad = 30;
            waveLife = 90;
            waveColor = methane.color;
        }};
        sprite = "aquarion-bolt";
        width = 6;
        height = 9;
        frontColor = Color.white;
        backColor = methane.color;
        collidesGround = true;
        collidesAir = false;
        scaleLife = true;
        sticky = true;
        stickyExtraLifetime = 200;
        drag = 0.02f;
        intervalBullets = 3;
        bulletInterval = 10;
        trailInterval = 10;
        despawnShake = 8;
        despawnSound = explosion;
        fragOffsetMax = 3;
        fragOffsetMin = 0;
        trailEffect = new Effect(400f * 1.2f, 250f, e -> {
            color(methane.color, 0.35f);

            randLenVectors(e.id, 3, 15f, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 3.75f * Mathf.clamp(e.fin() / 0.1f) * Mathf.clamp(e.fout() / 0.1f));
            });
        });
        intervalBullet = new EmptyBulletType() {{
            lifetime = 400;
            hittable = false;
            collides = false;
            fragBullets = 1;
            hitEffect = Fx.ballfire;
            fragBullet = new BasicBulletType(0, 20) {{
                collidesAir = false;
                width = height = 0;
                instantDisappear = true;
                lifetime = 0;
                splashDamage = 20;
                splashDamageRadius = 34;
                hitEffect = despawnEffect = new ExplosionEffect() {{
                    waveRad = 30;
                    waveLife = 90;
                    waveColor = methane.color;
                }};
                despawnShake = 1;

            }};
        }};
    }},
    perforateMinium = new BasicBulletType(6, 30){{
        width = 9;
        height = 14;
        randomAngleOffset = 5;
        reloadMultiplier = 2f;
        frontColor = Color.white;
        backColor = trailColor = minium.color;
        trailLength = 14;
        lifeScaleRandMax = 1.1f;
        lifeScaleRandMin = 0.9f;
        shootEffect = AquaFx.shootMassive;
        smokeEffect = Fx.fireSmoke;
        hitEffect = despawnEffect = AquaFx.hitBulletColor2;
    }},
    perforateBrimstone = new BasicBulletType(12, 60){{
        width = 9;
        height = 14;
        randomAngleOffset = 1;
        frontColor = Color.white;
        backColor = trailColor = AquaPal.brimstone;
        trailLength = 14;
        lifeScaleRandMax = 1.1f;
        lifeScaleRandMin = 0.9f;
        makeFire = true;
        status = burning;
        statusDuration = 400;
        shootEffect = AquaFx.shootMassive;
        smokeEffect = Fx.fireSmoke;
        hitEffect = despawnEffect = AquaFx.hitBulletColor2;
    }},
    perforateLead = new BasicBulletType(4, 45){{
        width = 9;
        height = 14;
        randomAngleOffset = 1;
        frontColor = Color.white;
        backColor = trailColor = lead.color;
        trailLength = 14;
        lifeScaleRandMax = 1.1f;
        lifeScaleRandMin = 0.9f;
        shootEffect = AquaFx.shootMassive;
        smokeEffect = Fx.fireSmoke;
        hitEffect = despawnEffect = AquaFx.hitBulletColor2;
    }},
    perforateFerricMatter = new BasicBulletType(6, 90){{
        width = 9;
        height = 14;
        randomAngleOffset = 1;
        frontColor = Color.white;
        backColor = trailColor = AquaItems.ferricMatter.color;
        trailLength = 14;
        lifeScaleRandMax = 1.1f;
        lifeScaleRandMin = 0.9f;
        shootEffect = AquaFx.shootMassive;
        smokeEffect = Fx.fireSmoke;
        hitEffect = despawnEffect = AquaFx.hitBulletColor2;
    }},
    aftershockMinium = new BasicBulletType(6, 30, "aquarion-shrapnel") {{
        width = 16;
        spin = 2;
        randomAngleOffset = 1;
        frontColor = Color.white;
        backColor = trailColor = minium.color;
        angleOffset = 5;
        height = 19;
        status = burning;
        statusDuration = 10 * 60;
        shootEffect = AquaFx.shootMassive;
        smokeEffect = Fx.shootBigSmoke;
        hitEffect = despawnEffect = Fx.hitScepterSecondary;
        trailWidth = 3;
        trailInterp = pow2Out;

        trailLength = 12;
        ammoMultiplier = 1;
        shrinkX = 0.5f;
        shrinkY = 0.8f;
    }},
    aftershockMetaglass = new BasicBulletType(9, 45, "aquarion-shrapnel") {{
        width = 15;
        spin = 2;
        randomAngleOffset = 1;
        angleOffset = 5;
        height = 20;
        trailWidth = 3;
        trailLength = 12;
        shrinkX = 0.5f;
        status = StatusEffects.blasted;
        statusDuration = 120 * 60;
        smokeEffect = Fx.shootBigSmoke;
        shootEffect = AquaFx.shootMassive;
        ammoMultiplier = 1;
        reloadMultiplier = 0.9f;
        trailInterp = pow2Out;

        shrinkY = 0.8f;
        hitEffect = despawnEffect = Fx.hitScepterSecondary;
        frontColor = Color.white;
        backColor = trailColor = Pal.lightishGray;
    }},
    aftershockScrap = new BasicBulletType(6, 30, "aquarion-shrapnel") {{
        width = 12;
        spin = 2;
        randomAngleOffset = 1;
        angleOffset = 5;
        despawnHit = true;
        height = 19;
        status = StatusEffects.melting;
        statusDuration = 15 * 60;
        trailInterp = pow2Out;
        trailWidth = 3;
        trailLength = 12;
        ammoMultiplier = 1;
        hitEffect = despawnEffect = Fx.hitScepterSecondary;
        smokeEffect = Fx.shootBigSmoke;
        shrinkX = 0.5f;
        shrinkY = 0.8f;
        puddleLiquid = slag;
        puddleAmount = 20;
        puddles = 2;
        rangeChange = -48;
        puddleRange = 6;
        shootEffect = Fx.shootLiquid;
    }},
    sentrySilicon = new InterceptorBulletType(10, 30f, "aquarion-flechette") {{
        collidesGround = false;
        trailLength = 5;
        hitSize = 7;
        collidesTiles = false;
        collidesAir = false;
        ammoMultiplier = 5;
        width = height = 8;
        frontColor = lightColor = Pal.siliconAmmoFront;
        backColor = trailColor = Pal.siliconAmmoBack;
        trailInterp = v -> Math.max(Mathf.slope(v), 0.9f);
    }},
    sentryCopper = new InterceptorBulletType(25, 45f, "aquarion-flechette") {{
        collidesGround = false;
        trailLength = 7;
        hitSize = 8;
        width = height = 8;
        collidesTiles = false;
        collidesAir = false;
        ammoMultiplier = 6;
        reloadMultiplier = 2;
        trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);
    }},
    thrashCoal = new FlakBulletType(18f, 220f) {{
        smokeEffect = AquaFx.shootSmoke3;
        trailEffect = AquaFx.thrashTrailSmoke;
        shootEffect = AquaFx.shootLudicrous;
        trailColor = backColor = hitColor = lightColor = coal.color;
        despawnShake = 3;
        trailInterval = 2;
        despawnEffect = hitEffect = new MultiEffect(AquaFx.thrashExplosion,AquaFx.thrashExplodeSmoke);
        sprite = "aquarion-shell";
        splashDamage = 340;
        splashDamageRadius = 64f;
        drag = 0.018f;
        makeFire = true;
        status = burning;
        shrinkX = 0.35F;
        collidesGround = true;
        collidesAir = false;
        shrinkY = 0.63F;
        width = 12;
        explodeRange = 32f;
        height = 18;
        statusDuration = 600;
        shrinkInterp = Interp.pow2Out;
        despawnSound = explosion;
    }},
    thrashMagnesium = new FlakBulletType(22f, 320f) {{
        smokeEffect = AquaFx.shootSmoke3;
        trailEffect = AquaFx.trailSmoke1;
        shootEffect = AquaFx.shootLudicrous;
        trailColor = backColor = hitColor = lightColor = magnesiumPowder.color;
        despawnShake = 3;
        trailInterval = 2;
        despawnEffect = hitEffect = new MultiEffect(AquaFx.thrashExplosion,AquaFx.thrashExplodeSmoke);
        splashDamage = 125;
        splashDamageRadius = 80f;
        drag = 0.016f;
        makeFire = true;
        status = burning;
        explodeRange = 40;
        collidesAir = false;
        collidesGround = true;
        sprite = "aquarion-shell";
        shrinkX = 0.35F;
        shrinkY = 0.63F;
        width = 12;
        height = 18;
        statusDuration = 1200;
        shrinkInterp = Interp.pow2Out;
        despawnSound = explosion;
    }},
    thrashBrimstone =  new FlakBulletType(18f, 250f) {{
        smokeEffect = AquaFx.shootSmoke3;
        shootEffect = AquaFx.shootLudicrous;
        trailEffect = AquaFx.trailSmoke1;
        trailColor = backColor = hitColor = lightColor = AquaPal.brimstone;
        despawnShake = 7;
        trailInterval = 1;
        despawnEffect = hitEffect = new MultiEffect(AquaFx.thrashExplosion,AquaFx.thrashExplodeSmoke);
        splashDamage = 400;
        splashDamageRadius = 50f;
        drag = 0.013f;
        makeFire = true;
        status = burning;
        sprite = "aquarion-shell";
        explodeRange = 25;
        collidesGround = true;
        collidesAir = false;
        shrinkX = 0.35F;
        shrinkY = 0.63F;
        width = 12;
        height = 18;
        statusDuration = 450;
        shrinkInterp = Interp.pow2Out;
        despawnSound = explosion;
    }},
    thrashSporePod = new FlakBulletType(18f, 120f) {{
        smokeEffect = AquaFx.shootSmoke3;
        shootEffect = AquaFx.shootLudicrous;
        trailEffect = AquaFx.trailSmoke1;
        trailColor = backColor = hitColor = lightColor = sporePod.color;
        despawnShake = 7;
        trailInterval = 1;
        despawnEffect = hitEffect = new MultiEffect(AquaFx.thrashExplosion,AquaFx.thrashExplodeSmoke);
        splashDamage = 380f;
        splashDamageRadius = 40f;
        drag = 0.013f;
        makeFire = true;
        status = burning;
        incendChance = 1;
        incendAmount = 3;
        incendSpread = 8*5f;
        sprite = "aquarion-shell";
        explodeRange = 25;
        collidesGround = true;
        collidesAir = false;
        shrinkX = 0.35F;
        shrinkY = 0.63F;
        width = 12;
        height = 18;
        rangeChange = 8 * 10f;
        statusDuration = 900;
        shrinkInterp = Interp.pow2Out;
        despawnSound = explosion;
    }},
    flagellateSteel = new ArtilleryBulletType(14, 300) {{
        drag = 0.007f;
        splashDamage = 1800;
        splashDamageRadius = 12 * 8f;
        width = 20;
        frontColor = hitColor = Pal.turretHeat;
        backColor = trailColor = lightColor = Pal.accentBack;
        height = 32;
        trailLength = 18;
        trailWidth = 8;
        ammoMultiplier = 1;
        shrinkY = 0.8f;
        shrinkInterp = Interp.pow2In;
        trailInterp = Interp.pow2In;
        shootEffect = Fx.shootBig2;
        smokeEffect = AquaFx.shootSmokeMassive;
        despawnEffect = AquaFx.flagellateExplosion;
    }},
    dislocateFerricMatter = new GambleBulletType( new float[]{0.5f, 0.5f}, new FlakBulletType(35, 40) {{
        trailLength = 7;
        hitSize = 8;
        scaleLife = true;
        explodeDelay = 2;
        ammoMultiplier = 10;
        explodeRange = 10;
        fragBullets = 4;
        collidesGround = true;
        collidesAir = false;
        fragBullet = new BasicBulletType(4, 20f, "aquarion-flechette") {{
            width = 4;
            height = 6;
            lifetime = 12;
            frontColor = lightColor = Pal.siliconAmmoFront;
            backColor = trailColor = Pal.siliconAmmoBack;
            collidesGround = true;
            collidesAir = false;
        }};
        width = 8;
        height = 12;
        frontColor = lightColor = Pal.siliconAmmoFront;
        backColor = trailColor = Pal.siliconAmmoBack;
        lifetime = 10;
        trailInterp = v -> Math.max(Mathf.slope(v), 0.9f);
    }}, new FlakBulletType(35, 50) {{
        trailLength = 7;
        hitSize = 8;
        scaleLife = true;
        explodeDelay = 2;
        ammoMultiplier = 10;
        explodeRange = 10;
        lifetime = 10;
        fragBullets = 4;
        collidesGround = false;
        fragBullet = new BasicBulletType(4, 20f, "aquarion-flechette") {{
            width = 4;
            height = 6;
            lifetime = 12;
            frontColor = lightColor = Pal.siliconAmmoFront;
            collidesGround = false;
            collidesAir = true;
            backColor = trailColor = Pal.siliconAmmoBack;
        }};
        width = 8;
        height = 12;
        frontColor = lightColor = Pal.siliconAmmoFront;
        backColor = trailColor = Pal.siliconAmmoBack;
        trailInterp = v -> Math.max(Mathf.slope(v), 0.9f);
    }}){{
        ammoMultiplier = 3;
    }},
    dislocateFerrosilicon = new GambleBulletType( new float[]{0.5f, 0.5f}, new FlakBulletType(35, 100f) {{
        trailLength = 7;
        hitSize = 8;
        scaleLife = true;
        explodeDelay = 2;
        ammoMultiplier = 10;
        explodeRange = 10;
        fragBullets = 4;
        collidesGround = true;
        collidesAir = false;
        fragBullet = new BasicBulletType(4, 45f, "aquarion-flechette") {{
            width = 4;
            height = 6;
            lifetime = 12;
            frontColor = lightColor = Pal.siliconAmmoFront;
            backColor = trailColor = Pal.siliconAmmoBack;
            collidesGround = true;
            collidesAir = false;
        }};
        width = 8;
        height = 12;
        frontColor = lightColor = Pal.siliconAmmoFront;
        backColor = trailColor = Pal.siliconAmmoBack;
        lifetime = 10;
        trailInterp = v -> Math.max(Mathf.slope(v), 0.9f);
    }}, new FlakBulletType(35, 100f) {{
        trailLength = 7;
        hitSize = 8;
        scaleLife = true;
        explodeDelay = 2;
        ammoMultiplier = 10;
        explodeRange = 10;
        lifetime = 10;
        fragBullets = 4;
        collidesGround = false;
        fragBullet = new BasicBulletType(4, 45f, "aquarion-flechette") {{
            width = 4;
            height = 6;
            lifetime = 12;
            frontColor = lightColor = Pal.siliconAmmoFront;
            collidesGround = false;
            collidesAir = true;
            backColor = trailColor = Pal.siliconAmmoBack;
        }};
        width = 8;
        height = 12;
        frontColor = lightColor = Pal.siliconAmmoFront;
        backColor = trailColor = Pal.siliconAmmoBack;
        trailInterp = v -> Math.max(Mathf.slope(v), 0.9f);
    }}){{
        ammoMultiplier = 3;
    }},
    dislocateSteel = new BasicBulletType(45, 90) {{
        trailLength = 9;
        hitSize = 12;
        ammoMultiplier = 20;
        splashDamage = 60;
        scaleLife = true;
        fragBullets = 10;
        collidesGround = true;
        fragBullet = new BasicBulletType(4, 20f, "aquarion-flechette") {{
            width = 4;
            height = 6;
            lifetime = 12;
            frontColor = lightColor = Color.white;
            backColor = trailColor = Pal.gray;
        }};
        width = 8;
        height = 12;
        frontColor = lightColor = Color.white;
        backColor = trailColor = Pal.gray;
        trailInterp = v -> Math.max(Mathf.slope(v), 0.9f);
    }},
    douseWater =  new LiquidBulletType(water) {{
        lifetime = 49f;
        speed = 4f;
        knockback = 1.7f;
        puddleSize = 8f;
        orbSize = 4f;
        drag = 0.001f;
        ammoMultiplier = 0.4f;
        statusDuration = 60f * 4f;
        damage = 0.2f;
        layer = Layer.bullet - 2f;
    }},
    douseClearWater =  new LiquidBulletType(AquaLiquids.clearwater) {{
        lifetime = 49f;
        speed = 7f;
        knockback = 1.7f;
        puddleSize = 8f;
        orbSize = 4f;
        drag = 0.001f;
        ammoMultiplier = 0.4f;
        statusDuration = 60f * 4f;
        damage = 0.2f;
        layer = Layer.bullet - 2f;
    }},
    douseSlag = new LiquidBulletType(Liquids.slag) {{
        lifetime = 49f;
        speed = 4f;
        knockback = 1.3f;
        puddleSize = 8f;
        orbSize = 4f;
        damage = 4.75f;
        drag = 0.001f;
        ammoMultiplier = 0.4f;
        statusDuration = 60f * 4f;
    }},
    douseCryofluid = new LiquidBulletType(Liquids.cryofluid) {{
        lifetime = 49f;
        speed = 4f;
        knockback = 1.3f;
        puddleSize = 8f;
        orbSize = 4f;
        drag = 0.001f;
        ammoMultiplier = 0.4f;
        statusDuration = 60f * 4f;
        damage = 0.2f;
    }},
    douseOil = new LiquidBulletType(Liquids.oil) {{
        lifetime = 49f;
        speed = 4f;
        knockback = 1.3f;
        puddleSize = 8f;
        orbSize = 4f;
        drag = 0.001f;
        ammoMultiplier = 0.4f;
        statusDuration = 60f * 4f;
        damage = 0.2f;
        layer = Layer.bullet - 2f;
    }},
    dousePetrol = new LiquidBulletType(petroleum) {{
        lifetime = 52f;
        speed = 3.5f;
        knockback = 1.5f;
        puddleSize = 9f;
        orbSize = 4f;
        drag = 0.001f;
        status = tarred;
        ammoMultiplier = 0.4f;
        statusDuration = 60f * 8f;
        damage = 0.8f;
        layer = Layer.bullet - 2f;
    }},
    douseMagma = new LiquidBulletType(AquaLiquids.magma) {{
        lifetime = 49f;
        speed = 4f;
        knockback = 0.5f;
        puddleSize = 9f;
        orbSize = 4f;
        drag = 0.001f;
        status = melting;
        ammoMultiplier = 0.8f;
        statusDuration = 60f * 5f;
        damage = 5f;
        layer = Layer.bullet - 2f;
    }},
    douseHaze = new LiquidBulletType(haze) {{
        lifetime = 49f;
        speed = 7f;
        knockback = 0.5f;
        puddleSize = 9f;
        reloadMultiplier = 5;
        orbSize = 4f;
        drag = 0.001f;
        status = burning;
        ammoMultiplier = 0.8f;
        statusDuration = 60f * 2f;
        damage = 1f;
        layer = Layer.bullet - 2f;
    }},
    fomentLead =  new MissileBulletType(3f, 75, "bullet") {{
        width = 10f;
        height = 16f;
        trailLength = 12;

        lifetime = 60f;
        ammoMultiplier = 1;
        shootEffect = AquaFx.shootHori;
        smokeEffect = new MultiEffect(AquaFx.shootSmokeTri, AquaFx.fomentShootSmoke);
        trailEffect = Fx.none;
        weaveMag = 2;
        homingPower = 0.01f;
        homingDelay = 5;
        weaveScale = 1.75f;
        shrinkX = 0.2f;
        shrinkY = 0.8f;
        frontColor = lightColor = hitColor = Color.white;
        backColor = trailColor = Color.valueOf("8d70ab");
        hitEffect = despawnEffect = AquaFx.fomentHitColor;
    }},
    fomentFerric = new MissileBulletType(3.2f, 150, "bullet") {{
        width = 9f;
        height = 15f;
        trailLength = 10;
        lifetime = 60f;
        reloadMultiplier = 0.4f;
        rangeChange = 32;
        trailEffect = Fx.none;
        shootEffect = AquaFx.shootHori;
        smokeEffect = new MultiEffect(Fx.shootSmokeSquareSparse, AquaFx.pentagonShootSmoke, AquaFx.fomentShootSmoke);
        weaveMag = 2;
        homingPower = 0.01f;
        homingDelay = 10;
        pierce = true;
        pierceCap = 2;
        pierceDamageFactor = 0.5f;
        weaveScale = 1.75f;
        shrinkX = 0.2f;
        shrinkY = 0.8f;
        reloadMultiplier = 0.7f;
        ammoMultiplier = 3;
        frontColor = lightColor = hitColor = Color.white;
        backColor = trailColor = Color.valueOf("8d706a");
        hitEffect = despawnEffect = AquaFx.fomentHitColor;
    }},
    fomentBrass = new MissileBulletType(3.2f, 120, "bullet") {{
        width = 9f;
        height = 15f;
        trailLength = 10;
        lifetime = 60f;
        reloadMultiplier = 0.4f;
        rangeChange = 32;
        trailEffect = Fx.none;
        shootEffect = AquaFx.shootHori;
        smokeEffect = new MultiEffect(Fx.shootSmokeSquareSparse, AquaFx.pentagonShootSmoke, AquaFx.fomentShootSmoke);
        weaveMag = 2;
        homingPower = 0.01f;
        homingDelay = 10;
        weaveScale = 1.75f;
        shrinkX = 0.2f;
        shrinkY = 0.8f;
        reloadMultiplier = 1.1f;
        ammoMultiplier = 5;
        frontColor = lightColor = hitColor = Color.white;
        backColor = trailColor = Pal.accent;
        hitEffect = despawnEffect = AquaFx.fomentHitColor;
    }},
    fomentFerrosilicon = new MissileBulletType(7.5f, 190, "bullet") {{
        width = 12f;
        height = 18f;
        rangeChange = 16;
        trailLength = 8;
        lifetime = 60f;
        reloadMultiplier = 0.9f;
        pierce = true;
        pierceCap = 3;
        pierceDamageFactor = 0.8f;
        ammoMultiplier = 5;
        trailEffect = Fx.none;
        shootEffect = AquaFx.shootHori;
        hitEffect = despawnEffect = AquaFx.fomentHitColor;
        smokeEffect = new MultiEffect(AquaFx.pentagonShootSmoke, Fx.shootSmokeSquareSparse, AquaFx.fomentShootSmoke);
        weaveMag = 0;
        homingPower = 0.00f;
        weaveScale = 0f;
        shrinkX = 0.2f;
        knockback = 4;
        shrinkY = 0.8f;
        frontColor = lightColor = hitColor = Color.white;
        backColor = trailColor = Color.valueOf("98a1ab");
    }},
    graceSilicon = new ArtilleryBulletType(3, 45) {{
        width = 8;
        height = 10;
        status = shocked;
        statusDuration = 10 * 60;
        buildingDamageMultiplier = 0.1f;
        trailLength = 12;
        trailEffect = Fx.mineSmall;
        trailInterval = 5;
        frontColor = Color.white;
        shootEffect = AquaFx.shootGrace;
        hitColor = backColor = lightColor = trailColor = silicon.color;
        splashDamage = 45f;
        splashDamageRadius = 3f * 8f;
        despawnSound = AquaSounds.electricExplosion;
        despawnEffect = hitEffect = AquaFx.graceExplosion;
        parts.addAll(new FlarePart(){{
            sides = 4;
            color1 = frontColor;
            color2 = frontColor;
            radius = 0;
            radiusTo = 10f;
            progress = PartProgress.life.curve(bounce).inv();
        }});
    }},
    graceMagnesium = new ArtilleryBulletType(2.5f, 80) {{
        width = 8;
        height = 10;
        status = blasted;
        buildingDamageMultiplier = 0.1f;
        statusDuration = 10 * 60;
        trailLength = 12;
        trailEffect = Fx.mineSmall;
        trailInterval = 5;
        shootEffect = AquaFx.shootGrace;
        frontColor = Color.white;
        hitColor = backColor = lightColor = trailColor = Color.gray;
        splashDamage = 75f;
        splashDamageRadius = 5 * 8f;
        despawnSound = AquaSounds.electricExplosion;
        despawnEffect = hitEffect = AquaFx.graceExplosion;
    }},
    graceCopper = new ArtilleryBulletType(6f, 35) {{
        width = 8;
        height = 10;
        trailLength = 12;
        status = electrified;
        statusDuration = 10 * 60;
        buildingDamageMultiplier = 0.1f;
        shootEffect = AquaFx.shootGrace;
        trailEffect = Fx.mineSmall;
        trailInterval = 5;
        frontColor = Color.white;
        ammoMultiplier = 2;
        hitColor = backColor = lightColor = trailColor = copper.color;
        splashDamage = 35f;
        splashDamageRadius = 3.5f * 8f;
        despawnSound = AquaSounds.electricExplosion;
        despawnEffect = hitEffect = AquaFx.graceExplosion;
    }},
    confrontLead = new BasicBulletType(6, 32, "bullet") {
        {
            width = 10f;
            height = 16f;
            trailLength = 12;
            lifetime = 50f;
            ammoMultiplier = 1;
            shootEffect = AquaFx.shootLong;
            smokeEffect = Fx.smoke;
            trailEffect = Fx.none;
            despawnEffect = hitEffect = AquaFx.fomentHitColor;
            shrinkX = 0.2f;
            shrinkY = 0.8f;
            frontColor = lightColor = hitColor = Color.white;
            backColor = trailColor = Color.valueOf("8d70ab");
        }},
    confrontFerricMatter = new BasicBulletType(6, 40, "bullet") {{
        width = 10f;
        height = 18f;
        trailLength = 10;
        lifetime = 50f;
        ammoMultiplier = 1;
        shootEffect = AquaFx.shootLong;
        smokeEffect = Fx.smoke;
        trailEffect = Fx.none;
        shrinkX = 0.2f;
        shrinkY = 0.8f;
        frontColor = lightColor = hitColor = Color.white;
        backColor = trailColor = AquaItems.ferricMatter.color;
        splashDamage = 25;
        splashDamageRadius = 8 * 3;
        hitEffect = despawnEffect = new ExplosionEffect() {{
            smoke = smokeLight;
            sparks = 12;
            sparkLen = 4;
            smokeRad = 2;
            smokes = 5;
            waveLife = 10;
            waveColor = Pal.accent;
            sparkColor = Pal.accent;
            waveRad = 8;
        }};
    }},
    confrontBrass = new BasicBulletType(6, 25, "bullet") {{
        width = 10f;
        height = 18f;
        trailLength = 10;
        lifetime = 50f;
        ammoMultiplier = 1;
        reloadMultiplier = 2f;
        shootEffect = AquaFx.shootLong;
        smokeEffect = Fx.smoke;
        trailEffect = Fx.none;
        despawnEffect = hitEffect = AquaFx.fomentHitColor;
        shrinkX = 0.2f;
        shrinkY = 0.8f;
        frontColor = lightColor = hitColor = Color.white;
        backColor = trailColor = AquaItems.brass.color;
    }},
    redactCuprite = new BasicBulletType(6f, 50, "missile-large") {{
        width = 8f;
        pierce = true;
        height = 14f;
        splashDamage = 20;
        homingPower = 0.001f;
        splashDamageRadius = 8;
        trailLength = 25;
        trailWidth = 2.5f;
        lifetime = 55f;
        reloadMultiplier = 2f;
        ammoMultiplier = 2;
        shootEffect = new Effect(15, e -> {
            color(e.color);
            float w = 1.5f + 9 * e.fout();

            Drawf.tri(e.x, e.y, w, 60f * e.fout(), e.rotation);
            color(e.color);

            Drawf.tri(e.x, e.y, w * 1.2f, 90f * e.fout(), e.rotation * 45f);
            Drawf.tri(e.x, e.y, w, 60f * e.fout(), e.rotation + 180 - 45f);
        });
        smokeEffect = new MultiEffect(new Effect(110f, e -> {
            color(e.color, e.fin());
            rand.setSeed(e.id);
            for (int i = 0; i < 7; i++) {
                float rot = e.rotation + rand.range(40f);
                v.trns(rot, rand.random(e.finpow() * 8f));
                float randomRotationSpeed = rand.random(0f, 180f);
                float slowRotation = Interp.pow2In.apply(randomRotationSpeed * e.fout());
                Fill.poly(e.x + v.x, e.y + v.y, 5, e.fout() * 5f, rand.random(700f) + slowRotation);
            }
        }), Fx.colorSpark);
        trailRotation = true;
        trailEffect = new Effect(60f, e -> {
            color(e.color, e.fin());
            rand.setSeed(e.id);
            for (int i = 0; i < 5; i++) {
                float rot = e.rotation + rand.range(15f);
                v.trns(rot, rand.random(e.finpow() * 8f));
                float randomRotationSpeed = rand.random(0f, 180f);
                float slowRotation = Interp.pow2In.apply(randomRotationSpeed * e.fout());
                Fill.poly(e.x + v.x, e.y + v.y, 5, e.fout() * 4f, rand.random(700f) + slowRotation);
            }
        });
        trailInterval = 1;
        frontColor = lightColor = hitColor = Color.white;
        hitEffect = Fx.hitSquaresColor;
        despawnEffect = new ExplosionEffect() {{
            waveRad = 33;
            waveStroke = 6;
            waveLife = 20;
            smokes = 8;
            smokeRad = 6;
            sparks = 12;
            sparkLen = 6;
            sparkStroke = 3;
            sparkColor = AquaPal.arsenic;
            waveColor = AquaPal.arsenic;
            smoke = smokeLight;
        }};
        backColor = trailColor = Color.valueOf("e58ca0");
        fragBullets = 3;
        fragBullet = new BasicBulletType(4, 10, "missile-large") {{
            width = height = 8;
            trailLength = 8;
            frontColor = Color.white;
            backColor = trailColor = lightColor = hitColor = AquaPal.arsenic;
            despawnEffect = new ExplosionEffect() {{
                waveRad = 10;
                waveStroke = 6;
                waveLife = 10;
                smokes = 3;
                smokeRad = 2;
                sparks = 6;
                sparkLen = 3;
                sparkStroke = 2;
                sparkColor = AquaPal.arsenic;
                waveColor = AquaPal.arsenic;
                smoke = smokeLight;
            }};
        }};
    }},
    redactZinc = new BasicBulletType(7f, 55, "missile-large") {{
        width = 15f;
        pierce = true;
        pierceCap = 2;
        height = 19f;
        trailLength = 34;
        trailWidth = 3;
        lifetime = 60f;
        rangeChange = -5 * 8f;
        ammoMultiplier = 1;
        homingPower = 0.001f;
        shootEffect = new Effect(10, e -> {
            color(e.color);
            float w = 1.5f + 9 * e.fout();

            Drawf.tri(e.x, e.y, w, 60f * e.fout(), e.rotation);
            color(e.color);

            for (int i : Mathf.signs) {
                Drawf.tri(e.x, e.y, w * 1.2f, 90f * e.fout(), e.rotation + i * 45f);
            }

            Drawf.tri(e.x, e.y, w, 60f * e.fout(), e.rotation + 180 - 45f);
        });
        smokeEffect = new MultiEffect(AquaFx.pentagonShootSmoke, Fx.colorSpark);
        trailRotation = true;
        trailEffect = AquaFx.pentagonShootSmoke;
        trailInterval = 2;
        frontColor = lightColor = hitColor = Color.white;
        hitEffect = Fx.hitSquaresColor;
        despawnEffect = new Effect(17f, e -> {
            color(e.color);
            Drawf.tri(e.x, e.y, e.fout() * 1.7f, 12f, e.rotation);
        });
        backColor = trailColor = AquaItems.zinc.color;

    }},
    redactAluminum = new BasicBulletType(5f, 155, "missile-large") {{
        width = 15f;
        pierce = false;
        height = 19f;
        trailLength = 34;
        trailWidth = 3;
        homingPower = 0.001f;
        lifetime = 60f;
        reloadMultiplier = 0.7f;
        ammoMultiplier = 1;
        shootEffect = new Effect(10, e -> {
            color(e.color);
            float w = 1.5f + 9 * e.fout();

            Drawf.tri(e.x, e.y, w, 60f * e.fout(), e.rotation);
            color(e.color);

            Drawf.tri(e.x, e.y, w * 1.2f, 90f * e.fout(), e.rotation * 45f);
            Drawf.tri(e.x, e.y, w, 60f * e.fout(), e.rotation + 180 - 45f);
        });
        smokeEffect = new MultiEffect(new Effect(90f, e -> {
            color(e.color, e.fin());
            rand.setSeed(e.id);
            for (int i = 0; i < 6; i++) {
                float rot = e.rotation + rand.range(40f);
                v.trns(rot, rand.random(e.finpow() * 8f));
                float randomRotationSpeed = rand.random(0f, 180f);
                float slowRotation = Interp.pow2In.apply(randomRotationSpeed * e.fout());
                Fill.poly(e.x + v.x, e.y + v.y, 5, e.fout() * 5f, rand.random(700f) + slowRotation);
            }
        }), Fx.colorSpark);
        trailRotation = true;
        trailEffect = new Effect(80f, e -> {
            color(e.color, e.fin());
            rand.setSeed(e.id);
            for (int i = 0; i < 3; i++) {
                float rot = e.rotation + rand.range(15f);
                v.trns(rot, rand.random(e.finpow() * 8f));
                float randomRotationSpeed = rand.random(0f, 180f);
                float slowRotation = Interp.pow2In.apply(randomRotationSpeed * e.fout());
                Fill.poly(e.x + v.x, e.y + v.y, 5, e.fout() * 4f, rand.random(700f) + slowRotation);
            }
        });
        trailInterval = 2;
        frontColor = lightColor = hitColor = Color.white;
        hitEffect = Fx.hitSquaresColor;
        despawnEffect = new Effect(17f, e -> {
            color(e.color);
            Drawf.tri(e.x, e.y, e.fout() * 1.7f, 12f, e.rotation);
        });
        backColor = trailColor = Color.valueOf("a3bbc8");
    }},
    redactSteel = new BasicBulletType(4f, 350f, "missile-large") {{
        width = 15f;
        pierce = false;
        height = 19f;
        splashDamage = 50;
        homingPower = 0.001f;
        splashDamageRadius = 16;
        trailLength = 34;
        trailWidth = 3;
        lifetime = 60f;
        reloadMultiplier = 0.7f;
        ammoMultiplier = 1;
        shootEffect = new Effect(10, e -> {
            color(e.color);
            float w = 1.5f + 9 * e.fout();

            Drawf.tri(e.x, e.y, w, 60f * e.fout(), e.rotation);
            color(e.color);

            Drawf.tri(e.x, e.y, w * 1.2f, 90f * e.fout(), e.rotation * 45f);
            Drawf.tri(e.x, e.y, w, 60f * e.fout(), e.rotation + 180 - 45f);
        });
        smokeEffect = new MultiEffect(new Effect(90f, e -> {
            color(e.color, e.fin());
            rand.setSeed(e.id);
            for (int i = 0; i < 6; i++) {
                float rot = e.rotation + rand.range(40f);
                v.trns(rot, rand.random(e.finpow() * 8f));
                float randomRotationSpeed = rand.random(0f, 180f);
                float slowRotation = Interp.pow2In.apply(randomRotationSpeed * e.fout());
                Fill.poly(e.x + v.x, e.y + v.y, 5, e.fout() * 5f, rand.random(700f) + slowRotation);
            }
        }), Fx.colorSpark);
        trailRotation = true;
        trailEffect = new Effect(80f, e -> {
            color(e.color, e.fin());
            rand.setSeed(e.id);
            for (int i = 0; i < 3; i++) {
                float rot = e.rotation + rand.range(15f);
                v.trns(rot, rand.random(e.finpow() * 8f));
                float randomRotationSpeed = rand.random(0f, 180f);
                float slowRotation = Interp.pow2In.apply(randomRotationSpeed * e.fout());
                Fill.poly(e.x + v.x, e.y + v.y, 5, e.fout() * 4f, rand.random(700f) + slowRotation);
            }
        });
        trailInterval = 2;
        frontColor = lightColor = hitColor = Color.white;
        hitEffect = Fx.hitSquaresColor;
        despawnEffect = new Effect(17f, e -> {
            color(e.color);
            Drawf.tri(e.x, e.y, e.fout() * 1.7f, 12f, e.rotation);
        });
        backColor = trailColor = Color.valueOf("f3efa7");
    }},
    focusTowanite = new LaserBulletType() {{
        length = 400;
        damage = 350;
        shootEffect = Fx.shootTitan;
        smokeEffect = AquaFx.GyreShootSmoke;
        hitEffect = Fx.blastExplosion;
        colors = new Color[]{
                Color.valueOf("fffe27"),
                Color.valueOf("e3ba0d").a(0.8f),
                Color.valueOf("a5340d").a(0.5f),
                Color.valueOf("5d0303").a(0.2f),
        };
    }},
    focusAcuminite = new LaserBulletType() {{
        length = 250;
        damage = 180;
        sideAngle = 45;
        rangeChange = -150;
        reloadMultiplier = 2f;
        shootEffect = Fx.shootTitan;
        smokeEffect = AquaFx.GyreShootSmoke;
        hitEffect = Fx.blastExplosion;
        colors = new Color[]{
                Color.valueOf("fffe27"),
                Color.valueOf("e3ba0d").a(0.8f),
                Color.valueOf("a5340d").a(0.5f),
                Color.valueOf("5d0303").a(0.2f),
        };
    }},
    focusAzurite = new LaserBulletType() {{
        length = 510;
        damage = 550;
        reloadMultiplier = 0.25f;
        sideAngle = 45;
        rangeChange = 90;
        shootEffect = Fx.shootTitan;
        smokeEffect = AquaFx.GyreShootSmoke;
        hitEffect = Fx.blastExplosion;
        colors = new Color[]{

                Color.valueOf("6d5fff"),
                Color.valueOf("7a35da").a(0.8f),
                Color.valueOf("621a8b").a(0.5f),
                Color.valueOf("6b0e6a").a(0.2f),
        };
    }},
    maelstromFumes = new MissileBulletType(7, 10) {{
        knockback = 2f;
        drag = -0.02f;
        lifetime = 25;
        trailLength = 18;
        trailWidth = 2;
        weaveScale = 4;
        weaveMag = 2;
        splashDamage = 30f;
        splashDamageRadius = 24f;
        homingPower = 0.09f;
        collidesTiles = false;
        ammoMultiplier = 0.05f;
        collidesGround = false;
        shootEffect = Fx.shootSmokeSquareSparse;
        backColor = trailColor = hitColor = lightColor = fumes.color;
        frontColor = Color.white;
        hitEffect = Fx.hitSquaresColor;
        layer = Layer.bullet - 2f;
        backSprite = "aquarion-star-bullet";
        sprite = "aquarion-star-bullet";
        hitEffect = new Effect(60f * 1.2f, 250f, e -> {
            color(fumes.color, 0.65f);

            randLenVectors(e.id, 10, 25f, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 4f * Mathf.clamp(e.fin() / 0.1f) * Mathf.clamp(e.fout() / 0.1f));
            });
        });
        fragBullet = new EmptyBulletType() {{
            lifetime = 60f;
            bulletInterval = 10f;
            hitEffect = Fx.none;
            despawnEffect = Fx.none;
            intervalBullet = new EmptyBulletType() {{
                splashDamage = 5f;
                collidesGround = false;
                collidesAir = true;
                collides = false;
                hitEffect = Fx.none;
                pierce = true;
                despawnEffect = Fx.none;
                instantDisappear = true;
                splashDamageRadius = 20f;
                buildingDamageMultiplier = 0.2f;
            }};
        }};
    }},
    maelstromArgon = new FlakBulletType(9, 15) {{
        knockback = 2f;
        drag = -0.02f;
        splashDamage = 25f;
        ammoMultiplier = 0.1f;
        splashDamageRadius = 18f;
        lifetime = 20;
        trailLength = 20;
        trailWidth = 2;
        weaveScale = 4;
        weaveMag = 2;
        homingPower = 0.04f;
        collidesTiles = false;
        collidesGround = false;
        shootEffect = Fx.shootSmokeSquareSparse;
        backColor = trailColor = hitColor = lightColor = argon.color;
        frontColor = Color.white;
        hitEffect = Fx.hitSquaresColor;
        layer = Layer.bullet - 2f;
        backSprite = "aquarion-star-bullet";
        sprite = "aquarion-star-bullet";
        fragBullets = 6;
        fragBullet = new BasicBulletType(3f, 4) {{
            width = 5f;
            height = 12f;
            shrinkY = 1f;
            lifetime = 20f;
            backColor = trailColor = Color.white;
            hitColor = frontColor = argon.color;
            despawnEffect = Fx.none;
            collidesGround = false;
        }};
    }},
    maelstromFluorine = new BasicBulletType(9, 15) {{
        knockback = 2f;
        drag = -0.02f;
        splashDamage = 30f * 1.5f;
        splashDamageRadius = 24f;
        lifetime = 20;
        trailLength = 20;
        trailWidth = 2;
        weaveScale = 4;
        ammoMultiplier = 0.05f;
        reloadMultiplier = 0.5f;
        weaveMag = 2;
        homingPower = 0.04f;
        collidesTiles = false;
        collidesGround = false;
        shootEffect = Fx.shootSmokeSquareSparse;
        backColor = trailColor = hitColor = lightColor = fluorine.color;
        frontColor = Color.white;
        hitEffect = Fx.hitSquaresColor;
        layer = Layer.bullet - 2f;
        backSprite = "aquarion-star-bullet";
        sprite = "aquarion-star-bullet";
        bulletInterval = 8;
        intervalBullets = 3;
        intervalDelay = 5;
        intervalBullet = new BasicBulletType(3, 5) {{
            weaveMag = 2;
            weaveScale = 15;
            collidesTiles = false;
            collidesGround = false;
            homingPower = 0.01f;
            frontColor = Color.white;
            backColor = trailColor = lightColor = hitColor = fluorine.color;
            width = height = 4;
            lifetime = 20;
            shrinkX = shrinkY = 0;
            trailLength = 8;
            backSprite = "aquarion-star-bullet";
            sprite = "aquarion-star-bullet";
            despawnEffect = hitEffect = new ExplosionEffect() {{
                waveColor = fluorine.color;
                waveStroke = 2;
                waveRad = 5;
                smokes = 0;
                waveLife = 25;
                sparkStroke = 1;
                sparkLen = 3;
                sparkColor = Color.white;
            }};
        }};
        despawnEffect = hitEffect = new ExplosionEffect() {{
            waveColor = fluorine.color;
            waveStroke = 4;
            waveRad = 10;
            waveLife = 25;
            sparkStroke = 2;
            sparkLen = 6;
            smokes = 3;
            smokeSize = 4;
            smoke = Color.gray;
            sparkColor = Color.white;
        }};
    }},
    ensignLead = new RailBulletType() {{
        length = 155f;
        damage = 45f;
        smokeEffect = Fx.colorSpark;
        hitColor = Color.valueOf("b397f0");
        hitEffect = endEffect = Fx.hitBulletColor;
        pierceDamageFactor = 0.6f;

        shootEffect = new Effect(10, e -> {
            color(e.color);
            float w = 1.2f + 7 * e.fout();

            Drawf.tri(e.x, e.y, w, 30f * e.fout(), e.rotation);
            color(e.color);

            for (int i : Mathf.signs) {
                Drawf.tri(e.x, e.y, w * 1.1f, 18f * e.fout(), e.rotation + i * 90f);
            }

            Drawf.tri(e.x, e.y, w, 4f * e.fout(), e.rotation + 180f);
        });

        lineEffect = new Effect(20f, e -> {
            if (!(e.data instanceof Vec2 v)) return;

            color(e.color);
            stroke(e.fout() * 0.9f + 0.6f);

            Fx.rand.setSeed(e.id);
            for (int i = 0; i < 7; i++) {
                Fx.v.trns(e.rotation, Fx.rand.random(8f, v.dst(e.x, e.y) - 8f));
                Lines.lineAngleCenter(e.x + Fx.v.x, e.y + Fx.v.y, e.rotation + e.finpow(), e.foutpowdown() * 20f * Fx.rand.random(0.5f, 1f) + 0.3f);
            }

            e.scaled(14f, b -> {
                stroke(b.fout() * 1.5f);
                color(e.color);
                Lines.line(e.x, e.y, v.x, v.y);
            });
        });
    }},
    ensignNickel = new RailBulletType() {{
        length = 160f;
        damage = 60f;
        knockback = 0.5f;
        smokeEffect = Fx.colorSpark;
        hitColor = Color.valueOf("f7e7be");
        final Color[] col = {Color.valueOf("f9350f")};
        col[0] = Color.valueOf("ff956e");
        hitEffect = endEffect = Fx.hitBulletColor;
        pierceDamageFactor = 0.8f;

        shootEffect = new Effect(13, e -> {
            color(e.color);
            float w = 1.4f + 7 * e.fout();

            Drawf.tri(e.x, e.y, w, 35f * e.fout(), e.rotation);
            color(e.color);

            for (int i : Mathf.signs) {
                Drawf.tri(e.x, e.y, w * 1.2f, 22f * e.fout(), e.rotation + i * 90f);
            }

            Drawf.tri(e.x, e.y, w, 5f * e.fout(), e.rotation + 180f);
        });

        lineEffect = new Effect(25f, e -> {
            if (!(e.data instanceof Vec2 v)) return;

            color(e.color);
            stroke(e.fout() * 1.1f + 0.8f);

            Fx.rand.setSeed(e.id);
            for (int i = 0; i < 9; i++) {
                Fx.v.trns(e.rotation, Fx.rand.random(9f, v.dst(e.x, e.y) - 7f));
                Lines.lineAngleCenter(e.x + Fx.v.x, e.y + Fx.v.y, e.rotation + e.finpow(), e.foutpowdown() * 20f * Fx.rand.random(0.5f, 1f) + 0.3f);
            }

            e.scaled(18f, b -> {
                stroke(b.fout() * 1.7f);
                color(e.color);
                Lines.line(e.x, e.y, v.x, v.y);
            });
        });
    }},
    ensignChalkalloy = new RailBulletType() {{
        length = 165f;
        damage = 65f;
        smokeEffect = Fx.colorSpark;
        hitColor = Color.valueOf("fdff84");
        hitEffect = endEffect = Fx.hitBulletColor;
        pierceDamageFactor = 1.1f;
        fragBullets = 3;
        fragRandomSpread = 5;
        fragAngle = 0;
        fragOnHit = true;
        fragVelocityMax = 1.1f;
        fragLifeMin = 0.9f;
        fragSpread = 2;
        fragBullet = new BasicBulletType(2.5f, 20) {{
            lifetime = 15;
            hitColor = Color.valueOf("fdff84");
            hitEffect = endEffect = Fx.hitBulletColor;
            frontColor = Color.white;
            backColor = trailColor = lightColor = Color.valueOf("fdff84");
            trailLength = 9;
            trailWidth = 2f;
            width = 8;
            height = 12;
            shrinkX = 0.9f;
        }};
        final Color[] col = {Color.valueOf("f9350f")};
        col[0] = Color.valueOf("fdff84");

        shootEffect = new Effect(10, e -> {
            color(e.color);
            float w = 1.2f + 7 * e.fout();

            Drawf.tri(e.x, e.y, w, 30f * e.fout(), e.rotation);
            color(e.color);

            for (int i : Mathf.signs) {
                Drawf.tri(e.x, e.y, w * 1.1f, 18f * e.fout(), e.rotation + i * 90f);
            }

            Drawf.tri(e.x, e.y, w, 4f * e.fout(), e.rotation + 180f);
        });

        lineEffect = new Effect(20f, e -> {
            if (!(e.data instanceof Vec2 v)) return;

            color(e.color);
            stroke(e.fout() * 0.9f + 0.6f);

            Fx.rand.setSeed(e.id);
            for (int i = 0; i < 7; i++) {
                Fx.v.trns(e.rotation, Fx.rand.random(8f, v.dst(e.x, e.y) - 8f));
                Lines.lineAngleCenter(e.x + Fx.v.x, e.y + Fx.v.y, e.rotation + e.finpow(), e.foutpowdown() * 20f * Fx.rand.random(0.5f, 1f) + 0.3f);
            }

            e.scaled(14f, b -> {
                stroke(b.fout() * 1.5f);
                color(e.color);
                Lines.line(e.x, e.y, v.x, v.y);
            });
        });
    }},
    hackChalkalloy = new BasicBulletType(3, 9) {{
        frontColor = Color.white;
        backColor = trailColor = Color.valueOf("667fba");
        lightColor = Color.valueOf("667fba");
        trailLength = 9;
        width = 9;
        height = 12;
        ammoMultiplier = 2;
        shootEffect = Fx.shootSmokeSquareSparse;
        hitEffect = despawnEffect = Fx.hitSquaresColor;
    }},
    hackNickel = new BasicBulletType(2.5f, 6) {{
        frontColor = Color.white;
        backColor = trailColor = Color.valueOf("ffbaba");
        lightColor = Color.valueOf("ffbaba");
        trailLength = 9;
        splashDamage = 3;
        splashDamageRadius = 10.5f;
        width = 9;
        height = 14;
        reloadMultiplier = 1.25f;
        ammoMultiplier = 1;
        knockback = 1;
        shootEffect = Fx.shootSmokeSquareSparse;
        hitEffect = despawnEffect = Fx.hitSquaresColor;
    }},
    hackSurgeAlloy = new BasicBulletType(4f, 30) {{
        frontColor = Color.white;
        backColor = trailColor = Color.valueOf("f5e459");
        lightColor = Color.valueOf("f5e459");
        trailLength = 12;
        splashDamage = 12;
        pierce = true;
        pierceCap = 3;
        pierceDamageFactor = 0.9f;
        pierceBuilding = true;
        ammoMultiplier = 5;
        knockback = 4;
        reloadMultiplier = 0.8f;
        splashDamageRadius = 18f;
        width = 12;
        height = 16;
        shootEffect = Fx.shootSmokeSquareSparse;
        hitEffect = despawnEffect = Fx.hitSquaresColor;
    }},
    clobberLead = new BasicBulletType(4,140){{
        width = 10;
        height = 12;
        trailLength = 8;
        hitSize = 8;
        despawnShake = 0.25f;
        despawnSound = explosion;
        hitSound = explosion;
        despawnEffect = hitEffect = Fx.hitBulletBig;
        smokeEffect = Fx.shootBigSmoke;
        ammoMultiplier = 1;
        shootEffect = Fx.shootSmokeSquareSparse;
        frontColor = Color.white;
        backColor = trailColor = lightColor = hitColor = lead.color;
    }},
    clobberChalkalloy = new BasicBulletType(4,200){{
        width = 10;
        height = 12;
        trailLength = 8;
        hitSize = 8;
        despawnShake = 0.25f;
        ammoMultiplier = 1;
        reloadMultiplier = 0.8f;
        despawnSound = explosion;
        hitSound = explosion;
        despawnEffect = hitEffect = Fx.hitBulletBig;
        smokeEffect = Fx.shootBigSmoke;
        shootEffect = Fx.shootSmokeSquareSparse;
        frontColor = Color.white;
        backColor = trailColor = lightColor = hitColor = chalkalloy.color;
    }},
    mayhemOil = new BasicBulletType(1.5f, 15){{
        sprite = "circle";
        despawnHit = true;
        scaleLife = true;
        puddleLiquid = oil;
        makeFire = true;
        width = height = 5;
        puddles = 3;
        puddleRange = 12;
        lifetime = 110;
        puddleAmount = 10;
        status = burning;
        frontColor = backColor = trailColor = lightColor = hitColor = Pal.lighterOrange;
        trailInterval = 2;
        trailLength = 9;
        hitEffect = Fx.fireHit;
        shootEffect = Fx.fireSmoke;
        trailEffect = new MultiEffect(Fx.ballfire, Fx.fireballsmoke);
    }},
    illustrateMetaglass = new EmptyBulletType(){{
        instantDisappear = true;
        fragOffsetMin = 0;
        fragOffsetMax = 0;
        fragRandomSpread = 15f;
        fragLifeMin = 1;
        fragLifeMax = 1;
        fragVelocityMax = 1.1f;
        fragVelocityMin = 1;
        fragBullets = 4;
        ammoMultiplier = 4;
        fragBullet = new BasicBulletType(5, 25){{
            trailEffect = Fx.disperseTrail;
            trailInterval = 4f;
            trailWidth = 1.5f;
            trailLength = 6;
            trailColor = Color.white;
            trailRotation = true;
            rotationOffset = 90f;
            sprite = "large-bomb-back";
            width = height = 6;
            lifetime = 56;
            splashDamage = 25;
            splashDamageRadius = 16;
            frontColor = Color.white;
            backColor = hitColor = lightColor = Pal.gray;
            collidesGround = false;
            //velocityRnd = 0.1f;
        }};
    }},
    illustrateFerricMatter = new EmptyBulletType(){{
        instantDisappear = true;
        fragOffsetMin = 0;
        fragOffsetMax = 0;
        fragRandomSpread = 5f;
        fragLifeMin = 1;
        fragLifeMax = 1;
        fragVelocityMax = 1.1f;
        fragVelocityMin = 1;
        fragBullets = 2;
        ammoMultiplier = 6;
        fragBullet = new BasicBulletType(10f, 70){
            {
                trailEffect = Fx.disperseTrail;
                trailInterval = 2f;
                trailWidth = 1.1f;
                trailLength = 6;
                trailColor = frontColor = AquaPal.ferricMatter;
                trailRotation = true;
                rotationOffset = 90f;
                sprite = "large-bomb-back";
                width = height = 8;
                //velocityRnd = 0.2f;
                lifetime = 28;
                backColor = hitColor = lightColor = AquaPal.cupronickel;
                collidesGround = false;
            }};
    }},
    illustratePlastanium = new EmptyBulletType(){{
        instantDisappear = true;
        fragOffsetMin = 0;
        fragOffsetMax = 0;
        fragRandomSpread = 15f;
        fragLifeMin = 1;
        fragLifeMax = 1;
        fragVelocityMax = 1.1f;
        fragVelocityMin = 1;
        fragBullets = 5;
        ammoMultiplier = 3;
        fragBullet = new BasicBulletType(6, 30){{
            trailEffect = Fx.disperseTrail;
            trailInterval = 2f;
            trailWidth = 1f;
            trailLength = 6;
            trailColor = frontColor = Pal.berylShot;
            trailRotation = true;
            rotationOffset = 90f;
            sprite = "large-bomb-back";
            width = height = 6;
            //velocityRnd = 0.1f;
            lifetime = 47;
            splashDamage = 45;
            splashDamageRadius = 24;
            backColor = hitColor = lightColor = Pal.plastanium;
            collidesGround = false;
        }};
    }},
    neoplasmGlob = new NeoplasmGlobBulletType(3f, 0){{
        width = 8;
        height = 8;
        hitSize = 6;
        shrinkY = 0;
        shrinkX = 0;
        lifetime = 120f;
        drag = 0f;
        blobAmount = 80f;
        ammoMultiplier = 1;
        scaleLife = true;
        range = 100f;
        sprite = "circle-bullet";
        hitEffect = despawnEffect = AquaFx.neoplasiaPlace;
        frontColor = Color.valueOf("cf5a3b");
        backColor = Color.valueOf("701e1e");
    }};
}
