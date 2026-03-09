package aquarion.tools.proc;

import aquarion.tools.Processor;
import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.io.PropertiesUtils;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.ctype.UnlockableContent;
import mindustry.type.UnitType;
import mindustry.world.blocks.ConstructBlock;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.ExecutorService;

public class IconProcessor implements Processor {
    @Override
    public void process(ExecutorService exec) throws IOException {
        Fi iconfile = Fi.get("../../../assets/icons/icons.properties");
        OrderedMap<String, String> map = new OrderedMap<>();
        PropertiesUtils.load(map, iconfile.reader(256));

        ObjectMap<String, String> content2id = new ObjectMap<>();
        map.each((key, val) -> content2id.put(val.split("\\|")[0], key));

        Seq<UnlockableContent> cont = Seq.withArrays(Vars.content.blocks(), Vars.content.items(), Vars.content.liquids(), Vars.content.units(), Vars.content.statusEffects());
        cont.removeAll(u -> u instanceof ConstructBlock || u == Blocks.air || (u instanceof UnitType t && t.internal));

        int minid = 0xF8FF;
        for(String key : map.keys()){
            minid = Math.min(Integer.parseInt(key) - 1, minid);
        }

        for(UnlockableContent c : cont){
            if(!content2id.containsKey(c.name)){
                map.put(minid + "", c.name + "|" + texname(c));
                minid --;
            }
        }

        Writer writer = iconfile.writer(false);
        for(String key : map.keys()){
            writer.write(key + "=" + map.get(key) + "\n");
        }

        writer.close();
    }
    static String texname(UnlockableContent c){
        return c.getContentType() + "-" + c.name + "-ui";
    }

}
