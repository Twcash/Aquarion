package aquarion.world.blocks.power;

import arc.func.Boolf;
import arc.func.Cons;
import arc.func.Func;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.IntSeq;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import mindustry.core.Renderer;
import mindustry.core.UI;
import mindustry.core.World;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.input.Placement;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.blocks.power.*;
import arc.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.meta.*;
import mindustry.world.modules.*;

import static mindustry.Vars.*;
public class PowerPylon extends PowerBlock {
    protected static BuildPlan otherReq;
    protected static int returnInt = 0;
    protected final static ObjectSet<PowerGraph> graphs = new ObjectSet<>();
    /** The maximum range of all power nodes on the map */
    protected static float maxRange;
    public TextureRegion cable;
    public TextureRegion cableEnd;
    public TextureRegion glow;
    public TextureRegion glowBase;
    public float laserRange = 6;
    public int maxNodes = 3;
    public boolean autolink = true, drawRange = true, sameBlockConnection = false;

    @Override
    public void load(){
        super.load();
        cableEnd = Core.atlas.find(this.name + "-cable-end");
        cable = Core.atlas.find(this.name + "-cable");
        glow = Core.atlas.find(this.name + "-cable-glow");
        glowBase = Core.atlas.find(this.name + "-glow");
    }
    public PowerPylon(String name){
        super(name);
        configurable = true;
        consumesPower = false;
        insulated = true;
        outputsPower = false;
        canOverdrive = false;
        swapDiagonalPlacement = true;
        schematicPriority = -10;
        drawDisabled = false;
        envEnabled |= Env.space;
        destructible = true;

        //nodes do not even need to update
        update = false;

        config(Integer.class, (entity, value) -> {
            PowerModule power = entity.power;
            Building other = world.build(value);
            boolean contains = power.links.contains(value), valid = other != null && other.power != null;

            if(contains){
                //unlink
                power.links.removeValue(value);
                if(valid) other.power.links.removeValue(entity.pos());

                PowerGraph newgraph = new PowerGraph();

                //reflow from this point, covering all tiles on this side
                newgraph.reflow(entity);

                if(valid && other.power.graph != newgraph){
                    //create new graph for other end
                    PowerGraph og = new PowerGraph();
                    //reflow from other end
                    og.reflow(other);
                }
            }else if(linkValid(entity, other) && valid && power.links.size < maxNodes){

                power.links.addUnique(other.pos());

                if(other.team == entity.team){
                    other.power.links.addUnique(entity.pos());
                }

                power.graph.addGraph(other.power.graph);
            }
        });

        config(Point2[].class, (tile, value) -> {
            IntSeq old = new IntSeq(tile.power.links);

            //clear old
            for(int i = 0; i < old.size; i++){
                configurations.get(Integer.class).get(tile, old.get(i));
            }

            //set new
            for(Point2 p : value){
                configurations.get(Integer.class).get(tile, Point2.pack(p.x + tile.tileX(), p.y + tile.tileY()));
            }
        });
    }
    public static boolean isAllowedLinkTarget(Building build){
        return build instanceof PowerPylonBuild || build instanceof PowerGenerator.GeneratorBuild || build instanceof PowerOutlet.OutletBuild || (build instanceof Battery.BatteryBuild && build.block.insulated);
    }
    @Override
    public void setBars(){
        super.setBars();
        addBar("power", makePowerBalance());
        addBar("batteries", makeBatteryBalance());

        addBar("connections", entity -> new Bar(() ->
                Core.bundle.format("bar.powerlines", entity.power.links.size, maxNodes),
                () -> Pal.items,
                () -> (float)entity.power.links.size / (float)maxNodes
        ));
    }

    public static Func<Building, Bar> makePowerBalance(){
        return entity -> new Bar(() ->
                Core.bundle.format("bar.powerbalance",
                        ((entity.power.graph.getPowerBalance() >= 0 ? "+" : "") + UI.formatAmount((long)(entity.power.graph.getPowerBalance() * 60)))),
                () -> Pal.powerBar,
                () -> Mathf.clamp(entity.power.graph.getLastPowerProduced() / entity.power.graph.getLastPowerNeeded())
        );
    }

