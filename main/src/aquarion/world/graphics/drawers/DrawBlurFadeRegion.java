package aquarion.world.graphics.drawers;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.draw.DrawBlock;

public class DrawBlurFadeRegion extends DrawBlock {
    public TextureRegion region, blurRegion;
    public String suffix = "";
    public float rotateSpeed = 1f, x, y, blurThresh = 0.7f, targetWarmup = 1.0f;

    public DrawBlurFadeRegion(String suffix, float speed){
        this.suffix = suffix;
        rotateSpeed = speed;
    }

    public DrawBlurFadeRegion(){
    }

    @Override
    public void draw(Building build){
        Drawf.spinSprite(region, build.x+x, build.y+y, build.totalProgress() * rotateSpeed);
        Draw.alpha(Mathf.lerp(0, 1, build.warmup()/targetWarmup));
        Drawf.spinSprite(build.warmup() > blurThresh ? blurRegion : region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed);
        Draw.reset();
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{region};
    }

    @Override
    public void load(Block block){
        region = Core.atlas.find(block.name + suffix);
        blurRegion = Core.atlas.find(block.name + suffix + "-blur");
    }
}