package aquarion.world.AI;

import aquarion.gen.Gerbc;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.gen.Building;

import static mindustry.Vars.tilesize;

/**
 * Flank behavior: swings around turret-dense defenses by approaching the enemy
 * core from an angle instead of charging straight in. Picks a per-unit side and
 * mirrors the waypoint if it lands in water or inside another turret cluster.
 */
public class GerbFlankAI extends GerbAI {
    @Override
    public void updateMovement(){
        Building core = nearestEnemyCore();
        if(core == null || !(unit instanceof Gerbc g)) return;

        //roll a flank direction once per flank (reset when leaving flank)
        if(g.flankAngle() == 0f){
            g.flankAngle((unit.id % 2 == 0 ? -1f : 1f) * Mathf.random(35f, 70f));
        }

        //pick a waypoint off the direct line to the core
        float dist = unit.dst(core);
        float outDist = Math.min(dist * 0.6f, tilesize * 20f);
        Tmp.v1.trns(unit.angleTo(core) + g.flankAngle(), outDist).add(core);

        //if that lands in water or inside a turret cluster, mirror it to the other side
        if(isWater(Tmp.v1.x, Tmp.v1.y) || turretNear(Tmp.v1.x, Tmp.v1.y, tilesize * 6f)){
            Tmp.v1.trns(unit.angleTo(core) - g.flankAngle(), outDist).add(core);
        }

        pathMoveTo(Tmp.v1, 10f);
        engageNearest(unit.range());
        faceTarget();
    }
}