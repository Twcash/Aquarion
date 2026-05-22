package aquarion.world.content;

import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;

public class DialogueKey extends UnlockableContent {

    public DialogueKey(String name) {
        super(name);
    }
    @Override
    public void load(){
        super.load();

    }

    @Override
    public ContentType getContentType() {
        //TODO Change?
        return ContentType.status;
    }
}
