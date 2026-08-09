package aquarion.dialogs;

import aquarion.world.dialogue.DialogueOption;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.scene.Element;
import arc.scene.event.ClickListener;
import arc.scene.event.InputEvent;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Scl;
import arc.util.Align;
import arc.util.Time;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;

/** A single dialogue option, rendered as a draggable physics block with its sprite.
 * Blocks fall with gravity, bounce off the floor/walls, and rotate around the grab point
 * when dragged off-center. */
public class DialogueChoiceBlock extends Element {
    public static final float gravity = 1500f, bounce = 0.35f, airFriction = 0.7f, rotFriction = 0.5f;
    /** Friction applied while resting on the floor, so blocks stop instead of sliding on ice. */
    public static final float groundFriction = 6f;
    /** How much of the cursor's speed the block keeps, and the maximum throw speed / spin. */
    public static final float throwPower = 0.4f, maxThrowSpeed = 550f, maxThrowRot = 2400f;
    /** Hitbox is inset from the visual square by this much, so the click area is a bit smaller. */
    public static final float hitInset = 10f;

    public final DialogueOption option;
    public boolean held, hovered, insideBox, settle;
    public float vx, vy, vrot, rot;
    /** Grab offset in the block's local frame, and the cursor angle at grab time. */
    float grabX, grabY, lastMouseAng;
    /** Previous cursor position/time, for computing throw velocity. */
    float lastMouseX, lastMouseY;
    long lastMouseTime;

    private final DialogueDialog dialog;

