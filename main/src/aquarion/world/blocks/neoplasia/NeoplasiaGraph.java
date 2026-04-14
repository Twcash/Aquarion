package aquarion.world.blocks.neoplasia;

import aquarion.world.graphics.Renderer;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.IntMap;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.core.World;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.Tile;

import static aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock.activeNeoplasia;

//Anything that iterates over every neoplasia block shall be placed here.
//TODO chunking system??
public class NeoplasiaGraph {

    public static ObjectMap<Item, Seq<GenericNeoplasiaBlock.NeoplasiaBuild>> itemRequesters = new ObjectMap<>();
    public static ObjectMap<Item, Seq<GenericNeoplasiaBlock.NeoplasiaBuild>> itemHolders = new ObjectMap<>();
    public static ObjectMap<Item, Float> shortageTimers = new ObjectMap<>();
    public static ObjectMap<Item, Seq<GenericNeoplasiaBlock>> blockProducers = new ObjectMap<>();
    public static ObjectMap<Item, Seq<GenericNeoplasiaBlock.NeoplasiaBuild>> activeProducers = new ObjectMap<>();

    public static final Seq<ItemPacket> packets = new Seq<>();

    public static IntMap<NeoplasiaChunk> chunks = new IntMap<>();
    static int chunkSize = 8;

    public static void registerProducer(Item item, GenericNeoplasiaBlock block) {
        if (item == null || block == null) return;

        Seq<GenericNeoplasiaBlock> list = blockProducers.get(item);
        if (list == null) {
            list = new Seq<>();
            blockProducers.put(item, list);
        }

        list.addUnique(block);
    }

    public static void registerActiveProducer(Item item, GenericNeoplasiaBlock.NeoplasiaBuild build) {
        if (item == null || build == null) return;

        Seq<GenericNeoplasiaBlock.NeoplasiaBuild> list = activeProducers.get(item);
        if (list == null) {
            list = new Seq<>();
            activeProducers.put(item, list);
        }

        list.addUnique(build);
    }

    public static boolean ensureProduction(Item item, ObjectSet<Item> visited, GenericNeoplasiaBlock.NeoplasiaBuild requester){
        if(item == null || requester == null) return false;
        if(!visited.add(item)) return false;

        Seq<GenericNeoplasiaBlock> possible = blockProducers.get(item);
        if(possible == null || possible.isEmpty()) return false;

        GenericNeoplasiaBlock chosen = possible.first();

        if(chosen.itemCost == null){
            return true; // base resource
        }

        boolean canEventuallyBuild = true;

        for(ItemStack stack : chosen.itemCost){
            int have = requester.items.get(stack.item);
            if(have < stack.amount){
                boolean ok = ensureProduction(stack.item, visited, requester);
                requester.requestItem(stack.item, stack.amount - have);
                if(!ok) canEventuallyBuild = false;
            }
        }
        return canEventuallyBuild;
    }
    public static void update() {

        if (Vars.state.isPaused()) return;

        for (var entry : itemRequesters){
            Item item = entry.key;
            Seq<GenericNeoplasiaBlock.NeoplasiaBuild> reqs = entry.value;

            if (reqs.isEmpty()) continue;

            Seq<GenericNeoplasiaBlock.NeoplasiaBuild> producers = activeProducers.get(item);

            boolean hasSupply = false;

            if (producers != null) {
                for (var p : producers) {
                    if (p != null && p.isProducing(item) && p.tile != null) {
                        hasSupply = true;
                        break;
                    }
                }
            }

            float timer = shortageTimers.get(item, 0f);

            if (!hasSupply){
                timer += Time.delta;
            } else {
                timer = 0f;
            }

            shortageTimers.put(item, timer);

            if (timer > 180f){
                trySpawnProducer(reqs.first(), item);
                shortageTimers.put(item, 0f);
            }
        }
    }

    public static void draw() {
//        for (ItemPacket p : packets) {
//            Draw.z(Renderer.Layer.neoplasiaBase - 0.2f);
//            Draw.rect(p.item.fullIcon, p.drawX(), p.drawY(), 0);
//        }
    }

    public static void registerHolder(Item item, GenericNeoplasiaBlock.NeoplasiaBuild build) {
        Seq<GenericNeoplasiaBlock.NeoplasiaBuild> list = itemHolders.get(item);
        if (list == null) {
            list = new Seq<>();
            itemHolders.put(item, list);
        }
        list.addUnique(build);
        register(build);
    }

