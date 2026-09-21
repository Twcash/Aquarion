package aquarion.world.blocks.payload;

import aquarion.world.graphics.AquaShaders;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.ItemStack;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;

import static mindustry.Vars.tilesize;

public class PayloadCentrifuge extends PayloadBlock {
    public float processTime = 240f;
    public float spinRadius = 15f;
    public float warmupSpeed = 0.06f;

    public PayloadCentrifuge(String name) {
        super(name);
        outputsPayload = false;
        acceptsPayload = true;
        hasItems = true;
        update = true;
        rotate = false;
    }

    @Override
    public void init(){
        super.init();
    }

    @Override
    public boolean outputsItems() {
        return true;
    }

    public class PayloadCentrifugeBuild extends PayloadBlockBuild<BuildPayload> {
        public float progress;
        public float warmup;
        public float totalProgress;
        public Seq<BuildPayload> payloads = new Seq<>();
        @Override
        public float progress(){
            return progress;
        }
        @Override
        public boolean shouldConsume(){
            return payloads.size > 0;
        }
        @Override
        public void handlePayload(Building source, Payload payload){
            payloads.add((BuildPayload) payload);
        }
        @Override
        public void updateTile(){
            super.updateTile();

            dump();
            if(payloads.isEmpty() || efficiency <= 0.001f) {
                efficiency = 0;
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);

                return;
            }
            if(payloads.size <4 ) return;
            totalProgress += edelta();
            progress += edelta();
            warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);

            if(progress >= processTime){
                progress = 0;
                for(int i=0; i < payloads.size; i++){
                    BuildPayload pay = payloads.get(i);
                    if(pay.block() instanceof ReagentMix mix){
                        for(ItemStack item : mix.containing){
                            for(int f = 0; f <item.amount; f++){
                                offload(item.item);
                            }
                        }
                    }
                }
                payloads.clear();
            }
        }


        @Override
        public void draw() {
            super.draw();
            if(payloads.size > 0) {
                for (int i = 0; i < payloads.size; i++) {
                    //Orbit position math
                    float baseRotation = totalProgress * 10;
                    float angle = baseRotation +(360f / payloads.size) * i;
                    float rad = Mathf.degRad * angle;
                    float cx = x + Mathf.cos(rad) * spinRadius;
                    float cy = y + Mathf.sin(rad) * spinRadius;

                    //Draw.alpha(Interp.reverse.apply(Interp.pow2In.apply(progress/processTime)));
                    TextureRegion reg = payloads.get(i).block().fullIcon;
                    Draw.rect(reg, cx, cy, 0);
                }
            }
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            return payloads.size < 4 &&
                    payload.fits(block.size) &&
                    payload instanceof BuildPayload build &&
                    build.block() instanceof ReagentMix;
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 1){
                progress = read.f();
            }
        }
    }
}
