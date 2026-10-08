package aquarion.world.graphics;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Color;
import arc.graphics.GL20;
import arc.graphics.Mesh;
import arc.graphics.Pixmap;
import arc.graphics.VertexAttribute;
import arc.math.Mathf;
import arc.math.geom.Mat3D;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.TechTree;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.PlanetMesh;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;

/**
 * A planet mesh built from a glTF scene. The whole scene is baked into a single vertex buffer that is
 * compatible with Mindustry's planet shaders (position + normal + color + emissive).
 *
 * <p>Supports node animation: {@link #setProgress(float)} advances the animation to a 0..1 progress value
 * (or {@link #setProgressProvider(ProgressProvider)} to have it driven automatically). The pose is only
 * re-baked when the progress actually changes, so it stays cheap while the planet view is idle.</p>
 *
 * <p>Rendering disables backface culling, so single-sided / open models render correctly.</p>
 */
public class GlTFMesh extends PlanetMesh {

    public static final int VERTEX_SIZE = 11; // position(3) + normal(3) + color(1) + emissive(4)

    public final GlTF gltf;
    public boolean emissive;

    /** Multiplies the base (non-emissive) vertex color; use to brighten dark materials. */
    public float brightness = 1f;
    /** When auto-fitting (scale <= 0), the model is scaled so its outer radius = planet.radius * fitScale. */
    public float fitScale = 2.5f;
    private float bakeScale = 1f;

    /** Provides a 0..1 progress value used to advance the animation (e.g. research progress). */
    public interface ProgressProvider {
        float get();
    }

    public ProgressProvider progressProvider;
    /** When true, the provider's progress is inverted (1 - p), so less research advances the animation further. */
    public boolean invertProgress;
    private float progress;
    private float lastBaked = Float.NaN;
    private float[] bakedVertices;
    private final float[] tmp3 = new float[3];
    private final float[] normalMatrix = new float[9];

    public GlTFMesh(Planet planet, String gltfPath) {
        this(planet, gltfPath, -1f);
    }

    /**
     * @param scale explicit model scale; pass {@code <= 0} to auto-fit the model around the planet
     *              (outer radius = {@code planet.radius * fitScale}).
     */
    public GlTFMesh(Planet planet, String gltfPath, float scale) {
        this.planet = planet;
        this.shader = Shaders.planet;
        Fi file = Core.files.internal(gltfPath);
        if (!file.exists()) file = Vars.tree.get(gltfPath);
        this.gltf = file.exists() ? new GlTF(file, 1f) : null;
        this.emissive = gltf != null && gltf.emissive;
        if (gltf != null) {
            if (scale > 0f) {
                bakeScale = scale;
            } else {
                // auto-fit: measure the authored size, then scale so it wraps the planet nicely
                gltf.compute(Float.NaN);
                float extent = gltf.maxExtent();
                bakeScale = extent > 0f ? (planet.radius * fitScale) / extent : 1f;
                Log.info("GlTF: auto-fit @ to @ (model extent @, planet radius @, fitScale @)",
                        gltfPath, bakeScale, extent, planet.radius, fitScale);
            }
            bake();
        }
    }

    public float progress() {
        return progress;
    }

    /** Advances the animation to a 0..1 progress value. */
    public void setProgress(float p) {
        progress = Mathf.clamp(p, 0f, 1f);
    }

    /** Sets the animation progress directly in seconds of animation time. */
    public void setTime(float seconds) {
        progress = gltf == null || gltf.duration <= 0f ? 0f : Mathf.clamp(seconds / gltf.duration, 0f, 1f);
    }

    public void setProgressProvider(ProgressProvider provider) {
        this.progressProvider = provider;
    }

    /** Computes research progress (0..1) from how many tech tree nodes are unlocked. */
    public static float techProgress() {
        if (TechTree.all == null || TechTree.all.isEmpty()) return 0f;
        int done = 0;
        for (TechTree.TechNode n : TechTree.all) {
            if (n.content.unlocked()) done++;
        }
        return done / (float) TechTree.all.size;
    }

    private float animTime() {
        if (gltf == null || gltf.duration <= 0f) return 0f;
        return progress * gltf.duration;
    }

