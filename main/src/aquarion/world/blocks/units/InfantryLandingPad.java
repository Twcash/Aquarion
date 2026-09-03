package aquarion.world.blocks.units;

import aquarion.content.AquaUnitTypes;
import arc.math.Angles;
import arc.math.Mathf;
import arc.Core;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static mindustry.Vars.tilesize;

/**
 * Infantry landing pad. Periodically drops a squad of infantry from the sky.
 * Engineers build these to set up remote outposts far from the player's base.
 */
public class InfantryLandingPad extends Block {
    public final int timerSpawn = timers++;

    public UnitType unitType = AquaUnitTypes.infantry;
    public int spawnCount = 4;
    public float spawnInterval = 10f;

    public InfantryLandingPad(String name){
        super(name);
        update = true;
        solid = false;
        sync = true;
        destructible = true;
        group = BlockGroup.logic;
        envEnabled |= Env.any;
    }
    void drop(Building b){
        if(unitType == null) return;

        int n = Math.max(1, spawnCount);
        float r = size / 2f * tilesize + unitType.hitSize + 2f;

        for(int i = 0; i < n; i++){
            float ang = i * 360f / n + Mathf.random(-6f, 6f);
            float x = b.x + Angles.trnsx(ang, r);
            float y = b.y + Angles.trnsy(ang, r);

            Unit u = unitType.spawn(b.team, x, y);
            if(u != null){
                u.rotation = b.rotdeg();
                Fx.spawn.at(x, y, 0, b.team.color);
            }
        }
    }

    public class InfantryLandingPadBuild extends Building {
        @Override
        public void updateTile(){
            if(efficiency <= 0.001f) return;
            if(timer.get(timerSpawn, spawnInterval * 60f / efficiency)){
                drop(this);
            }
        }
    }
}