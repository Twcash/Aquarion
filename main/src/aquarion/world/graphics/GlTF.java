package aquarion.world.graphics;

import arc.Core;
import arc.files.Fi;
import arc.graphics.Pixmap;
import arc.math.geom.Quat;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.serialization.Base64Coder;
import arc.util.serialization.JsonReader;
import arc.util.serialization.JsonValue;
import mindustry.Vars;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
//GLTF 2.0 model loader based on GDX's loader.
public class GlTF {

    public static final int
            BYTE = 5120,
            UNSIGNED_BYTE = 5121,
            SHORT = 5122,
            UNSIGNED_SHORT = 5123,
            UNSIGNED_INT = 5125,
            FLOAT = 5126;

    public final float scale;

    public byte[][] buffers;
    public BufferView[] bufferViews;
    public Accessor[] accessors;
    public MeshData[] meshes;
    public Material[] materials;
    public Node[] nodes;
    public Animation[] animations;
    public int[] roots;
    public Pixmap[] images;
    public int[] textureSources;

    public float duration;
    public boolean emissive;

    /** World matrices (16 floats per node) for the currently computed pose. */
    public final float[] nodeWorld;

    /** Raw bytes of the GLB binary chunk (GLB files only); becomes buffer 0 when the JSON has no buffer URI. */
    private byte[] glbBin;

    private final ObjectMap<Integer, float[]> floatCache = new ObjectMap<>();
    private final ObjectMap<Integer, int[]> intCache = new ObjectMap<>();
    private float currentTime = Float.NaN;

    public GlTF(Fi gltfFile) {
        this(gltfFile, 1f);
    }

    public GlTF(Fi gltfFile, float scale) {
        this.scale = scale;

        byte[] bytes = gltfFile.readBytes();
        String json;
        if (isGlb(bytes)) {
            json = parseGlb(bytes);
            if (json == null) {
                Log.err("GlTF: @ is not a valid GLB container", gltfFile.name());
                nodeWorld = new float[16];
                return;
            }
        } else {
            json = new String(bytes, StandardCharsets.UTF_8);
        }

        JsonValue root = new JsonReader().parse(json);
        loadBuffers(gltfFile, root);
        loadBufferViews(root);
        loadAccessors(root);
        loadImages(gltfFile, root);
        loadMaterials(root);
        loadMeshes(root);
        loadNodes(root);
        loadScenes(root);
        loadAnimations(root);

        nodeWorld = nodes == null ? new float[16] : new float[nodes.length * 16];
        compute(Float.NaN);

        Log.info("GlTF: loaded @ (@ nodes, @ meshes, @ accessors, @ materials, @ animations, @ images, @ GLB)",
                gltfFile.name(), nodes == null ? 0 : nodes.length,
                meshes == null ? 0 : meshes.length, accessors == null ? 0 : accessors.length,
                materials == null ? 0 : materials.length, animations == null ? 0 : animations.length,
                images == null ? 0 : images.length, glbBin != null);
    }

    /** Detects a GLB container by its magic number (the JSON/text version starts with '{'). */
    private static boolean isGlb(byte[] bytes) {
        return bytes.length >= 4 && bytes[0] == 'g' && bytes[1] == 'l' && bytes[2] == 'T' && bytes[3] == 'F';
    }

    /**
     * Parses a GLB container: 12-byte header (magic, version, length) followed by JSON and optional BIN chunks.
     * Returns the JSON text and stores the BIN chunk into {@link #glbBin}.
     */
    private String parseGlb(byte[] bytes) {
        int length = readU32(bytes, 8);
        int pos = 12;
        String json = null;
        while (pos + 8 <= length && pos + 8 <= bytes.length) {
            int chunkLength = readU32(bytes, pos);
            int chunkType = readU32(bytes, pos + 4);
            int start = pos + 8;
            if (start + chunkLength > bytes.length) break;
            if (chunkType == 0x4E4F534A) { // "JSON"
                json = new String(bytes, start, chunkLength, StandardCharsets.UTF_8);
            } else if (chunkType == 0x004E4942) { // "BIN\0"
                glbBin = Arrays.copyOfRange(bytes, start, start + chunkLength);
            }
            pos = start + chunkLength;
        }
        return json;
    }

