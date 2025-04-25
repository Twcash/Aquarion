package aquarion.world.AI;

import aquarion.blocks.ProspectorBlocks;
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
    final Block turret = Blocks.duo;
    final Block powerGen = Blocks.combustionGenerator;
    final Block powerNode = Blocks.powerNode;
    final Block wall = Blocks.copperWall;

    int buildPhase = 0; // simple phase tracking

    @Override
    public void unit(Unit unit){
        this.unit = unit;
    }

    @Override
    public void updateUnit(){
        //nullcheck for obvious reasons

        if(unit == null) return;

        if(timer.get(0, 10f)){
            switch(buildPhase){
                case 0 -> { // power generator
                    if(tryPlace(powerGen, offsetTile(0, 0))) buildPhase++;
                }
                case 1 -> { // power node near it
                    if(tryPlace(powerNode, offsetTile(2, 0))) buildPhase++;
                }
                case 2 -> { // turret in range of node
                    if(tryPlace(turret, offsetTile(5, 0))) buildPhase++;
                }
                case 3 -> { // wall all around
                    wrapWithWalls(offsetTile(0, 0), 5);
                    buildPhase++;
                }
                case 4 -> {
                    moveToNewSpot();
                    buildPhase = 0;
                }
            }
        }
    }

    Tile offsetTile(int dx, int dy){
        return world.tileWorld(unit.x + dx * tilesize, unit.y + dy * tilesize);
    }

    boolean tryPlace(Block block, Tile tile){
        if(tile == null || !tile.block().isAir()) return false;
        if(!Build.validPlace(block, unit.team, tile.x, tile.y, 0)) return false;

        unit.addBuild(new BuildPlan(tile.x, tile.y, 0, block));
        return true;
    }

    void wrapWithWalls(Tile center, int size){
        if(center == null) return;

        for(int dx = -1; dx <= size + 1; dx++){
            for(int dy = -1; dy <= 1; dy++){
                if(dx == 0 && dy == 0) continue;
                Tile t1 = world.tile(center.x + dx, center.y - 1);
                Tile t2 = world.tile(center.x + dx, center.y + 1);
                Tile t3 = world.tile(center.x - 1, center.y + dy);
                Tile t4 = world.tile(center.x + size + 1, center.y + dy);
                tryPlace(wall, t1);
                tryPlace(wall, t2);
                tryPlace(wall, t3);
                tryPlace(wall, t4);
            }
        }
    }

    void moveToNewSpot(){
        float angle = Mathf.random(360f);
        float distance = Mathf.random(40f, 80f) * tilesize;

        float x = unit.x + Angles.trnsx(angle, distance);
        float y = unit.y + Angles.trnsy(angle, distance);

        unit.moveAt(new Vec2(x, y));
    }
}