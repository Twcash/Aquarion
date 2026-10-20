package aquarion.world.units;

import aquarion.gen.Burrowc;
import aquarion.units.type.BurrowUnitType;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.Units;
import mindustry.gen.*;

import static mindustry.Vars.tilesize;

/** Shared underground orbit steering for autonomous and commanded Trawl attacks. */
public class BurrowCombat{
    public static boolean valid(Unit unit, Teamc target){
        return target != null && !Units.invalidateTarget(target, unit.team, unit.x, unit.y) &&
            (!(target instanceof Unit other) || other.isGrounded());
    }

    public static float targetRadius(Teamc target){
        return target instanceof Building build ? build.block.size * tilesize / 2f :
            target instanceof Unit unit ? unit.hitSize / 2f : 0f;
    }

    public static void move(Unit unit, Teamc target){
        if(!(unit instanceof Burrowc worm) || !(unit.type instanceof BurrowUnitType cfg) || !valid(unit, target)) return;
        worm.burrowAttackTarget(target);
        float dx = unit.x - target.x(), dy = unit.y - target.y();
        float distance = Mathf.len(dx, dy);
        if(distance < 0.001f){ dx = Mathf.cosDeg(unit.rotation); dy = Mathf.sinDeg(unit.rotation); }
        else{ dx /= distance; dy /= distance; }
        float radius = Math.max(cfg.orbitRadius, targetRadius(target) + unit.hitSize + 8f);
        float radial = Mathf.clamp((radius - distance) / Math.max(radius * 0.5f, 1f), -1f, 1f);
        float tangent = distance > radius + cfg.breachRange ? 0f : (unit.id % 2 == 0 ? 1f : -1f);
        Tmp.v3.set(dx * radial - dy * tangent, dy * radial + dx * tangent).setLength(unit.speed());
        unit.moveAt(Tmp.v3);
        if(!worm.burrowActive()) unit.lookAt(Tmp.v3.angle());
    }
}
