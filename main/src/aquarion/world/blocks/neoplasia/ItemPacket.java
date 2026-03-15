package aquarion.world.blocks.neoplasia;

import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Tile;
public class ItemPacket {

    public Item item;

    public GenericNeoplasiaBlock.NeoplasiaBuild current;
    public GenericNeoplasiaBlock.NeoplasiaBuild next;
    public GenericNeoplasiaBlock.NeoplasiaBuild target;

    public float progress = 0f;
    public float speed = 0.08f;

    boolean finished = false;

    public ItemPacket(Item item, GenericNeoplasiaBlock.NeoplasiaBuild from, GenericNeoplasiaBlock.NeoplasiaBuild to){
        this.item = item;
        this.current = from;
        this.target = to;
    }

    public void update(){
        if(current == null || target == null) return;

        if(next == null){
            Building step = NeoplasiaGraph.stepToward(current, target);

            if(!(step instanceof GenericNeoplasiaBlock.NeoplasiaBuild build)){
                next = target;
            }else{
                next = build;
            }
        }

        progress += Time.delta * speed;

        if(progress >= 1f){
            current = next;
            next = null;
            progress = 0f;

            if(current == target){
                target.handleItem(null, item);
                finished = true;
            }
        }
    }

    public boolean arrived(){
        return finished;
    }

    public float drawX(){
        if(next == null) return current.x;
        return Mathf.lerp(current.x, next.x, progress);
    }

    public float drawY(){
        if(next == null) return current.y;
        return Mathf.lerp(current.y, next.y, progress);
    }
}