package aquarion.world.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import mindustry.graphics.Shaders;
import mindustry.type.Planet;

import static mindustry.Vars.*;

public class PlanetShadowMap {

    public static FrameBuffer shadowBuffer;
    public static Shader depthShader;
    public static ShadowedPlanetShader shadowedShader;
    public static Mat3D lightSpaceMatrix = new Mat3D();
    private static Mat3D lightView = new Mat3D();
    private static Mat3D lightProjection = new Mat3D();

    public static final int SHADOW_SIZE = 2048;
    private static final int MAX_SHADOW_PLANETS = 8;

    public static void init() {
        shadowBuffer = new FrameBuffer(Pixmap.Format.rgba8888, SHADOW_SIZE, SHADOW_SIZE, true);
        depthShader = new Shader(
                tree.get("shaders/shadowDepth.vert"),
                tree.get("shaders/shadowDepth.frag")
        );
        shadowedShader = new ShadowedPlanetShader();
    }

    public static void updateLightSpace(Planet planet) {
        Vec3 lightDir = new Vec3(planet.solarSystem.position)
                .sub(planet.position)
                .rotate(Vec3.Y, planet.getRotation())
                .nor();
        float extent = 10f;
        lightProjection.setToOrtho(-extent, extent, -extent, extent, -1f, extent * 3f);
        lightView.setToLookAt(lightDir.scl(extent * 2), Vec3.Zero, Vec3.Y);
        lightSpaceMatrix.set(lightProjection).mul(lightView);
    }

    public static void begin() {
        shadowBuffer.begin(Color.clear);
    }

    public static void end() {
        shadowBuffer.end();
    }

    public static void bindShadowMap(int unit) {
        shadowBuffer.getTexture().bind(unit);
    }

    public static void dispose() {
        if (shadowBuffer != null) shadowBuffer.dispose();
        if (depthShader != null) depthShader.dispose();
        if (shadowedShader != null) shadowedShader.dispose();
    }

    private static void collectPlanets(Planet p, Planet exclude, Seq<Planet> out) {
        if (p != exclude && p != p.solarSystem) {
            out.add(p);
        }
        if (p.children != null) {
            for (int i = 0; i < p.children.size; i++) {
                collectPlanets(p.children.get(i), exclude, out);
            }
        }
    }

    public static class ShadowedPlanetShader extends Shaders.LoadShader {
        public Vec3 lightDir = new Vec3(1, 1, 1).nor();
        public Color ambientColor = Color.white.cpy();
        public Vec3 camDir = new Vec3();
        public Vec3 camPos = new Vec3();
        public boolean emissive;
        public Planet planet;

        private final Seq<Planet> tmpPlanets = new Seq<>();

        public ShadowedPlanetShader() {
            super("planetShadowed", "planetShadowed");
        }

        @Override
        public void apply() {
            camDir.set(renderer.planets.cam.direction).rotate(Vec3.Y, planet.getRotation());
            setUniformf("u_lightdir", lightDir);
            setUniformf("u_ambientColor", ambientColor.r, ambientColor.g, ambientColor.b);
            setUniformf("u_camdir", camDir);
            setUniformf("u_campos", renderer.planets.cam.position);
            setUniformf("u_emissive", emissive ? 1f : 0f);
            setUniformi("u_shadowMap", 1);
            setUniformMatrix4("u_lightSpaceMatrix", PlanetShadowMap.lightSpaceMatrix.val);

            setUniformf("u_sunPos", planet.solarSystem.position);

            tmpPlanets.clear();
            collectPlanets(planet.solarSystem, planet, tmpPlanets);
            int count = Math.min(tmpPlanets.size, MAX_SHADOW_PLANETS);
            setUniformi("u_planetCount", count);
            for (int i = 0; i < count; i++) {
                Planet p = tmpPlanets.get(i);
                setUniformf("u_planetPos[" + i + "]", p.position);
                setUniformf("u_planetRadius[" + i + "]", p.radius);
            }
        }
    }
}
