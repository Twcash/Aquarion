package aquarion.world.dialogue.draw;

import aquarion.world.dialogue.DialogueDrawer;
import aquarion.world.dialogue.DialogueNode;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Scl;

/** Draws an atlas region with mouse-driven parallax. {@code x}/{@code y} set the base position relative
 *  to the center of the dialogue (0, 0 is the center) and {@code distance} sets the depth: a distance of 0
 *  sits right at the screen and moves 1:1 with the mouse, while larger distances recede toward the
 *  background and drift less. The region is loaded from the mod's atlas under the {@code aquarion-} prefix. */
public class DialogueRegion extends DialogueDrawer {
    /** Controls how strongly {@code distance} reduces movement. */
    public static float parallaxAmt = 30f;

    public String region;
    public float x, y;
    public float distance;
    public float scale = 1f;
    public float rotation;
    public float alpha = 1f;
    public boolean parallax = true;
    public Color color = Color.white;

    private TextureRegion reg;
    private float curX, curY;
    private boolean init;

    public DialogueRegion(String region, float x, float y, float distance){
        this.region = region;
        this.x = x;
        this.y = y;
        this.distance = distance;
    }

    public DialogueRegion(String region, float x, float y){
        this(region, x, y, 0f);
    }

    public DialogueRegion scale(float scale){
        this.scale = scale;
        return this;
    }

    public DialogueRegion rotation(float rotation){
        this.rotation = rotation;
        return this;
    }

    public DialogueRegion alpha(float alpha){
        this.alpha = alpha;
        return this;
    }

    public DialogueRegion color(Color color){
        this.color = color;
        return this;
    }

    public DialogueRegion parallax(boolean parallax){
        this.parallax = parallax;
        return this;
    }

    @Override
    public void load(DialogueNode node){
        reg = Core.atlas.find("aquarion-" + region, Core.atlas.find(region, Core.atlas.find("white")));
        init = false;
    }

    @Override
    public void draw(DialogueNode node){
        if(reg == null) return;

        float tx = DialogueDrawer.cx(x), ty = DialogueDrawer.cy(y);
        if(parallax){
            float cx = drawWidth / 2f, cy = drawHeight / 2f;
            float mx = Core.input.mouseX();
            float my = Core.graphics.getHeight() - Core.input.mouseY();
            float amt = distance == 0f ? 1f : parallaxAmt / (distance + parallaxAmt);
            tx += (mx - cx) * amt;
            ty += (my - cy) * amt;
        }
        if(!init){
            init = true;
            curX = tx;
            curY = ty;
        }else{
            curX = Mathf.lerpDelta(curX, tx, 8f);
            curY = Mathf.lerpDelta(curY, ty, 8f);
        }

        float w = reg.width * Scl.scl(scale);
        float h = reg.height * Scl.scl(scale);

        float baseA = Draw.getColor().a;
        Draw.color(color);
        Draw.alpha(baseA * alpha);
        Draw.rect(reg, curX, curY, w, h, rotation);
        Draw.color();
    }
}
