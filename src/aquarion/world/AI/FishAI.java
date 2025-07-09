package aquarion.world.AI;

import arc.func.Prov;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.Sector;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.BlockFlag;

import static mindustry.Vars.*;

public class FishAI extends AIController {
    public Teamc following;

    @Override
    public void updateMovement(){
        if(target == null){
            unit.speedMultiplier = 0.25f;
        } else {unit.speedMultiplier = 1;}

        if(following != null && target == null){
            unit.lookAt(following);
            vec.set(following).sub(unit);
            float ang = unit.angleTo(following);
            float diff = Angles.angleDist(ang, unit.rotation());

            if(diff > 70f && vec.len() < 30){
                vec.setAngle(unit.vel().angle());
            }else{
                vec.setAngle(Angles.moveToward(unit.vel().angle(), vec.angle(), 6f));
            }

            vec.setLength(prefSpeed());
            unit.movePref(vec);
        } else if (following == null && target == null){
            Vec2 mov = new Vec2(Mathf.random(unit.x-5, unit.x+5),Mathf.random(unit.y-5, unit.y+5));
            unit.approach(mov);
        }

        if(timer.get(timerTarget3, 30f) && target == null){
            following = Units.closest(unit.team, unit.x, unit.y, Math.max(unit.type.range, 400f),
                    u -> !u.dead() && (u.controller() instanceof FishAI) && ((FishAI) u.controller()).following != this.unit);
        }
    }
    @Override
    public void updateUnit(){
        if(state.won) unit.kill();
        float groupDps = unit.type.dpsEstimate;
        int allyCount = 0;
        updateMovement();
        Teamc inRange = Units.closestTarget(unit.team, unit.x, unit.y, unit.range(),
                u -> !u.dead() && u.checkTarget(unit.type.targetAir, unit.type.targetGround),
                b -> !b.dead() && unit.dst(b) <= unit.range());

        if(inRange != null){
            target = inRange;

            updateWeapons();

            if(target instanceof Position){
                unit.lookAt(target);
                circleAttack(30);
            }

            return;
        }
        for(Unit other : Groups.unit){
            if(other.team == unit.team && other != unit && other.dst(unit) < 120f){
                if(other.controller() instanceof FishAI){
                    allyCount++;
                }
                groupDps += other.type.dpsEstimate;
            }
        }

        Building chosenTurret = null;
        float closestTurretDst = Float.MAX_VALUE;

        for(Building turret : indexer.getEnemy(unit.team, BlockFlag.turret)){
            if(turret.dead() || !(turret instanceof Turret.TurretBuild)) continue;

            float combinedDps = 0f;
            for(Building nearby : indexer.getEnemy(unit.team, BlockFlag.turret)){
                if(nearby.dead() || !(nearby instanceof Turret.TurretBuild)) continue;
                if(nearby.dst(turret) <= 100f){ // adjust radius as needed
                    combinedDps += ((Turret.TurretBuild) nearby).estimateDps();
                }
            }

            if(groupDps >= combinedDps * 1.5f){ // multiplier for safety margin
                float dst2 = unit.dst2(turret);
                if(dst2 < closestTurretDst){
                    chosenTurret = turret;
                    closestTurretDst = dst2;
                }
            }
        }

        if(chosenTurret != null){
            target = chosenTurret;
            Log.info("Target turret: " + target.x() + " " + target.y());

            updateWeapons();

            if(target != null){
                unit.lookAt(target);
                circleAttack(30);
            }

            return;
        }

        if(allyCount >= 8){
            for(Building b : indexer.getEnemy(unit.team, BlockFlag.factory)){
                if(!isDefended(b)){
                    target = b;
                    Log.info("Target factory: " + target.x() + " " + target.y());

                    updateWeapons();

                    if(target != null){
                        unit.lookAt(target);
                        circleAttack(30);
                    }

                    return;
                }
            }
        }
        target = null;
    }
    @Override
    public Teamc findTarget(float x, float y, float range, boolean air, boolean ground){

        //if the main target is in range, use it, otherwise target whatever is closest
        return target(x, y, range, air, ground);
    }

    private boolean isDefended(Building building){
        Seq<Building> turrets = indexer.getEnemy(unit.team, BlockFlag.turret);
        for(Building t : turrets){
            if(t.block instanceof Turret && !t.dead() && t.dst(building) < ((Turret)t.block).range){
                return true;
            }
        }
        return false;
    }
}