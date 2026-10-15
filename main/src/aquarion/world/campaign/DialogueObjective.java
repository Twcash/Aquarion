package aquarion.world.campaign;

import arc.util.Time;
import mindustry.game.MapObjectives.MapObjective;

import static mindustry.Vars.state;

/** A map objective displayed as an in-game dialogue bar. */
public class DialogueObjective extends MapObjective {
    /** Only becomes available once this objective flag is set. Empty means always available. */
    public String requiresFlag = "";
    public String speaker = "";
    /** Atlas region name for the speaker portrait. */
    public String icon = "";
    public String text = "";
    public DialogueObjectiveOption[] options = {};
    /** Seconds before the dialogue ends automatically. Zero means wait for a choice or click. */
    public float timer = 0f;
    /** Ends the dialogue once this objective flag is set. Empty means never. */
    public String endFlag = "";

    private transient boolean finished;
    private transient float elapsed;

    @Override
    public boolean update(){
        if(finished) return true;
        if(!endFlag.isEmpty() && state.rules.objectiveFlags.contains(endFlag)){
            finished = true;
            return true;
        }
        if(timer > 0f){
            elapsed += Time.delta / 60f;
            if(elapsed >= timer){
                finished = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean qualified(){
        return super.qualified() && (requiresFlag.isEmpty() || state.rules.objectiveFlags.contains(requiresFlag));
    }

    public void finish(){
        finished = true;
    }

    public boolean isFinished(){
        return finished;
    }
}
