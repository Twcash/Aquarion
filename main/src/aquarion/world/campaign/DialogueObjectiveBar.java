package aquarion.world.campaign;

import arc.Core;
import arc.Events;
import arc.math.Mathf;
import arc.scene.event.Touchable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.Label;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.ui.Styles;

/** Bottom-left HUD bar for dialogue map objectives. */
public class DialogueObjectiveBar {
    private static DialogueObjectiveBar instance;
    private static final float charRate = 12f;
    private static final float enterDur = 0.25f;
    private static final float exitDur = 0.3f;
    private static final float basePad = 12f;

    private final Table root = new Table();
    private final Table bar = new Table();
    private final Image portrait = new Image();
    private final Label speaker = new Label("");
    private final Label text = new Label("");
    private final Table options = new Table();
    private Cell<Table> barCell;
    private DialogueObjective shown;
    private float typeTimer, enterT, exitT, slideOffset;
    private int charCount;
    private boolean entering, exiting, built;

    public void build(){
        if(built) return;
        built = true;
        instance = this;

        bar.setBackground(Styles.black6);
        bar.margin(14f);
        bar.defaults().left();
        portrait.setScaling(Scaling.fit);
        bar.add(portrait).size(64f).padRight(12f);
        bar.table(info -> {
            info.add(speaker).left().padBottom(4f);
            info.row();
            info.add(text).left().width(700f).wrap();
            info.row();
            info.add(options).left().padTop(10f);
        });
        text.setFontScale(1.6f);
        speaker.setFontScale(1.4f);

        root.setFillParent(true);
        root.bottom().left();
        root.touchable = Touchable.childrenOnly;
        root.visible = false;
        barCell = root.add(bar).bottom().left().pad(basePad);
        Vars.ui.hudGroup.addChild(root);
        bar.clicked(() -> {
            if(!exiting && shown != null && charCount >= (shown.text == null ? 0 : shown.text.length()) && (shown.options == null || shown.options.length == 0)){
                DialogueObjectiveSystem.advance();
                beginExit();
            }
        });

        Events.run(EventType.Trigger.update, this::update);
    }

    static boolean isOpen(){
        return instance != null && instance.shown != null;
    }

    static void reset(){
        if(instance == null) return;
        instance.shown = null;
        instance.entering = false;
        instance.exiting = false;
        instance.root.visible = false;
        instance.options.clearChildren();
        if(instance.barCell != null) instance.barCell.padLeft(basePad);
    }

    private float slideDistance(){
        return Math.max(bar.getPrefWidth() + 60f, 700f);
    }

    private void beginExit(){
        exiting = true;
        exitT = 0f;
        slideOffset = slideDistance();
    }

    private void update(){
        if(DialogueObjectiveSystem.active != null && DialogueObjectiveSystem.active.isFinished()){
            if(shown == DialogueObjectiveSystem.active && !exiting) beginExit();
            DialogueObjectiveSystem.active = null;
        }
        if(DialogueObjectiveSystem.active == null){
            DialogueObjective next = DialogueObjectiveSystem.findActive();
            if(next != null) DialogueObjectiveSystem.active = next;
        }

        DialogueObjective current = DialogueObjectiveSystem.active != null ? DialogueObjectiveSystem.active : shown;
        if(current == null){
            root.visible = false;
            return;
        }

        if(shown != current){
            shown = current;
            typeTimer = 0f;
            charCount = 0;
            entering = true;
            exiting = false;
            enterT = exitT = 0f;
            slideOffset = slideDistance();
            barCell.padLeft(basePad - slideOffset);
            speaker.setText(localize(current.speaker));
            portrait.visible = current.icon != null && !current.icon.isEmpty();
            if(portrait.visible) portrait.setDrawable(new TextureRegionDrawable(Core.atlas.find(current.icon)));
            text.setText("");
            options.clearChildren();
            if(current.options != null){
                options.defaults().height(44f);
                for(DialogueObjectiveOption option : current.options){
                    if(option == null) continue;
                    TextButton button = new TextButton(localize(option.text), Styles.cleart);
                    button.clicked(() -> {
                        DialogueObjectiveSystem.choose(option);
                        beginExit();
                    });
                    button.getLabel().setFontScale(1.4f);
                    button.getLabel().setWrap(false);
                    options.add(button).left().padRight(28f);
                }
            }
            root.visible = true;
        }

        if(entering){
            enterT += Time.delta / 60f;
            float progress = Mathf.clamp(enterT / enterDur);
            barCell.padLeft(basePad - slideOffset * (1f - progress));
            if(progress >= 1f){
                entering = false;
                barCell.padLeft(basePad);
            }
        }else if(exiting){
            exitT += Time.delta / 60f;
            float progress = Mathf.clamp(exitT / exitDur);
            barCell.padLeft(basePad - slideOffset * progress);
            if(progress >= 1f){
                exiting = false;
                barCell.padLeft(basePad);
                root.visible = false;
                shown = null;
            }
            return;
        }

        String dialogueText = current.text == null ? "" : current.text;
        if(charCount < dialogueText.length()){
            typeTimer += Time.delta / 60f;
            int target = Math.min((int)(typeTimer * charRate), dialogueText.length());
            if(target > charCount){
                charCount = target;
                text.setText(dialogueText.substring(0, charCount));
            }
        }
    }

    private String localize(String value){
        if(value == null) return "";
        return value.startsWith("@") ? Core.bundle.get(value.substring(1), value) : value;
    }
}