    /** Recomputes the vertex buffer from the current animation progress. */
    public void bake() {
        if (gltf == null || gltf.meshes == null) return;
        float time = animTime();
        if (time == lastBaked) return;
        lastBaked = time;

        gltf.compute(time);

        int vertexCount = countVertices();
        int needed = vertexCount * VERTEX_SIZE;
        if (bakedVertices == null || bakedVertices.length < needed) {
            bakedVertices = new float[needed];
        }

        int w = 0;
        for (int i = 0; i < gltf.nodes.length; i++) {
            GlTF.Node n = gltf.nodes[i];
            if (n.mesh < 0) continue;
            w = bakeNode(i, n, w);
        }

        if (w <= 0) return;
        if (mesh == null || mesh.isDisposed() || mesh.getMaxVertices() < w / VERTEX_SIZE) {
            if (mesh != null && !mesh.isDisposed()) mesh.dispose();
            mesh = new Mesh(true, w / VERTEX_SIZE, 0,
                    VertexAttribute.position3, VertexAttribute.normal, VertexAttribute.color,
                    new VertexAttribute(4, "a_emissive"));
        }
        mesh.setVertices(bakedVertices, 0, w);
    }

    private int countVertices() {
        if (gltf.nodes == null) return 0;
        int count = 0;
        for (GlTF.Node n : gltf.nodes) {
            if (n.mesh < 0) continue;
            for (GlTF.Primitive p : gltf.meshes[n.mesh].primitives) {
                if (p.position < 0) continue;
                if (p.indices >= 0) {
                    count += gltf.readIndices(p.indices).length;
                } else {
                    count += gltf.accessors[p.position].count;
                }
            }
        }
        return count;
    }

    private int bakeNode(int nodeIndex, GlTF.Node n, int w) {
        float[] world = gltf.nodeWorld;
        int o = nodeIndex * 16;
        computeNormalMatrix(world, o, normalMatrix);
        float sx = bakeScale;

        for (GlTF.Primitive p : gltf.meshes[n.mesh].primitives) {
            if (p.position < 0) continue;

            int[] inds = p.indices >= 0 ? gltf.readIndices(p.indices) : null;
            float[] pos = gltf.readFloats(p.position);
            float[] nrm = p.normal >= 0 ? gltf.readFloats(p.normal) : null;
            float[] col = p.color >= 0 ? gltf.readFloats(p.color) : null;
            float[] uv = p.texcoord >= 0 ? gltf.readFloats(p.texcoord) : null;
            int colComps = col != null ? GlTF.componentCount(gltf.accessors[p.color].type) : 0;
            GlTF.Material mat = p.material >= 0 && p.material < gltf.materials.length ? gltf.materials[p.material] : null;
            boolean hasBaseTex = uv != null && mat != null && mat.baseImage >= 0 && mat.baseImage < gltf.images.length && gltf.images[mat.baseImage] != null;
            boolean hasEmissiveTex = uv != null && mat != null && mat.emissiveImage >= 0 && mat.emissiveImage < gltf.images.length && gltf.images[mat.emissiveImage] != null;

            int vertCount = inds != null ? inds.length : pos.length / 3;
            int primCount;
            switch (p.mode) {
                case 5: case 6: primCount = Math.max(0, vertCount - 2); break;
                default: primCount = vertCount / 3;
            }

            for (int tri = 0; tri < primCount; tri++) {
                int i0, i1, i2;
                if (p.mode == 4) {
                    i0 = tri * 3;
                    i1 = tri * 3 + 1;
                    i2 = tri * 3 + 2;
                } else if (p.mode == 5) {
                    i0 = tri;
                    i1 = tri + 1;
                    i2 = tri + 2;
                    if ((tri & 1) == 1) {
                        int t = i1;
                        i1 = i2;
                        i2 = t;
                    }
                } else {
                    i0 = 0;
                    i1 = tri + 1;
                    i2 = tri + 2;
                }
                int a = inds != null ? inds[i0] : i0;
                int b = inds != null ? inds[i1] : i1;
                int c = inds != null ? inds[i2] : i2;
                w = writeVertex(w, world, o, normalMatrix, pos, nrm, col, uv, colComps, mat, a, hasBaseTex, hasEmissiveTex, sx);
                w = writeVertex(w, world, o, normalMatrix, pos, nrm, col, uv, colComps, mat, b, hasBaseTex, hasEmissiveTex, sx);
                w = writeVertex(w, world, o, normalMatrix, pos, nrm, col, uv, colComps, mat, c, hasBaseTex, hasEmissiveTex, sx);
            }
        }
        return w;
    }

