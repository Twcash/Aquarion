package aquarion.world.blocks.logic;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.world.Block;
import aquarion.world.blocks.logic.toggler;
import aquarion.world.blocks.logic.BinaryChannel;

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
        public boolean active;

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
                    if(other instanceof BinaryChannel.BinaryChannelBuild h){
                        if(h.front() != null && h.front() == this) h.active = active;
                        if(h.back() != null && h.back() == this) h.active = active;
                    }
                    else if(other instanceof toggler.togglerBuild b){
                        active = b.enabled;
                    }
                    else {
                        other.enabled = active;
                    }
                }
            }
        }
    }
}