package aquarion.world.dialogue;

import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import mindustry.io.SaveFileReader;
import mindustry.io.SaveVersion;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/** Story progress, stored inside the save file as a custom chunk, so choices are remembered per-save.
 * A fresh save starts empty; loading a save restores that save's read nodes. */
public class StoryProgress {
    public static final String CHUNK_NAME = "aquarion-story";

    /** tree name -> set of read node keys. */
    public static final ObjectMap<String, ObjectSet<String>> read = new ObjectMap<>();

    private static boolean registered = false;

    private StoryProgress(){}

    /** Registers the save chunk. Safe to call more than once. */
    public static void register(){
        if(registered) return;
        registered = true;

        SaveVersion.addCustomChunk(CHUNK_NAME, new SaveFileReader.CustomChunk(){
            @Override
            public void write(DataOutput stream) throws IOException{
                stream.writeInt(read.size);
                for(ObjectMap.Entry<String, ObjectSet<String>> entry : read){
                    stream.writeUTF(entry.key);
                    stream.writeInt(entry.value.size);
                    for(String node : entry.value){
                        stream.writeUTF(node);
                    }
                }
            }

            @Override
            public void read(DataInput stream) throws IOException{
                try{
                    read.clear();
                    int trees = stream.readInt();
                    for(int i = 0; i < trees; i++){
                        String tree = stream.readUTF();
                        int count = stream.readInt();
                        ObjectSet<String> set = new ObjectSet<>();
                        for(int j = 0; j < count; j++){
                            set.add(stream.readUTF());
                        }
                        if(!set.isEmpty()) read.put(tree, set);
                    }
                }catch(Throwable e){
                    // never let a corrupt chunk break save loading
                    read.clear();
                }
            }
        });
    }

    /** Clears in-memory state. The save chunk repopulates it when a save is loaded. */
    public static void load(){
        register();
        read.clear();
    }

    public static boolean isRead(DialogueTree tree, String node){
        if(tree == null || node == null) return false;
        ObjectSet<String> set = read.get(tree.name);
        return set != null && set.contains(node);
    }

    /** A node is available if it's the root, has been read, or is linked (option/unlock) from a read node. */
    public static boolean isAvailable(DialogueTree tree, DialogueNode node){
        if(tree == null || node == null) return false;
        if(node.name.equals(tree.root)) return true;
        if(isRead(tree, node.name)) return true;

        for(DialogueNode other : tree.all()){
            if(!isRead(tree, other.name)) continue;
            for(DialogueOption opt : other.options){
                if(opt.target != null && opt.target.equals(node.name)) return true;
            }
            if(other.unlock.contains(node.name)) return true;
        }
        return false;
    }

    /** Available but not yet read. */
    public static boolean hasNew(DialogueTree tree, DialogueNode node){
        return isAvailable(tree, node) && !isRead(tree, node.name);
    }

    public static void markRead(DialogueTree tree, DialogueNode node){
        register();
        if(tree == null || node == null || isRead(tree, node.name)) return;

        ObjectSet<String> set = read.get(tree.name);
        if(set == null) read.put(tree.name, set = new ObjectSet<>());
        set.add(node.name);
    }
}
