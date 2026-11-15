package aquarion.world.campaign;

import aquarion.world.graphics.AquaFill;
import arc.scene.ui.Label;

/** Connects dialogue text and its per-character shake to the shared drawing utility. */
class DialogueLabel extends Label {
    private DialogueTypewriter typewriter;
    private final AquaFill.ShakingText shakingText = new AquaFill.ShakingText();

    DialogueLabel(){
        super("");
    }

    void setDialogue(DialogueTypewriter value){
        typewriter = value;
        setText(value == null ? "" : value.text());
    }

    @Override
    public void draw(){
        validate();
        if(typewriter == null || !typewriter.hasShake()){
            super.draw();
            return;
        }
        shakingText.draw(cache, layout, typewriter.text(), x, y,
            cache.getFont().getLineHeight() * getFontScaleY(), typewriter::shakeAt, super::draw);
    }
}
