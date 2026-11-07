package aquarion.world.blocks.environment;

import arc.Events;
import arc.struct.IntMap;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType.ResetEvent;
import mindustry.game.EventType.TileFloorChangeEvent;
import mindustry.io.SaveFileReader;
import mindustry.io.SaveVersion;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OverlayFloor;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/** A removable floor layer for any world tile, without changing shared Floor content. */
public final class TemporaryFloors{
    private static final IntMap<Layer> layers = new IntMap<>();
    private static boolean registered;

    private TemporaryFloors(){}

    public static void register(){
        if(registered) return;
        registered = true;
        Events.on(ResetEvent.class, event -> layers.clear());
        Events.on(TileFloorChangeEvent.class, event -> {
            Layer layer = layers.get(event.tile.pos());
            if(layer != null && event.floor != layer.placed) layers.remove(event.tile.pos());
        });
        SaveVersion.addCustomChunk("aquarion-temporary-floors", new SaveFileReader.CustomChunk(){
            @Override
            public void write(DataOutput stream) throws IOException{
                // Discard entries whose terrain was replaced by another system.
                var iterator = layers.entries();
                while(iterator.hasNext()){
                    var entry = iterator.next();
                    Tile tile = Vars.world.tile(entry.key);
                    if(tile == null || tile.floor() != entry.value.placed) iterator.remove();
                }
                stream.writeInt(layers.size);
                for(var entry : layers){
                    stream.writeInt(entry.key);
                    stream.writeUTF(entry.value.base.name);
                    stream.writeUTF(entry.value.overlay.name);
                    stream.writeUTF(entry.value.placed.name);
                }
            }

            @Override
            public void read(DataInput stream) throws IOException{
                layers.clear();
                int count = stream.readInt();
                if(count < 0 || count > Vars.world.width() * Vars.world.height()){
                    throw new IOException("Invalid temporary floor count: " + count);
                }
                for(int i = 0; i < count; i++){
                    int pos = stream.readInt();
                    Block base = Vars.content.block(stream.readUTF());
                    Block overlay = Vars.content.block(stream.readUTF());
                    Block placed = Vars.content.block(stream.readUTF());
                    Tile tile = Vars.world.tile(pos);
                    if(tile != null && base instanceof Floor baseFloor && !(base instanceof OverlayFloor)
                        && overlay instanceof Floor overlayFloor && placed instanceof Floor placedFloor
                        && tile.floor() == placedFloor){
                        layers.put(pos, new Layer(baseFloor, overlayFloor, placedFloor));
                    }
                }
            }
        });
    }

    /** Server-side placement. Replacing a temporary floor retains the original terrain. */
    public static boolean place(Tile tile, Floor floor){
        if(Vars.net.client() || tile == null || floor == null || floor == Blocks.air
            || floor instanceof OverlayFloor || Vars.world.tile(tile.pos()) != tile) return false;
        Layer previous = layer(tile);
        if(tile.floor() == floor) return false;
        layers.put(tile.pos(), new Layer(previous == null ? tile.floor() : previous.base,
            previous == null ? tile.overlay() : previous.overlay, floor));
        // Cover ore while the temporary floor is present; restore it on removal.
        tile.setFloorNet(floor, Blocks.air);
        return true;
    }

    /** Server-side removal. Buildings on the tile are left in place. */
    public static boolean remove(Tile tile){
        if(Vars.net.client()) return false;
        Layer layer = layer(tile);
        if(layer == null) return false;
        layers.remove(tile.pos());
        tile.setFloorNet(layer.base, layer.overlay);
        return true;
    }

    public static boolean has(Tile tile){
        return layer(tile) != null;
    }

    public static Floor baseFloor(Tile tile){
        Layer layer = layer(tile);
        return layer == null ? (tile == null ? null : tile.floor()) : layer.base;
    }

    private static Layer layer(Tile tile){
        if(tile == null || Vars.world.tile(tile.pos()) != tile) return null;
        Layer layer = layers.get(tile.pos());
        if(layer != null && tile.floor() != layer.placed){
            layers.remove(tile.pos());
            return null;
        }
        return layer;
    }

    private static class Layer{
        final Floor base, overlay, placed;

        Layer(Floor base, Floor overlay, Floor placed){
            this.base = base;
            this.overlay = overlay;
            this.placed = placed;
        }
    }
}
