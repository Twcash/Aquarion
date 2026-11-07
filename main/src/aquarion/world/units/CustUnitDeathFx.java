package aquarion.world.units;

import aquarion.units.AquaLegUnitType;
import aquarion.world.graphics.AquaFx;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.RegionPart;
import mindustry.gen.Unit;
import mindustry.gen.WaterMovec;
import mindustry.graphics.Pal;

/** Particles only; dying units retain their renderer, mounts and component timer. */
public class CustUnitDeathFx {
    public static boolean sinking(Unit unit){
        return unit instanceof aquarion.gen.AquaUnitc aqua && aqua.deathSinking()
            || unit instanceof WaterMovec && !unit.type.flying && unit.floorOn().isLiquid;
    }

    public static float duration(Unit unit){
        return sinking(unit) ? 210f : unit.type instanceof AquaLegUnitType ? 180f : 0f;
    }

    public static void update(Unit unit, float timer){
        if(unit.type.flying && !sinking(unit) && Mathf.randomSeed(unit.id, 0, 3) == 0){
            unit.rotation += (unit.id % 2 == 0 ? 1f : -1f) * Time.delta * 2f;
        }
        if(Vars.headless) return;
        if(sinking(unit)){
            var floor = unit.floorOn();
            // Match ordinary drowning: use the floor's own bubble effect and emission rate.
            if(Mathf.chanceDelta(0.05f)) floor.drownUpdateEffect.at(unit.x, unit.y, unit.hitSize, floor.mapColor);
            if(Mathf.chanceDelta(0.03f)) AquaFx.waterSplash.at(unit.x + Mathf.range(unit.hitSize * 0.3f),
                unit.y + Mathf.range(unit.hitSize * 0.3f), Mathf.clamp(unit.hitSize / 20f * Mathf.random(0.7f, 1.3f), 0.4f, 2.5f), floor.mapColor);
            return;
        }
        float heat = Mathf.clamp(timer / Math.max(duration(unit), 1f));
        if(Mathf.chanceDelta(Mathf.lerp(0.06f, 0.3f, heat))){
            float x = unit.x + Mathf.range(unit.hitSize * 0.3f);
            float y = unit.y + Mathf.range(unit.hitSize * 0.3f);
            Fx.smoke.at(x, y, 0f, Color.gray);
            if(heat > 0.4f) AquaFx.spark1.at(x, y, 0f, Pal.lightOrange);
            unit.hitTime = Math.max(unit.hitTime, heat * 0.5f);
        }
    }

    public static void spawn(Unit unit){
        if(Vars.headless) return;
        AquaFx.factoryDestroy.at(unit.x, unit.y, Mathf.clamp(unit.hitSize / 16f, 0.65f, 3f));
        // Body-mounted parts use the same debris lifecycle and water impact as weapon parts.
        for(var part : unit.type.parts){
            var params = new DrawPart.PartParams().set(0f, 0f, 0f, 0f, 0f, 0f, unit.x, unit.y, unit.rotation);
            if(part.weaponIndex >= 0 && part.weaponIndex < unit.mounts.length){
                var mount = unit.mounts[part.weaponIndex];
                params.set(mount.warmup, mount.reload / Math.max(mount.weapon.reload, 0.001f),
                    mount.smoothReload, mount.heat, mount.recoil, mount.charge, unit.x, unit.y, unit.rotation);
            }
            if(unit instanceof arc.math.Scaled scaled) params.life = scaled.fin();
            scatterPart(part, params, 1f, 1f, unit.hitSize);
        }
        for(var mount : unit.mounts){
            var weapon = mount.weapon;
            float rotation = unit.rotation - 90f;
            float weaponRotation = rotation + (weapon.rotate ? mount.rotation : weapon.baseRotation);
            float recoil = Mathf.pow(mount.recoil, weapon.recoilPow) * weapon.recoil;
            float x = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0f, -recoil);
            float y = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0f, -recoil);
            scatter(weapon.region, x, y, weaponRotation, weapon.flipSprite ? -1f : 1f, 1f, unit.hitSize);
            var params = new DrawPart.PartParams().set(mount.warmup, mount.reload / Math.max(weapon.reload, 0.001f),
                mount.smoothReload, mount.heat, mount.recoil, mount.charge, x, y, weaponRotation + 90f);
            params.sideMultiplier = weapon.flipSprite ? -1 : 1;
            for(var part : weapon.parts){
                params.recoil = part.recoilIndex >= 0 && mount.recoils != null && part.recoilIndex < mount.recoils.length
                    ? mount.recoils[part.recoilIndex] : mount.recoil;
                scatterPart(part, params, 1f, 1f, unit.hitSize);
            }
        }
    }

    private static void scatter(TextureRegion region, float x, float y, float rotation, float scaleX, float scaleY, float size){
        AquaFx.scatterWeaponDebris(region, x, y, rotation, scaleX, scaleY, size);
    }

    /** Snapshot physical sprites at their current animated positions, including mirrored and nested parts. */
    private static void scatterPart(DrawPart drawPart, DrawPart.PartParams params, float parentScaleX, float parentScaleY, float size){
        if(!(drawPart instanceof RegionPart part)) return;
        float progress = part.progress.getClamp(params, part.clampProgress);
        float grow = part.growProgress.getClamp(params, part.clampProgress);
        float mx = part.moveX * progress, my = part.moveY * progress;
        float rotation = part.rotation + part.moveRot * progress;
        float gx = part.growX * grow, gy = part.growY * grow;
        for(var move : part.moves){
            float value = move.progress.getClamp(params, part.clampProgress);
            mx += move.x * value;
            my += move.y * value;
            rotation += move.rot * value;
            gx += move.gx * value;
            gy += move.gy * value;
        }
        float scaleX = parentScaleX * (part.xScl + gx), scaleY = parentScaleY * (part.yScl + gy);
        int sides = part.mirror && params.sideOverride == -1 ? 2 : 1;
        for(int side = 0; side < sides; side++){
            int index = params.sideOverride == -1 ? side : params.sideOverride;
            float sign = (index == 0 ? 1f : -1f) * params.sideMultiplier;
            float px = (part.x + mx) * sign, py = part.y + my;
            float x = params.x + Angles.trnsx(params.rotation - 90f, px, py);
            float y = params.y + Angles.trnsy(params.rotation - 90f, px, py);
            float partRotation = params.rotation + rotation * sign;
            if(part.drawRegion && part.regions.length > 0){
                scatter(part.regions[Math.min(index, part.regions.length - 1)], x, y, partRotation - 90f,
                    scaleX * sign, scaleY, size);
            }
            var childParams = new DrawPart.PartParams().set(params.warmup, params.reload, params.smoothReload,
                params.heat, params.recoil, params.charge, x, y, partRotation);
            childParams.life = params.life;
            childParams.sideMultiplier = params.sideMultiplier;
            childParams.sideOverride = index;
            for(var child : part.children) scatterPart(child, childParams, scaleX, scaleY, size);
        }
    }
}
