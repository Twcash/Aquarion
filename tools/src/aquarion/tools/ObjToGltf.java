package aquarion.tools;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One-shot converter that turns an OBJ (+ MTL) model into a glTF 2.0 asset compatible with the mod's
 * {@code GlTFMesh} loader. Faces are grouped per material; materials keep their diffuse (Kd) and
 * emissive (Ke) colors. Outputs {@code <name>.gltf} and {@code <name>.gltf.bin}.
 *
 * <p>Usage: {@code java -cp tools ObjToGltf <input.obj> <outputBaseName> <scale>}</p>
 */
public final class ObjToGltf {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("usage: ObjToGltf <input.obj> <outputBase> [scale]");
            System.exit(1);
        }
        Path input = Paths.get(args[0]);
        String base = args[1];
        float scale = args.length > 2 ? Float.parseFloat(args[2]) : 1f;
        new ObjToGltf().run(input, base, scale);
    }

    private final List<float[]> positions = new ArrayList<>();
    private final List<float[]> normals = new ArrayList<>();
    private final Map<String, Material> materials = new LinkedHashMap<>();
    private String currentMaterial = "";

    private void run(Path input, String base, float scale) throws IOException {
        List<String> lines = Files.readAllLines(input, StandardCharsets.UTF_8);
        String mtlPath = null;

        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] parts = line.split("\\s+");
            switch (parts[0]) {
                case "mtllib": {
                    mtlPath = parts.length > 1 ? parts[1] : null;
                    break;
                }
                case "v": {
                    positions.add(new float[]{
                            Float.parseFloat(parts[1]) * scale,
                            Float.parseFloat(parts[2]) * scale,
                            Float.parseFloat(parts[3]) * scale
                    });
                    break;
                }
                case "vn": {
                    normals.add(new float[]{
                            Float.parseFloat(parts[1]),
                            Float.parseFloat(parts[2]),
                            Float.parseFloat(parts[3])
                    });
                    break;
                }
                case "usemtl": {
                    currentMaterial = parts.length > 1 ? line.substring(7).trim() : "";
                    break;
                }
                default:
            }
        }

        // expand to unique (v, vn) vertices, tracking faces per material
        List<float[]> outPos = new ArrayList<>();
        List<float[]> outNrm = new ArrayList<>();
        Map<String, List<Integer>> facesByMat = new LinkedHashMap<>();
        facesByMat.put("", new ArrayList<>());
        Map<Long, Integer> keyToIndex = new LinkedHashMap<>();

        for (String raw : lines) {
            String line = raw.trim();
            if (line.startsWith("usemtl ")) {
                currentMaterial = line.substring(7).trim();
                facesByMat.computeIfAbsent(currentMaterial, k -> new ArrayList<>());
                continue;
            }
            if (!line.startsWith("f ")) continue;
            String[] parts = line.split("\\s+");
            String mat = currentMaterial;
            facesByMat.computeIfAbsent(mat, k -> new ArrayList<>());

            // triangulate fan
            int[] cornerIdx = new int[parts.length - 1];
            for (int i = 1; i < parts.length; i++) {
                String[] idx = parts[i].split("/");
                int v = Integer.parseInt(idx[0]);
                v = v < 0 ? positions.size() + v : v - 1;
                int vn = -1;
                if (idx.length >= 3 && !idx[2].isEmpty()) {
                    int ni = Integer.parseInt(idx[2]);
                    vn = ni < 0 ? normals.size() + ni : ni - 1;
                }
                long key = ((long) v << 32) | (vn & 0xFFFFFFFFL);
                Integer existing = keyToIndex.get(key);
                if (existing == null) {
                    existing = outPos.size();
                    keyToIndex.put(key, existing);
                    outPos.add(positions.get(v));
                    float[] n = vn >= 0 ? normals.get(vn) : new float[]{0f, 1f, 0f};
                    outNrm.add(n);
                }
                cornerIdx[i - 1] = existing;
            }
            for (int i = 2; i < cornerIdx.length; i++) {
                facesByMat.get(mat).add(cornerIdx[0]);
                facesByMat.get(mat).add(cornerIdx[i - 1]);
                facesByMat.get(mat).add(cornerIdx[i]);
            }
        }

        // load MTL
        if (mtlPath != null) {
            Path mtl = input.getParent() == null ? Paths.get(mtlPath) : input.getParent().resolve(mtlPath);
            if (Files.exists(mtl)) parseMtl(mtl);
        }

        // default material for faces without an explicit material
        Material fallback = new Material("station-fallback");
        fallback.kd = new float[]{0.51f, 0.61f, 0.67f};
        materials.putIfAbsent("", fallback);

        writeGltf(base, outPos, outNrm, facesByMat);
    }

    private void parseMtl(Path mtl) throws IOException {
        Material current = null;
        for (String raw : Files.readAllLines(mtl, StandardCharsets.UTF_8)) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] parts = line.split("\\s+");
            switch (parts[0]) {
                case "newmtl": {
                    String name = line.substring(7).trim();
                    current = new Material(name);
                    materials.put(name, current);
                    break;
                }
                case "Kd": {
                    if (current != null) {
                        current.kd = new float[]{
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2]),
                                Float.parseFloat(parts[3])
                        };
                    }
                    break;
                }
                case "Ke": {
                    if (current != null) {
                        current.ke = new float[]{
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2]),
                                Float.parseFloat(parts[3])
                        };
                    }
                    break;
                }
                default:
            }
        }
    }

    private void writeGltf(String base, List<float[]> pos, List<float[]> nrm,
                           Map<String, List<Integer>> facesByMat) throws IOException {
        ByteBuffer data = ByteBuffer.allocate(pos.size() * 12 + nrm.size() * 12 + facesByMat.values().stream()
                .mapToInt(List::size).sum() * 2 + 64).order(ByteOrder.LITTLE_ENDIAN);

        List<int[]> views = new ArrayList<>(); // {offset, length}
        // positions
        int pOffset = data.position();
        for (float[] p : pos) {
            data.putFloat(p[0]).putFloat(p[1]).putFloat(p[2]);
        }
        views.add(new int[]{pOffset, data.position() - pOffset});
        // normals
        int nOffset = data.position();
        for (float[] n : nrm) {
            data.putFloat(n[0]).putFloat(n[1]).putFloat(n[2]);
        }
        views.add(new int[]{nOffset, data.position() - nOffset});

        // per-material index views
        List<String> matNames = new ArrayList<>();
        List<List<Integer>> matFaces = new ArrayList<>();
        List<int[]> idxViews = new ArrayList<>();
        List<Integer> idxViewOffsets = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> e : facesByMat.entrySet()) {
            if (e.getValue().isEmpty()) continue;
            matNames.add(e.getKey());
            matFaces.add(e.getValue());
            int off = data.position();
            for (int idx : e.getValue()) data.putShort((short) idx);
            views.add(new int[]{off, data.position() - off});
            idxViewOffsets.add(off);
        }

        byte[] binBytes = new byte[data.position()];
        data.flip();
        data.get(binBytes);

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"asset\": {\"version\": \"2.0\", \"generator\": \"aquarion obj-to-gltf\"},\n");
        sb.append("  \"scene\": 0,\n");
        sb.append("  \"scenes\": [{\"nodes\": [0]}],\n");
        sb.append("  \"nodes\": [{\"mesh\": 0, \"name\": \"station\"}],\n");
        sb.append("  \"meshes\": [{\"name\": \"station\", \"primitives\": [");

        // accessors: 0 = positions, 1 = normals, 2+ = per material indices
        int accCount = 2 + matNames.size();
        for (int m = 0; m < matNames.size(); m++) {
            if (m > 0) sb.append(",");
            sb.append("{\n");
            sb.append("    \"attributes\": {\"POSITION\": 0, \"NORMAL\": 1},\n");
            sb.append("    \"indices\": ").append(2 + m).append(",\n");
            sb.append("    \"material\": ").append(m).append(",\n");
            sb.append("    \"mode\": 4\n");
            sb.append("  }");
        }
        sb.append("]}],\n");
        sb.append("  \"materials\": [");
        for (int m = 0; m < matNames.size(); m++) {
            if (m > 0) sb.append(",");
            Material mat = materials.get(matNames.get(m));
            if (mat == null) mat = new Material(matNames.get(m));
            sb.append("{\"name\": \"").append(escape(mat.name)).append("\", ");
            sb.append("\"pbrMetallicRoughness\": {\"baseColorFactor\": [")
                    .append(f(mat.kd[0])).append(", ").append(f(mat.kd[1])).append(", ").append(f(mat.kd[2])).append(", 1.0]}, ");
            float[] ke = mat.ke;
            float kmax = Math.max(ke[0], Math.max(ke[1], ke[2]));
            if (kmax > 1f) {
                ke = new float[]{ke[0] / kmax, ke[1] / kmax, ke[2] / kmax};
            }
            sb.append("\"emissiveFactor\": [")
                    .append(f(ke[0])).append(", ").append(f(ke[1])).append(", ").append(f(ke[2])).append("], ");
            sb.append("\"doubleSided\": true}");
        }
        sb.append("],\n");

        sb.append("  \"accessors\": [");
        // 0 positions
        sb.append("{\"bufferView\": 0, \"componentType\": 5126, \"count\": ").append(pos.size())
                .append(", \"type\": \"VEC3\", \"min\": [").append(f(minPos(pos, 0))).append(", ")
                .append(f(minPos(pos, 1))).append(", ").append(f(minPos(pos, 2))).append("], \"max\": [")
                .append(f(maxPos(pos, 0))).append(", ").append(f(maxPos(pos, 1))).append(", ").append(f(maxPos(pos, 2))).append("]}, ");
        // 1 normals
        sb.append("{\"bufferView\": 1, \"componentType\": 5126, \"count\": ").append(nrm.size())
                .append(", \"type\": \"VEC3\"}");
        // 2+ indices
        for (int m = 0; m < matNames.size(); m++) {
            sb.append(", {\"bufferView\": ").append(2 + m).append(", \"componentType\": 5123, \"count\": ")
                    .append(matFaces.get(m).size()).append(", \"type\": \"SCALAR\"}");
        }
        sb.append("],\n");

        sb.append("  \"bufferViews\": [");
        for (int i = 0; i < views.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("{\"buffer\": 0, \"byteOffset\": ").append(views.get(i)[0])
                    .append(", \"byteLength\": ").append(views.get(i)[1]).append("}");
        }
        sb.append("],\n");

        sb.append("  \"buffers\": [{\"uri\": \"").append(Paths.get(base).getFileName()).append(".bin")
                .append("\", \"byteLength\": ").append(binBytes.length).append("}]\n");
        sb.append("}\n");

        Files.write(Paths.get(base + ".gltf"), sb.toString().getBytes(StandardCharsets.UTF_8));
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(base + ".bin"))) {
            os.write(binBytes);
        }
        System.out.println("wrote " + base + ".gltf and " + base + ".bin ("
                + pos.size() + " vertices, " + nrm.size() + " normals, "
                + matNames.size() + " materials)");
    }

    private static float minPos(List<float[]> pos, int c) {
        float v = Float.MAX_VALUE;
        for (float[] p : pos) v = Math.min(v, p[c]);
        return v;
    }

    private static float maxPos(List<float[]> pos, int c) {
        float v = -Float.MAX_VALUE;
        for (float[] p : pos) v = Math.max(v, p[c]);
        return v;
    }

    private static String f(float v) {
        if (v == (long) v) return String.valueOf((long) v);
        return String.valueOf(v);
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static class Material {
        final String name;
        float[] kd = {1f, 1f, 1f};
        float[] ke = {0f, 0f, 0f};

        Material(String name) {
            this.name = name;
        }
    }
}