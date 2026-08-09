package aquarion.world.dialogue;

import arc.Core;

/** A single response shown in the dialogue. Rendered as a draggable block. */
public class DialogueOption {
    public String text;
    /** Key of the node this option leads to. Null ends the dialogue. */
    public String target;
    /** If set, this option is only shown after the referenced node has been read. */
    public String requires;
    /** If set, this option is hidden once the referenced node has been read. */
    public String requiresNot;
    /** Optional atlas region shown on the option's block. */
    public String icon;

    public DialogueOption(String text, String target){
        this.text = text;
        this.target = target;
    }

    public DialogueOption(String text, String target, String requires){
        this.text = text;
        this.target = target;
        this.requires = requires;
    }

    public DialogueOption icon(String icon){
        this.icon = icon;
        return this;
    }

    /** Hides this option once the given node has been read. */
    public DialogueOption notAfter(String node){
        this.requiresNot = node;
        return this;
    }

    /** Returns the localized text, or the raw text if it isn't a bundle key. */
    public String displayText(){
        return text != null && text.startsWith("@") ? Core.bundle.get(text.substring(1), text) : text;
    }
}