    public DialogueChoiceBlock(DialogueDialog dialog, DialogueOption option){
        this.dialog = dialog;
        this.option = option;
        touchable = Touchable.enabled;

        setSize(Scl.scl(84f));

        addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button){
                held = true;
                settle = false;
                vx = vy = vrot = 0f;

                // grabbing a different block knocks the selected one out of the box
                if(dialog.view != null && dialog.view.current != null && dialog.view.current != DialogueChoiceBlock.this){
                    dialog.view.ejectCurrent();
                }

                float mx = Core.input.mouseX(), my = Core.input.mouseY();
                float cx = DialogueChoiceBlock.this.x + width / 2f;
                float cy = DialogueChoiceBlock.this.y + height / 2f;
                float dx = mx - cx, dy = my - cy;
                float cos = Mathf.cosDeg(rot), sin = Mathf.sinDeg(rot);
                grabX = dx * cos + dy * sin;
                grabY = -dx * sin + dy * cos;
                lastMouseAng = Mathf.atan2(my - cy, mx - cx);
                lastMouseX = mx;
                lastMouseY = my;
                lastMouseTime = Time.nanos();
                toFront();
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer){
                float mx = Core.input.mouseX(), my = Core.input.mouseY();
                long now = Time.nanos();
                float dt = Math.max((now - lastMouseTime) / 1000000000f, 0.0001f);
                float cx = DialogueChoiceBlock.this.x + width / 2f, cy = DialogueChoiceBlock.this.y + height / 2f;

                // rotate around the grab point as the cursor orbits the center
                if(Mathf.len(grabX, grabY) > 4f){
                    float curDeg = Mathf.atan2(my - cy, mx - cx) * Mathf.radDeg;
                    float lastDeg = lastMouseAng * Mathf.radDeg;
                    float deltaAng = ((curDeg - lastDeg + 540f) % 360f) - 180f;
                    rot += deltaAng;
                    vrot = Mathf.clamp(deltaAng / dt, -maxThrowRot, maxThrowRot);
                    lastMouseAng = curDeg * Mathf.degRad;
                }

                // cursor velocity, smoothed, becomes the block's throw on release
                float ivx = (mx - lastMouseX) / dt, ivy = (my - lastMouseY) / dt;
                vx = vx + (ivx * throwPower - vx) * 0.35f;
                vy = vy + (ivy * throwPower - vy) * 0.35f;
                float spd = Mathf.len(vx, vy);
                if(spd > maxThrowSpeed){
                    vx *= maxThrowSpeed / spd;
                    vy *= maxThrowSpeed / spd;
                }
                lastMouseX = mx;
                lastMouseY = my;
                lastMouseTime = now;

                // keep the grabbed point under the cursor, pivoting around it
                float cos = Mathf.cosDeg(rot), sin = Mathf.sinDeg(rot);
                float gx = grabX * cos - grabY * sin;
                float gy = grabX * sin + grabY * cos;
                float nx = mx - gx, ny = my - gy;
                DialogueChoiceBlock.this.x = nx - width / 2f;
                DialogueChoiceBlock.this.y = ny - height / 2f;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button){
                held = false;
                DialogueDropBox box = dialog.view != null ? dialog.view.box : null;
                settle = box != null
                    && DialogueChoiceBlock.this.x + width / 2f >= box.x
                    && DialogueChoiceBlock.this.x + width / 2f <= box.x + box.getWidth()
                    && DialogueChoiceBlock.this.y + height / 2f >= box.y
                    && DialogueChoiceBlock.this.y + height / 2f <= box.y + box.getHeight();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Element fromActor){
                hovered = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Element toActor){
                if(!held) hovered = false;
            }
        });
    }

    @Override
    public Element hit(float x, float y, boolean touchable){
        if(touchable && this.touchable != Touchable.enabled) return null;
        float half = Math.min(width, height) / 2f - Scl.scl(hitInset);
        if(half <= 0f) return null;
        // rotate the point into the block's local frame: a square hitbox aligned to the visible block
        float dx = x - width / 2f, dy = y - height / 2f;
        float a = -rot * Mathf.degRad;
        float cos = Mathf.cos(a), sin = Mathf.sin(a);
        float rx = dx * cos - dy * sin, ry = dx * sin + dy * cos;
        return Math.abs(rx) <= half && Math.abs(ry) <= half ? this : null;
    }

    @Override
    public void act(float delta){
        super.act(delta);
        if(dialog.view == null) return;
        float floor = dialog.view.floorY;
        float w = dialog.view.getWidth();

        // released inside the drop box: settle in place
        if(insideBox && settle && !held){
            vx *= 0.8f;
            vy *= 0.8f;
            vrot *= 0.8f;
            return;
        }

        if(!held){
            vy -= gravity * delta;
            vx *= Math.max(0f, 1f - airFriction * delta);
            vrot *= Math.max(0f, 1f - rotFriction * delta);

            x += vx * delta;
            y += vy * delta;
            rot += vrot * delta;

            // world-space extents of the rotated hitbox, so collisions respect rotation
            float inset = Scl.scl(hitInset);
            float hw = width / 2f - inset, hh = height / 2f - inset;
            float c = Mathf.cosDeg(rot), s = Mathf.sinDeg(rot);
            float extX = Math.abs(hw * c) + Math.abs(hh * s);
            float extY = Math.abs(hw * s) + Math.abs(hh * c);

            float cx = x + width / 2f, cy = y + height / 2f;

            if(cy - extY < floor){
                y = floor + extY - height / 2f;
                if(Math.abs(vy) > 60f){
                    vy = -vy * bounce;
                    vrot *= 0.5f;
                    vx *= 0.8f;
                }else{
                    vy = 0f;
                    // rest on the floor: strong friction so it stops, not slides
                    vx *= Math.max(0f, 1f - groundFriction * delta);
                    vrot *= Math.max(0f, 1f - groundFriction * 0.6f * delta);
                    if(Math.abs(vx) < 3f) vx = 0f;
                    if(Math.abs(vrot) < 4f) vrot = 0f;
                }
            }
            if(cx - extX < 0f){
                x = extX - width / 2f;
                if(Math.abs(vx) > 40f){
                    vx = Math.abs(vx) * bounce;
                    vrot *= 0.5f;
                }else{
                    vx = 0f;
                }
            }else if(cx + extX > w){
                x = w - extX - width / 2f;
                if(Math.abs(vx) > 40f){
                    vx = -Math.abs(vx) * bounce;
                    vrot *= 0.5f;
                }else{
                    vx = 0f;
                }
            }
        }
    }

    @Override
    public void draw(){
        float cx = x + width / 2f, cy = y + height / 2f;
        float inset = Scl.scl(8f);
        TextureRegion region = option != null && option.icon != null ? Core.atlas.find("aquarion-" + option.icon) : null;
        boolean hasRegion = region != null && Core.atlas.isFound(region);

        Draw.alpha(parentAlpha);

        // shadow under the box
        Draw.color(0f, 0f, 0f, 0.25f);
        Fill.rect(cx, cy - Scl.scl(3f), width, height, rot);

        Draw.color(Pal.gray);
        Fill.rect(cx, cy, width, height, rot);

        if(hasRegion){
            float rw = width - inset * 2f, rh = height - inset * 2f;
            float scale = Math.min(rw / region.width, rh / region.height);
            Draw.color(Color.white);
            Draw.rect(region, cx, cy, region.width * scale, region.height * scale, rot);
        }

        Draw.color(held ? Pal.lighterOrange : insideBox ? Pal.accent : Pal.lightishGray);
        Lines.stroke(Scl.scl(2.5f));
        Lines.poly(cx, cy, 4, width * 0.7071f, rot + 45f);
        Lines.stroke(1f);
        Draw.reset();

        if(option != null){
            if(!hasRegion){
                String t = option.displayText();
                if(!t.isEmpty()){
                    Fonts.outline.draw(t, cx, cy - Scl.scl(4f), Color.white, Scl.scl(0.5f), false, Align.center);
                }
            }
            if(hovered && !held){
                String name = option.displayText();
                if(!name.isEmpty()){
                    Fonts.outline.draw(name, cx, y + height + Scl.scl(26f), Color.white, Scl.scl(0.8f), false, Align.center);
                }
            }
        }
    }
}
