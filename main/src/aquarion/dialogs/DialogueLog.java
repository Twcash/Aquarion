package aquarion.dialogs;

import arc.scene.event.Touchable;
import arc.scene.ui.Label;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import aquarion.world.dialogue.DialogueNode;
import arc.graphics.g2d.Font.FontData;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;

/** Command-prompt style chat log for the dialogue. Your actions are left-aligned, the speaker's
 * answers are right-aligned, and the whole thing scrolls with a bar on the right. */
public class DialogueLog extends Table {
    public final Table content = new Table();
    private final ScrollPane scroll;

    /** Mindustry only enables markup on its def/outline fonts; the mono font needs it for bundle color tags.
     *  It is also generated tiny, so scale it up for readable log text. */
    private static boolean fontSet;

    private static void ensureFont(){
        if(fontSet) return;
        if(Fonts.monospace != null){
            FontData data = Fonts.monospace.getData();
            data.markupEnabled = true;
            data.setScale(1.5f);
            fontSet = true;
        }
    }

    public DialogueLog(){
        ensureFont();
        touchable = Touchable.enabled;
        setBackground(Styles.black);
        top().left();

        content.top().left();
        content.touchable = Touchable.disabled;

        scroll = new ScrollPane(content, Styles.defaultPane);
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);
        scroll.setOverscroll(false, true);
        scroll.setScrollBarPositions(false, true);

        add(scroll).grow();
    }

    public void clearLog(){
        content.clear();
        content.top().left();
    }

    /** Logs an action the player chose. */
    public void addAction(String optionText){
        if(optionText == null || optionText.isEmpty()) return;
        ensureFont();
        Label you = new Label("> " + optionText, Styles.monoLabel);
        you.setColor(Pal.accent);
        you.setWrap(true);
        content.top().left().add(you).growX().left().pad(6f).row();
        scrollToBottom();
    }

    /** Logs what the speaker says at the given node. */
    public void addReply(DialogueNode node){
        if(node == null) return;
        String sp = node.displaySpeaker();
        String tx = node.displayText();
        StringBuilder sb = new StringBuilder();
        if(sp != null && !sp.isEmpty()) sb.append(sp).append(": ");
        if(tx != null) sb.append(tx);
        if(sb.length() == 0) return;

        ensureFont();
        DialogueTypeLabel line = new DialogueTypeLabel(sb.toString(), Styles.monoLabel, this::scrollToBottom);
        line.label.setColor(Pal.lightishGray);
        content.top().right().add(line).growX().right().pad(6f).row();
        scrollToBottom();
    }

    private void scrollToBottom(){
        content.layout();
        scroll.layout();
        scroll.setScrollYForce(scroll.getMaxY());
        scroll.updateVisualScroll();
    }
}
