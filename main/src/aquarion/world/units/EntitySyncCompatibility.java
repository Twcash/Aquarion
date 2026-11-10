package aquarion.world.units;

import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.ctype.MappableContent;
import mindustry.gen.EntityMapping;
import mindustry.io.SaveFileReader;
import mindustry.io.SaveVersion;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/** Validate the IDs used by live snapshots before the client starts receiving them. */
public final class EntitySyncCompatibility{
    private static final ContentType[] syncedContent = {
        ContentType.unit, ContentType.weather, ContentType.status,
        ContentType.item, ContentType.liquid, ContentType.block
    };

    private EntitySyncCompatibility(){}

    public static void register(){
        SaveVersion.addCustomChunk("aquarion-entity-sync-ids", new SaveFileReader.CustomChunk(){
            @Override
            public boolean shouldWrite(){
                return Vars.net != null && Vars.net.server();
            }

            @Override
            public void write(DataOutput stream) throws IOException{
                stream.writeInt(EntityMapping.customIdMap.size);
                for(var entry : EntityMapping.customIdMap){
                    stream.writeInt(entry.key);
                    stream.writeUTF(entry.value);
                }
                for(ContentType type : syncedContent){
                    var entries = Vars.content.<MappableContent>getBy(type);
                    stream.writeInt(entries.size);
                    for(var content : entries) stream.writeUTF(content.name);
                }
            }

            @Override
            public void read(DataInput stream, int length) throws IOException{
                if(Vars.net != null && Vars.net.client()){
                    read(stream);
                    return;
                }
                // A save made by a host can also contain this chunk.
                while(length > 0){
                    int skipped = stream.skipBytes(length);
                    if(skipped == 0){
                        stream.readByte();
                        skipped = 1;
                    }
                    length -= skipped;
                }
            }

            @Override
            public void read(DataInput stream) throws IOException{
                int entities = stream.readInt();
                for(int i = 0; i < entities; i++){
                    int id = stream.readInt();
                    String expected = stream.readUTF();
                    verify("entity", id, expected, EntityMapping.customIdMap.get(id));
                }
                for(ContentType type : syncedContent){
                    var entries = Vars.content.<MappableContent>getBy(type);
                    int count = stream.readInt();
                    for(int id = 0; id < count; id++){
                        String expected = stream.readUTF();
                        verify(type.name(), id, expected, id < entries.size ? entries.get(id).name : null);
                    }
                }
            }
        });
    }

    private static void verify(String kind, int id, String host, String client) throws IOException{
        if(!host.equals(client)){
            throw new IOException("Aquarion multiplayer ID mismatch for " + kind + " " + id +
                " (host " + host + ", client " + client + "). Use the same game and mod builds on both sides.");
        }
    }
}
