package aquarion.world.blocks.logic;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.gen.Building;
import aquarion.world.blocks.logic.BinarySplitter;
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
            boolean readBack = false;

            if(back() instanceof toggler.togglerBuild b){
                if(back().front()!=null && back().front()==this){ active = b.enabled; readBack = true; }
            }else if(back() instanceof BinaryChannelBuild c){
                if(back().front()!=null && back().front()==this){ active = c.active; readBack = true; }
            }

            if(!readBack && front() instanceof toggler.togglerBuild b){
                if(front().back()!=null && front().back()==this) active = b.enabled;
            }else if(!readBack && front() instanceof BinaryChannelBuild c){
                if(front().back()!=null && front().back()==this) active = c.active;
            }

            if(front() != null){
                if(front() instanceof BinaryChannelBuild y){
                    if(front().back()!=null && front().back()==this) y.active = active;
                }else if(!(front() instanceof BinarySplitter.BinarySplitterBuild || front() instanceof toggler.togglerBuild)){
                    front().enabled = active;
                }
            }

            if(!readBack && back() instanceof BinaryChannelBuild y){
                if(back().front()!=null && back().front()==this) y.active = active;
            }
        }
    }
}
