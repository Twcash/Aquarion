package aquarion.world.graphics;

import arc.graphics.Colors;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Font.Glyph;
import arc.graphics.g2d.FontCache;
import arc.graphics.g2d.GlyphLayout;
import arc.graphics.g2d.GlyphLayout.GlyphRun;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.FloatSeq;
import arc.util.Time;
import java.util.Arrays;
import java.util.function.IntToDoubleFunction;

public class AquaFill {

    public static final FloatSeq points = new FloatSeq();

    public static final Vec2 v1 = new Vec2(), v2 = new Vec2(), v3 = new Vec2();

    /**
     * Draws a slice of a circle with center at x and y, a measure (fraction of the circle), a rotation, and a radius to represent its outer curved edge.
     * Due to the use of polygon approximations, side is the number of sides to draw for each curve section.
     * */
    public static void circleSlice(float x, float y,  float measure, float rotation, float radius, int sides){
        arcSlice(x, y, measure, rotation, 0, radius, sides);
    }

    public static void circleSlice(Position center, float measure, float rotation, float radius, int sides){
        arcSlice(center, measure, rotation, 0, radius, sides);
    }

    public static void arcSlice(Position center, float measure, float rotation, float innerRadius, float outerRadius, int sides){
        arcSlice(center.getX(), center.getY(), measure, rotation, innerRadius, outerRadius, sides);
    }

    /**
     * Draws an arc with center at x and y, a measure (fraction of the circle), a rotation, and two radii to represent its inner and outer curved edges.
     * Due to the use of polygon approximations, side is the number of sides to draw for each curve section.
     * */
    public static void arcSlice(float x, float y, float measure, float rotation, float innerRadius, float outerRadius, int sides){
        if (Mathf.zero(outerRadius) || Mathf.zero(measure) || sides == 0) return;
        points.clear();
        float fraction = measure / sides;
        if(Mathf.zero(innerRadius)){
            points.clear();
            points.add(x, y);
            for(int i = 0; i < sides + 1; i++){
                float angle = rotation - (measure / 2) + fraction * i;
                v1.trns(angle, outerRadius).add(x, y);
                points.add(v1.x, v1.y);
            }
            Fill.poly(points);
        } else {
            v2.trns(rotation - (measure / 2), outerRadius).add(x,y);
            v3.trns(rotation - (measure / 2), innerRadius).add(x,y);

            for (int i = 1; i < sides + 1; i++) {
                points.clear();
                points.add(v2.x, v2.y, v3.x, v3.y);
                float angle = rotation - (measure / 2) + fraction * i;
                v1.trns(angle, innerRadius).add(x, y);
                points.add(v1.x, v1.y);
                v3.set(v1);
                v1.trns(angle, outerRadius).add(x, y);
                points.add(v1.x, v1.y);
                v2.set(v1);
                Fill.poly(points);
            }
        }
    }


    public static class ShakingText {
        private final FloatSeq positions = new FloatSeq();
        private int[] pageOffsets = new int[0];
        public void draw(FontCache cache, GlyphLayout layout, String value, float x, float y, float size,
                         IntToDoubleFunction shakeAt, Runnable draw){
            int pages = cache.getFont().getRegions().size;
            if(pageOffsets.length != pages) pageOffsets = new int[pages];
            Arrays.fill(pageOffsets, 0);
            positions.clear();
            cache.setPosition(x, y);
            int cursor = 0, letter = 0;
            for(GlyphRun run : layout.runs){
                for(Glyph glyph : run.glyphs){
                    float strength = 0f;
                    // Wrapping can omit whitespace. Match the remaining source to the actual laid-out glyphs.
                    while(cursor < value.length()){
                        int start = cursor;
                        char ch = value.charAt(cursor++);
                        if(ch == '['){
                            if(cursor < value.length() && value.charAt(cursor) == '['){
                                cursor++;
                            }else{
                                int end = value.indexOf(']', cursor);
                                if(end >= 0){
                                    String tag = value.substring(cursor, end);
                                    if(tag.isEmpty() || tag.matches("#[0-9a-fA-F]{6}(?:[0-9a-fA-F]{2})?") || Colors.get(tag) != null){
                                        cursor = end + 1;
                                        continue;
                                    }
                                }
                            }
                        }
                        if(ch == '\n' || ch == '\r') continue;
                        Glyph candidate = cache.getFont().getData().getGlyph(ch);
                        if(candidate == null) candidate = cache.getFont().getData().missingGlyph;
                        if(candidate == glyph){
                            strength = (float)shakeAt.applyAsDouble(start) * size;
                            break;
                        }
                    }
                    float phase = Time.globalTime * 1.7f + letter++ * 13.37f;
                    float dx = (float)Math.sin(phase * 2.13f) * strength;
                    float dy = (float)Math.sin(phase * 3.71f + 1f) * strength;
                    int offset = pageOffsets[glyph.page];
                    pageOffsets[glyph.page] += 24;
                    float[] vertices = cache.getVertices(glyph.page);
                    for(int vertex = offset; vertex < offset + 24; vertex += 6){
                        positions.add(vertices[vertex], vertices[vertex + 1]);
                    }
                    shift(vertices, offset, dx, dy);
                }
            }
            try{
                draw.run();
            }finally{
                Arrays.fill(pageOffsets, 0);
                int index = 0;
                for(GlyphRun run : layout.runs){
                    for(Glyph glyph : run.glyphs){
                        int offset = pageOffsets[glyph.page];
                        pageOffsets[glyph.page] += 24;
                        float[] vertices = cache.getVertices(glyph.page);
                        for(int vertex = offset; vertex < offset + 24; vertex += 6){
                            vertices[vertex] = positions.get(index++);
                            vertices[vertex + 1] = positions.get(index++);
                        }
                    }
                }
            }
        }
    }

    private static void shift(float[] vertices, int offset, float dx, float dy){
        // Arc stores four vertices of six floats per glyph; only the x/y coordinates move.
        for(int vertex = offset; vertex < offset + 24; vertex += 6){
            vertices[vertex] += dx;
            vertices[vertex + 1] += dy;
        }
    }
}
