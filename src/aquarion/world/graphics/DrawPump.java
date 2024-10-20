package aquarion.world.graphics;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Eachable;
import arc.util.Time;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawPump extends DrawBlock {
    public float sinMag = 0.3f, sinScl = 12f;
public float x, y = 0;
public TextureRegion region;
public String suffix = "-pump";
    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        if(region.found()){
            Draw.rect(region, plan.drawx(), plan.drawy());
        }
    }
    @Override
    public void draw(Building build) {
        // Retrieve warmup value
        float warmup = build.warmup();

        // Define base oscillation range (0.75 to 1.25, with 1 as the midpoint)
        float minScale = 0.75f;
        float maxScale = 1.25f;
        float scaleRange = (maxScale - minScale) / 2f;

        // Keep the oscillation frequency stable, but adjust amplitude based on warmup
        float oscillation = Mathf.sin(Time.time * 0.15f, 1f, scaleRange) * warmup;  // Oscillation frequency is stable, amplitude scales with warmup

        // Final scale is centered around 1
        float finalScale = 1f + oscillation;

        // Apply the final scale to both xSCL and ySCL
        Draw.xscl = finalScale;
        Draw.yscl = finalScale;

        // Draw shadow region (still using warmup for offsets if necessary)
        Draw.color(Pal.shadow);
        float offsetX = warmup * (1f - sinMag) + Mathf.absin(Time.time, sinScl, sinMag) * warmup;
        float offsetY = warmup * (1f - sinMag) + Mathf.absin(Time.time, sinScl, sinMag) * warmup;
        Draw.rect(region, build.x - offsetX, build.y - offsetY);

        // Reset color and draw the main region
        Draw.color();
        Draw.rect(region, build.x + x, build.y + y);
    }
    @Override
    public void load(Block block){
        super.load(block);
        region = Core.atlas.find(block.name + suffix, block.name + suffix);
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{region};
    }
}
