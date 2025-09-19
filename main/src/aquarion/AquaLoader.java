package aquarion;

import aquarion.tools.IconLoader;
import aquarion.ui.AquaStyles;
import aquarion.world.graphics.AquaShaders;
import aquarion.world.graphics.Renderer;
import arc.Core;
import arc.Events;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.MappableContent;
import mindustry.game.EventType;
import mindustry.gen.EntityMapping;
import mindustry.mod.Mod;

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
    public static boolean tools;
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


        if(!headless){
            Events.on(EventType.FileTreeInitEvent.class, e -> Core.app.post(AquaSounds::load));

        }

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
        aquarionEntityMapping.init();;
    }

}
