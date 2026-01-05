package aquarion.world.blocks.payload;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class PayloadJumper extends PayloadBlock{
    public int range = 10;
    public float chargeTime = 100f;
    public TextureRegion pad, under;

    public PayloadJumper(String name){
        super(name);
        update = true;
        solid = true;
        size = 3;
        rotate = true;
        acceptsPayload = true;
        outputsPayload = true;
        regionRotated1 = 1;
        group = BlockGroup.units;
        sync = true;
    }
    @Override
    public void load(){
        super.load();
        pad = Core.atlas.find(name + "-pad");
        under = Core.atlas.find(name + "-under");

    }
    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        int maxLen = range + size / 2;
        Building dest = null;

        var dir = Geometry.d4[rotation];
        int dx = dir.x, dy = dir.y;
        int offset = size / 2;

        for(int j = 1 + offset; j <= range + offset; j++){
            var other = world.build(x + j * dx, y + j * dy);

            if(other instanceof Wall.WallBuild || (other != null && other.isInsulated())){
                break;
            }

            if(other != null && (other.block.acceptsPayload || other.block.outputsPayload) && other.team == Vars.player.team()){
                maxLen = j;
                dest = other;
                break;
            }
        }

        Drawf.dashLine(
                Pal.placing,
                x * tilesize + dx * (tilesize * size / 2f + 2),
                y * tilesize + dy * (tilesize * size / 2f + 2),
                x * tilesize + dx * maxLen * tilesize,
                y * tilesize + dy * maxLen * tilesize
        );

        if(dest != null){
            Drawf.square(dest.x, dest.y, dest.block.size * tilesize / 2f + 2.5f, 0f);
        }
    }

    public class PayloadJumperBuild extends PayloadBlockBuild{
        public Building link;
        public Tile dest;
        public int lastChange = -1;
        public float charge;
        public boolean sending = false;
        boolean flying = false;
        float flightTime = 0f;
        float flightDuration = 20f;
        float sendProg = 0;
        @Override
        public void updateTile(){
            moveInPayload();

            if(lastChange != world.tileChanges){
                lastChange = world.tileChanges;
                updateLink();
            }

            if(sending){
                sendProg += Time.delta / 20f;
                if(sendProg >= 1f){
                    sendProg = 0f;
                    sending = false;
                }
            }

            if(payload == null){
                flying = false;
                flightTime = 0f;
                sending = false;
                charge = 0f;
                return;
            }

            if(flying){
                flightTime += Time.delta;

                float progress = Interp.pow2Out.apply(
                        Mathf.clamp(flightTime / flightDuration, 0f, 1f)
                );

                float xe = Mathf.lerp(x, link.x, progress);
                float ye = Mathf.lerp(y, link.y, progress);

                payload.set(xe, ye, payRotation);

                if(progress >= 1f){
                    flying = false;

                    if(link != null && link.acceptPayload(this, payload)){
                        link.handlePayload(this, payload);
                        Sounds.blockPlace1.at(link.x, link.y, Mathf.random(0.8f, 1.1f));
                        Fx.smokePuff.at(link.x, link.y);

                        payload = null;
                    }else{
                        payload.set(x, y, payRotation);
                        payVector.set(x, y);
                    }

                    sending = false;
                    charge = 0f;
                    link = null;
                    dest = null;
                }
                return;
            }

            if(
                    link != null &&
                            !sending &&
                            Mathf.within(payload.x(), payload.y(), x, y, 0.01f) &&
                            link.acceptPayload(this, payload)
            ){
                charge += edelta();
                if(charge >= chargeTime){
                    charge = 0f;
                    sending = true;
                    jump();
                }
            }else{
                charge = 0f;
            }

            moveOutPayload();
        }
        void jump(){
            payload.set(x, y, payRotation);
            payVector.set(x, y);

            Sounds.blockBreak1.at(x, y, Mathf.random(0.8f, 1.1f));
            Fx.disperseTrail.at(x, y, rotdeg());

            flying = true;
            flightTime = 0f;
        }

        public void updateLink(){
            link = null;
            dest = null;

            var dir = Geometry.d4[rotation];
            int offset = size / 2;

            for(int j = 1 + offset; j <= range + offset; j++){
                var other = world.build(tile.x + j * dir.x, tile.y + j * dir.y);

                if(other == null) continue;

                if(other instanceof Wall.WallBuild || other.isInsulated()){
                    break;
                }
                if((other.block.acceptsPayload || other.block.outputsPayload) && other.team == team && other != this){
                    link = other;
                    dest = other.tile;
                    break;
                }

            }
        }
        @Override
        public void draw(){
            if(flying){Draw.z(Layer.blockOver+0.1f);}else{Draw.z(Layer.block);}
            Draw.rect(region, x, y);
            for(int i = 0; i < 4; i++){
                if(blends(i) && i != rotation){
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                }
            }
            if(front()!= null) {
                if (link == front()) {
                    Draw.rect(outRegion, x, y, rotdeg());
                }
            }
            float push = -4f * Mathf.pow(sendProg - 0.5f, 2f) + 1f;
            push = Mathf.clamp(push);

            float len = tilesize * 2.5f;
            float ox = Angles.trnsx(rotdeg(), push * len);
            float oy = Angles.trnsy(rotdeg(), push * len);
            float squash = Mathf.lerp( 1f, 0.75f, sendProg);
            Draw.rect(under, x + ox/2f, y + oy/2f, rotdeg());
            if(rotation == 1 || rotation == 4){
                Draw.scl(1f, squash);
            }else{
                Draw.scl(squash, 1f);
            }
            Draw.rect(pad, x + ox, y + oy, rotdeg());
            Draw.scl();
            if(payload == null) return;
            float scl = -1*(Mathf.pow(Interp.pow2Out.apply(flightTime/flightDuration)-.5f,2))+1.25f;

            Draw.scl(scl);
            payload.draw();
            Draw.scl();
            Draw.scl();
        }
        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return !flying && this.payload == null && charge <= 0f;
        }
        @Override
        public void updatePayload(){
            if(payload == null) return;

            if(flying){
                return;
            }

            payVector.set(x, y);
            payload.set(x, y, payRotation);
        }

        @Override
        public byte version(){
            return 4;
        }
        @Override
        public void write(Writes write){
            super.write(write);

            write.f(flightTime);
            write.f(charge);
            write.bool(sending);
            write.bool(flying);

            if(link != null){
                write.bool(true);
                write.i(link.tile.x);
                write.i(link.tile.y);
            }else{
                write.bool(false);
            }
        }
        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            flightTime = read.f();
            charge = read.f();
            sending = read.bool();
            flying = read.bool();

            if(read.bool()){
                int lx = read.i();
                int ly = read.i();
                link = world.build(lx, ly);
            }else{
                link = null;
            }
        }

    }
}
