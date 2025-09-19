package aquarion.world.graphics.drawers;

import arc.Core;
import arc.graphics.g2d.Draw;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawRegion;

public class SwitchRegion extends DrawRegion {
    public SwitchRegion(){
        suffix = "-switch";
    }
    @Override
    public void draw(Building build){
        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        if(build.enabled){
            Draw.rect(region, x, y, 0);
        }
        Draw.z(z);
    }
    @Override
    public void load(Block block){
        region = Core.atlas.find(block.name + suffix);
    }
}
