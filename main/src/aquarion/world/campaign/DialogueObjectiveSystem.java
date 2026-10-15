package aquarion.world.campaign;

import arc.struct.Seq;

import static mindustry.Vars.state;

/** Coordinates the active campaign map dialogue and its objective flags. */
public class DialogueObjectiveSystem {
    public static DialogueObjective active;
    private static final Seq<DialogueObjective> suppressedOnLoad = new Seq<>();
    private static boolean suppressQualifiedOnLoad;

    private DialogueObjectiveSystem(){
    }

    public static void reset(){
        suppressQualifiedOnLoad = active != null || DialogueObjectiveBar.isOpen();
        active = null;
        suppressedOnLoad.clear();
        DialogueObjectiveBar.reset();
    }

    public static DialogueObjective findActive(){
        if(state == null || state.rules == null || state.rules.objectives == null) return null;

        if(suppressQualifiedOnLoad){
            state.rules.objectives.eachRunning(
                objective -> objective instanceof DialogueObjective,
                (DialogueObjective dialogue) -> suppressedOnLoad.addUnique(dialogue)
            );
            suppressQualifiedOnLoad = false;
        }

        // If a suppressed objective stops qualifying and later qualifies again, let it show normally.
        for(int i = suppressedOnLoad.size - 1; i >= 0; i--){
            DialogueObjective dialogue = suppressedOnLoad.get(i);
            if(!dialogue.qualified() || dialogue.isFinished()) suppressedOnLoad.remove(i);
        }

        DialogueObjective[] result = {null};
        state.rules.objectives.eachRunning(
            objective -> objective instanceof DialogueObjective dialogue && !dialogue.isFinished() && !suppressedOnLoad.contains(dialogue),
            (DialogueObjective dialogue) -> {
                if(result[0] == null) result[0] = dialogue;
            }
        );
        return result[0];
    }

    public static void advance(){
        if(active != null){
            active.finish();
            active = null;
        }
    }

    public static void choose(DialogueObjectiveOption option){
        if(active == null || option == null) return;
        if(option.setFlags != null){
            for(String flag : option.setFlags){
                if(flag != null && !flag.isEmpty() && !state.rules.objectiveFlags.contains(flag)){
                    state.rules.objectiveFlags.add(flag);
                }
            }
        }
        advance();
    }
}
