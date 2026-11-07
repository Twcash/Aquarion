package aquarion.world.blocks.environment;

import arc.Events;
import arc.math.geom.Geometry;
import arc.struct.IntSeq;
import arc.struct.IntSet;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.Liquids;
import mindustry.game.EventType.*;
import mindustry.io.SaveFileReader;
import mindustry.io.SaveVersion;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static mindustry.Vars.*;

/** Persistent crater basins refill when water or slag later reaches their edge. */
public final class CraterFloods{
    private static final Seq<Basin> basins = new Seq<>();
    private static boolean registered;

    private CraterFloods(){}

    public static void register(){
        if(registered) return;
        registered = true;
        Events.on(ResetEvent.class, e -> basins.clear());
        Events.on(WorldLoadEvent.class, e -> basins.each(b -> b.dirty = true));
        Events.on(TileFloorChangeEvent.class, e -> {
            for(Basin basin : basins){
                if(basin.tiles.contains(e.tile.pos())) basin.dirty = true;
                else for(var direction : Geometry.d4){
                    Tile next = world.tile(e.tile.x + direction.x, e.tile.y + direction.y);
                    if(next != null && basin.tiles.contains(next.pos())){ basin.dirty = true; break; }
                }
            }
        });
        Events.run(Trigger.update, () -> {
            if(net.client() || !state.isGame() || state.isPaused()) return;
            for(Basin basin : basins) if(basin.dirty){ basin.dirty = false; fill(basin); }
        });
        SaveVersion.addCustomChunk("aquarion-crater-basins", new SaveFileReader.CustomChunk(){
            public void write(DataOutput stream) throws IOException{
                stream.writeInt(basins.size);
                for(Basin basin : basins){
                    stream.writeInt(basin.tiles.size);
                    for(var positions = basin.tiles.iterator(); positions.hasNext;) stream.writeInt(positions.next());
                }
            }
            public void read(DataInput stream) throws IOException{
                basins.clear();
                int count = stream.readInt();
                if(count < 0 || count > world.width() * world.height()) throw new IOException("Invalid basin count");
                for(int i = 0; i < count; i++){
                    Basin basin = new Basin();
                    int size = stream.readInt();
                    if(size < 0 || size > world.width() * world.height()) throw new IOException("Invalid basin size");
                    for(int j = 0; j < size; j++){
                        int pos = stream.readInt();
                        if(world.tile(pos) != null) basin.tiles.add(pos);
                    }
                    basins.add(basin);
                }
            }
        });
    }

    public static boolean allowed(Floor floor){
        return floor.isLiquid && (floor.liquidDrop == Liquids.water || floor.liquidDrop == Liquids.slag);
    }

    public static void track(IntSeq positions){
        if(net.client() || positions.isEmpty()) return;
        Basin basin = new Basin();
        for(int i = 0; i < positions.size; i++) basin.tiles.add(positions.get(i));
        basins.add(basin);
        fill(basin);
    }

    private static void fill(Basin basin){
        IntSet visited = new IntSet();
        Seq<Tile> queue = new Seq<>();
        // Water and slag contacts both produce the same dark-sand shallows.
        for(var positions = basin.tiles.iterator(); positions.hasNext;){
            int pos = positions.next();
            Tile tile = world.tile(pos);
            if(tile == null) continue;
            Floor source = allowed(tile.floor()) ? tile.floor() : null;
            if(source == null) for(var direction : Geometry.d4){
                Tile next = world.tile(tile.x + direction.x, tile.y + direction.y);
                if(next != null && allowed(next.floor())){ source = next.floor(); break; }
            }
            if(source != null){ visited.add(pos); queue.add(tile); }
        }
        for(int i = 0; i < queue.size; i++){
            Tile tile = queue.get(i);
            if(tile.overlay() != Blocks.spawn && tile.floor() != Blocks.darksandWater){
                tile.setFloorNet(Blocks.darksandWater, Blocks.air);
            }
            for(var direction : Geometry.d4){
                Tile next = world.tile(tile.x + direction.x, tile.y + direction.y);
                if(next != null && basin.tiles.contains(next.pos()) && visited.add(next.pos())){
                    queue.add(next);
                }
            }
        }
    }

    private static class Basin{
        final IntSet tiles = new IntSet();
        boolean dirty = true;
    }
}
