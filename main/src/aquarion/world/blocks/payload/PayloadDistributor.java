package aquarion.world.blocks.payload;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadRouter;

public class PayloadDistributor extends PayloadRouter {
    public PayloadDistributor(String name) {
        super(name);
    }
    public TextureRegion inRegion, outRegion;
    @Override
    public void load(){
        super.load();
        inRegion = Core.atlas.find(name+"-in");
        outRegion = Core.atlas.find(name+"-out");

    }
    public class payloadDistributorBuild extends PayloadRouterBuild{
        @Override
        public void draw(){
            super.draw();
            for(int i = 0; i < 4; i++){
                if(blends(i) && i != rotation){
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                }
            }
        }
        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return this.item == null
                    && payload.fits(payloadLimit)
                    && (source == this || this.enabled);
        }
    }
}
