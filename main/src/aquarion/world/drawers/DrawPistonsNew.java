package aquarion.world.drawers;

import arc.graphics.g2d.Draw;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.world.Block;
import mindustry.world.draw.DrawPistons;

public class DrawPistonsNew extends DrawPistons {
    public boolean drawIcon = true;
    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        if(iconRegion.found() && drawIcon){
            Draw.rect(iconRegion, plan.drawx(), plan.drawy());
        }
    }

}
