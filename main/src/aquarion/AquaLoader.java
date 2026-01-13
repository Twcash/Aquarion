package aquarion;

import aquarion.tools.IconLoader;
import aquarion.ui.AquaStyles;
import aquarion.world.graphics.AquaShaders;
import aquarion.world.graphics.AquaSoundControl;
import aquarion.world.graphics.Renderer;
import arc.*;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.ctype.*;
import mindustry.game.EventType;
import mindustry.game.MapObjectives;
import mindustry.game.Rules;
import mindustry.gen.Building;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;
import aquarion.annotations.Annotations.*;
import aquarion.gen.*;

import static arc.Core.app;
import static mindustry.Vars.headless;
@LoadRegs("error")
@EnsureLoad
public class AquaLoader extends Mod {
    public static boolean tools = false;
    protected static Mods.LoadedMod mod;
    public AquaLoader(){
        this(false);
    }
    public AquaLoader(boolean tools){
        this.tools = tools;
        Events.run(EventType.Trigger.draw, Renderer::draw);
        ModEventHandler.init();
        Events.on(EventType.FileTreeInitEvent.class, e ->
                app.post(AquaShaders::init)
        );
        Events.on(EventType.DisposeEvent.class, e ->
                AquaShaders.dispose()
        );
        Events.on(EventType.ClientLoadEvent.class, e -> {
            IconLoader.loadIcons();
            Planets.erekir.accessible = false;
            Planets.serpulo.accessible = false;
            //:trol:
            Vars.control.sound = new AquaSoundControl();
        });
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


        if (!Vars.headless && Vars.ui != null) {
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
