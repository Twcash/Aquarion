package aquarion.world.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Scl;
import arc.util.Nullable;

import mindustry.Vars;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Shaders;
import mindustry.graphics.Shaders.*;
import arc.util.Time;

import static arc.util.ArcNativesLoader.loaded;
import static mindustry.Vars.*;

public class AquaShaders {
    public static @Nullable ModSurfaceShader brine, oxygen;
    public static void load() {
        brine = new ModSurfaceShader("brine");
        oxygen = new ModSurfaceShader("oxygen");
    }

    public static class ModSurfaceShader extends Shader {
        Texture noiseTex;



        /** Shaders that get plastered on blocks, notably walls. */
        public static class BlockShader extends Shader {
            public BlockShader(String name){
                super(Core.files.internal("shaders/default.vert"),
                        tree.get("shaders/" + name + ".frag"));
            }

            @Override
            public void apply(){
                setUniformf("u_time", Time.time / Scl.scl(1f));
                //setUniformf("u_resolution", Core.camera.width, Core.camera.height);
                setUniformf("u_offset",
                        Core.camera.position.x,
                        Core.camera.position.y
                );
            }
        }
        public ModSurfaceShader(String frag) {
            super(Shaders.getShaderFi("screenspace.vert"), Vars.tree.get("shaders/" + frag + ".frag"));
            loadNoise();
        }

        public String textureName(){
            return "noise";
        }

        public void loadNoise(){
            Core.assets.load("sprites/" + textureName() + ".png", Texture.class).loaded = t -> {
                t.setFilter(Texture.TextureFilter.linear);
                t.setWrap(Texture.TextureWrap.repeat);
            };
        }

        @Override
        public void apply() {
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);
            setUniformf("u_time", Time.time);

            if(hasUniform("u_noise")){
                if(noiseTex == null) {
                    noiseTex = Core.assets.get("sprites/" + textureName() + ".png", Texture.class);
                }

                noiseTex.bind(1);
                renderer.effectBuffer.getTexture().bind(0);

                setUniformi("u_noise", 1);
            }
        }
        public static class UIShader extends Shader {
            public Vec2 pos = new Vec2();
            public float alpha = 1;

            public UIShader(String frag) {
                super(Core.files.internal("shaders/screenspace.vert"), Vars.tree.get("shaders/" + frag + ".frag"));
            }

            @Override
            public void apply() {
                setUniformf("u_resolution", Core.graphics.getWidth(), Core.graphics.getHeight());
                setUniformf("u_position", pos.x, pos.y);
                setUniformf("u_opacity", alpha);
                setUniformf("u_time", Time.globalTime);
            }
        }
    }
}