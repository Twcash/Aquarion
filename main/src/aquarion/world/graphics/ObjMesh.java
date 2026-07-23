package aquarion.world.graphics;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Mesh;
import arc.graphics.VertexAttribute;
import arc.math.geom.Mat3D;
import arc.struct.FloatSeq;
import arc.struct.IntSeq;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.math.Rand;
import mindustry.Vars;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.PlanetMesh;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ObjMesh extends PlanetMesh {

    public boolean emissive;

    public Planet getPlanet() { return planet; }

    public ObjMesh(Planet planet, String objPath) {
        this(planet, objPath, 1f);
    }

    public ObjMesh(Planet planet, String objPath, float scale) {
        this.planet = planet;
        this.shader = Shaders.planet;
        boolean[] emissiveOut = new boolean[1];
        this.mesh = loadObj(planet, objPath, scale, emissiveOut);
        this.emissive = emissiveOut[0];
    }

    private static final int[] STATION_COLORS = {
            0x8da6ab,
            0x697d85,
            0x333f4b,
            0x25303a
    };

    private static ObjectMap<String, float[]> loadMtl(Fi dir, String mtlName) {
        ObjectMap<String, float[]> materials = new ObjectMap<>();
        Fi file = dir.child(mtlName);
        if (!file.exists()) {
            file = Vars.tree.get("models/" + mtlName);
        }
        if (!file.exists()) {
            Log.warn("ObjMesh: MTL not found: " + mtlName);
            return materials;
        }

        String currentName = null;
        float[] current = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.read()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("newmtl ")) {
                    if (currentName != null && current != null) {
                        materials.put(currentName, current);
                    }
                    currentName = line.substring(7).trim();
                    current = null;
                } else if (currentName != null) {
                    if (line.startsWith("Kd ")) {
                        if (current == null) current = new float[6];
                        String[] parts = line.split("\\s+");
                        current[0] = Float.parseFloat(parts[1]);
                        current[1] = Float.parseFloat(parts[2]);
                        current[2] = Float.parseFloat(parts[3]);
                    } else if (line.startsWith("Ke ")) {
                        if (current == null) current = new float[6];
                        String[] parts = line.split("\\s+");
                        current[3] = Float.parseFloat(parts[1]);
                        current[4] = Float.parseFloat(parts[2]);
                        current[5] = Float.parseFloat(parts[3]);
                    }
                }
            }
            if (currentName != null && current != null) {
                materials.put(currentName, current);
            }
        } catch (Exception e) {
            Log.err("ObjMesh: error loading MTL: " + mtlName, e);
        }

        Log.info("ObjMesh: loaded " + materials.size + " materials from " + mtlName);
        return materials;
    }

    private static Mesh loadObj(Planet planet, String path, float scale, boolean[] emissiveOut) {
        Fi file = Core.files.internal(path);
        if (!file.exists()) {
            file = Vars.tree.get(path);
        }
        if (!file.exists()) {
            Log.warn("ObjMesh: file not found: " + path);
            return null;
        }

        Log.info("ObjMesh: loading " + path);

        FloatSeq positions = new FloatSeq();
        FloatSeq normals = new FloatSeq();
        IntSeq indices = new IntSeq();
        IntSeq normalIndices = new IntSeq();
        IntSeq faceMaterials = new IntSeq();
        ObjectMap<String, float[]> materials = new ObjectMap<>();
        ObjectMap<String, Integer> materialNames = new ObjectMap<>();
        List<String> materialOrder = new ArrayList<>();
        String currentMtl = null;
        String mtlFile = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.read()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("mtllib ")) {
                    mtlFile = line.substring(7).trim();
                } else if (line.startsWith("usemtl ")) {
                    String name = line.substring(7).trim();
                    currentMtl = name;
                    if (!materialNames.containsKey(name)) {
                        materialNames.put(name, materialOrder.size());
                        materialOrder.add(name);
                    }
                } else if (line.startsWith("vn ")) {
                    String[] parts = line.split("\\s+");
                    normals.add(Float.parseFloat(parts[1]));
                    normals.add(Float.parseFloat(parts[2]));
                    normals.add(Float.parseFloat(parts[3]));
                } else if (line.startsWith("v ")) {
                    String[] parts = line.split("\\s+");
                    positions.add(Float.parseFloat(parts[1]) * scale);
                    positions.add(Float.parseFloat(parts[2]) * scale);
                    positions.add(Float.parseFloat(parts[3]) * scale);
                } else if (line.startsWith("f ")) {
                    String[] parts = line.split("\\s+");
                    for (int i = 2; i < parts.length - 1; i++) {
                        int v1 = parseIndex(parts[1], positions.size / 3);
                        int v2 = parseIndex(parts[i], positions.size / 3);
                        int v3 = parseIndex(parts[i + 1], positions.size / 3);
                        indices.add(v1);
                        indices.add(v2);
                        indices.add(v3);

                        int n1 = parseNormalIndex(parts[1], normals.size / 3);
                        int n2 = parseNormalIndex(parts[i], normals.size / 3);
                        int n3 = parseNormalIndex(parts[i + 1], normals.size / 3);
                        normalIndices.add(n1);
                        normalIndices.add(n2);
                        normalIndices.add(n3);

                        faceMaterials.add(currentMtl != null && materialNames.containsKey(currentMtl) ? materialNames.get(currentMtl) : -1);
                    }
                }
            }
        } catch (Exception e) {
            Log.err("ObjMesh: error loading " + path, e);
            return null;
        }

        boolean hasNormals = normals.size > 0 && normalIndices.size == indices.size;
        if (!hasNormals) {
            normalIndices.clear();
        }

        if (mtlFile != null) {
            Fi parent = file.parent();
            materials = loadMtl(parent, mtlFile);
        }

        if (positions.size == 0) {
            Log.warn("ObjMesh: no vertices in " + path);
            return null;
        }

        int triCount = indices.size / 3;
        Log.info("ObjMesh: " + (positions.size / 3) + " vertices, " + triCount + " triangles");

        boolean anyMaterials = materials.size > 0;
        boolean hasEmission = false;

        float[][] triColors = new float[triCount][3];
        float[][] triEmissive = new float[triCount][4];
        float[][] smoothed = new float[triCount][3];
        Rand rand = new Rand(indices.hashCode());

        for (int t = 0; t < triCount; t++) {
            int mtlIdx = faceMaterials.get(t);
            float[] mat = null;
            if (anyMaterials && mtlIdx >= 0 && mtlIdx < materialOrder.size()) {
                mat = materials.get(materialOrder.get(mtlIdx));
            }
            if (mat != null) {
                triColors[t][0] = mat[0];
                triColors[t][1] = mat[1];
                triColors[t][2] = mat[2];
                triEmissive[t][0] = mat[3];
                triEmissive[t][1] = mat[4];
                triEmissive[t][2] = mat[5];
                float keMax = Math.max(mat[3], Math.max(mat[4], mat[5]));
                triEmissive[t][3] = keMax > 0 ? 1f : 0f;
                if (keMax > 0) hasEmission = true;
            } else {
                int rgb = STATION_COLORS[rand.random(STATION_COLORS.length - 1)];
                triColors[t][0] = ((rgb >> 16) & 0xFF) / 255f;
                triColors[t][1] = ((rgb >> 8) & 0xFF) / 255f;
                triColors[t][2] = (rgb & 0xFF) / 255f;
            }
        }

        if (!anyMaterials) {
            ObjectMap<Long, IntSeq> edgeFaces = new ObjectMap<>();
            for (int t = 0; t < triCount; t++) {
                int a = indices.get(t * 3), b = indices.get(t * 3 + 1), c = indices.get(t * 3 + 2);
                addEdge(edgeFaces, a, b, t);
                addEdge(edgeFaces, b, c, t);
                addEdge(edgeFaces, c, a, t);
            }

            List<IntSeq> adj = new ArrayList<>(triCount);
            for (int t = 0; t < triCount; t++) {
                adj.add(new IntSeq());
            }
            for (ObjectMap.Entry<Long, IntSeq> entry : edgeFaces) {
                IntSeq faces = entry.value;
                for (int i = 0; i < faces.size; i++) {
                    for (int j = i + 1; j < faces.size; j++) {
                        adj.get(faces.get(i)).add(faces.get(j));
                        adj.get(faces.get(j)).add(faces.get(i));
                    }
                }
            }

            int iterations = 5;
            for (int iter = 0; iter < iterations; iter++) {
                for (int t = 0; t < triCount; t++) {
                    smoothed[t][0] = triColors[t][0];
                    smoothed[t][1] = triColors[t][1];
                    smoothed[t][2] = triColors[t][2];
                }
                for (int t = 0; t < triCount; t++) {
                    IntSeq neighbors = adj.get(t);
                    if (neighbors.size == 0) continue;
                    float r = triColors[t][0], g = triColors[t][1], b = triColors[t][2];
                    for (int n = 0; n < neighbors.size; n++) {
                        int nb = neighbors.get(n);
                        r += triColors[nb][0];
                        g += triColors[nb][1];
                        b += triColors[nb][2];
                    }
                    int count = neighbors.size + 1;
                    smoothed[t][0] = r / count;
                    smoothed[t][1] = g / count;
                    smoothed[t][2] = b / count;
                }
                float[][] tmp = triColors;
                triColors = smoothed;
                smoothed = tmp;
            }
        }

        int vertexSize = 3 + 3 + 1 + 4;
        float[] vertices = new float[triCount * 3 * vertexSize];

        for (int t = 0; t < triCount; t++) {
            float color = new arc.graphics.Color(triColors[t][0], triColors[t][1], triColors[t][2], 1f).toFloatBits();
            float er = triEmissive[t][0], eg = triEmissive[t][1], eb = triEmissive[t][2], ei = triEmissive[t][3];

            for (int v = 0; v < 3; v++) {
                int vi = indices.get(t * 3 + v);
                int offset = (t * 3 + v) * vertexSize;
                float x = positions.get(vi * 3);
                float y = positions.get(vi * 3 + 1);
                float z = positions.get(vi * 3 + 2);

                vertices[offset] = x;
                vertices[offset + 1] = y;
                vertices[offset + 2] = z;

                if (hasNormals) {
                    int ni = normalIndices.get(t * 3 + v);
                    if (ni >= 0 && ni * 3 + 2 < normals.size) {
                        vertices[offset + 3] = normals.get(ni * 3);
                        vertices[offset + 4] = normals.get(ni * 3 + 1);
                        vertices[offset + 5] = normals.get(ni * 3 + 2);
                    } else {
                        vertices[offset + 3] = 0;
                        vertices[offset + 4] = 1;
                        vertices[offset + 5] = 0;
                    }
                } else {
                    float len = (float) Math.sqrt(x * x + y * y + z * z);
                    if (len > 0) {
                        vertices[offset + 3] = x / len;
                        vertices[offset + 4] = y / len;
                        vertices[offset + 5] = z / len;
                    } else {
                        vertices[offset + 3] = 0;
                        vertices[offset + 4] = 1;
                        vertices[offset + 5] = 0;
                    }
                }

                vertices[offset + 6] = color;
                vertices[offset + 7] = er;
                vertices[offset + 8] = eg;
                vertices[offset + 9] = eb;
                vertices[offset + 10] = ei;
            }
        }

        Mesh mesh = new Mesh(true, triCount * 3, 0,
                VertexAttribute.position3, VertexAttribute.normal, VertexAttribute.color,
                new VertexAttribute(4, "a_emissive"));

        mesh.setVertices(vertices);

        emissiveOut[0] = hasEmission;
        return mesh;
    }

    private static int parseIndex(String part, int vCount) {
        String indexStr = part.split("/")[0];
        int idx = Integer.parseInt(indexStr);
        return idx < 0 ? vCount + idx : idx - 1;
    }

    private static int parseNormalIndex(String part, int nCount) {
        String[] slashParts = part.split("/");
        if (slashParts.length < 3 || slashParts[2].isEmpty()) return -1;
        int idx = Integer.parseInt(slashParts[2]);
        return idx < 0 ? nCount + idx : idx - 1;
    }

    private static void addEdge(ObjectMap<Long, IntSeq> edgeFaces, int a, int b, int face) {
        long key = ((long) Math.min(a, b) << 32) | (long) Math.max(a, b);
        Long boxed = key;
        IntSeq list = edgeFaces.get(boxed);
        if (list == null) {
            list = new IntSeq();
            edgeFaces.put(boxed, list);
        }
        list.add(face);
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
        arc.graphics.GL20 gl = arc.Core.gl;
        boolean wasCulling = gl.glIsEnabled(arc.graphics.GL20.GL_CULL_FACE);
        if (wasCulling) gl.glDisable(arc.graphics.GL20.GL_CULL_FACE);
        super.render(params, projection, transform);
        if (wasCulling) gl.glEnable(arc.graphics.GL20.GL_CULL_FACE);
    }
}
