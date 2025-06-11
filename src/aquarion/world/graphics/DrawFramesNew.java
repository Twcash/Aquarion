package aquarion.world.graphics;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.DrawBlock;

public class DrawFramesNew extends DrawBlock {
    /** Number of frames to draw. */
    public int frames = 3;
    /** Ticks between frames. */
    public float interval = 5f;
    /** If true, frames wil alternate back and forth in a sine wave. */
    public boolean sine = false;
    /** If true the frames will fade in with block warmup */
    public boolean fadeIn = false;
    /** Allows me to use multiple DrawFrames */
    public String suffix = "-frame";

    public TextureRegion[] regions;
    @Override
    public void draw(Building build){
        if(fadeIn){
            Draw.alpha(build.warmup());
        }
        Draw.rect(
                sine ?
                        regions[(int)Mathf.absin(build.totalProgress(), interval, frames - 0.001f)] :
                        regions[(int)((build.totalProgress() / interval) % frames)],
                build.x, build.y);
        Draw.reset();
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{regions[0]};
    }

    @Override
    public void load(Block block){
        regions = new TextureRegion[frames];
        for(int i = 0; i < frames; i++){
            //Starting from 1 rather than zero means I can export straight from aseprite
            regions[i] = Core.atlas.find(block.name + suffix + i);
        }
    }
}