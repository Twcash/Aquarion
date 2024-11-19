package aquarion.planets;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.Tmp;
import arc.util.noise.Simplex;
import mindustry.maps.generators.PlanetGenerator;

public class QeraltarPlanetGenerator extends PlanetGenerator {
    Color c1 = Color.valueOf("5057a6"), c2 = Color.valueOf("272766"), out = new Color();
    Color c3 = Color.valueOf("727be2"), c4 = Color.valueOf("#90aae4");
    Color c5 = Color.valueOf("485256"), c6 = Color.valueOf("#909fa5");
    Color c7 = Color.valueOf("45526f"), c8 = Color.valueOf("#96a2bd");
    float scl = 5f;
    float waterOffset = 0.5f; // Offset for water level (adjust as needed)

    float water = 0.5f ;
    @Override
    public float getHeight(Vec3 position){
        // Base height using noise
        float poles = Math.abs(position.y);
        float depth = Simplex.noise3d(seed, 4, 0.55f, 1.7f, position.x, position.y, position.z);
        float baseHeight = rawHeight(position);

        // Calculate final height with clamping for oceans and smooth transitions
        float height = baseHeight + poles * 0.4f - depth * 0.3f;
        return height < 0.4f ? 0.2f : Mathf.clamp(height);
    }

    @Override
    public Color getColor(Vec3 position){
        float height = getHeight(position);

        if (height <= 0.2f) {
            // Ocean color
            return c1.write(out).lerp(c2, height / 0.2f).a(0.8f);
        } else if (height <= 0.4f) {
            // Coastal/low spots near water
            return c7.write(out).lerp(c8, (height - 0.2f) / 0.2f).a(0.9f);
        } else {
            // Land color
            return c5.write(out).lerp(c6, (height - 0.4f) / 0.6f).a(1f);
        }
    }

    float rawHeight(Vec3 position){
        // Noise function for raw height data
        return Simplex.noise3d(seed, 8, 0.7f, 1f, position.x, position.y, position.z);
    }
}
