package aquarion.dialogs;

import aquarion.world.dialogue.DialogueDrawer;
import aquarion.world.dialogue.DialogueNode;
import aquarion.world.dialogue.DialogueTree;
import aquarion.world.dialogue.StoryProgress;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.Scl;
import arc.struct.Seq;

/** Full-screen dialogue. Each node's drawers render a full-screen scene, like block drawers render a block;
 * the branching choices are draggable physics blocks that must be dropped into the center box and confirmed.
 * A command-prompt style log at the top shows your actions (left) and the speaker's answers (right). */
public class DialogueDialog extends BaseInteractDialogue {
    public DialogueTree tree;
    public DialogueSceneView view = new DialogueSceneView(this);
    public DialogueLog log = new DialogueLog();

    public DialogueNode selected;
    /** Drawers for the current scene: the selected node's own, or the last node that defined any. */
    public Seq<DialogueDrawer> currentDrawers;
    public final arc.struct.Seq<String> history = new arc.struct.Seq<>();

    public DialogueDialog(){
        super();
        margin(0f);
        titleTable.remove();

        clearChildren();
        stack(cont, buttons).grow();
        cont.touchable = Touchable.enabled;
        cont.defaults().pad(0f);

        cont.add(log).top().growX().height(Scl.scl(190f)).row();
        cont.add(view).grow();
    }

    public void open(DialogueTree tree){
        this.tree = tree;
        history.clear();
        selected = null;
        currentDrawers = null;
        log.clearLog();

        DialogueNode root = tree != null ? tree.rootNode() : null;
        if(root != null) select(root, false);
        super.show();
        view.buildGame();
    }

    void select(DialogueNode node){
        select(node, true);
    }

    void select(DialogueNode node, boolean recordHistory){
        if(node == null) return;
        if(recordHistory && selected != null && node != selected) history.add(selected.name);

        selected = node;
        StoryProgress.markRead(tree, node);

        if(!node.drawers.isEmpty()){
            currentDrawers = node.drawers;
        }
        if(currentDrawers != null){
            for(DialogueDrawer drawer : currentDrawers){
                drawer.load(node);
            }
        }
        view.buildGame();
        log.addReply(node);
    }

    public boolean isRead(String name){
        return StoryProgress.isRead(tree, name);
    }

    void confirmChoice(){
        DialogueChoiceBlock chosen = view.current;
        if(chosen == null || chosen.option == null) return;
        DialogueNode target = tree != null && chosen.option.target != null ? tree.node(chosen.option.target) : null;

        log.addAction(chosen.option.displayText());

        if(target == null){
            hide();
        }else{
            select(target);
        }
    }

    /** Advances a linear (no-option) node; closes the dialogue if it has no {@code next} node. */
    void confirmLinear(){
        if(selected == null) return;
        DialogueNode target = selected.next != null && tree != null ? tree.node(selected.next) : null;
        if(target == null){
            hide();
        }else{
            select(target);
        }
    }

    void back(){
        if(history.isEmpty()) return;
        DialogueNode prev = tree != null ? tree.node(history.pop()) : null;
        if(prev != null) select(prev, false);
    }
}
