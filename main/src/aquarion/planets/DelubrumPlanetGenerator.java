package aquarion.planets;

import arc.graphics.Color;
import arc.math.geom.Vec3;
import mindustry.maps.generators.PlanetGenerator;

public class DelubrumPlanetGenerator extends PlanetGenerator{
    
    Color ocean = Color.valueOf("#5b7dac");
    public DelubrumPlanetGenerator(){
        baseSeed = 10;
    }

    @Override
    public void getColor(Vec3 position, Color out){
        out.set(ocean);
    }

    @Override
    public float getHeight(Vec3 position){
        return 0f;
    }
}
