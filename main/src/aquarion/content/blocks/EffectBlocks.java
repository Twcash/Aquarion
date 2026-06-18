package aquarion.content.blocks;

import aquarion.world.blocks.core.OverdrivePylon;
import aquarion.world.graphics.AquaFx;
import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.content.Planets;
import mindustry.entities.effect.MultiEffect;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.blocks.defense.OverdriveProjector;
import mindustry.world.blocks.power.LightBlock;
import mindustry.world.consumers.ConsumeLiquidFlammable;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import static aquarion.content.AquaItems.*;
import static aquarion.content.AquaLiquids.magma;
import static aquarion.content.AquaPlanets.*;
import static mindustry.content.Items.silicon;
import static mindustry.type.ItemStack.with;

public class EffectBlocks {
    public static Block  lantern;

    public static void loadContent() {
        lantern = new LightBlock("lantern") {{
            shownPlanets.addAll(fakeSerpulo,fakeErekir, tantros2, qeraltar);
            requirements(Category.effect, BuildVisibility.lightingOnly, with(silicon, 30));
            brightness = 0.8f;
            destroyEffect = new MultiEffect(Fx.dynamicExplosion, AquaFx.factoryDestroy);
            size = 2;
            radius = 90;
            envEnabled |= Env.terrestrial | Env.underwater;
            envDisabled = Env.none;
            consume(new ConsumeLiquidFlammable(0.1f, 2 / 60f));
        }};
    }
}
