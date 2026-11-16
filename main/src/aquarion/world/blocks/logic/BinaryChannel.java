package aquarion.world.blocks.logic;

import aquarion.annotations.Annotations.Load;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

public class BinaryChannel extends BinaryBlock {
    private static final Color lightColor = Color.valueOf("e1d7f8");
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
        /** Back and side inputs are combined: any active source enables the channel. */
        @Override
        public boolean acceptsInput(Building source){
            return source != front();
        }

        @Override
        public void draw(){
            Draw.z(Layer.blockUnder);
            Draw.rect(active ? onRegion : region, x, y, 0);
            Draw.rect(dirRegion, x, y, rotdeg());
            Draw.z(Layer.blockUnder-1);
            if(front() != null) Draw.rect(active ? arrowRegionON : arrowRegion, x, y, rotdeg());
            Drawf.light(x, y, 12f * warmup(), lightColor, 0.3f * warmup());
        }
        @Override
        public void updateTile(){
            super.updateTile();
            transmit(this, front(), active);
        }
    }
}
