package aquarion.world.AI;

import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.world.*;

import java.util.PriorityQueue;

import static mindustry.Vars.*;
public class FishAIPathfinder {
    private static final int maxSearch = 5000;

    public static @Nullable Seq<Tile> findPath(Tile start, Tile target){
        if(start == null || target == null) return null;

        ObjectMap<Tile, Tile> cameFrom = new ObjectMap<>();
        ObjectMap<Tile, Float> costSoFar = new ObjectMap<>();

        PriorityQueue<Tile> frontier = new PriorityQueue<>((a, b) ->
                Float.compare(costSoFar.get(a, Float.MAX_VALUE), costSoFar.get(b, Float.MAX_VALUE)) +
                        Float.compare(estimate(a, target), estimate(b, target))
        );

        frontier.add(start);
        costSoFar.put(start, 0f);

        int searchCount = 0;

        while(!frontier.isEmpty() && searchCount++ < maxSearch){
            Tile current = frontier.poll();

            if(current == target){
                return reconstructPath(cameFrom, start, target);
            }

            for(Point2 p : Geometry.d4){
                Tile next = world.tile(current.x + p.x, current.y + p.y);
                if(next == null) continue;

                float danger = FishAIDangerMap.getDanger(next.worldx(), next.worldy());
                float newCost = costSoFar.get(current) + 1f + danger * 10f;

                if(danger >= 1f) continue; // completely avoid max danger

                if(!costSoFar.containsKey(next) || newCost < costSoFar.get(next)){
                    costSoFar.put(next, newCost);
                    cameFrom.put(next, current);
                    frontier.add(next);
                }
            }
        }

        return null;
    }

    private static float estimate(Tile a, Tile b){
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private static Seq<Tile> reconstructPath(ObjectMap<Tile, Tile> cameFrom, Tile start, Tile end){
        Seq<Tile> path = new Seq<>();
        Tile current = end;

        while(current != start){
            path.add(current);
            current = cameFrom.get(current);
            if(current == null) break; // path broke
        }

        path.reverse();
        return path;
    }
}