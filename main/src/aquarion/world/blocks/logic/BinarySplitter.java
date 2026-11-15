package aquarion.world.blocks.logic;

import aquarion.world.blocks.logic.BinaryChannel.BinaryChannelBuild;
import aquarion.world.blocks.logic.toggler.togglerBuild;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;

public class BinarySplitter extends BinaryBlock {
    public BinarySplitter(String name) {
        super(name);
        rotate = true;
        update = true;
        drawArrow = true;
    }

    public TextureRegion onRegion;

    @Override
    public void load() {
        super.load();
        onRegion = Core.atlas.find(name + "-on");
    }

    public class BinarySplitterBuild extends BinaryBlockBuild {

        @Override
        public void draw() {
            Draw.rect(region, x, y, 0);
            if (active) Draw.rect(onRegion, x, y, rotation);
        }

        @Override
        public void updateTile() {
            for (Building build : proximity) {
                if(build instanceof BinaryBlockBuild b){
                    if(build instanceof BinaryChannelBuild c){
                        if(c.front() != null && front() == this){
                            continue;
                        }
                        b.active = active;
                    } else if (build instanceof BinarySplitterBuild d) {
                        if(active)b.active = active;
                        continue;
                    }
                }
                if(build instanceof togglerBuild){
                    continue;
                } else{
                    build.enabled = active;
                }
            }
        }
    }
}