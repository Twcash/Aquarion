package aquarion.entities.comp;

import aquarion.AquaLoader;
import aquarion.annotations.Annotations;
import aquarion.gen.HeatMapUpdaterc;
import aquarion.world.map.TemperatureMap;
import mindustry.gen.Entityc;
import mindustry.world.blocks.power.PowerGraph;

@Annotations.EntityDef(value = HeatMapUpdaterc.class, serialize = false, genio = false)
@Annotations.EntityComponent
abstract class HeatMapUpdaterComp implements Entityc {
    public transient TemperatureMap map;

    @Override
    public void update(){
        map.update();
    }
}