package aquarion.world;

import arc.struct.Seq;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class Statf extends Stat{
    public static final Seq<Stat> all = new Seq<>();

    public Statf(String name, StatCat category) {
        super(name, category);
    }

    public static final Stat
            magnetism = new Stat("magnetism"),
            acidity = new Stat("acidity");
}
