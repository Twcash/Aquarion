package aquarion.world.blocks.logic;

import aquarion.annotations.Annotations.Load;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import arc.util.Nullable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

public class BinaryChannel extends BinaryBlock {
    public @Load("@-arrow") TextureRegion arrowRegion;
    public @Load("@-arrow-on") TextureRegion arrowRegionON;
    public @Load("@-dir") TextureRegion dirRegion;
    public @Load("@-on") TextureRegion onRegion;
    public BinaryChannel(String name) {
        super(name);
        rotate = true;
        drawArrow = true;
        update = true;
        rotateDraw = true;
    }
    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(region, plan.drawx(), plan.drawy(),0);
    }
    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region};
    }
    public class BinaryChannelBuild extends BinaryBlockBuild {
    public @Nullable  Building front;
    public @Nullable  Building back;
        @Override
        public void draw(){
            Draw.z(Layer.blockUnder);
            Draw.rect(active ? onRegion : region, x, y, 0);
            Draw.rect(dirRegion, x, y, rotdeg());
            Draw.z(Layer.blockUnder-1);
            if(front()!=null)Draw.rect(active ? arrowRegionON : arrowRegion, x, y, rotation * 90);
            if(active) Drawf.light(x,y, 12 * warmup(), Color.valueOf("e1d7f8"), 0.3f*warmup());
        }
        @Override
        public void updateTile(){
            if(active){
                warmup = Mathf.approachDelta(warmup, 1, 0.05f);
            } else {
                warmup = Mathf.approachDelta(warmup, 0, 0.01f);
            }
            if(front()!=null){
                if(front() instanceof BinaryBlockBuild b){
                    b.active = active;
                } else {
                    front().enabled = active;
                }
            }
        }
    }
}
