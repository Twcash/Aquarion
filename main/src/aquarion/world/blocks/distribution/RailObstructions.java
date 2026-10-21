package aquarion.world.blocks.distribution;

import arc.Core;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.world.Tile;

import static mindustry.Vars.*;

/** Collision checks against the curved route, rather than its endpoint chord. */
final class RailObstructions{
    private final RailPoint block;
    private final Seq<Path> tracks = new Seq<>();
    private long frame = Long.MIN_VALUE;
    private int changes = Integer.MIN_VALUE;
    private long revision = Long.MIN_VALUE;
    private Object cachedWorld;

    RailObstructions(RailPoint block){
        this.block = block;
    }

    void invalidate(){
        RailPoint.graphRevision++;
        frame = Long.MIN_VALUE;
    }

    boolean clear(float x, float y, float endX, float endY, float[] controls){
        if(world == null) return true;
        Path path = new Path(x, y, endX, endY, controls);
        if(!tilesClear(path)) return false;
        refreshTracks();
        for(Path other : tracks){
            if(path.sameEndpoints(other)) continue;
            if(path.maxX < other.minX || path.minX > other.maxX
                || path.maxY < other.minY || path.minY > other.maxY) continue;
            for(int i = 2; i < path.points.length; i += 2){
                for(int j = 2; j < other.points.length; j += 2){
                    if(crosses(path, i, other, j)) return false;
                }
            }
        }
        return true;
    }

    private void refreshTracks(){
        long currentFrame = Core.graphics == null ? -1L : Core.graphics.getFrameId();
        if(changes == world.tileChanges
            && revision == RailPoint.graphRevision && cachedWorld == world) return;
        tracks.clear();
        frame = currentFrame;
        changes = world.tileChanges;
        revision = RailPoint.graphRevision;
        cachedWorld = world;
        if(Groups.build == null) return;
        float[] controls = new float[4];
        for(Building building : Groups.build){
            if(!(building instanceof RailPoint.RailPointBuild source) || source.isPayload()) continue;
            for(int i = 0; i < source.links.size; i++){
                Building target = world.build(source.links.get(i));
                if(!(target instanceof RailPoint.RailPointBuild rail) || target.team != source.team) continue;
                if(source.pos() > target.pos() && rail.links.contains(source.pos())) continue;
                // Geometry only: recursively testing track collisions here would never terminate.
                if(!((RailPoint)source.block).curve(source.x, source.y, source.rotation, target.x, target.y, target.rotation, controls)) continue;
                Path path = new Path(source.x, source.y, target.x, target.y, controls);
                if(tilesClear(path)) tracks.add(path);
            }
        }
    }

    private boolean tilesClear(Path path){
        float margin = block.trackWidth / 2f;
        for(int i = 2; i < path.points.length; i += 2){
            float x1 = path.points[i - 2], y1 = path.points[i - 1], x2 = path.points[i], y2 = path.points[i + 1];
            int minX = Mathf.ceil((Math.min(x1, x2) - margin) / tilesize - 0.5f);
            int maxX = Mathf.floor((Math.max(x1, x2) + margin) / tilesize + 0.5f);
            int minY = Mathf.ceil((Math.min(y1, y2) - margin) / tilesize - 0.5f);
            int maxY = Mathf.floor((Math.max(y1, y2) + margin) / tilesize + 0.5f);
            for(int tx = minX; tx <= maxX; tx++){
                for(int ty = minY; ty <= maxY; ty++){
                    Tile tile = world.tile(tx, ty);
                    if(tile == null) return false;
                    boolean wall = tile.block().isStatic() && tile.block().solid;
                    boolean node = tile.block() instanceof RailPoint;
                    if(node){
                        float nx = tile.build == null ? tx * tilesize : tile.build.x;
                        float ny = tile.build == null ? ty * tilesize : tile.build.y;
                        if(path.endpoint(nx, ny)) node = false;
                    }
                    if(!wall && !node) continue;
                    float half = tilesize / 2f + margin;
                    if(segmentRect(x1, y1, x2, y2, tx * tilesize - half, ty * tilesize - half,
                        tx * tilesize + half, ty * tilesize + half)) return false;
                }
            }
        }
        return true;
    }

