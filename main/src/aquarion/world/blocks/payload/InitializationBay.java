package aquarion.world.blocks.payload;

import aquarion.world.blocks.units.UnitBlock;
import arc.Events;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import mindustry.ai.UnitCommand;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.PayloadUnit;
import mindustry.gen.Unit;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.UnitPayload;

public class InitializationBay extends PayloadBlock {

    public InitializationBay(String name) {
        super(name);
        outputsPayload = acceptsPayload = true;

    }
    public class initializeBuild extends PayloadBlockBuild {
        public float progress = 0;
        public @Nullable Vec2 commandPos;
        public @Nullable UnitCommand command;


        @Override
        public void updateTile() {
            super.updateTile();
            if(payload == null) return;
            moveInPayload();
            if(payload instanceof BuildPayload pay){
                if(pay.build instanceof UnitBlock.UnitBlockBuild unit){
                    if(unit.block instanceof UnitBlock blok){
                        float timeNeed = blok.time;
                        progress += edelta()/(timeNeed/2f);
                        if(progress >= blok.time){
                            Unit unitb = blok.unit.create(this.team);
                            if(unit.isCommandable()){
                                if(commandPos != null){
                                    unitb.command().commandPosition(commandPos);
                                }

                                unitb.command().command(command == null && unitb.type.defaultCommand != null ? unitb.type.defaultCommand : command);
                            }

                            payload = new UnitPayload(unitb);

                            progress = 0;
                        }
                    }
                } else {
                    //KILL IT WITH FIRE
                    payload = null;
                }
            } else if(payload instanceof UnitPayload t){
                moveOutPayload();
                Events.fire(new EventType.UnitCreateEvent(t.unit, this));
                payVector.setZero();
            }else {
                //KILL IT WITH FIRE
                payload = null;
            }
        }
        @Override
        public boolean acceptPayload(Building source, Payload payload){
            if(payload.size() <= this.block.size && this.payload == null && payload instanceof BuildPayload pay){
                if(pay.build instanceof UnitBlock.UnitBlockBuild) return true;
            };
            return false;
        }
    }
}
