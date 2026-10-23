package aquarion.world.blocks.distribution;

import aquarion.annotations.Annotations;
import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.IntSet;
import java.util.LinkedHashMap;
import arc.struct.Seq;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.core.World;
import mindustry.gen.Building;
import mindustry.game.Team;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.Build;
import aquarion.world.graphics.Renderer;
import aquarion.world.graphics.RailDraw;

import static aquarion.world.graphics.Renderer.Layer.shadow;
import static mindustry.Vars.*;

/** Visual rail nodes with editable links constrained by endpoint positions and rotations. */
public class RailPoint extends Block{
    public static long graphRevision;
    public float range = 80f;
    public boolean fourDirections;
    /** Smallest allowed bend radius, in world units. */
    public float minRadius = 16f;
    //This is in tiles.
    public float trackWidth = 2f;
    public float railShadowX = -10f, railShadowY = -10f;
    public TextureRegion railRegion;
    private final RailObstructions obstructions = new RailObstructions(this);
    private record PlacementKey(float x, float y, int rotation, int placingRotation){}
    private static class PlacementMask{
        final IntSeq tiles = new IntSeq();
        final IntSet contains = new IntSet();
    }
    private final LinkedHashMap<PlacementKey, PlacementMask> placementMasks = new LinkedHashMap<>(32, 0.75f, true);
    private Object placementWorld;
    private int placementChanges, placementSettings;
    private long placementRevision;

    private PlacementMask placementMask(float x, float y, int rotation, int placingRotation){
        int settings = Float.floatToIntBits(range) * 31 + Float.floatToIntBits(minRadius);
        settings = settings * 31 + Float.floatToIntBits(trackWidth);
        settings = settings * 31 + Float.floatToIntBits(offset);
        settings = settings * 31 + size + (fourDirections ? 1 : 0);
        if(placementWorld != world || placementChanges != world.tileChanges || placementRevision != graphRevision || placementSettings != settings){
            placementMasks.clear();
            placementWorld = world;
            placementChanges = world.tileChanges;
            placementRevision = graphRevision;
            placementSettings = settings;
        }
        PlacementKey key = new PlacementKey(x, y, rotation, placingRotation);
        PlacementMask cached = placementMasks.get(key);
        if(cached != null) return cached;
        PlacementMask mask = new PlacementMask();
        int radius = Mathf.ceil(range / tilesize), tx = World.toTile(x), ty = World.toTile(y);
        float[] controls = new float[4];
        for(int ox = -radius; ox <= radius; ox++){
            for(int oy = -radius; oy <= radius; oy++){
                int tileX = tx + ox, tileY = ty + oy;
                if(world.tile(tileX, tileY) == null) continue;
                float wx = tileX * tilesize + offset, wy = tileY * tilesize + offset;
                if(Mathf.dst2(x, y, wx, wy) > range * range
                    || !connectionCurve(x, y, rotation, wx, wy, placingRotation, controls)) continue;
                int packed = Point2.pack(tileX, tileY);
                mask.tiles.add(packed);
                mask.contains.add(packed);
            }
        }
        if(placementMasks.size() >= 128) placementMasks.remove(placementMasks.keySet().iterator().next());
        placementMasks.put(key, mask);
        return mask;
    }

