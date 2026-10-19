package aquarion.world.blocks.payload;

import aquarion.annotations.Annotations;
import aquarion.world.graphics.AquaFx;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.type.ItemStack;
import mindustry.type.PayloadSeq;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawMulti;

public class PayloadCentrifuge extends PayloadBlock {
    public float processTime = 240f;
    public float spinRadius = 16f;
    public float warmupSpeed = 0.01f;
    public DrawBlock drawer = new DrawMulti();
    public PayloadCentrifuge(String name) {
        super(name);
        outputsPayload = false;
        acceptsPayload = true;
        hasItems = true;
        update = true;
        rotate = false;
    }
    @Override
    public TextureRegion[] icons(){
        return drawer.finalIcons(this);
    }
    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        drawer.drawPlan(this, plan, list);
    }
    @Override
    public boolean outputsItems() {
        return true;
    }
    @Override
    public void load(){
        super.load();
        drawer.load(this);
    }
    public class PayloadCentrifugeBuild extends PayloadBlockBuild<BuildPayload> {
        public float progress;
        public float warmup;
        public float totalProgress;
        public Seq<BuildPayload> payloads = new Seq<>();
        public PayloadSeq paySeq = new PayloadSeq();
        public FloatSeq currX = new FloatSeq();
        public FloatSeq currY = new FloatSeq();
        public FloatSeq arrive = new FloatSeq();

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
            paySeq.add(payload.content());
            currX.add(x);
            currY.add(y);
            AquaFx.acceptPayload.at(payload.x(), payload.y(),0,  payload);
            arrive.add(0f);
        }

        @Override
        public void updateTile(){
            super.updateTile();

            dump();
            if(payloads.isEmpty()){
                efficiency = 0;
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                return;
            } else {
                for(BuildPayload pay : payloads){
                    pay.set(x,y,0);
                }
            }

            moveInPayload();

            if(efficiency <= 0.001f){
                efficiency = 0;
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                return;
            }
            if(payloads.size < 4) return;

            progress += edelta();
            warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed);
            totalProgress += edelta() * warmup;

            if(progress >= processTime){
                progress = 0;
                paySeq.clear();
                for(int i=0; i < payloads.size; i++){
                    BuildPayload pay = payloads.get(i);
                    if(pay.block() instanceof ReagentMix mix){
                        for(ItemStack item : mix.containing){
                            for(int f = 0; f < item.amount; f++){
                                offload(item.item);
                            }
                        }
                    }
                }
                payloads.clear();
                currX.clear();
                currY.clear();
                arrive.clear();
            }
        }
        @Override
        public void draw() {
            super.draw();
            drawer.draw(this);
            Draw.reset();
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
            return 2;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
            write.f(warmup);
            paySeq.write(write);
            write.b((byte)payloads.size);
            for(int i = 0; i < payloads.size; i++){
                Payload.write(payloads.get(i), write);
            }
        }
        @Override
        public float warmup(){
            return warmup;
        }
        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            if(revision >= 2){
                progress = read.f();
                warmup = read.f();
                paySeq.read(read);
                payloads.clear();
                byte amount = read.b();
                for(int i = 0; i < amount; i++){
                    payloads.add((BuildPayload) Payload.read(read));
                }

                currX.clear();
                currY.clear();
                arrive.clear();
                for(int i = 0; i < payloads.size; i++){
                    arrive.add(1f);
                }
            }
        }
    }
}