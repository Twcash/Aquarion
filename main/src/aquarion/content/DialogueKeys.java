package aquarion.content;

import aquarion.world.content.DialogueKey;

public class DialogueKeys {
    public static DialogueKey departure, crux, sharded, defunct, research;
    public static void loadContent(){
        departure = new DialogueKey("departure");
        crux = new DialogueKey("crux");
        sharded = new DialogueKey("sharded");
        defunct = new DialogueKey("defunct");
        research = new DialogueKey("research");

    }
}