    private static int readU32(byte[] b, int off) {
        return (b[off] & 0xFF) | ((b[off + 1] & 0xFF) << 8) | ((b[off + 2] & 0xFF) << 16) | ((b[off + 3] & 0xFF) << 24);
    }

    /** Recomputes the node pose (local + world matrices) at the given animation time. No-op if unchanged. */
    public void compute(float time) {
        if (time == currentTime || nodes == null) return;
        currentTime = time;

        for (Node n : nodes) {
            computeLocal(n, time);
        }
        Arrays.fill(nodeWorld, 0f);
        for (int r : roots) {
            computeWorld(r, -1);
        }
    }

    /** Largest distance from the origin of any baked vertex at the current pose (model space). */
    public float maxExtent() {
        if (nodes == null) return 0f;
        float max = 0f;
        for (int i = 0; i < nodes.length; i++) {
            Node n = nodes[i];
            if (n.mesh < 0) continue;
            int o = i * 16;
            for (Primitive p : meshes[n.mesh].primitives) {
                if (p.position < 0) continue;
                float[] pos = readFloats(p.position);
                float[] w = nodeWorld;
                for (int vi = 0; vi < pos.length; vi += 3) {
                    float x = pos[vi], y = pos[vi + 1], z = pos[vi + 2];
                    float px = w[o + 0] * x + w[o + 4] * y + w[o + 8] * z + w[o + 12];
                    float py = w[o + 1] * x + w[o + 5] * y + w[o + 9] * z + w[o + 13];
                    float pz = w[o + 2] * x + w[o + 6] * y + w[o + 10] * z + w[o + 14];
                    max = Math.max(max, px * px + py * py + pz * pz);
                }
            }
        }
        return (float) Math.sqrt(max);
    }

    /** Reads float components for a given accessor (handles normalized integer types). Cached. */
    public float[] readFloats(int accessor) {
        float[] cached = floatCache.get(accessor);
        if (cached != null) return cached;

        Accessor a = accessors[accessor];
        byte[] buf = buffers[a.buffer];
        int comps = componentCount(a.type);
        int compSize = componentSize(a.componentType);
        int stride = a.byteStride > 0 ? a.byteStride : compSize * comps;
        int base = a.byteOffset;
        float[] out = new float[a.count * comps];
        boolean normalized = a.normalized;

        for (int i = 0; i < a.count; i++) {
            int off = base + i * stride;
            for (int c = 0; c < comps; c++) {
                out[i * comps + c] = readComponent(buf, off + c * compSize, a.componentType, normalized);
            }
        }
        floatCache.put(accessor, out);
        return out;
    }

    /** Reads an integer (index) accessor. Cached. */
    public int[] readIndices(int accessor) {
        int[] cached = intCache.get(accessor);
        if (cached != null) return cached;

        Accessor a = accessors[accessor];
        byte[] buf = buffers[a.buffer];
        int compSize = componentSize(a.componentType);
        int stride = a.byteStride > 0 ? a.byteStride : compSize;
        int base = a.byteOffset;
        int[] out = new int[a.count];

        for (int i = 0; i < a.count; i++) {
            int off = base + i * stride;
            switch (a.componentType) {
                case UNSIGNED_BYTE: out[i] = buf[off] & 0xFF; break;
                case UNSIGNED_SHORT: out[i] = (buf[off] & 0xFF) | ((buf[off + 1] & 0xFF) << 8); break;
                case UNSIGNED_INT: out[i] = (buf[off] & 0xFF) | ((buf[off + 1] & 0xFF) << 8)
                        | ((buf[off + 2] & 0xFF) << 16) | ((buf[off + 3] & 0xFF) << 24); break;
                default: out[i] = 0;
            }
        }
        intCache.put(accessor, out);
        return out;
    }

    /** Reads the raw bytes of a buffer view (used for embedded images). */
    public byte[] readBufferView(int index) {
        BufferView bv = bufferViews[index];
        byte[] buf = buffers[bv.buffer];
        return Arrays.copyOfRange(buf, bv.byteOffset, bv.byteOffset + bv.byteLength);
    }

    // --- matrix helpers -----------------------------------------------------

