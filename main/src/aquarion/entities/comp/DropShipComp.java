package aquarion.entities.comp;

import aquarion.annotations.Annotations;
import aquarion.units.type.AquaUnitType;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.entities.Damage;
import mindustry.gen.ElevationMovec;
import mindustry.gen.Posc;
import mindustry.gen.Unit;
import mindustry.gen.Unitc;
import mindustry.type.UnitType;

@Annotations.EntityComponent
abstract class DropShipComp implements Unitc, Posc, ElevationMovec {
    //Do not use the standard UnitType with this component
    @Annotations.Import UnitType type;

    @Annotations.Import float x, y, elevation, speedMultiplier;
    boolean didDrop = false;
    @Override
    public void update(){
        if(didDrop){
            //fly back up to cruise altitude after the drop
            elevation = Mathf.approachDelta(elevation, 0.9f, 0.005f);
            speedMultiplier = Mathf.approachDelta(speedMultiplier, 1f, 0.1f);
        }
        if(!onSolid() && !didDrop){
            if(within(closestEnemyCore(), 300)) {
                elevation = Mathf.approachDelta(elevation, 0.1f, 0.005f);
                speedMultiplier = Mathf.approachDelta(speedMultiplier, 0.1f, 0.01f);
                if(elevation <= .2) {
                    didDrop = true;
                    if (type instanceof AquaUnitType typee) {
                        if(typee.landDamage > 0) {
                            Damage.damage(team(), x, y, typee.landRange, typee.landDamage);
                        }
                        typee.dropEffect.at(x,y, rotation()-90);
                        for (UnitType crew : typee.dropCrew) {
                            Unit unit = crew.create(team());
                            unit.x = x + Mathf.range(10);
                            unit.y = y + Mathf.range(10);
                            unit.add();
                        }
                    }
                }
            }
        }
    }
}
