package aquarion.world.graphics.weather;

import aquarion.content.AquaWeathers;
import aquarion.world.campaign.PlanetWeather;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import mindustry.Vars;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.*;
import mindustry.type.*;

/** A cloud shell whose occupied hexes come from the campaign weather simulation. */
public class WeatherCloudMesh extends HexSkyMesh{
    private long revision = Long.MIN_VALUE;
    private final Mat3D cloudTransform = new Mat3D();

    public WeatherCloudMesh(Planet planet){
        this.planet = planet;
        this.shader = Shaders.clouds;
    }

    @Override public void render(PlanetParams params, Mat3D projection, Mat3D transform){
        long nextRevision = PlanetWeather.cloudRevision(planet);
        if(mesh == null || nextRevision != revision){
            if(mesh != null) mesh.dispose();
            mesh = MeshBuilder.buildHex(new HexMesher(){
                private Sector sector(Vec3 position){
                    return planet.sectors.min(s -> s.tile.v.dst2(position));
                }
                @Override public float getHeight(Vec3 position){ return 1f; }
                @Override public boolean skip(Vec3 position){
                    Sector sector = sector(position);
                    return PlanetWeather.effectiveWeather(planet, sector) == null || PlanetWeather.visibility(sector) < 0.02f;
                }
                @Override public void getColor(Vec3 position, Color out){
                    Sector sector = sector(position);
                    Weather weather = PlanetWeather.effectiveWeather(planet, sector);
                    if(weather == AquaWeathers.thunderstorm) out.set(0.26f, 0.31f, 0.40f, 0.72f);
                    else if(weather == AquaWeathers.monsoon) out.set(0.48f, 0.56f, 0.66f, 0.62f);
                    else if(weather == AquaWeathers.blizzard) out.set(0.88f, 0.94f, 1f, 0.65f);
                    else if(weather == AquaWeathers.sedimentDisturance) out.set(0.55f, 0.49f, 0.38f, 0.4f);
                    else out.set(0.73f, 0.83f, 0.91f, 0.38f);
                    out.a *= PlanetWeather.visibility(sector);
                }
            }, 5, planet.radius, planet.radius * 0.035f);
            revision = nextRevision;
        }
        preRender(params);
        shader.bind();
        shader.setUniformMatrix4("u_proj", projection.val);
        shader.setUniformMatrix4("u_trans", cloudTransform.setToTranslation(planet.position).rotate(Vec3.Y, planet.getRotation()).val);
        shader.apply();
        mesh.render(shader, Gl.triangles);
    }

    @Override public void preRender(PlanetParams params){
        Shaders.clouds.planet = planet;
        Shaders.clouds.lightDir.set(planet.solarSystem.position).sub(planet.position).rotate(Vec3.Y, planet.getRotation()).nor();
        Shaders.clouds.ambientColor.set(planet.solarSystem.lightColor);
        // Keep a thin cloud layer at sector zoom, where the normal sky mesh fades out.
        Shaders.clouds.alpha = params.planet == planet ? 0.55f : 0.8f;
    }

    @Override public void dispose(){ if(mesh != null) mesh.dispose(); }
}
