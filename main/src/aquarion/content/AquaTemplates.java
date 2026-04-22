package aquarion.content;

import aquarion.world.blocks.turrets.AquaItemTurret;
import aquarion.world.graphics.AquaFx;
import mindustry.content.Fx;
import mindustry.content.Planets;
import mindustry.entities.effect.MultiEffect;

import static aquarion.content.AquaPlanets.*;
import static aquarion.world.graphics.AquaPal.tantDarkestTone;

public class AquaTemplates {
    public static class AquaItemTurretTemplate extends AquaItemTurret {
        public AquaItemTurretTemplate(String name) {
            super(name);
            shownPlanets.addAll(Planets.serpulo, Planets.erekir, fakeSerpulo, tantros2, qeraltar);
            squareSprite = false;
            outlineColor = tantDarkestTone;
            destroyEffect = new MultiEffect(AquaFx.factoryDestroy, Fx.dynamicExplosion);
        }
    }
}
