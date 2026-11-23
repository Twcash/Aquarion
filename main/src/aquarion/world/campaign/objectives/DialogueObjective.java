package aquarion.world.campaign.objectives;

import arc.util.Time;
import mindustry.game.MapObjectives.MapObjective;

import static mindustry.Vars.state;

public class DialogueObjective extends MapObjective {
    public String requiresFlag = "";
    public String requiresGlobalFlag = "";
    public String endGlobalFlag = "";
    public String[] setGlobalFlags = {};
    public String speaker = "";
    public String icon = "";

    /**
     * Literal text or @bundle/map-locale key. Color tags are instant.
     * {draft, correction} Short for the typo format.
     * {typo: draft, correction} is equivalent. Either version may contain nested declarations.
     * Backspacing runs at least at the default typing speed, even if the draft uses a slower speed.
     * {shake: 0.1} shakes subsequent letters by 10% of line height; {shake: 0} disables it. Untested negative values.
     * {speed: 24} sets characters per second. {speed: reset} restores the default.
     * {pause: 0.5} {0.5} will default to this. Declarations persist until changed or the dialogue ends.
     */

    public String text = "";
    public DialogueObjectiveOption[] options = {};
    public float timer = 0f;
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
    public <T extends MapObjective> T next(T next){
        if(next == null || next == this) throw new IllegalArgumentException("Expected a different next objective");
        child(next);
        return next;
    }

    public boolean isFinished(){
        return finished;
    }
}
