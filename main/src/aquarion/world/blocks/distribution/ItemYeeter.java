package aquarion.world.blocks.distribution;

import aquarion.content.AquaBullets;
import aquarion.world.graphics.Renderer;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;

import static mindustry.game.Team.derelict;

public class ItemYeeter extends Block {
    public float reload = 15;
    public TextureRegion glowRegion;
    @Override
    public void load(){
        super.load();
        glowRegion = Core.atlas.find(name  + "-glow");
    }
    public ItemYeeter(String name) {
        super(name);
        rotate = true;
        rotateDraw = true;
        drawArrow = true;
        hasItems = true;
        itemCapacity = 1;
        acceptsItems = true;
        update = true;
    }
    public class yeeterBuild extends Building{
        public float reloadProg  = 0;
        @Override
        public void updateTile(){
            reloadProg += edelta();
        }
        @Override
        public boolean acceptItem(Building source, Item item){
            float curProg = reloadProg;
            if(curProg >= reload) reloadProg = 0;
            return curProg >= reload && source == back() && efficiency > 0;
        }
        @Override
        public void draw(){
            Draw.z(Renderer.Layer.blockUnder);
            Draw.rect(region, x, y,  rotdeg());
            Draw.alpha(reloadProg/reload);
            Draw.rect(glowRegion, x, y, rotdeg());
            Draw.reset();
        }
        @Override
        public void handleItem(Building source, Item item){
            AquaBullets.throwItem.create(
                    null,
                    this.team,
                    x,
                    y,
                    rotdeg() + Mathf.range(2f),
                    item.hardness,
                    1 * efficiency,
                    Mathf.random(2.9f, 3.2f),
                   item
            );
        }
    }
}
