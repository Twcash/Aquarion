package aquarion.ui;

import arc.Core;
import arc.math.Interp;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Table;
import arc.util.Align;
import mindustry.ui.Styles;

public class ModUI {
    public static void showBottomToast(String text) {
        float dur = 1f;

        Table t = new Table(Styles.black3);
        t.touchable = Touchable.disabled;
        t.margin(10).add(text).labelAlign(Align.center).style(Styles.outlineLabel);
        t.actions(
                Actions.moveToAligned(Core.graphics.getWidth()/2f, -40, Align.center),
                Actions.delay(0.5f),
                Actions.moveToAligned(Core.graphics.getWidth()/2f, 30, Align.center, dur, Interp.pow2),
                Actions.delay(2f),
                Actions.moveToAligned(Core.graphics.getWidth()/2f, -40, Align.center, dur, Interp.pow2),
                Actions.remove()
        );

        t.pack();
        t.act(0.1f);
        Core.scene.add(t);
    }
}
