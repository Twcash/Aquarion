package aquarion.world.blocks.power;

import aquarion.world.graphics.Renderer;
import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.power.PowerGenerator;

import java.awt.*;

public class VoidGenerator extends PowerGenerator {
    public VoidGenerator(String name) {
        super(name);
        hasItems = true;
        acceptsItems = true;
        update = true;
    }
    public float powerPerItem = 30f;
    public class VoidGenBuild extends GeneratorBuild {
        float producedThisTick = 0f;
        float smoothProduction = 0f;
        public float smoothing = 0.1f;
        @Override
        public float getPowerProduction() {
            return enabled ? powerPerItem * smoothProduction * efficiency: 0f;
        }
        @Override
        public void updateTile(){
            super.updateTile();
            if(!enabled){
                producedThisTick = 0f;
                smoothProduction = 0f;
                productionEfficiency = 0f;
                return;
            }
            smoothProduction += (producedThisTick - smoothProduction) * smoothing;
            productionEfficiency = getPowerProduction();
            producedThisTick = 0f;
        }
        @Override
        public boolean acceptItem(Building source, Item item){
            return true;
        }
        @Override
        public void handleItem(Building source, Item item){
            producedThisTick += 1f;
        }
        @Override
        public void draw() {
            super.draw();
            Fill.circle(x, y, 20 * efficiency);
        }
    }
}
