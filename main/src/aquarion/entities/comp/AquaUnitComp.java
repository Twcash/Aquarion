package aquarion.entities.comp;

import aquarion.annotations.Annotations.EntityComponent;
import aquarion.world.units.AquaPlayerUnits;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;

/** Shared behavior for Aquarion core and buildable units, under player or AI control. */
@EntityComponent
public abstract class AquaUnitComp implements Unitc{
    public boolean aquaPlayerUnit(){
        return AquaPlayerUnits.contains(type());
    }

    @Override public void update(){
        if(aquaPlayerUnit()) aquarion.world.units.AquaPlayerUnitHooks.updatePlayerUnit(self());
    }

}

