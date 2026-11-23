package aquarion.world.campaign.objectives;

import mindustry.game.MapObjectives.MapObjective;

import static mindustry.Vars.state;

public class SectorCapturedObjective extends MapObjective {
    public boolean showText = false;
    public String text = "Capture Sector";
    @Override
    public boolean update(){
        return state.isCampaign() && state.getSector().isCaptured();
    }

    @Override
    public String text(){
        return showText ? text : null;
    }
}