    private void computeLocal(Node n, float time) {
        if (n.hasMatrix) {
            System.arraycopy(n.matrix, 0, n.local, 0, 16);
            return;
        }

        float[] t = n.animT, r = n.animR, s = n.animS;
        System.arraycopy(n.translation, 0, t, 0, 3);
        System.arraycopy(n.rotation, 0, r, 0, 4);
        System.arraycopy(n.scale, 0, s, 0, 3);

        if (n.channels != null && !Float.isNaN(time)) {
            for (Channel ch : n.channels) {
                sample(ch.sampler, time, ch.path, t, r, s);
            }
        }

        Quat quat = TmpVars.quat.set(r[0], r[1], r[2], r[3]).nor();
        quat.toMatrix(n.local);
        n.local[0] *= s[0];
        n.local[1] *= s[0];
        n.local[2] *= s[0];
        n.local[4] *= s[1];
        n.local[5] *= s[1];
        n.local[6] *= s[1];
        n.local[8] *= s[2];
        n.local[9] *= s[2];
        n.local[10] *= s[2];
        n.local[12] = t[0];
        n.local[13] = t[1];
        n.local[14] = t[2];
        n.local[3] = n.local[7] = n.local[11] = 0f;
        n.local[15] = 1f;
    }

    private void computeWorld(int idx, int parent) {
        float[] w = nodeWorld;
        int o = idx * 16;
        if (parent < 0) {
            System.arraycopy(nodes[idx].local, 0, w, o, 16);
        } else {
            mul4(w, parent * 16, nodes[idx].local, 0, w, o);
        }
        for (int c : nodes[idx].children) {
            computeWorld(c, idx);
        }
    }

