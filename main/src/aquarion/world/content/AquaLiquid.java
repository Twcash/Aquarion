package aquarion.world.content;

import aquarion.world.Uti.Statf;
import arc.graphics.Color;
import mindustry.type.Liquid;

public class AquaLiquid extends Liquid {
    public float acidity = 0;
    public AquaLiquid(String name, Color color) {
        super(name, color);
    }
    @Override
    public void setStats(){
        super.setStats();
        stats.addPercent(Statf.acidity, acidity);
    }

}
