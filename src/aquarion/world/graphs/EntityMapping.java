package aquarion.world.graphs;

import arc.func.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import mindustry.gen.*;


public class EntityMapping {
    public static int customUnits;
    public static ObjectIntMap<Class<? extends Entityc>> idMap = new ObjectIntMap<>();
    public static Entry<Class<? extends Entityc>, Prov<? extends Entityc>>[] entities = new Entry[]{
            entry(GraphUpdater.class, GraphUpdater::new)
    };

    private static <T extends Entityc> Entry<Class<T>, Prov<T>> entry(Class<T> name, Prov<T> prov) {
        Entry<Class<T>, Prov<T>> out = new Entry<>();
        out.key = name;
        out.value = prov;
        return out;
    }
}