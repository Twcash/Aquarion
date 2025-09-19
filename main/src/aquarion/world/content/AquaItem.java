package aquarion.world.content;

import aquarion.world.Statf;
import arc.graphics.Color;
import mindustry.type.Item;

public class AquaItem extends Item {
    public AquaItem(String name, Color color) {
        super(name, color);
    }
    public float magnetism = 0;
    @Override
    public void setStats(){
        super.setStats();
        stats.addPercent(Statf.magnetism, magnetism);
    }
}
