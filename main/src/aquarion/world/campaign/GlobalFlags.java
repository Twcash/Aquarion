package aquarion.world.campaign;

import arc.struct.ObjectMap;
import arc.struct.StringMap;
import mindustry.game.Rules;

import static mindustry.Vars.*;

/** Shared campaign flags stored in sector save rules, never in application preferences. */
public final class GlobalFlags{
    private static final String prefix = "aquarion-campaign-flag:";
    private static final ObjectMap<String, String> values = new ObjectMap<>();
    private static Rules loadedRules;
    private static long revision;

    private GlobalFlags(){}

    public static void reload(){
        values.clear();
        revision = 0;
        loadedRules = state.rules;
        if(!state.isCampaign()) return;
        for(var planet : content.planets()) for(var sector : planet.sectors){
            if(sector == state.getSector() || sector.save == null || sector.save.meta == null
                || sector.save.meta.rules == null) continue;
            merge(sector.save.meta.rules.tags);
        }
        merge(state.rules.tags);
        // Carry the complete ledger into this sector's next save, including cleared flags.
        for(var entry : values) state.rules.tags.put(prefix + entry.key, entry.value);
    }

    private static void merge(StringMap tags){
        if(tags == null) return;
        for(var entry : tags){
            if(!entry.key.startsWith(prefix)) continue;
            String key = entry.key.substring(prefix.length());
            long stamp = stamp(entry.value);
            if(stamp < 0) continue;
            revision = Math.max(revision, stamp);
            String previous = values.get(key);
            if(previous == null || stamp > stamp(previous)) values.put(key, entry.value);
        }
    }

    private static long stamp(String record){
        try{
            return Long.parseLong(record.substring(0, record.indexOf(':')));
        }catch(RuntimeException invalid){
            return -1;
        }
    }

    public static boolean has(String flag){
        if(state == null || !state.isCampaign() || flag == null || flag.isEmpty()) return false;
        if(loadedRules != state.rules) reload();
        String record = values.get(flag);
        return record != null && record.endsWith(":1");
    }

    public static void set(String flag){
        set(flag, true);
    }

    public static void set(String flag, boolean value){
        if(state == null || !state.isCampaign() || flag == null || flag.isEmpty()) return;
        if(loadedRules != state.rules) reload();
        if(has(flag) == value) return;
        revision = Math.max(System.currentTimeMillis(), revision + 1);
        String record = revision + (value ? ":1" : ":0");
        values.put(flag, record);
        state.rules.tags.put(prefix + flag, record);
    }
}
