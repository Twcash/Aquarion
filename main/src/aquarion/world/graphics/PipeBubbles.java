package aquarion.world.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.geom.Geometry;
import arc.util.Tmp;

/**
 * Shared drawing and travel logic for the cosmetic bubbles that flow through pipes.
 * Bubbles travel along a curved path from their entry side to their exit side, so they
 * follow the pipe around corners, and are handed between adjacent pipe blocks to stay
 * continuous. Each bubble is tinted with the color of the liquid it represents.
 */
public class PipeBubbles {
    public static float speed = 0.3f;   // tiles/second
    public static float radius = 1f;    // base bubble radius, in world units
    public static float spread = 2f;    // lateral scatter, in world units
    public static float lighten = 0.2f; // how much bubbles are lightened toward white

    /** X position of a bubble travelling from its entry side to its exit side, curving through the tile center. */
    public static float bubbleX(float x, float y, PipeBubble b, float half){
        int sd = b.src == -1 ? b.dst : b.src;
        float p0x = (b.src == -1 ? x : x + Geometry.d4x(b.src) * half) - Geometry.d4y(sd) * b.lat;
        float p2x = x + Geometry.d4x(b.dst) * half - Geometry.d4y(b.dst) * b.lat;
        float inv = 1f - b.t;
        return inv * inv * p0x + 2f * inv * b.t * x + b.t * b.t * p2x;
    }

    /** Y position of a bubble travelling from its entry side to its exit side, curving through the tile center. */
    public static float bubbleY(float x, float y, PipeBubble b, float half){
        int sd = b.src == -1 ? b.dst : b.src;
        float p0y = (b.src == -1 ? y : y + Geometry.d4y(b.src) * half) + Geometry.d4x(sd) * b.lat;
        float p2y = y + Geometry.d4y(b.dst) * half + Geometry.d4x(b.dst) * b.lat;
        float inv = 1f - b.t;
        return inv * inv * p0y + 2f * inv * b.t * y + b.t * b.t * p2y;
    }

    /** Draws a single bubble. Callers should reset the draw color afterwards. */
    public static void drawBubble(PipeBubble b, float x, float y, float fill, float half){
        Tmp.c2.set(b.liquid.color).lerp(Color.white, lighten);
        Draw.color(Tmp.c2, fill * 0.85f);
        Fill.circle(bubbleX(x, y, b, half), bubbleY(x, y, b, half), radius * b.size);
    }
}
