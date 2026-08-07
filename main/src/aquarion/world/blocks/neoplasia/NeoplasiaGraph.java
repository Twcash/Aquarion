package aquarion.world.blocks.neoplasia;

import arc.struct.*;

import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.Tile;

import aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock.NeoplasiaBuild;
import static aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock.activeNeoplasia;

public class NeoplasiaGraph {
    public static int chunkSize = 32;

    public static Seq<NeoplasmHeart.NeoplasmHeartBuild> hearts = new Seq<>();
    public static NeoplasmHeart.NeoplasmHeartBuild primeHeart = null;
    public static ObjectIntMap<NeoplasiaBuild> compMap = new ObjectIntMap<>();
    public static final IntIntMap heartCounts = new IntIntMap();
    public static final IntSeq comps = new IntSeq();
    public static final IntMap<NeoplasiaBuild> biggestBlobs = new IntMap<>();
    public static final IntFloatMap biggestAmounts = new IntFloatMap();
    public static LongMap<NeoplasiaChunk> chunks = new LongMap<>();
    public static final Queue<NeoplasiaBuild> pulseQueue = new Queue<>();
    public static float pulseTimer = 0f;
    public static float pulseInterval = 30f;
    public static float spawnCooldown = 0f;
    public static float spawnInterval = 600f;
    public static float heartCost = 150f;
    public static GenericNeoplasiaBlock heartBlock;

    public static long chunkKey(int cx, int cy) {
        return ((long) cx << 32) | (cy & 0xffffffffL);
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
        long key = chunkKey(cx, cy);
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

    public static void addHeart(NeoplasmHeart.NeoplasmHeartBuild heart) {
        hearts.add(heart);
        if (primeHeart == null || primeHeart.dead() || primeHeart.dissolving) {
            primeHeart = heart;
            heart.isPrime = true;
        }
    }

    public static void removeHeart(NeoplasmHeart.NeoplasmHeartBuild heart) {
        hearts.remove(heart);
        if (primeHeart == heart) {
            primeHeart = null;
            heart.isPrime = false;
        }
    }

    public static int techLevel() {
        if (primeHeart == null || primeHeart.dead() || primeHeart.dissolving) return 0;
        return primeHeart.techLevel();
    }

    public static boolean isConnected(NeoplasiaBuild build) {
        if (build instanceof NeoplasmHeart.NeoplasmHeartBuild hb) {
            return !hb.dissolving;
        }
        int comp = compMap.get(build, 0);
        return comp != 0 && heartCounts.get(comp, 0) > 0;
    }

    public static void update() {
        pulseTimer += 1f;
        if (pulseTimer < pulseInterval) return;
        pulseTimer = 0f;

        if (primeHeart == null || primeHeart.dead() || primeHeart.dissolving) {
            primeHeart = null;
            NeoplasmHeart.NeoplasmHeartBuild best = null;
            for (int i = 0; i < hearts.size; i++) {
                NeoplasmHeart.NeoplasmHeartBuild heart = hearts.get(i);
                if (heart.dead() || heart.dissolving) continue;
                if (heart.isPrime) {
                    best = heart;
                    break;
                }
                if (best == null || heart.amount > best.amount) {
                    best = heart;
                }
            }
            if (best != null) {
                primeHeart = best;
                best.isPrime = true;
            }
        }

        compMap.clear();
        heartCounts.clear();
        biggestBlobs.clear();
        biggestAmounts.clear();
        comps.clear();

        int cid = 0;
        Queue<NeoplasiaBuild> queue = pulseQueue;
        for (int i = 0; i < activeNeoplasia.size; i++) {
            NeoplasiaBuild seed = activeNeoplasia.get(i);
            if (seed.dead() || compMap.get(seed, 0) != 0) continue;
            cid++;
            comps.add(cid);
            queue.clear();
            compMap.put(seed, cid);
            queue.addLast(seed);
            while (queue.size > 0) {
                NeoplasiaBuild current = queue.removeFirst();
                if (current instanceof NeoplasmHeart.NeoplasmHeartBuild hb && !hb.dissolving) {
                    heartCounts.increment(cid);
                }
                float amt = current.amount;
                if (biggestAmounts.get(cid, 0f) < amt) {
                    biggestAmounts.put(cid, amt);
                    biggestBlobs.put(cid, current);
                }
                for (Building neighbor : current.proximity) {
                    if (!(neighbor instanceof NeoplasiaBuild nb)) continue;
                    if (nb.dead() || compMap.get(nb, 0) != 0) continue;
                    compMap.put(nb, cid);
                    queue.addLast(nb);
                }
            }
        }

        if (spawnCooldown > 0f) spawnCooldown -= pulseInterval;
        if (heartBlock != null) {
            for (int i = 0; i < comps.size; i++) {
                if (spawnCooldown > 0f) break;
                int comp = comps.get(i);
                if (heartCounts.get(comp, 0) > 0) continue;
                NeoplasiaBuild big = biggestBlobs.get(comp);
                if (big != null && big.amount >= heartCost) {
                    spawnCooldown = spawnInterval;
                    big.upgradeTo(heartBlock);
                    heartCounts.increment(comp);
                }
            }
        }

        for (int i = 0; i < comps.size; i++) {
            int comp = comps.get(i);
            if (heartCounts.get(comp, 0) < 2) continue;
            NeoplasmHeart.NeoplasmHeartBuild keep = null;
            for (int h = 0; h < hearts.size; h++) {
                NeoplasmHeart.NeoplasmHeartBuild heart = hearts.get(h);
                if (heart.dead() || heart.dissolving || compMap.get(heart, 0) != comp) continue;
                if (keep == null) keep = heart;
                if (primeHeart != null && !primeHeart.dead() && !primeHeart.dissolving && compMap.get(primeHeart, 0) == comp) {
                    keep = primeHeart;
                    break;
                }
            }
            if (keep == null) continue;
            for (int h = 0; h < hearts.size; h++) {
                NeoplasmHeart.NeoplasmHeartBuild heart = hearts.get(h);
                if (heart.dead() || heart.dissolving || heart == keep || compMap.get(heart, 0) != comp) continue;
                heart.dissolving = true;
                hearts.remove(h);
                h--;
            }
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

    public static void reset() {
        hearts.clear();
        primeHeart = null;
        compMap.clear();
        heartCounts.clear();
        biggestBlobs.clear();
        biggestAmounts.clear();
        comps.clear();
        chunks.clear();
        activeNeoplasia.clear();
        pulseTimer = 0f;
        spawnCooldown = 0f;
        if (Vars.state.isGame() && Vars.world.tiles != null) {
            for (Tile tile : Vars.world.tiles) {
                if (tile.build instanceof NeoplasmHeart.NeoplasmHeartBuild hb) {
                    register(hb);
                    hearts.add(hb);
                    if (hb.isPrime) primeHeart = hb;
                } else if (tile.build instanceof NeoplasiaBuild nb) {
                    register(nb);
                }
            }
            if (primeHeart == null && hearts.size > 0) {
                primeHeart = hearts.first();
                primeHeart.isPrime = true;
            }
        }
    }
}
