package aquarion.world.graphics;

import arc.graphics.Color;
import arc.graphics.gl.Shader;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Mat3D;
import arc.util.Log;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.GenericMesh;
import mindustry.graphics.g3d.PlanetMesh;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;

import java.lang.reflect.Field;

public class ShadowMultiMesh implements GenericMesh {
    private final Planet planet;
    private final PlanetMesh ring;
    private final PlanetMesh hexMesh;
    private final GenericMesh[] allMeshes;

    private static final Field shaderField;
    static {
        Field f = null;
        try {
            f = PlanetMesh.class.getDeclaredField("shader");
            f.setAccessible(true);
        } catch (Exception e) {
            Log.err("ShadowMultiMesh: failed to access PlanetMesh.shader", e);
        }
        shaderField = f;
    }

    public ShadowMultiMesh(Planet planet, PlanetMesh ring, GenericMesh... others) {
        this.planet = planet;
        this.ring = ring;
        this.hexMesh = (others.length > 0 && others[0] instanceof PlanetMesh) ? (PlanetMesh) others[0] : null;
        this.allMeshes = new GenericMesh[1 + others.length];
        this.allMeshes[0] = ring;
        System.arraycopy(others, 0, this.allMeshes, 1, others.length);
    }

    @Override
    public void render(PlanetParams params, Mat3D projection, Mat3D transform) {
        // Lower zoom values are closer. The sector UI starts appearing at 1.9.
        float ringVisibility = params.drawUi && params.planet == planet
            ? Interp.smooth.apply(Mathf.clamp((params.zoom - 1.3f) / (1.9f - 1.3f)))
            : 1f;
        if (PlanetShadowMap.shadowBuffer == null || ringVisibility == 0f) {
            for (GenericMesh m : allMeshes) {
                if (m != ring || ringVisibility > 0f) m.render(params, projection, transform);
            }
            return;
        }

        PlanetShadowMap.updateLightSpace(planet);

        Shader depthShader = PlanetShadowMap.depthShader;

        PlanetShadowMap.begin();
        depthShader.bind();
        depthShader.setUniformMatrix4("u_lightSpaceMatrix", PlanetShadowMap.lightSpaceMatrix.val);

        Shader origHex = null;
        if (hexMesh != null) {
            origHex = getShader(hexMesh);
            setShader(hexMesh, depthShader);
            hexMesh.render(params, projection, transform);
            setShader(hexMesh, origHex);
        }

        Shader origRing = getShader(ring);
        setShader(ring, depthShader);
        // Both passes use the globe's transform, which includes its axial rotation.
        ring.render(params, projection, transform);
        setShader(ring, origRing);

        PlanetShadowMap.end();

        PlanetShadowMap.bindShadowMap(1);
        PlanetShadowMap.ShadowedPlanetShader ss = PlanetShadowMap.shadowedShader;
        ss.planet = planet;
        ss.emissive = ringEmissive();
        ss.visibility = ringVisibility;
        ss.lightDir.set(planet.solarSystem.position).sub(planet.position).nor();
        ss.ambientColor.set(planet.solarSystem.lightColor);

        if (hexMesh != null) {
            hexMesh.render(params, projection, transform);
        }

        Shader ringFinal = getShader(ring);
        setShader(ring, ss);
        ring.render(params, projection, transform);
        setShader(ring, ringFinal);
    }

    private boolean ringEmissive() {
        if (ring instanceof GlTFMesh) return ((GlTFMesh) ring).emissive;
        if (ring instanceof ObjMesh) return ((ObjMesh) ring).emissive;
        return false;
    }

    private static Shader getShader(PlanetMesh mesh) {
        if (shaderField == null) return null;
        try {
            return (Shader) shaderField.get(mesh);
        } catch (Exception e) {
            return null;
        }
    }

    private static void setShader(PlanetMesh mesh, Shader shader) {
        if (shaderField == null || shader == null) return;
        try {
            shaderField.set(mesh, shader);
        } catch (Exception e) {
            Log.err("ShadowMultiMesh: failed to set shader", e);
        }
    }

    @Override
    public void dispose() {
        for (GenericMesh m : allMeshes) m.dispose();
    }
}
