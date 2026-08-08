package aquarion.world.dialogue.draw;

import aquarion.world.dialogue.DialogueDrawer;
import aquarion.world.dialogue.DialogueNode;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;

/** Fills the whole dialogue with a solid color, optionally with a stretched region on top. Draw first to sit behind other drawers. */
public class DialogueSceneBackground extends DialogueDrawer{
    public Color color = Color.valueOf("121318");
    /** Optional region stretched over the whole drawing area. */
    public String regionName;

    public DialogueSceneBackground(){
    }

    public DialogueSceneBackground(Color color){
        this.color = color;
    }

    public DialogueSceneBackground(Color color, String regionName){
        this.color = color;
        this.regionName = regionName;
    }

    @Override
    public void draw(DialogueNode node){
        Draw.color(color);
        Draw.rect("white", drawWidth / 2f, drawHeight / 2f, drawWidth, drawHeight);
        Draw.color();
        if(regionName != null){
            Draw.rect(regionName, drawWidth / 2f, drawHeight / 2f, drawWidth, drawHeight);
        }
    }
}
