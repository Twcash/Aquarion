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
        Properties iconProperties = new Properties();
        try(Reader reader = Vars.tree.get("icons/aquarion-icons.properties").reader(512)){
            iconProperties.load(reader);
        }catch(Exception e){
            return;
        }

        for(Map.Entry<Object, Object> entry : iconProperties.entrySet()){
            String codePointStr = (String)entry.getKey();
            String[] valueParts = ((String)entry.getValue()).split("\\|");
            if(valueParts.length < 2) continue;

            try{
                int codePoint = Integer.parseInt(codePointStr);
                String textureName = valueParts[1];
                TextureRegion region = Core.atlas.find(textureName);

                //Fonts.registerIcon(valueParts[0], textureName, codePoint, region);

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