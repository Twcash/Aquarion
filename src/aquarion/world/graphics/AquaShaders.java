package aquarion.world.graphics;

import arc.*;
import arc.files.*;
import arc.graphics.*;
import arc.graphics.Texture.*;
import arc.graphics.gl.*;
import arc.math.geom.Vec2;
import arc.util.*;
import mindustry.Vars;
import mindustry.graphics.*;

import static aquarion.world.graphics.AquaShaders.ExtendedSurfaceShader.replaceShader;
import static arc.Core.assets;
import static mindustry.Vars.*;
import static mindustry.graphics.Shaders.getShaderFi;
import static mindustry.graphics.Shaders.water;


public class AquaShaders {
    public static @Nullable SurfaceShader brine, shadow;
    public static CacheLayer.ShaderLayer brineLayer, shadowLayer;
    public static Fi file(String name){
        return Core.files.internal("shaders/" + name);
    }



public static void init(){


        brine = new SurfaceShader("brine");
        shadow = new SurfaceShader("shadow");

        shadowLayer = new CacheLayer.ShaderLayer(shadow);
        brineLayer = new CacheLayer.ShaderLayer(brine);
        CacheLayer.addLast(brineLayer);
        CacheLayer.add(25, shadowLayer);
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
    //replacing surface Shaders
    public static class ExtendedSurfaceShader extends Shaders.SurfaceShader {
        public final Shader shad;

        public ExtendedSurfaceShader(String shaderName, ShaderExtension ext) {
            super(shaderName);
            this.shad = new Shader("shaders/screenspace.vert", "shaders/" + shaderName + ".frag");

            if (ext != null) {
                ext.extend(this, shad);
            }
        }

        @Override
        public void disableVertexAttribute(String name) {
            shad.disableVertexAttribute(name);
        }

        @Override
        public int fetchUniformLocation(String name, boolean pedantic) {
            return shad.fetchUniformLocation(name, pedantic);
        }

        @Override
        public int getAttributeLocation(String name) {
            return shad.getAttributeLocation(name);
        }

        @Override
        public String[] getAttributes() {
            return shad.getAttributes();
        }

        @Override
        public String[] getUniforms() {
            return shad.getUniforms();
        }

        @Override
        public int getAttributeSize(String name) {
            return shad.getAttributeSize(name);
        }

        @Override
        public void bind() {
            shad.bind();
        }

        @Override
        public boolean hasUniform(String name) {
            return shad.hasUniform(name);
        }

        @Override
        public int getUniformType(String name) {
            return shad.getUniformType(name);
        }

        @Override
        public int getUniformLocation(String name) {
            return shad.getUniformLocation(name);
        }

        @Override
        public int getUniformSize(String name) {
            return shad.getUniformSize(name);
        }

        @Override
        public void dispose() {
            shad.dispose();
            super.dispose();
        }

        @Override
        public boolean isDisposed() {
            return shad.isDisposed();
        }

        // Optional extension hook
        @FunctionalInterface
        public interface ShaderExtension {
            void extend(ExtendedSurfaceShader self, Shader internal);
        }
        //replace
        public static void replaceShader(Shader shader, String name) {
            Reflect.set(Shaders.class, name, shader);
            Object original = Reflect.get(CacheLayer.class, name);
            CacheLayer[] allLayers = CacheLayer.all;

            for (int i = 0; i < allLayers.length; i++) {
                if (allLayers[i] == original) {
                    CacheLayer.ShaderLayer newLayer = new CacheLayer.ShaderLayer(shader);
                    Reflect.set(CacheLayer.class, name, newLayer);
                    allLayers[i] = newLayer;
                    newLayer.id = i;
                }
            }
        }
    }
    //Replacing block shaders
    public static class ExtendedBlockShader extends Shaders.BlockBuildShader {
        public final Shader shad;

        public ExtendedBlockShader(String shaderName, ShaderExtension ext) {
            super();
            this.shad = new Shader("shaders/screenspace.vert", "shaders/" + shaderName + ".frag");

            if (ext != null) {
                ext.extend(this, shad);
            }
        }

        @Override
        public void disableVertexAttribute(String name) {
            shad.disableVertexAttribute(name);
        }

        @Override
        public int fetchUniformLocation(String name, boolean pedantic) {
            return shad.fetchUniformLocation(name, pedantic);
        }

        @Override
        public int getAttributeLocation(String name) {
            return shad.getAttributeLocation(name);
        }

        @Override
        public String[] getAttributes() {
            return shad.getAttributes();
        }

        @Override
        public String[] getUniforms() {
            return shad.getUniforms();
        }

        @Override
        public int getAttributeSize(String name) {
            return shad.getAttributeSize(name);
        }

        @Override
        public void bind() {
            shad.bind();
        }

        @Override
        public boolean hasUniform(String name) {
            return shad.hasUniform(name);
        }

        @Override
        public int getUniformType(String name) {
            return shad.getUniformType(name);
        }

        @Override
        public int getUniformLocation(String name) {
            return shad.getUniformLocation(name);
        }

        @Override
        public int getUniformSize(String name) {
            return shad.getUniformSize(name);
        }

        @Override
        public void dispose() {
            shad.dispose();
            super.dispose();
        }

        @Override
        public boolean isDisposed() {
            return shad.isDisposed();
        }

        // Optional extension hook
        @FunctionalInterface
        public interface ShaderExtension {
            void extend(ExtendedBlockShader self, Shader internal);
        }
        //replace
        public static void replaceShader(Shader shader, String name) {
            Reflect.set(Shaders.class, name, shader);
            Object original = Reflect.get(CacheLayer.class, name);
            CacheLayer[] allLayers = CacheLayer.all;

            for (int i = 0; i < allLayers.length; i++) {
                if (allLayers[i] == original) {
                    CacheLayer.ShaderLayer newLayer = new CacheLayer.ShaderLayer(shader);
                    Reflect.set(CacheLayer.class, name, newLayer);
                    allLayers[i] = newLayer;
                    newLayer.id = i;
                }
            }
        }
    }
}