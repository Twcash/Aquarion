package aquarion.world.drawers;

import aquarion.world.blocks.payload.PayloadCentrifuge;
import arc.graphics.g2d.Draw;
import mindustry.gen.Building;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.draw.DrawBlock;

public class DrawPayload extends DrawBlock {
    @Override
    public void draw(Building build){
        if(build instanceof PayloadBlock.PayloadBlockBuild<?> payBlock){
            if(payBlock.payload!=null){
                Draw.rect(payBlock.payload.icon(), payBlock.payload.x(), payBlock.payload.y(), payBlock.payload.rotation());
            };
        }
        //Maybe a MultiPayloadBlock or a PayloadStack would be necessary
        if(build instanceof PayloadCentrifuge.PayloadCentrifugeBuild builde){
            if(!builde.payloads.isEmpty()){
                for(BuildPayload pay : builde.payloads){
                    Draw.rect(pay.icon(), pay.x(), pay.y(), pay.rotation());
                }
            }
        }
    }
}
