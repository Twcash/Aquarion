package aquarion.dialogs;

import aquarion.world.dialogue.DialogueDrawer;
import aquarion.world.dialogue.DialogueNode;
import aquarion.world.dialogue.DialogueOption;
import aquarion.world.dialogue.DialogueTree;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.scene.Group;
import arc.scene.event.Touchable;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;

/** The interactive scene layer of the dialogue: draws the node's drawers, hosts the draggable choice blocks
 * and the drop box. */
public class DialogueSceneView extends Group {
    public float floorY;
    public DialogueChoiceBlock current;
    public DialogueDropBox box;
    public TextButton confirm;
    /** True when the current node has no visible options: only a confirm button is shown. */
    public boolean linear;

    public final DialogueDialog dialog;
    public final Seq<DialogueChoiceBlock> blocks = new Seq<>();

    public DialogueSceneView(DialogueDialog dialog){
        this.dialog = dialog;
        touchable = Touchable.enabled;
    }

    public void buildGame(){
        clear();
        blocks.clear();
        current = null;

        float w = getWidth(), h = getHeight();
        floorY = 150f;

        DialogueNode node = dialog.selected;
        if(node != null){
            for(DialogueOption opt : node.options){
                if(opt.requires != null && !dialog.isRead(opt.requires)) continue;
                if(opt.requiresNot != null && dialog.isRead(opt.requiresNot)) continue;
                DialogueChoiceBlock b = new DialogueChoiceBlock(dialog, opt);
                blocks.add(b);
                addChild(b);
            }
        }
        linear = blocks.isEmpty();
        if(!linear) spawnBlocks(w, h);

        box = new DialogueDropBox();
        if(linear){
            box.visible = false;
        }else{
            float boxSize = 220f;
            box.setSize(boxSize, boxSize);
            box.setPosition(w / 2f - boxSize / 2f, h / 2f - boxSize / 2f - 20f);
        }
        addChild(box);

        confirm = new TextButton(linear ? "@dialogue.continue" : "@dialogue.confirm", linear ? Styles.flatt : Styles.cleart);
        confirm.setSize(240f, 56f);
        confirm.setPosition(w / 2f - 120f, 28f);
        confirm.visible = false;
        confirm.clicked(linear ? dialog::confirmLinear : dialog::confirmChoice);
        addChild(confirm);

        Table corner = new Table();
        corner.button("@dialogue.back", Icon.left, Styles.cleart, dialog::back).size(140f, 48f);
        corner.button("@dialogue.leave", Icon.cancel, Styles.cleart, () -> dialog.hide()).size(140f, 48f);
        corner.pack();
        corner.setPosition(w - corner.getWidth() - 12f, h - corner.getHeight() - 12f);
        addChild(corner);
    }

    void spawnBlocks(float w, float h){
        int n = blocks.size;
        if(n == 0) return;
        for(int i = 0; i < n; i++){
            DialogueChoiceBlock b = blocks.get(i);
            b.setPosition(
                w / (n + 1) * (i + 1) - b.getWidth() / 2f + Mathf.random(-16f, 16f),
                h - 90f - b.getHeight() + Mathf.random(-30f, 30f)
            );
            b.vx = Mathf.random(-70f, 70f);
            b.vy = Mathf.random(-40f, 0f);
            b.rot = Mathf.random(-20f, 20f);
        }
    }

    boolean inBox(DialogueChoiceBlock b){
        float cx = b.x + b.getWidth() / 2f, cy = b.y + b.getHeight() / 2f;
        return cx >= box.x && cx <= box.x + box.getWidth()
            && cy >= box.y && cy <= box.y + box.getHeight();
    }

    /** Throws the currently selected block out of the box. Called when a different block is grabbed. */
    public void ejectCurrent(){
        DialogueChoiceBlock b = current;
        if(b == null || b.held) return;
        b.settle = false;
        b.insideBox = false;
        float bcx = b.x + b.getWidth() / 2f, bcy = b.y + b.getHeight() / 2f;
        float cx = box.x + box.getWidth() / 2f, cy = box.y + box.getHeight() / 2f;
        float dx = bcx - cx, dy = bcy - cy;
        float len = Mathf.len(dx, dy);
        b.vx = (len > 1f ? dx / len : Mathf.randomSign()) * Mathf.random(180f, 320f) + Mathf.random(-60f, 60f);
        b.vy = Mathf.random(260f, 420f);
        b.vrot = Mathf.random(-900f, 900f);
        current = null;
    }

