package aquarion.world.blocks.neoplasia;

import aquarion.world.graphics.Renderer;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.core.World;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;

import static aquarion.world.blocks.neoplasia.GenericNeoplasiaBlock.activeNeoplasia;

//Anything that iterates over every neoplasia block shall be placed here.
//TODO chunking system??
public class NeoplasiaGraph {
    public static ObjectMap<Item, Seq<GenericNeoplasiaBlock.NeoplasiaBuild>> itemRequesters = new ObjectMap<>();
    public static ObjectMap<Item, Seq<GenericNeoplasiaBlock.NeoplasiaBuild>> producers = new ObjectMap<>();
    public static final Seq<ItemPacket> packets = new Seq<>();
    public static ObjectMap<Item, Seq<GenericNeoplasiaBlock.NeoplasiaBuild>> itemHolders = new ObjectMap<>();
    public static ObjectMap<Item, Seq<GenericNeoplasiaBlock>> itemProducers = new ObjectMap<>();

    public static void registerProducer(Item item, GenericNeoplasiaBlock block) {
        Seq<GenericNeoplasiaBlock> list = itemProducers.get(item);
        if (list == null) {
            list = new Seq<>();
            itemProducers.put(item, list);
        }
        list.add(block);
    }

    public static void update() {
        Events.on(EventType.WorldLoadEvent.class, t -> {
            packets.clear();
        });
        if (Vars.state.isPaused()) return;
        for (int i = packets.size - 1; i >= 0; i--) {
            ItemPacket p = packets.get(i);

            p.update();

            if (p.arrived()) {
                if (p.target != null) {
                    p.target.handleItem(null, p.item);
                }

                packets.remove(i);
            }
        }
    }

    public static void draw() {
        for (ItemPacket p : packets) {
            Draw.z(Renderer.Layer.neoplasiaBase - 0.2f);
            Draw.rect(p.item.fullIcon, p.drawX(), p.drawY(), 0);
        }
    }

    public static int totalAvailable(Item item) {
        int total = 0;

        for (GenericNeoplasiaBlock.NeoplasiaBuild b : activeNeoplasia) {
            total += b.items.get(item);
        }

        return total;
    }

    public static GenericNeoplasiaBlock.NeoplasiaBuild findProducer(Item item, Building requester) {
        Seq<GenericNeoplasiaBlock.NeoplasiaBuild> seq = producers.get(item);
        if (seq == null || seq.isEmpty()) return null;

        GenericNeoplasiaBlock.NeoplasiaBuild best = null;
        float bestDst = Float.MAX_VALUE;

        for (GenericNeoplasiaBlock.NeoplasiaBuild b : seq) {
            if (b.items.get(item) <= 0) continue;

            float d = requester.dst2(b);
            if (d < bestDst) {
                bestDst = d;
                best = b;
            }
        }

        return best;
    }

    public static void registerHolder(Item item, GenericNeoplasiaBlock.NeoplasiaBuild build) {
        Seq<GenericNeoplasiaBlock.NeoplasiaBuild> list = itemHolders.get(item);
        if (list == null) {
            list = new Seq<>();
            itemHolders.put(item, list);
        }

        list.addUnique(build);
    }

    public static int countProducers(GenericNeoplasiaBlock block) {
        // Return number of active producers of this block
        int count = 0;
        for (GenericNeoplasiaBlock.NeoplasiaBuild n : activeNeoplasia) {
            if (n.tile.block() == block) count++;
        }
        return count;
    }

    public static GenericNeoplasiaBlock.NeoplasiaBuild findBlockBuilder(Building requester) {
        GenericNeoplasiaBlock.NeoplasiaBuild best = null;
        float bestScore = -Float.MAX_VALUE;

        for (GenericNeoplasiaBlock.NeoplasiaBuild other : activeNeoplasia) {
            if (other.tile == null) continue;

            float score = other.amount;
            score -= requester.dst2(other) * 0.01f;

            if (score > bestScore) {
                bestScore = score;
                best = other;
            }
        }

        return best;
    }

    public static void send(Item item, GenericNeoplasiaBlock.NeoplasiaBuild from, GenericNeoplasiaBlock.NeoplasiaBuild to) {
        if (from == null || to == null) return;
        ItemPacket packet = new ItemPacket(item, from, to);
        packets.add(packet);
    }

    public static Building stepToward(Building from, Building target){
        Building best = null;
        float bestScore = Float.MAX_VALUE;
        float currentDst = Mathf.dst2(from.x, from.y, target.x, target.y);
        for(Point2 p : Geometry.d4){
            Tile otherTile = Vars.world.tile(from.tile.x + p.x, from.tile.y + p.y);
            if(otherTile == null) continue;
            if(otherTile.solid() || otherTile.floor().isDeep()) continue;
            if(!(otherTile.build instanceof GenericNeoplasiaBlock.NeoplasiaBuild build)) continue;
            if(build.items.total() >= build.block.itemCapacity) continue;
            float penalty = 0f;
            if(target instanceof GenericNeoplasiaBlock.NeoplasiaBuild tBuild){
                if(!tBuild.requestedQueue.contains(s -> s.item == from.items.first())) penalty += 50f;
            }
            float dst = Mathf.dst2(otherTile.worldx(), otherTile.worldy(), target.x, target.y) + penalty;
            if((dst < bestScore && dst < currentDst) || best == null){
                bestScore = dst;
                best = build;
            }
        }

        return best;
    }
}