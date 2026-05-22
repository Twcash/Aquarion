package aquarion.world.blocks.distribution;

import aquarion.content.AquaBullets;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;

import static mindustry.game.Team.derelict;

public class ItemYeeter extends Block {
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
        @Override
        public boolean acceptItem(Building source, Item item){
            return true;
        }
        @Override
        public void handleItem(Building source, Item item){
            AquaBullets.dumpitem.create(
                    null,
                    derelict,
                    x,
                    y,
                    rotdeg() + Mathf.range(2f),
                    item.hardness,
                    1,
                    Mathf.random(2.9f, 3.2f),
                   item
            );
        }
    }
}