    /** Separates two blocks using their rotated hitboxes (SAT). Returns true if they overlapped. */
    private static boolean obbSeparate(DialogueChoiceBlock a, DialogueChoiceBlock b){
        float inset = Scl.scl(DialogueChoiceBlock.hitInset);
        float ahw = a.getWidth() / 2f - inset, ahh = a.getHeight() / 2f - inset;
        float bhw = b.getWidth() / 2f - inset, bhh = b.getHeight() / 2f - inset;
        if(ahw <= 0f || ahh <= 0f || bhw <= 0f || bhh <= 0f) return false;

        float acx = a.x + a.getWidth() / 2f, acy = a.y + a.getHeight() / 2f;
        float bcx = b.x + b.getWidth() / 2f, bcy = b.y + b.getHeight() / 2f;

        float ac = Mathf.cosDeg(a.rot), as = Mathf.sinDeg(a.rot);
        float bc = Mathf.cosDeg(b.rot), bs = Mathf.sinDeg(b.rot);

        float minOverlap = Float.MAX_VALUE, mtvX = 0f, mtvY = 0f;

        // the four separating axes: each block's local x/y axes, rotated
        float[][] axes = {{ac, as}, {-as, ac}, {bc, bs}, {-bs, bc}};
        for(float[] axis : axes){
            float ux = axis[0], uy = axis[1];
            float pa = ahw * Math.abs(ac * ux + as * uy) + ahh * Math.abs(-as * ux + ac * uy);
            float pb = bhw * Math.abs(bc * ux + bs * uy) + bhh * Math.abs(-bs * ux + bc * uy);
            float dc = (bcx - acx) * ux + (bcy - acy) * uy;
            float overlap = pa + pb - Math.abs(dc);
            if(overlap <= 0.001f) return false;
            if(overlap < minOverlap){
                minOverlap = overlap;
                float sign = dc >= 0f ? 1f : -1f;
                mtvX = ux * sign;
                mtvY = uy * sign;
            }
        }

        if(a.held && b.held) return true;
        if(a.held){
            b.x -= mtvX * minOverlap;
            b.y -= mtvY * minOverlap;
        }else if(b.held){
            a.x += mtvX * minOverlap;
            a.y += mtvY * minOverlap;
        }else{
            a.x += mtvX * minOverlap * 0.5f;
            a.y += mtvY * minOverlap * 0.5f;
            b.x -= mtvX * minOverlap * 0.5f;
            b.y -= mtvY * minOverlap * 0.5f;
        }
        return true;
    }

    @Override
    public void act(float delta){
        super.act(delta);
        if(getWidth() <= 0f || getHeight() <= 0f) return;

        for(int i = 0; i < blocks.size; i++){
            for(int j = i + 1; j < blocks.size; j++){
                DialogueChoiceBlock a = blocks.get(i), b = blocks.get(j);
                if(a.held || b.held || a.insideBox || b.insideBox) continue;
                obbSeparate(a, b);
            }
        }

        current = null;
        boolean anyIn = false;
        for(DialogueChoiceBlock b : blocks){
            boolean in = !b.held && inBox(b);
            b.insideBox = in;
            if(in && b.settle){
                current = b;
            }
            if(in) anyIn = true;
        }
        box.contains = anyIn;
        confirm.visible = linear ? true : current != null;
    }

    @Override
    public void drawChildren(){
        if(dialog.tree != null && dialog.selected != null && dialog.currentDrawers != null){
            DialogueDrawer.drawWidth = getWidth();
            DialogueDrawer.drawHeight = getHeight();
            Draw.alpha(parentAlpha);
            for(DialogueDrawer drawer : dialog.currentDrawers){
                drawer.draw(dialog.selected);
            }
            Draw.color();
            Draw.reset();
        }
        super.drawChildren();
    }
}