    private void drawPlacementTiles(IntSeq tiles, Color color, Team team, int placingRotation){
        Draw.color(color);
        for(int i = 0; i < tiles.size; i++){
            int packed = tiles.get(i), x = Point2.x(packed), y = Point2.y(packed);
            // Keep dynamic placement rules live; only static rail geometry is cached.
            if(team != null && !Build.validPlace(this, team, x, y, placingRotation)) continue;
            Fill.square(x * tilesize + offset, y * tilesize + offset, tilesize / 2f);
        }
    }
    public @Annotations.Load("@-top") TextureRegion topRegion;
    public @Annotations.Load("@-shadow") TextureRegion shadowRegion;
    public RailPoint(String name){
        super(name);
        rotate = true;
        solid = false;
        underBullets = true;
        update = true;
        destructible = true;
        configurable = true;
        saveConfig = false;
        config(Point2.class, (RailPointBuild build, Point2 point) -> {
            Building other = world.build(build.tileX() + point.x, build.tileY() + point.y);
            if(other instanceof RailPointBuild rail){
                build.setLink(rail, !build.hasLink(rail));
            }
        });
        config(Point2[].class, RailPointBuild::replaceLinks);
        config(Boolean.class, (RailPointBuild build, Boolean connect) -> {
            if(connect) build.replaceLinks(build.nearbyConfig());
            else build.replaceLinks(new Point2[0]);
        });
        configClear((RailPointBuild build) -> build.replaceLinks(new Point2[0]));
    }

    @Override
    public void init(){
        super.init();
        clipSize = Math.max(clipSize, (range + Math.max(Math.abs(railShadowX), Math.abs(railShadowY))) * 2f + size * tilesize);
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find(name, "junction");
        topRegion = Core.atlas.find(name + "-top");
        railRegion = Core.atlas.find(name + "-rail", Core.atlas.find("aquarion-rail", Core.atlas.find("rail")));
        if(railRegion.found()) trackWidth = railRegion.height * railRegion.scl();
    }

    @Override
    public TextureRegion[] icons(){
        return topRegion != null && topRegion.found()
            ? new TextureRegion[]{region, topRegion}
            : new TextureRegion[]{region};
    }

    public void eachNearby(float x, float y, Team team, Cons<Building> consumer){
        int radius = Mathf.ceil(range / tilesize);
        int tx = World.toTile(x), ty = World.toTile(y);
        for(int dx = -radius; dx <= radius; dx++){
            for(int dy = -radius; dy <= radius; dy++){
                Building other = world.build(tx + dx, ty + dy);
                if(!(other instanceof RailPointBuild) || other.team != team) continue;
                // Visit a multiblock's center tile only, so it cannot produce duplicate edges.
                if(other.tile.x != tx + dx || other.tile.y != ty + dy) continue;
                float distance = Mathf.dst2(x, y, other.x, other.y);
                if(distance < 0.01f || distance > range * range) continue;
                consumer.get(other);
            }
        }
    }

    public void eachConnection(float x, float y, int rotation, Team team, Cons<Building> consumer){
        eachNearby(x, y, team, other -> {
            if(canConnect(x, y, rotation, other.x, other.y, other.rotation)) consumer.get(other);
        });
    }

    public boolean canConnect(float x, float y, int rotation, float targetX, float targetY, int targetRotation){
        return connectionCurve(x, y, rotation, targetX, targetY, targetRotation, new float[4]);
    }

    private boolean connectionCurve(float x, float y, int rotation, float targetX, float targetY, int targetRotation, float[] controls){
        return curve(x, y, rotation, targetX, targetY, targetRotation, controls)
            && obstructions.clear(x, y, targetX, targetY, controls);
    }

