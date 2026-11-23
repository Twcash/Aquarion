package aquarion.world.blocks.logic;

import aquarion.annotations.Annotations.Load;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.blocks.logic.SwitchBlock;

public class Toggler extends SwitchBlock {
    public @Load("@-arrow") TextureRegion arrowRegion;
    public @Load("@-arrow-on") TextureRegion arrowRegionON;
    public @Load("@-top") TextureRegion topRegion;
    public @Load("@-on") TextureRegion onRegion;
    public Toggler(String name) {
        super(name);
        rotate = true;
        rotateDraw = false;
        drawArrow = true;
        regionRotated1 = 1;
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(topRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region};
    }

    public class TogglerBuild extends SwitchBlock.SwitchBuild{
        public float warmup;
        @Override
        public void draw(){
            Draw.z(Layer.blockUnder);
            Draw.rect(enabled ? onRegion : region, x, y, 0);
            Draw.rect(topRegion, x, y, rotdeg());
            Draw.z(Layer.blockUnder-1);
            if(front() != null) Draw.rect(enabled ? arrowRegionON : arrowRegion, x, y, rotdeg());
            Drawf.light(x, y, 12f * warmup(), lightColor, 0.3f * warmup());
        }

        @Override
        public void updateTile(){
            BinaryBlock.transmit(this, front(), enabled);
            warmup = arc.math.Mathf.approachDelta(warmup, enabled ? 1f : 0f, enabled ? 0.05f : 0.01f);
        }
    }
}
