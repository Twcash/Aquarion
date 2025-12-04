package aquarion.world.blocks.payload;

import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Position;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
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

public class PayloadTram extends PayloadBlock {
    public float range = 160f;
    public float transportSpeed = 10f;

    public PayloadTram(String name){
        super(name);
        update = true;
        hasPower = true;
        solid = true;
        rotate = true;
        outputsPayload = true;
        acceptsPayload = true;
        group = BlockGroup.transportation;
        drawArrow = true;
        noUpdateDisabled = true;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);
    }

    public class PayloadTramBuild extends PayloadBlockBuild<Payload> {
        public Seq<PayloadTramBuild> links = new Seq<>();
        public Seq<MovingPayload> moving = new Seq<>();
        public Seq<Payload> queue = new Seq<>(); // queue for payloads to send
        private int lastSentIndex = 0;
        private PayloadTramBuild lastSource;
        private float linkUpdateTimer = 0f;

        @Override
        public void updateTile() {
            linkUpdateTimer += Time.delta;
            if(linkUpdateTimer >= 30f){
                rebuildLinks();
                linkUpdateTimer = 0f;
            }

            // Move payloads along links
            for(int i = moving.size - 1; i >= 0; i--){
                var mp = moving.get(i);
                mp.progress += Time.delta * mp.speed;
                if(mp.progress >= 1f){
                    if(mp.to != null && mp.to.acceptPayload(this, mp.payload)){
                        mp.to.queue.add(mp.payload);  // add to queue of destination
                        mp.to.lastSource = this;
                    }
                    moving.remove(i);
                }
            }

            // Send queued payloads
            if(!queue.isEmpty()){
                sendPayload(queue.first());
                if(queue.first() == null) queue.remove(0);
            }
        }

        /** Automatically rebuild links in 8 directions, only exact 45° */
        void rebuildLinks(){
            links.clear();
            for(var dir : Geometry.d8){
                for(int j = 1; j <= ((PayloadTram)block).range / tilesize; j++){
                    var otherTile = world.tile(tile.x + dir.x * j, tile.y + dir.y * j);
                    if(otherTile == null) break;
                    if(otherTile.build instanceof PayloadTramBuild pb && pb.team == team && pb != this){
                        // check angle matches exact 45° increment
                        float dx = pb.x - x;
                        float dy = pb.y - y;
                        float angleDeg = Mathf.atan2(dy, dx) * Mathf.radDeg;
                        float snapped = Mathf.round(angleDeg / 45f) * 45f;
                        if(Mathf.equal(angleDeg, snapped, 0.1f)){
                            links.add(pb);
                            break;
                        }
                    }
                }
            }
        }

        /** Send payload using round-robin, avoiding backflow or occupied trams */
        boolean sendPayload(Payload p){
            int n = links.size;
            for(int i = 0; i < n; i++){
                int idx = (lastSentIndex + i) % n;
                var target = links.get(idx);
                if(target == null || target == this) continue;
                // Check backflow
                boolean blocked = false;
                for(var mp : target.moving){
                    if(mp.to == this && mp.from == target){
                        blocked = true; break;
                    }
                }
                if(blocked) continue;

                // Compute distance
                float dx = target.x - x, dy = target.y - y;
                float baseDistance = Mathf.sqrt(dx*dx + dy*dy);
                float speed = ((PayloadTram)block).transportSpeed / baseDistance;

                // Calculate spacing along link
                for(var mp : moving){
                    if(mp.to == this) return false;
                }
                moving.add(new MovingPayload(this, target, p, baseDistance, speed, 0));
                lastSentIndex = (idx + 1) % n;
                return true; // successfully sent
            }
            return false; // couldn't send
        }



        @Override
        public boolean acceptPayload(Building source, Payload p){
            float minSpacing = 0.1f; // % of link
            for(var mp : moving){
                if(mp.to == this && mp.progress > 1f - minSpacing){
                    return false;
                }
            }
            return true;
        }


        @Override
        public void handlePayload(Building source, Payload payload){
            this.payload = payload;
        }

        @Override
        public void remove(){
            super.remove();
            moving.clear();
            links.clear();
        }

        @Override
        public void draw(){
            super.draw();

            // debug links
            Draw.z(Layer.blockOver - 0.1f);
            Draw.color(Color.valueOf("6ecdec"));
            Lines.stroke(1f);
            for(var link : links){
                Lines.line(x, y, link.x, link.y);
            }
            Draw.reset();

            // moving payloads
            for(var mp : moving){
                if(mp.payload == null) continue;
                float px = Mathf.lerp(mp.from.x, mp.to.x, mp.progress);
                float py = Mathf.lerp(mp.from.y, mp.to.y, mp.progress);
                float ox = mp.payload.x(), oy = mp.payload.y(), orot = mp.payload.rotation();
                mp.payload.set(px, py, orot);
                Draw.z(Layer.blockOver + 0.01f);
                mp.payload.draw();
                mp.payload.set(ox, oy, orot);
            }

            // held payload
            if(payload != null){
                float ox = payload.x(), oy = payload.y(), orot = payload.rotation();
                payload.set(x, y, orot);
                Draw.z(Layer.blockOver + 0.02f);
                payload.draw();
                payload.set(ox, oy, orot);
            }
        }
    }

    /** Moving payload data */
    public static class MovingPayload implements Position {
        public PayloadTramBuild from, to; // source and target trams
        public Payload payload;           // actual payload being transported
        public float progress;            // progress along the link (0 = at source, 1 = at destination)
        public float distance;            // total distance of the link
        public float speed;               // travel speed along the link
        public float progressOffset;      // initial offset for spacing multiple payloads

        public MovingPayload(PayloadTramBuild from, PayloadTramBuild to, Payload payload, float distance, float speed, float progressOffset) {
            this.from = from;
            this.to = to;
            this.payload = payload;
            this.distance = distance;
            this.speed = speed;
            this.progressOffset = progressOffset;
            this.progress = progressOffset; // start with offset along the link
        }
        @Override
        public float getX(){
            return Mathf.lerp(from.x, to.x, progress);
        }

        @Override
        public float getY(){
            return Mathf.lerp(from.y, to.y, progress);
        }
    }
}