package aquarion.world.graphics;

import arc.*;
import arc.files.*;
import arc.graphics.*;
import arc.graphics.Texture.*;
import arc.graphics.gl.*;
import arc.util.*;
import mindustry.Vars;
import mindustry.graphics.*;

import static arc.Core.assets;
import static mindustry.Vars.*;
import static mindustry.graphics.Shaders.getShaderFi;


public class AquaShaders {
    public static @Nullable SurfaceShader brine, heat;
    public static CacheLayer.ShaderLayer brineLayer, heatLayer;

    public static Fi file(String name){
        return Core.files.internal("shaders/" + name);
    }

    public static void init(){

        brine = new SurfaceShader("brine");
        heat = new SurfaceShader("heat");

        heatLayer = new CacheLayer.ShaderLayer(heat);
        brineLayer = new CacheLayer.ShaderLayer(brine);
        CacheLayer.addLast(brineLayer);
        CacheLayer.add(126, heatLayer);
    }

    public static void dispose(){
        if (!Vars.headless) {
            brine.dispose();
        }
    }

    public static class SurfaceShader extends Shader{
        Texture noiseTex;

        public SurfaceShader(String frag){
            super(Shaders.getShaderFi("screenspace.vert"), tree.get("shaders/" + frag + ".frag"));
            loadNoise();
        }

        public SurfaceShader(String vertRaw, String fragRaw){
            super(vertRaw, fragRaw);
            loadNoise();
        }

        public String textureName(){
            return "noise";
        }

        public void loadNoise(){
            assets.load("sprites/" + textureName() + ".png", Texture.class).loaded = t -> {
                t.setFilter(Texture.TextureFilter.linear);
                t.setWrap(Texture.TextureWrap.repeat);
            };
        }
        @Override
        public void apply(){
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);
            setUniformf("u_time", Time.time);

            if(hasUniform("u_noise")){
                if(noiseTex == null){
                    noiseTex = assets.get("sprites/" + textureName() + ".png", Texture.class);
                }

                noiseTex.bind(1);
                renderer.effectBuffer.getTexture().bind(0);

                setUniformi("u_noise", 1);
            }
        }
    }
}