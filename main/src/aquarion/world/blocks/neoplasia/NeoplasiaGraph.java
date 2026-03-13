package aquarion.world.blocks.neoplasia;

import aquarion.world.graphics.Renderer;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.core.GameState;
import mindustry.core.World;
import mindustry.game.EventType;
import mindustry.type.Item;
import mindustry.world.Tile;

//This graph will only be updated via the blocks.
public class NeoplasiaGraph{

    public static final Seq<ItemPacket> packets = new Seq<>();

    public static void update(){
        Events.on(EventType.WorldLoadEvent.class, t ->{
            packets.clear();
        });
        if(Vars.state.isPaused()) return;
        for(int i = packets.size - 1; i >= 0; i--){
            ItemPacket p = packets.get(i);

            p.update();

            if(p.arrived()){
                if(p.target != null){
                    p.target.handleItem(null, p.item);
                }

                packets.remove(i);
            }
        }
    }

    public static void draw(){
        for(ItemPacket p : packets){
            Draw.z(Renderer.Layer.neoplasiaBase - 0.2f);
            Draw.rect(p.item.fullIcon, p.drawX(), p.drawY(), 0);
        }
    }

    public static void send(Item item, GenericNeoplasiaBlock.NeoplasiaBuild from, GenericNeoplasiaBlock.NeoplasiaBuild to){
        if(from == null || to == null) return;

        ItemPacket packet = new ItemPacket(item, from.tile, to.tile, to);
        packets.add(packet);
    }
}
