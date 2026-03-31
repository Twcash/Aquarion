package aquarion.world.blocks.logic;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.world.blocks.logic.SwitchBlock;

public class toggler extends SwitchBlock {
    public toggler(String name) {
        super(name);
        rotate = true;
        rotateDraw = false;
        drawArrow = true;
        regionRotated1 = 1;
    }
    public TextureRegion topRegion;
    @Override
    public void load(){
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }
    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(topRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
    }
    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region, topRegion};
    }
    public class togglerBuild extends SwitchBlock.SwitchBuild{
        @Override
        public void draw(){
            super.draw();
            Draw.rect(topRegion, x, y, rotation * 90);
        }
        @Override
        public void updateTile(){
            if(!enabled && front()!=null) front().enabled = false;
            if(enabled && front()!=null) front().enabled = true;
        }
    }
}