    public static void register(GenericNeoplasiaBlock.NeoplasiaBuild build) {
        int cx = chunkX(build.tile.x);
        int cy = chunkY(build.tile.y);

        int key = chunkKey(cx, cy);

        NeoplasiaChunk chunk = chunks.get(key);

        if (chunk == null) {
            chunk = new NeoplasiaChunk();
            chunks.put(key, chunk);
        }

        chunk.builds.addUnique(build);
    }

    public static GenericNeoplasiaBlock.NeoplasiaBuild findBlockBuilder(Building requester){
        GenericNeoplasiaBlock.NeoplasiaBuild best = null;
        float bestScore = -Float.MAX_VALUE;

        for (var entry : chunks) {
            NeoplasiaChunk chunk = entry.value;

            for (var other : chunk.builds) {
                if (other.tile == null) continue;
                if (Mathf.dst2(requester.x, requester.y, other.x, other.y) > 500) continue;

                float score = other.amount;
                score -= requester.dst2(other) * 0.01f;

                if (score > bestScore) {
                    bestScore = score;
                    best = other;
                }
            }
        }

        return best;
    }

    public static Building stepToward(Building from, Building target) {
        Building best = null;
        float bestScore = Float.MAX_VALUE;
        float currentDst = Mathf.dst2(from.x, from.y, target.x, target.y);

        for (Point2 p : Geometry.d4) {
            Tile otherTile = Vars.world.tile(from.tile.x + p.x, from.tile.y + p.y);
            if (otherTile == null) continue;
            if (otherTile.solid() || otherTile.floor().isDeep()) continue;

            if (!(otherTile.build instanceof GenericNeoplasiaBlock.NeoplasiaBuild build)) continue;
            if (build.items.total() >= build.block.itemCapacity) continue;

            float penalty = 0f;

            if (target instanceof GenericNeoplasiaBlock.NeoplasiaBuild tBuild) {
                if (!tBuild.requestedQueue.contains(s -> s.item == from.items.first())) {
                    penalty += 50f;
                }
            }

            float dst = Mathf.dst2(otherTile.worldx(), otherTile.worldy(), target.x, target.y) + penalty;

            if ((dst < bestScore && dst < currentDst) || best == null) {
                bestScore = dst;
                best = build;
            }
        }

        return best;
    }


    private static boolean validTile(GenericNeoplasiaBlock.NeoplasiaBuild build){
        if(build == null || build.tile == null) return false;
        if(build.floor() != null && build.floor().itemDrop != null) return false;
        if(build.tile.overlay() != null && build.tile.overlay().itemDrop != null) return false;
        if(build.tile.solid()) return false;
        return !build.tile.floor().isDeep();
    }
    public static void trySpawnProducer(GenericNeoplasiaBlock.NeoplasiaBuild requester, Item item){
        trySpawnProducer(requester, item, new ObjectSet<>());
    }

    private static void trySpawnProducer(GenericNeoplasiaBlock.NeoplasiaBuild requester, Item item, ObjectSet<Item> visited){
        if(requester == null || item == null) return;
        if(!visited.add(item)) return;

        Seq<GenericNeoplasiaBlock> possible = blockProducers.get(item);
        if(possible == null || possible.isEmpty()) return;

        GenericNeoplasiaBlock chosen = possible.first();

        boolean missingDependencies = false;

        if(chosen.itemCost != null){
            for(ItemStack stack : chosen.itemCost){
                int have = requester.items.get(stack.item);

                if(have < stack.amount){
                    requester.requestItem(stack.item, stack.amount - have);
                    trySpawnProducer(requester, stack.item, visited);

                    missingDependencies = true;
                }
            }
        }

        GenericNeoplasiaBlock.NeoplasiaBuild builder = findBlockBuilder(requester);
        if(builder == null || builder.tile == null) return;
        if(!validTile(builder)) return;

        if(builder.amount < chosen.cost) return;

        if(chosen.itemCost != null && !builder.hasItemCost(chosen.itemCost)){
            return;
        }

        builder.consumeItemCost(chosen.itemCost);
        builder.amount -= chosen.cost;
        builder.tile.setBlock(chosen, builder.team);
        builder.producerRequestCooldown = 10f;
    }

    static class NeoplasiaChunk {
        Seq<GenericNeoplasiaBlock.NeoplasiaBuild> builds = new Seq<>();
    }

    static int chunkX(int tileX) {
        return tileX / chunkSize;
    }

    static int chunkY(int tileY) {
        return tileY / chunkSize;
    }

    static int chunkKey(int cx, int cy) {
        return (cx << 16) | (cy & 0xFFFF);
    }
}