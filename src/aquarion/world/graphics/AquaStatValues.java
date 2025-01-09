package aquarion.world.graphics;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.Tooltip.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.ctype.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.maps.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.content;
import static mindustry.Vars.mobile;

public class AquaStatValues extends StatValues {
    public static StatValue newSpeedBoosters(String unit, float amount, float speed, boolean strength, Liquid filter){
        return table -> {
            table.row();
            table.table(c -> {
                for(Liquid liquid : content.liquids()){

                    c.table(Styles.grayPanel, b -> {
                        b.image(liquid.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit).with(i -> withTooltip(i, liquid, false));;
                        b.table(info -> {
                            info.add(liquid.localizedName).left().row();
                            info.add(Strings.autoFixed(amount * 60f, 2) + StatUnit.perSecond.localized()).left().color(Color.lightGray);
                        });

                        b.table(bt -> {
                            bt.right().defaults().padRight(3).left();
                            if(speed != Float.MAX_VALUE) bt.add(unit.replace("{0}", "[stat]" + Strings.autoFixed(speed * (strength ? liquid.heatCapacity : 1f) + (strength ? 1f : 0f), 2) + "[lightgray]")).pad(5);
                        }).right().grow().pad(10f).padRight(15f);
                    }).growX().pad(5).row();
                }
            }).growX().colspan(table.getColumns());
            table.row();
        };
    }
    public static <T extends Element> T withTooltip(T element, UnlockableContent content, boolean tooltip){
        if(content != null){
            if(!mobile){
                if(tooltip){
                    element.addListener(Tooltips.getInstance().create(content.localizedName));
                }
                element.addListener(new HandCursorListener(() -> !content.isHidden(), true));
            }
            element.clicked(() -> {
                if(!content.isHidden()){
                    Vars.ui.content.show(content);
                }
            });
        }
        return element;
    }

    public static <T extends Element> T withTooltip(T element, UnlockableContent content){
        return withTooltip(element, content, false);
    }
}
