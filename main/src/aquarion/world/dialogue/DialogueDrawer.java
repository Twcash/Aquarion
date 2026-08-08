package aquarion.world.dialogue;

/** An implementation of custom rendering behavior for a dialogue scene, analogous to
 * {@link mindustry.world.draw.DrawBlock}. Attach drawers to a node with
 * {@link DialogueNode#drawer(DialogueDrawer)}.
 * <p>
 * Drawers render the current scene on screen using the Draw API, like block drawers render a block
 * in the world. Coordinates are screen pixels relative to the center of the drawing area: (0, 0) is the
 * center. {@link #drawWidth} and {@link #drawHeight} hold the size of the drawing area and are updated
 * each frame. */
public class DialogueDrawer{
    /** Size of the current dialogue drawing area, set each frame. */
    public static float drawWidth, drawHeight;

    /** Translates a center-origin x to screen pixel coordinates. */
    public static float cx(float x){
        return x + drawWidth / 2f;
    }

    /** Translates a center-origin y to screen pixel coordinates. */
    public static float cy(float y){
        return y + drawHeight / 2f;
    }

    /** Load any relevant texture regions. Called each time a scene is shown. */
    public void load(DialogueNode node){
    }

    /** Draws this node's scene on screen. */
    public void draw(DialogueNode node){
    }
}
