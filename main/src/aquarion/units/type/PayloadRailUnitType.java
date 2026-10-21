package aquarion.units.type;

import aquarion.world.graphics.Renderer;
import mindustry.gen.Building;
import mindustry.gen.PayloadUnit;
import mindustry.Vars;
import mindustry.gen.Unit;
import mindustry.gen.Payloadc;
import mindustry.world.blocks.payloads.BuildPayload;
import aquarion.world.units.TrainBuildPayload;
import mindustry.world.blocks.payloads.Payload;
import arc.graphics.g2d.Draw;

/** A dedicated rail car for building payloads up to 2x2. */
public class PayloadRailUnitType extends RailUnitType{
    public PayloadRailUnitType(String name){
        super(name);
        constructor = PayloadUnit::create;
        itemCapacity = 0;
        payloadCapacity = 4f * Vars.tilesize * Vars.tilesize;
        pickupUnits = false;
    }

    @Override
    public void update(Unit unit){
        super.update(unit);

        if(unit instanceof Payloadc carrier){
            for(int i = 0; i < carrier.payloads().size; i++){
                if(carrier.payloads().get(i) instanceof BuildPayload payload
                        && !(payload instanceof TrainBuildPayload)){

                    TrainBuildPayload active = new TrainBuildPayload(payload.build);
                    carrier.payloads().set(i, active);

                    if(!payload.build.block.updateInUnits ||
                            (
                                    !payload.build.block.alwaysUpdateInUnits)){
                        active.update(unit, null);
                    }
                }
            }
        }
    }
    @Override
    public <T extends Unit & Payloadc> void drawPayload(T unit){
        if(unit.payloads().isEmpty()) return;

        float z = Draw.z();

        Payload payload = unit.payloads().first();

        if(payload instanceof BuildPayload bay){
            Building build = bay.build;

            float oldX = build.x;
            float oldY = build.y;

            build.x = unit.x;
            build.y = unit.y;

            Draw.z(z);

            build.draw();

            build.x = oldX;
            build.y = oldY;
        }else{
            payload.set(unit.x, unit.y, unit.rotation);
            payload.draw();
        }

        Draw.reset();
        Draw.z(z);
    }
}
