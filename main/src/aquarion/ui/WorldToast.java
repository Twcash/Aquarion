package aquarion.ui;

import arc.Core;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.actions.Actions;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.gen.Tex;

public class WorldToast extends Table {

    public float worldX, worldY;

    public float stickRadius = 80f;
    public float dragRadius = 220f;
    public float lerpSpeed = 0.1f;

    float drawX, drawY;

    public WorldToast(String text, float wx, float wy){
        this.worldX = wx;
        this.worldY = wy;

        background(Tex.button);
        margin(6);
        add(text).wrap().width(220f);

        pack();
        touchable = Touchable.disabled;

        // start at projected position
        Vec2 v = Core.camera.project(worldX, worldY);
        drawX = v.x;
        drawY = v.y;

        setPosition(drawX, drawY);

        Vars.ui.hudGroup.addChild(this);

        // optional fade-in (like vanilla toast)
        color.a = 0f;
        actions(Actions.fadeIn(0.2f, Interp.fade));
    }

    @Override
    public void act(float delta){
        super.act(delta);

        Vec2 screen = Core.camera.project(worldX, worldY);

        float dst = Mathf.dst(worldX, worldY,
                Core.camera.position.x,
                Core.camera.position.y);

        float targetX = screen.x;
        float targetY = screen.y;

        if(dst > stickRadius){
            float t = Mathf.clamp((dst - stickRadius) / (dragRadius - stickRadius));

            float cx = Core.graphics.getWidth() / 2f;
            float cy = Core.graphics.getHeight() / 2f;

            float dx = screen.x - cx;
            float dy = screen.y - cy;

            float len = Mathf.len(dx, dy);
            if(len > 0f){
                dx /= len;
                dy /= len;
            }

            float edgeX = cx + dx * (cx - 40f);
            float edgeY = cy + dy * (cy - 40f);

            targetX = Mathf.lerp(screen.x, edgeX, t);
            targetY = Mathf.lerp(screen.y, edgeY, t);
        }

        drawX = Mathf.lerp(drawX, targetX, lerpSpeed);
        drawY = Mathf.lerp(drawY, targetY, lerpSpeed);

        float w = getWidth();
        float h = getHeight();

        drawX = Mathf.clamp(drawX, 0, Core.graphics.getWidth() - w);
        drawY = Mathf.clamp(drawY, 0, Core.graphics.getHeight() - h);

        setPosition(drawX, drawY);
    }

    public void removeToast(){
        actions(
                Actions.scaleBy(0, 1, 10, Interp.pow2Out),
                Actions.fadeOut(0.2f, Interp.fade),
                Actions.remove()
        );
    }
}