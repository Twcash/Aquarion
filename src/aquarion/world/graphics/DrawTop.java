package aquarion.world.graphics;
import arc.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.DrawBlock;

public class DrawTop extends DrawBlock {
    public TextureRegion[][] regions;
    public TextureRegion icon;
    public String suffix = "-top";
    public float layer = -1;
    public float x, y = 0;

    @Override
    public void draw(Building build) {
        float z = Draw.z();
        if (layer > 0) Draw.z(layer);
        Draw.rect(regions[build.rotation][0], build.x + x, build.y + y);
        Draw.z(z);
    }

    @Override public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(regions[plan.rotation][0], plan.drawx() + x, plan.drawy() + y);
    }


    @Override public void load(Block block) {
            regions = Core.atlas.find(block.name + suffix).split(block.size*32, block.size*32);
            icon = Core.atlas.find(block.name + suffix + "-icon");
        }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{icon};
    }
}