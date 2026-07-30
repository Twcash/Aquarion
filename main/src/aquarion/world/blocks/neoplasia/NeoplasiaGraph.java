package aquarion.world.blocks.neoplasia;

import arc.struct.*;

import mindustry.gen.Building;
import mindustry.world.Tile;

import aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock.NeoplasiaBuild;
import static aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock.activeNeoplasia;

public class NeoplasiaGraph {
    public static int chunkSize = 32;

    public static Seq<NeoplasmHeart.NeoplasmHeartBuild> hearts = new Seq<>();
    public static ObjectIntMap<NeoplasiaBuild> buildPulseIds = new ObjectIntMap<>();
    public static ObjectMap<String, NeoplasiaChunk> chunks = new ObjectMap<>();
    public static int pulseId = 0;
    public static float pulseTimer = 0f;
    public static float pulseInterval = 60f;
    public static boolean gracePeriod = true;

    public static String chunkKey(int cx, int cy) {
        return cx + "," + cy;
    }

    public static int chunkX(int x) {
        return Math.floorDiv(x, chunkSize);
    }

    public static int chunkY(int y) {
        return Math.floorDiv(y, chunkSize);
    }

    public static void register(Building b) {
        if (!(b instanceof NeoplasiaBuild nb)) return;
        Tile tile = b.tile;
        int cx = chunkX(tile.x);
        int cy = chunkY(tile.y);
        String key = chunkKey(cx, cy);
        NeoplasiaChunk chunk = chunks.get(key);
        if (chunk == null) {
            chunk = new NeoplasiaChunk(cx, cy);
            chunks.put(key, chunk);
        }
        chunk.builds.add(nb);
        if (activeNeoplasia != null) {
            activeNeoplasia.add(nb);
        }
    }

    public static class NeoplasiaChunk {
        public int cx, cy;
        public Seq<NeoplasiaBuild> builds = new Seq<>();

        public NeoplasiaChunk(int cx, int cy) {
            this.cx = cx;
            this.cy = cy;
        }
    }

    public static void update() {
        pulseTimer += 1f;
        if (pulseTimer >= pulseInterval) {
            pulseTimer = 0;
            gracePeriod = false;
            int pid = ++pulseId;
            buildPulseIds.clear();
            for (NeoplasmHeart.NeoplasmHeartBuild heart : hearts) {
                Queue<NeoplasiaBuild> queue = new Queue<>();
                for (Building neighbor : heart.proximity) {
                    if (neighbor instanceof NeoplasiaBuild nb && buildPulseIds.get(nb, 0) != pid) {
                        buildPulseIds.put(nb, pid);
                        queue.addLast(nb);
                    }
                }
                while (queue.size > 0) {
                    NeoplasiaBuild current = queue.removeFirst();
                    for (Building neighbor : current.proximity) {
                        if (neighbor instanceof NeoplasiaBuild nb && buildPulseIds.get(nb, 0) != pid) {
                            buildPulseIds.put(nb, pid);
                            queue.addLast(nb);
                        }
                    }
                }
            }
        }
    }

    public static boolean isConnected(NeoplasiaBuild build) {
        if (gracePeriod) return true;
        if (hearts.isEmpty()) return true;
        return buildPulseIds.get(build, 0) == pulseId;
    }
}