    private static void mul4(float[] a, int ao, float[] b, int bo, float[] out, int oo) {
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                float s = 0f;
                for (int k = 0; k < 4; k++) {
                    s += a[ao + k * 4 + row] * b[bo + col * 4 + k];
                }
                out[oo + col * 4 + row] = s;
            }
        }
    }

    // --- animation sampling -------------------------------------------------

    private void sample(Sampler s, float time, String path, float[] t, float[] r, float[] scale) {
        float[] times = s.times;
        if (times.length == 0) return;

        if (time <= times[0]) {
            writeKeyframe(s, 0, path, t, r, scale);
            return;
        }
        if (time >= times[times.length - 1]) {
            writeKeyframe(s, times.length - 1, path, t, r, scale);
            return;
        }

        int k1 = 1;
        while (k1 < times.length - 1 && times[k1] < time) k1++;
        int k0 = k1 - 1;
        float frac = (time - times[k0]) / (times[k1] - times[k0]);

        int comps = s.comps;
        if ("STEP".equals(s.interpolation)) {
            writeKeyframe(s, k0, path, t, r, scale);
        } else if ("CUBICSPLINE".equals(s.interpolation)) {
            if (comps == 4) {
                slerpKeyframes(s, k0, k1, frac, r);
            } else {
                int stride = comps * 3;
                float[] v = s.values;
                for (int i = 0; i < comps; i++) {
                    float p0 = v[k0 * stride + comps + i];
                    float m0 = v[k0 * stride + comps * 2 + i];
                    float p1 = v[k1 * stride + comps + i];
                    float m1 = v[k1 * stride + i];
                    float f = frac, f2 = f * f, f3 = f2 * f;
                    float val = (2f * f3 - 3f * f2 + 1f) * p0
                            + (f3 - 2f * f2 + f) * m0
                            + (-2f * f3 + 3f * f2) * p1
                            + (f3 - f2) * m1;
                    setComponent(t, r, scale, path, i, val);
                }
            }
        } else {
            if (comps == 4) {
                slerpKeyframes(s, k0, k1, frac, r);
            } else {
                for (int i = 0; i < comps; i++) {
                    float v0 = s.values[k0 * comps + i];
                    float v1 = s.values[k1 * comps + i];
                    setComponent(t, r, scale, path, i, v0 + (v1 - v0) * frac);
                }
            }
        }
    }

    private static void writeKeyframe(Sampler s, int k, String path, float[] t, float[] r, float[] scale) {
        int comps = s.comps;
        int off = "CUBICSPLINE".equals(s.interpolation) ? k * comps * 3 + comps : k * comps;
        for (int i = 0; i < comps; i++) {
            setComponent(t, r, scale, path, i, s.values[off + i]);
        }
    }

    private static void slerpKeyframes(Sampler s, int k0, int k1, float frac, float[] r) {
        boolean cubic = "CUBICSPLINE".equals(s.interpolation);
        int o0 = cubic ? k0 * 12 + 4 : k0 * 4;
        int o1 = cubic ? k1 * 12 + 4 : k1 * 4;
        Quat a = TmpVars.quat.set(s.values[o0], s.values[o0 + 1], s.values[o0 + 2], s.values[o0 + 3]).nor();
        Quat b = TmpVars.quat2.set(s.values[o1], s.values[o1 + 1], s.values[o1 + 2], s.values[o1 + 3]).nor();
        if (a.dot(b) < 0f) b.mul(-1f);
        a.slerp(b, frac);
        r[0] = a.x;
        r[1] = a.y;
        r[2] = a.z;
        r[3] = a.w;
    }

    private static void setComponent(float[] t, float[] r, float[] s, String path, int i, float v) {
        if ("translation".equals(path)) t[i] = v;
        else if ("rotation".equals(path)) r[i] = v;
        else s[i] = v;
    }

    // --- JSON parsing -------------------------------------------------------

    private void loadBuffers(Fi gltfFile, JsonValue root) {
        JsonValue arr = root.get("buffers");
        if (arr == null) return;
        buffers = new byte[arr.size][];
        int i = 0;
        for (JsonValue b : arr) {
            String uri = b.getString("uri", null);
            if (uri == null) {
                // in GLB, buffer 0 with no uri is the binary chunk
                buffers[i] = i == 0 && glbBin != null ? glbBin : new byte[b.getInt("byteLength", 0)];
            } else if (uri.startsWith("data:")) {
                int comma = uri.indexOf(',');
                buffers[i] = Base64Coder.decode(uri.substring(comma + 1));
            } else {
                buffers[i] = resolve(gltfFile, uri).readBytes();
            }
            i++;
        }
    }

    private void loadBufferViews(JsonValue root) {
        JsonValue arr = root.get("bufferViews");
        if (arr == null) return;
        bufferViews = new BufferView[arr.size];
        int i = 0;
        for (JsonValue b : arr) {
            BufferView bv = new BufferView();
            bv.buffer = b.getInt("buffer");
            bv.byteOffset = b.getInt("byteOffset", 0);
            bv.byteLength = b.getInt("byteLength");
            bv.byteStride = b.getInt("byteStride", 0);
            bufferViews[i++] = bv;
        }
    }

    private void loadImages(Fi gltfFile, JsonValue root) {
        JsonValue imagesJson = root.get("images");
        JsonValue textures = root.get("textures");
        if (imagesJson == null) return;

        images = new Pixmap[imagesJson.size];
        int i = 0;
        for (JsonValue img : imagesJson) {
            String uri = img.getString("uri", null);
            if (uri != null) {
                if (uri.startsWith("data:")) {
                    int comma = uri.indexOf(',');
                    images[i] = new Pixmap(Base64Coder.decode(uri.substring(comma + 1)));
                } else {
                    images[i] = new Pixmap(resolve(gltfFile, uri));
                }
            } else if (img.has("bufferView")) {
                byte[] data = readBufferView(img.getInt("bufferView"));
                if (data != null) images[i] = new Pixmap(data);
            }
            i++;
        }

        if (textures != null) {
            textureSources = new int[textures.size];
            int ti = 0;
            for (JsonValue tex : textures) {
                textureSources[ti++] = tex.getInt("source", -1);
            }
        }
    }

    private void loadMaterials(JsonValue root) {
        JsonValue arr = root.get("materials");
        if (arr == null) return;
        materials = new Material[arr.size];
        int i = 0;
        for (JsonValue m : arr) {
            Material mat = new Material();
            JsonValue pbr = m.get("pbrMetallicRoughness");
            if (pbr != null) {
                JsonValue bcf = pbr.get("baseColorFactor");
                if (bcf != null && bcf.size >= 3) {
                    mat.r = bcf.get(0).asFloat();
                    mat.g = bcf.get(1).asFloat();
                    mat.b = bcf.get(2).asFloat();
                    if (bcf.size >= 4) mat.a = bcf.get(3).asFloat();
                }
                JsonValue bct = pbr.get("baseColorTexture");
                if (bct != null && textureSources != null) {
                    int tex = bct.getInt("index", -1);
                    if (tex >= 0 && tex < textureSources.length) {
                        mat.baseImage = textureSources[tex];
                        mat.baseTexCoord = bct.getInt("texCoord", 0);
                    }
                }
            }
            JsonValue ef = m.get("emissiveFactor");
            if (ef != null && ef.size >= 3) {
                mat.er = ef.get(0).asFloat();
                mat.eg = ef.get(1).asFloat();
                mat.eb = ef.get(2).asFloat();
            }
            JsonValue et = m.get("emissiveTexture");
            if (et != null && textureSources != null) {
                int tex = et.getInt("index", -1);
                if (tex >= 0 && tex < textureSources.length) {
                    mat.emissiveImage = textureSources[tex];
                    mat.emissiveTexCoord = et.getInt("texCoord", 0);
                }
            }
            mat.doubleSided = m.getBoolean("doubleSided", false);
            materials[i++] = mat;
            if (Math.max(mat.er, Math.max(mat.eg, mat.eb)) > 0.01f || mat.emissiveImage >= 0) emissive = true;
        }
    }

    private void loadAccessors(JsonValue root) {
        JsonValue arr = root.get("accessors");
        if (arr == null) return;
        accessors = new Accessor[arr.size];
        int i = 0;
        for (JsonValue a : arr) {
            Accessor acc = new Accessor();
            acc.componentType = a.getInt("componentType");
            acc.count = a.getInt("count");
            acc.type = a.getString("type", "SCALAR");
            acc.normalized = a.getBoolean("normalized", false);
            acc.byteOffset = a.getInt("byteOffset", 0);
            int bv = a.getInt("bufferView", -1);
            if (bv >= 0 && bufferViews != null && bv < bufferViews.length) {
                BufferView view = bufferViews[bv];
                acc.buffer = view.buffer;
                acc.byteOffset += view.byteOffset;
                acc.byteStride = view.byteStride;
            }
            accessors[i++] = acc;
        }
    }

    private void loadMeshes(JsonValue root) {
        JsonValue arr = root.get("meshes");
        if (arr == null) return;
        meshes = new MeshData[arr.size];
        int i = 0;
        for (JsonValue m : arr) {
            MeshData mesh = new MeshData();
            JsonValue prims = m.get("primitives");
            if (prims != null) {
                mesh.primitives = new Primitive[prims.size];
                int p = 0;
                for (JsonValue prim : prims) {
                    Primitive pr = new Primitive();
                    JsonValue attrs = prim.get("attributes");
                    if (attrs != null) {
                        pr.position = attrs.getInt("POSITION", -1);
                        pr.normal = attrs.getInt("NORMAL", -1);
                        pr.color = attrs.getInt("COLOR_0", -1);
                        pr.texcoord = attrs.getInt("TEXCOORD_0", -1);
                    }
                    pr.indices = prim.getInt("indices", -1);
                    pr.material = prim.getInt("material", -1);
                    pr.mode = prim.getInt("mode", 4);
                    mesh.primitives[p++] = pr;
                }
            }
            meshes[i++] = mesh;
        }
    }

    private void loadNodes(JsonValue root) {
        JsonValue arr = root.get("nodes");
        if (arr == null) return;
        nodes = new Node[arr.size];
        int i = 0;
        for (JsonValue n : arr) {
            Node node = new Node();
            node.name = n.getString("name", null);
            node.mesh = n.getInt("mesh", -1);

            JsonValue children = n.get("children");
            if (children != null) {
                node.children = new int[children.size];
                int c = 0;
                for (JsonValue child : children) node.children[c++] = child.asInt();
            }

            JsonValue matrix = n.get("matrix");
            if (matrix != null && matrix.size >= 16) {
                node.hasMatrix = true;
                node.matrix = new float[16];
                for (int k = 0; k < 16; k++) node.matrix[k] = matrix.get(k).asFloat();
            }

            JsonValue translation = n.get("translation");
            if (translation != null && translation.size >= 3) {
                node.translation = new float[]{translation.get(0).asFloat(), translation.get(1).asFloat(), translation.get(2).asFloat()};
            }
            JsonValue rotation = n.get("rotation");
            if (rotation != null && rotation.size >= 4) {
                node.rotation = new float[]{rotation.get(0).asFloat(), rotation.get(1).asFloat(), rotation.get(2).asFloat(), rotation.get(3).asFloat()};
            }
            JsonValue scale = n.get("scale");
            if (scale != null && scale.size >= 3) {
                node.scale = new float[]{scale.get(0).asFloat(), scale.get(1).asFloat(), scale.get(2).asFloat()};
            }
            nodes[i++] = node;
        }
    }

    private void loadScenes(JsonValue root) {
        JsonValue scenes = root.get("scenes");
        if (scenes == null || scenes.size == 0) return;
        int scene = root.getInt("scene", 0);
        JsonValue chosen = scene >= 0 && scene < scenes.size ? scenes.get(scene) : scenes.get(0);
        JsonValue sceneNodes = chosen != null ? chosen.get("nodes") : null;
        if (sceneNodes == null) return;
        roots = new int[sceneNodes.size];
        int i = 0;
        for (JsonValue r : sceneNodes) roots[i++] = r.asInt();
    }

    private void loadAnimations(JsonValue root) {
        JsonValue arr = root.get("animations");
        if (arr == null || arr.size == 0) return;
        animations = new Animation[arr.size];
        int ai = 0;
        for (JsonValue anim : arr) {
            Animation a = new Animation();
            JsonValue samplers = anim.get("samplers");
            if (samplers != null) {
                a.samplers = new Sampler[samplers.size];
                int s = 0;
                for (JsonValue sm : samplers) {
                    Sampler sampler = new Sampler();
                    sampler.input = sm.getInt("input");
                    sampler.output = sm.getInt("output");
                    sampler.interpolation = sm.getString("interpolation", "LINEAR");
                    sampler.times = readFloats(sampler.input);
                    sampler.comps = componentCount(accessors[sampler.output].type);
                    sampler.values = readFloats(sampler.output);
                    for (float t : sampler.times) {
                        if (t > a.duration) a.duration = t;
                    }
                    a.samplers[s++] = sampler;
                }
            }
            JsonValue channels = anim.get("channels");
            if (channels != null) {
                a.channels = new Channel[channels.size];
                int c = 0;
                for (JsonValue ch : channels) {
                    Channel channel = new Channel();
                    channel.sampler = a.samplers[ch.getInt("sampler")];
                    JsonValue target = ch.get("target");
                    channel.node = target.getInt("node", -1);
                    channel.path = target.getString("path");
                    a.channels[c++] = channel;
                }
            }
            animations[ai++] = a;

            for (Channel ch : a.channels) {
                if (ch.node < 0 || ch.node >= nodes.length) continue;
                Node n = nodes[ch.node];
                if (n.channels == null) {
                    n.channels = new Channel[]{ch};
                } else {
                    Channel[] next = new Channel[n.channels.length + 1];
                    System.arraycopy(n.channels, 0, next, 0, n.channels.length);
                    next[n.channels.length] = ch;
                    n.channels = next;
                }
            }
        }

        // glTF spec uses seconds; some exporters write milliseconds. Heuristic conversion.
        duration = 0f;
        for (Animation an : animations) {
            duration = Math.max(duration, an.duration);
        }
        if (duration > 100f) {
            duration /= 1000f;
            for (Animation an : animations) {
                for (Sampler s : an.samplers) {
                    for (int i = 0; i < s.times.length; i++) s.times[i] /= 1000f;
                }
            }
        }
    }

    private Fi resolve(Fi gltfFile, String uri) {
        // preserve the glTF's own file type (works for both the file system and the mod jar tree)
        Fi sibling = gltfFile.parent().child(uri);
        if (sibling.exists()) return sibling;

        String path = gltfFile.parent().path().isEmpty() ? uri : gltfFile.parent().child(uri).path();
        Fi f = Fi.get(path);
        if (!f.exists()) f = Core.files.internal(path);
        if (!f.exists()) f = Vars.tree.get(path);
        return f;
    }

    private static float readComponent(byte[] b, int off, int type, boolean normalized) {
        switch (type) {
            case BYTE:
                return normalized ? Math.max(b[off] / 127f, -1f) : b[off];
            case UNSIGNED_BYTE:
                return normalized ? (b[off] & 0xFF) / 255f : (b[off] & 0xFF);
            case SHORT: {
                short v = (short) ((b[off] & 0xFF) | ((b[off + 1] & 0xFF) << 8));
                return normalized ? Math.max(v / 32767f, -1f) : v;
            }
            case UNSIGNED_SHORT: {
                int v = (b[off] & 0xFF) | ((b[off + 1] & 0xFF) << 8);
                return normalized ? v / 65535f : v;
            }
            case UNSIGNED_INT:
                return (b[off] & 0xFF) | ((b[off + 1] & 0xFF) << 8) | ((b[off + 2] & 0xFF) << 16) | ((b[off + 3] & 0xFF) << 24);
            case FLOAT: {
                int bits = (b[off] & 0xFF) | ((b[off + 1] & 0xFF) << 8) | ((b[off + 2] & 0xFF) << 16) | ((b[off + 3] & 0xFF) << 24);
                return Float.intBitsToFloat(bits);
            }
            default: return 0f;
        }
    }

    public static int componentCount(String type) {
        switch (type) {
            case "SCALAR": return 1;
            case "VEC2": return 2;
            case "VEC3": return 3;
            case "VEC4": return 4;
            case "MAT4": return 16;
            default: return 0;
        }
    }

    public static int componentSize(int type) {
        switch (type) {
            case BYTE: case UNSIGNED_BYTE: return 1;
            case SHORT: case UNSIGNED_SHORT: return 2;
            case UNSIGNED_INT: case FLOAT: return 4;
            default: return 0;
        }
    }

    public void dispose() {
        if (images != null) {
            for (Pixmap p : images) {
                if (p != null && !p.isDisposed()) p.dispose();
            }
        }
    }

    // --- data classes -------------------------------------------------------

    public static class BufferView {
        public int buffer = -1;
        public int byteOffset;
        public int byteLength;
        public int byteStride;
    }

    public static class Accessor {
        public int buffer = -1;
        public int byteOffset;
        public int byteStride;
        public int componentType;
        public int count;
        public String type = "SCALAR";
        public boolean normalized;
    }

    public static class Material {
        public float r = 1f, g = 1f, b = 1f, a = 1f;
        public float er, eg, eb;
        public int baseImage = -1;
        public int baseTexCoord;
        public int emissiveImage = -1;
        public int emissiveTexCoord;
        public boolean doubleSided;
    }

    public static class Primitive {
        public int position = -1;
        public int normal = -1;
        public int color = -1;
        public int texcoord = -1;
        public int indices = -1;
        public int material = -1;
        public int mode = 4;
    }

    public static class MeshData {
        public Primitive[] primitives;
    }

    public static class Node {
        public String name;
        public int mesh = -1;
        public int[] children = new int[0];
        public boolean hasMatrix;
        public float[] matrix;
        public float[] translation = new float[3];
        public float[] rotation = new float[]{0f, 0f, 0f, 1f};
        public float[] scale = new float[]{1f, 1f, 1f};
        public Channel[] channels;
        // animated TRS scratch (preallocated)
        public float[] animT = new float[3];
        public float[] animR = new float[4];
        public float[] animS = new float[3];
        public final float[] local = new float[16];
    }

    public static class Channel {
        public Sampler sampler;
        public int node = -1;
        public String path;
    }

    public static class Sampler {
        public int input;
        public int output;
        public String interpolation = "LINEAR";
        public float[] times;
        public float[] values;
        public int comps;
    }

    public static class Animation {
        public Channel[] channels;
        public Sampler[] samplers;
        public float duration;
    }

    private static final class TmpVars {
        static final Quat quat = new Quat();
        static final Quat quat2 = new Quat();
    }
}