    private boolean segmentRect(float x1, float y1, float x2, float y2, float minX, float minY, float maxX, float maxY){
        float dx = x2 - x1, dy = y2 - y1, lo = 0f, hi = 1f;
        if(Math.abs(dx) < 0.000001f){
            if(x1 < minX || x1 > maxX) return false;
        }else{
            float a = (minX - x1) / dx, b = (maxX - x1) / dx;
            lo = Math.max(lo, Math.min(a, b));
            hi = Math.min(hi, Math.max(a, b));
        }
        if(Math.abs(dy) < 0.000001f){
            if(y1 < minY || y1 > maxY) return false;
        }else{
            float a = (minY - y1) / dy, b = (maxY - y1) / dy;
            lo = Math.max(lo, Math.min(a, b));
            hi = Math.min(hi, Math.max(a, b));
        }
        return lo <= hi;
    }

    private boolean crosses(Path a, int i, Path b, int j){
        float ax = a.points[i - 2], ay = a.points[i - 1], bx = b.points[j - 2], by = b.points[j - 1];
        if(Math.max(ax, a.points[i]) < Math.min(bx, b.points[j])
            || Math.min(ax, a.points[i]) > Math.max(bx, b.points[j])
            || Math.max(ay, a.points[i + 1]) < Math.min(by, b.points[j + 1])
            || Math.min(ay, a.points[i + 1]) > Math.max(by, b.points[j + 1])) return false;
        float rx = a.points[i] - ax, ry = a.points[i + 1] - ay;
        float sx = b.points[j] - bx, sy = b.points[j + 1] - by;
        float qx = bx - ax, qy = by - ay, denominator = rx * sy - ry * sx;
        if(Math.abs(denominator) < 0.000001f){
            if(Math.abs(qx * ry - qy * rx) > 0.00001f) return false;
            float length2 = rx * rx + ry * ry;
            if(length2 < 0.000001f) return false;
            float t0 = (qx * rx + qy * ry) / length2, t1 = t0 + (sx * rx + sy * ry) / length2;
            float lo = Math.max(0f, Math.min(t0, t1)), hi = Math.min(1f, Math.max(t0, t1));
            if(lo > hi) return false;
            return !junction(a, b, ax + lo * rx, ay + lo * ry) || !junction(a, b, ax + hi * rx, ay + hi * ry);
        }
        float t = (qx * sy - qy * sx) / denominator, u = (qx * ry - qy * rx) / denominator;
        if(t < 0f || t > 1f || u < 0f || u > 1f) return false;
        return !junction(a, b, ax + t * rx, ay + t * ry);
    }

    private boolean junction(Path a, Path b, float x, float y){
        float half = block.size * tilesize / 2f;
        for(int end = 0; end < 2; end++){
            int index = end == 0 ? 0 : a.points.length - 2;
            float nx = a.points[index], ny = a.points[index + 1];
            if(b.endpoint(nx, ny) && Math.abs(x - nx) <= half && Math.abs(y - ny) <= half) return true;
        }
        return false;
    }

    private static class Path{
        final float[] points;
        float minX = Float.POSITIVE_INFINITY, minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;

        Path(float x, float y, float endX, float endY, float[] c){
            float length = Mathf.dst(x, y, c[0], c[1]) + Mathf.dst(c[0], c[1], c[2], c[3]) + Mathf.dst(c[2], c[3], endX, endY);
            int steps = Math.max(16, Mathf.ceil(length / (tilesize / 4f)));
            points = new float[(steps + 1) * 2];
            for(int i = 0; i <= steps; i++){
                float t = i / (float)steps, s = 1f - t;
                float px = s * s * s * x + 3f * s * s * t * c[0] + 3f * s * t * t * c[2] + t * t * t * endX;
                float py = s * s * s * y + 3f * s * s * t * c[1] + 3f * s * t * t * c[3] + t * t * t * endY;
                points[i * 2] = px;
                points[i * 2 + 1] = py;
                minX = Math.min(minX, px);
                minY = Math.min(minY, py);
                maxX = Math.max(maxX, px);
                maxY = Math.max(maxY, py);
            }
        }

        boolean endpoint(float x, float y){
            int end = points.length - 2;
            return (Math.abs(points[0] - x) < 0.01f && Math.abs(points[1] - y) < 0.01f)
                || (Math.abs(points[end] - x) < 0.01f && Math.abs(points[end + 1] - y) < 0.01f);
        }

        boolean sameEndpoints(Path other){
            return endpoint(other.points[0], other.points[1])
                && endpoint(other.points[other.points.length - 2], other.points[other.points.length - 1]);
        }
    }
}

