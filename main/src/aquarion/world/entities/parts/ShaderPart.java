package aquarion.world.entities.parts;

import aquarion.world.graphics.Renderer;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.Shader;
import arc.util.Time;
import mindustry.entities.part.RegionPart;
import aquarion.world.graphics.AquaShaders;

public class ShaderPart extends RegionPart {
    public Shader shader;
    @Override
    public void draw(PartParams params){
        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        if(shader != null){
            Draw.draw(z, ()->{
                Draw.shader(shader);
                if(shader instanceof AquaShaders.PartRegionShader regionShader && regions.length > 0){
                    regionShader.setRegion(regions[0]);
                    regionShader.time = Time.time;
                }
                super.draw(params);
                Draw.shader();
            });
        }
    }
}