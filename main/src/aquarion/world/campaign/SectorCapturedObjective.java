package aquarion.world.campaign;

import mindustry.game.MapObjectives.MapObjective;

import static mindustry.Vars.state;

/** Completes once the current campaign sector is captured. */
public class SectorCapturedObjective extends MapObjective {
    /** Enable only when this objective should contribute text to the HUD. */
    public boolean showText = false;
    public String text = "Capture the sector";

    @Override
    public boolean update(){
        return state.isCampaign() && state.getSector().isCaptured();
    }

    @Override
    public String text(){
        return showText ? text : null;
    }
}
