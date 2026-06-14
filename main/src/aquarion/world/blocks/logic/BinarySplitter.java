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
        rotate = true;
        update = true;
        drawArrow = true;
    }

    public TextureRegion onRegion;

    @Override
    public void load(){
        super.load();
        onRegion = Core.atlas.find(name + "-on");
    }

    public class BinarySplitterBuild extends Building {
        public boolean active;

        @Override
        public void draw(){
            Draw.rect(region, x, y, 0);
            if(active) Draw.rect(onRegion, x, y, rotation);
        }

        @Override
        public void updateTile(){
            if(back() instanceof toggler.togglerBuild b){
                if(back().front()!=null && back().front()==this) active = b.enabled;
            }else if(back() instanceof BinaryChannel.BinaryChannelBuild c){
                if(back().front()!=null && back().front()==this) active = c.active;
            }

            for(int i = 0; i < proximity.size; i++){
                Building other = proximity.get(i);
                if(other != null && other != back()){
                    if(other instanceof BinaryChannel.BinaryChannelBuild h){
                        if(h.front() == this || h.back() == this) h.active = active;
                    }else if(!(other instanceof toggler.togglerBuild)){
                        other.enabled = active;
                    }
                }
            }
        }
    }
}