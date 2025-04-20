package aquarion.world.AI;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.units.AIController;
import mindustry.entities.units.BuildPlan;
import mindustry.game.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.ConstructBlock;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.storage.*;

import static mindustry.Vars.*;

public class ProspectorBaseBuilderAI extends AIController {
    Unit unit;
    final Interval timer = new Interval(1);

    final Seq<Block> turretOptions = Seq.with(
            Blocks.duo,
            Blocks.scatter,
            Blocks.hail
    );

    final Seq<Tile> placedTurrets = new Seq<>();

    @Override
    public void unit(Unit unit){
        this.unit = unit;
    }

    @Override
    public void updateUnit(){
        if(unit == null) return;

        if(timer.get(0, 10f)){
            Block turret = pickTurret();
            float range = getTurretRange(turret);

            if(turret == null || range <= 0f) return;

            Tile tile = findNextTurretTile(turret, range);

            if(tile != null){
                unit.addBuild(new BuildPlan(tile.x, tile.y, 0, turret));
            }else{
                moveToNewBuildZone();
            }
        }
    }

    Block pickTurret(){
        return turretOptions.random();
    }

    float getTurretRange(Block block){
        return block instanceof Turret ? ((Turret) block).range : 0f;
    }

    Tile findNextTurretTile(Block turret, float range){
        CoreBlock.CoreBuild core = unit.team.core();
        if(core == null) return null;

        float spacing = range * 0.9f / tilesize;
        float spacingSqr = spacing * spacing;
        int attempts = 50;

        for(int i = 0; i < attempts; i++){
            int dx = Mathf.random(-30, 30);
            int dy = Mathf.random(-30, 30);
            int tx = core.tile.x + dx;
            int ty = core.tile.y + dy;
            Tile tile = world.tile(tx, ty);
            //isAir?
            if(tile == null || !tile.block().isAir()) continue;

            boolean overlaps = false;
            //This is horribly BAD
            for(Building b : Groups.build){
                if(b.team == unit.team && b.block instanceof Turret){
                    if(Mathf.dst2(b.tile.x, b.tile.y, tile.x, tile.y) < spacingSqr){
                        overlaps = true;
                        break;
                    }
                }
            }

            if(overlaps) continue;

            return tile;
        }

        return null;
    }
    void moveToNewBuildZone(){
        CoreBlock.CoreBuild core = unit.team.core();
        if(core == null) return;

        int dx = Mathf.random(-60, 60);
        int dy = Mathf.random(-60, 60);
        float x = core.x() + dx * tilesize;
        float y = core.y() + dy * tilesize;

        unit.move(x, y);
    }
}