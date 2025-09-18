package aquarion.planets;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.Time;
import arc.util.noise.Noise;
import arc.util.noise.Simplex;
import mindustry.content.Blocks;
import mindustry.game.Rules;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Planet;
import mindustry.type.Sector;
import mindustry.world.Block;
import mindustry.world.TileGen;

public class AquaPlanetGenerator extends PlanetGenerator {

    public AquaPlanetGenerator() {
        baseSeed = 12345;
    }

    public Color makeColor(Vec3 position) {
        float t = (Simplex.noise3d(
                12, 5, 1,
                Mathf.absin(1+Time.globalTime / 10f,1,  1f),
                position.x, position.y, position.z
        ));

        return Color.valueOf("6d7cdf").lerp(Color.valueOf("6058ba"), t);
    }

    @Override
    public void getColor(Vec3 position, Color out) {
        out.set(makeColor(position));
    }
}