package aquarion.entities.comp;

import aquarion.annotations.Annotations;
import aquarion.units.type.AquaUnitType;
import arc.Events;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.entities.Damage;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.type.UnitType;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;

@Annotations.EntityComponent
abstract class DropShipComp implements Unitc, Posc, ElevationMovec, Payloadc {
    //Do not use the standard UnitType with this component
    @Annotations.Import UnitType type;
    @Annotations.Import float x, y, elevation, speedMultiplier, health, maxHealth, rotation;
    @Annotations.Import Seq<Payload> payloads = new Seq<>();

    boolean didDrop = false;
    @Override
    public void update(){
        if(didDrop){
            //fly back up to cruise altitude after the drop
            elevation = Mathf.approachDelta(elevation, 0.9f, 0.005f);
            speedMultiplier = Mathf.approachDelta(speedMultiplier, 1f, 0.1f);
        }
        if(!onSolid() && !didDrop){
            if((closestEnemyCore() != null&& within(closestEnemyCore(), 600)) || health <= maxHealth/2) {
                elevation = Mathf.approachDelta(elevation, 0.1f, 0.005f);
                speedMultiplier = Mathf.approachDelta(speedMultiplier, 0.1f, 0.01f);
                if(elevation <= .2) {
                    if (type instanceof AquaUnitType typee) {
                        if(typee.landDamage > 0) {
                            Damage.damage(team(), x, y, typee.landRange, typee.landDamage);
                        }
                        typee.dropEffect.at(x,y, rotation()-90);
                        do {
                            dropLastPayload();
                        }while(!payloads.isEmpty());
                        didDrop = true;
                    }
                }
            }
        }
    }
}
