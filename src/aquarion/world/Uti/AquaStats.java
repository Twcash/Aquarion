package aquarion.world.Uti;

import arc.graphics.Color;
import arc.util.Strings;
import mindustry.gen.Iconc;
import mindustry.ui.Styles;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.StatValues;

public class AquaStats {
    public static final Stat
            prodTime = new Stat("production-time"),
            MaxFlow = new Stat("maxflow");
    public static StatValue heatBooster(float heatRequirement, float overheatScale, float maxEfficiency, boolean flipHeat){
        return table -> {
            float overheatHeat = (maxEfficiency - 1f) * heatRequirement / overheatScale;
            float totalHeat = heatRequirement + overheatHeat;
            table.row();
            table.table(Styles.grayPanel, b -> {
                b.defaults().pad(5).left();
                b.add("[accent]Max Heat:[white] " + Strings.autoFixed(totalHeat, 1) + " " +
                        (flipHeat ? "[royal]" : "[red]") + Iconc.waves + "[]").row();
                b.add("[accent]Max Efficiency:[white] " +
                        Strings.autoFixed(maxEfficiency, 2) + StatUnit.timesSpeed.localized()).row();
            }).growX().pad(10f);

            table.row();
        };
    }
}