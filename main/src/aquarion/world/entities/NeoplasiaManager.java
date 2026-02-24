package aquarion.world.entities;

import aquarion.gen.NeoplasiaUpdater;
import aquarion.gen.NeoplasiaUpdaterc;
import aquarion.world.blocks.neoplasia.NeoplasiaSource;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.EventType;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.world.*;
import mindustry.world.blocks.environment.Floor;

public class NeoplasiaManager {
    private final ObjectFloatMap<Tile> amount = new ObjectFloatMap<>();
    private final Seq<Tile> active = new Seq<>();
    public static final NeoplasiaManager instance = new NeoplasiaManager();

    // tuning
    private final float flowRate = 0.02f;
    private final float flowThreshold = 0.4f;
    private final float maxFlow = 0.15f;
    private final float oreBoost = 0.02f;
    private final float maxHeight = 400f;
    private @Nullable NeoplasiaUpdater update;
    private final float resistancePerSize = 2f;
    private final float damageScale = 0.6f;

    public NeoplasiaManager(){
        Events.on(WorldLoadEvent.class, e -> {
            amount.clear();
            active.clear();

            if(update == null || !update.isAdded()){
                update = NeoplasiaUpdater.create();
                update.add();
            }
        });

        update = NeoplasiaUpdater.create();
        NeoplasiaUpdater.manager = this;
        update.add();
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
        Log.info("UP HAHAH");
        Seq<Tile> nextActive = new Seq<>(active.size);

        for(int i = 0; i < active.size; i++){
            Tile tile = active.get(i);

            float current = amount.get(tile, 0f);
            if(current <= 0f) continue;

            // ore growth
            if(tile.overlay() != null && tile.overlay().itemDrop != null){
                current += oreBoost * tile.overlay().itemDrop.hardness * Time.delta;
            }

            current = Mathf.clamp(current, 0f, maxHeight);
            amount.put(tile, current);

            // damage if overflowing block
            if(tile.build != null && !(tile.build instanceof NeoplasiaSource.NeoplasiaSourceBuild)){
                float resistance = blockResistance(tile);
                float overflow = current - resistance;

                if(overflow > 0f){
                    tile.build.damage(overflow * damageScale * Time.delta);
                }
            }

            // find lowest neighbor to flow into
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
                    float flow = Math.min(diff * flowRate, maxFlow);

                    amount.put(tile, current - flow);
                    amount.put(lowest, Mathf.clamp(amount.get(lowest, 0f) + flow, 0f, maxHeight));

                    nextActive.add(lowest);
                }
            }

            if(amount.get(tile, 0f) > 0.001f){
                nextActive.add(tile);
            }
        }

        active.clear();
        active.addAll(nextActive);
    }

    public void draw(){
        Draw.z(Layer.blockOver);
        for(int i = 0; i < active.size; i++){
            Tile tile = active.get(i);
            float h = amount.get(tile, 0f);
            if(h <= 0f) continue;
            Lines.square(tile.x, tile.y, Vars.tilesize/2f);

            float alpha = h / maxHeight;
            Draw.color(0.5f, 0f, 0.1f, alpha);
            Fill.square(tile.x, tile.y, Vars.tilesize / 2f);
        }
    }
}