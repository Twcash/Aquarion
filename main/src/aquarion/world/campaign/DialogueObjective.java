package aquarion.world.campaign;

import arc.util.Time;
import mindustry.game.MapObjectives.MapObjective;

import static mindustry.Vars.state;

/** A map objective displayed as an in-game dialogue bar. */
public class DialogueObjective extends MapObjective {
    /** Only becomes available once this objective flag is set. Empty means always available. */
    public String requiresFlag = "";
    /** Shared across sector saves in this campaign. */
    public String requiresGlobalFlag = "";
    public String endGlobalFlag = "";
    public String[] setGlobalFlags = {};
    public String speaker = "";
    /** Atlas region name for the speaker portrait. */
    public String icon = "";
    /**
     * Literal text or @bundle/map-locale key. Color tags are instant; {0.5} pauses for half a second.
     * {draft, correction} types the draft, backspaces it, then types the correction.
     * {typo: draft, correction} is equivalent. Either version may contain nested declarations.
     * Backspacing runs at least at the default typing speed, even if the draft uses a slower speed.
     * {shake: 0.1} shakes subsequent letters by 10% of line height; {shake: 0} disables it.
     * {speed: 24} sets characters per second; {speed: reset} restores the default.
     * {pause: 0.5} is an alias for {0.5}. Declarations persist until changed or the dialogue ends.
     */
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
        if(!endFlag.isEmpty() && state.rules.objectiveFlags.contains(endFlag)
            || !endGlobalFlag.isEmpty() && GlobalFlags.has(endGlobalFlag)){
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
        return super.qualified() && (requiresFlag.isEmpty() || state.rules.objectiveFlags.contains(requiresFlag))
            && (requiresGlobalFlag.isEmpty() || GlobalFlags.has(requiresGlobalFlag));
    }

    @Override
    public void done(){
        super.done();
        for(String flag : setGlobalFlags) GlobalFlags.set(flag);
    }

    public void finish(){
        finished = true;
    }

    /** Run the next objective after this dialogue completes; show the first dialogue to queue both. */
    public <T extends MapObjective> T next(T next){
        if(next == null || next == this) throw new IllegalArgumentException("Expected a different next objective");
        child(next);
        return next;
    }

    public boolean isFinished(){
        return finished;
    }
}
