package aquarion.world.graphics;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.geom.Vec3;
import arc.scene.ui.layout.Scl;
import arc.util.Nullable;
import arc.util.Reflect;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Liquids;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.type.Planet;

import static arc.Core.assets;
import static mindustry.Vars.renderer;
import static mindustry.Vars.tree;


public class AquaShaders {
    public static PlanetShader planet;
    public static LightShader light;
    public static @Nullable SurfaceShader brine, petroleum, lava, shallowLava, shallowSlag, shadow, heat, glitch, neoplasiaBaseShader;
    public static @Nullable PartRegionShader knight1;
    public static @Nullable MonsoonShader monsoon;
    public static @Nullable deflectorShader deflectorShield;
    public static @Nullable DissolveShader dissolveShader;
    public static WaterReflectShader waterReflect;
    public static CacheLayer.ShaderLayer lavalLayer, slavaLayer, petroleumLayer, sslagLayer, brineLayer, shadowLayer, heatLayer, podLayer, glitchLayer, deflecterLayer, neoplasiaBaseLayer, sslagLayer2,
            wetUnderLayer;
    public static Fi file(String name){
        return Core.files.internal("shaders/" + name);
    }


public static void init() {

    planet = new PlanetShader();
    PlanetShadowMap.init();
    brine = new SurfaceShader("brine");
    knight1 = new PartRegionShader("knight1");
    lava = new SurfaceShader("lava");
    shallowSlag = new SurfaceShader("shallowSlag");
    shallowLava = new SurfaceShader("shallowLava");
    petroleum = new SurfaceShader("petroleum");
    neoplasiaBaseShader = new SurfaceShader("neoplasiaBase");

    waterReflect = new WaterReflectShader();
    ((CacheLayer.ShaderLayer)CacheLayer.water).shader = waterReflect;

    shadow = new SurfaceShader("shadow");
    heat = new SurfaceShader("heat");
    monsoon = new MonsoonShader();
    dissolveShader = new DissolveShader("dissolve");
    glitch = new SurfaceShader("glitch");
    deflectorShield = new deflectorShader();
    shadowLayer = new CacheLayer.ShaderLayer(shadow);
    neoplasiaBaseLayer = new CacheLayer.ShaderLayer(neoplasiaBaseShader);
    brineLayer = new CacheLayer.ShaderLayer(brine);
    sslagLayer = new CacheLayer.ShaderLayer(shallowSlag);
    sslagLayer2 = new LiquidUnderFloorLayer(Shaders.slag, Color.valueOf("ff8142"), "molten-slag");
    wetUnderLayer = new LiquidUnderFloorLayer(waterReflect, Color.valueOf("596ab8"), "deep-water");
    slavaLayer = new CacheLayer.ShaderLayer(shallowLava);
    lavalLayer = new CacheLayer.ShaderLayer(lava);
    heatLayer = new CacheLayer.ShaderLayer(heat);
    glitchLayer = new CacheLayer.ShaderLayer(glitch);
    deflecterLayer = new CacheLayer.ShaderLayer(deflectorShield);
    petroleumLayer = new CacheLayer.ShaderLayer(petroleum);
    CacheLayer.addLast(petroleumLayer);
    CacheLayer.addLast(brineLayer);
    CacheLayer.addLast(slavaLayer);
    CacheLayer.addLast(sslagLayer);
    CacheLayer.add(CacheLayer.normal.id,sslagLayer2);
    CacheLayer.add(CacheLayer.normal.id,wetUnderLayer);
    CacheLayer.addLast(lavalLayer);
    CacheLayer.addLast(neoplasiaBaseLayer);
    light = new LightShader();
    Shaders.light = light;

}

    public static void dispose(){
        if (!Vars.headless) {
            brine.dispose();
            lava.dispose();
            petroleum.dispose();
            shallowLava.dispose();
            shallowSlag.dispose();
            waterReflect.dispose();
            neoplasiaBaseShader.dispose();
            //neoplasiaPodShader.dispose();
            knight1.dispose();
            PlanetShadowMap.dispose();
        }
    }
    public static class LightShader extends Shaders.LightShader {

        public Color ambient = new Color(0.01f, 0.01f, 0.04f, 0.99f);

        public LightShader(){
            super();
        }

        @Override
        public void apply(){
            setUniformf("u_ambient", ambient);
            setUniformf("u_texelsize", 4f / Core.graphics.getWidth(), 4f / Core.graphics.getHeight());
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
        public static class FogShader extends Shader{
            Texture noiseTex;

            public FogShader(String frag){
                super(Shaders.getShaderFi("screenspace.vert"), tree.get("shaders/" + frag + ".frag"));
                loadNoise();
            }

            public FogShader(String vertRaw, String fragRaw){
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
            }
        @Override
        public void apply(){
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);
            setUniformf("u_time", Time.time);

            if(hasUniform("u_noise")){
                if(noiseTex == null){
                    noiseTex = assets.get("sprites/" + textureName() + ".png", Texture.class);
                };

                noiseTex.bind(2);
                renderer.effectBuffer.getTexture().bind(0);

                setUniformi("u_noise", 2);
            }
        }
    }
    public static class regionShader extends Shaders.LoadShader {
        public float progress;
        //Alpha changes the opacity of *everything*, while the provided batch color only changes the outline
        public float alpha = 1f;
        public TextureRegion region = new TextureRegion();
        public float time;

