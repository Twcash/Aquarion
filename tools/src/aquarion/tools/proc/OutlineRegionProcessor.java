package aquarion.tools.proc;

import aquarion.gen.*;
import aquarion.gen.Regions.*;
import aquarion.tools.GenAtlas.GenRegion;
import aquarion.tools.Processor;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.g2d.PixmapRegion;
import arc.util.Reflect;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.ExecutorService;

/**
 * A processor to outline certain regions in {@link Regions}.
 * @author GlennFolker
 */
public class OutlineRegionProcessor implements Processor{
    @Override
    public void process(ExecutorService exec){
        for(var field : Regions.class.getFields()){
            Outline anno = field.getAnnotation(Outline.class);
            if(anno == null) continue;

            String name = field.getName();

            GenRegion rawRegion = Reflect.get(Regions.class, name.replace("OutlineRegion", "Region"));

            submit(exec, "Regions.java", () -> {
                Color color = Color.valueOf(anno.color());
                int rad = anno.radius();

                PixmapRegion region = new PixmapRegion(rawRegion.pixmap());
                Pixmap out = Pixmaps.outline(region, color, rad);

                GenRegion outlineRegion = new GenRegion(rawRegion.name + "-outline", out);
                outlineRegion.relativePath = rawRegion.relativePath;
                outlineRegion.save();

                VarHandle handle = MethodHandles.publicLookup().unreflectVarHandle(field);
                handle.setVolatile(outlineRegion);
            });
        }
    }
}