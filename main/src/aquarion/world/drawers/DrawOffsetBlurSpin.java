package aquarion.world.drawers;

import arc.graphics.g2d.Draw;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlurSpin;

public class DrawOffsetBlurSpin extends DrawBlurSpin {
    public DrawOffsetBlurSpin(){}

    public DrawOffsetBlurSpin(String suffix, float rotateSpeed){
        super(suffix, rotateSpeed);
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        if(region.found()) Draw.rect(region, plan.drawx() + x, plan.drawy() + y);
    }
}
