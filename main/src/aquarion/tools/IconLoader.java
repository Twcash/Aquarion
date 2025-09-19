package aquarion.tools;

import arc.Core;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.ui.Fonts;

import java.io.Reader;
import java.util.Map;
import java.util.Properties;
//Stolen From omaloon :troll:
public class IconLoader{
    public static void loadIcons(){
        Seq<Font> availableFonts = Seq.with(Fonts.def, Fonts.outline);
        int fontSize = (int)(Fonts.def.getData().lineHeight / Fonts.def.getData().scaleY);

        Properties iconProperties = new Properties();
        //TODO generate class with char fields with icons
        try(Reader reader = Vars.tree.get("icons/aquarion-icons.properties").reader(512)){
            iconProperties.load(reader);
        }catch(Exception e){
            return;
        }

        for(Map.Entry<Object, Object> entry : iconProperties.entrySet()){
            String codePointStr = (String)entry.getKey();
            String[] valueParts = ((String)entry.getValue()).split("\\|");
            if(valueParts.length < 2){
                continue;
            }

            try{
                int codePoint = Integer.parseInt(codePointStr);
                String textureName = valueParts[1];
                TextureRegion region = Core.atlas.find(textureName);

                Vec2 scaledSize = Scaling.fit.apply(region.width, region.height, fontSize, fontSize);
                Font.Glyph glyph = constructGlyph(codePoint, region, scaledSize, fontSize);

                for(Font font : availableFonts){
                    font.getData().setGlyph(codePoint, glyph);
                }

            }catch(Exception ignored){
            }
        }
    }

    private static Font.Glyph constructGlyph(int id, TextureRegion region, Vec2 size, int fontSize){
        Font.Glyph glyph = new Font.Glyph();
        glyph.id = id;
        glyph.srcX = 0;
        glyph.srcY = 0;
        glyph.width = (int)size.x;
        glyph.height = (int)size.y;
        glyph.u = region.u;
        glyph.v = region.v2;
        glyph.u2 = region.u2;
        glyph.v2 = region.v;
        glyph.xoffset = 0;
        glyph.yoffset = -fontSize;
        glyph.xadvance = fontSize;
        glyph.fixedWidth = true;
        glyph.page = 0;
        return glyph;
    }
}