    private int writeVertex(int w, float[] world, int wo, float[] nm, float[] pos, float[] nrm,
                            float[] col, float[] uv, int colComps, GlTF.Material mat, int vi,
                            boolean hasBaseTex, boolean hasEmissiveTex, float sx) {
        float x = pos[vi * 3], y = pos[vi * 3 + 1], z = pos[vi * 3 + 2];
        float[] m = world;
        float px = m[wo + 0] * x + m[wo + 4] * y + m[wo + 8] * z + m[wo + 12];
        float py = m[wo + 1] * x + m[wo + 5] * y + m[wo + 9] * z + m[wo + 13];
        float pz = m[wo + 2] * x + m[wo + 6] * y + m[wo + 10] * z + m[wo + 14];

        bakedVertices[w++] = px * sx;
        bakedVertices[w++] = py * sx;
        bakedVertices[w++] = pz * sx;

        float nx, ny, nz;
        if (nrm != null) {
            float nx0 = nrm[vi * 3], ny0 = nrm[vi * 3 + 1], nz0 = nrm[vi * 3 + 2];
            nx = nm[0] * nx0 + nm[1] * ny0 + nm[2] * nz0;
            ny = nm[3] * nx0 + nm[4] * ny0 + nm[5] * nz0;
            nz = nm[6] * nx0 + nm[7] * ny0 + nm[8] * nz0;
        } else {
            float len = (float) Math.sqrt(px * px + py * py + pz * pz);
            nx = len > 0 ? px / len : 0f;
            ny = len > 0 ? py / len : 1f;
            nz = len > 0 ? pz / len : 0f;
        }
        float nlen = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (nlen > 0) {
            nx /= nlen;
            ny /= nlen;
            nz /= nlen;
        }
        bakedVertices[w++] = nx;
        bakedVertices[w++] = ny;
        bakedVertices[w++] = nz;

        float cr = 1f, cg = 1f, cb = 1f;
        if (mat != null) {
            cr = mat.r;
            cg = mat.g;
            cb = mat.b;
        }
        if (col != null) {
            cr *= col[vi * colComps];
            cg *= col[vi * colComps + 1];
            cb *= col[vi * colComps + 2];
        }
        if (hasBaseTex) {
            sampleTexture(gltf.images[mat.baseImage], uv[vi * 2], uv[vi * 2 + 1], tmp3);
            cr *= tmp3[0];
            cg *= tmp3[1];
            cb *= tmp3[2];
        }
        bakedVertices[w++] = Color.toFloatBits(cr * brightness, cg * brightness, cb * brightness, 1f);

        float er = mat != null ? mat.er : 0f;
        float eg = mat != null ? mat.eg : 0f;
        float eb = mat != null ? mat.eb : 0f;
        if (hasEmissiveTex) {
            sampleTexture(gltf.images[mat.emissiveImage], uv[vi * 2], uv[vi * 2 + 1], tmp3);
            er *= tmp3[0];
            eg *= tmp3[1];
            eb *= tmp3[2];
        }
        bakedVertices[w++] = er;
        bakedVertices[w++] = eg;
        bakedVertices[w++] = eb;
        bakedVertices[w++] = Math.max(er, Math.max(eg, eb)) > 0.01f ? 1f : 0f;

        return w;
    }

    /** Samples a texture with bilinear filtering into {@code out} (0..1). */
    private static void sampleTexture(Pixmap pm, float u, float v, float[] out) {
        u -= (float) Math.floor(u);
        v -= (float) Math.floor(v);
        float fx = Mathf.clamp(u * (pm.width - 1), 0f, pm.width - 1);
        float fy = Mathf.clamp(v * (pm.height - 1), 0f, pm.height - 1);
        int x0 = (int) fx, y0 = (int) fy;
        int x1 = Math.min(x0 + 1, pm.width - 1);
        int y1 = Math.min(y0 + 1, pm.height - 1);
        float tx = fx - x0, ty = fy - y0;
        int c00 = pm.get(x0, y0), c10 = pm.get(x1, y0), c01 = pm.get(x0, y1), c11 = pm.get(x1, y1);
        float r0 = Mathf.lerp(Color.ri(c00), Color.ri(c10), tx);
        float r1 = Mathf.lerp(Color.ri(c01), Color.ri(c11), tx);
        float g0 = Mathf.lerp(Color.gi(c00), Color.gi(c10), tx);
        float g1 = Mathf.lerp(Color.gi(c01), Color.gi(c11), tx);
        float b0 = Mathf.lerp(Color.bi(c00), Color.bi(c10), tx);
        float b1 = Mathf.lerp(Color.bi(c01), Color.bi(c11), tx);
        out[0] = Mathf.lerp(r0, r1, ty) / 255f;
        out[1] = Mathf.lerp(g0, g1, ty) / 255f;
        out[2] = Mathf.lerp(b0, b1, ty) / 255f;
    }

