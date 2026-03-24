package aquarion.world.blocks.payload;

import aquarion.world.graphics.Renderer.*;
import aquarion.world.graphics.Renderer.Layer;
import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import arc.util.pooling.*;
import arc.util.pooling.Pool.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;
import static mindustry.Vars.tilesize;

public class PayloadJumper extends PayloadBlock {
    public int range = 10;
    public float cooldownTime = 20f;
    // visual
    public float sendTime = 5f, sendLength = 20f;

    public TextureRegion pad, under;

    public PayloadJumper(String name) {
        super(name);
        rotate = true;
        acceptsPayload = true;
        outputsPayload = true;
        group = BlockGroup.units;
        regionRotated1 = 2;
        regionRotated2 = 3;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("cooldown", (PayloadJumperBuild build) -> new Bar("bar.cooldown", Pal.ammo, () -> 1f - (build.cooldown / cooldownTime)));
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.range, range, StatUnit.blocks);
        stats.add(Stat.cooldownTime, cooldownTime / 60f, StatUnit.seconds);
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(under, plan.drawx(), plan.drawy(), plan.rotation * 90f);
        Draw.rect(pad, plan.drawx(), plan.drawy(), plan.rotation * 90f);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        int maxLen = range + size / 2;
        Building dest = null;

        var dir = Geometry.d4[rotation];
        int dx = dir.x, dy = dir.y;
        int offset = size / 2;

        for (int j = 1 + offset; j <= range + offset; j++) {
            var other = world.build(x + j * dx, y + j * dy);

            if (other instanceof Wall.WallBuild || (other != null && other.isInsulated())) {
                break;
            }

            if (other != null && (other.block.acceptsPayload || other.block.outputsPayload) && other.team == Vars.player.team()) {
                maxLen = j;
                dest = other;
                break;
            }
        }

        Drawf.dashLine(
                Pal.placing,
                x * tilesize + dx * (tilesize * size / 2f + 2),
                y * tilesize + dy * (tilesize * size / 2f + 2),
                dest != null ? dest.x - dx * (tilesize * dest.block.size / 2f + 2f) : x * tilesize + dx * maxLen * tilesize,
                dest != null ? dest.y - dy * (tilesize * dest.block.size / 2f + 2f) : y * tilesize + dy * maxLen * tilesize
        );

