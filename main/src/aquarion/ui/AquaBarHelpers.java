package aquarion.ui;

import arc.Core;
import arc.func.Func;
import arc.graphics.Color;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.ui.Bar;

public class AquaBarHelpers {
    public interface CustomBarHolder{

        <T extends Building> void addBar(String name, Func<T, Bar> sup);

        default void addLiquidBoostBar(Liquid liq){
            addBar("liquid-" + liq.name + Core.bundle.get("bar.boost"), entity -> !liq.unlockedNow() ? null : new Bar(
                    () -> Core.bundle.format("bar.boost.liquid", liq.localizedName),
                    liq::barColor,
                    () -> entity.liquids.get(liq) / entity.block.liquidCapacity
            ));
        }

        default <T extends Building> void addLiquidBoostBar(Func<T, Liquid> current){
            this.<T>addBar("liquid", entity -> {
                return new Bar(
                        () -> {
                            Liquid liq = current.get(entity);
                            if(liq != null && !liq.unlockedNow()) liq = null;
                            return (liq == null || entity.liquids.get(liq) <= 0.001f ? Core.bundle.get("bar.boost.empty") : Core.bundle.format("bar.boost.liquid", liq.localizedName));
                            },
                        () -> {
                            Liquid liq = current.get(entity);
                            if(liq != null && !liq.unlockedNow()) liq = null;
                            return liq == null ? Color.clear : liq.barColor();
                            },
                        () -> {
                            Liquid liq = current.get(entity);
                            if(liq != null && !liq.unlockedNow()) liq = null;
                            return liq == null ? 0f : entity.liquids.get(liq) / entity.block.liquidCapacity;
                        }
                );
            });
        }
    }
}
