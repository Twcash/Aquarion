package aquarion.world.blocks.logic;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.world.Block;

public class BinarySplitter extends Block {
    public BinarySplitter(String name) {
        super(name);
    }
    public TextureRegion onRegion;
    @Override
    public void load(){
        update = true;
        super.load();
        onRegion = Core.atlas.find(name + "-on");
    }
    public class BinarySplitterBuild extends Building {
        @Override
        public void draw(){
            super.draw();
            if(enabled) Draw.rect(onRegion, x, y);
        }
        @Override
        public void updateTile(){
            for(int i = 0; i < proximity.size; i++){
                Building other = proximity.get(i);
                if(other != null) {
                    if (other.front() == this) {
                        enabled = other.enabled;
                        return;
                    }
                    other.enabled = enabled;
                }
            }
        }
    }
}
