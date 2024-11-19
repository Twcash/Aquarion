package aquarion.world.meta;

import aquarion.world.interfaces.HasHeat;
import arc.Core;
import arc.graphics.Color;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Strings;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.meta.Stats;

//uujuju1 mainly made this code I just modified it so I could learn graphs less painfully. Thank you!
public class HeatConfig {
    public boolean hasHeat = true;

    //UTTERLY USELESS DO NOT USE KEEP NULL
    public Seq<Point2> connections = new Seq<>();

    public Color barColor = Pal.redderDust;

    public void addBars(Block block) {
        if (!hasHeat) return;
        block.addBar("heat", building -> {
            HasHeat b = building.as();
            return new Bar(
                    () -> Core.bundle.get("bar.aqua-heat", "heat") + ": " + Strings.fixed(b.heat().heat, 2),
                    () -> barColor,
                    () -> 1
            );
        });
    }
}