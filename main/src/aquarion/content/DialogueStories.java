package aquarion.content;

import aquarion.world.dialogue.DialogueNode;
import aquarion.world.dialogue.DialogueTree;
import aquarion.world.dialogue.draw.DialogueRegion;
import aquarion.world.dialogue.draw.DialogueSceneBackground;
import arc.graphics.Color;
import arc.struct.Seq;

/** Dialogue trees for the storyteller blocks. Add your stories here. */
public class DialogueStories {
    public static Seq<DialogueTree> all = new Seq<>();

    public static void loadContent(){
        all.clear();
        all.add(start());
    }

    private static DialogueTree start(){
        return new DialogueTree("1", "@1", "1s")
                .add(new DialogueNode("1s", "@env", "@1.s")
                        .option("@1.sc", "1sc", null, "subsystem-compassion")
                        .option("@1.sa", "1sa", null, "subsystem-architect"))
                //1sa route
                .add(new DialogueNode("1sa", "@a", "@1sa").next(null))
                //1sc route
                .add(new DialogueNode("1sc", "@c", "@1sc").next("1.2sc"))
                .add(new DialogueNode("1.2sc", "@t", "@1.2sc").next("1.21sc"))
                .add(new DialogueNode("1.21sc", "@t", "@1.21sc").next(null));
                //
    }
}
