package aquarion.world.entities;

import arc.Events;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.Floor;

public class NeoplasiaManager {

    private final ObjectFloatMap<Tile> amount = new ObjectFloatMap<>();

    private final Seq<Tile> active = new Seq<>();
    //Look. I know they can be local But this feels more organized
    private final float flowRate = 0.02f;
    private final float flowThreshold = 0.4f;
    private final float maxFlow = 0.15f;
    private final float oreBoost = 0.02f;
    private final float maxHeight = 10f;

    private final float resistancePerSize = 2f;
    private final float damageScale = 0.6f;
    //I don't have a loading mechanism yet!!!
    public NeoplasiaManager(){
        Events.on(WorldLoadEvent.class, e -> {
            amount.clear();
            active.clear();
        });
    }

    public void add(Tile tile, float value){
        if(tile == null) return;

        float current = amount.get(tile, 0f);
        float next = Mathf.clamp(current + value, 0f, maxHeight);

        if(next <= 0.001f){
            amount.remove(tile, next);
        }else{
            amount.put(tile, next);
            active.add(tile);
        }
    }

    public float get(Tile tile){
        return amount.get(tile, 0f);
    }

    private float blockResistance(Tile tile){
        if(tile.build == null) return 0f;
        return tile.block().size * resistancePerSize;
    }

    public void update(){
        if(active.isEmpty()) return;

        Seq<Tile> nextActive = new Seq<>(active.size);

        for(int i = 0; i < active.size; i++){
            Tile tile = active.get(i);

            float current = amount.get(tile, 0f);
            if(current <= 0f) continue;

            if(tile.overlay() != null && tile.overlay().itemDrop != null){
                current += oreBoost * tile.overlay().itemDrop.hardness * Time.delta;
            }

            current = Mathf.clamp(current, 0f, maxHeight);
            amount.put(tile, current);

            // damage if overflowing block
            if(tile.build != null){
                float resistance = tile.block().size * resistancePerSize;
                float overflow = current - resistance;

                if(overflow > 0f){
                    tile.build.damage(overflow * damageScale * Time.delta);
                }
            }
            Tile lowest = null;
            float lowestAmount = current;

            for(Point2 p : Geometry.d4){
                Tile other = Vars.world.tile(tile.x + p.x, tile.y + p.y);
                if(other == null) continue;

                float resistance = blockResistance(other);

                if(current < resistance) continue;

                float otherAmount = amount.get(other, 0f);

                if(otherAmount < lowestAmount){
                    lowestAmount = otherAmount;
                    lowest = other;
                }
            }

            if(lowest != null){
                float diff = current - lowestAmount;

                if(diff > flowThreshold){
                    float flow = diff * flowRate;
                    if(flow > maxFlow) flow = maxFlow;

                    float newCurrent = current - flow;
                    float newOther = amount.get(lowest, 0f) + flow;

                    amount.put(tile, newCurrent);
                    amount.put(lowest, Mathf.clamp(newOther, 0f, maxHeight));

                    nextActive.add(lowest);
                    current = newCurrent;
                }
            }

            if(current > 0.001f){
                nextActive.add(tile);
            }
        }

        active.clear();
        active.addAll(nextActive);
    }

    public void draw(){
        for(int i = 0; i < active.size; i++){
            Tile tile = active.get(i);

            float h = amount.get(tile, 0f);
            if(h <= 0f) continue;

            float alpha = h / maxHeight;

            Draw.color(0.5f, 0f, 0.1f, alpha);
            Fill.square(tile.worldx(), tile.worldy(), Vars.tilesize / 2f);
        }
        Draw.color();
    }
}