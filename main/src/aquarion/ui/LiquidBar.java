package aquarion.ui;

import arc.scene.ui.Tooltip;
import arc.util.Strings;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.ui.Styles;

/**
 * A single bar row for one liquid, showing that liquid's fill relative to the
 * block's liquid capacity. One of these is added per present liquid in a block's
 * {@code displayBars}, stacking the rows downwards. Hovering shows the exact
 * amount/capacity.
 */
public class LiquidBar extends Bar{
    public LiquidBar(Building build, Liquid liquid){
        super(() -> liquid.localizedName, liquid::barColor, () -> build.liquids.get(liquid) / build.block.liquidCapacity);

        addListener(new Tooltip(table -> {
            table.background(Styles.black6);
            table.margin(4f);
            table.label(() -> liquid.localizedName + " " + Strings.autoFixed(build.liquids.get(liquid), 1) + "/" + (int)build.block.liquidCapacity).style(Styles.outlineLabel);
        }));
    }
}
