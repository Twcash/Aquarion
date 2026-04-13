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

    public static void ensureProduction(Item item, ObjectSet<Item> visited, GenericNeoplasiaBlock.NeoplasiaBuild requester) {
        if (item == null) return;
        if (visited.contains(item)) return;

        visited.add(item);

        Seq<GenericNeoplasiaBlock> possible = blockProducers.get(item);
        if (possible == null || possible.isEmpty()) return;

        GenericNeoplasiaBlock chosen = possible.first();

        if (chosen.itemCost != null) {
            for (ItemStack stack : chosen.itemCost) {
                ensureProduction(stack.item, visited, requester);
            }
        }
    }

    public static void update() {

        if (Vars.state.isPaused()) return;

        for (var entry : itemRequesters){
            Item item = entry.key;
            Seq<GenericNeoplasiaBlock.NeoplasiaBuild> reqs = entry.value;

            if (reqs.isEmpty()) continue;

            Seq<GenericNeoplasiaBlock.NeoplasiaBuild> producers = activeProducers.get(item);

            boolean hasSupply = producers != null && producers.contains(p -> p.isProducing(item));

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

    public static void trySpawnProducer(GenericNeoplasiaBlock.NeoplasiaBuild requester, Item item) {
        if (requester == null || requester.tile == null) return;

        Seq<GenericNeoplasiaBlock> possible = blockProducers.get(item);
        if (possible == null || possible.isEmpty()) return;

        GenericNeoplasiaBlock.NeoplasiaBuild builder = findBlockBuilder(requester);
        if (builder == null || builder.tile == null) return;
        if(builder.floor().itemDrop != null || builder.tile.overlay().itemDrop != null) return;
        if (builder.tile.solid() || builder.tile.floor().isDeep()) return;
        for (GenericNeoplasiaBlock block : possible) {
            if (block == null) continue;

            if (builder.amount < block.cost) continue;

            if (!builder.hasItemCost(block.itemCost)) {
                for (ItemStack stack : block.itemCost) {
                    int missing = stack.amount - builder.items.get(stack.item);
                    if (missing > 0) {
                        requester.requestItem(stack.item, missing);
                    }
                }
                continue;
            }

            builder.consumeItemCost(block.itemCost);
            builder.amount -= block.cost;

            builder.tile.setBlock(block, builder.team);

            builder.producerRequestCooldown = 60f;
            return;
        }
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