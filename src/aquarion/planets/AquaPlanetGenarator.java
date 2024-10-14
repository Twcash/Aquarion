package aquarion.planets;

import aquarion.blocks.AquaEnv;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.g3d.PlanetGrid;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class AquaPlanetGenarator extends PlanetGenerator{

    Color c1 = Color.valueOf("5057a6"), c2 = Color.valueOf("272766"), out = new Color();
    Color c3 = Color.valueOf("727be2"), c4 = Color.valueOf("#90aae4");
    Color c5 = Color.valueOf("dde8ff"), c6 = Color.valueOf("#e5edff");
    Block[][] arr = {
            {Blocks.shale, AquaEnv.phylite_floor, AquaEnv.slate, Blocks.basalt, AquaEnv.basaltSpikes, AquaEnv.ferric_extrusions, Blocks.ferricStone, Blocks.dacite, AquaEnv.andesite, AquaEnv.andesiteRubble, AquaEnv.andesiteLayers}
    };

    {
        baseSeed = 1;
    }

    @Override
    public void generateSector(Sector sector){
        //no bases
    }

    @Override
    public float getHeight(Vec3 position){
        float poles = Math.abs(position.y);
        float depth = Simplex.noise3d(seed, 2, 0.56, 1.7f, position.x, position.y, position.z) / 2f;
        if (depth + poles> 1.1f) {return Mathf.pow(rawHeight(position), 0.25f) /3.5f;} else return 0;
    }
    @Override
    public Color getColor(Vec3 position){
        float depth = Simplex.noise3d(seed, 2, 0.56, 1.7f, position.x, position.y, position.z) / 2f;
        if (Math.abs(position.y) + depth> 0.98) {return c5.write(out).lerp(c6, Mathf.clamp(Mathf.round(depth, 0.25f))).a(0.6f);} else  if (Math.abs(position.y) + depth> 0.8) {return c3.write(out).lerp(c4, Mathf.clamp(Mathf.round(depth, 0.25f))).a(0.5f);}
        return c1.write(out).lerp(c2, Mathf.clamp(Mathf.round(depth, 0.15f))).a(0.2f);
    }

    @Override
    public float getSizeScl(){
        return 2000;
    }

    @Override
    public void addWeather(Sector sector, Rules rules){
        //no weather... yet
    }

    @Override
    public void genTile(Vec3 position, TileGen tile){
        tile.floor = getBlock(position);
        tile.block = tile.floor.asFloor().wall;

        if(Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, 22) > 0.31){
            tile.block = Blocks.air;
        }
        tile.floor = getBlock(position);
    }

    @Override
    protected void generate(){
        decoration(0.017f);
        pass((x, y) -> {
            float max = 0;
            for(Point2 p : Geometry.d8){
                max = Math.max(max, world.getDarkness(x + p.x, y + p.y));
            }
            if(max > 0){
                block = floor.asFloor().wall;
            }

            if(noise(x, y, 40f, 1f) > 0.9){
                //block = Blocks.coralChunk;
            }
        });

        Schematics.placeLaunchLoadout(width / 3, height / 3);
    }

    float rawHeight(Vec3 position){
        return Simplex.noise3d(seed, 8, 0.7f, 1f, position.x, position.y, position.z);
    }

    Block getBlock(Vec3 position){
        float height = rawHeight(position);
        Tmp.v31.set(position);
        position = Tmp.v33.set(position).scl(2f);
        float temp = Simplex.noise3d(seed, 8, 0.6, 1f/2f, position.x, position.y + 99f, position.z);
        height *= 1.2f;
        height = Mathf.clamp(height);

        //float tar = (float)noise.octaveNoise3D(4, 0.55f, 1f/2f, position.x, position.y + 999f, position.z) * 0.3f + Tmp.v31.dst(0, 0, 1f) * 0.2f;

        return arr[Mathf.clamp((int)(temp * arr.length), 0, arr[0].length - 1)][Mathf.clamp((int)(height * arr[0].length), 0, arr[0].length - 1)];
    }

}