    /** Solve the same tangent-constrained cubic for previews, placement areas and tracks.
     * Optional controls receives the two control points as x1, y1, x2, y2. */
    public boolean curve(float x, float y, int rotation, float targetX, float targetY, int targetRotation, float[] controls){
        // Canonical endpoint order ensures reversing a link produces exactly the same curve.
        if(x > targetX || (x == targetX && y > targetY)){
            boolean valid = curve(targetX, targetY, targetRotation, x, y, rotation, controls);
            if(valid && controls != null){
                float cx = controls[0], cy = controls[1];
                controls[0] = controls[2];
                controls[1] = controls[3];
                controls[2] = cx;
                controls[3] = cy;
            }
            return valid;
        }
        float dx = targetX - x, dy = targetY - y;
        float distance2 = dx * dx + dy * dy;
        if(distance2 < 0.01f || distance2 > range * range) return false;
        float distance = Mathf.sqrt(distance2);
        float handle = distance * 0.4f;
        // Cardinal axes are exact: no trigonometric rounding at perpendicular endpoints.
        boolean startFour = fourDirectionsAt(x, y), endFour = fourDirectionsAt(targetX, targetY);
        int preferred = Math.abs(dx) >= Math.abs(dy) ? 0 : 1;
        for(int startAxis = 0; startAxis < (startFour ? 2 : 1); startAxis++){
            for(int endAxis = 0; endAxis < (endFour ? 2 : 1); endAxis++){
                float ax = ((startFour ? preferred + startAxis : rotation) & 1) == 0 ? 1f : 0f, ay = 1f - ax;
                float bx = ((endFour ? preferred + endAxis : targetRotation) & 1) == 0 ? 1f : 0f, by = 1f - bx;
                for(int startSign = 1; startSign >= -1; startSign -= 2){
                    float ux = ax * startSign, uy = ay * startSign;
                    if(ux * dx + uy * dy < 0f) continue;
                    for(int endSign = 1; endSign >= -1; endSign -= 2){
                        float vx = bx * endSign, vy = by * endSign;
                        if(vx * dx + vy * dy < 0f) continue;
                        float c1x = x + ux * handle, c1y = y + uy * handle;
                        float c2x = targetX - vx * handle, c2y = targetY - vy * handle;
                        if(!smoothCurve(x, y, c1x, c1y, c2x, c2y, targetX, targetY)) continue;
                        if(controls != null){
                            controls[0] = c1x;
                            controls[1] = c1y;
                            controls[2] = c2x;
                            controls[3] = c2y;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean fourDirectionsAt(float x, float y){
        Building build = world == null ? null : world.buildWorld(x, y);
        return build != null && build.block instanceof RailPoint rail ? rail.fourDirections : fourDirections;
    }

    private boolean smoothCurve(float x, float y, float c1x, float c1y, float c2x, float c2y, float endX, float endY){
        float dx = endX - x, dy = endY - y;
        for(int i = 0; i <= 32; i++){
            float t = i / 32f, s = 1f - t;
            float vx = 3f * (s * s * (c1x - x) + 2f * s * t * (c2x - c1x) + t * t * (endX - c2x));
            float vy = 3f * (s * s * (c1y - y) + 2f * s * t * (c2y - c1y) + t * t * (endY - c2y));
            float speed2 = vx * vx + vy * vy;
            // Reject cusps and paths that double back along the connection.
            if(speed2 < 0.0001f || vx * dx + vy * dy < -0.0001f) return false;
            float accelX = 6f * (s * (c2x - 2f * c1x + x) + t * (endX - 2f * c2x + c1x));
            float accelY = 6f * (s * (c2y - 2f * c1y + y) + t * (endY - 2f * c2y + c1y));
            float cross = vx * accelY - vy * accelX;
            if(cross * cross * minRadius * minRadius > speed2 * speed2 * speed2) return false;
        }
        return true;
    }

    public void drawTrack(float x, float y, int rotation, Building target){
        drawTrack(x, y, rotation, target.x, target.y, target.rotation);
    }

    public void drawTrack(float x, float y, int rotation, float targetX, float targetY, int targetRotation){
        drawTrack(x, y, rotation, targetX, targetY, targetRotation, 0f, 0f);
    }

    private void drawTrack(float x, float y, int rotation, float targetX, float targetY, int targetRotation, float offsetX, float offsetY){
        float[] controls = new float[4];
        if(!connectionCurve(x, y, rotation, targetX, targetY, targetRotation, controls)) return;
        drawTrackCurve(x, y, targetX, targetY, controls, offsetX, offsetY);
    }

    private void drawTrackCurve(float x, float y, float targetX, float targetY, float[] controls, float offsetX, float offsetY){
        if(railRegion != null && railRegion.found()){
            RailDraw.curve(railRegion, x, y, targetX, targetY, controls, offsetX, offsetY);
        }else{
            Lines.curve(x + offsetX, y + offsetY, controls[0] + offsetX, controls[1] + offsetY,
                controls[2] + offsetX, controls[3] + offsetY, targetX + offsetX, targetY + offsetY,
                Math.max(16, Mathf.ceil(Mathf.dst(x, y, targetX, targetY))));
        }
    }

    public void drawPlacementArea(float x, float y, int rotation, int placingRotation, Color color, Team team){
        PlacementMask mask = placementMask(x, y, rotation, placingRotation);
        Renderer.requestRailPlacementPass(() -> drawPlacementTiles(mask.tiles, color, team, placingRotation));
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        float wx = x * tilesize + offset, wy = y * tilesize + offset;
        if(player == null) return;
        Team team = player.team();
        IntSeq visible = new IntSeq();
        IntSet seen = new IntSet();
        int cursor = Point2.pack(x, y);
        eachNearby(wx, wy, team, source -> {
            PlacementMask mask = placementMask(source.x, source.y, source.rotation, rotation);
            for(int i = 0; i < mask.tiles.size; i++){
                int tile = mask.tiles.get(i);
                if(seen.add(tile)) visible.add(tile);
            }
            if(mask.contains.contains(cursor)){
                float[] controls = new float[4];
                if(!curve(source.x, source.y, source.rotation, wx, wy, rotation, controls)) return;
                Color color = valid ? Pal.heal : Pal.remove;
                Draw.color(color);
                Lines.stroke(trackWidth);
                drawTrackCurve(source.x, source.y, wx, wy, controls, 0f, 0f);
                Drawf.square(source.x, source.y, size * tilesize / 2f + 1f, color);
            }
        });
        if(visible.size > 0) Renderer.requestRailPlacementPass(() -> drawPlacementTiles(visible, Pal.heal, team, rotation));
        Draw.reset();
    }
    public class RailPointBuild extends Building{
        public final IntSeq links = new IntSeq();
        private boolean linksInitialized;

        public boolean hasLink(RailPointBuild other){
            return links.contains(other.pos()) || other.links.contains(pos());
        }

        public boolean linkValid(RailPointBuild other){
            return other != this && other.team == team
                && dst(other) <= Math.min(range, ((RailPoint)other.block).range)
                && canConnect(x, y, rotation, other.x, other.y, other.rotation)
                && ((RailPoint)other.block).canConnect(other.x, other.y, other.rotation, x, y, rotation);
        }

        public void setLink(RailPointBuild other, boolean connected){
            if(other == this || other.team != team) return;
            if(connected && !linkValid(other)) return;
            linksInitialized = true;
            obstructions.invalidate();
            if(connected){
                links.addUnique(other.pos());
                other.links.addUnique(pos());
            }else{
                links.removeValue(other.pos());
                other.links.removeValue(pos());
            }
        }

        public Point2[] nearbyConfig(){
            Seq<Point2> points = new Seq<>();
            eachConnection(x, y, rotation, team, other -> points.add(new Point2(other.tileX() - tileX(), other.tileY() - tileY())));
            return points.toArray(Point2.class);
        }

        public void clearLinks(){
            obstructions.invalidate();
            for(int i = 0; i < links.size; i++){
                Building other = world.build(links.get(i));
                if(other instanceof RailPointBuild rail) rail.links.removeValue(pos());
            }
            // Configurations restored from schematics may temporarily be one-sided.
            eachNearby(x, y, team, other -> {
                if(other instanceof RailPointBuild rail) rail.links.removeValue(pos());
            });
            links.clear();
        }

        public void replaceLinks(Point2[] points){
            clearLinks();
            linksInitialized = true;
            for(Point2 point : points){
                if(point == null) continue;
                int tx = tileX() + point.x, ty = tileY() + point.y;
                if(Mathf.dst2(x, y, tx * tilesize + offset, ty * tilesize + offset) > range * range
                    || (point.x == 0 && point.y == 0)) continue;
                Building other = world.build(tx, ty);
                if(other instanceof RailPointBuild rail){
                    setLink(rail, true);
                }else if(other == null){
                    // Keep pending schematic links until their endpoint is built.
                    links.addUnique(Point2.pack(tx, ty));
                }
            }
        }

        @Override
        public void placed(){
            super.placed();
            obstructions.invalidate();
            if(!net.client() && !linksInitialized) configureAny(nearbyConfig());
        }

        @Override
        public void updateTile(){
            // Revision 0 worlds had implicit links; preserve those on their first server tick.
            if(!net.client() && !linksInitialized) configureAny(nearbyConfig());
        }

        @Override
        public void onRemoved(){
            clearLinks();
            super.onRemoved();
        }

        @Override
        public void pickedUp(){
            clearLinks();
            super.pickedUp();
        }

        @Override
        public void dropped(){
            super.dropped();
            linksInitialized = true;
            links.clear();
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(other == this){
                deselect();
                return false;
            }
            if(other instanceof RailPointBuild rail && other.team == team
                && (within(other, range) || hasLink(rail))){
                if(hasLink(rail) || linkValid(rail)) configure(new Point2(other.tileX() - tileX(), other.tileY() - tileY()));
                return false;
            }
            return true;
        }

        @Override
        public Object config(){
            Point2[] points = new Point2[links.size];
            for(int i = 0; i < links.size; i++){
                points[i] = new Point2(Point2.x(links.get(i)) - tileX(), Point2.y(links.get(i)) - tileY());
            }
            return points;
        }

        @Override
        public void drawConfigure(){
            eachNearby(x, y, team, other -> {
                if(!(other instanceof RailPointBuild rail)) return;
                boolean linked = hasLink(rail), valid = linkValid(rail);
                Color color = valid ? (linked ? Pal.heal : Pal.techBlue) : Pal.remove;
                Drawf.square(other.x, other.y, size * tilesize / 2f + 1f, color);
                if(linked && valid){
                    Draw.color(color);
                    Lines.stroke(trackWidth);
                    drawTrack(x, y, rotation, other);
                }
            });
            Draw.reset();
        }

        @Override
        public void draw(){
            super.draw();
            float previousZ = Draw.z();
            eachConnection(x, y, rotation, team, target -> {
                if(!(target instanceof RailPointBuild rail) || !hasLink(rail)) return;
                if(target.pos() < pos()) return;
                Draw.color(Color.white);
                Lines.stroke(trackWidth);
                Draw.z(shadow);
                drawTrack(x, y, rotation, target.x, target.y, target.rotation, railShadowX, railShadowY);
                Draw.z(Layer.blockOver+4);
                drawTrack(x, y, rotation, target);
                Draw.reset();
            });
            Draw.z(Layer.power-1.1f);
            drawUnderTop();
            Draw.z(Layer.power-1);
            if(topRegion != null && topRegion.found()) Draw.rect(topRegion, x,y,0);
            Draw.z(shadow);
            if(shadowRegion != null && shadowRegion.found()) Draw.rect(shadowRegion, x,y, 0);
            Draw.reset();
            Draw.z(previousZ);
        }

        protected void drawUnderTop(){}

        @Override
        public void drawSelect(){
            drawPlacementArea(x, y, rotation, rotation, Pal.accent, team);
        }

        @Override
        public byte version(){
            return 1;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.bool(linksInitialized);
            write.i(links.size);
            for(int i = 0; i < links.size; i++) write.i(links.get(i));
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            obstructions.invalidate();
            links.clear();
            linksInitialized = revision >= 1 && read.bool();
            if(revision >= 1){
                int count = read.i();
                for(int i = 0; i < count; i++) links.addUnique(read.i());
            }
        }
    }
}






