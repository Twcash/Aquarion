package aquarion.world.dialogue.draw;

import aquarion.world.dialogue.DialogueDrawer;
import aquarion.world.dialogue.DialogueNode;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Font;
import arc.scene.ui.layout.Scl;
import arc.util.Align;
import mindustry.ui.Fonts;

/** Draws localized text at a position. Set {@code text} to a bundle key (e.g. {@code "@key"}) to localize it. */
public class DialogueText extends DialogueDrawer{
    public String text;
    public float x, y;
    public Color color = Color.white;
    public float scale = 1f;
    public int alignment = Align.center;
    public boolean outline = true;

    public DialogueText(String text, float x, float y){
        this.text = text;
        this.x = x;
        this.y = y;
    }

    public DialogueText(String text, float x, float y, Color color){
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public void draw(DialogueNode node){
        Font font = outline ? Fonts.outline : Fonts.def;
        String str = text != null && text.startsWith("@") ? Core.bundle.get(text.substring(1), text) : text;
        font.draw(str, DialogueDrawer.cx(x), DialogueDrawer.cy(y), color, Scl.scl(scale), false, alignment);
    }
}
