package aquarion.dialogs;

import arc.Events;
import mindustry.game.EventType;
import mindustry.ui.dialogs.BaseDialog;

public class BaseInteractDialogue extends BaseDialog {
    public BaseInteractDialogue() {
        super("");
        Events.on(EventType.ResetEvent.class, e -> {
            hide();
        });
        shouldPause = true;
    }

}
