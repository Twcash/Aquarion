package aquarion.world.blocks.payload;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.RotBlock;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class PayloadTram extends PayloadBlock{
    public float range = 160f;
    public float transportSpeed = 0.8f;
    public TextureRegion bridgeRegion, topRegion, midRegion;
    public PayloadTram(String name){
        super(name);
        update = true;
        solid = true;
        rotate = true;
        outputsPayload = true;
        acceptsPayload = true;
        group = BlockGroup.transportation;
        drawArrow = true;
        noUpdateDisabled = true;
    }
    @Override
    public void load() {
        super.load();
        bridgeRegion = Core.atlas.find(name + "-bridge");
        topRegion = Core.atlas.find(name + "-top");
        midRegion = Core.atlas.find(name + "-mid");

    }
    public class PayloadTramBuild extends PayloadBlockBuild<Payload>{
        public Seq<PayloadTramBuild> links = new Seq<>();
        public Seq<MovingPayload> moving = new Seq<>();
        private float linkTimer = 0f;

        @Override
        public void updateTile(){
            linkTimer += Time.delta;
            if(linkTimer >= 30f){
                rebuildLinks();
                linkTimer = 0f;
            }
            for(int i = moving.size - 1; i >= 0; i--){
                MovingPayload mp = moving.get(i);
                mp.update();

                if(mp.progress >= 1f){
                    if(mp.to != null && mp.to.acceptPayload(this, mp.payload)){
                        mp.to.handlePayload(this, mp.payload);
                        moving.remove(i);
                    }else{
                        mp.progress = 1f; // stop, blocked
                    }
                }
            }

            if(payload == null) return;

            Building front = front();
            if(front != null && front.acceptPayload(this, payload)){
                front.handlePayload(this, payload);
                payload = null;
                return;
            }
            for(PayloadTramBuild link : links){
                if(link == this) continue;
                if(isBlocked(link)) continue;

                float dist = Mathf.dst(x, y, link.x, link.y);
                if(dist < 1f) continue;

                moving.add(new MovingPayload(this, link, payload, transportSpeed / dist));
                payload = null;
                return;
            }
        }

        boolean isBlocked(PayloadTramBuild target){
            if(target.payload != null) return true;

            for(MovingPayload mp : moving)
                if(mp.to == target && mp.progress < 0.9f) return true;

            for(MovingPayload mp : target.moving)
                if(mp.to == this && mp.progress < 0.9f) return true;

            return false;
        }

        @Override
        public boolean acceptPayload(Building source, Payload p){
            return payload == null && p.fits(this.block.size);
        }

        @Override
        public void handlePayload(Building source, Payload p){
            payload = p;
        }

        void rebuildLinks(){
            links.clear();
            int max = Mathf.floor(range / tilesize);

            for(Point2 d : Geometry.d8){
                for(int i = 1; i <= max; i++){
                    Tile t = world.tile(tile.x + d.x * i, tile.y + d.y * i);
                    if(t == null) break;

                    if(t.build instanceof PayloadTramBuild b && b.team == team && b != this){
                        links.add(b);
                        break;
                    }
                }
            }
        }

        @Override
        public void draw(){
            Draw.z(Layer.blockOver+0.01f);
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());
            for(int i = 0; i < 4; i++){
                if(blends(i) && i != rotation){
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                }
            }
            if(payload != null) payload.draw();
            Draw.rect(midRegion, x, y);
            Draw.z(Layer.blockOver + 0.1f);

            Draw.reset();

            for(MovingPayload mp : moving){
                mp.draw();
            }
            Draw.z(Layer.blockOver+0.2f);
            Lines.stroke(8);
            for(PayloadTramBuild b : links){
                Lines.line(bridgeRegion, x, y, b.x, b.y, false);
            }
            Draw.z(Layer.blockOver+0.3f);
            Draw.rect(topRegion, x, y);
            Draw.reset();
        }
    }

    public static class MovingPayload{
        public Payload payload;
        public PayloadTramBuild from, to;
        public float progress = 0f;
        public float speed;
        public float fx, fy, tx, ty;

        public MovingPayload(PayloadTramBuild from, PayloadTramBuild to, Payload payload, float speed){
            this.from = from;
            this.to = to;
            this.payload = payload;
            this.speed = speed;

            fx = from.x;
            fy = from.y;
            tx = to.x;
            ty = to.y;
        }

        public void update(){
            progress = Mathf.clamp(progress + Time.delta * speed, 0f, 1f);
        }

        public void draw(){
            float px = Mathf.lerp(fx, tx, progress);
            float py = Mathf.lerp(fy, ty, progress);

            float ox = payload.x();
            float oy = payload.y();
            float or = payload.rotation();

            payload.set(px, py, or);
            payload.draw();
            payload.set(ox, oy, or);
        }
    }
}
