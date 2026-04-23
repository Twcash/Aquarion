package aquarion.world.blocks.core;

import aquarion.content.blocks.EnvironmentBlocks;
import arc.math.Mathf;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OverlayFloor;
import mindustry.world.meta.Env;

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

                    float dst2 = x * x + y * y;
                    if(dst2 > radius * radius) continue;

                    Tile other = world.tile(cx + x, cy + y);
                    if(other == null) continue;

                    if(other.floor().isLiquid) continue;
                    Floor floor = null;
                    boolean hadOre = other.overlay() != Blocks.air && other.overlay().itemDrop != null;
                    if(hadOre) floor = other.overlay();
                    boolean protectOverlay = false;

                    if(other.overlay() == EnvironmentBlocks.oreNickel){
                        other.setOverlay(Blocks.air);
                        other.setFloor(EnvironmentBlocks.nickelFloor.asFloor());
                        protectOverlay = true;
                    }else{
                        Floor result = getExplosionResult(other);
                        other.setFloor(result);
                        if(floor!=null) other.setOverlay(floor);
                    }

                    if(other.block() != Blocks.air){
                        if(other.build != null){
                            other.build.kill();
                        }else{
                            other.remove();
                        }
                    }

                    if(!protectOverlay){
                        if(other.overlay() != Blocks.air && !hadOre){
                            other.setOverlay(Blocks.air);
                        }

                        float edgeStart = (radius - 1f) * (radius - 1f);
                        if(EnvironmentBlocks.scorche != null && dst2 >= edgeStart && Mathf.chance(0.6f)){
                            if(other.overlay() == Blocks.air || other.overlay().itemDrop == null){
                                other.setOverlay(EnvironmentBlocks.scorche);
                            }
                        }
                    }
                }
            }

            Sounds.explosion.at(tile.worldx(), tile.worldy());
            kill();
        }
        private Floor getExplosionResult(Tile tile){
            if(tile.overlay() == EnvironmentBlocks.oreNickel){
                return EnvironmentBlocks.nickelFloor.asFloor();
            }

            if(tile.floor() == EnvironmentBlocks.nickelFloor){
                return EnvironmentBlocks.nickelFloor.asFloor();
            }

            if(tile.block() != Blocks.air){
                return targetFloor;
            }

            if(tile.floor() == Blocks.ice){
                return EnvironmentBlocks.iceWater.asFloor();
            }

            if(tile.floor() == EnvironmentBlocks.iceWater){
                return Blocks.water.asFloor();
            }

            if(tile.floor() == Blocks.stone){
                if(Mathf.chanceDelta(0.5f)){
                    return Blocks.craters.asFloor();
                }
                return Blocks.charr.asFloor();
            }

            if(tile.floor().itemDrop != null){
                return tile.floor();
            }

            return targetFloor;
        }
    }
}