package aquarion.world.blocks.payload;

import aquarion.world.blocks.units.UnitBlock;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import mindustry.ai.UnitCommand;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.UnitPayload;

public class InitializationBay extends PayloadBlock {

    public InitializationBay(String name) {
        super(name);
        outputsPayload = acceptsPayload = true;
        rotate = true;
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("progress", (initializeBuild e) -> new Bar("bar.progress", Pal.ammo, e::totProg));
    }

    public class initializeBuild extends PayloadBlockBuild {
        public float progress = 0;
        public float timeNeed = 1;

        public @Nullable Vec2 commandPos;
        public @Nullable UnitCommand command;
        public float totProg(){
            return progress/timeNeed;
        }
        @Override
        public void updateTile() {
            super.updateTile();
            if (payload == null) return;

            if (payload instanceof BuildPayload) {
                moveInPayload();
            }
            if (payload instanceof BuildPayload pay) {
                if (pay.build instanceof UnitBlock.UnitBlockBuild unitBuild &&
                        unitBuild.block instanceof UnitBlock blok) {
                    timeNeed = blok.time/10f;
                    progress += edelta();
                    if (progress >= timeNeed) {
                        Unit unit = blok.unit.create(team);

                        if (unit.isCommandable()) {
                            if (commandPos != null) {
                                unit.command().commandPosition(commandPos);
                            }

                            unit.command().command(command != null ? command : unit.type.defaultCommand);
                        }
                        payload = new UnitPayload(unit);
                        progress = 0f;
                    }
                } else {
                    payload = null;
                    progress = 0f;
                }
            } else if (payload instanceof UnitPayload) {
                moveOutPayload();
            }
        }

        @Override
        public void draw(){
            Draw.rect(region,x,y);
            if(payload!= null) payload.draw();
            Draw.rect(topRegion, x, y);
            Draw.rect(outRegion,x,y, rotdeg());
            for(int i = 0; i < 4; i++){
                if(blends(i) && i != rotation){
                    Draw.rect(inRegion, x, y, (i * 90f) - 180f);
                }
            }
        }
//        @Override
//        public boolean acceptPayload(Building source, Payload payload){
//            if(this.payload != null) return false;
//
//            if(payload instanceof BuildPayload pay){
//                if(pay.build instanceof UnitBlock.UnitBlockBuild unit){
//                    return payload.size() <= block.size;
//                }
//            }
//
//            return false;
//        }

    }
}
