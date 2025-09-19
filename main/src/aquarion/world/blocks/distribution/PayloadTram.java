package aquarion.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.Queue;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.core.Renderer;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.*;

//Thanks meep... I'm useless.
public class PayloadTram extends PayloadBlock {
    public TextureRegion baseRegion;
    public TextureRegion topRegion;
    public TextureRegion bridgeRegion;
    public TextureRegion capRegion;
    public TextureRegion endRegion;
    public float thicc;
    public TextureRegion arrowRegion;
    public float grabWidth = 8f, grabHeight = 11/4f;
    public float targetSize = grabWidth*2f, curSize = targetSize;
    public float speed;
    public float maxPayloadSize = 3;
    public float range;
    public float distMultiplier = 1f;
    //TODO implement max links for a payload tram 3-5 at least
    public float maxConnections = 2f;
    public PayloadTram(String name) {
        super(name);
        thicc = 8;
        update = true;
        solid = true;
        configurable = true;
        hasPower = true;
        //clipSize has to be massive or visual bugs
        clipSize = 300;
        rotate = true;
        payloadSpeed = 0.7f;
        outputsPayload = true;
        group = BlockGroup.units;
        config(Point2.class, (PayloadTramBuild tile, Point2 point) -> tile.link = Point2.pack(point.x + tile.tileX(), point.y + tile.tileY()));
        config(Integer.class, (PayloadTramBuild tile, Integer point) -> tile.link = point);
    }

    public boolean positionsValid(int x1, int y1, int x2, int y2) {
        return Mathf.within(x1, y1, x2, y2, range + 0.5f);
    }


