package aquarion;

import aquarion.tools.IconLoader;
import aquarion.ui.AquaStyles;
import aquarion.world.graphics.AquaShaders;
import aquarion.world.graphics.Renderer;
import aquarion.world.map.TemperatureMap;
import arc.*;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.util.*;
import mindustry.Vars;
import mindustry.ctype.*;
import mindustry.game.EventType;
import mindustry.game.EventType.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;
import aquarion.annotations.Annotations.*;
import aquarion.gen.*;

import static mindustry.Vars.*;
import static arc.Core.app;
import static mindustry.Vars.headless;
import arc.*;
import arc.util.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;
import aquarion.annotations.Annotations.*;
import aquarion.gen.*;

import static mindustry.Vars.*;
@LoadRegs("error")// Need this temporarily, so the class gets generated.
@EnsureLoad
public class AquaLoader extends Mod {
    public static boolean tools = false;
//    public boolean debug = true;
    protected static Mods.LoadedMod mod;
//    public static TemperatureMap tempMap = new TemperatureMap();
    public AquaLoader(){
        this(false);
    }
    public AquaLoader(boolean tools){
        Events.run(EventType.Trigger.draw, Renderer::draw);
        ModEventHandler.init();
        Events.on(EventType.FileTreeInitEvent.class, e ->
                app.post(AquaShaders::init)
        );

        Events.on(EventType.DisposeEvent.class, e ->
                AquaShaders.dispose()
        );
        AquaLoader.tools = tools;
//        if (debug) {
//            Events.run(EventType.Trigger.draw, () -> {
//                for (int i = 0; i < tempMap.tempMap.length; i++) {
//                    if (tempMap.tempMap[i] != 0) continue;
//
//                    Draw.z(Layer.flyingUnit+2);
//                    Draw.color(Pal.orangeSpark);
//                    //256 will be the max temperature EVER
//                    Draw.alpha(tempMap.tempMap[i] / 256f);
//
//                    Draw.rect("empty", world.tiles.geti(i).worldx(), world.tiles.geti(i).worldy());
//
//                    Draw.reset();
//                }
//            });
//        }

        Events.on(EventType.ContentInitEvent.class, e -> {
            if(!headless){
                Regions.load();
                Vars.content.each(content -> {
                    if (isTemplate(content) && content instanceof MappableContent mContent) {
                        aquarionContentRegionRegistry.load(mContent);
                    }
                });
            }
        });
    }
    @Override
    public void init() {

        IconLoader.loadIcons();

        if (!Vars.headless && ui != null) {
            AquaStyles.load();
            ModEventHandler.load();
        }
    }
    public static boolean isTemplate(Content content){
        return content.minfo.mod != null && content.minfo.mod.name.equals("template");
    }
    @Override
    public void loadContent() {
        AquarionMod.loadContent();

        aquarionEntityMapping.init();
    }
    public static Mods.LoadedMod mod(){
        return mod;
    }
}
