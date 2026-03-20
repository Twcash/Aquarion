package aquarion;

import aquarion.tools.IconLoader;
import aquarion.ui.AquaStyles;
import aquarion.world.MultiBlockLib.LinkBlock;
import aquarion.world.MultiBlockLib.PlaceholderBlock;
import aquarion.world.blocks.neoplasia.NeoplasiaGraph;
import aquarion.world.graphics.AquaShaders;
import aquarion.world.graphics.AquaSoundControl;
import aquarion.world.graphics.Renderer;
import arc.*;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.ArcRuntimeException;
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
import mindustry.ui.Fonts;
import mindustry.ui.dialogs.*;
import aquarion.annotations.Annotations.*;
import aquarion.gen.*;
import mindustry.world.Block;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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
    public static final Seq<Block> mirrorList = new Seq<>();
    public static Block block;

    public static final int maxsize = 4;
    public static LinkBlock[] linkEntity, linkEntityLiquid;
    public static PlaceholderBlock[] placeholderEntity;

    public static void postLoad(){
        Events.on(EventType.ContentInitEvent.class, e -> {
            mirrorList.each(block -> {
                Block mirror = new Block(block.name + "-mirror");
                Field[] fields = Block.class.getFields();
                for (Field field : fields) {
                    try {
                        if (field.getModifiers() == Modifier.PUBLIC && !(field.getName().equals("name") || field.getName().equals("mirror"))) {
                            field.set(mirror, field.get(block));
                        }

                        if (field.getModifiers() == Modifier.PUBLIC && field.getName().equals("mirror")){
                            field.set(mirror, true);
                        }
                    }catch (IllegalAccessException ex) {
                        throw new ArcRuntimeException(ex);
                    }
                }
                mirror.init();
                mirror.loadIcon();
                mirror.load();
            });
        });
    }

    public static void loadBlock() {
        linkEntity = new LinkBlock[maxsize];
        linkEntityLiquid = new LinkBlock[maxsize];
        placeholderEntity = new PlaceholderBlock[maxsize];
        for (int i = 0; i < maxsize; i++) {
            int s = i + 1;
            linkEntity[i] = new LinkBlock("link-entity-" + s) {{
                size = s;
            }};
            linkEntityLiquid[i] = new LinkBlock("link-entity-liquid-" + s) {{
                size = s;
                outputsLiquid = true;
            }};
            placeholderEntity[i] = new PlaceholderBlock("placeholder-entity-" + s) {{
                size = s;
            }};
        }
    }

    public AquaLoader(boolean tools){
        AquaLoader.tools = tools;
        Events.run(EventType.Trigger.draw, Renderer::draw);
        ModEventHandler.init();

        Events.on(EventType.FileTreeInitEvent.class, e ->
                app.post(AquaShaders::init)
        );
        Events.on(EventType.DisposeEvent.class, e ->
                AquaShaders.dispose()
        );
        Events.on(EventType.ClientLoadEvent.class, e -> {
            //IconLoader.loadIcons();
            Planets.erekir.accessible = false;
            Planets.serpulo.accessible = false;
            Planets.erekir.visible = false;
            Planets.serpulo.visible = false;
            Planets.sun.visible = false;
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
        loadBlock();
        AquarionMod.loadContent();

        aquarionEntityMapping.init();
//        Events.run(EventType.Trigger.update, NeoplasiaGraph::update);
//        Events.run(EventType.Trigger.draw, NeoplasiaGraph::draw);
        AquaLoader.postLoad();

    }
    public static Mods.LoadedMod mod(){
        return mod;
    }

}
