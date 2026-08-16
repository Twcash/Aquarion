package aquarion.world.graphics;

import mindustry.type.Liquid;

/** A cosmetic bubble travelling through a pipe. Tracks its entry/exit directions so it can bend around corners. */
public class PipeBubble {
    public Liquid liquid;
    public int src = -1; // entry direction (0-3), or -1 for a bubble spawned at the tile center
    public int dst;      // exit direction (0-3)
    public float lat;    // lateral offset from the path centerline, in world units
    public float t;      // travel progress from entry to exit, 0..1
    public float size;   // radius multiplier

    public PipeBubble(Liquid liquid, int src, int dst, float lat, float size){
        this.liquid = liquid;
        this.src = src;
        this.dst = dst;
        this.lat = lat;
        this.size = size;
    }
}
