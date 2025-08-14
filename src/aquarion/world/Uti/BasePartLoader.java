package aquarion.world.Uti;

import arc.Core;
import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.ai.BaseRegistry;
import mindustry.content.Blocks;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.Pump;
import mindustry.content.Items;
import mindustry.Vars;
import arc.util.Log;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.world.blocks.sandbox.ItemSource;
import mindustry.world.blocks.sandbox.LiquidSource;
import mindustry.world.blocks.storage.CoreBlock;

import java.lang.reflect.Field;

import static arc.Core.assets;

// Your package here
// package your.mod.package;

public class BasePartLoader {

    public void prepareRegistryCopies() {

    }

    public static void loadAllBaseParts() {

        Seq<Schematic> schematics = loadSchematics("baseparts");
        addSchematicsToBaseRegistry(schematics);
        Log.info("[BasePartLoader] Loaded @ base parts.", schematics.size);
    }

    /** Loads all .msch schematics from assets/baseparts */
    public static Seq<Schematic> loadSchematics(String folderName) {
        Seq<Schematic> schematics = new Seq<>();
        Fi folder = Core.files.internal("saves/mods/aquarion/baseparts");

        if (!folder.exists() || !folder.isDirectory()) {
            Log.err("[BasePartLoader] Folder '@' not found or not a directory.", folder.absolutePath());
            return schematics;
        }

        for (Fi file : folder.list()) {
            if (file.extension().equalsIgnoreCase("msch")) {
                try {
                    schematics.add(Schematics.read(file));
                } catch (Throwable t) {
                    Log.err("[BasePartLoader] Failed to load schematic '@': @", file.name(), t);
                }
            }
        }
        return schematics;
    }

    /** Converts schematics into BaseParts and adds them to the registry */
    public static void addSchematicsToBaseRegistry(Seq<Schematic> schematics) {

        for (Schematic schem : schematics) {
            BaseRegistry.BasePart Bee = new BaseRegistry.BasePart(schem);
            Vars.bases.parts.add(Bee);

            BaseRegistry.BasePart found = Vars.bases.parts.find(b -> b == Bee);
            if(Bee.core != null){
                Vars.bases.cores.addUnique( Bee);
            }else if(Bee.required == null){
                Vars.bases.parts.addUnique(Bee);
            }
            if(Bee.required != null && Bee.core == null){
                Vars.bases.reqParts.get(Bee.required, Seq::new).addUnique( Bee);
            }
            if(found != null){
                Log.info("Found base part: " + found.schematic.name());
            } else {
                Log.info("Base part not found. " + schem.name());
            }
            for(Schematic.Stile tile : schem.tiles){
                if((tile.block instanceof ItemSource)){
                    Item config = (Item)tile.config;
                    tile.block = Blocks.air;
                }
            }
            for(Schematic.Stile tile : schem.tiles){
                if((tile.block instanceof LiquidSource)){
                    Item config = (Item)tile.config;
                    tile.block = Blocks.air;
                }
            }
        }
/*       Vars.bases.parts.each(p => {
                Log.info(p.schematic.file.name());
      });*/
        Vars.bases.parts.sort();
        Vars.bases.reqParts.each((key, arr) -> arr.sort());
    }
}