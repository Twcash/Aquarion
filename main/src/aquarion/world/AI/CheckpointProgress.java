package aquarion.world.AI;

import aquarion.gen.AquaMechc;
import arc.Events;
import arc.struct.IntMap;
import mindustry.game.EventType.ResetEvent;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.io.SaveFileReader;
import mindustry.io.SaveVersion;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/** Stores boss lifetime separately so existing mech entity save formats remain compatible. */
public class CheckpointProgress {
    private static final IntMap<Progress> pending = new IntMap<>();
    private static boolean registered;

    public static void register(){
        if(registered) return;
        registered = true;
        Events.on(ResetEvent.class, event -> pending.clear());
        SaveVersion.addCustomChunk("aquarion-checkpoint-progress", new SaveFileReader.CustomChunk(){
            @Override
            public void write(DataOutput stream) throws IOException{
                stream.writeInt(Groups.unit.count(unit -> unit instanceof AquaMechc));
                for(Unit unit : Groups.unit){
                    if(unit instanceof AquaMechc mech){
                        restore(unit);
                        stream.writeInt(unit.id);
                        stream.writeFloat(mech.checkpointTime());
                        stream.writeBoolean(mech.checkpointReleased());
                    }
                }
            }

            @Override
            public void read(DataInput stream) throws IOException{
                pending.clear();
                int count = stream.readInt();
                if(count < 0) throw new IOException("Invalid checkpoint progress count");
                for(int i = 0; i < count; i++){
                    int id = stream.readInt();
                    float elapsed = stream.readFloat();
                    boolean released = stream.readBoolean();
                    pending.put(id, new Progress(elapsed, released));
                }
            }
        });
    }

    public static void restore(Unit unit){
        if(unit instanceof AquaMechc mech){
            Progress progress = pending.remove(unit.id);
            if(progress != null){
                mech.checkpointTime(progress.elapsed);
                mech.checkpointReleased(progress.released);
            }
        }
    }

    private static class Progress {
        final float elapsed;
        final boolean released;

        Progress(float elapsed, boolean released){
            this.elapsed = elapsed;
            this.released = released;
        }
    }
}
