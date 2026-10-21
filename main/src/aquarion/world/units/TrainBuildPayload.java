package aquarion.world.units;

import aquarion.units.type.PayloadRailUnitType;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.world.blocks.payloads.BuildPayload;

/** Keeps vanilla payload serialization while allowing carried train buildings to operate. */
public class TrainBuildPayload extends BuildPayload{
    public TrainBuildPayload(Building building){ super(building); }

    @Override public void update(Unit holder, Building station){
        if(holder == null || !(holder.type instanceof PayloadRailUnitType)){
            super.update(holder, station);
            return;
        }
        build.tile = Vars.emptyTile;
        RailPayloadSupport.feed(holder, build);
        if(build.power != null) build.power.status = 1f;
        build.updatePayload(holder, null);
    }
    @Override public void draw(){

    }
}
