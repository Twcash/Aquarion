package aquarion.world.blocks.logic;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import aquarion.world.blocks.logic.toggler;
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
    public boolean active = false;
        @Override
        public void draw(){
            super.draw();
            Draw.rect(region, x, y, rotation * 90);
            if(active) Draw.rect(onRegion, x, y, rotation * 90);
        }
        @Override
        public void updateTile(){
        if(back() != null){
            if(back() instanceof TogglerBuild b){
            active = b.enabled;
            }else if(back() instanceof BinaryChannelBuild c){
                active = c.active;
            }
            if(front() != null){
                if(front() instanceof BinaryChannelBuild y){
                    y.active = active;
                }else if(front() instanceof Building Gregory){
                    if(Gregory.rotation % this.rotation == 0){
                        Gregory.enabled = active;
                    }
                }
            }
        }
    }
    }
}
