package aquarion.world.dialogue;

import arc.Core;
import arc.struct.Seq;

/** A single scene in a dialogue story. The scene is rendered on screen by its {@link #drawers}. */
public class DialogueNode {
    public String name;
    public String speaker;
    public String text;
    /** Node to go to when a linear node's confirm button is pressed. Null closes the dialogue. */
    public String next;
    public Seq<DialogueOption> options = new Seq<>();
    /** Extra nodes unlocked when this node is read, besides option targets. */
    public Seq<String> unlock = new Seq<>();
    /** Drawers render this scene on screen, like block drawers. */
    public Seq<DialogueDrawer> drawers = new Seq<>();

    public DialogueNode(String name, String speaker, String text){
        this.name = name;
        this.speaker = speaker;
        this.text = text;
    }

    public DialogueNode text(String text){
        this.text = text;
        return this;
    }

    public DialogueNode speaker(String speaker){
        this.speaker = speaker;
        return this;
    }

    /** Makes this a linear node: a node with no options shows only a confirm button that advances to {@code next}. */
    public DialogueNode next(String next){
        this.next = next;
        return this;
    }

    public DialogueNode option(String text, String target){
        options.add(new DialogueOption(text, target));
        return this;
    }

    public DialogueNode option(String text, String target, String requires){
        options.add(new DialogueOption(text, target, requires));
        return this;
    }

    public DialogueNode option(String text, String target, String requires, String icon){
        options.add(new DialogueOption(text, target, requires).icon(icon));
        return this;
    }

    public DialogueNode option(String text, String target, String requires, String requiresNot, String icon){
        options.add(new DialogueOption(text, target, requires).notAfter(requiresNot).icon(icon));
        return this;
    }

    public DialogueNode unlock(String node){
        unlock.add(node);
        return this;
    }

    public DialogueNode drawer(DialogueDrawer drawer){
        drawers.add(drawer);
        return this;
    }

    /** Returns the localized speaker name, or the raw text if it isn't a bundle key. */
    public String displaySpeaker(){
        return speaker != null && speaker.startsWith("@") ? Core.bundle.get(speaker.substring(1), speaker) : speaker;
    }

    /** Returns the localized text, or the raw text if it isn't a bundle key. */
    public String displayText(){
        return text != null && text.startsWith("@") ? Core.bundle.get(text.substring(1), text) : text;
    }
}
