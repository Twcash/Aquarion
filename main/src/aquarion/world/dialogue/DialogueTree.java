package aquarion.world.dialogue;

import arc.struct.ObjectMap;
import arc.struct.Seq;

/** A named, rooted collection of dialogue nodes. */
public class DialogueTree {
    public String name;
    public String title;
    /** Key of the root node. Always available, even on a fresh save. */
    public String root;
    public final ObjectMap<String, DialogueNode> nodes = new ObjectMap<>();

    public DialogueTree(String name, String title, String root){
        this.name = name;
        this.title = title;
        this.root = root;
    }

    public DialogueNode node(String key){
        return nodes.get(key);
    }

    public DialogueTree add(DialogueNode node){
        nodes.put(node.name, node);
        return this;
    }

    public Seq<DialogueNode> all(){
        return nodes.values().toSeq();
    }

    public DialogueNode rootNode(){
        return node(root);
    }
}
