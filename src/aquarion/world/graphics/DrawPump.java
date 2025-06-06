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
    public float timeOffset = 0;
    public float x, y = 0;
    public TextureRegion region;
    public String suffix = "";

    public DrawPump(String suffix){
        this.suffix = suffix;
    }
    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
        if (region.found()) {
            Draw.rect(region, plan.drawx() + x, plan.drawy() + y);
        }
    }



    @Override
    public void draw(Building build) {
        float warmup = build.warmup();

        float minScale = 1f;
        float maxScale = 1.5f;
        float scaleRange = (maxScale - minScale) / 2f;

        // Keep the oscillation frequency stable, but adjust amplitude based on warmup
        float oscillation = Mathf.sin(Time.time * 0.15f + timeOffset, 1f, scaleRange) * warmup;  // Oscillation frequency is stable, amplitude scales with warmup

        float finalScale = 1f +oscillation;
            //yada yada never go below 1
        Draw.xscl = Math.min(finalScale, 1);
        Draw.yscl = Math.min(finalScale, 1);

        // Draw shadow region
        Draw.color(Pal.shadow);
        float offsetX = warmup * (1f - sinMag) + Mathf.absin(Time.time, sinScl, sinMag) * warmup;
        float offsetY = warmup * (1f - sinMag) + Mathf.absin(Time.time, sinScl, sinMag) * warmup;
        Draw.rect(region, build.x + x - offsetX, build.y + y - offsetY);

        // Reset color and draw the main region
        Draw.color();
        Draw.rect(region, build.x + x, build.y + y);

        Draw.reset();
    }

    @Override
    public void load(Block block) {
        super.load(block);
        region = Core.atlas.find(block.name + suffix, block.name + suffix);
    }

    @Override
    public TextureRegion[] icons(Block block) {
        return new TextureRegion[]{region};
    }
}
