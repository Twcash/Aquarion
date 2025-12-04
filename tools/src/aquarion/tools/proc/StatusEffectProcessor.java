package aquarion.tools.proc;

import aquarion.AquaLoader;
import aquarion.tools.GenAtlas.GenRegion;
import aquarion.tools.Processor;
import arc.graphics.Color;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.g2d.PixmapRegion;
import arc.util.Log;
import mindustry.graphics.Pal;

import java.util.concurrent.ExecutorService;

import static aquarion.tools.Tools.atlas;
import static mindustry.Vars.content;

/**
 * Mirrors the logic from mindustry.tools.Generators#generate("item-icons") but overwrites original regions.
 * @author stabu_
 */
public class StatusEffectProcessor implements Processor{
    @Override
    public void process(ExecutorService exec){
        content.statusEffects().each(AquaLoader::isTemplate, effect -> submit(exec, effect.name, () -> {
            String regionName = effect.name;

            GenRegion regionToModify = atlas.find(regionName);
            if(!regionToModify.found()){
                Log.warn("Base region '@' not found for status effect '@'. Skipping modification.", regionName, effect.name);
                return;
            }

            String originalRelativePath = regionToModify.relativePath;

            Pixmap originalPixmap;
            Pixmap baseCopy = null;
            Pixmap tintedPixmap = null;
            Pixmap containerPixmap = null;
            Pixmap outlinedPixmap = null;

            try{
                originalPixmap = regionToModify.pixmap();
                baseCopy = originalPixmap.copy();

                tintedPixmap = baseCopy.copy();
                Color effectColor = effect.color;
                Pixmap finalTintedPixmap = tintedPixmap;
                tintedPixmap.each((x, y) -> {
                    int current = finalTintedPixmap.getRaw(x, y);
                    finalTintedPixmap.setRaw(x, y, Color.muli(current, effectColor.rgba()));
                });

                int containerWidth = tintedPixmap.width + 3 * 2;
                int containerHeight = tintedPixmap.height + 3 * 2;
                containerPixmap = new Pixmap(containerWidth, containerHeight);
                containerPixmap.draw(tintedPixmap, 3, 3, true);

                outlinedPixmap = Pixmaps.outline(new PixmapRegion(containerPixmap), Pal.gray, 3);

                GenRegion finalRegion = new GenRegion(regionName, outlinedPixmap);
                finalRegion.relativePath = originalRelativePath;
                finalRegion.save(true);
            }catch(Exception e){
                Log.err("Failed to modify UI icon for status effect: " + effect.name, e);
                if(outlinedPixmap != null && !outlinedPixmap.isDisposed()){
                    GenRegion potentiallyUpdatedRegion = atlas.find(regionName);
                    if(!potentiallyUpdatedRegion.found() || potentiallyUpdatedRegion.pixmap() != outlinedPixmap){
                        outlinedPixmap.dispose();
                    }
                }
            }finally{
                if(baseCopy != null && !baseCopy.isDisposed()) baseCopy.dispose();
                if(tintedPixmap != null && !tintedPixmap.isDisposed()) tintedPixmap.dispose();
                if(containerPixmap != null && !containerPixmap.isDisposed()) containerPixmap.dispose();
            }
        }));
    }
}