        public regionShader(){
            super("blockbuild", "default");
        }

        @Override
        public void apply(){
            setUniformf("u_time", time);
            setUniformf("u_alpha", alpha);
            if(region.texture == null){
                setUniformf("u_uv", 0f, 0f);
                setUniformf("u_uv2", 1f, 1f);
                setUniformf("u_texsize", 1, 1);
            }else{
                setUniformf("u_uv", region.u, region.v);
                setUniformf("u_uv2", region.u2, region.v2);
                setUniformf("u_texsize", region.texture.width, region.texture.height);
            }
        }
    }

    public static class PartRegionShader extends Shaders.LoadShader {
        public TextureRegion region = new TextureRegion();
        public float time;
        public float fade = 1f;

        public PartRegionShader(String frag){
            super(frag, "default");
        }

        public PartRegionShader setRegion(TextureRegion region){
            this.region = region;
            return this;
        }

        @Override
        public void apply(){
            setUniformf("u_time", time);
            setUniformf("u_fade", fade);
            if(region.texture == null){
                setUniformf("u_region", 0f, 0f, 1f, 1f);
                setUniformf("u_regionSize", 1f, 1f);
            }else{
                setUniformf("u_region", region.u, region.v, region.u2, region.v2);
                setUniformf("u_regionSize", region.width, region.height);
            }
        }
    }

    public static class deflectorShader extends Shaders.LoadShader {

        public deflectorShader(){
            super("deflectorShield", "screenspace");
        }

        @Override
        public void apply(){
            setUniformf("u_dp", Scl.scl(1f));
            setUniformf("u_time", Time.time / Scl.scl(1f));
            setUniformf("u_offset",
                    Core.camera.position.x - Core.camera.width / 2,
                    Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_texsize", Core.camera.width, Core.camera.height);
            setUniformf("u_invsize", 1f/Core.camera.width, 1f/Core.camera.height);
        }
    }
    public static class PlanetShader extends Shaders.LoadShader {
        public Vec3 lightDir = new Vec3(1, 1, 1).nor();
        public Color ambientColor = Color.white.cpy();
        public Vec3 camDir = new Vec3();
        public boolean emissive;
        public Planet planet;

        public PlanetShader(){
            super("planet", "planet");
        }

        @Override
        public void apply(){
            camDir.set(renderer.planets.cam.direction).rotate(Vec3.Y, planet.getRotation());

            setUniformf("u_lightdir", lightDir);
            setUniformf("u_ambientColor", ambientColor.r, ambientColor.g, ambientColor.b);
            setUniformf("u_camdir", camDir);
            setUniformf("u_campos", renderer.planets.cam.position);
            setUniformf("u_emissive", emissive ? 1f : 0f);
        }
    }
    public static class WaterReflectShader extends SurfaceShader {
        public WaterReflectShader() {
            super("water");
        }
        //see https://en.wikipedia.org/wiki/Reflection_(mathematics) if you don't know how the hell reflections works
        @Override
        public void apply() {
            super.apply();
            if (WaterReflections.buffer != null) {
                WaterReflections.buffer.getTexture().bind(1);
                setUniformi("u_reflection", 1);
                setUniformf("u_refTint", WaterReflections.refTint.r, WaterReflections.refTint.g, WaterReflections.refTint.b, WaterReflections.refTintAmount);
                setUniformf("u_refOpacity", WaterReflections.refOpacity);
            }
        }
    }
    public static class MonsoonShader extends SurfaceShader {

        private float intensity = 1f;

        public MonsoonShader() {
            super("monsoon");
        }

        public void setIntensity(float value) {
            this.intensity = value;
        }

        @Override
        public void apply() {
            super.apply(); // keep base uniforms

            setUniformf("u_intensity", intensity);
        }
    }
    public static class DissolveShader extends Shaders.LoadShader{
        /** 0..1; how much of the region has burned away. */
        public float progress;
        /** Colour of the glowing edge left behind by the dissolve. Alpha controls its strength. */
        public Color edgeColor = Color.valueOf("ffd37f");
        public TextureRegion region = new TextureRegion();
        public @Nullable Texture noiseTex;

        public DissolveShader(String frag){
            //the "default" vertex shader is the one the sprite batch uses; screenspace.vert has no
            //projection matrix, so anything drawn with it in the world ends up off-screen.
            super(frag, "default");
            loadNoise();
        }

        @Override
        public void apply(){
            setUniformf("u_progress", progress);
            setUniformf("u_edgeColor", edgeColor);

            if(region.texture == null){
                setUniformf("u_uv", 0f, 0f);
                setUniformf("u_uv2", 1f, 1f);
                setUniformf("u_texsize", 1f, 1f);
            }else{
                setUniformf("u_uv", region.u, region.v);
                setUniformf("u_uv2", region.u2, region.v2);
                setUniformf("u_texsize", region.texture.width, region.texture.height);
            }

            if(noiseTex == null && assets.isLoaded("sprites/" + textureName() + ".png", Texture.class)){
                noiseTex = assets.get("sprites/" + textureName() + ".png", Texture.class);
            }

            if(noiseTex != null){
                //unit 0 is taken by the sprite batch texture, which is bound right after apply()
                noiseTex.bind(1);
                setUniformi("u_noise", 1);
            }
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
    }
}