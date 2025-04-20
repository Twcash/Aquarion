package aquarion.world.Uti;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.ai.BaseRegistry;
import mindustry.ctype.Content;
import mindustry.game.Schematic;
import mindustry.world.Block;

public class AquaBaseRegistry {
    public ObjectMap<Content, Seq<BasePartNew>> reqParts = new ObjectMap<>();
    public Seq<BasePartNew> parts = new Seq<>();
    public Seq<AquaBaseRegistry.BasePartNew> forResource(Content item){
        return reqParts.get(item, Seq::new);
    }

    public static class BasePartNew implements Comparable<BasePartNew>{
        public final Schematic schematic;

        //offsets for drills
        public int centerX, centerY;

        public @Nullable Content required;
        public @Nullable Block core;

        //total build cost
        public float tier;

        public BasePartNew(Schematic schematic){
            this.schematic = schematic;
        }

        @Override
        public int compareTo(BasePartNew other){
            return Float.compare(tier, other.tier);
        }
    }
}
