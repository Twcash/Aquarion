package aquarion.world.blocks.logic;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;

public class BinarySplitter extends BinaryBlock {
    private static final Color lightColor = Color.valueOf("e1d7f8");
    public BinarySplitter(String name) {
        super(name);
        rotate = false;
        update = true;
        drawArrow = false;
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
            Draw.rect(active ? onRegion : region, x, y, 0);
            Drawf.light(x, y, 12f * warmup(), lightColor, 0.3f * warmup());

        }

        @Override
        public boolean outputsTo(Building target){
            return target != null && proximity.contains(target);
        }

        @Override
        public boolean acceptsInput(Building source){
            return true;
        }

        @Override
        public void updateTile() {
            super.updateTile();
            for(Building target : proximity){
                if(outputsTo(target)) transmit(this, target, active);
            }
        }
    }
}