    /** Computes the inverse-transpose of the upper 3x3 of a world matrix (column-major, 16 floats at {@code wo}). */
    private static void computeNormalMatrix(float[] world, int wo, float[] out) {
        float m00 = world[wo + 0], m01 = world[wo + 4], m02 = world[wo + 8];
        float m10 = world[wo + 1], m11 = world[wo + 5], m12 = world[wo + 9];
        float m20 = world[wo + 2], m21 = world[wo + 6], m22 = world[wo + 10];

        float det = m00 * (m11 * m22 - m12 * m21) - m01 * (m10 * m22 - m12 * m20) + m02 * (m10 * m21 - m11 * m20);
        if (Math.abs(det) < 0.000001f) {
            // fall back to the upper 3x3 (rotation-only)
            out[0] = m00; out[1] = m10; out[2] = m20;
            out[3] = m01; out[4] = m11; out[5] = m21;
            out[6] = m02; out[7] = m12; out[8] = m22;
            return;
        }
        float inv = 1f / det;
        // inverse
        float i00 = (m11 * m22 - m12 * m21) * inv;
        float i01 = (m02 * m21 - m01 * m22) * inv;
        float i02 = (m01 * m12 - m02 * m11) * inv;
        float i10 = (m12 * m20 - m10 * m22) * inv;
        float i11 = (m00 * m22 - m02 * m20) * inv;
        float i12 = (m02 * m10 - m00 * m12) * inv;
        float i20 = (m10 * m21 - m11 * m20) * inv;
        float i21 = (m01 * m20 - m00 * m21) * inv;
        float i22 = (m00 * m11 - m01 * m10) * inv;
        // transpose
        out[0] = i00; out[1] = i10; out[2] = i20;
        out[3] = i01; out[4] = i11; out[5] = i21;
        out[6] = i02; out[7] = i12; out[8] = i22;
    }

    @Override
    public void preRender(PlanetParams params) {
        if (shader instanceof Shaders.PlanetShader) {
            Shaders.PlanetShader s = (Shaders.PlanetShader) shader;
            s.planet = planet;
            s.emissive = emissive || (planet.generator != null && planet.generator.isEmissive());
            s.lightDir.set(planet.solarSystem.position).sub(planet.position).rotate(arc.math.geom.Vec3.Y, planet.getRotation()).nor();
            s.ambientColor.set(planet.solarSystem.lightColor);
        } else if (shader instanceof PlanetShadowMap.ShadowedPlanetShader) {
            PlanetShadowMap.ShadowedPlanetShader s = (PlanetShadowMap.ShadowedPlanetShader) shader;
            s.planet = planet;
            s.emissive = emissive || (planet.generator != null && planet.generator.isEmissive());
            s.lightDir.set(planet.solarSystem.position).sub(planet.position).nor();
            s.ambientColor.set(planet.solarSystem.lightColor);
        }
    }

    @Override
    public void render(PlanetParams params, Mat3D projection, Mat3D transform) {
        if (mesh == null || mesh.isDisposed()) return;
        if (progressProvider != null) {
            float p = progressProvider.get();
            if (invertProgress) p = 1f - p;
            if (p != progress) {
                progress = Mathf.clamp(p, 0f, 1f);
                bake();
            }
        }
        GL20 gl = Core.gl;
        boolean wasCulling = gl.glIsEnabled(GL20.GL_CULL_FACE);
        if (wasCulling) gl.glDisable(GL20.GL_CULL_FACE);
        super.render(params, projection, transform);
        if (wasCulling) gl.glEnable(GL20.GL_CULL_FACE);
    }

    @Override
    public void dispose() {
        if (gltf != null) gltf.dispose();
        super.dispose();
    }
}