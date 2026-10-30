package aquarion.world.entities.bullet;

import arc.Events;
import arc.struct.Seq;
import mindustry.game.EventType.ResetEvent;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.io.SaveFileReader;
import mindustry.io.SaveVersion;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static mindustry.Vars.*;

/** Bullets are normally transient; escaped reactor cores are part of the saved world. */
public final class LooseSingularitySaves{
    private static boolean registered;
    private static final Seq<SavedCore> pending = new Seq<>();

    private LooseSingularitySaves(){}

    public static void register(){
        if(registered) return;
        registered = true;
        Events.on(ResetEvent.class, event -> pending.clear());
        Events.on(WorldLoadEvent.class, event -> {
            for(SavedCore core : pending) core.restore();
            pending.clear();
        });
        SaveVersion.addCustomChunk("aquarion-loose-singularities", new SaveFileReader.CustomChunk(){
            public void write(DataOutput stream) throws IOException{
                LooseSingularitySaves.write(stream);
            }

            public void read(DataInput stream) throws IOException{
                LooseSingularitySaves.read(stream);
            }
        });
    }

    public static void write(DataOutput stream) throws IOException{
        Seq<Bullet> cores = new Seq<>();
        Groups.bullet.each(b -> {
            if(b.type instanceof LooseSingularityBulletType && b.isAdded()) cores.add(b);
        });
        stream.writeInt(1);
        stream.writeInt(cores.size);
        for(Bullet b : cores){
            stream.writeInt(b.type.id);
            stream.writeInt(b.team.id);
            stream.writeFloat(b.x);
            stream.writeFloat(b.y);
            stream.writeFloat(b.time);
            stream.writeFloat(b.lifetime);
            stream.writeFloat(b.fdata);
            stream.writeFloat(b.vel.x);
            stream.writeFloat(b.vel.y);
            var state = (LooseSingularityBulletType.CoreState)b.data;
            stream.writeInt(state.seed);
            stream.writeInt(state.size);
            for(var entry : state){
                stream.writeInt(entry.key);
                stream.writeFloat(entry.value);
            }
        }
    }

    public static void read(DataInput stream) throws IOException{
        pending.clear();
        if(stream.readInt() != 1) throw new IOException("Unsupported singularity save version");
        int count = stream.readInt();
        if(count < 0 || count > 100000) throw new IOException("Invalid singularity count");
        for(int i = 0; i < count; i++){
            SavedCore core = new SavedCore();
            core.type = stream.readInt();
            core.team = stream.readInt();
            core.x = stream.readFloat();
            core.y = stream.readFloat();
            core.time = stream.readFloat();
            core.lifetime = stream.readFloat();
            core.area = stream.readFloat();
            core.vx = stream.readFloat();
            core.vy = stream.readFloat();
            core.state = new LooseSingularityBulletType.CoreState(stream.readInt());
            int released = stream.readInt();
            if(released < 0 || released > 1000000) throw new IOException("Invalid singularity release count");
            for(int j = 0; j < released; j++) core.state.put(stream.readInt(), stream.readFloat());
            if(!Float.isFinite(core.x) || !Float.isFinite(core.y) || !Float.isFinite(core.area)
                || !Float.isFinite(core.time) || !Float.isFinite(core.lifetime)
                || !Float.isFinite(core.vx) || !Float.isFinite(core.vy)) throw new IOException("Invalid singularity state");
            pending.add(core);
        }
    }

    private static class SavedCore{
        int type, team;
        float x, y, time, lifetime, area, vx, vy;
        LooseSingularityBulletType.CoreState state;

        void restore(){
            if(type < 0 || type >= content.bullets().size || team < 0 || team >= Team.all.length) return;
            if(!(content.bullet(type) instanceof LooseSingularityBulletType bulletType)) return;
            if(area <= 1f || time >= lifetime) return;
            Bullet bullet = bulletType.create(null, Team.get(team), x, y, 0f);
            if(bullet == null) return;
            bullet.time = time;
            bullet.lifetime = lifetime;
            bullet.fdata = area;
            bullet.vel.set(vx, vy);
            bullet.data = state;
        }
    }
}
