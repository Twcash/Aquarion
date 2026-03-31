package aquarion.world.blocks.logic;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;

public class BinaryChannel extends Block {
    public BinaryChannel(String name) {
        super(name);
        rotate = true;
        drawArrow = true;
        update = true;
        rotateDraw = true;
    }
    public TextureRegion onRegion;

    @Override
    public void load(){
        super.load();
        onRegion = Core.atlas.find(name + "-on");
    }
    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(region, plan.drawx(), plan.drawy(),plan.rotation * 90);
    }
    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region};
    }
    public class BinaryChannelBuild extends Building{

        @Override
        public void draw(){
            super.draw();
            Draw.rect(region, x, y, rotation * 90);
            if(enabled) Draw.rect(onRegion, x, y, rotation * 90);
        }
        @Override
        public void updateTile(){
            if(back()!= null) {
                enabled = back().enabled;
            }
            if(front()!=null){
                if(front() instanceof BinaryChannelBuild b ){
                    if(b.rotation == rotation)b.enabled = enabled;
                } else {
                    return;
                }
                front().enabled = enabled;
            }
        }
    }
}
