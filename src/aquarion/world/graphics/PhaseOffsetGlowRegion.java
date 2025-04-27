package aquarion.world.graphics;

import mindustry.world.draw.DrawGlowRegion;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

public class PhaseOffsetGlowRegion extends DrawGlowRegion {
    public float phaseOffset = 0;
    public PhaseOffsetGlowRegion(){
    }

    public PhaseOffsetGlowRegion(float layer){
        this.layer = layer;
    }

    public PhaseOffsetGlowRegion(boolean rotate){
        this.rotate = rotate;
    }


    public PhaseOffsetGlowRegion(String suffix){
        this.suffix = suffix;
    }
    @Override
    public void draw(Building build){
        if(build.warmup() <= 0.001f) return;

        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        Draw.blend(blending);
        Draw.color(color);
        Draw.alpha((Mathf.absin(build.totalProgress() + phaseOffset, glowScale, alpha) * glowIntensity + 1f - glowIntensity) * build.warmup() * alpha);
        Draw.rect(region, build.x, build.y, build.totalProgress() * rotateSpeed + (rotate ? build.rotdeg() : 0f));
        Draw.reset();
        Draw.blend();
        Draw.z(z);
    }
}
