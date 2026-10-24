package aquarion.world.campaign;

import arc.graphics.Colors;
import arc.graphics.g2d.Font.Glyph;
import arc.graphics.g2d.GlyphLayout.GlyphRun;
import arc.scene.ui.Label;
import arc.struct.FloatSeq;
import arc.util.Time;
import java.util.Arrays;

/** Applies shake to individual cached glyphs without changing text layout or accumulating drift. */
class DialogueLabel extends Label {
    private DialogueTypewriter typewriter;
    private final FloatSeq positions = new FloatSeq();
    private int[] pageOffsets = new int[0];

    DialogueLabel(){
        super("");
    }

    void setDialogue(DialogueTypewriter value){
        typewriter = value;
        setText(value == null ? "" : value.text());
    }

    @Override
    public void draw(){
        validate();
        if(typewriter == null || !typewriter.hasShake()){
            super.draw();
            return;
        }
        int pages = cache.getFont().getRegions().size;
        if(pageOffsets.length != pages) pageOffsets = new int[pages];
        Arrays.fill(pageOffsets, 0);
        positions.clear();
        cache.setPosition(x, y);
        String value = typewriter.text();
        int cursor = 0, letter = 0;
        float size = cache.getFont().getLineHeight() * getFontScaleY();
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
                        strength = typewriter.shakeAt(start) * size;
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
            super.draw();
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

    static void shift(float[] vertices, int offset, float dx, float dy){
        // Arc stores four vertices of six floats per glyph; only the x/y coordinates move.
        for(int vertex = offset; vertex < offset + 24; vertex += 6){
            vertices[vertex] += dx;
            vertices[vertex + 1] += dy;
        }
    }
}