    @Override
    public void load() {
        super.load();
        baseRegion = Core.atlas.find(name);
        topRegion = Core.atlas.find(name + "-top");
        capRegion = Core.atlas.find(name + "-cap");
        bridgeRegion = Core.atlas.find(name + "-bridge");
        endRegion = Core.atlas.find(name + "-bridge-end");
        arrowRegion = Core.atlas.find(name + "-bridge-arrow");
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{baseRegion, outRegion, topRegion};
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(baseRegion, plan.drawx(), plan.drawy());
        Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.rect(topRegion, plan.drawx(), plan.drawy());
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.shootRange, range / tilesize, StatUnit.blocks);
        stats.add(Stat.payloadCapacity, StatValues.squared(maxPayloadSize, StatUnit.blocksSquared));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize, y * tilesize, range, Pal.accent);

        //uagh oog abugg
        if (!control.input.config.isShown()) return;
        Building selected = control.input.config.getSelected();
        if (selected == null || selected.block != this || !selected.within(x * tilesize, y * tilesize, range)) return;

        //line
        float sin = Mathf.absin(Time.time, 6f, 1f);
        Tmp.v1.set(x * tilesize + offset, y * tilesize + offset).sub(selected.x, selected.y).limit((size / 2f + 1) * tilesize + sin + 0.5f);
        float x2 = x * tilesize - Tmp.v1.x, y2 = y * tilesize - Tmp.v1.y,
                x1 = selected.x + Tmp.v1.x, y1 = selected.y + Tmp.v1.y;
        int segs = (int) (selected.dst(x * tilesize, y * tilesize) / tilesize);

        Lines.stroke(4f, Pal.gray);
        Lines.dashLine(x1, y1, x2, y2, segs);
        Lines.stroke(2f, Pal.placing);
        Lines.dashLine(x1, y1, x2, y2, segs);
        Draw.reset();
    }

    public class PayloadTramBuild extends PayloadBlockBuild<Payload> {
        public int link = -1;
        public @Nullable Payload item;
        public float progress, itemRotation, animation;
        public float curInterp, lastInterp;
        public int step = -1, stepAccepted = -1;
        public float payLength = 0f;
        public Payload recPayload;
        public Queue<Building> waitingTram = new Queue<>();
        public boolean sending;
        public boolean dumping;
        public boolean sent;
        public boolean accepting = false;
        public boolean f = false;
        public boolean loaded;
        public boolean received = false;

        @Override
        public Payload getPayload() {
            return item;
        }

        public Building currentShooter() {
            return waitingTram.isEmpty() ? null : waitingTram.first();
        }
        //Seeing this will always awaken a deep trauma inside of me. Life is meaningless and cruel
        @Override
        public void updateTile() {
            super.updateTile();
            if(received && !linkValid()){
                dumping = true;
                accepting = true;
                loaded = false;
            }
            if(!linkValid()){
                moveOutPayload();
            }
            Building link = world.build(this.link);
            PayloadTramBuild other = (PayloadTramBuild) link;
            if(linkValid()){
                if(payload != null) {
                    updatePayload();
                    accepting = true;
                    if(other.payload == null){
                        int dist = (int) (link.dst(x, y));
                        float requiredProgress = 1f + (dist * distMultiplier); // Adjust multiplier as needed
                        progress += edelta();
                        sent = true;
                        loaded = true;
                        if (progress >= requiredProgress) {
                            // Handle the payload to the linked tram
                            other.handlePayload(this, payload);
                            this.updatePayload();
                            payload = null;
                            other.updatePayload();
                            other.payVector.setZero();
                            other.loaded = true;
                            loaded = false;
                            sent = false;
                            progress = 0;
                            other.received = true;
                            sending = false;
                            other.recPayload = null;
                        }
                }else{
                        //other link already has payload
                        loaded = false;
                        sending = false;
                    }
                }else{
                    //no payload
                    loaded = moveInPayload();
                }
                }else{
                //no link dump payloads
                moveOutPayload();
                sending = false;
                loaded = false;
            }
        }
        @Override
        public void updatePayload(){
            if(payload != null){
                if(!sending) {
                    payload.set(x + payVector.x, y + payVector.y, payRotation);
                }
            }
        }
        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return super.acceptPayload(source, payload) && payload.size() <= maxPayloadSize * tilesize;
        }
        @Override
        public void draw() {
            Draw.rect(region, x, y);
            //draw input
            for (int i = 0; i < 4; i++) {
                if (blends(i) && i != rotation) {
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                }
            }
            Draw.rect(outRegion, x, y, rotdeg());
            if(payload != null) {
                if(loaded){
                    Draw.z(Layer.blockOver);
                    Draw.alpha(0);
                    drawPayload();
                    Draw.reset();
                }else{
                drawPayload();
                }
            }
            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(topRegion, x, y);
            Draw.z(Layer.flyingUnitLow - 0.1f);
            Draw.rect(capRegion, x, y);
            if (linkValid()) {
                Building link = world.build(this.link);
                PayloadTramBuild other = (PayloadTramBuild) link;
                float x1 = this.x;
                float x2 = other.x;
                float y1 = this.y;
                int dist = (int) (link.dst(x, y));
                float y2 = other.y;
                float requiredProgress = 1f + (dist * distMultiplier);
                float lerpX = Mathf.lerp(x1, x2, progress / requiredProgress);
                float lerpY = Mathf.lerp(y1, y2, progress / requiredProgress);
                if (payload != null && sent) {
                    Draw.z(Layer.flyingUnitLow - 0.3f);
                    TextureRegion payloadthing = payload.content().uiIcon;
                    Draw.rect(payloadthing, lerpX, lerpY);
                }
                updatePayload();
            }
            Building target = world.build(link);
            if (linkValid()) {
                Draw.z(Layer.flyingUnitLow - 0.2f);
                float angle = this.angleTo(target) - 90;
                //Until I find a better way this is hardcoded
                if (angle >= 0f && angle < 180f) Draw.yscl = -1f;
                float thickness;
                if (angle >= 0f && angle < 180f){
                    thickness = thicc * -1;
                }else{
                    thickness = thicc;
                }
                Lines.stroke(thickness);
                Draw.z(Layer.flyingUnitLow - 0.3f);
                Draw.alpha(Renderer.bridgeOpacity);
                Lines.line(bridgeRegion, this.x, this.y, target.x, target.y, false);
                Draw.rect(endRegion, this.x, this.y, angle + 90f);
                Draw.xscl = -1f;
                Draw.z(Layer.flyingUnitLow - 0.2f);
                Draw.rect(endRegion, target.x, target.y, angle + 90f);
                Draw.xscl = Draw.yscl = 1f;
            }
        }

        @Override
        public void drawConfigure() {
            float sin = Mathf.absin(Time.time, 6f, 1f);

            Draw.color(Pal.accent);
            Lines.stroke(1f);
            Drawf.circles(x, y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.accent);

            for (Building shooter : waitingTram) {
                Drawf.circles(shooter.x, shooter.y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.place);
                Drawf.arrow(shooter.x, shooter.y, x, y, size * tilesize + sin, 4f + sin, Pal.place);
            }
            if (linkValid()) {
                Building target = world.build(link);
                Drawf.circles(target.x, target.y, (target.block.size / 2f + 1) * tilesize + sin - 2f, Pal.place);
                Drawf.arrow(x, y, target.x, target.y, size * tilesize + sin, 4f + sin);
            }

            Drawf.dashCircle(x, y, range, Pal.accent);
        }

        protected boolean linkValid() {
            Building other = world.build(this.link);
            return link != -1 && other instanceof PayloadTramBuild && other.block == block && other.team == team && within(other, range);
        }

        @Override
        public boolean onConfigureBuildTapped(Building other) {
            if (this == other) {
                if (link == -1) deselect();
                configure(-1);
                return false;
            }

            if (link == other.pos()) {
                configure(-1);
                return false;
            } else if (other.block instanceof PayloadTram && other.dst(tile) <= range && other.team == team) {
                configure(other.pos());
                return false;
            }

            return true;
        }
        @Override
        public void drawPayload() {
            if (payload != null) {
                if (!loaded) {
                    updatePayload();
                    Draw.z(Layer.blockOver);
                    payload.draw();
                }
            }
        }

        @Override
        public byte version() {
            return 1;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(link);
            write.bool(loaded);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            link = read.i();
            loaded = read.bool();
        }
    }
}