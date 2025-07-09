package aquarion.world.graphics;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.DrawBlock;

public class DrawTop extends DrawBlock{
    public TextureRegion heat, glow, top1, top2;


    public int rotOffset = 0;
    public boolean drawGlow = true;

    public DrawTop(){}

    public DrawTop(int rotOffset){
        this.rotOffset = rotOffset;
    }

    @Override
    public void draw(Building build){
        float rotdeg = (build.rotation + rotOffset) * 90;
        Draw.rect(Mathf.mod((build.rotation + rotOffset), 4) > 1 ? top2 : top1, build.x, build.y, rotdeg);
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(Mathf.mod((plan.rotation + rotOffset), 4) > 1 ? top2 : top1, plan.drawx(), plan.drawy(), (plan.rotation + rotOffset) * 90);
    }

    @Override
    public void load(Block block){
        top1 = Core.atlas.find(block.name + "-top1");
        top2 = Core.atlas.find(block.name + "-top2");
    }

}