    public static Func<Building, Bar> makeBatteryBalance(){
        return entity -> new Bar(() ->
                Core.bundle.format("bar.powerstored",
                        (UI.formatAmount((long)entity.power.graph.getLastPowerStored())), UI.formatAmount((long)entity.power.graph.getLastCapacity())),
                () -> Pal.powerBar,
                () -> Mathf.clamp(entity.power.graph.getLastPowerStored() / entity.power.graph.getLastCapacity())
        );
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.powerRange, laserRange, StatUnit.blocks);
        stats.add(Stat.powerConnections, maxNodes, StatUnit.none);
    }

    @Override
    public void init(){
        super.init();

        clipSize = Math.max(clipSize, laserRange * tilesize);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Tile tile = world.tile(x, y);

        if(tile == null || !autolink) return;

        Lines.stroke(1f);
        Draw.color(Pal.placing);
        Drawf.circles(x * tilesize + offset, y * tilesize + offset, laserRange * tilesize);

        getPotentialLinks(tile, player.team(), other -> {
            drawLaser(x * tilesize + offset, y * tilesize + offset, other.x, other.y, size, other.block.size);

            Drawf.square(other.x, other.y, other.block.size * tilesize / 2f + 2f, Pal.place);
        });

        Draw.reset();
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation){
        Placement.calculateNodes(points, this, rotation, (point, other) -> overlaps(world.tile(point.x, point.y), world.tile(other.x, other.y)));
    }



    public void drawLaser(float x1, float y1, float x2, float y2, int size1, int size2){
        float angle1 = Angles.angle(x1, y1, x2, y2),
                vx = Mathf.cosDeg(angle1), vy = Mathf.sinDeg(angle1),
                len1 = size1 * tilesize / 2f - 1.5f, len2 = size2 * tilesize / 2f - 1.5f;
        Lines.stroke(2);
        float thickness = 8;
        float angle = Angles.angle(x1, y1, x2, y2);
        Vec2 pos1 = new Vec2(x1, y1), pos2 = new Vec2(x2, y2);

        boolean reverse = pos1.x > pos2.x;
        if(reverse)Draw.xscl = -1;

        Lines.stroke(thickness);
        Lines.line(cable, x1 + vx*len1, y1 + vy*len1, x2 - vx*len2, y2 - vy*len2, false);
        Draw.rect(cableEnd, x1 + vx*len1, y1 + vy*len1, angle);
        Draw.xscl = -1f;
        Draw.rect(cableEnd, x2 + vx*len1, y2 + vy*len1, angle);
        Draw.xscl = Draw.yscl = 1f;
    }

    public boolean overlaps(float srcx, float srcy, Tile other, Block otherBlock, float range){
        return Intersector.overlaps(Tmp.cr1.set(srcx, srcy, range), Tmp.r1.setCentered(other.worldx() + otherBlock.offset, other.worldy() + otherBlock.offset,
                otherBlock.size * tilesize, otherBlock.size * tilesize));
    }

    public boolean overlaps(float srcx, float srcy, Tile other, float range){
        return Intersector.overlaps(Tmp.cr1.set(srcx, srcy, range), other.getHitbox(Tmp.r1));
    }

    public boolean overlaps(Building src, Building other, float range){
        return overlaps(src.x, src.y, other.tile, range);
    }

    protected boolean overlaps(Tile src, Tile other, float range){
        return overlaps(src.drawx(), src.drawy(), other, range);
    }

    public boolean overlaps(@Nullable Tile src, @Nullable Tile other){
        if(src == null || other == null) return true;
        return Intersector.overlaps(Tmp.cr1.set(src.worldx() + offset, src.worldy() + offset, laserRange * tilesize), Tmp.r1.setSize(size * tilesize).setCenter(other.worldx() + offset, other.worldy() + offset));
    }

    protected void getPotentialLinks(Tile tile, Team team, Cons<Building> others){
        if(!autolink) return;

        Boolf<Building> valid = other -> other != null && other.tile != tile && other.block.connectedPower && other.power != null &&
                isAllowedLinkTarget(other) &&
                overlaps(tile.x * tilesize + offset, tile.y * tilesize + offset, other.tile, laserRange * tilesize) && other.team == team &&
                !graphs.contains(other.power.graph) &&
                !(other instanceof PowerPylonBuild obuild && obuild.power.links.size >= ((PowerPylon)obuild.block).maxNodes) &&
                !Structs.contains(Edges.getEdges(size), p -> { //do not link to adjacent buildings
                    var t = world.tile(tile.x + p.x, tile.y + p.y);
                    return t != null && t.build == other;
                });

        tempBuilds.clear();
        graphs.clear();

        if(tile.build != null && tile.build.power != null){
            graphs.add(tile.build.power.graph);
        }

        var worldRange = laserRange * tilesize;
        var tree = team.data().buildingTree;
        if(tree != null){
            tree.intersect(tile.worldx() - worldRange, tile.worldy() - worldRange, worldRange * 2, worldRange * 2, build -> {
                if(valid.get(build) && !tempBuilds.contains(build)){
                    tempBuilds.add(build);
                }
            });
        }

        tempBuilds.sort((a, b) -> {
            int type = -Boolean.compare(a.block instanceof PowerPylon, b.block instanceof PowerPylon);
            if(type != 0) return type;
            return Float.compare(a.dst2(tile), b.dst2(tile));
        });

        returnInt = 0;

        tempBuilds.each(valid, t -> {
            if(returnInt ++ < maxNodes){
                graphs.add(t.power.graph);
                others.get(t);
            }
        });
    }
    @Override
    public void drawPlanConfigTop(BuildPlan plan, Eachable<BuildPlan> list){
        if(plan.config instanceof Point2[] ps){
            for(Point2 point : ps){
                int px = plan.x + point.x, py = plan.y + point.y;
                otherReq = null;
                list.each(other -> {
                    if(other.block != null
                            && (px >= other.x - ((other.block.size-1)/2) && py >= other.y - ((other.block.size-1)/2) && px <= other.x + other.block.size/2 && py <= other.y + other.block.size/2)
                            && other != plan && other.block.hasPower && (other.build() instanceof PowerPylonBuild || other.build() instanceof PowerGenerator.GeneratorBuild || other.build() instanceof PowerOutlet.OutletBuild)){
                        otherReq = other;
                    }
                });

                if(otherReq == null || otherReq.block == null) continue;

                drawLaser(plan.drawx(), plan.drawy(), otherReq.drawx(), otherReq.drawy(), size, otherReq.block.size);
            }
            Draw.color();
        }
    }

    public boolean linkValid(Building tile, Building link){
        return linkValid(tile, link, true);
    }

    public boolean linkValid(Building tile, Building link, boolean checkMaxNodes){
        if(tile == link || link == null || !link.block.hasPower || !link.block.connectedPower || tile.team != link.team || (sameBlockConnection && tile.block != link.block && !isAllowedLinkTarget(link))) return false;

        if(overlaps(tile, link, laserRange * tilesize) || (link.block instanceof PowerPylon node && overlaps(link, tile, node.laserRange * tilesize))){
            if(checkMaxNodes && link.block instanceof PowerPylon node){
                return link.power.links.size < node.maxNodes || link.power.links.contains(tile.pos()) && isAllowedLinkTarget(link);
            }
            return isAllowedLinkTarget(link);
        }
        return false;
    }

    public static boolean insulated(Tile tile, Tile other){
        return insulated(tile.x, tile.y, other.x, other.y);
    }

    public static boolean insulated(Building tile, Building other){
        return insulated(tile.tileX(), tile.tileY(), other.tileX(), other.tileY());
    }

    public static boolean insulated(int x, int y, int x2, int y2){
        return World.raycast(x, y, x2, y2, (wx, wy) -> {
            Building tile = world.build(wx, wy);
            return tile != null && tile.isInsulated();
        });
    }

    public class PowerPylonBuild extends Building{

        @Override
        public void created(){ // Called when one is placed/loaded in the world
            if(autolink && laserRange > maxRange) maxRange = laserRange;

            super.created();
        }


        @Override
        public void placed(){
            if(net.client() || power.links.size > 0) return;

            getPotentialLinks(tile, team, other -> {
                if(!power.links.contains(other.pos())){
                    configureAny(other.pos());
                }
            });

            super.placed();
        }

        @Override
        public void dropped(){
            power.links.clear();
            updatePowerGraph();
        }
        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(linkValid(this, other)){
                configure(other.pos());
                return false;
            }

            if(this == other){ //double tapped
                if(other.power.links.size == 0){ //find links
                    Seq<Point2> points = new Seq<>();
                    getPotentialLinks(tile, team, link -> {
                        if(points.size < maxNodes){
                            if(isAllowedLinkTarget(link))points.add(new Point2(link.tileX() - tile.x, link.tileY() - tile.y));

                        }
                    });
                    configure(points.toArray(Point2.class));
                }else{ //clear links
                    configure(new Point2[0]);
                }
                deselect();
                return false;
            }

            return true;
        }

        @Override
        public void drawSelect(){
            super.drawSelect();

            if(!drawRange) return;

            Lines.stroke(1f);

            Draw.color(Pal.accent);
            Drawf.circles(x, y, laserRange * tilesize);
            Draw.reset();
        }

        @Override
        public void drawConfigure(){

            Drawf.circles(x, y, tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f));

            if(drawRange){
                Drawf.circles(x, y, laserRange * tilesize);

                for(int x = (int)(tile.x - laserRange - 2); x <= tile.x + laserRange + 2; x++){
                    for(int y = (int)(tile.y - laserRange - 2); y <= tile.y + laserRange + 2; y++){
                        Building link = world.build(x, y);

                        if(link != this && linkValid(this, link, false)){
                            boolean linked = linked(link);

                            if(linked){
                                Drawf.square(link.x, link.y, link.block.size * tilesize / 2f + 1f, Pal.place);
                            }
                        }
                    }
                }

                Draw.reset();
            }else{
                power.links.each(i -> {
                    var link = world.build(i);
                    if(link != null && linkValid(this, link, false)){
                        Drawf.square(link.x, link.y, link.block.size * tilesize / 2f + 1f, Pal.place);
                    }
                });
            }
        }

        @Override
        public void draw(){
            super.draw();
            Draw.color(Color.red);
            float powerDiff = this.power.graph.getPowerProduced() - this.power.graph.getPowerNeeded();
            float mag = Mathf.clamp(powerDiff / 100f, 0f, 0.5f);
            Draw.alpha(Mathf.absin(Time.time, 10f, mag));
            Draw.rect(glowBase, x, y, 0);
            Draw.reset();

            if(Mathf.zero(Renderer.laserOpacity) || isPayload() || team == Team.derelict) return;

            Draw.z(Layer.power);

            for(int i = 0; i < power.links.size; i++){
                Building link = world.build(power.links.get(i));

                if(!linkValid(this, link)) continue;

                if(link.block instanceof PowerPylon && link.id >= id) continue;

                float angle1 = Angles.angle(x, y, link.x, link.y),
                        vx = Mathf.cosDeg(angle1), vy = Mathf.sinDeg(angle1),
                        len1 = this.block.size * tilesize / 2f - 1.5f, len2 = link.block.size * tilesize / 2f - 1.5f;
                Lines.stroke(2);
                float thickness = 8;
                float angle = Angles.angle(x, y, link.x, link.y);
                Vec2 pos1 = new Vec2(x, y), pos2 = new Vec2(link.x, link.y);
                boolean reverse = pos1.x > pos2.x;
                if(reverse)Draw.xscl = -1;

                Lines.stroke(thickness);
                Lines.line(cable, x + vx*len1, y + vy*len1, link.x - vx*len2, link.y - vy*len2, false);
                Draw.rect(cableEnd, x + vx*len1, y + vy*len1, angle);
                Draw.xscl = -1f;
                Draw.rect(cableEnd, link.x - vx*len2, link.y - vy*len2, angle);
                Draw.xscl = Draw.yscl = 1f;
                Draw.blend(Blending.additive);
                Draw.color(Color.red);
                Draw.alpha(Mathf.absin(Time.time, 10f, mag));
                Lines.line(glow, x + vx*len1, y + vy*len1, link.x - vx*len2, link.y - vy*len2, false);
                Draw.reset();
                Draw.reset();
                Draw.blend();
            }

            Draw.reset();
        }

        protected boolean linked(Building other){
            return power.links.contains(other.pos());
        }

        @Override
        public Point2[] config(){
            Point2[] out = new Point2[power.links.size];
            for(int i = 0; i < out.length; i++){
                out[i] = Point2.unpack(power.links.get(i)).sub(tile.x, tile.y);
            }
            return out;
        }
    }
}