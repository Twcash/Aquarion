package aquarion.world.blocks.neoplasia;

import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.type.Item;
import mindustry.world.Tile;

public class ItemPacket{

    public Item item;

    public Seq<Tile> path = new Seq<>();

    public Tile fromTile;
    public Tile toTile;

    public int index = 0;

    public float progress = 0f;
    public float speed = 3f;

    public GenericNeoplasiaBlock.NeoplasiaBuild target;

    public ItemPacket(Item item, Tile start, Tile targetTile, GenericNeoplasiaBlock.NeoplasiaBuild target){
        this.item = item;
        this.target = target;

        path = findPath(start, targetTile);

        fromTile = start;

        if(path.isEmpty()){
            toTile = targetTile;
        }else{
            toTile = path.first();
        }
    }

    Seq<Tile> findPath(Tile start, Tile target){
        ObjectSet<Tile> visited = new ObjectSet<>();
        Queue<Tile> queue = new Queue<>();
        ObjectMap<Tile, Tile> parent = new ObjectMap<>();

        queue.addLast(start);
        visited.add(start);

        while(queue.size > 0){
            Tile current = queue.removeFirst();

            if(current == target) break;

            for(int i = 0; i < 4; i++){
                Tile next = current.nearby(i);

                if(next == null) continue;
                if(!(next.build instanceof GenericNeoplasiaBlock.NeoplasiaBuild)) continue;
                if(visited.contains(next)) continue;

                visited.add(next);
                parent.put(next, current);
                queue.addLast(next);
            }
        }

        Seq<Tile> path = new Seq<>();

        Tile cur = target;

        while(cur != null && cur != start){
            path.add(cur);
            cur = parent.get(cur);
        }

        path.reverse();

        return path;
    }

    public void update(){
        if(toTile == null) return;

        progress += speed * Time.delta / 60f;

        if(progress >= 1f){
            progress = 0f;

            fromTile = toTile;

            index++;

            if(index < path.size){
                toTile = path.get(index);
            }else{
                toTile = null;
            }
        }
    }

    public boolean arrived(){
        return toTile == null;
    }

    public float drawX(){
        if(toTile == null) return fromTile.worldx();
        return Mathf.lerp(fromTile.worldx(), toTile.worldx(), progress);
    }

    public float drawY(){
        if(toTile == null) return fromTile.worldy();
        return Mathf.lerp(fromTile.worldy(), toTile.worldy(), progress);
    }
}