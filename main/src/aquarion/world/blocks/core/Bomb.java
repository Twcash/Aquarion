package aquarion.world.blocks.core;

import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OverlayFloor;

import static mindustry.Vars.world;

public class Bomb extends Block {
    public OverlayFloor overlayFloor;
    public Floor targetFloor;
    public Floor bannedFloor;
    public int radius = 3;
    public Bomb(String name){
        super(name);
        update = false;
        solid = true;
        destructible = true;
        buildCostMultiplier = 1f;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        if(tile.floor() == bannedFloor) return false;

        return super.canPlaceOn(tile, team, rotation);
    }

    public class bombBuild extends Building {

        @Override
        public void placed(){
            super.placed();

            int cx = tile.x;
            int cy = tile.y;

            for(int x = -radius; x <= radius; x++){
                for(int y = -radius; y <= radius; y++){

                    if(x * x + y * y > radius * radius) continue;

                    Tile other = world.tile(cx + x, cy + y);
                    if(other == null) continue;

                    if(other.floor().isLiquid) continue;


                    other.setFloor(targetFloor);

                    if(other.block() != Blocks.air){
                        other.remove();
                    }
                }
            }
//            if(overlayFloor!=null){
//                for(int x = -radius+1; x <= radius+1; x++){
//                    for(int y = -radius+1; y <= radius+1; y++){
//
//                        if(x * x + y * y > (radius+1) * (radius+1)) continue;
//
//                        Tile other = world.tile(cx + x, cy + y);
//                        if(other == null) continue;
//
//                        if(other.floor().isLiquid) continue;
//
//
//                        other.setOverlay(overlayFloor);
//
//                        if(other.block() != Blocks.air){
//                            other.remove();
//                        }
//                    }
//                }
//            }
            Sounds.explosion.at(tile.x, tile.y);
            kill();
        }
    }
}