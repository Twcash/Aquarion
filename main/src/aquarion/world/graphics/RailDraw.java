package aquarion.world.graphics;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;

/** A textured ribbon: the sprite repeats by distance, with continuous joins on bends. */
public class RailDraw{
    private static final float[] vertices = new float[24];

    public static void curve(TextureRegion region, float x, float y, float endX, float endY, float[] c){
        curve(region, x, y, endX, endY, c, 0f, 0f);
    }

    /** Translate the completed ribbon so shadows retain the exact shape and texture phase. */
    public static void curve(TextureRegion region, float x, float y, float endX, float endY, float[] c, float offsetX, float offsetY){
        float width = region.height * region.scl() / 2f;
        float repeat = Math.max(0.25f, region.width * region.scl());
        float length = Mathf.dst(x, y, c[0], c[1]) + Mathf.dst(c[0], c[1], c[2], c[3]) + Mathf.dst(c[2], c[3], endX, endY);
        int steps = Math.max(16, Mathf.ceil(length));
        float previousX = x, previousY = y, phase = 0f;
        float tangentX = c[0] - x, tangentY = c[1] - y;
        float normalScale = width / Mathf.len(tangentX, tangentY);
        float previousNX = -tangentY * normalScale, previousNY = tangentX * normalScale;
        float color = Draw.getColor().toFloatBits();
        for(int i = 1; i <= steps; i++){
            float t = i / (float)steps, s = 1f - t;
            //I love math
            //Based off of https://www.artstation.com/blogs/kanoba/p3yX/basics-of-ribbons-in-ue4

            float px = s*s*s*x + 3f*s*s*t*c[0] + 3f*s*t*t*c[2] + t*t*t*endX;
            float py = s*s*s*y + 3f*s*s*t*c[1] + 3f*s*t*t*c[3] + t*t*t*endY;
            tangentX = s*s*(c[0]-x) + 2f*s*t*(c[2]-c[0]) + t*t*(endX-c[2]);
            tangentY = s*s*(c[1]-y) + 2f*s*t*(c[3]-c[1]) + t*t*(endY-c[3]);
            normalScale = width / Mathf.len(tangentX, tangentY);
            float nx = -tangentY * normalScale, ny = tangentX * normalScale;
            float span = Mathf.dst(previousX, previousY, px, py), consumed = 0f;
            while(consumed < span){
                float piece = Math.min(span - consumed, repeat - phase);
                float a = consumed / span, b = (consumed + piece) / span;
                float ax = Mathf.lerp(previousX, px, a), ay = Mathf.lerp(previousY, py, a);
                float bx = Mathf.lerp(previousX, px, b), by = Mathf.lerp(previousY, py, b);
                float anx = Mathf.lerp(previousNX, nx, a), any = Mathf.lerp(previousNY, ny, a);
                float bnx = Mathf.lerp(previousNX, nx, b), bny = Mathf.lerp(previousNY, ny, b);
                float u1 = Mathf.lerp(region.u, region.u2, phase / repeat);
                float u2 = Mathf.lerp(region.u, region.u2, (phase + piece) / repeat);
                vertex(0, ax - anx + offsetX, ay - any + offsetY, color, u1, region.v2);
                vertex(6, ax + anx + offsetX, ay + any + offsetY, color, u1, region.v);
                vertex(12, bx + bnx + offsetX, by + bny + offsetY, color, u2, region.v);
                vertex(18, bx - bnx + offsetX, by - bny + offsetY, color, u2, region.v2);
                Draw.vert(region.texture, vertices, 0, vertices.length);
                consumed += piece;
                phase += piece;
                if(phase >= repeat - 0.00001f) phase = 0f;
            }
            previousX = px;
            previousY = py;
            previousNX = nx;
            previousNY = ny;
        }
    }

    private static void vertex(int offset, float x, float y, float color, float u, float v){
        vertices[offset] = x;
        vertices[offset + 1] = y;
        vertices[offset + 2] = color;
        vertices[offset + 3] = u;
        vertices[offset + 4] = v;
        vertices[offset + 5] = 0f;
    }
}
