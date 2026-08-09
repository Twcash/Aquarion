package aquarion.dialogs;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.scene.Element;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Scl;
import arc.util.Align;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;

/** The central target the player drags a choice block into. */
public class DialogueDropBox extends Element {
    public boolean contains;

    public DialogueDropBox(){
        touchable = Touchable.disabled;
    }

    @Override
    public void draw(){
        float cx = x + width / 2f, cy = y + height / 2f;

        Draw.alpha(parentAlpha);
        Draw.color(contains ? Pal.accent : Pal.darkishGray, contains ? 0.35f : 0.2f);
        Fill.rect(cx, cy, width, height);

        Draw.color(contains ? Pal.accent : Pal.lightishGray);
        Lines.stroke(Scl.scl(2f));
        Lines.rect(x, y, width, height);

        String label = Core.bundle.get("dialogue.drop");
        Fonts.outline.draw(label, cx, cy - Scl.scl(8f), contains ? Pal.accent : Pal.lightishGray, Scl.scl(0.7f), false, Align.center);
        Draw.reset();
    }
}
