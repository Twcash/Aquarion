package aquarion.world.blocks.distribution;

import mindustry.world.blocks.payloads.Payload;

public class PayloadTramData{
    public float x, y, ox, oy;
    public Payload payload;

    public PayloadTramData(float x, float y, float ox, float oy, Payload payload){
        this.x = x;
        this.y = y;
        this.ox = ox;
        this.oy = oy;
        this.payload = payload;
    }
}