        if (dest != null) {
            Drawf.square(dest.x, dest.y, dest.block.size * tilesize / 2f + 2.5f, 0f);
        }
    }

    @Override
    public void load() {
        super.load();
        pad = Core.atlas.find(name + "-pad");
        under = Core.atlas.find(name + "-under");
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{region, outRegion, under, pad};
    }

    public class PayloadJumperBuild extends PayloadBlockBuild<Payload> {
        public Seq<JumpedPayloadData> jumpedPayloads = new Seq<>();
        public Building link;
        public int lastChange = -2;
        public float cooldown;

        @Override
        public void updateTile() {
            if (lastChange != Vars.world.tileChanges) {
                lastChange = Vars.world.tileChanges;
                updateLink();
            }

            if (link != null && link == front()) {
                moveOutPayload();
                return;
            }

            for (int i = 0; i < jumpedPayloads.size; i++) {
                if (jumpedPayloads.get(i).update()) {
                    Pools.free(jumpedPayloads.get(i));
                    jumpedPayloads.remove(i);
                    i--;
                }
            }

            if (cooldown > 0f) {
                cooldown = Mathf.maxZero(cooldown - edelta());
            }

            if (moveInPayload()) {
                if (cooldown <= 0f) {
                    if (link != null && link.acceptPayload(this, payload)) {
                        jumpedPayloads.add(Pools.obtain(JumpedPayloadData.class, JumpedPayloadData::new).set(
                                payload,
                                x, y,
                                link.x, link.y,
                                0f, this
                        ));
                        //W H Y
                        if(link instanceof PayloadConveyor.PayloadConveyorBuild conv){
                            conv.item = payload;
                        }
                        if(link instanceof PayloadRouter.PayloadRouterBuild conv){
                            conv.item = payload;
                        }
                        payload = null;
                        cooldown = cooldownTime;
                    }
                }
            }
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            if(this.payload != null) return false;
            if(super.acceptPayload(source, payload) && payload.fits(size) && cooldown <= 0) return true;
            return false;
        }

        public void updateLink() {
            link = null;

            var dir = Geometry.d4[rotation];
            int offset = size / 2;

            for (int j = 1 + offset; j <= range + offset; j++) {
                var other = Vars.world.build(tile.x + j * dir.x, tile.y + j * dir.y);

                if (other == null) continue;

                if (other instanceof Wall.WallBuild || other.isInsulated()) {
                    break;
                }

                if ((other.block.acceptsPayload || other.block.outputsPayload) && other.team == team && other != this) {
                    link = other;
                    break;
                }
            }
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);
            for (int i = 0; i < 4; i++) {
                if (blends(i) && i != rotation) {
                    Draw.rect(inRegion, x, y, (i * 90f) - 180f);
                }
            }
            if (link != null && link == front()) {
                Draw.rect(outRegion, x, y, rotdeg());
            }

            float sendProgress = Interp.pow2Out.apply(cooldown > cooldownTime - sendTime ? (cooldownTime - cooldown) / sendTime : cooldown / cooldownTime);
            float sendX = sendLength * sendProgress * Geometry.d4x[rotation], sendY = sendLength * sendProgress * Geometry.d4y[rotation];
            float sendScl = Mathf.lerp(1f, 0.75f, sendProgress);

            Draw.rect(under, x + sendX / 2f, y + sendY / 2f, rotdeg());
            Draw.scl(sendScl, 1f);
            Draw.rect(pad, x + sendX, y + sendY, rotdeg());
            Draw.scl();

            drawPayload();
            Draw.z(Layer.flyingUnit + 1f);
            jumpedPayloads.each(JumpedPayloadData::draw);
        }

        @Override
        public void drawSelect() {
            int maxLen = range + size / 2;
            int dx = Geometry.d4x[rotation], dy = Geometry.d4y[rotation];

            Drawf.dashLine(
                    Pal.place,
                    x + dx * (tilesize * size / 2f + 2),
                    y + dy * (tilesize * size / 2f + 2),
                    link != null ? link.x - dx * (tilesize * link.block.size / 2f + 2f) : x + dx * maxLen * tilesize,
                    link != null ? link.y - dy * (tilesize * link.block.size / 2f + 2f) : y + dy * maxLen * tilesize
            );

            if (link != null) {
                Drawf.square(link.x, link.y, link.block.size * tilesize / 2f + 2.5f, 0f, Pal.place);
            }
        }

        @Override
        public byte version() {
            return 5;
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(cooldown);
            write.i(jumpedPayloads.size);
            for (int i = 0; i < jumpedPayloads.size; i++) {
                JumpedPayloadData jumpedPayload = jumpedPayloads.get(i);

                Payload.write(jumpedPayload.payload, write);
                write.f(jumpedPayload.startX);
                write.f(jumpedPayload.startY);
                write.f(jumpedPayload.endX);
                write.f(jumpedPayload.endY);
                write.f(jumpedPayload.progress);
            }
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            if (revision >= 5) {
                cooldown = read.f();
                int size = read.i();
                for (int i = 0; i < size; i++) {
                    Payload payload = Payload.read(read);
                    float startX = read.f(), startY = read.f(), endX = read.f(), endY = read.f(), progress = read.f();
                    jumpedPayloads.add(Pools.obtain(JumpedPayloadData.class, JumpedPayloadData::new).set(payload, startX, startY, endX, endY, progress, this));
                }
            } else {
                read.f();
                read.f();
                read.bool();
                read.bool();
                if (read.bool()) {
                    read.i();
                    read.i();
                }
            }
        }
    }

    public static class JumpedPayloadData implements Poolable {
        public static float jumpSpeed = 4f;
        public static float jumpScl = 0.3f;
        public static float maxSclDistance = 7f * 8f;
        public Building source;
        public Payload payload;
        public float startX, startY, endX, endY;
        public float progress; // not 0-1 !!!!! maxes out with distance instead

        public JumpedPayloadData set(Payload payload, float startX, float startY, float endX, float endY, float progress, Building source) {
            this.payload = payload;
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.progress = progress;
            this.source = source;
            return this;
        }

        public boolean update() {
            if (progress < Mathf.dst(startX, startY, endX, endY)) {
                progress += jumpSpeed * Time.delta;
            } else {
                payload.set(endX, endY, Angles.angle(startX, startY, endX, endY));
                Building receiver = Vars.world.buildWorld(endX, endY);
                if (receiver != null && receiver.block.acceptsPayload) {
                    if (receiver.acceptPayload(source, payload)) {
                        Fx.unitDrop.at(receiver);
                        receiver.handlePayload(source, payload);
                        return true;
                    }
                } else {
                    // got launched onto nothing
                    return true;
                }
            }

            Tmp.v1.set(startX, startY).lerp(endX, endY, progress / Mathf.dst(startX, startY, endX, endY));
            payload.set(Tmp.v1.x, Tmp.v1.y, Angles.angle(startX, startY, endX, endY));

            return false;
        }

        public void draw() {
            float fin = progress / Mathf.dst(startX, startY, endX, endY);
            float sfin = Interp.pow2Out.apply(Mathf.clamp(Mathf.slope(fin)));

            Draw.scl(1f + (sfin * jumpScl * (Math.min(Mathf.dst(startX, startY, endX, endY), maxSclDistance) / maxSclDistance)));
            Draw.z(Layer.flyingUnit - 1f);
            payload.draw();
            Draw.scl();
        }

        @Override
        public void reset() {
            payload = null;
            startX = 0f;
            startY = 0f;
            endX = 0f;
            endY = 0f;
            progress = 0f;
        }
    }
}
