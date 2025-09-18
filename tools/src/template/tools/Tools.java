package template.tools;

import arc.*;
import arc.assets.*;
import arc.files.*;
import arc.graphics.g2d.*;
import arc.mock.*;
import arc.struct.*;
import arc.util.*;
import arc.util.Log.*;
import mindustry.async.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import template.*;
import template.gen.*;
import template.tools.GenAtlas.*;

import java.nio.file.*;
import java.util.concurrent.*;

import static mindustry.Vars.*;

/**
 * Main entry point of the tools' module. This must only affect the main project's asset directory.
 * @author GlennFolker
 */
public final class Tools{
    public static Template thisMod;
    public static LoadedMod mod;
    public static ModMeta meta;

    public static final Fi
        assetsDir, spritesDir;

    public static GenAtlas atlas;

    private static final TaskQueue runs = new TaskQueue();
    private static final IntSet[] initialized = new IntSet[ContentType.all.length];
    private static final IntSet[] loaded = new IntSet[ContentType.all.length];

    static{
        assetsDir = new Fi(Paths.get("").toFile());
        spritesDir = assetsDir.child("sprites");

        for(var type : ContentType.all){
            int i = type.ordinal();
            synchronized(initialized){ initialized[i] = new IntSet(); }
            synchronized(loaded){ loaded[i] = new IntSet(); }
        }
    }

    private Tools(){}

    public static void main(String[] args){
        Log.logger = new NoopLogHandler();

        headless = true;
        Core.app = new MockApplication(){
            @Override
            public void post(Runnable runnable){
                runs.post(runnable);
            }
        };
        Core.files = new MockFiles();
        Core.assets = new AssetManager(tree = new FileTree());
        Core.settings = new Settings();
        Core.atlas = atlas = new GenAtlas();

        asyncCore = new AsyncCore();
        state = new GameState();
        mods = new Mods();

        content = new ContentLoader();
        content.createBaseContent();

        thisMod = new Template(true);

        meta = new ModMeta(){{ name = System.getProperty("currentModName"); }};
        mod = new LoadedMod(null, null, thisMod, Tools.class.getClassLoader(), meta);

        Reflect.<Seq<LoadedMod>>get(Mods.class, mods, "mods").add(mod);
        Reflect.<ObjectMap<Class<?>, ModMeta>>get(Mods.class, mods, "metas").put(Template.class, meta);

        addRegions();
        atlas.clear = atlas.find("clear");

        content.setCurrentMod(mod);
        thisMod.loadContent();
        content.setCurrentMod(null);

        Log.logger = new DefaultLogHandler();
        loadLogger();

        runs.run();
        Processors.process();
        runs.run();

        atlas.dispose();
    }

    private static void addRegions(){
        Log.info("Adding regions...");
        Time.mark();

        ExecutorService exec = Executors.newCachedThreadPool();

        spritesDir.walk(path -> {
            if(!path.extEquals("png")) return;
            exec.submit(() -> atlas.addRegion(path));
        });

        Threads.await(exec);
        Log.info("Total time to add regions: @ms", Time.elapsed());
    }

    @SuppressWarnings("all")
    public static boolean init(Content content){
        synchronized(initialized){
            boolean should = initialized[content.getContentType().ordinal()].add(content.id);
            if(should) content.init();

            return should;
        }
    }

    @SuppressWarnings("all")
    public static boolean load(Content content){
        synchronized(loaded){
            boolean should = loaded[content.getContentType().ordinal()].add(content.id);
            if(should){
                content.load();
                if (content instanceof MappableContent c) TemplateContentRegionRegistry.load(c);
            }

            return should;
        }
    }

    public static GenRegion conv(TextureRegion region){
        return (GenRegion)region;
    }
}