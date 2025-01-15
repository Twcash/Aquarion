package aquarion.world.graphics;

import arc.graphics.g2d.Draw;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.draw.DrawRegion;

public class DrawBetterRegion extends DrawRegion {
    public DrawBetterRegion(String suffix){
        this.suffix = suffix;
    }

    public DrawBetterRegion(String suffix, float rotateSpeed){
        this.suffix = suffix;
        this.rotateSpeed = rotateSpeed;
    }

    public DrawBetterRegion(String suffix, float rotateSpeed, boolean spinSprite){
        this.suffix = suffix;
        this.spinSprite = spinSprite;
        this.rotateSpeed = rotateSpeed;
    }

    public DrawBetterRegion(){
    }
    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        if(!drawPlan) return;
        Draw.z(layer);
        Drawf.spinSprite(region, plan.drawx()+ x, plan.drawy() + y, (buildingRotate ? plan.rotation * 90f : 0 + rotation));
    }
}
